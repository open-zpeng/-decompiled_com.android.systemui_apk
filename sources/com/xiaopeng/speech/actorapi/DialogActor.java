package com.xiaopeng.speech.actorapi;

import android.os.Parcel;
import com.xiaopeng.speech.actor.Actor;
/* loaded from: classes23.dex */
public class DialogActor extends Actor {
    public static final String KEY_STATE = "state";
    public static final String NAME = "DialogActor";
    public static final int STATE_CONTINUE = 4;
    public static final int STATE_END = 3;
    public static final int STATE_ERROR = 2;
    public static final int STATE_START = 1;
    private int mState;

    public DialogActor() {
        super(NAME);
    }

    public DialogActor(String name, Parcel in) {
        super(name, in);
        this.mState = in.readInt();
    }

    @Override // com.xiaopeng.speech.actor.Actor, android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(getState());
    }

    public int getState() {
        return this.mState;
    }

    public void setState(int state) {
        this.mState = state;
    }

    public String toString() {
        return "DialogActor{state='" + this.mState + "'}";
    }
}
