package com.android.systemui.stackdivider;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.Region;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Choreographer;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.policy.DividerSnapAlgorithm;
import com.android.internal.policy.DockedDividerUtils;
import com.android.internal.view.SurfaceFlingerVsyncChoreographer;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.shared.system.WindowManagerWrapper;
import com.android.systemui.statusbar.FlingAnimationUtils;
/* loaded from: classes21.dex */
public class DividerView extends FrameLayout implements View.OnTouchListener, ViewTreeObserver.OnComputeInternalInsetsListener {
    private static final float ADJUSTED_FOR_IME_SCALE = 0.5f;
    public static final int INVALID_RECENTS_GROW_TARGET = -1;
    private static final int LOG_VALUE_RESIZE_50_50 = 0;
    private static final int LOG_VALUE_RESIZE_DOCKED_LARGER = 2;
    private static final int LOG_VALUE_RESIZE_DOCKED_SMALLER = 1;
    private static final int LOG_VALUE_UNDOCK_MAX_DOCKED = 0;
    private static final int LOG_VALUE_UNDOCK_MAX_OTHER = 1;
    private static final float MINIMIZE_DOCK_SCALE = 0.0f;
    private static final int MSG_RESIZE_STACK = 0;
    private static final int TASK_POSITION_SAME = Integer.MAX_VALUE;
    static final long TOUCH_ANIMATION_DURATION = 150;
    static final long TOUCH_RELEASE_ANIMATION_DURATION = 200;
    private boolean mAdjustedForIme;
    private View mBackground;
    private boolean mBackgroundLifted;
    private DividerCallbacks mCallback;
    private ValueAnimator mCurrentAnimator;
    private final Display mDefaultDisplay;
    private int mDisplayHeight;
    private int mDisplayRotation;
    private int mDisplayWidth;
    private int mDividerInsets;
    private int mDividerSize;
    private int mDividerWindowWidth;
    private int mDockSide;
    private final Rect mDockedInsetRect;
    private final Rect mDockedRect;
    private boolean mDockedStackMinimized;
    private final Rect mDockedTaskRect;
    private boolean mEntranceAnimationRunning;
    private boolean mExitAnimationRunning;
    private int mExitStartPosition;
    private FlingAnimationUtils mFlingAnimationUtils;
    private boolean mGrowRecents;
    private DividerHandleView mHandle;
    private final View.AccessibilityDelegate mHandleDelegate;
    private final Handler mHandler;
    private boolean mHomeStackResizable;
    private boolean mIsInMinimizeInteraction;
    private final Rect mLastResizeRect;
    private int mLongPressEntraceAnimDuration;
    private MinimizedDockShadow mMinimizedShadow;
    private DividerSnapAlgorithm mMinimizedSnapAlgorithm;
    private boolean mMoving;
    private final Rect mOtherInsetRect;
    private final Rect mOtherRect;
    private final Rect mOtherTaskRect;
    private boolean mRemoved;
    private final Runnable mResetBackgroundRunnable;
    private final SurfaceFlingerVsyncChoreographer mSfChoreographer;
    private DividerSnapAlgorithm mSnapAlgorithm;
    private DividerSnapAlgorithm.SnapTarget mSnapTargetBeforeMinimized;
    private final Rect mStableInsets;
    private int mStartPosition;
    private int mStartX;
    private int mStartY;
    private DividerState mState;
    private final int[] mTempInt2;
    private final Rect mTmpRect;
    private int mTouchElevation;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;
    private DividerWindowManager mWindowManager;
    private final WindowManagerProxy mWindowManagerProxy;
    private static final PathInterpolator SLOWDOWN_INTERPOLATOR = new PathInterpolator(0.5f, 1.0f, 0.5f, 1.0f);
    private static final PathInterpolator DIM_INTERPOLATOR = new PathInterpolator(0.23f, 0.87f, 0.52f, -0.11f);
    private static final Interpolator IME_ADJUST_INTERPOLATOR = new PathInterpolator(0.2f, 0.0f, 0.1f, 1.0f);

    /* loaded from: classes21.dex */
    public interface DividerCallbacks {
        void growRecents();

        void onDraggingEnd();

        void onDraggingStart();
    }

    public DividerView(Context context) {
        this(context, null);
    }

