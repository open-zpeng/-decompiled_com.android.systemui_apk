package com.xiaopeng.systemui.statusbar;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import com.xiaopeng.systemui.IView;
import com.xiaopeng.systemui.server.SystemBarRecord;
/* loaded from: classes24.dex */
public interface IStatusbarView extends IView {
    public static final int STATUS_BAR_ICON_STATUS_HIDE = 0;
    public static final int STATUS_BAR_ICON_STATUS_SHOW = 1;
    public static final int STATUS_BAR_ICON_STATUS_SHOW_TITLE = 2;

    void autoHideQuickMenu(int i);

    void onActivityChanged(String str);

    void onActivityChanged(String str, String str2, String str3);

    void onConfigurationChanged(Configuration configuration);

    @Override // com.xiaopeng.systemui.IView
    void onThemeChanged();

    void refreshTimeFormat(String str);

    void setAuthMode(boolean z);

    void setBackDefrostStatus(boolean z);

    void setBatteryLevel(int i);

    void setBatteryStatus(int i);

    void setBluetoothStatus(int i);

    void setChargingStatus(boolean z);

    void setChildMode(boolean z);

    void setChildSafetySeatStatus(int i);

    void setDashCamStatus(int i);

    void setDcPreWarmState(int i);

    void setDiagnosticMode(boolean z);

    void setDisableMode(int i);

    void setDistanceRemain(float f);

    void setDownloadStatus(int i);

    void setDriverAvatar(Bitmap bitmap);

    void setDriverStatus(boolean z);

    void setECallEnable(boolean z);

    void setFrontDefrostStatus(boolean z);

    void setIHBStatus(int i);

    void setLockStatus(boolean z);

    void setMicrophoneMuteStatus(boolean z);

    void setPsnWirelessChargeStatus(int i);

    void setRepairMode(boolean z);

    void setSeatHeatLevel(int i);

    void setSeatVentLevel(int i);

    void setSignalLevel(int i);

    void setSignalType(int i);

    void setSrsState(boolean z);

    void setStatusBarIcon(int i, SystemBarRecord systemBarRecord);

    void setUpgradeStatus(boolean z);

    void setUsbStatus(boolean z);

    void setVisibility(boolean z);

    void setWifiLevel(int i);

    void setWirelessChargeStatus(int i);

    void showDownloadIcon(boolean z);

    void showMiniProgramCloseBtn(boolean z);

    void showSignalIcon(boolean z);

    void showWifiConnectionAnim(boolean z, int i);

    void showWifiIcon(boolean z);
}
