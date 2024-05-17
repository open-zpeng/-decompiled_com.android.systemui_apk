package com.xiaopeng.systemui.statusbar;

import android.graphics.Bitmap;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.systemui.controller.AccountController;
import com.xiaopeng.systemui.infoflow.util.ImageUtil;
import com.xiaopeng.systemui.server.SystemBarRecord;
import com.xiaopeng.systemui.utils.SystemUIMediatorUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
/* loaded from: classes24.dex */
public class Statusbar3DView extends BaseStatusbarView {
    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void showMiniProgramCloseBtn(boolean show) {
        Map<String, Object> map = new HashMap<>();
        map.put("show", Boolean.valueOf(show));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("showMiniProgramCloseBtn", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void autoHideQuickMenu(int screenIndex) {
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setDriverAvatar(Bitmap avatar) {
        Map<String, Object> map = new HashMap<>();
        map.put(AccountController.ACCOUNT_AVATAR, ImageUtil.getBase64String(avatar));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setDriverAvatar", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void onActivityChanged(String topPackage, String primaryTopPackage, String secondTopPackage) {
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setLockStatus(boolean status) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", Boolean.valueOf(status));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setLockStatus", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setWirelessChargeStatus(int status) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", Integer.valueOf(status));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setWirelessChargeStatus", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setPsnWirelessChargeStatus(int status) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", Integer.valueOf(status));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setPsnWirelessChargeStatus", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setFrontDefrostStatus(boolean on) {
        Map<String, Object> map = new HashMap<>();
        map.put("on", Boolean.valueOf(on));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setFrontDefrostStatus", map, false);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setBackDefrostStatus(boolean on) {
        Map<String, Object> map = new HashMap<>();
        map.put("on", Boolean.valueOf(on));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setBackDefrostStatus", map, false);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setMicrophoneMuteStatus(boolean status) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", Boolean.valueOf(status));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setMicrophoneMuteStatus", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setAuthMode(boolean status) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", Boolean.valueOf(status));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setAuthMode", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setDisableMode(int status) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", Integer.valueOf(status));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setDisableMode", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setRepairMode(boolean status) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", Boolean.valueOf(status));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setRepairMode", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setUpgradeStatus(boolean show) {
        Map<String, Object> map = new HashMap<>();
        map.put("show", Boolean.valueOf(show));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setUpgradeStatus", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setWifiLevel(int level) {
        Map<String, Object> map = new HashMap<>();
        map.put("level", Integer.valueOf(level));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setWifiLevel", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setSignalLevel(int level) {
        Map<String, Object> map = new HashMap<>();
        map.put("level", Integer.valueOf(level));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setSignalLevel", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void showWifiIcon(boolean show) {
        Map<String, Object> map = new HashMap<>();
        map.put("show", Boolean.valueOf(show));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("showWifiIcon", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void showSignalIcon(boolean show) {
        Map<String, Object> map = new HashMap<>();
        map.put("show", Boolean.valueOf(show));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("showSignalIcon", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setSignalType(int type) {
        Map<String, Object> map = new HashMap<>();
        map.put(VuiConstants.ELEMENT_TYPE, Integer.valueOf(type));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setSignalType", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void showWifiConnectionAnim(boolean show, int level) {
        Map<String, Object> map = new HashMap<>();
        map.put("show", Boolean.valueOf(show));
        map.put("level", Integer.valueOf(level));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("showWifiConnectionAnim", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setUsbStatus(boolean hasUsbDevice) {
        Map<String, Object> map = new HashMap<>();
        map.put("hasUsbDevice", Boolean.valueOf(hasUsbDevice));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setUsbStatus", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setChargingStatus(boolean charging) {
        Map<String, Object> map = new HashMap<>();
        map.put("charging", Boolean.valueOf(charging));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setChargingStatus", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setBatteryStatus(int state) {
        Map<String, Object> map = new HashMap<>();
        map.put("state", Integer.valueOf(state));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setBatteryStatus", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setBatteryLevel(int level) {
        Map<String, Object> map = new HashMap<>();
        map.put("level", Integer.valueOf(level));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setBatteryLevel", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setDriverStatus(boolean hasPeople) {
        Map<String, Object> map = new HashMap<>();
        map.put("hasPeople", Boolean.valueOf(hasPeople));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setDriverStatus", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setBluetoothStatus(int state) {
        Map<String, Object> map = new HashMap<>();
        map.put("state", Integer.valueOf(state));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setBluetoothStatus", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void showDownloadIcon(boolean hasDownload) {
        Map<String, Object> map = new HashMap<>();
        map.put("hasDownload", Boolean.valueOf(hasDownload));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("showDownloadIcon", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setDownloadStatus(int downloadStatus) {
        Map<String, Object> map = new HashMap<>();
        map.put("downloadStatus", Integer.valueOf(downloadStatus));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setDownloadStatus", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setChildSafetySeatStatus(int status) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", Integer.valueOf(status));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setChildSafetySeatStatus", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setDcPreWarmState(int dcPreWarmState) {
        Map<String, Object> map = new HashMap<>();
        map.put("dcPreWarmState", Integer.valueOf(dcPreWarmState));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setDcPreWarmState", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setChildMode(boolean on) {
        Map<String, Object> map = new HashMap<>();
        map.put("on", Boolean.valueOf(on));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setChildMode", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setVisibility(boolean visible) {
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setDistanceRemain(float distanceRemain) {
        Map<String, Object> map = new HashMap<>();
        map.put("distanceRemain", Float.valueOf(distanceRemain));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setDistanceRemain", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView, com.xiaopeng.systemui.IView
    public void onThemeChanged() {
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setDiagnosticMode(boolean on) {
        Map<String, Object> map = new HashMap<>();
        map.put("on", Boolean.valueOf(on));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setDiagnosticMode", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setSrsState(boolean state) {
        Map<String, Object> map = new HashMap<>();
        map.put("state", Boolean.valueOf(state));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setSrsState", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setECallEnable(boolean enable) {
        Map<String, Object> map = new HashMap<>();
        map.put("enable", Boolean.valueOf(enable));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setECallEnable", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void refreshTimeFormat(String timeFormat) {
        Map<String, Object> map = new HashMap<>();
        map.put("timeFormat", timeFormat);
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("refreshTimeFormat", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setIHBStatus(int status) {
        Map<String, Object> map = new HashMap<>();
        map.put(VuiConstants.ELEMENT_VALUE, Integer.valueOf(status));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setIHBStatus", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setDashCamStatus(int status) {
        Map<String, Object> map = new HashMap<>();
        map.put(VuiConstants.ELEMENT_VALUE, Integer.valueOf(status));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setDashCamStatus", map);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setStatusBarIcon(int status, SystemBarRecord item) {
        if (item == null || item.getBar() == null) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("status", Integer.valueOf(status));
        map.put(VuiConstants.ELEMENT_TYPE, Integer.valueOf(item.getBar().getType()));
        map.put("key", item.getBarKey());
        map.put(SpeechWidget.WIDGET_TITLE, item.getBar().getTitle());
        map.put(SpeechWidget.WIDGET_EXTRA, Objects.requireNonNull(item.getBar().getExtras().get("state")));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setStatusBarIcon", map);
    }
}
