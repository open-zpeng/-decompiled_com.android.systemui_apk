package com.xiaopeng.systemui.carconfig.utils;

import android.car.Car;
import android.os.SystemProperties;
/* loaded from: classes24.dex */
public class CarStatusUtils {
    private static final String TAG = "CarVersionUtil";
    private static Boolean mIsStageP = null;

    public static String getHardwareCarType() {
        String versionFinger = "";
        try {
            versionFinger = Car.getHardwareCarType();
            if (!"".equals(versionFinger) && versionFinger != null && versionFinger.length() > 3) {
                return versionFinger.substring(0, 3);
            }
            return versionFinger;
        } catch (Exception e) {
            e.printStackTrace();
            return versionFinger;
        }
    }

    public static String getCarStageVersion() {
        try {
            return Car.getHardwareCarStage();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean isCarStageP() {
        if (mIsStageP == null) {
            mIsStageP = Boolean.valueOf("P".equals(getCarStageVersion()));
        }
        return mIsStageP.booleanValue();
    }

    public static String getXpCduType() {
        try {
            return Car.getXpCduType();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getRegion() {
        try {
            String versionFinger = SystemProperties.get("ro.xiaopeng.software", "");
            if (versionFinger != null && !versionFinger.isEmpty()) {
                return versionFinger.substring(7, 9);
            }
            return versionFinger;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
