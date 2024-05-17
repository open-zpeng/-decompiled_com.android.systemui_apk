package com.android.systemui.statusbar.phone;

import android.view.animation.Interpolator;
/* loaded from: classes21.dex */
public class BounceInterpolator implements Interpolator {
    private static final float SCALE_FACTOR = 7.5625f;

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float t) {
        float t2 = t * 1.1f;
        if (t2 < 0.36363637f) {
            return SCALE_FACTOR * t2 * t2;
        }
        if (t2 < 0.72727275f) {
            float t22 = t2 - 0.54545456f;
            return (SCALE_FACTOR * t22 * t22) + 0.75f;
        } else if (t2 < 0.90909094f) {
            float t23 = t2 - 0.8181818f;
            return (SCALE_FACTOR * t23 * t23) + 0.9375f;
        } else {
            return 1.0f;
        }
    }
}
