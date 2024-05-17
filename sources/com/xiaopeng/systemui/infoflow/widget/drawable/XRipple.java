package com.xiaopeng.systemui.infoflow.widget.drawable;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import androidx.annotation.ColorInt;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;
/* loaded from: classes24.dex */
public class XRipple {
    private static final long ANIMATION_TIME = 400;
    private static final String TAG = XRipple.class.getSimpleName();
    private AnimTransformListener mAnimTransformListener;
    private ValueAnimator mAnimationPress;
    private ValueAnimator mAnimationUp;
    private int mBackgroundColor;
    private float mClearDistance;
    private float mCurrentDistance;
    private float mDownX;
    private float mDownY;
    private boolean mIsAnimating;
    private boolean mIsPressAnimating;
    private boolean mIsTouched;
    private boolean mIsUpAnimating;
    private float mMaxPressRadius;
    private boolean mNeedUpAnim;
    private Paint mPaint;
    private float mPressRadius;
    private int mRippleAlpha;
    private int mRippleColor;
    private Path mRipplePath;
    private float mRippleRadius;
    private RectF mRippleRectF;
    private boolean mSupportScale;
    private View mView;

    /* loaded from: classes24.dex */
    public interface AnimTransformListener {
        void onDownTransformation(float f, Transformation transformation);

        void onUpTransformation(float f, Transformation transformation);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public XRipple() {
        init();
    }

    public XRipple(View view) {
        this.mView = view;
        init();
    }

    private void init() {
        this.mRipplePath = new Path();
        this.mRippleColor = SystemUIApplication.getContext().getColor(R.color.x_ripple_default_color);
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(this.mRippleColor);
        this.mRippleAlpha = this.mPaint.getAlpha();
        initAnimation();
    }

