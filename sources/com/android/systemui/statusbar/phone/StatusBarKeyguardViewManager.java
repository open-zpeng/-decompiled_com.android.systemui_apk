package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.StatsLog;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewRootImpl;
import android.view.WindowManagerGlobal;
import androidx.annotation.VisibleForTesting;
import com.android.internal.util.LatencyTracker;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.keyguard.ViewMediatorCallback;
import com.android.systemui.DejankUtils;
import com.android.systemui.Dependency;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.dock.DockManager;
import com.android.systemui.keyguard.DismissCallbackRegistry;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.notification.ViewGroupFadeHelper;
import com.android.systemui.statusbar.phone.KeyguardBouncer;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.statusbar.policy.KeyguardMonitorImpl;
import java.io.PrintWriter;
import java.util.ArrayList;
/* loaded from: classes21.dex */
public class StatusBarKeyguardViewManager implements RemoteInputController.Callback, StatusBarStateController.StateListener, ConfigurationController.ConfigurationListener, PanelExpansionListener, NavigationModeController.ModeChangedListener {
    private static final long HIDE_TIMING_CORRECTION_MS = -48;
    private static final long KEYGUARD_DISMISS_DURATION_LOCKED = 2000;
    private static final long NAV_BAR_SHOW_DELAY_BOUNCER = 320;
    private static String TAG = "StatusBarKeyguardViewManager";
    private static final long WAKE_AND_UNLOCK_SCRIM_FADEOUT_DURATION_MS = 200;
    private ActivityStarter.OnDismissAction mAfterKeyguardGoneAction;
    private BiometricUnlockController mBiometricUnlockController;
    protected KeyguardBouncer mBouncer;
    private KeyguardBypassController mBypassController;
    private ViewGroup mContainer;
    protected final Context mContext;
    private final DockManager mDockManager;
    private boolean mDozing;
    private boolean mGesturalNav;
    private boolean mGoingToSleepVisibleNotOccluded;
    private boolean mIsDocked;
    private Runnable mKeyguardGoneCancelAction;
    private int mLastBiometricMode;
    private boolean mLastBouncerDismissible;
    private boolean mLastBouncerShowing;
    private boolean mLastDozing;
    private boolean mLastGesturalNav;
    private boolean mLastIsDocked;
    private boolean mLastLockVisible;
    protected boolean mLastOccluded;
    private boolean mLastPulsing;
    protected boolean mLastRemoteInputActive;
    protected boolean mLastShowing;
    private ViewGroup mLockIconContainer;
    protected LockPatternUtils mLockPatternUtils;
    private View mNotificationContainer;
    private NotificationPanelView mNotificationPanelView;
    protected boolean mOccluded;
    private DismissWithActionRequest mPendingWakeupAction;
    private boolean mPulsing;
    protected boolean mRemoteInputActive;
    protected boolean mShowing;
    protected StatusBar mStatusBar;
    protected ViewMediatorCallback mViewMediatorCallback;
    private final KeyguardBouncer.BouncerExpansionCallback mExpansionCallback = new KeyguardBouncer.BouncerExpansionCallback() { // from class: com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager.1
        @Override // com.android.systemui.statusbar.phone.KeyguardBouncer.BouncerExpansionCallback
        public void onFullyShown() {
            StatusBarKeyguardViewManager.this.updateStates();
            StatusBarKeyguardViewManager.this.mStatusBar.wakeUpIfDozing(SystemClock.uptimeMillis(), StatusBarKeyguardViewManager.this.mContainer, "BOUNCER_VISIBLE");
            StatusBarKeyguardViewManager.this.updateLockIcon();
        }

        @Override // com.android.systemui.statusbar.phone.KeyguardBouncer.BouncerExpansionCallback
        public void onStartingToHide() {
            StatusBarKeyguardViewManager.this.updateStates();
        }

        @Override // com.android.systemui.statusbar.phone.KeyguardBouncer.BouncerExpansionCallback
        public void onStartingToShow() {
            StatusBarKeyguardViewManager.this.updateLockIcon();
        }

        @Override // com.android.systemui.statusbar.phone.KeyguardBouncer.BouncerExpansionCallback
        public void onFullyHidden() {
            StatusBarKeyguardViewManager.this.updateStates();
            StatusBarKeyguardViewManager.this.updateLockIcon();
        }
    };
    private final DockManager.DockEventListener mDockEventListener = new DockManager.DockEventListener() { // from class: com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager.2
        @Override // com.android.systemui.dock.DockManager.DockEventListener
        public void onEvent(int event) {
            boolean isDocked = StatusBarKeyguardViewManager.this.mDockManager.isDocked();
            if (isDocked != StatusBarKeyguardViewManager.this.mIsDocked) {
                StatusBarKeyguardViewManager.this.mIsDocked = isDocked;
                StatusBarKeyguardViewManager.this.updateStates();
            }
        }
    };
    protected boolean mFirstUpdate = true;
    private final ArrayList<Runnable> mAfterKeyguardGoneRunnables = new ArrayList<>();
    private final KeyguardMonitorImpl mKeyguardMonitor = (KeyguardMonitorImpl) Dependency.get(KeyguardMonitor.class);
    private final NotificationMediaManager mMediaManager = (NotificationMediaManager) Dependency.get(NotificationMediaManager.class);
    private final SysuiStatusBarStateController mStatusBarStateController = (SysuiStatusBarStateController) Dependency.get(StatusBarStateController.class);
    private final KeyguardUpdateMonitorCallback mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() { // from class: com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager.3
        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onEmergencyCallAction() {
            if (StatusBarKeyguardViewManager.this.mOccluded) {
                StatusBarKeyguardViewManager.this.reset(true);
            }
        }
    };
    private Runnable mMakeNavigationBarVisibleRunnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager.7
        @Override // java.lang.Runnable
        public void run() {
            StatusBarKeyguardViewManager.this.mStatusBar.getNavigationBarView().getRootView().setVisibility(0);
        }
    };
    private final StatusBarWindowController mStatusBarWindowController = (StatusBarWindowController) Dependency.get(StatusBarWindowController.class);

    public StatusBarKeyguardViewManager(Context context, ViewMediatorCallback callback, LockPatternUtils lockPatternUtils) {
        this.mContext = context;
        this.mViewMediatorCallback = callback;
        this.mLockPatternUtils = lockPatternUtils;
        KeyguardUpdateMonitor.getInstance(context).registerCallback(this.mUpdateMonitorCallback);
        this.mStatusBarStateController.addCallback(this);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this);
        this.mGesturalNav = QuickStepContract.isGesturalMode(((NavigationModeController) Dependency.get(NavigationModeController.class)).addListener(this));
        this.mDockManager = (DockManager) Dependency.get(DockManager.class);
        DockManager dockManager = this.mDockManager;
        if (dockManager != null) {
            dockManager.addListener(this.mDockEventListener);
            this.mIsDocked = this.mDockManager.isDocked();
        }
    }

    public void registerStatusBar(StatusBar statusBar, ViewGroup container, NotificationPanelView notificationPanelView, BiometricUnlockController biometricUnlockController, DismissCallbackRegistry dismissCallbackRegistry, ViewGroup lockIconContainer, View notificationContainer, KeyguardBypassController bypassController, FalsingManager falsingManager) {
        this.mStatusBar = statusBar;
        this.mContainer = container;
        this.mLockIconContainer = lockIconContainer;
        ViewGroup viewGroup = this.mLockIconContainer;
        if (viewGroup != null) {
            this.mLastLockVisible = viewGroup.getVisibility() == 0;
        }
        this.mBiometricUnlockController = biometricUnlockController;
        this.mBouncer = SystemUIFactory.getInstance().createKeyguardBouncer(this.mContext, this.mViewMediatorCallback, this.mLockPatternUtils, container, dismissCallbackRegistry, this.mExpansionCallback, falsingManager, bypassController);
        this.mNotificationPanelView = notificationPanelView;
        notificationPanelView.addExpansionListener(this);
        this.mBypassController = bypassController;
        this.mNotificationContainer = notificationContainer;
    }

    @Override // com.android.systemui.statusbar.phone.PanelExpansionListener
    public void onPanelExpansionChanged(float expansion, boolean tracking) {
        if (this.mNotificationPanelView.isUnlockHintRunning()) {
            this.mBouncer.setExpansion(1.0f);
        } else if (bouncerNeedsScrimming()) {
            this.mBouncer.setExpansion(0.0f);
        } else if (this.mShowing) {
            if (!isWakeAndUnlocking() && !this.mStatusBar.isInLaunchTransition()) {
                this.mBouncer.setExpansion(expansion);
            }
            if (expansion != 1.0f && tracking && this.mStatusBar.isKeyguardCurrentlySecure() && !this.mBouncer.isShowing() && !this.mBouncer.isAnimatingAway()) {
                this.mBouncer.show(false, false);
            }
        } else if (this.mPulsing && expansion == 0.0f) {
            this.mStatusBar.wakeUpIfDozing(SystemClock.uptimeMillis(), this.mContainer, "BOUNCER_VISIBLE");
        }
    }

    @Override // com.android.systemui.statusbar.phone.PanelExpansionListener
    public void onQsExpansionChanged(float expansion) {
        updateLockIcon();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLockIcon() {
        long duration;
        if (this.mLockIconContainer == null) {
            return;
        }
        boolean keyguardWithoutQs = this.mStatusBarStateController.getState() == 1 && !this.mNotificationPanelView.isQsExpanded();
        boolean lockVisible = (!(this.mBouncer.isShowing() || keyguardWithoutQs) || this.mBouncer.isAnimatingAway() || this.mKeyguardMonitor.isKeyguardFadingAway()) ? false : true;
        if (this.mLastLockVisible != lockVisible) {
            this.mLastLockVisible = lockVisible;
            if (lockVisible) {
                CrossFadeHelper.fadeIn(this.mLockIconContainer, 220L, 0);
                return;
            }
            if (needsBypassFading()) {
                duration = 67;
            } else {
                duration = 110;
            }
            CrossFadeHelper.fadeOut(this.mLockIconContainer, duration, 0, null);
        }
    }

    public void show(Bundle options) {
        this.mShowing = true;
        this.mStatusBarWindowController.setKeyguardShowing(true);
        KeyguardMonitorImpl keyguardMonitorImpl = this.mKeyguardMonitor;
        keyguardMonitorImpl.notifyKeyguardState(this.mShowing, keyguardMonitorImpl.isSecure(), this.mKeyguardMonitor.isOccluded());
        reset(true);
        StatsLog.write(62, 2);
    }

    protected void showBouncerOrKeyguard(boolean hideBouncerWhenShowing) {
        if (this.mBouncer.needsFullscreenBouncer() && !this.mDozing) {
            this.mStatusBar.hideKeyguard();
            this.mBouncer.show(true);
        } else {
            this.mStatusBar.showKeyguard();
            if (hideBouncerWhenShowing) {
                hideBouncer(shouldDestroyViewOnReset());
                this.mBouncer.prepare();
            }
        }
        updateStates();
    }

    protected boolean shouldDestroyViewOnReset() {
        return false;
    }

    @VisibleForTesting
    void hideBouncer(boolean destroyView) {
        if (this.mBouncer == null) {
            return;
        }
        if (this.mShowing) {
            this.mAfterKeyguardGoneAction = null;
            Runnable runnable = this.mKeyguardGoneCancelAction;
            if (runnable != null) {
                runnable.run();
                this.mKeyguardGoneCancelAction = null;
            }
        }
        this.mBouncer.hide(destroyView);
        cancelPendingWakeupAction();
    }

    public void showBouncer(boolean scrimmed) {
        if (this.mShowing && !this.mBouncer.isShowing()) {
            this.mBouncer.show(false, scrimmed);
        }
        updateStates();
    }

    public void dismissWithAction(ActivityStarter.OnDismissAction r, Runnable cancelAction, boolean afterKeyguardGone) {
        dismissWithAction(r, cancelAction, afterKeyguardGone, null);
    }

    public void dismissWithAction(ActivityStarter.OnDismissAction r, Runnable cancelAction, boolean afterKeyguardGone, String message) {
        if (this.mShowing) {
            cancelPendingWakeupAction();
            if (this.mDozing && !isWakeAndUnlocking()) {
                this.mPendingWakeupAction = new DismissWithActionRequest(r, cancelAction, afterKeyguardGone, message);
                return;
            } else if (!afterKeyguardGone) {
                this.mBouncer.showWithDismissAction(r, cancelAction);
            } else {
                this.mAfterKeyguardGoneAction = r;
                this.mKeyguardGoneCancelAction = cancelAction;
                this.mBouncer.show(false);
            }
        }
        updateStates();
    }

    private boolean isWakeAndUnlocking() {
        int mode = this.mBiometricUnlockController.getMode();
        return mode == 1 || mode == 2;
    }

    public void addAfterKeyguardGoneRunnable(Runnable runnable) {
        this.mAfterKeyguardGoneRunnables.add(runnable);
    }

    public void reset(boolean hideBouncerWhenShowing) {
        if (this.mShowing) {
            if (this.mOccluded && !this.mDozing) {
                this.mStatusBar.hideKeyguard();
                if (hideBouncerWhenShowing || this.mBouncer.needsFullscreenBouncer()) {
                    hideBouncer(false);
                }
            } else {
                showBouncerOrKeyguard(hideBouncerWhenShowing);
            }
            KeyguardUpdateMonitor.getInstance(this.mContext).sendKeyguardReset();
            updateStates();
        }
    }

    public boolean isGoingToSleepVisibleNotOccluded() {
        return this.mGoingToSleepVisibleNotOccluded;
    }

    public void onStartedGoingToSleep() {
        this.mGoingToSleepVisibleNotOccluded = isShowing() && !isOccluded();
    }

    public void onFinishedGoingToSleep() {
        this.mGoingToSleepVisibleNotOccluded = false;
        this.mBouncer.onScreenTurnedOff();
    }

    public void onStartedWakingUp() {
    }

    public void onScreenTurningOn() {
    }

    public void onScreenTurnedOn() {
    }

    @Override // com.android.systemui.statusbar.RemoteInputController.Callback
    public void onRemoteInputActive(boolean active) {
        this.mRemoteInputActive = active;
        updateStates();
    }

    private void setDozing(boolean dozing) {
        if (this.mDozing != dozing) {
            this.mDozing = dozing;
            if (dozing || this.mBouncer.needsFullscreenBouncer() || this.mOccluded) {
                reset(dozing);
            }
            updateStates();
            if (!dozing) {
                launchPendingWakeupAction();
            }
        }
    }

    public void setPulsing(boolean pulsing) {
        if (this.mPulsing != pulsing) {
            this.mPulsing = pulsing;
            updateStates();
        }
    }

    public void setNeedsInput(boolean needsInput) {
        this.mStatusBarWindowController.setKeyguardNeedsInput(needsInput);
    }

    public boolean isUnlockWithWallpaper() {
        return this.mStatusBarWindowController.isShowingWallpaper();
    }

    public void setOccluded(boolean occluded, boolean animate) {
        this.mStatusBar.setOccluded(occluded);
        boolean z = true;
        if (occluded && !this.mOccluded && this.mShowing) {
            StatsLog.write(62, 3);
            if (this.mStatusBar.isInLaunchTransition()) {
                this.mOccluded = true;
                this.mStatusBar.fadeKeyguardAfterLaunchTransition(null, new Runnable() { // from class: com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager.4
                    @Override // java.lang.Runnable
                    public void run() {
                        StatusBarKeyguardViewManager.this.mStatusBarWindowController.setKeyguardOccluded(StatusBarKeyguardViewManager.this.mOccluded);
                        StatusBarKeyguardViewManager.this.reset(true);
                    }
                });
                return;
            }
        } else if (!occluded && this.mOccluded && this.mShowing) {
            StatsLog.write(62, 2);
        }
        boolean isOccluding = !this.mOccluded && occluded;
        this.mOccluded = occluded;
        if (this.mShowing) {
            NotificationMediaManager notificationMediaManager = this.mMediaManager;
            if (!animate || occluded) {
                z = false;
            }
            notificationMediaManager.updateMediaMetaData(false, z);
        }
        this.mStatusBarWindowController.setKeyguardOccluded(occluded);
        if (!this.mDozing) {
            reset(isOccluding);
        }
        if (animate && !occluded && this.mShowing && !this.mBouncer.isShowing()) {
            this.mStatusBar.animateKeyguardUnoccluding();
        }
    }

    public boolean isOccluded() {
        return this.mOccluded;
    }

    public void startPreHideAnimation(Runnable finishRunnable) {
        if (this.mBouncer.isShowing()) {
            this.mBouncer.startPreHideAnimation(finishRunnable);
            this.mNotificationPanelView.onBouncerPreHideAnimation();
        } else if (finishRunnable != null) {
            finishRunnable.run();
        }
        this.mNotificationPanelView.blockExpansionForCurrentTouch();
        updateLockIcon();
    }

    public void hide(long startTime, long fadeoutDuration) {
        long fadeoutDuration2;
        this.mShowing = false;
        KeyguardMonitorImpl keyguardMonitorImpl = this.mKeyguardMonitor;
        keyguardMonitorImpl.notifyKeyguardState(this.mShowing, keyguardMonitorImpl.isSecure(), this.mKeyguardMonitor.isOccluded());
        launchPendingWakeupAction();
        if (!KeyguardUpdateMonitor.getInstance(this.mContext).needsSlowUnlockTransition()) {
            fadeoutDuration2 = fadeoutDuration;
        } else {
            fadeoutDuration2 = 2000;
        }
        long uptimeMillis = SystemClock.uptimeMillis();
        long delay = Math.max(0L, (startTime + HIDE_TIMING_CORRECTION_MS) - uptimeMillis);
        if (this.mStatusBar.isInLaunchTransition()) {
            this.mStatusBar.fadeKeyguardAfterLaunchTransition(new Runnable() { // from class: com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager.5
                @Override // java.lang.Runnable
                public void run() {
                    StatusBarKeyguardViewManager.this.mStatusBarWindowController.setKeyguardShowing(false);
                    StatusBarKeyguardViewManager.this.mStatusBarWindowController.setKeyguardFadingAway(true);
                    StatusBarKeyguardViewManager.this.hideBouncer(true);
                    StatusBarKeyguardViewManager.this.updateStates();
                }
            }, new Runnable() { // from class: com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager.6
                @Override // java.lang.Runnable
                public void run() {
                    StatusBarKeyguardViewManager.this.mStatusBar.hideKeyguard();
                    StatusBarKeyguardViewManager.this.mStatusBarWindowController.setKeyguardFadingAway(false);
                    StatusBarKeyguardViewManager.this.mViewMediatorCallback.keyguardGone();
                    StatusBarKeyguardViewManager.this.executeAfterKeyguardGoneAction();
                }
            });
        } else {
            executeAfterKeyguardGoneAction();
            boolean wakeUnlockPulsing = this.mBiometricUnlockController.getMode() == 2;
            boolean needsFading = needsBypassFading();
            if (needsFading) {
                delay = 0;
                fadeoutDuration2 = 67;
            } else if (wakeUnlockPulsing) {
                delay = 0;
                fadeoutDuration2 = 240;
            }
            this.mStatusBar.setKeyguardFadingAway(startTime, delay, fadeoutDuration2, needsFading);
            this.mBiometricUnlockController.startKeyguardFadingAway();
            hideBouncer(true);
            if (wakeUnlockPulsing) {
                if (needsFading) {
                    ViewGroupFadeHelper.fadeOutAllChildrenExcept(this.mNotificationPanelView, this.mNotificationContainer, fadeoutDuration2, new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBarKeyguardViewManager$aIusP5sgaSr59XXK3nFh48FBNI4
                        @Override // java.lang.Runnable
                        public final void run() {
                            StatusBarKeyguardViewManager.this.lambda$hide$0$StatusBarKeyguardViewManager();
                        }
                    });
                } else {
                    this.mStatusBar.fadeKeyguardWhilePulsing();
                }
                wakeAndUnlockDejank();
            } else {
                boolean staying = this.mStatusBarStateController.leaveOpenOnKeyguardHide();
                if (!staying) {
                    this.mStatusBarWindowController.setKeyguardFadingAway(true);
                    if (needsFading) {
                        ViewGroupFadeHelper.fadeOutAllChildrenExcept(this.mNotificationPanelView, this.mNotificationContainer, fadeoutDuration2, new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBarKeyguardViewManager$EJI38cHcIk60L5eHmdpMvFRistw
                            @Override // java.lang.Runnable
                            public final void run() {
                                StatusBarKeyguardViewManager.this.lambda$hide$1$StatusBarKeyguardViewManager();
                            }
                        });
                    } else {
                        this.mStatusBar.hideKeyguard();
                    }
                    this.mStatusBar.updateScrimController();
                    wakeAndUnlockDejank();
                } else {
                    this.mStatusBar.hideKeyguard();
                    this.mStatusBar.finishKeyguardFadingAway();
                    this.mBiometricUnlockController.finishKeyguardFadingAway();
                }
            }
            updateLockIcon();
            updateStates();
            this.mStatusBarWindowController.setKeyguardShowing(false);
            this.mViewMediatorCallback.keyguardGone();
        }
        StatsLog.write(62, 1);
    }

    public /* synthetic */ void lambda$hide$0$StatusBarKeyguardViewManager() {
        this.mStatusBar.hideKeyguard();
        onKeyguardFadedAway();
    }

    public /* synthetic */ void lambda$hide$1$StatusBarKeyguardViewManager() {
        this.mStatusBar.hideKeyguard();
    }

    private boolean needsBypassFading() {
        return (this.mBiometricUnlockController.getMode() == 7 || this.mBiometricUnlockController.getMode() == 2 || this.mBiometricUnlockController.getMode() == 1) && this.mBypassController.getBypassEnabled();
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onDensityOrFontScaleChanged() {
        hideBouncer(true);
    }

    @Override // com.android.systemui.statusbar.phone.NavigationModeController.ModeChangedListener
    public void onNavigationModeChanged(int mode) {
        boolean gesturalNav = QuickStepContract.isGesturalMode(mode);
        if (gesturalNav != this.mGesturalNav) {
            this.mGesturalNav = gesturalNav;
            updateStates();
        }
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onThemeChanged() {
        hideBouncer(true);
        this.mBouncer.prepare();
    }

    public /* synthetic */ void lambda$onKeyguardFadedAway$2$StatusBarKeyguardViewManager() {
        this.mStatusBarWindowController.setKeyguardFadingAway(false);
    }

    public void onKeyguardFadedAway() {
        this.mContainer.postDelayed(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBarKeyguardViewManager$nb9yQRGKq0kAyQz17NqvixIA7LU
            @Override // java.lang.Runnable
            public final void run() {
                StatusBarKeyguardViewManager.this.lambda$onKeyguardFadedAway$2$StatusBarKeyguardViewManager();
            }
        }, 100L);
        ViewGroupFadeHelper.reset(this.mNotificationPanelView);
        this.mStatusBar.finishKeyguardFadingAway();
        this.mBiometricUnlockController.finishKeyguardFadingAway();
        WindowManagerGlobal.getInstance().trimMemory(20);
    }

    private void wakeAndUnlockDejank() {
        if (this.mBiometricUnlockController.getMode() == 1 && LatencyTracker.isEnabled(this.mContext)) {
            DejankUtils.postAfterTraversal(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBarKeyguardViewManager$WtAkg4w14mbTRLi3kx_TWboxp-s
                @Override // java.lang.Runnable
                public final void run() {
                    StatusBarKeyguardViewManager.this.lambda$wakeAndUnlockDejank$3$StatusBarKeyguardViewManager();
                }
            });
        }
    }

    public /* synthetic */ void lambda$wakeAndUnlockDejank$3$StatusBarKeyguardViewManager() {
        LatencyTracker.getInstance(this.mContext).onActionEnd(2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void executeAfterKeyguardGoneAction() {
        ActivityStarter.OnDismissAction onDismissAction = this.mAfterKeyguardGoneAction;
        if (onDismissAction != null) {
            onDismissAction.onDismiss();
            this.mAfterKeyguardGoneAction = null;
        }
        this.mKeyguardGoneCancelAction = null;
        for (int i = 0; i < this.mAfterKeyguardGoneRunnables.size(); i++) {
            this.mAfterKeyguardGoneRunnables.get(i).run();
        }
        this.mAfterKeyguardGoneRunnables.clear();
    }

    public void dismissAndCollapse() {
        this.mStatusBar.executeRunnableDismissingKeyguard(null, null, true, false, true);
    }

    public boolean isSecure() {
        return this.mBouncer.isSecure();
    }

    public boolean isShowing() {
        return this.mShowing;
    }

    public boolean onBackPressed(boolean hideImmediately) {
        if (this.mBouncer.isShowing()) {
            this.mStatusBar.endAffordanceLaunch();
            if (this.mBouncer.isScrimmed() && !this.mBouncer.needsFullscreenBouncer()) {
                hideBouncer(false);
                updateStates();
                return true;
            }
            reset(hideImmediately);
            return true;
        }
        return false;
    }

    public boolean isBouncerShowing() {
        return this.mBouncer.isShowing();
    }

    public boolean bouncerIsOrWillBeShowing() {
        return this.mBouncer.isShowing() || this.mBouncer.inTransit();
    }

    public boolean isFullscreenBouncer() {
        return this.mBouncer.isFullscreenBouncer();
    }

    private long getNavBarShowDelay() {
        if (this.mKeyguardMonitor.isKeyguardFadingAway()) {
            return this.mKeyguardMonitor.getKeyguardFadingAwayDelay();
        }
        if (this.mBouncer.isShowing()) {
            return NAV_BAR_SHOW_DELAY_BOUNCER;
        }
        return 0L;
    }

    protected void updateStates() {
        int vis = this.mContainer.getSystemUiVisibility();
        boolean showing = this.mShowing;
        boolean occluded = this.mOccluded;
        boolean bouncerShowing = this.mBouncer.isShowing();
        boolean z = true;
        boolean bouncerDismissible = !this.mBouncer.isFullscreenBouncer();
        boolean remoteInputActive = this.mRemoteInputActive;
        if ((bouncerDismissible || !showing || remoteInputActive) != (this.mLastBouncerDismissible || !this.mLastShowing || this.mLastRemoteInputActive) || this.mFirstUpdate) {
            if (bouncerDismissible || !showing || remoteInputActive) {
                this.mContainer.setSystemUiVisibility((-4194305) & vis);
            } else {
                this.mContainer.setSystemUiVisibility(4194304 | vis);
            }
        }
        boolean navBarVisible = isNavBarVisible();
        boolean lastNavBarVisible = getLastNavBarVisible();
        if (navBarVisible != lastNavBarVisible || this.mFirstUpdate) {
            updateNavigationBarVisibility(navBarVisible);
        }
        if (bouncerShowing != this.mLastBouncerShowing || this.mFirstUpdate) {
            this.mStatusBarWindowController.setBouncerShowing(bouncerShowing);
            this.mStatusBar.setBouncerShowing(bouncerShowing);
        }
        KeyguardUpdateMonitor updateMonitor = KeyguardUpdateMonitor.getInstance(this.mContext);
        if ((showing && !occluded) != (this.mLastShowing && !this.mLastOccluded) || this.mFirstUpdate) {
            if (!showing || occluded) {
                z = false;
            }
            updateMonitor.onKeyguardVisibilityChanged(z);
        }
        if (bouncerShowing != this.mLastBouncerShowing || this.mFirstUpdate) {
            updateMonitor.sendKeyguardBouncerChanged(bouncerShowing);
        }
        this.mFirstUpdate = false;
        this.mLastShowing = showing;
        this.mLastOccluded = occluded;
        this.mLastBouncerShowing = bouncerShowing;
        this.mLastBouncerDismissible = bouncerDismissible;
        this.mLastRemoteInputActive = remoteInputActive;
        this.mLastDozing = this.mDozing;
        this.mLastPulsing = this.mPulsing;
        this.mLastBiometricMode = this.mBiometricUnlockController.getMode();
        this.mLastGesturalNav = this.mGesturalNav;
        this.mLastIsDocked = this.mIsDocked;
        this.mStatusBar.onKeyguardViewManagerStatesUpdated();
    }

    protected void updateNavigationBarVisibility(boolean navBarVisible) {
        if (this.mStatusBar.getNavigationBarView() != null) {
            if (navBarVisible) {
                long delay = getNavBarShowDelay();
                if (delay == 0) {
                    this.mMakeNavigationBarVisibleRunnable.run();
                    return;
                } else {
                    this.mContainer.postOnAnimationDelayed(this.mMakeNavigationBarVisibleRunnable, delay);
                    return;
                }
            }
            this.mContainer.removeCallbacks(this.mMakeNavigationBarVisibleRunnable);
            this.mStatusBar.getNavigationBarView().getRootView().setVisibility(8);
        }
    }

    protected boolean isNavBarVisible() {
        int biometricMode = this.mBiometricUnlockController.getMode();
        boolean keyguardShowing = this.mShowing && !this.mOccluded;
        boolean hideWhileDozing = this.mDozing && biometricMode != 2;
        boolean keyguardWithGestureNav = ((keyguardShowing && !this.mDozing) || (this.mPulsing && !this.mIsDocked)) && this.mGesturalNav;
        return !(keyguardShowing || hideWhileDozing) || this.mBouncer.isShowing() || this.mRemoteInputActive || keyguardWithGestureNav;
    }

    protected boolean getLastNavBarVisible() {
        boolean keyguardShowing = this.mLastShowing && !this.mLastOccluded;
        boolean hideWhileDozing = this.mLastDozing && this.mLastBiometricMode != 2;
        boolean keyguardWithGestureNav = ((keyguardShowing && !this.mLastDozing) || (this.mLastPulsing && !this.mLastIsDocked)) && this.mLastGesturalNav;
        return !(keyguardShowing || hideWhileDozing) || this.mLastBouncerShowing || this.mLastRemoteInputActive || keyguardWithGestureNav;
    }

    public boolean shouldDismissOnMenuPressed() {
        return this.mBouncer.shouldDismissOnMenuPressed();
    }

    public boolean interceptMediaKey(KeyEvent event) {
        return this.mBouncer.interceptMediaKey(event);
    }

    public void readyForKeyguardDone() {
        this.mViewMediatorCallback.readyForKeyguardDone();
    }

    public boolean shouldDisableWindowAnimationsForUnlock() {
        return this.mStatusBar.isInLaunchTransition();
    }

    public boolean shouldSubtleWindowAnimationsForUnlock() {
        return needsBypassFading();
    }

    public boolean isGoingToNotificationShade() {
        return ((SysuiStatusBarStateController) Dependency.get(StatusBarStateController.class)).leaveOpenOnKeyguardHide();
    }

    public boolean isSecure(int userId) {
        return this.mBouncer.isSecure() || this.mLockPatternUtils.isSecure(userId);
    }

    public void keyguardGoingAway() {
        this.mStatusBar.keyguardGoingAway();
    }

    public void animateCollapsePanels(float speedUpFactor) {
        this.mStatusBar.animateCollapsePanels(0, true, false, speedUpFactor);
    }

    public void onCancelClicked() {
    }

    public void notifyKeyguardAuthenticated(boolean strongAuth) {
        this.mBouncer.notifyKeyguardAuthenticated(strongAuth);
    }

    public void showBouncerMessage(String message, ColorStateList colorState) {
        this.mBouncer.showMessage(message, colorState);
    }

    public ViewRootImpl getViewRootImpl() {
        return this.mStatusBar.getStatusBarView().getViewRootImpl();
    }

    public void launchPendingWakeupAction() {
        DismissWithActionRequest request = this.mPendingWakeupAction;
        this.mPendingWakeupAction = null;
        if (request != null) {
            if (this.mShowing) {
                dismissWithAction(request.dismissAction, request.cancelAction, request.afterKeyguardGone, request.message);
            } else if (request.dismissAction != null) {
                request.dismissAction.onDismiss();
            }
        }
    }

    public void cancelPendingWakeupAction() {
        DismissWithActionRequest request = this.mPendingWakeupAction;
        this.mPendingWakeupAction = null;
        if (request != null && request.cancelAction != null) {
            request.cancelAction.run();
        }
    }

    public boolean bouncerNeedsScrimming() {
        return this.mOccluded || this.mBouncer.willDismissWithAction() || this.mStatusBar.isFullScreenUserSwitcherState() || (this.mBouncer.isShowing() && this.mBouncer.isScrimmed()) || this.mBouncer.isFullscreenBouncer();
    }

    public void dump(PrintWriter pw) {
        pw.println("StatusBarKeyguardViewManager:");
        pw.println("  mShowing: " + this.mShowing);
        pw.println("  mOccluded: " + this.mOccluded);
        pw.println("  mRemoteInputActive: " + this.mRemoteInputActive);
        pw.println("  mDozing: " + this.mDozing);
        pw.println("  mGoingToSleepVisibleNotOccluded: " + this.mGoingToSleepVisibleNotOccluded);
        pw.println("  mAfterKeyguardGoneAction: " + this.mAfterKeyguardGoneAction);
        pw.println("  mAfterKeyguardGoneRunnables: " + this.mAfterKeyguardGoneRunnables);
        pw.println("  mPendingWakeupAction: " + this.mPendingWakeupAction);
        KeyguardBouncer keyguardBouncer = this.mBouncer;
        if (keyguardBouncer != null) {
            keyguardBouncer.dump(pw);
        }
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onStateChanged(int newState) {
        updateLockIcon();
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onDozingChanged(boolean isDozing) {
        setDozing(isDozing);
    }

    public KeyguardBouncer getBouncer() {
        return this.mBouncer;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class DismissWithActionRequest {
        final boolean afterKeyguardGone;
        final Runnable cancelAction;
        final ActivityStarter.OnDismissAction dismissAction;
        final String message;

        DismissWithActionRequest(ActivityStarter.OnDismissAction dismissAction, Runnable cancelAction, boolean afterKeyguardGone, String message) {
            this.dismissAction = dismissAction;
            this.cancelAction = cancelAction;
            this.afterKeyguardGone = afterKeyguardGone;
            this.message = message;
        }
    }
}
