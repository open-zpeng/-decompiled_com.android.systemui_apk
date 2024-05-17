package com.android.systemui.bubbles;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.util.StatsLog;
import android.view.Choreographer;
import android.view.DisplayCutout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.widget.ViewClippingUtil;
import com.android.systemui.R;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.bubbles.animation.ExpandedAnimationController;
import com.android.systemui.bubbles.animation.PhysicsAnimationLayout;
import com.android.systemui.bubbles.animation.StackAnimationController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes21.dex */
public class BubbleStackView extends FrameLayout {
    private static final float DARKEN_PERCENT = 0.3f;
    private static final SurfaceSynchronizer DEFAULT_SURFACE_SYNCHRONIZER = new SurfaceSynchronizer() { // from class: com.android.systemui.bubbles.BubbleStackView.1
        @Override // com.android.systemui.bubbles.BubbleStackView.SurfaceSynchronizer
        public void syncSurfaceAndRun(final Runnable callback) {
            Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() { // from class: com.android.systemui.bubbles.BubbleStackView.1.1
                private int mFrameWait = 2;

                @Override // android.view.Choreographer.FrameCallback
                public void doFrame(long frameTimeNanos) {
                    int i = this.mFrameWait - 1;
                    this.mFrameWait = i;
                    if (i > 0) {
                        Choreographer.getInstance().postFrameCallback(this);
                    } else {
                        callback.run();
                    }
                }
            });
        }
    };
    private static final int FLYOUT_ALPHA_ANIMATION_DURATION = 100;
    private static final float FLYOUT_DISMISS_VELOCITY = 2000.0f;
    static final float FLYOUT_DRAG_PERCENT_DISMISS = 0.25f;
    @VisibleForTesting
    static final int FLYOUT_HIDE_AFTER = 5000;
    private static final float FLYOUT_OVERSCROLL_ATTENUATION_FACTOR = 8.0f;
    private static final String TAG = "Bubbles";
    private final DynamicAnimation.OnAnimationEndListener mAfterFlyoutTransitionSpring;
    private Runnable mAfterMagnet;
    private Runnable mAnimateInFlyout;
    private boolean mAnimatingMagnet;
    private PhysicsAnimationLayout mBubbleContainer;
    private final BubbleData mBubbleData;
    private BubbleIconFactory mBubbleIconFactory;
    private int mBubblePaddingTop;
    private int mBubbleSize;
    @Nullable
    private Bubble mBubbleToExpandAfterFlyoutCollapse;
    private int mBubbleTouchPadding;
    private ViewClippingUtil.ClippingParameters mClippingParameters;
    private final ValueAnimator mDesaturateAndDarkenAnimator;
    private final Paint mDesaturateAndDarkenPaint;
    private View mDesaturateAndDarkenTargetView;
    private BubbleDismissView mDismissContainer;
    private Point mDisplaySize;
    private boolean mDraggingInDismissTarget;
    private BubbleController.BubbleExpandListener mExpandListener;
    private int mExpandedAnimateXDistance;
    private int mExpandedAnimateYDistance;
    private ExpandedAnimationController mExpandedAnimationController;
    private Bubble mExpandedBubble;
    private FrameLayout mExpandedViewContainer;
    private int mExpandedViewPadding;
    private final SpringAnimation mExpandedViewXAnim;
    private final SpringAnimation mExpandedViewYAnim;
    private BubbleFlyoutView mFlyout;
    private final FloatPropertyCompat mFlyoutCollapseProperty;
    private float mFlyoutDragDeltaX;
    private Runnable mFlyoutOnHide;
    private final SpringAnimation mFlyoutTransitionSpring;
    private Runnable mHideFlyout;
    private int mImeOffset;
    private LayoutInflater mInflater;
    private boolean mIsExpanded;
    private boolean mIsExpansionAnimating;
    private boolean mIsGestureInProgress;
    private int mOrientation;
    private View.OnLayoutChangeListener mOrientationChangedListener;
    private int mPointerHeight;
    private boolean mShowingDismiss;
    private StackAnimationController mStackAnimationController;
    private boolean mStackOnLeftOrWillBe;
    private int mStatusBarHeight;
    private final SurfaceSynchronizer mSurfaceSynchronizer;
    private ViewTreeObserver.OnDrawListener mSystemGestureExcludeUpdater;
    private final List<Rect> mSystemGestureExclusionRects;
    int[] mTempLoc;
    RectF mTempRect;
    private BubbleTouchHandler mTouchHandler;
    private float mVerticalPosPercentBeforeRotation;
    private final Vibrator mVibrator;
    private boolean mViewUpdatedRequested;
    private ViewTreeObserver.OnPreDrawListener mViewUpdater;
    private boolean mWasOnLeftBeforeRotation;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public interface SurfaceSynchronizer {
        void syncSurfaceAndRun(Runnable runnable);
    }

    public /* synthetic */ void lambda$new$0$BubbleStackView() {
        animateFlyoutCollapsed(true, 0.0f);
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("Stack view state:");
        pw.print("  gestureInProgress:    ");
        pw.println(this.mIsGestureInProgress);
        pw.print("  showingDismiss:       ");
        pw.println(this.mShowingDismiss);
        pw.print("  isExpansionAnimating: ");
        pw.println(this.mIsExpansionAnimating);
        pw.print("  draggingInDismiss:    ");
        pw.println(this.mDraggingInDismissTarget);
        pw.print("  animatingMagnet:      ");
        pw.println(this.mAnimatingMagnet);
        this.mStackAnimationController.dump(fd, pw, args);
        this.mExpandedAnimationController.dump(fd, pw, args);
    }

    public /* synthetic */ void lambda$new$1$BubbleStackView(DynamicAnimation dynamicAnimation, boolean b, float v, float v1) {
        if (this.mFlyoutDragDeltaX == 0.0f) {
            this.mFlyout.postDelayed(this.mHideFlyout, 5000L);
        } else {
            this.mFlyout.hideFlyout();
        }
    }

    public BubbleStackView(Context context, BubbleData data, @Nullable SurfaceSynchronizer synchronizer) {
        super(context);
        this.mDesaturateAndDarkenPaint = new Paint();
        this.mHideFlyout = new Runnable() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleStackView$jXS10HgKCVgyvjX1UcSgdO2D_ug
            @Override // java.lang.Runnable
            public final void run() {
                BubbleStackView.this.lambda$new$0$BubbleStackView();
            }
        };
        this.mWasOnLeftBeforeRotation = false;
        this.mVerticalPosPercentBeforeRotation = -1.0f;
        this.mStackOnLeftOrWillBe = false;
        this.mIsGestureInProgress = false;
        this.mViewUpdatedRequested = false;
        this.mIsExpansionAnimating = false;
        this.mShowingDismiss = false;
        this.mDraggingInDismissTarget = false;
        this.mAnimatingMagnet = false;
        this.mTempLoc = new int[2];
        this.mTempRect = new RectF();
        this.mSystemGestureExclusionRects = Collections.singletonList(new Rect());
        this.mViewUpdater = new ViewTreeObserver.OnPreDrawListener() { // from class: com.android.systemui.bubbles.BubbleStackView.2
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                BubbleStackView.this.getViewTreeObserver().removeOnPreDrawListener(BubbleStackView.this.mViewUpdater);
                BubbleStackView.this.updateExpandedView();
                BubbleStackView.this.mViewUpdatedRequested = false;
                return true;
            }
        };
        this.mSystemGestureExcludeUpdater = new ViewTreeObserver.OnDrawListener() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleStackView$c-OiserdP7VIvU56hCAARnBncEE
            @Override // android.view.ViewTreeObserver.OnDrawListener
            public final void onDraw() {
                BubbleStackView.this.updateSystemGestureExcludeRects();
            }
        };
        this.mClippingParameters = new ViewClippingUtil.ClippingParameters() { // from class: com.android.systemui.bubbles.BubbleStackView.3
            public boolean shouldFinish(View view) {
                return false;
            }

            public boolean isClippingEnablingAllowed(View view) {
                return !BubbleStackView.this.mIsExpanded;
            }
        };
        this.mFlyoutCollapseProperty = new FloatPropertyCompat("FlyoutCollapseSpring") { // from class: com.android.systemui.bubbles.BubbleStackView.4
            @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
            public float getValue(Object o) {
                return BubbleStackView.this.mFlyoutDragDeltaX;
            }

            @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
            public void setValue(Object o, float v) {
                BubbleStackView.this.onFlyoutDragged(v);
            }
        };
        this.mFlyoutTransitionSpring = new SpringAnimation(this, this.mFlyoutCollapseProperty);
        this.mFlyoutDragDeltaX = 0.0f;
        this.mAfterFlyoutTransitionSpring = new DynamicAnimation.OnAnimationEndListener() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleStackView$qNTN7f0ovKQkRVyENDOFd8Z5ydA
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                BubbleStackView.this.lambda$new$1$BubbleStackView(dynamicAnimation, z, f, f2);
            }
        };
        this.mOrientation = 0;
        this.mBubbleToExpandAfterFlyoutCollapse = null;
        this.mBubbleData = data;
        this.mInflater = LayoutInflater.from(context);
        this.mTouchHandler = new BubbleTouchHandler(this, data, context);
        setOnTouchListener(this.mTouchHandler);
        this.mInflater = LayoutInflater.from(context);
        Resources res = getResources();
        this.mBubbleSize = res.getDimensionPixelSize(R.dimen.individual_bubble_size);
        this.mBubblePaddingTop = res.getDimensionPixelSize(R.dimen.bubble_padding_top);
        this.mBubbleTouchPadding = res.getDimensionPixelSize(R.dimen.bubble_touch_padding);
        this.mExpandedAnimateXDistance = res.getDimensionPixelSize(R.dimen.bubble_expanded_animate_x_distance);
        this.mExpandedAnimateYDistance = res.getDimensionPixelSize(R.dimen.bubble_expanded_animate_y_distance);
        this.mPointerHeight = res.getDimensionPixelSize(R.dimen.bubble_pointer_height);
        this.mStatusBarHeight = res.getDimensionPixelSize(17105438);
        this.mImeOffset = res.getDimensionPixelSize(R.dimen.pip_ime_offset);
        this.mDisplaySize = new Point();
        WindowManager wm = (WindowManager) context.getSystemService("window");
        wm.getDefaultDisplay().getRealSize(this.mDisplaySize);
        this.mVibrator = (Vibrator) context.getSystemService("vibrator");
        this.mExpandedViewPadding = res.getDimensionPixelSize(R.dimen.bubble_expanded_view_padding);
        int elevation = res.getDimensionPixelSize(R.dimen.bubble_elevation);
        this.mStackAnimationController = new StackAnimationController();
        this.mExpandedAnimationController = new ExpandedAnimationController(this.mDisplaySize, this.mExpandedViewPadding, res.getConfiguration().orientation);
        this.mSurfaceSynchronizer = synchronizer != null ? synchronizer : DEFAULT_SURFACE_SYNCHRONIZER;
        this.mBubbleContainer = new PhysicsAnimationLayout(context);
        this.mBubbleContainer.setActiveController(this.mStackAnimationController);
        this.mBubbleContainer.setElevation(elevation);
        this.mBubbleContainer.setClipChildren(false);
        addView(this.mBubbleContainer, new FrameLayout.LayoutParams(-1, -1));
        this.mBubbleIconFactory = new BubbleIconFactory(context);
        this.mExpandedViewContainer = new FrameLayout(context);
        this.mExpandedViewContainer.setElevation(elevation);
        FrameLayout frameLayout = this.mExpandedViewContainer;
        int i = this.mExpandedViewPadding;
        frameLayout.setPadding(i, i, i, i);
        this.mExpandedViewContainer.setClipChildren(false);
        addView(this.mExpandedViewContainer);
        setUpFlyout();
        this.mFlyoutTransitionSpring.setSpring(new SpringForce().setStiffness(200.0f).setDampingRatio(0.75f));
        this.mFlyoutTransitionSpring.addEndListener(this.mAfterFlyoutTransitionSpring);
        this.mDismissContainer = new BubbleDismissView(this.mContext);
        this.mDismissContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, getResources().getDimensionPixelSize(R.dimen.pip_dismiss_gradient_height), 80));
        addView(this.mDismissContainer);
        this.mExpandedViewXAnim = new SpringAnimation(this.mExpandedViewContainer, DynamicAnimation.TRANSLATION_X);
        this.mExpandedViewXAnim.setSpring(new SpringForce().setStiffness(200.0f).setDampingRatio(0.75f));
        this.mExpandedViewYAnim = new SpringAnimation(this.mExpandedViewContainer, DynamicAnimation.TRANSLATION_Y);
        this.mExpandedViewYAnim.setSpring(new SpringForce().setStiffness(200.0f).setDampingRatio(0.75f));
        this.mExpandedViewYAnim.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleStackView$pASZEuVtfFyo-FF2s4CpK8srlzg
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                BubbleStackView.this.lambda$new$2$BubbleStackView(dynamicAnimation, z, f, f2);
            }
        });
        setClipChildren(false);
        setFocusable(true);
        this.mBubbleContainer.bringToFront();
        setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleStackView$JEhiIzPncR72OLevX_9noDIsyDo
            @Override // android.view.View.OnApplyWindowInsetsListener
            public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                return BubbleStackView.this.lambda$new$4$BubbleStackView(view, windowInsets);
            }
        });
        this.mOrientationChangedListener = new View.OnLayoutChangeListener() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleStackView$zB8p0_cj-tonbCXvIH4kDoBtabk
            @Override // android.view.View.OnLayoutChangeListener
            public final void onLayoutChange(View view, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
                BubbleStackView.this.lambda$new$6$BubbleStackView(view, i2, i3, i4, i5, i6, i7, i8, i9);
            }
        };
        getViewTreeObserver().addOnDrawListener(this.mSystemGestureExcludeUpdater);
        final ColorMatrix animatedMatrix = new ColorMatrix();
        final ColorMatrix darkenMatrix = new ColorMatrix();
        this.mDesaturateAndDarkenAnimator = ValueAnimator.ofFloat(1.0f, 0.0f);
        this.mDesaturateAndDarkenAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleStackView$nTtH9EoKZ3I47Rp-Pl0BGULUUeI
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                BubbleStackView.this.lambda$new$7$BubbleStackView(animatedMatrix, darkenMatrix, valueAnimator);
            }
        });
    }

    public /* synthetic */ void lambda$new$2$BubbleStackView(DynamicAnimation anim, boolean cancelled, float value, float velocity) {
        Bubble bubble;
        if (this.mIsExpanded && (bubble = this.mExpandedBubble) != null) {
            bubble.getExpandedView().updateView();
        }
    }

    public /* synthetic */ WindowInsets lambda$new$4$BubbleStackView(View view, final WindowInsets insets) {
        if (!this.mIsExpanded || this.mIsExpansionAnimating) {
            return view.onApplyWindowInsets(insets);
        }
        this.mExpandedAnimationController.updateYPosition(new Runnable() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleStackView$SGgilPVMr7ds9JBrPWP0ZRoSeUQ
            @Override // java.lang.Runnable
            public final void run() {
                BubbleStackView.this.lambda$new$3$BubbleStackView(insets);
            }
        });
        return view.onApplyWindowInsets(insets);
    }

    public /* synthetic */ void lambda$new$3$BubbleStackView(WindowInsets insets) {
        this.mExpandedBubble.getExpandedView().updateInsets(insets);
    }

    public /* synthetic */ void lambda$new$6$BubbleStackView(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        this.mExpandedAnimationController.updateOrientation(this.mOrientation, this.mDisplaySize);
        this.mStackAnimationController.updateOrientation(this.mOrientation);
        if (this.mIsExpanded) {
            this.mExpandedViewContainer.setTranslationY(getExpandedViewY());
            this.mExpandedBubble.getExpandedView().updateView();
        }
        WindowInsets insets = getRootWindowInsets();
        int leftPadding = this.mExpandedViewPadding;
        int rightPadding = this.mExpandedViewPadding;
        if (insets != null) {
            int cutoutLeft = 0;
            int cutoutRight = 0;
            DisplayCutout cutout = insets.getDisplayCutout();
            if (cutout != null) {
                cutoutLeft = cutout.getSafeInsetLeft();
                cutoutRight = cutout.getSafeInsetRight();
            }
            leftPadding += Math.max(cutoutLeft, insets.getStableInsetLeft());
            rightPadding += Math.max(cutoutRight, insets.getStableInsetRight());
        }
        FrameLayout frameLayout = this.mExpandedViewContainer;
        int i = this.mExpandedViewPadding;
        frameLayout.setPadding(leftPadding, i, rightPadding, i);
        if (this.mIsExpanded) {
            this.mExpandedAnimationController.expandFromStack(new Runnable() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleStackView$BhIZ4rN3xHvYX6KiS0mXLMuJu-g
                @Override // java.lang.Runnable
                public final void run() {
                    BubbleStackView.this.lambda$new$5$BubbleStackView();
                }
            });
        }
        float f = this.mVerticalPosPercentBeforeRotation;
        if (f >= 0.0f) {
            this.mStackAnimationController.moveStackToSimilarPositionAfterRotation(this.mWasOnLeftBeforeRotation, f);
        }
        removeOnLayoutChangeListener(this.mOrientationChangedListener);
    }

    public /* synthetic */ void lambda$new$7$BubbleStackView(ColorMatrix animatedMatrix, ColorMatrix darkenMatrix, ValueAnimator animation) {
        float animatedValue = ((Float) animation.getAnimatedValue()).floatValue();
        animatedMatrix.setSaturation(animatedValue);
        float animatedDarkenValue = (1.0f - animatedValue) * DARKEN_PERCENT;
        darkenMatrix.setScale(1.0f - animatedDarkenValue, 1.0f - animatedDarkenValue, 1.0f - animatedDarkenValue, 1.0f);
        animatedMatrix.postConcat(darkenMatrix);
        this.mDesaturateAndDarkenPaint.setColorFilter(new ColorMatrixColorFilter(animatedMatrix));
        this.mDesaturateAndDarkenTargetView.setLayerPaint(this.mDesaturateAndDarkenPaint);
    }

    private void setUpFlyout() {
        BubbleFlyoutView bubbleFlyoutView = this.mFlyout;
        if (bubbleFlyoutView != null) {
            removeView(bubbleFlyoutView);
        }
        this.mFlyout = new BubbleFlyoutView(getContext());
        this.mFlyout.setVisibility(8);
        this.mFlyout.animate().setDuration(100L).setInterpolator(new AccelerateDecelerateInterpolator());
        addView(this.mFlyout, new FrameLayout.LayoutParams(-2, -2));
    }

    public void onThemeChanged() {
        this.mBubbleIconFactory = new BubbleIconFactory(this.mContext);
        setUpFlyout();
        for (Bubble b : this.mBubbleData.getBubbles()) {
            b.getIconView().setBubbleIconFactory(this.mBubbleIconFactory);
            b.getIconView().updateViews();
            b.getExpandedView().applyThemeAttrs();
        }
    }

    public void onOrientationChanged(int orientation) {
        this.mOrientation = orientation;
        WindowManager wm = (WindowManager) getContext().getSystemService("window");
        wm.getDefaultDisplay().getRealSize(this.mDisplaySize);
        Resources res = getContext().getResources();
        this.mStatusBarHeight = res.getDimensionPixelSize(17105438);
        this.mBubblePaddingTop = res.getDimensionPixelSize(R.dimen.bubble_padding_top);
        RectF allowablePos = this.mStackAnimationController.getAllowableStackPositionRegion();
        this.mWasOnLeftBeforeRotation = this.mStackAnimationController.isStackOnLeftSide();
        this.mVerticalPosPercentBeforeRotation = (this.mStackAnimationController.getStackPosition().y - allowablePos.top) / (allowablePos.bottom - allowablePos.top);
        addOnLayoutChangeListener(this.mOrientationChangedListener);
        hideFlyoutImmediate();
    }

    public void getBoundsOnScreen(Rect outRect, boolean clipToParent) {
        getBoundsOnScreen(outRect);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnPreDrawListener(this.mViewUpdater);
    }

    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfoInternal(info);
        AccessibilityNodeInfo.AccessibilityAction moveTopLeft = new AccessibilityNodeInfo.AccessibilityAction(R.id.action_move_top_left, getContext().getResources().getString(R.string.bubble_accessibility_action_move_top_left));
        info.addAction(moveTopLeft);
        AccessibilityNodeInfo.AccessibilityAction moveTopRight = new AccessibilityNodeInfo.AccessibilityAction(R.id.action_move_top_right, getContext().getResources().getString(R.string.bubble_accessibility_action_move_top_right));
        info.addAction(moveTopRight);
        AccessibilityNodeInfo.AccessibilityAction moveBottomLeft = new AccessibilityNodeInfo.AccessibilityAction(R.id.action_move_bottom_left, getContext().getResources().getString(R.string.bubble_accessibility_action_move_bottom_left));
        info.addAction(moveBottomLeft);
        AccessibilityNodeInfo.AccessibilityAction moveBottomRight = new AccessibilityNodeInfo.AccessibilityAction(R.id.action_move_bottom_right, getContext().getResources().getString(R.string.bubble_accessibility_action_move_bottom_right));
        info.addAction(moveBottomRight);
        info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_DISMISS);
        if (this.mIsExpanded) {
            info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_COLLAPSE);
        } else {
            info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_EXPAND);
        }
    }

    public boolean performAccessibilityActionInternal(int action, Bundle arguments) {
        if (super.performAccessibilityActionInternal(action, arguments)) {
            return true;
        }
        RectF stackBounds = this.mStackAnimationController.getAllowableStackPositionRegion();
        if (action == 1048576) {
            this.mBubbleData.dismissAll(6);
            return true;
        } else if (action == 524288) {
            this.mBubbleData.setExpanded(false);
            return true;
        } else if (action == 262144) {
            this.mBubbleData.setExpanded(true);
            return true;
        } else if (action == R.id.action_move_top_left) {
            this.mStackAnimationController.springStack(stackBounds.left, stackBounds.top);
            return true;
        } else if (action == R.id.action_move_top_right) {
            this.mStackAnimationController.springStack(stackBounds.right, stackBounds.top);
            return true;
        } else if (action == R.id.action_move_bottom_left) {
            this.mStackAnimationController.springStack(stackBounds.left, stackBounds.bottom);
            return true;
        } else if (action == R.id.action_move_bottom_right) {
            this.mStackAnimationController.springStack(stackBounds.right, stackBounds.bottom);
            return true;
        } else {
            return false;
        }
    }

    public void updateContentDescription() {
        if (this.mBubbleData.getBubbles().isEmpty()) {
            return;
        }
        Bubble topBubble = this.mBubbleData.getBubbles().get(0);
        String appName = topBubble.getAppName();
        Notification notification = topBubble.getEntry().notification.getNotification();
        CharSequence titleCharSeq = notification.extras.getCharSequence("android.title");
        String titleStr = getResources().getString(R.string.stream_notification);
        if (titleCharSeq != null) {
            titleStr = titleCharSeq.toString();
        }
        int moreCount = this.mBubbleContainer.getChildCount() - 1;
        String singleDescription = getResources().getString(R.string.bubble_content_description_single, titleStr, appName);
        String stackDescription = getResources().getString(R.string.bubble_content_description_stack, titleStr, appName, Integer.valueOf(moreCount));
        if (!this.mIsExpanded) {
            if (moreCount > 0) {
                this.mBubbleContainer.setContentDescription(stackDescription);
            } else {
                this.mBubbleContainer.setContentDescription(singleDescription);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSystemGestureExcludeRects() {
        Rect excludeZone = this.mSystemGestureExclusionRects.get(0);
        if (this.mBubbleContainer.getChildCount() > 0) {
            View firstBubble = this.mBubbleContainer.getChildAt(0);
            excludeZone.set(firstBubble.getLeft(), firstBubble.getTop(), firstBubble.getRight(), firstBubble.getBottom());
            excludeZone.offset((int) (firstBubble.getTranslationX() + 0.5f), (int) (firstBubble.getTranslationY() + 0.5f));
            this.mBubbleContainer.setSystemGestureExclusionRects(this.mSystemGestureExclusionRects);
            return;
        }
        excludeZone.setEmpty();
        this.mBubbleContainer.setSystemGestureExclusionRects(Collections.emptyList());
    }

    public void updateDotVisibility(String key) {
        Bubble b = this.mBubbleData.getBubbleWithKey(key);
        if (b != null) {
            b.updateDotVisibility();
        }
    }

    public void setExpandListener(BubbleController.BubbleExpandListener listener) {
        this.mExpandListener = listener;
    }

    public boolean isExpanded() {
        return this.mIsExpanded;
    }

    public boolean isExpansionAnimating() {
        return this.mIsExpansionAnimating;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BubbleView getExpandedBubbleView() {
        Bubble bubble = this.mExpandedBubble;
        if (bubble != null) {
            return bubble.getIconView();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Bubble getExpandedBubble() {
        return this.mExpandedBubble;
    }

    @Deprecated
    void setExpandedBubble(String key) {
        Bubble bubbleToExpand = this.mBubbleData.getBubbleWithKey(key);
        if (bubbleToExpand != null) {
            setSelectedBubble(bubbleToExpand);
            bubbleToExpand.setShowInShadeWhenBubble(false);
            setExpanded(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addBubble(Bubble bubble) {
        if (this.mBubbleContainer.getChildCount() == 0) {
            this.mStackOnLeftOrWillBe = this.mStackAnimationController.isStackOnLeftSide();
        }
        bubble.inflate(this.mInflater, this);
        bubble.getIconView().setBubbleIconFactory(this.mBubbleIconFactory);
        bubble.getIconView().updateViews();
        bubble.getIconView().setDotPosition(!this.mStackOnLeftOrWillBe, false);
        this.mBubbleContainer.addView(bubble.getIconView(), 0, new FrameLayout.LayoutParams(-2, -2));
        ViewClippingUtil.setClippingDeactivated(bubble.getIconView(), true, this.mClippingParameters);
        animateInFlyoutForBubble(bubble);
        requestUpdate();
        logBubbleEvent(bubble, 1);
        lambda$new$5$BubbleStackView();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeBubble(Bubble bubble) {
        int removedIndex = this.mBubbleContainer.indexOfChild(bubble.getIconView());
        if (removedIndex >= 0) {
            this.mBubbleContainer.removeViewAt(removedIndex);
            bubble.cleanupExpandedState();
            logBubbleEvent(bubble, 5);
        } else {
            Log.d(TAG, "was asked to remove Bubble, but didn't find the view! " + bubble);
        }
        lambda$new$5$BubbleStackView();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateBubble(Bubble bubble) {
        animateInFlyoutForBubble(bubble);
        requestUpdate();
        logBubbleEvent(bubble, 2);
    }

    public void updateBubbleOrder(List<Bubble> bubbles) {
        for (int i = 0; i < bubbles.size(); i++) {
            Bubble bubble = bubbles.get(i);
            this.mBubbleContainer.reorderView(bubble.getIconView(), i);
        }
        updateBubbleZOrdersAndDotPosition(false);
    }

    public void setSelectedBubble(@Nullable final Bubble bubbleToSelect) {
        Bubble bubble = this.mExpandedBubble;
        if (bubble != null && bubble.equals(bubbleToSelect)) {
            return;
        }
        final Bubble previouslySelected = this.mExpandedBubble;
        this.mExpandedBubble = bubbleToSelect;
        if (this.mIsExpanded) {
            this.mExpandedViewContainer.setAlpha(0.0f);
            this.mSurfaceSynchronizer.syncSurfaceAndRun(new Runnable() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleStackView$wCioQyOrJp7EuggzuABQDC4qQPs
                @Override // java.lang.Runnable
                public final void run() {
                    BubbleStackView.this.lambda$setSelectedBubble$8$BubbleStackView(previouslySelected, bubbleToSelect);
                }
            });
        }
    }

    public /* synthetic */ void lambda$setSelectedBubble$8$BubbleStackView(Bubble previouslySelected, Bubble bubbleToSelect) {
        if (previouslySelected != null) {
            previouslySelected.setContentVisibility(false);
        }
        updateExpandedBubble();
        lambda$new$5$BubbleStackView();
        requestUpdate();
        logBubbleEvent(previouslySelected, 4);
        logBubbleEvent(bubbleToSelect, 3);
        notifyExpansionChanged(previouslySelected, false);
        notifyExpansionChanged(bubbleToSelect, true);
    }

    public void setExpanded(boolean shouldExpand) {
        boolean z = this.mIsExpanded;
        if (shouldExpand == z) {
            return;
        }
        if (z) {
            animateCollapse();
            logBubbleEvent(this.mExpandedBubble, 4);
        } else {
            animateExpansion();
            logBubbleEvent(this.mExpandedBubble, 3);
            logBubbleEvent(this.mExpandedBubble, 15);
        }
        notifyExpansionChanged(this.mExpandedBubble, this.mIsExpanded);
    }

    @Deprecated
    void stackDismissed(int reason) {
        this.mBubbleData.dismissAll(reason);
        logBubbleEvent(null, 6);
    }

    @Nullable
    public View getTargetView(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        if (this.mIsExpanded) {
            if (isIntersecting(this.mBubbleContainer, x, y)) {
                for (int i = 0; i < this.mBubbleContainer.getChildCount(); i++) {
                    BubbleView view = (BubbleView) this.mBubbleContainer.getChildAt(i);
                    if (isIntersecting(view, x, y)) {
                        return view;
                    }
                }
            }
            BubbleExpandedView bev = (BubbleExpandedView) this.mExpandedViewContainer.getChildAt(0);
            if (bev.intersectingTouchableContent((int) x, (int) y)) {
                return bev;
            }
            return null;
        } else if (this.mFlyout.getVisibility() == 0 && isIntersecting(this.mFlyout, x, y)) {
            return this.mFlyout;
        } else {
            return this;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public View getFlyoutView() {
        return this.mFlyout;
    }

    @MainThread
    @Deprecated
    void collapseStack() {
        this.mBubbleData.setExpanded(false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @MainThread
    @Deprecated
    public void collapseStack(Runnable endRunnable) {
        collapseStack();
        endRunnable.run();
    }

    @MainThread
    @Deprecated
    void expandStack() {
        this.mBubbleData.setExpanded(true);
    }

    private void beforeExpandedViewAnimation() {
        hideFlyoutImmediate();
        updateExpandedBubble();
        updateExpandedView();
        this.mIsExpansionAnimating = true;
    }

    private void afterExpandedViewAnimation() {
        updateExpandedView();
        this.mIsExpansionAnimating = false;
        requestUpdate();
    }

    private void animateCollapse() {
        this.mIsExpanded = false;
        final Bubble previouslySelected = this.mExpandedBubble;
        beforeExpandedViewAnimation();
        this.mBubbleContainer.cancelAllAnimations();
        this.mExpandedAnimationController.collapseBackToStack(this.mStackAnimationController.getStackPositionAlongNearestHorizontalEdge(), new Runnable() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleStackView$iPZOxogmUqIzaWGRGN6BsKfV7Ys
            @Override // java.lang.Runnable
            public final void run() {
                BubbleStackView.this.lambda$animateCollapse$9$BubbleStackView(previouslySelected);
            }
        });
        this.mExpandedViewXAnim.animateToFinalPosition(getCollapsedX());
        this.mExpandedViewYAnim.animateToFinalPosition(getCollapsedY());
        this.mExpandedViewContainer.animate().setDuration(100L).alpha(0.0f);
    }

    public /* synthetic */ void lambda$animateCollapse$9$BubbleStackView(Bubble previouslySelected) {
        this.mBubbleContainer.setActiveController(this.mStackAnimationController);
        afterExpandedViewAnimation();
        previouslySelected.setContentVisibility(false);
    }

    private void animateExpansion() {
        this.mIsExpanded = true;
        beforeExpandedViewAnimation();
        this.mBubbleContainer.setActiveController(this.mExpandedAnimationController);
        this.mExpandedAnimationController.expandFromStack(new Runnable() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleStackView$vo_7qhVTGBrVaeUfzKm3qxH9Tbg
            @Override // java.lang.Runnable
            public final void run() {
                BubbleStackView.this.lambda$animateExpansion$10$BubbleStackView();
            }
        });
        this.mExpandedViewContainer.setTranslationX(getCollapsedX());
        this.mExpandedViewContainer.setTranslationY(getCollapsedY());
        this.mExpandedViewContainer.setAlpha(0.0f);
        this.mExpandedViewXAnim.animateToFinalPosition(0.0f);
        this.mExpandedViewYAnim.animateToFinalPosition(getExpandedViewY());
        this.mExpandedViewContainer.animate().setDuration(100L).alpha(1.0f);
    }

    public /* synthetic */ void lambda$animateExpansion$10$BubbleStackView() {
        lambda$new$5$BubbleStackView();
        afterExpandedViewAnimation();
    }

    private float getCollapsedX() {
        if (this.mStackAnimationController.getStackPosition().x < getWidth() / 2) {
            return -this.mExpandedAnimateXDistance;
        }
        return this.mExpandedAnimateXDistance;
    }

    private float getCollapsedY() {
        return Math.min(this.mStackAnimationController.getStackPosition().y, this.mExpandedAnimateYDistance);
    }

    private void notifyExpansionChanged(Bubble bubble, boolean expanded) {
        BubbleController.BubbleExpandListener bubbleExpandListener = this.mExpandListener;
        if (bubbleExpandListener != null && bubble != null) {
            bubbleExpandListener.onBubbleExpandChanged(expanded, bubble.getKey());
        }
    }

    public BubbleView getBubbleAt(int i) {
        if (this.mBubbleContainer.getChildCount() > i) {
            return (BubbleView) this.mBubbleContainer.getChildAt(i);
        }
        return null;
    }

    public void onImeVisibilityChanged(boolean visible, int height) {
        this.mStackAnimationController.setImeHeight(visible ? this.mImeOffset + height : 0);
        if (!this.mIsExpanded) {
            this.mStackAnimationController.animateForImeVisibility(visible);
        }
    }

    public void onBubbleDragStart(View bubble) {
        this.mExpandedAnimationController.prepareForBubbleDrag(bubble);
    }

    public void onBubbleDragged(View bubble, float x, float y) {
        if (!this.mIsExpanded || this.mIsExpansionAnimating) {
            return;
        }
        this.mExpandedAnimationController.dragBubbleOut(bubble, x, y);
        springInDismissTarget();
    }

    public void onBubbleDragFinish(View bubble, float x, float y, float velX, float velY) {
        if (!this.mIsExpanded || this.mIsExpansionAnimating) {
            return;
        }
        this.mExpandedAnimationController.snapBubbleBack(bubble, velX, velY);
        hideDismissTarget();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDragStart() {
        if (this.mIsExpanded || this.mIsExpansionAnimating) {
            return;
        }
        this.mStackAnimationController.cancelStackPositionAnimations();
        this.mBubbleContainer.setActiveController(this.mStackAnimationController);
        hideFlyoutImmediate();
        this.mDraggingInDismissTarget = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDragged(float x, float y) {
        if (this.mIsExpanded || this.mIsExpansionAnimating) {
            return;
        }
        springInDismissTarget();
        this.mStackAnimationController.moveStackFromTouch(x, y);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDragFinish(float x, float y, float velX, float velY) {
        if (this.mIsExpanded || this.mIsExpansionAnimating) {
            return;
        }
        float newStackX = this.mStackAnimationController.flingStackThenSpringToEdge(x, velX, velY);
        logBubbleEvent(null, 7);
        this.mStackOnLeftOrWillBe = newStackX <= 0.0f;
        updateBubbleZOrdersAndDotPosition(true);
        hideDismissTarget();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onFlyoutDragStart() {
        this.mFlyout.removeCallbacks(this.mHideFlyout);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onFlyoutDragged(float deltaX) {
        if (this.mFlyout.getWidth() <= 0) {
            return;
        }
        boolean onLeft = this.mStackAnimationController.isStackOnLeftSide();
        this.mFlyoutDragDeltaX = deltaX;
        float collapsePercent = onLeft ? (-deltaX) / this.mFlyout.getWidth() : deltaX / this.mFlyout.getWidth();
        this.mFlyout.setCollapsePercent(Math.min(1.0f, Math.max(0.0f, collapsePercent)));
        float overscrollTranslation = 0.0f;
        if (collapsePercent < 0.0f || collapsePercent > 1.0f) {
            boolean z = false;
            boolean overscrollingPastDot = collapsePercent > 1.0f;
            if ((onLeft && collapsePercent > 1.0f) || (!onLeft && collapsePercent < 0.0f)) {
                z = true;
            }
            boolean overscrollingLeft = z;
            overscrollTranslation = (overscrollingPastDot ? collapsePercent - 1.0f : (-1.0f) * collapsePercent) * (overscrollingLeft ? -1 : 1) * (this.mFlyout.getWidth() / (FLYOUT_OVERSCROLL_ATTENUATION_FACTOR / (overscrollingPastDot ? 2 : 1)));
        }
        BubbleFlyoutView bubbleFlyoutView = this.mFlyout;
        bubbleFlyoutView.setTranslationX(bubbleFlyoutView.getRestingTranslationX() + overscrollTranslation);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onFlyoutTapped() {
        this.mBubbleToExpandAfterFlyoutCollapse = this.mBubbleData.getSelectedBubble();
        this.mFlyout.removeCallbacks(this.mHideFlyout);
        this.mHideFlyout.run();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onFlyoutDragFinished(float deltaX, float velX) {
        boolean onLeft = this.mStackAnimationController.isStackOnLeftSide();
        boolean shouldDismiss = true;
        boolean metRequiredVelocity = !onLeft ? velX <= FLYOUT_DISMISS_VELOCITY : velX >= -2000.0f;
        boolean metRequiredDeltaX = onLeft ? deltaX < ((float) (-this.mFlyout.getWidth())) * 0.25f : deltaX > ((float) this.mFlyout.getWidth()) * 0.25f;
        boolean isCancelFling = !onLeft ? velX >= 0.0f : velX <= 0.0f;
        if (!metRequiredVelocity && (!metRequiredDeltaX || isCancelFling)) {
            shouldDismiss = false;
        }
        this.mFlyout.removeCallbacks(this.mHideFlyout);
        animateFlyoutCollapsed(shouldDismiss, velX);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onGestureStart() {
        this.mIsGestureInProgress = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onGestureFinished() {
        this.mIsGestureInProgress = false;
        if (this.mIsExpanded) {
            this.mExpandedAnimationController.onGestureFinished();
        }
    }

    private void animateDesaturateAndDarken(View targetView, boolean desaturateAndDarken) {
        this.mDesaturateAndDarkenTargetView = targetView;
        if (desaturateAndDarken) {
            this.mDesaturateAndDarkenTargetView.setLayerType(2, this.mDesaturateAndDarkenPaint);
            this.mDesaturateAndDarkenAnimator.removeAllListeners();
            this.mDesaturateAndDarkenAnimator.start();
            return;
        }
        this.mDesaturateAndDarkenAnimator.removeAllListeners();
        this.mDesaturateAndDarkenAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.bubbles.BubbleStackView.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                BubbleStackView.this.resetDesaturationAndDarken();
            }
        });
        this.mDesaturateAndDarkenAnimator.reverse();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetDesaturationAndDarken() {
        this.mDesaturateAndDarkenAnimator.removeAllListeners();
        this.mDesaturateAndDarkenAnimator.cancel();
        this.mDesaturateAndDarkenTargetView.setLayerType(0, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void animateMagnetToDismissTarget(View magnetView, boolean toTarget, float x, float y, float velX, float velY) {
        this.mDraggingInDismissTarget = toTarget;
        int i = 0;
        if (toTarget) {
            float destY = this.mDismissContainer.getDismissTargetCenterY() - (this.mBubbleSize / 2.0f);
            this.mAnimatingMagnet = true;
            Runnable afterMagnet = new Runnable() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleStackView$oLhNqxGbPa3FqJeraIwHlBcS7tk
                @Override // java.lang.Runnable
                public final void run() {
                    BubbleStackView.this.lambda$animateMagnetToDismissTarget$11$BubbleStackView();
                }
            };
            if (magnetView == this) {
                this.mStackAnimationController.magnetToDismiss(velX, velY, destY, afterMagnet);
                animateDesaturateAndDarken(this.mBubbleContainer, true);
            } else {
                this.mExpandedAnimationController.magnetBubbleToDismiss(magnetView, velX, velY, destY, afterMagnet);
                animateDesaturateAndDarken(magnetView, true);
            }
        } else {
            this.mAnimatingMagnet = false;
            if (magnetView == this) {
                this.mStackAnimationController.demagnetizeFromDismissToPoint(x, y, velX, velY);
                animateDesaturateAndDarken(this.mBubbleContainer, false);
            } else {
                this.mExpandedAnimationController.demagnetizeBubbleTo(x, y, velX, velY);
                animateDesaturateAndDarken(magnetView, false);
            }
        }
        Vibrator vibrator = this.mVibrator;
        if (!toTarget) {
            i = 2;
        }
        vibrator.vibrate(VibrationEffect.get(i));
    }

    public /* synthetic */ void lambda$animateMagnetToDismissTarget$11$BubbleStackView() {
        this.mAnimatingMagnet = false;
        Runnable runnable = this.mAfterMagnet;
        if (runnable != null) {
            runnable.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void magnetToStackIfNeededThenAnimateDismissal(final View touchedView, float velX, float velY, final Runnable after) {
        final View draggedOutBubble = this.mExpandedAnimationController.getDraggedOutBubble();
        Runnable animateDismissal = new Runnable() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleStackView$wNBb9TcVorXyGaagZMMDs0nXEJw
            @Override // java.lang.Runnable
            public final void run() {
                BubbleStackView.this.lambda$magnetToStackIfNeededThenAnimateDismissal$14$BubbleStackView(touchedView, after, draggedOutBubble);
            }
        };
        if (this.mAnimatingMagnet) {
            this.mAfterMagnet = animateDismissal;
        } else if (this.mDraggingInDismissTarget) {
            animateDismissal.run();
        } else {
            animateMagnetToDismissTarget(touchedView, true, -1.0f, -1.0f, velX, velY);
            this.mAfterMagnet = animateDismissal;
        }
    }

    public /* synthetic */ void lambda$magnetToStackIfNeededThenAnimateDismissal$14$BubbleStackView(View touchedView, final Runnable after, View draggedOutBubble) {
        this.mAfterMagnet = null;
        this.mVibrator.vibrate(VibrationEffect.get(0));
        this.mDismissContainer.springOut();
        if (touchedView == this) {
            this.mStackAnimationController.implodeStack(new Runnable() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleStackView$e34XM71IP6gJFzDCLRuPf64iTJU
                @Override // java.lang.Runnable
                public final void run() {
                    BubbleStackView.this.lambda$magnetToStackIfNeededThenAnimateDismissal$12$BubbleStackView(after);
                }
            });
        } else {
            this.mExpandedAnimationController.dismissDraggedOutBubble(draggedOutBubble, new Runnable() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleStackView$6IgSc7n-WF13kFj8_shFyg558sU
                @Override // java.lang.Runnable
                public final void run() {
                    BubbleStackView.this.lambda$magnetToStackIfNeededThenAnimateDismissal$13$BubbleStackView(after);
                }
            });
        }
    }

    public /* synthetic */ void lambda$magnetToStackIfNeededThenAnimateDismissal$12$BubbleStackView(Runnable after) {
        this.mAnimatingMagnet = false;
        this.mShowingDismiss = false;
        this.mDraggingInDismissTarget = false;
        after.run();
        resetDesaturationAndDarken();
    }

    public /* synthetic */ void lambda$magnetToStackIfNeededThenAnimateDismissal$13$BubbleStackView(Runnable after) {
        this.mAnimatingMagnet = false;
        this.mShowingDismiss = false;
        this.mDraggingInDismissTarget = false;
        resetDesaturationAndDarken();
        after.run();
    }

    private void springInDismissTarget() {
        if (this.mShowingDismiss) {
            return;
        }
        this.mShowingDismiss = true;
        this.mDismissContainer.springIn();
        this.mDismissContainer.bringToFront();
        this.mDismissContainer.setZ(32766.0f);
    }

    private void hideDismissTarget() {
        if (!this.mShowingDismiss) {
            return;
        }
        this.mDismissContainer.springOut();
        this.mShowingDismiss = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isInDismissTarget(MotionEvent ev) {
        return isIntersecting(this.mDismissContainer.getDismissTarget(), ev.getRawX(), ev.getRawY());
    }

    private void animateFlyoutCollapsed(boolean collapsed, float velX) {
        float f;
        float f2;
        boolean onLeft = this.mStackAnimationController.isStackOnLeftSide();
        SpringForce spring = this.mFlyoutTransitionSpring.getSpring();
        if (this.mBubbleToExpandAfterFlyoutCollapse != null) {
            f = 1500.0f;
        } else {
            f = 200.0f;
        }
        spring.setStiffness(f);
        SpringAnimation startVelocity = this.mFlyoutTransitionSpring.setStartValue(this.mFlyoutDragDeltaX).setStartVelocity(velX);
        if (collapsed) {
            int width = this.mFlyout.getWidth();
            if (onLeft) {
                width = -width;
            }
            f2 = width;
        } else {
            f2 = 0.0f;
        }
        startVelocity.animateToFinalPosition(f2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateDots() {
        int bubbsCount = this.mBubbleContainer.getChildCount();
        for (int i = 0; i < bubbsCount; i++) {
            BubbleView bv = (BubbleView) this.mBubbleContainer.getChildAt(i);
            bv.updateDotVisibility(true);
        }
    }

    float getExpandedViewY() {
        return getStatusBarHeight() + this.mBubbleSize + this.mBubblePaddingTop + this.mPointerHeight;
    }

    @VisibleForTesting
    void animateInFlyoutForBubble(final Bubble bubble) {
        final CharSequence updateMessage = bubble.getUpdateMessage(getContext());
        if (!bubble.showFlyoutForBubble()) {
            bubble.setSuppressFlyout(false);
        } else if (updateMessage == null || isExpanded() || this.mIsExpansionAnimating || this.mIsGestureInProgress || this.mBubbleToExpandAfterFlyoutCollapse != null || bubble.getIconView() == null) {
        } else {
            this.mFlyoutDragDeltaX = 0.0f;
            clearFlyoutOnHide();
            this.mFlyoutOnHide = new Runnable() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleStackView$Vnx81abGMMclYnqAiYuaTg3EgOw
                @Override // java.lang.Runnable
                public final void run() {
                    BubbleStackView.this.lambda$animateInFlyoutForBubble$15$BubbleStackView(bubble);
                }
            };
            this.mFlyout.setVisibility(4);
            bubble.getIconView().setSuppressDot(true, false);
            post(new Runnable() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleStackView$CW6xKkcWJ8QOK9vdaay-gmev5J0
                @Override // java.lang.Runnable
                public final void run() {
                    BubbleStackView.this.lambda$animateInFlyoutForBubble$18$BubbleStackView(updateMessage, bubble);
                }
            });
            this.mFlyout.removeCallbacks(this.mHideFlyout);
            this.mFlyout.postDelayed(this.mHideFlyout, 5000L);
            logBubbleEvent(bubble, 16);
        }
    }

    public /* synthetic */ void lambda$animateInFlyoutForBubble$15$BubbleStackView(Bubble bubble) {
        resetDot(bubble);
        Bubble bubble2 = this.mBubbleToExpandAfterFlyoutCollapse;
        if (bubble2 == null) {
            return;
        }
        this.mBubbleData.setSelectedBubble(bubble2);
        this.mBubbleData.setExpanded(true);
        this.mBubbleToExpandAfterFlyoutCollapse = null;
    }

    public /* synthetic */ void lambda$animateInFlyoutForBubble$18$BubbleStackView(CharSequence updateMessage, Bubble bubble) {
        if (isExpanded()) {
            return;
        }
        Runnable expandFlyoutAfterDelay = new Runnable() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleStackView$Us4IKYXczGW3KCR3VSnmnEEdKeU
            @Override // java.lang.Runnable
            public final void run() {
                BubbleStackView.this.lambda$animateInFlyoutForBubble$17$BubbleStackView();
            }
        };
        this.mFlyout.setupFlyoutStartingAsDot(updateMessage, this.mStackAnimationController.getStackPosition(), getWidth(), this.mStackAnimationController.isStackOnLeftSide(), bubble.getIconView().getBadgeColor(), expandFlyoutAfterDelay, this.mFlyoutOnHide, bubble.getIconView().getDotCenter());
        this.mFlyout.bringToFront();
    }

    public /* synthetic */ void lambda$animateInFlyoutForBubble$17$BubbleStackView() {
        this.mAnimateInFlyout = new Runnable() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleStackView$lttCI93NoRx-VG7o9I_UxixXqXc
            @Override // java.lang.Runnable
            public final void run() {
                BubbleStackView.this.lambda$animateInFlyoutForBubble$16$BubbleStackView();
            }
        };
        this.mFlyout.postDelayed(this.mAnimateInFlyout, 200L);
    }

    public /* synthetic */ void lambda$animateInFlyoutForBubble$16$BubbleStackView() {
        float width;
        this.mFlyout.setVisibility(0);
        if (this.mStackAnimationController.isStackOnLeftSide()) {
            width = -this.mFlyout.getWidth();
        } else {
            width = this.mFlyout.getWidth();
        }
        this.mFlyoutDragDeltaX = width;
        animateFlyoutCollapsed(false, 0.0f);
        this.mFlyout.postDelayed(this.mHideFlyout, 5000L);
    }

    private void resetDot(Bubble bubble) {
        boolean suppressDot = !bubble.showBubbleDot();
        if (suppressDot) {
            bubble.getIconView().setSuppressDot(false, false);
        }
        bubble.getIconView().setSuppressDot(suppressDot, suppressDot);
    }

    private void hideFlyoutImmediate() {
        clearFlyoutOnHide();
        this.mFlyout.removeCallbacks(this.mAnimateInFlyout);
        this.mFlyout.removeCallbacks(this.mHideFlyout);
        this.mFlyout.hideFlyout();
    }

    private void clearFlyoutOnHide() {
        this.mFlyout.removeCallbacks(this.mAnimateInFlyout);
        Runnable runnable = this.mFlyoutOnHide;
        if (runnable == null) {
            return;
        }
        runnable.run();
        this.mFlyoutOnHide = null;
    }

    public void getBoundsOnScreen(Rect outRect) {
        if (!this.mIsExpanded) {
            if (this.mBubbleContainer.getChildCount() > 0) {
                this.mBubbleContainer.getChildAt(0).getBoundsOnScreen(outRect);
            }
            outRect.top -= this.mBubbleTouchPadding;
            outRect.left -= this.mBubbleTouchPadding;
            outRect.right += this.mBubbleTouchPadding;
            outRect.bottom += this.mBubbleTouchPadding;
        } else {
            this.mBubbleContainer.getBoundsOnScreen(outRect);
        }
        if (this.mFlyout.getVisibility() == 0) {
            Rect flyoutBounds = new Rect();
            this.mFlyout.getBoundsOnScreen(flyoutBounds);
            outRect.union(flyoutBounds);
        }
    }

    private int getStatusBarHeight() {
        if (getRootWindowInsets() != null) {
            WindowInsets insets = getRootWindowInsets();
            return Math.max(this.mStatusBarHeight, insets.getDisplayCutout() != null ? insets.getDisplayCutout().getSafeInsetTop() : 0);
        }
        return 0;
    }

    private boolean isIntersecting(View view, float x, float y) {
        this.mTempLoc = view.getLocationOnScreen();
        RectF rectF = this.mTempRect;
        int[] iArr = this.mTempLoc;
        rectF.set(iArr[0], iArr[1], iArr[0] + view.getWidth(), this.mTempLoc[1] + view.getHeight());
        return this.mTempRect.contains(x, y);
    }

    private void requestUpdate() {
        if (this.mViewUpdatedRequested || this.mIsExpansionAnimating) {
            return;
        }
        this.mViewUpdatedRequested = true;
        getViewTreeObserver().addOnPreDrawListener(this.mViewUpdater);
        invalidate();
    }

    private void updateExpandedBubble() {
        this.mExpandedViewContainer.removeAllViews();
        Bubble bubble = this.mExpandedBubble;
        if (bubble != null && this.mIsExpanded) {
            this.mExpandedViewContainer.addView(bubble.getExpandedView());
            this.mExpandedBubble.getExpandedView().populateExpandedView();
            this.mExpandedViewContainer.setVisibility(this.mIsExpanded ? 0 : 8);
            this.mExpandedViewContainer.setAlpha(1.0f);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateExpandedView() {
        this.mExpandedViewContainer.setVisibility(this.mIsExpanded ? 0 : 8);
        if (this.mIsExpanded) {
            this.mExpandedBubble.getExpandedView().updateView();
            float y = getExpandedViewY();
            if (!this.mExpandedViewYAnim.isRunning()) {
                this.mExpandedViewContainer.setTranslationY(y);
                this.mExpandedBubble.getExpandedView().updateView();
            } else {
                this.mExpandedViewYAnim.animateToFinalPosition(y);
            }
        }
        this.mStackOnLeftOrWillBe = this.mStackAnimationController.isStackOnLeftSide();
        updateBubbleZOrdersAndDotPosition(false);
    }

    private void updateBubbleZOrdersAndDotPosition(boolean animate) {
        int bubbleCount = this.mBubbleContainer.getChildCount();
        for (int i = 0; i < bubbleCount; i++) {
            BubbleView bv = (BubbleView) this.mBubbleContainer.getChildAt(i);
            bv.updateDotVisibility(true);
            bv.setZ((getResources().getDimensionPixelSize(R.dimen.bubble_elevation) * 5) - i);
            boolean dotPositionOnLeft = bv.getDotPositionOnLeft();
            boolean z = this.mStackOnLeftOrWillBe;
            if (dotPositionOnLeft == z) {
                bv.setDotPosition(!z, animate);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updatePointerPosition */
    public void lambda$new$5$BubbleStackView() {
        Bubble expandedBubble = getExpandedBubble();
        if (expandedBubble == null) {
            return;
        }
        int index = getBubbleIndex(expandedBubble);
        float bubbleLeftFromScreenLeft = this.mExpandedAnimationController.getBubbleLeft(index);
        float halfBubble = this.mBubbleSize / 2.0f;
        float bubbleCenter = bubbleLeftFromScreenLeft + halfBubble;
        expandedBubble.getExpandedView().setPointerPosition(bubbleCenter - this.mExpandedViewContainer.getPaddingLeft());
    }

    public int getBubbleCount() {
        return this.mBubbleContainer.getChildCount();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getBubbleIndex(@Nullable Bubble bubble) {
        if (bubble == null) {
            return 0;
        }
        return this.mBubbleContainer.indexOfChild(bubble.getIconView());
    }

    public float getNormalizedXPosition() {
        BigDecimal bigDecimal = new BigDecimal(getStackPosition().x / this.mDisplaySize.x);
        RoundingMode roundingMode = RoundingMode.CEILING;
        return bigDecimal.setScale(4, RoundingMode.HALF_UP).floatValue();
    }

    public float getNormalizedYPosition() {
        BigDecimal bigDecimal = new BigDecimal(getStackPosition().y / this.mDisplaySize.y);
        RoundingMode roundingMode = RoundingMode.CEILING;
        return bigDecimal.setScale(4, RoundingMode.HALF_UP).floatValue();
    }

    public PointF getStackPosition() {
        return this.mStackAnimationController.getStackPosition();
    }

    private void logBubbleEvent(@Nullable Bubble bubble, int action) {
        if (bubble == null || bubble.getEntry() == null || bubble.getEntry().notification == null) {
            StatsLog.write(149, null, null, 0, 0, getBubbleCount(), action, getNormalizedXPosition(), getNormalizedYPosition(), false, false, false);
            return;
        }
        StatusBarNotification notification = bubble.getEntry().notification;
        StatsLog.write(149, notification.getPackageName(), notification.getNotification().getChannelId(), notification.getId(), getBubbleIndex(bubble), getBubbleCount(), action, getNormalizedXPosition(), getNormalizedYPosition(), bubble.showInShadeWhenBubble(), bubble.isOngoing(), false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean performBackPressIfNeeded() {
        if (!isExpanded()) {
            return false;
        }
        return this.mExpandedBubble.getExpandedView().performBackPressIfNeeded();
    }

    List<Bubble> getBubblesOnScreen() {
        List<Bubble> bubbles = new ArrayList<>();
        for (int i = 0; i < this.mBubbleContainer.getChildCount(); i++) {
            View child = this.mBubbleContainer.getChildAt(i);
            if (child instanceof BubbleView) {
                String key = ((BubbleView) child).getKey();
                Bubble bubble = this.mBubbleData.getBubbleWithKey(key);
                bubbles.add(bubble);
            }
        }
        return bubbles;
    }
}
