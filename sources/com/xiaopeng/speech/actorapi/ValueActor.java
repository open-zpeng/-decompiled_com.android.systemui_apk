package com.xiaopeng.speech.actorapi;

import android.os.Parcel;
import com.xiaopeng.speech.actor.Actor;
import com.xiaopeng.speech.common.bean.Value;
/* loaded from: classes23.dex */
public class ValueActor extends Actor {
    public static final String NAME = "ValueActor";
    protected String mEvent;
    protected Value mValue;

    public ValueActor(String event) {
        super(NAME);
        this.mEvent = "";
        this.mEvent = event;
    }

    public ValueActor(String name, Parcel in) {
        super(name, in);
        this.mEvent = "";
        this.mEvent = in.readString();
        this.mValue = (Value) in.readParcelable(Value.class.getClassLoader());
    }

    @Override // com.xiaopeng.speech.actor.Actor, android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.mEvent);
        dest.writeParcelable(this.mValue, 0);
    }

    public Value getValue() {
        return this.mValue;
    }

    public ValueActor setValue(Object value) {
        this.mValue = new Value(value);
        return this;
    }

    public String getEvent() {
        return this.mEvent;
    }

    public ValueActor setEvent(String event) {
        this.mEvent = event;
        return this;
    }
}
