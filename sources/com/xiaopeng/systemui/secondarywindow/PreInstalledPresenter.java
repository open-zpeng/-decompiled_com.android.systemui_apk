package com.xiaopeng.systemui.secondarywindow;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import com.android.systemui.R;
import com.google.gson.reflect.TypeToken;
import com.xiaopeng.appstore.storeprovider.AssembleConstants;
import com.xiaopeng.appstore.storeprovider.RequestContinuation;
import com.xiaopeng.appstore.storeprovider.bean.AppGroupsResp;
import com.xiaopeng.speech.protocol.event.OOBEEvent;
import com.xiaopeng.systemui.AppDownloadPresenter;
import com.xiaopeng.systemui.IAppDownloadPresenter;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.controller.CarController;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.egg.utils.TimeUtils;
import com.xiaopeng.systemui.infoflow.message.util.SharedPreferenceUtil;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.systemui.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
/* loaded from: classes24.dex */
public class PreInstalledPresenter {
    private static final int MIN_CHECK_TIME = 60000;
    private static final String TAG = "PreInstalled";
    private IAppDownloadPresenter.AppCallBack mAppCallBack;
    private CarController.CarCallback mCarCallback;
    private Context mContext;
    private boolean mIgOff;
    private boolean mInit;
    private boolean mIsPrivacyReadyAgree;
    private List<String> mLabels;
    private long mLastCheckTime;
    private ContentObserver mOOBEObserver;
    private List<String> mPackages;
    private static final String KEY_PRIVACY_READY_AGREE = "xp_oobe_privacy_ready_agree";
    private static final Uri URI_PRIVACY_READY_AGREE = Settings.Global.getUriFor(KEY_PRIVACY_READY_AGREE);

    /* loaded from: classes24.dex */
    private static class SingleHolder {
        private static final PreInstalledPresenter sInstance = new PreInstalledPresenter();

        private SingleHolder() {
        }
    }

    public static PreInstalledPresenter get() {
        return SingleHolder.sInstance;
    }

    private PreInstalledPresenter() {
        this.mPackages = new CopyOnWriteArrayList();
        this.mLabels = new CopyOnWriteArrayList();
    }

    public void init(Context context) {
        if ((!CarModelsManager.getFeature().isPreInstallAppSupport() && Utils.isChineseLanguage()) || this.mInit) {
            return;
        }
        long time = System.currentTimeMillis();
        this.mInit = true;
        this.mContext = context.getApplicationContext();
        loadAppsInfo();
        monitorAppDownload();
        monitorOOBE();
        monitorIGOn();
        this.mIsPrivacyReadyAgree = isPrivacyReadyAgree();
        log(String.format("init time:%s  isPrivacyReadyAgree:%s ", Long.valueOf(System.currentTimeMillis() - time), Boolean.valueOf(this.mIsPrivacyReadyAgree)));
    }

    public void test() {
        doCheck("test");
    }

