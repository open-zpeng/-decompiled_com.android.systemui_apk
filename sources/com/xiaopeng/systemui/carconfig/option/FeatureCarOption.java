package com.xiaopeng.systemui.carconfig.option;

import com.xiaopeng.util.FeatureOption;
import java.util.HashMap;
/* loaded from: classes24.dex */
public class FeatureCarOption {
    public static final boolean FO_OSD_REDUCE_SELF_USE = true;
    public static final int HAS_CIU = 1;
    public static final int HAS_XPU = 0;
    public static final int PROPERTY_AUTO_WIPER_SENSITIVITY = 2;
    public static final int PROPERTY_AUTO_WIPER_SPEED = 1;
    public static final boolean SIMPLE_ACCOUNT_ICON = true;
    private static HashMap<String, Boolean> sFeatureSupport;
    private static HashMap<String, Integer> sFeatureValue;
    public static final int FO_MAX_WIND_SPEED = getFeatureValue("fo.systemui.windspeed.max", 7);
    public static final boolean BACK_BOX_ONLY_OPEN = hasFeature("fo.systemui.backbox.open.only", false);
    public static final boolean WINDOW_LOADING_DISABLE = hasFeature("fo.systemui.window.loading.disable", false);
    public static final boolean AUTO_WIPER_TEXT = hasFeature("fo.systemui.auto.wiper.text", false);
    public static final boolean FO_SYNC_TEMPERATURE = hasFeature("fo.systemui.sync.temperature", false);
    public static final int FO_CHECK_FUNCTION_TYPE = getFeatureValue("fo.systemui.check.function.type", 2);
    public static final boolean FO_PURIFY_IN_ALL_SERIES = hasFeature("fo.systemui.purify.in.all.series", false);
    public static final boolean FO_LOCK_SUPPORT = hasFeature("fo.systemui.lock.support", false);
    public static final boolean FO_HAS_AUTO_FAN_SPEED = hasFeature("fo.systemui.has.auto.fan.speed", true);
    public static final boolean FO_HAS_MEDITATION_MODE = hasFeature("fo.systemui.has.meditation.mode", true);
    public static final int FO_WIND_LEVEL = getFeatureValue("fo.systemui.wind.level", 7);
    public static final boolean FO_SPEECH_INTERACTION = hasFeature("fo.systemui.speech.interaction", true);
    public static final boolean FO_HAS_SECONDARY_WINDOW = FeatureOption.FO_SHARED_DISPLAY_ENABLED;
    public static final boolean FO_SUPPORT_MULTIPLAYER_VOICE = hasFeature("fo.systemui.support.multiplayer.voice", false);
    public static final boolean FO_SUPPORT_SECOND_SCREEN_APPS_INFO_CONFIG = hasFeature("fo.systemui.support.second.screen.apps.info.config", true);
    public static final int FO_SYSUI_DISPLAY_TYPE = FeatureOption.FO_PROJECT_UI_TYPE;
    public static final boolean FO_SYSTEMUI_SPEECHUI_ASR_OLD = hasFeature("fo.systemui.speechui.asr.old", false);
    public static final boolean FO_IS_FACE_ANGLE_ENABLED = FeatureOption.FO_FACE_ANGLE_ENABLED;
    public static final boolean FO_IS_DUAL_CHARGING_PORT = hasFeature("fo.device.dual.charging.port", false);
    public static final boolean FO_HAS_PRE_INSTALL_APP = hasFeature("fo.systemui.has.preinstallapp", false);
    public static final boolean FO_SUPPORT_NEW_QS = hasFeature("fo.systemui.support.newqspanel", true);

    public static boolean hasFeature(String propertyName, boolean defalueValue) {
        if (sFeatureSupport == null) {
            sFeatureSupport = new HashMap<>();
        }
        Boolean support = sFeatureSupport.get(propertyName);
        if (support == null) {
            support = Boolean.valueOf(FeatureOption.getBoolean(propertyName, defalueValue));
            sFeatureSupport.put(propertyName, support);
        }
        return support.booleanValue();
    }

    public static int getFeatureValue(String propertyName, int defalutValue) {
        if (sFeatureValue == null) {
            sFeatureValue = new HashMap<>();
        }
        Integer value = sFeatureValue.get(propertyName);
        if (value == null) {
            value = Integer.valueOf(FeatureOption.getInt(propertyName, defalutValue));
            sFeatureValue.put(propertyName, value);
        }
        return value.intValue();
    }

    public static boolean isFaceAngleEnabled() {
        return FeatureOption.FO_FACE_ANGLE_ENABLED;
    }
}
