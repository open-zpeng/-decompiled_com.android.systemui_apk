package com.android.systemui.statusbar.notification.stack;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.Interpolator;
import com.android.systemui.Interpolators;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes21.dex */
public class NotificationSection {
    private ActivatableNotificationView mFirstVisibleChild;
    private ActivatableNotificationView mLastVisibleChild;
    private View mOwningView;
    private Rect mBounds = new Rect();
    private Rect mCurrentBounds = new Rect(-1, -1, -1, -1);
    private Rect mStartAnimationRect = new Rect();
    private Rect mEndAnimationRect = new Rect();
    private ObjectAnimator mTopAnimator = null;
    private ObjectAnimator mBottomAnimator = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NotificationSection(View owningView) {
        this.mOwningView = owningView;
    }

    public void cancelAnimators() {
        ObjectAnimator objectAnimator = this.mBottomAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        ObjectAnimator objectAnimator2 = this.mTopAnimator;
        if (objectAnimator2 != null) {
            objectAnimator2.cancel();
        }
    }

    public Rect getCurrentBounds() {
        return this.mCurrentBounds;
    }

    public Rect getBounds() {
        return this.mBounds;
    }

    public boolean didBoundsChange() {
        return !this.mCurrentBounds.equals(this.mBounds);
    }

    public boolean areBoundsAnimating() {
        return (this.mBottomAnimator == null && this.mTopAnimator == null) ? false : true;
    }

    public void startBackgroundAnimation(boolean animateTop, boolean animateBottom) {
        this.mCurrentBounds.left = this.mBounds.left;
        this.mCurrentBounds.right = this.mBounds.right;
        startBottomAnimation(animateBottom);
        startTopAnimation(animateTop);
    }

