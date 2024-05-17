package com.android.keyguard;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.Instrumentation;
import android.app.PendingIntent;
import android.app.UserSwitchObserver;
import android.app.admin.DevicePolicyManager;
import android.app.trust.TrustManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.IPackageManager;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.BiometricSourceType;
import android.hardware.biometrics.CryptoObject;
import android.hardware.biometrics.IBiometricEnabledOnKeyguardCallback;
import android.hardware.face.FaceManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.IRemoteCallback;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.service.dreams.IDreamManager;
import android.telephony.CarrierConfigManager;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.IccCardConstants;
import com.android.internal.util.Preconditions;
import com.android.internal.widget.LockPatternUtils;
import com.android.settingslib.WirelessUtils;
import com.android.systemui.R;
import com.android.systemui.globalactions.GlobalActionsDialog;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.google.android.collect.Lists;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Consumer;
/* loaded from: classes19.dex */
public class KeyguardUpdateMonitor implements TrustManager.TrustListener {
    private static final String ACTION_FACE_UNLOCK_STARTED = "com.android.facelock.FACE_UNLOCK_STARTED";
    private static final String ACTION_FACE_UNLOCK_STOPPED = "com.android.facelock.FACE_UNLOCK_STOPPED";
    private static final int BIOMETRIC_CONTINUE_DELAY_MS = 500;
    public static final int BIOMETRIC_HELP_FACE_NOT_RECOGNIZED = -2;
    private static final int BIOMETRIC_HELP_FINGERPRINT_NOT_RECOGNIZED = -1;
    private static final int BIOMETRIC_STATE_CANCELLING = 2;
    private static final int BIOMETRIC_STATE_CANCELLING_RESTARTING = 3;
    private static final int BIOMETRIC_STATE_RUNNING = 1;
    private static final int BIOMETRIC_STATE_STOPPED = 0;
    public static final boolean CORE_APPS_ONLY;
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_FACE = true;
    private static final boolean DEBUG_SIM_STATES = true;
    private static final int DEFAULT_CHARGING_VOLTAGE_MICRO_VOLT = 5000000;
    private static final ComponentName FALLBACK_HOME_COMPONENT = new ComponentName("com.android.settings", "com.android.settings.FallbackHome");
    private static final int HAL_ERROR_RETRY_MAX = 10;
    private static final int HAL_ERROR_RETRY_TIMEOUT = 500;
    private static final int LOW_BATTERY_THRESHOLD = 20;
    private static final int MSG_AIRPLANE_MODE_CHANGED = 329;
    private static final int MSG_ASSISTANT_STACK_CHANGED = 335;
    private static final int MSG_BATTERY_UPDATE = 302;
    private static final int MSG_BIOMETRIC_AUTHENTICATION_CONTINUE = 336;
    private static final int MSG_BOOT_COMPLETED = 313;
    private static final int MSG_DEVICE_POLICY_MANAGER_STATE_CHANGED = 337;
    private static final int MSG_DEVICE_PROVISIONED = 308;
    private static final int MSG_DPM_STATE_CHANGED = 309;
    private static final int MSG_DREAMING_STATE_CHANGED = 333;
    private static final int MSG_FACE_UNLOCK_STATE_CHANGED = 327;
    private static final int MSG_FINISHED_GOING_TO_SLEEP = 320;
    private static final int MSG_KEYGUARD_BOUNCER_CHANGED = 322;
    private static final int MSG_KEYGUARD_RESET = 312;
    private static final int MSG_PHONE_STATE_CHANGED = 306;
    private static final int MSG_REPORT_EMERGENCY_CALL_ACTION = 318;
    private static final int MSG_RINGER_MODE_CHANGED = 305;
    private static final int MSG_SCREEN_TURNED_OFF = 332;
    private static final int MSG_SCREEN_TURNED_ON = 331;
    private static final int MSG_SERVICE_STATE_CHANGE = 330;
    private static final int MSG_SIM_STATE_CHANGE = 304;
    private static final int MSG_SIM_SUBSCRIPTION_INFO_CHANGED = 328;
    private static final int MSG_STARTED_GOING_TO_SLEEP = 321;
    private static final int MSG_STARTED_WAKING_UP = 319;
    private static final int MSG_TELEPHONY_CAPABLE = 338;
    private static final int MSG_TIMEZONE_UPDATE = 339;
    private static final int MSG_TIME_UPDATE = 301;
    private static final int MSG_USER_INFO_CHANGED = 317;
    private static final int MSG_USER_SWITCHING = 310;
    private static final int MSG_USER_SWITCH_COMPLETE = 314;
    private static final int MSG_USER_UNLOCKED = 334;
    private static final String TAG = "KeyguardUpdateMonitor";
    private static int sCurrentUser;
    private static boolean sDisableHandlerCheckForTesting;
    private static KeyguardUpdateMonitor sInstance;
    private boolean mAssistantVisible;
    private boolean mAuthInterruptActive;
    private BatteryStatus mBatteryStatus;
    private BiometricManager mBiometricManager;
    private boolean mBootCompleted;
    private boolean mBouncer;
    private final Context mContext;
    private boolean mDeviceInteractive;
    private final DevicePolicyManager mDevicePolicyManager;
    private ContentObserver mDeviceProvisionedObserver;
    private final IDreamManager mDreamManager;
    private CancellationSignal mFaceCancelSignal;
    private FaceManager mFaceManager;
    private CancellationSignal mFingerprintCancelSignal;
    private FingerprintManager mFpm;
    private boolean mGoingToSleep;
    private boolean mHasLockscreenWallpaper;
    private boolean mIsDreaming;
    private final boolean mIsPrimaryUser;
    private KeyguardBypassController mKeyguardBypassController;
    private boolean mKeyguardGoingAway;
    private boolean mKeyguardIsVisible;
    private boolean mKeyguardOccluded;
    private boolean mLockIconPressed;
    private LockPatternUtils mLockPatternUtils;
    private boolean mLogoutEnabled;
    private boolean mNeedsSlowUnlockTransition;
    private int mPhoneState;
    private int mRingMode;
    private boolean mScreenOn;
    private boolean mSecureCameraLaunched;
    @VisibleForTesting
    protected StrongAuthTracker mStrongAuthTracker;
    private List<SubscriptionInfo> mSubscriptionInfo;
    private SubscriptionManager mSubscriptionManager;
    private boolean mSwitchingUser;
    @VisibleForTesting
    protected boolean mTelephonyCapable;
    private TrustManager mTrustManager;
    private UserManager mUserManager;
    HashMap<Integer, SimData> mSimDatas = new HashMap<>();
    HashMap<Integer, ServiceState> mServiceStates = new HashMap<>();
    private final ArrayList<WeakReference<KeyguardUpdateMonitorCallback>> mCallbacks = Lists.newArrayList();
    private int mFingerprintRunningState = 0;
    private int mFaceRunningState = 0;
    private int mActiveMobileDataSubscription = -1;
    private int mHardwareFingerprintUnavailableRetryCount = 0;
    private int mHardwareFaceUnavailableRetryCount = 0;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: com.android.keyguard.KeyguardUpdateMonitor.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 301:
                    KeyguardUpdateMonitor.this.handleTimeUpdate();
                    return;
                case 302:
                    KeyguardUpdateMonitor.this.handleBatteryUpdate((BatteryStatus) msg.obj);
                    return;
                case 303:
                case 307:
                case 311:
                case 315:
                case 316:
                case 323:
                case 324:
                case 325:
                case 326:
                default:
                    super.handleMessage(msg);
                    return;
                case 304:
                    KeyguardUpdateMonitor.this.handleSimStateChange(msg.arg1, msg.arg2, (IccCardConstants.State) msg.obj);
                    return;
                case 305:
                    KeyguardUpdateMonitor.this.handleRingerModeChange(msg.arg1);
                    return;
                case 306:
                    KeyguardUpdateMonitor.this.handlePhoneStateChanged((String) msg.obj);
                    return;
                case KeyguardUpdateMonitor.MSG_DEVICE_PROVISIONED /* 308 */:
                    KeyguardUpdateMonitor.this.handleDeviceProvisioned();
                    return;
                case KeyguardUpdateMonitor.MSG_DPM_STATE_CHANGED /* 309 */:
                    KeyguardUpdateMonitor.this.handleDevicePolicyManagerStateChanged();
                    return;
                case KeyguardUpdateMonitor.MSG_USER_SWITCHING /* 310 */:
                    KeyguardUpdateMonitor.this.handleUserSwitching(msg.arg1, (IRemoteCallback) msg.obj);
                    return;
                case KeyguardUpdateMonitor.MSG_KEYGUARD_RESET /* 312 */:
                    KeyguardUpdateMonitor.this.handleKeyguardReset();
                    return;
                case KeyguardUpdateMonitor.MSG_BOOT_COMPLETED /* 313 */:
                    KeyguardUpdateMonitor.this.handleBootCompleted();
                    return;
                case KeyguardUpdateMonitor.MSG_USER_SWITCH_COMPLETE /* 314 */:
                    KeyguardUpdateMonitor.this.handleUserSwitchComplete(msg.arg1);
                    return;
                case KeyguardUpdateMonitor.MSG_USER_INFO_CHANGED /* 317 */:
                    KeyguardUpdateMonitor.this.handleUserInfoChanged(msg.arg1);
                    return;
                case KeyguardUpdateMonitor.MSG_REPORT_EMERGENCY_CALL_ACTION /* 318 */:
                    KeyguardUpdateMonitor.this.handleReportEmergencyCallAction();
                    return;
                case KeyguardUpdateMonitor.MSG_STARTED_WAKING_UP /* 319 */:
                    Trace.beginSection("KeyguardUpdateMonitor#handler MSG_STARTED_WAKING_UP");
                    KeyguardUpdateMonitor.this.handleStartedWakingUp();
                    Trace.endSection();
                    return;
                case 320:
                    KeyguardUpdateMonitor.this.handleFinishedGoingToSleep(msg.arg1);
                    return;
                case KeyguardUpdateMonitor.MSG_STARTED_GOING_TO_SLEEP /* 321 */:
                    KeyguardUpdateMonitor.this.handleStartedGoingToSleep(msg.arg1);
                    return;
                case KeyguardUpdateMonitor.MSG_KEYGUARD_BOUNCER_CHANGED /* 322 */:
                    KeyguardUpdateMonitor.this.handleKeyguardBouncerChanged(msg.arg1);
                    return;
                case KeyguardUpdateMonitor.MSG_FACE_UNLOCK_STATE_CHANGED /* 327 */:
                    Trace.beginSection("KeyguardUpdateMonitor#handler MSG_FACE_UNLOCK_STATE_CHANGED");
                    KeyguardUpdateMonitor.this.handleFaceUnlockStateChanged(msg.arg1 != 0, msg.arg2);
                    Trace.endSection();
                    return;
                case KeyguardUpdateMonitor.MSG_SIM_SUBSCRIPTION_INFO_CHANGED /* 328 */:
                    KeyguardUpdateMonitor.this.handleSimSubscriptionInfoChanged();
                    return;
                case KeyguardUpdateMonitor.MSG_AIRPLANE_MODE_CHANGED /* 329 */:
                    KeyguardUpdateMonitor.this.handleAirplaneModeChanged();
                    return;
                case KeyguardUpdateMonitor.MSG_SERVICE_STATE_CHANGE /* 330 */:
                    KeyguardUpdateMonitor.this.handleServiceStateChange(msg.arg1, (ServiceState) msg.obj);
                    return;
                case KeyguardUpdateMonitor.MSG_SCREEN_TURNED_ON /* 331 */:
                    KeyguardUpdateMonitor.this.handleScreenTurnedOn();
                    return;
                case KeyguardUpdateMonitor.MSG_SCREEN_TURNED_OFF /* 332 */:
                    Trace.beginSection("KeyguardUpdateMonitor#handler MSG_SCREEN_TURNED_ON");
                    KeyguardUpdateMonitor.this.handleScreenTurnedOff();
                    Trace.endSection();
                    return;
                case KeyguardUpdateMonitor.MSG_DREAMING_STATE_CHANGED /* 333 */:
                    KeyguardUpdateMonitor.this.handleDreamingStateChanged(msg.arg1);
                    return;
                case KeyguardUpdateMonitor.MSG_USER_UNLOCKED /* 334 */:
                    KeyguardUpdateMonitor.this.handleUserUnlocked();
                    return;
                case KeyguardUpdateMonitor.MSG_ASSISTANT_STACK_CHANGED /* 335 */:
                    KeyguardUpdateMonitor.this.setAssistantVisible(((Boolean) msg.obj).booleanValue());
                    return;
                case KeyguardUpdateMonitor.MSG_BIOMETRIC_AUTHENTICATION_CONTINUE /* 336 */:
                    KeyguardUpdateMonitor.this.updateBiometricListeningState();
                    return;
                case KeyguardUpdateMonitor.MSG_DEVICE_POLICY_MANAGER_STATE_CHANGED /* 337 */:
                    KeyguardUpdateMonitor.this.updateLogoutEnabled();
                    return;
                case KeyguardUpdateMonitor.MSG_TELEPHONY_CAPABLE /* 338 */:
                    KeyguardUpdateMonitor.this.updateTelephonyCapable(((Boolean) msg.obj).booleanValue());
                    return;
                case KeyguardUpdateMonitor.MSG_TIMEZONE_UPDATE /* 339 */:
                    KeyguardUpdateMonitor.this.handleTimeZoneUpdate((String) msg.obj);
                    return;
            }
        }
    };
    private SparseBooleanArray mFaceSettingEnabledForUser = new SparseBooleanArray();
    private IBiometricEnabledOnKeyguardCallback mBiometricEnabledCallback = new IBiometricEnabledOnKeyguardCallback.Stub() { // from class: com.android.keyguard.KeyguardUpdateMonitor.2
        public void onChanged(BiometricSourceType type, boolean enabled, int userId) throws RemoteException {
            if (type == BiometricSourceType.FACE) {
                KeyguardUpdateMonitor.this.mFaceSettingEnabledForUser.put(userId, enabled);
                KeyguardUpdateMonitor.this.updateFaceListeningState();
            }
        }
    };
    @VisibleForTesting
    public PhoneStateListener mPhoneStateListener = new PhoneStateListener() { // from class: com.android.keyguard.KeyguardUpdateMonitor.3
        @Override // android.telephony.PhoneStateListener
        public void onActiveDataSubscriptionIdChanged(int subId) {
            KeyguardUpdateMonitor.this.mActiveMobileDataSubscription = subId;
            KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(KeyguardUpdateMonitor.MSG_SIM_SUBSCRIPTION_INFO_CHANGED);
        }
    };
    private SubscriptionManager.OnSubscriptionsChangedListener mSubscriptionListener = new SubscriptionManager.OnSubscriptionsChangedListener() { // from class: com.android.keyguard.KeyguardUpdateMonitor.4
        @Override // android.telephony.SubscriptionManager.OnSubscriptionsChangedListener
        public void onSubscriptionsChanged() {
            KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(KeyguardUpdateMonitor.MSG_SIM_SUBSCRIPTION_INFO_CHANGED);
        }
    };
    private SparseBooleanArray mUserHasTrust = new SparseBooleanArray();
    private SparseBooleanArray mUserTrustIsManaged = new SparseBooleanArray();
    private SparseBooleanArray mUserFingerprintAuthenticated = new SparseBooleanArray();
    private SparseBooleanArray mUserFaceAuthenticated = new SparseBooleanArray();
    private SparseBooleanArray mUserFaceUnlockRunning = new SparseBooleanArray();
    private Runnable mUpdateBiometricListeningState = new Runnable() { // from class: com.android.keyguard.-$$Lambda$KeyguardUpdateMonitor$w3Onnt26KGuFqBxQaSJgQd6Y_G4
        @Override // java.lang.Runnable
        public final void run() {
            KeyguardUpdateMonitor.this.updateBiometricListeningState();
        }
    };
    private Runnable mRetryFingerprintAuthentication = new Runnable() { // from class: com.android.keyguard.KeyguardUpdateMonitor.5
        @Override // java.lang.Runnable
        public void run() {
            Log.w(KeyguardUpdateMonitor.TAG, "Retrying fingerprint after HW unavailable, attempt " + KeyguardUpdateMonitor.this.mHardwareFingerprintUnavailableRetryCount);
            KeyguardUpdateMonitor.this.updateFingerprintListeningState();
        }
    };
    private Runnable mRetryFaceAuthentication = new Runnable() { // from class: com.android.keyguard.KeyguardUpdateMonitor.6
        @Override // java.lang.Runnable
        public void run() {
            Log.w(KeyguardUpdateMonitor.TAG, "Retrying face after HW unavailable, attempt " + KeyguardUpdateMonitor.this.mHardwareFaceUnavailableRetryCount);
            KeyguardUpdateMonitor.this.updateFaceListeningState();
        }
    };
    private DisplayClientState mDisplayClientState = new DisplayClientState();
    @VisibleForTesting
    protected final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.keyguard.KeyguardUpdateMonitor.7
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int maxChargingMicroWatt;
            String action = intent.getAction();
            if ("android.intent.action.TIME_TICK".equals(action) || "android.intent.action.TIME_SET".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(301);
            } else if ("android.intent.action.TIMEZONE_CHANGED".equals(action)) {
                Message msg = KeyguardUpdateMonitor.this.mHandler.obtainMessage(KeyguardUpdateMonitor.MSG_TIMEZONE_UPDATE, intent.getStringExtra("time-zone"));
                KeyguardUpdateMonitor.this.mHandler.sendMessage(msg);
            } else if ("android.intent.action.BATTERY_CHANGED".equals(action)) {
                int status = intent.getIntExtra("status", 1);
                int plugged = intent.getIntExtra("plugged", 0);
                int level = intent.getIntExtra("level", 0);
                int health = intent.getIntExtra("health", 1);
                int maxChargingMicroAmp = intent.getIntExtra("max_charging_current", -1);
                int maxChargingMicroVolt = intent.getIntExtra("max_charging_voltage", -1);
                if (maxChargingMicroVolt <= 0) {
                    maxChargingMicroVolt = KeyguardUpdateMonitor.DEFAULT_CHARGING_VOLTAGE_MICRO_VOLT;
                }
                if (maxChargingMicroAmp > 0) {
                    maxChargingMicroWatt = (maxChargingMicroAmp / 1000) * (maxChargingMicroVolt / 1000);
                } else {
                    maxChargingMicroWatt = -1;
                }
                Message msg2 = KeyguardUpdateMonitor.this.mHandler.obtainMessage(302, new BatteryStatus(status, level, plugged, health, maxChargingMicroWatt));
                KeyguardUpdateMonitor.this.mHandler.sendMessage(msg2);
            } else if ("android.intent.action.SIM_STATE_CHANGED".equals(action)) {
                SimData args = SimData.fromIntent(intent);
                if (intent.getBooleanExtra("rebroadcastOnUnlock", false)) {
                    if (args.simState == IccCardConstants.State.ABSENT) {
                        KeyguardUpdateMonitor.this.mHandler.obtainMessage(KeyguardUpdateMonitor.MSG_TELEPHONY_CAPABLE, true).sendToTarget();
                        return;
                    }
                    return;
                }
                Log.v(KeyguardUpdateMonitor.TAG, "action " + action + " state: " + intent.getStringExtra("ss") + " slotId: " + args.slotId + " subid: " + args.subId);
                KeyguardUpdateMonitor.this.mHandler.obtainMessage(304, args.subId, args.slotId, args.simState).sendToTarget();
            } else if ("android.media.RINGER_MODE_CHANGED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(305, intent.getIntExtra("android.media.EXTRA_RINGER_MODE", -1), 0));
            } else if ("android.intent.action.PHONE_STATE".equals(action)) {
                String state = intent.getStringExtra("state");
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(306, state));
            } else if ("android.intent.action.AIRPLANE_MODE".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(KeyguardUpdateMonitor.MSG_AIRPLANE_MODE_CHANGED);
            } else if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
                KeyguardUpdateMonitor.this.dispatchBootCompleted();
            } else if ("android.intent.action.SERVICE_STATE".equals(action)) {
                ServiceState serviceState = ServiceState.newFromBundle(intent.getExtras());
                int subId = intent.getIntExtra("subscription", -1);
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(KeyguardUpdateMonitor.MSG_SERVICE_STATE_CHANGE, subId, 0, serviceState));
            } else if ("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(KeyguardUpdateMonitor.MSG_SIM_SUBSCRIPTION_INFO_CHANGED);
            } else if ("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(KeyguardUpdateMonitor.MSG_DEVICE_POLICY_MANAGER_STATE_CHANGED);
            }
        }
    };
    @VisibleForTesting
    protected final BroadcastReceiver mBroadcastAllReceiver = new BroadcastReceiver() { // from class: com.android.keyguard.KeyguardUpdateMonitor.8
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.app.action.NEXT_ALARM_CLOCK_CHANGED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(301);
            } else if ("android.intent.action.USER_INFO_CHANGED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(KeyguardUpdateMonitor.MSG_USER_INFO_CHANGED, intent.getIntExtra("android.intent.extra.user_handle", getSendingUserId()), 0));
            } else if (KeyguardUpdateMonitor.ACTION_FACE_UNLOCK_STARTED.equals(action)) {
                Trace.beginSection("KeyguardUpdateMonitor.mBroadcastAllReceiver#onReceive ACTION_FACE_UNLOCK_STARTED");
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(KeyguardUpdateMonitor.MSG_FACE_UNLOCK_STATE_CHANGED, 1, getSendingUserId()));
                Trace.endSection();
            } else if (KeyguardUpdateMonitor.ACTION_FACE_UNLOCK_STOPPED.equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(KeyguardUpdateMonitor.MSG_FACE_UNLOCK_STATE_CHANGED, 0, getSendingUserId()));
            } else if ("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(KeyguardUpdateMonitor.MSG_DPM_STATE_CHANGED);
            } else if ("android.intent.action.USER_UNLOCKED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(KeyguardUpdateMonitor.MSG_USER_UNLOCKED);
            }
        }
    };
    private final FingerprintManager.LockoutResetCallback mFingerprintLockoutResetCallback = new FingerprintManager.LockoutResetCallback() { // from class: com.android.keyguard.KeyguardUpdateMonitor.9
        public void onLockoutReset() {
            KeyguardUpdateMonitor.this.handleFingerprintLockoutReset();
        }
    };
    private final FaceManager.LockoutResetCallback mFaceLockoutResetCallback = new FaceManager.LockoutResetCallback() { // from class: com.android.keyguard.KeyguardUpdateMonitor.10
        public void onLockoutReset() {
            KeyguardUpdateMonitor.this.handleFaceLockoutReset();
        }
    };
    private FingerprintManager.AuthenticationCallback mFingerprintAuthenticationCallback = new FingerprintManager.AuthenticationCallback() { // from class: com.android.keyguard.KeyguardUpdateMonitor.11
        @Override // android.hardware.fingerprint.FingerprintManager.AuthenticationCallback
        public void onAuthenticationFailed() {
            KeyguardUpdateMonitor.this.handleFingerprintAuthFailed();
        }

        @Override // android.hardware.fingerprint.FingerprintManager.AuthenticationCallback
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            Trace.beginSection("KeyguardUpdateMonitor#onAuthenticationSucceeded");
            KeyguardUpdateMonitor.this.handleFingerprintAuthenticated(result.getUserId());
            Trace.endSection();
        }

        @Override // android.hardware.fingerprint.FingerprintManager.AuthenticationCallback
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            KeyguardUpdateMonitor.this.handleFingerprintHelp(helpMsgId, helpString.toString());
        }

        @Override // android.hardware.fingerprint.FingerprintManager.AuthenticationCallback
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            KeyguardUpdateMonitor.this.handleFingerprintError(errMsgId, errString.toString());
        }

        public void onAuthenticationAcquired(int acquireInfo) {
            KeyguardUpdateMonitor.this.handleFingerprintAcquired(acquireInfo);
        }
    };
    @VisibleForTesting
    FaceManager.AuthenticationCallback mFaceAuthenticationCallback = new FaceManager.AuthenticationCallback() { // from class: com.android.keyguard.KeyguardUpdateMonitor.12
        public void onAuthenticationFailed() {
            KeyguardUpdateMonitor.this.handleFaceAuthFailed();
        }

        public void onAuthenticationSucceeded(FaceManager.AuthenticationResult result) {
            Trace.beginSection("KeyguardUpdateMonitor#onAuthenticationSucceeded");
            KeyguardUpdateMonitor.this.handleFaceAuthenticated(result.getUserId());
            Trace.endSection();
        }

        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            KeyguardUpdateMonitor.this.handleFaceHelp(helpMsgId, helpString.toString());
        }

        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            KeyguardUpdateMonitor.this.handleFaceError(errMsgId, errString.toString());
        }

        public void onAuthenticationAcquired(int acquireInfo) {
            KeyguardUpdateMonitor.this.handleFaceAcquired(acquireInfo);
        }
    };
    private final TaskStackChangeListener mTaskStackListener = new TaskStackChangeListener() { // from class: com.android.keyguard.KeyguardUpdateMonitor.15
        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onTaskStackChangedBackground() {
            try {
                ActivityManager.StackInfo info = ActivityTaskManager.getService().getStackInfo(0, 4);
                if (info == null) {
                    return;
                }
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(KeyguardUpdateMonitor.MSG_ASSISTANT_STACK_CHANGED, Boolean.valueOf(info.visible)));
            } catch (RemoteException e) {
                Log.e(KeyguardUpdateMonitor.TAG, "unable to check task stack", e);
            }
        }
    };
    private boolean mDeviceProvisioned = isDeviceProvisionedInSettingsDb();

    static {
        try {
            CORE_APPS_ONLY = IPackageManager.Stub.asInterface(ServiceManager.getService("package")).isOnlyCoreApps();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static synchronized void setCurrentUser(int currentUser) {
        synchronized (KeyguardUpdateMonitor.class) {
            sCurrentUser = currentUser;
        }
    }

    public static synchronized int getCurrentUser() {
        int i;
        synchronized (KeyguardUpdateMonitor.class) {
            i = sCurrentUser;
        }
        return i;
    }

    public void onTrustChanged(boolean enabled, int userId, int flags) {
        checkIsHandlerThread();
        this.mUserHasTrust.put(userId, enabled);
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onTrustChanged(userId);
                if (enabled && flags != 0) {
                    cb.onTrustGrantedWithFlags(flags, userId);
                }
            }
        }
    }

    public void onTrustError(CharSequence message) {
        dispatchErrorMessage(message);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSimSubscriptionInfoChanged() {
        Log.v(TAG, "onSubscriptionInfoChanged()");
        List<SubscriptionInfo> sil = this.mSubscriptionManager.getActiveSubscriptionInfoList(false);
        if (sil == null) {
            Log.v(TAG, "onSubscriptionInfoChanged: list is null");
        } else {
            for (SubscriptionInfo subInfo : sil) {
                Log.v(TAG, "SubInfo:" + subInfo);
            }
        }
        List<SubscriptionInfo> subscriptionInfos = getSubscriptionInfo(true);
        ArrayList<SubscriptionInfo> changedSubscriptions = new ArrayList<>();
        for (int i = 0; i < subscriptionInfos.size(); i++) {
            SubscriptionInfo info = subscriptionInfos.get(i);
            boolean changed = refreshSimState(info.getSubscriptionId(), info.getSimSlotIndex());
            if (changed) {
                changedSubscriptions.add(info);
            }
        }
        for (int i2 = 0; i2 < changedSubscriptions.size(); i2++) {
            SimData data = this.mSimDatas.get(Integer.valueOf(changedSubscriptions.get(i2).getSubscriptionId()));
            for (int j = 0; j < this.mCallbacks.size(); j++) {
                KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(j).get();
                if (cb != null) {
                    cb.onSimStateChanged(data.subId, data.slotId, data.simState);
                }
            }
        }
        for (int j2 = 0; j2 < this.mCallbacks.size(); j2++) {
            KeyguardUpdateMonitorCallback cb2 = this.mCallbacks.get(j2).get();
            if (cb2 != null) {
                cb2.onRefreshCarrierInfo();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleAirplaneModeChanged() {
        for (int j = 0; j < this.mCallbacks.size(); j++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(j).get();
            if (cb != null) {
                cb.onRefreshCarrierInfo();
            }
        }
    }

    public List<SubscriptionInfo> getSubscriptionInfo(boolean forceReload) {
        List<SubscriptionInfo> sil = this.mSubscriptionInfo;
        if (sil == null || forceReload) {
            sil = this.mSubscriptionManager.getActiveSubscriptionInfoList(false);
        }
        if (sil == null) {
            this.mSubscriptionInfo = new ArrayList();
        } else {
            this.mSubscriptionInfo = sil;
        }
        return new ArrayList(this.mSubscriptionInfo);
    }

    public List<SubscriptionInfo> getFilteredSubscriptionInfo(boolean forceReload) {
        List<SubscriptionInfo> subscriptions = getSubscriptionInfo(false);
        if (subscriptions.size() == 2) {
            SubscriptionInfo info1 = subscriptions.get(0);
            SubscriptionInfo info2 = subscriptions.get(1);
            if (info1.getGroupUuid() != null && info1.getGroupUuid().equals(info2.getGroupUuid())) {
                if (!info1.isOpportunistic() && !info2.isOpportunistic()) {
                    return subscriptions;
                }
                boolean alwaysShowPrimary = CarrierConfigManager.getDefaultConfig().getBoolean("always_show_primary_signal_bar_in_opportunistic_network_boolean");
                if (alwaysShowPrimary) {
                    subscriptions.remove(info1.isOpportunistic() ? info1 : info2);
                } else {
                    subscriptions.remove(info1.getSubscriptionId() == this.mActiveMobileDataSubscription ? info2 : info1);
                }
            }
        }
        return subscriptions;
    }

    public void onTrustManagedChanged(boolean managed, int userId) {
        checkIsHandlerThread();
        this.mUserTrustIsManaged.put(userId, managed);
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onTrustManagedChanged(userId);
            }
        }
    }

    public void setKeyguardGoingAway(boolean goingAway) {
        this.mKeyguardGoingAway = goingAway;
        updateFingerprintListeningState();
    }

    public void setKeyguardOccluded(boolean occluded) {
        this.mKeyguardOccluded = occluded;
        updateBiometricListeningState();
    }

    public void onCameraLaunched() {
        this.mSecureCameraLaunched = true;
        updateBiometricListeningState();
    }

    public boolean isDreaming() {
        return this.mIsDreaming;
    }

    public void awakenFromDream() {
        IDreamManager iDreamManager;
        if (this.mIsDreaming && (iDreamManager = this.mDreamManager) != null) {
            try {
                iDreamManager.awaken();
            } catch (RemoteException e) {
                Log.e(TAG, "Unable to awaken from dream");
            }
        }
    }

    @VisibleForTesting
    protected void onFingerprintAuthenticated(int userId) {
        Trace.beginSection("KeyGuardUpdateMonitor#onFingerPrintAuthenticated");
        this.mUserFingerprintAuthenticated.put(userId, true);
        if (getUserCanSkipBouncer(userId)) {
            this.mTrustManager.unlockedByBiometricForUser(userId, BiometricSourceType.FINGERPRINT);
        }
        this.mFingerprintCancelSignal = null;
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onBiometricAuthenticated(userId, BiometricSourceType.FINGERPRINT);
            }
        }
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(MSG_BIOMETRIC_AUTHENTICATION_CONTINUE), 500L);
        this.mAssistantVisible = false;
        Trace.endSection();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFingerprintAuthFailed() {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onBiometricAuthFailed(BiometricSourceType.FINGERPRINT);
            }
        }
        handleFingerprintHelp(-1, this.mContext.getString(R.string.kg_fingerprint_not_recognized));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFingerprintAcquired(int acquireInfo) {
        if (acquireInfo != 0) {
            return;
        }
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onBiometricAcquired(BiometricSourceType.FINGERPRINT);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFingerprintAuthenticated(int authUserId) {
        Trace.beginSection("KeyGuardUpdateMonitor#handlerFingerPrintAuthenticated");
        try {
            int userId = ActivityManager.getService().getCurrentUser().id;
            if (userId != authUserId) {
                Log.d(TAG, "Fingerprint authenticated for wrong user: " + authUserId);
            } else if (!isFingerprintDisabled(userId)) {
                onFingerprintAuthenticated(userId);
                setFingerprintRunningState(0);
                Trace.endSection();
            } else {
                Log.d(TAG, "Fingerprint disabled by DPM for userId: " + userId);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to get current user id: ", e);
        } finally {
            setFingerprintRunningState(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFingerprintHelp(int msgId, String helpString) {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onBiometricHelp(msgId, helpString, BiometricSourceType.FINGERPRINT);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFingerprintError(int msgId, String errString) {
        int i;
        if (msgId == 5 && this.mFingerprintRunningState == 3) {
            setFingerprintRunningState(0);
            updateFingerprintListeningState();
        } else {
            setFingerprintRunningState(0);
        }
        if (msgId == 1 && (i = this.mHardwareFingerprintUnavailableRetryCount) < 10) {
            this.mHardwareFingerprintUnavailableRetryCount = i + 1;
            this.mHandler.removeCallbacks(this.mRetryFingerprintAuthentication);
            this.mHandler.postDelayed(this.mRetryFingerprintAuthentication, 500L);
        }
        if (msgId == 9) {
            this.mLockPatternUtils.requireStrongAuth(8, getCurrentUser());
        }
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i2).get();
            if (cb != null) {
                cb.onBiometricError(msgId, errString, BiometricSourceType.FINGERPRINT);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFingerprintLockoutReset() {
        updateFingerprintListeningState();
    }

    private void setFingerprintRunningState(int fingerprintRunningState) {
        boolean wasRunning = this.mFingerprintRunningState == 1;
        boolean isRunning = fingerprintRunningState == 1;
        this.mFingerprintRunningState = fingerprintRunningState;
        Log.d(TAG, "fingerprintRunningState: " + this.mFingerprintRunningState);
        if (wasRunning != isRunning) {
            notifyFingerprintRunningStateChanged();
        }
    }

    private void notifyFingerprintRunningStateChanged() {
        checkIsHandlerThread();
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onBiometricRunningStateChanged(isFingerprintDetectionRunning(), BiometricSourceType.FINGERPRINT);
            }
        }
    }

    @VisibleForTesting
    protected void onFaceAuthenticated(int userId) {
        Trace.beginSection("KeyGuardUpdateMonitor#onFaceAuthenticated");
        this.mUserFaceAuthenticated.put(userId, true);
        if (getUserCanSkipBouncer(userId)) {
            this.mTrustManager.unlockedByBiometricForUser(userId, BiometricSourceType.FACE);
        }
        this.mFaceCancelSignal = null;
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onBiometricAuthenticated(userId, BiometricSourceType.FACE);
            }
        }
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(MSG_BIOMETRIC_AUTHENTICATION_CONTINUE), 500L);
        this.mAssistantVisible = false;
        Trace.endSection();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFaceAuthFailed() {
        setFaceRunningState(0);
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onBiometricAuthFailed(BiometricSourceType.FACE);
            }
        }
        handleFaceHelp(-2, this.mContext.getString(R.string.kg_face_not_recognized));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFaceAcquired(int acquireInfo) {
        if (acquireInfo != 0) {
            return;
        }
        Log.d(TAG, "Face acquired");
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onBiometricAcquired(BiometricSourceType.FACE);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFaceAuthenticated(int authUserId) {
        Trace.beginSection("KeyGuardUpdateMonitor#handlerFaceAuthenticated");
        try {
            if (this.mGoingToSleep) {
                Log.d(TAG, "Aborted successful auth because device is going to sleep.");
                return;
            }
            int userId = ActivityManager.getService().getCurrentUser().id;
            if (userId != authUserId) {
                Log.d(TAG, "Face authenticated for wrong user: " + authUserId);
            } else if (isFaceDisabled(userId)) {
                Log.d(TAG, "Face authentication disabled by DPM for userId: " + userId);
            } else {
                Log.d(TAG, "Face auth succeeded for user " + userId);
                onFaceAuthenticated(userId);
                setFaceRunningState(0);
                Trace.endSection();
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to get current user id: ", e);
        } finally {
            setFaceRunningState(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFaceHelp(int msgId, String helpString) {
        Log.d(TAG, "Face help received: " + helpString);
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onBiometricHelp(msgId, helpString, BiometricSourceType.FACE);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFaceError(int msgId, String errString) {
        int i;
        Log.d(TAG, "Face error received: " + errString);
        if (msgId == 5 && this.mFaceRunningState == 3) {
            setFaceRunningState(0);
            updateFaceListeningState();
        } else {
            setFaceRunningState(0);
        }
        if ((msgId == 1 || msgId == 2) && (i = this.mHardwareFaceUnavailableRetryCount) < 10) {
            this.mHardwareFaceUnavailableRetryCount = i + 1;
            this.mHandler.removeCallbacks(this.mRetryFaceAuthentication);
            this.mHandler.postDelayed(this.mRetryFaceAuthentication, 500L);
        }
        if (msgId == 9) {
            this.mLockPatternUtils.requireStrongAuth(8, getCurrentUser());
        }
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i2).get();
            if (cb != null) {
                cb.onBiometricError(msgId, errString, BiometricSourceType.FACE);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFaceLockoutReset() {
        updateFaceListeningState();
    }

    private void setFaceRunningState(int faceRunningState) {
        boolean wasRunning = this.mFaceRunningState == 1;
        boolean isRunning = faceRunningState == 1;
        this.mFaceRunningState = faceRunningState;
        Log.d(TAG, "faceRunningState: " + this.mFaceRunningState);
        if (wasRunning != isRunning) {
            notifyFaceRunningStateChanged();
        }
    }

    private void notifyFaceRunningStateChanged() {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onBiometricRunningStateChanged(isFaceDetectionRunning(), BiometricSourceType.FACE);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFaceUnlockStateChanged(boolean running, int userId) {
        checkIsHandlerThread();
        this.mUserFaceUnlockRunning.put(userId, running);
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onFaceUnlockStateChanged(running, userId);
            }
        }
    }

    public boolean isFaceUnlockRunning(int userId) {
        return this.mUserFaceUnlockRunning.get(userId);
    }

    public boolean isFingerprintDetectionRunning() {
        return this.mFingerprintRunningState == 1;
    }

    public boolean isFaceDetectionRunning() {
        return this.mFaceRunningState == 1;
    }

    private boolean isTrustDisabled(int userId) {
        boolean disabledBySimPin = isSimPinSecure();
        return disabledBySimPin;
    }

    private boolean isFingerprintDisabled(int userId) {
        DevicePolicyManager dpm = (DevicePolicyManager) this.mContext.getSystemService("device_policy");
        return !(dpm == null || (dpm.getKeyguardDisabledFeatures(null, userId) & 32) == 0) || isSimPinSecure();
    }

    private boolean isFaceDisabled(int userId) {
        DevicePolicyManager dpm = (DevicePolicyManager) this.mContext.getSystemService("device_policy");
        return !(dpm == null || (dpm.getKeyguardDisabledFeatures(null, userId) & 128) == 0) || isSimPinSecure();
    }

    public boolean getUserCanSkipBouncer(int userId) {
        return getUserHasTrust(userId) || getUserUnlockedWithBiometric(userId);
    }

    public boolean getUserHasTrust(int userId) {
        return !isTrustDisabled(userId) && this.mUserHasTrust.get(userId);
    }

    public boolean getUserUnlockedWithBiometric(int userId) {
        boolean fingerprintOrFace = this.mUserFingerprintAuthenticated.get(userId) || this.mUserFaceAuthenticated.get(userId);
        return fingerprintOrFace && isUnlockingWithBiometricAllowed();
    }

    public boolean getUserTrustIsManaged(int userId) {
        return this.mUserTrustIsManaged.get(userId) && !isTrustDisabled(userId);
    }

    public boolean isUnlockingWithBiometricAllowed() {
        return this.mStrongAuthTracker.isUnlockingWithBiometricAllowed();
    }

    public boolean isUserInLockdown(int userId) {
        return containsFlag(this.mStrongAuthTracker.getStrongAuthForUser(userId), 32);
    }

    public boolean userNeedsStrongAuth() {
        return this.mStrongAuthTracker.getStrongAuthForUser(getCurrentUser()) != 0;
    }

    private boolean containsFlag(int haystack, int needle) {
        return (haystack & needle) != 0;
    }

    public boolean needsSlowUnlockTransition() {
        return this.mNeedsSlowUnlockTransition;
    }

    public StrongAuthTracker getStrongAuthTracker() {
        return this.mStrongAuthTracker;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyStrongAuthStateChanged(int userId) {
        checkIsHandlerThread();
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onStrongAuthStateChanged(userId);
            }
        }
    }

    public boolean isScreenOn() {
        return this.mScreenOn;
    }

    private void dispatchErrorMessage(CharSequence message) {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onTrustAgentErrorMessage(message);
            }
        }
    }

    @VisibleForTesting
    void setAssistantVisible(boolean assistantVisible) {
        this.mAssistantVisible = assistantVisible;
        updateBiometricListeningState();
    }

    /* loaded from: classes19.dex */
    static class DisplayClientState {
        public boolean clearing;
        public int clientGeneration;
        public PendingIntent intent;
        public long playbackEventTime;
        public int playbackState;

        DisplayClientState() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes19.dex */
    public static class SimData {
        public IccCardConstants.State simState;
        public int slotId;
        public int subId;

        SimData(IccCardConstants.State state, int slot, int id) {
            this.simState = state;
            this.slotId = slot;
            this.subId = id;
        }

        static SimData fromIntent(Intent intent) {
            IccCardConstants.State state;
            if (!"android.intent.action.SIM_STATE_CHANGED".equals(intent.getAction())) {
                throw new IllegalArgumentException("only handles intent ACTION_SIM_STATE_CHANGED");
            }
            String stateExtra = intent.getStringExtra("ss");
            int slotId = intent.getIntExtra("phone", 0);
            int subId = intent.getIntExtra("subscription", -1);
            if ("ABSENT".equals(stateExtra)) {
                String absentReason = intent.getStringExtra(GlobalActionsDialog.SYSTEM_DIALOG_REASON_KEY);
                if ("PERM_DISABLED".equals(absentReason)) {
                    state = IccCardConstants.State.PERM_DISABLED;
                } else {
                    state = IccCardConstants.State.ABSENT;
                }
            } else if ("READY".equals(stateExtra)) {
                state = IccCardConstants.State.READY;
            } else if ("LOCKED".equals(stateExtra)) {
                String lockedReason = intent.getStringExtra(GlobalActionsDialog.SYSTEM_DIALOG_REASON_KEY);
                if ("PIN".equals(lockedReason)) {
                    state = IccCardConstants.State.PIN_REQUIRED;
                } else if ("PUK".equals(lockedReason)) {
                    state = IccCardConstants.State.PUK_REQUIRED;
                } else {
                    state = IccCardConstants.State.UNKNOWN;
                }
            } else if ("NETWORK".equals(stateExtra)) {
                state = IccCardConstants.State.NETWORK_LOCKED;
            } else if ("CARD_IO_ERROR".equals(stateExtra)) {
                state = IccCardConstants.State.CARD_IO_ERROR;
            } else if ("LOADED".equals(stateExtra) || "IMSI".equals(stateExtra)) {
                state = IccCardConstants.State.READY;
            } else {
                state = IccCardConstants.State.UNKNOWN;
            }
            return new SimData(state, slotId, subId);
        }

        public String toString() {
            return "SimData{state=" + this.simState + ",slotId=" + this.slotId + ",subId=" + this.subId + "}";
        }
    }

    /* loaded from: classes19.dex */
    public static class BatteryStatus {
        public static final int CHARGING_FAST = 2;
        public static final int CHARGING_REGULAR = 1;
        public static final int CHARGING_SLOWLY = 0;
        public static final int CHARGING_UNKNOWN = -1;
        public final int health;
        public final int level;
        public final int maxChargingWattage;
        public final int plugged;
        public final int status;

        public BatteryStatus(int status, int level, int plugged, int health, int maxChargingWattage) {
            this.status = status;
            this.level = level;
            this.plugged = plugged;
            this.health = health;
            this.maxChargingWattage = maxChargingWattage;
        }

        public boolean isPluggedIn() {
            int i = this.plugged;
            return i == 1 || i == 2 || i == 4;
        }

        public boolean isPluggedInWired() {
            int i = this.plugged;
            return i == 1 || i == 2;
        }

        public boolean isCharged() {
            return this.status == 5 || this.level >= 100;
        }

        public boolean isBatteryLow() {
            return this.level < 20;
        }

        public final int getChargingSpeed(int slowThreshold, int fastThreshold) {
            int i = this.maxChargingWattage;
            if (i <= 0) {
                return -1;
            }
            if (i < slowThreshold) {
                return 0;
            }
            return i > fastThreshold ? 2 : 1;
        }

        public String toString() {
            return "BatteryStatus{status=" + this.status + ",level=" + this.level + ",plugged=" + this.plugged + ",health=" + this.health + ",maxChargingWattage=" + this.maxChargingWattage + "}";
        }
    }

    /* loaded from: classes19.dex */
    public static class StrongAuthTracker extends LockPatternUtils.StrongAuthTracker {
        private final Consumer<Integer> mStrongAuthRequiredChangedCallback;

        public StrongAuthTracker(Context context, Consumer<Integer> strongAuthRequiredChangedCallback) {
            super(context);
            this.mStrongAuthRequiredChangedCallback = strongAuthRequiredChangedCallback;
        }

        public boolean isUnlockingWithBiometricAllowed() {
            int userId = KeyguardUpdateMonitor.getCurrentUser();
            return isBiometricAllowedForUser(userId);
        }

        public boolean hasUserAuthenticatedSinceBoot() {
            int userId = KeyguardUpdateMonitor.getCurrentUser();
            return (getStrongAuthForUser(userId) & 1) == 0;
        }

        public void onStrongAuthRequiredChanged(int userId) {
            this.mStrongAuthRequiredChangedCallback.accept(Integer.valueOf(userId));
        }
    }

    public static KeyguardUpdateMonitor getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new KeyguardUpdateMonitor(context);
        }
        return sInstance;
    }

    protected void handleStartedWakingUp() {
        Trace.beginSection("KeyguardUpdateMonitor#handleStartedWakingUp");
        updateBiometricListeningState();
        int count = this.mCallbacks.size();
        for (int i = 0; i < count; i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onStartedWakingUp();
            }
        }
        Trace.endSection();
    }

    protected void handleStartedGoingToSleep(int arg1) {
        this.mLockIconPressed = false;
        clearBiometricRecognized();
        int count = this.mCallbacks.size();
        for (int i = 0; i < count; i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onStartedGoingToSleep(arg1);
            }
        }
        this.mGoingToSleep = true;
        updateBiometricListeningState();
    }

    protected void handleFinishedGoingToSleep(int arg1) {
        this.mGoingToSleep = false;
        int count = this.mCallbacks.size();
        for (int i = 0; i < count; i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onFinishedGoingToSleep(arg1);
            }
        }
        updateBiometricListeningState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleScreenTurnedOn() {
        int count = this.mCallbacks.size();
        for (int i = 0; i < count; i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onScreenTurnedOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleScreenTurnedOff() {
        this.mHardwareFingerprintUnavailableRetryCount = 0;
        this.mHardwareFaceUnavailableRetryCount = 0;
        int count = this.mCallbacks.size();
        for (int i = 0; i < count; i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onScreenTurnedOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDreamingStateChanged(int dreamStart) {
        int count = this.mCallbacks.size();
        this.mIsDreaming = dreamStart == 1;
        for (int i = 0; i < count; i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onDreamingStateChanged(this.mIsDreaming);
            }
        }
        updateBiometricListeningState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleUserInfoChanged(int userId) {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onUserInfoChanged(userId);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleUserUnlocked() {
        this.mNeedsSlowUnlockTransition = resolveNeedsSlowUnlockTransition();
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onUserUnlocked();
            }
        }
    }

    @VisibleForTesting
    protected KeyguardUpdateMonitor(Context context) {
        this.mContext = context;
        this.mSubscriptionManager = SubscriptionManager.from(context);
        this.mStrongAuthTracker = new StrongAuthTracker(context, new Consumer() { // from class: com.android.keyguard.-$$Lambda$KeyguardUpdateMonitor$-GZaxeQabrHzh5b8rORPTQGQVD8
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                KeyguardUpdateMonitor.this.notifyStrongAuthStateChanged(((Integer) obj).intValue());
            }
        });
        if (!this.mDeviceProvisioned) {
            watchForDeviceProvisioning();
        }
        this.mBatteryStatus = new BatteryStatus(1, 100, 0, 0, 0);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.TIME_TICK");
        filter.addAction("android.intent.action.TIME_SET");
        filter.addAction("android.intent.action.BATTERY_CHANGED");
        filter.addAction("android.intent.action.TIMEZONE_CHANGED");
        filter.addAction("android.intent.action.AIRPLANE_MODE");
        filter.addAction("android.intent.action.SIM_STATE_CHANGED");
        filter.addAction("android.intent.action.SERVICE_STATE");
        filter.addAction("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED");
        filter.addAction("android.intent.action.PHONE_STATE");
        filter.addAction("android.media.RINGER_MODE_CHANGED");
        filter.addAction("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
        context.registerReceiver(this.mBroadcastReceiver, filter, null, this.mHandler);
        IntentFilter bootCompleteFilter = new IntentFilter();
        bootCompleteFilter.setPriority(1000);
        bootCompleteFilter.addAction("android.intent.action.BOOT_COMPLETED");
        context.registerReceiver(this.mBroadcastReceiver, bootCompleteFilter, null, this.mHandler);
        IntentFilter allUserFilter = new IntentFilter();
        allUserFilter.addAction("android.intent.action.USER_INFO_CHANGED");
        allUserFilter.addAction("android.app.action.NEXT_ALARM_CLOCK_CHANGED");
        allUserFilter.addAction(ACTION_FACE_UNLOCK_STARTED);
        allUserFilter.addAction(ACTION_FACE_UNLOCK_STOPPED);
        allUserFilter.addAction("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
        allUserFilter.addAction("android.intent.action.USER_UNLOCKED");
        context.registerReceiverAsUser(this.mBroadcastAllReceiver, UserHandle.ALL, allUserFilter, null, this.mHandler);
        this.mSubscriptionManager.addOnSubscriptionsChangedListener(this.mSubscriptionListener);
        try {
            ActivityManager.getService().registerUserSwitchObserver(new UserSwitchObserver() { // from class: com.android.keyguard.KeyguardUpdateMonitor.13
                public void onUserSwitching(int newUserId, IRemoteCallback reply) {
                    KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(KeyguardUpdateMonitor.MSG_USER_SWITCHING, newUserId, 0, reply));
                }

                public void onUserSwitchComplete(int newUserId) throws RemoteException {
                    KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(KeyguardUpdateMonitor.MSG_USER_SWITCH_COMPLETE, newUserId, 0));
                }
            }, TAG);
        } catch (RemoteException e) {
            e.rethrowAsRuntimeException();
        }
        this.mTrustManager = (TrustManager) context.getSystemService("trust");
        this.mTrustManager.registerTrustListener(this);
        this.mLockPatternUtils = new LockPatternUtils(context);
        this.mLockPatternUtils.registerStrongAuthTracker(this.mStrongAuthTracker);
        this.mDreamManager = IDreamManager.Stub.asInterface(ServiceManager.getService("dreams"));
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.fingerprint")) {
            this.mFpm = (FingerprintManager) context.getSystemService("fingerprint");
        }
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.biometrics.face")) {
            this.mFaceManager = (FaceManager) context.getSystemService("face");
        }
        if (this.mFpm != null || this.mFaceManager != null) {
            this.mBiometricManager = (BiometricManager) context.getSystemService(BiometricManager.class);
            this.mBiometricManager.registerEnabledOnKeyguardCallback(this.mBiometricEnabledCallback);
        }
        updateBiometricListeningState();
        FingerprintManager fingerprintManager = this.mFpm;
        if (fingerprintManager != null) {
            fingerprintManager.addLockoutResetCallback(this.mFingerprintLockoutResetCallback);
        }
        FaceManager faceManager = this.mFaceManager;
        if (faceManager != null) {
            faceManager.addLockoutResetCallback(this.mFaceLockoutResetCallback);
        }
        ActivityManagerWrapper.getInstance().registerTaskStackListener(this.mTaskStackListener);
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
        this.mIsPrimaryUser = this.mUserManager.isPrimaryUser();
        this.mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
        this.mLogoutEnabled = this.mDevicePolicyManager.isLogoutEnabled();
        updateAirplaneModeState();
        TelephonyManager telephony = (TelephonyManager) context.getSystemService("phone");
        if (telephony != null) {
            telephony.listen(this.mPhoneStateListener, 4194304);
        }
    }

    private void updateAirplaneModeState() {
        if (WirelessUtils.isAirplaneModeOn(this.mContext) && !this.mHandler.hasMessages(MSG_AIRPLANE_MODE_CHANGED)) {
            this.mHandler.sendEmptyMessage(MSG_AIRPLANE_MODE_CHANGED);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBiometricListeningState() {
        updateFingerprintListeningState();
        updateFaceListeningState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFingerprintListeningState() {
        if (this.mHandler.hasMessages(MSG_BIOMETRIC_AUTHENTICATION_CONTINUE)) {
            return;
        }
        this.mHandler.removeCallbacks(this.mRetryFingerprintAuthentication);
        boolean shouldListenForFingerprint = shouldListenForFingerprint();
        int i = this.mFingerprintRunningState;
        boolean z = true;
        if (i != 1 && i != 3) {
            z = false;
        }
        boolean runningOrRestarting = z;
        if (runningOrRestarting && !shouldListenForFingerprint) {
            stopListeningForFingerprint();
        } else if (!runningOrRestarting && shouldListenForFingerprint) {
            startListeningForFingerprint();
        }
    }

    public void onAuthInterruptDetected(boolean active) {
        if (this.mAuthInterruptActive == active) {
            return;
        }
        this.mAuthInterruptActive = active;
        updateFaceListeningState();
    }

    public void requestFaceAuth() {
        updateFaceListeningState();
    }

    public void cancelFaceAuth() {
        stopListeningForFace();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFaceListeningState() {
        if (this.mHandler.hasMessages(MSG_BIOMETRIC_AUTHENTICATION_CONTINUE)) {
            return;
        }
        this.mHandler.removeCallbacks(this.mRetryFaceAuthentication);
        boolean shouldListenForFace = shouldListenForFace();
        if (this.mFaceRunningState == 1 && !shouldListenForFace) {
            stopListeningForFace();
        } else if (this.mFaceRunningState != 1 && shouldListenForFace) {
            startListeningForFace();
        }
    }

    private boolean shouldListenForFingerprintAssistant() {
        return this.mAssistantVisible && this.mKeyguardOccluded && !this.mUserFingerprintAuthenticated.get(getCurrentUser(), false) && !this.mUserHasTrust.get(getCurrentUser(), false);
    }

    private boolean shouldListenForFaceAssistant() {
        return this.mAssistantVisible && this.mKeyguardOccluded && !this.mUserFaceAuthenticated.get(getCurrentUser(), false) && !this.mUserHasTrust.get(getCurrentUser(), false);
    }

    private boolean shouldListenForFingerprint() {
        return (this.mKeyguardIsVisible || !this.mDeviceInteractive || ((this.mBouncer && !this.mKeyguardGoingAway) || this.mGoingToSleep || shouldListenForFingerprintAssistant() || (this.mKeyguardOccluded && this.mIsDreaming))) && !this.mSwitchingUser && !isFingerprintDisabled(getCurrentUser()) && !(this.mKeyguardGoingAway && this.mDeviceInteractive) && this.mIsPrimaryUser;
    }

    public boolean shouldListenForFace() {
        boolean awakeKeyguard = this.mKeyguardIsVisible && this.mDeviceInteractive && !this.mGoingToSleep;
        int user = getCurrentUser();
        int strongAuth = this.mStrongAuthTracker.getStrongAuthForUser(user);
        boolean isLockOutOrLockDown = containsFlag(strongAuth, 8) || containsFlag(strongAuth, 2) || containsFlag(strongAuth, 32);
        boolean isEncryptedOrTimedOut = containsFlag(strongAuth, 1) || containsFlag(strongAuth, 16);
        KeyguardBypassController keyguardBypassController = this.mKeyguardBypassController;
        boolean canBypass = keyguardBypassController != null && keyguardBypassController.canBypass();
        boolean becauseCannotSkipBouncer = !getUserCanSkipBouncer(user) || canBypass;
        boolean strongAuthAllowsScanning = (!isEncryptedOrTimedOut || (canBypass && !this.mBouncer)) && !isLockOutOrLockDown;
        return (this.mBouncer || this.mAuthInterruptActive || awakeKeyguard || shouldListenForFaceAssistant()) && !this.mSwitchingUser && !isFaceDisabled(user) && becauseCannotSkipBouncer && !this.mKeyguardGoingAway && this.mFaceSettingEnabledForUser.get(user) && !this.mLockIconPressed && strongAuthAllowsScanning && this.mIsPrimaryUser && !this.mSecureCameraLaunched;
    }

    public void onLockIconPressed() {
        this.mLockIconPressed = true;
        this.mUserFaceAuthenticated.put(getCurrentUser(), false);
        updateFaceListeningState();
    }

    private void startListeningForFingerprint() {
        int i = this.mFingerprintRunningState;
        if (i == 2) {
            setFingerprintRunningState(3);
        } else if (i == 3) {
        } else {
            int userId = getCurrentUser();
            if (isUnlockWithFingerprintPossible(userId)) {
                CancellationSignal cancellationSignal = this.mFingerprintCancelSignal;
                if (cancellationSignal != null) {
                    cancellationSignal.cancel();
                }
                this.mFingerprintCancelSignal = new CancellationSignal();
                this.mFpm.authenticate(null, this.mFingerprintCancelSignal, 0, this.mFingerprintAuthenticationCallback, null, userId);
                setFingerprintRunningState(1);
            }
        }
    }

    private void startListeningForFace() {
        if (this.mFaceRunningState == 2) {
            setFaceRunningState(3);
            return;
        }
        int userId = getCurrentUser();
        if (isUnlockWithFacePossible(userId)) {
            CancellationSignal cancellationSignal = this.mFaceCancelSignal;
            if (cancellationSignal != null) {
                cancellationSignal.cancel();
            }
            this.mFaceCancelSignal = new CancellationSignal();
            this.mFaceManager.authenticate((CryptoObject) null, this.mFaceCancelSignal, 0, this.mFaceAuthenticationCallback, (Handler) null, userId);
            setFaceRunningState(1);
        }
    }

    public boolean isUnlockingWithBiometricsPossible(int userId) {
        return isUnlockWithFacePossible(userId) || isUnlockWithFingerprintPossible(userId);
    }

    private boolean isUnlockWithFingerprintPossible(int userId) {
        FingerprintManager fingerprintManager = this.mFpm;
        return fingerprintManager != null && fingerprintManager.isHardwareDetected() && !isFingerprintDisabled(userId) && this.mFpm.getEnrolledFingerprints(userId).size() > 0;
    }

    private boolean isUnlockWithFacePossible(int userId) {
        return isFaceAuthEnabledForUser(userId) && !isFaceDisabled(userId);
    }

    public boolean isFaceAuthEnabledForUser(int userId) {
        FaceManager faceManager = this.mFaceManager;
        return faceManager != null && faceManager.isHardwareDetected() && this.mFaceManager.hasEnrolledTemplates(userId) && this.mFaceSettingEnabledForUser.get(userId);
    }

    private void stopListeningForFingerprint() {
        if (this.mFingerprintRunningState == 1) {
            CancellationSignal cancellationSignal = this.mFingerprintCancelSignal;
            if (cancellationSignal != null) {
                cancellationSignal.cancel();
                this.mFingerprintCancelSignal = null;
            }
            setFingerprintRunningState(2);
        }
        if (this.mFingerprintRunningState == 3) {
            setFingerprintRunningState(2);
        }
    }

    private void stopListeningForFace() {
        if (this.mFaceRunningState == 1) {
            CancellationSignal cancellationSignal = this.mFaceCancelSignal;
            if (cancellationSignal != null) {
                cancellationSignal.cancel();
                this.mFaceCancelSignal = null;
            }
            setFaceRunningState(2);
        }
        if (this.mFaceRunningState == 3) {
            setFaceRunningState(2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isDeviceProvisionedInSettingsDb() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "device_provisioned", 0) != 0;
    }

    private void watchForDeviceProvisioning() {
        this.mDeviceProvisionedObserver = new ContentObserver(this.mHandler) { // from class: com.android.keyguard.KeyguardUpdateMonitor.14
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                KeyguardUpdateMonitor keyguardUpdateMonitor = KeyguardUpdateMonitor.this;
                keyguardUpdateMonitor.mDeviceProvisioned = keyguardUpdateMonitor.isDeviceProvisionedInSettingsDb();
                if (KeyguardUpdateMonitor.this.mDeviceProvisioned) {
                    KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(KeyguardUpdateMonitor.MSG_DEVICE_PROVISIONED);
                }
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("device_provisioned"), false, this.mDeviceProvisionedObserver);
        boolean provisioned = isDeviceProvisionedInSettingsDb();
        if (provisioned != this.mDeviceProvisioned) {
            this.mDeviceProvisioned = provisioned;
            if (this.mDeviceProvisioned) {
                this.mHandler.sendEmptyMessage(MSG_DEVICE_PROVISIONED);
            }
        }
    }

    public void setHasLockscreenWallpaper(boolean hasLockscreenWallpaper) {
        checkIsHandlerThread();
        if (hasLockscreenWallpaper != this.mHasLockscreenWallpaper) {
            this.mHasLockscreenWallpaper = hasLockscreenWallpaper;
            for (int i = this.mCallbacks.size() - 1; i >= 0; i--) {
                KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
                if (cb != null) {
                    cb.onHasLockscreenWallpaperChanged(hasLockscreenWallpaper);
                }
            }
        }
    }

    public boolean hasLockscreenWallpaper() {
        return this.mHasLockscreenWallpaper;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDevicePolicyManagerStateChanged() {
        updateFingerprintListeningState();
        for (int i = this.mCallbacks.size() - 1; i >= 0; i--) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onDevicePolicyManagerStateChanged();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleUserSwitching(int userId, IRemoteCallback reply) {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onUserSwitching(userId);
            }
        }
        try {
            reply.sendResult((Bundle) null);
        } catch (RemoteException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleUserSwitchComplete(int userId) {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onUserSwitchComplete(userId);
            }
        }
    }

    public void dispatchBootCompleted() {
        this.mHandler.sendEmptyMessage(MSG_BOOT_COMPLETED);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleBootCompleted() {
        if (this.mBootCompleted) {
            return;
        }
        this.mBootCompleted = true;
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onBootCompleted();
            }
        }
    }

    public boolean hasBootCompleted() {
        return this.mBootCompleted;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDeviceProvisioned() {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onDeviceProvisioned();
            }
        }
        if (this.mDeviceProvisionedObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mDeviceProvisionedObserver);
            this.mDeviceProvisionedObserver = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePhoneStateChanged(String newState) {
        if (TelephonyManager.EXTRA_STATE_IDLE.equals(newState)) {
            this.mPhoneState = 0;
        } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(newState)) {
            this.mPhoneState = 2;
        } else if (TelephonyManager.EXTRA_STATE_RINGING.equals(newState)) {
            this.mPhoneState = 1;
        }
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onPhoneStateChanged(this.mPhoneState);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRingerModeChange(int mode) {
        this.mRingMode = mode;
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onRingerModeChanged(mode);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleTimeUpdate() {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onTimeChanged();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleTimeZoneUpdate(String timeZone) {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onTimeZoneChanged(TimeZone.getTimeZone(timeZone));
                cb.onTimeChanged();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleBatteryUpdate(BatteryStatus status) {
        boolean batteryUpdateInteresting = isBatteryUpdateInteresting(this.mBatteryStatus, status);
        this.mBatteryStatus = status;
        if (batteryUpdateInteresting) {
            for (int i = 0; i < this.mCallbacks.size(); i++) {
                KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
                if (cb != null) {
                    cb.onRefreshBatteryInfo(status);
                }
            }
        }
    }

    @VisibleForTesting
    void updateTelephonyCapable(boolean capable) {
        if (capable == this.mTelephonyCapable) {
            return;
        }
        this.mTelephonyCapable = capable;
        Iterator<WeakReference<KeyguardUpdateMonitorCallback>> it = this.mCallbacks.iterator();
        while (it.hasNext()) {
            WeakReference<KeyguardUpdateMonitorCallback> ref = it.next();
            KeyguardUpdateMonitorCallback cb = ref.get();
            if (cb != null) {
                cb.onTelephonyCapable(this.mTelephonyCapable);
            }
        }
    }

    @VisibleForTesting
    void handleSimStateChange(int subId, int slotId, IccCardConstants.State state) {
        boolean changed;
        checkIsHandlerThread();
        Log.d(TAG, "handleSimStateChange(subId=" + subId + ", slotId=" + slotId + ", state=" + state + NavigationBarInflaterView.KEY_CODE_END);
        boolean becameAbsent = false;
        boolean z = true;
        if (!SubscriptionManager.isValidSubscriptionId(subId)) {
            Log.w(TAG, "invalid subId in handleSimStateChange()");
            if (state == IccCardConstants.State.ABSENT) {
                updateTelephonyCapable(true);
                becameAbsent = true;
                for (SimData data : this.mSimDatas.values()) {
                    if (data.slotId == slotId) {
                        data.simState = IccCardConstants.State.ABSENT;
                    }
                }
            } else if (state == IccCardConstants.State.CARD_IO_ERROR) {
                updateTelephonyCapable(true);
            } else {
                return;
            }
        }
        SimData data2 = this.mSimDatas.get(Integer.valueOf(subId));
        if (data2 == null) {
            this.mSimDatas.put(Integer.valueOf(subId), new SimData(state, slotId, subId));
            changed = true;
        } else {
            if (data2.simState == state && data2.subId == subId && data2.slotId == slotId) {
                z = false;
            }
            changed = z;
            data2.simState = state;
            data2.subId = subId;
            data2.slotId = slotId;
        }
        if ((changed || becameAbsent) && state != IccCardConstants.State.UNKNOWN) {
            for (int i = 0; i < this.mCallbacks.size(); i++) {
                KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
                if (cb != null) {
                    cb.onSimStateChanged(subId, slotId, state);
                }
            }
        }
    }

    @VisibleForTesting
    void handleServiceStateChange(int subId, ServiceState serviceState) {
        if (!SubscriptionManager.isValidSubscriptionId(subId)) {
            Log.w(TAG, "invalid subId in handleServiceStateChange()");
            return;
        }
        updateTelephonyCapable(true);
        this.mServiceStates.put(Integer.valueOf(subId), serviceState);
        for (int j = 0; j < this.mCallbacks.size(); j++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(j).get();
            if (cb != null) {
                cb.onRefreshCarrierInfo();
            }
        }
    }

    public boolean isKeyguardVisible() {
        return this.mKeyguardIsVisible;
    }

    public void onKeyguardVisibilityChanged(boolean showing) {
        checkIsHandlerThread();
        Log.d(TAG, "onKeyguardVisibilityChanged(" + showing + NavigationBarInflaterView.KEY_CODE_END);
        this.mKeyguardIsVisible = showing;
        if (showing) {
            this.mSecureCameraLaunched = false;
        }
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onKeyguardVisibilityChangedRaw(showing);
            }
        }
        updateBiometricListeningState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleKeyguardReset() {
        updateBiometricListeningState();
        this.mNeedsSlowUnlockTransition = resolveNeedsSlowUnlockTransition();
    }

    private boolean resolveNeedsSlowUnlockTransition() {
        if (this.mUserManager.isUserUnlocked(getCurrentUser())) {
            return false;
        }
        Intent homeIntent = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME");
        ResolveInfo resolveInfo = this.mContext.getPackageManager().resolveActivity(homeIntent, 0);
        return FALLBACK_HOME_COMPONENT.equals(resolveInfo.getComponentInfo().getComponentName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleKeyguardBouncerChanged(int bouncer) {
        boolean isBouncer = bouncer == 1;
        this.mBouncer = isBouncer;
        if (isBouncer) {
            this.mSecureCameraLaunched = false;
        }
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onKeyguardBouncerChanged(isBouncer);
            }
        }
        updateBiometricListeningState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleReportEmergencyCallAction() {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onEmergencyCallAction();
            }
        }
    }

    private boolean isBatteryUpdateInteresting(BatteryStatus old, BatteryStatus current) {
        boolean nowPluggedIn = current.isPluggedIn();
        boolean wasPluggedIn = old.isPluggedIn();
        boolean stateChangedWhilePluggedIn = wasPluggedIn && nowPluggedIn && old.status != current.status;
        if (wasPluggedIn == nowPluggedIn && !stateChangedWhilePluggedIn && old.level == current.level) {
            return nowPluggedIn && current.maxChargingWattage != old.maxChargingWattage;
        }
        return true;
    }

    public void removeCallback(KeyguardUpdateMonitorCallback callback) {
        checkIsHandlerThread();
        for (int i = this.mCallbacks.size() - 1; i >= 0; i--) {
            if (this.mCallbacks.get(i).get() == callback) {
                this.mCallbacks.remove(i);
            }
        }
    }

    public void registerCallback(KeyguardUpdateMonitorCallback callback) {
        checkIsHandlerThread();
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            if (this.mCallbacks.get(i).get() == callback) {
                return;
            }
        }
        this.mCallbacks.add(new WeakReference<>(callback));
        removeCallback(null);
        sendUpdates(callback);
    }

    public void setKeyguardBypassController(KeyguardBypassController keyguardBypassController) {
        this.mKeyguardBypassController = keyguardBypassController;
    }

    public boolean isSwitchingUser() {
        return this.mSwitchingUser;
    }

    public void setSwitchingUser(boolean switching) {
        this.mSwitchingUser = switching;
        this.mHandler.post(this.mUpdateBiometricListeningState);
    }

    private void sendUpdates(KeyguardUpdateMonitorCallback callback) {
        callback.onRefreshBatteryInfo(this.mBatteryStatus);
        callback.onTimeChanged();
        callback.onRingerModeChanged(this.mRingMode);
        callback.onPhoneStateChanged(this.mPhoneState);
        callback.onRefreshCarrierInfo();
        callback.onClockVisibilityChanged();
        callback.onKeyguardVisibilityChangedRaw(this.mKeyguardIsVisible);
        callback.onTelephonyCapable(this.mTelephonyCapable);
        for (Map.Entry<Integer, SimData> data : this.mSimDatas.entrySet()) {
            SimData state = data.getValue();
            callback.onSimStateChanged(state.subId, state.slotId, state.simState);
        }
    }

    public void sendKeyguardReset() {
        this.mHandler.obtainMessage(MSG_KEYGUARD_RESET).sendToTarget();
    }

    public void sendKeyguardBouncerChanged(boolean showingBouncer) {
        Message message = this.mHandler.obtainMessage(MSG_KEYGUARD_BOUNCER_CHANGED);
        message.arg1 = showingBouncer ? 1 : 0;
        message.sendToTarget();
    }

    public void reportSimUnlocked(int subId) {
        Log.v(TAG, "reportSimUnlocked(subId=" + subId + NavigationBarInflaterView.KEY_CODE_END);
        int slotId = SubscriptionManager.getSlotIndex(subId);
        handleSimStateChange(subId, slotId, IccCardConstants.State.READY);
    }

    public void reportEmergencyCallAction(boolean bypassHandler) {
        if (!bypassHandler) {
            this.mHandler.obtainMessage(MSG_REPORT_EMERGENCY_CALL_ACTION).sendToTarget();
            return;
        }
        checkIsHandlerThread();
        handleReportEmergencyCallAction();
    }

    public boolean isDeviceProvisioned() {
        return this.mDeviceProvisioned;
    }

    public ServiceState getServiceState(int subId) {
        return this.mServiceStates.get(Integer.valueOf(subId));
    }

    public void clearBiometricRecognized() {
        this.mUserFingerprintAuthenticated.clear();
        this.mUserFaceAuthenticated.clear();
        this.mTrustManager.clearAllBiometricRecognized(BiometricSourceType.FINGERPRINT);
        this.mTrustManager.clearAllBiometricRecognized(BiometricSourceType.FACE);
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onBiometricsCleared();
            }
        }
    }

    public boolean isSimPinVoiceSecure() {
        return isSimPinSecure();
    }

    public boolean isSimPinSecure() {
        for (SubscriptionInfo info : getSubscriptionInfo(false)) {
            if (isSimPinSecure(getSimState(info.getSubscriptionId()))) {
                return true;
            }
        }
        return false;
    }

    public IccCardConstants.State getSimState(int subId) {
        if (this.mSimDatas.containsKey(Integer.valueOf(subId))) {
            return this.mSimDatas.get(Integer.valueOf(subId)).simState;
        }
        return IccCardConstants.State.UNKNOWN;
    }

    private boolean refreshSimState(int subId, int slotId) {
        IccCardConstants.State state;
        TelephonyManager tele = TelephonyManager.from(this.mContext);
        int simState = tele.getSimState(slotId);
        try {
            state = IccCardConstants.State.intToState(simState);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Unknown sim state: " + simState);
            state = IccCardConstants.State.UNKNOWN;
        }
        SimData data = this.mSimDatas.get(Integer.valueOf(subId));
        if (data == null) {
            this.mSimDatas.put(Integer.valueOf(subId), new SimData(state, slotId, subId));
            return true;
        }
        boolean changed = data.simState != state;
        data.simState = state;
        return changed;
    }

    public static boolean isSimPinSecure(IccCardConstants.State state) {
        return state == IccCardConstants.State.PIN_REQUIRED || state == IccCardConstants.State.PUK_REQUIRED || state == IccCardConstants.State.PERM_DISABLED;
    }

    public DisplayClientState getCachedDisplayClientState() {
        return this.mDisplayClientState;
    }

    public void dispatchStartedWakingUp() {
        synchronized (this) {
            this.mDeviceInteractive = true;
        }
        this.mHandler.sendEmptyMessage(MSG_STARTED_WAKING_UP);
    }

    public void dispatchStartedGoingToSleep(int why) {
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(MSG_STARTED_GOING_TO_SLEEP, why, 0));
    }

    public void dispatchFinishedGoingToSleep(int why) {
        synchronized (this) {
            this.mDeviceInteractive = false;
        }
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(320, why, 0));
    }

    public void dispatchScreenTurnedOn() {
        synchronized (this) {
            this.mScreenOn = true;
        }
        this.mHandler.sendEmptyMessage(MSG_SCREEN_TURNED_ON);
    }

    public void dispatchScreenTurnedOff() {
        synchronized (this) {
            this.mScreenOn = false;
        }
        this.mHandler.sendEmptyMessage(MSG_SCREEN_TURNED_OFF);
    }

    public void dispatchDreamingStarted() {
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(MSG_DREAMING_STATE_CHANGED, 1, 0));
    }

    public void dispatchDreamingStopped() {
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(MSG_DREAMING_STATE_CHANGED, 0, 0));
    }

    public boolean isDeviceInteractive() {
        return this.mDeviceInteractive;
    }

    public boolean isGoingToSleep() {
        return this.mGoingToSleep;
    }

    public int getNextSubIdForState(IccCardConstants.State state) {
        List<SubscriptionInfo> list = getSubscriptionInfo(false);
        int resultId = -1;
        int bestSlotId = Integer.MAX_VALUE;
        for (int i = 0; i < list.size(); i++) {
            SubscriptionInfo info = list.get(i);
            int id = info.getSubscriptionId();
            int slotId = SubscriptionManager.getSlotIndex(id);
            if (state == getSimState(id) && bestSlotId > slotId) {
                resultId = id;
                bestSlotId = slotId;
            }
        }
        return resultId;
    }

    public SubscriptionInfo getSubscriptionInfoForSubId(int subId) {
        List<SubscriptionInfo> list = getSubscriptionInfo(false);
        for (int i = 0; i < list.size(); i++) {
            SubscriptionInfo info = list.get(i);
            if (subId == info.getSubscriptionId()) {
                return info;
            }
        }
        return null;
    }

    public boolean isLogoutEnabled() {
        return this.mLogoutEnabled;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLogoutEnabled() {
        checkIsHandlerThread();
        boolean logoutEnabled = this.mDevicePolicyManager.isLogoutEnabled();
        if (this.mLogoutEnabled != logoutEnabled) {
            this.mLogoutEnabled = logoutEnabled;
            for (int i = 0; i < this.mCallbacks.size(); i++) {
                KeyguardUpdateMonitorCallback cb = this.mCallbacks.get(i).get();
                if (cb != null) {
                    cb.onLogoutEnabledChanged();
                }
            }
        }
    }

    private void checkIsHandlerThread() {
        if (!sDisableHandlerCheckForTesting && !this.mHandler.getLooper().isCurrentThread()) {
            Log.wtf(TAG, "must call on mHandler's thread " + this.mHandler.getLooper().getThread() + ", not " + Thread.currentThread());
        }
    }

    @VisibleForTesting
    public static void disableHandlerCheckForTesting(Instrumentation instrumentation) {
        Preconditions.checkNotNull(instrumentation, "Must only call this method in tests!");
        sDisableHandlerCheckForTesting = true;
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("KeyguardUpdateMonitor state:");
        pw.println("  SIM States:");
        for (SimData data : this.mSimDatas.values()) {
            pw.println("    " + data.toString());
        }
        pw.println("  Subs:");
        if (this.mSubscriptionInfo != null) {
            for (int i = 0; i < this.mSubscriptionInfo.size(); i++) {
                pw.println("    " + this.mSubscriptionInfo.get(i));
            }
        }
        pw.println("  Current active data subId=" + this.mActiveMobileDataSubscription);
        pw.println("  Service states:");
        for (Integer num : this.mServiceStates.keySet()) {
            int subId = num.intValue();
            pw.println("    " + subId + "=" + this.mServiceStates.get(Integer.valueOf(subId)));
        }
        FingerprintManager fingerprintManager = this.mFpm;
        if (fingerprintManager != null && fingerprintManager.isHardwareDetected()) {
            int userId = ActivityManager.getCurrentUser();
            int strongAuthFlags = this.mStrongAuthTracker.getStrongAuthForUser(userId);
            pw.println("  Fingerprint state (user=" + userId + NavigationBarInflaterView.KEY_CODE_END);
            StringBuilder sb = new StringBuilder();
            sb.append("    allowed=");
            sb.append(isUnlockingWithBiometricAllowed());
            pw.println(sb.toString());
            pw.println("    auth'd=" + this.mUserFingerprintAuthenticated.get(userId));
            pw.println("    authSinceBoot=" + getStrongAuthTracker().hasUserAuthenticatedSinceBoot());
            pw.println("    disabled(DPM)=" + isFingerprintDisabled(userId));
            pw.println("    possible=" + isUnlockWithFingerprintPossible(userId));
            pw.println("    listening: actual=" + this.mFingerprintRunningState + " expected=" + (shouldListenForFingerprint() ? 1 : 0));
            StringBuilder sb2 = new StringBuilder();
            sb2.append("    strongAuthFlags=");
            sb2.append(Integer.toHexString(strongAuthFlags));
            pw.println(sb2.toString());
            pw.println("    trustManaged=" + getUserTrustIsManaged(userId));
        }
        FaceManager faceManager = this.mFaceManager;
        if (faceManager != null && faceManager.isHardwareDetected()) {
            int userId2 = ActivityManager.getCurrentUser();
            int strongAuthFlags2 = this.mStrongAuthTracker.getStrongAuthForUser(userId2);
            pw.println("  Face authentication state (user=" + userId2 + NavigationBarInflaterView.KEY_CODE_END);
            StringBuilder sb3 = new StringBuilder();
            sb3.append("    allowed=");
            sb3.append(isUnlockingWithBiometricAllowed());
            pw.println(sb3.toString());
            pw.println("    auth'd=" + this.mUserFaceAuthenticated.get(userId2));
            pw.println("    authSinceBoot=" + getStrongAuthTracker().hasUserAuthenticatedSinceBoot());
            pw.println("    disabled(DPM)=" + isFaceDisabled(userId2));
            pw.println("    possible=" + isUnlockWithFacePossible(userId2));
            pw.println("    strongAuthFlags=" + Integer.toHexString(strongAuthFlags2));
            pw.println("    trustManaged=" + getUserTrustIsManaged(userId2));
            pw.println("    enabledByUser=" + this.mFaceSettingEnabledForUser.get(userId2));
            pw.println("    mSecureCameraLaunched=" + this.mSecureCameraLaunched);
        }
    }
}
