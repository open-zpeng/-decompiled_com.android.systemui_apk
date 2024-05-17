package com.xiaopeng.speech.protocol.node.app;
/* loaded from: classes23.dex */
public abstract class AbsAppListener implements AppListener {
    @Override // com.xiaopeng.speech.protocol.node.app.AppListener
    public void onQuery(String event, String appName) {
    }

    @Override // com.xiaopeng.speech.protocol.node.app.AppListener
    public void onAppStoreOpen(String event, String appName) {
    }

    @Override // com.xiaopeng.speech.protocol.node.app.AppListener
    public void onAppOpen(String event, String appName) {
    }

    @Override // com.xiaopeng.speech.protocol.node.app.AppListener
    public void onAppPageOpen(String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.app.AppListener
    public void onKeyeventBack() {
    }

    @Override // com.xiaopeng.speech.protocol.node.app.AppListener
    public void onTriggerIntent(String skill, String task, String intent, String slots) {
    }

    @Override // com.xiaopeng.speech.protocol.node.app.AppListener
    public void onDebugOpen() {
    }

    @Override // com.xiaopeng.speech.protocol.node.app.AppListener
    public void onAppActive() {
    }

    @Override // com.xiaopeng.speech.protocol.node.app.AppListener
    public void onAiHomepageOpen() {
    }

    @Override // com.xiaopeng.speech.protocol.node.app.AppListener
    public void onAiHomepageClose() {
    }

    @Override // com.xiaopeng.speech.protocol.node.app.AppListener
    public void onAppLauncherExit() {
    }

    @Override // com.xiaopeng.speech.protocol.node.app.AppListener
    public void onStartPage(String packageName, String action, String extraData) {
    }

    @Override // com.xiaopeng.speech.protocol.node.app.AppListener
    public void onGuiSpeechDebugPage() {
    }

    @Override // com.xiaopeng.speech.protocol.node.app.AppListener
    public void onOpenYoukuSearch(String data) {
    }
}
