package com.android.systemui.assist;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class AssistDisclosure {
    private final Context mContext;
    private final Handler mHandler;
    private Runnable mShowRunnable = new Runnable() { // from class: com.android.systemui.assist.AssistDisclosure.1
        @Override // java.lang.Runnable
        public void run() {
            AssistDisclosure.this.show();
        }
    };
    private AssistDisclosureView mView;
    private boolean mViewAdded;
    private final WindowManager mWm;

    public AssistDisclosure(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
        this.mWm = (WindowManager) this.mContext.getSystemService(WindowManager.class);
    }

    public void postShow() {
        this.mHandler.removeCallbacks(this.mShowRunnable);
        this.mHandler.post(this.mShowRunnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void show() {
        if (this.mView == null) {
            this.mView = new AssistDisclosureView(this.mContext);
        }
        if (!this.mViewAdded) {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams(2015, 525576, -3);
            lp.setTitle("AssistDisclosure");
            this.mWm.addView(this.mView, lp);
            this.mViewAdded = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hide() {
        if (this.mViewAdded) {
            this.mWm.removeView(this.mView);
            this.mViewAdded = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class AssistDisclosureView extends View implements ValueAnimator.AnimatorUpdateListener {
        static final int ALPHA_IN_ANIMATION_DURATION = 400;
        static final int ALPHA_OUT_ANIMATION_DURATION = 300;
        static final int FULL_ALPHA = 222;
        private int mAlpha;
        private final ValueAnimator mAlphaInAnimator;
        private final ValueAnimator mAlphaOutAnimator;
        private final AnimatorSet mAnimator;
        private final Paint mPaint;
        private final Paint mShadowPaint;
        private float mShadowThickness;
        private float mThickness;

        public AssistDisclosureView(Context context) {
            super(context);
            this.mPaint = new Paint();
            this.mShadowPaint = new Paint();
            this.mAlpha = 0;
            this.mAlphaInAnimator = ValueAnimator.ofInt(0, FULL_ALPHA).setDuration(400L);
            this.mAlphaInAnimator.addUpdateListener(this);
            this.mAlphaInAnimator.setInterpolator(Interpolators.CUSTOM_40_40);
            this.mAlphaOutAnimator = ValueAnimator.ofInt(FULL_ALPHA, 0).setDuration(300L);
            this.mAlphaOutAnimator.addUpdateListener(this);
            this.mAlphaOutAnimator.setInterpolator(Interpolators.CUSTOM_40_40);
            this.mAnimator = new AnimatorSet();
            this.mAnimator.play(this.mAlphaInAnimator).before(this.mAlphaOutAnimator);
            this.mAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.assist.AssistDisclosure.AssistDisclosureView.1
                boolean mCancelled;

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    this.mCancelled = false;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    this.mCancelled = true;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (!this.mCancelled) {
                        AssistDisclosure.this.hide();
                    }
                }
            });
            PorterDuffXfermode srcMode = new PorterDuffXfermode(PorterDuff.Mode.SRC);
            this.mPaint.setColor(-1);
            this.mPaint.setXfermode(srcMode);
            this.mShadowPaint.setColor(-12303292);
            this.mShadowPaint.setXfermode(srcMode);
            this.mThickness = getResources().getDimension(R.dimen.assist_disclosure_thickness);
            this.mShadowThickness = getResources().getDimension(R.dimen.assist_disclosure_shadow_thickness);
        }

        @Override // android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            startAnimation();
            sendAccessibilityEvent(16777216);
        }

        @Override // android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.mAnimator.cancel();
            this.mAlpha = 0;
        }

        private void startAnimation() {
            this.mAnimator.cancel();
            this.mAnimator.start();
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            this.mPaint.setAlpha(this.mAlpha);
            this.mShadowPaint.setAlpha(this.mAlpha / 4);
            drawGeometry(canvas, this.mShadowPaint, this.mShadowThickness);
            drawGeometry(canvas, this.mPaint, 0.0f);
        }

        private void drawGeometry(Canvas canvas, Paint paint, float padding) {
            int width = getWidth();
            int height = getHeight();
            float thickness = this.mThickness;
            drawBeam(canvas, 0.0f, height - thickness, width, height, paint, padding);
            drawBeam(canvas, 0.0f, 0.0f, thickness, height - thickness, paint, padding);
            drawBeam(canvas, width - thickness, 0.0f, width, height - thickness, paint, padding);
            drawBeam(canvas, thickness, 0.0f, width - thickness, thickness, paint, padding);
        }

        private void drawBeam(Canvas canvas, float left, float top, float right, float bottom, Paint paint, float padding) {
            canvas.drawRect(left - padding, top - padding, right + padding, bottom + padding, paint);
        }

        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator animation) {
            ValueAnimator valueAnimator = this.mAlphaOutAnimator;
            if (animation == valueAnimator) {
                this.mAlpha = ((Integer) valueAnimator.getAnimatedValue()).intValue();
            } else {
                ValueAnimator valueAnimator2 = this.mAlphaInAnimator;
                if (animation == valueAnimator2) {
                    this.mAlpha = ((Integer) valueAnimator2.getAnimatedValue()).intValue();
                }
            }
            invalidate();
        }
    }
}
