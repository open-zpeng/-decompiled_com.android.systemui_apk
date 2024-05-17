package com.xiaopeng.systemui.speech.data;
/* loaded from: classes24.dex */
public class SpeechDataInput {
    private boolean invalid;
    private int soundArea;
    private String text;

    public int getSoundArea() {
        return this.soundArea;
    }

    public void setSoundArea(int soundArea) {
        this.soundArea = soundArea;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isInvalid() {
        return this.invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    public String toString() {
        return "SpeechDataInput{soundArea=" + this.soundArea + ", text='" + this.text + "', invalid=" + this.invalid + '}';
    }
}
