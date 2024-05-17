package com.xiaopeng.aar.client.app;

import com.xiaopeng.aar.client.ApiListener;
/* loaded from: classes22.dex */
public class ServerTestProcessorImpl extends AppProcessorImpl {
    @Override // com.xiaopeng.aar.client.app.AppProcessorImpl, com.xiaopeng.aar.client.app.AppProcessor
    public /* bridge */ /* synthetic */ void onReceived(ApiListener apiListener, long j, String str, String str2, String str3, String str4, byte[] bArr) {
        super.onReceived(apiListener, j, str, str2, str3, str4, bArr);
    }

    @Override // com.xiaopeng.aar.client.app.AppProcessorImpl, com.xiaopeng.aar.client.app.AppProcessor
    public /* bridge */ /* synthetic */ void onServerStatus(ApiListener apiListener, String str, int i) {
        super.onServerStatus(apiListener, str, i);
    }

    ServerTestProcessorImpl(String name) {
        super(name);
    }

    @Override // com.xiaopeng.aar.client.app.AppProcessorImpl, com.xiaopeng.aar.client.app.AppProcessor
    public String call(String appId, String module, String method, String param, byte[] blob) {
        return super.call(appId, module, method, param, blob);
    }
}
