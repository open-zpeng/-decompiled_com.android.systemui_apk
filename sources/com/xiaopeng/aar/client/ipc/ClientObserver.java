package com.xiaopeng.aar.client.ipc;

import androidx.annotation.RestrictTo;
import com.xiaopeng.lib.apirouter.server.IServicePublisher;
import com.xiaopeng.lib.apirouter.server.Publish;
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes22.dex */
public class ClientObserver implements IServicePublisher {
    @Publish
    public void onReceived(int type, String appId, String module, String msgId, String data) {
        try {
            IpcImpl.get().onReceived(type, appId, module, msgId, data, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Publish
    public void onReceivedBlob(int type, String appId, String module, String msgId, String data, byte[] blob) {
        try {
            IpcImpl.get().onReceived(type, appId, module, msgId, data, blob);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
