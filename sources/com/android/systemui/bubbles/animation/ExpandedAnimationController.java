package com.android.systemui.bubbles.animation;

import android.content.res.Resources;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;
import androidx.annotation.Nullable;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.bubbles.animation.PhysicsAnimationLayout;
import com.google.android.collect.Sets;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Set;
/* loaded from: classes21.dex */
public class ExpandedAnimationController extends PhysicsAnimationLayout.PhysicsAnimationController {
    private static final int ANIMATE_TRANSLATION_FACTOR = 4;
    private static final float CENTER_BUBBLES_LANDSCAPE_PERCENT = 0.66f;
    private static final int EXPAND_COLLAPSE_ANIM_STIFFNESS = 1000;
    private static final int EXPAND_COLLAPSE_TARGET_ANIM_DURATION = 175;
    private Runnable mAfterCollapse;
    private Runnable mAfterExpand;
    private View mBubbleDraggingOut;
    private float mBubblePaddingTop;
    private float mBubbleSizePx;
    private int mBubblesMaxRendered;
    private PointF mCollapsePoint;
    private Point mDisplaySize;
    private int mExpandedViewPadding;
    private float mLauncherGridDiff;
    private int mScreenOrientation;
    private float mStackOffsetPx;
    private float mStatusBarHeight;
    private boolean mIndividualBubbleWithinDismissTarget = false;
    private boolean mAnimatingExpand = false;
    private boolean mAnimatingCollapse = false;
    private boolean mSpringingBubbleToTouch = false;
    private boolean mBubbleDraggedOutEnough = false;

    public ExpandedAnimationController(Point displaySize, int expandedViewPadding, int orientation) {
        updateOrientation(orientation, displaySize);
        this.mExpandedViewPadding = expandedViewPadding;
        this.mLauncherGridDiff = 30.0f;
    }

    public void expandFromStack(Runnable after) {
        this.mAnimatingCollapse = false;
        this.mAnimatingExpand = true;
        this.mAfterExpand = after;
        startOrUpdatePathAnimation(true);
    }

    public void collapseBackToStack(PointF collapsePoint, Runnable after) {
        this.mAnimatingExpand = false;
        this.mAnimatingCollapse = true;
        this.mAfterCollapse = after;
        this.mCollapsePoint = collapsePoint;
        startOrUpdatePathAnimation(false);
    }

    public void updateOrientation(int orientation, Point displaySize) {
        this.mScreenOrientation = orientation;
        this.mDisplaySize = displaySize;
        if (this.mLayout != null) {
            Resources res = this.mLayout.getContext().getResources();
            this.mBubblePaddingTop = res.getDimensionPixelSize(R.dimen.bubble_padding_top);
            this.mStatusBarHeight = res.getDimensionPixelSize(17105438);
        }
    }

    private void startOrUpdatePathAnimation(final boolean expanding) {
        Runnable after;
        if (expanding) {
            after = new Runnable() { // from class: com.android.systemui.bubbles.animation.-$$Lambda$ExpandedAnimationController$gE2Cl95ubR0Pg2NTtDLGoNhSLoM
                @Override // java.lang.Runnable
                public final void run() {
                    ExpandedAnimationController.this.lambda$startOrUpdatePathAnimation$0$ExpandedAnimationController();
                }
            };
        } else {
            after = new Runnable() { // from class: com.android.systemui.bubbles.animation.-$$Lambda$ExpandedAnimationController$WjMaDVcvCcyW4ns9Ixw4Q7pkHT4
                @Override // java.lang.Runnable
                public final void run() {
                    ExpandedAnimationController.this.lambda$startOrUpdatePathAnimation$1$ExpandedAnimationController();
                }
            };
        }
        animationsForChildrenFromIndex(0, new PhysicsAnimationLayout.PhysicsAnimationController.ChildAnimationConfigurator() { // from class: com.android.systemui.bubbles.animation.-$$Lambda$ExpandedAnimationController$7Il03mDM0nM9UqZB95uu3PfeMxA
            @Override // com.android.systemui.bubbles.animation.PhysicsAnimationLayout.PhysicsAnimationController.ChildAnimationConfigurator
            public final void configureAnimationForChildAtIndex(int i, PhysicsAnimationLayout.PhysicsPropertyAnimator physicsPropertyAnimator) {
                ExpandedAnimationController.this.lambda$startOrUpdatePathAnimation$2$ExpandedAnimationController(expanding, i, physicsPropertyAnimator);
            }
        }).startAll(after);
    }

