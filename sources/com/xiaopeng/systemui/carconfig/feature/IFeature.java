package com.xiaopeng.systemui.carconfig.feature;
/* loaded from: classes24.dex */
public interface IFeature {
    boolean Navigation_isProcessRawTextSupport();

    int checkFunctionType();

    int getSysUIDisplayType();

    int getWindLevel();

    int hasCIU();

    int hasXPU();

    boolean isAutoFanSpeedSupport();

    int isAutoWiperSensitivity();

    int isAutoWiperSpeed();

    boolean isAutoWiperTextSupport();

    boolean isAutopilotSupport();

    boolean isCarCheckSupport();

    boolean isCarControlPreloadSupport();

    boolean isChineseVersion();

    boolean isClockSupport();

    boolean isDataProviderSupport();

    boolean isDriverAccountSupport();

    boolean isDropQuickMenuSupport();

    boolean isDualChargingPort();

    boolean isFaceAngleEnabled();

    boolean isInnerQualitySupport();

    boolean isLockSupport();

    boolean isMeditationModeSupport();

    boolean isMultiplayerVoiceSupport();

    boolean isNewSpeechUI();

    boolean isOldAsr();

    boolean isOnlyOpenBackBoxSupport();

    boolean isOsdReduceSelfUse();

    boolean isPassengerAccountSupport();

    boolean isPreInstallAppSupport();

    boolean isPurifyInAllSeries();

    boolean isRepairModeSupported();

    boolean isSeatHeatSupportOnStatusBar();

    boolean isSeatVentSupportOnStatusBar();

    boolean isSecondScreenAppsInfoConfigSupport();

    boolean isSecondaryWindowSupport();

    boolean isShowDefrostBackOnStatusBar();

    boolean isShowDefrostOnStatusBar();

    boolean isShowWifiAndSignalTogether();

    boolean isSimpleAccountIcon();

    boolean isSmartDeviceSupport();

    boolean isSpeechInterActionSupport();

    boolean isSupport360();

    boolean isSupportNewQs();

    boolean isTrunkSupport();

    boolean isValidWindSpeed(int i);

    boolean isWindowLoadingDisable();

    boolean needSyncTemperature();

    boolean showPopupWinOnStatusbarClick();
}
