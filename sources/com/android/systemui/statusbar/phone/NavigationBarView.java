package com.android.systemui.statusbar.phone;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dependency;
import com.android.systemui.DockedStackExistsListener;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.RecentsOnboarding;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.WindowManagerWrapper;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.statusbar.policy.DeadZone;
import com.android.systemui.statusbar.policy.KeyButtonDrawable;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.speech.protocol.event.OOBEEvent;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class NavigationBarView extends FrameLayout implements NavigationModeController.ModeChangedListener {
    static final boolean ALTERNATE_CAR_MODE_UI = false;
    static final boolean DEBUG = false;
    static final boolean SLIPPERY_WHEN_DISABLED = true;
    static final String TAG = "StatusBar/NavBarView";
    private final Region mActiveRegion;
    private Rect mBackButtonBounds;
    private KeyButtonDrawable mBackIcon;
    private final NavigationBarTransitions mBarTransitions;
    private final SparseArray<ButtonDispatcher> mButtonDispatchers;
    private Configuration mConfiguration;
    private final ContextualButtonGroup mContextualButtonGroup;
    private int mCurrentRotation;
    View mCurrentView;
    private final DeadZone mDeadZone;
    private boolean mDeadZoneConsuming;
    int mDisabledFlags;
    private KeyButtonDrawable mDockedIcon;
    private final Consumer<Boolean> mDockedListener;
    private boolean mDockedStackExists;
    private final EdgeBackGestureHandler mEdgeBackGestureHandler;
    private FloatingRotationButton mFloatingRotationButton;
    private Rect mHomeButtonBounds;
    private KeyButtonDrawable mHomeDefaultIcon;
    private View mHorizontal;
    private final View.OnClickListener mImeSwitcherClickListener;
    private boolean mImeVisible;
    private boolean mInCarMode;
    private boolean mIsVertical;
    private boolean mLayoutTransitionsEnabled;
    boolean mLongClickableAccessibilityButton;
    private int mNavBarMode;
    int mNavigationIconHints;
    private NavigationBarInflaterView mNavigationInflaterView;
    private final ViewTreeObserver.OnComputeInternalInsetsListener mOnComputeInternalInsetsListener;
    private OnVerticalChangedListener mOnVerticalChangedListener;
    private final OverviewProxyService mOverviewProxyService;
    private NotificationPanelView mPanelView;
    private final View.AccessibilityDelegate mQuickStepAccessibilityDelegate;
    private KeyButtonDrawable mRecentIcon;
    private Rect mRecentsButtonBounds;
    private RecentsOnboarding mRecentsOnboarding;
    private Rect mRotationButtonBounds;
    private RotationButtonController mRotationButtonController;
    private ScreenPinningNotify mScreenPinningNotify;
    private NavBarTintController mTintController;
    private Configuration mTmpLastConfiguration;
    private int[] mTmpPosition;
    private final NavTransitionListener mTransitionListener;
    private boolean mUseCarModeUi;
    private View mVertical;
    private boolean mWakeAndUnlocking;

    /* loaded from: classes21.dex */
    public interface OnVerticalChangedListener {
        void onVerticalChanged(boolean z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class NavTransitionListener implements LayoutTransition.TransitionListener {
        private boolean mBackTransitioning;
        private long mDuration;
        private boolean mHomeAppearing;
        private TimeInterpolator mInterpolator;
        private long mStartDelay;

        private NavTransitionListener() {
        }

        @Override // android.animation.LayoutTransition.TransitionListener
        public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
            if (view.getId() == R.id.back) {
                this.mBackTransitioning = true;
            } else if (view.getId() == R.id.home && transitionType == 2) {
                this.mHomeAppearing = true;
                this.mStartDelay = transition.getStartDelay(transitionType);
                this.mDuration = transition.getDuration(transitionType);
                this.mInterpolator = transition.getInterpolator(transitionType);
            }
        }

        @Override // android.animation.LayoutTransition.TransitionListener
        public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
            if (view.getId() == R.id.back) {
                this.mBackTransitioning = false;
            } else if (view.getId() == R.id.home && transitionType == 2) {
                this.mHomeAppearing = false;
            }
        }

        public void onBackAltCleared() {
            ButtonDispatcher backButton = NavigationBarView.this.getBackButton();
            if (!this.mBackTransitioning && backButton.getVisibility() == 0 && this.mHomeAppearing && NavigationBarView.this.getHomeButton().getAlpha() == 0.0f) {
                NavigationBarView.this.getBackButton().setAlpha(0.0f);
                ValueAnimator a = ObjectAnimator.ofFloat(backButton, ThemeManager.AttributeSet.ALPHA, 0.0f, 1.0f);
                a.setStartDelay(this.mStartDelay);
                a.setDuration(this.mDuration);
                a.setInterpolator(this.mInterpolator);
                a.start();
            }
        }
    }

    public /* synthetic */ void lambda$new$0$NavigationBarView(ViewTreeObserver.InternalInsetsInfo info) {
        if (!QuickStepContract.isGesturalMode(this.mNavBarMode) || this.mImeVisible) {
            info.setTouchableInsets(0);
            return;
        }
        info.setTouchableInsets(3);
        ButtonDispatcher imeSwitchButton = getImeSwitchButton();
        if (imeSwitchButton.getVisibility() == 0) {
            int[] loc = new int[2];
            View buttonView = imeSwitchButton.getCurrentView();
            buttonView.getLocationInWindow(loc);
            info.touchableRegion.set(loc[0], loc[1], loc[0] + buttonView.getWidth(), loc[1] + buttonView.getHeight());
            return;
        }
        info.touchableRegion.setEmpty();
    }

    public NavigationBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCurrentView = null;
        this.mCurrentRotation = -1;
        this.mDisabledFlags = 0;
        this.mNavigationIconHints = 0;
        this.mNavBarMode = 0;
        this.mHomeButtonBounds = new Rect();
        this.mBackButtonBounds = new Rect();
        this.mRecentsButtonBounds = new Rect();
        this.mRotationButtonBounds = new Rect();
        this.mActiveRegion = new Region();
        this.mTmpPosition = new int[2];
        this.mDeadZoneConsuming = false;
        this.mTransitionListener = new NavTransitionListener();
        this.mLayoutTransitionsEnabled = true;
        this.mUseCarModeUi = false;
        this.mInCarMode = false;
        this.mButtonDispatchers = new SparseArray<>();
        this.mImeSwitcherClickListener = new View.OnClickListener() { // from class: com.android.systemui.statusbar.phone.NavigationBarView.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ((InputMethodManager) NavigationBarView.this.mContext.getSystemService(InputMethodManager.class)).showInputMethodPickerFromSystem(true, NavigationBarView.this.getContext().getDisplayId());
            }
        };
        this.mQuickStepAccessibilityDelegate = new View.AccessibilityDelegate() { // from class: com.android.systemui.statusbar.phone.NavigationBarView.2
            private AccessibilityNodeInfo.AccessibilityAction mToggleOverviewAction;

            @Override // android.view.View.AccessibilityDelegate
            public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                if (this.mToggleOverviewAction == null) {
                    this.mToggleOverviewAction = new AccessibilityNodeInfo.AccessibilityAction(R.id.action_toggle_overview, NavigationBarView.this.getContext().getString(R.string.quick_step_accessibility_toggle_overview));
                }
                info.addAction(this.mToggleOverviewAction);
            }

            @Override // android.view.View.AccessibilityDelegate
            public boolean performAccessibilityAction(View host, int action, Bundle args) {
                if (action == R.id.action_toggle_overview) {
                    ((Recents) SysUiServiceProvider.getComponent(NavigationBarView.this.getContext(), Recents.class)).toggleRecentApps();
                    return true;
                }
                return super.performAccessibilityAction(host, action, args);
            }
        };
        this.mOnComputeInternalInsetsListener = new ViewTreeObserver.OnComputeInternalInsetsListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NavigationBarView$khIxhJwBd7pJnFFXnq8zupcHrv8
            public final void onComputeInternalInsets(ViewTreeObserver.InternalInsetsInfo internalInsetsInfo) {
                NavigationBarView.this.lambda$new$0$NavigationBarView(internalInsetsInfo);
            }
        };
        this.mDockedListener = new Consumer() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NavigationBarView$3_rm_LYAhHXvCBhrsX10ry5w8OA
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                NavigationBarView.this.lambda$new$2$NavigationBarView((Boolean) obj);
            }
        };
        this.mIsVertical = false;
        this.mLongClickableAccessibilityButton = false;
        this.mNavBarMode = ((NavigationModeController) Dependency.get(NavigationModeController.class)).addListener(this);
        boolean isGesturalMode = QuickStepContract.isGesturalMode(this.mNavBarMode);
        this.mContextualButtonGroup = new ContextualButtonGroup(R.id.menu_container);
        ContextualButton imeSwitcherButton = new ContextualButton(R.id.ime_switcher, R.drawable.ic_ime_switcher_default);
        RotationContextButton rotateSuggestionButton = new RotationContextButton(R.id.rotate_suggestion, R.drawable.ic_sysbar_rotate_button);
        ContextualButton accessibilityButton = new ContextualButton(R.id.accessibility_button, R.drawable.ic_sysbar_accessibility_button);
        this.mContextualButtonGroup.addButton(imeSwitcherButton);
        if (!isGesturalMode) {
            this.mContextualButtonGroup.addButton(rotateSuggestionButton);
        }
        this.mContextualButtonGroup.addButton(accessibilityButton);
        this.mOverviewProxyService = (OverviewProxyService) Dependency.get(OverviewProxyService.class);
        this.mRecentsOnboarding = new RecentsOnboarding(context, this.mOverviewProxyService);
        this.mFloatingRotationButton = new FloatingRotationButton(context);
        this.mRotationButtonController = new RotationButtonController(context, R.style.RotateButtonCCWStart90, isGesturalMode ? this.mFloatingRotationButton : rotateSuggestionButton);
        ContextualButton backButton = new ContextualButton(R.id.back, 0);
        this.mConfiguration = new Configuration();
        this.mTmpLastConfiguration = new Configuration();
        this.mConfiguration.updateFrom(context.getResources().getConfiguration());
        this.mScreenPinningNotify = new ScreenPinningNotify(this.mContext);
        this.mBarTransitions = new NavigationBarTransitions(this);
        this.mButtonDispatchers.put(R.id.back, backButton);
        this.mButtonDispatchers.put(R.id.home, new ButtonDispatcher(R.id.home));
        this.mButtonDispatchers.put(R.id.home_handle, new ButtonDispatcher(R.id.home_handle));
        this.mButtonDispatchers.put(R.id.recent_apps, new ButtonDispatcher(R.id.recent_apps));
        this.mButtonDispatchers.put(R.id.ime_switcher, imeSwitcherButton);
        this.mButtonDispatchers.put(R.id.accessibility_button, accessibilityButton);
        this.mButtonDispatchers.put(R.id.rotate_suggestion, rotateSuggestionButton);
        this.mButtonDispatchers.put(R.id.menu_container, this.mContextualButtonGroup);
        this.mDeadZone = new DeadZone(this);
        this.mEdgeBackGestureHandler = new EdgeBackGestureHandler(context, this.mOverviewProxyService);
        this.mTintController = new NavBarTintController(this, getLightTransitionsController());
    }

    public NavBarTintController getTintController() {
        return this.mTintController;
    }

    public NavigationBarTransitions getBarTransitions() {
        return this.mBarTransitions;
    }

    public LightBarTransitionsController getLightTransitionsController() {
        return this.mBarTransitions.getLightTransitionsController();
    }

    public void setComponents(NotificationPanelView panel, AssistManager assistManager) {
        this.mPanelView = panel;
        updatePanelSystemUiStateFlags();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        this.mTintController.onDraw();
    }

    public void setOnVerticalChangedListener(OnVerticalChangedListener onVerticalChangedListener) {
        this.mOnVerticalChangedListener = onVerticalChangedListener;
        notifyVerticalChangedListener(this.mIsVertical);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return shouldDeadZoneConsumeTouchEvents(event) || super.onInterceptTouchEvent(event);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        shouldDeadZoneConsumeTouchEvents(event);
        return super.onTouchEvent(event);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onBarTransition(int newMode) {
        if (newMode == 0) {
            this.mTintController.stop();
            getLightTransitionsController().setIconsDark(false, true);
            return;
        }
        this.mTintController.start();
    }

    private boolean shouldDeadZoneConsumeTouchEvents(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == 0) {
            this.mDeadZoneConsuming = false;
        }
        if (!this.mDeadZone.onTouchEvent(event) && !this.mDeadZoneConsuming) {
            return false;
        }
        if (action != 0) {
            if (action == 1 || action == 3) {
                updateSlippery();
                this.mDeadZoneConsuming = false;
            }
        } else {
            setSlippery(true);
            this.mDeadZoneConsuming = true;
        }
        return true;
    }

    public void abortCurrentGesture() {
        getHomeButton().abortCurrentGesture();
    }

    public View getCurrentView() {
        return this.mCurrentView;
    }

    public RotationButtonController getRotationButtonController() {
        return this.mRotationButtonController;
    }

    public FloatingRotationButton getFloatingRotationButton() {
        return this.mFloatingRotationButton;
    }

    public ButtonDispatcher getRecentsButton() {
        return this.mButtonDispatchers.get(R.id.recent_apps);
    }

    public ButtonDispatcher getBackButton() {
        return this.mButtonDispatchers.get(R.id.back);
    }

    public ButtonDispatcher getHomeButton() {
        return this.mButtonDispatchers.get(R.id.home);
    }

    public ButtonDispatcher getImeSwitchButton() {
        return this.mButtonDispatchers.get(R.id.ime_switcher);
    }

    public ButtonDispatcher getAccessibilityButton() {
        return this.mButtonDispatchers.get(R.id.accessibility_button);
    }

    public RotationContextButton getRotateSuggestionButton() {
        return (RotationContextButton) this.mButtonDispatchers.get(R.id.rotate_suggestion);
    }

    public ButtonDispatcher getHomeHandle() {
        return this.mButtonDispatchers.get(R.id.home_handle);
    }

    public SparseArray<ButtonDispatcher> getButtonDispatchers() {
        return this.mButtonDispatchers;
    }

    public boolean isRecentsButtonVisible() {
        return getRecentsButton().getVisibility() == 0;
    }

    public boolean isOverviewEnabled() {
        return (this.mDisabledFlags & 16777216) == 0;
    }

    public boolean isQuickStepSwipeUpEnabled() {
        return this.mOverviewProxyService.shouldShowSwipeUpUI() && isOverviewEnabled();
    }

    private void reloadNavIcons() {
        updateIcons(Configuration.EMPTY);
    }

    private void updateIcons(Configuration oldConfig) {
        boolean orientationChange = oldConfig.orientation != this.mConfiguration.orientation;
        boolean densityChange = oldConfig.densityDpi != this.mConfiguration.densityDpi;
        boolean dirChange = oldConfig.getLayoutDirection() != this.mConfiguration.getLayoutDirection();
        if (orientationChange || densityChange) {
            this.mDockedIcon = getDrawable(R.drawable.ic_sysbar_docked);
            this.mHomeDefaultIcon = getHomeDrawable();
        }
        if (densityChange || dirChange) {
            this.mRecentIcon = getDrawable(R.drawable.ic_sysbar_recent);
            this.mContextualButtonGroup.updateIcons();
        }
        if (orientationChange || densityChange || dirChange) {
            this.mBackIcon = getBackDrawable();
        }
    }

    public KeyButtonDrawable getBackDrawable() {
        KeyButtonDrawable drawable = getDrawable(getBackDrawableRes());
        orientBackButton(drawable);
        return drawable;
    }

    public int getBackDrawableRes() {
        return chooseNavigationIconDrawableRes(R.drawable.ic_sysbar_back, R.drawable.ic_sysbar_back_quick_step);
    }

    public KeyButtonDrawable getHomeDrawable() {
        KeyButtonDrawable drawable;
        boolean quickStepEnabled = this.mOverviewProxyService.shouldShowSwipeUpUI();
        if (quickStepEnabled) {
            drawable = getDrawable(R.drawable.ic_sysbar_home_quick_step);
        } else {
            drawable = getDrawable(R.drawable.ic_sysbar_home);
        }
        orientHomeButton(drawable);
        return drawable;
    }

    private void orientBackButton(KeyButtonDrawable drawable) {
        float degrees;
        boolean useAltBack = (this.mNavigationIconHints & 1) != 0;
        boolean isRtl = this.mConfiguration.getLayoutDirection() == 1;
        float targetY = 0.0f;
        if (useAltBack) {
            degrees = isRtl ? 90 : -90;
        } else {
            degrees = 0.0f;
        }
        if (drawable.getRotation() == degrees) {
            return;
        }
        if (QuickStepContract.isGesturalMode(this.mNavBarMode)) {
            drawable.setRotation(degrees);
            return;
        }
        if (!this.mOverviewProxyService.shouldShowSwipeUpUI() && !this.mIsVertical && useAltBack) {
            targetY = -getResources().getDimension(R.dimen.navbar_back_button_ime_offset);
        }
        ObjectAnimator navBarAnimator = ObjectAnimator.ofPropertyValuesHolder(drawable, PropertyValuesHolder.ofFloat(KeyButtonDrawable.KEY_DRAWABLE_ROTATE, degrees), PropertyValuesHolder.ofFloat(KeyButtonDrawable.KEY_DRAWABLE_TRANSLATE_Y, targetY));
        navBarAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        navBarAnimator.setDuration(200L);
        navBarAnimator.start();
    }

    private void orientHomeButton(KeyButtonDrawable drawable) {
        drawable.setRotation(this.mIsVertical ? 90.0f : 0.0f);
    }

    private KeyButtonDrawable chooseNavigationIconDrawable(int icon, int quickStepIcon) {
        return getDrawable(chooseNavigationIconDrawableRes(icon, quickStepIcon));
    }

    private int chooseNavigationIconDrawableRes(int icon, int quickStepIcon) {
        boolean quickStepEnabled = this.mOverviewProxyService.shouldShowSwipeUpUI();
        return quickStepEnabled ? quickStepIcon : icon;
    }

    private KeyButtonDrawable getDrawable(int icon) {
        return KeyButtonDrawable.create(this.mContext, icon, true);
    }

    private KeyButtonDrawable getDrawable(int icon, boolean hasShadow) {
        return KeyButtonDrawable.create(this.mContext, icon, hasShadow);
    }

    public void setWindowVisible(boolean visible) {
        this.mTintController.setWindowVisible(visible);
        this.mRotationButtonController.onNavigationBarWindowVisibilityChange(visible);
    }

    @Override // android.view.View
    public void setLayoutDirection(int layoutDirection) {
        reloadNavIcons();
        super.setLayoutDirection(layoutDirection);
    }

    public void setNavigationIconHints(int hints) {
        if (hints == this.mNavigationIconHints) {
            return;
        }
        boolean newBackAlt = (hints & 1) != 0;
        boolean oldBackAlt = (this.mNavigationIconHints & 1) != 0;
        if (newBackAlt != oldBackAlt) {
            onImeVisibilityChanged(newBackAlt);
        }
        this.mNavigationIconHints = hints;
        updateNavButtonIcons();
    }

    private void onImeVisibilityChanged(boolean visible) {
        if (!visible) {
            this.mTransitionListener.onBackAltCleared();
        }
        this.mImeVisible = visible;
        this.mRotationButtonController.getRotationButton().setCanShowRotationButton(!this.mImeVisible);
    }

    public void setDisabledFlags(int disabledFlags) {
        if (this.mDisabledFlags == disabledFlags) {
            return;
        }
        boolean overviewEnabledBefore = isOverviewEnabled();
        this.mDisabledFlags = disabledFlags;
        if (!overviewEnabledBefore && isOverviewEnabled()) {
            reloadNavIcons();
        }
        updateNavButtonIcons();
        updateSlippery();
        setUpSwipeUpOnboarding(isQuickStepSwipeUpEnabled());
        updateDisabledSystemUiStateFlags();
    }

    public void updateNavButtonIcons() {
        LayoutTransition lt;
        boolean useAltBack = (this.mNavigationIconHints & 1) != 0;
        KeyButtonDrawable backIcon = this.mBackIcon;
        orientBackButton(backIcon);
        KeyButtonDrawable homeIcon = this.mHomeDefaultIcon;
        if (!this.mUseCarModeUi) {
            orientHomeButton(homeIcon);
        }
        getHomeButton().setImageDrawable(homeIcon);
        getBackButton().setImageDrawable(backIcon);
        updateRecentsIcon();
        this.mContextualButtonGroup.setButtonVisibility(R.id.ime_switcher, (this.mNavigationIconHints & 2) != 0);
        this.mBarTransitions.reapplyDarkIntensity();
        boolean disableHome = QuickStepContract.isGesturalMode(this.mNavBarMode) || (this.mDisabledFlags & 2097152) != 0;
        boolean disableRecent = isRecentsButtonDisabled();
        boolean disableHomeHandle = disableRecent && (2097152 & this.mDisabledFlags) != 0;
        boolean disableBack = !useAltBack && (QuickStepContract.isGesturalMode(this.mNavBarMode) || (this.mDisabledFlags & 4194304) != 0);
        boolean pinningActive = ActivityManagerWrapper.getInstance().isScreenPinningActive();
        if (this.mOverviewProxyService.isEnabled()) {
            disableRecent |= true ^ QuickStepContract.isLegacyMode(this.mNavBarMode);
            if (pinningActive && !QuickStepContract.isGesturalMode(this.mNavBarMode)) {
                disableHome = false;
                disableBack = false;
            }
        } else if (pinningActive) {
            disableRecent = false;
            disableBack = false;
        }
        ViewGroup navButtons = (ViewGroup) getCurrentView().findViewById(R.id.nav_buttons);
        if (navButtons != null && (lt = navButtons.getLayoutTransition()) != null && !lt.getTransitionListeners().contains(this.mTransitionListener)) {
            lt.addTransitionListener(this.mTransitionListener);
        }
        getBackButton().setVisibility(disableBack ? 4 : 0);
        getHomeButton().setVisibility(disableHome ? 4 : 0);
        getRecentsButton().setVisibility(disableRecent ? 4 : 0);
        getHomeHandle().setVisibility(disableHomeHandle ? 4 : 0);
    }

    @VisibleForTesting
    boolean isRecentsButtonDisabled() {
        return (!this.mUseCarModeUi && isOverviewEnabled() && getContext().getDisplayId() == 0) ? false : true;
    }

    private Display getContextDisplay() {
        return getContext().getDisplay();
    }

    public void setLayoutTransitionsEnabled(boolean enabled) {
        this.mLayoutTransitionsEnabled = enabled;
        updateLayoutTransitionsEnabled();
    }

    public void setWakeAndUnlocking(boolean wakeAndUnlocking) {
        setUseFadingAnimations(wakeAndUnlocking);
        this.mWakeAndUnlocking = wakeAndUnlocking;
        updateLayoutTransitionsEnabled();
    }

    private void updateLayoutTransitionsEnabled() {
        boolean enabled = !this.mWakeAndUnlocking && this.mLayoutTransitionsEnabled;
        ViewGroup navButtons = (ViewGroup) getCurrentView().findViewById(R.id.nav_buttons);
        LayoutTransition lt = navButtons.getLayoutTransition();
        if (lt != null) {
            if (enabled) {
                lt.enableTransitionType(2);
                lt.enableTransitionType(3);
                lt.enableTransitionType(0);
                lt.enableTransitionType(1);
                return;
            }
            lt.disableTransitionType(2);
            lt.disableTransitionType(3);
            lt.disableTransitionType(0);
            lt.disableTransitionType(1);
        }
    }

    private void setUseFadingAnimations(boolean useFadingAnimations) {
        WindowManager.LayoutParams lp = (WindowManager.LayoutParams) ((ViewGroup) getParent()).getLayoutParams();
        if (lp != null) {
            boolean old = lp.windowAnimations != 0;
            if (!old && useFadingAnimations) {
                lp.windowAnimations = R.style.Animation_NavigationBarFadeIn;
            } else if (old && !useFadingAnimations) {
                lp.windowAnimations = 0;
            } else {
                return;
            }
            WindowManager wm = (WindowManager) getContext().getSystemService("window");
            wm.updateViewLayout((View) getParent(), lp);
        }
    }

    public void onStatusBarPanelStateChanged() {
        updateSlippery();
        updatePanelSystemUiStateFlags();
    }

    public void updateDisabledSystemUiStateFlags() {
        int displayId = this.mContext.getDisplayId();
        this.mOverviewProxyService.setSystemUiStateFlag(1, ActivityManagerWrapper.getInstance().isScreenPinningActive(), displayId);
        this.mOverviewProxyService.setSystemUiStateFlag(128, (this.mDisabledFlags & 16777216) != 0, displayId);
        this.mOverviewProxyService.setSystemUiStateFlag(256, (this.mDisabledFlags & 2097152) != 0, displayId);
        this.mOverviewProxyService.setSystemUiStateFlag(1024, (this.mDisabledFlags & 33554432) != 0, displayId);
    }

    public void updatePanelSystemUiStateFlags() {
        int displayId = this.mContext.getDisplayId();
        NotificationPanelView notificationPanelView = this.mPanelView;
        if (notificationPanelView != null) {
            this.mOverviewProxyService.setSystemUiStateFlag(4, notificationPanelView.isFullyExpanded() && !this.mPanelView.isInSettings(), displayId);
            this.mOverviewProxyService.setSystemUiStateFlag(2048, this.mPanelView.isInSettings(), displayId);
        }
    }

    public void updateStates() {
        boolean showSwipeUpUI = this.mOverviewProxyService.shouldShowSwipeUpUI();
        NavigationBarInflaterView navigationBarInflaterView = this.mNavigationInflaterView;
        if (navigationBarInflaterView != null) {
            navigationBarInflaterView.onLikelyDefaultLayoutChange();
        }
        updateSlippery();
        reloadNavIcons();
        updateNavButtonIcons();
        setUpSwipeUpOnboarding(isQuickStepSwipeUpEnabled());
        WindowManagerWrapper.getInstance().setNavBarVirtualKeyHapticFeedbackEnabled(!showSwipeUpUI);
        getHomeButton().setAccessibilityDelegate(showSwipeUpUI ? this.mQuickStepAccessibilityDelegate : null);
    }

    public void updateSlippery() {
        setSlippery(!isQuickStepSwipeUpEnabled() || (this.mPanelView.isFullyExpanded() && !this.mPanelView.isCollapsing()));
    }

    private void setSlippery(boolean slippery) {
        setWindowFlag(536870912, slippery);
    }

    private void setWindowFlag(int flags, boolean enable) {
        WindowManager.LayoutParams lp;
        ViewGroup navbarView = (ViewGroup) getParent();
        if (navbarView == null || (lp = (WindowManager.LayoutParams) navbarView.getLayoutParams()) == null) {
            return;
        }
        if (enable == ((lp.flags & flags) != 0)) {
            return;
        }
        if (enable) {
            lp.flags |= flags;
        } else {
            lp.flags &= ~flags;
        }
        WindowManager wm = (WindowManager) getContext().getSystemService("window");
        wm.updateViewLayout(navbarView, lp);
    }

    @Override // com.android.systemui.statusbar.phone.NavigationModeController.ModeChangedListener
    public void onNavigationModeChanged(int mode) {
        Context curUserCtx = ((NavigationModeController) Dependency.get(NavigationModeController.class)).getCurrentUserContext();
        this.mNavBarMode = mode;
        this.mBarTransitions.onNavigationModeChanged(this.mNavBarMode);
        this.mEdgeBackGestureHandler.onNavigationModeChanged(this.mNavBarMode, curUserCtx);
        this.mRecentsOnboarding.onNavigationModeChanged(this.mNavBarMode);
        getRotateSuggestionButton().onNavigationModeChanged(this.mNavBarMode);
        this.mTintController.onNavigationModeChanged(this.mNavBarMode);
        if (QuickStepContract.isGesturalMode(this.mNavBarMode)) {
            this.mTintController.start();
        } else {
            this.mTintController.stop();
        }
    }

    public void setAccessibilityButtonState(boolean visible, boolean longClickable) {
        this.mLongClickableAccessibilityButton = longClickable;
        getAccessibilityButton().setLongClickable(longClickable);
        this.mContextualButtonGroup.setButtonVisibility(R.id.accessibility_button, visible);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void hideRecentsOnboarding() {
        this.mRecentsOnboarding.hide(true);
    }

    @Override // android.view.View
    public void onFinishInflate() {
        this.mNavigationInflaterView = (NavigationBarInflaterView) findViewById(R.id.navigation_inflater);
        this.mNavigationInflaterView.setButtonDispatchers(this.mButtonDispatchers);
        getImeSwitchButton().setOnClickListener(this.mImeSwitcherClickListener);
        DockedStackExistsListener.register(this.mDockedListener);
        updateOrientationViews();
        reloadNavIcons();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        this.mDeadZone.onDraw(canvas);
        super.onDraw(canvas);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.mActiveRegion.setEmpty();
        updateButtonLocation(getBackButton(), this.mBackButtonBounds, true);
        updateButtonLocation(getHomeButton(), this.mHomeButtonBounds, false);
        updateButtonLocation(getRecentsButton(), this.mRecentsButtonBounds, false);
        updateButtonLocation(getRotateSuggestionButton(), this.mRotationButtonBounds, true);
        this.mOverviewProxyService.onActiveNavBarRegionChanges(this.mActiveRegion);
        this.mRecentsOnboarding.setNavBarHeight(getMeasuredHeight());
    }

    private void updateButtonLocation(ButtonDispatcher button, Rect buttonBounds, boolean isActive) {
        View view = button.getCurrentView();
        if (view == null) {
            buttonBounds.setEmpty();
            return;
        }
        float posX = view.getTranslationX();
        float posY = view.getTranslationY();
        view.setTranslationX(0.0f);
        view.setTranslationY(0.0f);
        if (isActive) {
            view.getLocationOnScreen(this.mTmpPosition);
            int[] iArr = this.mTmpPosition;
            buttonBounds.set(iArr[0], iArr[1], iArr[0] + view.getMeasuredWidth(), this.mTmpPosition[1] + view.getMeasuredHeight());
            this.mActiveRegion.op(buttonBounds, Region.Op.UNION);
        }
        view.getLocationInWindow(this.mTmpPosition);
        int[] iArr2 = this.mTmpPosition;
        buttonBounds.set(iArr2[0], iArr2[1], iArr2[0] + view.getMeasuredWidth(), this.mTmpPosition[1] + view.getMeasuredHeight());
        view.setTranslationX(posX);
        view.setTranslationY(posY);
    }

    private void updateOrientationViews() {
        this.mHorizontal = findViewById(R.id.horizontal);
        this.mVertical = findViewById(R.id.vertical);
        updateCurrentView();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean needsReorient(int rotation) {
        return this.mCurrentRotation != rotation;
    }

    private void updateCurrentView() {
        resetViews();
        this.mCurrentView = this.mIsVertical ? this.mVertical : this.mHorizontal;
        this.mCurrentView.setVisibility(0);
        this.mNavigationInflaterView.setVertical(this.mIsVertical);
        this.mCurrentRotation = getContextDisplay().getRotation();
        this.mNavigationInflaterView.setAlternativeOrder(this.mCurrentRotation == 1);
        this.mNavigationInflaterView.updateButtonDispatchersCurrentView();
        updateLayoutTransitionsEnabled();
    }

    private void resetViews() {
        this.mHorizontal.setVisibility(8);
        this.mVertical.setVisibility(8);
    }

    private void updateRecentsIcon() {
        this.mDockedIcon.setRotation((this.mDockedStackExists && this.mIsVertical) ? 90.0f : 0.0f);
        getRecentsButton().setImageDrawable(this.mDockedStackExists ? this.mDockedIcon : this.mRecentIcon);
        this.mBarTransitions.reapplyDarkIntensity();
    }

    public void showPinningEnterExitToast(boolean entering) {
        if (entering) {
            this.mScreenPinningNotify.showPinningStartToast();
        } else {
            this.mScreenPinningNotify.showPinningExitToast();
        }
    }

    public void showPinningEscapeToast() {
        this.mScreenPinningNotify.showEscapeToast(this.mNavBarMode == 2, isRecentsButtonVisible());
    }

    public boolean isVertical() {
        return this.mIsVertical;
    }

    public void reorient() {
        updateCurrentView();
        ((NavigationBarFrame) getRootView()).setDeadZone(this.mDeadZone);
        this.mDeadZone.onConfigurationChanged(this.mCurrentRotation);
        this.mBarTransitions.init();
        if (!isLayoutDirectionResolved()) {
            resolveLayoutDirection();
        }
        updateNavButtonIcons();
        getHomeButton().setVertical(this.mIsVertical);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height;
        int w = View.MeasureSpec.getSize(widthMeasureSpec);
        int h = View.MeasureSpec.getSize(heightMeasureSpec);
        boolean newVertical = w > 0 && h > w && !QuickStepContract.isGesturalMode(this.mNavBarMode);
        if (newVertical != this.mIsVertical) {
            this.mIsVertical = newVertical;
            reorient();
            notifyVerticalChangedListener(newVertical);
        }
        if (QuickStepContract.isGesturalMode(this.mNavBarMode)) {
            if (this.mIsVertical) {
                height = getResources().getDimensionPixelSize(17105294);
            } else {
                height = getResources().getDimensionPixelSize(17105292);
            }
            int frameHeight = getResources().getDimensionPixelSize(17105289);
            this.mBarTransitions.setBackgroundFrame(new Rect(0, frameHeight - height, w, h));
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void notifyVerticalChangedListener(boolean newVertical) {
        OnVerticalChangedListener onVerticalChangedListener = this.mOnVerticalChangedListener;
        if (onVerticalChangedListener != null) {
            onVerticalChangedListener.onVerticalChanged(newVertical);
        }
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mTmpLastConfiguration.updateFrom(this.mConfiguration);
        this.mConfiguration.updateFrom(newConfig);
        boolean uiCarModeChanged = updateCarMode();
        updateIcons(this.mTmpLastConfiguration);
        updateRecentsIcon();
        this.mRecentsOnboarding.onConfigurationChanged(this.mConfiguration);
        if (uiCarModeChanged || this.mTmpLastConfiguration.densityDpi != this.mConfiguration.densityDpi || this.mTmpLastConfiguration.getLayoutDirection() != this.mConfiguration.getLayoutDirection()) {
            updateNavButtonIcons();
        }
    }

    private boolean updateCarMode() {
        Configuration configuration = this.mConfiguration;
        if (configuration != null) {
            int uiMode = configuration.uiMode & 15;
            boolean isCarMode = uiMode == 3;
            if (isCarMode != this.mInCarMode) {
                this.mInCarMode = isCarMode;
                this.mUseCarModeUi = false;
            }
        }
        return false;
    }

    private String getResourceName(int resId) {
        if (resId != 0) {
            Resources res = getContext().getResources();
            try {
                return res.getResourceName(resId);
            } catch (Resources.NotFoundException e) {
                return "(unknown)";
            }
        }
        return "(null)";
    }

    private static String visibilityToString(int vis) {
        if (vis != 4) {
            if (vis == 8) {
                return "GONE";
            }
            return "VISIBLE";
        }
        return "INVISIBLE";
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        requestApplyInsets();
        reorient();
        onNavigationModeChanged(this.mNavBarMode);
        setUpSwipeUpOnboarding(isQuickStepSwipeUpEnabled());
        RotationButtonController rotationButtonController = this.mRotationButtonController;
        if (rotationButtonController != null) {
            rotationButtonController.registerListeners();
        }
        this.mEdgeBackGestureHandler.onNavBarAttached();
        getViewTreeObserver().addOnComputeInternalInsetsListener(this.mOnComputeInternalInsetsListener);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ((NavigationModeController) Dependency.get(NavigationModeController.class)).removeListener(this);
        setUpSwipeUpOnboarding(false);
        for (int i = 0; i < this.mButtonDispatchers.size(); i++) {
            this.mButtonDispatchers.valueAt(i).onDestroy();
        }
        RotationButtonController rotationButtonController = this.mRotationButtonController;
        if (rotationButtonController != null) {
            rotationButtonController.unregisterListeners();
        }
        this.mEdgeBackGestureHandler.onNavBarDetached();
        getViewTreeObserver().removeOnComputeInternalInsetsListener(this.mOnComputeInternalInsetsListener);
    }

    private void setUpSwipeUpOnboarding(boolean connectedToOverviewProxy) {
        if (connectedToOverviewProxy) {
            this.mRecentsOnboarding.onConnectedToLauncher();
        } else {
            this.mRecentsOnboarding.onDisconnectedFromLauncher();
        }
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("NavigationBarView {");
        Rect r = new Rect();
        Point size = new Point();
        getContextDisplay().getRealSize(size);
        pw.println(String.format("      this: " + StatusBar.viewInfo(this) + " " + visibilityToString(getVisibility()), new Object[0]));
        getWindowVisibleDisplayFrame(r);
        boolean offscreen = r.right > size.x || r.bottom > size.y;
        StringBuilder sb = new StringBuilder();
        sb.append("      window: ");
        sb.append(r.toShortString());
        sb.append(" ");
        sb.append(visibilityToString(getWindowVisibility()));
        sb.append(offscreen ? " OFFSCREEN!" : "");
        pw.println(sb.toString());
        pw.println(String.format("      mCurrentView: id=%s (%dx%d) %s %f", getResourceName(getCurrentView().getId()), Integer.valueOf(getCurrentView().getWidth()), Integer.valueOf(getCurrentView().getHeight()), visibilityToString(getCurrentView().getVisibility()), Float.valueOf(getCurrentView().getAlpha())));
        Object[] objArr = new Object[3];
        objArr[0] = Integer.valueOf(this.mDisabledFlags);
        objArr[1] = this.mIsVertical ? OOBEEvent.STRING_TRUE : OOBEEvent.STRING_FALSE;
        objArr[2] = Float.valueOf(getLightTransitionsController().getCurrentDarkIntensity());
        pw.println(String.format("      disabled=0x%08x vertical=%s darkIntensity=%.2f", objArr));
        dumpButton(pw, NavigationBarInflaterView.BACK, getBackButton());
        dumpButton(pw, "home", getHomeButton());
        dumpButton(pw, "rcnt", getRecentsButton());
        dumpButton(pw, "rota", getRotateSuggestionButton());
        dumpButton(pw, "a11y", getAccessibilityButton());
        pw.println("    }");
        this.mContextualButtonGroup.dump(pw);
        this.mRecentsOnboarding.dump(pw);
        this.mTintController.dump(pw);
        this.mEdgeBackGestureHandler.dump(pw);
    }

    @Override // android.view.View
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        int leftInset = insets.getSystemWindowInsetLeft();
        int rightInset = insets.getSystemWindowInsetRight();
        setPadding(leftInset, insets.getSystemWindowInsetTop(), rightInset, insets.getSystemWindowInsetBottom());
        this.mEdgeBackGestureHandler.setInsets(leftInset, rightInset);
        return super.onApplyWindowInsets(insets);
    }

    private static void dumpButton(PrintWriter pw, String caption, ButtonDispatcher button) {
        pw.print("      " + caption + ": ");
        if (button == null) {
            pw.print("null");
        } else {
            pw.print(visibilityToString(button.getVisibility()) + " alpha=" + button.getAlpha());
        }
        pw.println();
    }

    public /* synthetic */ void lambda$new$2$NavigationBarView(final Boolean exists) {
        post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NavigationBarView$seIN-E1MF9Wb6jBs3U7jhkEzAV4
            @Override // java.lang.Runnable
            public final void run() {
                NavigationBarView.this.lambda$new$1$NavigationBarView(exists);
            }
        });
    }

    public /* synthetic */ void lambda$new$1$NavigationBarView(Boolean exists) {
        this.mDockedStackExists = exists.booleanValue();
        updateRecentsIcon();
    }
}
