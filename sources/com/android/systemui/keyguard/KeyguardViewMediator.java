package com.android.systemui.keyguard;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.StatusBarManager;
import android.app.trust.TrustManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.hardware.biometrics.BiometricSourceType;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.EventLog;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.android.internal.policy.IKeyguardDismissCallback;
import com.android.internal.policy.IKeyguardDrawnCallback;
import com.android.internal.policy.IKeyguardExitCallback;
import com.android.internal.policy.IKeyguardStateCallback;
import com.android.internal.telephony.IccCardConstants;
import com.android.internal.util.LatencyTracker;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardDisplayManager;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.keyguard.ViewMediatorCallback;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.SystemUI;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.UiOffloadThread;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.phone.BiometricUnlockController;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.android.systemui.statusbar.phone.NotificationPanelView;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.phone.StatusBarWindowController;
import com.android.systemui.util.InjectionInflationController;
import com.xiaopeng.speech.speechwidget.ListWidget;
import com.xiaopeng.systemui.controller.OsdController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
/* loaded from: classes21.dex */
public class KeyguardViewMediator extends SystemUI {
    public static final int AWAKE_INTERVAL_BOUNCER_MS = 10000;
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_SIM_STATES = true;
    private static final String DELAYED_KEYGUARD_ACTION = "com.android.internal.policy.impl.PhoneWindowManager.DELAYED_KEYGUARD";
    private static final String DELAYED_LOCK_PROFILE_ACTION = "com.android.internal.policy.impl.PhoneWindowManager.DELAYED_LOCK";
    private static final int DISMISS = 11;
    private static final int HIDE = 2;
    private static final int KEYGUARD_DISPLAY_TIMEOUT_DELAY_DEFAULT = 30000;
    private static final int KEYGUARD_DONE = 7;
    private static final int KEYGUARD_DONE_DRAWING = 8;
    private static final int KEYGUARD_DONE_DRAWING_TIMEOUT_MS = 2000;
    private static final int KEYGUARD_DONE_PENDING_TIMEOUT = 13;
    private static final long KEYGUARD_DONE_PENDING_TIMEOUT_MS = 3000;
    private static final int KEYGUARD_LOCK_AFTER_DELAY_DEFAULT = 5000;
    private static final int KEYGUARD_TIMEOUT = 10;
    private static final int NOTIFY_FINISHED_GOING_TO_SLEEP = 5;
    private static final int NOTIFY_SCREEN_TURNED_OFF = 16;
    private static final int NOTIFY_SCREEN_TURNED_ON = 15;
    private static final int NOTIFY_SCREEN_TURNING_ON = 6;
    private static final int NOTIFY_STARTED_GOING_TO_SLEEP = 17;
    private static final int NOTIFY_STARTED_WAKING_UP = 14;
    public static final String OPTION_FORCE_SHOW = "force_show";
    private static final int RESET = 3;
    private static final int SET_OCCLUDED = 9;
    private static final int SHOW = 1;
    private static final int START_KEYGUARD_EXIT_ANIM = 12;
    private static final String SYSTEMUI_PERMISSION = "com.android.systemui.permission.SELF";
    private static final int SYSTEM_READY = 18;
    private static final String TAG = "KeyguardViewMediator";
    private static final Intent USER_PRESENT_INTENT = new Intent("android.intent.action.USER_PRESENT").addFlags(606076928);
    private static final int VERIFY_UNLOCK = 4;
    private AlarmManager mAlarmManager;
    private boolean mAodShowing;
    private AudioManager mAudioManager;
    private boolean mBootCompleted;
    private boolean mBootSendUserPresent;
    private CharSequence mCustomMessage;
    private int mDelayedProfileShowingSequence;
    private int mDelayedShowingSequence;
    private boolean mDeviceInteractive;
    private boolean mDozing;
    private IKeyguardDrawnCallback mDrawnCallback;
    private IKeyguardExitCallback mExitSecureCallback;
    private boolean mGoingToSleep;
    private Animation mHideAnimation;
    private boolean mHiding;
    private boolean mInputRestricted;
    private KeyguardDisplayManager mKeyguardDisplayManager;
    private boolean mLockLater;
    private LockPatternUtils mLockPatternUtils;
    private int mLockSoundId;
    private int mLockSoundStreamId;
    private float mLockSoundVolume;
    private SoundPool mLockSounds;
    private PowerManager mPM;
    private boolean mPendingLock;
    private boolean mPendingReset;
    private boolean mPulsing;
    private PowerManager.WakeLock mShowKeyguardWakeLock;
    private boolean mShowing;
    private boolean mShuttingDown;
    private StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
    private StatusBarManager mStatusBarManager;
    private boolean mSystemReady;
    private TrustManager mTrustManager;
    private int mTrustedSoundId;
    private int mUiSoundsStreamType;
    private int mUnlockSoundId;
    private KeyguardUpdateMonitor mUpdateMonitor;
    private boolean mWakeAndUnlocking;
    private WorkLockActivityController mWorkLockController;
    private final StatusBarWindowController mStatusBarWindowController = (StatusBarWindowController) Dependency.get(StatusBarWindowController.class);
    private final UiOffloadThread mUiOffloadThread = (UiOffloadThread) Dependency.get(UiOffloadThread.class);
    private boolean mExternallyEnabled = true;
    private boolean mNeedToReshowWhenReenabled = false;
    private boolean mOccluded = false;
    private final DismissCallbackRegistry mDismissCallbackRegistry = new DismissCallbackRegistry();
    private final SparseArray<IccCardConstants.State> mLastSimStates = new SparseArray<>();
    private String mPhoneState = TelephonyManager.EXTRA_STATE_IDLE;
    private boolean mWaitingUntilKeyguardVisible = false;
    private boolean mKeyguardDonePending = false;
    private boolean mHideAnimationRun = false;
    private boolean mHideAnimationRunning = false;
    private final ArrayList<IKeyguardStateCallback> mKeyguardStateCallbacks = new ArrayList<>();
    KeyguardUpdateMonitorCallback mUpdateCallback = new KeyguardUpdateMonitorCallback() { // from class: com.android.systemui.keyguard.KeyguardViewMediator.1
        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onUserSwitching(int userId) {
            synchronized (KeyguardViewMediator.this) {
                KeyguardViewMediator.this.resetKeyguardDonePendingLocked();
                if (!KeyguardViewMediator.this.mLockPatternUtils.isLockScreenDisabled(userId)) {
                    KeyguardViewMediator.this.resetStateLocked();
                } else {
                    KeyguardViewMediator.this.dismiss(null, null);
                }
                KeyguardViewMediator.this.adjustStatusBarLocked();
            }
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onUserSwitchComplete(int userId) {
            UserInfo info;
            if (userId == 0 || (info = UserManager.get(KeyguardViewMediator.this.mContext).getUserInfo(userId)) == null || KeyguardViewMediator.this.mLockPatternUtils.isSecure(userId)) {
                return;
            }
            if (info.isGuest() || info.isDemo()) {
                KeyguardViewMediator.this.dismiss(null, null);
            }
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onUserInfoChanged(int userId) {
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onClockVisibilityChanged() {
            KeyguardViewMediator.this.adjustStatusBarLocked();
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onDeviceProvisioned() {
            KeyguardViewMediator.this.sendUserPresentBroadcast();
            synchronized (KeyguardViewMediator.this) {
                if (KeyguardViewMediator.this.mustNotUnlockCurrentUser()) {
                    KeyguardViewMediator.this.doKeyguardLocked(null);
                }
            }
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onSimStateChanged(int subId, int slotId, IccCardConstants.State simState) {
            boolean simWasLocked;
            Log.d(KeyguardViewMediator.TAG, "onSimStateChanged(subId=" + subId + ", slotId=" + slotId + ",state=" + simState + NavigationBarInflaterView.KEY_CODE_END);
            int size = KeyguardViewMediator.this.mKeyguardStateCallbacks.size();
            boolean simPinSecure = KeyguardViewMediator.this.mUpdateMonitor.isSimPinSecure();
            for (int i = size + (-1); i >= 0; i--) {
                try {
                    ((IKeyguardStateCallback) KeyguardViewMediator.this.mKeyguardStateCallbacks.get(i)).onSimSecureStateChanged(simPinSecure);
                } catch (RemoteException e) {
                    Slog.w(KeyguardViewMediator.TAG, "Failed to call onSimSecureStateChanged", e);
                    if (e instanceof DeadObjectException) {
                        KeyguardViewMediator.this.mKeyguardStateCallbacks.remove(i);
                    }
                }
            }
            synchronized (KeyguardViewMediator.this) {
                IccCardConstants.State lastState = (IccCardConstants.State) KeyguardViewMediator.this.mLastSimStates.get(slotId);
                if (lastState != IccCardConstants.State.PIN_REQUIRED && lastState != IccCardConstants.State.PUK_REQUIRED) {
                    simWasLocked = false;
                    KeyguardViewMediator.this.mLastSimStates.append(slotId, simState);
                }
                simWasLocked = true;
                KeyguardViewMediator.this.mLastSimStates.append(slotId, simState);
            }
            switch (AnonymousClass7.$SwitchMap$com$android$internal$telephony$IccCardConstants$State[simState.ordinal()]) {
                case 1:
                case 2:
                    synchronized (KeyguardViewMediator.this) {
                        if (KeyguardViewMediator.this.shouldWaitForProvisioning()) {
                            if (KeyguardViewMediator.this.mShowing) {
                                KeyguardViewMediator.this.resetStateLocked();
                            } else {
                                Log.d(KeyguardViewMediator.TAG, "ICC_ABSENT isn't showing, we need to show the keyguard since the device isn't provisioned yet.");
                                KeyguardViewMediator.this.doKeyguardLocked(null);
                            }
                        }
                        if (simState == IccCardConstants.State.ABSENT && simWasLocked) {
                            Log.d(KeyguardViewMediator.TAG, "SIM moved to ABSENT when the previous state was locked. Reset the state.");
                            KeyguardViewMediator.this.resetStateLocked();
                        }
                    }
                    return;
                case 3:
                case 4:
                    synchronized (KeyguardViewMediator.this) {
                        if (KeyguardViewMediator.this.mShowing) {
                            KeyguardViewMediator.this.resetStateLocked();
                        } else {
                            Log.d(KeyguardViewMediator.TAG, "INTENT_VALUE_ICC_LOCKED and keygaurd isn't showing; need to show keyguard so user can enter sim pin");
                            KeyguardViewMediator.this.doKeyguardLocked(null);
                        }
                    }
                    return;
                case 5:
                    synchronized (KeyguardViewMediator.this) {
                        if (!KeyguardViewMediator.this.mShowing) {
                            Log.d(KeyguardViewMediator.TAG, "PERM_DISABLED and keygaurd isn't showing.");
                            KeyguardViewMediator.this.doKeyguardLocked(null);
                        } else {
                            Log.d(KeyguardViewMediator.TAG, "PERM_DISABLED, resetStateLocked toshow permanently disabled message in lockscreen.");
                            KeyguardViewMediator.this.resetStateLocked();
                        }
                    }
                    return;
                case 6:
                    synchronized (KeyguardViewMediator.this) {
                        Log.d(KeyguardViewMediator.TAG, "READY, reset state? " + KeyguardViewMediator.this.mShowing);
                        if (KeyguardViewMediator.this.mShowing && simWasLocked) {
                            Log.d(KeyguardViewMediator.TAG, "SIM moved to READY when the previous state was locked. Reset the state.");
                            KeyguardViewMediator.this.resetStateLocked();
                        }
                    }
                    return;
                default:
                    Log.v(KeyguardViewMediator.TAG, "Unspecific state: " + simState);
                    return;
            }
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onBiometricAuthFailed(BiometricSourceType biometricSourceType) {
            int currentUser = KeyguardUpdateMonitor.getCurrentUser();
            if (KeyguardViewMediator.this.mLockPatternUtils.isSecure(currentUser)) {
                KeyguardViewMediator.this.mLockPatternUtils.getDevicePolicyManager().reportFailedBiometricAttempt(currentUser);
            }
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onBiometricAuthenticated(int userId, BiometricSourceType biometricSourceType) {
            if (KeyguardViewMediator.this.mLockPatternUtils.isSecure(userId)) {
                KeyguardViewMediator.this.mLockPatternUtils.getDevicePolicyManager().reportSuccessfulBiometricAttempt(userId);
            }
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onTrustChanged(int userId) {
            if (userId == KeyguardUpdateMonitor.getCurrentUser()) {
                synchronized (KeyguardViewMediator.this) {
                    KeyguardViewMediator.this.notifyTrustedChangedLocked(KeyguardViewMediator.this.mUpdateMonitor.getUserHasTrust(userId));
                }
            }
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onHasLockscreenWallpaperChanged(boolean hasLockscreenWallpaper) {
            synchronized (KeyguardViewMediator.this) {
                KeyguardViewMediator.this.notifyHasLockscreenWallpaperChanged(hasLockscreenWallpaper);
            }
        }
    };
    ViewMediatorCallback mViewMediatorCallback = new ViewMediatorCallback() { // from class: com.android.systemui.keyguard.KeyguardViewMediator.2
        @Override // com.android.keyguard.ViewMediatorCallback
        public void userActivity() {
            KeyguardViewMediator.this.userActivity();
        }

        @Override // com.android.keyguard.ViewMediatorCallback
        public void keyguardDone(boolean strongAuth, int targetUserId) {
            if (targetUserId == ActivityManager.getCurrentUser()) {
                KeyguardViewMediator.this.tryKeyguardDone();
            }
        }

        @Override // com.android.keyguard.ViewMediatorCallback
        public void keyguardDoneDrawing() {
            Trace.beginSection("KeyguardViewMediator.mViewMediatorCallback#keyguardDoneDrawing");
            KeyguardViewMediator.this.mHandler.sendEmptyMessage(8);
            Trace.endSection();
        }

        @Override // com.android.keyguard.ViewMediatorCallback
        public void setNeedsInput(boolean needsInput) {
            KeyguardViewMediator.this.mStatusBarKeyguardViewManager.setNeedsInput(needsInput);
        }

        @Override // com.android.keyguard.ViewMediatorCallback
        public void keyguardDonePending(boolean strongAuth, int targetUserId) {
            Trace.beginSection("KeyguardViewMediator.mViewMediatorCallback#keyguardDonePending");
            if (targetUserId == ActivityManager.getCurrentUser()) {
                KeyguardViewMediator.this.mKeyguardDonePending = true;
                KeyguardViewMediator.this.mHideAnimationRun = true;
                KeyguardViewMediator.this.mHideAnimationRunning = true;
                KeyguardViewMediator.this.mStatusBarKeyguardViewManager.startPreHideAnimation(KeyguardViewMediator.this.mHideAnimationFinishedRunnable);
                KeyguardViewMediator.this.mHandler.sendEmptyMessageDelayed(13, KeyguardViewMediator.KEYGUARD_DONE_PENDING_TIMEOUT_MS);
                Trace.endSection();
                return;
            }
            Trace.endSection();
        }

        @Override // com.android.keyguard.ViewMediatorCallback
        public void keyguardGone() {
            Trace.beginSection("KeyguardViewMediator.mViewMediatorCallback#keyguardGone");
            KeyguardViewMediator.this.mKeyguardDisplayManager.hide();
            Trace.endSection();
        }

        @Override // com.android.keyguard.ViewMediatorCallback
        public void readyForKeyguardDone() {
            Trace.beginSection("KeyguardViewMediator.mViewMediatorCallback#readyForKeyguardDone");
            if (KeyguardViewMediator.this.mKeyguardDonePending) {
                KeyguardViewMediator.this.mKeyguardDonePending = false;
                KeyguardViewMediator.this.tryKeyguardDone();
            }
            Trace.endSection();
        }

        @Override // com.android.keyguard.ViewMediatorCallback
        public void resetKeyguard() {
            KeyguardViewMediator.this.resetStateLocked();
        }

        @Override // com.android.keyguard.ViewMediatorCallback
        public void onCancelClicked() {
            KeyguardViewMediator.this.mStatusBarKeyguardViewManager.onCancelClicked();
        }

        @Override // com.android.keyguard.ViewMediatorCallback
        public void onBouncerVisiblityChanged(boolean shown) {
            synchronized (KeyguardViewMediator.this) {
                KeyguardViewMediator.this.adjustStatusBarLocked(shown);
            }
        }

        @Override // com.android.keyguard.ViewMediatorCallback
        public void playTrustedSound() {
            KeyguardViewMediator.this.playTrustedSound();
        }

        @Override // com.android.keyguard.ViewMediatorCallback
        public boolean isScreenOn() {
            return KeyguardViewMediator.this.mDeviceInteractive;
        }

        @Override // com.android.keyguard.ViewMediatorCallback
        public int getBouncerPromptReason() {
            int currentUser = ActivityManager.getCurrentUser();
            boolean trust = KeyguardViewMediator.this.mTrustManager.isTrustUsuallyManaged(currentUser);
            boolean biometrics = KeyguardViewMediator.this.mUpdateMonitor.isUnlockingWithBiometricsPossible(currentUser);
            boolean any = trust || biometrics;
            KeyguardUpdateMonitor.StrongAuthTracker strongAuthTracker = KeyguardViewMediator.this.mUpdateMonitor.getStrongAuthTracker();
            int strongAuth = strongAuthTracker.getStrongAuthForUser(currentUser);
            if (!any || strongAuthTracker.hasUserAuthenticatedSinceBoot()) {
                if (any && (strongAuth & 16) != 0) {
                    return 2;
                }
                if (any && (strongAuth & 2) != 0) {
                    return 3;
                }
                if (trust && (strongAuth & 4) != 0) {
                    return 4;
                }
                if (!any || (strongAuth & 8) == 0) {
                    return 0;
                }
                return 5;
            }
            return 1;
        }

        @Override // com.android.keyguard.ViewMediatorCallback
        public CharSequence consumeCustomMessage() {
            CharSequence message = KeyguardViewMediator.this.mCustomMessage;
            KeyguardViewMediator.this.mCustomMessage = null;
            return message;
        }
    };
    private final BroadcastReceiver mDelayedLockBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.systemui.keyguard.KeyguardViewMediator.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (KeyguardViewMediator.DELAYED_KEYGUARD_ACTION.equals(intent.getAction())) {
                int sequence = intent.getIntExtra("seq", 0);
                synchronized (KeyguardViewMediator.this) {
                    if (KeyguardViewMediator.this.mDelayedShowingSequence == sequence) {
                        KeyguardViewMediator.this.doKeyguardLocked(null);
                    }
                }
            } else if (KeyguardViewMediator.DELAYED_LOCK_PROFILE_ACTION.equals(intent.getAction())) {
                int sequence2 = intent.getIntExtra("seq", 0);
                int userId = intent.getIntExtra("android.intent.extra.USER_ID", 0);
                if (userId != 0) {
                    synchronized (KeyguardViewMediator.this) {
                        if (KeyguardViewMediator.this.mDelayedProfileShowingSequence == sequence2) {
                            KeyguardViewMediator.this.lockProfile(userId);
                        }
                    }
                }
            }
        }
    };
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.systemui.keyguard.KeyguardViewMediator.4
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.ACTION_SHUTDOWN".equals(intent.getAction())) {
                synchronized (KeyguardViewMediator.this) {
                    KeyguardViewMediator.this.mShuttingDown = true;
                }
            }
        }
    };
    private Handler mHandler = new Handler(Looper.myLooper(), null, true) { // from class: com.android.systemui.keyguard.KeyguardViewMediator.5
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    KeyguardViewMediator.this.handleShow((Bundle) msg.obj);
                    return;
                case 2:
                    KeyguardViewMediator.this.handleHide();
                    return;
                case 3:
                    KeyguardViewMediator.this.handleReset();
                    return;
                case 4:
                    Trace.beginSection("KeyguardViewMediator#handleMessage VERIFY_UNLOCK");
                    KeyguardViewMediator.this.handleVerifyUnlock();
                    Trace.endSection();
                    return;
                case 5:
                    KeyguardViewMediator.this.handleNotifyFinishedGoingToSleep();
                    return;
                case 6:
                    Trace.beginSection("KeyguardViewMediator#handleMessage NOTIFY_SCREEN_TURNING_ON");
                    KeyguardViewMediator.this.handleNotifyScreenTurningOn((IKeyguardDrawnCallback) msg.obj);
                    Trace.endSection();
                    return;
                case 7:
                    Trace.beginSection("KeyguardViewMediator#handleMessage KEYGUARD_DONE");
                    KeyguardViewMediator.this.handleKeyguardDone();
                    Trace.endSection();
                    return;
                case 8:
                    Trace.beginSection("KeyguardViewMediator#handleMessage KEYGUARD_DONE_DRAWING");
                    KeyguardViewMediator.this.handleKeyguardDoneDrawing();
                    Trace.endSection();
                    return;
                case 9:
                    Trace.beginSection("KeyguardViewMediator#handleMessage SET_OCCLUDED");
                    KeyguardViewMediator.this.handleSetOccluded(msg.arg1 != 0, msg.arg2 != 0);
                    Trace.endSection();
                    return;
                case 10:
                    synchronized (KeyguardViewMediator.this) {
                        KeyguardViewMediator.this.doKeyguardLocked((Bundle) msg.obj);
                    }
                    return;
                case 11:
                    DismissMessage message = (DismissMessage) msg.obj;
                    KeyguardViewMediator.this.handleDismiss(message.getCallback(), message.getMessage());
                    return;
                case 12:
                    Trace.beginSection("KeyguardViewMediator#handleMessage START_KEYGUARD_EXIT_ANIM");
                    StartKeyguardExitAnimParams params = (StartKeyguardExitAnimParams) msg.obj;
                    KeyguardViewMediator.this.handleStartKeyguardExitAnimation(params.startTime, params.fadeoutDuration);
                    ((FalsingManager) Dependency.get(FalsingManager.class)).onSucccessfulUnlock();
                    Trace.endSection();
                    return;
                case 13:
                    Trace.beginSection("KeyguardViewMediator#handleMessage KEYGUARD_DONE_PENDING_TIMEOUT");
                    Log.w(KeyguardViewMediator.TAG, "Timeout while waiting for activity drawn!");
                    Trace.endSection();
                    return;
                case 14:
                    Trace.beginSection("KeyguardViewMediator#handleMessage NOTIFY_STARTED_WAKING_UP");
                    KeyguardViewMediator.this.handleNotifyStartedWakingUp();
                    Trace.endSection();
                    return;
                case 15:
                    Trace.beginSection("KeyguardViewMediator#handleMessage NOTIFY_SCREEN_TURNED_ON");
                    KeyguardViewMediator.this.handleNotifyScreenTurnedOn();
                    Trace.endSection();
                    return;
                case 16:
                    KeyguardViewMediator.this.handleNotifyScreenTurnedOff();
                    return;
                case 17:
                    KeyguardViewMediator.this.handleNotifyStartedGoingToSleep();
                    return;
                case 18:
                    KeyguardViewMediator.this.handleSystemReady();
                    return;
                default:
                    return;
            }
        }
    };
    private final Runnable mKeyguardGoingAwayRunnable = new AnonymousClass6();
    private final Runnable mHideAnimationFinishedRunnable = new Runnable() { // from class: com.android.systemui.keyguard.-$$Lambda$KeyguardViewMediator$cwsnZe582iHRLRSJWQeXdqmun1k
        @Override // java.lang.Runnable
        public final void run() {
            KeyguardViewMediator.this.lambda$new$4$KeyguardViewMediator();
        }
    };

    /* renamed from: com.android.systemui.keyguard.KeyguardViewMediator$7  reason: invalid class name */
    /* loaded from: classes21.dex */
    static /* synthetic */ class AnonymousClass7 {
        static final /* synthetic */ int[] $SwitchMap$com$android$internal$telephony$IccCardConstants$State = new int[IccCardConstants.State.values().length];

        static {
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.NOT_READY.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.ABSENT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.PIN_REQUIRED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.PUK_REQUIRED.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.PERM_DISABLED.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.READY.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    public void userActivity() {
        this.mPM.userActivity(SystemClock.uptimeMillis(), false);
    }

    boolean mustNotUnlockCurrentUser() {
        return UserManager.isSplitSystemUser() && KeyguardUpdateMonitor.getCurrentUser() == 0;
    }

    private void setupLocked() {
        this.mPM = (PowerManager) this.mContext.getSystemService("power");
        this.mTrustManager = (TrustManager) this.mContext.getSystemService("trust");
        this.mShowKeyguardWakeLock = this.mPM.newWakeLock(1, "show keyguard");
        boolean z = false;
        this.mShowKeyguardWakeLock.setReferenceCounted(false);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.ACTION_SHUTDOWN");
        this.mContext.registerReceiver(this.mBroadcastReceiver, filter);
        IntentFilter delayedActionFilter = new IntentFilter();
        delayedActionFilter.addAction(DELAYED_KEYGUARD_ACTION);
        delayedActionFilter.addAction(DELAYED_LOCK_PROFILE_ACTION);
        this.mContext.registerReceiver(this.mDelayedLockBroadcastReceiver, delayedActionFilter, "com.android.systemui.permission.SELF", null);
        InjectionInflationController injectionInflationController = new InjectionInflationController(SystemUIFactory.getInstance().getRootComponent());
        this.mKeyguardDisplayManager = new KeyguardDisplayManager(this.mContext, injectionInflationController);
        this.mAlarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        this.mUpdateMonitor = KeyguardUpdateMonitor.getInstance(this.mContext);
        this.mLockPatternUtils = new LockPatternUtils(this.mContext);
        KeyguardUpdateMonitor.setCurrentUser(ActivityManager.getCurrentUser());
        if (this.mContext.getResources().getBoolean(R.bool.config_enableKeyguardService)) {
            if (!shouldWaitForProvisioning() && !this.mLockPatternUtils.isLockScreenDisabled(KeyguardUpdateMonitor.getCurrentUser())) {
                z = true;
            }
            setShowingLocked(z, true);
        } else {
            setShowingLocked(false, true);
        }
        this.mStatusBarKeyguardViewManager = SystemUIFactory.getInstance().createStatusBarKeyguardViewManager(this.mContext, this.mViewMediatorCallback, this.mLockPatternUtils);
        ContentResolver cr = this.mContext.getContentResolver();
        this.mDeviceInteractive = this.mPM.isInteractive();
        this.mLockSounds = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(new AudioAttributes.Builder().setUsage(13).setContentType(4).build()).build();
        String soundPath = Settings.Global.getString(cr, "lock_sound");
        if (soundPath != null) {
            this.mLockSoundId = this.mLockSounds.load(soundPath, 1);
        }
        if (soundPath == null || this.mLockSoundId == 0) {
            Log.w(TAG, "failed to load lock sound from " + soundPath);
        }
        String soundPath2 = Settings.Global.getString(cr, "unlock_sound");
        if (soundPath2 != null) {
            this.mUnlockSoundId = this.mLockSounds.load(soundPath2, 1);
        }
        if (soundPath2 == null || this.mUnlockSoundId == 0) {
            Log.w(TAG, "failed to load unlock sound from " + soundPath2);
        }
        String soundPath3 = Settings.Global.getString(cr, "trusted_sound");
        if (soundPath3 != null) {
            this.mTrustedSoundId = this.mLockSounds.load(soundPath3, 1);
        }
        if (soundPath3 == null || this.mTrustedSoundId == 0) {
            Log.w(TAG, "failed to load trusted sound from " + soundPath3);
        }
        int lockSoundDefaultAttenuation = this.mContext.getResources().getInteger(17694822);
        this.mLockSoundVolume = (float) Math.pow(10.0d, lockSoundDefaultAttenuation / 20.0f);
        this.mHideAnimation = AnimationUtils.loadAnimation(this.mContext, 17432680);
        this.mWorkLockController = new WorkLockActivityController(this.mContext);
    }

    @Override // com.android.systemui.SystemUI
    public void start() {
        synchronized (this) {
            setupLocked();
        }
        putComponent(KeyguardViewMediator.class, this);
    }

    public void onSystemReady() {
        this.mHandler.obtainMessage(18).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSystemReady() {
        synchronized (this) {
            this.mSystemReady = true;
            doKeyguardLocked(null);
            this.mUpdateMonitor.registerCallback(this.mUpdateCallback);
        }
        maybeSendUserPresentBroadcast();
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x0046 A[Catch: all -> 0x0080, TryCatch #0 {, blocks: (B:4:0x0002, B:6:0x0013, B:11:0x001f, B:13:0x002d, B:17:0x003b, B:19:0x0042, B:34:0x006b, B:36:0x006f, B:37:0x0072, B:20:0x0046, B:22:0x004a, B:30:0x005b, B:31:0x0061, B:33:0x0069, B:16:0x0034), top: B:43:0x0002, inners: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:36:0x006f A[Catch: all -> 0x0080, TryCatch #0 {, blocks: (B:4:0x0002, B:6:0x0013, B:11:0x001f, B:13:0x002d, B:17:0x003b, B:19:0x0042, B:34:0x006b, B:36:0x006f, B:37:0x0072, B:20:0x0046, B:22:0x004a, B:30:0x005b, B:31:0x0061, B:33:0x0069, B:16:0x0034), top: B:43:0x0002, inners: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:45:0x002d A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onStartedGoingToSleep(int r9) {
        /*
            r8 = this;
            monitor-enter(r8)
            r0 = 0
            r8.mDeviceInteractive = r0     // Catch: java.lang.Throwable -> L80
            r1 = 1
            r8.mGoingToSleep = r1     // Catch: java.lang.Throwable -> L80
            int r2 = com.android.keyguard.KeyguardUpdateMonitor.getCurrentUser()     // Catch: java.lang.Throwable -> L80
            com.android.internal.widget.LockPatternUtils r3 = r8.mLockPatternUtils     // Catch: java.lang.Throwable -> L80
            boolean r3 = r3.getPowerButtonInstantlyLocks(r2)     // Catch: java.lang.Throwable -> L80
            if (r3 != 0) goto L1e
            com.android.internal.widget.LockPatternUtils r3 = r8.mLockPatternUtils     // Catch: java.lang.Throwable -> L80
            boolean r3 = r3.isSecure(r2)     // Catch: java.lang.Throwable -> L80
            if (r3 != 0) goto L1c
            goto L1e
        L1c:
            r3 = r0
            goto L1f
        L1e:
            r3 = r1
        L1f:
            int r4 = com.android.keyguard.KeyguardUpdateMonitor.getCurrentUser()     // Catch: java.lang.Throwable -> L80
            long r4 = r8.getLockTimeout(r4)     // Catch: java.lang.Throwable -> L80
            r8.mLockLater = r0     // Catch: java.lang.Throwable -> L80
            com.android.internal.policy.IKeyguardExitCallback r6 = r8.mExitSecureCallback     // Catch: java.lang.Throwable -> L80
            if (r6 == 0) goto L46
            com.android.internal.policy.IKeyguardExitCallback r6 = r8.mExitSecureCallback     // Catch: android.os.RemoteException -> L33 java.lang.Throwable -> L80
            r6.onKeyguardExitResult(r0)     // Catch: android.os.RemoteException -> L33 java.lang.Throwable -> L80
            goto L3b
        L33:
            r0 = move-exception
            java.lang.String r6 = "KeyguardViewMediator"
            java.lang.String r7 = "Failed to call onKeyguardExitResult(false)"
            android.util.Slog.w(r6, r7, r0)     // Catch: java.lang.Throwable -> L80
        L3b:
            r0 = 0
            r8.mExitSecureCallback = r0     // Catch: java.lang.Throwable -> L80
            boolean r0 = r8.mExternallyEnabled     // Catch: java.lang.Throwable -> L80
            if (r0 != 0) goto L6b
            r8.hideLocked()     // Catch: java.lang.Throwable -> L80
            goto L6b
        L46:
            boolean r0 = r8.mShowing     // Catch: java.lang.Throwable -> L80
            if (r0 == 0) goto L4d
            r8.mPendingReset = r1     // Catch: java.lang.Throwable -> L80
            goto L6b
        L4d:
            r0 = 3
            if (r9 != r0) goto L56
            r6 = 0
            int r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r0 > 0) goto L5b
        L56:
            r0 = 2
            if (r9 != r0) goto L61
            if (r3 != 0) goto L61
        L5b:
            r8.doKeyguardLaterLocked(r4)     // Catch: java.lang.Throwable -> L80
            r8.mLockLater = r1     // Catch: java.lang.Throwable -> L80
            goto L6b
        L61:
            com.android.internal.widget.LockPatternUtils r0 = r8.mLockPatternUtils     // Catch: java.lang.Throwable -> L80
            boolean r0 = r0.isLockScreenDisabled(r2)     // Catch: java.lang.Throwable -> L80
            if (r0 != 0) goto L6b
            r8.mPendingLock = r1     // Catch: java.lang.Throwable -> L80
        L6b:
            boolean r0 = r8.mPendingLock     // Catch: java.lang.Throwable -> L80
            if (r0 == 0) goto L72
            r8.playSounds(r1)     // Catch: java.lang.Throwable -> L80
        L72:
            monitor-exit(r8)     // Catch: java.lang.Throwable -> L80
            android.content.Context r0 = r8.mContext
            com.android.keyguard.KeyguardUpdateMonitor r0 = com.android.keyguard.KeyguardUpdateMonitor.getInstance(r0)
            r0.dispatchStartedGoingToSleep(r9)
            r8.notifyStartedGoingToSleep()
            return
        L80:
            r0 = move-exception
            monitor-exit(r8)     // Catch: java.lang.Throwable -> L80
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.keyguard.KeyguardViewMediator.onStartedGoingToSleep(int):void");
    }

    public void onFinishedGoingToSleep(int why, boolean cameraGestureTriggered) {
        synchronized (this) {
            this.mDeviceInteractive = false;
            this.mGoingToSleep = false;
            this.mWakeAndUnlocking = false;
            resetKeyguardDonePendingLocked();
            this.mHideAnimationRun = false;
            notifyFinishedGoingToSleep();
            if (cameraGestureTriggered) {
                Log.i(TAG, "Camera gesture was triggered, preventing Keyguard locking.");
                ((PowerManager) this.mContext.getSystemService(PowerManager.class)).wakeUp(SystemClock.uptimeMillis(), 5, "com.android.systemui:CAMERA_GESTURE_PREVENT_LOCK");
                this.mPendingLock = false;
                this.mPendingReset = false;
            }
            if (this.mPendingReset) {
                resetStateLocked();
                this.mPendingReset = false;
            }
            if (this.mPendingLock) {
                doKeyguardLocked(null);
                this.mPendingLock = false;
            }
            if (!this.mLockLater && !cameraGestureTriggered) {
                doKeyguardForChildProfilesLocked();
            }
        }
        KeyguardUpdateMonitor.getInstance(this.mContext).dispatchFinishedGoingToSleep(why);
    }

    private long getLockTimeout(int userId) {
        ContentResolver cr = this.mContext.getContentResolver();
        long lockAfterTimeout = Settings.Secure.getInt(cr, "lock_screen_lock_after_timeout", 5000);
        long policyTimeout = this.mLockPatternUtils.getDevicePolicyManager().getMaximumTimeToLock(null, userId);
        if (policyTimeout <= 0) {
            return lockAfterTimeout;
        }
        long displayTimeout = Settings.System.getInt(cr, "screen_off_timeout", 30000);
        long timeout = Math.min(policyTimeout - Math.max(displayTimeout, 0L), lockAfterTimeout);
        return Math.max(timeout, 0L);
    }

    private void doKeyguardLaterLocked() {
        long timeout = getLockTimeout(KeyguardUpdateMonitor.getCurrentUser());
        if (timeout == 0) {
            doKeyguardLocked(null);
        } else {
            doKeyguardLaterLocked(timeout);
        }
    }

    private void doKeyguardLaterLocked(long timeout) {
        long when = SystemClock.elapsedRealtime() + timeout;
        Intent intent = new Intent(DELAYED_KEYGUARD_ACTION);
        intent.putExtra("seq", this.mDelayedShowingSequence);
        intent.addFlags(268435456);
        PendingIntent sender = PendingIntent.getBroadcast(this.mContext, 0, intent, 268435456);
        this.mAlarmManager.setExactAndAllowWhileIdle(2, when, sender);
        doKeyguardLaterForChildProfilesLocked();
    }

    private void doKeyguardLaterForChildProfilesLocked() {
        int[] enabledProfileIds;
        UserManager um = UserManager.get(this.mContext);
        for (int profileId : um.getEnabledProfileIds(UserHandle.myUserId())) {
            if (this.mLockPatternUtils.isSeparateProfileChallengeEnabled(profileId)) {
                long userTimeout = getLockTimeout(profileId);
                if (userTimeout == 0) {
                    doKeyguardForChildProfilesLocked();
                } else {
                    long userWhen = SystemClock.elapsedRealtime() + userTimeout;
                    Intent lockIntent = new Intent(DELAYED_LOCK_PROFILE_ACTION);
                    lockIntent.putExtra("seq", this.mDelayedProfileShowingSequence);
                    lockIntent.putExtra("android.intent.extra.USER_ID", profileId);
                    lockIntent.addFlags(268435456);
                    PendingIntent lockSender = PendingIntent.getBroadcast(this.mContext, 0, lockIntent, 268435456);
                    this.mAlarmManager.setExactAndAllowWhileIdle(2, userWhen, lockSender);
                }
            }
        }
    }

    private void doKeyguardForChildProfilesLocked() {
        int[] enabledProfileIds;
        UserManager um = UserManager.get(this.mContext);
        for (int profileId : um.getEnabledProfileIds(UserHandle.myUserId())) {
            if (this.mLockPatternUtils.isSeparateProfileChallengeEnabled(profileId)) {
                lockProfile(profileId);
            }
        }
    }

    private void cancelDoKeyguardLaterLocked() {
        this.mDelayedShowingSequence++;
    }

    private void cancelDoKeyguardForChildProfilesLocked() {
        this.mDelayedProfileShowingSequence++;
    }

    public void onStartedWakingUp() {
        Trace.beginSection("KeyguardViewMediator#onStartedWakingUp");
        synchronized (this) {
            this.mDeviceInteractive = true;
            cancelDoKeyguardLaterLocked();
            cancelDoKeyguardForChildProfilesLocked();
            notifyStartedWakingUp();
        }
        KeyguardUpdateMonitor.getInstance(this.mContext).dispatchStartedWakingUp();
        maybeSendUserPresentBroadcast();
        Trace.endSection();
    }

    public void onScreenTurningOn(IKeyguardDrawnCallback callback) {
        Trace.beginSection("KeyguardViewMediator#onScreenTurningOn");
        notifyScreenOn(callback);
        Trace.endSection();
    }

    public void onScreenTurnedOn() {
        Trace.beginSection("KeyguardViewMediator#onScreenTurnedOn");
        notifyScreenTurnedOn();
        this.mUpdateMonitor.dispatchScreenTurnedOn();
        Trace.endSection();
    }

    public void onScreenTurnedOff() {
        notifyScreenTurnedOff();
        this.mUpdateMonitor.dispatchScreenTurnedOff();
    }

    private void maybeSendUserPresentBroadcast() {
        if (this.mSystemReady && this.mLockPatternUtils.isLockScreenDisabled(KeyguardUpdateMonitor.getCurrentUser())) {
            sendUserPresentBroadcast();
        } else if (this.mSystemReady && shouldWaitForProvisioning()) {
            getLockPatternUtils().userPresent(KeyguardUpdateMonitor.getCurrentUser());
        }
    }

    public void onDreamingStarted() {
        KeyguardUpdateMonitor.getInstance(this.mContext).dispatchDreamingStarted();
        synchronized (this) {
            if (this.mDeviceInteractive && this.mLockPatternUtils.isSecure(KeyguardUpdateMonitor.getCurrentUser())) {
                doKeyguardLaterLocked();
            }
        }
    }

    public void onDreamingStopped() {
        KeyguardUpdateMonitor.getInstance(this.mContext).dispatchDreamingStopped();
        synchronized (this) {
            if (this.mDeviceInteractive) {
                cancelDoKeyguardLaterLocked();
            }
        }
    }

    public void setKeyguardEnabled(boolean enabled) {
        synchronized (this) {
            this.mExternallyEnabled = enabled;
            if (!enabled && this.mShowing) {
                if (this.mExitSecureCallback != null) {
                    return;
                }
                this.mNeedToReshowWhenReenabled = true;
                updateInputRestrictedLocked();
                hideLocked();
            } else if (enabled && this.mNeedToReshowWhenReenabled) {
                this.mNeedToReshowWhenReenabled = false;
                updateInputRestrictedLocked();
                if (this.mExitSecureCallback != null) {
                    try {
                        this.mExitSecureCallback.onKeyguardExitResult(false);
                    } catch (RemoteException e) {
                        Slog.w(TAG, "Failed to call onKeyguardExitResult(false)", e);
                    }
                    this.mExitSecureCallback = null;
                    resetStateLocked();
                } else {
                    showLocked(null);
                    this.mWaitingUntilKeyguardVisible = true;
                    this.mHandler.sendEmptyMessageDelayed(8, OsdController.TN.DURATION_TIMEOUT_SHORT);
                    while (this.mWaitingUntilKeyguardVisible) {
                        try {
                            wait();
                        } catch (InterruptedException e2) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        }
    }

    public void verifyUnlock(IKeyguardExitCallback callback) {
        Trace.beginSection("KeyguardViewMediator#verifyUnlock");
        synchronized (this) {
            if (shouldWaitForProvisioning()) {
                try {
                    callback.onKeyguardExitResult(false);
                } catch (RemoteException e) {
                    Slog.w(TAG, "Failed to call onKeyguardExitResult(false)", e);
                }
            } else if (this.mExternallyEnabled) {
                Log.w(TAG, "verifyUnlock called when not externally disabled");
                try {
                    callback.onKeyguardExitResult(false);
                } catch (RemoteException e2) {
                    Slog.w(TAG, "Failed to call onKeyguardExitResult(false)", e2);
                }
            } else if (this.mExitSecureCallback != null) {
                try {
                    callback.onKeyguardExitResult(false);
                } catch (RemoteException e3) {
                    Slog.w(TAG, "Failed to call onKeyguardExitResult(false)", e3);
                }
            } else if (!isSecure()) {
                this.mExternallyEnabled = true;
                this.mNeedToReshowWhenReenabled = false;
                updateInputRestricted();
                try {
                    callback.onKeyguardExitResult(true);
                } catch (RemoteException e4) {
                    Slog.w(TAG, "Failed to call onKeyguardExitResult(false)", e4);
                }
            } else {
                try {
                    callback.onKeyguardExitResult(false);
                } catch (RemoteException e5) {
                    Slog.w(TAG, "Failed to call onKeyguardExitResult(false)", e5);
                }
            }
        }
        Trace.endSection();
    }

    public boolean isShowingAndNotOccluded() {
        return this.mShowing && !this.mOccluded;
    }

    public void setOccluded(boolean isOccluded, boolean animate) {
        Trace.beginSection("KeyguardViewMediator#setOccluded");
        this.mHandler.removeMessages(9);
        Message msg = this.mHandler.obtainMessage(9, isOccluded ? 1 : 0, animate ? 1 : 0);
        this.mHandler.sendMessage(msg);
        Trace.endSection();
    }

    public boolean isHiding() {
        return this.mHiding;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSetOccluded(boolean isOccluded, boolean animate) {
        Trace.beginSection("KeyguardViewMediator#handleSetOccluded");
        synchronized (this) {
            if (this.mHiding && isOccluded) {
                startKeyguardExitAnimation(0L, 0L);
            }
            if (this.mOccluded != isOccluded) {
                this.mOccluded = isOccluded;
                this.mUpdateMonitor.setKeyguardOccluded(isOccluded);
                this.mStatusBarKeyguardViewManager.setOccluded(isOccluded, animate && this.mDeviceInteractive);
                adjustStatusBarLocked();
            }
        }
        Trace.endSection();
    }

    public void doKeyguardTimeout(Bundle options) {
        this.mHandler.removeMessages(10);
        Message msg = this.mHandler.obtainMessage(10, options);
        this.mHandler.sendMessage(msg);
    }

    public boolean isInputRestricted() {
        return this.mShowing || this.mNeedToReshowWhenReenabled;
    }

    private void updateInputRestricted() {
        synchronized (this) {
            updateInputRestrictedLocked();
        }
    }

    private void updateInputRestrictedLocked() {
        boolean inputRestricted = isInputRestricted();
        if (this.mInputRestricted != inputRestricted) {
            this.mInputRestricted = inputRestricted;
            int size = this.mKeyguardStateCallbacks.size();
            for (int i = size - 1; i >= 0; i--) {
                IKeyguardStateCallback callback = this.mKeyguardStateCallbacks.get(i);
                try {
                    callback.onInputRestrictedStateChanged(inputRestricted);
                } catch (RemoteException e) {
                    Slog.w(TAG, "Failed to call onDeviceProvisioned", e);
                    if (e instanceof DeadObjectException) {
                        this.mKeyguardStateCallbacks.remove(callback);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doKeyguardLocked(Bundle options) {
        if (KeyguardUpdateMonitor.CORE_APPS_ONLY) {
            return;
        }
        boolean forceShow = true;
        if (!this.mExternallyEnabled) {
            this.mNeedToReshowWhenReenabled = true;
        } else if (this.mStatusBarKeyguardViewManager.isShowing()) {
            resetStateLocked();
        } else {
            if (!mustNotUnlockCurrentUser() || !this.mUpdateMonitor.isDeviceProvisioned()) {
                boolean requireSim = !SystemProperties.getBoolean("keyguard.no_require_sim", false);
                boolean absent = SubscriptionManager.isValidSubscriptionId(this.mUpdateMonitor.getNextSubIdForState(IccCardConstants.State.ABSENT));
                boolean disabled = SubscriptionManager.isValidSubscriptionId(this.mUpdateMonitor.getNextSubIdForState(IccCardConstants.State.PERM_DISABLED));
                boolean lockedOrMissing = this.mUpdateMonitor.isSimPinSecure() || ((absent || disabled) && requireSim);
                if (!lockedOrMissing && shouldWaitForProvisioning()) {
                    return;
                }
                if (options == null || !options.getBoolean(OPTION_FORCE_SHOW, false)) {
                    forceShow = false;
                }
                if (this.mLockPatternUtils.isLockScreenDisabled(KeyguardUpdateMonitor.getCurrentUser()) && !lockedOrMissing && !forceShow) {
                    return;
                }
                if (this.mLockPatternUtils.checkVoldPassword(KeyguardUpdateMonitor.getCurrentUser())) {
                    setShowingLocked(false);
                    hideLocked();
                    return;
                }
            }
            showLocked(options);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void lockProfile(int userId) {
        this.mTrustManager.setDeviceLockedForUser(userId, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean shouldWaitForProvisioning() {
        return (this.mUpdateMonitor.isDeviceProvisioned() || isSecure()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDismiss(IKeyguardDismissCallback callback, CharSequence message) {
        if (this.mShowing) {
            if (callback != null) {
                this.mDismissCallbackRegistry.addCallback(callback);
            }
            this.mCustomMessage = message;
            this.mStatusBarKeyguardViewManager.dismissAndCollapse();
        } else if (callback != null) {
            new DismissCallbackWrapper(callback).notifyDismissError();
        }
    }

    public void dismiss(IKeyguardDismissCallback callback, CharSequence message) {
        this.mHandler.obtainMessage(11, new DismissMessage(callback, message)).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetStateLocked() {
        Message msg = this.mHandler.obtainMessage(3);
        this.mHandler.sendMessage(msg);
    }

    private void verifyUnlockLocked() {
        this.mHandler.sendEmptyMessage(4);
    }

    private void notifyStartedGoingToSleep() {
        this.mHandler.sendEmptyMessage(17);
    }

    private void notifyFinishedGoingToSleep() {
        this.mHandler.sendEmptyMessage(5);
    }

    private void notifyStartedWakingUp() {
        this.mHandler.sendEmptyMessage(14);
    }

    private void notifyScreenOn(IKeyguardDrawnCallback callback) {
        Message msg = this.mHandler.obtainMessage(6, callback);
        this.mHandler.sendMessage(msg);
    }

    private void notifyScreenTurnedOn() {
        Message msg = this.mHandler.obtainMessage(15);
        this.mHandler.sendMessage(msg);
    }

    private void notifyScreenTurnedOff() {
        Message msg = this.mHandler.obtainMessage(16);
        this.mHandler.sendMessage(msg);
    }

    private void showLocked(Bundle options) {
        Trace.beginSection("KeyguardViewMediator#showLocked aqcuiring mShowKeyguardWakeLock");
        this.mShowKeyguardWakeLock.acquire();
        Message msg = this.mHandler.obtainMessage(1, options);
        this.mHandler.sendMessage(msg);
        Trace.endSection();
    }

    private void hideLocked() {
        Trace.beginSection("KeyguardViewMediator#hideLocked");
        Message msg = this.mHandler.obtainMessage(2);
        this.mHandler.sendMessage(msg);
        Trace.endSection();
    }

    public boolean isSecure() {
        return isSecure(KeyguardUpdateMonitor.getCurrentUser());
    }

    public boolean isSecure(int userId) {
        return this.mLockPatternUtils.isSecure(userId) || KeyguardUpdateMonitor.getInstance(this.mContext).isSimPinSecure();
    }

    public void setSwitchingUser(boolean switching) {
        KeyguardUpdateMonitor.getInstance(this.mContext).setSwitchingUser(switching);
    }

    public void setCurrentUser(int newUserId) {
        KeyguardUpdateMonitor.setCurrentUser(newUserId);
        synchronized (this) {
            notifyTrustedChangedLocked(this.mUpdateMonitor.getUserHasTrust(newUserId));
        }
    }

    public void keyguardDone() {
        Trace.beginSection("KeyguardViewMediator#keyguardDone");
        userActivity();
        EventLog.writeEvent(70000, 2);
        Message msg = this.mHandler.obtainMessage(7);
        this.mHandler.sendMessage(msg);
        Trace.endSection();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void tryKeyguardDone() {
        if (!this.mKeyguardDonePending && this.mHideAnimationRun && !this.mHideAnimationRunning) {
            handleKeyguardDone();
        } else if (!this.mHideAnimationRun) {
            this.mHideAnimationRun = true;
            this.mHideAnimationRunning = true;
            this.mStatusBarKeyguardViewManager.startPreHideAnimation(this.mHideAnimationFinishedRunnable);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleKeyguardDone() {
        Trace.beginSection("KeyguardViewMediator#handleKeyguardDone");
        final int currentUser = KeyguardUpdateMonitor.getCurrentUser();
        this.mUiOffloadThread.submit(new Runnable() { // from class: com.android.systemui.keyguard.-$$Lambda$KeyguardViewMediator$_5R5BVmx-ThRHQbevLLkSqkvnz0
            @Override // java.lang.Runnable
            public final void run() {
                KeyguardViewMediator.this.lambda$handleKeyguardDone$0$KeyguardViewMediator(currentUser);
            }
        });
        synchronized (this) {
            resetKeyguardDonePendingLocked();
        }
        this.mUpdateMonitor.clearBiometricRecognized();
        if (this.mGoingToSleep) {
            Log.i(TAG, "Device is going to sleep, aborting keyguardDone");
            return;
        }
        IKeyguardExitCallback iKeyguardExitCallback = this.mExitSecureCallback;
        if (iKeyguardExitCallback != null) {
            try {
                iKeyguardExitCallback.onKeyguardExitResult(true);
            } catch (RemoteException e) {
                Slog.w(TAG, "Failed to call onKeyguardExitResult()", e);
            }
            this.mExitSecureCallback = null;
            this.mExternallyEnabled = true;
            this.mNeedToReshowWhenReenabled = false;
            updateInputRestricted();
        }
        handleHide();
        Trace.endSection();
    }

    public /* synthetic */ void lambda$handleKeyguardDone$0$KeyguardViewMediator(int currentUser) {
        if (this.mLockPatternUtils.isSecure(currentUser)) {
            this.mLockPatternUtils.getDevicePolicyManager().reportKeyguardDismissed(currentUser);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendUserPresentBroadcast() {
        synchronized (this) {
            if (this.mBootCompleted) {
                final int currentUserId = KeyguardUpdateMonitor.getCurrentUser();
                final UserHandle currentUser = new UserHandle(currentUserId);
                final UserManager um = (UserManager) this.mContext.getSystemService("user");
                this.mUiOffloadThread.submit(new Runnable() { // from class: com.android.systemui.keyguard.-$$Lambda$KeyguardViewMediator$Zo4yApohhKZDVLRFEZ5fXw6KLNI
                    @Override // java.lang.Runnable
                    public final void run() {
                        KeyguardViewMediator.this.lambda$sendUserPresentBroadcast$1$KeyguardViewMediator(um, currentUser, currentUserId);
                    }
                });
            } else {
                this.mBootSendUserPresent = true;
            }
        }
    }

    public /* synthetic */ void lambda$sendUserPresentBroadcast$1$KeyguardViewMediator(UserManager um, UserHandle currentUser, int currentUserId) {
        int[] profileIdsWithDisabled;
        for (int profileId : um.getProfileIdsWithDisabled(currentUser.getIdentifier())) {
            this.mContext.sendBroadcastAsUser(USER_PRESENT_INTENT, UserHandle.of(profileId));
        }
        getLockPatternUtils().userPresent(currentUserId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleKeyguardDoneDrawing() {
        Trace.beginSection("KeyguardViewMediator#handleKeyguardDoneDrawing");
        synchronized (this) {
            if (this.mWaitingUntilKeyguardVisible) {
                this.mWaitingUntilKeyguardVisible = false;
                notifyAll();
                this.mHandler.removeMessages(8);
            }
        }
        Trace.endSection();
    }

    private void playSounds(boolean locked) {
        playSound(locked ? this.mLockSoundId : this.mUnlockSoundId);
    }

    private void playSound(final int soundId) {
        if (soundId == 0) {
            return;
        }
        ContentResolver cr = this.mContext.getContentResolver();
        if (Settings.System.getInt(cr, "lockscreen_sounds_enabled", 1) == 1) {
            this.mLockSounds.stop(this.mLockSoundStreamId);
            if (this.mAudioManager == null) {
                this.mAudioManager = (AudioManager) this.mContext.getSystemService(ListWidget.EXTRA_TYPE_AUDIO);
                AudioManager audioManager = this.mAudioManager;
                if (audioManager == null) {
                    return;
                }
                this.mUiSoundsStreamType = audioManager.getUiSoundsStreamType();
            }
            this.mUiOffloadThread.submit(new Runnable() { // from class: com.android.systemui.keyguard.-$$Lambda$KeyguardViewMediator$gkamSmMNqxOX1FiRuNJrEyzupq0
                @Override // java.lang.Runnable
                public final void run() {
                    KeyguardViewMediator.this.lambda$playSound$2$KeyguardViewMediator(soundId);
                }
            });
        }
    }

    public /* synthetic */ void lambda$playSound$2$KeyguardViewMediator(int soundId) {
        if (this.mAudioManager.isStreamMute(this.mUiSoundsStreamType)) {
            return;
        }
        SoundPool soundPool = this.mLockSounds;
        float f = this.mLockSoundVolume;
        int id = soundPool.play(soundId, f, f, 1, 0, 1.0f);
        synchronized (this) {
            this.mLockSoundStreamId = id;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playTrustedSound() {
        playSound(this.mTrustedSoundId);
    }

    private void updateActivityLockScreenState(final boolean showing, final boolean aodShowing) {
        this.mUiOffloadThread.submit(new Runnable() { // from class: com.android.systemui.keyguard.-$$Lambda$KeyguardViewMediator$5eut-TA6Yee7TYJOtzflkZDg_6Y
            @Override // java.lang.Runnable
            public final void run() {
                ActivityTaskManager.getService().setLockScreenShown(showing, aodShowing);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleShow(Bundle options) {
        Trace.beginSection("KeyguardViewMediator#handleShow");
        int currentUser = KeyguardUpdateMonitor.getCurrentUser();
        if (this.mLockPatternUtils.isSecure(currentUser)) {
            this.mLockPatternUtils.getDevicePolicyManager().reportKeyguardSecured(currentUser);
        }
        synchronized (this) {
            if (this.mSystemReady) {
                this.mHiding = false;
                this.mWakeAndUnlocking = false;
                setShowingLocked(true);
                this.mStatusBarKeyguardViewManager.show(options);
                resetKeyguardDonePendingLocked();
                this.mHideAnimationRun = false;
                adjustStatusBarLocked();
                userActivity();
                this.mUpdateMonitor.setKeyguardGoingAway(false);
                this.mStatusBarWindowController.setKeyguardGoingAway(false);
                this.mShowKeyguardWakeLock.release();
                this.mKeyguardDisplayManager.show();
                Trace.endSection();
            }
        }
    }

    /* renamed from: com.android.systemui.keyguard.KeyguardViewMediator$6  reason: invalid class name */
    /* loaded from: classes21.dex */
    class AnonymousClass6 implements Runnable {
        AnonymousClass6() {
        }

        @Override // java.lang.Runnable
        public void run() {
            Trace.beginSection("KeyguardViewMediator.mKeyGuardGoingAwayRunnable");
            KeyguardViewMediator.this.mStatusBarKeyguardViewManager.keyguardGoingAway();
            int flags = 0;
            if (KeyguardViewMediator.this.mStatusBarKeyguardViewManager.shouldDisableWindowAnimationsForUnlock() || (KeyguardViewMediator.this.mWakeAndUnlocking && !KeyguardViewMediator.this.mPulsing)) {
                flags = 0 | 2;
            }
            if (KeyguardViewMediator.this.mStatusBarKeyguardViewManager.isGoingToNotificationShade() || (KeyguardViewMediator.this.mWakeAndUnlocking && KeyguardViewMediator.this.mPulsing)) {
                flags |= 1;
            }
            if (KeyguardViewMediator.this.mStatusBarKeyguardViewManager.isUnlockWithWallpaper()) {
                flags |= 4;
            }
            if (KeyguardViewMediator.this.mStatusBarKeyguardViewManager.shouldSubtleWindowAnimationsForUnlock()) {
                flags |= 8;
            }
            KeyguardViewMediator.this.mUpdateMonitor.setKeyguardGoingAway(true);
            KeyguardViewMediator.this.mStatusBarWindowController.setKeyguardGoingAway(true);
            final int keyguardFlag = flags;
            KeyguardViewMediator.this.mUiOffloadThread.submit(new Runnable() { // from class: com.android.systemui.keyguard.-$$Lambda$KeyguardViewMediator$6$vq3ik26ABDUycidqAw1WbR1vD54
                @Override // java.lang.Runnable
                public final void run() {
                    KeyguardViewMediator.AnonymousClass6.lambda$run$0(keyguardFlag);
                }
            });
            Trace.endSection();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$run$0(int keyguardFlag) {
            try {
                ActivityTaskManager.getService().keyguardGoingAway(keyguardFlag);
            } catch (RemoteException e) {
                Log.e(KeyguardViewMediator.TAG, "Error while calling WindowManager", e);
            }
        }
    }

    public /* synthetic */ void lambda$new$4$KeyguardViewMediator() {
        this.mHideAnimationRunning = false;
        tryKeyguardDone();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleHide() {
        Trace.beginSection("KeyguardViewMediator#handleHide");
        if (this.mAodShowing) {
            PowerManager pm = (PowerManager) this.mContext.getSystemService(PowerManager.class);
            pm.wakeUp(SystemClock.uptimeMillis(), 4, "com.android.systemui:BOUNCER_DOZING");
        }
        synchronized (this) {
            if (mustNotUnlockCurrentUser()) {
                return;
            }
            this.mHiding = true;
            if (this.mShowing && !this.mOccluded) {
                this.mKeyguardGoingAwayRunnable.run();
            } else {
                handleStartKeyguardExitAnimation(SystemClock.uptimeMillis() + this.mHideAnimation.getStartOffset(), this.mHideAnimation.getDuration());
            }
            Trace.endSection();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleStartKeyguardExitAnimation(long startTime, long fadeoutDuration) {
        Trace.beginSection("KeyguardViewMediator#handleStartKeyguardExitAnimation");
        synchronized (this) {
            if (!this.mHiding) {
                setShowingLocked(this.mShowing, true);
                return;
            }
            this.mHiding = false;
            if (this.mWakeAndUnlocking && this.mDrawnCallback != null) {
                this.mStatusBarKeyguardViewManager.getViewRootImpl().setReportNextDraw();
                notifyDrawn(this.mDrawnCallback);
                this.mDrawnCallback = null;
            }
            if (TelephonyManager.EXTRA_STATE_IDLE.equals(this.mPhoneState)) {
                playSounds(false);
            }
            setShowingLocked(false);
            this.mWakeAndUnlocking = false;
            this.mDismissCallbackRegistry.notifyDismissSucceeded();
            this.mStatusBarKeyguardViewManager.hide(startTime, fadeoutDuration);
            resetKeyguardDonePendingLocked();
            this.mHideAnimationRun = false;
            adjustStatusBarLocked();
            sendUserPresentBroadcast();
            Trace.endSection();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void adjustStatusBarLocked() {
        adjustStatusBarLocked(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void adjustStatusBarLocked(boolean forceHideHomeRecentsButtons) {
        if (this.mStatusBarManager == null) {
            this.mStatusBarManager = (StatusBarManager) this.mContext.getSystemService("statusbar");
        }
        if (this.mStatusBarManager == null) {
            Log.w(TAG, "Could not get status bar manager");
            return;
        }
        int flags = 0;
        if (forceHideHomeRecentsButtons || isShowingAndNotOccluded()) {
            flags = 0 | 18874368;
        }
        this.mStatusBarManager.disable(flags);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleReset() {
        synchronized (this) {
            this.mStatusBarKeyguardViewManager.reset(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleVerifyUnlock() {
        Trace.beginSection("KeyguardViewMediator#handleVerifyUnlock");
        synchronized (this) {
            setShowingLocked(true);
            this.mStatusBarKeyguardViewManager.dismissAndCollapse();
        }
        Trace.endSection();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleNotifyStartedGoingToSleep() {
        synchronized (this) {
            this.mStatusBarKeyguardViewManager.onStartedGoingToSleep();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleNotifyFinishedGoingToSleep() {
        synchronized (this) {
            this.mStatusBarKeyguardViewManager.onFinishedGoingToSleep();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleNotifyStartedWakingUp() {
        Trace.beginSection("KeyguardViewMediator#handleMotifyStartedWakingUp");
        synchronized (this) {
            this.mStatusBarKeyguardViewManager.onStartedWakingUp();
        }
        Trace.endSection();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleNotifyScreenTurningOn(IKeyguardDrawnCallback callback) {
        Trace.beginSection("KeyguardViewMediator#handleNotifyScreenTurningOn");
        synchronized (this) {
            this.mStatusBarKeyguardViewManager.onScreenTurningOn();
            if (callback != null) {
                if (this.mWakeAndUnlocking) {
                    this.mDrawnCallback = callback;
                } else {
                    notifyDrawn(callback);
                }
            }
        }
        Trace.endSection();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleNotifyScreenTurnedOn() {
        Trace.beginSection("KeyguardViewMediator#handleNotifyScreenTurnedOn");
        if (LatencyTracker.isEnabled(this.mContext)) {
            LatencyTracker.getInstance(this.mContext).onActionEnd(5);
        }
        synchronized (this) {
            this.mStatusBarKeyguardViewManager.onScreenTurnedOn();
        }
        Trace.endSection();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleNotifyScreenTurnedOff() {
        synchronized (this) {
            this.mDrawnCallback = null;
        }
    }

    private void notifyDrawn(IKeyguardDrawnCallback callback) {
        Trace.beginSection("KeyguardViewMediator#notifyDrawn");
        try {
            callback.onDrawn();
        } catch (RemoteException e) {
            Slog.w(TAG, "Exception calling onDrawn():", e);
        }
        Trace.endSection();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetKeyguardDonePendingLocked() {
        this.mKeyguardDonePending = false;
        this.mHandler.removeMessages(13);
    }

    @Override // com.android.systemui.SystemUI
    public void onBootCompleted() {
        this.mUpdateMonitor.dispatchBootCompleted();
        synchronized (this) {
            this.mBootCompleted = true;
            if (this.mBootSendUserPresent) {
                sendUserPresentBroadcast();
            }
        }
    }

    public void onWakeAndUnlocking() {
        Trace.beginSection("KeyguardViewMediator#onWakeAndUnlocking");
        this.mWakeAndUnlocking = true;
        keyguardDone();
        Trace.endSection();
    }

    public StatusBarKeyguardViewManager registerStatusBar(StatusBar statusBar, ViewGroup container, NotificationPanelView panelView, BiometricUnlockController biometricUnlockController, ViewGroup lockIconContainer, View notificationContainer, KeyguardBypassController bypassController, FalsingManager falsingManager) {
        this.mStatusBarKeyguardViewManager.registerStatusBar(statusBar, container, panelView, biometricUnlockController, this.mDismissCallbackRegistry, lockIconContainer, notificationContainer, bypassController, falsingManager);
        return this.mStatusBarKeyguardViewManager;
    }

    public void startKeyguardExitAnimation(long startTime, long fadeoutDuration) {
        Trace.beginSection("KeyguardViewMediator#startKeyguardExitAnimation");
        Message msg = this.mHandler.obtainMessage(12, new StartKeyguardExitAnimParams(startTime, fadeoutDuration));
        this.mHandler.sendMessage(msg);
        Trace.endSection();
    }

    public void onShortPowerPressedGoHome() {
    }

    public ViewMediatorCallback getViewMediatorCallback() {
        return this.mViewMediatorCallback;
    }

    public LockPatternUtils getLockPatternUtils() {
        return this.mLockPatternUtils;
    }

    @Override // com.android.systemui.SystemUI
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.print("  mSystemReady: ");
        pw.println(this.mSystemReady);
        pw.print("  mBootCompleted: ");
        pw.println(this.mBootCompleted);
        pw.print("  mBootSendUserPresent: ");
        pw.println(this.mBootSendUserPresent);
        pw.print("  mExternallyEnabled: ");
        pw.println(this.mExternallyEnabled);
        pw.print("  mShuttingDown: ");
        pw.println(this.mShuttingDown);
        pw.print("  mNeedToReshowWhenReenabled: ");
        pw.println(this.mNeedToReshowWhenReenabled);
        pw.print("  mShowing: ");
        pw.println(this.mShowing);
        pw.print("  mInputRestricted: ");
        pw.println(this.mInputRestricted);
        pw.print("  mOccluded: ");
        pw.println(this.mOccluded);
        pw.print("  mDelayedShowingSequence: ");
        pw.println(this.mDelayedShowingSequence);
        pw.print("  mExitSecureCallback: ");
        pw.println(this.mExitSecureCallback);
        pw.print("  mDeviceInteractive: ");
        pw.println(this.mDeviceInteractive);
        pw.print("  mGoingToSleep: ");
        pw.println(this.mGoingToSleep);
        pw.print("  mHiding: ");
        pw.println(this.mHiding);
        pw.print("  mDozing: ");
        pw.println(this.mDozing);
        pw.print("  mAodShowing: ");
        pw.println(this.mAodShowing);
        pw.print("  mWaitingUntilKeyguardVisible: ");
        pw.println(this.mWaitingUntilKeyguardVisible);
        pw.print("  mKeyguardDonePending: ");
        pw.println(this.mKeyguardDonePending);
        pw.print("  mHideAnimationRun: ");
        pw.println(this.mHideAnimationRun);
        pw.print("  mPendingReset: ");
        pw.println(this.mPendingReset);
        pw.print("  mPendingLock: ");
        pw.println(this.mPendingLock);
        pw.print("  mWakeAndUnlocking: ");
        pw.println(this.mWakeAndUnlocking);
        pw.print("  mDrawnCallback: ");
        pw.println(this.mDrawnCallback);
    }

    public void setDozing(boolean dozing) {
        if (dozing == this.mDozing) {
            return;
        }
        this.mDozing = dozing;
        setShowingLocked(this.mShowing);
    }

    public void setPulsing(boolean pulsing) {
        this.mPulsing = pulsing;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class StartKeyguardExitAnimParams {
        long fadeoutDuration;
        long startTime;

        private StartKeyguardExitAnimParams(long startTime, long fadeoutDuration) {
            this.startTime = startTime;
            this.fadeoutDuration = fadeoutDuration;
        }
    }

    private void setShowingLocked(boolean showing) {
        setShowingLocked(showing, false);
    }

    private void setShowingLocked(boolean showing, boolean forceCallbacks) {
        boolean notifyDefaultDisplayCallbacks = true;
        boolean aodShowing = this.mDozing && !this.mWakeAndUnlocking;
        if (showing == this.mShowing && aodShowing == this.mAodShowing && !forceCallbacks) {
            notifyDefaultDisplayCallbacks = false;
        }
        this.mShowing = showing;
        this.mAodShowing = aodShowing;
        if (notifyDefaultDisplayCallbacks) {
            notifyDefaultDisplayCallbacks(showing);
            updateActivityLockScreenState(showing, aodShowing);
        }
    }

    private void notifyDefaultDisplayCallbacks(boolean showing) {
        int size = this.mKeyguardStateCallbacks.size();
        for (int i = size - 1; i >= 0; i--) {
            IKeyguardStateCallback callback = this.mKeyguardStateCallbacks.get(i);
            try {
                callback.onShowingStateChanged(showing);
            } catch (RemoteException e) {
                Slog.w(TAG, "Failed to call onShowingStateChanged", e);
                if (e instanceof DeadObjectException) {
                    this.mKeyguardStateCallbacks.remove(callback);
                }
            }
        }
        updateInputRestrictedLocked();
        this.mUiOffloadThread.submit(new Runnable() { // from class: com.android.systemui.keyguard.-$$Lambda$KeyguardViewMediator$tj2ooDljMkGvvuUoztwrUid_YnI
            @Override // java.lang.Runnable
            public final void run() {
                KeyguardViewMediator.this.lambda$notifyDefaultDisplayCallbacks$5$KeyguardViewMediator();
            }
        });
    }

    public /* synthetic */ void lambda$notifyDefaultDisplayCallbacks$5$KeyguardViewMediator() {
        this.mTrustManager.reportKeyguardShowingChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyTrustedChangedLocked(boolean trusted) {
        int size = this.mKeyguardStateCallbacks.size();
        for (int i = size - 1; i >= 0; i--) {
            try {
                this.mKeyguardStateCallbacks.get(i).onTrustedChanged(trusted);
            } catch (RemoteException e) {
                Slog.w(TAG, "Failed to call notifyTrustedChangedLocked", e);
                if (e instanceof DeadObjectException) {
                    this.mKeyguardStateCallbacks.remove(i);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyHasLockscreenWallpaperChanged(boolean hasLockscreenWallpaper) {
        int size = this.mKeyguardStateCallbacks.size();
        for (int i = size - 1; i >= 0; i--) {
            try {
                this.mKeyguardStateCallbacks.get(i).onHasLockscreenWallpaperChanged(hasLockscreenWallpaper);
            } catch (RemoteException e) {
                Slog.w(TAG, "Failed to call onHasLockscreenWallpaperChanged", e);
                if (e instanceof DeadObjectException) {
                    this.mKeyguardStateCallbacks.remove(i);
                }
            }
        }
    }

    public void addStateMonitorCallback(IKeyguardStateCallback callback) {
        synchronized (this) {
            this.mKeyguardStateCallbacks.add(callback);
            try {
                callback.onSimSecureStateChanged(this.mUpdateMonitor.isSimPinSecure());
                callback.onShowingStateChanged(this.mShowing);
                callback.onInputRestrictedStateChanged(this.mInputRestricted);
                callback.onTrustedChanged(this.mUpdateMonitor.getUserHasTrust(KeyguardUpdateMonitor.getCurrentUser()));
                callback.onHasLockscreenWallpaperChanged(this.mUpdateMonitor.hasLockscreenWallpaper());
            } catch (RemoteException e) {
                Slog.w(TAG, "Failed to call to IKeyguardStateCallback", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class DismissMessage {
        private final IKeyguardDismissCallback mCallback;
        private final CharSequence mMessage;

        DismissMessage(IKeyguardDismissCallback callback, CharSequence message) {
            this.mCallback = callback;
            this.mMessage = message;
        }

        public IKeyguardDismissCallback getCallback() {
            return this.mCallback;
        }

        public CharSequence getMessage() {
            return this.mMessage;
        }
    }
}
