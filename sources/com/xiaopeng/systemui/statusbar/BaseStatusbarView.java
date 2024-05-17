package com.xiaopeng.systemui.statusbar;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import com.xiaopeng.systemui.PresenterCenter;
/* loaded from: classes24.dex */
public abstract class BaseStatusbarView implements IStatusbarView {
    protected IStatusbarPresenter mPresenter = PresenterCenter.getInstance().getStatusbarPresenter();

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void autoHideQuickMenu(int i);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void onActivityChanged(String str, String str2, String str3);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void setAuthMode(boolean z);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void setBackDefrostStatus(boolean z);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void setBatteryLevel(int i);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void setBatteryStatus(int i);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void setBluetoothStatus(int i);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void setChargingStatus(boolean z);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void setChildSafetySeatStatus(int i);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void setDisableMode(int i);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void setDownloadStatus(int i);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void setDriverAvatar(Bitmap bitmap);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void setDriverStatus(boolean z);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void setFrontDefrostStatus(boolean z);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void setLockStatus(boolean z);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void setMicrophoneMuteStatus(boolean z);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void setRepairMode(boolean z);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void setSignalLevel(int i);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void setSignalType(int i);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void setUpgradeStatus(boolean z);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void setUsbStatus(boolean z);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void setWifiLevel(int i);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void setWirelessChargeStatus(int i);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void showDownloadIcon(boolean z);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void showMiniProgramCloseBtn(boolean z);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void showSignalIcon(boolean z);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void showWifiConnectionAnim(boolean z, int i);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public abstract void showWifiIcon(boolean z);

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public void onActivityChanged(String pkgName) {
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public void onConfigurationChanged(Configuration newConfig) {
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setDistanceRemain(float distanceRemain) {
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setSeatVentLevel(int level) {
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setSeatHeatLevel(int level) {
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setSrsState(boolean state) {
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public void refreshTimeFormat(String timeFormat) {
    }
}
