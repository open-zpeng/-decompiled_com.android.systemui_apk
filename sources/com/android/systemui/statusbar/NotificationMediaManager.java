package com.android.systemui.statusbar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Trace;
import android.provider.DeviceConfig;
import android.util.ArraySet;
import android.widget.ImageView;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.Interpolators;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.phone.BiometricUnlockController;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.LockscreenWallpaper;
import com.android.systemui.statusbar.phone.ScrimController;
import com.android.systemui.statusbar.phone.ScrimState;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.statusbar.phone.StatusBarWindowController;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.xiaopeng.speech.protocol.event.OOBEEvent;
import dagger.Lazy;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class NotificationMediaManager implements Dumpable {
    public static final boolean DEBUG_MEDIA = false;
    private static final HashSet<Integer> PAUSED_MEDIA_STATES = new HashSet<>();
    private static final String TAG = "NotificationMediaManager";
    private BackDropView mBackdrop;
    private ImageView mBackdropBack;
    private ImageView mBackdropFront;
    private BiometricUnlockController mBiometricUnlockController;
    private final Context mContext;
    private NotificationEntryManager mEntryManager;
    private final KeyguardBypassController mKeyguardBypassController;
    private LockscreenWallpaper mLockscreenWallpaper;
    private final MediaArtworkProcessor mMediaArtworkProcessor;
    private MediaController mMediaController;
    private MediaMetadata mMediaMetadata;
    private String mMediaNotificationKey;
    private final MediaSessionManager mMediaSessionManager;
    protected NotificationPresenter mPresenter;
    private ScrimController mScrimController;
    private Lazy<ShadeController> mShadeController;
    private boolean mShowCompactMediaSeekbar;
    private Lazy<StatusBarWindowController> mStatusBarWindowController;
    private final StatusBarStateController mStatusBarStateController = (StatusBarStateController) Dependency.get(StatusBarStateController.class);
    private final SysuiColorExtractor mColorExtractor = (SysuiColorExtractor) Dependency.get(SysuiColorExtractor.class);
    private final KeyguardMonitor mKeyguardMonitor = (KeyguardMonitor) Dependency.get(KeyguardMonitor.class);
    private final Handler mHandler = (Handler) Dependency.get(Dependency.MAIN_HANDLER);
    private final Set<AsyncTask<?, ?, ?>> mProcessArtworkTasks = new ArraySet();
    private final DeviceConfig.OnPropertiesChangedListener mPropertiesChangedListener = new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.systemui.statusbar.NotificationMediaManager.1
        public void onPropertiesChanged(DeviceConfig.Properties properties) {
            for (String name : properties.getKeyset()) {
                if ("compact_media_notification_seekbar_enabled".equals(name)) {
                    String value = properties.getString(name, (String) null);
                    NotificationMediaManager.this.mShowCompactMediaSeekbar = OOBEEvent.STRING_TRUE.equals(value);
                }
            }
        }
    };
    private final MediaController.Callback mMediaListener = new MediaController.Callback() { // from class: com.android.systemui.statusbar.NotificationMediaManager.2
        @Override // android.media.session.MediaController.Callback
        public void onPlaybackStateChanged(PlaybackState state) {
            super.onPlaybackStateChanged(state);
            if (state != null) {
                if (!NotificationMediaManager.this.isPlaybackActive(state.getState())) {
                    NotificationMediaManager.this.clearCurrentMediaNotification();
                }
                NotificationMediaManager.this.dispatchUpdateMediaMetaData(true, true);
            }
        }

        @Override // android.media.session.MediaController.Callback
        public void onMetadataChanged(MediaMetadata metadata) {
            super.onMetadataChanged(metadata);
            NotificationMediaManager.this.mMediaArtworkProcessor.clearCache();
            NotificationMediaManager.this.mMediaMetadata = metadata;
            NotificationMediaManager.this.dispatchUpdateMediaMetaData(true, true);
        }
    };
    protected final Runnable mHideBackdropFront = new Runnable() { // from class: com.android.systemui.statusbar.NotificationMediaManager.4
        @Override // java.lang.Runnable
        public void run() {
            NotificationMediaManager.this.mBackdropFront.setVisibility(4);
            NotificationMediaManager.this.mBackdropFront.animate().cancel();
            NotificationMediaManager.this.mBackdropFront.setImageDrawable(null);
        }
    };
    private final ArrayList<MediaListener> mMediaListeners = new ArrayList<>();

    /* loaded from: classes21.dex */
    public interface MediaListener {
        void onMetadataOrStateChanged(MediaMetadata mediaMetadata, int i);
    }

    static {
        PAUSED_MEDIA_STATES.add(0);
        PAUSED_MEDIA_STATES.add(1);
        PAUSED_MEDIA_STATES.add(2);
        PAUSED_MEDIA_STATES.add(7);
    }

    @Inject
    public NotificationMediaManager(Context context, Lazy<ShadeController> shadeController, Lazy<StatusBarWindowController> statusBarWindowController, NotificationEntryManager notificationEntryManager, MediaArtworkProcessor mediaArtworkProcessor, KeyguardBypassController keyguardBypassController) {
        this.mContext = context;
        this.mMediaArtworkProcessor = mediaArtworkProcessor;
        this.mKeyguardBypassController = keyguardBypassController;
        this.mMediaSessionManager = (MediaSessionManager) this.mContext.getSystemService("media_session");
        this.mShadeController = shadeController;
        this.mStatusBarWindowController = statusBarWindowController;
        this.mEntryManager = notificationEntryManager;
        notificationEntryManager.addNotificationEntryListener(new NotificationEntryListener() { // from class: com.android.systemui.statusbar.NotificationMediaManager.3
            @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
            public void onEntryRemoved(NotificationEntry entry, NotificationVisibility visibility, boolean removedByUser) {
                NotificationMediaManager.this.onNotificationRemoved(entry.key);
            }
        });
        this.mShowCompactMediaSeekbar = OOBEEvent.STRING_TRUE.equals(DeviceConfig.getProperty("systemui", "compact_media_notification_seekbar_enabled"));
        DeviceConfig.addOnPropertiesChangedListener("systemui", this.mContext.getMainExecutor(), this.mPropertiesChangedListener);
    }

    public static boolean isPlayingState(int state) {
        return !PAUSED_MEDIA_STATES.contains(Integer.valueOf(state));
    }

    public void setUpWithPresenter(NotificationPresenter presenter) {
        this.mPresenter = presenter;
    }

    public void onNotificationRemoved(String key) {
        if (key.equals(this.mMediaNotificationKey)) {
            clearCurrentMediaNotification();
            dispatchUpdateMediaMetaData(true, true);
        }
    }

    public String getMediaNotificationKey() {
        return this.mMediaNotificationKey;
    }

    public MediaMetadata getMediaMetadata() {
        return this.mMediaMetadata;
    }

    public boolean getShowCompactMediaSeekbar() {
        return this.mShowCompactMediaSeekbar;
    }

    public Icon getMediaIcon() {
        if (this.mMediaNotificationKey == null) {
            return null;
        }
        synchronized (this.mEntryManager.getNotificationData()) {
            NotificationEntry entry = this.mEntryManager.getNotificationData().get(this.mMediaNotificationKey);
            if (entry != null && entry.expandedIcon != null) {
                return entry.expandedIcon.getSourceIcon();
            }
            return null;
        }
    }

    public void addCallback(MediaListener callback) {
        this.mMediaListeners.add(callback);
        callback.onMetadataOrStateChanged(this.mMediaMetadata, getMediaControllerPlaybackState(this.mMediaController));
    }

    public void removeCallback(MediaListener callback) {
        this.mMediaListeners.remove(callback);
    }

    public void findAndUpdateMediaNotifications() {
        MediaSession.Token token;
        boolean metaDataChanged = false;
        synchronized (this.mEntryManager.getNotificationData()) {
            ArrayList<NotificationEntry> activeNotifications = this.mEntryManager.getNotificationData().getActiveNotifications();
            int N = activeNotifications.size();
            NotificationEntry mediaNotification = null;
            MediaController controller = null;
            int i = 0;
            while (true) {
                if (i >= N) {
                    break;
                }
                NotificationEntry entry = activeNotifications.get(i);
                if (entry.isMediaNotification() && (token = (MediaSession.Token) entry.notification.getNotification().extras.getParcelable("android.mediaSession")) != null) {
                    MediaController aController = new MediaController(this.mContext, token);
                    if (3 == getMediaControllerPlaybackState(aController)) {
                        mediaNotification = entry;
                        controller = aController;
                        break;
                    }
                }
                i++;
            }
            if (mediaNotification == null && this.mMediaSessionManager != null) {
                List<MediaController> sessions = this.mMediaSessionManager.getActiveSessionsForUser(null, -1);
                for (MediaController aController2 : sessions) {
                    if (3 == getMediaControllerPlaybackState(aController2)) {
                        String pkg = aController2.getPackageName();
                        int i2 = 0;
                        while (true) {
                            if (i2 < N) {
                                NotificationEntry entry2 = activeNotifications.get(i2);
                                if (!entry2.notification.getPackageName().equals(pkg)) {
                                    i2++;
                                } else {
                                    controller = aController2;
                                    mediaNotification = entry2;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (controller != null && !sameSessions(this.mMediaController, controller)) {
                clearCurrentMediaNotificationSession();
                this.mMediaController = controller;
                this.mMediaController.registerCallback(this.mMediaListener);
                this.mMediaMetadata = this.mMediaController.getMetadata();
                metaDataChanged = true;
            }
            if (mediaNotification != null && !mediaNotification.notification.getKey().equals(this.mMediaNotificationKey)) {
                this.mMediaNotificationKey = mediaNotification.notification.getKey();
            }
        }
        if (metaDataChanged) {
            this.mEntryManager.updateNotifications();
        }
        dispatchUpdateMediaMetaData(metaDataChanged, true);
    }

    public void clearCurrentMediaNotification() {
        this.mMediaNotificationKey = null;
        clearCurrentMediaNotificationSession();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchUpdateMediaMetaData(boolean changed, boolean allowEnterAnimation) {
        NotificationPresenter notificationPresenter = this.mPresenter;
        if (notificationPresenter != null) {
            notificationPresenter.updateMediaMetaData(changed, allowEnterAnimation);
        }
        int state = getMediaControllerPlaybackState(this.mMediaController);
        ArrayList<MediaListener> callbacks = new ArrayList<>(this.mMediaListeners);
        for (int i = 0; i < callbacks.size(); i++) {
            callbacks.get(i).onMetadataOrStateChanged(this.mMediaMetadata, state);
        }
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.print("    mMediaSessionManager=");
        pw.println(this.mMediaSessionManager);
        pw.print("    mMediaNotificationKey=");
        pw.println(this.mMediaNotificationKey);
        pw.print("    mMediaController=");
        pw.print(this.mMediaController);
        if (this.mMediaController != null) {
            pw.print(" state=" + this.mMediaController.getPlaybackState());
        }
        pw.println();
        pw.print("    mMediaMetadata=");
        pw.print(this.mMediaMetadata);
        if (this.mMediaMetadata != null) {
            pw.print(" title=" + ((Object) this.mMediaMetadata.getText("android.media.metadata.TITLE")));
        }
        pw.println();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPlaybackActive(int state) {
        return (state == 1 || state == 7 || state == 0) ? false : true;
    }

    private boolean sameSessions(MediaController a, MediaController b) {
        if (a == b) {
            return true;
        }
        if (a == null) {
            return false;
        }
        return a.controlsSameSession(b);
    }

    private int getMediaControllerPlaybackState(MediaController controller) {
        PlaybackState playbackState;
        if (controller != null && (playbackState = controller.getPlaybackState()) != null) {
            return playbackState.getState();
        }
        return 0;
    }

    private void clearCurrentMediaNotificationSession() {
        this.mMediaArtworkProcessor.clearCache();
        this.mMediaMetadata = null;
        MediaController mediaController = this.mMediaController;
        if (mediaController != null) {
            mediaController.unregisterCallback(this.mMediaListener);
        }
        this.mMediaController = null;
    }

    public void updateMediaMetaData(boolean metaDataChanged, boolean allowEnterAnimation) {
        Trace.beginSection("StatusBar#updateMediaMetaData");
        if (this.mBackdrop == null) {
            Trace.endSection();
            return;
        }
        BiometricUnlockController biometricUnlockController = this.mBiometricUnlockController;
        boolean wakeAndUnlock = biometricUnlockController != null && biometricUnlockController.isWakeAndUnlock();
        if (this.mKeyguardMonitor.isLaunchTransitionFadingAway() || wakeAndUnlock) {
            this.mBackdrop.setVisibility(4);
            Trace.endSection();
            return;
        }
        MediaMetadata mediaMetadata = getMediaMetadata();
        Bitmap artworkBitmap = null;
        if (mediaMetadata != null && !this.mKeyguardBypassController.getBypassEnabled() && (artworkBitmap = mediaMetadata.getBitmap("android.media.metadata.ART")) == null) {
            artworkBitmap = mediaMetadata.getBitmap("android.media.metadata.ALBUM_ART");
        }
        if (metaDataChanged) {
            for (AsyncTask<?, ?, ?> task : this.mProcessArtworkTasks) {
                task.cancel(true);
            }
            this.mProcessArtworkTasks.clear();
        }
        if (artworkBitmap != null) {
            this.mProcessArtworkTasks.add(new ProcessArtworkTask(this, metaDataChanged, allowEnterAnimation).execute(artworkBitmap));
        } else {
            finishUpdateMediaMetaData(metaDataChanged, allowEnterAnimation, null);
        }
        Trace.endSection();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishUpdateMediaMetaData(boolean metaDataChanged, boolean allowEnterAnimation, Bitmap bmp) {
        BiometricUnlockController biometricUnlockController;
        boolean metaDataChanged2;
        Drawable artworkDrawable = bmp != null ? new BitmapDrawable(this.mBackdropBack.getResources(), bmp) : null;
        boolean cannotAnimateDoze = true;
        boolean hasMediaArtwork = artworkDrawable != null;
        boolean allowWhenShade = false;
        if (artworkDrawable == null) {
            LockscreenWallpaper lockscreenWallpaper = this.mLockscreenWallpaper;
            Bitmap lockWallpaper = lockscreenWallpaper != null ? lockscreenWallpaper.getBitmap() : null;
            if (lockWallpaper != null) {
                artworkDrawable = new LockscreenWallpaper.WallpaperDrawable(this.mBackdropBack.getResources(), lockWallpaper);
                allowWhenShade = this.mStatusBarStateController.getState() == 1;
            }
        }
        ShadeController shadeController = this.mShadeController.get();
        StatusBarWindowController windowController = this.mStatusBarWindowController.get();
        boolean hideBecauseOccluded = shadeController != null && shadeController.isOccluded();
        boolean hasArtwork = artworkDrawable != null;
        this.mColorExtractor.setHasMediaArtwork(hasMediaArtwork);
        ScrimController scrimController = this.mScrimController;
        if (scrimController != null) {
            scrimController.setHasBackdrop(hasArtwork);
        }
        if (hasArtwork && ((this.mStatusBarStateController.getState() != 0 || allowWhenShade) && (biometricUnlockController = this.mBiometricUnlockController) != null && biometricUnlockController.getMode() != 2 && !hideBecauseOccluded)) {
            if (this.mBackdrop.getVisibility() == 0) {
                metaDataChanged2 = metaDataChanged;
            } else {
                this.mBackdrop.setVisibility(0);
                if (allowEnterAnimation) {
                    this.mBackdrop.setAlpha(0.0f);
                    this.mBackdrop.animate().alpha(1.0f);
                } else {
                    this.mBackdrop.animate().cancel();
                    this.mBackdrop.setAlpha(1.0f);
                }
                if (windowController != null) {
                    windowController.setBackdropShowing(true);
                }
                metaDataChanged2 = true;
            }
            if (metaDataChanged2) {
                if (this.mBackdropBack.getDrawable() != null) {
                    Drawable drawable = this.mBackdropBack.getDrawable().getConstantState().newDrawable(this.mBackdropFront.getResources()).mutate();
                    this.mBackdropFront.setImageDrawable(drawable);
                    this.mBackdropFront.setAlpha(1.0f);
                    this.mBackdropFront.setVisibility(0);
                } else {
                    this.mBackdropFront.setVisibility(4);
                }
                this.mBackdropBack.setImageDrawable(artworkDrawable);
                if (this.mBackdropFront.getVisibility() == 0) {
                    this.mBackdropFront.animate().setDuration(250L).alpha(0.0f).withEndAction(this.mHideBackdropFront);
                    return;
                }
                return;
            }
            return;
        }
        if (this.mBackdrop.getVisibility() != 8) {
            if (shadeController == null || !shadeController.isDozing() || ScrimState.AOD.getAnimateChange()) {
                cannotAnimateDoze = false;
            }
            boolean needsBypassFading = this.mKeyguardMonitor.isBypassFadingAnimation();
            BiometricUnlockController biometricUnlockController2 = this.mBiometricUnlockController;
            if ((((biometricUnlockController2 != null && biometricUnlockController2.getMode() == 2) || cannotAnimateDoze) && !needsBypassFading) || hideBecauseOccluded) {
                this.mBackdrop.setVisibility(8);
                this.mBackdropBack.setImageDrawable(null);
                if (windowController != null) {
                    windowController.setBackdropShowing(false);
                }
            } else {
                if (windowController != null) {
                    windowController.setBackdropShowing(false);
                }
                this.mBackdrop.animate().alpha(0.0f).setInterpolator(Interpolators.ACCELERATE_DECELERATE).setDuration(300L).setStartDelay(0L).withEndAction(new Runnable() { // from class: com.android.systemui.statusbar.-$$Lambda$NotificationMediaManager$5ApBYxWBRgBH6AkWUHgwLiCFqEk
                    @Override // java.lang.Runnable
                    public final void run() {
                        NotificationMediaManager.this.lambda$finishUpdateMediaMetaData$0$NotificationMediaManager();
                    }
                });
                if (this.mKeyguardMonitor.isKeyguardFadingAway()) {
                    this.mBackdrop.animate().setDuration(this.mKeyguardMonitor.getShortenedFadingAwayDuration()).setStartDelay(this.mKeyguardMonitor.getKeyguardFadingAwayDelay()).setInterpolator(Interpolators.LINEAR).start();
                }
            }
        }
    }

    public /* synthetic */ void lambda$finishUpdateMediaMetaData$0$NotificationMediaManager() {
        this.mBackdrop.setVisibility(8);
        this.mBackdropFront.animate().cancel();
        this.mBackdropBack.setImageDrawable(null);
        this.mHandler.post(this.mHideBackdropFront);
    }

    public void setup(BackDropView backdrop, ImageView backdropFront, ImageView backdropBack, ScrimController scrimController, LockscreenWallpaper lockscreenWallpaper) {
        this.mBackdrop = backdrop;
        this.mBackdropFront = backdropFront;
        this.mBackdropBack = backdropBack;
        this.mScrimController = scrimController;
        this.mLockscreenWallpaper = lockscreenWallpaper;
    }

    public void setBiometricUnlockController(BiometricUnlockController biometricUnlockController) {
        this.mBiometricUnlockController = biometricUnlockController;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Bitmap processArtwork(Bitmap artwork) {
        return this.mMediaArtworkProcessor.processArtwork(this.mContext, artwork);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeTask(AsyncTask<?, ?, ?> task) {
        this.mProcessArtworkTasks.remove(task);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static final class ProcessArtworkTask extends AsyncTask<Bitmap, Void, Bitmap> {
        private final boolean mAllowEnterAnimation;
        private final WeakReference<NotificationMediaManager> mManagerRef;
        private final boolean mMetaDataChanged;

        ProcessArtworkTask(NotificationMediaManager manager, boolean changed, boolean allowAnimation) {
            this.mManagerRef = new WeakReference<>(manager);
            this.mMetaDataChanged = changed;
            this.mAllowEnterAnimation = allowAnimation;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Bitmap doInBackground(Bitmap... bitmaps) {
            NotificationMediaManager manager = this.mManagerRef.get();
            if (manager != null && bitmaps.length != 0 && !isCancelled()) {
                return manager.processArtwork(bitmaps[0]);
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Bitmap result) {
            NotificationMediaManager manager = this.mManagerRef.get();
            if (manager != null && !isCancelled()) {
                manager.removeTask(this);
                manager.finishUpdateMediaMetaData(this.mMetaDataChanged, this.mAllowEnterAnimation, result);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onCancelled(Bitmap result) {
            if (result != null) {
                result.recycle();
            }
            NotificationMediaManager manager = this.mManagerRef.get();
            if (manager != null) {
                manager.removeTask(this);
            }
        }
    }
}
