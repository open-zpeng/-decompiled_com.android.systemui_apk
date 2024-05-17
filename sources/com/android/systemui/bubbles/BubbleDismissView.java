package com.android.systemui.bubbles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class BubbleDismissView extends FrameLayout {
    private static final int DISMISS_TARGET_ANIMATION_BASE_DURATION = 150;
    private static final float SCALE_FOR_DISMISS = 0.9f;
    private static final float SCALE_FOR_POP = 1.2f;
    private View mDismissCircle;
    private ImageView mDismissIcon;
    private LinearLayout mDismissTarget;
    private SpringAnimation mDismissTargetAlphaSpring;
    private SpringAnimation mDismissTargetVerticalSpring;

    public BubbleDismissView(Context context) {
        super(context);
        setVisibility(8);
        LayoutInflater.from(context).inflate(R.layout.bubble_dismiss_target, (ViewGroup) this, true);
        this.mDismissTarget = (LinearLayout) findViewById(R.id.bubble_dismiss_icon_container);
        this.mDismissIcon = (ImageView) findViewById(R.id.bubble_dismiss_close_icon);
        this.mDismissCircle = findViewById(R.id.bubble_dismiss_circle);
        AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        this.mDismissIcon.animate().setDuration(150L).setInterpolator(interpolator);
        this.mDismissCircle.animate().setDuration(75L).setInterpolator(interpolator);
        this.mDismissTargetAlphaSpring = new SpringAnimation(this.mDismissTarget, DynamicAnimation.ALPHA).setSpring(new SpringForce().setStiffness(200.0f).setDampingRatio(0.75f));
        this.mDismissTargetVerticalSpring = new SpringAnimation(this.mDismissTarget, DynamicAnimation.TRANSLATION_Y).setSpring(new SpringForce().setStiffness(1500.0f).setDampingRatio(0.75f));
        this.mDismissTargetAlphaSpring.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleDismissView$k9Xt4VfLNoRaJ7mqmfivzckWcKM
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                BubbleDismissView.this.lambda$new$0$BubbleDismissView(dynamicAnimation, z, f, f2);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$BubbleDismissView(DynamicAnimation anim, boolean canceled, float alpha, float velocity) {
        if (alpha < 0.5f) {
            setVisibility(4);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void springIn() {
        setVisibility(0);
        this.mDismissIcon.animate().setDuration(50L).scaleX(1.0f).scaleY(1.0f).alpha(1.0f);
        this.mDismissTarget.setAlpha(0.0f);
        this.mDismissTargetAlphaSpring.animateToFinalPosition(1.0f);
        LinearLayout linearLayout = this.mDismissTarget;
        linearLayout.setTranslationY(linearLayout.getHeight() / 2.0f);
        this.mDismissTargetVerticalSpring.animateToFinalPosition(0.0f);
        this.mDismissCircle.setAlpha(0.0f);
        this.mDismissCircle.setScaleX(SCALE_FOR_POP);
        this.mDismissCircle.setScaleY(SCALE_FOR_POP);
        this.mDismissCircle.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void springOut() {
        this.mDismissIcon.animate().setDuration(50L).scaleX(SCALE_FOR_DISMISS).scaleY(SCALE_FOR_DISMISS).alpha(0.0f);
        this.mDismissTargetAlphaSpring.animateToFinalPosition(0.0f);
        this.mDismissTargetVerticalSpring.animateToFinalPosition(this.mDismissTarget.getHeight() / 2.0f);
        this.mDismissCircle.animate().scaleX(SCALE_FOR_DISMISS).scaleY(SCALE_FOR_DISMISS).alpha(0.0f);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float getDismissTargetCenterY() {
        return getTop() + this.mDismissTarget.getTop() + (this.mDismissTarget.getHeight() / 2.0f);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public View getDismissTarget() {
        return this.mDismissTarget;
    }
}
