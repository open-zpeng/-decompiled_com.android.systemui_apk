package com.android.systemui.classifier;
/* loaded from: classes21.dex */
public class AnglesVarianceEvaluator {
    public static float evaluate(float value, int type) {
        float evaluation = ((double) value) > 0.2d ? 0.0f + 1.0f : 0.0f;
        if (value > 0.4d) {
            evaluation += 1.0f;
        }
        if (value > 0.8d) {
            evaluation += 1.0f;
        }
        return ((double) value) > 1.5d ? evaluation + 1.0f : evaluation;
    }
}
