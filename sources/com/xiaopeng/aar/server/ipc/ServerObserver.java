package com.xiaopeng.aar.server.ipc;

import androidx.annotation.RestrictTo;
import com.xiaopeng.aar.utils.LogUtils;
import com.xiaopeng.lib.apirouter.server.IServicePublisher;
import com.xiaopeng.lib.apirouter.server.Publish;
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes22.dex */
public class ServerObserver implements IServicePublisher {
    private static final String TAG = "ServerOb";

    @Publish
    public String call(int type, String module, String method, String param) {
        try {
            LogUtils.d(TAG, String.format("call-- type:%s , module:%s ,method:%s ,param:%s", Integer.valueOf(type), module, method, LogUtils.stringLog(param)));
            return IpcServerImpl.get().onCall(type, module, method, param, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Publish
    public String callBlob(int type, String module, String method, String param, byte[] blob) {
        try {
            Object[] objArr = new Object[5];
            objArr[0] = Integer.valueOf(type);
            objArr[1] = module;
            objArr[2] = method;
            Object obj = "";
            objArr[3] = param == null ? "" : Integer.valueOf(param.length());
            if (blob != null) {
                obj = Integer.valueOf(blob.length);
            }
            objArr[4] = obj;
            LogUtils.d(TAG, String.format("callBlob-- type:%s , module:%s ,method:%s ,param:%s ,blob-length:%s", objArr));
            return IpcServerImpl.get().onCall(type, module, method, param, blob);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
