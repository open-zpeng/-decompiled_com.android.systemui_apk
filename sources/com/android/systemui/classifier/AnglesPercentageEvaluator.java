package com.android.systemui.classifier;
/* loaded from: classes21.dex */
public class AnglesPercentageEvaluator {
    public static float evaluate(float value, int type) {
        boolean secureUnlock = type == 8;
        float evaluation = 0.0f;
        if (value < 1.0d && !secureUnlock) {
            evaluation = 0.0f + 1.0f;
        }
        if (value < 0.9d && !secureUnlock) {
            evaluation += 1.0f;
        }
        return ((double) value) < 0.7d ? evaluation + 1.0f : evaluation;
    }
}
