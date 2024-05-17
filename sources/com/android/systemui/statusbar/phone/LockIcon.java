package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.biometrics.BiometricSourceType;
import android.os.Trace;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.Nullable;
import com.android.internal.graphics.ColorUtils;
import com.android.internal.telephony.IccCardConstants;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.dock.DockManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.KeyguardAffordanceView;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.phone.UnlockMethodCache;
import com.android.systemui.statusbar.policy.AccessibilityController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import com.android.systemui.statusbar.policy.UserInfoController;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Inject;
import javax.inject.Named;
/* loaded from: classes21.dex */
public class LockIcon extends KeyguardAffordanceView implements UserInfoController.OnUserInfoChangedListener, StatusBarStateController.StateListener, ConfigurationController.ConfigurationListener, UnlockMethodCache.OnUnlockMethodChangedListener, NotificationWakeUpCoordinator.WakeUpListener, ViewTreeObserver.OnPreDrawListener, OnHeadsUpChangedListener {
    private static final int ERROR = 0;
    private static final int LOCK = 2;
    private static final int[][] LOCK_ANIM_RES_IDS = {new int[]{R.anim.lock_to_error, R.anim.lock_unlock, R.anim.lock_lock, R.anim.lock_scanning}, new int[]{R.anim.lock_to_error_circular, R.anim.lock_unlock_circular, R.anim.lock_lock_circular, R.anim.lock_scanning_circular}, new int[]{R.anim.lock_to_error_filled, R.anim.lock_unlock_filled, R.anim.lock_lock_filled, R.anim.lock_scanning_filled}, new int[]{R.anim.lock_to_error_rounded, R.anim.lock_unlock_rounded, R.anim.lock_lock_rounded, R.anim.lock_scanning_rounded}};
    private static final int SCANNING = 3;
    private static final int STATE_BIOMETRICS_ERROR = 3;
    private static final int STATE_LOCKED = 0;
    private static final int STATE_LOCK_OPEN = 1;
    private static final int STATE_SCANNING_FACE = 2;
    private static final int UNLOCK = 1;
    private final AccessibilityController mAccessibilityController;
    private boolean mBlockUpdates;
    private boolean mBouncerShowingScrimmed;
    private final KeyguardBypassController mBypassController;
    private final ConfigurationController mConfigurationController;
    private int mDensity;
    private final DockManager.DockEventListener mDockEventListener;
    private final DockManager mDockManager;
    private boolean mDocked;
    private float mDozeAmount;
    private boolean mDozing;
    private boolean mForceUpdate;
    private final HeadsUpManagerPhone mHeadsUpManager;
    private int mIconColor;
    private boolean mIsFaceUnlockState;
    private boolean mKeyguardJustShown;
    private final KeyguardMonitor mKeyguardMonitor;
    private final KeyguardMonitor.Callback mKeyguardMonitorCallback;
    private boolean mKeyguardShowing;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private int mLastState;
    private boolean mPulsing;
    private boolean mShowingLaunchAffordance;
    private boolean mSimLocked;
    private final StatusBarStateController mStatusBarStateController;
    private boolean mTransientBiometricsError;
    private final UnlockMethodCache mUnlockMethodCache;
    private final KeyguardUpdateMonitorCallback mUpdateMonitorCallback;
    private boolean mUpdatePending;
    private boolean mWakeAndUnlockRunning;
    private final NotificationWakeUpCoordinator mWakeUpCoordinator;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes21.dex */
    @interface LockAnimIndex {
    }

