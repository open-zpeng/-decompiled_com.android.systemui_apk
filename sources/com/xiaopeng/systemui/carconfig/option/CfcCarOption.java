package com.xiaopeng.systemui.carconfig.option;

import android.os.SystemProperties;
import java.util.HashMap;
/* loaded from: classes24.dex */
public class CfcCarOption {
    public static final int CONFIG_CODE_01 = 1;
    public static final int CONFIG_CODE_02 = 2;
    public static final int CONFIG_CODE_03 = 3;
    public static final int CONFIG_CODE_04 = 4;
    public static final int CONFIG_CODE_05 = 5;
    public static final int CONFIG_CODE_06 = 6;
    public static final int CONFIG_CODE_07 = 7;
    public static final int CONFIG_CODE_08 = 8;
    public static final int CONFIG_CODE_09 = 9;
    public static final int INVALID = 0;
    private static HashMap<String, Boolean> sFeatureSupport;
    private static HashMap<String, Integer> sFeatureValue;
    public static final int PROPERTY_CFC_VALUE = getConfigValue("persist.sys.xiaopeng.cfcIndex", 0);
    public static final int CFG_CONFIG_CODE_VALUE = getConfigValue("persist.sys.xiaopeng.configCode", 0);
    public static final int CFC_VEHICLE_LEVEL_VALUE = getConfigValue("persist.sys.xiaopeng.cfcVehicleLevel", 0);
    public static final boolean CFG_PACKAGE_1_SUPPORT = hasConfig("persist.sys.xiaopeng.Package1", 0);
    public static final boolean CFG_PACKAGE_2_SUPPORT = hasConfig("persist.sys.xiaopeng.Package2", 0);
    public static final boolean CFG_PACKAGE_3_SUPPORT = hasConfig("persist.sys.xiaopeng.Package3", 0);
    public static final boolean CFG_PACKAGE_4_SUPPORT = hasConfig("persist.sys.xiaopeng.Package4", 0);
    public static final boolean CFG_ATLS_SUPPORT = hasConfig("persist.sys.xiaopeng.ATLS", 0);
    public static final boolean CFG_LLU_SUPPORT = hasConfig("persist.sys.xiaopeng.LLU", 0);
    public static final boolean CFG_AQS_SUPPORT = hasConfig("persist.sys.xiaopeng.AQS", 0);
    public static final boolean CFG_MIRROR_SUPPORT = hasConfig("persist.sys.xiaopeng.MIRROR", 0);
    public static final boolean CFG_MSMD_SUPPORT = hasConfig("persist.sys.xiaopeng.MSMD", 0);
    public static final boolean CFG_MSMP_SUPPORT = hasConfig("persist.sys.xiaopeng.MSMP", 0);
    public static final boolean CFG_MSB_SUPPORT = hasConfig("persist.sys.xiaopeng.MSB", 0);
    public static final boolean CFG_AVM_SUPPORT = hasConfig("persist.sys.xiaopeng.AVM", 0);
    public static final boolean CFG_SCU_SUPPORT = hasConfig("persist.sys.xiaopeng.SCU", 0);
    public static final boolean CFG_MRR_SUPPORT = hasConfig("persist.sys.xiaopeng.MRR", 0);
    public static final boolean CFG_SRR_RR_SUPPORT = hasConfig("persist.sys.xiaopeng.SRR_RR", 0);
    public static final boolean CFG_SRR_RL_SUPPORT = hasConfig("persist.sys.xiaopeng.SRR_RL", 0);
    public static final boolean CFG_SRR_FR_SUPPORT = hasConfig("persist.sys.xiaopeng.SRR_FR", 0);
    public static final boolean CFG_SRR_FL_SUPPORT = hasConfig("persist.sys.xiaopeng.SRR_FL", 0);
    public static final boolean CFG_XPU_SUPPORT = hasConfig("persist.sys.xiaopeng.XPU", 0);
    public static final boolean CFG_VPM_SUPPORT = hasConfig("persist.sys.xiaopeng.VPM", 0);
    public static final boolean CFG_CIU_SUPPORT = hasConfig("persist.sys.xiaopeng.CIU", 0);
    public static final boolean CFG_IMU_SUPPORT = hasConfig("persist.sys.xiaopeng.IMU", 0);
    public static final boolean CFG_PAS_SUPPORT = hasConfig("persist.sys.xiaopeng.PAS", 0);
    public static final boolean CFG_RLS_SUPPORT = hasConfig("persist.sys.xiaopeng.RLS", 0);
    public static final boolean CFG_AMP_SUPPORT = hasConfig("persist.sys.xiaopeng.AMP", 0);
    public static final boolean CFG_NFC_SUPPORT = hasConfig("persist.sys.xiaopeng.NFC", 0);
    public static final boolean CFG_CWC_SUPPORT = hasConfig("persist.sys.xiaopeng.CWC", 0);
    public static final boolean CFG_SHC_SUPPORT = hasConfig("persist.sys.xiaopeng.SHC", 0);
    public static final boolean CFG_IPUF_SUPPORT = hasConfig("persist.sys.xiaopeng.IPUF", 0);
    public static final boolean CFG_IPUR_SUPPORT = hasConfig("persist.sys.xiaopeng.IPUR", 0);
    @Deprecated
    public static final boolean CFG_SEAT_MASSAGE_SUPPORT = hasConfig("persist.sys.xiaopeng.SEAT_MASS", 0);
    public static final boolean CFG_THEME_SUPPORT = hasConfig("persist.sys.sysui.theme.switcher", 0);
    public static final boolean CFG_FONTS_SUPPORT = hasConfig("persist.sys.sysui.fonts.switcher", 0);
    public static final boolean CFG_APPLIST_DIRECTLY = hasConfig("persist.sys.sysui.applist.directly", 1);
    public static final boolean CFG_CLOSE_PANEL_FOR_INFOFLOW_SPEECH_SUPPORT = hasConfig("persist.systemui.close.panel.for.infoflow.speech", 0);
    public static final boolean CFG_SEAT_HEAT_SUPPORT = hasConfig("persist.sys.xiaopeng.MSMD_HEAT", 0);
    public static final boolean CFG_SEAT_VENT_SUPPORT = hasConfig("persist.sys.xiaopeng.MSMD_VENT", 0);
    public static final boolean CFG_PSN_SEAT_HEAT_SUPPORT = hasConfig("persist.sys.xiaopeng.MSMP_HEAT", 0);
    public static final boolean CFG_PSN_SEAT_VENT_SUPPORT = hasConfig("persist.sys.xiaopeng.MSMP_VENT", 0);
    public static final boolean CFG_DRV_SEAT_MASS_SUPPORT = hasConfig("persist.sys.xiaopeng.MSMD_MASSG", 0);
    public static final boolean CFG_PSN_SEAT_MASS_SUPPORT = hasConfig("persist.sys.xiaopeng.MSMP_MASSG", 0);
    public static final boolean CFG_SECROW_LT_MASSG_SUPPORT = hasConfig("persist.sys.xiaopeng.SECROW_LT_MASSG", 0);
    public static final boolean CFG_SECROW_RT_MASSG_SUPPORT = hasConfig("persist.sys.xiaopeng.SECROW_RT_MASSG", 0);
    public static final boolean CFG_XSPORT_SUPPORT = hasConfig("persist.sys.xiaopeng.XSPORT", 0);
    public static final int CFG_ALL_WHEEL_KEY_TO_ICM_SUPPORT = getConfigValue("persist.sys.keyconfig.icm", 1);
    public static final float CFG_QUICK_MENU_OPAQUE_POS_VALUE = getConfigValue("persist.systemui.quick.menu.opaque.pos", 50);
    public static final int CFG_QUICK_MENU_TAKE_EFFECT_DIST_VALUE = getConfigValue("persist.systemui.quick.menu.take.effect.dist", 100);
    public static final int CFG_SPEAKER_VALUE = getConfigValue("persist.sys.xiaopeng.SPEAKER", 0);
    public static final boolean CFG_MAKEUP_MIRROR_SUPPORT = hasConfig("persist.sys.xiaopeng.MAKEUP_MIRROR", 0);
    public static final boolean CFG_DOLBY_SUPPORT = hasConfig("persist.sys.xiaopeng.DOLBY", 0);
    public static final int FO_CMS_SCREEN = getConfigValue("persist.sys.xiaopeng.CMS", 0);
    public static final boolean CFG_AS_SUPPORT = hasConfig("persist.sys.xiaopeng.AS", 0);
    public static final String CFG_CAR_RECORDER_NAME = SystemProperties.get("persist.sys.xp.recorder_ssid", "");
    public static final boolean CFG_SRS_SUPPORT = hasConfig("persist.sys.xiaopeng.PAB.Switch", 0);
    public static final boolean CFG_PM25 = hasConfig("persist.sys.xiaopeng.pm25", 0);

    public static boolean hasConfig(String propertyName, int defalueValue) {
        if (sFeatureSupport == null) {
            sFeatureSupport = new HashMap<>();
        }
        Boolean support = sFeatureSupport.get(propertyName);
        if (support == null) {
            int propValue = SystemProperties.getInt(propertyName, defalueValue);
            support = Boolean.valueOf(propValue == 1);
            sFeatureSupport.put(propertyName, support);
        }
        return support.booleanValue();
    }

    public static int getConfigValue(String propertyName, int defaultValue) {
        if (sFeatureValue == null) {
            sFeatureValue = new HashMap<>();
        }
        Integer value = sFeatureValue.get(propertyName);
        if (value == null) {
            value = Integer.valueOf(SystemProperties.getInt(propertyName, defaultValue));
            sFeatureValue.put(propertyName, value);
        }
        return value.intValue();
    }

    public static String getConfigString(String propertyName, String defaultValue) {
        return SystemProperties.get(propertyName, defaultValue);
    }
}
