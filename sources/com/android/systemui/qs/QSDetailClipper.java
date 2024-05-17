package com.android.systemui.qs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;
import android.view.ViewAnimationUtils;
/* loaded from: classes21.dex */
public class QSDetailClipper {
    private Animator mAnimator;
    private final TransitionDrawable mBackground;
    private final View mDetail;
    private final Runnable mReverseBackground = new Runnable() { // from class: com.android.systemui.qs.QSDetailClipper.1
        @Override // java.lang.Runnable
        public void run() {
            if (QSDetailClipper.this.mAnimator != null) {
                QSDetailClipper.this.mBackground.reverseTransition((int) (QSDetailClipper.this.mAnimator.getDuration() * 0.35d));
            }
        }
    };
    private final AnimatorListenerAdapter mVisibleOnStart = new AnimatorListenerAdapter() { // from class: com.android.systemui.qs.QSDetailClipper.2
        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animation) {
            QSDetailClipper.this.mDetail.setVisibility(0);
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            QSDetailClipper.this.mAnimator = null;
        }
    };
    private final AnimatorListenerAdapter mGoneOnEnd = new AnimatorListenerAdapter() { // from class: com.android.systemui.qs.QSDetailClipper.3
        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            QSDetailClipper.this.mDetail.setVisibility(8);
            QSDetailClipper.this.mBackground.resetTransition();
            QSDetailClipper.this.mAnimator = null;
        }
    };

    public QSDetailClipper(View detail) {
        this.mDetail = detail;
        this.mBackground = (TransitionDrawable) detail.getBackground();
    }

    public void animateCircularClip(int x, int y, boolean in, Animator.AnimatorListener listener) {
        Animator animator = this.mAnimator;
        if (animator != null) {
            animator.cancel();
        }
        int w = this.mDetail.getWidth() - x;
        int h = this.mDetail.getHeight() - y;
        int innerR = 0;
        if (x < 0 || w < 0 || y < 0 || h < 0) {
            int innerR2 = Math.abs(x);
            innerR = Math.min(Math.min(Math.min(innerR2, Math.abs(y)), Math.abs(w)), Math.abs(h));
        }
        int r = (int) Math.ceil(Math.sqrt((x * x) + (y * y)));
        int r2 = (int) Math.max((int) Math.max((int) Math.max(r, Math.ceil(Math.sqrt((w * w) + (y * y)))), Math.ceil(Math.sqrt((w * w) + (h * h)))), Math.ceil(Math.sqrt((x * x) + (h * h))));
        if (in) {
            this.mAnimator = ViewAnimationUtils.createCircularReveal(this.mDetail, x, y, innerR, r2);
        } else {
            this.mAnimator = ViewAnimationUtils.createCircularReveal(this.mDetail, x, y, r2, innerR);
        }
        Animator animator2 = this.mAnimator;
        animator2.setDuration((long) (animator2.getDuration() * 1.5d));
        if (listener != null) {
            this.mAnimator.addListener(listener);
        }
        if (in) {
            this.mBackground.startTransition((int) (this.mAnimator.getDuration() * 0.6d));
            this.mAnimator.addListener(this.mVisibleOnStart);
        } else {
            this.mDetail.postDelayed(this.mReverseBackground, (long) (this.mAnimator.getDuration() * 0.65d));
            this.mAnimator.addListener(this.mGoneOnEnd);
        }
        this.mAnimator.start();
    }

    public void showBackground() {
        this.mBackground.showSecondLayer();
    }
}
