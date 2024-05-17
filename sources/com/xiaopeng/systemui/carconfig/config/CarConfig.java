package com.xiaopeng.systemui.carconfig.config;

import com.xiaopeng.speech.vui.utils.VuiUtils;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carconfig.option.CfcCarOption;
import com.xiaopeng.systemui.carconfig.utils.CarStatusUtils;
import com.xiaopeng.systemui.quickmenu.CarSettingsManager;
import com.xiaopeng.util.FeatureOption;
/* loaded from: classes24.dex */
public class CarConfig implements IConfig {
    private static String TAG = "CarConfig";

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean hasPackage1() {
        return CfcCarOption.CFG_PACKAGE_1_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    @Deprecated
    public boolean isSeatMessageSupport() {
        return CfcCarOption.CFG_SEAT_MASSAGE_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isMakeupSpaceSupport() {
        return CfcCarOption.CFG_PACKAGE_1_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isATLSSupport() {
        return CfcCarOption.CFG_ATLS_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isLLUSupport() {
        return CfcCarOption.CFG_LLU_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isAQSSupport() {
        return CfcCarOption.CFG_AQS_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public int autoBrightness() {
        boolean mHasCiuDevice = CarSettingsManager.getInstance().hasCiuDevice();
        if (CfcCarOption.CFG_XPU_SUPPORT) {
            return 0;
        }
        if (mHasCiuDevice) {
            return 1;
        }
        return -1;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isXPUSupport() {
        return CfcCarOption.CFG_XPU_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isVPMSupport() {
        return CfcCarOption.CFG_VPM_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isCIUSupport() {
        return CfcCarOption.CFG_CIU_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isIMUSupport() {
        return CfcCarOption.CFG_IMU_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isPASSupport() {
        return CfcCarOption.CFG_PAS_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isRLSSupport() {
        return CfcCarOption.CFG_RLS_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isIhbSupport() {
        return isXPUSupport();
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isMakeupMirrorSupport() {
        return CfcCarOption.CFG_MAKEUP_MIRROR_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isDolbySupport() {
        return CfcCarOption.CFG_DOLBY_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isAmpSupport() {
        return CfcCarOption.CFG_AMP_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isNFCSupport() {
        return CfcCarOption.CFG_NFC_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isCWCSupport() {
        return CfcCarOption.CFG_CWC_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isSHCSupport() {
        return CfcCarOption.CFG_SHC_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isIPUFSupport() {
        return CfcCarOption.CFG_IPUF_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isIPURSupport() {
        return CfcCarOption.CFG_IPUR_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isAirCleanSupport() {
        return CfcCarOption.CFG_PM25;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isDualChargePort() {
        String xpCduType = CarStatusUtils.getXpCduType();
        if (((xpCduType.hashCode() == 2567 && xpCduType.equals("Q8")) ? (char) 0 : (char) 65535) != 0) {
            return false;
        }
        return !FeatureOption.FO_DEVICE_INTERNATIONAL_ENABLED;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public int isAutoWiperSupport() {
        return 2;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isSleepSupport() {
        boolean z;
        String xpCduType = CarStatusUtils.getXpCduType();
        int hashCode = xpCduType.hashCode();
        if (hashCode != 2562) {
            if (hashCode == 2566 && xpCduType.equals(VuiUtils.CAR_PLATFORM_Q7)) {
                z = true;
            }
            z = true;
        } else {
            if (xpCduType.equals(VuiUtils.CAR_PLATFORM_Q3)) {
                z = false;
            }
            z = true;
        }
        if (z) {
            if (!z) {
                return false;
            }
            if (CfcCarOption.CFC_VEHICLE_LEVEL_VALUE == -1) {
                Logger.d(TAG, "car config level invalid!");
            }
            return CfcCarOption.CFC_VEHICLE_LEVEL_VALUE != 0;
        }
        return CfcCarOption.CFG_PACKAGE_1_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isThemeSupport() {
        return CfcCarOption.CFG_THEME_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isFontSupport() {
        return CfcCarOption.CFG_FONTS_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isApplistDirectly() {
        return CfcCarOption.CFG_APPLIST_DIRECTLY;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isClosePanelForInfoflowSpeechSupport() {
        return CfcCarOption.CFG_CLOSE_PANEL_FOR_INFOFLOW_SPEECH_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isSeatHeatSupport() {
        return CfcCarOption.CFG_SEAT_HEAT_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isSeatVentSupport() {
        return CfcCarOption.CFG_SEAT_VENT_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isPsnSeatHeatSupport() {
        return CfcCarOption.CFG_PSN_SEAT_HEAT_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isPsnSeatVentSupport() {
        return CfcCarOption.CFG_PSN_SEAT_VENT_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isDrvSeatMassSupport() {
        return CfcCarOption.CFG_DRV_SEAT_MASS_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isPsnSeatMassSupport() {
        return CfcCarOption.CFG_PSN_SEAT_MASS_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isXSportSupport() {
        return CfcCarOption.CFG_XSPORT_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isAllWheelKeyToICMSupport() {
        return CfcCarOption.CFG_ALL_WHEEL_KEY_TO_ICM_SUPPORT != 0;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public int getSpeakerCount() {
        return CfcCarOption.CFG_SPEAKER_VALUE;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public float getQuickMenuOpaquePos() {
        return CfcCarOption.CFG_QUICK_MENU_OPAQUE_POS_VALUE / 100.0f;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public int getQuickMenuTakeEffectDist() {
        return CfcCarOption.CFG_QUICK_MENU_TAKE_EFFECT_DIST_VALUE;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isEasyLoadSupport() {
        return CfcCarOption.CFG_AS_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isSteeringWheelSupport() {
        return CfcCarOption.CFC_VEHICLE_LEVEL_VALUE > 0;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public int getCfcVehicleLevel() {
        return CfcCarOption.CFC_VEHICLE_LEVEL_VALUE;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public int getConfigCode() {
        return CfcCarOption.CFG_CONFIG_CODE_VALUE;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isCarRecorderName(String ssid) {
        String recorderName = CfcCarOption.getConfigString("persist.sys.xp.recorder_ssid", "");
        String str = TAG;
        Logger.d(str, "CAR_RECORDER_NAME : " + recorderName + " ssid : " + ssid + " isMatched : " + ssid.matches(recorderName));
        return ssid.matches(recorderName);
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isSeatMemorySupport() {
        return CfcCarOption.CFG_MSMD_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isMSMPSupport() {
        return CfcCarOption.CFG_MSMP_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isMSBSupport() {
        return CfcCarOption.CFG_MSB_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isAVMSupport() {
        return CfcCarOption.CFG_AVM_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isSCUSupport() {
        return CfcCarOption.CFG_SCU_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isMRRSupport() {
        return CfcCarOption.CFG_MRR_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isSRR_RRSupport() {
        return CfcCarOption.CFG_SRR_RR_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isSRR_RLSupport() {
        return CfcCarOption.CFG_SRR_RL_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isSRR_FRSupport() {
        return CfcCarOption.CFG_SRR_FR_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isSRR_FLSupport() {
        return CfcCarOption.CFG_SRR_FL_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isRearMirrorFoldSupport() {
        return CfcCarOption.CFG_MIRROR_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isSrsSupport() {
        return CfcCarOption.CFG_SRS_SUPPORT;
    }
}
