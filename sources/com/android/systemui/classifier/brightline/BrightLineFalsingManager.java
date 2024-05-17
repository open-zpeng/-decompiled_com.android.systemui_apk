package com.android.systemui.classifier.brightline;

import android.hardware.biometrics.BiometricSourceType;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import com.android.internal.logging.MetricsLogger;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.classifier.FalsingManagerImpl;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.util.ProximitySensor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* loaded from: classes21.dex */
public class BrightLineFalsingManager implements FalsingManager {
    static final boolean DEBUG = false;
    private static final String TAG = "FalsingManagerPlugin";
    private final List<FalsingClassifier> mClassifiers;
    private final FalsingDataProvider mDataProvider;
    private int mIsFalseTouchCalls;
    private boolean mJustUnlockedWithFace;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private MetricsLogger mMetricsLogger;
    private final ProximitySensor mProximitySensor;
    private boolean mScreenOn;
    private boolean mSessionStarted;
    private boolean mShowingAod;
    private ProximitySensor.ProximitySensorListener mSensorEventListener = new ProximitySensor.ProximitySensorListener() { // from class: com.android.systemui.classifier.brightline.-$$Lambda$BrightLineFalsingManager$EIbc1Czf0k1qNoCbD0gLgP-Ksv4
        @Override // com.android.systemui.util.ProximitySensor.ProximitySensorListener
        public final void onProximitySensorEvent(ProximitySensor.ProximityEvent proximityEvent) {
            BrightLineFalsingManager.this.onProximityEvent(proximityEvent);
        }
    };
    private final KeyguardUpdateMonitorCallback mKeyguardUpdateCallback = new KeyguardUpdateMonitorCallback() { // from class: com.android.systemui.classifier.brightline.BrightLineFalsingManager.1
        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onBiometricAuthenticated(int userId, BiometricSourceType biometricSourceType) {
            if (userId == KeyguardUpdateMonitor.getCurrentUser() && biometricSourceType == BiometricSourceType.FACE) {
                BrightLineFalsingManager.this.mJustUnlockedWithFace = true;
            }
        }
    };

    public BrightLineFalsingManager(FalsingDataProvider falsingDataProvider, KeyguardUpdateMonitor keyguardUpdateMonitor, ProximitySensor proximitySensor) {
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mDataProvider = falsingDataProvider;
        this.mProximitySensor = proximitySensor;
        this.mKeyguardUpdateMonitor.registerCallback(this.mKeyguardUpdateCallback);
        this.mMetricsLogger = new MetricsLogger();
        this.mClassifiers = new ArrayList();
        DistanceClassifier distanceClassifier = new DistanceClassifier(this.mDataProvider);
        ProximityClassifier proximityClassifier = new ProximityClassifier(distanceClassifier, this.mDataProvider);
        this.mClassifiers.add(new PointerCountClassifier(this.mDataProvider));
        this.mClassifiers.add(new TypeClassifier(this.mDataProvider));
        this.mClassifiers.add(new DiagonalClassifier(this.mDataProvider));
        this.mClassifiers.add(distanceClassifier);
        this.mClassifiers.add(proximityClassifier);
        this.mClassifiers.add(new ZigZagClassifier(this.mDataProvider));
    }

    private void registerSensors() {
        this.mProximitySensor.register(this.mSensorEventListener);
    }

    private void unregisterSensors() {
        this.mProximitySensor.unregister(this.mSensorEventListener);
    }

