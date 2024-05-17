package com.android.systemui.classifier;
/* loaded from: classes21.dex */
public class SpeedAnglesPercentageEvaluator {
    public static float evaluate(float value) {
        float evaluation = ((double) value) < 1.0d ? 0.0f + 1.0f : 0.0f;
        if (value < 0.9d) {
            evaluation += 1.0f;
        }
        return ((double) value) < 0.7d ? evaluation + 1.0f : evaluation;
    }
}
