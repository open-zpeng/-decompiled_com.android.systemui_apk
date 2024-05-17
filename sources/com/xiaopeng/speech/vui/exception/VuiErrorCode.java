package com.xiaopeng.speech.vui.exception;
/* loaded from: classes.dex */
public enum VuiErrorCode {
    UNSPECIFIED(1000, "未知错误"),
    ID_REPEAT(100, "ID重复"),
    ID_ILLEGAL(101, "ID不合法"),
    ID_EMPTY(100, "ID为空"),
    UPDATE_OPERATE_ILLEGAL(100, "更新操作不合法");
    
    private int code;
    private String description;

    VuiErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static VuiErrorCode getByCode(int code) {
        VuiErrorCode[] values;
        for (VuiErrorCode value : values()) {
            if (code == value.getCode()) {
                return value;
            }
        }
        return UNSPECIFIED;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
