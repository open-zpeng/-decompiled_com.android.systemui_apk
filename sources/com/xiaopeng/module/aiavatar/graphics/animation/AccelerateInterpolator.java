package com.xiaopeng.module.aiavatar.graphics.animation;
/* loaded from: classes23.dex */
public class AccelerateInterpolator implements Interpolator {
    private final double mDoubleFactor;
    private final float mFactor;

    public AccelerateInterpolator() {
        this.mFactor = 1.0f;
        this.mDoubleFactor = 2.0d;
    }

    public AccelerateInterpolator(float factor) {
        this.mFactor = factor;
        this.mDoubleFactor = this.mFactor * 2.0f;
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.TimeInterpolator
    public float getInterpolation(float input) {
        if (this.mFactor == 1.0f) {
            return input * input;
        }
        return (float) Math.pow(input, this.mDoubleFactor);
    }
}
