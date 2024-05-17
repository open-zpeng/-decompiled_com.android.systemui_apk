package com.android.systemui.classifier;
/* loaded from: classes21.dex */
public class DirectionEvaluator {
    public static float evaluate(float xDiff, float yDiff, int type) {
        boolean vertical = Math.abs(yDiff) >= Math.abs(xDiff);
        if (type != 0) {
            if (type == 1) {
                if (vertical) {
                    return 5.5f;
                }
                return 0.0f;
            } else if (type != 2) {
                if (type != 4) {
                    if (type == 5) {
                        if (xDiff < 0.0d && yDiff > 0.0d) {
                            return 5.5f;
                        }
                        return 0.0f;
                    } else if (type == 6) {
                        if (xDiff > 0.0d && yDiff > 0.0d) {
                            return 5.5f;
                        }
                        return 0.0f;
                    } else if (type != 8) {
                        if (type != 9) {
                            return 0.0f;
                        }
                    }
                }
                if (!vertical || yDiff >= 0.0d) {
                    return 5.5f;
                }
                return 0.0f;
            }
        }
        if (!vertical || yDiff <= 0.0d) {
            return 5.5f;
        }
        return 0.0f;
    }
}
