package com.xiaopeng.systemui.infoflow.message.data.bean;

import androidx.annotation.NonNull;
/* loaded from: classes24.dex */
public class Action {
    public String actionImg;
    public String actionName;
    public int actionType;
    public Request datas;

    @NonNull
    public String toString() {
        return String.format("actionType= %d && actionName = %s", Integer.valueOf(this.actionType), this.actionName);
    }
}
