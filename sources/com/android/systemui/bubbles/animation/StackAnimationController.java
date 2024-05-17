package com.android.systemui.bubbles.animation;

import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import androidx.annotation.Nullable;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.systemui.R;
import com.android.systemui.bubbles.animation.PhysicsAnimationLayout;
import com.google.android.collect.Sets;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Set;
/* loaded from: classes21.dex */
public class StackAnimationController extends PhysicsAnimationLayout.PhysicsAnimationController {
    private static final float ANIMATE_IN_STARTING_SCALE = 1.15f;
    private static final int ANIMATE_IN_START_DELAY = 25;
    private static final float ANIMATE_IN_STIFFNESS = 1000.0f;
    private static final int ANIMATE_TRANSLATION_FACTOR = 4;
    private static final float DEFAULT_BOUNCINESS = 0.9f;
    private static final int DEFAULT_STIFFNESS = 12000;
    private static final float ESCAPE_VELOCITY = 750.0f;
    private static final int FLING_FOLLOW_STIFFNESS = 20000;
    private static final float FLING_FRICTION_X = 2.2f;
    private static final float FLING_FRICTION_Y = 2.2f;
    private static final float SPRING_AFTER_FLING_DAMPING_RATIO = 0.85f;
    private static final int SPRING_AFTER_FLING_STIFFNESS = 750;
    private static final String TAG = "Bubbs.StackCtrl";
    private int mBubbleIconBitmapSize;
    private int mBubbleOffscreen;
    private int mBubblePaddingTop;
    private int mBubbleSize;
    @Nullable
    private PointF mRestingStackPosition;
    private float mStackOffset;
    private int mStackStartingVerticalOffset;
    private float mStatusBarHeight;
    private PointF mStackPosition = new PointF(-1.0f, -1.0f);
    private boolean mStackMovedToStartPosition = false;
    private float mImeHeight = 0.0f;
    private float mPreImeY = Float.MIN_VALUE;
    private HashMap<DynamicAnimation.ViewProperty, DynamicAnimation> mStackPositionAnimations = new HashMap<>();
    private boolean mIsMovingFromFlinging = false;
    private boolean mWithinDismissTarget = false;
    private boolean mFirstBubbleSpringingToTouch = false;

    public void moveFirstBubbleWithStackFollowing(float x, float y) {
        this.mPreImeY = Float.MIN_VALUE;
        moveFirstBubbleWithStackFollowing(DynamicAnimation.TRANSLATION_X, x);
        moveFirstBubbleWithStackFollowing(DynamicAnimation.TRANSLATION_Y, y);
        this.mIsMovingFromFlinging = false;
    }

    public PointF getStackPosition() {
        return this.mStackPosition;
    }

    public boolean isStackOnLeftSide() {
        if (this.mLayout == null || !isStackPositionSet()) {
            return false;
        }
        float stackCenter = this.mStackPosition.x + (this.mBubbleIconBitmapSize / 2);
        float screenCenter = this.mLayout.getWidth() / 2;
        return stackCenter < screenCenter;
    }

    public void springStack(float destinationX, float destinationY) {
        springFirstBubbleWithStackFollowing(DynamicAnimation.TRANSLATION_X, new SpringForce().setStiffness(ESCAPE_VELOCITY).setDampingRatio(SPRING_AFTER_FLING_DAMPING_RATIO), 0.0f, destinationX, new Runnable[0]);
        springFirstBubbleWithStackFollowing(DynamicAnimation.TRANSLATION_Y, new SpringForce().setStiffness(ESCAPE_VELOCITY).setDampingRatio(SPRING_AFTER_FLING_DAMPING_RATIO), 0.0f, destinationY, new Runnable[0]);
    }

