package com.xiaopeng.module.aiavatar.graphics.interceptor;
/* loaded from: classes23.dex */
public class AnticipateInterpolator implements Interpolator {
    private final float mTension;

    public AnticipateInterpolator() {
        this.mTension = 2.0f;
    }

    public AnticipateInterpolator(float tension) {
        this.mTension = tension;
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.interceptor.TimeInterpolator
    public float getInterpolation(float t) {
        float f = this.mTension;
        return t * t * (((1.0f + f) * t) - f);
    }
}
