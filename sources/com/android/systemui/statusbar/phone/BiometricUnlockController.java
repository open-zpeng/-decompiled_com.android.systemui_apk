package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.hardware.biometrics.BiometricSourceType;
import android.metrics.LogMaker;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Trace;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.LatencyTracker;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.Dependency;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.statusbar.NotificationMediaManager;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes21.dex */
public class BiometricUnlockController extends KeyguardUpdateMonitorCallback {
    private static final float BIOMETRIC_COLLAPSE_SPEEDUP_FACTOR = 1.1f;
    private static final long BIOMETRIC_WAKELOCK_TIMEOUT_MS = 15000;
    private static final String BIOMETRIC_WAKE_LOCK_NAME = "wake-and-unlock wakelock";
    private static final boolean DEBUG_BIO_WAKELOCK = true;
    public static final int MODE_DISMISS_BOUNCER = 8;
    public static final int MODE_NONE = 0;
    public static final int MODE_ONLY_WAKE = 4;
    public static final int MODE_SHOW_BOUNCER = 3;
    public static final int MODE_UNLOCK_COLLAPSING = 5;
    public static final int MODE_UNLOCK_FADING = 7;
    public static final int MODE_WAKE_AND_UNLOCK = 1;
    public static final int MODE_WAKE_AND_UNLOCK_FROM_DREAM = 6;
    public static final int MODE_WAKE_AND_UNLOCK_PULSING = 2;
    private static final String TAG = "BiometricUnlockController";
    private final Context mContext;
    private DozeScrimController mDozeScrimController;
    private boolean mFadedAwayAfterWakeAndUnlock;
    private final Handler mHandler;
    private boolean mHasScreenTurnedOnSinceAuthenticating;
    private final KeyguardBypassController mKeyguardBypassController;
    private KeyguardViewMediator mKeyguardViewMediator;
    private final NotificationMediaManager mMediaManager;
    private final MetricsLogger mMetricsLogger;
    private int mMode;
    private BiometricSourceType mPendingAuthenticatedBioSourceType;
    private int mPendingAuthenticatedUserId;
    private boolean mPendingShowBouncer;
    private final PowerManager mPowerManager;
    private final Runnable mReleaseBiometricWakeLockRunnable;
    private final ScreenLifecycle.Observer mScreenObserver;
    private ScrimController mScrimController;
    private StatusBar mStatusBar;
    private StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
    private final StatusBarWindowController mStatusBarWindowController;
    private final UnlockMethodCache mUnlockMethodCache;
    private final KeyguardUpdateMonitor mUpdateMonitor;
    private PowerManager.WakeLock mWakeLock;
    private final int mWakeUpDelay;
    @VisibleForTesting
    final WakefulnessLifecycle.Observer mWakefulnessObserver;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes21.dex */
    public @interface WakeAndUnlockMode {
    }

    public BiometricUnlockController(Context context, DozeScrimController dozeScrimController, KeyguardViewMediator keyguardViewMediator, ScrimController scrimController, StatusBar statusBar, UnlockMethodCache unlockMethodCache, Handler handler, KeyguardUpdateMonitor keyguardUpdateMonitor, KeyguardBypassController keyguardBypassController) {
        this(context, dozeScrimController, keyguardViewMediator, scrimController, statusBar, unlockMethodCache, handler, keyguardUpdateMonitor, context.getResources().getInteger(17694910), keyguardBypassController);
    }

