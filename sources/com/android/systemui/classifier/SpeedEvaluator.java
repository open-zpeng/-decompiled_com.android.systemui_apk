package com.android.systemui.classifier;
/* loaded from: classes21.dex */
public class SpeedEvaluator {
    public static float evaluate(float value) {
        float evaluation = ((double) value) < 4.0d ? 0.0f + 1.0f : 0.0f;
        if (value < 2.2d) {
            evaluation += 1.0f;
        }
        if (value > 35.0d) {
            evaluation += 1.0f;
        }
        return ((double) value) > 50.0d ? evaluation + 1.0f : evaluation;
    }
}