    @Inject
    public LockIcon(@Named("view_context") Context context, AttributeSet attrs, StatusBarStateController statusBarStateController, ConfigurationController configurationController, AccessibilityController accessibilityController, KeyguardBypassController bypassController, NotificationWakeUpCoordinator wakeUpCoordinator, KeyguardMonitor keyguardMonitor, @Nullable DockManager dockManager, HeadsUpManagerPhone headsUpManager) {
        super(context, attrs);
        this.mLastState = 0;
        this.mKeyguardMonitorCallback = new KeyguardMonitor.Callback() { // from class: com.android.systemui.statusbar.phone.LockIcon.1
            @Override // com.android.systemui.statusbar.policy.KeyguardMonitor.Callback
            public void onKeyguardShowingChanged() {
                boolean force = false;
                boolean wasShowing = LockIcon.this.mKeyguardShowing;
                LockIcon lockIcon = LockIcon.this;
                lockIcon.mKeyguardShowing = lockIcon.mKeyguardMonitor.isShowing();
                if (!wasShowing && LockIcon.this.mKeyguardShowing && LockIcon.this.mBlockUpdates) {
                    LockIcon.this.mBlockUpdates = false;
                    force = true;
                }
                if (!wasShowing && LockIcon.this.mKeyguardShowing) {
                    LockIcon.this.mKeyguardJustShown = true;
                }
                LockIcon.this.update(force);
            }

            @Override // com.android.systemui.statusbar.policy.KeyguardMonitor.Callback
            public void onKeyguardFadingAwayChanged() {
                if (!LockIcon.this.mKeyguardMonitor.isKeyguardFadingAway() && LockIcon.this.mBlockUpdates) {
                    LockIcon.this.mBlockUpdates = false;
                    LockIcon.this.update(true);
                }
            }
        };
        this.mDockEventListener = new DockManager.DockEventListener() { // from class: com.android.systemui.statusbar.phone.LockIcon.2
            @Override // com.android.systemui.dock.DockManager.DockEventListener
            public void onEvent(int event) {
                boolean docked = true;
                if (event != 1 && event != 2) {
                    docked = false;
                }
                if (docked != LockIcon.this.mDocked) {
                    LockIcon.this.mDocked = docked;
                    LockIcon.this.update();
                }
            }
        };
        this.mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() { // from class: com.android.systemui.statusbar.phone.LockIcon.3
            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onSimStateChanged(int subId, int slotId, IccCardConstants.State simState) {
                LockIcon lockIcon = LockIcon.this;
                lockIcon.mSimLocked = lockIcon.mKeyguardUpdateMonitor.isSimPinSecure();
                LockIcon.this.update();
            }

            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onKeyguardVisibilityChanged(boolean showing) {
                LockIcon.this.update();
            }

            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onBiometricRunningStateChanged(boolean running, BiometricSourceType biometricSourceType) {
                LockIcon.this.update();
            }

            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onStrongAuthStateChanged(int userId) {
                LockIcon.this.update();
            }
        };
        this.mContext = context;
        this.mUnlockMethodCache = UnlockMethodCache.getInstance(context);
        this.mKeyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(this.mContext);
        this.mAccessibilityController = accessibilityController;
        this.mConfigurationController = configurationController;
        this.mStatusBarStateController = statusBarStateController;
        this.mBypassController = bypassController;
        this.mWakeUpCoordinator = wakeUpCoordinator;
        this.mKeyguardMonitor = keyguardMonitor;
        this.mDockManager = dockManager;
        this.mHeadsUpManager = headsUpManager;
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mStatusBarStateController.addCallback(this);
        this.mConfigurationController.addCallback(this);
        this.mKeyguardMonitor.addCallback(this.mKeyguardMonitorCallback);
        this.mKeyguardUpdateMonitor.registerCallback(this.mUpdateMonitorCallback);
        this.mUnlockMethodCache.addListener(this);
        this.mWakeUpCoordinator.addListener(this);
        this.mSimLocked = this.mKeyguardUpdateMonitor.isSimPinSecure();
        DockManager dockManager = this.mDockManager;
        if (dockManager != null) {
            dockManager.addListener(this.mDockEventListener);
        }
        onThemeChanged();
        update();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mStatusBarStateController.removeCallback(this);
        this.mConfigurationController.removeCallback(this);
        this.mKeyguardUpdateMonitor.removeCallback(this.mUpdateMonitorCallback);
        this.mKeyguardMonitor.removeCallback(this.mKeyguardMonitorCallback);
        this.mWakeUpCoordinator.removeListener(this);
        this.mUnlockMethodCache.removeListener(this);
        DockManager dockManager = this.mDockManager;
        if (dockManager != null) {
            dockManager.removeListener(this.mDockEventListener);
        }
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onThemeChanged() {
        TypedArray typedArray = this.mContext.getTheme().obtainStyledAttributes(null, new int[]{R.attr.wallpaperTextColor}, 0, 0);
        this.mIconColor = typedArray.getColor(0, -1);
        typedArray.recycle();
        updateDarkTint();
    }

    @Override // com.android.systemui.statusbar.policy.UserInfoController.OnUserInfoChangedListener
    public void onUserInfoChanged(String name, Drawable picture, String userAccount) {
        update();
    }

    public void setTransientBiometricsError(boolean transientBiometricsError) {
        this.mTransientBiometricsError = transientBiometricsError;
        update();
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int density = newConfig.densityDpi;
        if (density != this.mDensity) {
            this.mDensity = density;
            update();
        }
    }

    public void update() {
        update(false);
    }

    public void update(boolean force) {
        if (force) {
            this.mForceUpdate = true;
        }
        if (!this.mUpdatePending) {
            this.mUpdatePending = true;
            getViewTreeObserver().addOnPreDrawListener(this);
        }
    }

    @Override // android.view.ViewTreeObserver.OnPreDrawListener
    public boolean onPreDraw() {
        boolean z;
        boolean shouldUpdate;
        boolean isAnim;
        final AnimatedVectorDrawable animation;
        this.mUpdatePending = false;
        getViewTreeObserver().removeOnPreDrawListener(this);
        final int state = getState();
        int lastState = this.mLastState;
        boolean keyguardJustShown = this.mKeyguardJustShown;
        if (state != 2) {
            z = false;
        } else {
            z = true;
        }
        this.mIsFaceUnlockState = z;
        this.mLastState = state;
        this.mKeyguardJustShown = false;
        if (lastState == state && !this.mForceUpdate) {
            shouldUpdate = false;
        } else {
            shouldUpdate = true;
        }
        if (this.mBlockUpdates && canBlockUpdates()) {
            shouldUpdate = false;
        }
        if (shouldUpdate) {
            this.mForceUpdate = false;
            final int lockAnimIndex = getAnimationIndexForTransition(lastState, state, this.mPulsing, this.mDozing, keyguardJustShown);
            if (lockAnimIndex == -1) {
                isAnim = false;
            } else {
                isAnim = true;
            }
            int iconRes = isAnim ? getThemedAnimationResId(lockAnimIndex) : getIconForState(state);
            Drawable icon = this.mContext.getDrawable(iconRes);
            if (icon instanceof AnimatedVectorDrawable) {
                animation = (AnimatedVectorDrawable) icon;
            } else {
                animation = null;
            }
            setImageDrawable(icon, false);
            if (this.mIsFaceUnlockState) {
                announceForAccessibility(getContext().getString(R.string.accessibility_scanning_face));
            }
            if (animation != null && isAnim) {
                animation.forceAnimationOnUI();
                animation.clearAnimationCallbacks();
                animation.registerAnimationCallback(new Animatable2.AnimationCallback() { // from class: com.android.systemui.statusbar.phone.LockIcon.4
                    @Override // android.graphics.drawable.Animatable2.AnimationCallback
                    public void onAnimationEnd(Drawable drawable) {
                        if (LockIcon.this.getDrawable() == animation && state == LockIcon.this.getState() && LockIcon.this.doesAnimationLoop(lockAnimIndex)) {
                            animation.start();
                        } else {
                            Trace.endAsyncSection("LockIcon#Animation", state);
                        }
                    }
                });
                Trace.beginAsyncSection("LockIcon#Animation", state);
                animation.start();
            }
        }
        updateDarkTint();
        updateIconVisibility();
        updateClickability();
        return true;
    }

    private boolean updateIconVisibility() {
        boolean onAodNotPulsingOrDocked = this.mDozing && (!this.mPulsing || this.mDocked);
        boolean invisible = onAodNotPulsingOrDocked || this.mWakeAndUnlockRunning || this.mShowingLaunchAffordance;
        if (this.mBypassController.getBypassEnabled() && !this.mBouncerShowingScrimmed && ((this.mHeadsUpManager.isHeadsUpGoingAway() || this.mHeadsUpManager.hasPinnedHeadsUp() || this.mStatusBarStateController.getState() == 1) && !this.mWakeUpCoordinator.getNotificationsFullyHidden())) {
            invisible = true;
        }
        boolean wasInvisible = getVisibility() == 4;
        if (invisible != wasInvisible) {
            setVisibility(invisible ? 4 : 0);
            animate().cancel();
            if (!invisible) {
                setScaleX(0.0f);
                setScaleY(0.0f);
                animate().setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN).scaleX(1.0f).scaleY(1.0f).withLayer().setDuration(233L).start();
            }
            return true;
        }
        return false;
    }

