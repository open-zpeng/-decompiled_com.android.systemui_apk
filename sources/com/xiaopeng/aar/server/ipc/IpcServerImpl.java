package com.xiaopeng.aar.server.ipc;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import androidx.annotation.RestrictTo;
import com.xiaopeng.aar.client.ipc.ClientObserver;
import com.xiaopeng.aar.server.ipc.IpcServer;
import com.xiaopeng.aar.utils.LogUtils;
import com.xiaopeng.aar.utils.ThreadUtils;
import com.xiaopeng.lib.apirouter.ApiRouter;
import com.xiaopeng.lib.apirouter.server.ApiPublisherProvider;
import com.xiaopeng.lib.apirouter.server.IManifestHandler;
import com.xiaopeng.lib.apirouter.server.IManifestHelper;
import com.xiaopeng.lib.apirouter.server.ManifestHelper_aar;
import com.xiaopeng.speech.vui.constants.VuiConstants;
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes22.dex */
public class IpcServerImpl implements IpcServer {
    private static final String TAG = "IpcSer";
    private String mAppId;
    private String mClientName;
    private IpcServer.OnClientListener mOnClientListener;
    private String mTargetPkg;

    /* loaded from: classes22.dex */
    private static class SingletonHolder {
        private static final IpcServerImpl INSTANCE = new IpcServerImpl();

        private SingletonHolder() {
        }
    }

    public static IpcServerImpl get() {
        return SingletonHolder.INSTANCE;
    }

    private IpcServerImpl() {
        LogUtils.d(TAG, "IpcServerImpl");
        ApiPublisherProvider.addManifestHandler(new IManifestHandler() { // from class: com.xiaopeng.aar.server.ipc.-$$Lambda$IpcServerImpl$bALdnsAjHrdxqfBqpctflBIh184
            @Override // com.xiaopeng.lib.apirouter.server.IManifestHandler
            public final IManifestHelper[] getManifestHelpers() {
                return IpcServerImpl.lambda$new$0();
            }
        });
        this.mTargetPkg = "com.xiaopeng.napa";
        this.mClientName = ClientObserver.class.getSimpleName();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ IManifestHelper[] lambda$new$0() {
        return new IManifestHelper[]{new ManifestHelper_aar()};
    }

    @Override // com.xiaopeng.aar.server.ipc.IpcServer
    public void send(int type, String module, String msgId, String data, byte[] blob) {
        callApiRouter(type, module, msgId, data, blob);
    }

    @Override // com.xiaopeng.aar.server.ipc.IpcServer
    public void setOnClientListener(IpcServer.OnClientListener listener) {
        this.mOnClientListener = listener;
    }

    @Override // com.xiaopeng.aar.server.ipc.IpcServer
    public void keepAlive(Context context) {
        Intent intent = new Intent(context, KeepAliveService.class);
        context.startForegroundService(intent);
    }

    @Override // com.xiaopeng.aar.server.ipc.IpcServer
    public void setAppId(String appId) {
        if (this.mAppId != null) {
            return;
        }
        this.mAppId = appId;
        ThreadUtils.MULTI.post(new Runnable() { // from class: com.xiaopeng.aar.server.ipc.-$$Lambda$IpcServerImpl$I7z5-u1DDwaGpQtaAsXr_Otwc_c
            @Override // java.lang.Runnable
            public final void run() {
                IpcServerImpl.this.lambda$setAppId$1$IpcServerImpl();
            }
        });
    }

    public /* synthetic */ void lambda$setAppId$1$IpcServerImpl() {
        LogUtils.i(TAG, "send SERVER_NOTIFY_STARTED");
        send(202, this.mAppId, null, null, null);
    }

    @Override // com.xiaopeng.aar.server.ipc.IpcServer
    public void setTargetPackage(String pkgName) {
        LogUtils.i(TAG, "setTargetPackage " + pkgName);
        if (pkgName != null) {
            this.mTargetPkg = pkgName;
        }
    }

    @Override // com.xiaopeng.aar.server.ipc.IpcServer
    public String onCall(int type, String module, String method, String param, byte[] blob) {
        IpcServer.OnClientListener listener = this.mOnClientListener;
        if (listener != null) {
            return listener.onCall(type, module, method, param, blob);
        }
        return null;
    }

    private void callApiRouter(int type, String module, String msgId, String data, byte[] blob) {
        if (TextUtils.isEmpty(this.mAppId)) {
            LogUtils.e(TAG, "not apppid ");
            return;
        }
        try {
            Uri.Builder builder = new Uri.Builder();
            Uri.Builder builder2 = builder.authority(this.mTargetPkg + "." + this.mClientName).appendQueryParameter(VuiConstants.ELEMENT_TYPE, String.valueOf(type)).appendQueryParameter("appId", this.mAppId).appendQueryParameter("module", module).appendQueryParameter("msgId", msgId).appendQueryParameter("data", data);
            if (blob == null) {
                Uri targetUrl = builder2.path("onReceived").build();
                ApiRouter.route(targetUrl);
            } else {
                Uri targetUrl2 = builder2.path("onReceivedBlob").build();
                ApiRouter.route(targetUrl2, blob);
            }
        } catch (Exception e) {
            LogUtils.e(TAG, String.format("callApiRouter error type: %s , module: %s, msgId: %s, data:%s, blob%s", Integer.valueOf(type), module, msgId, data, LogUtils.bytesLog(blob)));
            e.printStackTrace();
        }
    }
}
