package com.xiaopeng.lib.utils.info;

import android.os.SystemProperties;
/* loaded from: classes23.dex */
public class DeviceInfoUtils {
    public static final int POWER_STATUS_FAKESLEEP = 1;
    public static final int POWER_STATUS_NORMAL = 0;
    public static final int POWER_STATUS_SLEEP = 2;
    public static final int POWER_STATUS_UNKNOWN = -1;
    private static final String PROPERTY_POWER_STATUS = "sys.power.xp_power_status";
    private static final String PROPERTY_PRODUCT_MODEL = "ro.product.model";

    public static int getPowerStatus() {
        return SystemProperties.getInt(PROPERTY_POWER_STATUS, -1);
    }

    public static String getProductModel() {
        return SystemProperties.get(PROPERTY_PRODUCT_MODEL, "");
    }
}