    private boolean canBlockUpdates() {
        return this.mKeyguardShowing || this.mKeyguardMonitor.isKeyguardFadingAway();
    }

    private void updateClickability() {
        if (this.mAccessibilityController == null) {
            return;
        }
        boolean z = true;
        boolean canLock = this.mUnlockMethodCache.isMethodSecure() && this.mUnlockMethodCache.canSkipBouncer();
        boolean clickToUnlock = this.mAccessibilityController.isAccessibilityEnabled();
        setClickable(clickToUnlock);
        if (!canLock || clickToUnlock) {
            z = false;
        }
        setLongClickable(z);
        setFocusable(this.mAccessibilityController.isAccessibilityEnabled());
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        boolean fingerprintRunning = this.mKeyguardUpdateMonitor.isFingerprintDetectionRunning();
        boolean unlockingAllowed = this.mKeyguardUpdateMonitor.isUnlockingWithBiometricAllowed();
        if (fingerprintRunning && unlockingAllowed) {
            AccessibilityNodeInfo.AccessibilityAction unlock = new AccessibilityNodeInfo.AccessibilityAction(16, getContext().getString(R.string.accessibility_unlock_without_fingerprint));
            info.addAction(unlock);
            info.setHintText(getContext().getString(R.string.accessibility_waiting_for_fingerprint));
        } else if (this.mIsFaceUnlockState) {
            info.setClassName(LockIcon.class.getName());
            info.setContentDescription(getContext().getString(R.string.accessibility_scanning_face));
        }
    }