    private void startTopAnimation(boolean animate) {
        int previousEndValue = this.mEndAnimationRect.top;
        int newEndValue = this.mBounds.top;
        ObjectAnimator previousAnimator = this.mTopAnimator;
        if (previousAnimator == null || previousEndValue != newEndValue) {
            if (!animate) {
                if (previousAnimator != null) {
                    int previousStartValue = this.mStartAnimationRect.top;
                    PropertyValuesHolder[] values = previousAnimator.getValues();
                    values[0].setIntValues(previousStartValue, newEndValue);
                    this.mStartAnimationRect.top = previousStartValue;
                    this.mEndAnimationRect.top = newEndValue;
                    previousAnimator.setCurrentPlayTime(previousAnimator.getCurrentPlayTime());
                    return;
                }
                setBackgroundTop(newEndValue);
                return;
            }
            if (previousAnimator != null) {
                previousAnimator.cancel();
            }
            ObjectAnimator animator = ObjectAnimator.ofInt(this, "backgroundTop", this.mCurrentBounds.top, newEndValue);
            Interpolator interpolator = Interpolators.FAST_OUT_SLOW_IN;
            animator.setInterpolator(interpolator);
            animator.setDuration(360L);
            animator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.notification.stack.NotificationSection.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    NotificationSection.this.mStartAnimationRect.top = -1;
                    NotificationSection.this.mEndAnimationRect.top = -1;
                    NotificationSection.this.mTopAnimator = null;
                }
            });
            animator.start();
            this.mStartAnimationRect.top = this.mCurrentBounds.top;
            this.mEndAnimationRect.top = newEndValue;
            this.mTopAnimator = animator;
        }
    }

    private void startBottomAnimation(boolean animate) {
        int previousStartValue = this.mStartAnimationRect.bottom;
        int previousEndValue = this.mEndAnimationRect.bottom;
        int newEndValue = this.mBounds.bottom;
        ObjectAnimator previousAnimator = this.mBottomAnimator;
        if (previousAnimator == null || previousEndValue != newEndValue) {
            if (!animate) {
                if (previousAnimator != null) {
                    PropertyValuesHolder[] values = previousAnimator.getValues();
                    values[0].setIntValues(previousStartValue, newEndValue);
                    this.mStartAnimationRect.bottom = previousStartValue;
                    this.mEndAnimationRect.bottom = newEndValue;
                    previousAnimator.setCurrentPlayTime(previousAnimator.getCurrentPlayTime());
                    return;
                }
                setBackgroundBottom(newEndValue);
                return;
            }
            if (previousAnimator != null) {
                previousAnimator.cancel();
            }
            ObjectAnimator animator = ObjectAnimator.ofInt(this, "backgroundBottom", this.mCurrentBounds.bottom, newEndValue);
            Interpolator interpolator = Interpolators.FAST_OUT_SLOW_IN;
            animator.setInterpolator(interpolator);
            animator.setDuration(360L);
            animator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.notification.stack.NotificationSection.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    NotificationSection.this.mStartAnimationRect.bottom = -1;
                    NotificationSection.this.mEndAnimationRect.bottom = -1;
                    NotificationSection.this.mBottomAnimator = null;
                }
            });
            animator.start();
            this.mStartAnimationRect.bottom = this.mCurrentBounds.bottom;
            this.mEndAnimationRect.bottom = newEndValue;
            this.mBottomAnimator = animator;
        }
    }

    private void setBackgroundTop(int top) {
        this.mCurrentBounds.top = top;
        this.mOwningView.invalidate();
    }

    private void setBackgroundBottom(int bottom) {
        this.mCurrentBounds.bottom = bottom;
        this.mOwningView.invalidate();
    }

    public ActivatableNotificationView getFirstVisibleChild() {
        return this.mFirstVisibleChild;
    }

    public ActivatableNotificationView getLastVisibleChild() {
        return this.mLastVisibleChild;
    }

    public void setFirstVisibleChild(ActivatableNotificationView child) {
        this.mFirstVisibleChild = child;
    }

    public void setLastVisibleChild(ActivatableNotificationView child) {
        this.mLastVisibleChild = child;
    }

    public void resetCurrentBounds() {
        this.mCurrentBounds.set(this.mBounds);
    }

    public boolean isTargetTop(int top) {
        return (this.mTopAnimator == null && this.mCurrentBounds.top == top) || (this.mTopAnimator != null && this.mEndAnimationRect.top == top);
    }

    public boolean isTargetBottom(int bottom) {
        return (this.mBottomAnimator == null && this.mCurrentBounds.bottom == bottom) || (this.mBottomAnimator != null && this.mEndAnimationRect.bottom == bottom);
    }

    public int updateBounds(int minTopPosition, int minBottomPosition, boolean shiftBackgroundWithFirst) {
        int newBottom;
        int newTop;
        int top = minTopPosition;
        int bottom = minTopPosition;
        ActivatableNotificationView firstView = getFirstVisibleChild();
        if (firstView != null) {
            int finalTranslationY = (int) Math.ceil(ViewState.getFinalTranslationY(firstView));
            if (isTargetTop(finalTranslationY)) {
                newTop = finalTranslationY;
            } else {
                newTop = (int) Math.ceil(firstView.getTranslationY());
            }
            top = Math.max(newTop, top);
            if (firstView.showingPulsing()) {
                bottom = Math.max(bottom, ExpandableViewState.getFinalActualHeight(firstView) + finalTranslationY);
                if (shiftBackgroundWithFirst) {
                    Rect rect = this.mBounds;
                    rect.left = (int) (rect.left + Math.max(firstView.getTranslation(), 0.0f));
                    Rect rect2 = this.mBounds;
                    rect2.right = (int) (rect2.right + Math.min(firstView.getTranslation(), 0.0f));
                }
            }
        }
        int top2 = Math.max(minTopPosition, top);
        ActivatableNotificationView lastView = getLastVisibleChild();
        if (lastView != null) {
            float finalTranslationY2 = ViewState.getFinalTranslationY(lastView);
            int finalHeight = ExpandableViewState.getFinalActualHeight(lastView);
            int finalBottom = (int) Math.floor((finalHeight + finalTranslationY2) - lastView.getClipBottomAmount());
            if (isTargetBottom(finalBottom)) {
                newBottom = finalBottom;
            } else {
                newBottom = (int) ((lastView.getTranslationY() + lastView.getActualHeight()) - lastView.getClipBottomAmount());
                minBottomPosition = (int) Math.min(lastView.getTranslationY() + lastView.getActualHeight(), minBottomPosition);
            }
            bottom = Math.max(bottom, Math.max(newBottom, minBottomPosition));
        }
        int bottom2 = Math.max(top2, bottom);
        Rect rect3 = this.mBounds;
        rect3.top = top2;
        rect3.bottom = bottom2;
        return bottom2;
    }
}
