package com.xiaopeng.aar.client.app;

import androidx.annotation.RestrictTo;
import com.xiaopeng.aar.client.ApiListener;
import com.xiaopeng.aar.client.ByteWrapper;
import com.xiaopeng.aar.client.ipc.IpcManager;
import com.xiaopeng.aar.utils.HandlerThreadHelper;
import com.xiaopeng.aar.utils.LogUtils;
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes22.dex */
class AppProcessorImpl implements AppProcessor {
    private static final String TAG = "AppPro";
    private static final String mDefaultString = "";
    private final HandlerThreadHelper mHandlerThreadHelper;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppProcessorImpl(String name) {
        this.mHandlerThreadHelper = new HandlerThreadHelper(name);
    }

    @Override // com.xiaopeng.aar.client.app.AppProcessor
    public String call(String appId, String module, String method, String param, byte[] blob) {
        String result = IpcManager.get().getIpc().call(appId, module, method, param, blob);
        return result == null ? "" : result;
    }

    @Override // com.xiaopeng.aar.client.app.AppProcessor
    public void onReceived(final ApiListener listener, final long receivedId, final String appId, final String module, final String msgId, final String data, final byte[] blob) {
        this.mHandlerThreadHelper.post(new Runnable() { // from class: com.xiaopeng.aar.client.app.-$$Lambda$AppProcessorImpl$kfdL2VEmJLDuaYCT8UexMDcBPvw
            @Override // java.lang.Runnable
            public final void run() {
                AppProcessorImpl.lambda$onReceived$0(ApiListener.this, appId, module, msgId, data, blob, receivedId);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$onReceived$0(ApiListener listener, String appId, String module, String msgId, String data, byte[] blob, long receivedId) {
        try {
            long time = System.currentTimeMillis();
            listener.onReceived(appId, module, msgId, data == null ? "" : data, blob == null ? null : new ByteWrapper(blob));
            long cast = System.currentTimeMillis() - time;
            LogUtils.i(TAG, String.format("onReceived post  cast:%s ,Id:%s, appId:%s ,module:%s ,msgId:%s", Long.valueOf(cast), Long.valueOf(receivedId), appId, module, msgId));
        } catch (Exception e) {
            LogUtils.e(TAG, "onReceived exception=" + e.getMessage());
        }
    }

    @Override // com.xiaopeng.aar.client.app.AppProcessor
    public void onServerStatus(final ApiListener listener, final String appId, final int status) {
        this.mHandlerThreadHelper.post(new Runnable() { // from class: com.xiaopeng.aar.client.app.-$$Lambda$AppProcessorImpl$k3LMRwxYwhzS7XQgl9PaFcooyHk
            @Override // java.lang.Runnable
            public final void run() {
                AppProcessorImpl.lambda$onServerStatus$1(ApiListener.this, appId, status);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$onServerStatus$1(ApiListener listener, String appId, int status) {
        try {
            listener.onServerStatus(appId, status);
        } catch (Exception e) {
            LogUtils.e(TAG, "onServerStatus exception=" + e.getMessage());
        }
    }
}
