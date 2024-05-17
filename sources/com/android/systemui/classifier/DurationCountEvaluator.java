package com.android.systemui.classifier;
/* loaded from: classes21.dex */
public class DurationCountEvaluator {
    public static float evaluate(float value) {
        float evaluation = ((double) value) < 0.0105d ? 0.0f + 1.0f : 0.0f;
        if (value < 0.00909d) {
            evaluation += 1.0f;
        }
        if (value < 0.00667d) {
            evaluation += 1.0f;
        }
        if (value > 0.0333d) {
            evaluation += 1.0f;
        }
        return ((double) value) > 0.05d ? evaluation + 1.0f : evaluation;
    }
}
