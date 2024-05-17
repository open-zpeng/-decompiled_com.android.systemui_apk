package com.android.systemui.bubbles.animation;

import androidx.dynamicanimation.animation.DynamicAnimation;
/* loaded from: classes21.dex */
public class OneTimeEndListener implements DynamicAnimation.OnAnimationEndListener {
    @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
    public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
        animation.removeEndListener(this);
    }
}
