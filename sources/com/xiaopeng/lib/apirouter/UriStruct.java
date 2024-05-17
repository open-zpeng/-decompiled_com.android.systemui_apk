package com.xiaopeng.lib.apirouter;

import androidx.annotation.NonNull;
/* loaded from: classes22.dex */
public class UriStruct {
    public String applicationId;
    public String processTag;
    public String serviceName;

    @NonNull
    public String toString() {
        return this.applicationId + "." + this.serviceName;
    }
}
