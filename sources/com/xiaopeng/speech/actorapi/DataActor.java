package com.xiaopeng.speech.actorapi;

import android.os.Parcel;
import com.xiaopeng.speech.actor.Actor;
/* loaded from: classes23.dex */
public class DataActor extends Actor {
    public static final String NAME = "DataActor";
    protected String mEvent;
    protected boolean mIsSupport;
    protected String mResultData;

    public DataActor(String event) {
        super(NAME);
        this.mEvent = "";
        this.mIsSupport = true;
        this.mEvent = event;
    }

    public DataActor(String name, Parcel in) {
        super(name, in);
        this.mEvent = "";
        this.mIsSupport = true;
        this.mEvent = in.readString();
        this.mResultData = in.readString();
        this.mIsSupport = in.readByte() != 0;
    }

    @Override // com.xiaopeng.speech.actor.Actor, android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(getEvent());
        dest.writeString(this.mResultData);
        dest.writeByte(this.mIsSupport ? (byte) 1 : (byte) 0);
    }

    public String getEvent() {
        return this.mEvent;
    }

    public DataActor setResult(String resultData) {
        this.mResultData = resultData;
        return this;
    }

    public String getResultData() {
        return this.mResultData;
    }

    public boolean isSupport() {
        return this.mIsSupport;
    }

    public DataActor setSupport(boolean support) {
        this.mIsSupport = support;
        return this;
    }

    public String toString() {
        return "DataActor{mEvent='" + this.mEvent + "', mResultData='" + this.mResultData + "', mIsSupport=" + this.mIsSupport + '}';
    }
}
