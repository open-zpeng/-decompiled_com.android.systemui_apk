package com.xiaopeng.systemui.infoflow.message.util;

import android.os.SystemProperties;
/* loaded from: classes24.dex */
public class Config {
    public static final String TEST_ENABLE_KEY = "persist.debug.test.enable";
    public static final String WEBP_ENABLE_KEY = "persist.debug.webp.enable";

    public static boolean isWebpEnable() {
        return SystemProperties.getBoolean(WEBP_ENABLE_KEY, false);
    }

    public static boolean isTestEnable() {
        return SystemProperties.getBoolean(TEST_ENABLE_KEY, false);
    }
}
