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
public class LandToMusicModeAnimation extends ToMusicModeAnimation {
    private static final String TAG = LandToMusicModeAnimation.class.getSimpleName();
    private ValueAnimator mAlphaAnimation;
    private final int mBigCardYPosition;
    private ValueAnimator mScaleAnimator;
    private View[] mSwitchViews;
    private View[] mToHideViews;
    private View[] mToShowViews;

    public LandToMusicModeAnimation(MusicCardHolder cardHolder) {
        super(cardHolder);
        this.mBigCardYPosition = this.mCardView.getContext().getResources().getDimensionPixelSize(R.dimen.card_big_music_margin_top);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.anim.musiccard.ToMusicModeAnimation
    public void doAnimation() {
        if (this.mScaleAnimator == null) {
            initScaleCardAnimator();
        }
        if (this.mAlphaAnimation == null) {
            initAlphaAnimator();
        }
        this.mScaleAnimator.start();
        this.mAlphaAnimation.start();
        startTranslationAnimator();
    }

    private void initScaleCardAnimator() {
        int smallCardHeight = this.mCardView.getContext().getResources().getDimensionPixelSize(R.dimen.card_height);
        int bigCardHeight = this.mCardView.getContext().getResources().getDimensionPixelSize(R.dimen.card_big_music_height);
        if (this.mScaleAnimator == null) {
            this.mScaleAnimator = ValueAnimator.ofInt(smallCardHeight, bigCardHeight).setDuration(this.ANIMATION_DURATION);
            this.mScaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.systemui.infoflow.message.anim.musiccard.LandToMusicModeAnimation.1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    ViewGroup.LayoutParams params = LandToMusicModeAnimation.this.mCardView.getLayoutParams();
                    params.height = ((Integer) animation.getAnimatedValue()).intValue();
                    LandToMusicModeAnimation.this.mCardView.setLayoutParams(params);
                }
            });
        }
    }

    private void initAlphaAnimator() {
        if (this.mAlphaAnimation == null) {
            this.mAlphaAnimation = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(this.ANIMATION_DURATION);
            this.mAlphaAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.systemui.infoflow.message.anim.musiccard.LandToMusicModeAnimation.2
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    View[] viewArr;
                    View[] viewArr2;
                    float alpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    for (View view : LandToMusicModeAnimation.this.mToShowViews) {
                        view.setAlpha(alpha);
                    }
                    for (View view2 : LandToMusicModeAnimation.this.mToHideViews) {
                        view2.setAlpha(1.0f - alpha);
                    }
                }
            });
            this.mAlphaAnimation.addListener(new AnimatorListenerAdapter() { // from class: com.xiaopeng.systemui.infoflow.message.anim.musiccard.LandToMusicModeAnimation.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    View[] viewArr;
                    super.onAnimationEnd(animation);
                    for (View view : LandToMusicModeAnimation.this.mSwitchViews) {
                        view.setClickable(true);
                    }
                    for (View view2 : LandToMusicModeAnimation.this.mToHideViews) {
                        if (view2 != null) {
                            view2.setVisibility(8);
                        }
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    Logger.d(LandToMusicModeAnimation.TAG, "alpha animation canceled");
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    View[] viewArr;
                    super.onAnimationStart(animation);
                    for (View view : LandToMusicModeAnimation.this.mSwitchViews) {
                        view.setClickable(false);
                    }
                    for (View view2 : LandToMusicModeAnimation.this.mToShowViews) {
                        if (view2 != null) {
                            view2.setVisibility(0);
                        }
                    }
                }
            });
        }
    }

    private void startTranslationAnimator() {
        int[] pointer = new int[2];
        this.mCardView.getLocationOnScreen(pointer);
        int currentY = pointer[1];
        if (currentY != this.mBigCardYPosition) {
            ObjectAnimator translationAnimation = ObjectAnimator.ofFloat(this.mCardView, "translationY", 0.0f, this.mBigCardYPosition - currentY);
            translationAnimation.setDuration(this.ANIMATION_DURATION);
            translationAnimation.addListener(new AnimatorListenerAdapter() { // from class: com.xiaopeng.systemui.infoflow.message.anim.musiccard.LandToMusicModeAnimation.4
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    LandToMusicModeAnimation.this.ensureBigCardPositionInSpecialCase();
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                }
            });
            translationAnimation.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ensureBigCardPositionInSpecialCase() {
        int[] pointer = new int[2];
        this.mCardView.getLocationOnScreen(pointer);
        int currentY = pointer[1];
        int i = this.mBigCardYPosition;
        if (currentY != i) {
            int delta = i - currentY;
            this.mCardView.setTranslationY(this.mCardView.getTranslationY() + delta);
        }
    }
}