    public /* synthetic */ void lambda$startOrUpdatePathAnimation$0$ExpandedAnimationController() {
        this.mAnimatingExpand = false;
        Runnable runnable = this.mAfterExpand;
        if (runnable != null) {
            runnable.run();
        }
        this.mAfterExpand = null;
    }

    public /* synthetic */ void lambda$startOrUpdatePathAnimation$1$ExpandedAnimationController() {
        this.mAnimatingCollapse = false;
        Runnable runnable = this.mAfterCollapse;
        if (runnable != null) {
            runnable.run();
        }
        this.mAfterCollapse = null;
    }

    public /* synthetic */ void lambda$startOrUpdatePathAnimation$2$ExpandedAnimationController(boolean expanding, int index, PhysicsAnimationLayout.PhysicsPropertyAnimator animation) {
        int startDelay;
        View bubble = this.mLayout.getChildAt(index);
        Path path = new Path();
        path.moveTo(bubble.getTranslationX(), bubble.getTranslationY());
        float expandedY = getExpandedY();
        if (expanding) {
            path.lineTo(bubble.getTranslationX(), expandedY);
            path.lineTo(getBubbleLeft(index), expandedY);
        } else {
            float sideMultiplier = this.mLayout.isFirstChildXLeftOfCenter(this.mCollapsePoint.x) ? -1.0f : 1.0f;
            float stackedX = this.mCollapsePoint.x + (index * sideMultiplier * this.mStackOffsetPx);
            path.lineTo(stackedX, expandedY);
            path.lineTo(stackedX, this.mCollapsePoint.y);
        }
        boolean firstBubbleLeads = (expanding && !this.mLayout.isFirstChildXLeftOfCenter(bubble.getTranslationX())) || (!expanding && this.mLayout.isFirstChildXLeftOfCenter(this.mCollapsePoint.x));
        if (firstBubbleLeads) {
            startDelay = index * 10;
        } else {
            startDelay = (this.mLayout.getChildCount() - index) * 10;
        }
        animation.followAnimatedTargetAlongPath(path, EXPAND_COLLAPSE_TARGET_ANIM_DURATION, Interpolators.LINEAR, new Runnable[0]).withStartDelay(startDelay).withStiffness(1000.0f);
    }

    public void prepareForBubbleDrag(View bubble) {
        this.mLayout.cancelAnimationsOnView(bubble);
        this.mBubbleDraggingOut = bubble;
        this.mBubbleDraggingOut.setTranslationZ(32767.0f);
    }

    public void dragBubbleOut(View bubbleView, float x, float y) {
        boolean z = true;
        if (this.mSpringingBubbleToTouch) {
            if (this.mLayout.arePropertiesAnimatingOnView(bubbleView, DynamicAnimation.TRANSLATION_X, DynamicAnimation.TRANSLATION_Y)) {
                animationForChild(this.mBubbleDraggingOut).translationX(x, new Runnable[0]).translationY(y, new Runnable[0]).withStiffness(10000.0f).start(new Runnable[0]);
            } else {
                this.mSpringingBubbleToTouch = false;
            }
        }
        if (!this.mSpringingBubbleToTouch && !this.mIndividualBubbleWithinDismissTarget) {
            bubbleView.setTranslationX(x);
            bubbleView.setTranslationY(y);
        }
        if (y <= getExpandedY() + this.mBubbleSizePx && y >= getExpandedY() - this.mBubbleSizePx) {
            z = false;
        }
        boolean draggedOutEnough = z;
        if (draggedOutEnough != this.mBubbleDraggedOutEnough) {
            updateBubblePositions();
            this.mBubbleDraggedOutEnough = draggedOutEnough;
        }
    }

