package com.xiaopeng.speech.actorapi;

import android.os.Parcel;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import com.xiaopeng.speech.speechwidget.SupportWidget;
/* loaded from: classes23.dex */
public class SupportActor extends ResultActor {
    public static final String NAME = "SupportActor";
    public static final String TTS_DEFAULT_NOT_SUPPORT = "暂不支持此功能";
    public static final String TTS_DEFAULT_SUPPORT = "好的";

    public SupportActor(String event) {
        super(event);
        this.mName = NAME;
    }

    public SupportActor(String name, Parcel in) {
        super(name, in);
    }

    public SupportActor setResult(SupportWidget supportWidget) {
        return (SupportActor) super.setResult((SpeechWidget) supportWidget);
    }

    @Override // com.xiaopeng.speech.actorapi.ResultActor
    public SupportActor setResult(String resultData) {
        return (SupportActor) super.setResult(resultData);
    }

    public SupportWidget getWidget() {
        return new SupportWidget().fromJson(this.mResultData);
    }
}
