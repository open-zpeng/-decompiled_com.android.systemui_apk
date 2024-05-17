package com.xiaopeng.speech.vui.model;
/* loaded from: classes.dex */
public enum VuiFeedbackCode {
    SUCCESS(1),
    FAIL(0);
    
    private int code;

    VuiFeedbackCode(int code) {
        this.code = code;
    }

    public int getFeedbackCode() {
        return this.code;
    }
}
