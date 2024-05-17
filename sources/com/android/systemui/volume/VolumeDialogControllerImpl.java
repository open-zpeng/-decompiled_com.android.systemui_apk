package com.android.systemui.volume;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.AudioSystem;
import android.media.IAudioService;
import android.media.IVolumeController;
import android.media.VolumePolicy;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.service.notification.Condition;
import android.service.notification.ZenModeConfig;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import androidx.mediarouter.media.MediaRouterJellybean;
import androidx.mediarouter.media.SystemMediaRouteProvider;
import com.android.internal.annotations.GuardedBy;
import com.android.settingslib.volume.MediaSessions;
import com.android.systemui.Dumpable;
import com.android.systemui.R;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.plugins.VolumeDialogController;
import com.android.systemui.qs.tiles.DndTile;
import com.android.systemui.statusbar.phone.StatusBar;
import com.xiaopeng.speech.speechwidget.ListWidget;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class VolumeDialogControllerImpl implements VolumeDialogController, Dumpable {
    private static final int DYNAMIC_STREAM_START_INDEX = 100;
    private static final int TOUCH_FEEDBACK_TIMEOUT_MS = 1000;
    private static final int VIBRATE_HINT_DURATION = 50;
    private AudioManager mAudio;
    private IAudioService mAudioService;
    private final Context mContext;
    private boolean mDestroyed;
    private final boolean mHasVibrator;
    private long mLastToggledRingerOn;
    private final MediaSessions mMediaSessions;
    private final NotificationManager mNoMan;
    private final NotificationManager mNotificationManager;
    private final SettingObserver mObserver;
    private boolean mShowA11yStream;
    private boolean mShowSafetyWarning;
    private boolean mShowVolumeDialog;
    protected StatusBar mStatusBar;
    @GuardedBy({"this"})
    private UserActivityListener mUserActivityListener;
    private final Vibrator mVibrator;
    private VolumePolicy mVolumePolicy;
    private final W mWorker;
    private final HandlerThread mWorkerThread;
    private static final String TAG = Util.logTag(VolumeDialogControllerImpl.class);
    private static final AudioAttributes SONIFICIATION_VIBRATION_ATTRIBUTES = new AudioAttributes.Builder().setContentType(4).setUsage(13).build();
    static final ArrayMap<Integer, Integer> STREAMS = new ArrayMap<>();
    private final Receiver mReceiver = new Receiver();
    protected C mCallbacks = new C();
    private final VolumeDialogController.State mState = new VolumeDialogController.State();
    protected final MediaSessionsCallbacks mMediaSessionsCallbacksW = new MediaSessionsCallbacks();
    private boolean mShowDndTile = true;
    protected final VC mVolumeController = new VC();

    /* loaded from: classes21.dex */
    public interface UserActivityListener {
        void onUserActivity();
    }

    static {
        STREAMS.put(4, Integer.valueOf(R.string.stream_alarm));
        STREAMS.put(6, Integer.valueOf(R.string.stream_bluetooth_sco));
        STREAMS.put(8, Integer.valueOf(R.string.stream_dtmf));
        STREAMS.put(3, Integer.valueOf(R.string.stream_music));
        STREAMS.put(10, Integer.valueOf(R.string.stream_accessibility));
        STREAMS.put(5, Integer.valueOf(R.string.stream_notification));
        STREAMS.put(2, Integer.valueOf(R.string.stream_ring));
        STREAMS.put(1, Integer.valueOf(R.string.stream_system));
        STREAMS.put(7, Integer.valueOf(R.string.stream_system_enforced));
        STREAMS.put(9, Integer.valueOf(R.string.stream_tts));
        STREAMS.put(0, Integer.valueOf(R.string.stream_voice_call));
    }

    @Inject
    public VolumeDialogControllerImpl(Context context) {
        this.mContext = context.getApplicationContext();
        this.mNotificationManager = (NotificationManager) this.mContext.getSystemService("notification");
        Events.writeEvent(this.mContext, 5, new Object[0]);
        this.mWorkerThread = new HandlerThread(VolumeDialogControllerImpl.class.getSimpleName());
        this.mWorkerThread.start();
        this.mWorker = new W(this.mWorkerThread.getLooper());
        this.mMediaSessions = createMediaSessions(this.mContext, this.mWorkerThread.getLooper(), this.mMediaSessionsCallbacksW);
        this.mAudio = (AudioManager) this.mContext.getSystemService(ListWidget.EXTRA_TYPE_AUDIO);
        this.mNoMan = (NotificationManager) this.mContext.getSystemService("notification");
        this.mObserver = new SettingObserver(this.mWorker);
        this.mObserver.init();
        this.mReceiver.init();
        this.mVibrator = (Vibrator) this.mContext.getSystemService("vibrator");
        Vibrator vibrator = this.mVibrator;
        this.mHasVibrator = vibrator != null && vibrator.hasVibrator();
        this.mAudioService = IAudioService.Stub.asInterface(ServiceManager.getService(ListWidget.EXTRA_TYPE_AUDIO));
        updateStatusBar();
        boolean accessibilityVolumeStreamActive = ((AccessibilityManager) context.getSystemService(AccessibilityManager.class)).isAccessibilityVolumeStreamActive();
        this.mVolumeController.setA11yMode(accessibilityVolumeStreamActive ? 1 : 0);
    }

    @Override // com.android.systemui.plugins.VolumeDialogController
    public AudioManager getAudioManager() {
        return this.mAudio;
    }

    public void dismiss() {
        this.mCallbacks.onDismissRequested(2);
    }

    protected void setVolumeController() {
        try {
            this.mAudio.setVolumeController(this.mVolumeController);
        } catch (SecurityException e) {
            Log.w(TAG, "Unable to set the volume controller", e);
        }
    }

    protected void setAudioManagerStreamVolume(int stream, int level, int flag) {
        this.mAudio.setStreamVolume(stream, level, flag);
    }

    protected int getAudioManagerStreamVolume(int stream) {
        return this.mAudio.getLastAudibleStreamVolume(stream);
    }

    protected int getAudioManagerStreamMaxVolume(int stream) {
        return this.mAudio.getStreamMaxVolume(stream);
    }

    protected int getAudioManagerStreamMinVolume(int stream) {
        return this.mAudio.getStreamMinVolumeInt(stream);
    }

    public void register() {
        setVolumeController();
        setVolumePolicy(this.mVolumePolicy);
        showDndTile(this.mShowDndTile);
        try {
            this.mMediaSessions.init();
        } catch (SecurityException e) {
            Log.w(TAG, "No access to media sessions", e);
        }
    }

    public void setVolumePolicy(VolumePolicy policy) {
        this.mVolumePolicy = policy;
        VolumePolicy volumePolicy = this.mVolumePolicy;
        if (volumePolicy == null) {
            return;
        }
        try {
            this.mAudio.setVolumePolicy(volumePolicy);
        } catch (NoSuchMethodError e) {
            Log.w(TAG, "No volume policy api");
        }
    }

    protected MediaSessions createMediaSessions(Context context, Looper looper, MediaSessions.Callbacks callbacks) {
        return new MediaSessions(context, looper, callbacks);
    }

    public void destroy() {
        if (D.BUG) {
            Log.d(TAG, "destroy");
        }
        if (this.mDestroyed) {
            return;
        }
        this.mDestroyed = true;
        Events.writeEvent(this.mContext, 6, new Object[0]);
        this.mMediaSessions.destroy();
        this.mObserver.destroy();
        this.mReceiver.destroy();
        this.mWorkerThread.quitSafely();
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println(VolumeDialogControllerImpl.class.getSimpleName() + " state:");
        pw.print("  mDestroyed: ");
        pw.println(this.mDestroyed);
        pw.print("  mVolumePolicy: ");
        pw.println(this.mVolumePolicy);
        pw.print("  mState: ");
        pw.println(this.mState.toString(4));
        pw.print("  mShowDndTile: ");
        pw.println(this.mShowDndTile);
        pw.print("  mHasVibrator: ");
        pw.println(this.mHasVibrator);
        pw.print("  mRemoteStreams: ");
        pw.println(this.mMediaSessionsCallbacksW.mRemoteStreams.values());
        pw.print("  mShowA11yStream: ");
        pw.println(this.mShowA11yStream);
        pw.println();
        this.mMediaSessions.dump(pw);
    }

    @Override // com.android.systemui.plugins.VolumeDialogController
    public void addCallback(VolumeDialogController.Callbacks callback, Handler handler) {
        this.mCallbacks.add(callback, handler);
        callback.onAccessibilityModeChanged(Boolean.valueOf(this.mShowA11yStream));
    }

    public void setUserActivityListener(UserActivityListener listener) {
        if (this.mDestroyed) {
            return;
        }
        synchronized (this) {
            this.mUserActivityListener = listener;
        }
    }

    @Override // com.android.systemui.plugins.VolumeDialogController
    public void removeCallback(VolumeDialogController.Callbacks callback) {
        this.mCallbacks.remove(callback);
    }

    @Override // com.android.systemui.plugins.VolumeDialogController
    public void getState() {
        if (this.mDestroyed) {
            return;
        }
        this.mWorker.sendEmptyMessage(3);
    }

    @Override // com.android.systemui.plugins.VolumeDialogController
    public boolean areCaptionsEnabled() {
        int currentValue = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "odi_captions_enabled", 0, -2);
        return currentValue == 1;
    }

    @Override // com.android.systemui.plugins.VolumeDialogController
    public void setCaptionsEnabled(boolean isEnabled) {
        Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "odi_captions_enabled", isEnabled ? 1 : 0, -2);
    }

    @Override // com.android.systemui.plugins.VolumeDialogController
    public boolean isCaptionStreamOptedOut() {
        return false;
    }

    @Override // com.android.systemui.plugins.VolumeDialogController
    public void getCaptionsComponentState(boolean fromTooltip) {
        if (this.mDestroyed) {
            return;
        }
        this.mWorker.obtainMessage(16, Boolean.valueOf(fromTooltip)).sendToTarget();
    }

    @Override // com.android.systemui.plugins.VolumeDialogController
    public void notifyVisible(boolean visible) {
        if (this.mDestroyed) {
            return;
        }
        this.mWorker.obtainMessage(12, visible ? 1 : 0, 0).sendToTarget();
    }

    @Override // com.android.systemui.plugins.VolumeDialogController
    public void userActivity() {
        if (this.mDestroyed) {
            return;
        }
        this.mWorker.removeMessages(13);
        this.mWorker.sendEmptyMessage(13);
    }

    @Override // com.android.systemui.plugins.VolumeDialogController
    public void setRingerMode(int value, boolean external) {
        if (this.mDestroyed) {
            return;
        }
        this.mWorker.obtainMessage(4, value, external ? 1 : 0).sendToTarget();
    }

    public void setZenMode(int value) {
        if (this.mDestroyed) {
            return;
        }
        this.mWorker.obtainMessage(5, value, 0).sendToTarget();
    }

    public void setExitCondition(Condition condition) {
        if (this.mDestroyed) {
            return;
        }
        this.mWorker.obtainMessage(6, condition).sendToTarget();
    }

    public void setStreamMute(int stream, boolean mute) {
        if (this.mDestroyed) {
            return;
        }
        this.mWorker.obtainMessage(7, stream, mute ? 1 : 0).sendToTarget();
    }

    @Override // com.android.systemui.plugins.VolumeDialogController
    public void setStreamVolume(int stream, int level) {
        if (this.mDestroyed) {
            return;
        }
        this.mWorker.obtainMessage(10, stream, level).sendToTarget();
    }

    @Override // com.android.systemui.plugins.VolumeDialogController
    public void setActiveStream(int stream) {
        if (this.mDestroyed) {
            return;
        }
        this.mWorker.obtainMessage(11, stream, 0).sendToTarget();
    }

    public void setEnableDialogs(boolean volumeUi, boolean safetyWarning) {
        this.mShowVolumeDialog = volumeUi;
        this.mShowSafetyWarning = safetyWarning;
    }

    @Override // com.android.systemui.plugins.VolumeDialogController
    public void scheduleTouchFeedback() {
        this.mLastToggledRingerOn = System.currentTimeMillis();
    }

    private void playTouchFeedback() {
        if (System.currentTimeMillis() - this.mLastToggledRingerOn < 1000) {
            try {
                this.mAudioService.playSoundEffect(5);
            } catch (RemoteException e) {
            }
        }
    }

    @Override // com.android.systemui.plugins.VolumeDialogController
    public void vibrate(VibrationEffect effect) {
        if (this.mHasVibrator) {
            this.mVibrator.vibrate(effect, SONIFICIATION_VIBRATION_ATTRIBUTES);
        }
    }

    @Override // com.android.systemui.plugins.VolumeDialogController
    public boolean hasVibrator() {
        return this.mHasVibrator;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNotifyVisibleW(boolean visible) {
        if (this.mDestroyed) {
            return;
        }
        this.mAudio.notifyVolumeControllerVisible(this.mVolumeController, visible);
        if (!visible && updateActiveStreamW(-1)) {
            this.mCallbacks.onStateChanged(this.mState);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUserActivityW() {
        synchronized (this) {
            if (this.mUserActivityListener != null) {
                this.mUserActivityListener.onUserActivity();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onShowSafetyWarningW(int flags) {
        if (this.mShowSafetyWarning) {
            this.mCallbacks.onShowSafetyWarning(flags);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onGetCaptionsComponentStateW(boolean fromTooltip) {
        try {
            String componentNameString = this.mContext.getString(17039714);
            if (TextUtils.isEmpty(componentNameString)) {
                this.mCallbacks.onCaptionComponentStateChanged(false, Boolean.valueOf(fromTooltip));
                return;
            }
            boolean z = true;
            if (D.BUG) {
                Log.i(TAG, String.format("isCaptionsServiceEnabled componentNameString=%s", componentNameString));
            }
            ComponentName componentName = ComponentName.unflattenFromString(componentNameString);
            if (componentName == null) {
                this.mCallbacks.onCaptionComponentStateChanged(false, Boolean.valueOf(fromTooltip));
                return;
            }
            PackageManager packageManager = this.mContext.getPackageManager();
            C c = this.mCallbacks;
            if (packageManager.getComponentEnabledSetting(componentName) != 1) {
                z = false;
            }
            c.onCaptionComponentStateChanged(Boolean.valueOf(z), Boolean.valueOf(fromTooltip));
        } catch (Exception ex) {
            Log.e(TAG, "isCaptionsServiceEnabled failed to check for captions component", ex);
            this.mCallbacks.onCaptionComponentStateChanged(false, Boolean.valueOf(fromTooltip));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAccessibilityModeChanged(Boolean showA11yStream) {
        this.mCallbacks.onAccessibilityModeChanged(showA11yStream);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkRoutedToBluetoothW(int stream) {
        if (stream != 3) {
            return false;
        }
        boolean routedToBluetooth = (this.mAudio.getDevicesForStream(3) & MediaRouterJellybean.DEVICE_OUT_BLUETOOTH) != 0;
        boolean changed = false | updateStreamRoutedToBluetoothW(stream, routedToBluetooth);
        return changed;
    }

    private void updateStatusBar() {
        if (this.mStatusBar == null) {
            this.mStatusBar = (StatusBar) SysUiServiceProvider.getComponent(this.mContext, StatusBar.class);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean shouldShowUI(int flags) {
        updateStatusBar();
        StatusBar statusBar = this.mStatusBar;
        return statusBar != null ? (statusBar.getWakefulnessState() == 0 || this.mStatusBar.getWakefulnessState() == 3 || !this.mStatusBar.isDeviceInteractive() || (flags & 1) == 0 || !this.mShowVolumeDialog) ? false : true : this.mShowVolumeDialog && (flags & 1) != 0;
    }

    boolean onVolumeChangedW(int stream, int flags) {
        boolean showUI = shouldShowUI(flags);
        boolean fromKey = (flags & 4096) != 0;
        boolean showVibrateHint = (flags & 2048) != 0;
        boolean showSilentHint = (flags & 128) != 0;
        boolean changed = showUI ? false | updateActiveStreamW(stream) : false;
        int lastAudibleStreamVolume = getAudioManagerStreamVolume(stream);
        boolean changed2 = changed | updateStreamLevelW(stream, lastAudibleStreamVolume) | checkRoutedToBluetoothW(showUI ? 3 : stream);
        if (changed2) {
            this.mCallbacks.onStateChanged(this.mState);
        }
        if (showUI) {
            this.mCallbacks.onShowRequested(1);
        }
        if (showVibrateHint) {
            this.mCallbacks.onShowVibrateHint();
        }
        if (showSilentHint) {
            this.mCallbacks.onShowSilentHint();
        }
        if (changed2 && fromKey) {
            Events.writeEvent(this.mContext, 4, Integer.valueOf(stream), Integer.valueOf(lastAudibleStreamVolume));
        }
        return changed2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean updateActiveStreamW(int activeStream) {
        if (activeStream == this.mState.activeStream) {
            return false;
        }
        this.mState.activeStream = activeStream;
        Events.writeEvent(this.mContext, 2, Integer.valueOf(activeStream));
        if (D.BUG) {
            String str = TAG;
            Log.d(str, "updateActiveStreamW " + activeStream);
        }
        int s = activeStream < 100 ? activeStream : -1;
        if (D.BUG) {
            String str2 = TAG;
            Log.d(str2, "forceVolumeControlStream " + s);
        }
        this.mAudio.forceVolumeControlStream(s);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public VolumeDialogController.StreamState streamStateW(int stream) {
        VolumeDialogController.StreamState ss = this.mState.states.get(stream);
        if (ss == null) {
            VolumeDialogController.StreamState ss2 = new VolumeDialogController.StreamState();
            this.mState.states.put(stream, ss2);
            return ss2;
        }
        return ss;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onGetStateW() {
        for (Integer num : STREAMS.keySet()) {
            int stream = num.intValue();
            updateStreamLevelW(stream, getAudioManagerStreamVolume(stream));
            streamStateW(stream).levelMin = getAudioManagerStreamMinVolume(stream);
            streamStateW(stream).levelMax = Math.max(1, getAudioManagerStreamMaxVolume(stream));
            updateStreamMuteW(stream, this.mAudio.isStreamMute(stream));
            VolumeDialogController.StreamState ss = streamStateW(stream);
            ss.muteSupported = this.mAudio.isStreamAffectedByMute(stream);
            ss.name = STREAMS.get(Integer.valueOf(stream)).intValue();
            checkRoutedToBluetoothW(stream);
        }
        updateRingerModeExternalW(this.mAudio.getRingerMode());
        updateZenModeW();
        updateZenConfig();
        updateEffectsSuppressorW(this.mNoMan.getEffectsSuppressor());
        this.mCallbacks.onStateChanged(this.mState);
    }

    private boolean updateStreamRoutedToBluetoothW(int stream, boolean routedToBluetooth) {
        VolumeDialogController.StreamState ss = streamStateW(stream);
        if (ss.routedToBluetooth == routedToBluetooth) {
            return false;
        }
        ss.routedToBluetooth = routedToBluetooth;
        if (D.BUG) {
            String str = TAG;
            Log.d(str, "updateStreamRoutedToBluetoothW stream=" + stream + " routedToBluetooth=" + routedToBluetooth);
            return true;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean updateStreamLevelW(int stream, int level) {
        VolumeDialogController.StreamState ss = streamStateW(stream);
        if (ss.level == level) {
            return false;
        }
        ss.level = level;
        if (isLogWorthy(stream)) {
            Events.writeEvent(this.mContext, 10, Integer.valueOf(stream), Integer.valueOf(level));
        }
        return true;
    }

    private static boolean isLogWorthy(int stream) {
        if (stream == 0 || stream == 1 || stream == 2 || stream == 3 || stream == 4 || stream == 6) {
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean updateStreamMuteW(int stream, boolean muted) {
        VolumeDialogController.StreamState ss = streamStateW(stream);
        if (ss.muted == muted) {
            return false;
        }
        ss.muted = muted;
        if (isLogWorthy(stream)) {
            Events.writeEvent(this.mContext, 15, Integer.valueOf(stream), Boolean.valueOf(muted));
        }
        if (muted && isRinger(stream)) {
            updateRingerModeInternalW(this.mAudio.getRingerModeInternal());
        }
        return true;
    }

    private static boolean isRinger(int stream) {
        return stream == 2 || stream == 5;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean updateEffectsSuppressorW(ComponentName effectsSuppressor) {
        if (Objects.equals(this.mState.effectsSuppressor, effectsSuppressor)) {
            return false;
        }
        VolumeDialogController.State state = this.mState;
        state.effectsSuppressor = effectsSuppressor;
        state.effectsSuppressorName = getApplicationName(this.mContext, state.effectsSuppressor);
        Events.writeEvent(this.mContext, 14, this.mState.effectsSuppressor, this.mState.effectsSuppressorName);
        return true;
    }

    private static String getApplicationName(Context context, ComponentName component) {
        String rt;
        if (component == null) {
            return null;
        }
        PackageManager pm = context.getPackageManager();
        String pkg = component.getPackageName();
        try {
            ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);
            rt = Objects.toString(ai.loadLabel(pm), "").trim();
        } catch (PackageManager.NameNotFoundException e) {
        }
        if (rt.length() > 0) {
            return rt;
        }
        return pkg;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean updateZenModeW() {
        int zen = Settings.Global.getInt(this.mContext.getContentResolver(), "zen_mode", 0);
        if (this.mState.zenMode == zen) {
            return false;
        }
        this.mState.zenMode = zen;
        Events.writeEvent(this.mContext, 13, Integer.valueOf(zen));
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean updateZenConfig() {
        NotificationManager.Policy policy = this.mNotificationManager.getConsolidatedNotificationPolicy();
        boolean disallowAlarms = (policy.priorityCategories & 32) == 0;
        boolean disallowMedia = (policy.priorityCategories & 64) == 0;
        boolean disallowSystem = (policy.priorityCategories & 128) == 0;
        boolean disallowRinger = ZenModeConfig.areAllPriorityOnlyNotificationZenSoundsMuted(policy);
        if (this.mState.disallowAlarms == disallowAlarms && this.mState.disallowMedia == disallowMedia && this.mState.disallowRinger == disallowRinger && this.mState.disallowSystem == disallowSystem) {
            return false;
        }
        VolumeDialogController.State state = this.mState;
        state.disallowAlarms = disallowAlarms;
        state.disallowMedia = disallowMedia;
        state.disallowSystem = disallowSystem;
        state.disallowRinger = disallowRinger;
        Context context = this.mContext;
        Events.writeEvent(context, 17, "disallowAlarms=" + disallowAlarms + " disallowMedia=" + disallowMedia + " disallowSystem=" + disallowSystem + " disallowRinger=" + disallowRinger);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean updateRingerModeExternalW(int rm) {
        if (rm == this.mState.ringerModeExternal) {
            return false;
        }
        this.mState.ringerModeExternal = rm;
        Events.writeEvent(this.mContext, 12, Integer.valueOf(rm));
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean updateRingerModeInternalW(int rm) {
        if (rm == this.mState.ringerModeInternal) {
            return false;
        }
        this.mState.ringerModeInternal = rm;
        Events.writeEvent(this.mContext, 11, Integer.valueOf(rm));
        if (this.mState.ringerModeInternal == 2) {
            playTouchFeedback();
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSetRingerModeW(int mode, boolean external) {
        if (external) {
            this.mAudio.setRingerMode(mode);
        } else {
            this.mAudio.setRingerModeInternal(mode);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSetStreamMuteW(int stream, boolean mute) {
        this.mAudio.adjustStreamVolume(stream, mute ? -100 : 100, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSetStreamVolumeW(int stream, int level) {
        if (D.BUG) {
            String str = TAG;
            Log.d(str, "onSetStreamVolume " + stream + " level=" + level);
        }
        if (stream >= 100) {
            this.mMediaSessionsCallbacksW.setStreamVolume(stream, level);
        } else {
            setAudioManagerStreamVolume(stream, level, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSetActiveStreamW(int stream) {
        boolean changed = updateActiveStreamW(stream);
        if (changed) {
            this.mCallbacks.onStateChanged(this.mState);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSetExitConditionW(Condition condition) {
        this.mNoMan.setZenMode(this.mState.zenMode, condition != null ? condition.id : null, TAG);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSetZenModeW(int mode) {
        if (D.BUG) {
            String str = TAG;
            Log.d(str, "onSetZenModeW " + mode);
        }
        this.mNoMan.setZenMode(mode, null, TAG);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDismissRequestedW(int reason) {
        this.mCallbacks.onDismissRequested(reason);
    }

    public void showDndTile(boolean visible) {
        if (D.BUG) {
            Log.d(TAG, "showDndTile");
        }
        DndTile.setVisible(this.mContext, visible);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public final class VC extends IVolumeController.Stub {
        private final String TAG;

        private VC() {
            this.TAG = VolumeDialogControllerImpl.TAG + ".VC";
        }

        public void displaySafeVolumeWarning(int flags) throws RemoteException {
            if (D.BUG) {
                String str = this.TAG;
                Log.d(str, "displaySafeVolumeWarning " + Util.audioManagerFlagsToString(flags));
            }
            if (VolumeDialogControllerImpl.this.mDestroyed) {
                return;
            }
            VolumeDialogControllerImpl.this.mWorker.obtainMessage(14, flags, 0).sendToTarget();
        }

        public void volumeChanged(int streamType, int flags) throws RemoteException {
            if (D.BUG) {
                String str = this.TAG;
                Log.d(str, "volumeChanged " + AudioSystem.streamToString(streamType) + " " + Util.audioManagerFlagsToString(flags));
            }
            if (VolumeDialogControllerImpl.this.mDestroyed) {
                return;
            }
            VolumeDialogControllerImpl.this.mWorker.obtainMessage(1, streamType, flags).sendToTarget();
        }

        public void masterMuteChanged(int flags) throws RemoteException {
            if (D.BUG) {
                Log.d(this.TAG, "masterMuteChanged");
            }
        }

        public void setLayoutDirection(int layoutDirection) throws RemoteException {
            if (D.BUG) {
                Log.d(this.TAG, "setLayoutDirection");
            }
            if (VolumeDialogControllerImpl.this.mDestroyed) {
                return;
            }
            VolumeDialogControllerImpl.this.mWorker.obtainMessage(8, layoutDirection, 0).sendToTarget();
        }

        public void dismiss() throws RemoteException {
            if (D.BUG) {
                Log.d(this.TAG, "dismiss requested");
            }
            if (VolumeDialogControllerImpl.this.mDestroyed) {
                return;
            }
            VolumeDialogControllerImpl.this.mWorker.obtainMessage(2, 2, 0).sendToTarget();
            VolumeDialogControllerImpl.this.mWorker.sendEmptyMessage(2);
        }

        public void setA11yMode(int mode) {
            if (D.BUG) {
                String str = this.TAG;
                Log.d(str, "setA11yMode to " + mode);
            }
            if (VolumeDialogControllerImpl.this.mDestroyed) {
                return;
            }
            if (mode == 0) {
                VolumeDialogControllerImpl.this.mShowA11yStream = false;
            } else if (mode == 1) {
                VolumeDialogControllerImpl.this.mShowA11yStream = true;
            } else {
                String str2 = this.TAG;
                Log.e(str2, "Invalid accessibility mode " + mode);
            }
            VolumeDialogControllerImpl.this.mWorker.obtainMessage(15, Boolean.valueOf(VolumeDialogControllerImpl.this.mShowA11yStream)).sendToTarget();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public final class W extends Handler {
        private static final int ACCESSIBILITY_MODE_CHANGED = 15;
        private static final int CONFIGURATION_CHANGED = 9;
        private static final int DISMISS_REQUESTED = 2;
        private static final int GET_CAPTIONS_COMPONENT_STATE = 16;
        private static final int GET_STATE = 3;
        private static final int LAYOUT_DIRECTION_CHANGED = 8;
        private static final int NOTIFY_VISIBLE = 12;
        private static final int SET_ACTIVE_STREAM = 11;
        private static final int SET_EXIT_CONDITION = 6;
        private static final int SET_RINGER_MODE = 4;
        private static final int SET_STREAM_MUTE = 7;
        private static final int SET_STREAM_VOLUME = 10;
        private static final int SET_ZEN_MODE = 5;
        private static final int SHOW_SAFETY_WARNING = 14;
        private static final int USER_ACTIVITY = 13;
        private static final int VOLUME_CHANGED = 1;

        W(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    VolumeDialogControllerImpl.this.onVolumeChangedW(msg.arg1, msg.arg2);
                    return;
                case 2:
                    VolumeDialogControllerImpl.this.onDismissRequestedW(msg.arg1);
                    return;
                case 3:
                    VolumeDialogControllerImpl.this.onGetStateW();
                    return;
                case 4:
                    VolumeDialogControllerImpl.this.onSetRingerModeW(msg.arg1, msg.arg2 != 0);
                    return;
                case 5:
                    VolumeDialogControllerImpl.this.onSetZenModeW(msg.arg1);
                    return;
                case 6:
                    VolumeDialogControllerImpl.this.onSetExitConditionW((Condition) msg.obj);
                    return;
                case 7:
                    VolumeDialogControllerImpl.this.onSetStreamMuteW(msg.arg1, msg.arg2 != 0);
                    return;
                case 8:
                    VolumeDialogControllerImpl.this.mCallbacks.onLayoutDirectionChanged(msg.arg1);
                    return;
                case 9:
                    VolumeDialogControllerImpl.this.mCallbacks.onConfigurationChanged();
                    return;
                case 10:
                    VolumeDialogControllerImpl.this.onSetStreamVolumeW(msg.arg1, msg.arg2);
                    return;
                case 11:
                    VolumeDialogControllerImpl.this.onSetActiveStreamW(msg.arg1);
                    return;
                case 12:
                    VolumeDialogControllerImpl.this.onNotifyVisibleW(msg.arg1 != 0);
                    return;
                case 13:
                    VolumeDialogControllerImpl.this.onUserActivityW();
                    return;
                case 14:
                    VolumeDialogControllerImpl.this.onShowSafetyWarningW(msg.arg1);
                    return;
                case 15:
                    VolumeDialogControllerImpl.this.onAccessibilityModeChanged((Boolean) msg.obj);
                    return;
                case 16:
                    VolumeDialogControllerImpl.this.onGetCaptionsComponentStateW(((Boolean) msg.obj).booleanValue());
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public class C implements VolumeDialogController.Callbacks {
        private final HashMap<VolumeDialogController.Callbacks, Handler> mCallbackMap = new HashMap<>();

        C() {
        }

        public void add(VolumeDialogController.Callbacks callback, Handler handler) {
            if (callback == null || handler == null) {
                throw new IllegalArgumentException();
            }
            this.mCallbackMap.put(callback, handler);
        }

        public void remove(VolumeDialogController.Callbacks callback) {
            this.mCallbackMap.remove(callback);
        }

        @Override // com.android.systemui.plugins.VolumeDialogController.Callbacks
        public void onShowRequested(final int reason) {
            for (final Map.Entry<VolumeDialogController.Callbacks, Handler> entry : this.mCallbackMap.entrySet()) {
                entry.getValue().post(new Runnable() { // from class: com.android.systemui.volume.VolumeDialogControllerImpl.C.1
                    @Override // java.lang.Runnable
                    public void run() {
                        ((VolumeDialogController.Callbacks) entry.getKey()).onShowRequested(reason);
                    }
                });
            }
        }

        @Override // com.android.systemui.plugins.VolumeDialogController.Callbacks
        public void onDismissRequested(final int reason) {
            for (final Map.Entry<VolumeDialogController.Callbacks, Handler> entry : this.mCallbackMap.entrySet()) {
                entry.getValue().post(new Runnable() { // from class: com.android.systemui.volume.VolumeDialogControllerImpl.C.2
                    @Override // java.lang.Runnable
                    public void run() {
                        ((VolumeDialogController.Callbacks) entry.getKey()).onDismissRequested(reason);
                    }
                });
            }
        }

        @Override // com.android.systemui.plugins.VolumeDialogController.Callbacks
        public void onStateChanged(VolumeDialogController.State state) {
            long time = System.currentTimeMillis();
            final VolumeDialogController.State copy = state.copy();
            for (final Map.Entry<VolumeDialogController.Callbacks, Handler> entry : this.mCallbackMap.entrySet()) {
                entry.getValue().post(new Runnable() { // from class: com.android.systemui.volume.VolumeDialogControllerImpl.C.3
                    @Override // java.lang.Runnable
                    public void run() {
                        ((VolumeDialogController.Callbacks) entry.getKey()).onStateChanged(copy);
                    }
                });
            }
            Events.writeState(time, copy);
        }

        @Override // com.android.systemui.plugins.VolumeDialogController.Callbacks
        public void onLayoutDirectionChanged(final int layoutDirection) {
            for (final Map.Entry<VolumeDialogController.Callbacks, Handler> entry : this.mCallbackMap.entrySet()) {
                entry.getValue().post(new Runnable() { // from class: com.android.systemui.volume.VolumeDialogControllerImpl.C.4
                    @Override // java.lang.Runnable
                    public void run() {
                        ((VolumeDialogController.Callbacks) entry.getKey()).onLayoutDirectionChanged(layoutDirection);
                    }
                });
            }
        }

        @Override // com.android.systemui.plugins.VolumeDialogController.Callbacks
        public void onConfigurationChanged() {
            for (final Map.Entry<VolumeDialogController.Callbacks, Handler> entry : this.mCallbackMap.entrySet()) {
                entry.getValue().post(new Runnable() { // from class: com.android.systemui.volume.VolumeDialogControllerImpl.C.5
                    @Override // java.lang.Runnable
                    public void run() {
                        ((VolumeDialogController.Callbacks) entry.getKey()).onConfigurationChanged();
                    }
                });
            }
        }

        @Override // com.android.systemui.plugins.VolumeDialogController.Callbacks
        public void onShowVibrateHint() {
            for (final Map.Entry<VolumeDialogController.Callbacks, Handler> entry : this.mCallbackMap.entrySet()) {
                entry.getValue().post(new Runnable() { // from class: com.android.systemui.volume.VolumeDialogControllerImpl.C.6
                    @Override // java.lang.Runnable
                    public void run() {
                        ((VolumeDialogController.Callbacks) entry.getKey()).onShowVibrateHint();
                    }
                });
            }
        }

        @Override // com.android.systemui.plugins.VolumeDialogController.Callbacks
        public void onShowSilentHint() {
            for (final Map.Entry<VolumeDialogController.Callbacks, Handler> entry : this.mCallbackMap.entrySet()) {
                entry.getValue().post(new Runnable() { // from class: com.android.systemui.volume.VolumeDialogControllerImpl.C.7
                    @Override // java.lang.Runnable
                    public void run() {
                        ((VolumeDialogController.Callbacks) entry.getKey()).onShowSilentHint();
                    }
                });
            }
        }

        @Override // com.android.systemui.plugins.VolumeDialogController.Callbacks
        public void onScreenOff() {
            for (final Map.Entry<VolumeDialogController.Callbacks, Handler> entry : this.mCallbackMap.entrySet()) {
                entry.getValue().post(new Runnable() { // from class: com.android.systemui.volume.VolumeDialogControllerImpl.C.8
                    @Override // java.lang.Runnable
                    public void run() {
                        ((VolumeDialogController.Callbacks) entry.getKey()).onScreenOff();
                    }
                });
            }
        }

        @Override // com.android.systemui.plugins.VolumeDialogController.Callbacks
        public void onShowSafetyWarning(final int flags) {
            for (final Map.Entry<VolumeDialogController.Callbacks, Handler> entry : this.mCallbackMap.entrySet()) {
                entry.getValue().post(new Runnable() { // from class: com.android.systemui.volume.VolumeDialogControllerImpl.C.9
                    @Override // java.lang.Runnable
                    public void run() {
                        ((VolumeDialogController.Callbacks) entry.getKey()).onShowSafetyWarning(flags);
                    }
                });
            }
        }

        @Override // com.android.systemui.plugins.VolumeDialogController.Callbacks
        public void onAccessibilityModeChanged(Boolean showA11yStream) {
            final boolean show = showA11yStream == null ? false : showA11yStream.booleanValue();
            for (final Map.Entry<VolumeDialogController.Callbacks, Handler> entry : this.mCallbackMap.entrySet()) {
                entry.getValue().post(new Runnable() { // from class: com.android.systemui.volume.VolumeDialogControllerImpl.C.10
                    @Override // java.lang.Runnable
                    public void run() {
                        ((VolumeDialogController.Callbacks) entry.getKey()).onAccessibilityModeChanged(Boolean.valueOf(show));
                    }
                });
            }
        }

        @Override // com.android.systemui.plugins.VolumeDialogController.Callbacks
        public void onCaptionComponentStateChanged(Boolean isComponentEnabled, final Boolean fromTooltip) {
            final boolean componentEnabled = isComponentEnabled == null ? false : isComponentEnabled.booleanValue();
            for (final Map.Entry<VolumeDialogController.Callbacks, Handler> entry : this.mCallbackMap.entrySet()) {
                entry.getValue().post(new Runnable() { // from class: com.android.systemui.volume.-$$Lambda$VolumeDialogControllerImpl$C$Q4oXmUMuqtOXvcXaIdydaXsm_80
                    @Override // java.lang.Runnable
                    public final void run() {
                        ((VolumeDialogController.Callbacks) entry.getKey()).onCaptionComponentStateChanged(Boolean.valueOf(componentEnabled), fromTooltip);
                    }
                });
            }
        }
    }

    /* loaded from: classes21.dex */
    private final class SettingObserver extends ContentObserver {
        private final Uri ZEN_MODE_CONFIG_URI;
        private final Uri ZEN_MODE_URI;

        public SettingObserver(Handler handler) {
            super(handler);
            this.ZEN_MODE_URI = Settings.Global.getUriFor("zen_mode");
            this.ZEN_MODE_CONFIG_URI = Settings.Global.getUriFor("zen_mode_config_etag");
        }

        public void init() {
            VolumeDialogControllerImpl.this.mContext.getContentResolver().registerContentObserver(this.ZEN_MODE_URI, false, this);
            VolumeDialogControllerImpl.this.mContext.getContentResolver().registerContentObserver(this.ZEN_MODE_CONFIG_URI, false, this);
        }

        public void destroy() {
            VolumeDialogControllerImpl.this.mContext.getContentResolver().unregisterContentObserver(this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            boolean changed = false;
            if (this.ZEN_MODE_URI.equals(uri)) {
                changed = VolumeDialogControllerImpl.this.updateZenModeW();
            }
            if (this.ZEN_MODE_CONFIG_URI.equals(uri)) {
                changed |= VolumeDialogControllerImpl.this.updateZenConfig();
            }
            if (changed) {
                VolumeDialogControllerImpl.this.mCallbacks.onStateChanged(VolumeDialogControllerImpl.this.mState);
            }
        }
    }

    /* loaded from: classes21.dex */
    private final class Receiver extends BroadcastReceiver {
        private Receiver() {
        }

        public void init() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(SystemMediaRouteProvider.LegacyImpl.VolumeChangeReceiver.VOLUME_CHANGED_ACTION);
            filter.addAction("android.media.STREAM_DEVICES_CHANGED_ACTION");
            filter.addAction("android.media.RINGER_MODE_CHANGED");
            filter.addAction("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION");
            filter.addAction("android.media.STREAM_MUTE_CHANGED_ACTION");
            filter.addAction("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED");
            filter.addAction("android.intent.action.CONFIGURATION_CHANGED");
            filter.addAction("android.intent.action.SCREEN_OFF");
            filter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
            VolumeDialogControllerImpl.this.mContext.registerReceiver(this, filter, null, VolumeDialogControllerImpl.this.mWorker);
        }

        public void destroy() {
            VolumeDialogControllerImpl.this.mContext.unregisterReceiver(this);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            boolean changed = false;
            if (action.equals(SystemMediaRouteProvider.LegacyImpl.VolumeChangeReceiver.VOLUME_CHANGED_ACTION)) {
                int stream = intent.getIntExtra(SystemMediaRouteProvider.LegacyImpl.VolumeChangeReceiver.EXTRA_VOLUME_STREAM_TYPE, -1);
                int level = intent.getIntExtra(SystemMediaRouteProvider.LegacyImpl.VolumeChangeReceiver.EXTRA_VOLUME_STREAM_VALUE, -1);
                int oldLevel = intent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", -1);
                if (D.BUG) {
                    String str = VolumeDialogControllerImpl.TAG;
                    Log.d(str, "onReceive VOLUME_CHANGED_ACTION stream=" + stream + " level=" + level + " oldLevel=" + oldLevel);
                }
                changed = VolumeDialogControllerImpl.this.updateStreamLevelW(stream, level);
            } else if (action.equals("android.media.STREAM_DEVICES_CHANGED_ACTION")) {
                int stream2 = intent.getIntExtra(SystemMediaRouteProvider.LegacyImpl.VolumeChangeReceiver.EXTRA_VOLUME_STREAM_TYPE, -1);
                int devices = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_DEVICES", -1);
                int oldDevices = intent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_DEVICES", -1);
                if (D.BUG) {
                    String str2 = VolumeDialogControllerImpl.TAG;
                    Log.d(str2, "onReceive STREAM_DEVICES_CHANGED_ACTION stream=" + stream2 + " devices=" + devices + " oldDevices=" + oldDevices);
                }
                changed = VolumeDialogControllerImpl.this.checkRoutedToBluetoothW(stream2) | VolumeDialogControllerImpl.this.onVolumeChangedW(stream2, 0);
            } else if (action.equals("android.media.RINGER_MODE_CHANGED")) {
                int rm = intent.getIntExtra("android.media.EXTRA_RINGER_MODE", -1);
                if (isInitialStickyBroadcast()) {
                    VolumeDialogControllerImpl.this.mState.ringerModeExternal = rm;
                }
                if (D.BUG) {
                    String str3 = VolumeDialogControllerImpl.TAG;
                    Log.d(str3, "onReceive RINGER_MODE_CHANGED_ACTION rm=" + Util.ringerModeToString(rm));
                }
                changed = VolumeDialogControllerImpl.this.updateRingerModeExternalW(rm);
            } else if (action.equals("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION")) {
                int rm2 = intent.getIntExtra("android.media.EXTRA_RINGER_MODE", -1);
                if (isInitialStickyBroadcast()) {
                    VolumeDialogControllerImpl.this.mState.ringerModeInternal = rm2;
                }
                if (D.BUG) {
                    String str4 = VolumeDialogControllerImpl.TAG;
                    Log.d(str4, "onReceive INTERNAL_RINGER_MODE_CHANGED_ACTION rm=" + Util.ringerModeToString(rm2));
                }
                changed = VolumeDialogControllerImpl.this.updateRingerModeInternalW(rm2);
            } else if (action.equals("android.media.STREAM_MUTE_CHANGED_ACTION")) {
                int stream3 = intent.getIntExtra(SystemMediaRouteProvider.LegacyImpl.VolumeChangeReceiver.EXTRA_VOLUME_STREAM_TYPE, -1);
                boolean muted = intent.getBooleanExtra("android.media.EXTRA_STREAM_VOLUME_MUTED", false);
                if (D.BUG) {
                    String str5 = VolumeDialogControllerImpl.TAG;
                    Log.d(str5, "onReceive STREAM_MUTE_CHANGED_ACTION stream=" + stream3 + " muted=" + muted);
                }
                changed = VolumeDialogControllerImpl.this.updateStreamMuteW(stream3, muted);
            } else if (action.equals("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED")) {
                if (D.BUG) {
                    Log.d(VolumeDialogControllerImpl.TAG, "onReceive ACTION_EFFECTS_SUPPRESSOR_CHANGED");
                }
                VolumeDialogControllerImpl volumeDialogControllerImpl = VolumeDialogControllerImpl.this;
                changed = volumeDialogControllerImpl.updateEffectsSuppressorW(volumeDialogControllerImpl.mNoMan.getEffectsSuppressor());
            } else if (action.equals("android.intent.action.CONFIGURATION_CHANGED")) {
                if (D.BUG) {
                    Log.d(VolumeDialogControllerImpl.TAG, "onReceive ACTION_CONFIGURATION_CHANGED");
                }
                VolumeDialogControllerImpl.this.mCallbacks.onConfigurationChanged();
            } else if (action.equals("android.intent.action.SCREEN_OFF")) {
                if (D.BUG) {
                    Log.d(VolumeDialogControllerImpl.TAG, "onReceive ACTION_SCREEN_OFF");
                }
                VolumeDialogControllerImpl.this.mCallbacks.onScreenOff();
            } else if (action.equals("android.intent.action.CLOSE_SYSTEM_DIALOGS")) {
                if (D.BUG) {
                    Log.d(VolumeDialogControllerImpl.TAG, "onReceive ACTION_CLOSE_SYSTEM_DIALOGS");
                }
                VolumeDialogControllerImpl.this.dismiss();
            }
            if (changed) {
                VolumeDialogControllerImpl.this.mCallbacks.onStateChanged(VolumeDialogControllerImpl.this.mState);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes21.dex */
    public final class MediaSessionsCallbacks implements MediaSessions.Callbacks {
        private final HashMap<MediaSession.Token, Integer> mRemoteStreams = new HashMap<>();
        private int mNextStream = 100;

        protected MediaSessionsCallbacks() {
        }

        @Override // com.android.settingslib.volume.MediaSessions.Callbacks
        public void onRemoteUpdate(MediaSession.Token token, String name, MediaController.PlaybackInfo pi) {
            addStream(token, "onRemoteUpdate");
            int stream = this.mRemoteStreams.get(token).intValue();
            boolean changed = VolumeDialogControllerImpl.this.mState.states.indexOfKey(stream) < 0;
            VolumeDialogController.StreamState ss = VolumeDialogControllerImpl.this.streamStateW(stream);
            ss.dynamic = true;
            ss.levelMin = 0;
            ss.levelMax = pi.getMaxVolume();
            if (ss.level != pi.getCurrentVolume()) {
                ss.level = pi.getCurrentVolume();
                changed = true;
            }
            if (!Objects.equals(ss.remoteLabel, name)) {
                ss.name = -1;
                ss.remoteLabel = name;
                changed = true;
            }
            if (changed) {
                if (D.BUG) {
                    String str = VolumeDialogControllerImpl.TAG;
                    Log.d(str, "onRemoteUpdate: " + name + ": " + ss.level + " of " + ss.levelMax);
                }
                VolumeDialogControllerImpl.this.mCallbacks.onStateChanged(VolumeDialogControllerImpl.this.mState);
            }
        }

        @Override // com.android.settingslib.volume.MediaSessions.Callbacks
        public void onRemoteVolumeChanged(MediaSession.Token token, int flags) {
            addStream(token, "onRemoteVolumeChanged");
            int stream = this.mRemoteStreams.get(token).intValue();
            boolean showUI = VolumeDialogControllerImpl.this.shouldShowUI(flags);
            boolean changed = VolumeDialogControllerImpl.this.updateActiveStreamW(stream);
            if (showUI) {
                changed |= VolumeDialogControllerImpl.this.checkRoutedToBluetoothW(3);
            }
            if (changed) {
                VolumeDialogControllerImpl.this.mCallbacks.onStateChanged(VolumeDialogControllerImpl.this.mState);
            }
            if (showUI) {
                VolumeDialogControllerImpl.this.mCallbacks.onShowRequested(2);
            }
        }

        @Override // com.android.settingslib.volume.MediaSessions.Callbacks
        public void onRemoteRemoved(MediaSession.Token token) {
            if (!this.mRemoteStreams.containsKey(token)) {
                if (D.BUG) {
                    String str = VolumeDialogControllerImpl.TAG;
                    Log.d(str, "onRemoteRemoved: stream doesn't exist, aborting remote removed for token:" + token.toString());
                    return;
                }
                return;
            }
            int stream = this.mRemoteStreams.get(token).intValue();
            VolumeDialogControllerImpl.this.mState.states.remove(stream);
            if (VolumeDialogControllerImpl.this.mState.activeStream == stream) {
                VolumeDialogControllerImpl.this.updateActiveStreamW(-1);
            }
            VolumeDialogControllerImpl.this.mCallbacks.onStateChanged(VolumeDialogControllerImpl.this.mState);
        }

        public void setStreamVolume(int stream, int level) {
            MediaSession.Token t = findToken(stream);
            if (t == null) {
                String str = VolumeDialogControllerImpl.TAG;
                Log.w(str, "setStreamVolume: No token found for stream: " + stream);
                return;
            }
            VolumeDialogControllerImpl.this.mMediaSessions.setVolume(t, level);
        }

        private MediaSession.Token findToken(int stream) {
            for (Map.Entry<MediaSession.Token, Integer> entry : this.mRemoteStreams.entrySet()) {
                if (entry.getValue().equals(Integer.valueOf(stream))) {
                    return entry.getKey();
                }
            }
            return null;
        }

        private void addStream(MediaSession.Token token, String triggeringMethod) {
            if (!this.mRemoteStreams.containsKey(token)) {
                this.mRemoteStreams.put(token, Integer.valueOf(this.mNextStream));
                if (D.BUG) {
                    Log.d(VolumeDialogControllerImpl.TAG, triggeringMethod + ": added stream " + this.mNextStream + " from token + " + token.toString());
                }
                this.mNextStream++;
            }
        }
    }
}