    private void sessionStart() {
        if (!this.mSessionStarted && !this.mShowingAod && this.mScreenOn) {
            logDebug("Starting Session");
            this.mSessionStarted = true;
            this.mJustUnlockedWithFace = false;
            registerSensors();
            this.mClassifiers.forEach(new Consumer() { // from class: com.android.systemui.classifier.brightline.-$$Lambda$HclOlu42IVtKALxwbwHP3Y1rdRk
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((FalsingClassifier) obj).onSessionStarted();
                }
            });
        }
    }

    private void sessionEnd() {
        if (this.mSessionStarted) {
            logDebug("Ending Session");
            this.mSessionStarted = false;
            unregisterSensors();
            this.mDataProvider.onSessionEnd();
            this.mClassifiers.forEach(new Consumer() { // from class: com.android.systemui.classifier.brightline.-$$Lambda$47wU6WxQ-76Gt_ecwypSCrFl04Q
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((FalsingClassifier) obj).onSessionEnded();
                }
            });
            int i = this.mIsFalseTouchCalls;
            if (i != 0) {
                this.mMetricsLogger.histogram(FalsingManagerImpl.FALSING_REMAIN_LOCKED, i);
                this.mIsFalseTouchCalls = 0;
            }
        }
    }

    private void updateInteractionType(final int type) {
        logDebug("InteractionType: " + type);
        this.mClassifiers.forEach(new Consumer() { // from class: com.android.systemui.classifier.brightline.-$$Lambda$BrightLineFalsingManager$lNVlN0g8I4PHJqQP26X1fXH_2TU
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((FalsingClassifier) obj).setInteractionType(type);
            }
        });
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public boolean isClassiferEnabled() {
        return true;
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public boolean isFalseTouch() {
        boolean r = !this.mJustUnlockedWithFace && this.mClassifiers.stream().anyMatch(new Predicate() { // from class: com.android.systemui.classifier.brightline.-$$Lambda$BrightLineFalsingManager$Hwyy_7VqHdYEMuILU__cqMTjCOk
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return BrightLineFalsingManager.lambda$isFalseTouch$1((FalsingClassifier) obj);
            }
        });
        logDebug("Is false touch? " + r);
        return r;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$isFalseTouch$1(FalsingClassifier falsingClassifier) {
        boolean result = falsingClassifier.isFalseTouch();
        if (result) {
            logInfo(falsingClassifier.getClass().getName() + ": true");
        } else {
            logDebug(falsingClassifier.getClass().getName() + ": false");
        }
        return result;
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onTouchEvent(final MotionEvent motionEvent, int width, int height) {
        this.mDataProvider.onMotionEvent(motionEvent);
        this.mClassifiers.forEach(new Consumer() { // from class: com.android.systemui.classifier.brightline.-$$Lambda$BrightLineFalsingManager$dqBt-Gf6PUXlUGyEertsddqo7Kg
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((FalsingClassifier) obj).onTouchEvent(motionEvent);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onProximityEvent(final ProximitySensor.ProximityEvent proximityEvent) {
        this.mClassifiers.forEach(new Consumer() { // from class: com.android.systemui.classifier.brightline.-$$Lambda$BrightLineFalsingManager$-SnpFjXg0evvwd5NWvIx70G2rfg
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((FalsingClassifier) obj).onProximityEvent(ProximitySensor.ProximityEvent.this);
            }
        });
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onSucccessfulUnlock() {
        int i = this.mIsFalseTouchCalls;
        if (i != 0) {
            this.mMetricsLogger.histogram(FalsingManagerImpl.FALSING_SUCCESS, i);
            this.mIsFalseTouchCalls = 0;
        }
        sessionEnd();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificationActive() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void setShowingAod(boolean showingAod) {
        this.mShowingAod = showingAod;
        if (showingAod) {
            sessionEnd();
        } else {
            sessionStart();
        }
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificatonStartDraggingDown() {
        updateInteractionType(2);
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public boolean isUnlockingDisabled() {
        return false;
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificatonStopDraggingDown() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void setNotificationExpanded() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onQsDown() {
        updateInteractionType(0);
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void setQsExpanded(boolean b) {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public boolean shouldEnforceBouncer() {
        return false;
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onTrackingStarted(boolean secure) {
        updateInteractionType(secure ? 8 : 4);
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onTrackingStopped() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onLeftAffordanceOn() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onCameraOn() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onAffordanceSwipingStarted(boolean rightCorner) {
        updateInteractionType(rightCorner ? 6 : 5);
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onAffordanceSwipingAborted() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onStartExpandingFromPulse() {
        updateInteractionType(9);
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onExpansionFromPulseStopped() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public Uri reportRejectedTouch() {
        return null;
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onScreenOnFromTouch() {
        onScreenTurningOn();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public boolean isReportingEnabled() {
        return false;
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onUnlockHintStarted() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onCameraHintStarted() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onLeftAffordanceHintStarted() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onScreenTurningOn() {
        this.mScreenOn = true;
        sessionStart();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onScreenOff() {
        this.mScreenOn = false;
        sessionEnd();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificatonStopDismissing() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificationDismissed() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificatonStartDismissing() {
        updateInteractionType(1);
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificationDoubleTap(boolean b, float v, float v1) {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onBouncerShown() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onBouncerHidden() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void dump(PrintWriter printWriter) {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void cleanup() {
        unregisterSensors();
        this.mKeyguardUpdateMonitor.removeCallback(this.mKeyguardUpdateCallback);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void logDebug(String msg) {
        logDebug(msg, null);
    }

    static void logDebug(String msg, Throwable throwable) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void logInfo(String msg) {
        Log.i(TAG, msg);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void logError(String msg) {
        Log.e(TAG, msg);
    }
}