    public float flingStackThenSpringToEdge(float x, float velX, float velY) {
        boolean z;
        float startXVelocity;
        boolean stackOnLeftSide = x - ((float) (this.mBubbleIconBitmapSize / 2)) < ((float) (this.mLayout.getWidth() / 2));
        if (stackOnLeftSide) {
            z = velX < ESCAPE_VELOCITY;
        } else {
            z = velX < -750.0f;
        }
        boolean stackShouldFlingLeft = z;
        RectF stackBounds = getAllowableStackPositionRegion();
        float destinationRelativeX = stackShouldFlingLeft ? stackBounds.left : stackBounds.right;
        if (this.mLayout == null || this.mLayout.getChildCount() == 0) {
            return destinationRelativeX;
        }
        float minimumVelocityToReachEdge = (destinationRelativeX - x) * 9.24f;
        if (stackShouldFlingLeft) {
            startXVelocity = Math.min(minimumVelocityToReachEdge, velX);
        } else {
            startXVelocity = Math.max(minimumVelocityToReachEdge, velX);
        }
        flingThenSpringFirstBubbleWithStackFollowing(DynamicAnimation.TRANSLATION_X, startXVelocity, 2.2f, new SpringForce().setStiffness(ESCAPE_VELOCITY).setDampingRatio(SPRING_AFTER_FLING_DAMPING_RATIO), Float.valueOf(destinationRelativeX));
        flingThenSpringFirstBubbleWithStackFollowing(DynamicAnimation.TRANSLATION_Y, velY, 2.2f, new SpringForce().setStiffness(ESCAPE_VELOCITY).setDampingRatio(SPRING_AFTER_FLING_DAMPING_RATIO), null);
        this.mFirstBubbleSpringingToTouch = false;
        this.mIsMovingFromFlinging = true;
        return destinationRelativeX;
    }

    public PointF getStackPositionAlongNearestHorizontalEdge() {
        PointF stackPos = getStackPosition();
        boolean onLeft = this.mLayout.isFirstChildXLeftOfCenter(stackPos.x);
        RectF bounds = getAllowableStackPositionRegion();
        stackPos.x = onLeft ? bounds.left : bounds.right;
        return stackPos;
    }

    public void moveStackToSimilarPositionAfterRotation(boolean wasOnLeft, float verticalPercent) {
        RectF allowablePos = getAllowableStackPositionRegion();
        float allowableRegionHeight = allowablePos.bottom - allowablePos.top;
        float x = wasOnLeft ? allowablePos.left : allowablePos.right;
        float y = (allowableRegionHeight * verticalPercent) + allowablePos.top;
        setStackPosition(new PointF(x, y));
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("StackAnimationController state:");
        pw.print("  isActive:             ");
        pw.println(isActiveController());
        pw.print("  restingStackPos:      ");
        PointF pointF = this.mRestingStackPosition;
        pw.println(pointF != null ? pointF.toString() : "null");
        pw.print("  currentStackPos:      ");
        pw.println(this.mStackPosition.toString());
        pw.print("  isMovingFromFlinging: ");
        pw.println(this.mIsMovingFromFlinging);
        pw.print("  withinDismiss:        ");
        pw.println(this.mWithinDismissTarget);
        pw.print("  firstBubbleSpringing: ");
        pw.println(this.mFirstBubbleSpringingToTouch);
    }

