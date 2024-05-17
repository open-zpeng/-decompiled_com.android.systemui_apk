package com.xiaopeng.speech.protocol.query.appstore;

import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.annotation.QueryAnnotation;
import com.xiaopeng.speech.protocol.event.query.QueryAppAndAppletEvent;
/* loaded from: classes23.dex */
public class AppAndAppletQuery extends SpeechQuery<IAppAndAppletCaller> {
    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryAppAndAppletEvent.GUI_APPSHOP_PAGE_OPEN)
    public int onOpenAppshopPage(String event, String data) {
        return ((IAppAndAppletCaller) this.mQueryCaller).onOpenAppshopPage(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryAppAndAppletEvent.GUI_APPSHOP_PAGE_CLOSE)
    public int onCloseAppshopPage(String event, String data) {
        return ((IAppAndAppletCaller) this.mQueryCaller).onCloseAppshopPage();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryAppAndAppletEvent.GUI_APP_PAGE_OPEN)
    public int onOpenAppStoreMainPage(String event, String data) {
        return ((IAppAndAppletCaller) this.mQueryCaller).onOpenAppStoreMainPage(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryAppAndAppletEvent.GUI_APP_PAGE_CLOSE)
    public int onCloseAppStoreMainPage(String event, String data) {
        return ((IAppAndAppletCaller) this.mQueryCaller).onCloseAppStoreMainPage();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryAppAndAppletEvent.GUI_APPLET_PAGE_OPEN)
    public int onOpenAppletMainPage(String event, String data) {
        return ((IAppAndAppletCaller) this.mQueryCaller).onOpenAppletMainPage(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryAppAndAppletEvent.GUI_APPLET_PAGE_CLOSE)
    public int onCloseAppletMainPage(String event, String data) {
        return ((IAppAndAppletCaller) this.mQueryCaller).onCloseAppletMainPage();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryAppAndAppletEvent.GUI_APP_OPEN)
    public int getAppOpenStatus(String event, String data) {
        return ((IAppAndAppletCaller) this.mQueryCaller).getAppOpenStatus(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryAppAndAppletEvent.GUI_APP_CLOSE)
    public int getAppCloseStatus(String event, String data) {
        return ((IAppAndAppletCaller) this.mQueryCaller).getAppCloseStatus(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryAppAndAppletEvent.GUI_APPLET_OPEN)
    public int getAppletOpenStatus(String event, String data) {
        return ((IAppAndAppletCaller) this.mQueryCaller).getAppletOpenStatus(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryAppAndAppletEvent.GUI_APPLET_CLOSE)
    public int getAppletCloseStatus(String event, String data) {
        return ((IAppAndAppletCaller) this.mQueryCaller).getAppletCloseStatus(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryAppAndAppletEvent.GUI_CURRENT_CLOSE)
    public int getCurrentCloseStatus(String event, String data) {
        return ((IAppAndAppletCaller) this.mQueryCaller).getCurrentCloseStatus(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryAppAndAppletEvent.HAS_DIALOG_CLOSE_BYHAND)
    public boolean hasDialogCloseByHand(String event, String data) {
        return ((IAppAndAppletCaller) this.mQueryCaller).hasDialogCloseByHand();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryAppAndAppletEvent.GUI_APP_DOWNLOAD)
    public int getAppDownStatus(String event, String data) {
        return ((IAppAndAppletCaller) this.mQueryCaller).getAppDownStatus(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryAppAndAppletEvent.GUI_CURRENT_BACK)
    public int getCurrentBackStatus(String event, String data) {
        return ((IAppAndAppletCaller) this.mQueryCaller).getCurrentBackStatus(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryAppAndAppletEvent.GUI_VIDEO_APP_DEMAND)
    public int getVideoDemandStatus(String event, String data) {
        return ((IAppAndAppletCaller) this.mQueryCaller).getVideoDemandStatus(data);
    }
}
