package com.xiaopeng.systemui.infoflow.message.data.bean;

import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;
/* loaded from: classes24.dex */
public final class Request {
    public static final int BACK_RESULT_BROADCAST = 2;
    public static final int BACK_RESULT_CALLBACK = 1;
    public static final int BACK_RESULT_END = 3;
    public static final int BACK_RESULT_IPCMESSAGE = 0;
    public static final int BACK_RESULT_LOGI = 3;
    public static final int BACK_RESULT_START = 0;
    @SerializedName("app_id")
    private String mAppId;
    @SerializedName("back_type")
    private int mBackType = 0;
    @SerializedName("content")
    private String mContent;
    @SerializedName("out_time")
    private int mOutTime;
    @SerializedName("request_id")
    private int mRequestId;
    @SerializedName("what")
    private int mWhat;

    public String getApp_id() {
        return this.mAppId;
    }

    public void setApp_id(String appId) {
        this.mAppId = appId;
    }

    public int getRequest_id() {
        return this.mRequestId;
    }

    public void setRequest_id(int requestId) {
        this.mRequestId = requestId;
    }

    public String getContent() {
        return this.mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public int getWhat() {
        return this.mWhat;
    }

    public void setWhat(int what) {
        this.mWhat = what;
    }

    public int getBack_type() {
        return this.mBackType;
    }

    public void setBack_type(int backType) {
        this.mBackType = backType;
    }

    public void setOut_time(int time) {
        this.mOutTime = time;
    }

    public int getOut_time() {
        return this.mOutTime;
    }

    public boolean isValid() {
        int i;
        return !TextUtils.isEmpty(this.mAppId) && !TextUtils.isEmpty(this.mContent) && (i = this.mBackType) >= 0 && i <= 3;
    }

    public String toString() {
        return "Request{app_id='" + this.mAppId + "', request_id=" + this.mRequestId + ", content='" + this.mContent + "', what=" + this.mWhat + ", back_type=" + this.mBackType + ",out_time=" + this.mOutTime + '}';
    }
}
