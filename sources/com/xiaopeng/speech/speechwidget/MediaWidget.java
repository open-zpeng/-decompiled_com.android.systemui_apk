package com.xiaopeng.speech.speechwidget;
/* loaded from: classes23.dex */
public class MediaWidget extends ListWidget {
    public MediaWidget() {
        super("media");
    }

    @Override // com.xiaopeng.speech.speechwidget.ListWidget, com.xiaopeng.speech.speechwidget.SpeechWidget
    public MediaWidget fromJson(String data) {
        return (MediaWidget) super.fromJson(data);
    }
}