    public void dismissDraggedOutBubble(View bubble, Runnable after) {
        this.mIndividualBubbleWithinDismissTarget = false;
        animationForChild(bubble).withStiffness(10000.0f).scaleX(1.1f, new Runnable[0]).scaleY(1.1f, new Runnable[0]).alpha(0.0f, after).start(new Runnable[0]);
        updateBubblePositions();
    }

    @Nullable
    public View getDraggedOutBubble() {
        return this.mBubbleDraggingOut;
    }

    public void magnetBubbleToDismiss(View bubbleView, float velX, float velY, float destY, Runnable after) {
        this.mIndividualBubbleWithinDismissTarget = true;
        this.mSpringingBubbleToTouch = false;
        animationForChild(bubbleView).withStiffness(1500.0f).withDampingRatio(0.75f).withPositionStartVelocities(velX, velY).translationX((this.mLayout.getWidth() / 2.0f) - (this.mBubbleSizePx / 2.0f), new Runnable[0]).translationY(destY, after).start(new Runnable[0]);
    }

    public void demagnetizeBubbleTo(float x, float y, float velX, float velY) {
        this.mIndividualBubbleWithinDismissTarget = false;
        this.mSpringingBubbleToTouch = true;
        animationForChild(this.mBubbleDraggingOut).translationX(x, new Runnable[0]).translationY(y, new Runnable[0]).withPositionStartVelocities(velX, velY).withStiffness(10000.0f).start(new Runnable[0]);
    }

    public void snapBubbleBack(final View bubbleView, float velX, float velY) {
        int index = this.mLayout.indexOfChild(bubbleView);
        animationForChildAtIndex(index).position(getBubbleLeft(index), getExpandedY(), new Runnable[0]).withPositionStartVelocities(velX, velY).start(new Runnable() { // from class: com.android.systemui.bubbles.animation.-$$Lambda$ExpandedAnimationController$n3D_KDDz_uA6Zea2rmmE2_UxikI
            @Override // java.lang.Runnable
            public final void run() {
                bubbleView.setTranslationZ(0.0f);
            }
        });
        updateBubblePositions();
    }

    public void onGestureFinished() {
        this.mBubbleDraggedOutEnough = false;
        this.mBubbleDraggingOut = null;
        updateBubblePositions();
    }

    public void updateYPosition(Runnable after) {
        if (this.mLayout == null) {
            return;
        }
        animationsForChildrenFromIndex(0, new PhysicsAnimationLayout.PhysicsAnimationController.ChildAnimationConfigurator() { // from class: com.android.systemui.bubbles.animation.-$$Lambda$ExpandedAnimationController$8QomesE6Zam2GSy9tW1fTh6Elo8
            @Override // com.android.systemui.bubbles.animation.PhysicsAnimationLayout.PhysicsAnimationController.ChildAnimationConfigurator
            public final void configureAnimationForChildAtIndex(int i, PhysicsAnimationLayout.PhysicsPropertyAnimator physicsPropertyAnimator) {
                ExpandedAnimationController.this.lambda$updateYPosition$4$ExpandedAnimationController(i, physicsPropertyAnimator);
            }
        }).startAll(after);
    }

    public /* synthetic */ void lambda$updateYPosition$4$ExpandedAnimationController(int i, PhysicsAnimationLayout.PhysicsPropertyAnimator anim) {
        anim.translationY(getExpandedY(), new Runnable[0]);
    }

    public float getExpandedY() {
        if (this.mLayout == null || this.mLayout.getRootWindowInsets() == null) {
            return 0.0f;
        }
        WindowInsets insets = this.mLayout.getRootWindowInsets();
        return this.mBubblePaddingTop + Math.max(this.mStatusBarHeight, insets.getDisplayCutout() != null ? insets.getDisplayCutout().getSafeInsetTop() : 0.0f);
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("ExpandedAnimationController state:");
        pw.print("  isActive:          ");
        pw.println(isActiveController());
        pw.print("  animatingExpand:   ");
        pw.println(this.mAnimatingExpand);
        pw.print("  animatingCollapse: ");
        pw.println(this.mAnimatingCollapse);
        pw.print("  bubbleInDismiss:   ");
        pw.println(this.mIndividualBubbleWithinDismissTarget);
        pw.print("  springingBubble:   ");
        pw.println(this.mSpringingBubbleToTouch);
    }

