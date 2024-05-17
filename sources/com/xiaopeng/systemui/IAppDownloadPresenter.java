package com.xiaopeng.systemui;

import com.xiaopeng.appstore.storeprovider.AssembleInfo;
import com.xiaopeng.appstore.storeprovider.EnqueueRequest;
import com.xiaopeng.appstore.storeprovider.RequestContinuation;
import com.xiaopeng.appstore.storeprovider.bean.AppGroupsResp;
import java.util.List;
/* loaded from: classes24.dex */
public interface IAppDownloadPresenter {
    void addCallBack(AppCallBack appCallBack);

    void cancelDownloadApp(String str);

    RequestContinuation createRequest(String str, String str2);

    List<AssembleInfo> getAppDownloadInfoList();

    List<String> getNewInstalledApp();

    void pauseDownloadApp(String str);

    void removeCallBack(AppCallBack appCallBack);

    void resumeDownloadApp(String str);

    void startDownloadApp(EnqueueRequest enqueueRequest, String str);

    void startDownloadApp(String str, String str2);

    /* loaded from: classes24.dex */
    public interface AppCallBack {
        default void onAppInstalled(String pkgName) {
        }

        default void onAppUnInstalled(String pkgName) {
        }

        default void onAppNewInstalledChanged(List<String> apps) {
        }

        default void onSecondScreenAppsInfo(AppGroupsResp resp) {
        }

        default void onPreInstalledAppsInfo(AppGroupsResp resp) {
        }
    }
}
