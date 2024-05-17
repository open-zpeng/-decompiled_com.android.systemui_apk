package com.android.systemui.statusbar.notification;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.View;
import android.widget.ImageView;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class NotificationDozeHelper {
    private static final int DOZE_ANIMATOR_TAG = R.id.doze_intensity_tag;
    private final ColorMatrix mGrayscaleColorMatrix = new ColorMatrix();

    public void fadeGrayscale(final ImageView target, final boolean dark, long delay) {
        startIntensityAnimation(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.notification.NotificationDozeHelper.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                NotificationDozeHelper.this.updateGrayscale(target, ((Float) animation.getAnimatedValue()).floatValue());
            }
        }, dark, delay, new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.notification.NotificationDozeHelper.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (!dark) {
                    target.setColorFilter((ColorFilter) null);
                }
            }
        });
    }

    public void updateGrayscale(ImageView target, boolean dark) {
        updateGrayscale(target, dark ? 1.0f : 0.0f);
    }

    public void updateGrayscale(ImageView target, float darkAmount) {
        if (darkAmount > 0.0f) {
            updateGrayscaleMatrix(darkAmount);
            target.setColorFilter(new ColorMatrixColorFilter(this.mGrayscaleColorMatrix));
            return;
        }
        target.setColorFilter((ColorFilter) null);
    }

    public void startIntensityAnimation(ValueAnimator.AnimatorUpdateListener updateListener, boolean dark, long delay, Animator.AnimatorListener listener) {
        float startIntensity = dark ? 0.0f : 1.0f;
        float endIntensity = dark ? 1.0f : 0.0f;
        ValueAnimator animator = ValueAnimator.ofFloat(startIntensity, endIntensity);
        animator.addUpdateListener(updateListener);
        animator.setDuration(500L);
        animator.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
        animator.setStartDelay(delay);
        if (listener != null) {
            animator.addListener(listener);
        }
        animator.start();
    }

    public void setDozing(final Consumer<Float> listener, boolean dozing, boolean animate, long delay, final View view) {
        if (animate) {
            startIntensityAnimation(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.notification.-$$Lambda$NotificationDozeHelper$VENFYNxPWcqtSl2MMr8F4aMPH78
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    listener.accept((Float) valueAnimator.getAnimatedValue());
                }
            }, dozing, delay, new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.notification.NotificationDozeHelper.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    view.setTag(NotificationDozeHelper.DOZE_ANIMATOR_TAG, null);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    view.setTag(NotificationDozeHelper.DOZE_ANIMATOR_TAG, animation);
                }
            });
            return;
        }
        Animator animator = (Animator) view.getTag(DOZE_ANIMATOR_TAG);
        if (animator != null) {
            animator.cancel();
        }
        listener.accept(Float.valueOf(dozing ? 1.0f : 0.0f));
    }

    public void updateGrayscaleMatrix(float intensity) {
        this.mGrayscaleColorMatrix.setSaturation(1.0f - intensity);
    }

    public ColorMatrix getGrayscaleColorMatrix() {
        return this.mGrayscaleColorMatrix;
    }
}
