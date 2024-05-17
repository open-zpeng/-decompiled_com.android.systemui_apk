package com.xiaopeng.systemui.speech.data;
/* loaded from: classes24.dex */
public class SpeechDataHint {
    private String relateText;
    private int soundArea;
    private String text;

    public SpeechDataHint() {
    }

    public SpeechDataHint(int soundArea) {
        this.soundArea = soundArea;
    }

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

    public String getRelateText() {
        return this.relateText;
    }

    public void setRelateText(String relateText) {
        this.relateText = relateText;
    }

    public String toString() {
        return "SpeechDataHint{soundArea=" + this.soundArea + ", text='" + this.text + "', relateText='" + this.relateText + "'}";
    }
}
