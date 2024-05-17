package com.xiaopeng.module.aiavatar.graphics.animation;
/* loaded from: classes23.dex */
public class CycleInterpolator implements Interpolator {
    private float mCycles;

    public CycleInterpolator(float cycles) {
        this.mCycles = cycles;
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.TimeInterpolator
    public float getInterpolation(float input) {
        return (float) Math.sin(this.mCycles * 2.0f * 3.141592653589793d * input);
    }
}
