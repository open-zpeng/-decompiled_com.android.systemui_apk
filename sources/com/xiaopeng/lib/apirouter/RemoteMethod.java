package com.xiaopeng.lib.apirouter;

import android.util.Pair;
import java.util.List;
/* loaded from: classes22.dex */
public class RemoteMethod {
    private int mId;
    private String mMethodName;
    private List<Pair<String, String>> mParamsList;

    public RemoteMethod(String methodName, int id, List<Pair<String, String>> paramsList) {
        this.mMethodName = methodName;
        this.mId = id;
        this.mParamsList = paramsList;
    }

    public String getMethodName() {
        return this.mMethodName;
    }

    public int getId() {
        return this.mId;
    }

    public List<Pair<String, String>> getParamsList() {
        return this.mParamsList;
    }
}
