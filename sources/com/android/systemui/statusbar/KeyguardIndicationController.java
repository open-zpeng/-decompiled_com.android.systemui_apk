package com.android.systemui.statusbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.hardware.biometrics.BiometricSourceType;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IBatteryStats;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.ViewClippingUtil;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.settingslib.Utils;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.dock.DockManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.KeyguardIndicationTextView;
import com.android.systemui.statusbar.phone.LockIcon;
import com.android.systemui.statusbar.phone.LockscreenGestureLogger;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.phone.UnlockMethodCache;
import com.android.systemui.statusbar.policy.AccessibilityController;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.util.wakelock.SettableWakeLock;
import com.android.systemui.util.wakelock.WakeLock;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.IllegalFormatConversionException;
/* loaded from: classes21.dex */
public class KeyguardIndicationController implements StatusBarStateController.StateListener, UnlockMethodCache.OnUnlockMethodChangedListener {
    private static final float BOUNCE_ANIMATION_FINAL_Y = 0.0f;
    private static final boolean DEBUG_CHARGING_SPEED = false;
    private static final int MSG_CLEAR_BIOMETRIC_MSG = 2;
    private static final int MSG_HIDE_TRANSIENT = 1;
    private static final int MSG_SWIPE_UP_TO_UNLOCK = 3;
    private static final String TAG = "KeyguardIndication";
    private static final long TRANSIENT_BIOMETRIC_ERROR_TIMEOUT = 1300;
    private final AccessibilityController mAccessibilityController;
    private String mAlignmentIndication;
    private final IBatteryStats mBatteryInfo;
    private int mBatteryLevel;
    private int mChargingSpeed;
    private int mChargingWattage;
    private final ViewClippingUtil.ClippingParameters mClippingParams;
    private final Context mContext;
    private final DevicePolicyManager mDevicePolicyManager;
    private KeyguardIndicationTextView mDisclosure;
    private final DockManager mDockManager;
    private boolean mDozing;
    private final int mFastThreshold;
    private final Handler mHandler;
    private boolean mHideTransientMessageOnScreenOff;
    private ViewGroup mIndicationArea;
    private ColorStateList mInitialTextColorState;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final LockIcon mLockIcon;
    private final LockPatternUtils mLockPatternUtils;
    private LockscreenGestureLogger mLockscreenGestureLogger;
    private String mMessageToShowOnScreenOn;
    private boolean mPowerCharged;
    private boolean mPowerPluggedIn;
    private boolean mPowerPluggedInWired;
    private String mRestingIndication;
    private final ShadeController mShadeController;
    private final int mSlowThreshold;
    private StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
    private final StatusBarStateController mStatusBarStateController;
    private KeyguardIndicationTextView mTextView;
    private final KeyguardUpdateMonitorCallback mTickReceiver;
    private CharSequence mTransientIndication;
    private ColorStateList mTransientTextColorState;
    private final UnlockMethodCache mUnlockMethodCache;
    private KeyguardUpdateMonitorCallback mUpdateMonitorCallback;
    private final UserManager mUserManager;
    private boolean mVisible;
    private final SettableWakeLock mWakeLock;

    public KeyguardIndicationController(Context context, ViewGroup indicationArea, LockIcon lockIcon) {
        this(context, indicationArea, lockIcon, new LockPatternUtils(context), WakeLock.createPartial(context, "Doze:KeyguardIndication"), (ShadeController) Dependency.get(ShadeController.class), (AccessibilityController) Dependency.get(AccessibilityController.class), UnlockMethodCache.getInstance(context), (StatusBarStateController) Dependency.get(StatusBarStateController.class), KeyguardUpdateMonitor.getInstance(context), (DockManager) Dependency.get(DockManager.class));
    }

