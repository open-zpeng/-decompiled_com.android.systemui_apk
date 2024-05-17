package com.xiaopeng.speech.protocol.utils;

import android.os.SystemProperties;
import android.text.TextUtils;
import com.xiaopeng.speech.common.util.LogUtils;
/* loaded from: classes23.dex */
public class CarTypeUtils {
    private static final String CAR_COUNTRY_ZH = "ZH";
    private static final String CAR_PLATFORM_Q5 = "Q5";
    private static final String CAR_PLATFORM_Q8 = "Q8";
    private static final String CAR_TYPE_D21EU = "D21EU";
    private static final String CAR_TYPE_D21ZH = "D21ZH";
    private static final String CAR_TYPE_D22EU = "D22EU";
    private static final String CAR_TYPE_D55EU = "D55EU";
    private static final String CAR_TYPE_E28AEU = "E28AEU";
    private static final String CAR_TYPE_E28EU = "E28EU";
    private static final String CAR_TYPE_E28ZH = "E28ZH";
    private static final String CAR_TYPE_E38EU = "E38EU";
    private static final String CAR_TYPE_E38ZH = "E38ZH";
    private static final String TAG = "CarTypeUtils";

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static boolean isOverseasCarType() {
        char c;
        String carType = getHardwareCarType() + getVersionInCountryCode();
        LogUtils.d(TAG, "isOverseasCarType, carType: " + carType);
        switch (carType.hashCode()) {
            case 64338291:
                if (carType.equals(CAR_TYPE_D21EU)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 64339252:
                if (carType.equals(CAR_TYPE_D22EU)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 64431508:
                if (carType.equals(CAR_TYPE_D55EU)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 65268539:
                if (carType.equals(CAR_TYPE_E28EU)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 65298330:
                if (carType.equals(CAR_TYPE_E38EU)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        return c == 0 || c == 1 || c == 2 || c == 3 || c == 4;
    }

    public static boolean isD21EU() {
        String carType = getHardwareCarType() + getVersionInCountryCode();
        LogUtils.d(TAG, "isD21EU, carType: " + carType);
        return CAR_TYPE_D21EU.equalsIgnoreCase(carType);
    }

    public static boolean isD22EU() {
        String carType = getHardwareCarType() + getVersionInCountryCode();
        LogUtils.d(TAG, "isD22EU, carType: " + carType);
        return CAR_TYPE_D22EU.equalsIgnoreCase(carType);
    }

    public static boolean isD55EU() {
        String carType = getHardwareCarType() + getVersionInCountryCode();
        LogUtils.d(TAG, "isD55EU, carType: " + carType);
        return CAR_TYPE_D55EU.equalsIgnoreCase(carType);
    }

    public static boolean isE28EU() {
        String carType = getHardwareCarType() + getVersionInCountryCode();
        LogUtils.d(TAG, "isE28EU, carType: " + carType);
        return CAR_TYPE_E28EU.equalsIgnoreCase(carType);
    }

    public static boolean isE38EU() {
        String carType = getHardwareCarType() + getVersionInCountryCode();
        LogUtils.d(TAG, "isE38EU, carType: " + carType);
        return CAR_TYPE_E38EU.equalsIgnoreCase(carType);
    }

    public static boolean isE38ZH() {
        String carType = getHardwareCarType() + getVersionInCountryCode();
        LogUtils.d(TAG, "isE38ZH, carType: " + carType);
        return CAR_TYPE_E38ZH.equalsIgnoreCase(carType);
    }

    public static boolean isE28AZH() {
        String carType = getHardwareCarType() + getVersionInCountryCode();
        String carPlatform = getCarPlatform();
        LogUtils.d(TAG, "isE28AZH, carType: " + carType + ", carPlatform: " + carPlatform);
        return CAR_TYPE_E28ZH.equalsIgnoreCase(carType) && CAR_PLATFORM_Q8.equalsIgnoreCase(carPlatform);
    }

    public static boolean isE28AEU() {
        String carType = getHardwareCarType() + getVersionInCountryCode();
        String carPlatform = getCarPlatform();
        LogUtils.d(TAG, "isE28AEU, carType: " + carType + ", carPlatform: " + carPlatform);
        return CAR_TYPE_E28EU.equalsIgnoreCase(carType) && CAR_PLATFORM_Q8.equalsIgnoreCase(carPlatform);
    }

    public static boolean isD21ZH() {
        String carType = getHardwareCarType() + getVersionInCountryCode();
        String carPlatform = getCarPlatform();
        LogUtils.d(TAG, "isD21ZH, carType: " + carType + ", carPlatform: " + carPlatform);
        return CAR_TYPE_D21ZH.equalsIgnoreCase(carType) || ("Q5".equalsIgnoreCase(carPlatform) && CAR_COUNTRY_ZH.equalsIgnoreCase(getVersionInCountryCode()));
    }

    private static String getVersionInCountryCode() {
        String versionFinger = SystemProperties.get("ro.xiaopeng.software", "");
        if (!TextUtils.isEmpty(versionFinger)) {
            return versionFinger.substring(7, 9);
        }
        return versionFinger;
    }

    private static String getHardwareCarType() {
        String versionFinger = SystemProperties.get("ro.xiaopeng.software", "");
        if (!TextUtils.isEmpty(versionFinger)) {
            return versionFinger.substring(9, 12);
        }
        return versionFinger;
    }

    private static String getCarPlatform() {
        String versionFinger = SystemProperties.get("ro.xiaopeng.software", "");
        if (!TextUtils.isEmpty(versionFinger)) {
            return versionFinger.substring(5, 7);
        }
        return versionFinger;
    }
}
