package com.xiaopeng.module.aiavatar.graphics.animation;
/* loaded from: classes23.dex */
public class AccelerateDecelerateInterpolator implements Interpolator {
    @Override // com.xiaopeng.module.aiavatar.graphics.animation.TimeInterpolator
    public float getInterpolation(float input) {
        return ((float) (Math.cos((1.0f + input) * 3.141592653589793d) / 2.0d)) + 0.5f;
    }
}
