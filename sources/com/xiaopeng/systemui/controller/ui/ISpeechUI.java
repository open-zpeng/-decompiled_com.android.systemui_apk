package com.xiaopeng.systemui.controller.ui;
/* loaded from: classes24.dex */
public interface ISpeechUI {

    /* loaded from: classes24.dex */
    public interface ISpeechUICallBack {
        void onSpeechUIEnableChanged(boolean z);
    }

    void addSpeechUICallBack(ISpeechUICallBack iSpeechUICallBack);

    boolean isSpeechUIEnable();

    void removeSpeechUICallBack(ISpeechUICallBack iSpeechUICallBack);
}
