package com.xiaopeng.speech.protocol.query.appstore;

import com.xiaopeng.speech.IQueryCaller;
/* loaded from: classes23.dex */
public interface IAppAndAppletCaller extends IQueryCaller {
    int getAppCloseStatus(String str);

    int getAppOpenStatus(String str);

    int getAppletCloseStatus(String str);

    int getAppletOpenStatus(String str);

    int getCurrentCloseStatus(String str);

    boolean hasDialogCloseByHand();

    int onCloseAppStoreMainPage();

    int onCloseAppletMainPage();

    int onCloseAppshopPage();

    int onOpenAppStoreMainPage(String str);

    int onOpenAppletMainPage(String str);

    int onOpenAppshopPage(String str);

    default int getAppDownStatus(String paramString) {
        return -1;
    }

    default int getCurrentBackStatus(String data) {
        return -1;
    }

    default int getVideoDemandStatus(String data) {
        return -1;
    }
}
