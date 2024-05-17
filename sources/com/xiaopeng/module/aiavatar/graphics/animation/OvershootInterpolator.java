package com.xiaopeng.module.aiavatar.graphics.animation;
/* loaded from: classes23.dex */
public class OvershootInterpolator implements Interpolator {
    private final float mTension;

    public OvershootInterpolator() {
        this.mTension = 2.0f;
    }

    public OvershootInterpolator(float tension) {
        this.mTension = tension;
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.TimeInterpolator
    public float getInterpolation(float t) {
        float t2 = t - 1.0f;
        float f = this.mTension;
        return (t2 * t2 * (((f + 1.0f) * t2) + f)) + 1.0f;
    }
}
