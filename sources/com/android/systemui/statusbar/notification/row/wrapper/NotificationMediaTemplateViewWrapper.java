package com.android.systemui.statusbar.notification.row.wrapper;

import android.content.Context;
import android.content.res.ColorStateList;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.metrics.LogMaker;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.SeekBar;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.widget.MediaNotificationView;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import java.util.Timer;
import java.util.TimerTask;
/* loaded from: classes21.dex */
public class NotificationMediaTemplateViewWrapper extends NotificationTemplateViewWrapper {
    private static final String COMPACT_MEDIA_TAG = "media";
    private static final long PROGRESS_UPDATE_INTERVAL = 1000;
    private View mActions;
    private View.OnAttachStateChangeListener mAttachStateListener;
    private Context mContext;
    private long mDuration;
    private final Handler mHandler;
    private boolean mIsViewVisible;
    private MediaController.Callback mMediaCallback;
    private MediaController mMediaController;
    private NotificationMediaManager mMediaManager;
    private MediaMetadata mMediaMetadata;
    private MetricsLogger mMetricsLogger;
    protected final Runnable mOnUpdateTimerTick;
    private SeekBar mSeekBar;
    private TextView mSeekBarElapsedTime;
    private Timer mSeekBarTimer;
    private TextView mSeekBarTotalTime;
    private View mSeekBarView;
    @VisibleForTesting
    protected SeekBar.OnSeekBarChangeListener mSeekListener;
    private MediaNotificationView.VisibilityChangeListener mVisibilityListener;

