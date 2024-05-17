package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.view.View;
import com.android.systemui.R;
import com.xiaopeng.libtheme.ThemeManager;
/* loaded from: classes21.dex */
public final class PhoneStatusBarTransitions extends BarTransitions {
    private static final float ICON_ALPHA_WHEN_LIGHTS_OUT_BATTERY_CLOCK = 0.5f;
    private static final float ICON_ALPHA_WHEN_LIGHTS_OUT_NON_BATTERY_CLOCK = 0.0f;
    private static final float ICON_ALPHA_WHEN_NOT_OPAQUE = 1.0f;
    private View mBattery;
    private Animator mCurrentAnimation;
    private final float mIconAlphaWhenOpaque;
    private View mLeftSide;
    private View mStatusIcons;

    public PhoneStatusBarTransitions(PhoneStatusBarView statusBarView, View backgroundView) {
        super(backgroundView, R.drawable.status_background);
        Resources res = statusBarView.getContext().getResources();
        this.mIconAlphaWhenOpaque = res.getFraction(R.dimen.status_bar_icon_drawing_alpha, 1, 1);
        this.mLeftSide = statusBarView.findViewById(R.id.status_bar_left_side);
        this.mStatusIcons = statusBarView.findViewById(R.id.statusIcons);
        this.mBattery = statusBarView.findViewById(R.id.battery);
        applyModeBackground(-1, getMode(), false);
        applyMode(getMode(), false);
    }

    public ObjectAnimator animateTransitionTo(View v, float toAlpha) {
        return ObjectAnimator.ofFloat(v, ThemeManager.AttributeSet.ALPHA, v.getAlpha(), toAlpha);
    }

    private float getNonBatteryClockAlphaFor(int mode) {
        if (isLightsOut(mode)) {
            return 0.0f;
        }
        if (isOpaque(mode)) {
            return this.mIconAlphaWhenOpaque;
        }
        return 1.0f;
    }

    private float getBatteryClockAlpha(int mode) {
        if (isLightsOut(mode)) {
            return 0.5f;
        }
        return getNonBatteryClockAlphaFor(mode);
    }

    private boolean isOpaque(int mode) {
        return (mode == 1 || mode == 2 || mode == 4 || mode == 6) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.phone.BarTransitions
    public void onTransition(int oldMode, int newMode, boolean animate) {
        super.onTransition(oldMode, newMode, animate);
        applyMode(newMode, animate);
    }

    private void applyMode(int mode, boolean animate) {
        if (this.mLeftSide == null) {
            return;
        }
        float newAlpha = getNonBatteryClockAlphaFor(mode);
        float newAlphaBC = getBatteryClockAlpha(mode);
        Animator animator = this.mCurrentAnimation;
        if (animator != null) {
            animator.cancel();
        }
        if (animate) {
            AnimatorSet anims = new AnimatorSet();
            anims.playTogether(animateTransitionTo(this.mLeftSide, newAlpha), animateTransitionTo(this.mStatusIcons, newAlpha), animateTransitionTo(this.mBattery, newAlphaBC));
            if (isLightsOut(mode)) {
                anims.setDuration(1500L);
            }
            anims.start();
            this.mCurrentAnimation = anims;
            return;
        }
        this.mLeftSide.setAlpha(newAlpha);
        this.mStatusIcons.setAlpha(newAlpha);
        this.mBattery.setAlpha(newAlphaBC);
    }
}