    protected void flingThenSpringFirstBubbleWithStackFollowing(final DynamicAnimation.ViewProperty property, float vel, float friction, final SpringForce spring, final Float finalPosition) {
        float f;
        float f2;
        Log.d(TAG, String.format("Flinging %s.", PhysicsAnimationLayout.getReadablePropertyName(property)));
        StackPositionProperty firstBubbleProperty = new StackPositionProperty(property);
        float currentValue = firstBubbleProperty.getValue(this);
        RectF bounds = getAllowableStackPositionRegion();
        if (property.equals(DynamicAnimation.TRANSLATION_X)) {
            f = bounds.left;
        } else {
            f = bounds.top;
        }
        final float min = f;
        if (property.equals(DynamicAnimation.TRANSLATION_X)) {
            f2 = bounds.right;
        } else {
            f2 = bounds.bottom;
        }
        final float max = f2;
        FlingAnimation flingAnimation = new FlingAnimation(this, firstBubbleProperty);
        flingAnimation.setFriction(friction).setStartVelocity(vel).setMinValue(Math.min(currentValue, min)).setMaxValue(Math.max(currentValue, max)).addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: com.android.systemui.bubbles.animation.-$$Lambda$StackAnimationController$bZgezj9fblRl_isenTD4ApewvoU
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f3, float f4) {
                StackAnimationController.this.lambda$flingThenSpringFirstBubbleWithStackFollowing$0$StackAnimationController(property, spring, finalPosition, min, max, dynamicAnimation, z, f3, f4);
            }
        });
        cancelStackPositionAnimation(property);
        this.mStackPositionAnimations.put(property, flingAnimation);
        flingAnimation.start();
    }

    public /* synthetic */ void lambda$flingThenSpringFirstBubbleWithStackFollowing$0$StackAnimationController(DynamicAnimation.ViewProperty property, SpringForce spring, Float finalPosition, float min, float max, DynamicAnimation animation, boolean canceled, float endValue, float endVelocity) {
        if (!canceled) {
            this.mRestingStackPosition = new PointF();
            this.mRestingStackPosition.set(this.mStackPosition);
            springFirstBubbleWithStackFollowing(property, spring, endVelocity, finalPosition != null ? finalPosition.floatValue() : Math.max(min, Math.min(max, endValue)), new Runnable[0]);
        }
    }

    public void cancelStackPositionAnimations() {
        cancelStackPositionAnimation(DynamicAnimation.TRANSLATION_X);
        cancelStackPositionAnimation(DynamicAnimation.TRANSLATION_Y);
        removeEndActionForProperty(DynamicAnimation.TRANSLATION_X);
        removeEndActionForProperty(DynamicAnimation.TRANSLATION_Y);
    }

    public void setImeHeight(int imeHeight) {
        this.mImeHeight = imeHeight;
    }

    public void animateForImeVisibility(boolean imeVisible) {
        float maxBubbleY = getAllowableStackPositionRegion().bottom;
        float destinationY = Float.MIN_VALUE;
        if (!imeVisible) {
            if (this.mPreImeY > Float.MIN_VALUE) {
                destinationY = this.mPreImeY;
                this.mPreImeY = Float.MIN_VALUE;
            }
        } else if (this.mStackPosition.y > maxBubbleY && this.mPreImeY == Float.MIN_VALUE) {
            this.mPreImeY = this.mStackPosition.y;
            destinationY = maxBubbleY;
        }
        if (destinationY > Float.MIN_VALUE) {
            springFirstBubbleWithStackFollowing(DynamicAnimation.TRANSLATION_Y, getSpringForce(DynamicAnimation.TRANSLATION_Y, null).setStiffness(200.0f), 0.0f, destinationY, new Runnable[0]);
        }
    }

    public RectF getAllowableStackPositionRegion() {
        int i;
        int i2;
        float f;
        WindowInsets insets = this.mLayout.getRootWindowInsets();
        RectF allowableRegion = new RectF();
        if (insets != null) {
            int i3 = -this.mBubbleOffscreen;
            int systemWindowInsetLeft = insets.getSystemWindowInsetLeft();
            if (insets.getDisplayCutout() != null) {
                i = insets.getDisplayCutout().getSafeInsetLeft();
            } else {
                i = 0;
            }
            allowableRegion.left = i3 + Math.max(systemWindowInsetLeft, i);
            int width = (this.mLayout.getWidth() - this.mBubbleSize) + this.mBubbleOffscreen;
            int systemWindowInsetRight = insets.getSystemWindowInsetRight();
            if (insets.getDisplayCutout() != null) {
                i2 = insets.getDisplayCutout().getSafeInsetRight();
            } else {
                i2 = 0;
            }
            allowableRegion.right = width - Math.max(systemWindowInsetRight, i2);
            float f2 = this.mBubblePaddingTop;
            float f3 = this.mStatusBarHeight;
            if (insets.getDisplayCutout() != null) {
                f = insets.getDisplayCutout().getSafeInsetTop();
            } else {
                f = 0.0f;
            }
            allowableRegion.top = f2 + Math.max(f3, f);
            int height = this.mLayout.getHeight() - this.mBubbleSize;
            int i4 = this.mBubblePaddingTop;
            float f4 = height - i4;
            float f5 = this.mImeHeight;
            allowableRegion.bottom = (f4 - (f5 > Float.MIN_VALUE ? f5 + i4 : 0.0f)) - Math.max(insets.getSystemWindowInsetBottom(), insets.getDisplayCutout() != null ? insets.getDisplayCutout().getSafeInsetBottom() : 0);
        }
        return allowableRegion;
    }

    public void moveStackFromTouch(float x, float y) {
        if (this.mFirstBubbleSpringingToTouch) {
            SpringAnimation springToTouchX = (SpringAnimation) this.mStackPositionAnimations.get(DynamicAnimation.TRANSLATION_X);
            SpringAnimation springToTouchY = (SpringAnimation) this.mStackPositionAnimations.get(DynamicAnimation.TRANSLATION_Y);
            if (springToTouchX.isRunning() || springToTouchY.isRunning()) {
                springToTouchX.animateToFinalPosition(x);
                springToTouchY.animateToFinalPosition(y);
            } else {
                this.mFirstBubbleSpringingToTouch = false;
            }
        }
        if (!this.mFirstBubbleSpringingToTouch && !this.mWithinDismissTarget) {
            moveFirstBubbleWithStackFollowing(x, y);
        }
    }

    public void demagnetizeFromDismissToPoint(float x, float y, float velX, float velY) {
        this.mWithinDismissTarget = false;
        this.mFirstBubbleSpringingToTouch = true;
        springFirstBubbleWithStackFollowing(DynamicAnimation.TRANSLATION_X, new SpringForce().setDampingRatio(DEFAULT_BOUNCINESS).setStiffness(12000.0f), velX, x, new Runnable[0]);
        springFirstBubbleWithStackFollowing(DynamicAnimation.TRANSLATION_Y, new SpringForce().setDampingRatio(DEFAULT_BOUNCINESS).setStiffness(12000.0f), velY, y, new Runnable[0]);
    }

    public void magnetToDismiss(float velX, float velY, float destY, Runnable after) {
        this.mWithinDismissTarget = true;
        this.mFirstBubbleSpringingToTouch = false;
        springFirstBubbleWithStackFollowing(DynamicAnimation.TRANSLATION_X, new SpringForce().setDampingRatio(0.75f).setStiffness(1500.0f), velX, (this.mLayout.getWidth() / 2.0f) - (this.mBubbleIconBitmapSize / 2.0f), new Runnable[0]);
        springFirstBubbleWithStackFollowing(DynamicAnimation.TRANSLATION_Y, new SpringForce().setDampingRatio(0.75f).setStiffness(1500.0f), velY, destY, after);
    }

    public void implodeStack(final Runnable after) {
        animationForChildAtIndex(0).scaleX(0.5f, new Runnable[0]).scaleY(0.5f, new Runnable[0]).alpha(0.0f, new Runnable[0]).withDampingRatio(1.0f).withStiffness(10000.0f).start(new Runnable() { // from class: com.android.systemui.bubbles.animation.-$$Lambda$StackAnimationController$nsOO7KNkf7Bcvco8fAAwW0CqLEk
            @Override // java.lang.Runnable
            public final void run() {
                StackAnimationController.this.lambda$implodeStack$1$StackAnimationController(after);
            }
        });
    }

    public /* synthetic */ void lambda$implodeStack$1$StackAnimationController(Runnable after) {
        after.run();
        this.mWithinDismissTarget = false;
    }

    protected void springFirstBubbleWithStackFollowing(DynamicAnimation.ViewProperty property, SpringForce spring, float vel, float finalPosition, @Nullable final Runnable... after) {
        if (this.mLayout.getChildCount() == 0) {
            return;
        }
        Log.d(TAG, String.format("Springing %s to final position %f.", PhysicsAnimationLayout.getReadablePropertyName(property), Float.valueOf(finalPosition)));
        StackPositionProperty firstBubbleProperty = new StackPositionProperty(property);
        SpringAnimation springAnimation = new SpringAnimation(this, firstBubbleProperty).setSpring(spring).addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: com.android.systemui.bubbles.animation.-$$Lambda$StackAnimationController$utG_Ji6wVaLsAMlAinJOG0-4Hqc
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                StackAnimationController.lambda$springFirstBubbleWithStackFollowing$2(after, dynamicAnimation, z, f, f2);
            }
        }).setStartVelocity(vel);
        cancelStackPositionAnimation(property);
        this.mStackPositionAnimations.put(property, springAnimation);
        springAnimation.animateToFinalPosition(finalPosition);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$springFirstBubbleWithStackFollowing$2(Runnable[] after, DynamicAnimation dynamicAnimation, boolean b, float v, float v1) {
        if (after != null) {
            for (Runnable callback : after) {
                callback.run();
            }
        }
    }

    @Override // com.android.systemui.bubbles.animation.PhysicsAnimationLayout.PhysicsAnimationController
    Set<DynamicAnimation.ViewProperty> getAnimatedProperties() {
        return Sets.newHashSet(new DynamicAnimation.ViewProperty[]{DynamicAnimation.TRANSLATION_X, DynamicAnimation.TRANSLATION_Y, DynamicAnimation.ALPHA, DynamicAnimation.SCALE_X, DynamicAnimation.SCALE_Y});
    }

    @Override // com.android.systemui.bubbles.animation.PhysicsAnimationLayout.PhysicsAnimationController
    int getNextAnimationInChain(DynamicAnimation.ViewProperty property, int index) {
        if (property.equals(DynamicAnimation.TRANSLATION_X) || property.equals(DynamicAnimation.TRANSLATION_Y)) {
            return index + 1;
        }
        if (this.mWithinDismissTarget) {
            return index + 1;
        }
        return -1;
    }

    @Override // com.android.systemui.bubbles.animation.PhysicsAnimationLayout.PhysicsAnimationController
    float getOffsetForChainedPropertyAnimation(DynamicAnimation.ViewProperty property) {
        if (!property.equals(DynamicAnimation.TRANSLATION_X) || this.mWithinDismissTarget) {
            return 0.0f;
        }
        return this.mLayout.isFirstChildXLeftOfCenter(this.mStackPosition.x) ? -this.mStackOffset : this.mStackOffset;
    }

    @Override // com.android.systemui.bubbles.animation.PhysicsAnimationLayout.PhysicsAnimationController
    SpringForce getSpringForce(DynamicAnimation.ViewProperty property, View view) {
        return new SpringForce().setDampingRatio(DEFAULT_BOUNCINESS).setStiffness(this.mIsMovingFromFlinging ? 20000.0f : 12000.0f);
    }

    @Override // com.android.systemui.bubbles.animation.PhysicsAnimationLayout.PhysicsAnimationController
    void onChildAdded(View child, int index) {
        if (this.mWithinDismissTarget) {
            return;
        }
        if (this.mLayout.getChildCount() == 1) {
            moveStackToStartPosition();
        } else if (isStackPositionSet() && this.mLayout.indexOfChild(child) == 0) {
            animateInBubble(child, index);
        }
    }

    @Override // com.android.systemui.bubbles.animation.PhysicsAnimationLayout.PhysicsAnimationController
    void onChildRemoved(View child, int index, Runnable finishRemoval) {
        float xOffset = getOffsetForChainedPropertyAnimation(DynamicAnimation.TRANSLATION_X);
        animationForChild(child).alpha(0.0f, finishRemoval).scaleX(ANIMATE_IN_STARTING_SCALE, new Runnable[0]).scaleY(ANIMATE_IN_STARTING_SCALE, new Runnable[0]).translationX(this.mStackPosition.x - ((-xOffset) * 4.0f), new Runnable[0]).start(new Runnable[0]);
        if (this.mLayout.getChildCount() > 0) {
            animationForChildAtIndex(0).translationX(this.mStackPosition.x, new Runnable[0]).start(new Runnable[0]);
        } else {
            this.mWithinDismissTarget = false;
        }
    }

    @Override // com.android.systemui.bubbles.animation.PhysicsAnimationLayout.PhysicsAnimationController
    void onChildReordered(View child, int oldIndex, int newIndex) {
        if (isStackPositionSet()) {
            setStackPosition(this.mStackPosition);
        }
    }

    @Override // com.android.systemui.bubbles.animation.PhysicsAnimationLayout.PhysicsAnimationController
    void onActiveControllerForLayout(PhysicsAnimationLayout layout) {
        Resources res = layout.getResources();
        this.mStackOffset = res.getDimensionPixelSize(R.dimen.bubble_stack_offset);
        this.mBubbleSize = res.getDimensionPixelSize(R.dimen.individual_bubble_size);
        this.mBubbleIconBitmapSize = res.getDimensionPixelSize(R.dimen.bubble_icon_bitmap_size);
        this.mBubblePaddingTop = res.getDimensionPixelSize(R.dimen.bubble_padding_top);
        this.mBubbleOffscreen = res.getDimensionPixelSize(R.dimen.bubble_stack_offscreen);
        this.mStackStartingVerticalOffset = res.getDimensionPixelSize(R.dimen.bubble_stack_starting_offset_y);
        this.mStatusBarHeight = res.getDimensionPixelSize(17105438);
    }

    public void updateOrientation(int orientation) {
        if (this.mLayout != null) {
            Resources res = this.mLayout.getContext().getResources();
            this.mBubblePaddingTop = res.getDimensionPixelSize(R.dimen.bubble_padding_top);
            this.mStatusBarHeight = res.getDimensionPixelSize(17105438);
        }
    }

    private void moveStackToStartPosition() {
        this.mLayout.setVisibility(4);
        this.mLayout.post(new Runnable() { // from class: com.android.systemui.bubbles.animation.-$$Lambda$StackAnimationController$XG5dbVvx6CTopXCQV48uovjmoQo
            @Override // java.lang.Runnable
            public final void run() {
                StackAnimationController.this.lambda$moveStackToStartPosition$3$StackAnimationController();
            }
        });
    }

    public /* synthetic */ void lambda$moveStackToStartPosition$3$StackAnimationController() {
        PointF pointF = this.mRestingStackPosition;
        if (pointF == null) {
            pointF = getDefaultStartPosition();
        }
        setStackPosition(pointF);
        this.mStackMovedToStartPosition = true;
        this.mLayout.setVisibility(0);
        if (this.mLayout.getChildCount() > 0) {
            animateInBubble(this.mLayout.getChildAt(0), 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void moveFirstBubbleWithStackFollowing(DynamicAnimation.ViewProperty property, float value) {
        if (property.equals(DynamicAnimation.TRANSLATION_X)) {
            this.mStackPosition.x = value;
        } else if (property.equals(DynamicAnimation.TRANSLATION_Y)) {
            this.mStackPosition.y = value;
        }
        if (this.mLayout.getChildCount() > 0) {
            property.setValue(this.mLayout.getChildAt(0), value);
            if (this.mLayout.getChildCount() > 1) {
                animationForChildAtIndex(1).property(property, getOffsetForChainedPropertyAnimation(property) + value, new Runnable[0]).start(new Runnable[0]);
            }
        }
    }

    private void setStackPosition(PointF pos) {
        Log.d(TAG, String.format("Setting position to (%f, %f).", Float.valueOf(pos.x), Float.valueOf(pos.y)));
        this.mStackPosition.set(pos.x, pos.y);
        if (isActiveController()) {
            this.mLayout.cancelAllAnimationsOfProperties(DynamicAnimation.TRANSLATION_X, DynamicAnimation.TRANSLATION_Y);
            cancelStackPositionAnimations();
            float xOffset = getOffsetForChainedPropertyAnimation(DynamicAnimation.TRANSLATION_X);
            float yOffset = getOffsetForChainedPropertyAnimation(DynamicAnimation.TRANSLATION_Y);
            for (int i = 0; i < this.mLayout.getChildCount(); i++) {
                this.mLayout.getChildAt(i).setTranslationX(pos.x + (i * xOffset));
                this.mLayout.getChildAt(i).setTranslationY(pos.y + (i * yOffset));
            }
        }
    }

    private PointF getDefaultStartPosition() {
        return new PointF(getAllowableStackPositionRegion().right, getAllowableStackPositionRegion().top + this.mStackStartingVerticalOffset);
    }

    private boolean isStackPositionSet() {
        return this.mStackMovedToStartPosition;
    }

    private void animateInBubble(View child, int index) {
        if (!isActiveController()) {
            return;
        }
        float xOffset = getOffsetForChainedPropertyAnimation(DynamicAnimation.TRANSLATION_X);
        child.setTranslationX(this.mStackPosition.x + (index * xOffset));
        child.setTranslationY(this.mStackPosition.y);
        child.setScaleX(0.0f);
        child.setScaleY(0.0f);
        if (index + 1 < this.mLayout.getChildCount()) {
            animationForChildAtIndex(index + 1).translationX(this.mStackPosition.x + ((index + 1) * xOffset), new Runnable[0]).withStiffness(200.0f).start(new Runnable[0]);
        }
        animationForChild(child).scaleX(1.0f, new Runnable[0]).scaleY(1.0f, new Runnable[0]).withStiffness(ANIMATE_IN_STIFFNESS).withStartDelay(this.mLayout.getChildCount() > 1 ? 25L : 0L).start(new Runnable[0]);
    }

    private void cancelStackPositionAnimation(DynamicAnimation.ViewProperty property) {
        if (this.mStackPositionAnimations.containsKey(property)) {
            this.mStackPositionAnimations.get(property).cancel();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class StackPositionProperty extends FloatPropertyCompat<StackAnimationController> {
        private final DynamicAnimation.ViewProperty mProperty;

        private StackPositionProperty(DynamicAnimation.ViewProperty property) {
            super(property.toString());
            this.mProperty = property;
        }

        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public float getValue(StackAnimationController controller) {
            if (StackAnimationController.this.mLayout.getChildCount() > 0) {
                return this.mProperty.getValue(StackAnimationController.this.mLayout.getChildAt(0));
            }
            return 0.0f;
        }

        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public void setValue(StackAnimationController controller, float value) {
            StackAnimationController.this.moveFirstBubbleWithStackFollowing(this.mProperty, value);
        }
    }
}
