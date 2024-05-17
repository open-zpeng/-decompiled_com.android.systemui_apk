package com.xiaopeng.systemui.navigationbar;

import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
import com.xiaopeng.systemui.utils.SystemUIMediatorUtil;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public class NavigationBar3DView implements INavigationBarView {
    private static final String TAG = "NavigationBar3DView";
    private int primaryAppList = -1;
    private int secondaryAppList = -1;

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void onActivityChanged(String packageName, String className, boolean isCarControlReady, boolean isAppListOpened, int appListSharedId) {
        if (appListSharedId == 0) {
            if (isAppListOpened == this.primaryAppList) {
                return;
            }
            this.primaryAppList = isAppListOpened ? 1 : 0;
        }
        if (appListSharedId == 1) {
            if (isAppListOpened == this.secondaryAppList) {
                return;
            }
            this.secondaryAppList = isAppListOpened ? 1 : 0;
        }
        Logger.d(TAG, "onActivityChanged, PackageName=" + packageName + " isAppListOpened =" + isAppListOpened + ", appListSharedId = " + appListSharedId);
        Map<String, Object> map = new HashMap<>();
        map.put("isAppListOpened", Boolean.valueOf(isAppListOpened));
        map.put("appListSharedId", Integer.valueOf(appListSharedId));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setAppListState", map);
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setQuickTemperature(float temperature) {
        Map<String, Object> map = new HashMap<>();
        map.put("temperature", Float.valueOf(temperature));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setQuickTemperature", map);
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setHvacInfo(HvacInfo hvacInfo) {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("hvacInfo", new JSONObject(GsonUtil.toJson(hvacInfo)));
            SystemUIMediatorUtil.systemUIMediatorApiRouterCallImportant("setHvacInfo", map);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void switchHvacDashboard(boolean showNavBar, boolean showQuick) {
        Map<String, Object> map = new HashMap<>();
        map.put("showNavBar", Boolean.valueOf(showNavBar));
        map.put("showQuick", Boolean.valueOf(showQuick));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("switchHvacDashboard", map);
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void onHvacPanelChanged(boolean focused) {
        Map<String, Object> map = new HashMap<>();
        map.put("focused", Boolean.valueOf(focused));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("onHvacPanelChanged", map);
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setDriverTemperature(float temperature) {
        Logger.d(TAG, "setDriverTemperature : " + temperature);
        Map<String, Object> map = new HashMap<>();
        map.put("temperature", Float.valueOf(temperature));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setDriverTemperature", map);
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setPassengerTemperature(float temperature) {
        Map<String, Object> map = new HashMap<>();
        map.put("temperature", Float.valueOf(temperature));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setPassengerTemperature", map);
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setInnerQuality(int quality, String qualityContent) {
        Map<String, Object> map = new HashMap<>();
        map.put("quality", Integer.valueOf(quality));
        map.put("qualityContent", qualityContent);
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setInnerQuality", map, false);
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setPurgeMode(boolean purgeMode, boolean isAuto, int quality) {
        Map<String, Object> map = new HashMap<>();
        map.put("purgeMode", Boolean.valueOf(purgeMode));
        map.put("isAuto", Boolean.valueOf(isAuto));
        map.put("quality", Integer.valueOf(quality));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setPurgeMode", map);
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setAutoDefog(boolean autoDefog) {
        Map<String, Object> map = new HashMap<>();
        map.put("autoDefog", Boolean.valueOf(autoDefog));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setAutoDefog", map);
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setPsnSeatHeatLevel(int heatLevel) {
        Map<String, Object> map = new HashMap<>();
        Logger.d(TAG, "setPsnSeatHeatLevel : " + heatLevel);
        map.put("heatLevel", Integer.valueOf(heatLevel));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setPsnSeatHeatLevel", map);
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setPsnSeatVentLevel(int ventLevel) {
        Map<String, Object> map = new HashMap<>();
        map.put("ventLevel", Integer.valueOf(ventLevel));
        Logger.d(TAG, "setPsnSeatVentLevel : " + ventLevel);
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setPsnSeatVentLevel", map);
    }
}
