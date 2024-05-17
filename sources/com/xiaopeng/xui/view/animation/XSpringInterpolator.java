package com.xiaopeng.xui.view.animation;

import android.view.animation.Interpolator;
/* loaded from: classes25.dex */
public class XSpringInterpolator implements Interpolator {
    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float input) {
        return (float) ((Math.pow(2.0d, (-11.0f) * input) * Math.sin(((input - (0.7f / 4.0f)) * 6.283185307179586d) / 0.7f)) + 1.0d);
    }
}
