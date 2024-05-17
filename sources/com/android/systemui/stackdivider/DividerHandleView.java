package com.android.systemui.stackdivider;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.animation.Interpolator;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class DividerHandleView extends View {
    private AnimatorSet mAnimator;
    private final int mCircleDiameter;
    private int mCurrentHeight;
    private int mCurrentWidth;
    private final int mHeight;
    private final Paint mPaint;
    private boolean mTouching;
    private final int mWidth;
    private static final Property<DividerHandleView, Integer> WIDTH_PROPERTY = new Property<DividerHandleView, Integer>(Integer.class, "width") { // from class: com.android.systemui.stackdivider.DividerHandleView.1
        @Override // android.util.Property
        public Integer get(DividerHandleView object) {
            return Integer.valueOf(object.mCurrentWidth);
        }

        @Override // android.util.Property
        public void set(DividerHandleView object, Integer value) {
            object.mCurrentWidth = value.intValue();
            object.invalidate();
        }
    };
    private static final Property<DividerHandleView, Integer> HEIGHT_PROPERTY = new Property<DividerHandleView, Integer>(Integer.class, "height") { // from class: com.android.systemui.stackdivider.DividerHandleView.2
        @Override // android.util.Property
        public Integer get(DividerHandleView object) {
            return Integer.valueOf(object.mCurrentHeight);
        }

        @Override // android.util.Property
        public void set(DividerHandleView object, Integer value) {
            object.mCurrentHeight = value.intValue();
            object.invalidate();
        }
    };

    public DividerHandleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mPaint = new Paint();
        this.mPaint.setColor(getResources().getColor(R.color.docked_divider_handle, null));
        this.mPaint.setAntiAlias(true);
        this.mWidth = getResources().getDimensionPixelSize(R.dimen.docked_divider_handle_width);
        this.mHeight = getResources().getDimensionPixelSize(R.dimen.docked_divider_handle_height);
        int i = this.mWidth;
        this.mCurrentWidth = i;
        int i2 = this.mHeight;
        this.mCurrentHeight = i2;
        this.mCircleDiameter = (i + i2) / 3;
    }

    public void setTouching(boolean touching, boolean animate) {
        if (touching == this.mTouching) {
            return;
        }
        AnimatorSet animatorSet = this.mAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.mAnimator = null;
        }
        if (!animate) {
            if (touching) {
                int i = this.mCircleDiameter;
                this.mCurrentWidth = i;
                this.mCurrentHeight = i;
            } else {
                this.mCurrentWidth = this.mWidth;
                this.mCurrentHeight = this.mHeight;
            }
            invalidate();
        } else {
            animateToTarget(touching ? this.mCircleDiameter : this.mWidth, touching ? this.mCircleDiameter : this.mHeight, touching);
        }
        this.mTouching = touching;
    }

    private void animateToTarget(int targetWidth, int targetHeight, boolean touching) {
        long j;
        Interpolator interpolator;
        ObjectAnimator widthAnimator = ObjectAnimator.ofInt(this, WIDTH_PROPERTY, this.mCurrentWidth, targetWidth);
        ObjectAnimator heightAnimator = ObjectAnimator.ofInt(this, HEIGHT_PROPERTY, this.mCurrentHeight, targetHeight);
        this.mAnimator = new AnimatorSet();
        this.mAnimator.playTogether(widthAnimator, heightAnimator);
        AnimatorSet animatorSet = this.mAnimator;
        if (touching) {
            j = 150;
        } else {
            j = 200;
        }
        animatorSet.setDuration(j);
        AnimatorSet animatorSet2 = this.mAnimator;
        if (touching) {
            interpolator = Interpolators.TOUCH_RESPONSE;
        } else {
            interpolator = Interpolators.FAST_OUT_SLOW_IN;
        }
        animatorSet2.setInterpolator(interpolator);
        this.mAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.stackdivider.DividerHandleView.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                DividerHandleView.this.mAnimator = null;
            }
        });
        this.mAnimator.start();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int left = (getWidth() / 2) - (this.mCurrentWidth / 2);
        int i = this.mCurrentHeight;
        int top = (getHeight() / 2) - (i / 2);
        int radius = Math.min(this.mCurrentWidth, i) / 2;
        canvas.drawRoundRect(left, top, this.mCurrentWidth + left, this.mCurrentHeight + top, radius, radius, this.mPaint);
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }
}
