package com.android.systemui.statusbar.notification.stack;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.ServiceManager;
import android.service.notification.StatusBarNotification;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.MathUtils;
import android.util.Pair;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.OverScroller;
import android.widget.ScrollView;
import com.alibaba.fastjson.asm.Opcodes;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.graphics.ColorUtils;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.statusbar.IStatusBarService;
import com.android.settingslib.Utils;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.ExpandHelper;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;
import com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.DragDownHelper;
import com.android.systemui.statusbar.EmptyShadeView;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationShelf;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.notification.ActivityLaunchAnimator;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.statusbar.notification.VisibilityLocationProvider;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.notification.row.FooterView;
import com.android.systemui.statusbar.notification.row.NotificationBlockingHelperManager;
import com.android.systemui.statusbar.notification.row.NotificationGuts;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager;
import com.android.systemui.statusbar.notification.row.NotificationSnooze;
import com.android.systemui.statusbar.notification.row.StackScrollerDecorView;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.notification.stack.NotificationSwipeHelper;
import com.android.systemui.statusbar.phone.HeadsUpAppearanceController;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.HeadsUpTouchHelper;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.LockscreenGestureLogger;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.NotificationIconAreaController;
import com.android.systemui.statusbar.phone.NotificationPanelView;
import com.android.systemui.statusbar.phone.ScrimController;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.HeadsUpUtil;
import com.android.systemui.statusbar.policy.ScrollAdapter;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.Assert;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.inject.Inject;
import javax.inject.Named;
/* loaded from: classes21.dex */
public class NotificationStackScrollLayout extends ViewGroup implements ScrollAdapter, NotificationListContainer, ConfigurationController.ConfigurationListener, Dumpable, DynamicPrivacyController.Listener {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final float BACKGROUND_ALPHA_DIMMED = 0.7f;
    private static final boolean DEBUG = false;
    private static final int DISTANCE_BETWEEN_ADJACENT_SECTIONS_PX = 1;
    private static final int INVALID_POINTER = -1;
    static final int NUM_SECTIONS = 2;
    public static final int ROWS_ALL = 0;
    public static final int ROWS_GENTLE = 2;
    public static final int ROWS_HIGH_PRIORITY = 1;
    private static final float RUBBER_BAND_FACTOR_AFTER_EXPAND = 0.15f;
    private static final float RUBBER_BAND_FACTOR_NORMAL = 0.35f;
    private static final float RUBBER_BAND_FACTOR_ON_PANEL_EXPAND = 0.21f;
    private static final String TAG = "StackScroller";
    private boolean mActivateNeedsAnimation;
    private int mActivePointerId;
    private ArrayList<View> mAddedHeadsUpChildren;
    private final boolean mAllowLongPress;
    private final AmbientState mAmbientState;
    private boolean mAnimateBottomOnLayout;
    private boolean mAnimateNextBackgroundBottom;
    private boolean mAnimateNextBackgroundTop;
    private boolean mAnimateNextSectionBoundsChange;
    private ArrayList<AnimationEvent> mAnimationEvents;
    private HashSet<Runnable> mAnimationFinishedRunnables;
    private boolean mAnimationRunning;
    private boolean mAnimationsEnabled;
    private final Rect mBackgroundAnimationRect;
    private final Paint mBackgroundPaint;
    private ViewTreeObserver.OnPreDrawListener mBackgroundUpdater;
    private float mBackgroundXFactor;
    private boolean mBackwardScrollable;
    private final IStatusBarService mBarService;
    private int mBgColor;
    private int mBottomInset;
    private int mBottomMargin;
    private int mCachedBackgroundColor;
    private boolean mChangePositionInProgress;
    boolean mCheckForLeavebehind;
    private boolean mChildTransferInProgress;
    private ArrayList<ExpandableView> mChildrenChangingPositions;
    private HashSet<ExpandableView> mChildrenToAddAnimated;
    private ArrayList<ExpandableView> mChildrenToRemoveAnimated;
    private boolean mChildrenUpdateRequested;
    private ViewTreeObserver.OnPreDrawListener mChildrenUpdater;
    protected boolean mClearAllEnabled;
    private HashSet<ExpandableView> mClearTransientViewsWhenFinished;
    private final Rect mClipRect;
    private int mCollapsedSize;
    private final SysuiColorExtractor mColorExtractor;
    private int mContentHeight;
    private boolean mContinuousBackgroundUpdate;
    private boolean mContinuousShadowUpdate;
    private int mCornerRadius;
    private int mCurrentStackHeight;
    private Paint mDebugPaint;
    private float mDimAmount;
    private ValueAnimator mDimAnimator;
    private final Animator.AnimatorListener mDimEndListener;
    private ValueAnimator.AnimatorUpdateListener mDimUpdateListener;
    private boolean mDimmedNeedsAnimation;
    private boolean mDisallowDismissInThisMotion;
    private boolean mDisallowScrollingInThisMotion;
    private boolean mDismissAllInProgress;
    private boolean mDismissRtl;
    private final DisplayMetrics mDisplayMetrics;
    private boolean mDontClampNextScroll;
    private boolean mDontReportNextOverScroll;
    private int mDownX;
    private final DragDownHelper.DragDownCallback mDragDownCallback;
    private final DynamicPrivacyController mDynamicPrivacyController;
    protected EmptyShadeView mEmptyShadeView;
    private final NotificationEntryManager mEntryManager;
    private boolean mEverythingNeedsAnimation;
    private ExpandHelper mExpandHelper;
    private ExpandHelper.Callback mExpandHelperCallback;
    private ExpandableView mExpandedGroupView;
    private float mExpandedHeight;
    private ArrayList<BiConsumer<Float, Float>> mExpandedHeightListeners;
    private boolean mExpandedInThisMotion;
    private boolean mExpandingNotification;
    private boolean mFadeNotificationsOnDismiss;
    private FalsingManager mFalsingManager;
    private Runnable mFinishScrollingCallback;
    protected FooterView mFooterView;
    private boolean mForceNoOverlappingRendering;
    private View mForcedScroll;
    private boolean mForwardScrollable;
    private HashSet<View> mFromMoreCardAdditions;
    private boolean mGenerateChildOrderChangedEvent;
    private long mGoToFullShadeDelay;
    private boolean mGoToFullShadeNeedsAnimation;
    private boolean mGroupExpandedForMeasure;
    private NotificationGroupManager mGroupManager;
    private boolean mHeadsUpAnimatingAway;
    private HeadsUpAppearanceController mHeadsUpAppearanceController;
    private final HeadsUpTouchHelper.Callback mHeadsUpCallback;
    private HashSet<Pair<ExpandableNotificationRow, Boolean>> mHeadsUpChangeAnimations;
    private boolean mHeadsUpGoingAwayAnimationsAllowed;
    private int mHeadsUpInset;
    private HeadsUpManagerPhone mHeadsUpManager;
    private boolean mHideSensitiveNeedsAnimation;
    private Interpolator mHideXInterpolator;
    private boolean mHighPriorityBeforeSpeedBump;
    private NotificationIconAreaController mIconAreaController;
    private boolean mInHeadsUpPinnedMode;
    private int mIncreasedPaddingBetweenElements;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private float mInterpolatedHideAmount;
    private int mIntrinsicContentHeight;
    private int mIntrinsicPadding;
    private boolean mIsBeingDragged;
    private boolean mIsClipped;
    private boolean mIsExpanded;
    private boolean mIsExpansionChanging;
    private boolean mIsScrollerBoundSet;
    private final KeyguardBypassController mKeyguardBypassController;
    private int mLastMotionY;
    private int mLastScrollerY;
    private float mLastSentAppear;
    private float mLastSentExpandedHeight;
    private float mLinearHideAmount;
    private NotificationLogger.OnChildLocationsChangedListener mListener;
    private final LockscreenGestureLogger mLockscreenGestureLogger;
    private final NotificationLockscreenUserManager mLockscreenUserManager;
    private ExpandableNotificationRow.LongPressListener mLongPressListener;
    private int mMaxDisplayedNotifications;
    private int mMaxLayoutHeight;
    private float mMaxOverScroll;
    private int mMaxScrollAfterExpand;
    private int mMaxTopPadding;
    private int mMaximumVelocity;
    @VisibleForTesting
    protected final NotificationMenuRowPlugin.OnMenuEventListener mMenuEventListener;
    @VisibleForTesting
    protected final MetricsLogger mMetricsLogger;
    private int mMinInteractionHeight;
    private float mMinTopOverScrollToEscape;
    private int mMinimumVelocity;
    private boolean mNeedViewResizeAnimation;
    private boolean mNeedsAnimation;
    private boolean mNoAmbient;
    private final NotificationSwipeHelper.NotificationCallback mNotificationCallback;
    private final NotificationGutsManager mNotificationGutsManager;
    private NotificationPanelView mNotificationPanel;
    private OnEmptySpaceClickListener mOnEmptySpaceClickListener;
    private final NotificationGroupManager.OnGroupChangeListener mOnGroupChangeListener;
    private ExpandableView.OnHeightChangedListener mOnHeightChangedListener;
    private boolean mOnlyScrollingInThisMotion;
    private final ViewOutlineProvider mOutlineProvider;
    private float mOverScrolledBottomPixels;
    private float mOverScrolledTopPixels;
    private int mOverflingDistance;
    private OnOverscrollTopChangedListener mOverscrollTopChangedListener;
    private int mOwnScrollY;
    private int mPaddingBetweenElements;
    private boolean mPanelTracking;
    private boolean mPulsing;
    protected ViewGroup mQsContainer;
    private boolean mQsExpanded;
    private float mQsExpansionFraction;
    private Runnable mReclamp;
    private Runnable mReflingAndAnimateScroll;
    private final NotificationRemoteInputManager mRemoteInputManager;
    private Rect mRequestedClipBounds;
    private final NotificationRoundnessManager mRoundnessManager;
    private ViewTreeObserver.OnPreDrawListener mRunningAnimationUpdater;
    private ScrimController mScrimController;
    private View mScrollAnchorView;
    private int mScrollAnchorViewY;
    private boolean mScrollable;
    private boolean mScrolledToTopOnFirstDown;
    private OverScroller mScroller;
    protected boolean mScrollingEnabled;
    private NotificationSection[] mSections;
    private final NotificationSectionsManager mSectionsManager;
    private final ShadeController mShadeController;
    private ViewTreeObserver.OnPreDrawListener mShadowUpdater;
    private NotificationShelf mShelf;
    private final boolean mShouldDrawNotificationBackground;
    private boolean mShouldShowShelfOnly;
    private int mSidePaddings;
    private PorterDuffXfermode mSrcMode;
    protected final StackScrollAlgorithm mStackScrollAlgorithm;
    private float mStackTranslation;
    private final StackStateAnimator mStateAnimator;
    private final StatusBarStateController.StateListener mStateListener;
    private StatusBar mStatusBar;
    private int mStatusBarHeight;
    private int mStatusBarState;
    private final SysuiStatusBarStateController mStatusbarStateController;
    private final NotificationSwipeHelper mSwipeHelper;
    private ArrayList<View> mSwipedOutViews;
    private boolean mSwipingInProgress;
    private int[] mTempInt2;
    private final ArrayList<Pair<ExpandableNotificationRow, Boolean>> mTmpList;
    private final Rect mTmpRect;
    private ArrayList<ExpandableView> mTmpSortedChildren;
    private int mTopPadding;
    private boolean mTopPaddingNeedsAnimation;
    private float mTopPaddingOverflow;
    private boolean mTouchIsClick;
    private int mTouchSlop;
    private boolean mTrackingHeadsUp;
    private boolean mUsingLightTheme;
    private VelocityTracker mVelocityTracker;
    private Comparator<ExpandableView> mViewPositionComparator;
    private final VisualStabilityManager mVisualStabilityManager;
    private boolean mWillExpand;

    /* loaded from: classes21.dex */
    public interface OnEmptySpaceClickListener {
        void onEmptySpaceClicked(float f, float f2);
    }

    /* loaded from: classes21.dex */
    public interface OnOverscrollTopChangedListener {
        void flingTopOverscroll(float f, boolean z);

        void onOverscrollTopChanged(float f, boolean z);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes21.dex */
    public @interface SelectedRows {
    }

    public /* synthetic */ boolean lambda$new$0$NotificationStackScrollLayout() {
        updateBackground();
        return true;
    }