    public void setView(View view) {
        this.mView = view;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRippleColor(@ColorInt int rippleColor) {
        this.mRippleColor = rippleColor;
        this.mPaint.setColor(rippleColor);
        this.mRippleAlpha = this.mPaint.getAlpha();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRippleBackgroundColor(@ColorInt int backColor) {
        this.mBackgroundColor = backColor;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRippleRect(RectF rippleRectF) {
        this.mRippleRectF = rippleRectF;
        if (this.mRippleRectF.width() > this.mRippleRectF.height()) {
            this.mClearDistance = this.mRippleRectF.height() / 2.0f;
        } else {
            this.mClearDistance = this.mRippleRectF.width() / 2.0f;
        }
        resetPath();
    }

    public void setSupportScale(boolean supportScale) {
        this.mSupportScale = false;
    }

    public void setRippleRadius(float rippleRadius) {
        this.mRippleRadius = rippleRadius;
        resetPath();
    }

    private void resetPath() {
        if (this.mRippleRectF != null) {
            this.mRipplePath.reset();
            Path path = this.mRipplePath;
            RectF rectF = this.mRippleRectF;
            float f = this.mRippleRadius;
            path.addRoundRect(rectF, f, f, Path.Direction.CW);
        }
    }

    private void initAnimation() {
        Interpolator interpolator = new AccelerateDecelerateInterpolator();
        this.mAnimationPress = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(ANIMATION_TIME);
        this.mAnimationPress.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.systemui.infoflow.widget.drawable.XRipple.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                float interpolatedTime = ((Float) animation.getAnimatedValue()).floatValue();
                XRipple xRipple = XRipple.this;
                xRipple.mPressRadius = xRipple.mMaxPressRadius * interpolatedTime;
                if (XRipple.this.mAnimTransformListener != null) {
                    XRipple.this.mAnimTransformListener.onDownTransformation(interpolatedTime, null);
                }
                if (XRipple.this.mView != null) {
                    if (XRipple.this.mSupportScale) {
                        float scale = 1.0f - (0.1f * interpolatedTime);
                        XRipple.this.mView.setScaleX(scale);
                        XRipple.this.mView.setScaleY(scale);
                    }
                    XRipple.this.mView.invalidate();
                    return;
                }
                Log.d(XRipple.TAG, "view is null");
            }
        });
        this.mAnimationPress.addListener(new Animator.AnimatorListener() { // from class: com.xiaopeng.systemui.infoflow.widget.drawable.XRipple.2
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                XRipple.this.mPaint.setAlpha(XRipple.this.mRippleAlpha);
                XRipple.this.mIsPressAnimating = true;
                XRipple.this.mIsAnimating = true;
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (XRipple.this.mIsPressAnimating) {
                    XRipple.this.mIsPressAnimating = false;
                    if (XRipple.this.mNeedUpAnim) {
                        XRipple.this.mNeedUpAnim = false;
                        XRipple.this.mAnimationUp.start();
                        XRipple.this.mIsUpAnimating = true;
                    }
                }
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animation) {
            }
        });
        this.mAnimationPress.setInterpolator(interpolator);
        this.mAnimationPress.setDuration(ANIMATION_TIME);
        this.mAnimationUp = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(ANIMATION_TIME);
        this.mAnimationUp.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.systemui.infoflow.widget.drawable.XRipple.3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                float interpolatedTime = ((Float) animation.getAnimatedValue()).floatValue();
                XRipple xRipple = XRipple.this;
                xRipple.mCurrentDistance = xRipple.mClearDistance * (1.0f - interpolatedTime);
                if (XRipple.this.mAnimTransformListener != null) {
                    XRipple.this.mAnimTransformListener.onUpTransformation(interpolatedTime, null);
                }
                XRipple.this.mPaint.setAlpha((int) (XRipple.this.mRippleAlpha * (1.0f - interpolatedTime)));
                if (XRipple.this.mView != null) {
                    if (XRipple.this.mSupportScale) {
                        float scale = (0.1f * interpolatedTime) + 0.9f;
                        XRipple.this.mView.setScaleX(scale);
                        XRipple.this.mView.setScaleY(scale);
                    }
                    XRipple.this.mView.invalidate();
                }
            }
        });
        this.mAnimationUp.addListener(new Animator.AnimatorListener() { // from class: com.xiaopeng.systemui.infoflow.widget.drawable.XRipple.4
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                XRipple.this.mIsUpAnimating = true;
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                XRipple.this.mPaint.setAlpha(XRipple.this.mRippleAlpha);
                XRipple.this.mIsUpAnimating = false;
                XRipple.this.mIsAnimating = false;
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animation) {
            }
        });
        this.mAnimationUp.setInterpolator(interpolator);
        this.mAnimationUp.setDuration(ANIMATION_TIME);
    }

    public void setPressInterpolator(Interpolator interpolator) {
        this.mAnimationPress.setInterpolator(interpolator);
    }

    public void setUpInterpolator(Interpolator interpolator) {
        this.mAnimationUp.setInterpolator(interpolator);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPressDuration(long duration) {
        this.mAnimationPress.setDuration(duration);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setUpDuration(long duration) {
        this.mAnimationUp.setDuration(duration);
    }

    public void drawRipple(Canvas canvas) {
        View view = this.mView;
        if (view == null || view.getVisibility() == 8) {
            return;
        }
        if (this.mRippleRectF == null) {
            setRippleRect(new RectF(0.0f, 0.0f, canvas.getWidth(), canvas.getHeight()));
        }
        if (this.mIsPressAnimating && this.mIsAnimating) {
            drawRippleBackground(canvas);
            int count = canvas.saveLayer(0.0f, 0.0f, canvas.getWidth(), canvas.getHeight(), null);
            canvas.clipPath(this.mRipplePath);
            canvas.drawCircle(this.mDownX, this.mDownY, this.mPressRadius, this.mPaint);
            canvas.restoreToCount(count);
        } else if (this.mIsUpAnimating && this.mIsAnimating) {
            drawRippleBackground(canvas);
            int count2 = canvas.saveLayer(0.0f, 0.0f, canvas.getWidth(), canvas.getHeight(), null);
            RectF rectF = this.mRippleRectF;
            float f = this.mRippleRadius;
            canvas.drawRoundRect(rectF, f, f, this.mPaint);
            this.mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            float f2 = this.mCurrentDistance;
            float radius = (1.0f - (f2 / this.mClearDistance)) * this.mRippleRadius;
            canvas.drawRoundRect(f2 + this.mRippleRectF.left, this.mCurrentDistance + this.mRippleRectF.top, (this.mRippleRectF.width() - this.mCurrentDistance) + this.mRippleRectF.left, (this.mRippleRectF.height() - this.mCurrentDistance) + this.mRippleRectF.top, radius, radius, this.mPaint);
            this.mPaint.setXfermode(null);
            canvas.restoreToCount(count2);
        } else if (this.mIsTouched && this.mIsAnimating) {
            drawRippleBackground(canvas);
            RectF rectF2 = this.mRippleRectF;
            float f3 = this.mRippleRadius;
            canvas.drawRoundRect(rectF2, f3, f3, this.mPaint);
        }
    }

    private void drawRippleBackground(Canvas canvas) {
        int i = this.mBackgroundColor;
        if (i != 0) {
            this.mPaint.setColor(i);
            RectF rectF = this.mRippleRectF;
            float f = this.mRippleRadius;
            canvas.drawRoundRect(rectF, f, f, this.mPaint);
            this.mPaint.setColor(this.mRippleColor);
        }
    }

    public void onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == 0) {
            pressDown(event.getX(), event.getY());
        } else if (action == 1 || action == 3) {
            pressUp();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void pressDown(float downX, float downY) {
        View view = this.mView;
        if (view != null && view.getVisibility() == 0) {
            this.mDownX = downX;
            this.mDownY = downY;
            this.mNeedUpAnim = false;
            this.mIsAnimating = false;
            float x = this.mDownX - this.mRippleRectF.left;
            float y = this.mDownY - this.mRippleRectF.top;
            if (x < this.mRippleRectF.width() / 2.0f) {
                x = this.mRippleRectF.width() - x;
            }
            if (y < this.mRippleRectF.height() / 2.0f) {
                y = this.mRippleRectF.height() - y;
            }
            this.mMaxPressRadius = (float) Math.sqrt((x * x) + (y * y));
            if (this.mIsUpAnimating) {
                this.mIsUpAnimating = false;
                this.mAnimationUp.cancel();
            }
            this.mIsPressAnimating = true;
            if (this.mAnimationPress.isRunning()) {
                this.mAnimationPress.cancel();
            }
            this.mAnimationPress.start();
            this.mIsTouched = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void pressUp() {
        View view = this.mView;
        if (view != null && view.getVisibility() == 0) {
            this.mIsTouched = false;
            if (this.mIsPressAnimating) {
                this.mNeedUpAnim = true;
                return;
            }
            this.mAnimationUp.start();
            this.mIsUpAnimating = true;
        }
    }

    public void setVisible(boolean visible) {
        View view;
        if (!visible) {
            this.mAnimationPress.cancel();
            this.mAnimationUp.cancel();
            this.mIsPressAnimating = false;
            this.mIsUpAnimating = false;
            this.mIsAnimating = false;
            this.mPressRadius = 0.0f;
            this.mCurrentDistance = 0.0f;
        } else if (this.mSupportScale && (view = this.mView) != null) {
            view.setScaleX(1.0f);
            this.mView.setScaleY(1.0f);
        }
    }

    public void setAnimTransformListener(AnimTransformListener animTransformListener) {
        this.mAnimTransformListener = animTransformListener;
    }
}
