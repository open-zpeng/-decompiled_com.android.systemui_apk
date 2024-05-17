package com.xiaopeng.module.aiavatar.graphics.animation;
/* loaded from: classes23.dex */
public class SmootherInterpolation implements android.view.animation.Interpolator {
    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float input) {
        return input * input * input * ((((6.0f * input) - 15.0f) * input) + 10.0f);
    }
}
