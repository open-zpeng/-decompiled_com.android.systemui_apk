package com.xiaopeng.speech.vui.exception;
/* loaded from: classes.dex */
public class VuiException extends RuntimeException {
    protected final VuiErrorCode errorCode;

    public VuiException() {
        super(VuiErrorCode.UNSPECIFIED.getDescription());
        this.errorCode = VuiErrorCode.UNSPECIFIED;
    }

    public VuiException(VuiErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

    public VuiErrorCode getErrorCode() {
        return this.errorCode;
    }
}
