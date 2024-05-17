package com.android.systemui.bubbles;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import com.android.systemui.Dependency;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes21.dex */
public class BubbleTouchHandler implements View.OnTouchListener {
    private static final float DISMISS_FLING_TARGET_WIDTH_PERCENT = 0.5f;
    private static final float INDIVIDUAL_BUBBLE_DISMISS_MIN_VELOCITY = 6000.0f;
    private static final float STACK_DISMISS_MIN_VELOCITY = 4000.0f;
    private final BubbleData mBubbleData;
    private boolean mInDismissTarget;
    private boolean mMovedEnough;
    private final BubbleStackView mStack;
    private int mTouchSlopSquared;
    private View mTouchedView;
    private VelocityTracker mVelocityTracker;
    private final PointF mTouchDown = new PointF();
    private final PointF mViewPositionOnTouchDown = new PointF();
    private BubbleController mController = (BubbleController) Dependency.get(BubbleController.class);

    /* JADX INFO: Access modifiers changed from: package-private */
    public BubbleTouchHandler(BubbleStackView stackView, BubbleData bubbleData, Context context) {
        int touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mTouchSlopSquared = touchSlop * touchSlop;
        this.mBubbleData = bubbleData;
        this.mStack = stackView;
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        View view;
        boolean z;
        int action = event.getActionMasked();
        if (this.mTouchedView == null) {
            this.mTouchedView = this.mStack.getTargetView(event);
        }
        if (action == 4 || (view = this.mTouchedView) == null) {
            this.mBubbleData.setExpanded(false);
            resetForNextGesture();
            return false;
        } else if ((view instanceof BubbleView) || (view instanceof BubbleStackView) || (view instanceof BubbleFlyoutView)) {
            final boolean isStack = this.mStack.equals(this.mTouchedView);
            boolean isFlyout = this.mStack.getFlyoutView().equals(this.mTouchedView);
            float rawX = event.getRawX();
            float rawY = event.getRawY();
            float viewX = (this.mViewPositionOnTouchDown.x + rawX) - this.mTouchDown.x;
            float viewY = (this.mViewPositionOnTouchDown.y + rawY) - this.mTouchDown.y;
            if (action == 0) {
                trackMovement(event);
                this.mTouchDown.set(rawX, rawY);
                this.mStack.onGestureStart();
                if (isStack) {
                    this.mViewPositionOnTouchDown.set(this.mStack.getStackPosition());
                    this.mStack.onDragStart();
                    return true;
                } else if (isFlyout) {
                    this.mStack.onFlyoutDragStart();
                    return true;
                } else {
                    this.mViewPositionOnTouchDown.set(this.mTouchedView.getTranslationX(), this.mTouchedView.getTranslationY());
                    this.mStack.onBubbleDragStart(this.mTouchedView);
                    return true;
                }
            } else if (action != 1) {
                if (action != 2) {
                    if (action != 3) {
                        return true;
                    }
                    resetForNextGesture();
                    return true;
                }
                trackMovement(event);
                float deltaX = rawX - this.mTouchDown.x;
                float deltaY = rawY - this.mTouchDown.y;
                if ((deltaX * deltaX) + (deltaY * deltaY) > this.mTouchSlopSquared && !this.mMovedEnough) {
                    this.mMovedEnough = true;
                }
                if (this.mMovedEnough) {
                    if (isStack) {
                        this.mStack.onDragged(viewX, viewY);
                    } else if (!isFlyout) {
                        this.mStack.onBubbleDragged(this.mTouchedView, viewX, viewY);
                    } else {
                        this.mStack.onFlyoutDragged(deltaX);
                    }
                }
                boolean currentlyInDismissTarget = this.mStack.isInDismissTarget(event);
                if (currentlyInDismissTarget == this.mInDismissTarget) {
                    return true;
                }
                this.mInDismissTarget = currentlyInDismissTarget;
                this.mVelocityTracker.computeCurrentVelocity(1000);
                float velX = this.mVelocityTracker.getXVelocity();
                float velY = this.mVelocityTracker.getYVelocity();
                if (!isFlyout) {
                    this.mStack.animateMagnetToDismissTarget(this.mTouchedView, this.mInDismissTarget, viewX, viewY, velX, velY);
                    return true;
                }
                return true;
            } else {
                trackMovement(event);
                this.mVelocityTracker.computeCurrentVelocity(1000);
                float velX2 = this.mVelocityTracker.getXVelocity();
                float velY2 = this.mVelocityTracker.getYVelocity();
                if (isStack) {
                    z = this.mInDismissTarget || isFastFlingTowardsDismissTarget(rawX, rawY, velX2, velY2);
                } else {
                    z = this.mInDismissTarget || velY2 > INDIVIDUAL_BUBBLE_DISMISS_MIN_VELOCITY;
                }
                boolean shouldDismiss = z;
                if (isFlyout && this.mMovedEnough) {
                    this.mStack.onFlyoutDragFinished(rawX - this.mTouchDown.x, velX2);
                } else if (shouldDismiss) {
                    final String individualBubbleKey = isStack ? null : ((BubbleView) this.mTouchedView).getKey();
                    this.mStack.magnetToStackIfNeededThenAnimateDismissal(this.mTouchedView, velX2, velY2, new Runnable() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleTouchHandler$CRI1SYVVWTk0CE6JELaYKVCSdCI
                        @Override // java.lang.Runnable
                        public final void run() {
                            BubbleTouchHandler.this.lambda$onTouch$0$BubbleTouchHandler(isStack, individualBubbleKey);
                        }
                    });
                } else if (isFlyout) {
                    if (!this.mBubbleData.isExpanded() && !this.mMovedEnough) {
                        this.mStack.onFlyoutTapped();
                    }
                } else if (!this.mMovedEnough) {
                    if (this.mTouchedView == this.mStack.getExpandedBubbleView()) {
                        this.mBubbleData.setExpanded(false);
                    } else if (isStack || isFlyout) {
                        BubbleData bubbleData = this.mBubbleData;
                        bubbleData.setExpanded(!bubbleData.isExpanded());
                    } else {
                        String key = ((BubbleView) this.mTouchedView).getKey();
                        BubbleData bubbleData2 = this.mBubbleData;
                        bubbleData2.setSelectedBubble(bubbleData2.getBubbleWithKey(key));
                    }
                } else if (!isStack) {
                    this.mStack.onBubbleDragFinish(this.mTouchedView, viewX, viewY, velX2, velY2);
                } else {
                    this.mStack.onDragFinish(viewX, viewY, velX2, velY2);
                }
                resetForNextGesture();
                return true;
            }
        } else {
            resetForNextGesture();
            return false;
        }
    }

    public /* synthetic */ void lambda$onTouch$0$BubbleTouchHandler(boolean isStack, String individualBubbleKey) {
        if (isStack) {
            this.mController.dismissStack(1);
        } else {
            this.mController.removeBubble(individualBubbleKey, 1);
        }
    }

    private boolean isFastFlingTowardsDismissTarget(float rawX, float rawY, float velX, float velY) {
        if (velY <= 0.0f) {
            return false;
        }
        float bottomOfScreenInterceptX = rawX;
        if (velX != 0.0f) {
            float slope = velY / velX;
            float yIntercept = rawY - (slope * rawX);
            bottomOfScreenInterceptX = (this.mStack.getHeight() - yIntercept) / slope;
        }
        float dismissTargetWidth = this.mStack.getWidth() * 0.5f;
        return velY > STACK_DISMISS_MIN_VELOCITY && bottomOfScreenInterceptX > dismissTargetWidth / 2.0f && bottomOfScreenInterceptX < ((float) this.mStack.getWidth()) - (dismissTargetWidth / 2.0f);
    }

    private void resetForNextGesture() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
        this.mTouchedView = null;
        this.mMovedEnough = false;
        this.mInDismissTarget = false;
        this.mStack.onGestureFinished();
    }

    private void trackMovement(MotionEvent event) {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(event);
    }
}
