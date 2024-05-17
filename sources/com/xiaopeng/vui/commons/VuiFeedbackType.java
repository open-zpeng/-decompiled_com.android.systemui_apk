package com.xiaopeng.vui.commons;
/* loaded from: classes24.dex */
public enum VuiFeedbackType {
    SOUND("Sound"),
    TTS("Tts");
    
    private String type;

    VuiFeedbackType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
