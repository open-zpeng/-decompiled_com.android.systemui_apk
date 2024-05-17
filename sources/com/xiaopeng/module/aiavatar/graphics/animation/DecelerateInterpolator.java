package com.xiaopeng.module.aiavatar.graphics.animation;
/* loaded from: classes23.dex */
public class DecelerateInterpolator implements Interpolator {
    private float mFactor;

    public DecelerateInterpolator() {
        this.mFactor = 1.0f;
    }

    public DecelerateInterpolator(float factor) {
        this.mFactor = 1.0f;
        this.mFactor = factor;
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.TimeInterpolator
    public float getInterpolation(float input) {
        float f = this.mFactor;
        if (f == 1.0f) {
            float result = 1.0f - ((1.0f - input) * (1.0f - input));
            return result;
        }
        float result2 = (float) (1.0d - Math.pow(1.0f - input, f * 2.0f));
        return result2;
    }
}