    private int getIconForState(int state) {
        if (state != 0) {
            if (state == 1) {
                return 17302461;
            }
            if (state != 2 && state != 3) {
                throw new IllegalArgumentException();
            }
        }
        return 17302452;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean doesAnimationLoop(int lockAnimIndex) {
        return lockAnimIndex == 3;
    }

    private static int getAnimationIndexForTransition(int oldState, int newState, boolean pulsing, boolean dozing, boolean keyguardJustShown) {
        if (dozing && !pulsing) {
            return -1;
        }
        if (newState == 3) {
            return 0;
        }
        if (oldState == 1 || newState != 1) {
            if (oldState == 1 && newState == 0 && !keyguardJustShown) {
                return 2;
            }
            if (newState != 2) {
                return -1;
            }
            return 3;
        }
        return 1;
    }

    @Override // com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator.WakeUpListener
    public void onFullyHiddenChanged(boolean isFullyHidden) {
        if (this.mBypassController.getBypassEnabled()) {
            boolean changed = updateIconVisibility();
            if (changed) {
                update();
            }
        }
    }

    public void setBouncerShowingScrimmed(boolean bouncerShowing) {
        this.mBouncerShowingScrimmed = bouncerShowing;
        if (this.mBypassController.getBypassEnabled()) {
            update();
        }
    }

    private int getThemedAnimationResId(int lockAnimIndex) {
        String setting = TextUtils.emptyIfNull(Settings.Secure.getString(getContext().getContentResolver(), "theme_customization_overlay_packages"));
        if (setting.contains("com.android.theme.icon_pack.circular.android")) {
            return LOCK_ANIM_RES_IDS[1][lockAnimIndex];
        }
        if (setting.contains("com.android.theme.icon_pack.filled.android")) {
            return LOCK_ANIM_RES_IDS[2][lockAnimIndex];
        }
        if (setting.contains("com.android.theme.icon_pack.rounded.android")) {
            return LOCK_ANIM_RES_IDS[3][lockAnimIndex];
        }
        return LOCK_ANIM_RES_IDS[0][lockAnimIndex];
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getState() {
        KeyguardUpdateMonitor updateMonitor = KeyguardUpdateMonitor.getInstance(this.mContext);
        if ((this.mUnlockMethodCache.canSkipBouncer() || !this.mKeyguardShowing || this.mKeyguardMonitor.isKeyguardGoingAway()) && !this.mSimLocked) {
            return 1;
        }
        if (this.mTransientBiometricsError) {
            return 3;
        }
        if (updateMonitor.isFaceDetectionRunning() && !this.mPulsing) {
            return 2;
        }
        return 0;
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onDozeAmountChanged(float linear, float eased) {
        this.mDozeAmount = eased;
        updateDarkTint();
    }

    public void setPulsing(boolean pulsing) {
        this.mPulsing = pulsing;
        update();
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onDozingChanged(boolean dozing) {
        this.mDozing = dozing;
        update();
    }

    private void updateDarkTint() {
        int color = ColorUtils.blendARGB(this.mIconColor, -1, this.mDozeAmount);
        setImageTintList(ColorStateList.valueOf(color));
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onDensityOrFontScaleChanged() {
        ViewGroup.LayoutParams lp = getLayoutParams();
        if (lp == null) {
            return;
        }
        lp.width = getResources().getDimensionPixelSize(R.dimen.keyguard_lock_width);
        lp.height = getResources().getDimensionPixelSize(R.dimen.keyguard_lock_height);
        setLayoutParams(lp);
        update(true);
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onLocaleListChanged() {
        setContentDescription(getContext().getText(R.string.accessibility_unlock_button));
        update(true);
    }

    @Override // com.android.systemui.statusbar.phone.UnlockMethodCache.OnUnlockMethodChangedListener
    public void onUnlockMethodStateChanged() {
        update();
    }

    public void onBiometricAuthModeChanged(boolean wakeAndUnlock, boolean isUnlock) {
        if (wakeAndUnlock) {
            this.mWakeAndUnlockRunning = true;
        }
        if (isUnlock && this.mBypassController.getBypassEnabled() && canBlockUpdates()) {
            this.mBlockUpdates = true;
        }
        update();
    }

    public void onShowingLaunchAffordanceChanged(boolean showing) {
        this.mShowingLaunchAffordance = showing;
        update();
    }

    public void onScrimVisibilityChanged(int scrimsVisible) {
        if (this.mWakeAndUnlockRunning && scrimsVisible == 0) {
            this.mWakeAndUnlockRunning = false;
            update();
        }
    }
}
