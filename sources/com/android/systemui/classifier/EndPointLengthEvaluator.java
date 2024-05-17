package com.android.systemui.classifier;
/* loaded from: classes21.dex */
public class EndPointLengthEvaluator {
    public static float evaluate(float value) {
        float evaluation = ((double) value) < 0.05d ? (float) (0.0f + 2.0d) : 0.0f;
        if (value < 0.1d) {
            evaluation = (float) (evaluation + 2.0d);
        }
        if (value < 0.2d) {
            evaluation = (float) (evaluation + 2.0d);
        }
        if (value < 0.3d) {
            evaluation = (float) (evaluation + 2.0d);
        }
        if (value < 0.4d) {
            evaluation = (float) (evaluation + 2.0d);
        }
        return ((double) value) < 0.5d ? (float) (evaluation + 2.0d) : evaluation;
    }
}
