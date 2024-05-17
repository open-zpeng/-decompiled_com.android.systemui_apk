package com.xiaopeng.speech.actorapi;

import android.os.Parcel;
import com.xiaopeng.speech.actor.Actor;
/* loaded from: classes23.dex */
public class TextActor extends Actor {
    public static final String NAME = "TextActor";
    private boolean mEof;
    private boolean mIsOutput;
    private String mText;

    public TextActor() {
        super(NAME);
        this.mIsOutput = false;
        this.mEof = false;
    }

    public TextActor(String name, Parcel in) {
        super(name, in);
        this.mIsOutput = false;
        this.mEof = false;
        this.mText = in.readString();
        this.mIsOutput = in.readByte() != 0;
        this.mEof = in.readByte() != 0;
    }

    public String getText() {
        return this.mText;
    }

    public void setText(String text) {
        this.mText = text;
    }

    public boolean isOutput() {
        return this.mIsOutput;
    }

    public void setIsOutput(boolean isOutput) {
        this.mIsOutput = isOutput;
    }

    public boolean isEof() {
        return this.mEof;
    }

    public void setEof(boolean eof) {
        this.mEof = eof;
    }

    @Override // com.xiaopeng.speech.actor.Actor, android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.mText);
        dest.writeByte(this.mIsOutput ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mEof ? (byte) 1 : (byte) 0);
    }
}
