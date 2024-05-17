package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;
import android.hardware.biometrics.BiometricSourceType;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.MathUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;
import com.alibaba.fastjson.asm.Opcodes;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.keyguard.KeyguardClockSwitch;
import com.android.keyguard.KeyguardStatusView;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.DejankUtils;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.fragments.FragmentHostManager;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.qs.QS;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.qs.QSFragment;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.FlingAnimationUtils;
import com.android.systemui.statusbar.GestureRecorder;
import com.android.systemui.statusbar.KeyguardAffordanceView;
import com.android.systemui.statusbar.KeyguardIndicationController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationShelf;
import com.android.systemui.statusbar.PulseExpansionHandler;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.notification.ActivityLaunchAnimator;
import com.android.systemui.statusbar.notification.AnimatableProperty;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.notification.PropertyAnimator;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.notification.stack.AnimationProperties;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.phone.KeyguardAffordanceHelper;
import com.android.systemui.statusbar.phone.KeyguardClockPositionAlgorithm;
import com.android.systemui.statusbar.phone.NotificationPanelView;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.statusbar.policy.KeyguardUserSwitcher;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.util.InjectionInflationController;
import com.xiaopeng.systemui.carmanager.controller.IIcmController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.inject.Inject;
import javax.inject.Named;
/* loaded from: classes21.dex */
public class NotificationPanelView extends PanelView implements ExpandableView.OnHeightChangedListener, View.OnClickListener, NotificationStackScrollLayout.OnOverscrollTopChangedListener, KeyguardAffordanceHelper.Callback, NotificationStackScrollLayout.OnEmptySpaceClickListener, OnHeadsUpChangedListener, QS.HeightListener, ZenModeController.Callback, ConfigurationController.ConfigurationListener, StatusBarStateController.StateListener, PulseExpansionHandler.ExpansionCallback, DynamicPrivacyController.Listener, NotificationWakeUpCoordinator.WakeUpListener {
    private static final int CAP_HEIGHT = 1456;
    static final String COUNTER_PANEL_OPEN = "panel_open";
    private static final String COUNTER_PANEL_OPEN_PEEK = "panel_open_peek";
    static final String COUNTER_PANEL_OPEN_QS = "panel_open_qs";
    private static final boolean DEBUG = false;
    public static final int FLING_COLLAPSE = 1;
    public static final int FLING_EXPAND = 0;
    public static final int FLING_HIDE = 2;
    private static final int FONT_HEIGHT = 2163;
    private static final int MAX_TIME_TO_OPEN_WHEN_FLINGING_FROM_LAUNCHER = 300;
    private final AnimatableProperty PANEL_ALPHA;
    private final AnimationProperties PANEL_ALPHA_IN_PROPERTIES;
    private final AnimationProperties PANEL_ALPHA_OUT_PROPERTIES;
    private final AccessibilityManager mAccessibilityManager;
    private boolean mAffordanceHasPreview;
    @VisibleForTesting
    protected KeyguardAffordanceHelper mAffordanceHelper;
    private Consumer<Boolean> mAffordanceLaunchListener;
    private boolean mAllowExpandForSmallExpansion;
    private final Paint mAlphaPaint;
    private int mAmbientIndicationBottomPadding;
    private final Runnable mAnimateKeyguardBottomAreaInvisibleEndRunnable;
    private final Runnable mAnimateKeyguardStatusBarInvisibleEndRunnable;
    private final Runnable mAnimateKeyguardStatusViewGoneEndRunnable;
    private final Runnable mAnimateKeyguardStatusViewInvisibleEndRunnable;
    private final Runnable mAnimateKeyguardStatusViewVisibleEndRunnable;
    private boolean mAnimateNextPositionUpdate;
    private AnimatorListenerAdapter mAnimatorListenerAdapter;
    protected int mBarState;
    @VisibleForTesting
    protected ViewGroup mBigClockContainer;
    private boolean mBlockTouches;
    private boolean mBlockingExpansionForCurrentTouch;
    private float mBottomAreaShadeAlpha;
    private final ValueAnimator mBottomAreaShadeAlphaAnimator;
    private final KeyguardClockPositionAlgorithm mClockPositionAlgorithm;
    private final KeyguardClockPositionAlgorithm.Result mClockPositionResult;
    private boolean mClosingWithAlphaFadeOut;
    private boolean mCollapsedOnDown;
    private final CommandQueue mCommandQueue;
    private boolean mConflictingQsExpansionGesture;
    private int mCurrentPanelAlpha;
    private int mDarkIconSize;
    private boolean mDelayShowingKeyguardStatusBar;
    private int mDisplayId;
    private float mDownX;
    private float mDownY;
    private boolean mDozing;
    private boolean mDozingOnDown;
    private float mEmptyDragAmount;
    private final NotificationEntryManager mEntryManager;
    private Runnable mExpandAfterLayoutRunnable;
    private float mExpandOffset;
    private boolean mExpandingFromHeadsUp;
    private boolean mExpectingSynthesizedDown;
    private FalsingManager mFalsingManager;
    private boolean mFirstBypassAttempt;
    private FlingAnimationUtils mFlingAnimationUtils;
    private final FragmentHostManager.FragmentListener mFragmentListener;
    private NotificationGroupManager mGroupManager;
    private boolean mHeadsUpAnimatingAway;
    private HeadsUpAppearanceController mHeadsUpAppearanceController;
    private Runnable mHeadsUpExistenceChangedRunnable;
    private int mHeadsUpInset;
    private boolean mHeadsUpPinnedMode;
    private HeadsUpTouchHelper mHeadsUpTouchHelper;
    private boolean mHideIconsDuringNotificationLaunch;
    private int mIndicationBottomPadding;
    private float mInitialHeightOnTouch;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private final InjectionInflationController mInjectionInflationController;
    private boolean mIntercepting;
    private float mInterpolatedDarkAmount;
    private boolean mIsExpanding;
    private boolean mIsExpansionFromHeadsUp;
    private boolean mIsFullWidth;
    private boolean mIsLaunchTransitionFinished;
    private boolean mIsLaunchTransitionRunning;
    private final KeyguardBypassController mKeyguardBypassController;
    private float mKeyguardHeadsUpShowingAmount;
    private KeyguardIndicationController mKeyguardIndicationController;
    private final KeyguardMonitor.Callback mKeyguardMonitorCallback;
    private boolean mKeyguardShowing;
    @VisibleForTesting
    protected KeyguardStatusBarView mKeyguardStatusBar;
    private float mKeyguardStatusBarAnimateAlpha;
    @VisibleForTesting
    protected KeyguardStatusView mKeyguardStatusView;
    private boolean mKeyguardStatusViewAnimating;
    @VisibleForTesting
    final KeyguardUpdateMonitorCallback mKeyguardUpdateCallback;
    private KeyguardUserSwitcher mKeyguardUserSwitcher;
    private boolean mLastAnnouncementWasQuickSettings;
    private String mLastCameraLaunchSource;
    private boolean mLastEventSynthesizedDown;
    private int mLastOrientation;
    private float mLastOverscroll;
    private float mLastTouchX;
    private float mLastTouchY;
    private Runnable mLaunchAnimationEndRunnable;
    private boolean mLaunchingAffordance;
    private float mLinearDarkAmount;
    private boolean mListenForHeadsUp;
    private LockscreenGestureLogger mLockscreenGestureLogger;
    private final NotificationLockscreenUserManager mLockscreenUserManager;
    private int mMaxFadeoutHeight;
    private int mNavigationBarBottomHeight;
    private boolean mNoVisibleNotifications;
    protected NotificationsQuickSettingsContainer mNotificationContainerParent;
    protected NotificationStackScrollLayout mNotificationStackScroller;
    private int mNotificationsHeaderCollideDistance;
    private int mOldLayoutDirection;
    private Runnable mOnReinflationListener;
    private boolean mOnlyAffordanceInThisMotion;
    private int mPanelAlpha;
    private Runnable mPanelAlphaEndAction;
    private boolean mPanelExpanded;
    private int mPositionMinSideMargin;
    private final PowerManager mPowerManager;
    private final PulseExpansionHandler mPulseExpansionHandler;
    private boolean mPulsing;
    private QS mQs;
    private boolean mQsAnimatorExpand;
    private boolean mQsExpandImmediate;
    private boolean mQsExpanded;
    private boolean mQsExpandedWhenExpandingStarted;
    private ValueAnimator mQsExpansionAnimator;
    protected boolean mQsExpansionEnabled;
    private boolean mQsExpansionFromOverscroll;
    protected float mQsExpansionHeight;
    private int mQsFalsingThreshold;
    @VisibleForTesting
    protected FrameLayout mQsFrame;
    private boolean mQsFullyExpanded;
    protected int mQsMaxExpansionHeight;
    protected int mQsMinExpansionHeight;
    private View mQsNavbarScrim;
    private int mQsNotificationTopPadding;
    private int mQsPeekHeight;
    private boolean mQsScrimEnabled;
    private ValueAnimator mQsSizeChangeAnimator;
    private boolean mQsTouchAboveFalsingThreshold;
    private boolean mQsTracking;
    private VelocityTracker mQsVelocityTracker;
    private final ShadeController mShadeController;
    private int mShelfHeight;
    private boolean mShowEmptyShadeView;
    private boolean mShowIconsWhenExpanded;
    private boolean mShowingKeyguardHeadsUp;
    private int mStackScrollerMeasuringPass;
    private boolean mStackScrollerOverscrolling;
    private final ValueAnimator.AnimatorUpdateListener mStatusBarAnimateAlphaListener;
    private int mStatusBarMinHeight;
    private int mThemeResId;
    private ArrayList<Consumer<ExpandableNotificationRow>> mTrackingHeadsUpListeners;
    private int mTrackingPointer;
    private boolean mTwoFingerQsExpandPossible;
    private int mUnlockMoveDistance;
    @VisibleForTesting
    protected KeyguardUpdateMonitor mUpdateMonitor;
    private boolean mUserSetupComplete;
    private ArrayList<Runnable> mVerticalTranslationListener;
    private final NotificationWakeUpCoordinator mWakeUpCoordinator;
    private static final Rect mDummyDirtyRect = new Rect(0, 0, 1, 1);
    private static final Rect mEmptyRect = new Rect();
    private static final AnimationProperties CLOCK_ANIMATION_PROPERTIES = new AnimationProperties().setDuration(360);
    private static final AnimatableProperty KEYGUARD_HEADS_UP_SHOWING_AMOUNT = AnimatableProperty.from("KEYGUARD_HEADS_UP_SHOWING_AMOUNT", new BiConsumer() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NotificationPanelView$8G22_EmtDJSBkKVCqvCP10-xXeA
        @Override // java.util.function.BiConsumer
        public final void accept(Object obj, Object obj2) {
            ((NotificationPanelView) obj).setKeyguardHeadsUpShowingAmount(((Float) obj2).floatValue());
        }
    }, new Function() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NotificationPanelView$3eG2mRDkKhbGo7rATE21NiEDXnI
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            float keyguardHeadsUpShowingAmount;
            keyguardHeadsUpShowingAmount = ((NotificationPanelView) obj).getKeyguardHeadsUpShowingAmount();
            return Float.valueOf(keyguardHeadsUpShowingAmount);
        }
    }, R.id.keyguard_hun_animator_tag, R.id.keyguard_hun_animator_end_tag, R.id.keyguard_hun_animator_start_tag);
    private static final AnimationProperties KEYGUARD_HUN_PROPERTIES = new AnimationProperties().setDuration(360);

    @Inject
    public NotificationPanelView(@Named("view_context") Context context, AttributeSet attrs, InjectionInflationController injectionInflationController, NotificationWakeUpCoordinator coordinator, PulseExpansionHandler pulseExpansionHandler, DynamicPrivacyController dynamicPrivacyController, KeyguardBypassController bypassController, FalsingManager falsingManager) {
        super(context, attrs);
        this.mKeyguardUpdateCallback = new KeyguardUpdateMonitorCallback() { // from class: com.android.systemui.statusbar.phone.NotificationPanelView.1
            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onBiometricAuthenticated(int userId, BiometricSourceType biometricSourceType) {
                if (NotificationPanelView.this.mFirstBypassAttempt && NotificationPanelView.this.mUpdateMonitor.isUnlockingWithBiometricAllowed()) {
                    NotificationPanelView.this.mDelayShowingKeyguardStatusBar = true;
                }
            }

            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onBiometricRunningStateChanged(boolean running, BiometricSourceType biometricSourceType) {
                boolean z = true;
                if (NotificationPanelView.this.mBarState != 1 && NotificationPanelView.this.mBarState != 2) {
                    z = false;
                }
                boolean keyguardOrShadeLocked = z;
                if (!running && NotificationPanelView.this.mFirstBypassAttempt && keyguardOrShadeLocked && !NotificationPanelView.this.mDozing && !NotificationPanelView.this.mDelayShowingKeyguardStatusBar) {
                    NotificationPanelView.this.mFirstBypassAttempt = false;
                    NotificationPanelView.this.animateKeyguardStatusBarIn(360L);
                }
            }

            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onFinishedGoingToSleep(int why) {
                NotificationPanelView notificationPanelView = NotificationPanelView.this;
                notificationPanelView.mFirstBypassAttempt = notificationPanelView.mKeyguardBypassController.getBypassEnabled();
                NotificationPanelView.this.mDelayShowingKeyguardStatusBar = false;
            }
        };
        this.mKeyguardMonitorCallback = new KeyguardMonitor.Callback() { // from class: com.android.systemui.statusbar.phone.NotificationPanelView.2
            @Override // com.android.systemui.statusbar.policy.KeyguardMonitor.Callback
            public void onKeyguardFadingAwayChanged() {
                if (!NotificationPanelView.this.mKeyguardMonitor.isKeyguardFadingAway()) {
                    NotificationPanelView.this.mFirstBypassAttempt = false;
                    NotificationPanelView.this.mDelayShowingKeyguardStatusBar = false;
                }
            }
        };
        this.mQsExpansionEnabled = true;
        this.mClockPositionAlgorithm = new KeyguardClockPositionAlgorithm();
        this.mClockPositionResult = new KeyguardClockPositionAlgorithm.Result();
        this.mQsScrimEnabled = true;
        this.mKeyguardStatusBarAnimateAlpha = 1.0f;
        this.mLastOrientation = -1;
        this.mLastCameraLaunchSource = KeyguardBottomAreaView.CAMERA_LAUNCH_SOURCE_AFFORDANCE;
        this.mHeadsUpExistenceChangedRunnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.NotificationPanelView.3
            @Override // java.lang.Runnable
            public void run() {
                NotificationPanelView.this.setHeadsUpAnimatingAway(false);
                NotificationPanelView.this.notifyBarPanelExpansionChanged();
            }
        };
        this.mLockscreenGestureLogger = new LockscreenGestureLogger();
        this.mNoVisibleNotifications = true;
        this.mHideIconsDuringNotificationLaunch = true;
        this.mTrackingHeadsUpListeners = new ArrayList<>();
        this.mVerticalTranslationListener = new ArrayList<>();
        this.mAlphaPaint = new Paint();
        this.mAnimatorListenerAdapter = new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.phone.NotificationPanelView.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (NotificationPanelView.this.mPanelAlphaEndAction != null) {
                    NotificationPanelView.this.mPanelAlphaEndAction.run();
                }
            }
        };
        this.PANEL_ALPHA = AnimatableProperty.from("panelAlpha", new BiConsumer() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$aKsp0zdf_wKFZXD1TonJ2cFEsN4
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((NotificationPanelView) obj).setPanelAlphaInternal(((Float) obj2).floatValue());
            }
        }, new Function() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$SmdYpsZqQm1fpR9OgK3SiEL3pJQ
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Float.valueOf(((NotificationPanelView) obj).getCurrentPanelAlpha());
            }
        }, R.id.panel_alpha_animator_tag, R.id.panel_alpha_animator_start_tag, R.id.panel_alpha_animator_end_tag);
        this.PANEL_ALPHA_OUT_PROPERTIES = new AnimationProperties().setDuration(150L).setCustomInterpolator(this.PANEL_ALPHA.getProperty(), Interpolators.ALPHA_OUT);
        this.PANEL_ALPHA_IN_PROPERTIES = new AnimationProperties().setDuration(200L).setAnimationFinishListener(this.mAnimatorListenerAdapter).setCustomInterpolator(this.PANEL_ALPHA.getProperty(), Interpolators.ALPHA_IN);
        this.mEntryManager = (NotificationEntryManager) Dependency.get(NotificationEntryManager.class);
        this.mLockscreenUserManager = (NotificationLockscreenUserManager) Dependency.get(NotificationLockscreenUserManager.class);
        this.mShadeController = (ShadeController) Dependency.get(ShadeController.class);
        this.mKeyguardHeadsUpShowingAmount = 0.0f;
        this.mAnimateKeyguardStatusViewInvisibleEndRunnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.NotificationPanelView.9
            @Override // java.lang.Runnable
            public void run() {
                NotificationPanelView.this.mKeyguardStatusViewAnimating = false;
                NotificationPanelView.this.mKeyguardStatusView.setVisibility(4);
            }
        };
        this.mAnimateKeyguardStatusViewGoneEndRunnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.NotificationPanelView.10
            @Override // java.lang.Runnable
            public void run() {
                NotificationPanelView.this.mKeyguardStatusViewAnimating = false;
                NotificationPanelView.this.mKeyguardStatusView.setVisibility(8);
            }
        };
        this.mAnimateKeyguardStatusViewVisibleEndRunnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.NotificationPanelView.11
            @Override // java.lang.Runnable
            public void run() {
                NotificationPanelView.this.mKeyguardStatusViewAnimating = false;
            }
        };
        this.mAnimateKeyguardStatusBarInvisibleEndRunnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.NotificationPanelView.12
            @Override // java.lang.Runnable
            public void run() {
                NotificationPanelView.this.mKeyguardStatusBar.setVisibility(4);
                NotificationPanelView.this.mKeyguardStatusBar.setAlpha(1.0f);
                NotificationPanelView.this.mKeyguardStatusBarAnimateAlpha = 1.0f;
            }
        };
        this.mStatusBarAnimateAlphaListener = new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.phone.NotificationPanelView.14
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                NotificationPanelView.this.mKeyguardStatusBarAnimateAlpha = ((Float) animation.getAnimatedValue()).floatValue();
                NotificationPanelView.this.updateHeaderKeyguardAlpha();
            }
        };
        this.mAnimateKeyguardBottomAreaInvisibleEndRunnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.NotificationPanelView.15
            @Override // java.lang.Runnable
            public void run() {
                NotificationPanelView.this.mKeyguardBottomArea.setVisibility(8);
            }
        };
        this.mFragmentListener = new AnonymousClass22();
        setWillNotDraw(true);
        this.mInjectionInflationController = injectionInflationController;
        this.mFalsingManager = falsingManager;
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
        this.mWakeUpCoordinator = coordinator;
        this.mAccessibilityManager = (AccessibilityManager) context.getSystemService(AccessibilityManager.class);
        setAccessibilityPaneTitle(determineAccessibilityPaneTitle());
        this.mAlphaPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        setPanelAlpha(255, false);
        this.mCommandQueue = (CommandQueue) SysUiServiceProvider.getComponent(context, CommandQueue.class);
        this.mDisplayId = context.getDisplayId();
        this.mPulseExpansionHandler = pulseExpansionHandler;
        pulseExpansionHandler.setPulseExpandAbortListener(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NotificationPanelView$_yBzfez4yNorLL4Wz7TE5dCgf3o
            @Override // java.lang.Runnable
            public final void run() {
                NotificationPanelView.this.lambda$new$0$NotificationPanelView();
            }
        });
        this.mThemeResId = context.getThemeResId();
        this.mKeyguardBypassController = bypassController;
        this.mUpdateMonitor = KeyguardUpdateMonitor.getInstance(this.mContext);
        this.mFirstBypassAttempt = this.mKeyguardBypassController.getBypassEnabled();
        this.mKeyguardMonitor.addCallback(this.mKeyguardMonitorCallback);
        dynamicPrivacyController.addListener(this);
        this.mBottomAreaShadeAlphaAnimator = ValueAnimator.ofFloat(1.0f, 0.0f);
        this.mBottomAreaShadeAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NotificationPanelView$NSwb4zrunwx8nVzrQ3gQl4T8b5M
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                NotificationPanelView.this.lambda$new$1$NotificationPanelView(valueAnimator);
            }
        });
        this.mBottomAreaShadeAlphaAnimator.setDuration(160L);
        this.mBottomAreaShadeAlphaAnimator.setInterpolator(Interpolators.ALPHA_OUT);
    }

    public /* synthetic */ void lambda$new$0$NotificationPanelView() {
        QS qs = this.mQs;
        if (qs != null) {
            qs.animateHeaderSlidingOut();
        }
    }

    public /* synthetic */ void lambda$new$1$NotificationPanelView(ValueAnimator animation) {
        this.mBottomAreaShadeAlpha = ((Float) animation.getAnimatedValue()).floatValue();
        updateKeyguardBottomAreaAlpha();
    }

    public boolean hasCustomClock() {
        return this.mKeyguardStatusView.hasCustomClock();
    }

    private void setStatusBar(StatusBar bar) {
        this.mStatusBar = bar;
        this.mKeyguardBottomArea.setStatusBar(this.mStatusBar);
    }

    @Override // com.android.systemui.statusbar.phone.PanelView, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mKeyguardStatusBar = (KeyguardStatusBarView) findViewById(R.id.keyguard_header);
        this.mKeyguardStatusView = (KeyguardStatusView) findViewById(R.id.keyguard_status_view);
        KeyguardClockSwitch keyguardClockSwitch = (KeyguardClockSwitch) findViewById(R.id.keyguard_clock_container);
        this.mBigClockContainer = (ViewGroup) findViewById(R.id.big_clock_container);
        keyguardClockSwitch.setBigClockContainer(this.mBigClockContainer);
        this.mNotificationContainerParent = (NotificationsQuickSettingsContainer) findViewById(R.id.notification_container_parent);
        this.mNotificationStackScroller = (NotificationStackScrollLayout) findViewById(R.id.notification_stack_scroller);
        this.mNotificationStackScroller.setOnHeightChangedListener(this);
        this.mNotificationStackScroller.setOverscrollTopChangedListener(this);
        this.mNotificationStackScroller.setOnEmptySpaceClickListener(this);
        final NotificationStackScrollLayout notificationStackScrollLayout = this.mNotificationStackScroller;
        Objects.requireNonNull(notificationStackScrollLayout);
        addTrackingHeadsUpListener(new Consumer() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$hB_2bxao9PtuBwZm92el8Nt3UKY
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                NotificationStackScrollLayout.this.setTrackingHeadsUp((ExpandableNotificationRow) obj);
            }
        });
        this.mKeyguardBottomArea = (KeyguardBottomAreaView) findViewById(R.id.keyguard_bottom_area);
        this.mQsNavbarScrim = findViewById(R.id.qs_navbar_scrim);
        this.mLastOrientation = getResources().getConfiguration().orientation;
        initBottomArea();
        this.mWakeUpCoordinator.setStackScroller(this.mNotificationStackScroller);
        this.mQsFrame = (FrameLayout) findViewById(R.id.qs_frame);
        this.mPulseExpansionHandler.setUp(this.mNotificationStackScroller, this, this.mShadeController);
        this.mWakeUpCoordinator.addListener(new NotificationWakeUpCoordinator.WakeUpListener() { // from class: com.android.systemui.statusbar.phone.NotificationPanelView.5
            @Override // com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator.WakeUpListener
            public void onFullyHiddenChanged(boolean isFullyHidden) {
                NotificationPanelView.this.updateKeyguardStatusBarForHeadsUp();
            }

            @Override // com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator.WakeUpListener
            public void onPulseExpansionChanged(boolean expandingChanged) {
                if (NotificationPanelView.this.mKeyguardBypassController.getBypassEnabled()) {
                    NotificationPanelView.this.requestScrollerTopPaddingUpdate(false);
                    NotificationPanelView.this.updateQSPulseExpansion();
                }
            }
        });
    }

    @Override // com.android.systemui.statusbar.phone.PanelView, android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        FragmentHostManager.get(this).addTagListener(QS.TAG, this.mFragmentListener);
        ((StatusBarStateController) Dependency.get(StatusBarStateController.class)).addCallback(this);
        ((ZenModeController) Dependency.get(ZenModeController.class)).addCallback(this);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this);
        this.mUpdateMonitor.registerCallback(this.mKeyguardUpdateCallback);
        onThemeChanged();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        FragmentHostManager.get(this).removeTagListener(QS.TAG, this.mFragmentListener);
        ((StatusBarStateController) Dependency.get(StatusBarStateController.class)).removeCallback(this);
        ((ZenModeController) Dependency.get(ZenModeController.class)).removeCallback(this);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).removeCallback(this);
        this.mUpdateMonitor.removeCallback(this.mKeyguardUpdateCallback);
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected void loadDimens() {
        super.loadDimens();
        this.mFlingAnimationUtils = new FlingAnimationUtils(getContext(), 0.4f);
        this.mStatusBarMinHeight = getResources().getDimensionPixelSize(17105438);
        this.mQsPeekHeight = getResources().getDimensionPixelSize(R.dimen.qs_peek_height);
        this.mNotificationsHeaderCollideDistance = getResources().getDimensionPixelSize(R.dimen.header_notifications_collide_distance);
        this.mUnlockMoveDistance = getResources().getDimensionPixelOffset(R.dimen.unlock_move_distance);
        this.mClockPositionAlgorithm.loadDimens(getResources());
        this.mQsFalsingThreshold = getResources().getDimensionPixelSize(R.dimen.qs_falsing_threshold);
        this.mPositionMinSideMargin = getResources().getDimensionPixelSize(R.dimen.notification_panel_min_side_margin);
        this.mMaxFadeoutHeight = getResources().getDimensionPixelSize(R.dimen.max_notification_fadeout_height);
        this.mIndicationBottomPadding = getResources().getDimensionPixelSize(R.dimen.keyguard_indication_bottom_padding);
        this.mQsNotificationTopPadding = getResources().getDimensionPixelSize(R.dimen.qs_notification_padding);
        this.mShelfHeight = getResources().getDimensionPixelSize(R.dimen.notification_shelf_height);
        this.mDarkIconSize = getResources().getDimensionPixelSize(R.dimen.status_bar_icon_drawing_size_dark);
        int statusbarHeight = getResources().getDimensionPixelSize(17105438);
        this.mHeadsUpInset = getResources().getDimensionPixelSize(R.dimen.heads_up_status_bar_padding) + statusbarHeight;
    }

    public void setLaunchAffordanceListener(Consumer<Boolean> listener) {
        this.mAffordanceLaunchListener = listener;
    }

    public void updateResources() {
        Resources res = getResources();
        int qsWidth = res.getDimensionPixelSize(R.dimen.qs_panel_width);
        int panelGravity = getResources().getInteger(R.integer.notification_panel_layout_gravity);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) this.mQsFrame.getLayoutParams();
        if (lp.width != qsWidth || lp.gravity != panelGravity) {
            lp.width = qsWidth;
            lp.gravity = panelGravity;
            this.mQsFrame.setLayoutParams(lp);
        }
        int panelWidth = res.getDimensionPixelSize(R.dimen.notification_panel_width);
        FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) this.mNotificationStackScroller.getLayoutParams();
        if (lp2.width != panelWidth || lp2.gravity != panelGravity) {
            lp2.width = panelWidth;
            lp2.gravity = panelGravity;
            this.mNotificationStackScroller.setLayoutParams(lp2);
        }
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onDensityOrFontScaleChanged() {
        updateShowEmptyShadeView();
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onThemeChanged() {
        int themeResId = getContext().getThemeResId();
        if (this.mThemeResId == themeResId) {
            return;
        }
        this.mThemeResId = themeResId;
        reInflateViews();
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onOverlayChanged() {
        reInflateViews();
    }

    private void reInflateViews() {
        updateShowEmptyShadeView();
        int index = indexOfChild(this.mKeyguardStatusView);
        removeView(this.mKeyguardStatusView);
        this.mKeyguardStatusView = (KeyguardStatusView) this.mInjectionInflationController.injectable(LayoutInflater.from(this.mContext)).inflate(R.layout.keyguard_status_view, (ViewGroup) this, false);
        addView(this.mKeyguardStatusView, index);
        this.mBigClockContainer.removeAllViews();
        KeyguardClockSwitch keyguardClockSwitch = (KeyguardClockSwitch) findViewById(R.id.keyguard_clock_container);
        keyguardClockSwitch.setBigClockContainer(this.mBigClockContainer);
        int index2 = indexOfChild(this.mKeyguardBottomArea);
        removeView(this.mKeyguardBottomArea);
        KeyguardBottomAreaView oldBottomArea = this.mKeyguardBottomArea;
        this.mKeyguardBottomArea = (KeyguardBottomAreaView) this.mInjectionInflationController.injectable(LayoutInflater.from(this.mContext)).inflate(R.layout.keyguard_bottom_area, (ViewGroup) this, false);
        this.mKeyguardBottomArea.initFrom(oldBottomArea);
        addView(this.mKeyguardBottomArea, index2);
        initBottomArea();
        this.mKeyguardIndicationController.setIndicationArea(this.mKeyguardBottomArea);
        onDozeAmountChanged(this.mStatusBarStateController.getDozeAmount(), this.mStatusBarStateController.getInterpolatedDozeAmount());
        KeyguardStatusBarView keyguardStatusBarView = this.mKeyguardStatusBar;
        if (keyguardStatusBarView != null) {
            keyguardStatusBarView.onThemeChanged();
        }
        setKeyguardStatusViewVisibility(this.mBarState, false, false);
        setKeyguardBottomAreaVisibility(this.mBarState, false);
        Runnable runnable = this.mOnReinflationListener;
        if (runnable != null) {
            runnable.run();
        }
    }

    private void initBottomArea() {
        this.mAffordanceHelper = new KeyguardAffordanceHelper(this, getContext(), this.mFalsingManager);
        this.mKeyguardBottomArea.setAffordanceHelper(this.mAffordanceHelper);
        this.mKeyguardBottomArea.setStatusBar(this.mStatusBar);
        this.mKeyguardBottomArea.setUserSetupComplete(this.mUserSetupComplete);
    }

    public void setKeyguardIndicationController(KeyguardIndicationController indicationController) {
        this.mKeyguardIndicationController = indicationController;
        this.mKeyguardIndicationController.setIndicationArea(this.mKeyguardBottomArea);
    }

    @Override // com.android.systemui.statusbar.phone.PanelView, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        QS qs;
        super.onLayout(changed, left, top, right, bottom);
        setIsFullWidth(this.mNotificationStackScroller.getWidth() == getWidth());
        this.mKeyguardStatusView.setPivotX(getWidth() / 2);
        KeyguardStatusView keyguardStatusView = this.mKeyguardStatusView;
        keyguardStatusView.setPivotY(keyguardStatusView.getClockTextSize() * 0.34521484f);
        int oldMaxHeight = this.mQsMaxExpansionHeight;
        QS qs2 = this.mQs;
        if (qs2 != null) {
            this.mQsMinExpansionHeight = this.mKeyguardShowing ? 0 : qs2.getQsMinExpansionHeight();
            this.mQsMaxExpansionHeight = this.mQs.getDesiredHeight();
            this.mNotificationStackScroller.setMaxTopPadding(this.mQsMaxExpansionHeight + this.mQsNotificationTopPadding);
        }
        positionClockAndNotifications();
        if (this.mQsExpanded && this.mQsFullyExpanded) {
            this.mQsExpansionHeight = this.mQsMaxExpansionHeight;
            requestScrollerTopPaddingUpdate(false);
            requestPanelHeightUpdate();
            int i = this.mQsMaxExpansionHeight;
            if (i != oldMaxHeight) {
                startQsSizeChangeAnimation(oldMaxHeight, i);
            }
        } else if (!this.mQsExpanded) {
            setQsExpansion(this.mQsMinExpansionHeight + this.mLastOverscroll);
        }
        updateExpandedHeight(getExpandedHeight());
        updateHeader();
        if (this.mQsSizeChangeAnimator == null && (qs = this.mQs) != null) {
            qs.setHeightOverride(qs.getDesiredHeight());
        }
        updateMaxHeadsUpTranslation();
        updateGestureExclusionRect();
        Runnable runnable = this.mExpandAfterLayoutRunnable;
        if (runnable != null) {
            runnable.run();
            this.mExpandAfterLayoutRunnable = null;
        }
    }

    private void updateGestureExclusionRect() {
        List singletonList;
        Rect exclusionRect = calculateGestureExclusionRect();
        if (exclusionRect.isEmpty()) {
            singletonList = Collections.EMPTY_LIST;
        } else {
            singletonList = Collections.singletonList(exclusionRect);
        }
        setSystemGestureExclusionRects(singletonList);
    }

    private Rect calculateGestureExclusionRect() {
        Rect exclusionRect = null;
        Region touchableRegion = this.mHeadsUpManager.calculateTouchableRegion();
        if (isFullyCollapsed() && touchableRegion != null) {
            exclusionRect = touchableRegion.getBounds();
        }
        if (exclusionRect != null) {
            return exclusionRect;
        }
        return mEmptyRect;
    }

    private void setIsFullWidth(boolean isFullWidth) {
        this.mIsFullWidth = isFullWidth;
        this.mNotificationStackScroller.setIsFullWidth(isFullWidth);
    }

    private void startQsSizeChangeAnimation(int oldHeight, int newHeight) {
        ValueAnimator valueAnimator = this.mQsSizeChangeAnimator;
        if (valueAnimator != null) {
            oldHeight = ((Integer) valueAnimator.getAnimatedValue()).intValue();
            this.mQsSizeChangeAnimator.cancel();
        }
        this.mQsSizeChangeAnimator = ValueAnimator.ofInt(oldHeight, newHeight);
        this.mQsSizeChangeAnimator.setDuration(300L);
        this.mQsSizeChangeAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        this.mQsSizeChangeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.phone.NotificationPanelView.6
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                NotificationPanelView.this.requestScrollerTopPaddingUpdate(false);
                NotificationPanelView.this.requestPanelHeightUpdate();
                int height = ((Integer) NotificationPanelView.this.mQsSizeChangeAnimator.getAnimatedValue()).intValue();
                NotificationPanelView.this.mQs.setHeightOverride(height);
            }
        });
        this.mQsSizeChangeAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.phone.NotificationPanelView.7
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                NotificationPanelView.this.mQsSizeChangeAnimator = null;
            }
        });
        this.mQsSizeChangeAnimator.start();
    }

    private void positionClockAndNotifications() {
        int totalHeight;
        boolean animate = this.mNotificationStackScroller.isAddOrRemoveAnimationPending();
        boolean animateClock = animate || this.mAnimateNextPositionUpdate;
        if (this.mBarState != 1) {
            totalHeight = getUnlockedStackScrollerPadding();
        } else {
            int totalHeight2 = getHeight();
            int bottomPadding = Math.max(this.mIndicationBottomPadding, this.mAmbientIndicationBottomPadding);
            int clockPreferredY = this.mKeyguardStatusView.getClockPreferredY(totalHeight2);
            boolean bypassEnabled = this.mKeyguardBypassController.getBypassEnabled();
            boolean hasVisibleNotifications = (bypassEnabled || this.mNotificationStackScroller.getVisibleNotificationCount() == 0) ? false : true;
            this.mKeyguardStatusView.setHasVisibleNotifications(hasVisibleNotifications);
            this.mClockPositionAlgorithm.setup(this.mStatusBarMinHeight, totalHeight2 - bottomPadding, this.mNotificationStackScroller.getIntrinsicContentHeight(), getExpandedFraction(), totalHeight2, (int) ((this.mKeyguardStatusView.getHeight() - (this.mShelfHeight / 2.0f)) - (this.mDarkIconSize / 2.0f)), clockPreferredY, hasCustomClock(), hasVisibleNotifications, this.mInterpolatedDarkAmount, this.mEmptyDragAmount, bypassEnabled, getUnlockedStackScrollerPadding());
            this.mClockPositionAlgorithm.run(this.mClockPositionResult);
            PropertyAnimator.setProperty(this.mKeyguardStatusView, AnimatableProperty.X, this.mClockPositionResult.clockX, CLOCK_ANIMATION_PROPERTIES, animateClock);
            PropertyAnimator.setProperty(this.mKeyguardStatusView, AnimatableProperty.Y, this.mClockPositionResult.clockY, CLOCK_ANIMATION_PROPERTIES, animateClock);
            updateNotificationTranslucency();
            updateClock();
            totalHeight = this.mClockPositionResult.stackScrollerPaddingExpanded;
        }
        this.mNotificationStackScroller.setIntrinsicPadding(totalHeight);
        this.mKeyguardBottomArea.setAntiBurnInOffsetX(this.mClockPositionResult.clockX);
        this.mStackScrollerMeasuringPass++;
        requestScrollerTopPaddingUpdate(animate);
        this.mStackScrollerMeasuringPass = 0;
        this.mAnimateNextPositionUpdate = false;
    }

    private int getUnlockedStackScrollerPadding() {
        QS qs = this.mQs;
        return (qs != null ? qs.getHeader().getHeight() : 0) + this.mQsPeekHeight + this.mQsNotificationTopPadding;
    }

    public int computeMaxKeyguardNotifications(int maximum) {
        float minPadding = this.mClockPositionAlgorithm.getMinStackScrollerPadding();
        int notificationPadding = Math.max(1, getResources().getDimensionPixelSize(R.dimen.notification_divider_height));
        NotificationShelf shelf = this.mNotificationStackScroller.getNotificationShelf();
        float shelfSize = shelf.getVisibility() == 8 ? 0.0f : shelf.getIntrinsicHeight() + notificationPadding;
        float availableSpace = (((this.mNotificationStackScroller.getHeight() - minPadding) - shelfSize) - Math.max(this.mIndicationBottomPadding, this.mAmbientIndicationBottomPadding)) - this.mKeyguardStatusView.getLogoutButtonHeight();
        int count = 0;
        for (int i = 0; i < this.mNotificationStackScroller.getChildCount(); i++) {
            ExpandableView child = (ExpandableView) this.mNotificationStackScroller.getChildAt(i);
            if (child instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow row = (ExpandableNotificationRow) child;
                NotificationGroupManager notificationGroupManager = this.mGroupManager;
                boolean suppressedSummary = notificationGroupManager != null && notificationGroupManager.isSummaryOfSuppressedGroup(row.getStatusBarNotification());
                if (!suppressedSummary && this.mLockscreenUserManager.shouldShowOnKeyguard(row.getEntry()) && !row.isRemoved()) {
                    availableSpace -= child.getMinHeight(true) + notificationPadding;
                    if (availableSpace >= 0.0f && count < maximum) {
                        count++;
                    } else if (availableSpace > (-shelfSize)) {
                        for (int j = i + 1; j < this.mNotificationStackScroller.getChildCount(); j++) {
                            if (this.mNotificationStackScroller.getChildAt(j) instanceof ExpandableNotificationRow) {
                                return count;
                            }
                        }
                        return count + 1;
                    } else {
                        return count;
                    }
                }
            }
        }
        return count;
    }

    private void updateClock() {
        if (!this.mKeyguardStatusViewAnimating) {
            this.mKeyguardStatusView.setAlpha(this.mClockPositionResult.clockAlpha);
        }
    }

    public void animateToFullShade(long delay) {
        this.mNotificationStackScroller.goToFullShade(delay);
        requestLayout();
        this.mAnimateNextPositionUpdate = true;
    }

    public void setQsExpansionEnabled(boolean qsExpansionEnabled) {
        this.mQsExpansionEnabled = qsExpansionEnabled;
        QS qs = this.mQs;
        if (qs == null) {
            return;
        }
        qs.setHeaderClickable(qsExpansionEnabled);
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    public void resetViews(boolean animate) {
        this.mIsLaunchTransitionFinished = false;
        this.mBlockTouches = false;
        if (!this.mLaunchingAffordance) {
            this.mAffordanceHelper.reset(false);
            this.mLastCameraLaunchSource = KeyguardBottomAreaView.CAMERA_LAUNCH_SOURCE_AFFORDANCE;
        }
        this.mStatusBar.getGutsManager().closeAndSaveGuts(true, true, true, -1, -1, true);
        if (animate) {
            animateCloseQs(true);
        } else {
            closeQs();
        }
        this.mNotificationStackScroller.setOverScrollAmount(0.0f, true, animate, !animate);
        this.mNotificationStackScroller.resetScrollPosition();
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    public void collapse(boolean delayed, float speedUpFactor) {
        if (!canPanelBeCollapsed()) {
            return;
        }
        if (this.mQsExpanded) {
            this.mQsExpandImmediate = true;
            this.mNotificationStackScroller.setShouldShowShelfOnly(true);
        }
        super.collapse(delayed, speedUpFactor);
    }

    public void closeQs() {
        cancelQsAnimation();
        setQsExpansion(this.mQsMinExpansionHeight);
    }

    public void animateCloseQs(boolean animateAway) {
        ValueAnimator valueAnimator = this.mQsExpansionAnimator;
        if (valueAnimator != null) {
            if (!this.mQsAnimatorExpand) {
                return;
            }
            float height = this.mQsExpansionHeight;
            valueAnimator.cancel();
            setQsExpansion(height);
        }
        flingSettings(0.0f, animateAway ? 2 : 1);
    }

    public void expandWithQs() {
        if (this.mQsExpansionEnabled) {
            this.mQsExpandImmediate = true;
            this.mNotificationStackScroller.setShouldShowShelfOnly(true);
        }
        if (isFullyCollapsed()) {
            expand(true);
        } else {
            flingSettings(0.0f, 0);
        }
    }

    public void expandWithoutQs() {
        if (isQsExpanded()) {
            flingSettings(0.0f, 1);
        } else {
            expand(true);
        }
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    public void fling(float vel, boolean expand) {
        GestureRecorder gr = ((PhoneStatusBarView) this.mBar).mBar.getGestureRecorder();
        if (gr != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("fling ");
            sb.append(vel > 0.0f ? IIcmController.CMD_OPEN : "closed");
            String sb2 = sb.toString();
            gr.tag(sb2, "notifications,v=" + vel);
        }
        super.fling(vel, expand);
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected void flingToHeight(float vel, boolean expand, float target, float collapseSpeedUpFactor, boolean expandBecauseOfFalsing) {
        this.mHeadsUpTouchHelper.notifyFling(!expand);
        setClosingWithAlphaFadeout((expand || isOnKeyguard() || getFadeoutAlpha() != 1.0f) ? false : true);
        super.flingToHeight(vel, expand, target, collapseSpeedUpFactor, expandBecauseOfFalsing);
    }

    @Override // com.android.systemui.statusbar.phone.PanelView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.mBlockTouches) {
            return false;
        }
        if (this.mQsFullyExpanded && this.mQs.onInterceptTouchEvent(event)) {
            return false;
        }
        initDownStates(event);
        if (this.mStatusBar.isBouncerShowing()) {
            return true;
        }
        if (this.mBar.panelEnabled() && this.mHeadsUpTouchHelper.onInterceptTouchEvent(event)) {
            this.mIsExpansionFromHeadsUp = true;
            MetricsLogger.count(this.mContext, COUNTER_PANEL_OPEN, 1);
            MetricsLogger.count(this.mContext, COUNTER_PANEL_OPEN_PEEK, 1);
            return true;
        } else if (shouldQuickSettingsIntercept(this.mDownX, this.mDownY, 0.0f) || !this.mPulseExpansionHandler.onInterceptTouchEvent(event)) {
            if (isFullyCollapsed() || !onQsIntercept(event)) {
                return super.onInterceptTouchEvent(event);
            }
            return true;
        } else {
            return true;
        }
    }

    private boolean onQsIntercept(MotionEvent event) {
        int upPointer;
        int pointerIndex = event.findPointerIndex(this.mTrackingPointer);
        if (pointerIndex < 0) {
            pointerIndex = 0;
            this.mTrackingPointer = event.getPointerId(0);
        }
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);
        int actionMasked = event.getActionMasked();
        if (actionMasked == 0) {
            this.mIntercepting = true;
            this.mInitialTouchY = y;
            this.mInitialTouchX = x;
            initVelocityTracker();
            trackMovement(event);
            if (shouldQuickSettingsIntercept(this.mInitialTouchX, this.mInitialTouchY, 0.0f)) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            if (this.mQsExpansionAnimator != null) {
                onQsExpansionStarted();
                this.mInitialHeightOnTouch = this.mQsExpansionHeight;
                this.mQsTracking = true;
                this.mIntercepting = false;
                this.mNotificationStackScroller.cancelLongPress();
            }
        } else {
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    float h = y - this.mInitialTouchY;
                    trackMovement(event);
                    if (this.mQsTracking) {
                        setQsExpansion(this.mInitialHeightOnTouch + h);
                        trackMovement(event);
                        this.mIntercepting = false;
                        return true;
                    } else if (Math.abs(h) > this.mTouchSlop && Math.abs(h) > Math.abs(x - this.mInitialTouchX) && shouldQuickSettingsIntercept(this.mInitialTouchX, this.mInitialTouchY, h)) {
                        this.mQsTracking = true;
                        onQsExpansionStarted();
                        notifyExpandingFinished();
                        this.mInitialHeightOnTouch = this.mQsExpansionHeight;
                        this.mInitialTouchY = y;
                        this.mInitialTouchX = x;
                        this.mIntercepting = false;
                        this.mNotificationStackScroller.cancelLongPress();
                        return true;
                    }
                } else if (actionMasked != 3) {
                    if (actionMasked == 6 && this.mTrackingPointer == (upPointer = event.getPointerId(event.getActionIndex()))) {
                        int newIndex = event.getPointerId(0) != upPointer ? 0 : 1;
                        this.mTrackingPointer = event.getPointerId(newIndex);
                        this.mInitialTouchX = event.getX(newIndex);
                        this.mInitialTouchY = event.getY(newIndex);
                    }
                }
            }
            trackMovement(event);
            if (this.mQsTracking) {
                flingQsWithCurrentVelocity(y, event.getActionMasked() == 3);
                this.mQsTracking = false;
            }
            this.mIntercepting = false;
        }
        return false;
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected boolean isInContentBounds(float x, float y) {
        float stackScrollerX = this.mNotificationStackScroller.getX();
        return !this.mNotificationStackScroller.isBelowLastNotification(x - stackScrollerX, y) && stackScrollerX < x && x < ((float) this.mNotificationStackScroller.getWidth()) + stackScrollerX;
    }

    private void initDownStates(MotionEvent event) {
        if (event.getActionMasked() == 0) {
            this.mOnlyAffordanceInThisMotion = false;
            this.mQsTouchAboveFalsingThreshold = this.mQsFullyExpanded;
            this.mDozingOnDown = isDozing();
            this.mDownX = event.getX();
            this.mDownY = event.getY();
            this.mCollapsedOnDown = isFullyCollapsed();
            this.mListenForHeadsUp = this.mCollapsedOnDown && this.mHeadsUpManager.hasPinnedHeadsUp();
            boolean z = this.mExpectingSynthesizedDown;
            this.mAllowExpandForSmallExpansion = z;
            this.mTouchSlopExceededBeforeDown = z;
            if (z) {
                this.mLastEventSynthesizedDown = true;
                return;
            } else {
                this.mLastEventSynthesizedDown = false;
                return;
            }
        }
        this.mLastEventSynthesizedDown = false;
    }

    private void flingQsWithCurrentVelocity(float y, boolean isCancelMotionEvent) {
        float vel = getCurrentQSVelocity();
        boolean expandsQs = flingExpandsQs(vel);
        if (expandsQs) {
            logQsSwipeDown(y);
        }
        flingSettings(vel, (!expandsQs || isCancelMotionEvent) ? 1 : 0);
    }

    private void logQsSwipeDown(float y) {
        int gesture;
        float vel = getCurrentQSVelocity();
        if (this.mBarState == 1) {
            gesture = Opcodes.INSTANCEOF;
        } else {
            gesture = 194;
        }
        this.mLockscreenGestureLogger.write(gesture, (int) ((y - this.mInitialTouchY) / this.mStatusBar.getDisplayDensity()), (int) (vel / this.mStatusBar.getDisplayDensity()));
    }

    private boolean flingExpandsQs(float vel) {
        if (this.mFalsingManager.isUnlockingDisabled() || isFalseTouch()) {
            return false;
        }
        return Math.abs(vel) < this.mFlingAnimationUtils.getMinVelocityPxPerSecond() ? getQsExpansionFraction() > 0.5f : vel > 0.0f;
    }

    private boolean isFalseTouch() {
        if (!needsAntiFalsing()) {
            return false;
        }
        if (this.mFalsingManager.isClassiferEnabled()) {
            return this.mFalsingManager.isFalseTouch();
        }
        return !this.mQsTouchAboveFalsingThreshold;
    }

    private float getQsExpansionFraction() {
        float f = this.mQsExpansionHeight;
        int i = this.mQsMinExpansionHeight;
        return Math.min(1.0f, (f - i) / (this.mQsMaxExpansionHeight - i));
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected boolean shouldExpandWhenNotFlinging() {
        if (super.shouldExpandWhenNotFlinging()) {
            return true;
        }
        if (this.mAllowExpandForSmallExpansion) {
            long timeSinceDown = SystemClock.uptimeMillis() - this.mDownTime;
            return timeSinceDown <= 300;
        }
        return false;
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected float getOpeningHeight() {
        return this.mNotificationStackScroller.getOpeningHeight();
    }

    @Override // com.android.systemui.statusbar.phone.PanelView, android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        QS qs;
        if (this.mBlockTouches || (((qs = this.mQs) != null && qs.isCustomizing()) || this.mStatusBar.isBouncerShowingScrimmed())) {
            return false;
        }
        if (event.getAction() == 1 || event.getAction() == 3) {
            this.mBlockingExpansionForCurrentTouch = false;
        }
        if (this.mLastEventSynthesizedDown && event.getAction() == 1) {
            expand(true);
        }
        initDownStates(event);
        if (this.mIsExpanding || shouldQuickSettingsIntercept(this.mDownX, this.mDownY, 0.0f) || !this.mPulseExpansionHandler.onTouchEvent(event)) {
            if (this.mListenForHeadsUp && !this.mHeadsUpTouchHelper.isTrackingHeadsUp() && this.mHeadsUpTouchHelper.onInterceptTouchEvent(event)) {
                this.mIsExpansionFromHeadsUp = true;
                MetricsLogger.count(this.mContext, COUNTER_PANEL_OPEN_PEEK, 1);
            }
            boolean handled = false;
            if ((!this.mIsExpanding || this.mHintAnimationRunning) && !this.mQsExpanded && this.mBarState != 0 && !this.mDozing) {
                handled = false | this.mAffordanceHelper.onTouchEvent(event);
            }
            if (this.mOnlyAffordanceInThisMotion) {
                return true;
            }
            boolean handled2 = handled | this.mHeadsUpTouchHelper.onTouchEvent(event);
            if (this.mHeadsUpTouchHelper.isTrackingHeadsUp() || !handleQsTouch(event)) {
                if (event.getActionMasked() == 0 && isFullyCollapsed()) {
                    MetricsLogger.count(this.mContext, COUNTER_PANEL_OPEN, 1);
                    updateVerticalPanelPosition(event.getX());
                    handled2 = true;
                }
                return !this.mDozing || this.mPulsing || handled2 || super.onTouchEvent(event);
            }
            return true;
        }
        return true;
    }

    private boolean handleQsTouch(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == 0 && getExpandedFraction() == 1.0f && this.mBarState != 1 && !this.mQsExpanded && this.mQsExpansionEnabled) {
            this.mQsTracking = true;
            this.mConflictingQsExpansionGesture = true;
            onQsExpansionStarted();
            this.mInitialHeightOnTouch = this.mQsExpansionHeight;
            this.mInitialTouchY = event.getX();
            this.mInitialTouchX = event.getY();
        }
        if (!isFullyCollapsed()) {
            handleQsDown(event);
        }
        if (!this.mQsExpandImmediate && this.mQsTracking) {
            onQsTouch(event);
            if (!this.mConflictingQsExpansionGesture) {
                return true;
            }
        }
        if (action == 3 || action == 1) {
            this.mConflictingQsExpansionGesture = false;
        }
        if (action == 0 && isFullyCollapsed() && this.mQsExpansionEnabled) {
            this.mTwoFingerQsExpandPossible = true;
        }
        if (this.mTwoFingerQsExpandPossible && isOpenQsEvent(event) && event.getY(event.getActionIndex()) < this.mStatusBarMinHeight) {
            MetricsLogger.count(this.mContext, COUNTER_PANEL_OPEN_QS, 1);
            this.mQsExpandImmediate = true;
            this.mNotificationStackScroller.setShouldShowShelfOnly(true);
            requestPanelHeightUpdate();
            setListening(true);
        }
        return false;
    }

    private boolean isInQsArea(float x, float y) {
        return x >= this.mQsFrame.getX() && x <= this.mQsFrame.getX() + ((float) this.mQsFrame.getWidth()) && (y <= this.mNotificationStackScroller.getBottomMostNotificationBottom() || y <= this.mQs.getView().getY() + ((float) this.mQs.getView().getHeight()));
    }

    private boolean isOpenQsEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        int action = event.getActionMasked();
        boolean twoFingerDrag = action == 5 && pointerCount == 2;
        boolean stylusButtonClickDrag = action == 0 && (event.isButtonPressed(32) || event.isButtonPressed(64));
        boolean mouseButtonClickDrag = action == 0 && (event.isButtonPressed(2) || event.isButtonPressed(4));
        return twoFingerDrag || stylusButtonClickDrag || mouseButtonClickDrag;
    }

    private void handleQsDown(MotionEvent event) {
        if (event.getActionMasked() == 0 && shouldQuickSettingsIntercept(event.getX(), event.getY(), -1.0f)) {
            this.mFalsingManager.onQsDown();
            this.mQsTracking = true;
            onQsExpansionStarted();
            this.mInitialHeightOnTouch = this.mQsExpansionHeight;
            this.mInitialTouchY = event.getX();
            this.mInitialTouchX = event.getY();
            notifyExpandingFinished();
        }
    }

    public void startWaitingForOpenPanelGesture() {
        if (!isFullyCollapsed()) {
            return;
        }
        this.mExpectingSynthesizedDown = true;
        onTrackingStarted();
        updatePanelExpanded();
    }

    public void stopWaitingForOpenPanelGesture(final float velocity) {
        if (this.mExpectingSynthesizedDown) {
            this.mExpectingSynthesizedDown = false;
            maybeVibrateOnOpening();
            Runnable runnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NotificationPanelView$xA7cX216Lge0MlKS0GBWcVNjPAk
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationPanelView.this.lambda$stopWaitingForOpenPanelGesture$2$NotificationPanelView(velocity);
                }
            };
            if (this.mStatusBar.getStatusBarWindow().getHeight() != this.mStatusBar.getStatusBarHeight()) {
                runnable.run();
            } else {
                this.mExpandAfterLayoutRunnable = runnable;
            }
            onTrackingStopped(false);
        }
    }

    public /* synthetic */ void lambda$stopWaitingForOpenPanelGesture$2$NotificationPanelView(float velocity) {
        fling(velocity > 1.0f ? 1000.0f * velocity : 0.0f, true);
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected boolean flingExpands(float vel, float vectorVel, float x, float y) {
        boolean expands = super.flingExpands(vel, vectorVel, x, y);
        if (this.mQsExpansionAnimator != null) {
            return true;
        }
        return expands;
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected boolean shouldGestureWaitForTouchSlop() {
        if (!this.mExpectingSynthesizedDown) {
            return isFullyCollapsed() || this.mBarState != 0;
        }
        this.mExpectingSynthesizedDown = false;
        return false;
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected boolean shouldGestureIgnoreXTouchSlop(float x, float y) {
        return !this.mAffordanceHelper.isOnAffordanceIcon(x, y);
    }

    private void onQsTouch(MotionEvent event) {
        int upPointer;
        int pointerIndex = event.findPointerIndex(this.mTrackingPointer);
        if (pointerIndex < 0) {
            pointerIndex = 0;
            this.mTrackingPointer = event.getPointerId(0);
        }
        float y = event.getY(pointerIndex);
        float x = event.getX(pointerIndex);
        float h = y - this.mInitialTouchY;
        int actionMasked = event.getActionMasked();
        if (actionMasked == 0) {
            this.mQsTracking = true;
            this.mInitialTouchY = y;
            this.mInitialTouchX = x;
            onQsExpansionStarted();
            this.mInitialHeightOnTouch = this.mQsExpansionHeight;
            initVelocityTracker();
            trackMovement(event);
            return;
        }
        if (actionMasked != 1) {
            if (actionMasked == 2) {
                setQsExpansion(this.mInitialHeightOnTouch + h);
                if (h >= getFalsingThreshold()) {
                    this.mQsTouchAboveFalsingThreshold = true;
                }
                trackMovement(event);
                return;
            } else if (actionMasked != 3) {
                if (actionMasked == 6 && this.mTrackingPointer == (upPointer = event.getPointerId(event.getActionIndex()))) {
                    int newIndex = event.getPointerId(0) != upPointer ? 0 : 1;
                    float newY = event.getY(newIndex);
                    float newX = event.getX(newIndex);
                    this.mTrackingPointer = event.getPointerId(newIndex);
                    this.mInitialHeightOnTouch = this.mQsExpansionHeight;
                    this.mInitialTouchY = newY;
                    this.mInitialTouchX = newX;
                    return;
                }
                return;
            }
        }
        this.mQsTracking = false;
        this.mTrackingPointer = -1;
        trackMovement(event);
        float fraction = getQsExpansionFraction();
        if (fraction != 0.0f || y >= this.mInitialTouchY) {
            flingQsWithCurrentVelocity(y, event.getActionMasked() == 3);
        }
        VelocityTracker velocityTracker = this.mQsVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mQsVelocityTracker = null;
        }
    }

    private int getFalsingThreshold() {
        float factor = this.mStatusBar.isWakeUpComingFromTouch() ? 1.5f : 1.0f;
        return (int) (this.mQsFalsingThreshold * factor);
    }

    @Override // com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout.OnOverscrollTopChangedListener
    public void onOverscrollTopChanged(float amount, boolean isRubberbanded) {
        cancelQsAnimation();
        if (!this.mQsExpansionEnabled) {
            amount = 0.0f;
        }
        float rounded = amount >= 1.0f ? amount : 0.0f;
        setOverScrolling(rounded != 0.0f && isRubberbanded);
        this.mQsExpansionFromOverscroll = rounded != 0.0f;
        this.mLastOverscroll = rounded;
        updateQsState();
        setQsExpansion(this.mQsMinExpansionHeight + rounded);
    }

    @Override // com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout.OnOverscrollTopChangedListener
    public void flingTopOverscroll(float velocity, boolean open) {
        float f = 0.0f;
        this.mLastOverscroll = 0.0f;
        this.mQsExpansionFromOverscroll = false;
        setQsExpansion(this.mQsExpansionHeight);
        if (this.mQsExpansionEnabled || !open) {
            f = velocity;
        }
        flingSettings(f, (open && this.mQsExpansionEnabled) ? 0 : 1, new Runnable() { // from class: com.android.systemui.statusbar.phone.NotificationPanelView.8
            @Override // java.lang.Runnable
            public void run() {
                NotificationPanelView.this.mStackScrollerOverscrolling = false;
                NotificationPanelView.this.setOverScrolling(false);
                NotificationPanelView.this.updateQsState();
            }
        }, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setOverScrolling(boolean overscrolling) {
        this.mStackScrollerOverscrolling = overscrolling;
        QS qs = this.mQs;
        if (qs == null) {
            return;
        }
        qs.setOverscrolling(overscrolling);
    }

    private void onQsExpansionStarted() {
        onQsExpansionStarted(0);
    }

    protected void onQsExpansionStarted(int overscrollAmount) {
        cancelQsAnimation();
        cancelHeightAnimator();
        float height = this.mQsExpansionHeight - overscrollAmount;
        setQsExpansion(height);
        requestPanelHeightUpdate();
        this.mNotificationStackScroller.checkSnoozeLeavebehind();
        if (height == 0.0f) {
            this.mStatusBar.requestFaceAuth();
        }
    }

    private void setQsExpanded(boolean expanded) {
        boolean changed = this.mQsExpanded != expanded;
        if (changed) {
            this.mQsExpanded = expanded;
            updateQsState();
            requestPanelHeightUpdate();
            this.mFalsingManager.setQsExpanded(expanded);
            this.mStatusBar.setQsExpanded(expanded);
            this.mNotificationContainerParent.setQsExpanded(expanded);
            this.mPulseExpansionHandler.setQsExpanded(expanded);
            this.mKeyguardBypassController.setQSExpanded(expanded);
        }
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onStateChanged(int statusBarState) {
        QS qs;
        boolean goingToFullShade = this.mStatusBarStateController.goingToFullShade();
        boolean keyguardFadingAway = this.mKeyguardMonitor.isKeyguardFadingAway();
        int oldState = this.mBarState;
        boolean keyguardShowing = statusBarState == 1;
        setKeyguardStatusViewVisibility(statusBarState, keyguardFadingAway, goingToFullShade);
        setKeyguardBottomAreaVisibility(statusBarState, goingToFullShade);
        this.mBarState = statusBarState;
        this.mKeyguardShowing = keyguardShowing;
        if (oldState != 1 || (!goingToFullShade && statusBarState != 2)) {
            if (oldState == 2 && statusBarState == 1) {
                animateKeyguardStatusBarIn(360L);
                this.mNotificationStackScroller.resetScrollPosition();
                if (!this.mQsExpanded) {
                    this.mQs.animateHeaderSlidingOut();
                }
            } else {
                this.mKeyguardStatusBar.setAlpha(1.0f);
                this.mKeyguardStatusBar.setVisibility(keyguardShowing ? 0 : 4);
                if (keyguardShowing && oldState != this.mBarState && (qs = this.mQs) != null) {
                    qs.hideImmediately();
                }
            }
        } else {
            animateKeyguardStatusBarOut();
            long delay = this.mBarState == 2 ? 0L : this.mKeyguardMonitor.calculateGoingToFullShadeDelay();
            this.mQs.animateHeaderSlidingIn(delay);
        }
        updateKeyguardStatusBarForHeadsUp();
        if (keyguardShowing) {
            updateDozingVisibilities(false);
        }
        updateQSPulseExpansion();
        maybeAnimateBottomAreaAlpha();
        resetHorizontalPanelPosition();
        updateQsState();
    }

    private void maybeAnimateBottomAreaAlpha() {
        this.mBottomAreaShadeAlphaAnimator.cancel();
        if (this.mBarState == 2) {
            this.mBottomAreaShadeAlphaAnimator.start();
        } else {
            this.mBottomAreaShadeAlpha = 1.0f;
        }
    }

    private void animateKeyguardStatusBarOut() {
        long j;
        long duration;
        ValueAnimator anim = ValueAnimator.ofFloat(this.mKeyguardStatusBar.getAlpha(), 0.0f);
        anim.addUpdateListener(this.mStatusBarAnimateAlphaListener);
        if (this.mKeyguardMonitor.isKeyguardFadingAway()) {
            j = this.mKeyguardMonitor.getKeyguardFadingAwayDelay();
        } else {
            j = 0;
        }
        anim.setStartDelay(j);
        if (this.mKeyguardMonitor.isKeyguardFadingAway()) {
            duration = this.mKeyguardMonitor.getShortenedFadingAwayDuration();
        } else {
            duration = 360;
        }
        anim.setDuration(duration);
        anim.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
        anim.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.phone.NotificationPanelView.13
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                NotificationPanelView.this.mAnimateKeyguardStatusBarInvisibleEndRunnable.run();
            }
        });
        anim.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void animateKeyguardStatusBarIn(long duration) {
        this.mKeyguardStatusBar.setVisibility(0);
        this.mKeyguardStatusBar.setAlpha(0.0f);
        ValueAnimator anim = ValueAnimator.ofFloat(0.0f, 1.0f);
        anim.addUpdateListener(this.mStatusBarAnimateAlphaListener);
        anim.setDuration(duration);
        anim.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
        anim.start();
    }

    private void setKeyguardBottomAreaVisibility(int statusBarState, boolean goingToFullShade) {
        this.mKeyguardBottomArea.animate().cancel();
        if (goingToFullShade) {
            this.mKeyguardBottomArea.animate().alpha(0.0f).setStartDelay(this.mKeyguardMonitor.getKeyguardFadingAwayDelay()).setDuration(this.mKeyguardMonitor.getShortenedFadingAwayDuration()).setInterpolator(Interpolators.ALPHA_OUT).withEndAction(this.mAnimateKeyguardBottomAreaInvisibleEndRunnable).start();
        } else if (statusBarState == 1 || statusBarState == 2) {
            this.mKeyguardBottomArea.setVisibility(0);
            this.mKeyguardBottomArea.setAlpha(1.0f);
        } else {
            this.mKeyguardBottomArea.setVisibility(8);
        }
    }

    private void setKeyguardStatusViewVisibility(int statusBarState, boolean keyguardFadingAway, boolean goingToFullShade) {
        this.mKeyguardStatusView.animate().cancel();
        this.mKeyguardStatusViewAnimating = false;
        if ((!keyguardFadingAway && this.mBarState == 1 && statusBarState != 1) || goingToFullShade) {
            this.mKeyguardStatusViewAnimating = true;
            this.mKeyguardStatusView.animate().alpha(0.0f).setStartDelay(0L).setDuration(160L).setInterpolator(Interpolators.ALPHA_OUT).withEndAction(this.mAnimateKeyguardStatusViewGoneEndRunnable);
            if (keyguardFadingAway) {
                this.mKeyguardStatusView.animate().setStartDelay(this.mKeyguardMonitor.getKeyguardFadingAwayDelay()).setDuration(this.mKeyguardMonitor.getShortenedFadingAwayDuration()).start();
            }
        } else if (this.mBarState == 2 && statusBarState == 1) {
            this.mKeyguardStatusView.setVisibility(0);
            this.mKeyguardStatusViewAnimating = true;
            this.mKeyguardStatusView.setAlpha(0.0f);
            this.mKeyguardStatusView.animate().alpha(1.0f).setStartDelay(0L).setDuration(320L).setInterpolator(Interpolators.ALPHA_IN).withEndAction(this.mAnimateKeyguardStatusViewVisibleEndRunnable);
        } else if (statusBarState == 1) {
            if (!keyguardFadingAway) {
                this.mKeyguardStatusView.setVisibility(0);
                this.mKeyguardStatusView.setAlpha(1.0f);
                return;
            }
            this.mKeyguardStatusViewAnimating = true;
            this.mKeyguardStatusView.animate().alpha(0.0f).translationYBy((-getHeight()) * 0.05f).setInterpolator(Interpolators.FAST_OUT_LINEAR_IN).setDuration(125L).setStartDelay(0L).withEndAction(this.mAnimateKeyguardStatusViewInvisibleEndRunnable).start();
        } else {
            this.mKeyguardStatusView.setVisibility(8);
            this.mKeyguardStatusView.setAlpha(1.0f);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateQsState() {
        this.mNotificationStackScroller.setQsExpanded(this.mQsExpanded);
        int i = 0;
        this.mNotificationStackScroller.setScrollingEnabled(this.mBarState != 1 && (!this.mQsExpanded || this.mQsExpansionFromOverscroll));
        updateEmptyShadeView();
        this.mQsNavbarScrim.setVisibility((this.mBarState == 0 && this.mQsExpanded && !this.mStackScrollerOverscrolling && this.mQsScrimEnabled) ? 4 : 4);
        KeyguardUserSwitcher keyguardUserSwitcher = this.mKeyguardUserSwitcher;
        if (keyguardUserSwitcher != null && this.mQsExpanded && !this.mStackScrollerOverscrolling) {
            keyguardUserSwitcher.hideIfNotSimple(true);
        }
        QS qs = this.mQs;
        if (qs == null) {
            return;
        }
        qs.setExpanded(this.mQsExpanded);
    }

    private void setQsExpansion(float height) {
        float height2 = Math.min(Math.max(height, this.mQsMinExpansionHeight), this.mQsMaxExpansionHeight);
        int i = this.mQsMaxExpansionHeight;
        this.mQsFullyExpanded = height2 == ((float) i) && i != 0;
        if (height2 > this.mQsMinExpansionHeight && !this.mQsExpanded && !this.mStackScrollerOverscrolling && !this.mDozing) {
            setQsExpanded(true);
        } else if (height2 <= this.mQsMinExpansionHeight && this.mQsExpanded) {
            setQsExpanded(false);
        }
        this.mQsExpansionHeight = height2;
        updateQsExpansion();
        requestScrollerTopPaddingUpdate(false);
        updateHeaderKeyguardAlpha();
        int i2 = this.mBarState;
        if (i2 == 2 || i2 == 1) {
            updateKeyguardBottomAreaAlpha();
            updateBigClockAlpha();
        }
        if (this.mBarState == 0 && this.mQsExpanded && !this.mStackScrollerOverscrolling && this.mQsScrimEnabled) {
            this.mQsNavbarScrim.setAlpha(getQsExpansionFraction());
        }
        if (this.mAccessibilityManager.isEnabled()) {
            setAccessibilityPaneTitle(determineAccessibilityPaneTitle());
        }
        if (!this.mFalsingManager.isUnlockingDisabled() && this.mQsFullyExpanded && this.mFalsingManager.shouldEnforceBouncer()) {
            this.mStatusBar.executeRunnableDismissingKeyguard(null, null, false, true, false);
        }
        for (int i3 = 0; i3 < this.mExpansionListeners.size(); i3++) {
            PanelExpansionListener panelExpansionListener = this.mExpansionListeners.get(i3);
            int i4 = this.mQsMaxExpansionHeight;
            panelExpansionListener.onQsExpansionChanged(i4 != 0 ? this.mQsExpansionHeight / i4 : 0.0f);
        }
    }

    protected void updateQsExpansion() {
        if (this.mQs == null) {
            return;
        }
        float qsExpansionFraction = getQsExpansionFraction();
        this.mQs.setQsExpansion(qsExpansionFraction, getHeaderTranslation());
        this.mNotificationStackScroller.setQsExpansionFraction(qsExpansionFraction);
    }

    private String determineAccessibilityPaneTitle() {
        QS qs = this.mQs;
        if (qs != null && qs.isCustomizing()) {
            return getContext().getString(R.string.accessibility_desc_quick_settings_edit);
        }
        if (this.mQsExpansionHeight != 0.0f && this.mQsFullyExpanded) {
            return getContext().getString(R.string.accessibility_desc_quick_settings);
        }
        if (this.mBarState == 1) {
            return getContext().getString(R.string.accessibility_desc_lock_screen);
        }
        return getContext().getString(R.string.accessibility_desc_notification_shade);
    }

    private float calculateQsTopPadding() {
        int max;
        if (this.mKeyguardShowing && (this.mQsExpandImmediate || (this.mIsExpanding && this.mQsExpandedWhenExpandingStarted))) {
            int maxNotificationPadding = getKeyguardNotificationStaticPadding();
            int maxQsPadding = this.mQsMaxExpansionHeight + this.mQsNotificationTopPadding;
            if (this.mBarState == 1) {
                max = Math.max(maxNotificationPadding, maxQsPadding);
            } else {
                max = maxQsPadding;
            }
            return (int) MathUtils.lerp(this.mQsMinExpansionHeight, max, getExpandedFraction());
        }
        ValueAnimator valueAnimator = this.mQsSizeChangeAnimator;
        if (valueAnimator != null) {
            return Math.max(((Integer) valueAnimator.getAnimatedValue()).intValue(), getKeyguardNotificationStaticPadding());
        }
        if (this.mKeyguardShowing) {
            return MathUtils.lerp(getKeyguardNotificationStaticPadding(), this.mQsMaxExpansionHeight + this.mQsNotificationTopPadding, getQsExpansionFraction());
        }
        return this.mQsExpansionHeight + this.mQsNotificationTopPadding;
    }

    private int getKeyguardNotificationStaticPadding() {
        if (!this.mKeyguardShowing) {
            return 0;
        }
        if (!this.mKeyguardBypassController.getBypassEnabled()) {
            return this.mClockPositionResult.stackScrollerPadding;
        }
        int collapsedPosition = this.mHeadsUpInset;
        if (!this.mNotificationStackScroller.isPulseExpanding()) {
            return collapsedPosition;
        }
        int expandedPosition = this.mClockPositionResult.stackScrollerPadding;
        return (int) MathUtils.lerp(collapsedPosition, expandedPosition, this.mNotificationStackScroller.calculateAppearFractionBypass());
    }

    protected void requestScrollerTopPaddingUpdate(boolean animate) {
        this.mNotificationStackScroller.updateTopPadding(calculateQsTopPadding(), animate);
        if (this.mKeyguardShowing && this.mKeyguardBypassController.getBypassEnabled()) {
            updateQsExpansion();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateQSPulseExpansion() {
        QS qs = this.mQs;
        if (qs != null) {
            qs.setShowCollapsedOnKeyguard(this.mKeyguardShowing && this.mKeyguardBypassController.getBypassEnabled() && this.mNotificationStackScroller.isPulseExpanding());
        }
    }

    private void trackMovement(MotionEvent event) {
        VelocityTracker velocityTracker = this.mQsVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.addMovement(event);
        }
        this.mLastTouchX = event.getX();
        this.mLastTouchY = event.getY();
    }

    private void initVelocityTracker() {
        VelocityTracker velocityTracker = this.mQsVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
        }
        this.mQsVelocityTracker = VelocityTracker.obtain();
    }

    private float getCurrentQSVelocity() {
        VelocityTracker velocityTracker = this.mQsVelocityTracker;
        if (velocityTracker == null) {
            return 0.0f;
        }
        velocityTracker.computeCurrentVelocity(1000);
        return this.mQsVelocityTracker.getYVelocity();
    }

    private void cancelQsAnimation() {
        ValueAnimator valueAnimator = this.mQsExpansionAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    public void flingSettings(float vel, int type) {
        flingSettings(vel, type, null, false);
    }

    protected void flingSettings(float vel, int type, final Runnable onFinishRunnable, boolean isClick) {
        float target;
        boolean expanding;
        if (type != 0) {
            if (type == 1) {
                target = this.mQsMinExpansionHeight;
            } else {
                target = 0.0f;
            }
        } else {
            target = this.mQsMaxExpansionHeight;
        }
        if (target == this.mQsExpansionHeight) {
            if (onFinishRunnable != null) {
                onFinishRunnable.run();
                return;
            }
            return;
        }
        boolean oppositeDirection = false;
        if (type == 0) {
            expanding = true;
        } else {
            expanding = false;
        }
        if ((vel > 0.0f && !expanding) || (vel < 0.0f && expanding)) {
            vel = 0.0f;
            oppositeDirection = true;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(this.mQsExpansionHeight, target);
        if (isClick) {
            animator.setInterpolator(Interpolators.TOUCH_RESPONSE);
            animator.setDuration(368L);
        } else {
            this.mFlingAnimationUtils.apply(animator, this.mQsExpansionHeight, target, vel);
        }
        if (oppositeDirection) {
            animator.setDuration(350L);
        }
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NotificationPanelView$GBIvrcRMfk5MdTVeindE6SW10Nw
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                NotificationPanelView.this.lambda$flingSettings$3$NotificationPanelView(valueAnimator);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.phone.NotificationPanelView.16
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                NotificationPanelView.this.mNotificationStackScroller.resetCheckSnoozeLeavebehind();
                NotificationPanelView.this.mQsExpansionAnimator = null;
                Runnable runnable = onFinishRunnable;
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
        animator.start();
        this.mQsExpansionAnimator = animator;
        this.mQsAnimatorExpand = expanding;
    }

    public /* synthetic */ void lambda$flingSettings$3$NotificationPanelView(ValueAnimator animation) {
        setQsExpansion(((Float) animation.getAnimatedValue()).floatValue());
    }

    private boolean shouldQuickSettingsIntercept(float x, float y, float yDiff) {
        QS qs;
        if (!this.mQsExpansionEnabled || this.mCollapsedOnDown || (this.mKeyguardShowing && this.mKeyguardBypassController.getBypassEnabled())) {
            return false;
        }
        View header = (this.mKeyguardShowing || (qs = this.mQs) == null) ? this.mKeyguardStatusBar : qs.getHeader();
        boolean onHeader = x >= this.mQsFrame.getX() && x <= this.mQsFrame.getX() + ((float) this.mQsFrame.getWidth()) && y >= ((float) header.getTop()) && y <= ((float) header.getBottom());
        if (this.mQsExpanded) {
            return onHeader || (yDiff < 0.0f && isInQsArea(x, y));
        }
        return onHeader;
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected boolean isScrolledToBottom() {
        return isInSettings() || this.mBarState == 1 || this.mNotificationStackScroller.isScrolledToBottom();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.phone.PanelView
    public int getMaxPanelHeight() {
        if (this.mKeyguardBypassController.getBypassEnabled() && this.mBarState == 1) {
            return getMaxPanelHeightBypass();
        }
        return getMaxPanelHeightNonBypass();
    }

    private int getMaxPanelHeightNonBypass() {
        int maxHeight;
        int min = this.mStatusBarMinHeight;
        if (this.mBarState != 1 && this.mNotificationStackScroller.getNotGoneChildCount() == 0) {
            int minHeight = (int) (this.mQsMinExpansionHeight + getOverExpansionAmount());
            min = Math.max(min, minHeight);
        }
        if (this.mQsExpandImmediate || this.mQsExpanded || ((this.mIsExpanding && this.mQsExpandedWhenExpandingStarted) || this.mPulsing)) {
            maxHeight = calculatePanelHeightQsExpanded();
        } else {
            maxHeight = calculatePanelHeightShade();
        }
        return Math.max(min, maxHeight);
    }

    private int getMaxPanelHeightBypass() {
        int position = this.mClockPositionAlgorithm.getExpandedClockPosition() + this.mKeyguardStatusView.getHeight();
        if (this.mNotificationStackScroller.getVisibleNotificationCount() != 0) {
            return (int) (position + (this.mShelfHeight / 2.0f) + (this.mDarkIconSize / 2.0f));
        }
        return position;
    }

    public boolean isInSettings() {
        return this.mQsExpanded;
    }

    public boolean isExpanding() {
        return this.mIsExpanding;
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected void onHeightUpdated(float expandedHeight) {
        float panelHeightQsCollapsed;
        int i;
        if ((!this.mQsExpanded || this.mQsExpandImmediate || (this.mIsExpanding && this.mQsExpandedWhenExpandingStarted)) && this.mStackScrollerMeasuringPass <= 2) {
            positionClockAndNotifications();
        }
        if (this.mQsExpandImmediate || (this.mQsExpanded && !this.mQsTracking && this.mQsExpansionAnimator == null && !this.mQsExpansionFromOverscroll)) {
            if (this.mKeyguardShowing) {
                panelHeightQsCollapsed = expandedHeight / getMaxPanelHeight();
            } else {
                float panelHeightQsCollapsed2 = this.mNotificationStackScroller.getIntrinsicPadding() + this.mNotificationStackScroller.getLayoutMinHeight();
                float panelHeightQsExpanded = calculatePanelHeightQsExpanded();
                panelHeightQsCollapsed = (expandedHeight - panelHeightQsCollapsed2) / (panelHeightQsExpanded - panelHeightQsCollapsed2);
            }
            setQsExpansion(this.mQsMinExpansionHeight + ((this.mQsMaxExpansionHeight - i) * panelHeightQsCollapsed));
        }
        updateExpandedHeight(expandedHeight);
        updateHeader();
        updateNotificationTranslucency();
        updatePanelExpanded();
        updateGestureExclusionRect();
    }

    private void updatePanelExpanded() {
        boolean isExpanded = !isFullyCollapsed() || this.mExpectingSynthesizedDown;
        if (this.mPanelExpanded != isExpanded) {
            this.mHeadsUpManager.setIsPanelExpanded(isExpanded);
            this.mStatusBar.setPanelExpanded(isExpanded);
            this.mPanelExpanded = isExpanded;
        }
    }

    private int calculatePanelHeightShade() {
        int emptyBottomMargin = this.mNotificationStackScroller.getEmptyBottomMargin();
        int maxHeight = (int) ((this.mNotificationStackScroller.getHeight() - emptyBottomMargin) + this.mNotificationStackScroller.getTopPaddingOverflow());
        if (this.mBarState == 1) {
            int minKeyguardPanelBottom = this.mClockPositionAlgorithm.getExpandedClockPosition() + this.mKeyguardStatusView.getHeight() + this.mNotificationStackScroller.getIntrinsicContentHeight();
            return Math.max(maxHeight, minKeyguardPanelBottom);
        }
        return maxHeight;
    }

    private int calculatePanelHeightQsExpanded() {
        float notificationHeight = (this.mNotificationStackScroller.getHeight() - this.mNotificationStackScroller.getEmptyBottomMargin()) - this.mNotificationStackScroller.getTopPadding();
        if (this.mNotificationStackScroller.getNotGoneChildCount() == 0 && this.mShowEmptyShadeView) {
            notificationHeight = this.mNotificationStackScroller.getEmptyShadeViewHeight();
        }
        int maxQsHeight = this.mQsMaxExpansionHeight;
        if (this.mKeyguardShowing) {
            maxQsHeight += this.mQsNotificationTopPadding;
        }
        ValueAnimator valueAnimator = this.mQsSizeChangeAnimator;
        if (valueAnimator != null) {
            maxQsHeight = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        }
        float totalHeight = Math.max(maxQsHeight, this.mBarState == 1 ? this.mClockPositionResult.stackScrollerPadding : 0) + notificationHeight + this.mNotificationStackScroller.getTopPaddingOverflow();
        if (totalHeight > this.mNotificationStackScroller.getHeight()) {
            float fullyCollapsedHeight = this.mNotificationStackScroller.getLayoutMinHeight() + maxQsHeight;
            totalHeight = Math.max(fullyCollapsedHeight, this.mNotificationStackScroller.getHeight());
        }
        return (int) totalHeight;
    }

    private void updateNotificationTranslucency() {
        float alpha = 1.0f;
        if (this.mClosingWithAlphaFadeOut && !this.mExpandingFromHeadsUp && !this.mHeadsUpManager.hasPinnedHeadsUp()) {
            alpha = getFadeoutAlpha();
        }
        if (this.mBarState == 1 && !this.mHintAnimationRunning && !this.mKeyguardBypassController.getBypassEnabled()) {
            alpha *= this.mClockPositionResult.clockAlpha;
        }
        this.mNotificationStackScroller.setAlpha(alpha);
    }

    private float getFadeoutAlpha() {
        if (this.mQsMinExpansionHeight == 0) {
            return 1.0f;
        }
        float alpha = getExpandedHeight() / this.mQsMinExpansionHeight;
        return (float) Math.pow(Math.max(0.0f, Math.min(alpha, 1.0f)), 0.75d);
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected float getOverExpansionAmount() {
        return this.mNotificationStackScroller.getCurrentOverScrollAmount(true);
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected float getOverExpansionPixels() {
        return this.mNotificationStackScroller.getCurrentOverScrolledPixels(true);
    }

    private void updateHeader() {
        if (this.mBarState == 1) {
            updateHeaderKeyguardAlpha();
        }
        updateQsExpansion();
    }

    protected float getHeaderTranslation() {
        if (this.mBarState == 1 && !this.mKeyguardBypassController.getBypassEnabled()) {
            return -this.mQs.getQsMinExpansionHeight();
        }
        float appearAmount = this.mNotificationStackScroller.calculateAppearFraction(this.mExpandedHeight);
        float startHeight = -this.mQsExpansionHeight;
        if (this.mKeyguardBypassController.getBypassEnabled() && isOnKeyguard() && this.mNotificationStackScroller.isPulseExpanding()) {
            if (!this.mPulseExpansionHandler.isExpanding() && !this.mPulseExpansionHandler.getLeavingLockscreen()) {
                appearAmount = 0.0f;
            } else {
                appearAmount = this.mNotificationStackScroller.calculateAppearFractionBypass();
            }
            startHeight = -this.mQs.getQsMinExpansionHeight();
        }
        float translation = MathUtils.lerp(startHeight, 0.0f, Math.min(1.0f, appearAmount)) + this.mExpandOffset;
        return Math.min(0.0f, translation);
    }

    private float getKeyguardContentsAlpha() {
        float alpha;
        if (this.mBarState == 1) {
            alpha = getExpandedHeight() / (this.mKeyguardStatusBar.getHeight() + this.mNotificationsHeaderCollideDistance);
        } else {
            float alpha2 = getExpandedHeight();
            alpha = alpha2 / this.mKeyguardStatusBar.getHeight();
        }
        return (float) Math.pow(MathUtils.saturate(alpha), 0.75d);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateHeaderKeyguardAlpha() {
        if (!this.mKeyguardShowing) {
            return;
        }
        float alphaQsExpansion = 1.0f - Math.min(1.0f, getQsExpansionFraction() * 2.0f);
        float newAlpha = Math.min(getKeyguardContentsAlpha(), alphaQsExpansion) * this.mKeyguardStatusBarAnimateAlpha * (1.0f - this.mKeyguardHeadsUpShowingAmount);
        this.mKeyguardStatusBar.setAlpha(newAlpha);
        int i = 0;
        boolean hideForBypass = (this.mFirstBypassAttempt && this.mUpdateMonitor.shouldListenForFace()) || this.mDelayShowingKeyguardStatusBar;
        this.mKeyguardStatusBar.setVisibility((newAlpha == 0.0f || this.mDozing || hideForBypass) ? 4 : 4);
    }

    private void updateKeyguardBottomAreaAlpha() {
        int i;
        float expansionAlpha = MathUtils.map(isUnlockHintRunning() ? 0.0f : 0.95f, 1.0f, 0.0f, 1.0f, getExpandedFraction());
        float alpha = Math.min(expansionAlpha, 1.0f - getQsExpansionFraction()) * this.mBottomAreaShadeAlpha;
        this.mKeyguardBottomArea.setAffordanceAlpha(alpha);
        KeyguardBottomAreaView keyguardBottomAreaView = this.mKeyguardBottomArea;
        if (alpha == 0.0f) {
            i = 4;
        } else {
            i = 0;
        }
        keyguardBottomAreaView.setImportantForAccessibility(i);
        View ambientIndicationContainer = this.mStatusBar.getAmbientIndicationContainer();
        if (ambientIndicationContainer != null) {
            ambientIndicationContainer.setAlpha(alpha);
        }
    }

    private void updateBigClockAlpha() {
        float expansionAlpha = MathUtils.map(isUnlockHintRunning() ? 0.0f : 0.95f, 1.0f, 0.0f, 1.0f, getExpandedFraction());
        float alpha = Math.min(expansionAlpha, 1.0f - getQsExpansionFraction());
        this.mBigClockContainer.setAlpha(alpha);
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected void onExpandingStarted() {
        super.onExpandingStarted();
        this.mNotificationStackScroller.onExpansionStarted();
        this.mIsExpanding = true;
        this.mQsExpandedWhenExpandingStarted = this.mQsFullyExpanded;
        if (this.mQsExpanded) {
            onQsExpansionStarted();
        }
        QS qs = this.mQs;
        if (qs == null) {
            return;
        }
        qs.setHeaderListening(true);
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected void onExpandingFinished() {
        super.onExpandingFinished();
        this.mNotificationStackScroller.onExpansionStopped();
        this.mHeadsUpManager.onExpandingFinished();
        this.mIsExpanding = false;
        if (isFullyCollapsed()) {
            DejankUtils.postAfterTraversal(new Runnable() { // from class: com.android.systemui.statusbar.phone.NotificationPanelView.17
                @Override // java.lang.Runnable
                public void run() {
                    NotificationPanelView.this.setListening(false);
                }
            });
            postOnAnimation(new Runnable() { // from class: com.android.systemui.statusbar.phone.NotificationPanelView.18
                @Override // java.lang.Runnable
                public void run() {
                    NotificationPanelView.this.getParent().invalidateChild(NotificationPanelView.this, NotificationPanelView.mDummyDirtyRect);
                }
            });
        } else {
            setListening(true);
        }
        this.mQsExpandImmediate = false;
        this.mNotificationStackScroller.setShouldShowShelfOnly(false);
        this.mTwoFingerQsExpandPossible = false;
        this.mIsExpansionFromHeadsUp = false;
        notifyListenersTrackingHeadsUp(null);
        this.mExpandingFromHeadsUp = false;
        setPanelScrimMinFraction(0.0f);
    }

    private void notifyListenersTrackingHeadsUp(ExpandableNotificationRow pickedChild) {
        for (int i = 0; i < this.mTrackingHeadsUpListeners.size(); i++) {
            Consumer<ExpandableNotificationRow> listener = this.mTrackingHeadsUpListeners.get(i);
            listener.accept(pickedChild);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setListening(boolean listening) {
        this.mKeyguardStatusBar.setListening(listening);
        QS qs = this.mQs;
        if (qs == null) {
            return;
        }
        qs.setListening(listening);
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    public void expand(boolean animate) {
        super.expand(animate);
        setListening(true);
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected void setOverExpansion(float overExpansion, boolean isPixels) {
        if (!this.mConflictingQsExpansionGesture && !this.mQsExpandImmediate && this.mBarState != 1) {
            this.mNotificationStackScroller.setOnHeightChangedListener(null);
            if (isPixels) {
                this.mNotificationStackScroller.setOverScrolledPixels(overExpansion, true, false);
            } else {
                this.mNotificationStackScroller.setOverScrollAmount(overExpansion, true, false);
            }
            this.mNotificationStackScroller.setOnHeightChangedListener(this);
        }
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected void onTrackingStarted() {
        this.mFalsingManager.onTrackingStarted(this.mStatusBar.isKeyguardCurrentlySecure());
        super.onTrackingStarted();
        if (this.mQsFullyExpanded) {
            this.mQsExpandImmediate = true;
            this.mNotificationStackScroller.setShouldShowShelfOnly(true);
        }
        int i = this.mBarState;
        if (i == 1 || i == 2) {
            this.mAffordanceHelper.animateHideLeftRightIcon();
        }
        this.mNotificationStackScroller.onPanelTrackingStarted();
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected void onTrackingStopped(boolean expand) {
        this.mFalsingManager.onTrackingStopped();
        super.onTrackingStopped(expand);
        if (expand) {
            this.mNotificationStackScroller.setOverScrolledPixels(0.0f, true, true);
        }
        this.mNotificationStackScroller.onPanelTrackingStopped();
        if (expand) {
            int i = this.mBarState;
            if ((i == 1 || i == 2) && !this.mHintAnimationRunning) {
                this.mAffordanceHelper.reset(true);
            }
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView.OnHeightChangedListener
    public void onHeightChanged(ExpandableView view, boolean needsAnimation) {
        ExpandableNotificationRow firstRow;
        if (view == null && this.mQsExpanded) {
            return;
        }
        if (needsAnimation && this.mInterpolatedDarkAmount == 0.0f) {
            this.mAnimateNextPositionUpdate = true;
        }
        ExpandableView firstChildNotGone = this.mNotificationStackScroller.getFirstChildNotGone();
        if (firstChildNotGone instanceof ExpandableNotificationRow) {
            firstRow = (ExpandableNotificationRow) firstChildNotGone;
        } else {
            firstRow = null;
        }
        if (firstRow != null && (view == firstRow || firstRow.getNotificationParent() == firstRow)) {
            requestScrollerTopPaddingUpdate(false);
        }
        requestPanelHeightUpdate();
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView.OnHeightChangedListener
    public void onReset(ExpandableView view) {
    }

    @Override // com.android.systemui.plugins.qs.QS.HeightListener
    public void onQsHeightChanged() {
        QS qs = this.mQs;
        this.mQsMaxExpansionHeight = qs != null ? qs.getDesiredHeight() : 0;
        if (this.mQsExpanded && this.mQsFullyExpanded) {
            this.mQsExpansionHeight = this.mQsMaxExpansionHeight;
            requestScrollerTopPaddingUpdate(false);
            requestPanelHeightUpdate();
        }
        if (this.mAccessibilityManager.isEnabled()) {
            setAccessibilityPaneTitle(determineAccessibilityPaneTitle());
        }
        this.mNotificationStackScroller.setMaxTopPadding(this.mQsMaxExpansionHeight + this.mQsNotificationTopPadding);
    }

    @Override // com.android.systemui.statusbar.phone.PanelView, android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mAffordanceHelper.onConfigurationChanged();
        if (newConfig.orientation != this.mLastOrientation) {
            resetHorizontalPanelPosition();
        }
        this.mLastOrientation = newConfig.orientation;
    }

    @Override // android.view.View
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        this.mNavigationBarBottomHeight = insets.getStableInsetBottom();
        updateMaxHeadsUpTranslation();
        return insets;
    }

    private void updateMaxHeadsUpTranslation() {
        this.mNotificationStackScroller.setHeadsUpBoundaries(getHeight(), this.mNavigationBarBottomHeight);
    }

    @Override // android.view.View
    public void onRtlPropertiesChanged(int layoutDirection) {
        if (layoutDirection != this.mOldLayoutDirection) {
            this.mAffordanceHelper.onRtlPropertiesChanged();
            this.mOldLayoutDirection = layoutDirection;
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        onQsExpansionStarted();
        if (this.mQsExpanded) {
            flingSettings(0.0f, 1, null, true);
        } else if (this.mQsExpansionEnabled) {
            this.mLockscreenGestureLogger.write(195, 0, 0);
            flingSettings(0.0f, 0, null, true);
        }
    }

    @Override // com.android.systemui.statusbar.phone.KeyguardAffordanceHelper.Callback
    public void onAnimationToSideStarted(boolean rightPage, float translation, float vel) {
        boolean start = getLayoutDirection() == 1 ? rightPage : !rightPage;
        this.mIsLaunchTransitionRunning = true;
        this.mLaunchAnimationEndRunnable = null;
        float displayDensity = this.mStatusBar.getDisplayDensity();
        int lengthDp = Math.abs((int) (translation / displayDensity));
        int velocityDp = Math.abs((int) (vel / displayDensity));
        if (start) {
            this.mLockscreenGestureLogger.write(190, lengthDp, velocityDp);
            this.mFalsingManager.onLeftAffordanceOn();
            if (this.mFalsingManager.shouldEnforceBouncer()) {
                this.mStatusBar.executeRunnableDismissingKeyguard(new Runnable() { // from class: com.android.systemui.statusbar.phone.NotificationPanelView.19
                    @Override // java.lang.Runnable
                    public void run() {
                        NotificationPanelView.this.mKeyguardBottomArea.launchLeftAffordance();
                    }
                }, null, true, false, true);
            } else {
                this.mKeyguardBottomArea.launchLeftAffordance();
            }
        } else {
            if (KeyguardBottomAreaView.CAMERA_LAUNCH_SOURCE_AFFORDANCE.equals(this.mLastCameraLaunchSource)) {
                this.mLockscreenGestureLogger.write(189, lengthDp, velocityDp);
            }
            this.mFalsingManager.onCameraOn();
            if (this.mFalsingManager.shouldEnforceBouncer()) {
                this.mStatusBar.executeRunnableDismissingKeyguard(new Runnable() { // from class: com.android.systemui.statusbar.phone.NotificationPanelView.20
                    @Override // java.lang.Runnable
                    public void run() {
                        NotificationPanelView.this.mKeyguardBottomArea.launchCamera(NotificationPanelView.this.mLastCameraLaunchSource);
                    }
                }, null, true, false, true);
            } else {
                this.mKeyguardBottomArea.launchCamera(this.mLastCameraLaunchSource);
            }
        }
        this.mStatusBar.startLaunchTransitionTimeout();
        this.mBlockTouches = true;
    }

    @Override // com.android.systemui.statusbar.phone.KeyguardAffordanceHelper.Callback
    public void onAnimationToSideEnded() {
        this.mIsLaunchTransitionRunning = false;
        this.mIsLaunchTransitionFinished = true;
        Runnable runnable = this.mLaunchAnimationEndRunnable;
        if (runnable != null) {
            runnable.run();
            this.mLaunchAnimationEndRunnable = null;
        }
        this.mStatusBar.readyForKeyguardDone();
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected void startUnlockHintAnimation() {
        if (this.mPowerManager.isPowerSaveMode()) {
            onUnlockHintStarted();
            onUnlockHintFinished();
            return;
        }
        super.startUnlockHintAnimation();
    }

    @Override // com.android.systemui.statusbar.phone.KeyguardAffordanceHelper.Callback
    public float getMaxTranslationDistance() {
        return (float) Math.hypot(getWidth(), getHeight());
    }

    @Override // com.android.systemui.statusbar.phone.KeyguardAffordanceHelper.Callback
    public void onSwipingStarted(boolean rightIcon) {
        boolean camera;
        this.mFalsingManager.onAffordanceSwipingStarted(rightIcon);
        if (getLayoutDirection() == 1) {
            camera = !rightIcon;
        } else {
            camera = rightIcon;
        }
        if (camera) {
            this.mKeyguardBottomArea.bindCameraPrewarmService();
        }
        requestDisallowInterceptTouchEvent(true);
        this.mOnlyAffordanceInThisMotion = true;
        this.mQsTracking = false;
    }

    @Override // com.android.systemui.statusbar.phone.KeyguardAffordanceHelper.Callback
    public void onSwipingAborted() {
        this.mFalsingManager.onAffordanceSwipingAborted();
        this.mKeyguardBottomArea.unbindCameraPrewarmService(false);
    }

    @Override // com.android.systemui.statusbar.phone.KeyguardAffordanceHelper.Callback
    public void onIconClicked(boolean rightIcon) {
        if (this.mHintAnimationRunning) {
            return;
        }
        boolean rightIcon2 = true;
        this.mHintAnimationRunning = true;
        this.mAffordanceHelper.startHintAnimation(rightIcon, new Runnable() { // from class: com.android.systemui.statusbar.phone.NotificationPanelView.21
            @Override // java.lang.Runnable
            public void run() {
                NotificationPanelView notificationPanelView = NotificationPanelView.this;
                notificationPanelView.mHintAnimationRunning = false;
                notificationPanelView.mStatusBar.onHintFinished();
            }
        });
        if (getLayoutDirection() != 1) {
            rightIcon2 = rightIcon;
        } else if (rightIcon) {
            rightIcon2 = false;
        }
        if (rightIcon2) {
            this.mStatusBar.onCameraHintStarted();
        } else if (this.mKeyguardBottomArea.isLeftVoiceAssist()) {
            this.mStatusBar.onVoiceAssistHintStarted();
        } else {
            this.mStatusBar.onPhoneHintStarted();
        }
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected void onUnlockHintFinished() {
        super.onUnlockHintFinished();
        this.mNotificationStackScroller.setUnlockHintRunning(false);
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected void onUnlockHintStarted() {
        super.onUnlockHintStarted();
        this.mNotificationStackScroller.setUnlockHintRunning(true);
    }

    @Override // com.android.systemui.statusbar.phone.KeyguardAffordanceHelper.Callback
    public KeyguardAffordanceView getLeftIcon() {
        if (getLayoutDirection() == 1) {
            return this.mKeyguardBottomArea.getRightView();
        }
        return this.mKeyguardBottomArea.getLeftView();
    }

    @Override // com.android.systemui.statusbar.phone.KeyguardAffordanceHelper.Callback
    public KeyguardAffordanceView getRightIcon() {
        if (getLayoutDirection() == 1) {
            return this.mKeyguardBottomArea.getLeftView();
        }
        return this.mKeyguardBottomArea.getRightView();
    }

    @Override // com.android.systemui.statusbar.phone.KeyguardAffordanceHelper.Callback
    public View getLeftPreview() {
        if (getLayoutDirection() == 1) {
            return this.mKeyguardBottomArea.getRightPreview();
        }
        return this.mKeyguardBottomArea.getLeftPreview();
    }

    @Override // com.android.systemui.statusbar.phone.KeyguardAffordanceHelper.Callback
    public View getRightPreview() {
        if (getLayoutDirection() == 1) {
            return this.mKeyguardBottomArea.getLeftPreview();
        }
        return this.mKeyguardBottomArea.getRightPreview();
    }

    @Override // com.android.systemui.statusbar.phone.KeyguardAffordanceHelper.Callback
    public float getAffordanceFalsingFactor() {
        return this.mStatusBar.isWakeUpComingFromTouch() ? 1.5f : 1.0f;
    }

    @Override // com.android.systemui.statusbar.phone.KeyguardAffordanceHelper.Callback
    public boolean needsAntiFalsing() {
        return this.mBarState == 1;
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected float getPeekHeight() {
        if (this.mNotificationStackScroller.getNotGoneChildCount() > 0) {
            return this.mNotificationStackScroller.getPeekHeight();
        }
        return this.mQsMinExpansionHeight;
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected boolean shouldUseDismissingAnimation() {
        return (this.mBarState == 0 || (this.mStatusBar.isKeyguardCurrentlySecure() && isTracking())) ? false : true;
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected boolean fullyExpandedClearAllVisible() {
        return this.mNotificationStackScroller.isFooterViewNotGone() && this.mNotificationStackScroller.isScrolledToBottom() && !this.mQsExpandImmediate;
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected boolean isClearAllVisible() {
        return this.mNotificationStackScroller.isFooterViewContentVisible();
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected int getClearAllHeight() {
        return this.mNotificationStackScroller.getFooterViewHeight();
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected boolean isTrackingBlocked() {
        return (this.mConflictingQsExpansionGesture && this.mQsExpanded) || this.mBlockingExpansionForCurrentTouch;
    }

    public boolean isQsExpanded() {
        return this.mQsExpanded;
    }

    public boolean isQsDetailShowing() {
        return this.mQs.isShowingDetail();
    }

    public void closeQsDetail() {
        this.mQs.closeDetail();
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup
    public boolean shouldDelayChildPressedState() {
        return true;
    }

    public boolean isLaunchTransitionFinished() {
        return this.mIsLaunchTransitionFinished;
    }

    public boolean isLaunchTransitionRunning() {
        return this.mIsLaunchTransitionRunning;
    }

    public void setLaunchTransitionEndRunnable(Runnable r) {
        this.mLaunchAnimationEndRunnable = r;
    }

    @Override // com.android.systemui.statusbar.PulseExpansionHandler.ExpansionCallback
    public void setEmptyDragAmount(float amount) {
        this.mEmptyDragAmount = 0.2f * amount;
        positionClockAndNotifications();
    }

    private void updateDozingVisibilities(boolean animate) {
        this.mKeyguardBottomArea.setDozing(this.mDozing, animate);
        if (!this.mDozing && animate) {
            animateKeyguardStatusBarIn(360L);
        }
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    public boolean isDozing() {
        return this.mDozing;
    }

    public void showEmptyShadeView(boolean emptyShadeViewVisible) {
        this.mShowEmptyShadeView = emptyShadeViewVisible;
        updateEmptyShadeView();
    }

    private void updateEmptyShadeView() {
        this.mNotificationStackScroller.updateEmptyShadeView(this.mShowEmptyShadeView && !this.mQsExpanded);
    }

    public void setQsScrimEnabled(boolean qsScrimEnabled) {
        boolean changed = this.mQsScrimEnabled != qsScrimEnabled;
        this.mQsScrimEnabled = qsScrimEnabled;
        if (changed) {
            updateQsState();
        }
    }

    public void setKeyguardUserSwitcher(KeyguardUserSwitcher keyguardUserSwitcher) {
        this.mKeyguardUserSwitcher = keyguardUserSwitcher;
    }

    public void onScreenTurningOn() {
        this.mKeyguardStatusView.dozeTimeTick();
    }

    @Override // com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout.OnEmptySpaceClickListener
    public void onEmptySpaceClicked(float x, float y) {
        onEmptySpaceClick(x);
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected boolean onMiddleClicked() {
        int i = this.mBarState;
        if (i == 0) {
            post(this.mPostCollapseRunnable);
            return false;
        } else if (i == 1) {
            if (!this.mDozingOnDown) {
                if (this.mKeyguardBypassController.getBypassEnabled()) {
                    this.mUpdateMonitor.requestFaceAuth();
                } else {
                    this.mLockscreenGestureLogger.write(Opcodes.NEWARRAY, 0, 0);
                    startUnlockHintAnimation();
                }
            }
            return true;
        } else {
            if (i == 2 && !this.mQsExpanded) {
                this.mShadeController.goToKeyguard();
            }
            return true;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.mCurrentPanelAlpha != 255) {
            canvas.drawRect(0.0f, 0.0f, canvas.getWidth(), canvas.getHeight(), this.mAlphaPaint);
        }
    }

    public float getCurrentPanelAlpha() {
        return this.mCurrentPanelAlpha;
    }

    public boolean setPanelAlpha(int alpha, boolean animate) {
        if (this.mPanelAlpha != alpha) {
            this.mPanelAlpha = alpha;
            PropertyAnimator.setProperty(this, this.PANEL_ALPHA, alpha, alpha == 255 ? this.PANEL_ALPHA_IN_PROPERTIES : this.PANEL_ALPHA_OUT_PROPERTIES, animate);
            return true;
        }
        return false;
    }

    public void setPanelAlphaInternal(float alpha) {
        this.mCurrentPanelAlpha = (int) alpha;
        this.mAlphaPaint.setARGB(this.mCurrentPanelAlpha, 255, 255, 255);
        invalidate();
    }

    public void setPanelAlphaEndAction(Runnable r) {
        this.mPanelAlphaEndAction = r;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override // com.android.systemui.statusbar.policy.OnHeadsUpChangedListener
    public void onHeadsUpPinnedModeChanged(boolean inPinnedMode) {
        this.mNotificationStackScroller.setInHeadsUpPinnedMode(inPinnedMode);
        if (inPinnedMode) {
            this.mHeadsUpExistenceChangedRunnable.run();
            updateNotificationTranslucency();
        } else {
            setHeadsUpAnimatingAway(true);
            this.mNotificationStackScroller.runAfterAnimationFinished(this.mHeadsUpExistenceChangedRunnable);
        }
        updateGestureExclusionRect();
        this.mHeadsUpPinnedMode = inPinnedMode;
        updateHeadsUpVisibility();
        updateKeyguardStatusBarForHeadsUp();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateKeyguardStatusBarForHeadsUp() {
        boolean showingKeyguardHeadsUp = this.mKeyguardShowing && this.mHeadsUpAppearanceController.shouldBeVisible();
        if (this.mShowingKeyguardHeadsUp != showingKeyguardHeadsUp) {
            this.mShowingKeyguardHeadsUp = showingKeyguardHeadsUp;
            if (this.mKeyguardShowing) {
                PropertyAnimator.setProperty(this, KEYGUARD_HEADS_UP_SHOWING_AMOUNT, showingKeyguardHeadsUp ? 1.0f : 0.0f, KEYGUARD_HUN_PROPERTIES, true);
            } else {
                PropertyAnimator.applyImmediately(this, KEYGUARD_HEADS_UP_SHOWING_AMOUNT, 0.0f);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setKeyguardHeadsUpShowingAmount(float amount) {
        this.mKeyguardHeadsUpShowingAmount = amount;
        updateHeaderKeyguardAlpha();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getKeyguardHeadsUpShowingAmount() {
        return this.mKeyguardHeadsUpShowingAmount;
    }

    public void setHeadsUpAnimatingAway(boolean headsUpAnimatingAway) {
        this.mHeadsUpAnimatingAway = headsUpAnimatingAway;
        this.mNotificationStackScroller.setHeadsUpAnimatingAway(headsUpAnimatingAway);
        updateHeadsUpVisibility();
    }

    private void updateHeadsUpVisibility() {
        ((PhoneStatusBarView) this.mBar).setHeadsUpVisible(this.mHeadsUpAnimatingAway || this.mHeadsUpPinnedMode);
    }

    @Override // com.android.systemui.statusbar.policy.OnHeadsUpChangedListener
    public void onHeadsUpPinned(NotificationEntry entry) {
        if (!isOnKeyguard()) {
            this.mNotificationStackScroller.generateHeadsUpAnimation(entry.getHeadsUpAnimationView(), true);
        }
    }

    @Override // com.android.systemui.statusbar.policy.OnHeadsUpChangedListener
    public void onHeadsUpUnPinned(NotificationEntry entry) {
        if (isFullyCollapsed() && entry.isRowHeadsUp() && !isOnKeyguard()) {
            this.mNotificationStackScroller.generateHeadsUpAnimation(entry.getHeadsUpAnimationView(), false);
            entry.setHeadsUpIsVisible();
        }
    }

    @Override // com.android.systemui.statusbar.policy.OnHeadsUpChangedListener
    public void onHeadsUpStateChanged(NotificationEntry entry, boolean isHeadsUp) {
        this.mNotificationStackScroller.generateHeadsUpAnimation(entry, isHeadsUp);
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    public void setHeadsUpManager(HeadsUpManagerPhone headsUpManager) {
        super.setHeadsUpManager(headsUpManager);
        this.mHeadsUpTouchHelper = new HeadsUpTouchHelper(headsUpManager, this.mNotificationStackScroller.getHeadsUpCallback(), this);
    }

    public void setTrackedHeadsUp(ExpandableNotificationRow pickedChild) {
        if (pickedChild != null) {
            notifyListenersTrackingHeadsUp(pickedChild);
            this.mExpandingFromHeadsUp = true;
        }
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected void onClosingFinished() {
        super.onClosingFinished();
        resetHorizontalPanelPosition();
        setClosingWithAlphaFadeout(false);
    }

    private void setClosingWithAlphaFadeout(boolean closing) {
        this.mClosingWithAlphaFadeOut = closing;
        this.mNotificationStackScroller.forceNoOverlappingRendering(closing);
    }

    protected void updateVerticalPanelPosition(float x) {
        if (this.mNotificationStackScroller.getWidth() * 1.75f > getWidth()) {
            resetHorizontalPanelPosition();
            return;
        }
        float leftMost = this.mPositionMinSideMargin + (this.mNotificationStackScroller.getWidth() / 2);
        float rightMost = (getWidth() - this.mPositionMinSideMargin) - (this.mNotificationStackScroller.getWidth() / 2);
        if (Math.abs(x - (getWidth() / 2)) < this.mNotificationStackScroller.getWidth() / 4) {
            x = getWidth() / 2;
        }
        float x2 = Math.min(rightMost, Math.max(leftMost, x));
        float center = this.mNotificationStackScroller.getLeft() + (this.mNotificationStackScroller.getWidth() / 2);
        setHorizontalPanelTranslation(x2 - center);
    }

    private void resetHorizontalPanelPosition() {
        setHorizontalPanelTranslation(0.0f);
    }

    protected void setHorizontalPanelTranslation(float translation) {
        this.mNotificationStackScroller.setTranslationX(translation);
        this.mQsFrame.setTranslationX(translation);
        int size = this.mVerticalTranslationListener.size();
        for (int i = 0; i < size; i++) {
            this.mVerticalTranslationListener.get(i).run();
        }
    }

    protected void updateExpandedHeight(float expandedHeight) {
        if (this.mTracking) {
            this.mNotificationStackScroller.setExpandingVelocity(getCurrentExpandVelocity());
        }
        if (this.mKeyguardBypassController.getBypassEnabled() && isOnKeyguard()) {
            expandedHeight = getMaxPanelHeightNonBypass();
        }
        this.mNotificationStackScroller.setExpandedHeight(expandedHeight);
        updateKeyguardBottomAreaAlpha();
        updateBigClockAlpha();
        updateStatusBarIcons();
    }

    public boolean isFullWidth() {
        return this.mIsFullWidth;
    }

    private void updateStatusBarIcons() {
        boolean showIconsWhenExpanded = (isPanelVisibleBecauseOfHeadsUp() || isFullWidth()) && getExpandedHeight() < getOpeningHeight();
        if (showIconsWhenExpanded && this.mNoVisibleNotifications && isOnKeyguard()) {
            showIconsWhenExpanded = false;
        }
        if (showIconsWhenExpanded != this.mShowIconsWhenExpanded) {
            this.mShowIconsWhenExpanded = showIconsWhenExpanded;
            this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, false);
        }
    }

    private boolean isOnKeyguard() {
        return this.mBarState == 1;
    }

    public void setPanelScrimMinFraction(float minFraction) {
        this.mBar.panelScrimMinFractionChanged(minFraction);
    }

    public void clearNotificationEffects() {
        this.mStatusBar.clearNotificationEffects();
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    protected boolean isPanelVisibleBecauseOfHeadsUp() {
        return (this.mHeadsUpManager.hasPinnedHeadsUp() || this.mHeadsUpAnimatingAway) && this.mBarState == 0;
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return !this.mDozing;
    }

    public void launchCamera(boolean animate, int source) {
        if (source == 1) {
            this.mLastCameraLaunchSource = KeyguardBottomAreaView.CAMERA_LAUNCH_SOURCE_POWER_DOUBLE_TAP;
        } else if (source == 0) {
            this.mLastCameraLaunchSource = KeyguardBottomAreaView.CAMERA_LAUNCH_SOURCE_WIGGLE;
        } else if (source == 2) {
            this.mLastCameraLaunchSource = KeyguardBottomAreaView.CAMERA_LAUNCH_SOURCE_LIFT_TRIGGER;
        } else {
            this.mLastCameraLaunchSource = KeyguardBottomAreaView.CAMERA_LAUNCH_SOURCE_AFFORDANCE;
        }
        if (!isFullyCollapsed()) {
            setLaunchingAffordance(true);
        } else {
            animate = false;
        }
        this.mAffordanceHasPreview = this.mKeyguardBottomArea.getRightPreview() != null;
        this.mAffordanceHelper.launchAffordance(animate, getLayoutDirection() == 1);
    }

    public void onAffordanceLaunchEnded() {
        setLaunchingAffordance(false);
    }

    private void setLaunchingAffordance(boolean launchingAffordance) {
        this.mLaunchingAffordance = launchingAffordance;
        getLeftIcon().setLaunchingAffordance(launchingAffordance);
        getRightIcon().setLaunchingAffordance(launchingAffordance);
        this.mKeyguardBypassController.setLaunchingAffordance(launchingAffordance);
        Consumer<Boolean> consumer = this.mAffordanceLaunchListener;
        if (consumer != null) {
            consumer.accept(Boolean.valueOf(launchingAffordance));
        }
    }

    public boolean isLaunchingAffordanceWithPreview() {
        return this.mLaunchingAffordance && this.mAffordanceHasPreview;
    }

    public boolean canCameraGestureBeLaunched(boolean keyguardIsShowing) {
        if (this.mStatusBar.isCameraAllowedByAdmin()) {
            ResolveInfo resolveInfo = this.mKeyguardBottomArea.resolveCameraIntent();
            String packageToLaunch = (resolveInfo == null || resolveInfo.activityInfo == null) ? null : resolveInfo.activityInfo.packageName;
            if (packageToLaunch != null) {
                return (keyguardIsShowing || !isForegroundApp(packageToLaunch)) && !this.mAffordanceHelper.isSwipingInProgress();
            }
            return false;
        }
        return false;
    }

    private boolean isForegroundApp(String pkgName) {
        ActivityManager am = (ActivityManager) getContext().getSystemService(ActivityManager.class);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        return !tasks.isEmpty() && pkgName.equals(tasks.get(0).topActivity.getPackageName());
    }

    private void setGroupManager(NotificationGroupManager groupManager) {
        this.mGroupManager = groupManager;
    }

    public boolean hideStatusBarIconsWhenExpanded() {
        if (this.mLaunchingNotification) {
            return this.mHideIconsDuringNotificationLaunch;
        }
        HeadsUpAppearanceController headsUpAppearanceController = this.mHeadsUpAppearanceController;
        if (headsUpAppearanceController == null || !headsUpAppearanceController.shouldBeVisible()) {
            return (isFullWidth() && this.mShowIconsWhenExpanded) ? false : true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.statusbar.phone.NotificationPanelView$22  reason: invalid class name */
    /* loaded from: classes21.dex */
    public class AnonymousClass22 implements FragmentHostManager.FragmentListener {
        AnonymousClass22() {
        }

        @Override // com.android.systemui.fragments.FragmentHostManager.FragmentListener
        public void onFragmentViewCreated(String tag, Fragment fragment) {
            NotificationPanelView.this.mQs = (QS) fragment;
            NotificationPanelView.this.mQs.setPanelView(NotificationPanelView.this);
            NotificationPanelView.this.mQs.setExpandClickListener(NotificationPanelView.this);
            NotificationPanelView.this.mQs.setHeaderClickable(NotificationPanelView.this.mQsExpansionEnabled);
            NotificationPanelView.this.updateQSPulseExpansion();
            NotificationPanelView.this.mQs.setOverscrolling(NotificationPanelView.this.mStackScrollerOverscrolling);
            NotificationPanelView.this.mQs.getView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NotificationPanelView$22$__XIt3ZYZTq_RPQPhfW5iii8b-o
                @Override // android.view.View.OnLayoutChangeListener
                public final void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                    NotificationPanelView.AnonymousClass22.this.lambda$onFragmentViewCreated$0$NotificationPanelView$22(view, i, i2, i3, i4, i5, i6, i7, i8);
                }
            });
            NotificationPanelView.this.mNotificationStackScroller.setQsContainer((ViewGroup) NotificationPanelView.this.mQs.getView());
            if (NotificationPanelView.this.mQs instanceof QSFragment) {
                NotificationPanelView.this.mKeyguardStatusBar.setQSPanel(((QSFragment) NotificationPanelView.this.mQs).getQsPanel());
            }
            NotificationPanelView.this.updateQsExpansion();
        }

        public /* synthetic */ void lambda$onFragmentViewCreated$0$NotificationPanelView$22(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            int height = bottom - top;
            int oldHeight = oldBottom - oldTop;
            if (height != oldHeight) {
                NotificationPanelView.this.onQsHeightChanged();
            }
        }

        @Override // com.android.systemui.fragments.FragmentHostManager.FragmentListener
        public void onFragmentViewDestroyed(String tag, Fragment fragment) {
            if (fragment == NotificationPanelView.this.mQs) {
                NotificationPanelView.this.mQs = null;
            }
        }
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    public void setTouchAndAnimationDisabled(boolean disabled) {
        super.setTouchAndAnimationDisabled(disabled);
        if (disabled && this.mAffordanceHelper.isSwipingInProgress() && !this.mIsLaunchTransitionRunning) {
            this.mAffordanceHelper.reset(false);
        }
        this.mNotificationStackScroller.setAnimationsEnabled(!disabled);
    }

    public void setDozing(boolean dozing, boolean animate, PointF wakeUpTouchLocation) {
        if (dozing == this.mDozing) {
            return;
        }
        this.mDozing = dozing;
        this.mNotificationStackScroller.setDozing(this.mDozing, animate, wakeUpTouchLocation);
        this.mKeyguardBottomArea.setDozing(this.mDozing, animate);
        if (dozing) {
            this.mBottomAreaShadeAlphaAnimator.cancel();
        }
        int i = this.mBarState;
        if (i == 1 || i == 2) {
            updateDozingVisibilities(animate);
        }
        float dozeAmount = dozing ? 1.0f : 0.0f;
        this.mStatusBarStateController.setDozeAmount(dozeAmount, animate);
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onDozeAmountChanged(float linearAmount, float amount) {
        this.mInterpolatedDarkAmount = amount;
        this.mLinearDarkAmount = linearAmount;
        this.mKeyguardStatusView.setDarkAmount(this.mInterpolatedDarkAmount);
        this.mKeyguardBottomArea.setDarkAmount(this.mInterpolatedDarkAmount);
        positionClockAndNotifications();
    }

    public void setPulsing(boolean pulsing) {
        this.mPulsing = pulsing;
        DozeParameters dozeParameters = DozeParameters.getInstance(this.mContext);
        boolean animatePulse = !dozeParameters.getDisplayNeedsBlanking() && dozeParameters.getAlwaysOn();
        if (animatePulse) {
            this.mAnimateNextPositionUpdate = true;
        }
        if (!this.mPulsing && !this.mDozing) {
            this.mAnimateNextPositionUpdate = false;
        }
        this.mNotificationStackScroller.setPulsing(pulsing, animatePulse);
        this.mKeyguardStatusView.setPulsing(pulsing);
    }

    public void setAmbientIndicationBottomPadding(int ambientIndicationBottomPadding) {
        if (this.mAmbientIndicationBottomPadding != ambientIndicationBottomPadding) {
            this.mAmbientIndicationBottomPadding = ambientIndicationBottomPadding;
            this.mStatusBar.updateKeyguardMaxNotifications();
        }
    }

    public void dozeTimeTick() {
        this.mKeyguardBottomArea.dozeTimeTick();
        this.mKeyguardStatusView.dozeTimeTick();
        if (this.mInterpolatedDarkAmount > 0.0f) {
            positionClockAndNotifications();
        }
    }

    public void setStatusAccessibilityImportance(int mode) {
        this.mKeyguardStatusView.setImportantForAccessibility(mode);
    }

    public KeyguardBottomAreaView getKeyguardBottomAreaView() {
        return this.mKeyguardBottomArea;
    }

    public void setUserSetupComplete(boolean userSetupComplete) {
        this.mUserSetupComplete = userSetupComplete;
        this.mKeyguardBottomArea.setUserSetupComplete(userSetupComplete);
    }

    public void applyExpandAnimationParams(ActivityLaunchAnimator.ExpandAnimationParameters params) {
        this.mExpandOffset = params != null ? params.getTopChange() : 0.0f;
        updateQsExpansion();
        if (params != null) {
            boolean hideIcons = params.getProgress(14L, 100L) == 0.0f;
            if (hideIcons != this.mHideIconsDuringNotificationLaunch) {
                this.mHideIconsDuringNotificationLaunch = hideIcons;
                if (!hideIcons) {
                    this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, true);
                }
            }
        }
    }

    public void addTrackingHeadsUpListener(Consumer<ExpandableNotificationRow> listener) {
        this.mTrackingHeadsUpListeners.add(listener);
    }

    public void removeTrackingHeadsUpListener(Consumer<ExpandableNotificationRow> listener) {
        this.mTrackingHeadsUpListeners.remove(listener);
    }

    public void addVerticalTranslationListener(Runnable verticalTranslationListener) {
        this.mVerticalTranslationListener.add(verticalTranslationListener);
    }

    public void removeVerticalTranslationListener(Runnable verticalTranslationListener) {
        this.mVerticalTranslationListener.remove(verticalTranslationListener);
    }

    public void setHeadsUpAppearanceController(HeadsUpAppearanceController headsUpAppearanceController) {
        this.mHeadsUpAppearanceController = headsUpAppearanceController;
    }

    public void onBouncerPreHideAnimation() {
        setKeyguardStatusViewVisibility(this.mBarState, true, false);
    }

    public void blockExpansionForCurrentTouch() {
        this.mBlockingExpansionForCurrentTouch = this.mTracking;
    }

    @Override // com.android.systemui.statusbar.phone.PanelView
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        super.dump(fd, pw, args);
        pw.println("    gestureExclusionRect: " + calculateGestureExclusionRect());
        KeyguardStatusBarView keyguardStatusBarView = this.mKeyguardStatusBar;
        if (keyguardStatusBarView != null) {
            keyguardStatusBarView.dump(fd, pw, args);
        }
        KeyguardStatusView keyguardStatusView = this.mKeyguardStatusView;
        if (keyguardStatusView != null) {
            keyguardStatusView.dump(fd, pw, args);
        }
    }

    public boolean hasActiveClearableNotifications() {
        return this.mNotificationStackScroller.hasActiveClearableNotifications(0);
    }

    @Override // com.android.systemui.statusbar.policy.ZenModeController.Callback
    public void onZenChanged(int zen) {
        updateShowEmptyShadeView();
    }

    private void updateShowEmptyShadeView() {
        boolean z = true;
        boolean showEmptyShadeView = (this.mBarState == 1 || this.mEntryManager.getNotificationData().getActiveNotifications().size() != 0) ? false : false;
        showEmptyShadeView(showEmptyShadeView);
    }

    public RemoteInputController.Delegate createRemoteInputDelegate() {
        return this.mNotificationStackScroller.createDelegate();
    }

    public void updateNotificationViews() {
        this.mNotificationStackScroller.updateSectionBoundaries();
        this.mNotificationStackScroller.updateSpeedBumpIndex();
        this.mNotificationStackScroller.updateFooter();
        updateShowEmptyShadeView();
        this.mNotificationStackScroller.updateIconAreaViews();
    }

    public void onUpdateRowStates() {
        this.mNotificationStackScroller.onUpdateRowStates();
    }

    public boolean hasPulsingNotifications() {
        return this.mNotificationStackScroller.hasPulsingNotifications();
    }

    public ActivatableNotificationView getActivatedChild() {
        return this.mNotificationStackScroller.getActivatedChild();
    }

    public void setActivatedChild(ActivatableNotificationView o) {
        this.mNotificationStackScroller.setActivatedChild(o);
    }

    public void runAfterAnimationFinished(Runnable r) {
        this.mNotificationStackScroller.runAfterAnimationFinished(r);
    }

    public void setScrollingEnabled(boolean b) {
        this.mNotificationStackScroller.setScrollingEnabled(b);
    }

    public void initDependencies(StatusBar statusBar, NotificationGroupManager groupManager, NotificationShelf notificationShelf, HeadsUpManagerPhone headsUpManager, NotificationIconAreaController notificationIconAreaController, ScrimController scrimController) {
        setStatusBar(statusBar);
        setGroupManager(this.mGroupManager);
        this.mNotificationStackScroller.setNotificationPanel(this);
        this.mNotificationStackScroller.setIconAreaController(notificationIconAreaController);
        this.mNotificationStackScroller.setStatusBar(statusBar);
        this.mNotificationStackScroller.setGroupManager(groupManager);
        this.mNotificationStackScroller.setShelf(notificationShelf);
        this.mNotificationStackScroller.setScrimController(scrimController);
        updateShowEmptyShadeView();
    }

    public void showTransientIndication(int id) {
        this.mKeyguardIndicationController.showTransientIndication(id);
    }

    @Override // com.android.systemui.statusbar.notification.DynamicPrivacyController.Listener
    public void onDynamicPrivacyChanged() {
        if (this.mLinearDarkAmount != 0.0f) {
            return;
        }
        this.mAnimateNextPositionUpdate = true;
    }

    public void setOnReinflationListener(Runnable onReinflationListener) {
        this.mOnReinflationListener = onReinflationListener;
    }
}
