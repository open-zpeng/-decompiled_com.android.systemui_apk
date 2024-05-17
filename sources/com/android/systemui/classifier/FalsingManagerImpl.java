package com.android.systemui.classifier;

import android.content.Context;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.biometrics.BiometricSourceType;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.logging.MetricsLogger;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.Dependency;
import com.android.systemui.UiOffloadThread;
import com.android.systemui.analytics.DataCollector;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.StatusBarState;
import com.android.systemui.util.AsyncSensorManager;
import java.io.PrintWriter;
/* loaded from: classes21.dex */
public class FalsingManagerImpl implements FalsingManager {
    private static final int[] CLASSIFIER_SENSORS = {8};
    private static final int[] COLLECTOR_SENSORS = {1, 4, 8, 5, 11};
    private static final String ENFORCE_BOUNCER = "falsing_manager_enforce_bouncer";
    public static final String FALSING_REMAIN_LOCKED = "falsing_failure_after_attempts";
    public static final String FALSING_SUCCESS = "falsing_success_after_attempts";
    private final AccessibilityManager mAccessibilityManager;
    private final Context mContext;
    private final DataCollector mDataCollector;
    private final HumanInteractionClassifier mHumanInteractionClassifier;
    private int mIsFalseTouchCalls;
    private Runnable mPendingWtf;
    private boolean mScreenOn;
    private boolean mShowingAod;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean mEnforceBouncer = false;
    private boolean mBouncerOn = false;
    private boolean mBouncerOffOnDown = false;
    private boolean mSessionActive = false;
    private boolean mIsTouchScreen = true;
    private boolean mJustUnlockedWithFace = false;
    private int mState = 0;
    private SensorEventListener mSensorEventListener = new SensorEventListener() { // from class: com.android.systemui.classifier.FalsingManagerImpl.1
        @Override // android.hardware.SensorEventListener
        public synchronized void onSensorChanged(SensorEvent event) {
            FalsingManagerImpl.this.mDataCollector.onSensorChanged(event);
            FalsingManagerImpl.this.mHumanInteractionClassifier.onSensorChanged(event);
        }

        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            FalsingManagerImpl.this.mDataCollector.onAccuracyChanged(sensor, accuracy);
        }
    };
    public StatusBarStateController.StateListener mStatusBarStateListener = new StatusBarStateController.StateListener() { // from class: com.android.systemui.classifier.FalsingManagerImpl.2
        @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
        public void onStateChanged(int newState) {
            if (FalsingLog.ENABLED) {
                FalsingLog.i("setStatusBarState", "from=" + StatusBarState.toShortString(FalsingManagerImpl.this.mState) + " to=" + StatusBarState.toShortString(newState));
            }
            FalsingManagerImpl.this.mState = newState;
            FalsingManagerImpl.this.updateSessionActive();
        }
    };
    protected final ContentObserver mSettingsObserver = new ContentObserver(this.mHandler) { // from class: com.android.systemui.classifier.FalsingManagerImpl.3
        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            FalsingManagerImpl.this.updateConfiguration();
        }
    };
    private final KeyguardUpdateMonitorCallback mKeyguardUpdateCallback = new KeyguardUpdateMonitorCallback() { // from class: com.android.systemui.classifier.FalsingManagerImpl.4
        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onBiometricAuthenticated(int userId, BiometricSourceType biometricSourceType) {
            if (userId == KeyguardUpdateMonitor.getCurrentUser() && biometricSourceType == BiometricSourceType.FACE) {
                FalsingManagerImpl.this.mJustUnlockedWithFace = true;
            }
        }
    };
    private final SensorManager mSensorManager = (SensorManager) Dependency.get(AsyncSensorManager.class);
    private final UiOffloadThread mUiOffloadThread = (UiOffloadThread) Dependency.get(UiOffloadThread.class);
    private MetricsLogger mMetricsLogger = new MetricsLogger();

    /* JADX INFO: Access modifiers changed from: package-private */
    public FalsingManagerImpl(Context context) {
        this.mContext = context;
        this.mAccessibilityManager = (AccessibilityManager) context.getSystemService(AccessibilityManager.class);
        this.mDataCollector = DataCollector.getInstance(this.mContext);
        this.mHumanInteractionClassifier = HumanInteractionClassifier.getInstance(this.mContext);
        this.mScreenOn = ((PowerManager) context.getSystemService(PowerManager.class)).isInteractive();
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(ENFORCE_BOUNCER), false, this.mSettingsObserver, -1);
        updateConfiguration();
        ((StatusBarStateController) Dependency.get(StatusBarStateController.class)).addCallback(this.mStatusBarStateListener);
        KeyguardUpdateMonitor.getInstance(context).registerCallback(this.mKeyguardUpdateCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateConfiguration() {
        this.mEnforceBouncer = Settings.Secure.getInt(this.mContext.getContentResolver(), ENFORCE_BOUNCER, 0) != 0;
    }

    private boolean shouldSessionBeActive() {
        boolean z = FalsingLog.ENABLED;
        return isEnabled() && this.mScreenOn && this.mState == 1 && !this.mShowingAod;
    }

    private boolean sessionEntrypoint() {
        if (!this.mSessionActive && shouldSessionBeActive()) {
            onSessionStart();
            return true;
        }
        return false;
    }

    private void sessionExitpoint(boolean force) {
        if (this.mSessionActive) {
            if (force || !shouldSessionBeActive()) {
                this.mSessionActive = false;
                if (this.mIsFalseTouchCalls != 0) {
                    if (FalsingLog.ENABLED) {
                        FalsingLog.i("isFalseTouchCalls", "Calls before failure: " + this.mIsFalseTouchCalls);
                    }
                    this.mMetricsLogger.histogram(FALSING_REMAIN_LOCKED, this.mIsFalseTouchCalls);
                    this.mIsFalseTouchCalls = 0;
                }
                this.mUiOffloadThread.submit(new Runnable() { // from class: com.android.systemui.classifier.-$$Lambda$FalsingManagerImpl$8SXkW2Wsm8XWKvooYKTPgEEzXnU
                    @Override // java.lang.Runnable
                    public final void run() {
                        FalsingManagerImpl.this.lambda$sessionExitpoint$0$FalsingManagerImpl();
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$sessionExitpoint$0$FalsingManagerImpl() {
        this.mSensorManager.unregisterListener(this.mSensorEventListener);
    }

    public void updateSessionActive() {
        if (shouldSessionBeActive()) {
            sessionEntrypoint();
        } else {
            sessionExitpoint(false);
        }
    }

    private void onSessionStart() {
        if (FalsingLog.ENABLED) {
            FalsingLog.i("onSessionStart", "classifierEnabled=" + isClassiferEnabled());
            clearPendingWtf();
        }
        this.mBouncerOn = false;
        this.mSessionActive = true;
        this.mJustUnlockedWithFace = false;
        this.mIsFalseTouchCalls = 0;
        if (this.mHumanInteractionClassifier.isEnabled()) {
            registerSensors(CLASSIFIER_SENSORS);
        }
        if (this.mDataCollector.isEnabledFull()) {
            registerSensors(COLLECTOR_SENSORS);
        }
        if (this.mDataCollector.isEnabled()) {
            this.mDataCollector.onFalsingSessionStarted();
        }
    }

    private void registerSensors(int[] sensors) {
        for (int sensorType : sensors) {
            final Sensor s = this.mSensorManager.getDefaultSensor(sensorType);
            if (s != null) {
                this.mUiOffloadThread.submit(new Runnable() { // from class: com.android.systemui.classifier.-$$Lambda$FalsingManagerImpl$VJW_VOVtQGpUmd7AtKlCfAEhBZE
                    @Override // java.lang.Runnable
                    public final void run() {
                        FalsingManagerImpl.this.lambda$registerSensors$1$FalsingManagerImpl(s);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$registerSensors$1$FalsingManagerImpl(Sensor s) {
        this.mSensorManager.registerListener(this.mSensorEventListener, s, 1);
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public boolean isClassiferEnabled() {
        return this.mHumanInteractionClassifier.isEnabled();
    }

    private boolean isEnabled() {
        return this.mHumanInteractionClassifier.isEnabled() || this.mDataCollector.isEnabled();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public boolean isUnlockingDisabled() {
        return this.mDataCollector.isUnlockingDisabled();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public boolean isFalseTouch() {
        if (FalsingLog.ENABLED && !this.mSessionActive && ((PowerManager) this.mContext.getSystemService(PowerManager.class)).isInteractive() && this.mPendingWtf == null) {
            boolean isEnabled = isEnabled();
            boolean z = this.mScreenOn;
            final String state = StatusBarState.toShortString(this.mState);
            final Throwable here = new Throwable("here");
            StringBuilder sb = new StringBuilder();
            sb.append("Session is not active, yet there's a query for a false touch.");
            sb.append(" enabled=");
            int enabled = isEnabled ? 1 : 0;
            sb.append(enabled);
            sb.append(" mScreenOn=");
            int screenOn = z ? 1 : 0;
            sb.append(screenOn);
            sb.append(" mState=");
            sb.append(state);
            sb.append(". Escalating to WTF if screen does not turn on soon.");
            FalsingLog.wLogcat("isFalseTouch", sb.toString());
            final int enabled2 = isEnabled ? 1 : 0;
            final int screenOn2 = z ? 1 : 0;
            this.mPendingWtf = new Runnable() { // from class: com.android.systemui.classifier.-$$Lambda$FalsingManagerImpl$v5ZF-PRlWWHHEjWpilJxodWNKMI
                @Override // java.lang.Runnable
                public final void run() {
                    FalsingManagerImpl.this.lambda$isFalseTouch$2$FalsingManagerImpl(enabled2, screenOn2, state, here);
                }
            };
            this.mHandler.postDelayed(this.mPendingWtf, 1000L);
        }
        if (this.mAccessibilityManager.isTouchExplorationEnabled() || !this.mIsTouchScreen || this.mJustUnlockedWithFace) {
            return false;
        }
        this.mIsFalseTouchCalls++;
        boolean isFalse = this.mHumanInteractionClassifier.isFalseTouch();
        if (!isFalse) {
            if (FalsingLog.ENABLED) {
                FalsingLog.i("isFalseTouchCalls", "Calls before success: " + this.mIsFalseTouchCalls);
            }
            this.mMetricsLogger.histogram(FALSING_SUCCESS, this.mIsFalseTouchCalls);
            this.mIsFalseTouchCalls = 0;
        }
        return isFalse;
    }

    public /* synthetic */ void lambda$isFalseTouch$2$FalsingManagerImpl(int enabled, int screenOn, String state, Throwable here) {
        FalsingLog.wtf("isFalseTouch", "Session did not become active after query for a false touch. enabled=" + enabled + '/' + (isEnabled() ? 1 : 0) + " mScreenOn=" + screenOn + '/' + (this.mScreenOn ? 1 : 0) + " mState=" + state + '/' + StatusBarState.toShortString(this.mState) + ". Look for warnings ~1000ms earlier to see root cause.", here);
    }

    private void clearPendingWtf() {
        Runnable runnable = this.mPendingWtf;
        if (runnable != null) {
            this.mHandler.removeCallbacks(runnable);
            this.mPendingWtf = null;
        }
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public boolean shouldEnforceBouncer() {
        return this.mEnforceBouncer;
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void setShowingAod(boolean showingAod) {
        this.mShowingAod = showingAod;
        updateSessionActive();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onScreenTurningOn() {
        if (FalsingLog.ENABLED) {
            FalsingLog.i("onScreenTurningOn", "from=" + (this.mScreenOn ? 1 : 0));
            clearPendingWtf();
        }
        this.mScreenOn = true;
        if (sessionEntrypoint()) {
            this.mDataCollector.onScreenTurningOn();
        }
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onScreenOnFromTouch() {
        if (FalsingLog.ENABLED) {
            FalsingLog.i("onScreenOnFromTouch", "from=" + (this.mScreenOn ? 1 : 0));
        }
        this.mScreenOn = true;
        if (sessionEntrypoint()) {
            this.mDataCollector.onScreenOnFromTouch();
        }
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onScreenOff() {
        if (FalsingLog.ENABLED) {
            FalsingLog.i("onScreenOff", "from=" + (this.mScreenOn ? 1 : 0));
        }
        this.mDataCollector.onScreenOff();
        this.mScreenOn = false;
        sessionExitpoint(false);
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onSucccessfulUnlock() {
        if (FalsingLog.ENABLED) {
            FalsingLog.i("onSucccessfulUnlock", "");
        }
        this.mDataCollector.onSucccessfulUnlock();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onBouncerShown() {
        if (FalsingLog.ENABLED) {
            FalsingLog.i("onBouncerShown", "from=" + (this.mBouncerOn ? 1 : 0));
        }
        if (!this.mBouncerOn) {
            this.mBouncerOn = true;
            this.mDataCollector.onBouncerShown();
        }
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onBouncerHidden() {
        if (FalsingLog.ENABLED) {
            FalsingLog.i("onBouncerHidden", "from=" + (this.mBouncerOn ? 1 : 0));
        }
        if (this.mBouncerOn) {
            this.mBouncerOn = false;
            this.mDataCollector.onBouncerHidden();
        }
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onQsDown() {
        if (FalsingLog.ENABLED) {
            FalsingLog.i("onQsDown", "");
        }
        this.mHumanInteractionClassifier.setType(0);
        this.mDataCollector.onQsDown();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void setQsExpanded(boolean expanded) {
        this.mDataCollector.setQsExpanded(expanded);
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onTrackingStarted(boolean secure) {
        if (FalsingLog.ENABLED) {
            FalsingLog.i("onTrackingStarted", "");
        }
        this.mHumanInteractionClassifier.setType(secure ? 8 : 4);
        this.mDataCollector.onTrackingStarted();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onTrackingStopped() {
        this.mDataCollector.onTrackingStopped();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificationActive() {
        this.mDataCollector.onNotificationActive();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificationDoubleTap(boolean accepted, float dx, float dy) {
        if (FalsingLog.ENABLED) {
            FalsingLog.i("onNotificationDoubleTap", "accepted=" + accepted + " dx=" + dx + " dy=" + dy + " (px)");
        }
        this.mDataCollector.onNotificationDoubleTap();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void setNotificationExpanded() {
        this.mDataCollector.setNotificationExpanded();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificatonStartDraggingDown() {
        if (FalsingLog.ENABLED) {
            FalsingLog.i("onNotificatonStartDraggingDown", "");
        }
        this.mHumanInteractionClassifier.setType(2);
        this.mDataCollector.onNotificatonStartDraggingDown();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onStartExpandingFromPulse() {
        if (FalsingLog.ENABLED) {
            FalsingLog.i("onStartExpandingFromPulse", "");
        }
        this.mHumanInteractionClassifier.setType(9);
        this.mDataCollector.onStartExpandingFromPulse();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificatonStopDraggingDown() {
        this.mDataCollector.onNotificatonStopDraggingDown();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onExpansionFromPulseStopped() {
        this.mDataCollector.onExpansionFromPulseStopped();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificationDismissed() {
        this.mDataCollector.onNotificationDismissed();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificatonStartDismissing() {
        if (FalsingLog.ENABLED) {
            FalsingLog.i("onNotificatonStartDismissing", "");
        }
        this.mHumanInteractionClassifier.setType(1);
        this.mDataCollector.onNotificatonStartDismissing();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificatonStopDismissing() {
        this.mDataCollector.onNotificatonStopDismissing();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onCameraOn() {
        this.mDataCollector.onCameraOn();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onLeftAffordanceOn() {
        this.mDataCollector.onLeftAffordanceOn();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onAffordanceSwipingStarted(boolean rightCorner) {
        if (FalsingLog.ENABLED) {
            FalsingLog.i("onAffordanceSwipingStarted", "");
        }
        if (rightCorner) {
            this.mHumanInteractionClassifier.setType(6);
        } else {
            this.mHumanInteractionClassifier.setType(5);
        }
        this.mDataCollector.onAffordanceSwipingStarted(rightCorner);
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onAffordanceSwipingAborted() {
        this.mDataCollector.onAffordanceSwipingAborted();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onUnlockHintStarted() {
        this.mDataCollector.onUnlockHintStarted();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onCameraHintStarted() {
        this.mDataCollector.onCameraHintStarted();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onLeftAffordanceHintStarted() {
        this.mDataCollector.onLeftAffordanceHintStarted();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onTouchEvent(MotionEvent event, int width, int height) {
        if (event.getAction() == 0) {
            this.mIsTouchScreen = event.isFromSource(4098);
            this.mBouncerOffOnDown = !this.mBouncerOn;
        }
        if (this.mSessionActive) {
            if (!this.mBouncerOn) {
                this.mDataCollector.onTouchEvent(event, width, height);
            }
            if (this.mBouncerOffOnDown) {
                this.mHumanInteractionClassifier.onTouchEvent(event);
            }
        }
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void dump(PrintWriter pw) {
        pw.println("FALSING MANAGER");
        pw.print("classifierEnabled=");
        pw.println(isClassiferEnabled() ? 1 : 0);
        pw.print("mSessionActive=");
        pw.println(this.mSessionActive ? 1 : 0);
        pw.print("mBouncerOn=");
        pw.println(this.mSessionActive ? 1 : 0);
        pw.print("mState=");
        pw.println(StatusBarState.toShortString(this.mState));
        pw.print("mScreenOn=");
        pw.println(this.mScreenOn ? 1 : 0);
        pw.println();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void cleanup() {
        this.mSensorManager.unregisterListener(this.mSensorEventListener);
        this.mContext.getContentResolver().unregisterContentObserver(this.mSettingsObserver);
        ((StatusBarStateController) Dependency.get(StatusBarStateController.class)).removeCallback(this.mStatusBarStateListener);
        KeyguardUpdateMonitor.getInstance(this.mContext).removeCallback(this.mKeyguardUpdateCallback);
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public Uri reportRejectedTouch() {
        if (this.mDataCollector.isEnabled()) {
            return this.mDataCollector.reportRejectedTouch();
        }
        return null;
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public boolean isReportingEnabled() {
        return this.mDataCollector.isReportingEnabled();
    }
}