    @Override // com.android.systemui.bubbles.animation.PhysicsAnimationLayout.PhysicsAnimationController
    void onActiveControllerForLayout(PhysicsAnimationLayout layout) {
        Resources res = layout.getResources();
        this.mStackOffsetPx = res.getDimensionPixelSize(R.dimen.bubble_stack_offset);
        this.mBubblePaddingTop = res.getDimensionPixelSize(R.dimen.bubble_padding_top);
        this.mBubbleSizePx = res.getDimensionPixelSize(R.dimen.individual_bubble_size);
        this.mStatusBarHeight = res.getDimensionPixelSize(17105438);
        this.mBubblesMaxRendered = res.getInteger(R.integer.bubbles_max_rendered);
        this.mLayout.setVisibility(0);
        animationsForChildrenFromIndex(0, new PhysicsAnimationLayout.PhysicsAnimationController.ChildAnimationConfigurator() { // from class: com.android.systemui.bubbles.animation.-$$Lambda$ExpandedAnimationController$MQDrBXWQvl1BITN7BEHGEeBiDc0
            @Override // com.android.systemui.bubbles.animation.PhysicsAnimationLayout.PhysicsAnimationController.ChildAnimationConfigurator
            public final void configureAnimationForChildAtIndex(int i, PhysicsAnimationLayout.PhysicsPropertyAnimator physicsPropertyAnimator) {
                physicsPropertyAnimator.scaleX(1.0f, new Runnable[0]).scaleY(1.0f, new Runnable[0]).alpha(1.0f, new Runnable[0]);
            }
        }).startAll(new Runnable[0]);
    }

    @Override // com.android.systemui.bubbles.animation.PhysicsAnimationLayout.PhysicsAnimationController
    Set<DynamicAnimation.ViewProperty> getAnimatedProperties() {
        return Sets.newHashSet(new DynamicAnimation.ViewProperty[]{DynamicAnimation.TRANSLATION_X, DynamicAnimation.TRANSLATION_Y, DynamicAnimation.SCALE_X, DynamicAnimation.SCALE_Y, DynamicAnimation.ALPHA});
    }

    @Override // com.android.systemui.bubbles.animation.PhysicsAnimationLayout.PhysicsAnimationController
    int getNextAnimationInChain(DynamicAnimation.ViewProperty property, int index) {
        return -1;
    }

    @Override // com.android.systemui.bubbles.animation.PhysicsAnimationLayout.PhysicsAnimationController
    float getOffsetForChainedPropertyAnimation(DynamicAnimation.ViewProperty property) {
        return 0.0f;
    }

    @Override // com.android.systemui.bubbles.animation.PhysicsAnimationLayout.PhysicsAnimationController
    SpringForce getSpringForce(DynamicAnimation.ViewProperty property, View view) {
        return new SpringForce().setDampingRatio(0.75f).setStiffness(200.0f);
    }

    @Override // com.android.systemui.bubbles.animation.PhysicsAnimationLayout.PhysicsAnimationController
    void onChildAdded(View child, int index) {
        if (this.mAnimatingExpand) {
            startOrUpdatePathAnimation(true);
        } else if (this.mAnimatingCollapse) {
            startOrUpdatePathAnimation(false);
        } else {
            child.setTranslationX(getBubbleLeft(index));
            animationForChild(child).translationY(getExpandedY() - (this.mBubbleSizePx * 4.0f), getExpandedY(), new Runnable[0]).start(new Runnable[0]);
            updateBubblePositions();
        }
    }

