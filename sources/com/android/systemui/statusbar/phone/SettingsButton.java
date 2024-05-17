package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import com.android.keyguard.AlphaOptimizedImageButton;
import com.android.systemui.Interpolators;
/* loaded from: classes21.dex */
public class SettingsButton extends AlphaOptimizedImageButton {
    private static final long ACCEL_LENGTH = 750;
    private static final long FULL_SPEED_LENGTH = 375;
    private static final long LONG_PRESS_LENGTH = 1000;
    private static final long RUN_DURATION = 350;
    private static final boolean TUNER_ENABLE_AVAILABLE = false;
    private ObjectAnimator mAnimator;
    private final Runnable mLongPressCallback;
    private float mSlop;
    private boolean mUpToSpeed;

    public SettingsButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mLongPressCallback = new Runnable() { // from class: com.android.systemui.statusbar.phone.SettingsButton.3
            @Override // java.lang.Runnable
            public void run() {
                SettingsButton.this.startAccelSpin();
            }
        };
        this.mSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public boolean isAnimating() {
        ObjectAnimator objectAnimator = this.mAnimator;
        return objectAnimator != null && objectAnimator.isRunning();
    }

    public boolean isTunerClick() {
        return this.mUpToSpeed;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        int actionMasked = event.getActionMasked();
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    float x = event.getX();
                    float y = event.getY();
                    float f = this.mSlop;
                    if (x < (-f) || y < (-f) || x > getWidth() + this.mSlop || y > getHeight() + this.mSlop) {
                        cancelLongClick();
                    }
                } else if (actionMasked == 3) {
                    cancelLongClick();
                }
            } else if (this.mUpToSpeed) {
                startExitAnimation();
            } else {
                cancelLongClick();
            }
        }
        return super.onTouchEvent(event);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelLongClick() {
        cancelAnimation();
        this.mUpToSpeed = false;
        removeCallbacks(this.mLongPressCallback);
    }

    private void cancelAnimation() {
        ObjectAnimator objectAnimator = this.mAnimator;
        if (objectAnimator != null) {
            objectAnimator.removeAllListeners();
            this.mAnimator.cancel();
            this.mAnimator = null;
        }
    }

    private void startExitAnimation() {
        animate().translationX(((View) getParent().getParent()).getWidth() - getX()).alpha(0.0f).setDuration(350L).setInterpolator(AnimationUtils.loadInterpolator(this.mContext, 17563650)).setListener(new Animator.AnimatorListener() { // from class: com.android.systemui.statusbar.phone.SettingsButton.1
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animation) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                SettingsButton.this.setAlpha(1.0f);
                SettingsButton.this.setTranslationX(0.0f);
                SettingsButton.this.cancelLongClick();
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
            }
        }).start();
    }

    protected void startAccelSpin() {
        cancelAnimation();
        this.mAnimator = ObjectAnimator.ofFloat(this, View.ROTATION, 0.0f, 360.0f);
        this.mAnimator.setInterpolator(AnimationUtils.loadInterpolator(this.mContext, 17563648));
        this.mAnimator.setDuration(ACCEL_LENGTH);
        this.mAnimator.addListener(new Animator.AnimatorListener() { // from class: com.android.systemui.statusbar.phone.SettingsButton.2
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animation) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                SettingsButton.this.startContinuousSpin();
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
            }
        });
        this.mAnimator.start();
    }

    protected void startContinuousSpin() {
        cancelAnimation();
        performHapticFeedback(0);
        this.mUpToSpeed = true;
        this.mAnimator = ObjectAnimator.ofFloat(this, View.ROTATION, 0.0f, 360.0f);
        this.mAnimator.setInterpolator(Interpolators.LINEAR);
        this.mAnimator.setDuration(FULL_SPEED_LENGTH);
        this.mAnimator.setRepeatCount(-1);
        this.mAnimator.start();
    }
}
