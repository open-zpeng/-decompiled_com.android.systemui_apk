package com.android.systemui.classifier;
/* loaded from: classes21.dex */
public class SpeedRatioEvaluator {
    public static float evaluate(float value) {
        if (value != 0.0f) {
            float evaluation = ((double) value) <= 1.0d ? 0.0f + 1.0f : 0.0f;
            if (value <= 0.5d) {
                evaluation += 1.0f;
            }
            if (value > 9.0d) {
                evaluation += 1.0f;
            }
            return ((double) value) > 18.0d ? evaluation + 1.0f : evaluation;
        }
        return 0.0f;
    }
}
