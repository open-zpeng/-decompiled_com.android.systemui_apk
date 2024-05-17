package com.android.systemui.statusbar;

import android.R;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.CanvasProperty;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RecordingCanvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.RenderNodeAnimator;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import com.android.systemui.Interpolators;
/* loaded from: classes21.dex */
public class KeyguardAffordanceView extends ImageView {
    private static final long CIRCLE_APPEAR_DURATION = 80;
    private static final long CIRCLE_DISAPPEAR_MAX_DURATION = 200;
    public static final float MAX_ICON_SCALE_AMOUNT = 1.5f;
    public static final float MIN_ICON_SCALE_AMOUNT = 0.8f;
    private static final long NORMAL_ANIMATION_DURATION = 200;
    private ValueAnimator mAlphaAnimator;
    private AnimatorListenerAdapter mAlphaEndListener;
    private int mCenterX;
    private int mCenterY;
    private ValueAnimator mCircleAnimator;
    private int mCircleColor;
    private AnimatorListenerAdapter mCircleEndListener;
    private final Paint mCirclePaint;
    private float mCircleRadius;
    private float mCircleStartRadius;
    private float mCircleStartValue;
    private boolean mCircleWillBeHidden;
    private AnimatorListenerAdapter mClipEndListener;
    private final ArgbEvaluator mColorInterpolator;
    protected final int mDarkIconColor;
    private boolean mFinishing;
    private final FlingAnimationUtils mFlingAnimationUtils;
    private CanvasProperty<Float> mHwCenterX;
    private CanvasProperty<Float> mHwCenterY;
    private CanvasProperty<Paint> mHwCirclePaint;
    private CanvasProperty<Float> mHwCircleRadius;
    private float mImageScale;
    private boolean mIsLeft;
    private boolean mLaunchingAffordance;
    private float mMaxCircleSize;
    private final int mMinBackgroundRadius;
    protected final int mNormalColor;
    private Animator mPreviewClipper;
    private View mPreviewView;
    private float mRestingAlpha;
    private ValueAnimator mScaleAnimator;
    private AnimatorListenerAdapter mScaleEndListener;
    private boolean mShouldTint;
    private boolean mSupportHardware;
    private int[] mTempPoint;

    public KeyguardAffordanceView(Context context) {
        this(context, null);
    }

