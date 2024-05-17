package com.xiaopeng.systemui.secondarywindow;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.appstore.storeprovider.AssembleInfo;
import com.xiaopeng.appstore.storeprovider.AssembleResult;
import com.xiaopeng.appstore.storeprovider.bean.AppGroupsResp;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.systemui.AppDownloadPresenter;
import com.xiaopeng.systemui.IAppDownloadPresenter;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.controller.CarController;
import com.xiaopeng.systemui.infoflow.message.util.SharedPreferenceUtil;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
import com.xiaopeng.systemui.utils.SystemUIMediatorUtil;
import com.xiaopeng.systemui.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public class SecondaryWindow3DView implements ISecondaryWindowView, IAppDownloadPresenter.AppCallBack {
    private static final String TAG = "SecondaryWindow3DView";
    private String mInstalledApp;
    private final String[] mPackages = ContextUtils.getContext().getResources().getStringArray(R.array.preinstallapp_pkg);

    public SecondaryWindow3DView() {
        AppDownloadPresenter.getInstance().addCallBack(this);
        if (CarModelsManager.getFeature().isSecondScreenAppsInfoConfigSupport()) {
            setSecondScreenAppsInfo();
        }
    }

    private void setSecondScreenAppsInfo() {
        CarController.getInstance(ContextUtils.getContext()).addCallback(new CarController.CarCallback() { // from class: com.xiaopeng.systemui.secondarywindow.SecondaryWindow3DView.1
            @Override // com.xiaopeng.systemui.controller.CarController.CarCallback
            public void onCarControlChanged(int type, Object newValue) {
            }

            @Override // com.xiaopeng.systemui.controller.CarController.CarCallback
            public void onCarServiceChanged(int type, Object newValue) {
                if (3401 == type) {
                    int igStatus = ((Integer) newValue).intValue();
                    if (igStatus == 0) {
                        Logger.d(SecondaryWindow3DView.TAG, "onCarServiceChanged: IG_OFF");
                    } else if (igStatus == 1) {
                        Logger.d(SecondaryWindow3DView.TAG, "onCarServiceChanged: IG_ON");
                        AppDownloadPresenter.getInstance().getConfigAppsInfo(0);
                    }
                }
            }
        });
    }

    @Override // com.xiaopeng.systemui.secondarywindow.ISecondaryWindowView
    public void notifyDownloadInfo(AssembleInfo assembleInfo) {
        Logger.d(TAG, "notifyDownloadInfo");
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("assembleInfo", new JSONObject(com.alibaba.fastjson.JSONObject.toJSONString(assembleInfo)));
            SystemUIMediatorUtil.systemUIMediatorApiRouterCall("notifyDownloadInfo", map);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.systemui.secondarywindow.ISecondaryWindowView
    public void dispatchConfigurationChanged(Configuration newConfig) {
    }

    @Override // com.xiaopeng.systemui.secondarywindow.ISecondaryWindowView
    public void onActivityChanged(String pkgName) {
    }

    @Override // com.xiaopeng.systemui.secondarywindow.ISecondaryWindowView
    public void setPsnVolume(int volume) {
        Map<String, Object> map = new HashMap<>();
        map.put("volume", Integer.valueOf(volume));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setPsnVolume", map);
    }

    @Override // com.xiaopeng.systemui.secondarywindow.ISecondaryWindowView
    public void notifyUninstallResult(String packageName, int returnCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("pkgName", packageName);
        map.put("resultCode", Integer.valueOf(returnCode));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("notifyUninstallResult", map);
    }

    @Override // com.xiaopeng.systemui.secondarywindow.ISecondaryWindowView
    public void setPsnBluetoothState(int state) {
        Logger.d(TAG, "setPsnBluetoothState : " + state);
        Map<String, Object> map = new HashMap<>();
        map.put("state", Integer.valueOf(state));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setBluetoothHeadset", map);
    }

    @Override // com.xiaopeng.systemui.secondarywindow.ISecondaryWindowView
    public void notifyDownloadResult(String pkgName, AssembleResult assembleResult) {
        Logger.d(TAG, "notifyDownloadResult");
        Map<String, Object> map = new HashMap<>();
        if (assembleResult == null) {
            assembleResult = AssembleResult.createFail(AssembleResult.ERROR_EXECUTE, "Error execute");
        }
        try {
            JSONObject jsonResult = new JSONObject(com.alibaba.fastjson.JSONObject.toJSONString(assembleResult));
            jsonResult.put(VuiConstants.SCENE_PACKAGE_NAME, pkgName);
            map.put("assembleResult", jsonResult);
            SystemUIMediatorUtil.systemUIMediatorApiRouterCall("notifyDownloadResult", map);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.systemui.secondarywindow.ISecondaryWindowView
    public List<String> getNewInstalledApp() {
        List<String> list = AppDownloadPresenter.getInstance().getNewInstalledApp();
        return filteredNewInstalledApp(list);
    }

    private List<String> filteredNewInstalledApp(List<String> list) {
        String[] strArr;
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<String> filteredList = new ArrayList<>();
        for (String pkg : this.mPackages) {
            if (list.contains(pkg)) {
                filteredList.add(pkg);
            }
        }
        return filteredList;
    }

    @Override // com.xiaopeng.systemui.IAppDownloadPresenter.AppCallBack
    public void onAppNewInstalledChanged(List<String> apps) {
        List<String> filteredList = filteredNewInstalledApp(apps);
        String result = Utils.listToString(filteredList, NavigationBarInflaterView.GRAVITY_SEPARATOR);
        if ((result == null && this.mInstalledApp == null) || (result != null && result.equals(this.mInstalledApp))) {
            Logger.d(TAG, "onAppNewInstalledChanged-sec not changed！！ : " + this.mInstalledApp + " ,new: " + result);
            return;
        }
        Logger.d(TAG, "onAppNewInstalledChanged-sec : " + this.mInstalledApp + " ,new: " + result);
        this.mInstalledApp = result;
        Map<String, Object> map = new HashMap<>();
        map.put("data", result == null ? "" : result);
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("onAppNewInstalledChanged", map);
    }

    @Override // com.xiaopeng.systemui.IAppDownloadPresenter.AppCallBack
    public void onSecondScreenAppsInfo(AppGroupsResp resp) {
        ArrayList<HashMap<String, String>> apps = new ArrayList<>();
        if (resp.data != null && !resp.data.isEmpty() && resp.data.get(0) != null && resp.data.get(0).apps != null && !resp.data.get(0).apps.isEmpty()) {
            for (AppGroupsResp.App item : resp.data.get(0).apps) {
                HashMap<String, String> appInfo = new HashMap<>();
                appInfo.put("app_name", item.app_name);
                appInfo.put("package_name", item.package_name);
                appInfo.put("photo_url", item.photo_url);
                apps.add(appInfo);
            }
            try {
                JSONArray jsonData = new JSONArray(GsonUtil.toJson(apps));
                Logger.i(TAG, "onSecondScreenAppsInfo: apps = " + jsonData);
                SharedPreferenceUtil.set(ContextUtils.getContext(), SharedPreferenceUtil.PREF_FILE_NAME, SharedPreferenceUtil.DATA_SECOND_SCREEN_APPS_INFO, jsonData.toString());
                HashMap<String, Object> map = new HashMap<>();
                map.put("apps", jsonData);
                SystemUIMediatorUtil.systemUIMediatorApiRouterCall("onSecondScreenAppsInfo", map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override // com.xiaopeng.systemui.secondarywindow.ISecondaryWindowView
    public String getSecondScreenAppsInfo() {
        SharedPreferences sp = ContextUtils.getContext().getSharedPreferences("SecondaryScreenApps", 0);
        String info = sp.getString("apps", "");
        Logger.i(TAG, "getSecondScreenAppsInfo: info = " + info);
        return info;
    }
}
