package com.android.keyguard.clock;

import android.view.View;
/* loaded from: classes19.dex */
final class CrossFadeDarkController {
    private final View mFadeInView;
    private final View mFadeOutView;

    CrossFadeDarkController(View fadeInView, View fadeOutView) {
        this.mFadeInView = fadeInView;
        this.mFadeOutView = fadeOutView;
    }

    void setDarkAmount(float darkAmount) {
        this.mFadeInView.setAlpha(Math.max(0.0f, (darkAmount * 2.0f) - 1.0f));
        if (darkAmount == 0.0f) {
            this.mFadeInView.setVisibility(8);
        } else if (this.mFadeInView.getVisibility() == 8) {
            this.mFadeInView.setVisibility(0);
        }
        this.mFadeOutView.setAlpha(Math.max(0.0f, 1.0f - (2.0f * darkAmount)));
        if (darkAmount == 1.0f) {
            this.mFadeOutView.setVisibility(8);
        } else if (this.mFadeOutView.getVisibility() == 8) {
            this.mFadeOutView.setVisibility(0);
        }
    }
}