    public KeyguardAffordanceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyguardAffordanceView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public KeyguardAffordanceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mTempPoint = new int[2];
        this.mImageScale = 1.0f;
        this.mRestingAlpha = 1.0f;
        this.mShouldTint = true;
        this.mClipEndListener = new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.KeyguardAffordanceView.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                KeyguardAffordanceView.this.mPreviewClipper = null;
            }
        };
        this.mCircleEndListener = new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.KeyguardAffordanceView.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                KeyguardAffordanceView.this.mCircleAnimator = null;
            }
        };
        this.mScaleEndListener = new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.KeyguardAffordanceView.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                KeyguardAffordanceView.this.mScaleAnimator = null;
            }
        };
        this.mAlphaEndListener = new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.KeyguardAffordanceView.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                KeyguardAffordanceView.this.mAlphaAnimator = null;
            }
        };
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageView);
        this.mCirclePaint = new Paint();
        this.mCirclePaint.setAntiAlias(true);
        this.mCircleColor = -1;
        this.mCirclePaint.setColor(this.mCircleColor);
        this.mNormalColor = a.getColor(5, -1);
        this.mDarkIconColor = -16777216;
        this.mMinBackgroundRadius = this.mContext.getResources().getDimensionPixelSize(com.android.systemui.R.dimen.keyguard_affordance_min_background_radius);
        this.mColorInterpolator = new ArgbEvaluator();
        this.mFlingAnimationUtils = new FlingAnimationUtils(this.mContext, 0.3f);
        a.recycle();
    }

    public void setImageDrawable(Drawable drawable, boolean tint) {
        super.setImageDrawable(drawable);
        this.mShouldTint = tint;
        updateIconColor();
    }

    public boolean shouldTint() {
        return this.mShouldTint;
    }

    @Override // android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.mCenterX = getWidth() / 2;
        this.mCenterY = getHeight() / 2;
        this.mMaxCircleSize = getMaxCircleSize();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        this.mSupportHardware = canvas.isHardwareAccelerated();
        drawBackgroundCircle(canvas);
        canvas.save();
        float f = this.mImageScale;
        canvas.scale(f, f, getWidth() / 2, getHeight() / 2);
        super.onDraw(canvas);
        canvas.restore();
    }

    public void setPreviewView(View v) {
        if (this.mPreviewView == v) {
            return;
        }
        View oldPreviewView = this.mPreviewView;
        this.mPreviewView = v;
        View view = this.mPreviewView;
        if (view != null) {
            view.setVisibility(this.mLaunchingAffordance ? oldPreviewView.getVisibility() : 4);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateIconColor() {
        if (this.mShouldTint) {
            Drawable drawable = getDrawable().mutate();
            float alpha = this.mCircleRadius / this.mMinBackgroundRadius;
            int color = ((Integer) this.mColorInterpolator.evaluate(Math.min(1.0f, alpha), Integer.valueOf(this.mNormalColor), Integer.valueOf(this.mDarkIconColor))).intValue();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }

    private void drawBackgroundCircle(Canvas canvas) {
        CanvasProperty<Float> canvasProperty;
        if (this.mCircleRadius > 0.0f || this.mFinishing) {
            if (this.mFinishing && this.mSupportHardware && (canvasProperty = this.mHwCenterX) != null) {
                RecordingCanvas recordingCanvas = (RecordingCanvas) canvas;
                recordingCanvas.drawCircle(canvasProperty, this.mHwCenterY, this.mHwCircleRadius, this.mHwCirclePaint);
                return;
            }
            updateCircleColor();
            canvas.drawCircle(this.mCenterX, this.mCenterY, this.mCircleRadius, this.mCirclePaint);
        }
    }

    private void updateCircleColor() {
        float f = this.mCircleRadius;
        int i = this.mMinBackgroundRadius;
        float fraction = (Math.max(0.0f, Math.min(1.0f, (f - i) / (i * 0.5f))) * 0.5f) + 0.5f;
        View view = this.mPreviewView;
        if (view != null && view.getVisibility() == 0) {
            float finishingFraction = 1.0f - (Math.max(0.0f, this.mCircleRadius - this.mCircleStartRadius) / (this.mMaxCircleSize - this.mCircleStartRadius));
            fraction *= finishingFraction;
        }
        int color = Color.argb((int) (Color.alpha(this.mCircleColor) * fraction), Color.red(this.mCircleColor), Color.green(this.mCircleColor), Color.blue(this.mCircleColor));
        this.mCirclePaint.setColor(color);
    }

    public void finishAnimation(float velocity, final Runnable mAnimationEndRunnable) {
        Animator animatorToRadius;
        cancelAnimator(this.mCircleAnimator);
        cancelAnimator(this.mPreviewClipper);
        this.mFinishing = true;
        this.mCircleStartRadius = this.mCircleRadius;
        final float maxCircleSize = getMaxCircleSize();
        if (this.mSupportHardware) {
            initHwProperties();
            Animator animatorToRadius2 = getRtAnimatorToRadius(maxCircleSize);
            startRtAlphaFadeIn();
            animatorToRadius = animatorToRadius2;
        } else {
            animatorToRadius = getAnimatorToRadius(maxCircleSize);
        }
        this.mFlingAnimationUtils.applyDismissing(animatorToRadius, this.mCircleRadius, maxCircleSize, velocity, maxCircleSize);
        animatorToRadius.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.KeyguardAffordanceView.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                mAnimationEndRunnable.run();
                KeyguardAffordanceView.this.mFinishing = false;
                KeyguardAffordanceView.this.mCircleRadius = maxCircleSize;
                KeyguardAffordanceView.this.invalidate();
            }
        });
        animatorToRadius.start();
        setImageAlpha(0.0f, true);
        View view = this.mPreviewView;
        if (view != null) {
            view.setVisibility(0);
            this.mPreviewClipper = ViewAnimationUtils.createCircularReveal(this.mPreviewView, getLeft() + this.mCenterX, getTop() + this.mCenterY, this.mCircleRadius, maxCircleSize);
            this.mFlingAnimationUtils.applyDismissing(this.mPreviewClipper, this.mCircleRadius, maxCircleSize, velocity, maxCircleSize);
            this.mPreviewClipper.addListener(this.mClipEndListener);
            this.mPreviewClipper.start();
            if (this.mSupportHardware) {
                startRtCircleFadeOut(animatorToRadius.getDuration());
            }
        }
    }

    private void startRtAlphaFadeIn() {
        if (this.mCircleRadius == 0.0f && this.mPreviewView == null) {
            Paint modifiedPaint = new Paint(this.mCirclePaint);
            modifiedPaint.setColor(this.mCircleColor);
            modifiedPaint.setAlpha(0);
            this.mHwCirclePaint = CanvasProperty.createPaint(modifiedPaint);
            RenderNodeAnimator animator = new RenderNodeAnimator(this.mHwCirclePaint, 1, 255.0f);
            animator.setTarget(this);
            animator.setInterpolator(Interpolators.ALPHA_IN);
            animator.setDuration(250L);
            animator.start();
        }
    }

    public void instantFinishAnimation() {
        cancelAnimator(this.mPreviewClipper);
        View view = this.mPreviewView;
        if (view != null) {
            view.setClipBounds(null);
            this.mPreviewView.setVisibility(0);
        }
        this.mCircleRadius = getMaxCircleSize();
        setImageAlpha(0.0f, false);
        invalidate();
    }

    private void startRtCircleFadeOut(long duration) {
        RenderNodeAnimator animator = new RenderNodeAnimator(this.mHwCirclePaint, 1, 0.0f);
        animator.setDuration(duration);
        animator.setInterpolator(Interpolators.ALPHA_OUT);
        animator.setTarget(this);
        animator.start();
    }

    private Animator getRtAnimatorToRadius(float circleRadius) {
        RenderNodeAnimator animator = new RenderNodeAnimator(this.mHwCircleRadius, circleRadius);
        animator.setTarget(this);
        return animator;
    }

    private void initHwProperties() {
        this.mHwCenterX = CanvasProperty.createFloat(this.mCenterX);
        this.mHwCenterY = CanvasProperty.createFloat(this.mCenterY);
        this.mHwCirclePaint = CanvasProperty.createPaint(this.mCirclePaint);
        this.mHwCircleRadius = CanvasProperty.createFloat(this.mCircleRadius);
    }

    private float getMaxCircleSize() {
        getLocationInWindow(this.mTempPoint);
        float rootWidth = getRootView().getWidth();
        float width = this.mTempPoint[0] + this.mCenterX;
        float width2 = Math.max(rootWidth - width, width);
        float height = this.mTempPoint[1] + this.mCenterY;
        return (float) Math.hypot(width2, height);
    }

    public void setCircleRadius(float circleRadius) {
        setCircleRadius(circleRadius, false, false);
    }

    public void setCircleRadius(float circleRadius, boolean slowAnimation) {
        setCircleRadius(circleRadius, slowAnimation, false);
    }

    public void setCircleRadiusWithoutAnimation(float circleRadius) {
        cancelAnimator(this.mCircleAnimator);
        setCircleRadius(circleRadius, false, true);
    }

    private void setCircleRadius(float circleRadius, boolean slowAnimation, boolean noAnimation) {
        Interpolator interpolator;
        View view;
        boolean radiusHidden = (this.mCircleAnimator != null && this.mCircleWillBeHidden) || (this.mCircleAnimator == null && this.mCircleRadius == 0.0f);
        boolean nowHidden = circleRadius == 0.0f;
        boolean radiusNeedsAnimation = (radiusHidden == nowHidden || noAnimation) ? false : true;
        if (!radiusNeedsAnimation) {
            ValueAnimator valueAnimator = this.mCircleAnimator;
            if (valueAnimator == null) {
                this.mCircleRadius = circleRadius;
                updateIconColor();
                invalidate();
                if (nowHidden && (view = this.mPreviewView) != null) {
                    view.setVisibility(4);
                    return;
                }
                return;
            } else if (!this.mCircleWillBeHidden) {
                float diff = circleRadius - this.mMinBackgroundRadius;
                PropertyValuesHolder[] values = valueAnimator.getValues();
                values[0].setFloatValues(this.mCircleStartValue + diff, circleRadius);
                ValueAnimator valueAnimator2 = this.mCircleAnimator;
                valueAnimator2.setCurrentPlayTime(valueAnimator2.getCurrentPlayTime());
                return;
            } else {
                return;
            }
        }
        cancelAnimator(this.mCircleAnimator);
        cancelAnimator(this.mPreviewClipper);
        ValueAnimator animator = getAnimatorToRadius(circleRadius);
        if (circleRadius == 0.0f) {
            interpolator = Interpolators.FAST_OUT_LINEAR_IN;
        } else {
            interpolator = Interpolators.LINEAR_OUT_SLOW_IN;
        }
        animator.setInterpolator(interpolator);
        long duration = 250;
        if (!slowAnimation) {
            float durationFactor = Math.abs(this.mCircleRadius - circleRadius) / this.mMinBackgroundRadius;
            long duration2 = 80.0f * durationFactor;
            duration = Math.min(duration2, 200L);
        }
        animator.setDuration(duration);
        animator.start();
        View view2 = this.mPreviewView;
        if (view2 != null && view2.getVisibility() == 0) {
            this.mPreviewView.setVisibility(0);
            this.mPreviewClipper = ViewAnimationUtils.createCircularReveal(this.mPreviewView, getLeft() + this.mCenterX, getTop() + this.mCenterY, this.mCircleRadius, circleRadius);
            this.mPreviewClipper.setInterpolator(interpolator);
            this.mPreviewClipper.setDuration(duration);
            this.mPreviewClipper.addListener(this.mClipEndListener);
            this.mPreviewClipper.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.KeyguardAffordanceView.6
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    KeyguardAffordanceView.this.mPreviewView.setVisibility(4);
                }
            });
            this.mPreviewClipper.start();
        }
    }

    private ValueAnimator getAnimatorToRadius(float circleRadius) {
        ValueAnimator animator = ValueAnimator.ofFloat(this.mCircleRadius, circleRadius);
        this.mCircleAnimator = animator;
        this.mCircleStartValue = this.mCircleRadius;
        this.mCircleWillBeHidden = circleRadius == 0.0f;
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.KeyguardAffordanceView.7
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                KeyguardAffordanceView.this.mCircleRadius = ((Float) animation.getAnimatedValue()).floatValue();
                KeyguardAffordanceView.this.updateIconColor();
                KeyguardAffordanceView.this.invalidate();
            }
        });
        animator.addListener(this.mCircleEndListener);
        return animator;
    }

    private void cancelAnimator(Animator animator) {
        if (animator != null) {
            animator.cancel();
        }
    }

    public void setImageScale(float imageScale, boolean animate) {
        setImageScale(imageScale, animate, -1L, null);
    }

    public void setImageScale(float imageScale, boolean animate, long duration, Interpolator interpolator) {
        Interpolator interpolator2;
        cancelAnimator(this.mScaleAnimator);
        if (!animate) {
            this.mImageScale = imageScale;
            invalidate();
            return;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(this.mImageScale, imageScale);
        this.mScaleAnimator = animator;
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.KeyguardAffordanceView.8
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                KeyguardAffordanceView.this.mImageScale = ((Float) animation.getAnimatedValue()).floatValue();
                KeyguardAffordanceView.this.invalidate();
            }
        });
        animator.addListener(this.mScaleEndListener);
        if (interpolator == null) {
            if (imageScale == 0.0f) {
                interpolator2 = Interpolators.FAST_OUT_LINEAR_IN;
            } else {
                interpolator2 = Interpolators.LINEAR_OUT_SLOW_IN;
            }
            interpolator = interpolator2;
        }
        animator.setInterpolator(interpolator);
        if (duration == -1) {
            float durationFactor = Math.abs(this.mImageScale - imageScale) / 0.19999999f;
            duration = 200.0f * Math.min(1.0f, durationFactor);
        }
        animator.setDuration(duration);
        animator.start();
    }

    public float getRestingAlpha() {
        return this.mRestingAlpha;
    }

    public void setImageAlpha(float alpha, boolean animate) {
        setImageAlpha(alpha, animate, -1L, null, null);
    }

    public void setImageAlpha(float alpha, boolean animate, long duration, Interpolator interpolator, Runnable runnable) {
        Interpolator interpolator2;
        cancelAnimator(this.mAlphaAnimator);
        float alpha2 = this.mLaunchingAffordance ? 0.0f : alpha;
        int endAlpha = (int) (alpha2 * 255.0f);
        final Drawable background = getBackground();
        if (!animate) {
            if (background != null) {
                background.mutate().setAlpha(endAlpha);
            }
            setImageAlpha(endAlpha);
            return;
        }
        int currentAlpha = getImageAlpha();
        ValueAnimator animator = ValueAnimator.ofInt(currentAlpha, endAlpha);
        this.mAlphaAnimator = animator;
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.-$$Lambda$KeyguardAffordanceView$GLahQCZQtxFHfhh52YPyKQ2f5GE
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                KeyguardAffordanceView.this.lambda$setImageAlpha$0$KeyguardAffordanceView(background, valueAnimator);
            }
        });
        animator.addListener(this.mAlphaEndListener);
        if (interpolator == null) {
            if (alpha2 == 0.0f) {
                interpolator2 = Interpolators.FAST_OUT_LINEAR_IN;
            } else {
                interpolator2 = Interpolators.LINEAR_OUT_SLOW_IN;
            }
            interpolator = interpolator2;
        }
        animator.setInterpolator(interpolator);
        if (duration == -1) {
            float durationFactor = Math.abs(currentAlpha - endAlpha) / 255.0f;
            duration = 200.0f * Math.min(1.0f, durationFactor);
        }
        animator.setDuration(duration);
        if (runnable != null) {
            animator.addListener(getEndListener(runnable));
        }
        animator.start();
    }

    public /* synthetic */ void lambda$setImageAlpha$0$KeyguardAffordanceView(Drawable background, ValueAnimator animation) {
        int alpha1 = ((Integer) animation.getAnimatedValue()).intValue();
        if (background != null) {
            background.mutate().setAlpha(alpha1);
        }
        setImageAlpha(alpha1);
    }

    public boolean isAnimatingAlpha() {
        return this.mAlphaAnimator != null;
    }

    private Animator.AnimatorListener getEndListener(final Runnable runnable) {
        return new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.KeyguardAffordanceView.9
            boolean mCancelled;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                this.mCancelled = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (!this.mCancelled) {
                    runnable.run();
                }
            }
        };
    }

    public float getCircleRadius() {
        return this.mCircleRadius;
    }

    @Override // android.view.View
    public boolean performClick() {
        if (isClickable()) {
            return super.performClick();
        }
        return false;
    }

    public void setLaunchingAffordance(boolean launchingAffordance) {
        this.mLaunchingAffordance = launchingAffordance;
    }
}