    @VisibleForTesting
    protected BiometricUnlockController(Context context, DozeScrimController dozeScrimController, KeyguardViewMediator keyguardViewMediator, ScrimController scrimController, StatusBar statusBar, UnlockMethodCache unlockMethodCache, Handler handler, KeyguardUpdateMonitor keyguardUpdateMonitor, int wakeUpDelay, KeyguardBypassController keyguardBypassController) {
        this.mPendingAuthenticatedUserId = -1;
        this.mPendingAuthenticatedBioSourceType = null;
        this.mMetricsLogger = (MetricsLogger) Dependency.get(MetricsLogger.class);
        this.mReleaseBiometricWakeLockRunnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.BiometricUnlockController.1
            @Override // java.lang.Runnable
            public void run() {
                Log.i(BiometricUnlockController.TAG, "biometric wakelock: TIMEOUT!!");
                BiometricUnlockController.this.releaseBiometricWakeLock();
            }
        };
        this.mWakefulnessObserver = new WakefulnessLifecycle.Observer() { // from class: com.android.systemui.statusbar.phone.BiometricUnlockController.3
            @Override // com.android.systemui.keyguard.WakefulnessLifecycle.Observer
            public void onFinishedWakingUp() {
                if (BiometricUnlockController.this.mPendingShowBouncer) {
                    BiometricUnlockController.this.showBouncer();
                }
            }
        };
        this.mScreenObserver = new ScreenLifecycle.Observer() { // from class: com.android.systemui.statusbar.phone.BiometricUnlockController.4
            @Override // com.android.systemui.keyguard.ScreenLifecycle.Observer
            public void onScreenTurnedOn() {
                BiometricUnlockController.this.mHasScreenTurnedOnSinceAuthenticating = true;
            }
        };
        this.mContext = context;
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
        this.mUpdateMonitor = keyguardUpdateMonitor;
        this.mUpdateMonitor.registerCallback(this);
        this.mMediaManager = (NotificationMediaManager) Dependency.get(NotificationMediaManager.class);
        ((WakefulnessLifecycle) Dependency.get(WakefulnessLifecycle.class)).addObserver(this.mWakefulnessObserver);
        ((ScreenLifecycle) Dependency.get(ScreenLifecycle.class)).addObserver(this.mScreenObserver);
        this.mStatusBarWindowController = (StatusBarWindowController) Dependency.get(StatusBarWindowController.class);
        this.mDozeScrimController = dozeScrimController;
        this.mKeyguardViewMediator = keyguardViewMediator;
        this.mScrimController = scrimController;
        this.mStatusBar = statusBar;
        this.mUnlockMethodCache = unlockMethodCache;
        this.mHandler = handler;
        this.mWakeUpDelay = wakeUpDelay;
        this.mKeyguardBypassController = keyguardBypassController;
        this.mKeyguardBypassController.setUnlockController(this);
    }

    public void setStatusBarKeyguardViewManager(StatusBarKeyguardViewManager statusBarKeyguardViewManager) {
        this.mStatusBarKeyguardViewManager = statusBarKeyguardViewManager;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseBiometricWakeLock() {
        if (this.mWakeLock != null) {
            this.mHandler.removeCallbacks(this.mReleaseBiometricWakeLockRunnable);
            Log.i(TAG, "releasing biometric wakelock");
            this.mWakeLock.release();
            this.mWakeLock = null;
        }
    }

    @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
    public void onBiometricAcquired(BiometricSourceType biometricSourceType) {
        Trace.beginSection("BiometricUnlockController#onBiometricAcquired");
        releaseBiometricWakeLock();
        if (!this.mUpdateMonitor.isDeviceInteractive()) {
            if (LatencyTracker.isEnabled(this.mContext)) {
                int action = 2;
                if (biometricSourceType == BiometricSourceType.FACE) {
                    action = 6;
                }
                LatencyTracker.getInstance(this.mContext).onActionStart(action);
            }
            this.mWakeLock = this.mPowerManager.newWakeLock(1, BIOMETRIC_WAKE_LOCK_NAME);
            Trace.beginSection("acquiring wake-and-unlock");
            this.mWakeLock.acquire();
            Trace.endSection();
            Log.i(TAG, "biometric acquired, grabbing biometric wakelock");
            this.mHandler.postDelayed(this.mReleaseBiometricWakeLockRunnable, BIOMETRIC_WAKELOCK_TIMEOUT_MS);
        }
        Trace.endSection();
    }

    private boolean pulsingOrAod() {
        ScrimState scrimState = this.mScrimController.getState();
        return scrimState == ScrimState.AOD || scrimState == ScrimState.PULSING;
    }

    @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
    /* renamed from: onBiometricAuthenticated */
    public void lambda$onFinishedGoingToSleep$1$BiometricUnlockController(int userId, BiometricSourceType biometricSourceType) {
        Trace.beginSection("BiometricUnlockController#onBiometricAuthenticated");
        if (this.mUpdateMonitor.isGoingToSleep()) {
            this.mPendingAuthenticatedUserId = userId;
            this.mPendingAuthenticatedBioSourceType = biometricSourceType;
            Trace.endSection();
            return;
        }
        this.mMetricsLogger.write(new LogMaker(1697).setType(10).setSubtype(toSubtype(biometricSourceType)));
        boolean unlockAllowed = this.mKeyguardBypassController.onBiometricAuthenticated(biometricSourceType);
        if (unlockAllowed) {
            this.mKeyguardViewMediator.userActivity();
            startWakeAndUnlock(biometricSourceType);
            return;
        }
        Log.d(TAG, "onBiometricAuthenticated aborted by bypass controller");
    }

    public void startWakeAndUnlock(BiometricSourceType biometricSourceType) {
        startWakeAndUnlock(calculateMode(biometricSourceType));
    }

    public void startWakeAndUnlock(int mode) {
        Log.v(TAG, "startWakeAndUnlock(" + mode + NavigationBarInflaterView.KEY_CODE_END);
        final boolean wasDeviceInteractive = this.mUpdateMonitor.isDeviceInteractive();
        this.mMode = mode;
        this.mHasScreenTurnedOnSinceAuthenticating = false;
        if (this.mMode == 2 && pulsingOrAod()) {
            this.mStatusBarWindowController.setForceDozeBrightness(true);
        }
        boolean alwaysOnEnabled = DozeParameters.getInstance(this.mContext).getAlwaysOn();
        final boolean delayWakeUp = mode == 1 && alwaysOnEnabled && this.mWakeUpDelay > 0;
        Runnable wakeUp = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$BiometricUnlockController$eARUOiIHQidy4dPvrf3UVu6gsv0
            @Override // java.lang.Runnable
            public final void run() {
                BiometricUnlockController.this.lambda$startWakeAndUnlock$0$BiometricUnlockController(wasDeviceInteractive, delayWakeUp);
            }
        };
        if (!delayWakeUp && this.mMode != 0) {
            wakeUp.run();
        }
        int i = this.mMode;
        switch (i) {
            case 1:
            case 2:
            case 6:
                if (i == 2) {
                    Trace.beginSection("MODE_WAKE_AND_UNLOCK_PULSING");
                    this.mMediaManager.updateMediaMetaData(false, true);
                } else if (i == 1) {
                    Trace.beginSection("MODE_WAKE_AND_UNLOCK");
                } else {
                    Trace.beginSection("MODE_WAKE_AND_UNLOCK_FROM_DREAM");
                    this.mUpdateMonitor.awakenFromDream();
                }
                this.mStatusBarWindowController.setStatusBarFocusable(false);
                if (delayWakeUp) {
                    this.mHandler.postDelayed(wakeUp, this.mWakeUpDelay);
                } else {
                    this.mKeyguardViewMediator.onWakeAndUnlocking();
                }
                if (this.mStatusBar.getNavigationBarView() != null) {
                    this.mStatusBar.getNavigationBarView().setWakeAndUnlocking(true);
                }
                Trace.endSection();
                break;
            case 3:
            case 5:
                Trace.beginSection("MODE_UNLOCK_COLLAPSING or MODE_SHOW_BOUNCER");
                if (!wasDeviceInteractive) {
                    this.mPendingShowBouncer = true;
                } else {
                    showBouncer();
                }
                Trace.endSection();
                break;
            case 7:
            case 8:
                Trace.beginSection("MODE_DISMISS_BOUNCER or MODE_UNLOCK_FADING");
                this.mStatusBarKeyguardViewManager.notifyKeyguardAuthenticated(false);
                Trace.endSection();
                break;
        }
        this.mStatusBar.notifyBiometricAuthModeChanged();
        Trace.endSection();
    }

    public /* synthetic */ void lambda$startWakeAndUnlock$0$BiometricUnlockController(boolean wasDeviceInteractive, boolean delayWakeUp) {
        if (!wasDeviceInteractive) {
            Log.i(TAG, "bio wakelock: Authenticated, waking up...");
            this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), 4, "android.policy:BIOMETRIC");
        }
        if (delayWakeUp) {
            this.mKeyguardViewMediator.onWakeAndUnlocking();
        }
        Trace.beginSection("release wake-and-unlock");
        releaseBiometricWakeLock();
        Trace.endSection();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showBouncer() {
        if (this.mMode == 3) {
            this.mStatusBarKeyguardViewManager.showBouncer(false);
        }
        this.mStatusBarKeyguardViewManager.animateCollapsePanels(BIOMETRIC_COLLAPSE_SPEEDUP_FACTOR);
        this.mPendingShowBouncer = false;
    }

    @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
    public void onStartedGoingToSleep(int why) {
        resetMode();
        this.mFadedAwayAfterWakeAndUnlock = false;
        this.mPendingAuthenticatedUserId = -1;
        this.mPendingAuthenticatedBioSourceType = null;
    }

    @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
    public void onFinishedGoingToSleep(int why) {
        Trace.beginSection("BiometricUnlockController#onFinishedGoingToSleep");
        final BiometricSourceType pendingType = this.mPendingAuthenticatedBioSourceType;
        final int pendingUserId = this.mPendingAuthenticatedUserId;
        if (pendingUserId != -1 && pendingType != null) {
            this.mHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$BiometricUnlockController$vuxdlMXJFOLKBJ7XnmJEfPu__e4
                @Override // java.lang.Runnable
                public final void run() {
                    BiometricUnlockController.this.lambda$onFinishedGoingToSleep$1$BiometricUnlockController(pendingUserId, pendingType);
                }
            });
        }
        this.mPendingAuthenticatedUserId = -1;
        this.mPendingAuthenticatedBioSourceType = null;
        Trace.endSection();
    }

    public boolean hasPendingAuthentication() {
        return this.mPendingAuthenticatedUserId != -1 && this.mUpdateMonitor.isUnlockingWithBiometricAllowed() && this.mPendingAuthenticatedUserId == KeyguardUpdateMonitor.getCurrentUser();
    }

    public int getMode() {
        return this.mMode;
    }

    private int calculateMode(BiometricSourceType biometricSourceType) {
        if (biometricSourceType == BiometricSourceType.FACE || biometricSourceType == BiometricSourceType.IRIS) {
            return calculateModeForPassiveAuth();
        }
        return calculateModeForFingerprint();
    }

    private int calculateModeForFingerprint() {
        boolean unlockingAllowed = this.mUpdateMonitor.isUnlockingWithBiometricAllowed();
        boolean deviceDreaming = this.mUpdateMonitor.isDreaming();
        if (!this.mUpdateMonitor.isDeviceInteractive()) {
            if (!this.mStatusBarKeyguardViewManager.isShowing()) {
                return 4;
            }
            if (this.mDozeScrimController.isPulsing() && unlockingAllowed) {
                return 2;
            }
            return (unlockingAllowed || !this.mUnlockMethodCache.isMethodSecure()) ? 1 : 3;
        } else if (unlockingAllowed && deviceDreaming) {
            return 6;
        } else {
            if (this.mStatusBarKeyguardViewManager.isShowing()) {
                if (this.mStatusBarKeyguardViewManager.bouncerIsOrWillBeShowing() && unlockingAllowed) {
                    return 8;
                }
                if (unlockingAllowed) {
                    return 5;
                }
                return !this.mStatusBarKeyguardViewManager.isBouncerShowing() ? 3 : 0;
            }
            return 0;
        }
    }

    private int calculateModeForPassiveAuth() {
        boolean unlockingAllowed = this.mUpdateMonitor.isUnlockingWithBiometricAllowed();
        boolean deviceDreaming = this.mUpdateMonitor.isDreaming();
        boolean bypass = this.mKeyguardBypassController.getBypassEnabled();
        if (!this.mUpdateMonitor.isDeviceInteractive()) {
            return !this.mStatusBarKeyguardViewManager.isShowing() ? bypass ? 1 : 4 : !unlockingAllowed ? bypass ? 3 : 0 : this.mDozeScrimController.isPulsing() ? bypass ? 2 : 0 : bypass ? 2 : 4;
        } else if (unlockingAllowed && deviceDreaming) {
            return bypass ? 6 : 4;
        } else if (this.mStatusBarKeyguardViewManager.isShowing()) {
            return (this.mStatusBarKeyguardViewManager.bouncerIsOrWillBeShowing() && unlockingAllowed) ? (bypass && this.mKeyguardBypassController.canPlaySubtleWindowAnimations()) ? 7 : 8 : unlockingAllowed ? bypass ? 7 : 0 : bypass ? 3 : 0;
        } else {
            return 0;
        }
    }

    @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
    public void onBiometricAuthFailed(BiometricSourceType biometricSourceType) {
        this.mMetricsLogger.write(new LogMaker(1697).setType(11).setSubtype(toSubtype(biometricSourceType)));
        cleanup();
    }

    @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
    public void onBiometricError(int msgId, String errString, BiometricSourceType biometricSourceType) {
        this.mMetricsLogger.write(new LogMaker(1697).setType(15).setSubtype(toSubtype(biometricSourceType)).addTaggedData(1741, Integer.valueOf(msgId)));
        cleanup();
    }

    private void cleanup() {
        releaseBiometricWakeLock();
    }

    public void startKeyguardFadingAway() {
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.systemui.statusbar.phone.BiometricUnlockController.2
            @Override // java.lang.Runnable
            public void run() {
                BiometricUnlockController.this.mStatusBarWindowController.setForceDozeBrightness(false);
            }
        }, 96L);
    }

    public void finishKeyguardFadingAway() {
        if (isWakeAndUnlock()) {
            this.mFadedAwayAfterWakeAndUnlock = true;
        }
        resetMode();
    }

    private void resetMode() {
        this.mMode = 0;
        this.mStatusBarWindowController.setForceDozeBrightness(false);
        if (this.mStatusBar.getNavigationBarView() != null) {
            this.mStatusBar.getNavigationBarView().setWakeAndUnlocking(false);
        }
        this.mStatusBar.notifyBiometricAuthModeChanged();
    }

    public boolean hasScreenTurnedOnSinceAuthenticating() {
        return this.mHasScreenTurnedOnSinceAuthenticating;
    }

    public void dump(PrintWriter pw) {
        pw.println(" BiometricUnlockController:");
        pw.print("   mMode=");
        pw.println(this.mMode);
        pw.print("   mWakeLock=");
        pw.println(this.mWakeLock);
    }

    public boolean isWakeAndUnlock() {
        int i = this.mMode;
        return i == 1 || i == 2 || i == 6;
    }

    public boolean unlockedByWakeAndUnlock() {
        return isWakeAndUnlock() || this.mFadedAwayAfterWakeAndUnlock;
    }

    public boolean isBiometricUnlock() {
        int i;
        return isWakeAndUnlock() || (i = this.mMode) == 5 || i == 7;
    }

    public boolean isUnlockFading() {
        return this.mMode == 7;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.statusbar.phone.BiometricUnlockController$5  reason: invalid class name */
    /* loaded from: classes21.dex */
    public static /* synthetic */ class AnonymousClass5 {
        static final /* synthetic */ int[] $SwitchMap$android$hardware$biometrics$BiometricSourceType = new int[BiometricSourceType.values().length];

        static {
            try {
                $SwitchMap$android$hardware$biometrics$BiometricSourceType[BiometricSourceType.FINGERPRINT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$hardware$biometrics$BiometricSourceType[BiometricSourceType.FACE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$hardware$biometrics$BiometricSourceType[BiometricSourceType.IRIS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private int toSubtype(BiometricSourceType biometricSourceType) {
        int i = AnonymousClass5.$SwitchMap$android$hardware$biometrics$BiometricSourceType[biometricSourceType.ordinal()];
        if (i != 1) {
            if (i != 2) {
                return i != 3 ? 3 : 2;
            }
            return 1;
        }
        return 0;
    }
}
