package com.xiaopeng.systemui.infoflow.message.util;
/* loaded from: classes24.dex */
public class AngleUtils {
    private static final int MAX_ACCELERATION = 200;
    private static final int MAX_X_ANGLE = 6;

    public static int getAccelerationAngle(float value) {
        int angle = ((int) (value / 200.0f)) * 6;
        if (angle > 6) {
            return 6;
        }
        return angle;
    }

    public static void getAngularVelocityAngle(float value) {
    }
}