    /* JADX INFO: Access modifiers changed from: protected */
    public NotificationMediaTemplateViewWrapper(Context ctx, View view, ExpandableNotificationRow row) {
        super(ctx, view, row);
        this.mHandler = (Handler) Dependency.get(Dependency.MAIN_HANDLER);
        this.mDuration = 0L;
        this.mSeekListener = new SeekBar.OnSeekBarChangeListener() { // from class: com.android.systemui.statusbar.notification.row.wrapper.NotificationMediaTemplateViewWrapper.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (NotificationMediaTemplateViewWrapper.this.mMediaController != null) {
                    NotificationMediaTemplateViewWrapper.this.mMediaController.getTransportControls().seekTo(NotificationMediaTemplateViewWrapper.this.mSeekBar.getProgress());
                    NotificationMediaTemplateViewWrapper.this.mMetricsLogger.write(NotificationMediaTemplateViewWrapper.this.newLog(6));
                }
            }
        };
        this.mVisibilityListener = new MediaNotificationView.VisibilityChangeListener() { // from class: com.android.systemui.statusbar.notification.row.wrapper.NotificationMediaTemplateViewWrapper.2
            public void onAggregatedVisibilityChanged(boolean isVisible) {
                NotificationMediaTemplateViewWrapper.this.mIsViewVisible = isVisible;
                if (!isVisible || NotificationMediaTemplateViewWrapper.this.mMediaController == null) {
                    NotificationMediaTemplateViewWrapper.this.clearTimer();
                    return;
                }
                PlaybackState state = NotificationMediaTemplateViewWrapper.this.mMediaController.getPlaybackState();
                if (state != null && state.getState() == 3 && NotificationMediaTemplateViewWrapper.this.mSeekBarTimer == null && NotificationMediaTemplateViewWrapper.this.mSeekBarView != null && NotificationMediaTemplateViewWrapper.this.mSeekBarView.getVisibility() != 8) {
                    NotificationMediaTemplateViewWrapper.this.startTimer();
                }
            }
        };
        this.mAttachStateListener = new View.OnAttachStateChangeListener() { // from class: com.android.systemui.statusbar.notification.row.wrapper.NotificationMediaTemplateViewWrapper.3
            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewAttachedToWindow(View v) {
            }

            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewDetachedFromWindow(View v) {
                NotificationMediaTemplateViewWrapper.this.mIsViewVisible = false;
            }
        };
        this.mMediaCallback = new MediaController.Callback() { // from class: com.android.systemui.statusbar.notification.row.wrapper.NotificationMediaTemplateViewWrapper.4
            @Override // android.media.session.MediaController.Callback
            public void onSessionDestroyed() {
                NotificationMediaTemplateViewWrapper.this.clearTimer();
                NotificationMediaTemplateViewWrapper.this.mMediaController.unregisterCallback(this);
                if (NotificationMediaTemplateViewWrapper.this.mView instanceof MediaNotificationView) {
                    NotificationMediaTemplateViewWrapper.this.mView.removeVisibilityListener(NotificationMediaTemplateViewWrapper.this.mVisibilityListener);
                    NotificationMediaTemplateViewWrapper.this.mView.removeOnAttachStateChangeListener(NotificationMediaTemplateViewWrapper.this.mAttachStateListener);
                }
            }

            @Override // android.media.session.MediaController.Callback
            public void onPlaybackStateChanged(PlaybackState state) {
                if (state == null) {
                    return;
                }
                if (state.getState() != 3) {
                    NotificationMediaTemplateViewWrapper.this.updatePlaybackUi(state);
                    NotificationMediaTemplateViewWrapper.this.clearTimer();
                } else if (NotificationMediaTemplateViewWrapper.this.mSeekBarTimer == null && NotificationMediaTemplateViewWrapper.this.mSeekBarView != null && NotificationMediaTemplateViewWrapper.this.mSeekBarView.getVisibility() != 8) {
                    NotificationMediaTemplateViewWrapper.this.startTimer();
                }
            }

            @Override // android.media.session.MediaController.Callback
            public void onMetadataChanged(MediaMetadata metadata) {
                if (NotificationMediaTemplateViewWrapper.this.mMediaMetadata == null || !NotificationMediaTemplateViewWrapper.this.mMediaMetadata.equals(metadata)) {
                    NotificationMediaTemplateViewWrapper.this.mMediaMetadata = metadata;
                    NotificationMediaTemplateViewWrapper.this.updateDuration();
                }
            }
        };
        this.mOnUpdateTimerTick = new Runnable() { // from class: com.android.systemui.statusbar.notification.row.wrapper.NotificationMediaTemplateViewWrapper.6
            @Override // java.lang.Runnable
            public void run() {
                if (NotificationMediaTemplateViewWrapper.this.mMediaController == null || NotificationMediaTemplateViewWrapper.this.mSeekBar == null) {
                    NotificationMediaTemplateViewWrapper.this.clearTimer();
                    return;
                }
                PlaybackState playbackState = NotificationMediaTemplateViewWrapper.this.mMediaController.getPlaybackState();
                if (playbackState != null) {
                    NotificationMediaTemplateViewWrapper.this.updatePlaybackUi(playbackState);
                } else {
                    NotificationMediaTemplateViewWrapper.this.clearTimer();
                }
            }
        };
        this.mContext = ctx;
        this.mMediaManager = (NotificationMediaManager) Dependency.get(NotificationMediaManager.class);
        this.mMetricsLogger = (MetricsLogger) Dependency.get(MetricsLogger.class);
        if (this.mView instanceof MediaNotificationView) {
            MediaNotificationView mediaView = this.mView;
            mediaView.addVisibilityListener(this.mVisibilityListener);
            this.mView.addOnAttachStateChangeListener(this.mAttachStateListener);
        }
    }

    private void resolveViews() {
        this.mActions = this.mView.findViewById(16909197);
        this.mIsViewVisible = this.mView.isShown();
        MediaSession.Token token = (MediaSession.Token) this.mRow.getEntry().notification.getNotification().extras.getParcelable("android.mediaSession");
        boolean showCompactSeekbar = this.mMediaManager.getShowCompactMediaSeekbar();
        if (token == null || ("media".equals(this.mView.getTag()) && !showCompactSeekbar)) {
            View view = this.mSeekBarView;
            if (view != null) {
                view.setVisibility(8);
                return;
            }
            return;
        }
        boolean controllerUpdated = false;
        MediaController mediaController = this.mMediaController;
        if (mediaController == null || !mediaController.getSessionToken().equals(token)) {
            MediaController mediaController2 = this.mMediaController;
            if (mediaController2 != null) {
                mediaController2.unregisterCallback(this.mMediaCallback);
            }
            this.mMediaController = new MediaController(this.mContext, token);
            controllerUpdated = true;
        }
        this.mMediaMetadata = this.mMediaController.getMetadata();
        MediaMetadata mediaMetadata = this.mMediaMetadata;
        if (mediaMetadata != null) {
            long duration = mediaMetadata.getLong("android.media.metadata.DURATION");
            if (duration <= 0) {
                View view2 = this.mSeekBarView;
                if (view2 != null && view2.getVisibility() != 8) {
                    this.mSeekBarView.setVisibility(8);
                    this.mMetricsLogger.write(newLog(2));
                    clearTimer();
                    return;
                } else if (this.mSeekBarView == null && controllerUpdated) {
                    this.mMetricsLogger.write(newLog(2));
                    return;
                } else {
                    return;
                }
            }
            View view3 = this.mSeekBarView;
            if (view3 != null && view3.getVisibility() == 8) {
                this.mSeekBarView.setVisibility(0);
                this.mMetricsLogger.write(newLog(1));
                updateDuration();
                startTimer();
            }
        }
        ViewStub stub = (ViewStub) this.mView.findViewById(16909278);
        if (stub instanceof ViewStub) {
            LayoutInflater layoutInflater = LayoutInflater.from(stub.getContext());
            stub.setLayoutInflater(layoutInflater);
            stub.setLayoutResource(17367198);
            this.mSeekBarView = stub.inflate();
            this.mMetricsLogger.write(newLog(1));
            this.mSeekBar = (SeekBar) this.mSeekBarView.findViewById(16909276);
            this.mSeekBar.setOnSeekBarChangeListener(this.mSeekListener);
            this.mSeekBarElapsedTime = (TextView) this.mSeekBarView.findViewById(16909274);
            this.mSeekBarTotalTime = (TextView) this.mSeekBarView.findViewById(16909279);
            if (this.mSeekBarTimer == null) {
                MediaController mediaController3 = this.mMediaController;
                if (mediaController3 != null && canSeekMedia(mediaController3.getPlaybackState())) {
                    this.mMetricsLogger.write(newLog(3, 1));
                } else {
                    setScrubberVisible(false);
                }
                updateDuration();
                startTimer();
                this.mMediaController.registerCallback(this.mMediaCallback);
            }
        }
        updateSeekBarTint(this.mSeekBarView);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startTimer() {
        clearTimer();
        if (this.mIsViewVisible) {
            this.mSeekBarTimer = new Timer(true);
            this.mSeekBarTimer.schedule(new TimerTask() { // from class: com.android.systemui.statusbar.notification.row.wrapper.NotificationMediaTemplateViewWrapper.5
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    NotificationMediaTemplateViewWrapper.this.mHandler.post(NotificationMediaTemplateViewWrapper.this.mOnUpdateTimerTick);
                }
            }, 0L, 1000L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearTimer() {
        Timer timer = this.mSeekBarTimer;
        if (timer != null) {
            timer.cancel();
            this.mSeekBarTimer.purge();
            this.mSeekBarTimer = null;
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper
    public void setRemoved() {
        clearTimer();
        MediaController mediaController = this.mMediaController;
        if (mediaController != null) {
            mediaController.unregisterCallback(this.mMediaCallback);
        }
        if (this.mView instanceof MediaNotificationView) {
            this.mView.removeVisibilityListener(this.mVisibilityListener);
            this.mView.removeOnAttachStateChangeListener(this.mAttachStateListener);
        }
    }

    private boolean canSeekMedia(PlaybackState state) {
        if (state == null) {
            return false;
        }
        long actions = state.getActions();
        return (256 & actions) != 0;
    }

    private void setScrubberVisible(boolean isVisible) {
        SeekBar seekBar = this.mSeekBar;
        if (seekBar == null || seekBar.isEnabled() == isVisible) {
            return;
        }
        this.mSeekBar.getThumb().setAlpha(isVisible ? 255 : 0);
        this.mSeekBar.setEnabled(isVisible);
        this.mMetricsLogger.write(newLog(3, isVisible ? 1 : 0));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDuration() {
        MediaMetadata mediaMetadata = this.mMediaMetadata;
        if (mediaMetadata != null && this.mSeekBar != null) {
            long duration = mediaMetadata.getLong("android.media.metadata.DURATION");
            if (this.mDuration != duration) {
                this.mDuration = duration;
                this.mSeekBar.setMax((int) this.mDuration);
                this.mSeekBarTotalTime.setText(millisecondsToTimeString(duration));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePlaybackUi(PlaybackState state) {
        if (this.mSeekBar == null || this.mSeekBarElapsedTime == null) {
            return;
        }
        long position = state.getPosition();
        this.mSeekBar.setProgress((int) position);
        this.mSeekBarElapsedTime.setText(millisecondsToTimeString(position));
        setScrubberVisible(canSeekMedia(state));
    }

    private String millisecondsToTimeString(long milliseconds) {
        long seconds = milliseconds / 1000;
        String text = DateUtils.formatElapsedTime(seconds);
        return text;
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationTemplateViewWrapper, com.android.systemui.statusbar.notification.row.wrapper.NotificationHeaderViewWrapper, com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper
    public void onContentUpdated(ExpandableNotificationRow row) {
        resolveViews();
        super.onContentUpdated(row);
    }

    private void updateSeekBarTint(View seekBarContainer) {
        if (seekBarContainer == null || getNotificationHeader() == null) {
            return;
        }
        int tintColor = getNotificationHeader().getOriginalIconColor();
        this.mSeekBarElapsedTime.setTextColor(tintColor);
        this.mSeekBarTotalTime.setTextColor(tintColor);
        this.mSeekBarTotalTime.setShadowLayer(1.5f, 1.5f, 1.5f, this.mBackgroundColor);
        ColorStateList tintList = ColorStateList.valueOf(tintColor);
        this.mSeekBar.setThumbTintList(tintList);
        ColorStateList tintList2 = tintList.withAlpha(192);
        this.mSeekBar.setProgressTintList(tintList2);
        this.mSeekBar.setProgressBackgroundTintList(tintList2.withAlpha(128));
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationTemplateViewWrapper, com.android.systemui.statusbar.notification.row.wrapper.NotificationHeaderViewWrapper
    protected void updateTransformedTypes() {
        super.updateTransformedTypes();
        if (this.mActions != null) {
            this.mTransformationHelper.addTransformedView(5, this.mActions);
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper
    public boolean isDimmable() {
        return getCustomBackgroundColor() == 0;
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationTemplateViewWrapper, com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper
    public boolean shouldClipToRounding(boolean topRounded, boolean bottomRounded) {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public LogMaker newLog(int event) {
        String packageName = this.mRow.getEntry().notification.getPackageName();
        return new LogMaker(1743).setType(event).setPackageName(packageName);
    }

    private LogMaker newLog(int event, int subtype) {
        String packageName = this.mRow.getEntry().notification.getPackageName();
        return new LogMaker(1743).setType(event).setSubtype(subtype).setPackageName(packageName);
    }
}