    @Override // com.android.systemui.bubbles.animation.PhysicsAnimationLayout.PhysicsAnimationController
    void onChildRemoved(View child, int index, Runnable finishRemoval) {
        PhysicsAnimationLayout.PhysicsPropertyAnimator animator = animationForChild(child);
        if (child.equals(this.mBubbleDraggingOut)) {
            this.mBubbleDraggingOut = null;
            finishRemoval.run();
        } else {
            animator.alpha(0.0f, finishRemoval).withStiffness(10000.0f).withDampingRatio(1.0f).scaleX(1.1f, new Runnable[0]).scaleY(1.1f, new Runnable[0]).start(new Runnable[0]);
        }
        updateBubblePositions();
    }

    @Override // com.android.systemui.bubbles.animation.PhysicsAnimationLayout.PhysicsAnimationController
    void onChildReordered(View child, int oldIndex, int newIndex) {
        updateBubblePositions();
        if (this.mAnimatingCollapse) {
            startOrUpdatePathAnimation(false);
        }
    }

    private void updateBubblePositions() {
        if (this.mAnimatingExpand || this.mAnimatingCollapse) {
            return;
        }
        for (int i = 0; i < this.mLayout.getChildCount(); i++) {
            View bubble = this.mLayout.getChildAt(i);
            if (bubble.equals(this.mBubbleDraggingOut)) {
                return;
            }
            animationForChild(bubble).translationX(getBubbleLeft(i), new Runnable[0]).start(new Runnable[0]);
        }
    }

    public float getBubbleLeft(int index) {
        float bubbleFromRowLeft = index * (this.mBubbleSizePx + getSpaceBetweenBubbles());
        return getRowLeft() + bubbleFromRowLeft;
    }

    private float getWidthForDisplayingBubbles() {
        float availableWidth = getAvailableScreenWidth(true);
        if (this.mScreenOrientation == 2) {
            return Math.max(this.mDisplaySize.y, CENTER_BUBBLES_LANDSCAPE_PERCENT * availableWidth);
        }
        return availableWidth;
    }

    private float getAvailableScreenWidth(boolean subtractStableInsets) {
        float availableSize = this.mDisplaySize.x;
        WindowInsets insets = this.mLayout != null ? this.mLayout.getRootWindowInsets() : null;
        if (insets != null) {
            int cutoutLeft = 0;
            int cutoutRight = 0;
            DisplayCutout cutout = insets.getDisplayCutout();
            if (cutout != null) {
                cutoutLeft = cutout.getSafeInsetLeft();
                cutoutRight = cutout.getSafeInsetRight();
            }
            int stableLeft = subtractStableInsets ? insets.getStableInsetLeft() : 0;
            int stableRight = subtractStableInsets ? insets.getStableInsetRight() : 0;
            return (availableSize - Math.max(stableLeft, cutoutLeft)) - Math.max(stableRight, cutoutRight);
        }
        return availableSize;
    }

    private float getRowLeft() {
        if (this.mLayout == null) {
            return 0.0f;
        }
        int bubbleCount = this.mLayout.getChildCount();
        float totalBubbleWidth = bubbleCount * this.mBubbleSizePx;
        float totalGapWidth = (bubbleCount - 1) * getSpaceBetweenBubbles();
        float rowWidth = totalGapWidth + totalBubbleWidth;
        float trueCenter = getAvailableScreenWidth(false) / 2.0f;
        float halfRow = rowWidth / 2.0f;
        float rowLeft = trueCenter - halfRow;
        return rowLeft;
    }

    private float getSpaceBetweenBubbles() {
        float rowMargins = (this.mExpandedViewPadding + this.mLauncherGridDiff) * 2.0f;
        float maxRowWidth = getWidthForDisplayingBubbles() - rowMargins;
        int i = this.mBubblesMaxRendered;
        float totalBubbleWidth = i * this.mBubbleSizePx;
        float totalGapWidth = maxRowWidth - totalBubbleWidth;
        int gapCount = i - 1;
        float gapWidth = totalGapWidth / gapCount;
        return gapWidth;
    }
}