    public void testDoQuery() {
        doQuery();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPrivacyReadyAgree() {
        String state = Settings.Global.getString(this.mContext.getContentResolver(), KEY_PRIVACY_READY_AGREE);
        return OOBEEvent.STRING_TRUE.equals(state);
    }

    private void monitorOOBE() {
        if (this.mOOBEObserver != null) {
            return;
        }
        this.mOOBEObserver = new ContentObserver(new Handler()) { // from class: com.xiaopeng.systemui.secondarywindow.PreInstalledPresenter.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Uri uri) {
                int factorySwitchStatus = 0;
                try {
                    factorySwitchStatus = CarController.getInstance(PreInstalledPresenter.this.mContext).getCarServiceAdapter().getFactoryModeSwitchStatus();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Logger.d(PreInstalledPresenter.TAG, "factorySwitchStatus : " + factorySwitchStatus);
                if (factorySwitchStatus != 1) {
                    if (uri.equals(PreInstalledPresenter.URI_PRIVACY_READY_AGREE)) {
                        PreInstalledPresenter preInstalledPresenter = PreInstalledPresenter.this;
                        preInstalledPresenter.mIsPrivacyReadyAgree = preInstalledPresenter.isPrivacyReadyAgree();
                        PreInstalledPresenter preInstalledPresenter2 = PreInstalledPresenter.this;
                        preInstalledPresenter2.log("isPrivacyReadyAgree :" + PreInstalledPresenter.this.mIsPrivacyReadyAgree);
                        if (PreInstalledPresenter.this.mIsPrivacyReadyAgree) {
                            PreInstalledPresenter.this.doCheck("oobe changed");
                            return;
                        }
                        return;
                    }
                    return;
                }
                Logger.i(PreInstalledPresenter.TAG, "Factory Mode Switch is ON");
            }
        };
        this.mContext.getContentResolver().registerContentObserver(URI_PRIVACY_READY_AGREE, true, this.mOOBEObserver);
    }

    private void monitorIGOn() {
        if (this.mCarCallback != null) {
            return;
        }
        this.mCarCallback = new CarController.CarCallback() { // from class: com.xiaopeng.systemui.secondarywindow.PreInstalledPresenter.2
            @Override // com.xiaopeng.systemui.controller.CarController.CarCallback
            public void onCarControlChanged(int type, Object newValue) {
            }

            @Override // com.xiaopeng.systemui.controller.CarController.CarCallback
            public void onCarServiceChanged(int type, Object newValue) {
                if (3401 == type) {
                    try {
                        int igStatus = ((Integer) newValue).intValue();
                        if (igStatus == 0) {
                            PreInstalledPresenter.this.mIgOff = true;
                            PreInstalledPresenter.this.log("onCarServiceChanged IG_OFF");
                        } else if (igStatus == 1) {
                            PreInstalledPresenter preInstalledPresenter = PreInstalledPresenter.this;
                            preInstalledPresenter.log("onCarServiceChanged IG_ON mIgOff : " + PreInstalledPresenter.this.mIgOff);
                            if (PreInstalledPresenter.this.mIgOff && PreInstalledPresenter.this.mIsPrivacyReadyAgree) {
                                PreInstalledPresenter.this.mIgOff = false;
                                PreInstalledPresenter.this.doCheck("igon");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        CarController.getInstance(this.mContext).addCallback(this.mCarCallback);
    }

    private void monitorAppDownload() {
        if (this.mAppCallBack == null) {
            this.mAppCallBack = new IAppDownloadPresenter.AppCallBack() { // from class: com.xiaopeng.systemui.secondarywindow.PreInstalledPresenter.3
                @Override // com.xiaopeng.systemui.IAppDownloadPresenter.AppCallBack
                public void onAppInstalled(String pkgName) {
                    PreInstalledPresenter.this.saveInstalled(pkgName);
                }

                @Override // com.xiaopeng.systemui.IAppDownloadPresenter.AppCallBack
                public void onPreInstalledAppsInfo(AppGroupsResp resp) {
                    ArrayList<HashMap<String, String>> apps = new ArrayList<>();
                    if (resp.data != null && !resp.data.isEmpty() && resp.data.get(0) != null && resp.data.get(0).apps != null && !resp.data.get(0).apps.isEmpty()) {
                        for (AppGroupsResp.App item : resp.data.get(0).apps) {
                            HashMap<String, String> appInfo = new HashMap<>();
                            appInfo.put("app_name", item.app_name);
                            appInfo.put("package_name", item.package_name);
                            apps.add(appInfo);
                        }
                        Log.i(PreInstalledPresenter.TAG, "onPreInstalledAppsInfo: apps = " + GsonUtil.toJson(apps));
                        SharedPreferenceUtil.set(ContextUtils.getContext(), SharedPreferenceUtil.PREF_FILE_NAME, SharedPreferenceUtil.DATA_PRE_INSTALLED_APPS_INFO, GsonUtil.toJson(apps));
                        PreInstalledPresenter.this.loadAppsInfo();
                        PreInstalledPresenter.this.doDownload();
                    }
                }
            };
            AppDownloadPresenter.getInstance().addCallBack(this.mAppCallBack);
        }
    }

    private void doQuery() {
        if (Utils.isChineseLanguage()) {
            AppDownloadPresenter.getInstance().getConfigAppsInfo(1);
        } else {
            getConfigApp();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doDownload() {
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.secondarywindow.PreInstalledPresenter.4
            @Override // java.lang.Runnable
            public void run() {
                System.currentTimeMillis();
                int i = 0;
                while (i < PreInstalledPresenter.this.mPackages.size()) {
                    PreInstalledPresenter preInstalledPresenter = PreInstalledPresenter.this;
                    if (!preInstalledPresenter.isAlreadyInstalled((String) preInstalledPresenter.mPackages.get(i))) {
                        if (PackageHelper.isAppInstalled(PreInstalledPresenter.this.mContext, (String) PreInstalledPresenter.this.mPackages.get(i))) {
                            PreInstalledPresenter preInstalledPresenter2 = PreInstalledPresenter.this;
                            preInstalledPresenter2.saveInstalled((String) preInstalledPresenter2.mPackages.get(i));
                        } else {
                            PreInstalledPresenter preInstalledPresenter3 = PreInstalledPresenter.this;
                            preInstalledPresenter3.download((String) preInstalledPresenter3.mPackages.get(i), i < PreInstalledPresenter.this.mLabels.size() ? null : (String) PreInstalledPresenter.this.mLabels.get(i));
                        }
                    }
                    i++;
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadAppsInfo() {
        String AppsInfoJson = SharedPreferenceUtil.getString(ContextUtils.getContext(), SharedPreferenceUtil.PREF_FILE_NAME, SharedPreferenceUtil.DATA_PRE_INSTALLED_APPS_INFO, "");
        ArrayList<HashMap<String, String>> apps = (ArrayList) GsonUtil.fromJson(AppsInfoJson, new TypeToken<ArrayList<HashMap<String, String>>>() { // from class: com.xiaopeng.systemui.secondarywindow.PreInstalledPresenter.5
        }.getType());
        log("loadAppsInfo = " + apps);
        this.mLabels.clear();
        this.mPackages.clear();
        if (apps != null) {
            for (int i = 0; i < apps.size(); i++) {
                this.mLabels.add(apps.get(i).get("app_name"));
                this.mPackages.add(apps.get(i).get("package_name"));
            }
            return;
        }
        getConfigApp();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doCheck(String source) {
        if (!this.mInit) {
            log("doCheck must init source:" + source);
            return;
        }
        long time = System.currentTimeMillis();
        if (time - this.mLastCheckTime <= TimeUtils.TIME_ONE_MINUTE) {
            log("doCheck source:" + source + " time interval is too small-- : " + (time - this.mLastCheckTime));
            return;
        }
        this.mLastCheckTime = time;
        if (this.mLabels.size() == 0 || this.mPackages.size() == 0) {
            doQuery();
        } else {
            doDownload();
        }
        log("doCheck: mLabels = " + this.mLabels + "  mPackages = " + this.mPackages);
        log("doCheck source:" + source + " cast time " + (System.currentTimeMillis() - time));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void download(String pkg, String name) {
        log("download : " + pkg);
        RequestContinuation request = AppDownloadPresenter.getInstance().createRequest(pkg, name);
        request.setUseSystemUidDownload(true).putExtra(AssembleConstants.EXTRA_KEY_PARAMS_START_DOWNLOAD_SHOW_TOAST, false).putExtra(AssembleConstants.EXTRA_KEY_PARAMS_IGNORE_DOWNLOAD_LOCAL_CHECK, true);
        AppDownloadPresenter.getInstance().startDownloadApp(request.request(), pkg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveInstalled(String packageName) {
        for (String pkg : this.mPackages) {
            if (pkg.equals(packageName)) {
                Context context = this.mContext;
                SharedPreferenceUtil.set(context, SharedPreferenceUtil.PREF_FILE_NAME, "pre." + packageName, true);
                log(" saveInstalled : " + packageName);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isAlreadyInstalled(String pkg) {
        Context context = this.mContext;
        boolean isAlreadyInstalled = SharedPreferenceUtil.getBoolean(context, SharedPreferenceUtil.PREF_FILE_NAME, "pre." + pkg, false);
        if (isAlreadyInstalled) {
            log(pkg + " isAlreadyInstalled: " + isAlreadyInstalled);
        }
        return isAlreadyInstalled;
    }

    private void getConfigApp() {
        Log.d(TAG, "isChineseLanguage : " + Utils.isChineseLanguage());
        if (!Utils.isChineseLanguage()) {
            this.mLabels.clear();
            this.mPackages.clear();
            String[] labelArray = this.mContext.getResources().getStringArray(R.array.preinstallapp_label);
            String[] pkgArray = this.mContext.getResources().getStringArray(R.array.preinstallapp_pkg);
            Collections.addAll(this.mLabels, labelArray);
            Collections.addAll(this.mPackages, pkgArray);
            Logger.d(TAG, "Labels : " + this.mLabels + "  Pkg : " + this.mPackages);
            doDownload();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String msg) {
        Logger.i(TAG, msg + "  " + Thread.currentThread());
    }
}
