package com.xiaopeng.systemui.infoflow.message.anim.musiccard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.MusicCardHolder;
import com.xiaopeng.systemui.infoflow.util.Logger;
/* loaded from: classes24.dex */
public class LandToNormaModeAnimation extends ToNormalModeAnimation {
    private static final String TAG = LandToNormaModeAnimation.class.getSimpleName();
    private ValueAnimator mAlphaAnimation;
    private ValueAnimator mReduceAnimator;
    private View[] mSwitchViews;
    private View[] mToHideViews;
    private View[] mToShowViews;

    public LandToNormaModeAnimation(MusicCardHolder cardHolder) {
        super(cardHolder);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.anim.musiccard.ToNormalModeAnimation
    public void doAnimation() {
        if (this.mReduceAnimator == null) {
            initReduceAnimator();
        }
        if (this.mAlphaAnimation == null) {
            initAlphaAnimator();
        }
        this.mReduceAnimator.start();
        this.mAlphaAnimation.start();
        startTranslationAnimator();
    }

    private void initReduceAnimator() {
        int smallCardHeight = this.mCardView.getContext().getResources().getDimensionPixelSize(R.dimen.card_height);
        int bigCardHeight = this.mCardView.getContext().getResources().getDimensionPixelSize(R.dimen.card_big_music_height);
        if (this.mReduceAnimator == null) {
            this.mReduceAnimator = ValueAnimator.ofInt(bigCardHeight, smallCardHeight).setDuration(this.ANIMATION_DURATION);
            this.mReduceAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.systemui.infoflow.message.anim.musiccard.LandToNormaModeAnimation.1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    ViewGroup.LayoutParams params = LandToNormaModeAnimation.this.mCardView.getLayoutParams();
                    params.height = ((Integer) animation.getAnimatedValue()).intValue();
                    LandToNormaModeAnimation.this.mCardView.setLayoutParams(params);
                }
            });
            this.mReduceAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.xiaopeng.systemui.infoflow.message.anim.musiccard.LandToNormaModeAnimation.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                }
            });
        }
    }

    private void initAlphaAnimator() {
        if (this.mAlphaAnimation == null) {
            this.mAlphaAnimation = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(this.ANIMATION_DURATION);
            this.mAlphaAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.systemui.infoflow.message.anim.musiccard.LandToNormaModeAnimation.3
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    View[] viewArr;
                    View[] viewArr2;
                    float alpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    for (View view : LandToNormaModeAnimation.this.mToShowViews) {
                        view.setAlpha(alpha);
                    }
                    for (View view2 : LandToNormaModeAnimation.this.mToHideViews) {
                        view2.setAlpha(1.0f - alpha);
                    }
                }
            });
            this.mAlphaAnimation.addListener(new AnimatorListenerAdapter() { // from class: com.xiaopeng.systemui.infoflow.message.anim.musiccard.LandToNormaModeAnimation.4
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    View[] viewArr;
                    super.onAnimationEnd(animation);
                    for (View view : LandToNormaModeAnimation.this.mSwitchViews) {
                        view.setClickable(true);
                    }
                    for (View view2 : LandToNormaModeAnimation.this.mToHideViews) {
                        if (view2 != null) {
                            view2.setVisibility(8);
                        }
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    Logger.d(LandToNormaModeAnimation.TAG, "alpha animation cancelled");
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    View[] viewArr;
                    super.onAnimationStart(animation);
                    for (View view : LandToNormaModeAnimation.this.mSwitchViews) {
                        view.setClickable(false);
                    }
                    for (View view2 : LandToNormaModeAnimation.this.mToShowViews) {
                        if (view2 != null) {
                            view2.setVisibility(0);
                        }
                    }
                }
            });
        }
    }

    private void startTranslationAnimator() {
        int translationY = (int) this.mCardView.getTranslationY();
        if (translationY != 0) {
            ObjectAnimator.ofFloat(this.mCardView, "translationY", translationY, 0.0f).setDuration(500L).start();
        }
    }
}
