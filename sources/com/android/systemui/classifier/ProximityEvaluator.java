package com.android.systemui.classifier;
/* loaded from: classes21.dex */
public class ProximityEvaluator {
    public static float evaluate(float value, int type) {
        float threshold = 0.1f;
        if (type == 0) {
            threshold = 1.0f;
        }
        if (value >= threshold) {
            float evaluation = (float) (0.0f + 2.0d);
            return evaluation;
        }
        return 0.0f;
    }
}
