package com.xiaopeng.systemui.infoflow.message;

import com.android.systemui.SystemUIApplication;
import com.xiaopeng.systemui.infoflow.message.util.SharedPreferenceUtil;
/* loaded from: classes24.dex */
public class KeyConfig {
    private static final String KEY_KEY_CONFIG = "key_config";
    public static final int NUM_TO_FOCUS = 3;
    private static final String PRE_FILE_NAME = "base_config";
    public static int NORMAL = 0;
    public static int THREE_FOCUS_ONE_SCROLL = 1;
    public static int THREE_FOCUS_TWO_SCROLL = 2;
    public static int THREE_FOCUS_FOUR_SCROLL = 3;
    private static int currentConfig = THREE_FOCUS_TWO_SCROLL;

    public static void saveConfig(int config) {
        if (config != currentConfig) {
            currentConfig = config;
        }
    }

    public static int getCurrentConfig() {
        return currentConfig;
    }

    private void savePreferenceConfig(int config) {
        SharedPreferenceUtil.set(SystemUIApplication.getContext(), PRE_FILE_NAME, KEY_KEY_CONFIG, config);
    }

    private int getPreferenceConfig() {
        return SharedPreferenceUtil.get(SystemUIApplication.getContext(), PRE_FILE_NAME, KEY_KEY_CONFIG, THREE_FOCUS_TWO_SCROLL);
    }
}
