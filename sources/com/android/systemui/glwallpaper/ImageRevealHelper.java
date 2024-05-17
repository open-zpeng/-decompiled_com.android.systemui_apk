package com.android.systemui.glwallpaper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.util.Log;
import com.android.systemui.Interpolators;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes21.dex */
public class ImageRevealHelper {
    private static final boolean DEBUG = true;
    private static final float MAX_REVEAL = 0.0f;
    private static final float MIN_REVEAL = 1.0f;
    private static final String TAG = ImageRevealHelper.class.getSimpleName();
    private final RevealStateListener mRevealListener;
    private float mReveal = 0.0f;
    private boolean mAwake = false;
    private final ValueAnimator mAnimator = ValueAnimator.ofFloat(new float[0]);

    /* loaded from: classes21.dex */
    public interface RevealStateListener {
        void onRevealEnd();

        void onRevealStart(boolean z);

        void onRevealStateChanged();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ImageRevealHelper(RevealStateListener listener) {
        this.mRevealListener = listener;
        this.mAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.glwallpaper.-$$Lambda$ImageRevealHelper$F24215Snv58_ZInLQsaNs5JLH9M
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ImageRevealHelper.this.lambda$new$0$ImageRevealHelper(valueAnimator);
            }
        });
        this.mAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.glwallpaper.ImageRevealHelper.1
            private boolean mIsCanceled;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                this.mIsCanceled = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (ImageRevealHelper.this.mRevealListener != null) {
                    String str = ImageRevealHelper.TAG;
                    Log.d(str, "transition end, cancel=" + this.mIsCanceled + ", reveal=" + ImageRevealHelper.this.mReveal);
                    if (!this.mIsCanceled) {
                        ImageRevealHelper.this.mRevealListener.onRevealEnd();
                    }
                }
                this.mIsCanceled = false;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                if (ImageRevealHelper.this.mRevealListener != null) {
                    Log.d(ImageRevealHelper.TAG, "transition start");
                    ImageRevealHelper.this.mRevealListener.onRevealStart(true);
                }
            }
        });
    }

    public /* synthetic */ void lambda$new$0$ImageRevealHelper(ValueAnimator animator) {
        this.mReveal = ((Float) animator.getAnimatedValue()).floatValue();
        RevealStateListener revealStateListener = this.mRevealListener;
        if (revealStateListener != null) {
            revealStateListener.onRevealStateChanged();
        }
    }

    public float getReveal() {
        return this.mReveal;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateAwake(boolean awake, long duration) {
        String str = TAG;
        Log.d(str, "updateAwake: awake=" + awake + ", duration=" + duration);
        this.mAnimator.cancel();
        this.mAwake = awake;
        if (duration == 0) {
            this.mReveal = this.mAwake ? 0.0f : 1.0f;
            this.mRevealListener.onRevealStart(false);
            this.mRevealListener.onRevealStateChanged();
            this.mRevealListener.onRevealEnd();
            return;
        }
        this.mAnimator.setDuration(duration);
        ValueAnimator valueAnimator = this.mAnimator;
        float[] fArr = new float[2];
        fArr[0] = this.mReveal;
        fArr[1] = this.mAwake ? 0.0f : 1.0f;
        valueAnimator.setFloatValues(fArr);
        this.mAnimator.start();
    }
}
