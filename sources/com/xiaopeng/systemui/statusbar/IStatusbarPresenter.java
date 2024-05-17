package com.xiaopeng.systemui.statusbar;
/* loaded from: classes24.dex */
public interface IStatusbarPresenter {
    void closeMiniProg();

    void destroyQuickMenuGuide();

    int getBatteryLevel();

    int getBatteryState();

    int getBluetoothState();

    int getDashCamStatus();

    int getDisableMode();

    int getDownloadStatus();

    String getDriverAvatar();

    int getFRWirelessChargeStatus();

    int getIHBStatus();

    boolean getIfSrsOn();

    int getIfSupportSeatHeatVent();

    int getPsnBluetoothState();

    int getSignalLevel();

    int getSignalType();

    String getTimeFormat();

    int getWifiLevel();

    int getWirelessChargeStatus();

    boolean hasDownload();

    boolean hasUsbDevice();

    boolean isAuthModeOn();

    boolean isBackDefrostOn();

    boolean isBatteryCharging();

    boolean isCenterLocked();

    boolean isChildModeOn();

    boolean isDiagnosticModeOn();

    boolean isDriverSeatActive();

    boolean isECallEnable();

    boolean isFrontDefrostOn();

    boolean isMicrophoneMute();

    boolean isRepairModeOn();

    boolean isUpgrading();

    boolean isWifiConnected();

    void onAuthModeClicked();

    void onBluetoothClicked();

    void onChildModeClicked();

    void onChildSafetySeatClicked();

    void onDashCamClicked();

    void onDefrostBackClicked();

    void onDefrostFrontClicked();

    void onDiagnosticModeClicked();

    void onDownloadClicked();

    void onDriverClicked();

    void onECallClicked();

    void onEnergyClicked();

    void onIHBClicked();

    void onLockClicked();

    void onLogoClicked();

    void onMicrophoneMuteClicked();

    void onNetworkClicked();

    void onRepairModeClicked();

    void onSeatVentHeatClicked();

    void onStatusBarIconClick(String str);

    void onUsbClicked();

    void showStatusBar(boolean z);
}
