package com.xiaopeng.speech.speechwidget;
/* loaded from: classes23.dex */
public class TextWidget extends SpeechWidget {
    public TextWidget() {
        super("text");
    }

    public TextWidget setText(String text) {
        return (TextWidget) super.addContent("text", text);
    }

    public String getText() {
        return getContent("text");
    }
}