    @VisibleForTesting
    KeyguardIndicationController(Context context, ViewGroup indicationArea, LockIcon lockIcon, LockPatternUtils lockPatternUtils, WakeLock wakeLock, ShadeController shadeController, AccessibilityController accessibilityController, UnlockMethodCache unlockMethodCache, StatusBarStateController statusBarStateController, KeyguardUpdateMonitor keyguardUpdateMonitor, DockManager dockManager) {
        this.mLockscreenGestureLogger = new LockscreenGestureLogger();
        this.mAlignmentIndication = "";
        this.mClippingParams = new ViewClippingUtil.ClippingParameters() { // from class: com.android.systemui.statusbar.KeyguardIndicationController.1
            public boolean shouldFinish(View view) {
                return view == KeyguardIndicationController.this.mIndicationArea;
            }
        };
        this.mTickReceiver = new KeyguardUpdateMonitorCallback() { // from class: com.android.systemui.statusbar.KeyguardIndicationController.3
            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onTimeChanged() {
                if (KeyguardIndicationController.this.mVisible) {
                    KeyguardIndicationController.this.updateIndication(false);
                }
            }
        };
        this.mHandler = new Handler() { // from class: com.android.systemui.statusbar.KeyguardIndicationController.4
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    KeyguardIndicationController.this.hideTransientIndication();
                } else if (msg.what == 2) {
                    KeyguardIndicationController.this.mLockIcon.setTransientBiometricsError(false);
                } else if (msg.what == 3) {
                    KeyguardIndicationController.this.showSwipeUpToUnlock();
                }
            }
        };
        this.mContext = context;
        this.mLockIcon = lockIcon;
        this.mShadeController = shadeController;
        this.mAccessibilityController = accessibilityController;
        this.mUnlockMethodCache = unlockMethodCache;
        this.mStatusBarStateController = statusBarStateController;
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mDockManager = dockManager;
        this.mDockManager.addAlignmentStateListener(new DockManager.AlignmentStateListener() { // from class: com.android.systemui.statusbar.-$$Lambda$KeyguardIndicationController$MNRKvB1L0H3Iaik26PzOwQaf05I
            @Override // com.android.systemui.dock.DockManager.AlignmentStateListener
            public final void onAlignmentStateChanged(int i) {
                KeyguardIndicationController.this.lambda$new$1$KeyguardIndicationController(i);
            }
        });
        LockIcon lockIcon2 = this.mLockIcon;
        if (lockIcon2 != null) {
            lockIcon2.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.android.systemui.statusbar.-$$Lambda$KeyguardIndicationController$bqGTofRbajWF7T9LSeA5X_gxSW8
                @Override // android.view.View.OnLongClickListener
                public final boolean onLongClick(View view) {
                    boolean handleLockLongClick;
                    handleLockLongClick = KeyguardIndicationController.this.handleLockLongClick(view);
                    return handleLockLongClick;
                }
            });
            this.mLockIcon.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.statusbar.-$$Lambda$KeyguardIndicationController$KgoVbt1hJQ-ysK1ds1xLhviRDjE
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    KeyguardIndicationController.this.handleLockClick(view);
                }
            });
        }
        this.mWakeLock = new SettableWakeLock(wakeLock, TAG);
        this.mLockPatternUtils = lockPatternUtils;
        Resources res = context.getResources();
        this.mSlowThreshold = res.getInteger(R.integer.config_chargingSlowlyThreshold);
        this.mFastThreshold = res.getInteger(R.integer.config_chargingFastThreshold);
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
        this.mBatteryInfo = IBatteryStats.Stub.asInterface(ServiceManager.getService("batterystats"));
        this.mDevicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy");
        setIndicationArea(indicationArea);
        updateDisclosure();
        this.mKeyguardUpdateMonitor.registerCallback(getKeyguardCallback());
        this.mKeyguardUpdateMonitor.registerCallback(this.mTickReceiver);
        this.mStatusBarStateController.addCallback(this);
        this.mUnlockMethodCache.addListener(this);
    }

    public /* synthetic */ void lambda$new$1$KeyguardIndicationController(final int alignState) {
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.-$$Lambda$KeyguardIndicationController$NwxoQI6dmtDn2OcI-HNDf1DJVG0
            @Override // java.lang.Runnable
            public final void run() {
                KeyguardIndicationController.this.lambda$new$0$KeyguardIndicationController(alignState);
            }
        });
    }

    public void setIndicationArea(ViewGroup indicationArea) {
        this.mIndicationArea = indicationArea;
        this.mTextView = (KeyguardIndicationTextView) indicationArea.findViewById(R.id.keyguard_indication_text);
        KeyguardIndicationTextView keyguardIndicationTextView = this.mTextView;
        this.mInitialTextColorState = keyguardIndicationTextView != null ? keyguardIndicationTextView.getTextColors() : ColorStateList.valueOf(-1);
        this.mDisclosure = (KeyguardIndicationTextView) indicationArea.findViewById(R.id.keyguard_indication_enterprise_disclosure);
        updateIndication(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean handleLockLongClick(View view) {
        this.mLockscreenGestureLogger.write(191, 0, 0);
        showTransientIndication(R.string.keyguard_indication_trust_disabled);
        this.mKeyguardUpdateMonitor.onLockIconPressed();
        this.mLockPatternUtils.requireCredentialEntry(KeyguardUpdateMonitor.getCurrentUser());
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleLockClick(View view) {
        if (!this.mAccessibilityController.isAccessibilityEnabled()) {
            return;
        }
        this.mShadeController.animateCollapsePanels(0, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: handleAlignStateChanged */
    public void lambda$new$0$KeyguardIndicationController(int alignState) {
        String alignmentIndication = "";
        if (alignState == 1) {
            alignmentIndication = this.mContext.getResources().getString(R.string.dock_alignment_slow_charging);
        } else if (alignState == 2) {
            alignmentIndication = this.mContext.getResources().getString(R.string.dock_alignment_not_charging);
        }
        if (!alignmentIndication.equals(this.mAlignmentIndication)) {
            this.mAlignmentIndication = alignmentIndication;
            updateIndication(false);
        }
    }

    protected KeyguardUpdateMonitorCallback getKeyguardCallback() {
        if (this.mUpdateMonitorCallback == null) {
            this.mUpdateMonitorCallback = new BaseKeyguardCallback();
        }
        return this.mUpdateMonitorCallback;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDisclosure() {
        DevicePolicyManager devicePolicyManager = this.mDevicePolicyManager;
        if (devicePolicyManager == null) {
            return;
        }
        if (!this.mDozing && devicePolicyManager.isDeviceManaged()) {
            CharSequence organizationName = this.mDevicePolicyManager.getDeviceOwnerOrganizationName();
            if (organizationName != null) {
                this.mDisclosure.switchIndication(this.mContext.getResources().getString(R.string.do_disclosure_with_name, organizationName));
            } else {
                this.mDisclosure.switchIndication(R.string.do_disclosure_generic);
            }
            this.mDisclosure.setVisibility(0);
            return;
        }
        this.mDisclosure.setVisibility(8);
    }

    public void setVisible(boolean visible) {
        this.mVisible = visible;
        this.mIndicationArea.setVisibility(visible ? 0 : 8);
        if (visible) {
            if (!this.mHandler.hasMessages(1)) {
                hideTransientIndication();
            }
            updateIndication(false);
        } else if (!visible) {
            hideTransientIndication();
        }
    }

    public void setRestingIndication(String restingIndication) {
        this.mRestingIndication = restingIndication;
        updateIndication(false);
    }

    public void setUserInfoController(UserInfoController userInfoController) {
    }

    @VisibleForTesting
    String getTrustGrantedIndication() {
        return this.mContext.getString(R.string.keyguard_indication_trust_unlocked);
    }

    private String getTrustManagedIndication() {
        return null;
    }

    public void hideTransientIndicationDelayed(long delayMs) {
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(1), delayMs);
    }

    public void showTransientIndication(int transientIndication) {
        showTransientIndication(this.mContext.getResources().getString(transientIndication));
    }

    public void showTransientIndication(CharSequence transientIndication) {
        showTransientIndication(transientIndication, this.mInitialTextColorState, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showTransientIndication(CharSequence transientIndication, ColorStateList textColorState, boolean hideOnScreenOff) {
        this.mTransientIndication = transientIndication;
        this.mHideTransientMessageOnScreenOff = hideOnScreenOff && transientIndication != null;
        this.mTransientTextColorState = textColorState;
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(3);
        if (this.mDozing && !TextUtils.isEmpty(this.mTransientIndication)) {
            this.mWakeLock.setAcquired(true);
            hideTransientIndicationDelayed(5000L);
        }
        updateIndication(false);
    }

    public void hideTransientIndication() {
        if (this.mTransientIndication != null) {
            this.mTransientIndication = null;
            this.mHideTransientMessageOnScreenOff = false;
            this.mHandler.removeMessages(1);
            updateIndication(false);
        }
    }

    protected final void updateIndication(boolean animate) {
        if (TextUtils.isEmpty(this.mTransientIndication)) {
            this.mWakeLock.setAcquired(false);
        }
        if (this.mVisible) {
            if (this.mDozing) {
                this.mTextView.setTextColor(-1);
                if (!TextUtils.isEmpty(this.mTransientIndication)) {
                    this.mTextView.switchIndication(this.mTransientIndication);
                    return;
                } else if (!TextUtils.isEmpty(this.mAlignmentIndication)) {
                    this.mTextView.switchIndication(this.mAlignmentIndication);
                    this.mTextView.setTextColor(Utils.getColorError(this.mContext));
                    return;
                } else if (this.mPowerPluggedIn) {
                    String indication = computePowerIndication();
                    if (animate) {
                        animateText(this.mTextView, indication);
                        return;
                    } else {
                        this.mTextView.switchIndication(indication);
                        return;
                    }
                } else {
                    String percentage = NumberFormat.getPercentInstance().format(this.mBatteryLevel / 100.0f);
                    this.mTextView.switchIndication(percentage);
                    return;
                }
            }
            int userId = KeyguardUpdateMonitor.getCurrentUser();
            String trustGrantedIndication = getTrustGrantedIndication();
            String trustManagedIndication = getTrustManagedIndication();
            if (!this.mUserManager.isUserUnlocked(userId)) {
                this.mTextView.switchIndication(17040293);
                this.mTextView.setTextColor(this.mInitialTextColorState);
            } else if (!TextUtils.isEmpty(this.mTransientIndication)) {
                this.mTextView.switchIndication(this.mTransientIndication);
                this.mTextView.setTextColor(this.mTransientTextColorState);
            } else if (!TextUtils.isEmpty(trustGrantedIndication) && this.mKeyguardUpdateMonitor.getUserHasTrust(userId)) {
                this.mTextView.switchIndication(trustGrantedIndication);
                this.mTextView.setTextColor(this.mInitialTextColorState);
            } else if (!TextUtils.isEmpty(this.mAlignmentIndication)) {
                this.mTextView.switchIndication(this.mAlignmentIndication);
                this.mTextView.setTextColor(Utils.getColorError(this.mContext));
            } else if (this.mPowerPluggedIn) {
                String indication2 = computePowerIndication();
                this.mTextView.setTextColor(this.mInitialTextColorState);
                if (animate) {
                    animateText(this.mTextView, indication2);
                } else {
                    this.mTextView.switchIndication(indication2);
                }
            } else if (!TextUtils.isEmpty(trustManagedIndication) && this.mKeyguardUpdateMonitor.getUserTrustIsManaged(userId) && !this.mKeyguardUpdateMonitor.getUserHasTrust(userId)) {
                this.mTextView.switchIndication(trustManagedIndication);
                this.mTextView.setTextColor(this.mInitialTextColorState);
            } else {
                this.mTextView.switchIndication(this.mRestingIndication);
                this.mTextView.setTextColor(this.mInitialTextColorState);
            }
        }
    }

    private void animateText(final KeyguardIndicationTextView textView, final String indication) {
        int yTranslation = this.mContext.getResources().getInteger(R.integer.wired_charging_keyguard_text_animation_distance);
        int animateUpDuration = this.mContext.getResources().getInteger(R.integer.wired_charging_keyguard_text_animation_duration_up);
        final int animateDownDuration = this.mContext.getResources().getInteger(R.integer.wired_charging_keyguard_text_animation_duration_down);
        textView.animate().cancel();
        ViewClippingUtil.setClippingDeactivated(textView, true, this.mClippingParams);
        textView.animate().translationYBy(yTranslation).setInterpolator(Interpolators.LINEAR).setDuration(animateUpDuration).setListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.KeyguardIndicationController.2
            private boolean mCancelled;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                textView.switchIndication(indication);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                textView.setTranslationY(0.0f);
                this.mCancelled = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (this.mCancelled) {
                    ViewClippingUtil.setClippingDeactivated(textView, false, KeyguardIndicationController.this.mClippingParams);
                } else {
                    textView.animate().setDuration(animateDownDuration).setInterpolator(Interpolators.BOUNCE).translationY(0.0f).setListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.KeyguardIndicationController.2.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation2) {
                            textView.setTranslationY(0.0f);
                            ViewClippingUtil.setClippingDeactivated(textView, false, KeyguardIndicationController.this.mClippingParams);
                        }
                    });
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String computePowerIndication() {
        int chargingId;
        if (this.mPowerCharged) {
            return this.mContext.getResources().getString(R.string.keyguard_charged);
        }
        long chargingTimeRemaining = 0;
        try {
            chargingTimeRemaining = this.mBatteryInfo.computeChargeTimeRemaining();
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling IBatteryStats: ", e);
        }
        boolean hasChargingTime = chargingTimeRemaining > 0;
        if (this.mPowerPluggedInWired) {
            int i = this.mChargingSpeed;
            if (i != 0) {
                if (i == 2) {
                    if (hasChargingTime) {
                        chargingId = R.string.keyguard_indication_charging_time_fast;
                    } else {
                        chargingId = R.string.keyguard_plugged_in_charging_fast;
                    }
                } else if (hasChargingTime) {
                    chargingId = R.string.keyguard_indication_charging_time;
                } else {
                    chargingId = R.string.keyguard_plugged_in;
                }
            } else if (hasChargingTime) {
                chargingId = R.string.keyguard_indication_charging_time_slowly;
            } else {
                chargingId = R.string.keyguard_plugged_in_charging_slowly;
            }
        } else if (hasChargingTime) {
            chargingId = R.string.keyguard_indication_charging_time_wireless;
        } else {
            chargingId = R.string.keyguard_plugged_in_wireless;
        }
        String percentage = NumberFormat.getPercentInstance().format(this.mBatteryLevel / 100.0f);
        if (hasChargingTime) {
            String chargingTimeFormatted = Formatter.formatShortElapsedTimeRoundingUpToMinutes(this.mContext, chargingTimeRemaining);
            try {
                return this.mContext.getResources().getString(chargingId, chargingTimeFormatted, percentage);
            } catch (IllegalFormatConversionException e2) {
                return this.mContext.getResources().getString(chargingId, chargingTimeFormatted);
            }
        }
        try {
            return this.mContext.getResources().getString(chargingId, percentage);
        } catch (IllegalFormatConversionException e3) {
            return this.mContext.getResources().getString(chargingId);
        }
    }

    public void setStatusBarKeyguardViewManager(StatusBarKeyguardViewManager statusBarKeyguardViewManager) {
        this.mStatusBarKeyguardViewManager = statusBarKeyguardViewManager;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showSwipeUpToUnlock() {
        if (this.mDozing) {
            return;
        }
        if (this.mStatusBarKeyguardViewManager.isBouncerShowing()) {
            String message = this.mContext.getString(R.string.keyguard_retry);
            this.mStatusBarKeyguardViewManager.showBouncerMessage(message, this.mInitialTextColorState);
        } else if (this.mKeyguardUpdateMonitor.isScreenOn()) {
            showTransientIndication(this.mContext.getString(R.string.keyguard_unlock), this.mInitialTextColorState, true);
            hideTransientIndicationDelayed(5000L);
        }
    }

    public void setDozing(boolean dozing) {
        if (this.mDozing == dozing) {
            return;
        }
        this.mDozing = dozing;
        if (this.mHideTransientMessageOnScreenOff && this.mDozing) {
            hideTransientIndication();
        } else {
            updateIndication(false);
        }
        updateDisclosure();
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("KeyguardIndicationController:");
        pw.println("  mTransientTextColorState: " + this.mTransientTextColorState);
        pw.println("  mInitialTextColorState: " + this.mInitialTextColorState);
        pw.println("  mPowerPluggedInWired: " + this.mPowerPluggedInWired);
        pw.println("  mPowerPluggedIn: " + this.mPowerPluggedIn);
        pw.println("  mPowerCharged: " + this.mPowerCharged);
        pw.println("  mChargingSpeed: " + this.mChargingSpeed);
        pw.println("  mChargingWattage: " + this.mChargingWattage);
        pw.println("  mMessageToShowOnScreenOn: " + this.mMessageToShowOnScreenOn);
        pw.println("  mDozing: " + this.mDozing);
        pw.println("  mBatteryLevel: " + this.mBatteryLevel);
        StringBuilder sb = new StringBuilder();
        sb.append("  mTextView.getText(): ");
        KeyguardIndicationTextView keyguardIndicationTextView = this.mTextView;
        sb.append((Object) (keyguardIndicationTextView == null ? null : keyguardIndicationTextView.getText()));
        pw.println(sb.toString());
        pw.println("  computePowerIndication(): " + computePowerIndication());
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onStateChanged(int newState) {
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onDozingChanged(boolean isDozing) {
        setDozing(isDozing);
    }

    @Override // com.android.systemui.statusbar.phone.UnlockMethodCache.OnUnlockMethodChangedListener
    public void onUnlockMethodStateChanged() {
        updateIndication(!this.mDozing);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes21.dex */
    public class BaseKeyguardCallback extends KeyguardUpdateMonitorCallback {
        public static final int HIDE_DELAY_MS = 5000;

        protected BaseKeyguardCallback() {
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onRefreshBatteryInfo(KeyguardUpdateMonitor.BatteryStatus status) {
            boolean z = false;
            boolean isChargingOrFull = status.status == 2 || status.status == 5;
            boolean wasPluggedIn = KeyguardIndicationController.this.mPowerPluggedIn;
            KeyguardIndicationController.this.mPowerPluggedInWired = status.isPluggedInWired() && isChargingOrFull;
            KeyguardIndicationController.this.mPowerPluggedIn = status.isPluggedIn() && isChargingOrFull;
            KeyguardIndicationController.this.mPowerCharged = status.isCharged();
            KeyguardIndicationController.this.mChargingWattage = status.maxChargingWattage;
            KeyguardIndicationController keyguardIndicationController = KeyguardIndicationController.this;
            keyguardIndicationController.mChargingSpeed = status.getChargingSpeed(keyguardIndicationController.mSlowThreshold, KeyguardIndicationController.this.mFastThreshold);
            KeyguardIndicationController.this.mBatteryLevel = status.level;
            KeyguardIndicationController keyguardIndicationController2 = KeyguardIndicationController.this;
            if (!wasPluggedIn && keyguardIndicationController2.mPowerPluggedInWired) {
                z = true;
            }
            keyguardIndicationController2.updateIndication(z);
            if (KeyguardIndicationController.this.mDozing) {
                if (!wasPluggedIn && KeyguardIndicationController.this.mPowerPluggedIn) {
                    KeyguardIndicationController keyguardIndicationController3 = KeyguardIndicationController.this;
                    keyguardIndicationController3.showTransientIndication(keyguardIndicationController3.computePowerIndication());
                    KeyguardIndicationController.this.hideTransientIndicationDelayed(5000L);
                } else if (wasPluggedIn && !KeyguardIndicationController.this.mPowerPluggedIn) {
                    KeyguardIndicationController.this.hideTransientIndication();
                }
            }
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onKeyguardVisibilityChanged(boolean showing) {
            if (showing) {
                KeyguardIndicationController.this.updateDisclosure();
            }
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onBiometricHelp(int msgId, String helpString, BiometricSourceType biometricSourceType) {
            if (!KeyguardIndicationController.this.mKeyguardUpdateMonitor.isUnlockingWithBiometricAllowed()) {
                return;
            }
            boolean showSwipeToUnlock = msgId == -2;
            if (!KeyguardIndicationController.this.mStatusBarKeyguardViewManager.isBouncerShowing()) {
                if (KeyguardIndicationController.this.mKeyguardUpdateMonitor.isScreenOn()) {
                    KeyguardIndicationController keyguardIndicationController = KeyguardIndicationController.this;
                    keyguardIndicationController.showTransientIndication(helpString, keyguardIndicationController.mInitialTextColorState, showSwipeToUnlock);
                    if (!showSwipeToUnlock) {
                        KeyguardIndicationController.this.hideTransientIndicationDelayed(KeyguardIndicationController.TRANSIENT_BIOMETRIC_ERROR_TIMEOUT);
                    }
                }
            } else {
                KeyguardIndicationController.this.mStatusBarKeyguardViewManager.showBouncerMessage(helpString, KeyguardIndicationController.this.mInitialTextColorState);
            }
            if (showSwipeToUnlock) {
                KeyguardIndicationController.this.mHandler.sendMessageDelayed(KeyguardIndicationController.this.mHandler.obtainMessage(3), KeyguardIndicationController.TRANSIENT_BIOMETRIC_ERROR_TIMEOUT);
            }
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onBiometricError(int msgId, String errString, BiometricSourceType biometricSourceType) {
            if (shouldSuppressBiometricError(msgId, biometricSourceType, KeyguardIndicationController.this.mKeyguardUpdateMonitor)) {
                return;
            }
            animatePadlockError();
            if (msgId == 3) {
                KeyguardIndicationController.this.showSwipeUpToUnlock();
            } else if (KeyguardIndicationController.this.mStatusBarKeyguardViewManager.isBouncerShowing()) {
                KeyguardIndicationController.this.mStatusBarKeyguardViewManager.showBouncerMessage(errString, KeyguardIndicationController.this.mInitialTextColorState);
            } else if (!KeyguardIndicationController.this.mKeyguardUpdateMonitor.isScreenOn()) {
                KeyguardIndicationController.this.mMessageToShowOnScreenOn = errString;
            } else {
                KeyguardIndicationController.this.showTransientIndication(errString);
                KeyguardIndicationController.this.hideTransientIndicationDelayed(5000L);
            }
        }

        private void animatePadlockError() {
            KeyguardIndicationController.this.mLockIcon.setTransientBiometricsError(true);
            KeyguardIndicationController.this.mHandler.removeMessages(2);
            KeyguardIndicationController.this.mHandler.sendMessageDelayed(KeyguardIndicationController.this.mHandler.obtainMessage(2), KeyguardIndicationController.TRANSIENT_BIOMETRIC_ERROR_TIMEOUT);
        }

        private boolean shouldSuppressBiometricError(int msgId, BiometricSourceType biometricSourceType, KeyguardUpdateMonitor updateMonitor) {
            if (biometricSourceType == BiometricSourceType.FINGERPRINT) {
                return shouldSuppressFingerprintError(msgId, updateMonitor);
            }
            if (biometricSourceType == BiometricSourceType.FACE) {
                return shouldSuppressFaceError(msgId, updateMonitor);
            }
            return false;
        }

        private boolean shouldSuppressFingerprintError(int msgId, KeyguardUpdateMonitor updateMonitor) {
            return !(updateMonitor.isUnlockingWithBiometricAllowed() || msgId == 9) || msgId == 5;
        }

        private boolean shouldSuppressFaceError(int msgId, KeyguardUpdateMonitor updateMonitor) {
            return !(updateMonitor.isUnlockingWithBiometricAllowed() || msgId == 9) || msgId == 5;
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onTrustAgentErrorMessage(CharSequence message) {
            KeyguardIndicationController keyguardIndicationController = KeyguardIndicationController.this;
            keyguardIndicationController.showTransientIndication(message, Utils.getColorError(keyguardIndicationController.mContext), false);
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onScreenTurnedOn() {
            if (KeyguardIndicationController.this.mMessageToShowOnScreenOn != null) {
                KeyguardIndicationController keyguardIndicationController = KeyguardIndicationController.this;
                keyguardIndicationController.showTransientIndication(keyguardIndicationController.mMessageToShowOnScreenOn, Utils.getColorError(KeyguardIndicationController.this.mContext), false);
                KeyguardIndicationController.this.hideTransientIndicationDelayed(5000L);
                KeyguardIndicationController.this.mMessageToShowOnScreenOn = null;
            }
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onBiometricRunningStateChanged(boolean running, BiometricSourceType biometricSourceType) {
            if (running) {
                KeyguardIndicationController.this.hideTransientIndication();
                KeyguardIndicationController.this.mMessageToShowOnScreenOn = null;
            }
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onBiometricAuthenticated(int userId, BiometricSourceType biometricSourceType) {
            super.onBiometricAuthenticated(userId, biometricSourceType);
            KeyguardIndicationController.this.mHandler.sendEmptyMessage(1);
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onUserUnlocked() {
            if (KeyguardIndicationController.this.mVisible) {
                KeyguardIndicationController.this.updateIndication(false);
            }
        }
    }
}
