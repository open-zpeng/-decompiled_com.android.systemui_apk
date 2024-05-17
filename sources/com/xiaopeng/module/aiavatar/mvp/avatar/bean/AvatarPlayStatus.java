package com.xiaopeng.module.aiavatar.mvp.avatar.bean;

import com.google.gson.annotations.SerializedName;
import com.xiaopeng.speech.vui.constants.VuiConstants;
/* loaded from: classes23.dex */
public class AvatarPlayStatus {
    @SerializedName("eventId")
    private int eventId;
    @SerializedName(VuiConstants.SCENE_PACKAGE_NAME)
    private String packageName;
    @SerializedName("status")
    private int status;

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getEventId() {
        return this.eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String toString() {
        return "AvatarPlayStatus{packageName='" + this.packageName + "', eventId=" + this.eventId + ", status=" + this.status + '}';
    }
}