    @Inject
    public NotificationStackScrollLayout(@Named("view_context") Context context, AttributeSet attrs, @Named("allow_notif_longpress") boolean allowLongPress, NotificationRoundnessManager notificationRoundnessManager, DynamicPrivacyController dynamicPrivacyController, ConfigurationController configurationController, ActivityStarter activityStarter, StatusBarStateController statusBarStateController, HeadsUpManagerPhone headsUpManager, KeyguardBypassController keyguardBypassController, FalsingManager falsingManager) {
        super(context, attrs, 0, 0);
        this.mCurrentStackHeight = Integer.MAX_VALUE;
        this.mBackgroundPaint = new Paint();
        this.mActivePointerId = -1;
        this.mBottomInset = 0;
        this.mChildrenToAddAnimated = new HashSet<>();
        this.mAddedHeadsUpChildren = new ArrayList<>();
        this.mChildrenToRemoveAnimated = new ArrayList<>();
        this.mChildrenChangingPositions = new ArrayList<>();
        this.mFromMoreCardAdditions = new HashSet<>();
        this.mAnimationEvents = new ArrayList<>();
        this.mSwipedOutViews = new ArrayList<>();
        this.mStateAnimator = new StackStateAnimator(this);
        this.mIsExpanded = true;
        this.mChildrenUpdater = new ViewTreeObserver.OnPreDrawListener() { // from class: com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout.1
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                NotificationStackScrollLayout.this.updateForcedScroll();
                NotificationStackScrollLayout.this.updateChildren();
                NotificationStackScrollLayout.this.mChildrenUpdateRequested = false;
                NotificationStackScrollLayout.this.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        };
        this.mTempInt2 = new int[2];
        this.mAnimationFinishedRunnables = new HashSet<>();
        this.mClearTransientViewsWhenFinished = new HashSet<>();
        this.mHeadsUpChangeAnimations = new HashSet<>();
        this.mTmpList = new ArrayList<>();
        this.mRunningAnimationUpdater = new ViewTreeObserver.OnPreDrawListener() { // from class: com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout.2
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                NotificationStackScrollLayout.this.onPreDrawDuringAnimation();
                return true;
            }
        };
        this.mSections = new NotificationSection[2];
        this.mTmpSortedChildren = new ArrayList<>();
        this.mDimEndListener = new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                NotificationStackScrollLayout.this.mDimAnimator = null;
            }
        };
        this.mDimUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout.4
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                NotificationStackScrollLayout.this.setDimAmount(((Float) animation.getAnimatedValue()).floatValue());
            }
        };
        this.mShadowUpdater = new ViewTreeObserver.OnPreDrawListener() { // from class: com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout.5
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                NotificationStackScrollLayout.this.updateViewShadows();
                return true;
            }
        };
        this.mBackgroundUpdater = new ViewTreeObserver.OnPreDrawListener() { // from class: com.android.systemui.statusbar.notification.stack.-$$Lambda$NotificationStackScrollLayout$Q8bA-VckgKDEBbXIsfAy3cWAYiM
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public final boolean onPreDraw() {
                return NotificationStackScrollLayout.this.lambda$new$0$NotificationStackScrollLayout();
            }
        };
        this.mViewPositionComparator = new Comparator<ExpandableView>() { // from class: com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout.6
            @Override // java.util.Comparator
            public int compare(ExpandableView view, ExpandableView otherView) {
                float endY = view.getTranslationY() + view.getActualHeight();
                float otherEndY = otherView.getTranslationY() + otherView.getActualHeight();
                if (endY < otherEndY) {
                    return -1;
                }
                if (endY > otherEndY) {
                    return 1;
                }
                return 0;
            }
        };
        this.mOutlineProvider = new ViewOutlineProvider() { // from class: com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout.7
            @Override // android.view.ViewOutlineProvider
            public void getOutline(View view, Outline outline) {
                if (NotificationStackScrollLayout.this.mAmbientState.isHiddenAtAll()) {
                    float xProgress = NotificationStackScrollLayout.this.mHideXInterpolator.getInterpolation((1.0f - NotificationStackScrollLayout.this.mLinearHideAmount) * NotificationStackScrollLayout.this.mBackgroundXFactor);
                    outline.setRoundRect(NotificationStackScrollLayout.this.mBackgroundAnimationRect, MathUtils.lerp(NotificationStackScrollLayout.this.mCornerRadius / 2.0f, NotificationStackScrollLayout.this.mCornerRadius, xProgress));
                    outline.setAlpha(1.0f - NotificationStackScrollLayout.this.mAmbientState.getHideAmount());
                    return;
                }
                ViewOutlineProvider.BACKGROUND.getOutline(view, outline);
            }
        };
        this.mSrcMode = new PorterDuffXfermode(PorterDuff.Mode.SRC);
        this.mInterpolatedHideAmount = 0.0f;
        this.mLinearHideAmount = 0.0f;
        this.mBackgroundXFactor = 1.0f;
        this.mMaxDisplayedNotifications = -1;
        this.mClipRect = new Rect();
        this.mHeadsUpGoingAwayAnimationsAllowed = true;
        this.mReflingAndAnimateScroll = new Runnable() { // from class: com.android.systemui.statusbar.notification.stack.-$$Lambda$NotificationStackScrollLayout$Dpz6Zg1EwqGyFLQ68KdTUD2Xa-g
            @Override // java.lang.Runnable
            public final void run() {
                NotificationStackScrollLayout.this.lambda$new$1$NotificationStackScrollLayout();
            }
        };
        this.mBackgroundAnimationRect = new Rect();
        this.mExpandedHeightListeners = new ArrayList<>();
        this.mLockscreenUserManager = (NotificationLockscreenUserManager) Dependency.get(NotificationLockscreenUserManager.class);
        this.mTmpRect = new Rect();
        this.mEntryManager = (NotificationEntryManager) Dependency.get(NotificationEntryManager.class);
        this.mBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
        this.mMetricsLogger = (MetricsLogger) Dependency.get(MetricsLogger.class);
        this.mRemoteInputManager = (NotificationRemoteInputManager) Dependency.get(NotificationRemoteInputManager.class);
        this.mColorExtractor = (SysuiColorExtractor) Dependency.get(SysuiColorExtractor.class);
        this.mDisplayMetrics = (DisplayMetrics) Dependency.get(DisplayMetrics.class);
        this.mLockscreenGestureLogger = (LockscreenGestureLogger) Dependency.get(LockscreenGestureLogger.class);
        this.mVisualStabilityManager = (VisualStabilityManager) Dependency.get(VisualStabilityManager.class);
        this.mHideXInterpolator = Interpolators.FAST_OUT_SLOW_IN;
        this.mShadeController = (ShadeController) Dependency.get(ShadeController.class);
        this.mNotificationGutsManager = (NotificationGutsManager) Dependency.get(NotificationGutsManager.class);
        this.mReclamp = new Runnable() { // from class: com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout.10
            @Override // java.lang.Runnable
            public void run() {
                int range = NotificationStackScrollLayout.this.getScrollRange();
                NotificationStackScrollLayout.this.mScroller.startScroll(NotificationStackScrollLayout.this.mScrollX, NotificationStackScrollLayout.this.mOwnScrollY, 0, range - NotificationStackScrollLayout.this.mOwnScrollY);
                NotificationStackScrollLayout.this.mDontReportNextOverScroll = true;
                NotificationStackScrollLayout.this.mDontClampNextScroll = true;
                NotificationStackScrollLayout.this.lambda$new$1$NotificationStackScrollLayout();
            }
        };
        this.mStateListener = new StatusBarStateController.StateListener() { // from class: com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout.11
            @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
            public void onStatePreChange(int oldState, int newState) {
                if (oldState == 2 && newState == 1) {
                    NotificationStackScrollLayout.this.requestAnimateEverything();
                }
            }

            @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
            public void onStateChanged(int newState) {
                NotificationStackScrollLayout.this.setStatusBarState(newState);
            }

            @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
            public void onStatePostChange() {
                NotificationStackScrollLayout.this.onStatePostChange();
            }
        };
        this.mMenuEventListener = new NotificationMenuRowPlugin.OnMenuEventListener() { // from class: com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout.12
            @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin.OnMenuEventListener
            public void onMenuClicked(View view, int x, int y, NotificationMenuRowPlugin.MenuItem item) {
                if (NotificationStackScrollLayout.this.mLongPressListener == null) {
                    return;
                }
                if (view instanceof ExpandableNotificationRow) {
                    ExpandableNotificationRow row = (ExpandableNotificationRow) view;
                    NotificationStackScrollLayout.this.mMetricsLogger.write(row.getStatusBarNotification().getLogMaker().setCategory(333).setType(4));
                }
                NotificationStackScrollLayout.this.mLongPressListener.onLongPress(view, x, y, item);
            }

            @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin.OnMenuEventListener
            public void onMenuReset(View row) {
                View translatingParentView = NotificationStackScrollLayout.this.mSwipeHelper.getTranslatingParentView();
                if (translatingParentView != null && row == translatingParentView) {
                    NotificationStackScrollLayout.this.mSwipeHelper.clearExposedMenuView();
                    NotificationStackScrollLayout.this.mSwipeHelper.clearTranslatingParentView();
                    if (row instanceof ExpandableNotificationRow) {
                        NotificationStackScrollLayout.this.mHeadsUpManager.setMenuShown(((ExpandableNotificationRow) row).getEntry(), false);
                    }
                }
            }

            @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin.OnMenuEventListener
            public void onMenuShown(View row) {
                if (row instanceof ExpandableNotificationRow) {
                    ExpandableNotificationRow notificationRow = (ExpandableNotificationRow) row;
                    NotificationStackScrollLayout.this.mMetricsLogger.write(notificationRow.getStatusBarNotification().getLogMaker().setCategory(332).setType(4));
                    NotificationStackScrollLayout.this.mHeadsUpManager.setMenuShown(notificationRow.getEntry(), true);
                    NotificationStackScrollLayout.this.mSwipeHelper.onMenuShown(row);
                    NotificationStackScrollLayout.this.mNotificationGutsManager.closeAndSaveGuts(true, false, false, -1, -1, false);
                    NotificationMenuRowPlugin provider = notificationRow.getProvider();
                    if (provider.shouldShowGutsOnSnapOpen()) {
                        NotificationMenuRowPlugin.MenuItem item = provider.menuItemToExposeOnSnap();
                        if (item != null) {
                            Point origin = provider.getRevealAnimationOrigin();
                            NotificationStackScrollLayout.this.mNotificationGutsManager.openGuts(row, origin.x, origin.y, item);
                        } else {
                            Log.e(NotificationStackScrollLayout.TAG, "Provider has shouldShowGutsOnSnapOpen, but provided no menu item in menuItemtoExposeOnSnap. Skipping.");
                        }
                        NotificationStackScrollLayout.this.resetExposedMenuView(false, true);
                    }
                }
            }
        };
        this.mNotificationCallback = new NotificationSwipeHelper.NotificationCallback() { // from class: com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout.13
            @Override // com.android.systemui.statusbar.notification.stack.NotificationSwipeHelper.NotificationCallback
            public void onDismiss() {
                NotificationStackScrollLayout.this.mNotificationGutsManager.closeAndSaveGuts(true, false, false, -1, -1, false);
            }

            @Override // com.android.systemui.statusbar.notification.stack.NotificationSwipeHelper.NotificationCallback
            public void onSnooze(StatusBarNotification sbn, NotificationSwipeActionHelper.SnoozeOption snoozeOption) {
                NotificationStackScrollLayout.this.mStatusBar.setNotificationSnoozed(sbn, snoozeOption);
            }

            @Override // com.android.systemui.statusbar.notification.stack.NotificationSwipeHelper.NotificationCallback
            public boolean shouldDismissQuickly() {
                return NotificationStackScrollLayout.this.isExpanded() && NotificationStackScrollLayout.this.mAmbientState.isFullyAwake();
            }

            @Override // com.android.systemui.SwipeHelper.Callback
            public void onDragCancelled(View v) {
                NotificationStackScrollLayout.this.setSwipingInProgress(false);
                NotificationStackScrollLayout.this.mFalsingManager.onNotificatonStopDismissing();
            }

            @Override // com.android.systemui.SwipeHelper.Callback
            public void onChildDismissed(View view) {
                ExpandableNotificationRow row = (ExpandableNotificationRow) view;
                if (!row.isDismissed()) {
                    handleChildViewDismissed(view);
                }
                ViewGroup transientContainer = row.getTransientContainer();
                if (transientContainer != null) {
                    transientContainer.removeTransientView(view);
                }
            }

            @Override // com.android.systemui.statusbar.notification.stack.NotificationSwipeHelper.NotificationCallback
            public void handleChildViewDismissed(View view) {
                NotificationStackScrollLayout.this.setSwipingInProgress(false);
                if (NotificationStackScrollLayout.this.mDismissAllInProgress) {
                    return;
                }
                boolean isBlockingHelperShown = false;
                NotificationStackScrollLayout.this.mAmbientState.onDragFinished(view);
                NotificationStackScrollLayout.this.updateContinuousShadowDrawing();
                if (view instanceof ExpandableNotificationRow) {
                    ExpandableNotificationRow row = (ExpandableNotificationRow) view;
                    if (row.isHeadsUp()) {
                        NotificationStackScrollLayout.this.mHeadsUpManager.addSwipedOutNotification(row.getStatusBarNotification().getKey());
                    }
                    isBlockingHelperShown = row.performDismissWithBlockingHelper(false);
                }
                if (!isBlockingHelperShown) {
                    NotificationStackScrollLayout.this.mSwipedOutViews.add(view);
                }
                NotificationStackScrollLayout.this.mFalsingManager.onNotificationDismissed();
                if (NotificationStackScrollLayout.this.mFalsingManager.shouldEnforceBouncer()) {
                    NotificationStackScrollLayout.this.mStatusBar.executeRunnableDismissingKeyguard(null, null, false, true, false);
                }
            }

            @Override // com.android.systemui.SwipeHelper.Callback
            public boolean isAntiFalsingNeeded() {
                return NotificationStackScrollLayout.this.onKeyguard();
            }

            @Override // com.android.systemui.SwipeHelper.Callback
            public View getChildAtPosition(MotionEvent ev) {
                View child = NotificationStackScrollLayout.this.getChildAtPosition(ev.getX(), ev.getY());
                if (child instanceof ExpandableNotificationRow) {
                    ExpandableNotificationRow row = (ExpandableNotificationRow) child;
                    ExpandableNotificationRow parent = row.getNotificationParent();
                    if (parent != null && parent.areChildrenExpanded()) {
                        if (parent.areGutsExposed() || NotificationStackScrollLayout.this.mSwipeHelper.getExposedMenuView() == parent || (parent.getNotificationChildren().size() == 1 && parent.getEntry().isClearable())) {
                            return parent;
                        }
                        return child;
                    }
                    return child;
                }
                return child;
            }

            @Override // com.android.systemui.SwipeHelper.Callback
            public void onBeginDrag(View v) {
                NotificationStackScrollLayout.this.mFalsingManager.onNotificatonStartDismissing();
                NotificationStackScrollLayout.this.setSwipingInProgress(true);
                NotificationStackScrollLayout.this.mAmbientState.onBeginDrag((ExpandableView) v);
                NotificationStackScrollLayout.this.updateContinuousShadowDrawing();
                NotificationStackScrollLayout.this.updateContinuousBackgroundDrawing();
                NotificationStackScrollLayout.this.requestChildrenUpdate();
            }

            @Override // com.android.systemui.SwipeHelper.Callback
            public void onChildSnappedBack(View animView, float targetLeft) {
                NotificationStackScrollLayout.this.mAmbientState.onDragFinished(animView);
                NotificationStackScrollLayout.this.updateContinuousShadowDrawing();
                NotificationStackScrollLayout.this.updateContinuousBackgroundDrawing();
                if (animView instanceof ExpandableNotificationRow) {
                    ExpandableNotificationRow row = (ExpandableNotificationRow) animView;
                    if (row.isPinned() && !canChildBeDismissed(row) && row.getStatusBarNotification().getNotification().fullScreenIntent == null) {
                        NotificationStackScrollLayout.this.mHeadsUpManager.removeNotification(row.getStatusBarNotification().getKey(), true);
                    }
                }
            }

            @Override // com.android.systemui.SwipeHelper.Callback
            public boolean updateSwipeProgress(View animView, boolean dismissable, float swipeProgress) {
                return !NotificationStackScrollLayout.this.mFadeNotificationsOnDismiss;
            }

            @Override // com.android.systemui.SwipeHelper.Callback
            public float getFalsingThresholdFactor() {
                return NotificationStackScrollLayout.this.mStatusBar.isWakeUpComingFromTouch() ? 1.5f : 1.0f;
            }

            @Override // com.android.systemui.SwipeHelper.Callback
            public int getConstrainSwipeStartPosition() {
                NotificationMenuRowPlugin menuRow = NotificationStackScrollLayout.this.mSwipeHelper.getCurrentMenuRow();
                if (menuRow != null) {
                    return Math.abs(menuRow.getMenuSnapTarget());
                }
                return 0;
            }

            @Override // com.android.systemui.SwipeHelper.Callback
            public boolean canChildBeDismissed(View v) {
                return StackScrollAlgorithm.canChildBeDismissed(v);
            }

            @Override // com.android.systemui.SwipeHelper.Callback
            public boolean canChildBeDismissedInDirection(View v, boolean isRightOrDown) {
                return canChildBeDismissed(v);
            }
        };
        this.mDragDownCallback = new AnonymousClass14();
        this.mHeadsUpCallback = new HeadsUpTouchHelper.Callback() { // from class: com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout.15
            @Override // com.android.systemui.statusbar.phone.HeadsUpTouchHelper.Callback
            public ExpandableView getChildAtRawPosition(float touchX, float touchY) {
                return NotificationStackScrollLayout.this.getChildAtRawPosition(touchX, touchY);
            }

            @Override // com.android.systemui.statusbar.phone.HeadsUpTouchHelper.Callback
            public boolean isExpanded() {
                return NotificationStackScrollLayout.this.mIsExpanded;
            }

            @Override // com.android.systemui.statusbar.phone.HeadsUpTouchHelper.Callback
            public Context getContext() {
                return NotificationStackScrollLayout.this.mContext;
            }
        };
        this.mOnGroupChangeListener = new NotificationGroupManager.OnGroupChangeListener() { // from class: com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout.16
            @Override // com.android.systemui.statusbar.phone.NotificationGroupManager.OnGroupChangeListener
            public void onGroupExpansionChanged(final ExpandableNotificationRow changedRow, boolean expanded) {
                boolean animated = !NotificationStackScrollLayout.this.mGroupExpandedForMeasure && NotificationStackScrollLayout.this.mAnimationsEnabled && (NotificationStackScrollLayout.this.mIsExpanded || changedRow.isPinned());
                if (animated) {
                    NotificationStackScrollLayout.this.mExpandedGroupView = changedRow;
                    NotificationStackScrollLayout.this.mNeedsAnimation = true;
                }
                changedRow.setChildrenExpanded(expanded, animated);
                if (!NotificationStackScrollLayout.this.mGroupExpandedForMeasure) {
                    NotificationStackScrollLayout.this.onHeightChanged(changedRow, false);
                }
                NotificationStackScrollLayout.this.runAfterAnimationFinished(new Runnable() { // from class: com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout.16.1
                    @Override // java.lang.Runnable
                    public void run() {
                        changedRow.onFinishedExpansionChange();
                    }
                });
            }

            @Override // com.android.systemui.statusbar.phone.NotificationGroupManager.OnGroupChangeListener
            public void onGroupCreatedFromChildren(NotificationGroupManager.NotificationGroup group) {
                NotificationStackScrollLayout.this.mStatusBar.requestNotificationUpdate();
            }

            @Override // com.android.systemui.statusbar.phone.NotificationGroupManager.OnGroupChangeListener
            public void onGroupsChanged() {
                NotificationStackScrollLayout.this.mStatusBar.requestNotificationUpdate();
            }
        };
        this.mExpandHelperCallback = new ExpandHelper.Callback() { // from class: com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout.17
            @Override // com.android.systemui.ExpandHelper.Callback
            public ExpandableView getChildAtPosition(float touchX, float touchY) {
                return NotificationStackScrollLayout.this.getChildAtPosition(touchX, touchY);
            }

            @Override // com.android.systemui.ExpandHelper.Callback
            public ExpandableView getChildAtRawPosition(float touchX, float touchY) {
                return NotificationStackScrollLayout.this.getChildAtRawPosition(touchX, touchY);
            }

            @Override // com.android.systemui.ExpandHelper.Callback
            public boolean canChildBeExpanded(View v) {
                return (v instanceof ExpandableNotificationRow) && ((ExpandableNotificationRow) v).isExpandable() && !((ExpandableNotificationRow) v).areGutsExposed() && (NotificationStackScrollLayout.this.mIsExpanded || !((ExpandableNotificationRow) v).isPinned());
            }

            @Override // com.android.systemui.ExpandHelper.Callback
            public void setUserExpandedChild(View v, boolean userExpanded) {
                if (v instanceof ExpandableNotificationRow) {
                    ExpandableNotificationRow row = (ExpandableNotificationRow) v;
                    if (userExpanded && NotificationStackScrollLayout.this.onKeyguard()) {
                        row.setUserLocked(false);
                        NotificationStackScrollLayout.this.updateContentHeight();
                        NotificationStackScrollLayout.this.notifyHeightChangeListener(row);
                        return;
                    }
                    row.setUserExpanded(userExpanded, true);
                    row.onExpandedByGesture(userExpanded);
                }
            }

            @Override // com.android.systemui.ExpandHelper.Callback
            public void setExpansionCancelled(View v) {
                if (v instanceof ExpandableNotificationRow) {
                    ((ExpandableNotificationRow) v).setGroupExpansionChanging(false);
                }
            }

            @Override // com.android.systemui.ExpandHelper.Callback
            public void setUserLockedChild(View v, boolean userLocked) {
                if (v instanceof ExpandableNotificationRow) {
                    ((ExpandableNotificationRow) v).setUserLocked(userLocked);
                }
                NotificationStackScrollLayout.this.cancelLongPress();
                NotificationStackScrollLayout.this.requestDisallowInterceptTouchEvent(true);
            }

            @Override // com.android.systemui.ExpandHelper.Callback
            public void expansionStateChanged(boolean isExpanding) {
                NotificationStackScrollLayout.this.mExpandingNotification = isExpanding;
                if (!NotificationStackScrollLayout.this.mExpandedInThisMotion) {
                    NotificationStackScrollLayout notificationStackScrollLayout = NotificationStackScrollLayout.this;
                    notificationStackScrollLayout.mMaxScrollAfterExpand = notificationStackScrollLayout.mOwnScrollY;
                    NotificationStackScrollLayout.this.mExpandedInThisMotion = true;
                }
            }

            @Override // com.android.systemui.ExpandHelper.Callback
            public int getMaxExpandHeight(ExpandableView view) {
                return view.getMaxContentHeight();
            }
        };
        Resources res = getResources();
        this.mAllowLongPress = allowLongPress;
        for (int i = 0; i < 2; i++) {
            this.mSections[i] = new NotificationSection(this);
        }
        this.mRoundnessManager = notificationRoundnessManager;
        this.mHeadsUpManager = headsUpManager;
        this.mHeadsUpManager.addListener(this.mRoundnessManager);
        this.mHeadsUpManager.setAnimationStateHandler(new HeadsUpManagerPhone.AnimationStateHandler() { // from class: com.android.systemui.statusbar.notification.stack.-$$Lambda$2kmwH5TzrEUhlI4yYwStAmSu1DU
            @Override // com.android.systemui.statusbar.phone.HeadsUpManagerPhone.AnimationStateHandler
            public final void setHeadsUpGoingAwayAnimationsAllowed(boolean z) {
                NotificationStackScrollLayout.this.setHeadsUpGoingAwayAnimationsAllowed(z);
            }
        });
        this.mKeyguardBypassController = keyguardBypassController;
        this.mFalsingManager = falsingManager;
        this.mSectionsManager = new NotificationSectionsManager(this, activityStarter, statusBarStateController, configurationController, NotificationUtils.useNewInterruptionModel(context));
        this.mSectionsManager.initialize(LayoutInflater.from(context));
        this.mSectionsManager.setOnClearGentleNotifsClickListener(new View.OnClickListener() { // from class: com.android.systemui.statusbar.notification.stack.-$$Lambda$NotificationStackScrollLayout$FSzmDEARpk_ltemkfReRVTEnBdg
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                NotificationStackScrollLayout.this.lambda$new$2$NotificationStackScrollLayout(view);
            }
        });
        this.mAmbientState = new AmbientState(context, this.mSectionsManager, this.mHeadsUpManager);
        this.mBgColor = context.getColor(R.color.notification_shade_background_color);
        int minHeight = res.getDimensionPixelSize(R.dimen.notification_min_height);
        int maxHeight = res.getDimensionPixelSize(R.dimen.notification_max_height);
        this.mExpandHelper = new ExpandHelper(getContext(), this.mExpandHelperCallback, minHeight, maxHeight);
        this.mExpandHelper.setEventSource(this);
        this.mExpandHelper.setScrollAdapter(this);
        this.mSwipeHelper = new NotificationSwipeHelper(0, this.mNotificationCallback, getContext(), this.mMenuEventListener, this.mFalsingManager);
        this.mStackScrollAlgorithm = createStackScrollAlgorithm(context);
        initView(context);
        this.mShouldDrawNotificationBackground = res.getBoolean(R.bool.config_drawNotificationBackground);
        this.mFadeNotificationsOnDismiss = res.getBoolean(R.bool.config_fadeNotificationsOnDismiss);
        this.mRoundnessManager.setAnimatedChildren(this.mChildrenToAddAnimated);
        this.mRoundnessManager.setOnRoundingChangedCallback(new Runnable() { // from class: com.android.systemui.statusbar.notification.stack.-$$Lambda$ZNzbjhiYOpIhFG8SoCZYGISAg68
            @Override // java.lang.Runnable
            public final void run() {
                NotificationStackScrollLayout.this.invalidate();
            }
        });
        final NotificationRoundnessManager notificationRoundnessManager2 = this.mRoundnessManager;
        Objects.requireNonNull(notificationRoundnessManager2);
        addOnExpandedHeightChangedListener(new BiConsumer() { // from class: com.android.systemui.statusbar.notification.stack.-$$Lambda$7_f8XxLoO1HD4OWprUeIqEzesjU
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                NotificationRoundnessManager.this.setExpanded(((Float) obj).floatValue(), ((Float) obj2).floatValue());
            }
        });
        this.mLockscreenUserManager.addUserChangedListener(new NotificationLockscreenUserManager.UserChangedListener() { // from class: com.android.systemui.statusbar.notification.stack.-$$Lambda$NotificationStackScrollLayout$h47I7Qi44nR3_482BY7RSDFFr-0
            @Override // com.android.systemui.statusbar.NotificationLockscreenUserManager.UserChangedListener
            public final void onUserChanged(int i2) {
                NotificationStackScrollLayout.this.lambda$new$3$NotificationStackScrollLayout(i2);
            }
        });
        setOutlineProvider(this.mOutlineProvider);
        final NotificationBlockingHelperManager blockingHelperManager = (NotificationBlockingHelperManager) Dependency.get(NotificationBlockingHelperManager.class);
        addOnExpandedHeightChangedListener(new BiConsumer() { // from class: com.android.systemui.statusbar.notification.stack.-$$Lambda$NotificationStackScrollLayout$EOvrpynV_4_HqkQZPqElzpbHsN4
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                Float f = (Float) obj2;
                NotificationBlockingHelperManager.this.setNotificationShadeExpanded(((Float) obj).floatValue());
            }
        });
        boolean willDraw = this.mShouldDrawNotificationBackground;
        setWillNotDraw(!willDraw);
        this.mBackgroundPaint.setAntiAlias(true);
        this.mClearAllEnabled = res.getBoolean(R.bool.config_enableNotificationsClearAll);
        TunerService tunerService = (TunerService) Dependency.get(TunerService.class);
        tunerService.addTunable(new TunerService.Tunable() { // from class: com.android.systemui.statusbar.notification.stack.-$$Lambda$NotificationStackScrollLayout$Jw0uVZk_QqBt9QukDWfY9zQ7BQU
            @Override // com.android.systemui.tuner.TunerService.Tunable
            public final void onTuningChanged(String str, String str2) {
                NotificationStackScrollLayout.this.lambda$new$5$NotificationStackScrollLayout(str, str2);
            }
        }, NotificationIconAreaController.HIGH_PRIORITY, "notification_dismiss_rtl");
        this.mEntryManager.addNotificationEntryListener(new NotificationEntryListener() { // from class: com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout.8
            @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
            public void onPostEntryUpdated(NotificationEntry entry) {
                if (!entry.notification.isClearable()) {
                    NotificationStackScrollLayout.this.snapViewIfNeeded(entry);
                }
            }
        });
        dynamicPrivacyController.addListener(this);
        this.mDynamicPrivacyController = dynamicPrivacyController;
        this.mStatusbarStateController = (SysuiStatusBarStateController) statusBarStateController;
    }

    public /* synthetic */ void lambda$new$2$NotificationStackScrollLayout(View v) {
        boolean closeShade = true ^ hasActiveClearableNotifications(1);
        clearNotifications(2, closeShade);
    }

    public /* synthetic */ void lambda$new$3$NotificationStackScrollLayout(int userId) {
        updateSensitiveness(false);
    }

    public /* synthetic */ void lambda$new$5$NotificationStackScrollLayout(String key, String newValue) {
        if (key.equals(NotificationIconAreaController.HIGH_PRIORITY)) {
            this.mHighPriorityBeforeSpeedBump = "1".equals(newValue);
        } else if (key.equals("notification_dismiss_rtl")) {
            updateDismissRtlSetting("1".equals(newValue));
        }
    }

    private void updateDismissRtlSetting(boolean dismissRtl) {
        this.mDismissRtl = dismissRtl;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof ExpandableNotificationRow) {
                ((ExpandableNotificationRow) child).setDismissRtl(dismissRtl);
            }
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        inflateEmptyShadeView();
        inflateFooterView();
        this.mVisualStabilityManager.setVisibilityLocationProvider(new VisibilityLocationProvider() { // from class: com.android.systemui.statusbar.notification.stack.-$$Lambda$U5xT0qKII52vil_DFEsN5YX5CE0
            @Override // com.android.systemui.statusbar.notification.VisibilityLocationProvider
            public final boolean isInVisibleLocation(NotificationEntry notificationEntry) {
                return NotificationStackScrollLayout.this.isInVisibleLocation(notificationEntry);
            }
        });
        if (this.mAllowLongPress) {
            final NotificationGutsManager notificationGutsManager = this.mNotificationGutsManager;
            Objects.requireNonNull(notificationGutsManager);
            setLongPressListener(new ExpandableNotificationRow.LongPressListener() { // from class: com.android.systemui.statusbar.notification.stack.-$$Lambda$0lGYUT66Z7cr4TZs4rdZ8M7DQkw
                @Override // com.android.systemui.statusbar.notification.row.ExpandableNotificationRow.LongPressListener
                public final boolean onLongPress(View view, int i, int i2, NotificationMenuRowPlugin.MenuItem menuItem) {
                    return NotificationGutsManager.this.openGuts(view, i, i2, menuItem);
                }
            });
        }
    }

    public float getWakeUpHeight() {
        ActivatableNotificationView firstChild = getFirstChildWithBackground();
        if (firstChild != null) {
            if (this.mKeyguardBypassController.getBypassEnabled()) {
                return firstChild.getHeadsUpHeightWithoutHeader();
            }
            return firstChild.getCollapsedHeight();
        }
        return 0.0f;
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onDensityOrFontScaleChanged() {
        reinflateViews();
    }

    private void reinflateViews() {
        inflateFooterView();
        inflateEmptyShadeView();
        updateFooter();
        this.mSectionsManager.reinflateViews(LayoutInflater.from(this.mContext));
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onThemeChanged() {
        boolean useDarkText = this.mColorExtractor.getNeutralColors().supportsDarkText();
        updateDecorViews(useDarkText);
        updateFooter();
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onOverlayChanged() {
        int newRadius = this.mContext.getResources().getDimensionPixelSize(Utils.getThemeAttr(this.mContext, 16844145));
        if (this.mCornerRadius != newRadius) {
            this.mCornerRadius = newRadius;
            invalidate();
        }
        reinflateViews();
    }

    @VisibleForTesting
    public void updateFooter() {
        boolean showDismissView = this.mClearAllEnabled && hasActiveClearableNotifications(0);
        boolean showFooterView = ((!showDismissView && this.mEntryManager.getNotificationData().getActiveNotifications().size() == 0) || this.mStatusBarState == 1 || this.mRemoteInputManager.getController().isRemoteInputActive()) ? false : true;
        updateFooterView(showFooterView, showDismissView);
    }

    public boolean hasActiveClearableNotifications(int selection) {
        if (this.mDynamicPrivacyController.isInLockedDownShade()) {
            return false;
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow row = (ExpandableNotificationRow) child;
                if (row.canViewBeDismissed() && matchesSelection(row, selection)) {
                    return true;
                }
            }
        }
        return false;
    }

    public RemoteInputController.Delegate createDelegate() {
        return new RemoteInputController.Delegate() { // from class: com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout.9
            @Override // com.android.systemui.statusbar.RemoteInputController.Delegate
            public void setRemoteInputActive(NotificationEntry entry, boolean remoteInputActive) {
                NotificationStackScrollLayout.this.mHeadsUpManager.setRemoteInputActive(entry, remoteInputActive);
                entry.notifyHeightChanged(true);
                NotificationStackScrollLayout.this.updateFooter();
            }

            @Override // com.android.systemui.statusbar.RemoteInputController.Delegate
            public void lockScrollTo(NotificationEntry entry) {
                NotificationStackScrollLayout.this.lockScrollTo(entry.getRow());
            }

            @Override // com.android.systemui.statusbar.RemoteInputController.Delegate
            public void requestDisallowLongPressAndDismiss() {
                NotificationStackScrollLayout.this.requestDisallowLongPress();
                NotificationStackScrollLayout.this.requestDisallowDismiss();
            }
        };
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ((SysuiStatusBarStateController) Dependency.get(StatusBarStateController.class)).addCallback(this.mStateListener, 2);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ((StatusBarStateController) Dependency.get(StatusBarStateController.class)).removeCallback(this.mStateListener);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).removeCallback(this);
    }

    @Override // com.android.systemui.statusbar.notification.stack.NotificationListContainer
    public NotificationSwipeActionHelper getSwipeActionHelper() {
        return this.mSwipeHelper;
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onUiModeChanged() {
        this.mBgColor = this.mContext.getColor(R.color.notification_shade_background_color);
        updateBackgroundDimming();
        this.mShelf.onUiModeChanged();
        this.mSectionsManager.onUiModeChanged();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.mShouldDrawNotificationBackground && (this.mSections[0].getCurrentBounds().top < this.mSections[1].getCurrentBounds().bottom || this.mAmbientState.isDozing())) {
            drawBackground(canvas);
        } else if (this.mInHeadsUpPinnedMode || this.mHeadsUpAnimatingAway) {
            drawHeadsUpBackground(canvas);
        }
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    private void drawBackground(Canvas canvas) {
        boolean shouldDrawBackground;
        int lockScreenLeft = this.mSidePaddings;
        int lockScreenRight = getWidth() - this.mSidePaddings;
        int lockScreenTop = this.mSections[0].getCurrentBounds().top;
        int lockScreenBottom = this.mSections[1].getCurrentBounds().bottom;
        int hiddenLeft = getWidth() / 2;
        int hiddenTop = this.mTopPadding;
        float yProgress = 1.0f - this.mInterpolatedHideAmount;
        float xProgress = this.mHideXInterpolator.getInterpolation((1.0f - this.mLinearHideAmount) * this.mBackgroundXFactor);
        int left = (int) MathUtils.lerp(hiddenLeft, lockScreenLeft, xProgress);
        int right = (int) MathUtils.lerp(hiddenLeft, lockScreenRight, xProgress);
        int top = (int) MathUtils.lerp(hiddenTop, lockScreenTop, yProgress);
        int bottom = (int) MathUtils.lerp(hiddenTop, lockScreenBottom, yProgress);
        this.mBackgroundAnimationRect.set(left, top, right, bottom);
        int backgroundTopAnimationOffset = top - lockScreenTop;
        NotificationSection[] notificationSectionArr = this.mSections;
        int length = notificationSectionArr.length;
        boolean anySectionHasVisibleChild = false;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            NotificationSection section = notificationSectionArr[i];
            if (section.getFirstVisibleChild() == null) {
                i++;
            } else {
                anySectionHasVisibleChild = true;
                break;
            }
        }
        if (this.mKeyguardBypassController.getBypassEnabled() && onKeyguard()) {
            shouldDrawBackground = isPulseExpanding();
        } else {
            shouldDrawBackground = !this.mAmbientState.isDozing() || anySectionHasVisibleChild;
        }
        if (shouldDrawBackground) {
            drawBackgroundRects(canvas, left, right, top, backgroundTopAnimationOffset);
        }
        updateClipping();
    }

    private void drawBackgroundRects(Canvas canvas, int left, int right, int top, int animationYOffset) {
        NotificationSection[] notificationSectionArr;
        int i;
        int i2 = right;
        int backgroundRectTop = top;
        int i3 = 0;
        int lastSectionBottom = this.mSections[0].getCurrentBounds().bottom + animationYOffset;
        int currentLeft = left;
        int currentRight = right;
        boolean first = true;
        NotificationSection[] notificationSectionArr2 = this.mSections;
        int length = notificationSectionArr2.length;
        while (i3 < length) {
            NotificationSection section = notificationSectionArr2[i3];
            if (section.getFirstVisibleChild() == null) {
                notificationSectionArr = notificationSectionArr2;
                i = length;
            } else {
                int sectionTop = section.getCurrentBounds().top + animationYOffset;
                int ownLeft = Math.min(Math.max(left, section.getCurrentBounds().left), i2);
                int ownRight = Math.max(Math.min(i2, section.getCurrentBounds().right), ownLeft);
                if (sectionTop - lastSectionBottom > 1 || !((currentLeft == ownLeft && currentRight == ownRight) || first)) {
                    notificationSectionArr = notificationSectionArr2;
                    i = length;
                    int i4 = this.mCornerRadius;
                    canvas.drawRoundRect(currentLeft, backgroundRectTop, currentRight, lastSectionBottom, i4, i4, this.mBackgroundPaint);
                    backgroundRectTop = sectionTop;
                } else {
                    notificationSectionArr = notificationSectionArr2;
                    i = length;
                }
                int lastSectionBottom2 = section.getCurrentBounds().bottom + animationYOffset;
                first = false;
                currentRight = ownRight;
                lastSectionBottom = lastSectionBottom2;
                currentLeft = ownLeft;
            }
            i3++;
            i2 = right;
            notificationSectionArr2 = notificationSectionArr;
            length = i;
        }
        int i5 = this.mCornerRadius;
        canvas.drawRoundRect(currentLeft, backgroundRectTop, currentRight, lastSectionBottom, i5, i5, this.mBackgroundPaint);
    }

    private void drawHeadsUpBackground(Canvas canvas) {
        int left = this.mSidePaddings;
        int right = getWidth() - this.mSidePaddings;
        float top = getHeight();
        float bottom = 0.0f;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8 && (child instanceof ExpandableNotificationRow)) {
                ExpandableNotificationRow row = (ExpandableNotificationRow) child;
                if ((row.isPinned() || row.isHeadsUpAnimatingAway()) && row.getTranslation() < 0.0f && row.getProvider().shouldShowGutsOnSnapOpen()) {
                    top = Math.min(top, row.getTranslationY());
                    bottom = Math.max(bottom, row.getTranslationY() + row.getActualHeight());
                }
            }
        }
        int i2 = (top > bottom ? 1 : (top == bottom ? 0 : -1));
        if (i2 < 0) {
            int i3 = this.mCornerRadius;
            canvas.drawRoundRect(left, top, right, bottom, i3, i3, this.mBackgroundPaint);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBackgroundDimming() {
        if (!this.mShouldDrawNotificationBackground) {
            return;
        }
        float colorInterpolation = MathUtils.smoothStep(0.4f, 1.0f, this.mLinearHideAmount);
        int color = ColorUtils.blendARGB(this.mBgColor, -1, colorInterpolation);
        if (this.mCachedBackgroundColor != color) {
            this.mCachedBackgroundColor = color;
            this.mBackgroundPaint.setColor(color);
            invalidate();
        }
    }

    private void initView(Context context) {
        this.mScroller = new OverScroller(getContext());
        setDescendantFocusability(262144);
        setClipChildren(false);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        this.mTouchSlop = configuration.getScaledTouchSlop();
        this.mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        this.mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        this.mOverflingDistance = configuration.getScaledOverflingDistance();
        Resources res = context.getResources();
        this.mCollapsedSize = res.getDimensionPixelSize(R.dimen.notification_min_height);
        this.mStackScrollAlgorithm.initView(context);
        this.mAmbientState.reload(context);
        this.mPaddingBetweenElements = Math.max(1, res.getDimensionPixelSize(R.dimen.notification_divider_height));
        this.mIncreasedPaddingBetweenElements = res.getDimensionPixelSize(R.dimen.notification_divider_height_increased);
        this.mMinTopOverScrollToEscape = res.getDimensionPixelSize(R.dimen.min_top_overscroll_to_qs);
        this.mStatusBarHeight = res.getDimensionPixelSize(R.dimen.status_bar_height);
        this.mBottomMargin = res.getDimensionPixelSize(R.dimen.notification_panel_margin_bottom);
        this.mSidePaddings = res.getDimensionPixelSize(R.dimen.notification_side_paddings);
        this.mMinInteractionHeight = res.getDimensionPixelSize(R.dimen.notification_min_interaction_height);
        this.mCornerRadius = res.getDimensionPixelSize(Utils.getThemeAttr(this.mContext, 16844145));
        this.mHeadsUpInset = this.mStatusBarHeight + res.getDimensionPixelSize(R.dimen.heads_up_status_bar_padding);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyHeightChangeListener(ExpandableView view) {
        notifyHeightChangeListener(view, false);
    }

    private void notifyHeightChangeListener(ExpandableView view, boolean needsAnimation) {
        ExpandableView.OnHeightChangedListener onHeightChangedListener = this.mOnHeightChangedListener;
        if (onHeightChangedListener != null) {
            onHeightChangedListener.onHeightChanged(view, needsAnimation);
        }
    }

    public boolean isPulseExpanding() {
        return this.mAmbientState.isPulseExpanding();
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int childWidthSpec = View.MeasureSpec.makeMeasureSpec(width - (this.mSidePaddings * 2), View.MeasureSpec.getMode(widthMeasureSpec));
        int childHeightSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), 0);
        int size = getChildCount();
        for (int i = 0; i < size; i++) {
            measureChild(getChildAt(i), childWidthSpec, childHeightSpec);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        float centerX = getWidth() / 2.0f;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            float width = child.getMeasuredWidth();
            float height = child.getMeasuredHeight();
            child.layout((int) (centerX - (width / 2.0f)), 0, (int) ((width / 2.0f) + centerX), (int) height);
        }
        setMaxLayoutHeight(getHeight());
        updateContentHeight();
        clampScrollPosition();
        requestChildrenUpdate();
        updateFirstAndLastBackgroundViews();
        updateAlgorithmLayoutMinHeight();
        updateOwnTranslationZ();
    }

    private void requestAnimationOnViewResize(ExpandableNotificationRow row) {
        if (this.mAnimationsEnabled) {
            if (this.mIsExpanded || (row != null && row.isPinned())) {
                this.mNeedViewResizeAnimation = true;
                this.mNeedsAnimation = true;
            }
        }
    }

    public void updateSpeedBumpIndex(int newIndex, boolean noAmbient) {
        this.mAmbientState.setSpeedBumpIndex(newIndex);
        this.mNoAmbient = noAmbient;
    }

    @Override // com.android.systemui.statusbar.notification.stack.NotificationListContainer
    public void setChildLocationsChangedListener(NotificationLogger.OnChildLocationsChangedListener listener) {
        this.mListener = listener;
    }

    @Override // com.android.systemui.statusbar.notification.VisibilityLocationProvider
    public boolean isInVisibleLocation(NotificationEntry entry) {
        ExpandableNotificationRow row = entry.getRow();
        ExpandableViewState childViewState = row.getViewState();
        if (childViewState == null || (childViewState.location & 5) == 0 || row.getVisibility() != 0) {
            return false;
        }
        return true;
    }

    private void setMaxLayoutHeight(int maxLayoutHeight) {
        this.mMaxLayoutHeight = maxLayoutHeight;
        this.mShelf.setMaxLayoutHeight(maxLayoutHeight);
        updateAlgorithmHeightAndPadding();
    }

    private void updateAlgorithmHeightAndPadding() {
        this.mAmbientState.setLayoutHeight(getLayoutHeight());
        updateAlgorithmLayoutMinHeight();
        this.mAmbientState.setTopPadding(this.mTopPadding);
    }

    private void updateAlgorithmLayoutMinHeight() {
        this.mAmbientState.setLayoutMinHeight((this.mQsExpanded || isHeadsUpTransition()) ? getLayoutMinHeight() : 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateChildren() {
        float currVelocity;
        updateScrollStateForAddedChildren();
        AmbientState ambientState = this.mAmbientState;
        if (this.mScroller.isFinished()) {
            currVelocity = 0.0f;
        } else {
            currVelocity = this.mScroller.getCurrVelocity();
        }
        ambientState.setCurrentScrollVelocity(currVelocity);
        this.mAmbientState.setScrollY(this.mOwnScrollY);
        this.mStackScrollAlgorithm.resetViewStates(this.mAmbientState);
        if (!isCurrentlyAnimating() && !this.mNeedsAnimation) {
            applyCurrentState();
        } else {
            startAnimationToState();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPreDrawDuringAnimation() {
        this.mShelf.updateAppearance();
        updateClippingToTopRoundedCorner();
        if (!this.mNeedsAnimation && !this.mChildrenUpdateRequested) {
            updateBackground();
        }
    }

    private void updateClippingToTopRoundedCorner() {
        Float clipStart = Float.valueOf(this.mTopPadding + this.mStackTranslation + this.mAmbientState.getExpandAnimationTopChange());
        Float clipEnd = Float.valueOf(clipStart.floatValue() + this.mCornerRadius);
        boolean first = true;
        for (int i = 0; i < getChildCount(); i++) {
            ExpandableView child = (ExpandableView) getChildAt(i);
            if (child.getVisibility() != 8) {
                float start = child.getTranslationY();
                float end = child.getActualHeight() + start;
                boolean z = false;
                boolean clip = (clipStart.floatValue() > start && clipStart.floatValue() < end) || (clipEnd.floatValue() >= start && clipEnd.floatValue() <= end);
                child.setDistanceToTopRoundness(clip & ((first && isScrolledToTop()) ? true : true) ? Math.max(start - clipStart.floatValue(), 0.0f) : -1.0f);
                first = false;
            }
        }
    }

    private void updateScrollStateForAddedChildren() {
        int padding;
        if (this.mChildrenToAddAnimated.isEmpty()) {
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            ExpandableView child = (ExpandableView) getChildAt(i);
            if (this.mChildrenToAddAnimated.contains(child)) {
                int startingPosition = getPositionInLinearLayout(child);
                float increasedPaddingAmount = child.getIncreasedPaddingAmount();
                if (increasedPaddingAmount == 1.0f) {
                    padding = this.mIncreasedPaddingBetweenElements;
                } else {
                    padding = increasedPaddingAmount == -1.0f ? 0 : this.mPaddingBetweenElements;
                }
                int childHeight = getIntrinsicHeight(child) + padding;
                int i2 = this.mOwnScrollY;
                if (startingPosition < i2) {
                    setOwnScrollY(i2 + childHeight);
                }
            }
        }
        clampScrollPosition();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateForcedScroll() {
        View view = this.mForcedScroll;
        if (view != null && (!view.hasFocus() || !this.mForcedScroll.isAttachedToWindow())) {
            this.mForcedScroll = null;
        }
        View view2 = this.mForcedScroll;
        if (view2 != null) {
            ExpandableView expandableView = (ExpandableView) view2;
            int positionInLinearLayout = getPositionInLinearLayout(expandableView);
            int targetScroll = targetScrollForView(expandableView, positionInLinearLayout);
            int outOfViewScroll = expandableView.getIntrinsicHeight() + positionInLinearLayout;
            int targetScroll2 = Math.max(0, Math.min(targetScroll, getScrollRange()));
            int i = this.mOwnScrollY;
            if (i < targetScroll2 || outOfViewScroll < i) {
                setOwnScrollY(targetScroll2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestChildrenUpdate() {
        if (!this.mChildrenUpdateRequested) {
            getViewTreeObserver().addOnPreDrawListener(this.mChildrenUpdater);
            this.mChildrenUpdateRequested = true;
            invalidate();
        }
    }

    public int getVisibleNotificationCount() {
        int count = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8 && (child instanceof ExpandableNotificationRow)) {
                count++;
            }
        }
        return count;
    }

    private boolean isCurrentlyAnimating() {
        return this.mStateAnimator.isRunning();
    }

    private void clampScrollPosition() {
        int scrollRange = getScrollRange();
        if (scrollRange < this.mOwnScrollY) {
            setOwnScrollY(scrollRange);
        }
    }

    public int getTopPadding() {
        return this.mTopPadding;
    }

    private void setTopPadding(int topPadding, boolean animate) {
        if (this.mTopPadding != topPadding) {
            this.mTopPadding = topPadding;
            updateAlgorithmHeightAndPadding();
            updateContentHeight();
            if (animate && this.mAnimationsEnabled && this.mIsExpanded) {
                this.mTopPaddingNeedsAnimation = true;
                this.mNeedsAnimation = true;
            }
            requestChildrenUpdate();
            notifyHeightChangeListener(null, animate);
        }
    }

    public void setExpandedHeight(float height) {
        float translationY;
        int stackStartPosition;
        float translationY2;
        this.mExpandedHeight = height;
        setIsExpanded(height > 0.0f);
        int minExpansionHeight = getMinExpansionHeight();
        if (height < minExpansionHeight) {
            Rect rect = this.mClipRect;
            rect.left = 0;
            rect.right = getWidth();
            Rect rect2 = this.mClipRect;
            rect2.top = 0;
            rect2.bottom = (int) height;
            height = minExpansionHeight;
            setRequestedClipBounds(rect2);
        } else {
            setRequestedClipBounds(null);
        }
        float appearEndPosition = getAppearEndPosition();
        float appearStartPosition = getAppearStartPosition();
        boolean appearing = height < appearEndPosition;
        this.mAmbientState.setAppearing(appearing);
        if (!appearing) {
            translationY2 = 0.0f;
            if (this.mShouldShowShelfOnly) {
                stackStartPosition = this.mTopPadding + this.mShelf.getIntrinsicHeight();
            } else if (this.mQsExpanded) {
                int stackStartPosition2 = (this.mContentHeight - this.mTopPadding) + this.mIntrinsicPadding;
                int stackEndPosition = this.mMaxTopPadding + this.mShelf.getIntrinsicHeight();
                if (stackStartPosition2 <= stackEndPosition) {
                    stackStartPosition = stackEndPosition;
                } else {
                    stackStartPosition = (int) NotificationUtils.interpolate(stackStartPosition2, stackEndPosition, this.mQsExpansionFraction);
                }
            } else {
                stackStartPosition = (int) height;
            }
        } else {
            float appearFraction = calculateAppearFraction(height);
            if (appearFraction >= 0.0f) {
                translationY = NotificationUtils.interpolate(getExpandTranslationStart(), 0.0f, appearFraction);
            } else {
                float translationY3 = height - appearStartPosition;
                translationY = translationY3 + getExpandTranslationStart();
            }
            if (isHeadsUpTransition()) {
                int stackHeight = getFirstVisibleSection().getFirstVisibleChild().getPinnedHeadsUpHeight();
                translationY2 = MathUtils.lerp(this.mHeadsUpInset - this.mTopPadding, 0.0f, appearFraction);
                stackStartPosition = stackHeight;
            } else {
                float f = translationY;
                stackStartPosition = (int) (height - translationY);
                translationY2 = f;
            }
        }
        if (stackStartPosition != this.mCurrentStackHeight) {
            this.mCurrentStackHeight = stackStartPosition;
            updateAlgorithmHeightAndPadding();
            requestChildrenUpdate();
        }
        setStackTranslation(translationY2);
        notifyAppearChangedListeners();
    }

    private void notifyAppearChangedListeners() {
        float appear;
        float expandAmount;
        if (this.mKeyguardBypassController.getBypassEnabled() && onKeyguard()) {
            appear = calculateAppearFractionBypass();
            expandAmount = getPulseHeight();
        } else {
            float appear2 = this.mExpandedHeight;
            appear = MathUtils.saturate(calculateAppearFraction(appear2));
            expandAmount = this.mExpandedHeight;
        }
        if (appear != this.mLastSentAppear || expandAmount != this.mLastSentExpandedHeight) {
            this.mLastSentAppear = appear;
            this.mLastSentExpandedHeight = expandAmount;
            for (int i = 0; i < this.mExpandedHeightListeners.size(); i++) {
                BiConsumer<Float, Float> listener = this.mExpandedHeightListeners.get(i);
                listener.accept(Float.valueOf(expandAmount), Float.valueOf(appear));
            }
        }
    }

    private void setRequestedClipBounds(Rect clipRect) {
        this.mRequestedClipBounds = clipRect;
        updateClipping();
    }

    public int getIntrinsicContentHeight() {
        return this.mIntrinsicContentHeight;
    }

    public void updateClipping() {
        boolean clipped = (this.mRequestedClipBounds == null || this.mInHeadsUpPinnedMode || this.mHeadsUpAnimatingAway) ? false : true;
        boolean clipToOutline = false;
        if (this.mIsClipped != clipped) {
            this.mIsClipped = clipped;
        }
        if (this.mAmbientState.isHiddenAtAll()) {
            clipToOutline = true;
            invalidateOutline();
            if (isFullyHidden()) {
                setClipBounds(null);
            }
        } else if (clipped) {
            setClipBounds(this.mRequestedClipBounds);
        } else {
            setClipBounds(null);
        }
        setClipToOutline(clipToOutline);
    }

    private float getExpandTranslationStart() {
        return ((-this.mTopPadding) + getMinExpansionHeight()) - this.mShelf.getIntrinsicHeight();
    }

    private float getAppearStartPosition() {
        if (isHeadsUpTransition()) {
            return this.mHeadsUpInset + getFirstVisibleSection().getFirstVisibleChild().getPinnedHeadsUpHeight();
        }
        return getMinExpansionHeight();
    }

    private int getTopHeadsUpPinnedHeight() {
        NotificationEntry groupSummary;
        NotificationEntry topEntry = this.mHeadsUpManager.getTopEntry();
        if (topEntry == null) {
            return 0;
        }
        ExpandableNotificationRow row = topEntry.getRow();
        if (row.isChildInGroup() && (groupSummary = this.mGroupManager.getGroupSummary(row.getStatusBarNotification())) != null) {
            row = groupSummary.getRow();
        }
        return row.getPinnedHeadsUpHeight();
    }

    private float getAppearEndPosition() {
        int appearPosition;
        int notGoneChildCount = getNotGoneChildCount();
        if (this.mEmptyShadeView.getVisibility() == 8 && notGoneChildCount != 0) {
            if (isHeadsUpTransition() || (this.mHeadsUpManager.hasPinnedHeadsUp() && !this.mAmbientState.isDozing())) {
                appearPosition = getTopHeadsUpPinnedHeight();
            } else {
                appearPosition = 0;
                if (notGoneChildCount >= 1 && this.mShelf.getVisibility() != 8) {
                    appearPosition = 0 + this.mShelf.getIntrinsicHeight();
                }
            }
        } else {
            appearPosition = this.mEmptyShadeView.getHeight();
        }
        return (onKeyguard() ? this.mTopPadding : this.mIntrinsicPadding) + appearPosition;
    }

    private boolean isHeadsUpTransition() {
        NotificationSection firstVisibleSection = getFirstVisibleSection();
        return this.mTrackingHeadsUp && firstVisibleSection != null && firstVisibleSection.getFirstVisibleChild().isAboveShelf();
    }

    public float calculateAppearFraction(float height) {
        float appearEndPosition = getAppearEndPosition();
        float appearStartPosition = getAppearStartPosition();
        return (height - appearStartPosition) / (appearEndPosition - appearStartPosition);
    }

    public float getStackTranslation() {
        return this.mStackTranslation;
    }

    private void setStackTranslation(float stackTranslation) {
        if (stackTranslation != this.mStackTranslation) {
            this.mStackTranslation = stackTranslation;
            this.mAmbientState.setStackTranslation(stackTranslation);
            requestChildrenUpdate();
        }
    }

    private int getLayoutHeight() {
        return Math.min(this.mMaxLayoutHeight, this.mCurrentStackHeight);
    }

    public int getFirstItemMinHeight() {
        ExpandableView firstChild = getFirstChildNotGone();
        return firstChild != null ? firstChild.getMinHeight() : this.mCollapsedSize;
    }

    public void setQsContainer(ViewGroup qsContainer) {
        this.mQsContainer = qsContainer;
    }

    public static boolean isPinnedHeadsUp(View v) {
        if (v instanceof ExpandableNotificationRow) {
            ExpandableNotificationRow row = (ExpandableNotificationRow) v;
            return row.isHeadsUp() && row.isPinned();
        }
        return false;
    }

    private boolean isHeadsUp(View v) {
        if (v instanceof ExpandableNotificationRow) {
            ExpandableNotificationRow row = (ExpandableNotificationRow) v;
            return row.isHeadsUp();
        }
        return false;
    }

    public ExpandableView getClosestChildAtRawPosition(float touchX, float touchY) {
        getLocationOnScreen(this.mTempInt2);
        float localTouchY = touchY - this.mTempInt2[1];
        ExpandableView closestChild = null;
        float minDist = Float.MAX_VALUE;
        int count = getChildCount();
        for (int childIdx = 0; childIdx < count; childIdx++) {
            ExpandableView slidingChild = (ExpandableView) getChildAt(childIdx);
            if (slidingChild.getVisibility() != 8 && !(slidingChild instanceof StackScrollerDecorView)) {
                float childTop = slidingChild.getTranslationY();
                float top = slidingChild.getClipTopAmount() + childTop;
                float bottom = (slidingChild.getActualHeight() + childTop) - slidingChild.getClipBottomAmount();
                float dist = Math.min(Math.abs(top - localTouchY), Math.abs(bottom - localTouchY));
                if (dist < minDist) {
                    closestChild = slidingChild;
                    minDist = dist;
                }
            }
        }
        return closestChild;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ExpandableView getChildAtPosition(float touchX, float touchY) {
        return getChildAtPosition(touchX, touchY, true);
    }

    private ExpandableView getChildAtPosition(float touchX, float touchY, boolean requireMinHeight) {
        int count = getChildCount();
        for (int childIdx = 0; childIdx < count; childIdx++) {
            ExpandableView slidingChild = (ExpandableView) getChildAt(childIdx);
            if (slidingChild.getVisibility() == 0 && !(slidingChild instanceof StackScrollerDecorView)) {
                float childTop = slidingChild.getTranslationY();
                float top = slidingChild.getClipTopAmount() + childTop;
                float bottom = (slidingChild.getActualHeight() + childTop) - slidingChild.getClipBottomAmount();
                int right = getWidth();
                if ((bottom - top >= this.mMinInteractionHeight || !requireMinHeight) && touchY >= top && touchY <= bottom && touchX >= 0 && touchX <= right) {
                    if (slidingChild instanceof ExpandableNotificationRow) {
                        ExpandableNotificationRow row = (ExpandableNotificationRow) slidingChild;
                        NotificationEntry entry = row.getEntry();
                        if (this.mIsExpanded || !row.isHeadsUp() || !row.isPinned() || this.mHeadsUpManager.getTopEntry().getRow() == row || this.mGroupManager.getGroupSummary(this.mHeadsUpManager.getTopEntry().notification) == entry) {
                            return row.getViewAtPosition(touchY - childTop);
                        }
                    } else {
                        return slidingChild;
                    }
                }
            }
        }
        return null;
    }

    public ExpandableView getChildAtRawPosition(float touchX, float touchY) {
        getLocationOnScreen(this.mTempInt2);
        int[] iArr = this.mTempInt2;
        return getChildAtPosition(touchX - iArr[0], touchY - iArr[1]);
    }

    public void setScrollingEnabled(boolean enable) {
        this.mScrollingEnabled = enable;
    }

    public void lockScrollTo(View v) {
        if (this.mForcedScroll == v) {
            return;
        }
        this.mForcedScroll = v;
        scrollTo(v);
    }

    public boolean scrollTo(View v) {
        ExpandableView expandableView = (ExpandableView) v;
        int positionInLinearLayout = getPositionInLinearLayout(v);
        int targetScroll = targetScrollForView(expandableView, positionInLinearLayout);
        int outOfViewScroll = expandableView.getIntrinsicHeight() + positionInLinearLayout;
        int i = this.mOwnScrollY;
        if (i < targetScroll || outOfViewScroll < i) {
            OverScroller overScroller = this.mScroller;
            int i2 = this.mScrollX;
            int i3 = this.mOwnScrollY;
            overScroller.startScroll(i2, i3, 0, targetScroll - i3);
            this.mDontReportNextOverScroll = true;
            lambda$new$1$NotificationStackScrollLayout();
            return true;
        }
        return false;
    }

    private int targetScrollForView(ExpandableView v, int positionInLinearLayout) {
        return (((v.getIntrinsicHeight() + positionInLinearLayout) + getImeInset()) - getHeight()) + ((isExpanded() || !isPinnedHeadsUp(v)) ? getTopPadding() : this.mHeadsUpInset);
    }

    @Override // android.view.View
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        this.mBottomInset = insets.getSystemWindowInsetBottom();
        int range = getScrollRange();
        if (this.mOwnScrollY > range) {
            removeCallbacks(this.mReclamp);
            postDelayed(this.mReclamp, 50L);
        } else {
            View view = this.mForcedScroll;
            if (view != null) {
                scrollTo(view);
            }
        }
        return insets;
    }

    public void setExpandingEnabled(boolean enable) {
        this.mExpandHelper.setEnabled(enable);
    }

    private boolean isScrollingEnabled() {
        return this.mScrollingEnabled;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onKeyguard() {
        return this.mStatusBarState == 1;
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mStatusBarHeight = getResources().getDimensionPixelOffset(R.dimen.status_bar_height);
        float densityScale = getResources().getDisplayMetrics().density;
        this.mSwipeHelper.setDensityScale(densityScale);
        float pagingTouchSlop = ViewConfiguration.get(getContext()).getScaledPagingTouchSlop();
        this.mSwipeHelper.setPagingTouchSlop(pagingTouchSlop);
        initView(getContext());
    }

    public void dismissViewAnimated(View child, Runnable endRunnable, int delay, long duration) {
        this.mSwipeHelper.dismissChild(child, 0.0f, endRunnable, delay, true, duration, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void snapViewIfNeeded(NotificationEntry entry) {
        ExpandableNotificationRow child = entry.getRow();
        boolean animate = this.mIsExpanded || isPinnedHeadsUp(child);
        if (child.getProvider() != null) {
            float targetLeft = child.getProvider().isMenuVisible() ? child.getTranslation() : 0.0f;
            this.mSwipeHelper.snapChildIfNeeded(child, animate, targetLeft);
        }
    }

    @Override // com.android.systemui.statusbar.notification.stack.NotificationListContainer
    public ViewGroup getViewParentForNotification(NotificationEntry entry) {
        return this;
    }

    private float overScrollUp(int deltaY, int range) {
        int deltaY2 = Math.max(deltaY, 0);
        float currentTopAmount = getCurrentOverScrollAmount(true);
        float newTopAmount = currentTopAmount - deltaY2;
        if (currentTopAmount > 0.0f) {
            setOverScrollAmount(newTopAmount, true, false);
        }
        float scrollAmount = newTopAmount < 0.0f ? -newTopAmount : 0.0f;
        float newScrollY = this.mOwnScrollY + scrollAmount;
        if (newScrollY > range) {
            if (!this.mExpandedInThisMotion) {
                float currentBottomPixels = getCurrentOverScrolledPixels(false);
                setOverScrolledPixels((currentBottomPixels + newScrollY) - range, false, false);
            }
            setOwnScrollY(range);
            return 0.0f;
        }
        return scrollAmount;
    }

    private float overScrollDown(int deltaY) {
        int deltaY2 = Math.min(deltaY, 0);
        float currentBottomAmount = getCurrentOverScrollAmount(false);
        float newBottomAmount = deltaY2 + currentBottomAmount;
        if (currentBottomAmount > 0.0f) {
            setOverScrollAmount(newBottomAmount, false, false);
        }
        float scrollAmount = newBottomAmount < 0.0f ? newBottomAmount : 0.0f;
        float newScrollY = this.mOwnScrollY + scrollAmount;
        if (newScrollY < 0.0f) {
            float currentTopPixels = getCurrentOverScrolledPixels(true);
            setOverScrolledPixels(currentTopPixels - newScrollY, true, false);
            setOwnScrollY(0);
            return 0.0f;
        }
        return scrollAmount;
    }

    private void initVelocityTrackerIfNotExists() {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    private void initOrResetVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        } else {
            velocityTracker.clear();
        }
    }

    public void setFinishScrollingCallback(Runnable runnable) {
        this.mFinishScrollingCallback = runnable;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: animateScroll */
    public void lambda$new$1$NotificationStackScrollLayout() {
        if (this.mScroller.computeScrollOffset()) {
            int oldY = this.mOwnScrollY;
            int y = this.mScroller.getCurrY();
            if (oldY != y) {
                int range = getScrollRange();
                if ((y < 0 && oldY >= 0) || (y > range && oldY <= range)) {
                    setMaxOverScrollFromCurrentVelocity();
                }
                if (this.mDontClampNextScroll) {
                    range = Math.max(range, oldY);
                }
                customOverScrollBy(y - oldY, oldY, range, (int) this.mMaxOverScroll);
            }
            postOnAnimation(this.mReflingAndAnimateScroll);
            return;
        }
        this.mDontClampNextScroll = false;
        Runnable runnable = this.mFinishScrollingCallback;
        if (runnable != null) {
            runnable.run();
        }
    }

    private void setMaxOverScrollFromCurrentVelocity() {
        float currVelocity = this.mScroller.getCurrVelocity();
        if (currVelocity >= this.mMinimumVelocity) {
            this.mMaxOverScroll = (Math.abs(currVelocity) / 1000.0f) * this.mOverflingDistance;
        }
    }

    private void customOverScrollBy(int deltaY, int scrollY, int scrollRangeY, int maxOverScrollY) {
        int newScrollY = scrollY + deltaY;
        int top = -maxOverScrollY;
        int bottom = maxOverScrollY + scrollRangeY;
        boolean clampedY = false;
        if (newScrollY > bottom) {
            newScrollY = bottom;
            clampedY = true;
        } else if (newScrollY < top) {
            newScrollY = top;
            clampedY = true;
        }
        onCustomOverScrolled(newScrollY, clampedY);
    }

    public void setOverScrolledPixels(float numPixels, boolean onTop, boolean animate) {
        setOverScrollAmount(getRubberBandFactor(onTop) * numPixels, onTop, animate, true);
    }

    public void setOverScrollAmount(float amount, boolean onTop, boolean animate) {
        setOverScrollAmount(amount, onTop, animate, true);
    }

    public void setOverScrollAmount(float amount, boolean onTop, boolean animate, boolean cancelAnimators) {
        setOverScrollAmount(amount, onTop, animate, cancelAnimators, isRubberbanded(onTop));
    }

    public void setOverScrollAmount(float amount, boolean onTop, boolean animate, boolean cancelAnimators, boolean isRubberbanded) {
        if (cancelAnimators) {
            this.mStateAnimator.cancelOverScrollAnimators(onTop);
        }
        setOverScrollAmountInternal(amount, onTop, animate, isRubberbanded);
    }

    private void setOverScrollAmountInternal(float amount, boolean onTop, boolean animate, boolean isRubberbanded) {
        float amount2 = Math.max(0.0f, amount);
        if (animate) {
            this.mStateAnimator.animateOverScrollToAmount(amount2, onTop, isRubberbanded);
            return;
        }
        setOverScrolledPixels(amount2 / getRubberBandFactor(onTop), onTop);
        this.mAmbientState.setOverScrollAmount(amount2, onTop);
        if (onTop) {
            notifyOverscrollTopListener(amount2, isRubberbanded);
        }
        requestChildrenUpdate();
    }

    private void notifyOverscrollTopListener(float amount, boolean isRubberbanded) {
        this.mExpandHelper.onlyObserveMovements(amount > 1.0f);
        if (this.mDontReportNextOverScroll) {
            this.mDontReportNextOverScroll = false;
            return;
        }
        OnOverscrollTopChangedListener onOverscrollTopChangedListener = this.mOverscrollTopChangedListener;
        if (onOverscrollTopChangedListener != null) {
            onOverscrollTopChangedListener.onOverscrollTopChanged(amount, isRubberbanded);
        }
    }

    public void setOverscrollTopChangedListener(OnOverscrollTopChangedListener overscrollTopChangedListener) {
        this.mOverscrollTopChangedListener = overscrollTopChangedListener;
    }

    public float getCurrentOverScrollAmount(boolean top) {
        return this.mAmbientState.getOverScrollAmount(top);
    }

    public float getCurrentOverScrolledPixels(boolean top) {
        return top ? this.mOverScrolledTopPixels : this.mOverScrolledBottomPixels;
    }

    private void setOverScrolledPixels(float amount, boolean onTop) {
        if (onTop) {
            this.mOverScrolledTopPixels = amount;
        } else {
            this.mOverScrolledBottomPixels = amount;
        }
    }

    private void onCustomOverScrolledBy(int deltaY, boolean clampedY) {
        int i;
        this.mScrollAnchorViewY -= deltaY;
        if (!this.mScroller.isFinished()) {
            if (clampedY) {
                springBack();
            } else {
                float overScrollTop = getCurrentOverScrollAmount(true);
                if (isScrolledToTop() && (i = this.mScrollAnchorViewY) > 0) {
                    notifyOverscrollTopListener(i, isRubberbanded(true));
                } else {
                    notifyOverscrollTopListener(overScrollTop, isRubberbanded(true));
                }
            }
        }
        updateScrollAnchor();
        updateOnScrollChange();
    }

    private void onCustomOverScrolled(int scrollY, boolean clampedY) {
        if (!this.mScroller.isFinished()) {
            setOwnScrollY(scrollY);
            if (clampedY) {
                springBack();
                return;
            }
            float overScrollTop = getCurrentOverScrollAmount(true);
            int i = this.mOwnScrollY;
            if (i < 0) {
                notifyOverscrollTopListener(-i, isRubberbanded(true));
                return;
            } else {
                notifyOverscrollTopListener(overScrollTop, isRubberbanded(true));
                return;
            }
        }
        setOwnScrollY(scrollY);
    }

    private void springBack() {
        boolean onTop;
        float newAmount;
        int scrollRange = getScrollRange();
        boolean overScrolledTop = this.mOwnScrollY <= 0;
        boolean overScrolledBottom = this.mOwnScrollY >= scrollRange;
        if (overScrolledTop || overScrolledBottom) {
            if (overScrolledTop) {
                onTop = true;
                newAmount = -this.mOwnScrollY;
                setOwnScrollY(0);
                this.mDontReportNextOverScroll = true;
            } else {
                onTop = false;
                newAmount = this.mOwnScrollY - scrollRange;
                setOwnScrollY(scrollRange);
            }
            setOverScrollAmount(newAmount, onTop, false);
            setOverScrollAmount(0.0f, onTop, true);
            this.mScroller.forceFinished(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getScrollRange() {
        int contentHeight = this.mContentHeight;
        if (!isExpanded() && this.mHeadsUpManager.hasPinnedHeadsUp()) {
            contentHeight = this.mHeadsUpInset + getTopHeadsUpPinnedHeight();
        }
        int scrollRange = Math.max(0, contentHeight - this.mMaxLayoutHeight);
        int imeInset = getImeInset();
        return scrollRange + Math.min(imeInset, Math.max(0, contentHeight - (getHeight() - imeInset)));
    }

    private int getImeInset() {
        return Math.max(0, this.mBottomInset - (getRootView().getHeight() - getHeight()));
    }

    public ExpandableView getFirstChildNotGone() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8 && child != this.mShelf) {
                return (ExpandableView) child;
            }
        }
        return null;
    }

    public ExpandableView getViewBeforeView(ExpandableView view) {
        ExpandableView previousView = null;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child == view) {
                return previousView;
            }
            if (child.getVisibility() != 8) {
                previousView = (ExpandableView) child;
            }
        }
        return null;
    }

    private View getFirstChildBelowTranlsationY(float translationY, boolean ignoreChildren) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                float rowTranslation = child.getTranslationY();
                if (rowTranslation >= translationY) {
                    return child;
                }
                if (!ignoreChildren && (child instanceof ExpandableNotificationRow)) {
                    ExpandableNotificationRow row = (ExpandableNotificationRow) child;
                    if (row.isSummaryWithChildren() && row.areChildrenExpanded()) {
                        List<ExpandableNotificationRow> notificationChildren = row.getNotificationChildren();
                        for (int childIndex = 0; childIndex < notificationChildren.size(); childIndex++) {
                            ExpandableNotificationRow rowChild = notificationChildren.get(childIndex);
                            if (rowChild.getTranslationY() + rowTranslation >= translationY) {
                                return rowChild;
                            }
                        }
                        continue;
                    }
                }
            }
        }
        return null;
    }

    public ExpandableView getLastChildNotGone() {
        int childCount = getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8 && child != this.mShelf) {
                return (ExpandableView) child;
            }
        }
        return null;
    }

    private ExpandableNotificationRow getLastRowNotGone() {
        int childCount = getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View child = getChildAt(i);
            if ((child instanceof ExpandableNotificationRow) && child.getVisibility() != 8) {
                return (ExpandableNotificationRow) child;
            }
        }
        return null;
    }

    public int getNotGoneChildCount() {
        int childCount = getChildCount();
        int count = 0;
        for (int i = 0; i < childCount; i++) {
            ExpandableView child = (ExpandableView) getChildAt(i);
            if (child.getVisibility() != 8 && !child.willBeGone() && child != this.mShelf) {
                count++;
            }
        }
        return count;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateContentHeight() {
        float padding;
        int height = 0;
        float previousPaddingRequest = this.mPaddingBetweenElements;
        float previousPaddingAmount = 0.0f;
        int numShownItems = 0;
        boolean finish = false;
        int maxDisplayedNotifications = this.mMaxDisplayedNotifications;
        for (int i = 0; i < getChildCount(); i++) {
            ExpandableView expandableView = (ExpandableView) getChildAt(i);
            boolean limitReached = false;
            boolean footerViewOnLockScreen = expandableView == this.mFooterView && onKeyguard();
            if (expandableView.getVisibility() != 8 && !expandableView.hasNoContentHeight() && !footerViewOnLockScreen) {
                if (maxDisplayedNotifications != -1 && numShownItems >= maxDisplayedNotifications) {
                    limitReached = true;
                }
                if (limitReached) {
                    expandableView = this.mShelf;
                    finish = true;
                }
                float increasedPaddingAmount = expandableView.getIncreasedPaddingAmount();
                if (increasedPaddingAmount < 0.0f) {
                    int ownPadding = (int) NotificationUtils.interpolate(0.0f, this.mPaddingBetweenElements, 1.0f + increasedPaddingAmount);
                    padding = previousPaddingAmount > 0.0f ? (int) NotificationUtils.interpolate(ownPadding, this.mIncreasedPaddingBetweenElements, previousPaddingAmount) : ownPadding;
                    previousPaddingRequest = ownPadding;
                } else {
                    padding = (int) NotificationUtils.interpolate(previousPaddingRequest, this.mIncreasedPaddingBetweenElements, increasedPaddingAmount);
                    previousPaddingRequest = (int) NotificationUtils.interpolate(this.mPaddingBetweenElements, this.mIncreasedPaddingBetweenElements, increasedPaddingAmount);
                }
                if (height != 0) {
                    height = (int) (height + padding);
                }
                previousPaddingAmount = increasedPaddingAmount;
                height += expandableView.getIntrinsicHeight();
                numShownItems++;
                if (finish) {
                    break;
                }
            }
        }
        this.mIntrinsicContentHeight = height;
        this.mContentHeight = Math.max(this.mIntrinsicPadding, this.mTopPadding) + height + this.mBottomMargin;
        updateScrollability();
        clampScrollPosition();
        this.mAmbientState.setLayoutMaxHeight(this.mContentHeight);
    }

    @Override // com.android.systemui.statusbar.notification.stack.NotificationListContainer
    public boolean hasPulsingNotifications() {
        return this.mPulsing;
    }

    private void updateScrollability() {
        boolean scrollable = !this.mQsExpanded && getScrollRange() > 0;
        if (scrollable != this.mScrollable) {
            this.mScrollable = scrollable;
            setFocusable(scrollable);
            updateForwardAndBackwardScrollability();
        }
    }

    private void updateForwardAndBackwardScrollability() {
        boolean changed = true;
        boolean forwardScrollable = this.mScrollable && !isScrolledToBottom();
        boolean backwardsScrollable = this.mScrollable && !isScrolledToTop();
        if (forwardScrollable == this.mForwardScrollable && backwardsScrollable == this.mBackwardScrollable) {
            changed = false;
        }
        this.mForwardScrollable = forwardScrollable;
        this.mBackwardScrollable = backwardsScrollable;
        if (changed) {
            sendAccessibilityEvent(2048);
        }
    }

    private void updateBackground() {
        NotificationSection[] notificationSectionArr;
        if (!this.mShouldDrawNotificationBackground) {
            return;
        }
        updateBackgroundBounds();
        if (didSectionBoundsChange()) {
            boolean animate = this.mAnimateNextSectionBoundsChange || this.mAnimateNextBackgroundTop || this.mAnimateNextBackgroundBottom || areSectionBoundsAnimating();
            if (!isExpanded()) {
                abortBackgroundAnimators();
                animate = false;
            }
            if (animate) {
                startBackgroundAnimation();
            } else {
                for (NotificationSection section : this.mSections) {
                    section.resetCurrentBounds();
                }
                invalidate();
            }
        } else {
            abortBackgroundAnimators();
        }
        this.mAnimateNextBackgroundTop = false;
        this.mAnimateNextBackgroundBottom = false;
        this.mAnimateNextSectionBoundsChange = false;
    }

    private void abortBackgroundAnimators() {
        NotificationSection[] notificationSectionArr;
        for (NotificationSection section : this.mSections) {
            section.cancelAnimators();
        }
    }

    private boolean didSectionBoundsChange() {
        NotificationSection[] notificationSectionArr;
        for (NotificationSection section : this.mSections) {
            if (section.didBoundsChange()) {
                return true;
            }
        }
        return false;
    }

    private boolean areSectionBoundsAnimating() {
        NotificationSection[] notificationSectionArr;
        for (NotificationSection section : this.mSections) {
            if (section.areBoundsAnimating()) {
                return true;
            }
        }
        return false;
    }

    private void startBackgroundAnimation() {
        NotificationSection[] notificationSectionArr;
        boolean z;
        boolean z2;
        NotificationSection firstVisibleSection = getFirstVisibleSection();
        NotificationSection lastVisibleSection = getLastVisibleSection();
        for (NotificationSection section : this.mSections) {
            if (section == firstVisibleSection) {
                z = this.mAnimateNextBackgroundTop;
            } else {
                z = this.mAnimateNextSectionBoundsChange;
            }
            if (section == lastVisibleSection) {
                z2 = this.mAnimateNextBackgroundBottom;
            } else {
                z2 = this.mAnimateNextSectionBoundsChange;
            }
            section.startBackgroundAnimation(z, z2);
        }
    }

    private void updateBackgroundBounds() {
        NotificationSection[] notificationSectionArr;
        int minTopPosition;
        NotificationSection[] notificationSectionArr2;
        NotificationSection[] notificationSectionArr3;
        int left = this.mSidePaddings;
        int right = getWidth() - this.mSidePaddings;
        for (NotificationSection section : this.mSections) {
            section.getBounds().left = left;
            section.getBounds().right = right;
        }
        if (!this.mIsExpanded) {
            for (NotificationSection section2 : this.mSections) {
                section2.getBounds().top = 0;
                section2.getBounds().bottom = 0;
            }
            return;
        }
        NotificationSection lastSection = getLastVisibleSection();
        boolean shiftPulsingWithFirst = true;
        boolean onKeyguard = this.mStatusBarState == 1;
        if (!onKeyguard) {
            minTopPosition = (int) (this.mTopPadding + this.mStackTranslation);
        } else if (lastSection == null) {
            minTopPosition = this.mTopPadding;
        } else {
            NotificationSection firstVisibleSection = getFirstVisibleSection();
            firstVisibleSection.updateBounds(0, 0, false);
            minTopPosition = firstVisibleSection.getBounds().top;
        }
        if (this.mHeadsUpManager.getAllEntries().count() > 1 || (!this.mAmbientState.isDozing() && (!this.mKeyguardBypassController.getBypassEnabled() || !onKeyguard))) {
            shiftPulsingWithFirst = false;
        }
        for (NotificationSection section3 : this.mSections) {
            int minBottomPosition = minTopPosition;
            if (section3 == lastSection) {
                minBottomPosition = (int) (ViewState.getFinalTranslationY(this.mShelf) + this.mShelf.getIntrinsicHeight());
            }
            minTopPosition = section3.updateBounds(minTopPosition, minBottomPosition, shiftPulsingWithFirst);
            shiftPulsingWithFirst = false;
        }
    }

    private NotificationSection getFirstVisibleSection() {
        NotificationSection[] notificationSectionArr;
        for (NotificationSection section : this.mSections) {
            if (section.getFirstVisibleChild() != null) {
                return section;
            }
        }
        return null;
    }

    private NotificationSection getLastVisibleSection() {
        for (int i = this.mSections.length - 1; i >= 0; i--) {
            NotificationSection section = this.mSections[i];
            if (section.getLastVisibleChild() != null) {
                return section;
            }
        }
        return null;
    }

    private ActivatableNotificationView getLastChildWithBackground() {
        int childCount = getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8 && (child instanceof ActivatableNotificationView) && child != this.mShelf) {
                return (ActivatableNotificationView) child;
            }
        }
        return null;
    }

    private ActivatableNotificationView getFirstChildWithBackground() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8 && (child instanceof ActivatableNotificationView) && child != this.mShelf) {
                return (ActivatableNotificationView) child;
            }
        }
        return null;
    }

    protected void fling(int velocityY) {
        int minScrollY;
        if (getChildCount() > 0) {
            float topAmount = getCurrentOverScrollAmount(true);
            int i = 0;
            float bottomAmount = getCurrentOverScrollAmount(false);
            if (velocityY < 0 && topAmount > 0.0f) {
                setOwnScrollY(this.mOwnScrollY - ((int) topAmount));
                this.mDontReportNextOverScroll = true;
                setOverScrollAmount(0.0f, true, false);
                this.mMaxOverScroll = ((Math.abs(velocityY) / 1000.0f) * getRubberBandFactor(true) * this.mOverflingDistance) + topAmount;
            } else if (velocityY > 0 && bottomAmount > 0.0f) {
                setOwnScrollY((int) (this.mOwnScrollY + bottomAmount));
                setOverScrollAmount(0.0f, false, false);
                this.mMaxOverScroll = ((Math.abs(velocityY) / 1000.0f) * getRubberBandFactor(false) * this.mOverflingDistance) + bottomAmount;
            } else {
                this.mMaxOverScroll = 0.0f;
            }
            int scrollRange = getScrollRange();
            int minScrollY2 = Math.max(0, scrollRange);
            if (!this.mExpandedInThisMotion) {
                minScrollY = minScrollY2;
            } else {
                minScrollY = Math.min(minScrollY2, this.mMaxScrollAfterExpand);
            }
            OverScroller overScroller = this.mScroller;
            int i2 = this.mScrollX;
            int i3 = this.mOwnScrollY;
            overScroller.fling(i2, i3, 1, velocityY, 0, 0, 0, minScrollY, 0, (!this.mExpandedInThisMotion || i3 < 0) ? 1073741823 : 1073741823);
            lambda$new$1$NotificationStackScrollLayout();
        }
    }

    private void flingScroller(int velocityY) {
        this.mIsScrollerBoundSet = false;
        maybeFlingScroller(velocityY, true);
    }

    private void maybeFlingScroller(int velocityY, boolean alwaysFling) {
        int minY = Integer.MIN_VALUE;
        int maxY = Integer.MAX_VALUE;
        if (velocityY < 0) {
            minY = getMaxNegativeScrollAmount();
            if (minY > Integer.MIN_VALUE) {
                this.mIsScrollerBoundSet = true;
            }
        } else {
            maxY = getMaxPositiveScrollAmount();
            if (maxY < Integer.MAX_VALUE) {
                this.mIsScrollerBoundSet = true;
            }
        }
        if (this.mIsScrollerBoundSet || alwaysFling) {
            int i = 0;
            this.mLastScrollerY = 0;
            this.mScroller.fling(0, 0, 1, velocityY, 0, 0, minY, maxY, 0, (!this.mExpandedInThisMotion || isScrolledToTop()) ? 1073741823 : 1073741823);
        }
    }

    private int getMaxPositiveScrollAmount() {
        ExpandableNotificationRow lastRow = getLastRowNotGone();
        if (this.mScrollAnchorView != null && lastRow != null && !lastRow.isInShelf()) {
            return (int) ((((lastRow.getTranslationY() + lastRow.getActualHeight()) - this.mScrollAnchorView.getTranslationY()) + this.mScrollAnchorViewY) - ((this.mMaxLayoutHeight - getIntrinsicPadding()) - this.mFooterView.getActualHeight()));
        }
        return Integer.MAX_VALUE;
    }

    private int getMaxNegativeScrollAmount() {
        ExpandableView firstChild = getFirstChildNotGone();
        View view = this.mScrollAnchorView;
        if (view != null && firstChild != null) {
            return (int) (-((view.getTranslationY() - firstChild.getTranslationY()) - this.mScrollAnchorViewY));
        }
        return Integer.MIN_VALUE;
    }

    private void maybeReflingScroller() {
        if (!this.mIsScrollerBoundSet) {
            maybeFlingScroller((int) Math.signum(this.mScroller.getCurrVelocity()), false);
        }
    }

    private boolean shouldOverScrollFling(int initialVelocity) {
        float topOverScroll = getCurrentOverScrollAmount(true);
        return this.mScrolledToTopOnFirstDown && !this.mExpandedInThisMotion && topOverScroll > this.mMinTopOverScrollToEscape && initialVelocity > 0;
    }

    public void updateTopPadding(float qsHeight, boolean animate) {
        int topPadding = (int) qsHeight;
        int minStackHeight = getLayoutMinHeight();
        if (topPadding + minStackHeight > getHeight()) {
            this.mTopPaddingOverflow = (topPadding + minStackHeight) - getHeight();
        } else {
            this.mTopPaddingOverflow = 0.0f;
        }
        setTopPadding(topPadding, animate && !this.mKeyguardBypassController.getBypassEnabled());
        setExpandedHeight(this.mExpandedHeight);
    }

    public void setMaxTopPadding(int maxTopPadding) {
        this.mMaxTopPadding = maxTopPadding;
    }

    public int getLayoutMinHeight() {
        if (isHeadsUpTransition()) {
            return getTopHeadsUpPinnedHeight();
        }
        if (this.mShelf.getVisibility() == 8) {
            return 0;
        }
        return this.mShelf.getIntrinsicHeight();
    }

    public float getTopPaddingOverflow() {
        return this.mTopPaddingOverflow;
    }

    public int getPeekHeight() {
        ExpandableView firstChild = getFirstChildNotGone();
        int firstChildMinHeight = firstChild != null ? firstChild.getCollapsedHeight() : this.mCollapsedSize;
        int shelfHeight = 0;
        if (getLastVisibleSection() != null && this.mShelf.getVisibility() != 8) {
            shelfHeight = this.mShelf.getIntrinsicHeight();
        }
        return this.mIntrinsicPadding + firstChildMinHeight + shelfHeight;
    }

    private int clampPadding(int desiredPadding) {
        return Math.max(desiredPadding, this.mIntrinsicPadding);
    }

    private float getRubberBandFactor(boolean onTop) {
        if (!onTop) {
            return RUBBER_BAND_FACTOR_NORMAL;
        }
        if (this.mExpandedInThisMotion) {
            return RUBBER_BAND_FACTOR_AFTER_EXPAND;
        }
        if (this.mIsExpansionChanging || this.mPanelTracking) {
            return RUBBER_BAND_FACTOR_ON_PANEL_EXPAND;
        }
        if (!this.mScrolledToTopOnFirstDown) {
            return RUBBER_BAND_FACTOR_NORMAL;
        }
        return 1.0f;
    }

    private boolean isRubberbanded(boolean onTop) {
        return !onTop || this.mExpandedInThisMotion || this.mIsExpansionChanging || this.mPanelTracking || !this.mScrolledToTopOnFirstDown;
    }

    @Override // com.android.systemui.statusbar.notification.stack.NotificationListContainer
    public void setChildTransferInProgress(boolean childTransferInProgress) {
        Assert.isMainThread();
        this.mChildTransferInProgress = childTransferInProgress;
    }

    @Override // android.view.ViewGroup
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        if (!this.mChildTransferInProgress) {
            onViewRemovedInternal((ExpandableView) child, this);
        }
    }

    @Override // com.android.systemui.statusbar.notification.stack.NotificationListContainer
    public void cleanUpViewStateForEntry(NotificationEntry entry) {
        View child = entry.getRow();
        if (child == this.mSwipeHelper.getTranslatingParentView()) {
            this.mSwipeHelper.clearTranslatingParentView();
        }
    }

    private void onViewRemovedInternal(ExpandableView child, ViewGroup container) {
        if (this.mChangePositionInProgress) {
            return;
        }
        child.setOnHeightChangedListener(null);
        updateScrollStateForRemovedChild(child);
        boolean animationGenerated = generateRemoveAnimation(child);
        if (animationGenerated) {
            if (!this.mSwipedOutViews.contains(child) || Math.abs(child.getTranslation()) != child.getWidth()) {
                container.addTransientView(child, 0);
                child.setTransientContainer(container);
            }
        } else {
            this.mSwipedOutViews.remove(child);
        }
        updateAnimationState(false, child);
        focusNextViewIfFocused(child);
    }

    private void focusNextViewIfFocused(View view) {
        float translationY;
        if (view instanceof ExpandableNotificationRow) {
            ExpandableNotificationRow row = (ExpandableNotificationRow) view;
            if (row.shouldRefocusOnDismiss()) {
                View nextView = row.getChildAfterViewWhenDismissed();
                if (nextView == null) {
                    View groupParentWhenDismissed = row.getGroupParentWhenDismissed();
                    if (groupParentWhenDismissed != null) {
                        translationY = groupParentWhenDismissed.getTranslationY();
                    } else {
                        translationY = view.getTranslationY();
                    }
                    nextView = getFirstChildBelowTranlsationY(translationY, true);
                }
                if (nextView != null) {
                    nextView.requestAccessibilityFocus();
                }
            }
        }
    }

    private boolean isChildInGroup(View child) {
        return (child instanceof ExpandableNotificationRow) && this.mGroupManager.isChildInGroupWithSummary(((ExpandableNotificationRow) child).getStatusBarNotification());
    }

    private boolean generateRemoveAnimation(ExpandableView child) {
        if (removeRemovedChildFromHeadsUpChangeAnimations(child)) {
            this.mAddedHeadsUpChildren.remove(child);
            return false;
        } else if (isClickedHeadsUp(child)) {
            this.mClearTransientViewsWhenFinished.add(child);
            return true;
        } else if (this.mIsExpanded && this.mAnimationsEnabled && !isChildInInvisibleGroup(child)) {
            if (!this.mChildrenToAddAnimated.contains(child)) {
                this.mChildrenToRemoveAnimated.add(child);
                this.mNeedsAnimation = true;
                return true;
            }
            this.mChildrenToAddAnimated.remove(child);
            this.mFromMoreCardAdditions.remove(child);
            return false;
        } else {
            return false;
        }
    }

    private boolean isClickedHeadsUp(View child) {
        return HeadsUpUtil.isClickedHeadsUpNotification(child);
    }

    private boolean removeRemovedChildFromHeadsUpChangeAnimations(View child) {
        boolean hasAddEvent = false;
        Iterator<Pair<ExpandableNotificationRow, Boolean>> it = this.mHeadsUpChangeAnimations.iterator();
        while (it.hasNext()) {
            Pair<ExpandableNotificationRow, Boolean> eventPair = it.next();
            ExpandableNotificationRow row = (ExpandableNotificationRow) eventPair.first;
            boolean isHeadsUp = ((Boolean) eventPair.second).booleanValue();
            if (child == row) {
                this.mTmpList.add(eventPair);
                hasAddEvent |= isHeadsUp;
            }
        }
        if (hasAddEvent) {
            this.mHeadsUpChangeAnimations.removeAll(this.mTmpList);
            ((ExpandableNotificationRow) child).setHeadsUpAnimatingAway(false);
        }
        this.mTmpList.clear();
        return hasAddEvent;
    }

    private boolean isChildInInvisibleGroup(View child) {
        if (child instanceof ExpandableNotificationRow) {
            ExpandableNotificationRow row = (ExpandableNotificationRow) child;
            NotificationEntry groupSummary = this.mGroupManager.getGroupSummary(row.getStatusBarNotification());
            return (groupSummary == null || groupSummary.getRow() == row || row.getVisibility() != 4) ? false : true;
        }
        return false;
    }

    private void updateScrollStateForRemovedChild(ExpandableView removedChild) {
        int padding;
        int startingPosition = getPositionInLinearLayout(removedChild);
        float increasedPaddingAmount = removedChild.getIncreasedPaddingAmount();
        if (increasedPaddingAmount < 0.0f) {
            padding = (int) NotificationUtils.interpolate(0.0f, this.mPaddingBetweenElements, 1.0f + increasedPaddingAmount);
        } else {
            padding = (int) NotificationUtils.interpolate(this.mPaddingBetweenElements, this.mIncreasedPaddingBetweenElements, increasedPaddingAmount);
        }
        int childHeight = getIntrinsicHeight(removedChild) + padding;
        int endPosition = startingPosition + childHeight;
        int i = this.mOwnScrollY;
        if (endPosition <= i) {
            setOwnScrollY(i - childHeight);
        } else if (startingPosition < i) {
            setOwnScrollY(startingPosition);
        }
    }

    private int getIntrinsicHeight(View view) {
        if (view instanceof ExpandableView) {
            ExpandableView expandableView = (ExpandableView) view;
            return expandableView.getIntrinsicHeight();
        }
        return view.getHeight();
    }

    public int getPositionInLinearLayout(View requestedView) {
        float padding;
        ExpandableNotificationRow childInGroup = null;
        ExpandableNotificationRow requestedRow = null;
        if (isChildInGroup(requestedView)) {
            childInGroup = (ExpandableNotificationRow) requestedView;
            ExpandableNotificationRow notificationParent = childInGroup.getNotificationParent();
            requestedRow = notificationParent;
            requestedView = notificationParent;
        }
        int position = 0;
        float previousPaddingRequest = this.mPaddingBetweenElements;
        float previousPaddingAmount = 0.0f;
        int i = 0;
        while (true) {
            if (i >= getChildCount()) {
                return 0;
            }
            ExpandableView child = (ExpandableView) getChildAt(i);
            boolean notGone = child.getVisibility() != 8;
            if (notGone && !child.hasNoContentHeight()) {
                float increasedPaddingAmount = child.getIncreasedPaddingAmount();
                if (increasedPaddingAmount < 0.0f) {
                    int ownPadding = (int) NotificationUtils.interpolate(0.0f, this.mPaddingBetweenElements, 1.0f + increasedPaddingAmount);
                    if (previousPaddingAmount > 0.0f) {
                        padding = (int) NotificationUtils.interpolate(ownPadding, this.mIncreasedPaddingBetweenElements, previousPaddingAmount);
                    } else {
                        padding = ownPadding;
                    }
                    previousPaddingRequest = ownPadding;
                } else {
                    padding = (int) NotificationUtils.interpolate(previousPaddingRequest, this.mIncreasedPaddingBetweenElements, increasedPaddingAmount);
                    previousPaddingRequest = (int) NotificationUtils.interpolate(this.mPaddingBetweenElements, this.mIncreasedPaddingBetweenElements, increasedPaddingAmount);
                }
                if (position != 0) {
                    position = (int) (position + padding);
                }
                previousPaddingAmount = increasedPaddingAmount;
            }
            if (child == requestedView) {
                if (requestedRow != null) {
                    return position + requestedRow.getPositionOfChild(childInGroup);
                }
                return position;
            }
            if (notGone) {
                position += getIntrinsicHeight(child);
            }
            i++;
        }
    }

    @Override // android.view.ViewGroup
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        onViewAddedInternal((ExpandableView) child);
    }

    private void updateFirstAndLastBackgroundViews() {
        NotificationSection firstSection = getFirstVisibleSection();
        NotificationSection lastSection = getLastVisibleSection();
        ActivatableNotificationView previousFirstChild = firstSection == null ? null : firstSection.getFirstVisibleChild();
        ActivatableNotificationView previousLastChild = lastSection != null ? lastSection.getLastVisibleChild() : null;
        ActivatableNotificationView firstChild = getFirstChildWithBackground();
        ActivatableNotificationView lastChild = getLastChildWithBackground();
        NotificationSectionsManager notificationSectionsManager = this.mSectionsManager;
        NotificationSection[] notificationSectionArr = this.mSections;
        boolean z = true;
        boolean sectionViewsChanged = notificationSectionsManager.updateFirstAndLastViewsInSections(notificationSectionArr[0], notificationSectionArr[1], firstChild, lastChild);
        if (this.mAnimationsEnabled && this.mIsExpanded) {
            this.mAnimateNextBackgroundTop = firstChild != previousFirstChild;
            if (lastChild == previousLastChild && !this.mAnimateBottomOnLayout) {
                z = false;
            }
            this.mAnimateNextBackgroundBottom = z;
            this.mAnimateNextSectionBoundsChange = sectionViewsChanged;
        } else {
            this.mAnimateNextBackgroundTop = false;
            this.mAnimateNextBackgroundBottom = false;
            this.mAnimateNextSectionBoundsChange = false;
        }
        this.mAmbientState.setLastVisibleBackgroundChild(lastChild);
        this.mRoundnessManager.updateRoundedChildren(this.mSections);
        this.mAnimateBottomOnLayout = false;
        invalidate();
    }

    private void onViewAddedInternal(ExpandableView child) {
        updateHideSensitiveForChild(child);
        child.setOnHeightChangedListener(this);
        generateAddAnimation(child, false);
        updateAnimationState(child);
        updateChronometerForChild(child);
        if (child instanceof ExpandableNotificationRow) {
            ((ExpandableNotificationRow) child).setDismissRtl(this.mDismissRtl);
        }
    }

    private void updateHideSensitiveForChild(ExpandableView child) {
        child.setHideSensitiveForIntrinsicHeight(this.mAmbientState.isHideSensitive());
    }

    @Override // com.android.systemui.statusbar.notification.stack.NotificationListContainer
    public void notifyGroupChildRemoved(ExpandableView row, ViewGroup childrenContainer) {
        onViewRemovedInternal(row, childrenContainer);
    }

    @Override // com.android.systemui.statusbar.notification.stack.NotificationListContainer
    public void notifyGroupChildAdded(ExpandableView row) {
        onViewAddedInternal(row);
    }

    public void setAnimationsEnabled(boolean animationsEnabled) {
        this.mAnimationsEnabled = animationsEnabled;
        updateNotificationAnimationStates();
        if (!animationsEnabled) {
            this.mSwipedOutViews.clear();
            this.mChildrenToRemoveAnimated.clear();
            clearTemporaryViewsInGroup(this);
        }
    }

    private void updateNotificationAnimationStates() {
        boolean running = this.mAnimationsEnabled || hasPulsingNotifications();
        this.mShelf.setAnimationsEnabled(running);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            running &= this.mIsExpanded || isPinnedHeadsUp(child);
            updateAnimationState(running, child);
        }
    }

    private void updateAnimationState(View child) {
        updateAnimationState((this.mAnimationsEnabled || hasPulsingNotifications()) && (this.mIsExpanded || isPinnedHeadsUp(child)), child);
    }

    @Override // com.android.systemui.statusbar.notification.stack.NotificationListContainer
    public void setExpandingNotification(ExpandableNotificationRow row) {
        this.mAmbientState.setExpandingNotification(row);
        requestChildrenUpdate();
    }

    @Override // com.android.systemui.statusbar.notification.stack.NotificationListContainer
    public void bindRow(final ExpandableNotificationRow row) {
        row.setHeadsUpAnimatingAwayListener(new Consumer() { // from class: com.android.systemui.statusbar.notification.stack.-$$Lambda$NotificationStackScrollLayout$IyVMQHsc5qYX2sBD-ykvCcuqAGw
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                NotificationStackScrollLayout.this.lambda$bindRow$6$NotificationStackScrollLayout(row, (Boolean) obj);
            }
        });
    }

    public /* synthetic */ void lambda$bindRow$6$NotificationStackScrollLayout(ExpandableNotificationRow row, Boolean animatingAway) {
        this.mRoundnessManager.onHeadsupAnimatingAwayChanged(row, animatingAway.booleanValue());
        this.mHeadsUpAppearanceController.lambda$updateHeadsUpHeaders$4$HeadsUpAppearanceController(row.getEntry());
    }

    @Override // com.android.systemui.statusbar.notification.stack.NotificationListContainer
    public boolean containsView(View v) {
        return v.getParent() == this;
    }

    @Override // com.android.systemui.statusbar.notification.stack.NotificationListContainer
    public void applyExpandAnimationParams(ActivityLaunchAnimator.ExpandAnimationParameters params) {
        this.mAmbientState.setExpandAnimationTopChange(params == null ? 0 : params.getTopChange());
        requestChildrenUpdate();
    }

    private void updateAnimationState(boolean running, View child) {
        if (child instanceof ExpandableNotificationRow) {
            ExpandableNotificationRow row = (ExpandableNotificationRow) child;
            row.setIconAnimationRunning(running);
        }
    }

    public boolean isAddOrRemoveAnimationPending() {
        return this.mNeedsAnimation && !(this.mChildrenToAddAnimated.isEmpty() && this.mChildrenToRemoveAnimated.isEmpty());
    }

    @Override // com.android.systemui.statusbar.notification.stack.NotificationListContainer
    public void generateAddAnimation(ExpandableView child, boolean fromMoreCard) {
        if (this.mIsExpanded && this.mAnimationsEnabled && !this.mChangePositionInProgress && !isFullyHidden()) {
            this.mChildrenToAddAnimated.add(child);
            if (fromMoreCard) {
                this.mFromMoreCardAdditions.add(child);
            }
            this.mNeedsAnimation = true;
        }
        if (isHeadsUp(child) && this.mAnimationsEnabled && !this.mChangePositionInProgress && !isFullyHidden()) {
            this.mAddedHeadsUpChildren.add(child);
            this.mChildrenToAddAnimated.remove(child);
        }
    }

    @Override // com.android.systemui.statusbar.notification.stack.NotificationListContainer
    public void changeViewPosition(ExpandableView child, int newIndex) {
        Assert.isMainThread();
        if (this.mChangePositionInProgress) {
            throw new IllegalStateException("Reentrant call to changeViewPosition");
        }
        int currentIndex = indexOfChild(child);
        if (currentIndex == -1) {
            boolean isTransient = false;
            if ((child instanceof ExpandableNotificationRow) && ((ExpandableNotificationRow) child).getTransientContainer() != null) {
                isTransient = true;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Attempting to re-position ");
            sb.append(isTransient ? "transient" : "");
            sb.append(" view {");
            sb.append(child);
            sb.append("}");
            Log.e(TAG, sb.toString());
        } else if (child != null && child.getParent() == this && currentIndex != newIndex) {
            this.mChangePositionInProgress = true;
            child.setChangingPosition(true);
            removeView(child);
            addView(child, newIndex);
            child.setChangingPosition(false);
            this.mChangePositionInProgress = false;
            if (this.mIsExpanded && this.mAnimationsEnabled && child.getVisibility() != 8) {
                this.mChildrenChangingPositions.add(child);
                this.mNeedsAnimation = true;
            }
        }
    }

    private void startAnimationToState() {
        if (this.mNeedsAnimation) {
            generateAllAnimationEvents();
            this.mNeedsAnimation = false;
        }
        if (!this.mAnimationEvents.isEmpty() || isCurrentlyAnimating()) {
            setAnimationRunning(true);
            this.mStateAnimator.startAnimationForEvents(this.mAnimationEvents, this.mGoToFullShadeDelay);
            this.mAnimationEvents.clear();
            updateBackground();
            updateViewShadows();
            updateClippingToTopRoundedCorner();
        } else {
            applyCurrentState();
        }
        this.mGoToFullShadeDelay = 0L;
    }

    private void generateAllAnimationEvents() {
        generateHeadsUpAnimationEvents();
        generateChildRemovalEvents();
        generateChildAdditionEvents();
        generatePositionChangeEvents();
        generateTopPaddingEvent();
        generateActivateEvent();
        generateDimmedEvent();
        generateHideSensitiveEvent();
        generateGoToFullShadeEvent();
        generateViewResizeEvent();
        generateGroupExpansionEvent();
        generateAnimateEverythingEvent();
    }

    private void generateHeadsUpAnimationEvents() {
        int i;
        Iterator<Pair<ExpandableNotificationRow, Boolean>> it = this.mHeadsUpChangeAnimations.iterator();
        while (it.hasNext()) {
            Pair<ExpandableNotificationRow, Boolean> eventPair = it.next();
            ExpandableNotificationRow row = (ExpandableNotificationRow) eventPair.first;
            boolean isHeadsUp = ((Boolean) eventPair.second).booleanValue();
            if (isHeadsUp == row.isHeadsUp()) {
                int type = 14;
                boolean onBottom = false;
                boolean pinnedAndClosed = row.isPinned() && !this.mIsExpanded;
                boolean performDisappearAnimation = !this.mIsExpanded || (this.mKeyguardBypassController.getBypassEnabled() && onKeyguard() && this.mHeadsUpManager.hasPinnedHeadsUp());
                if (performDisappearAnimation && !isHeadsUp) {
                    if (row.wasJustClicked()) {
                        i = 13;
                    } else {
                        i = 12;
                    }
                    type = i;
                    if (row.isChildInGroup()) {
                        row.setHeadsUpAnimatingAway(false);
                    } else {
                        AnimationEvent event = new AnimationEvent(row, type);
                        event.headsUpFromBottom = onBottom;
                        this.mAnimationEvents.add(event);
                    }
                } else {
                    ExpandableViewState viewState = row.getViewState();
                    if (viewState != null) {
                        if (isHeadsUp && (this.mAddedHeadsUpChildren.contains(row) || pinnedAndClosed)) {
                            if (pinnedAndClosed || shouldHunAppearFromBottom(viewState)) {
                                type = 11;
                            } else {
                                type = 0;
                            }
                            onBottom = pinnedAndClosed ? false : true;
                        }
                        AnimationEvent event2 = new AnimationEvent(row, type);
                        event2.headsUpFromBottom = onBottom;
                        this.mAnimationEvents.add(event2);
                    }
                }
            }
        }
        this.mHeadsUpChangeAnimations.clear();
        this.mAddedHeadsUpChildren.clear();
    }

    private boolean shouldHunAppearFromBottom(ExpandableViewState viewState) {
        if (viewState.yTranslation + viewState.height < this.mAmbientState.getMaxHeadsUpTranslation()) {
            return false;
        }
        return true;
    }

    private void generateGroupExpansionEvent() {
        ExpandableView expandableView = this.mExpandedGroupView;
        if (expandableView != null) {
            this.mAnimationEvents.add(new AnimationEvent(expandableView, 10));
            this.mExpandedGroupView = null;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:7:0x0011  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void generateViewResizeEvent() {
        /*
            r5 = this;
            boolean r0 = r5.mNeedViewResizeAnimation
            if (r0 == 0) goto L34
            r0 = 0
            java.util.ArrayList<com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout$AnimationEvent> r1 = r5.mAnimationEvents
            java.util.Iterator r1 = r1.iterator()
        Lb:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L25
            java.lang.Object r2 = r1.next()
            com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout$AnimationEvent r2 = (com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout.AnimationEvent) r2
            int r3 = r2.animationType
            r4 = 13
            if (r3 == r4) goto L23
            r4 = 12
            if (r3 != r4) goto L22
            goto L23
        L22:
            goto Lb
        L23:
            r0 = 1
        L25:
            if (r0 != 0) goto L34
            java.util.ArrayList<com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout$AnimationEvent> r1 = r5.mAnimationEvents
            com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout$AnimationEvent r2 = new com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout$AnimationEvent
            r3 = 0
            r4 = 9
            r2.<init>(r3, r4)
            r1.add(r2)
        L34:
            r0 = 0
            r5.mNeedViewResizeAnimation = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout.generateViewResizeEvent():void");
    }

    private void generateChildRemovalEvents() {
        ViewGroup transientContainer;
        Iterator<ExpandableView> it = this.mChildrenToRemoveAnimated.iterator();
        while (it.hasNext()) {
            ExpandableView child = it.next();
            boolean childWasSwipedOut = this.mSwipedOutViews.contains(child);
            float removedTranslation = child.getTranslationY();
            boolean ignoreChildren = true;
            boolean z = false;
            int i = 1;
            if (child instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow row = (ExpandableNotificationRow) child;
                if (row.isRemoved() && row.wasChildInGroupWhenRemoved()) {
                    removedTranslation = row.getTranslationWhenRemoved();
                    ignoreChildren = false;
                }
                childWasSwipedOut |= Math.abs(row.getTranslation()) == ((float) row.getWidth());
            }
            if (!childWasSwipedOut) {
                Rect clipBounds = child.getClipBounds();
                if (clipBounds != null && clipBounds.height() == 0) {
                    z = true;
                }
                childWasSwipedOut = z;
                if (childWasSwipedOut && (child instanceof ExpandableView) && (transientContainer = child.getTransientContainer()) != null) {
                    transientContainer.removeTransientView(child);
                }
            }
            if (childWasSwipedOut) {
                i = 2;
            }
            int animationType = i;
            AnimationEvent event = new AnimationEvent(child, animationType);
            event.viewAfterChangingView = getFirstChildBelowTranlsationY(removedTranslation, ignoreChildren);
            this.mAnimationEvents.add(event);
            this.mSwipedOutViews.remove(child);
        }
        this.mChildrenToRemoveAnimated.clear();
    }

    private void generatePositionChangeEvents() {
        Iterator<ExpandableView> it = this.mChildrenChangingPositions.iterator();
        while (it.hasNext()) {
            ExpandableView child = it.next();
            this.mAnimationEvents.add(new AnimationEvent(child, 6));
        }
        this.mChildrenChangingPositions.clear();
        if (this.mGenerateChildOrderChangedEvent) {
            this.mAnimationEvents.add(new AnimationEvent(null, 6));
            this.mGenerateChildOrderChangedEvent = false;
        }
    }

    private void generateChildAdditionEvents() {
        Iterator<ExpandableView> it = this.mChildrenToAddAnimated.iterator();
        while (it.hasNext()) {
            ExpandableView child = it.next();
            if (this.mFromMoreCardAdditions.contains(child)) {
                this.mAnimationEvents.add(new AnimationEvent(child, 0, 360L));
            } else {
                this.mAnimationEvents.add(new AnimationEvent(child, 0));
            }
        }
        this.mChildrenToAddAnimated.clear();
        this.mFromMoreCardAdditions.clear();
    }

    private void generateTopPaddingEvent() {
        AnimationEvent event;
        if (this.mTopPaddingNeedsAnimation) {
            if (this.mAmbientState.isDozing()) {
                event = new AnimationEvent((ExpandableView) null, 3, 550L);
            } else {
                event = new AnimationEvent(null, 3);
            }
            this.mAnimationEvents.add(event);
        }
        this.mTopPaddingNeedsAnimation = false;
    }

    private void generateActivateEvent() {
        if (this.mActivateNeedsAnimation) {
            this.mAnimationEvents.add(new AnimationEvent(null, 4));
        }
        this.mActivateNeedsAnimation = false;
    }

    private void generateAnimateEverythingEvent() {
        if (this.mEverythingNeedsAnimation) {
            this.mAnimationEvents.add(new AnimationEvent(null, 15));
        }
        this.mEverythingNeedsAnimation = false;
    }

    private void generateDimmedEvent() {
        if (this.mDimmedNeedsAnimation) {
            this.mAnimationEvents.add(new AnimationEvent(null, 5));
        }
        this.mDimmedNeedsAnimation = false;
    }

    private void generateHideSensitiveEvent() {
        if (this.mHideSensitiveNeedsAnimation) {
            this.mAnimationEvents.add(new AnimationEvent(null, 8));
        }
        this.mHideSensitiveNeedsAnimation = false;
    }

    private void generateGoToFullShadeEvent() {
        if (this.mGoToFullShadeNeedsAnimation) {
            this.mAnimationEvents.add(new AnimationEvent(null, 7));
        }
        this.mGoToFullShadeNeedsAnimation = false;
    }

    protected StackScrollAlgorithm createStackScrollAlgorithm(Context context) {
        return new StackScrollAlgorithm(context, this);
    }

    public boolean isInContentBounds(float y) {
        return y < ((float) (getHeight() - getEmptyBottomMargin()));
    }

    public void setLongPressListener(ExpandableNotificationRow.LongPressListener listener) {
        this.mLongPressListener = listener;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        boolean isCancelOrUp = ev.getActionMasked() == 3 || ev.getActionMasked() == 1;
        handleEmptySpaceClick(ev);
        boolean expandWantsIt = false;
        boolean swipingInProgress = this.mSwipingInProgress;
        if (this.mIsExpanded && !swipingInProgress && !this.mOnlyScrollingInThisMotion) {
            if (isCancelOrUp) {
                this.mExpandHelper.onlyObserveMovements(false);
            }
            boolean wasExpandingBefore = this.mExpandingNotification;
            expandWantsIt = this.mExpandHelper.onTouchEvent(ev);
            if (this.mExpandedInThisMotion && !this.mExpandingNotification && wasExpandingBefore && !this.mDisallowScrollingInThisMotion) {
                dispatchDownEventToScroller(ev);
            }
        }
        boolean scrollerWantsIt = false;
        if (this.mIsExpanded && !swipingInProgress && !this.mExpandingNotification && !this.mDisallowScrollingInThisMotion) {
            scrollerWantsIt = onScrollTouch(ev);
        }
        boolean horizontalSwipeWantsIt = false;
        if (!this.mIsBeingDragged && !this.mExpandingNotification && !this.mExpandedInThisMotion && !this.mOnlyScrollingInThisMotion && !this.mDisallowDismissInThisMotion) {
            horizontalSwipeWantsIt = this.mSwipeHelper.onTouchEvent(ev);
        }
        NotificationGuts guts = this.mNotificationGutsManager.getExposedGuts();
        if (guts != null && !NotificationSwipeHelper.isTouchInView(ev, guts) && (guts.getGutsContent() instanceof NotificationSnooze)) {
            NotificationSnooze ns = (NotificationSnooze) guts.getGutsContent();
            if ((ns.isExpanded() && isCancelOrUp) || (!horizontalSwipeWantsIt && scrollerWantsIt)) {
                checkSnoozeLeavebehind();
            }
        }
        if (ev.getActionMasked() == 1) {
            this.mCheckForLeavebehind = true;
        }
        return horizontalSwipeWantsIt || scrollerWantsIt || expandWantsIt || super.onTouchEvent(ev);
    }

    private void dispatchDownEventToScroller(MotionEvent ev) {
        MotionEvent downEvent = MotionEvent.obtain(ev);
        downEvent.setAction(0);
        onScrollTouch(downEvent);
        downEvent.recycle();
    }

    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (!isScrollingEnabled() || !this.mIsExpanded || this.mSwipingInProgress || this.mExpandingNotification || this.mDisallowScrollingInThisMotion) {
            return false;
        }
        if ((event.getSource() & 2) != 0 && event.getAction() == 8 && !this.mIsBeingDragged) {
            float vscroll = event.getAxisValue(9);
            if (vscroll != 0.0f) {
                int delta = (int) (getVerticalScrollFactor() * vscroll);
                int range = getScrollRange();
                int oldScrollY = this.mOwnScrollY;
                int newScrollY = oldScrollY - delta;
                if (newScrollY < 0) {
                    newScrollY = 0;
                } else if (newScrollY > range) {
                    newScrollY = range;
                }
                if (newScrollY != oldScrollY) {
                    setOwnScrollY(newScrollY);
                    return true;
                }
            }
        }
        return super.onGenericMotionEvent(event);
    }

    private boolean onScrollTouch(MotionEvent ev) {
        float scrollAmount;
        if (isScrollingEnabled()) {
            if (!isInsideQsContainer(ev) || this.mIsBeingDragged) {
                this.mForcedScroll = null;
                initVelocityTrackerIfNotExists();
                this.mVelocityTracker.addMovement(ev);
                int action = ev.getActionMasked();
                if (ev.findPointerIndex(this.mActivePointerId) == -1 && action != 0) {
                    Log.e(TAG, "Invalid pointerId=" + this.mActivePointerId + " in onTouchEvent " + MotionEvent.actionToString(ev.getActionMasked()));
                    return true;
                }
                if (action != 0) {
                    if (action != 1) {
                        if (action == 2) {
                            int activePointerIndex = ev.findPointerIndex(this.mActivePointerId);
                            if (activePointerIndex == -1) {
                                Log.e(TAG, "Invalid pointerId=" + this.mActivePointerId + " in onTouchEvent");
                            } else {
                                int y = (int) ev.getY(activePointerIndex);
                                int x = (int) ev.getX(activePointerIndex);
                                int deltaY = this.mLastMotionY - y;
                                int xDiff = Math.abs(x - this.mDownX);
                                int yDiff = Math.abs(deltaY);
                                if (!this.mIsBeingDragged && yDiff > this.mTouchSlop && yDiff > xDiff) {
                                    setIsBeingDragged(true);
                                    deltaY = deltaY > 0 ? deltaY - this.mTouchSlop : deltaY + this.mTouchSlop;
                                }
                                if (this.mIsBeingDragged) {
                                    this.mLastMotionY = y;
                                    int range = getScrollRange();
                                    if (this.mExpandedInThisMotion) {
                                        range = Math.min(range, this.mMaxScrollAfterExpand);
                                    }
                                    if (deltaY < 0) {
                                        scrollAmount = overScrollDown(deltaY);
                                    } else {
                                        scrollAmount = overScrollUp(deltaY, range);
                                    }
                                    if (scrollAmount != 0.0f) {
                                        customOverScrollBy((int) scrollAmount, this.mOwnScrollY, range, getHeight() / 2);
                                        checkSnoozeLeavebehind();
                                    }
                                }
                            }
                        } else if (action != 3) {
                            if (action == 5) {
                                int index = ev.getActionIndex();
                                this.mLastMotionY = (int) ev.getY(index);
                                this.mDownX = (int) ev.getX(index);
                                this.mActivePointerId = ev.getPointerId(index);
                            } else if (action == 6) {
                                onSecondaryPointerUp(ev);
                                this.mLastMotionY = (int) ev.getY(ev.findPointerIndex(this.mActivePointerId));
                                this.mDownX = (int) ev.getX(ev.findPointerIndex(this.mActivePointerId));
                            }
                        } else if (this.mIsBeingDragged && getChildCount() > 0) {
                            if (this.mScroller.springBack(this.mScrollX, this.mOwnScrollY, 0, 0, 0, getScrollRange())) {
                                lambda$new$1$NotificationStackScrollLayout();
                            }
                            this.mActivePointerId = -1;
                            endDrag();
                        }
                    } else if (this.mIsBeingDragged) {
                        VelocityTracker velocityTracker = this.mVelocityTracker;
                        velocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
                        int initialVelocity = (int) velocityTracker.getYVelocity(this.mActivePointerId);
                        if (shouldOverScrollFling(initialVelocity)) {
                            onOverScrollFling(true, initialVelocity);
                        } else if (getChildCount() > 0) {
                            if (Math.abs(initialVelocity) > this.mMinimumVelocity) {
                                float currentOverScrollTop = getCurrentOverScrollAmount(true);
                                if (currentOverScrollTop == 0.0f || initialVelocity > 0) {
                                    fling(-initialVelocity);
                                } else {
                                    onOverScrollFling(false, initialVelocity);
                                }
                            } else if (this.mScroller.springBack(this.mScrollX, this.mOwnScrollY, 0, 0, 0, getScrollRange())) {
                                lambda$new$1$NotificationStackScrollLayout();
                            }
                        }
                        this.mActivePointerId = -1;
                        endDrag();
                    }
                } else if (getChildCount() == 0 || !isInContentBounds(ev)) {
                    return false;
                } else {
                    boolean isBeingDragged = !this.mScroller.isFinished();
                    setIsBeingDragged(isBeingDragged);
                    if (!this.mScroller.isFinished()) {
                        this.mScroller.forceFinished(true);
                    }
                    this.mLastMotionY = (int) ev.getY();
                    this.mDownX = (int) ev.getX();
                    this.mActivePointerId = ev.getPointerId(0);
                }
                return true;
            }
            return false;
        }
        return false;
    }

    protected boolean isInsideQsContainer(MotionEvent ev) {
        return ev.getY() < ((float) this.mQsContainer.getBottom());
    }

    private void onOverScrollFling(boolean open, int initialVelocity) {
        OnOverscrollTopChangedListener onOverscrollTopChangedListener = this.mOverscrollTopChangedListener;
        if (onOverscrollTopChangedListener != null) {
            onOverscrollTopChangedListener.flingTopOverscroll(initialVelocity, open);
        }
        this.mDontReportNextOverScroll = true;
        setOverScrollAmount(0.0f, true, false);
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int pointerIndex = (ev.getAction() & 65280) >> 8;
        int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == this.mActivePointerId) {
            int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            this.mLastMotionY = (int) ev.getY(newPointerIndex);
            this.mActivePointerId = ev.getPointerId(newPointerIndex);
            VelocityTracker velocityTracker = this.mVelocityTracker;
            if (velocityTracker != null) {
                velocityTracker.clear();
            }
        }
    }

    private void endDrag() {
        setIsBeingDragged(false);
        recycleVelocityTracker();
        if (getCurrentOverScrollAmount(true) > 0.0f) {
            setOverScrollAmount(0.0f, true, true);
        }
        if (getCurrentOverScrollAmount(false) > 0.0f) {
            setOverScrollAmount(0.0f, false, true);
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        initDownStates(ev);
        handleEmptySpaceClick(ev);
        boolean expandWantsIt = false;
        boolean swipingInProgress = this.mSwipingInProgress;
        if (!swipingInProgress && !this.mOnlyScrollingInThisMotion) {
            expandWantsIt = this.mExpandHelper.onInterceptTouchEvent(ev);
        }
        boolean scrollWantsIt = false;
        if (!swipingInProgress && !this.mExpandingNotification) {
            scrollWantsIt = onInterceptTouchEventScroll(ev);
        }
        boolean swipeWantsIt = false;
        if (!this.mIsBeingDragged && !this.mExpandingNotification && !this.mExpandedInThisMotion && !this.mOnlyScrollingInThisMotion && !this.mDisallowDismissInThisMotion) {
            swipeWantsIt = this.mSwipeHelper.onInterceptTouchEvent(ev);
        }
        boolean isUp = ev.getActionMasked() == 1;
        NotificationGuts guts = this.mNotificationGutsManager.getExposedGuts();
        if (!NotificationSwipeHelper.isTouchInView(ev, guts) && isUp && !swipeWantsIt && !expandWantsIt && !scrollWantsIt) {
            this.mCheckForLeavebehind = false;
            this.mNotificationGutsManager.closeAndSaveGuts(true, false, false, -1, -1, false);
        }
        if (ev.getActionMasked() == 1) {
            this.mCheckForLeavebehind = true;
        }
        return swipeWantsIt || scrollWantsIt || expandWantsIt || super.onInterceptTouchEvent(ev);
    }

    private void handleEmptySpaceClick(MotionEvent ev) {
        int actionMasked = ev.getActionMasked();
        if (actionMasked != 1) {
            if (actionMasked == 2 && this.mTouchIsClick) {
                if (Math.abs(ev.getY() - this.mInitialTouchY) > this.mTouchSlop || Math.abs(ev.getX() - this.mInitialTouchX) > this.mTouchSlop) {
                    this.mTouchIsClick = false;
                }
            }
        } else if (this.mStatusBarState != 1 && this.mTouchIsClick && isBelowLastNotification(this.mInitialTouchX, this.mInitialTouchY)) {
            this.mOnEmptySpaceClickListener.onEmptySpaceClicked(this.mInitialTouchX, this.mInitialTouchY);
        }
    }

    private void initDownStates(MotionEvent ev) {
        if (ev.getAction() == 0) {
            this.mExpandedInThisMotion = false;
            this.mOnlyScrollingInThisMotion = !this.mScroller.isFinished();
            this.mDisallowScrollingInThisMotion = false;
            this.mDisallowDismissInThisMotion = false;
            this.mTouchIsClick = true;
            this.mInitialTouchX = ev.getX();
            this.mInitialTouchY = ev.getY();
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
        if (disallowIntercept) {
            cancelLongPress();
        }
    }

    private boolean onInterceptTouchEventScroll(MotionEvent ev) {
        if (isScrollingEnabled()) {
            int action = ev.getAction();
            if (action == 2 && this.mIsBeingDragged) {
                return true;
            }
            int i = action & 255;
            if (i == 0) {
                int y = (int) ev.getY();
                this.mScrolledToTopOnFirstDown = isScrolledToTop();
                if (getChildAtPosition(ev.getX(), y, false) == null) {
                    setIsBeingDragged(false);
                    recycleVelocityTracker();
                } else {
                    this.mLastMotionY = y;
                    this.mDownX = (int) ev.getX();
                    this.mActivePointerId = ev.getPointerId(0);
                    initOrResetVelocityTracker();
                    this.mVelocityTracker.addMovement(ev);
                    boolean isBeingDragged = !this.mScroller.isFinished();
                    setIsBeingDragged(isBeingDragged);
                }
            } else {
                if (i != 1) {
                    if (i == 2) {
                        int activePointerId = this.mActivePointerId;
                        if (activePointerId != -1) {
                            int pointerIndex = ev.findPointerIndex(activePointerId);
                            if (pointerIndex == -1) {
                                Log.e(TAG, "Invalid pointerId=" + activePointerId + " in onInterceptTouchEvent");
                            } else {
                                int y2 = (int) ev.getY(pointerIndex);
                                int x = (int) ev.getX(pointerIndex);
                                int yDiff = Math.abs(y2 - this.mLastMotionY);
                                int xDiff = Math.abs(x - this.mDownX);
                                if (yDiff > this.mTouchSlop && yDiff > xDiff) {
                                    setIsBeingDragged(true);
                                    this.mLastMotionY = y2;
                                    this.mDownX = x;
                                    initVelocityTrackerIfNotExists();
                                    this.mVelocityTracker.addMovement(ev);
                                }
                            }
                        }
                    } else if (i != 3) {
                        if (i == 6) {
                            onSecondaryPointerUp(ev);
                        }
                    }
                }
                setIsBeingDragged(false);
                this.mActivePointerId = -1;
                recycleVelocityTracker();
                if (this.mScroller.springBack(this.mScrollX, this.mOwnScrollY, 0, 0, 0, getScrollRange())) {
                    lambda$new$1$NotificationStackScrollLayout();
                }
            }
            boolean isBeingDragged2 = this.mIsBeingDragged;
            return isBeingDragged2;
        }
        return false;
    }

    private boolean isInContentBounds(MotionEvent event) {
        return isInContentBounds(event.getY());
    }

    @VisibleForTesting
    void setIsBeingDragged(boolean isDragged) {
        this.mIsBeingDragged = isDragged;
        if (isDragged) {
            requestDisallowInterceptTouchEvent(true);
            cancelLongPress();
            resetExposedMenuView(true, true);
        }
    }

    public void requestDisallowLongPress() {
        cancelLongPress();
    }

    public void requestDisallowDismiss() {
        this.mDisallowDismissInThisMotion = true;
    }

    @Override // android.view.View
    public void cancelLongPress() {
        this.mSwipeHelper.cancelLongPress();
    }

    public void setOnEmptySpaceClickListener(OnEmptySpaceClickListener listener) {
        this.mOnEmptySpaceClickListener = listener;
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x0021, code lost:
        if (r10 != 16908346) goto L16;
     */
    /* JADX WARN: Removed duplicated region for block: B:20:0x004f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean performAccessibilityActionInternal(int r10, android.os.Bundle r11) {
        /*
            r9 = this;
            boolean r0 = super.performAccessibilityActionInternal(r10, r11)
            r1 = 1
            if (r0 == 0) goto L8
            return r1
        L8:
            boolean r0 = r9.isEnabled()
            r2 = 0
            if (r0 != 0) goto L10
            return r2
        L10:
            r0 = -1
            r3 = 4096(0x1000, float:5.74E-42)
            if (r10 == r3) goto L24
            r3 = 8192(0x2000, float:1.14794E-41)
            if (r10 == r3) goto L25
            r3 = 16908344(0x1020038, float:2.3877386E-38)
            if (r10 == r3) goto L25
            r3 = 16908346(0x102003a, float:2.3877392E-38)
            if (r10 == r3) goto L24
            goto L5e
        L24:
            r0 = 1
        L25:
            int r3 = r9.getHeight()
            int r4 = r9.mPaddingBottom
            int r3 = r3 - r4
            int r4 = r9.mTopPadding
            int r3 = r3 - r4
            int r4 = r9.mPaddingTop
            int r3 = r3 - r4
            com.android.systemui.statusbar.NotificationShelf r4 = r9.mShelf
            int r4 = r4.getIntrinsicHeight()
            int r3 = r3 - r4
            int r4 = r9.mOwnScrollY
            int r5 = r0 * r3
            int r4 = r4 + r5
            int r5 = r9.getScrollRange()
            int r4 = java.lang.Math.min(r4, r5)
            int r4 = java.lang.Math.max(r2, r4)
            int r5 = r9.mOwnScrollY
            if (r4 == r5) goto L5e
            android.widget.OverScroller r5 = r9.mScroller
            int r6 = r9.mScrollX
            int r7 = r9.mOwnScrollY
            int r8 = r4 - r7
            r5.startScroll(r6, r7, r2, r8)
            r9.lambda$new$1$NotificationStackScrollLayout()
            return r1
        L5e:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout.performAccessibilityActionInternal(int, android.os.Bundle):boolean");
    }

    public void closeControlsIfOutsideTouch(MotionEvent ev) {
        NotificationGuts guts = this.mNotificationGutsManager.getExposedGuts();
        NotificationMenuRowPlugin menuRow = this.mSwipeHelper.getCurrentMenuRow();
        View translatingParentView = this.mSwipeHelper.getTranslatingParentView();
        View view = null;
        if (guts != null && !guts.getGutsContent().isLeavebehind()) {
            view = guts;
        } else if (menuRow != null && menuRow.isMenuVisible() && translatingParentView != null) {
            view = translatingParentView;
        }
        if (view != null && !NotificationSwipeHelper.isTouchInView(ev, view)) {
            this.mNotificationGutsManager.closeAndSaveGuts(false, false, true, -1, -1, false);
            resetExposedMenuView(true, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSwipingInProgress(boolean swiping) {
        this.mSwipingInProgress = swiping;
        if (swiping) {
            requestDisallowInterceptTouchEvent(true);
        }
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus) {
            cancelLongPress();
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void clearChildFocus(View child) {
        super.clearChildFocus(child);
        if (this.mForcedScroll == child) {
            this.mForcedScroll = null;
        }
    }

    @Override // com.android.systemui.statusbar.policy.ScrollAdapter
    public boolean isScrolledToTop() {
        return this.mOwnScrollY == 0;
    }

    @Override // com.android.systemui.statusbar.policy.ScrollAdapter
    public boolean isScrolledToBottom() {
        return this.mOwnScrollY >= getScrollRange();
    }

    @Override // com.android.systemui.statusbar.policy.ScrollAdapter
    public View getHostView() {
        return this;
    }

    public int getEmptyBottomMargin() {
        return Math.max(this.mMaxLayoutHeight - this.mContentHeight, 0);
    }

    public void checkSnoozeLeavebehind() {
        if (this.mCheckForLeavebehind) {
            this.mNotificationGutsManager.closeAndSaveGuts(true, false, false, -1, -1, false);
            this.mCheckForLeavebehind = false;
        }
    }

    public void resetCheckSnoozeLeavebehind() {
        this.mCheckForLeavebehind = true;
    }

    public void onExpansionStarted() {
        this.mIsExpansionChanging = true;
        this.mAmbientState.setExpansionChanging(true);
        checkSnoozeLeavebehind();
    }

    public void onExpansionStopped() {
        this.mIsExpansionChanging = false;
        resetCheckSnoozeLeavebehind();
        this.mAmbientState.setExpansionChanging(false);
        if (!this.mIsExpanded) {
            resetScrollPosition();
            this.mStatusBar.resetUserExpandedStates();
            clearTemporaryViews();
            clearUserLockedViews();
            ArrayList<ExpandableView> draggedViews = this.mAmbientState.getDraggedViews();
            if (draggedViews.size() > 0) {
                draggedViews.clear();
                updateContinuousShadowDrawing();
            }
        }
    }

    private void clearUserLockedViews() {
        for (int i = 0; i < getChildCount(); i++) {
            ExpandableView child = (ExpandableView) getChildAt(i);
            if (child instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow row = (ExpandableNotificationRow) child;
                row.setUserLocked(false);
            }
        }
    }

    private void clearTemporaryViews() {
        clearTemporaryViewsInGroup(this);
        for (int i = 0; i < getChildCount(); i++) {
            ExpandableView child = (ExpandableView) getChildAt(i);
            if (child instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow row = (ExpandableNotificationRow) child;
                clearTemporaryViewsInGroup(row.getChildrenContainer());
            }
        }
    }

    private void clearTemporaryViewsInGroup(ViewGroup viewGroup) {
        while (viewGroup != null && viewGroup.getTransientViewCount() != 0) {
            viewGroup.removeTransientView(viewGroup.getTransientView(0));
        }
    }

    public void onPanelTrackingStarted() {
        this.mPanelTracking = true;
        this.mAmbientState.setPanelTracking(true);
        resetExposedMenuView(true, true);
    }

    public void onPanelTrackingStopped() {
        this.mPanelTracking = false;
        this.mAmbientState.setPanelTracking(false);
    }

    public void resetScrollPosition() {
        this.mScroller.abortAnimation();
        setOwnScrollY(0);
    }

    private void setIsExpanded(boolean isExpanded) {
        boolean changed = isExpanded != this.mIsExpanded;
        this.mIsExpanded = isExpanded;
        this.mStackScrollAlgorithm.setIsExpanded(isExpanded);
        this.mAmbientState.setShadeExpanded(isExpanded);
        this.mStateAnimator.setShadeExpanded(isExpanded);
        this.mSwipeHelper.setIsExpanded(isExpanded);
        if (changed) {
            this.mWillExpand = false;
            if (!this.mIsExpanded) {
                this.mGroupManager.collapseAllGroups();
                this.mExpandHelper.cancelImmediately();
            }
            updateNotificationAnimationStates();
            updateChronometers();
            requestChildrenUpdate();
        }
    }

    private void updateChronometers() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            updateChronometerForChild(getChildAt(i));
        }
    }

    private void updateChronometerForChild(View child) {
        if (child instanceof ExpandableNotificationRow) {
            ExpandableNotificationRow row = (ExpandableNotificationRow) child;
            row.setChronometerRunning(this.mIsExpanded);
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView.OnHeightChangedListener
    public void onHeightChanged(ExpandableView view, boolean needsAnimation) {
        ExpandableNotificationRow row;
        updateContentHeight();
        updateScrollPositionOnExpandInBottom(view);
        clampScrollPosition();
        notifyHeightChangeListener(view, needsAnimation);
        if (view instanceof ExpandableNotificationRow) {
            row = (ExpandableNotificationRow) view;
        } else {
            row = null;
        }
        NotificationSection firstSection = getFirstVisibleSection();
        ActivatableNotificationView firstVisibleChild = firstSection != null ? firstSection.getFirstVisibleChild() : null;
        if (row != null && (row == firstVisibleChild || row.getNotificationParent() == firstVisibleChild)) {
            updateAlgorithmLayoutMinHeight();
        }
        if (needsAnimation) {
            requestAnimationOnViewResize(row);
        }
        requestChildrenUpdate();
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView.OnHeightChangedListener
    public void onReset(ExpandableView view) {
        updateAnimationState(view);
        updateChronometerForChild(view);
    }

    private void updateScrollPositionOnExpandInBottom(ExpandableView view) {
        if ((view instanceof ExpandableNotificationRow) && !onKeyguard()) {
            ExpandableNotificationRow row = (ExpandableNotificationRow) view;
            if (!row.isUserLocked() || row == getFirstChildNotGone() || row.isSummaryWithChildren()) {
                return;
            }
            float endPosition = row.getTranslationY() + row.getActualHeight();
            if (row.isChildInGroup()) {
                endPosition += row.getNotificationParent().getTranslationY();
            }
            int layoutEnd = this.mMaxLayoutHeight + ((int) this.mStackTranslation);
            NotificationSection lastSection = getLastVisibleSection();
            ActivatableNotificationView lastVisibleChild = lastSection == null ? null : lastSection.getLastVisibleChild();
            if (row != lastVisibleChild && this.mShelf.getVisibility() != 8) {
                layoutEnd -= this.mShelf.getIntrinsicHeight() + this.mPaddingBetweenElements;
            }
            if (endPosition > layoutEnd) {
                setOwnScrollY((int) ((this.mOwnScrollY + endPosition) - layoutEnd));
                this.mDisallowScrollingInThisMotion = true;
            }
        }
    }

    public void setOnHeightChangedListener(ExpandableView.OnHeightChangedListener onHeightChangedListener) {
        this.mOnHeightChangedListener = onHeightChangedListener;
    }

    public void onChildAnimationFinished() {
        setAnimationRunning(false);
        requestChildrenUpdate();
        runAnimationFinishedRunnables();
        clearTransient();
        clearHeadsUpDisappearRunning();
    }

    private void clearHeadsUpDisappearRunning() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow row = (ExpandableNotificationRow) view;
                row.setHeadsUpAnimatingAway(false);
                if (row.isSummaryWithChildren()) {
                    for (ExpandableNotificationRow child : row.getNotificationChildren()) {
                        child.setHeadsUpAnimatingAway(false);
                    }
                }
            }
        }
    }

    private void clearTransient() {
        Iterator<ExpandableView> it = this.mClearTransientViewsWhenFinished.iterator();
        while (it.hasNext()) {
            ExpandableView view = it.next();
            StackStateAnimator.removeTransientView(view);
        }
        this.mClearTransientViewsWhenFinished.clear();
    }

    private void runAnimationFinishedRunnables() {
        Iterator<Runnable> it = this.mAnimationFinishedRunnables.iterator();
        while (it.hasNext()) {
            Runnable runnable = it.next();
            runnable.run();
        }
        this.mAnimationFinishedRunnables.clear();
    }

    public void setDimmed(boolean dimmed, boolean animate) {
        boolean dimmed2 = dimmed & onKeyguard();
        this.mAmbientState.setDimmed(dimmed2);
        if (animate && this.mAnimationsEnabled) {
            this.mDimmedNeedsAnimation = true;
            this.mNeedsAnimation = true;
            animateDimmed(dimmed2);
        } else {
            setDimAmount(dimmed2 ? 1.0f : 0.0f);
        }
        requestChildrenUpdate();
    }

    @VisibleForTesting
    boolean isDimmed() {
        return this.mAmbientState.isDimmed();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDimAmount(float dimAmount) {
        this.mDimAmount = dimAmount;
        updateBackgroundDimming();
    }

    private void animateDimmed(boolean dimmed) {
        ValueAnimator valueAnimator = this.mDimAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        float target = dimmed ? 1.0f : 0.0f;
        float f = this.mDimAmount;
        if (target == f) {
            return;
        }
        this.mDimAnimator = TimeAnimator.ofFloat(f, target);
        this.mDimAnimator.setDuration(220L);
        this.mDimAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        this.mDimAnimator.addListener(this.mDimEndListener);
        this.mDimAnimator.addUpdateListener(this.mDimUpdateListener);
        this.mDimAnimator.start();
    }

    private void updateSensitiveness(boolean animate) {
        boolean hideSensitive = this.mLockscreenUserManager.isAnyProfilePublicMode();
        if (hideSensitive != this.mAmbientState.isHideSensitive()) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                ExpandableView v = (ExpandableView) getChildAt(i);
                v.setHideSensitiveForIntrinsicHeight(hideSensitive);
            }
            this.mAmbientState.setHideSensitive(hideSensitive);
            if (animate && this.mAnimationsEnabled) {
                this.mHideSensitiveNeedsAnimation = true;
                this.mNeedsAnimation = true;
            }
            updateContentHeight();
            requestChildrenUpdate();
        }
    }

    public void setActivatedChild(ActivatableNotificationView activatedChild) {
        this.mAmbientState.setActivatedChild(activatedChild);
        if (this.mAnimationsEnabled) {
            this.mActivateNeedsAnimation = true;
            this.mNeedsAnimation = true;
        }
        requestChildrenUpdate();
    }

    public ActivatableNotificationView getActivatedChild() {
        return this.mAmbientState.getActivatedChild();
    }

    private void applyCurrentState() {
        int numChildren = getChildCount();
        for (int i = 0; i < numChildren; i++) {
            ExpandableView child = (ExpandableView) getChildAt(i);
            child.applyViewState();
        }
        NotificationLogger.OnChildLocationsChangedListener onChildLocationsChangedListener = this.mListener;
        if (onChildLocationsChangedListener != null) {
            onChildLocationsChangedListener.onChildLocationsChanged();
        }
        runAnimationFinishedRunnables();
        setAnimationRunning(false);
        updateBackground();
        updateViewShadows();
        updateClippingToTopRoundedCorner();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateViewShadows() {
        for (int i = 0; i < getChildCount(); i++) {
            ExpandableView child = (ExpandableView) getChildAt(i);
            if (child.getVisibility() != 8) {
                this.mTmpSortedChildren.add(child);
            }
        }
        Collections.sort(this.mTmpSortedChildren, this.mViewPositionComparator);
        ExpandableView previous = null;
        for (int i2 = 0; i2 < this.mTmpSortedChildren.size(); i2++) {
            ExpandableView expandableView = this.mTmpSortedChildren.get(i2);
            float translationZ = expandableView.getTranslationZ();
            float otherZ = previous == null ? translationZ : previous.getTranslationZ();
            float diff = otherZ - translationZ;
            if (diff <= 0.0f || diff >= 0.1f) {
                expandableView.setFakeShadowIntensity(0.0f, 0.0f, 0, 0);
            } else {
                float yLocation = ((previous.getTranslationY() + previous.getActualHeight()) - expandableView.getTranslationY()) - previous.getExtraBottomPadding();
                expandableView.setFakeShadowIntensity(diff / 0.1f, previous.getOutlineAlpha(), (int) yLocation, previous.getOutlineTranslation());
            }
            previous = expandableView;
        }
        this.mTmpSortedChildren.clear();
    }

    public void updateDecorViews(boolean lightTheme) {
        if (lightTheme == this.mUsingLightTheme) {
            return;
        }
        this.mUsingLightTheme = lightTheme;
        Context context = new ContextThemeWrapper(this.mContext, lightTheme ? R.style.Theme_SystemUI_Light : R.style.Theme_SystemUI);
        int textColor = Utils.getColorAttrDefaultColor(context, R.attr.wallpaperTextColor);
        this.mFooterView.setTextColor(textColor);
        this.mEmptyShadeView.setTextColor(textColor);
    }

    public void goToFullShade(long delay) {
        this.mGoToFullShadeNeedsAnimation = true;
        this.mGoToFullShadeDelay = delay;
        this.mNeedsAnimation = true;
        requestChildrenUpdate();
    }

    public void cancelExpandHelper() {
        this.mExpandHelper.cancel();
    }

    public void setIntrinsicPadding(int intrinsicPadding) {
        this.mIntrinsicPadding = intrinsicPadding;
        this.mAmbientState.setIntrinsicPadding(intrinsicPadding);
    }

    public int getIntrinsicPadding() {
        return this.mIntrinsicPadding;
    }

    @Override // android.view.ViewGroup
    public boolean shouldDelayChildPressedState() {
        return true;
    }

    public void setDozing(boolean dozing, boolean animate, PointF touchWakeUpScreenLocation) {
        if (this.mAmbientState.isDozing() == dozing) {
            return;
        }
        this.mAmbientState.setDozing(dozing);
        requestChildrenUpdate();
        notifyHeightChangeListener(this.mShelf);
    }

    public void setHideAmount(float linearHideAmount, float interpolatedHideAmount) {
        this.mLinearHideAmount = linearHideAmount;
        this.mInterpolatedHideAmount = interpolatedHideAmount;
        boolean wasFullyHidden = this.mAmbientState.isFullyHidden();
        boolean wasHiddenAtAll = this.mAmbientState.isHiddenAtAll();
        this.mAmbientState.setHideAmount(interpolatedHideAmount);
        boolean nowFullyHidden = this.mAmbientState.isFullyHidden();
        boolean nowHiddenAtAll = this.mAmbientState.isHiddenAtAll();
        if (nowFullyHidden != wasFullyHidden) {
            updateVisibility();
        }
        if (!wasHiddenAtAll && nowHiddenAtAll) {
            resetExposedMenuView(true, true);
        }
        if (nowFullyHidden != wasFullyHidden || wasHiddenAtAll != nowHiddenAtAll) {
            invalidateOutline();
        }
        updateAlgorithmHeightAndPadding();
        updateBackgroundDimming();
        requestChildrenUpdate();
        updateOwnTranslationZ();
    }

    private void updateOwnTranslationZ() {
        ExpandableView firstChildNotGone;
        float ownTranslationZ = 0.0f;
        if (this.mKeyguardBypassController.getBypassEnabled() && this.mAmbientState.isHiddenAtAll() && (firstChildNotGone = getFirstChildNotGone()) != null && firstChildNotGone.showingPulsing()) {
            ownTranslationZ = firstChildNotGone.getTranslationZ();
        }
        setTranslationZ(ownTranslationZ);
    }

    private void updateVisibility() {
        boolean shouldShow = (this.mAmbientState.isFullyHidden() && onKeyguard()) ? false : true;
        setVisibility(shouldShow ? 0 : 4);
    }

    public void notifyHideAnimationStart(boolean hide) {
        Interpolator interpolator;
        float f = this.mInterpolatedHideAmount;
        if (f == 0.0f || f == 1.0f) {
            this.mBackgroundXFactor = hide ? 1.8f : 1.5f;
            if (hide) {
                interpolator = Interpolators.FAST_OUT_SLOW_IN_REVERSE;
            } else {
                interpolator = Interpolators.FAST_OUT_SLOW_IN;
            }
            this.mHideXInterpolator = interpolator;
        }
    }

    private int getNotGoneIndex(View child) {
        int count = getChildCount();
        int notGoneIndex = 0;
        for (int i = 0; i < count; i++) {
            View v = getChildAt(i);
            if (child == v) {
                return notGoneIndex;
            }
            if (v.getVisibility() != 8) {
                notGoneIndex++;
            }
        }
        return -1;
    }

    public void setFooterView(FooterView footerView) {
        int index = -1;
        FooterView footerView2 = this.mFooterView;
        if (footerView2 != null) {
            index = indexOfChild(footerView2);
            removeView(this.mFooterView);
        }
        this.mFooterView = footerView;
        addView(this.mFooterView, index);
    }

    public void setEmptyShadeView(EmptyShadeView emptyShadeView) {
        int index = -1;
        EmptyShadeView emptyShadeView2 = this.mEmptyShadeView;
        if (emptyShadeView2 != null) {
            index = indexOfChild(emptyShadeView2);
            removeView(this.mEmptyShadeView);
        }
        this.mEmptyShadeView = emptyShadeView;
        addView(this.mEmptyShadeView, index);
    }

    public void updateEmptyShadeView(boolean visible) {
        this.mEmptyShadeView.setVisible(visible, this.mIsExpanded && this.mAnimationsEnabled);
        int oldTextRes = this.mEmptyShadeView.getTextResource();
        int newTextRes = this.mStatusBar.areNotificationsHidden() ? R.string.dnd_suppressing_shade_text : R.string.empty_shade_text;
        if (oldTextRes != newTextRes) {
            this.mEmptyShadeView.setText(newTextRes);
        }
    }

    public void updateFooterView(boolean visible, boolean showDismissView) {
        if (this.mFooterView == null) {
            return;
        }
        boolean animate = this.mIsExpanded && this.mAnimationsEnabled;
        this.mFooterView.setVisible(visible, animate);
        this.mFooterView.setSecondaryVisible(showDismissView, animate);
    }

    public void setDismissAllInProgress(boolean dismissAllInProgress) {
        this.mDismissAllInProgress = dismissAllInProgress;
        this.mAmbientState.setDismissAllInProgress(dismissAllInProgress);
        handleDismissAllClipping();
    }

    private void handleDismissAllClipping() {
        int count = getChildCount();
        boolean previousChildWillBeDismissed = false;
        for (int i = 0; i < count; i++) {
            ExpandableView child = (ExpandableView) getChildAt(i);
            if (child.getVisibility() != 8) {
                if (this.mDismissAllInProgress && previousChildWillBeDismissed) {
                    child.setMinClipTopAmount(child.getClipTopAmount());
                } else {
                    child.setMinClipTopAmount(0);
                }
                previousChildWillBeDismissed = StackScrollAlgorithm.canChildBeDismissed(child);
            }
        }
    }

    public boolean isFooterViewNotGone() {
        FooterView footerView = this.mFooterView;
        return (footerView == null || footerView.getVisibility() == 8 || this.mFooterView.willBeGone()) ? false : true;
    }

    public boolean isFooterViewContentVisible() {
        FooterView footerView = this.mFooterView;
        return footerView != null && footerView.isContentVisible();
    }

    public int getFooterViewHeight() {
        FooterView footerView = this.mFooterView;
        if (footerView == null) {
            return 0;
        }
        return footerView.getHeight() + this.mPaddingBetweenElements;
    }

    public int getEmptyShadeViewHeight() {
        return this.mEmptyShadeView.getHeight();
    }

    public float getBottomMostNotificationBottom() {
        int count = getChildCount();
        float max = 0.0f;
        for (int childIdx = 0; childIdx < count; childIdx++) {
            ExpandableView child = (ExpandableView) getChildAt(childIdx);
            if (child.getVisibility() != 8) {
                float bottom = (child.getTranslationY() + child.getActualHeight()) - child.getClipBottomAmount();
                if (bottom > max) {
                    max = bottom;
                }
            }
        }
        return getStackTranslation() + max;
    }

    public void setStatusBar(StatusBar statusBar) {
        this.mStatusBar = statusBar;
    }

    public void setGroupManager(NotificationGroupManager groupManager) {
        this.mGroupManager = groupManager;
        this.mGroupManager.addOnGroupChangeListener(this.mOnGroupChangeListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestAnimateEverything() {
        if (this.mIsExpanded && this.mAnimationsEnabled) {
            this.mEverythingNeedsAnimation = true;
            this.mNeedsAnimation = true;
            requestChildrenUpdate();
        }
    }

    public boolean isBelowLastNotification(float touchX, float touchY) {
        int childCount = getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            ExpandableView child = (ExpandableView) getChildAt(i);
            if (child.getVisibility() != 8) {
                float childTop = child.getY();
                if (childTop > touchY) {
                    return false;
                }
                boolean belowChild = touchY > (((float) child.getActualHeight()) + childTop) - ((float) child.getClipBottomAmount());
                FooterView footerView = this.mFooterView;
                if (child == footerView) {
                    if (!belowChild && !footerView.isOnEmptySpace(touchX - footerView.getX(), touchY - childTop)) {
                        return false;
                    }
                } else if (child == this.mEmptyShadeView) {
                    return true;
                } else {
                    if (!belowChild) {
                        return false;
                    }
                }
            }
        }
        int i2 = this.mTopPadding;
        return touchY > ((float) i2) + this.mStackTranslation;
    }

    public void onInitializeAccessibilityEventInternal(AccessibilityEvent event) {
        super.onInitializeAccessibilityEventInternal(event);
        event.setScrollable(this.mScrollable);
        event.setScrollX(this.mScrollX);
        event.setMaxScrollX(this.mScrollX);
        event.setScrollY(this.mOwnScrollY);
        event.setMaxScrollY(getScrollRange());
    }

    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfoInternal(info);
        if (this.mScrollable) {
            info.setScrollable(true);
            if (this.mBackwardScrollable) {
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD);
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_UP);
            }
            if (this.mForwardScrollable) {
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_DOWN);
            }
        }
        info.setClassName(ScrollView.class.getName());
    }

    @Override // com.android.systemui.statusbar.notification.stack.NotificationListContainer
    public void generateChildOrderChangedEvent() {
        if (this.mIsExpanded && this.mAnimationsEnabled) {
            this.mGenerateChildOrderChangedEvent = true;
            this.mNeedsAnimation = true;
            requestChildrenUpdate();
        }
    }

    @Override // com.android.systemui.statusbar.notification.stack.NotificationListContainer
    public int getContainerChildCount() {
        return getChildCount();
    }

    @Override // com.android.systemui.statusbar.notification.stack.NotificationListContainer
    public View getContainerChildAt(int i) {
        return getChildAt(i);
    }

    @Override // com.android.systemui.statusbar.notification.stack.NotificationListContainer
    public void removeContainerView(View v) {
        Assert.isMainThread();
        removeView(v);
    }

    @Override // com.android.systemui.statusbar.notification.stack.NotificationListContainer
    public void addContainerView(View v) {
        Assert.isMainThread();
        addView(v);
    }

    public void runAfterAnimationFinished(Runnable runnable) {
        this.mAnimationFinishedRunnables.add(runnable);
    }

    public void generateHeadsUpAnimation(NotificationEntry entry, boolean isHeadsUp) {
        ExpandableNotificationRow row = entry.getHeadsUpAnimationView();
        generateHeadsUpAnimation(row, isHeadsUp);
    }

    public void generateHeadsUpAnimation(ExpandableNotificationRow row, boolean isHeadsUp) {
        if (this.mAnimationsEnabled) {
            if (isHeadsUp || this.mHeadsUpGoingAwayAnimationsAllowed) {
                this.mHeadsUpChangeAnimations.add(new Pair<>(row, Boolean.valueOf(isHeadsUp)));
                this.mNeedsAnimation = true;
                if (!this.mIsExpanded && !this.mWillExpand && !isHeadsUp) {
                    row.setHeadsUpAnimatingAway(true);
                }
                requestChildrenUpdate();
            }
        }
    }

    public void setHeadsUpBoundaries(int height, int bottomBarHeight) {
        this.mAmbientState.setMaxHeadsUpTranslation(height - bottomBarHeight);
        this.mStateAnimator.setHeadsUpAppearHeightBottom(height);
        requestChildrenUpdate();
    }

    @Override // com.android.systemui.statusbar.notification.stack.NotificationListContainer
    public void setWillExpand(boolean willExpand) {
        this.mWillExpand = willExpand;
    }

    public void setTrackingHeadsUp(ExpandableNotificationRow row) {
        this.mTrackingHeadsUp = row != null;
        this.mRoundnessManager.setTrackingHeadsUp(row);
    }

    public void setScrimController(ScrimController scrimController) {
        this.mScrimController = scrimController;
        this.mScrimController.setScrimBehindChangeRunnable(new Runnable() { // from class: com.android.systemui.statusbar.notification.stack.-$$Lambda$NotificationStackScrollLayout$EebmavE8B0v9pYEId75j8vvZNvI
            @Override // java.lang.Runnable
            public final void run() {
                NotificationStackScrollLayout.this.updateBackgroundDimming();
            }
        });
    }

    public void forceNoOverlappingRendering(boolean force) {
        this.mForceNoOverlappingRendering = force;
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return !this.mForceNoOverlappingRendering && super.hasOverlappingRendering();
    }

    public void setAnimationRunning(boolean animationRunning) {
        if (animationRunning != this.mAnimationRunning) {
            if (animationRunning) {
                getViewTreeObserver().addOnPreDrawListener(this.mRunningAnimationUpdater);
            } else {
                getViewTreeObserver().removeOnPreDrawListener(this.mRunningAnimationUpdater);
            }
            this.mAnimationRunning = animationRunning;
            updateContinuousShadowDrawing();
        }
    }

    public boolean isExpanded() {
        return this.mIsExpanded;
    }

    public void setPulsing(boolean pulsing, boolean animated) {
        if (!this.mPulsing && !pulsing) {
            return;
        }
        this.mPulsing = pulsing;
        updateClipping();
        this.mAmbientState.setPulsing(pulsing);
        this.mSwipeHelper.setPulsing(pulsing);
        updateNotificationAnimationStates();
        updateAlgorithmHeightAndPadding();
        updateContentHeight();
        requestChildrenUpdate();
        notifyHeightChangeListener(null, animated);
    }

    public void setQsExpanded(boolean qsExpanded) {
        this.mQsExpanded = qsExpanded;
        updateAlgorithmLayoutMinHeight();
        updateScrollability();
    }

    public void setQsExpansionFraction(float qsExpansionFraction) {
        this.mQsExpansionFraction = qsExpansionFraction;
    }

    private void setOwnScrollY(int ownScrollY) {
        if (ownScrollY != this.mOwnScrollY) {
            onScrollChanged(this.mScrollX, ownScrollY, this.mScrollX, this.mOwnScrollY);
            this.mOwnScrollY = ownScrollY;
            updateOnScrollChange();
        }
    }

    private void updateOnScrollChange() {
        updateForwardAndBackwardScrollability();
        requestChildrenUpdate();
    }

    private void updateScrollAnchor() {
        int anchorIndex = indexOfChild(this.mScrollAnchorView);
        while (this.mScrollAnchorViewY < 0) {
            View nextAnchor = null;
            int i = anchorIndex + 1;
            while (true) {
                if (i >= getChildCount()) {
                    break;
                }
                View child = getChildAt(i);
                if (child.getVisibility() == 8 || !(child instanceof ExpandableNotificationRow)) {
                    i++;
                } else {
                    anchorIndex = i;
                    nextAnchor = child;
                    break;
                }
            }
            if (nextAnchor == null) {
                break;
            }
            this.mScrollAnchorViewY += (int) (nextAnchor.getTranslationY() - this.mScrollAnchorView.getTranslationY());
            this.mScrollAnchorView = nextAnchor;
        }
        while (anchorIndex > 0 && this.mScrollAnchorViewY > 0) {
            View prevAnchor = null;
            int i2 = anchorIndex - 1;
            while (true) {
                if (i2 < 0) {
                    break;
                }
                View child2 = getChildAt(i2);
                if (child2.getVisibility() == 8 || !(child2 instanceof ExpandableNotificationRow)) {
                    i2--;
                } else {
                    anchorIndex = i2;
                    prevAnchor = child2;
                    break;
                }
            }
            if (prevAnchor != null) {
                float distanceToPreviousAnchor = this.mScrollAnchorView.getTranslationY() - prevAnchor.getTranslationY();
                int i3 = this.mScrollAnchorViewY;
                if (distanceToPreviousAnchor < i3) {
                    this.mScrollAnchorViewY = i3 - ((int) distanceToPreviousAnchor);
                    this.mScrollAnchorView = prevAnchor;
                }
            } else {
                return;
            }
        }
    }

    public void setShelf(NotificationShelf shelf) {
        int index = -1;
        NotificationShelf notificationShelf = this.mShelf;
        if (notificationShelf != null) {
            index = indexOfChild(notificationShelf);
            removeView(this.mShelf);
        }
        this.mShelf = shelf;
        addView(this.mShelf, index);
        this.mAmbientState.setShelf(shelf);
        this.mStateAnimator.setShelf(shelf);
        shelf.bind(this.mAmbientState, this);
    }

    public NotificationShelf getNotificationShelf() {
        return this.mShelf;
    }

    @Override // com.android.systemui.statusbar.notification.stack.NotificationListContainer
    public void setMaxDisplayedNotifications(int maxDisplayedNotifications) {
        if (this.mMaxDisplayedNotifications != maxDisplayedNotifications) {
            this.mMaxDisplayedNotifications = maxDisplayedNotifications;
            updateContentHeight();
            notifyHeightChangeListener(this.mShelf);
        }
    }

    public void setShouldShowShelfOnly(boolean shouldShowShelfOnly) {
        this.mShouldShowShelfOnly = shouldShowShelfOnly;
        updateAlgorithmLayoutMinHeight();
    }

    public int getMinExpansionHeight() {
        return this.mShelf.getIntrinsicHeight() - ((this.mShelf.getIntrinsicHeight() - this.mStatusBarHeight) / 2);
    }

    public void setInHeadsUpPinnedMode(boolean inHeadsUpPinnedMode) {
        this.mInHeadsUpPinnedMode = inHeadsUpPinnedMode;
        updateClipping();
    }

    public void setHeadsUpAnimatingAway(boolean headsUpAnimatingAway) {
        this.mHeadsUpAnimatingAway = headsUpAnimatingAway;
        updateClipping();
    }

    @VisibleForTesting
    protected void setStatusBarState(int statusBarState) {
        this.mStatusBarState = statusBarState;
        this.mAmbientState.setStatusBarState(statusBarState);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onStatePostChange() {
        boolean onKeyguard = onKeyguard();
        this.mLockscreenUserManager.isAnyProfilePublicMode();
        HeadsUpAppearanceController headsUpAppearanceController = this.mHeadsUpAppearanceController;
        if (headsUpAppearanceController != null) {
            headsUpAppearanceController.onStateChanged();
        }
        SysuiStatusBarStateController state = (SysuiStatusBarStateController) Dependency.get(StatusBarStateController.class);
        updateSensitiveness(state.goingToFullShade());
        setDimmed(onKeyguard, state.fromShadeLocked());
        setExpandingEnabled(!onKeyguard);
        ActivatableNotificationView activatedChild = getActivatedChild();
        setActivatedChild(null);
        if (activatedChild != null) {
            activatedChild.makeInactive(false);
        }
        updateFooter();
        requestChildrenUpdate();
        onUpdateRowStates();
        this.mEntryManager.updateNotifications();
        updateVisibility();
    }

    public void setExpandingVelocity(float expandingVelocity) {
        this.mAmbientState.setExpandingVelocity(expandingVelocity);
    }

    public float getOpeningHeight() {
        if (this.mEmptyShadeView.getVisibility() == 8) {
            return getMinExpansionHeight();
        }
        return getAppearEndPosition();
    }

    public void setIsFullWidth(boolean isFullWidth) {
        this.mAmbientState.setPanelFullWidth(isFullWidth);
    }

    public void setUnlockHintRunning(boolean running) {
        this.mAmbientState.setUnlockHintRunning(running);
    }

    public void setQsCustomizerShowing(boolean isShowing) {
        this.mAmbientState.setQsCustomizerShowing(isShowing);
        requestChildrenUpdate();
    }

    public void setHeadsUpGoingAwayAnimationsAllowed(boolean headsUpGoingAwayAnimationsAllowed) {
        this.mHeadsUpGoingAwayAnimationsAllowed = headsUpGoingAwayAnimationsAllowed;
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        String str;
        Object[] objArr = new Object[9];
        objArr[0] = getClass().getSimpleName();
        objArr[1] = this.mPulsing ? "T" : "f";
        objArr[2] = this.mAmbientState.isQsCustomizerShowing() ? "T" : "f";
        if (getVisibility() == 0) {
            str = "visible";
        } else {
            str = getVisibility() == 8 ? "gone" : "invisible";
        }
        objArr[3] = str;
        objArr[4] = Float.valueOf(getAlpha());
        objArr[5] = Integer.valueOf(this.mAmbientState.getScrollY());
        objArr[6] = Integer.valueOf(this.mMaxTopPadding);
        objArr[7] = this.mShouldShowShelfOnly ? "T" : "f";
        objArr[8] = Float.valueOf(this.mQsExpansionFraction);
        pw.println(String.format("[%s: pulsing=%s qsCustomizerShowing=%s visibility=%s alpha:%f scrollY:%d maxTopPadding:%d showShelfOnly=%s qsExpandFraction=%f]", objArr));
        int childCount = getChildCount();
        pw.println("  Number of children: " + childCount);
        pw.println();
        for (int i = 0; i < childCount; i++) {
            ExpandableView child = (ExpandableView) getChildAt(i);
            child.dump(fd, pw, args);
            if (!(child instanceof ExpandableNotificationRow)) {
                pw.println("  " + child.getClass().getSimpleName());
                ExpandableViewState viewState = child.getViewState();
                if (viewState == null) {
                    pw.println("    no viewState!!!");
                } else {
                    pw.print("    ");
                    viewState.dump(fd, pw, args);
                    pw.println();
                    pw.println();
                }
            }
        }
        int transientViewCount = getTransientViewCount();
        pw.println("  Transient Views: " + transientViewCount);
        for (int i2 = 0; i2 < transientViewCount; i2++) {
            ExpandableView child2 = (ExpandableView) getTransientView(i2);
            child2.dump(fd, pw, args);
        }
        ArrayList<ExpandableView> draggedViews = this.mAmbientState.getDraggedViews();
        int draggedCount = draggedViews.size();
        pw.println("  Dragged Views: " + draggedCount);
        for (int i3 = 0; i3 < draggedCount; i3++) {
            ExpandableView child3 = draggedViews.get(i3);
            child3.dump(fd, pw, args);
        }
    }

    public boolean isFullyHidden() {
        return this.mAmbientState.isFullyHidden();
    }

    public void addOnExpandedHeightChangedListener(BiConsumer<Float, Float> listener) {
        this.mExpandedHeightListeners.add(listener);
    }

    public void removeOnExpandedHeightChangedListener(BiConsumer<Float, Float> listener) {
        this.mExpandedHeightListeners.remove(listener);
    }

    public void setHeadsUpAppearanceController(HeadsUpAppearanceController headsUpAppearanceController) {
        this.mHeadsUpAppearanceController = headsUpAppearanceController;
    }

    public void setIconAreaController(NotificationIconAreaController controller) {
        this.mIconAreaController = controller;
    }

    public void manageNotifications(View v) {
        Intent intent = new Intent("android.settings.ALL_APPS_NOTIFICATION_SETTINGS");
        this.mStatusBar.startActivity(intent, true, true, 536870912);
    }

    private void clearNotifications(final int selection, boolean closeShade) {
        int numChildren = getChildCount();
        ArrayList<View> viewsToHide = new ArrayList<>(numChildren);
        final ArrayList<ExpandableNotificationRow> viewsToRemove = new ArrayList<>(numChildren);
        for (int i = 0; i < numChildren; i++) {
            View child = getChildAt(i);
            if (child instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow row = (ExpandableNotificationRow) child;
                boolean parentVisible = false;
                boolean hasClipBounds = child.getClipBounds(this.mTmpRect);
                if (includeChildInDismissAll(row, selection)) {
                    viewsToRemove.add(row);
                    if (child.getVisibility() == 0 && (!hasClipBounds || this.mTmpRect.height() > 0)) {
                        viewsToHide.add(child);
                        parentVisible = true;
                    }
                } else if (child.getVisibility() == 0 && (!hasClipBounds || this.mTmpRect.height() > 0)) {
                    parentVisible = true;
                }
                List<ExpandableNotificationRow> children = row.getNotificationChildren();
                if (children != null) {
                    for (ExpandableNotificationRow childRow : children) {
                        if (includeChildInDismissAll(row, selection)) {
                            viewsToRemove.add(childRow);
                            if (parentVisible && row.areChildrenExpanded()) {
                                boolean hasClipBounds2 = childRow.getClipBounds(this.mTmpRect);
                                if (childRow.getVisibility() == 0 && (!hasClipBounds2 || this.mTmpRect.height() > 0)) {
                                    viewsToHide.add(childRow);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (viewsToRemove.isEmpty()) {
            if (closeShade) {
                this.mStatusBar.animateCollapsePanels(0);
                return;
            }
            return;
        }
        performDismissAllAnimations(viewsToHide, closeShade, new Runnable() { // from class: com.android.systemui.statusbar.notification.stack.-$$Lambda$NotificationStackScrollLayout$BKZ0Bh5XLVtMnQqHjBJSLhE_Z2M
            @Override // java.lang.Runnable
            public final void run() {
                NotificationStackScrollLayout.this.lambda$clearNotifications$7$NotificationStackScrollLayout(viewsToRemove, selection);
            }
        });
    }

    public /* synthetic */ void lambda$clearNotifications$7$NotificationStackScrollLayout(ArrayList viewsToRemove, int selection) {
        Iterator it = viewsToRemove.iterator();
        while (it.hasNext()) {
            ExpandableNotificationRow rowToRemove = (ExpandableNotificationRow) it.next();
            if (!StackScrollAlgorithm.canChildBeDismissed(rowToRemove)) {
                rowToRemove.resetTranslation();
            } else if (selection == 0) {
                this.mEntryManager.removeNotification(rowToRemove.getEntry().key, null, 3);
            } else {
                this.mEntryManager.performRemoveNotification(rowToRemove.getEntry().notification, 3);
            }
        }
        if (selection == 0) {
            try {
                this.mBarService.onClearAllNotifications(this.mLockscreenUserManager.getCurrentUserId());
            } catch (Exception e) {
            }
        }
    }

    private boolean includeChildInDismissAll(ExpandableNotificationRow row, int selection) {
        return StackScrollAlgorithm.canChildBeDismissed(row) && matchesSelection(row, selection);
    }

    private void performDismissAllAnimations(ArrayList<View> hideAnimatedList, final boolean closeShade, final Runnable onAnimationComplete) {
        Runnable onSlideAwayAnimationComplete = new Runnable() { // from class: com.android.systemui.statusbar.notification.stack.-$$Lambda$NotificationStackScrollLayout$NJnpZAF3yxT4RBuBz44A-eIeHrg
            @Override // java.lang.Runnable
            public final void run() {
                NotificationStackScrollLayout.this.lambda$performDismissAllAnimations$9$NotificationStackScrollLayout(closeShade, onAnimationComplete);
            }
        };
        if (!hideAnimatedList.isEmpty()) {
            setDismissAllInProgress(true);
            int numItems = hideAnimatedList.size();
            int currentDelay = 140;
            int totalDelay = 180;
            for (int i = numItems - 1; i >= 0; i--) {
                View view = hideAnimatedList.get(i);
                Runnable endRunnable = null;
                if (i == 0) {
                    endRunnable = onSlideAwayAnimationComplete;
                }
                dismissViewAnimated(view, endRunnable, totalDelay, 260L);
                currentDelay = Math.max(50, currentDelay - 10);
                totalDelay += currentDelay;
            }
            return;
        }
        onSlideAwayAnimationComplete.run();
    }

    public /* synthetic */ void lambda$performDismissAllAnimations$9$NotificationStackScrollLayout(boolean closeShade, final Runnable onAnimationComplete) {
        if (closeShade) {
            this.mShadeController.addPostCollapseAction(new Runnable() { // from class: com.android.systemui.statusbar.notification.stack.-$$Lambda$NotificationStackScrollLayout$w5SuV9LLRamOioCzTlwfHPKXPk0
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationStackScrollLayout.this.lambda$performDismissAllAnimations$8$NotificationStackScrollLayout(onAnimationComplete);
                }
            });
            this.mStatusBar.animateCollapsePanels(0);
            return;
        }
        setDismissAllInProgress(false);
        onAnimationComplete.run();
    }

    public /* synthetic */ void lambda$performDismissAllAnimations$8$NotificationStackScrollLayout(Runnable onAnimationComplete) {
        setDismissAllInProgress(false);
        onAnimationComplete.run();
    }

    @VisibleForTesting
    protected void inflateFooterView() {
        FooterView footerView = (FooterView) LayoutInflater.from(this.mContext).inflate(R.layout.status_bar_notification_footer, (ViewGroup) this, false);
        footerView.setDismissButtonClickListener(new View.OnClickListener() { // from class: com.android.systemui.statusbar.notification.stack.-$$Lambda$NotificationStackScrollLayout$op8qZbY4pqro2H5co8U9Hoiim3w
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                NotificationStackScrollLayout.this.lambda$inflateFooterView$10$NotificationStackScrollLayout(view);
            }
        });
        footerView.setManageButtonClickListener(new View.OnClickListener() { // from class: com.android.systemui.statusbar.notification.stack.-$$Lambda$mjP2-ECpzICMymoTPt8MeJd4_PU
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                NotificationStackScrollLayout.this.manageNotifications(view);
            }
        });
        setFooterView(footerView);
    }

    public /* synthetic */ void lambda$inflateFooterView$10$NotificationStackScrollLayout(View v) {
        this.mMetricsLogger.action(148);
        clearNotifications(0, true);
    }

    private void inflateEmptyShadeView() {
        EmptyShadeView view = (EmptyShadeView) LayoutInflater.from(this.mContext).inflate(R.layout.status_bar_no_notifications, (ViewGroup) this, false);
        view.setText(R.string.empty_shade_text);
        setEmptyShadeView(view);
    }

    public void onUpdateRowStates() {
        changeViewPosition(this.mFooterView, -1);
        int offsetFromEnd = 1 + 1;
        changeViewPosition(this.mEmptyShadeView, getChildCount() - 1);
        changeViewPosition(this.mShelf, getChildCount() - offsetFromEnd);
    }

    public void setNotificationPanel(NotificationPanelView notificationPanelView) {
        this.mNotificationPanel = notificationPanelView;
    }

    public void updateIconAreaViews() {
        this.mIconAreaController.updateNotificationIcons();
    }

    public float setPulseHeight(float height) {
        this.mAmbientState.setPulseHeight(height);
        if (this.mKeyguardBypassController.getBypassEnabled()) {
            notifyAppearChangedListeners();
        }
        requestChildrenUpdate();
        return Math.max(0.0f, height - this.mAmbientState.getInnerHeight(true));
    }

    public float getPulseHeight() {
        return this.mAmbientState.getPulseHeight();
    }

    public void setDozeAmount(float dozeAmount) {
        this.mAmbientState.setDozeAmount(dozeAmount);
        updateContinuousBackgroundDrawing();
        requestChildrenUpdate();
    }

    public void wakeUpFromPulse() {
        setPulseHeight(getWakeUpHeight());
        boolean firstVisibleView = true;
        float wakeUplocation = -1.0f;
        int childCount = getChildCount();
        int i = 0;
        while (true) {
            if (i < childCount) {
                ExpandableView view = (ExpandableView) getChildAt(i);
                if (view.getVisibility() != 8) {
                    boolean isShelf = view == this.mShelf;
                    if ((view instanceof ExpandableNotificationRow) || isShelf) {
                        if (view.getVisibility() == 0 && !isShelf) {
                            if (firstVisibleView) {
                                firstVisibleView = false;
                                wakeUplocation = (view.getTranslationY() + view.getActualHeight()) - this.mShelf.getIntrinsicHeight();
                            }
                        } else if (!firstVisibleView) {
                            view.setTranslationY(wakeUplocation);
                        }
                    }
                }
                i++;
            } else {
                this.mDimmedNeedsAnimation = true;
                return;
            }
        }
    }

    @Override // com.android.systemui.statusbar.notification.DynamicPrivacyController.Listener
    public void onDynamicPrivacyChanged() {
        if (this.mIsExpanded) {
            this.mAnimateBottomOnLayout = true;
        }
        post(new Runnable() { // from class: com.android.systemui.statusbar.notification.stack.-$$Lambda$NotificationStackScrollLayout$D4tNYDYD9CbJOONSnq9gkCauZio
            @Override // java.lang.Runnable
            public final void run() {
                NotificationStackScrollLayout.this.lambda$onDynamicPrivacyChanged$11$NotificationStackScrollLayout();
            }
        });
    }

    public /* synthetic */ void lambda$onDynamicPrivacyChanged$11$NotificationStackScrollLayout() {
        updateFooter();
        updateSectionBoundaries();
    }

    public void setOnPulseHeightChangedListener(Runnable listener) {
        this.mAmbientState.setOnPulseHeightChangedListener(listener);
    }

    public float calculateAppearFractionBypass() {
        float pulseHeight = getPulseHeight();
        float wakeUpHeight = getWakeUpHeight();
        float dragDownAmount = pulseHeight - wakeUpHeight;
        float totalDistance = getIntrinsicPadding();
        return MathUtils.smoothStep(0.0f, totalDistance, dragDownAmount);
    }

    public boolean hasActiveNotifications() {
        return !this.mEntryManager.getNotificationData().getActiveNotifications().isEmpty();
    }

    public void updateSpeedBumpIndex() {
        boolean beforeSpeedBump;
        int speedBumpIndex = 0;
        int currentIndex = 0;
        int N = getChildCount();
        int i = 0;
        while (true) {
            if (i >= N) {
                break;
            }
            View view = getChildAt(i);
            if (view.getVisibility() != 8 && (view instanceof ExpandableNotificationRow)) {
                ExpandableNotificationRow row = (ExpandableNotificationRow) view;
                currentIndex++;
                if (this.mHighPriorityBeforeSpeedBump) {
                    beforeSpeedBump = row.getEntry().isTopBucket();
                } else {
                    beforeSpeedBump = true ^ row.getEntry().ambient;
                }
                if (beforeSpeedBump) {
                    speedBumpIndex = currentIndex;
                }
            }
            i++;
        }
        boolean noAmbient = speedBumpIndex == N;
        updateSpeedBumpIndex(speedBumpIndex, noAmbient);
    }

    public void updateSectionBoundaries() {
        this.mSectionsManager.updateSectionBoundaries();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateContinuousBackgroundDrawing() {
        boolean continuousBackground = (this.mAmbientState.isFullyAwake() || this.mAmbientState.getDraggedViews().isEmpty()) ? false : true;
        if (continuousBackground != this.mContinuousBackgroundUpdate) {
            this.mContinuousBackgroundUpdate = continuousBackground;
            if (continuousBackground) {
                getViewTreeObserver().addOnPreDrawListener(this.mBackgroundUpdater);
            } else {
                getViewTreeObserver().removeOnPreDrawListener(this.mBackgroundUpdater);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateContinuousShadowDrawing() {
        boolean continuousShadowUpdate = this.mAnimationRunning || !this.mAmbientState.getDraggedViews().isEmpty();
        if (continuousShadowUpdate != this.mContinuousShadowUpdate) {
            if (continuousShadowUpdate) {
                getViewTreeObserver().addOnPreDrawListener(this.mShadowUpdater);
            } else {
                getViewTreeObserver().removeOnPreDrawListener(this.mShadowUpdater);
            }
            this.mContinuousShadowUpdate = continuousShadowUpdate;
        }
    }

    @Override // com.android.systemui.statusbar.notification.stack.NotificationListContainer
    public void resetExposedMenuView(boolean animate, boolean force) {
        this.mSwipeHelper.resetExposedMenuView(animate, force);
    }

    private static boolean matchesSelection(ExpandableNotificationRow row, int selection) {
        if (selection != 0) {
            if (selection != 1) {
                if (selection == 2) {
                    return true ^ row.getEntry().isTopBucket();
                }
                throw new IllegalArgumentException("Unknown selection: " + selection);
            }
            return row.getEntry().isTopBucket();
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public static class AnimationEvent {
        static final int ANIMATION_TYPE_ACTIVATED_CHILD = 4;
        static final int ANIMATION_TYPE_ADD = 0;
        static final int ANIMATION_TYPE_CHANGE_POSITION = 6;
        static final int ANIMATION_TYPE_DIMMED = 5;
        static final int ANIMATION_TYPE_EVERYTHING = 15;
        static final int ANIMATION_TYPE_GO_TO_FULL_SHADE = 7;
        static final int ANIMATION_TYPE_GROUP_EXPANSION_CHANGED = 10;
        static final int ANIMATION_TYPE_HEADS_UP_APPEAR = 11;
        static final int ANIMATION_TYPE_HEADS_UP_DISAPPEAR = 12;
        static final int ANIMATION_TYPE_HEADS_UP_DISAPPEAR_CLICK = 13;
        static final int ANIMATION_TYPE_HEADS_UP_OTHER = 14;
        static final int ANIMATION_TYPE_HIDE_SENSITIVE = 8;
        static final int ANIMATION_TYPE_REMOVE = 1;
        static final int ANIMATION_TYPE_REMOVE_SWIPED_OUT = 2;
        static final int ANIMATION_TYPE_TOP_PADDING_CHANGED = 3;
        static final int ANIMATION_TYPE_VIEW_RESIZE = 9;
        static AnimationFilter[] FILTERS = {new AnimationFilter().animateHeight().animateTopInset().animateY().animateZ().hasDelays(), new AnimationFilter().animateHeight().animateTopInset().animateY().animateZ().hasDelays(), new AnimationFilter().animateHeight().animateTopInset().animateY().animateZ().hasDelays(), new AnimationFilter().animateHeight().animateTopInset().animateY().animateDimmed().animateZ(), new AnimationFilter().animateZ(), new AnimationFilter().animateDimmed(), new AnimationFilter().animateAlpha().animateHeight().animateTopInset().animateY().animateZ(), new AnimationFilter().animateHeight().animateTopInset().animateY().animateDimmed().animateZ().hasDelays(), new AnimationFilter().animateHideSensitive(), new AnimationFilter().animateHeight().animateTopInset().animateY().animateZ(), new AnimationFilter().animateAlpha().animateHeight().animateTopInset().animateY().animateZ(), new AnimationFilter().animateHeight().animateTopInset().animateY().animateZ(), new AnimationFilter().animateHeight().animateTopInset().animateY().animateZ().hasDelays(), new AnimationFilter().animateHeight().animateTopInset().animateY().animateZ().hasDelays(), new AnimationFilter().animateHeight().animateTopInset().animateY().animateZ(), new AnimationFilter().animateAlpha().animateDimmed().animateHideSensitive().animateHeight().animateTopInset().animateY().animateZ()};
        static int[] LENGTHS = {StackStateAnimator.ANIMATION_DURATION_APPEAR_DISAPPEAR, StackStateAnimator.ANIMATION_DURATION_APPEAR_DISAPPEAR, StackStateAnimator.ANIMATION_DURATION_STANDARD, StackStateAnimator.ANIMATION_DURATION_STANDARD, StackStateAnimator.ANIMATION_DURATION_DIMMED_ACTIVATED, StackStateAnimator.ANIMATION_DURATION_DIMMED_ACTIVATED, StackStateAnimator.ANIMATION_DURATION_STANDARD, StackStateAnimator.ANIMATION_DURATION_GO_TO_FULL_SHADE, StackStateAnimator.ANIMATION_DURATION_STANDARD, StackStateAnimator.ANIMATION_DURATION_STANDARD, StackStateAnimator.ANIMATION_DURATION_STANDARD, 550, 300, 300, StackStateAnimator.ANIMATION_DURATION_STANDARD, StackStateAnimator.ANIMATION_DURATION_STANDARD};
        final int animationType;
        final long eventStartTime;
        final AnimationFilter filter;
        boolean headsUpFromBottom;
        final long length;
        final ExpandableView mChangingView;
        View viewAfterChangingView;

        AnimationEvent(ExpandableView view, int type) {
            this(view, type, LENGTHS[type]);
        }

        AnimationEvent(ExpandableView view, int type, AnimationFilter filter) {
            this(view, type, LENGTHS[type], filter);
        }

        AnimationEvent(ExpandableView view, int type, long length) {
            this(view, type, length, FILTERS[type]);
        }

        AnimationEvent(ExpandableView view, int type, long length, AnimationFilter filter) {
            this.eventStartTime = AnimationUtils.currentAnimationTimeMillis();
            this.mChangingView = view;
            this.animationType = type;
            this.length = length;
            this.filter = filter;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static long combineLength(ArrayList<AnimationEvent> events) {
            long length = 0;
            int size = events.size();
            for (int i = 0; i < size; i++) {
                AnimationEvent event = events.get(i);
                length = Math.max(length, event.length);
                if (event.animationType == 7) {
                    return event.length;
                }
            }
            return length;
        }
    }

    /* renamed from: com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout$14  reason: invalid class name */
    /* loaded from: classes21.dex */
    class AnonymousClass14 implements DragDownHelper.DragDownCallback {
        AnonymousClass14() {
        }

        @Override // com.android.systemui.statusbar.DragDownHelper.DragDownCallback
        public boolean onDraggedDown(View startingChild, int dragLengthY) {
            if (NotificationStackScrollLayout.this.mStatusBarState != 1 || !NotificationStackScrollLayout.this.hasActiveNotifications()) {
                if (NotificationStackScrollLayout.this.mDynamicPrivacyController.isInLockedDownShade()) {
                    NotificationStackScrollLayout.this.mStatusbarStateController.setLeaveOpenOnKeyguardHide(true);
                    NotificationStackScrollLayout.this.mStatusBar.dismissKeyguardThenExecute(new ActivityStarter.OnDismissAction() { // from class: com.android.systemui.statusbar.notification.stack.-$$Lambda$NotificationStackScrollLayout$14$018-LjwrYv2pCh1YYuEsk9izQUU
                        @Override // com.android.systemui.plugins.ActivityStarter.OnDismissAction
                        public final boolean onDismiss() {
                            return NotificationStackScrollLayout.AnonymousClass14.lambda$onDraggedDown$0();
                        }
                    }, null, false);
                    return true;
                }
                return false;
            }
            NotificationStackScrollLayout.this.mLockscreenGestureLogger.write(Opcodes.NEW, (int) (dragLengthY / NotificationStackScrollLayout.this.mDisplayMetrics.density), 0);
            if (!NotificationStackScrollLayout.this.mAmbientState.isDozing() || startingChild != null) {
                NotificationStackScrollLayout.this.mShadeController.goToLockedShade(startingChild);
                if (startingChild instanceof ExpandableNotificationRow) {
                    ExpandableNotificationRow row = (ExpandableNotificationRow) startingChild;
                    row.onExpandedByGesture(true);
                }
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ boolean lambda$onDraggedDown$0() {
            return false;
        }

        @Override // com.android.systemui.statusbar.DragDownHelper.DragDownCallback
        public void onDragDownReset() {
            NotificationStackScrollLayout.this.setDimmed(true, true);
            NotificationStackScrollLayout.this.resetScrollPosition();
            NotificationStackScrollLayout.this.resetCheckSnoozeLeavebehind();
        }

        @Override // com.android.systemui.statusbar.DragDownHelper.DragDownCallback
        public void onCrossedThreshold(boolean above) {
            NotificationStackScrollLayout.this.setDimmed(!above, true);
        }

        @Override // com.android.systemui.statusbar.DragDownHelper.DragDownCallback
        public void onTouchSlopExceeded() {
            NotificationStackScrollLayout.this.cancelLongPress();
            NotificationStackScrollLayout.this.checkSnoozeLeavebehind();
        }

        @Override // com.android.systemui.statusbar.DragDownHelper.DragDownCallback
        public void setEmptyDragAmount(float amount) {
            NotificationStackScrollLayout.this.mNotificationPanel.setEmptyDragAmount(amount);
        }

        @Override // com.android.systemui.statusbar.DragDownHelper.DragDownCallback
        public boolean isFalsingCheckNeeded() {
            return NotificationStackScrollLayout.this.mStatusBarState == 1;
        }

        @Override // com.android.systemui.statusbar.DragDownHelper.DragDownCallback
        public boolean isDragDownEnabledForView(ExpandableView view) {
            if (isDragDownAnywhereEnabled()) {
                return true;
            }
            if (NotificationStackScrollLayout.this.mDynamicPrivacyController.isInLockedDownShade()) {
                if (view == null) {
                    return true;
                }
                if (view instanceof ExpandableNotificationRow) {
                    return ((ExpandableNotificationRow) view).getEntry().isSensitive();
                }
                return false;
            }
            return false;
        }

        @Override // com.android.systemui.statusbar.DragDownHelper.DragDownCallback
        public boolean isDragDownAnywhereEnabled() {
            return NotificationStackScrollLayout.this.mStatusbarStateController.getState() == 1 && !NotificationStackScrollLayout.this.mKeyguardBypassController.getBypassEnabled();
        }
    }

    public DragDownHelper.DragDownCallback getDragDownCallback() {
        return this.mDragDownCallback;
    }

    public HeadsUpTouchHelper.Callback getHeadsUpCallback() {
        return this.mHeadsUpCallback;
    }

    public ExpandHelper.Callback getExpandHelperCallback() {
        return this.mExpandHelperCallback;
    }
}