    public DividerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DividerView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DividerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mTempInt2 = new int[2];
        this.mDockedRect = new Rect();
        this.mDockedTaskRect = new Rect();
        this.mOtherTaskRect = new Rect();
        this.mOtherRect = new Rect();
        this.mDockedInsetRect = new Rect();
        this.mOtherInsetRect = new Rect();
        this.mLastResizeRect = new Rect();
        this.mTmpRect = new Rect();
        this.mWindowManagerProxy = WindowManagerProxy.getInstance();
        this.mStableInsets = new Rect();
        this.mHandler = new Handler() { // from class: com.android.systemui.stackdivider.DividerView.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    DividerView.this.resizeStack(msg.arg1, msg.arg2, (DividerSnapAlgorithm.SnapTarget) msg.obj);
                } else {
                    super.handleMessage(msg);
                }
            }
        };
        this.mHandleDelegate = new View.AccessibilityDelegate() { // from class: com.android.systemui.stackdivider.DividerView.2
            @Override // android.view.View.AccessibilityDelegate
            public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                DividerSnapAlgorithm snapAlgorithm = DividerView.this.getSnapAlgorithm();
                if (DividerView.this.isHorizontalDivision()) {
                    info.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.id.action_move_tl_full, DividerView.this.mContext.getString(R.string.accessibility_action_divider_top_full)));
                    if (snapAlgorithm.isFirstSplitTargetAvailable()) {
                        info.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.id.action_move_tl_70, DividerView.this.mContext.getString(R.string.accessibility_action_divider_top_70)));
                    }
                    if (snapAlgorithm.showMiddleSplitTargetForAccessibility()) {
                        info.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.id.action_move_tl_50, DividerView.this.mContext.getString(R.string.accessibility_action_divider_top_50)));
                    }
                    if (snapAlgorithm.isLastSplitTargetAvailable()) {
                        info.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.id.action_move_tl_30, DividerView.this.mContext.getString(R.string.accessibility_action_divider_top_30)));
                    }
                    info.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.id.action_move_rb_full, DividerView.this.mContext.getString(R.string.accessibility_action_divider_bottom_full)));
                    return;
                }
                info.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.id.action_move_tl_full, DividerView.this.mContext.getString(R.string.accessibility_action_divider_left_full)));
                if (snapAlgorithm.isFirstSplitTargetAvailable()) {
                    info.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.id.action_move_tl_70, DividerView.this.mContext.getString(R.string.accessibility_action_divider_left_70)));
                }
                if (snapAlgorithm.showMiddleSplitTargetForAccessibility()) {
                    info.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.id.action_move_tl_50, DividerView.this.mContext.getString(R.string.accessibility_action_divider_left_50)));
                }
                if (snapAlgorithm.isLastSplitTargetAvailable()) {
                    info.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.id.action_move_tl_30, DividerView.this.mContext.getString(R.string.accessibility_action_divider_left_30)));
                }
                info.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.id.action_move_rb_full, DividerView.this.mContext.getString(R.string.accessibility_action_divider_right_full)));
            }

            @Override // android.view.View.AccessibilityDelegate
            public boolean performAccessibilityAction(View host, int action, Bundle args) {
                DividerSnapAlgorithm.SnapTarget nextTarget;
                int currentPosition = DividerView.this.getCurrentPosition();
                if (action == R.id.action_move_tl_full) {
                    DividerSnapAlgorithm.SnapTarget nextTarget2 = DividerView.this.mSnapAlgorithm.getDismissEndTarget();
                    nextTarget = nextTarget2;
                } else if (action == R.id.action_move_tl_70) {
                    DividerSnapAlgorithm.SnapTarget nextTarget3 = DividerView.this.mSnapAlgorithm.getLastSplitTarget();
                    nextTarget = nextTarget3;
                } else if (action == R.id.action_move_tl_50) {
                    DividerSnapAlgorithm.SnapTarget nextTarget4 = DividerView.this.mSnapAlgorithm.getMiddleTarget();
                    nextTarget = nextTarget4;
                } else if (action == R.id.action_move_tl_30) {
                    DividerSnapAlgorithm.SnapTarget nextTarget5 = DividerView.this.mSnapAlgorithm.getFirstSplitTarget();
                    nextTarget = nextTarget5;
                } else if (action == R.id.action_move_rb_full) {
                    DividerSnapAlgorithm.SnapTarget nextTarget6 = DividerView.this.mSnapAlgorithm.getDismissStartTarget();
                    nextTarget = nextTarget6;
                } else {
                    nextTarget = null;
                }
                if (nextTarget != null) {
                    DividerView.this.startDragging(true, false);
                    DividerView.this.stopDragging(currentPosition, nextTarget, 250L, Interpolators.FAST_OUT_SLOW_IN);
                    return true;
                }
                return super.performAccessibilityAction(host, action, args);
            }
        };
        this.mResetBackgroundRunnable = new Runnable() { // from class: com.android.systemui.stackdivider.DividerView.3
            @Override // java.lang.Runnable
            public void run() {
                DividerView.this.resetBackground();
            }
        };
        this.mSfChoreographer = new SurfaceFlingerVsyncChoreographer(this.mHandler, context.getDisplay(), Choreographer.getInstance());
        DisplayManager displayManager = (DisplayManager) this.mContext.getSystemService("display");
        this.mDefaultDisplay = displayManager.getDisplay(0);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mHandle = (DividerHandleView) findViewById(R.id.docked_divider_handle);
        this.mBackground = findViewById(R.id.docked_divider_background);
        this.mMinimizedShadow = (MinimizedDockShadow) findViewById(R.id.minimized_dock_shadow);
        this.mHandle.setOnTouchListener(this);
        this.mDividerWindowWidth = getResources().getDimensionPixelSize(17105146);
        this.mDividerInsets = getResources().getDimensionPixelSize(17105145);
        this.mDividerSize = this.mDividerWindowWidth - (this.mDividerInsets * 2);
        this.mTouchElevation = getResources().getDimensionPixelSize(R.dimen.docked_stack_divider_lift_elevation);
        this.mLongPressEntraceAnimDuration = getResources().getInteger(R.integer.long_press_dock_anim_duration);
        this.mGrowRecents = getResources().getBoolean(R.bool.recents_grow_in_multiwindow);
        this.mTouchSlop = ViewConfiguration.get(this.mContext).getScaledTouchSlop();
        this.mFlingAnimationUtils = new FlingAnimationUtils(getContext(), 0.3f);
        updateDisplayInfo();
        boolean landscape = getResources().getConfiguration().orientation == 2;
        this.mHandle.setPointerIcon(PointerIcon.getSystemIcon(getContext(), landscape ? 1014 : 1015));
        getViewTreeObserver().addOnComputeInternalInsetsListener(this);
        this.mHandle.setAccessibilityDelegate(this.mHandleDelegate);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mHomeStackResizable && this.mDockSide != -1 && !this.mIsInMinimizeInteraction) {
            saveSnapTargetBeforeMinimized(this.mSnapTargetBeforeMinimized);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDividerRemoved() {
        this.mRemoved = true;
        this.mCallback = null;
        this.mHandler.removeMessages(0);
    }

    @Override // android.view.View
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (this.mStableInsets.left != insets.getStableInsetLeft() || this.mStableInsets.top != insets.getStableInsetTop() || this.mStableInsets.right != insets.getStableInsetRight() || this.mStableInsets.bottom != insets.getStableInsetBottom()) {
            this.mStableInsets.set(insets.getStableInsetLeft(), insets.getStableInsetTop(), insets.getStableInsetRight(), insets.getStableInsetBottom());
            if (this.mSnapAlgorithm != null || this.mMinimizedSnapAlgorithm != null) {
                this.mSnapAlgorithm = null;
                this.mMinimizedSnapAlgorithm = null;
                initializeSnapAlgorithm();
            }
        }
        return super.onApplyWindowInsets(insets);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int minimizeLeft = 0;
        int minimizeTop = 0;
        int i = this.mDockSide;
        if (i == 2) {
            minimizeTop = this.mBackground.getTop();
        } else if (i == 1) {
            minimizeLeft = this.mBackground.getLeft();
        } else if (i == 3) {
            minimizeLeft = this.mBackground.getRight() - this.mMinimizedShadow.getWidth();
        }
        MinimizedDockShadow minimizedDockShadow = this.mMinimizedShadow;
        minimizedDockShadow.layout(minimizeLeft, minimizeTop, minimizedDockShadow.getMeasuredWidth() + minimizeLeft, this.mMinimizedShadow.getMeasuredHeight() + minimizeTop);
        if (changed) {
            this.mWindowManagerProxy.setTouchRegion(new Rect(this.mHandle.getLeft(), this.mHandle.getTop(), this.mHandle.getRight(), this.mHandle.getBottom()));
        }
    }

    public void injectDependencies(DividerWindowManager windowManager, DividerState dividerState, DividerCallbacks callback) {
        this.mWindowManager = windowManager;
        this.mState = dividerState;
        this.mCallback = callback;
        if (this.mStableInsets.isEmpty()) {
            WindowManagerWrapper.getInstance().getStableInsets(this.mStableInsets);
        }
        if (this.mState.mRatioPositionBeforeMinimized == 0.0f) {
            this.mSnapTargetBeforeMinimized = this.mSnapAlgorithm.getMiddleTarget();
        } else {
            repositionSnapTargetBeforeMinimized();
        }
    }

    public WindowManagerProxy getWindowManagerProxy() {
        return this.mWindowManagerProxy;
    }

    public Rect getNonMinimizedSplitScreenSecondaryBounds() {
        calculateBoundsForPosition(this.mSnapTargetBeforeMinimized.position, DockedDividerUtils.invertDockSide(this.mDockSide), this.mOtherTaskRect);
        this.mOtherTaskRect.bottom -= this.mStableInsets.bottom;
        int i = this.mDockSide;
        if (i == 1) {
            this.mOtherTaskRect.top += this.mStableInsets.top;
            this.mOtherTaskRect.right -= this.mStableInsets.right;
        } else if (i == 3) {
            this.mOtherTaskRect.top += this.mStableInsets.top;
            this.mOtherTaskRect.left += this.mStableInsets.left;
        }
        return this.mOtherTaskRect;
    }

    public boolean startDragging(boolean animate, boolean touching) {
        cancelFlingAnimation();
        if (touching) {
            this.mHandle.setTouching(true, animate);
        }
        this.mDockSide = this.mWindowManagerProxy.getDockSide();
        if (this.mDisplayRotation != this.mDefaultDisplay.getRotation()) {
            updateDisplayInfo();
        }
        initializeSnapAlgorithm();
        this.mWindowManagerProxy.setResizing(true);
        if (touching) {
            this.mWindowManager.setSlippery(false);
            liftBackground();
        }
        DividerCallbacks dividerCallbacks = this.mCallback;
        if (dividerCallbacks != null) {
            dividerCallbacks.onDraggingStart();
        }
        if (this.mDockSide != -1) {
            return true;
        }
        return false;
    }

    public void stopDragging(int position, float velocity, boolean avoidDismissStart, boolean logMetrics) {
        this.mHandle.setTouching(false, true);
        fling(position, velocity, avoidDismissStart, logMetrics);
        this.mWindowManager.setSlippery(true);
        releaseBackground();
    }

    public void stopDragging(int position, DividerSnapAlgorithm.SnapTarget target, long duration, Interpolator interpolator) {
        stopDragging(position, target, duration, 0L, 0L, interpolator);
    }

    public void stopDragging(int position, DividerSnapAlgorithm.SnapTarget target, long duration, Interpolator interpolator, long endDelay) {
        stopDragging(position, target, duration, 0L, endDelay, interpolator);
    }

    public void stopDragging(int position, DividerSnapAlgorithm.SnapTarget target, long duration, long startDelay, long endDelay, Interpolator interpolator) {
        this.mHandle.setTouching(false, true);
        flingTo(position, target, duration, startDelay, endDelay, interpolator);
        this.mWindowManager.setSlippery(true);
        releaseBackground();
    }

    private void stopDragging() {
        this.mHandle.setTouching(false, true);
        this.mWindowManager.setSlippery(true);
        releaseBackground();
    }

    private void updateDockSide() {
        this.mDockSide = this.mWindowManagerProxy.getDockSide();
        this.mMinimizedShadow.setDockSide(this.mDockSide);
    }

    private void initializeSnapAlgorithm() {
        if (this.mSnapAlgorithm == null) {
            this.mSnapAlgorithm = new DividerSnapAlgorithm(getContext().getResources(), this.mDisplayWidth, this.mDisplayHeight, this.mDividerSize, isHorizontalDivision(), this.mStableInsets, this.mDockSide);
            DividerSnapAlgorithm.SnapTarget snapTarget = this.mSnapTargetBeforeMinimized;
            if (snapTarget != null && snapTarget.isMiddleTarget) {
                this.mSnapTargetBeforeMinimized = this.mSnapAlgorithm.getMiddleTarget();
            }
        }
        if (this.mMinimizedSnapAlgorithm == null) {
            this.mMinimizedSnapAlgorithm = new DividerSnapAlgorithm(getContext().getResources(), this.mDisplayWidth, this.mDisplayHeight, this.mDividerSize, isHorizontalDivision(), this.mStableInsets, this.mDockSide, this.mDockedStackMinimized && this.mHomeStackResizable);
        }
    }

    public DividerSnapAlgorithm getSnapAlgorithm() {
        initializeSnapAlgorithm();
        return (this.mDockedStackMinimized && this.mHomeStackResizable) ? this.mMinimizedSnapAlgorithm : this.mSnapAlgorithm;
    }

    public int getCurrentPosition() {
        getLocationOnScreen(this.mTempInt2);
        if (isHorizontalDivision()) {
            return this.mTempInt2[1] + this.mDividerInsets;
        }
        return this.mTempInt2[0] + this.mDividerInsets;
    }

    /* JADX WARN: Code restructure failed: missing block: B:8:0x0013, code lost:
        if (r0 != 3) goto L8;
     */
    @Override // android.view.View.OnTouchListener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouch(android.view.View r10, android.view.MotionEvent r11) {
        /*
            r9 = this;
            r9.convertToScreenCoordinates(r11)
            int r0 = r11.getAction()
            r0 = r0 & 255(0xff, float:3.57E-43)
            r1 = 0
            r2 = 1
            if (r0 == 0) goto Lac
            if (r0 == r2) goto L78
            r3 = 2
            if (r0 == r3) goto L17
            r3 = 3
            if (r0 == r3) goto L78
            goto Lab
        L17:
            android.view.VelocityTracker r3 = r9.mVelocityTracker
            r3.addMovement(r11)
            float r3 = r11.getX()
            int r3 = (int) r3
            float r4 = r11.getY()
            int r4 = (int) r4
            boolean r5 = r9.isHorizontalDivision()
            if (r5 == 0) goto L39
            int r5 = r9.mStartY
            int r5 = r4 - r5
            int r5 = java.lang.Math.abs(r5)
            int r6 = r9.mTouchSlop
            if (r5 > r6) goto L4b
        L39:
            boolean r5 = r9.isHorizontalDivision()
            if (r5 != 0) goto L4d
            int r5 = r9.mStartX
            int r5 = r3 - r5
            int r5 = java.lang.Math.abs(r5)
            int r6 = r9.mTouchSlop
            if (r5 <= r6) goto L4d
        L4b:
            r5 = r2
            goto L4e
        L4d:
            r5 = r1
        L4e:
            boolean r6 = r9.mMoving
            if (r6 != 0) goto L5a
            if (r5 == 0) goto L5a
            r9.mStartX = r3
            r9.mStartY = r4
            r9.mMoving = r2
        L5a:
            boolean r6 = r9.mMoving
            if (r6 == 0) goto Lab
            int r6 = r9.mDockSide
            r7 = -1
            if (r6 == r7) goto Lab
            com.android.internal.policy.DividerSnapAlgorithm r6 = r9.getSnapAlgorithm()
            int r7 = r9.mStartPosition
            r8 = 0
            com.android.internal.policy.DividerSnapAlgorithm$SnapTarget r1 = r6.calculateSnapTarget(r7, r8, r1)
            int r6 = r9.calculatePosition(r3, r4)
            int r7 = r9.mStartPosition
            r9.resizeStackDelayed(r6, r7, r1)
            goto Lab
        L78:
            android.view.VelocityTracker r3 = r9.mVelocityTracker
            r3.addMovement(r11)
            float r3 = r11.getRawX()
            int r3 = (int) r3
            float r4 = r11.getRawY()
            int r4 = (int) r4
            android.view.VelocityTracker r5 = r9.mVelocityTracker
            r6 = 1000(0x3e8, float:1.401E-42)
            r5.computeCurrentVelocity(r6)
            int r5 = r9.calculatePosition(r3, r4)
            boolean r6 = r9.isHorizontalDivision()
            if (r6 == 0) goto L9f
            android.view.VelocityTracker r6 = r9.mVelocityTracker
            float r6 = r6.getYVelocity()
            goto La5
        L9f:
            android.view.VelocityTracker r6 = r9.mVelocityTracker
            float r6 = r6.getXVelocity()
        La5:
            r9.stopDragging(r5, r6, r1, r2)
            r9.mMoving = r1
        Lab:
            return r2
        Lac:
            android.view.VelocityTracker r3 = android.view.VelocityTracker.obtain()
            r9.mVelocityTracker = r3
            android.view.VelocityTracker r3 = r9.mVelocityTracker
            r3.addMovement(r11)
            float r3 = r11.getX()
            int r3 = (int) r3
            r9.mStartX = r3
            float r3 = r11.getY()
            int r3 = (int) r3
            r9.mStartY = r3
            boolean r2 = r9.startDragging(r2, r2)
            if (r2 != 0) goto Lce
            r9.stopDragging()
        Lce:
            int r3 = r9.getCurrentPosition()
            r9.mStartPosition = r3
            r9.mMoving = r1
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.stackdivider.DividerView.onTouch(android.view.View, android.view.MotionEvent):boolean");
    }

    private void logResizeEvent(DividerSnapAlgorithm.SnapTarget snapTarget) {
        if (snapTarget == this.mSnapAlgorithm.getDismissStartTarget()) {
            MetricsLogger.action(this.mContext, 390, dockSideTopLeft(this.mDockSide) ? 1 : 0);
        } else if (snapTarget == this.mSnapAlgorithm.getDismissEndTarget()) {
            MetricsLogger.action(this.mContext, 390, dockSideBottomRight(this.mDockSide) ? 1 : 0);
        } else if (snapTarget == this.mSnapAlgorithm.getMiddleTarget()) {
            MetricsLogger.action(this.mContext, 389, 0);
        } else {
            if (snapTarget == this.mSnapAlgorithm.getFirstSplitTarget()) {
                MetricsLogger.action(this.mContext, 389, dockSideTopLeft(this.mDockSide) ? 1 : 2);
            } else if (snapTarget == this.mSnapAlgorithm.getLastSplitTarget()) {
                MetricsLogger.action(this.mContext, 389, dockSideTopLeft(this.mDockSide) ? 2 : 1);
            }
        }
    }

    private void convertToScreenCoordinates(MotionEvent event) {
        event.setLocation(event.getRawX(), event.getRawY());
    }

    private void fling(int position, float velocity, boolean avoidDismissStart, boolean logMetrics) {
        DividerSnapAlgorithm currentSnapAlgorithm = getSnapAlgorithm();
        DividerSnapAlgorithm.SnapTarget snapTarget = currentSnapAlgorithm.calculateSnapTarget(position, velocity);
        if (avoidDismissStart && snapTarget == currentSnapAlgorithm.getDismissStartTarget()) {
            snapTarget = currentSnapAlgorithm.getFirstSplitTarget();
        }
        if (logMetrics) {
            logResizeEvent(snapTarget);
        }
        ValueAnimator anim = getFlingAnimator(position, snapTarget, 0L);
        this.mFlingAnimationUtils.apply(anim, position, snapTarget.position, velocity);
        anim.start();
    }

    private void flingTo(int position, DividerSnapAlgorithm.SnapTarget target, long duration, long startDelay, long endDelay, Interpolator interpolator) {
        ValueAnimator anim = getFlingAnimator(position, target, endDelay);
        anim.setDuration(duration);
        anim.setStartDelay(startDelay);
        anim.setInterpolator(interpolator);
        anim.start();
    }

    private ValueAnimator getFlingAnimator(int position, final DividerSnapAlgorithm.SnapTarget snapTarget, final long endDelay) {
        if (this.mCurrentAnimator != null) {
            cancelFlingAnimation();
            updateDockSide();
        }
        final boolean taskPositionSameAtEnd = snapTarget.flag == 0;
        ValueAnimator anim = ValueAnimator.ofInt(position, snapTarget.position);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.stackdivider.-$$Lambda$DividerView$o4c7SI5Mz67OwDjq6n3ndBTEfNk
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                DividerView.this.lambda$getFlingAnimator$0$DividerView(taskPositionSameAtEnd, snapTarget, valueAnimator);
            }
        });
        final Runnable endAction = new Runnable() { // from class: com.android.systemui.stackdivider.-$$Lambda$DividerView$fuKXuLmLQTYwkdmcrCKfS4vkKk8
            @Override // java.lang.Runnable
            public final void run() {
                DividerView.this.lambda$getFlingAnimator$1$DividerView(snapTarget);
            }
        };
        final Runnable notCancelledEndAction = new Runnable() { // from class: com.android.systemui.stackdivider.-$$Lambda$DividerView$PQ9UBhgXlQLedw0JrRm_JR-uZkk
            @Override // java.lang.Runnable
            public final void run() {
                DividerView.this.lambda$getFlingAnimator$2$DividerView();
            }
        };
        anim.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.stackdivider.DividerView.4
            private boolean mCancelled;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                DividerView.this.mHandler.removeMessages(0);
                this.mCancelled = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                long delay = 0;
                if (endDelay != 0) {
                    delay = endDelay;
                } else if (!this.mCancelled) {
                    if (DividerView.this.mSfChoreographer.getSurfaceFlingerOffsetMs() > 0) {
                        delay = DividerView.this.mSfChoreographer.getSurfaceFlingerOffsetMs();
                    }
                } else {
                    delay = 0;
                }
                if (delay == 0) {
                    if (!this.mCancelled) {
                        notCancelledEndAction.run();
                    }
                    endAction.run();
                    return;
                }
                if (!this.mCancelled) {
                    DividerView.this.mHandler.postDelayed(notCancelledEndAction, delay);
                }
                DividerView.this.mHandler.postDelayed(endAction, delay);
            }
        });
        this.mCurrentAnimator = anim;
        return anim;
    }

    public /* synthetic */ void lambda$getFlingAnimator$0$DividerView(boolean taskPositionSameAtEnd, DividerSnapAlgorithm.SnapTarget snapTarget, ValueAnimator animation) {
        int i;
        int intValue = ((Integer) animation.getAnimatedValue()).intValue();
        if (taskPositionSameAtEnd && animation.getAnimatedFraction() == 1.0f) {
            i = Integer.MAX_VALUE;
        } else {
            i = snapTarget.taskPosition;
        }
        resizeStackDelayed(intValue, i, snapTarget);
    }

    public /* synthetic */ void lambda$getFlingAnimator$1$DividerView(DividerSnapAlgorithm.SnapTarget snapTarget) {
        DividerSnapAlgorithm.SnapTarget saveTarget;
        commitSnapFlags(snapTarget);
        this.mWindowManagerProxy.setResizing(false);
        updateDockSide();
        this.mCurrentAnimator = null;
        this.mEntranceAnimationRunning = false;
        this.mExitAnimationRunning = false;
        DividerCallbacks dividerCallbacks = this.mCallback;
        if (dividerCallbacks != null) {
            dividerCallbacks.onDraggingEnd();
        }
        if (this.mHomeStackResizable && !this.mIsInMinimizeInteraction) {
            if (snapTarget.position < 0) {
                saveTarget = this.mSnapAlgorithm.getMiddleTarget();
            } else {
                saveTarget = snapTarget;
            }
            if (saveTarget.position != this.mSnapAlgorithm.getDismissEndTarget().position && saveTarget.position != this.mSnapAlgorithm.getDismissStartTarget().position) {
                saveSnapTargetBeforeMinimized(saveTarget);
            }
        }
    }

    public /* synthetic */ void lambda$getFlingAnimator$2$DividerView() {
        if (!this.mDockedStackMinimized && this.mIsInMinimizeInteraction) {
            this.mIsInMinimizeInteraction = false;
        }
    }

    private void cancelFlingAnimation() {
        ValueAnimator valueAnimator = this.mCurrentAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    private void commitSnapFlags(DividerSnapAlgorithm.SnapTarget target) {
        boolean dismissOrMaximize;
        if (target.flag == 0) {
            return;
        }
        boolean z = true;
        if (target.flag == 1) {
            int i = this.mDockSide;
            if (i != 1 && i != 2) {
                z = false;
            }
            dismissOrMaximize = z;
        } else {
            int i2 = this.mDockSide;
            if (i2 != 3 && i2 != 4) {
                z = false;
            }
            dismissOrMaximize = z;
        }
        if (dismissOrMaximize) {
            this.mWindowManagerProxy.dismissDockedStack();
        } else {
            this.mWindowManagerProxy.maximizeDockedStack();
        }
        this.mWindowManagerProxy.setResizeDimLayer(false, 0, 0.0f);
    }

    private void liftBackground() {
        if (this.mBackgroundLifted) {
            return;
        }
        if (isHorizontalDivision()) {
            this.mBackground.animate().scaleY(1.4f);
        } else {
            this.mBackground.animate().scaleX(1.4f);
        }
        this.mBackground.animate().setInterpolator(Interpolators.TOUCH_RESPONSE).setDuration(150L).translationZ(this.mTouchElevation).start();
        this.mHandle.animate().setInterpolator(Interpolators.TOUCH_RESPONSE).setDuration(150L).translationZ(this.mTouchElevation).start();
        this.mBackgroundLifted = true;
    }

    private void releaseBackground() {
        if (!this.mBackgroundLifted) {
            return;
        }
        this.mBackground.animate().setInterpolator(Interpolators.FAST_OUT_SLOW_IN).setDuration(200L).translationZ(0.0f).scaleX(1.0f).scaleY(1.0f).start();
        this.mHandle.animate().setInterpolator(Interpolators.FAST_OUT_SLOW_IN).setDuration(200L).translationZ(0.0f).start();
        this.mBackgroundLifted = false;
    }

    public void setMinimizedDockStack(boolean minimized, boolean isHomeStackResizable) {
        float width;
        this.mHomeStackResizable = isHomeStackResizable;
        updateDockSide();
        if (!minimized) {
            resetBackground();
        } else if (!isHomeStackResizable) {
            int i = this.mDockSide;
            if (i == 2) {
                this.mBackground.setPivotY(0.0f);
                this.mBackground.setScaleY(0.0f);
            } else if (i == 1 || i == 3) {
                View view = this.mBackground;
                if (this.mDockSide == 1) {
                    width = 0.0f;
                } else {
                    width = view.getWidth();
                }
                view.setPivotX(width);
                this.mBackground.setScaleX(0.0f);
            }
        }
        this.mMinimizedShadow.setAlpha(minimized ? 1.0f : 0.0f);
        if (!isHomeStackResizable) {
            this.mHandle.setAlpha(minimized ? 0.0f : 1.0f);
            this.mDockedStackMinimized = minimized;
        } else if (this.mDockedStackMinimized != minimized) {
            this.mDockedStackMinimized = minimized;
            if (this.mDisplayRotation != this.mDefaultDisplay.getRotation()) {
                WindowManagerWrapper.getInstance().getStableInsets(this.mStableInsets);
                repositionSnapTargetBeforeMinimized();
                updateDisplayInfo();
            } else {
                this.mMinimizedSnapAlgorithm = null;
                initializeSnapAlgorithm();
            }
            if (this.mIsInMinimizeInteraction != minimized || this.mCurrentAnimator != null) {
                cancelFlingAnimation();
                if (minimized) {
                    requestLayout();
                    this.mIsInMinimizeInteraction = true;
                    resizeStack(this.mMinimizedSnapAlgorithm.getMiddleTarget());
                    return;
                }
                resizeStack(this.mSnapTargetBeforeMinimized);
                this.mIsInMinimizeInteraction = false;
            }
        }
    }

    public void setMinimizedDockStack(boolean minimized, long animDuration, boolean isHomeStackResizable) {
        int currentPosition;
        DividerSnapAlgorithm.SnapTarget snapTarget;
        float width;
        this.mHomeStackResizable = isHomeStackResizable;
        updateDockSide();
        if (!isHomeStackResizable) {
            this.mMinimizedShadow.animate().alpha(minimized ? 1.0f : 0.0f).setInterpolator(Interpolators.ALPHA_IN).setDuration(animDuration).start();
            this.mHandle.animate().setInterpolator(Interpolators.FAST_OUT_SLOW_IN).setDuration(animDuration).alpha(minimized ? 0.0f : 1.0f).start();
            int i = this.mDockSide;
            if (i == 2) {
                this.mBackground.setPivotY(0.0f);
                this.mBackground.animate().scaleY(minimized ? 0.0f : 1.0f);
            } else if (i == 1 || i == 3) {
                View view = this.mBackground;
                if (this.mDockSide == 1) {
                    width = 0.0f;
                } else {
                    width = view.getWidth();
                }
                view.setPivotX(width);
                this.mBackground.animate().scaleX(minimized ? 0.0f : 1.0f);
            }
            this.mDockedStackMinimized = minimized;
        } else if (this.mDockedStackMinimized != minimized) {
            this.mIsInMinimizeInteraction = true;
            this.mMinimizedSnapAlgorithm = null;
            this.mDockedStackMinimized = minimized;
            initializeSnapAlgorithm();
            if (minimized) {
                currentPosition = this.mSnapTargetBeforeMinimized.position;
            } else {
                currentPosition = getCurrentPosition();
            }
            if (minimized) {
                snapTarget = this.mMinimizedSnapAlgorithm.getMiddleTarget();
            } else {
                snapTarget = this.mSnapTargetBeforeMinimized;
            }
            stopDragging(currentPosition, snapTarget, animDuration, Interpolators.FAST_OUT_SLOW_IN, 0L);
            setAdjustedForIme(false, animDuration);
        }
        if (!minimized) {
            this.mBackground.animate().withEndAction(this.mResetBackgroundRunnable);
        }
        this.mBackground.animate().setInterpolator(Interpolators.FAST_OUT_SLOW_IN).setDuration(animDuration).start();
    }

    public void setAdjustedForIme(boolean adjustedForIme) {
        updateDockSide();
        this.mHandle.setAlpha(adjustedForIme ? 0.0f : 1.0f);
        if (!adjustedForIme) {
            resetBackground();
        } else if (this.mDockSide == 2) {
            this.mBackground.setPivotY(0.0f);
            this.mBackground.setScaleY(0.5f);
        }
        this.mAdjustedForIme = adjustedForIme;
    }

    public void setAdjustedForIme(boolean adjustedForIme, long animDuration) {
        updateDockSide();
        this.mHandle.animate().setInterpolator(IME_ADJUST_INTERPOLATOR).setDuration(animDuration).alpha(adjustedForIme ? 0.0f : 1.0f).start();
        if (this.mDockSide == 2) {
            this.mBackground.setPivotY(0.0f);
            this.mBackground.animate().scaleY(adjustedForIme ? 0.5f : 1.0f);
        }
        if (!adjustedForIme) {
            this.mBackground.animate().withEndAction(this.mResetBackgroundRunnable);
        }
        this.mBackground.animate().setInterpolator(IME_ADJUST_INTERPOLATOR).setDuration(animDuration).start();
        this.mAdjustedForIme = adjustedForIme;
    }

    private void saveSnapTargetBeforeMinimized(DividerSnapAlgorithm.SnapTarget target) {
        this.mSnapTargetBeforeMinimized = target;
        this.mState.mRatioPositionBeforeMinimized = target.position / (isHorizontalDivision() ? this.mDisplayHeight : this.mDisplayWidth);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetBackground() {
        View view = this.mBackground;
        view.setPivotX(view.getWidth() / 2);
        View view2 = this.mBackground;
        view2.setPivotY(view2.getHeight() / 2);
        this.mBackground.setScaleX(1.0f);
        this.mBackground.setScaleY(1.0f);
        this.mMinimizedShadow.setAlpha(0.0f);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateDisplayInfo();
    }

    public void notifyDockSideChanged(int newDockSide) {
        int oldDockSide = this.mDockSide;
        this.mDockSide = newDockSide;
        this.mMinimizedShadow.setDockSide(this.mDockSide);
        requestLayout();
        WindowManagerWrapper.getInstance().getStableInsets(this.mStableInsets);
        this.mMinimizedSnapAlgorithm = null;
        initializeSnapAlgorithm();
        if ((oldDockSide == 1 && this.mDockSide == 3) || (oldDockSide == 3 && this.mDockSide == 1)) {
            repositionSnapTargetBeforeMinimized();
        }
        if (this.mHomeStackResizable && this.mDockedStackMinimized) {
            resizeStack(this.mMinimizedSnapAlgorithm.getMiddleTarget());
        }
    }

    private void repositionSnapTargetBeforeMinimized() {
        int position = (int) (this.mState.mRatioPositionBeforeMinimized * (isHorizontalDivision() ? this.mDisplayHeight : this.mDisplayWidth));
        this.mSnapAlgorithm = null;
        initializeSnapAlgorithm();
        this.mSnapTargetBeforeMinimized = this.mSnapAlgorithm.calculateNonDismissingSnapTarget(position);
    }

    private void updateDisplayInfo() {
        this.mDisplayRotation = this.mDefaultDisplay.getRotation();
        DisplayInfo info = new DisplayInfo();
        this.mDefaultDisplay.getDisplayInfo(info);
        this.mDisplayWidth = info.logicalWidth;
        this.mDisplayHeight = info.logicalHeight;
        this.mSnapAlgorithm = null;
        this.mMinimizedSnapAlgorithm = null;
        initializeSnapAlgorithm();
    }

    private int calculatePosition(int touchX, int touchY) {
        return isHorizontalDivision() ? calculateYPosition(touchY) : calculateXPosition(touchX);
    }

    public boolean isHorizontalDivision() {
        return getResources().getConfiguration().orientation == 1;
    }

    private int calculateXPosition(int touchX) {
        return (this.mStartPosition + touchX) - this.mStartX;
    }

    private int calculateYPosition(int touchY) {
        return (this.mStartPosition + touchY) - this.mStartY;
    }

    private void alignTopLeft(Rect containingRect, Rect rect) {
        int width = rect.width();
        int height = rect.height();
        rect.set(containingRect.left, containingRect.top, containingRect.left + width, containingRect.top + height);
    }

    private void alignBottomRight(Rect containingRect, Rect rect) {
        int width = rect.width();
        int height = rect.height();
        rect.set(containingRect.right - width, containingRect.bottom - height, containingRect.right, containingRect.bottom);
    }

    public void calculateBoundsForPosition(int position, int dockSide, Rect outRect) {
        DockedDividerUtils.calculateBoundsForPosition(position, dockSide, outRect, this.mDisplayWidth, this.mDisplayHeight, this.mDividerSize);
    }

    public void resizeStackDelayed(int position, int taskPosition, DividerSnapAlgorithm.SnapTarget taskSnapTarget) {
        Message message = this.mHandler.obtainMessage(0, position, taskPosition, taskSnapTarget);
        message.setAsynchronous(true);
        this.mSfChoreographer.scheduleAtSfVsync(this.mHandler, message);
    }

    private void resizeStack(DividerSnapAlgorithm.SnapTarget taskSnapTarget) {
        resizeStack(taskSnapTarget.position, taskSnapTarget.position, taskSnapTarget);
    }

    public void resizeStack(int position, int taskPosition, DividerSnapAlgorithm.SnapTarget taskSnapTarget) {
        if (!this.mRemoved) {
            calculateBoundsForPosition(position, this.mDockSide, this.mDockedRect);
            if (this.mDockedRect.equals(this.mLastResizeRect) && !this.mEntranceAnimationRunning) {
                return;
            }
            if (this.mBackground.getZ() > 0.0f) {
                this.mBackground.invalidate();
            }
            this.mLastResizeRect.set(this.mDockedRect);
            if (this.mHomeStackResizable && this.mIsInMinimizeInteraction) {
                calculateBoundsForPosition(this.mSnapTargetBeforeMinimized.position, this.mDockSide, this.mDockedTaskRect);
                calculateBoundsForPosition(this.mSnapTargetBeforeMinimized.position, DockedDividerUtils.invertDockSide(this.mDockSide), this.mOtherTaskRect);
                if (this.mDockSide == 3) {
                    this.mDockedTaskRect.offset((Math.max(position, this.mStableInsets.left - this.mDividerSize) - this.mDockedTaskRect.left) + this.mDividerSize, 0);
                }
                WindowManagerProxy windowManagerProxy = this.mWindowManagerProxy;
                Rect rect = this.mDockedRect;
                Rect rect2 = this.mDockedTaskRect;
                windowManagerProxy.resizeDockedStack(rect, rect2, rect2, this.mOtherTaskRect, null);
                return;
            }
            if (this.mEntranceAnimationRunning && taskPosition != Integer.MAX_VALUE) {
                calculateBoundsForPosition(taskPosition, this.mDockSide, this.mDockedTaskRect);
                if (this.mDockSide == 3) {
                    this.mDockedTaskRect.offset((Math.max(position, this.mStableInsets.left - this.mDividerSize) - this.mDockedTaskRect.left) + this.mDividerSize, 0);
                }
                calculateBoundsForPosition(taskPosition, DockedDividerUtils.invertDockSide(this.mDockSide), this.mOtherTaskRect);
                this.mWindowManagerProxy.resizeDockedStack(this.mDockedRect, this.mDockedTaskRect, null, this.mOtherTaskRect, null);
            } else if (this.mExitAnimationRunning && taskPosition != Integer.MAX_VALUE) {
                calculateBoundsForPosition(taskPosition, this.mDockSide, this.mDockedTaskRect);
                this.mDockedInsetRect.set(this.mDockedTaskRect);
                calculateBoundsForPosition(this.mExitStartPosition, DockedDividerUtils.invertDockSide(this.mDockSide), this.mOtherTaskRect);
                this.mOtherInsetRect.set(this.mOtherTaskRect);
                applyExitAnimationParallax(this.mOtherTaskRect, position);
                if (this.mDockSide == 3) {
                    this.mDockedTaskRect.offset((position - this.mStableInsets.left) + this.mDividerSize, 0);
                }
                this.mWindowManagerProxy.resizeDockedStack(this.mDockedRect, this.mDockedTaskRect, this.mDockedInsetRect, this.mOtherTaskRect, this.mOtherInsetRect);
            } else if (taskPosition != Integer.MAX_VALUE) {
                calculateBoundsForPosition(position, DockedDividerUtils.invertDockSide(this.mDockSide), this.mOtherRect);
                int dockSideInverted = DockedDividerUtils.invertDockSide(this.mDockSide);
                int taskPositionDocked = restrictDismissingTaskPosition(taskPosition, this.mDockSide, taskSnapTarget);
                int taskPositionOther = restrictDismissingTaskPosition(taskPosition, dockSideInverted, taskSnapTarget);
                calculateBoundsForPosition(taskPositionDocked, this.mDockSide, this.mDockedTaskRect);
                calculateBoundsForPosition(taskPositionOther, dockSideInverted, this.mOtherTaskRect);
                this.mTmpRect.set(0, 0, this.mDisplayWidth, this.mDisplayHeight);
                alignTopLeft(this.mDockedRect, this.mDockedTaskRect);
                alignTopLeft(this.mOtherRect, this.mOtherTaskRect);
                this.mDockedInsetRect.set(this.mDockedTaskRect);
                this.mOtherInsetRect.set(this.mOtherTaskRect);
                if (dockSideTopLeft(this.mDockSide)) {
                    alignTopLeft(this.mTmpRect, this.mDockedInsetRect);
                    alignBottomRight(this.mTmpRect, this.mOtherInsetRect);
                } else {
                    alignBottomRight(this.mTmpRect, this.mDockedInsetRect);
                    alignTopLeft(this.mTmpRect, this.mOtherInsetRect);
                }
                applyDismissingParallax(this.mDockedTaskRect, this.mDockSide, taskSnapTarget, position, taskPositionDocked);
                applyDismissingParallax(this.mOtherTaskRect, dockSideInverted, taskSnapTarget, position, taskPositionOther);
                this.mWindowManagerProxy.resizeDockedStack(this.mDockedRect, this.mDockedTaskRect, this.mDockedInsetRect, this.mOtherTaskRect, this.mOtherInsetRect);
            } else {
                this.mWindowManagerProxy.resizeDockedStack(this.mDockedRect, null, null, null, null);
            }
            DividerSnapAlgorithm.SnapTarget closestDismissTarget = getSnapAlgorithm().getClosestDismissTarget(position);
            float dimFraction = getDimFraction(position, closestDismissTarget);
            this.mWindowManagerProxy.setResizeDimLayer(dimFraction != 0.0f, getWindowingModeForDismissTarget(closestDismissTarget), dimFraction);
        }
    }

    private void applyExitAnimationParallax(Rect taskRect, int position) {
        int i = this.mDockSide;
        if (i == 2) {
            taskRect.offset(0, (int) ((position - this.mExitStartPosition) * 0.25f));
        } else if (i == 1) {
            taskRect.offset((int) ((position - this.mExitStartPosition) * 0.25f), 0);
        } else if (i == 3) {
            taskRect.offset((int) ((this.mExitStartPosition - position) * 0.25f), 0);
        }
    }

    private float getDimFraction(int position, DividerSnapAlgorithm.SnapTarget dismissTarget) {
        if (this.mEntranceAnimationRunning) {
            return 0.0f;
        }
        float fraction = getSnapAlgorithm().calculateDismissingFraction(position);
        float fraction2 = DIM_INTERPOLATOR.getInterpolation(Math.max(0.0f, Math.min(fraction, 1.0f)));
        if (hasInsetsAtDismissTarget(dismissTarget)) {
            return fraction2 * 0.8f;
        }
        return fraction2;
    }

    private boolean hasInsetsAtDismissTarget(DividerSnapAlgorithm.SnapTarget dismissTarget) {
        return isHorizontalDivision() ? dismissTarget == getSnapAlgorithm().getDismissStartTarget() ? this.mStableInsets.top != 0 : this.mStableInsets.bottom != 0 : dismissTarget == getSnapAlgorithm().getDismissStartTarget() ? this.mStableInsets.left != 0 : this.mStableInsets.right != 0;
    }

    private int restrictDismissingTaskPosition(int taskPosition, int dockSide, DividerSnapAlgorithm.SnapTarget snapTarget) {
        if (snapTarget.flag == 1 && dockSideTopLeft(dockSide)) {
            return Math.max(this.mSnapAlgorithm.getFirstSplitTarget().position, this.mStartPosition);
        }
        if (snapTarget.flag == 2 && dockSideBottomRight(dockSide)) {
            return Math.min(this.mSnapAlgorithm.getLastSplitTarget().position, this.mStartPosition);
        }
        return taskPosition;
    }

    private void applyDismissingParallax(Rect taskRect, int dockSide, DividerSnapAlgorithm.SnapTarget snapTarget, int position, int taskPosition) {
        float fraction = Math.min(1.0f, Math.max(0.0f, this.mSnapAlgorithm.calculateDismissingFraction(position)));
        DividerSnapAlgorithm.SnapTarget dismissTarget = null;
        DividerSnapAlgorithm.SnapTarget splitTarget = null;
        int start = 0;
        if (position <= this.mSnapAlgorithm.getLastSplitTarget().position && dockSideTopLeft(dockSide)) {
            dismissTarget = this.mSnapAlgorithm.getDismissStartTarget();
            splitTarget = this.mSnapAlgorithm.getFirstSplitTarget();
            start = taskPosition;
        } else if (position >= this.mSnapAlgorithm.getLastSplitTarget().position && dockSideBottomRight(dockSide)) {
            dismissTarget = this.mSnapAlgorithm.getDismissEndTarget();
            splitTarget = this.mSnapAlgorithm.getLastSplitTarget();
            start = splitTarget.position;
        }
        if (dismissTarget != null && fraction > 0.0f && isDismissing(splitTarget, position, dockSide)) {
            int offsetPosition = (int) (start + ((dismissTarget.position - splitTarget.position) * calculateParallaxDismissingFraction(fraction, dockSide)));
            int width = taskRect.width();
            int height = taskRect.height();
            if (dockSide == 1) {
                taskRect.left = offsetPosition - width;
                taskRect.right = offsetPosition;
            } else if (dockSide == 2) {
                taskRect.top = offsetPosition - height;
                taskRect.bottom = offsetPosition;
            } else if (dockSide == 3) {
                int i = this.mDividerSize;
                taskRect.left = offsetPosition + i;
                taskRect.right = offsetPosition + width + i;
            } else if (dockSide == 4) {
                int i2 = this.mDividerSize;
                taskRect.top = offsetPosition + i2;
                taskRect.bottom = offsetPosition + height + i2;
            }
        }
    }

    private static float calculateParallaxDismissingFraction(float fraction, int dockSide) {
        float result = SLOWDOWN_INTERPOLATOR.getInterpolation(fraction) / 3.5f;
        if (dockSide == 2) {
            return result / 2.0f;
        }
        return result;
    }

    private static boolean isDismissing(DividerSnapAlgorithm.SnapTarget snapTarget, int position, int dockSide) {
        return (dockSide == 2 || dockSide == 1) ? position < snapTarget.position : position > snapTarget.position;
    }

    private int getWindowingModeForDismissTarget(DividerSnapAlgorithm.SnapTarget dismissTarget) {
        if (dismissTarget.flag != 1 || !dockSideTopLeft(this.mDockSide)) {
            if (dismissTarget.flag == 2 && dockSideBottomRight(this.mDockSide)) {
                return 3;
            }
            return 4;
        }
        return 3;
    }

    private static boolean dockSideTopLeft(int dockSide) {
        return dockSide == 2 || dockSide == 1;
    }

    private static boolean dockSideBottomRight(int dockSide) {
        return dockSide == 4 || dockSide == 3;
    }

    public void onComputeInternalInsets(ViewTreeObserver.InternalInsetsInfo inoutInfo) {
        inoutInfo.setTouchableInsets(3);
        inoutInfo.touchableRegion.set(this.mHandle.getLeft(), this.mHandle.getTop(), this.mHandle.getRight(), this.mHandle.getBottom());
        inoutInfo.touchableRegion.op(this.mBackground.getLeft(), this.mBackground.getTop(), this.mBackground.getRight(), this.mBackground.getBottom(), Region.Op.UNION);
    }

    public int growsRecents() {
        boolean result = this.mGrowRecents && this.mDockSide == 2 && getCurrentPosition() == getSnapAlgorithm().getLastSplitTarget().position;
        if (result) {
            return getSnapAlgorithm().getMiddleTarget().position;
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onRecentsActivityStarting() {
        if (this.mGrowRecents && this.mDockSide == 2 && getSnapAlgorithm().getMiddleTarget() != getSnapAlgorithm().getLastSplitTarget() && getCurrentPosition() == getSnapAlgorithm().getLastSplitTarget().position) {
            this.mState.growAfterRecentsDrawn = true;
            startDragging(false, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDockedFirstAnimationFrame() {
        saveSnapTargetBeforeMinimized(this.mSnapAlgorithm.getMiddleTarget());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDockedTopTask() {
        DividerState dividerState = this.mState;
        dividerState.growAfterRecentsDrawn = false;
        dividerState.animateAfterRecentsDrawn = true;
        startDragging(false, false);
        updateDockSide();
        this.mEntranceAnimationRunning = true;
        resizeStack(calculatePositionForInsetBounds(), this.mSnapAlgorithm.getMiddleTarget().position, this.mSnapAlgorithm.getMiddleTarget());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onRecentsDrawn() {
        updateDockSide();
        final int position = calculatePositionForInsetBounds();
        if (this.mState.animateAfterRecentsDrawn) {
            this.mState.animateAfterRecentsDrawn = false;
            this.mHandler.post(new Runnable() { // from class: com.android.systemui.stackdivider.-$$Lambda$DividerView$6QXub7h9g3mZn2yBpbDKCxY_TW4
                @Override // java.lang.Runnable
                public final void run() {
                    DividerView.this.lambda$onRecentsDrawn$3$DividerView(position);
                }
            });
        }
        if (this.mState.growAfterRecentsDrawn) {
            this.mState.growAfterRecentsDrawn = false;
            updateDockSide();
            DividerCallbacks dividerCallbacks = this.mCallback;
            if (dividerCallbacks != null) {
                dividerCallbacks.growRecents();
            }
            stopDragging(position, getSnapAlgorithm().getMiddleTarget(), 336L, Interpolators.FAST_OUT_SLOW_IN);
        }
    }

    public /* synthetic */ void lambda$onRecentsDrawn$3$DividerView(int position) {
        stopDragging(position, getSnapAlgorithm().getMiddleTarget(), this.mLongPressEntraceAnimDuration, Interpolators.FAST_OUT_SLOW_IN, 200L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onUndockingTask() {
        DividerSnapAlgorithm.SnapTarget target;
        int dockSide = this.mWindowManagerProxy.getDockSide();
        if (dockSide != -1) {
            if (this.mHomeStackResizable || !this.mDockedStackMinimized) {
                startDragging(false, false);
                if (dockSideTopLeft(dockSide)) {
                    target = this.mSnapAlgorithm.getDismissEndTarget();
                } else {
                    target = this.mSnapAlgorithm.getDismissStartTarget();
                }
                this.mExitAnimationRunning = true;
                this.mExitStartPosition = getCurrentPosition();
                stopDragging(this.mExitStartPosition, target, 336L, 100L, 0L, Interpolators.FAST_OUT_SLOW_IN);
            }
        }
    }

    private int calculatePositionForInsetBounds() {
        this.mTmpRect.set(0, 0, this.mDisplayWidth, this.mDisplayHeight);
        this.mTmpRect.inset(this.mStableInsets);
        return DockedDividerUtils.calculatePositionForBounds(this.mTmpRect, this.mDockSide, this.mDividerSize);
    }
}
