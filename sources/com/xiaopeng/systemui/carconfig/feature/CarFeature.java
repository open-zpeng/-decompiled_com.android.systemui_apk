package com.xiaopeng.systemui.carconfig.feature;

import android.content.Context;
import android.content.pm.PackageInfo;
import com.android.systemui.R;
import com.xiaopeng.aar.utils.LogUtils;
import com.xiaopeng.systemui.carconfig.option.CfcCarOption;
import com.xiaopeng.systemui.carconfig.option.FeatureCarOption;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.util.FeatureOption;
/* loaded from: classes24.dex */
public class CarFeature implements IFeature {
    private final Context mContext = ContextUtils.getContext();

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean Navigation_isProcessRawTextSupport() {
        return FeatureOption.FO_DEVICE_INTERNATIONAL_ENABLED;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isValidWindSpeed(int speed) {
        return speed >= 0 && speed <= FeatureCarOption.FO_MAX_WIND_SPEED;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isOnlyOpenBackBoxSupport() {
        return FeatureCarOption.BACK_BOX_ONLY_OPEN;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isWindowLoadingDisable() {
        return FeatureCarOption.WINDOW_LOADING_DISABLE;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isAutoWiperTextSupport() {
        return FeatureCarOption.AUTO_WIPER_TEXT;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isAutopilotSupport() {
        int i = FeatureCarOption.FO_CHECK_FUNCTION_TYPE;
        return (i == 0 || i == 4) ? CfcCarOption.CFG_SCU_SUPPORT || CfcCarOption.CFG_XPU_SUPPORT : CfcCarOption.PROPERTY_CFC_VALUE == 2 || CfcCarOption.CFG_CONFIG_CODE_VALUE == 3;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean needSyncTemperature() {
        return FeatureCarOption.FO_SYNC_TEMPERATURE && !isAutopilotSupport();
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isOldAsr() {
        return FeatureCarOption.FO_SYSTEMUI_SPEECHUI_ASR_OLD;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isInnerQualitySupport() {
        int i = FeatureCarOption.FO_CHECK_FUNCTION_TYPE;
        if (i != 0) {
            if (i != 3) {
                return i != 4 ? CfcCarOption.CFG_CONFIG_CODE_VALUE != 1 : CfcCarOption.CFC_VEHICLE_LEVEL_VALUE > 1;
            }
            return true;
        }
        return CfcCarOption.CFG_PACKAGE_1_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isPurifyInAllSeries() {
        return FeatureCarOption.FO_PURIFY_IN_ALL_SERIES;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isLockSupport() {
        return FeatureCarOption.FO_LOCK_SUPPORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isAutoFanSpeedSupport() {
        return FeatureCarOption.FO_HAS_AUTO_FAN_SPEED;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isMeditationModeSupport() {
        return FeatureCarOption.FO_HAS_MEDITATION_MODE;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public int getWindLevel() {
        return FeatureCarOption.FO_WIND_LEVEL;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isSpeechInterActionSupport() {
        return FeatureCarOption.FO_SPEECH_INTERACTION;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isSecondaryWindowSupport() {
        return FeatureCarOption.FO_HAS_SECONDARY_WINDOW;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isMultiplayerVoiceSupport() {
        return FeatureCarOption.FO_SUPPORT_MULTIPLAYER_VOICE;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public int getSysUIDisplayType() {
        return FeatureCarOption.FO_SYSUI_DISPLAY_TYPE;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isDualChargingPort() {
        return FeatureCarOption.FO_IS_DUAL_CHARGING_PORT;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isPreInstallAppSupport() {
        return FeatureCarOption.FO_HAS_PRE_INSTALL_APP;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isOsdReduceSelfUse() {
        return true;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isSimpleAccountIcon() {
        return true;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isClockSupport() {
        return this.mContext.getResources().getBoolean(R.bool.cfg_clock_support);
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isDriverAccountSupport() {
        return this.mContext.getResources().getBoolean(R.bool.cfg_driver_account_support);
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isTrunkSupport() {
        return this.mContext.getResources().getBoolean(R.bool.cfg_trunk_support);
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isSmartDeviceSupport() {
        return this.mContext.getResources().getBoolean(R.bool.cfg_smart_device_support);
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isPassengerAccountSupport() {
        return this.mContext.getResources().getBoolean(R.bool.cfg_passenger_account_support);
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isDropQuickMenuSupport() {
        return this.mContext.getResources().getBoolean(R.bool.cfg_drop_quick_menu_support);
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isCarControlPreloadSupport() {
        return this.mContext.getResources().getBoolean(R.bool.cfg_car_control_preload_support);
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isRepairModeSupported() {
        return this.mContext.getResources().getBoolean(R.bool.cfg_repair_mode_support);
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isShowDefrostOnStatusBar() {
        return this.mContext.getResources().getBoolean(R.bool.show_defrost_on_status_bar);
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isShowDefrostBackOnStatusBar() {
        return this.mContext.getResources().getBoolean(R.bool.show_defrost_back_on_status_bar);
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isShowWifiAndSignalTogether() {
        return true;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isChineseVersion() {
        return this.mContext.getResources().getBoolean(R.bool.show_defrost_back_on_status_bar);
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isFaceAngleEnabled() {
        return FeatureCarOption.FO_IS_FACE_ANGLE_ENABLED;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isSeatVentSupportOnStatusBar() {
        if (FeatureOption.FO_DEVICE_INTERNATIONAL_ENABLED) {
            return CfcCarOption.CFG_SEAT_VENT_SUPPORT;
        }
        return false;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isSeatHeatSupportOnStatusBar() {
        if (FeatureOption.FO_DEVICE_INTERNATIONAL_ENABLED) {
            return CfcCarOption.CFG_SEAT_HEAT_SUPPORT;
        }
        return false;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isSecondScreenAppsInfoConfigSupport() {
        return FeatureCarOption.FO_SUPPORT_SECOND_SCREEN_APPS_INFO_CONFIG;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isDataProviderSupport() {
        return true;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isSupport360() {
        PackageInfo packageInfo;
        try {
            packageInfo = this.mContext.getPackageManager().getPackageInfo("com.xiaopeng.drivingimageassist", 0);
        } catch (Exception e) {
            LogUtils.i("CarFeature", "could not find package: com.xiaopeng.drivingimageassist");
            packageInfo = null;
        }
        return packageInfo != null;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isNewSpeechUI() {
        return true;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public int checkFunctionType() {
        return FeatureCarOption.FO_CHECK_FUNCTION_TYPE;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public int hasCIU() {
        return 1;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public int hasXPU() {
        return 0;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean showPopupWinOnStatusbarClick() {
        return true;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public int isAutoWiperSpeed() {
        return 1;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public int isAutoWiperSensitivity() {
        return 2;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isCarCheckSupport() {
        return !FeatureOption.FO_DEVICE_INTERNATIONAL_ENABLED;
    }

    @Override // com.xiaopeng.systemui.carconfig.feature.IFeature
    public boolean isSupportNewQs() {
        return FeatureCarOption.FO_SUPPORT_NEW_QS;
    }
}
