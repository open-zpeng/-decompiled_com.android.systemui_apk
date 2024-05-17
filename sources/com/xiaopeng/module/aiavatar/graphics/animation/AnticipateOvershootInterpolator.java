package com.xiaopeng.module.aiavatar.graphics.animation;
/* loaded from: classes23.dex */
public class AnticipateOvershootInterpolator implements Interpolator {
    private final float mTension;

    public AnticipateOvershootInterpolator() {
        this.mTension = 3.0f;
    }

    public AnticipateOvershootInterpolator(float tension) {
        this.mTension = 1.5f * tension;
    }

    public AnticipateOvershootInterpolator(float tension, float extraTension) {
        this.mTension = tension * extraTension;
    }

    private static float a(float t, float s) {
        return t * t * (((1.0f + s) * t) - s);
    }

    private static float o(float t, float s) {
        return t * t * (((1.0f + s) * t) + s);
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.TimeInterpolator
    public float getInterpolation(float t) {
        return t < 0.5f ? a(2.0f * t, this.mTension) * 0.5f : (o((t * 2.0f) - 2.0f, this.mTension) + 2.0f) * 0.5f;
    }
}
