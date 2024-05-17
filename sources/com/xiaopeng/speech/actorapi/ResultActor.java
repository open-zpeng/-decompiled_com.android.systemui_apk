package com.xiaopeng.speech.actorapi;

import android.os.Parcel;
import com.xiaopeng.speech.actor.Actor;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
/* loaded from: classes23.dex */
public class ResultActor extends Actor {
    public static final String NAME = "ResultActor";
    protected String mEvent;
    protected String mResultData;

    public ResultActor(String event) {
        super(NAME);
        this.mEvent = "";
        this.mEvent = event;
    }

    public ResultActor(String name, Parcel in) {
        super(name, in);
        this.mEvent = "";
        this.mEvent = in.readString();
        this.mResultData = in.readString();
    }

    @Override // com.xiaopeng.speech.actor.Actor, android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(getEvent());
        dest.writeString(this.mResultData);
    }

    public String getEvent() {
        return this.mEvent;
    }

    public ResultActor setResult(SpeechWidget speechWidget) {
        return setResult(speechWidget.toString());
    }

    public ResultActor setResult(String resultData) {
        this.mResultData = resultData;
        return this;
    }

    public String getResultData() {
        return this.mResultData;
    }
}
