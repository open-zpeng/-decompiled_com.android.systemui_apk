package com.xiaopeng.aar.client;

import com.xiaopeng.aar.client.app.AppProcessor;
import com.xiaopeng.aar.client.app.ProcessorManager;
import com.xiaopeng.aar.client.ipc.Ipc;
import com.xiaopeng.aar.client.ipc.IpcManager;
import com.xiaopeng.aar.utils.LogUtils;
import com.xiaopeng.aar.utils.Utils;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import kotlin.jvm.internal.LongCompanionObject;
/* loaded from: classes22.dex */
public class ApiManager {
    private static final String TAG = "ApiMg";
    private static final int THREAD_COUNT = 5;
    private ApiListener mApiListener;
    private final ExecutorService mAsyncExecutor;
    private long mAsyncRequestId;
    private long mReceivedId;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes22.dex */
    public static class SingletonHolder {
        private static final ApiManager INSTANCE = new ApiManager();

        private SingletonHolder() {
        }
    }

    public static ApiManager get() {
        return SingletonHolder.INSTANCE;
    }

    private ApiManager() {
        LogUtils.setLogTag("sdkc-");
        LogUtils.i(TAG, "init ,0.0.17_HEAD_029542b_2022/08/19 09:58:15");
        IpcManager.get().init();
        IpcManager.get().getIpc().setOnServerListener(new Ipc.OnServerListener() { // from class: com.xiaopeng.aar.client.-$$Lambda$ApiManager$-gOOPO4rfghQ8cRFEbXLMlvdE8A
            @Override // com.xiaopeng.aar.client.ipc.Ipc.OnServerListener
            public final void onEvent(int i, String str, String str2, String str3, String str4, byte[] bArr) {
                ApiManager.this.onReceived(i, str, str2, str3, str4, bArr);
            }
        });
        IpcManager.get().getIpc().addOnServerStatusListener(new Ipc.OnServerStatusListener() { // from class: com.xiaopeng.aar.client.-$$Lambda$ApiManager$ngtNGnc9UiTFfXPTkCIgrjeKLxE
            @Override // com.xiaopeng.aar.client.ipc.Ipc.OnServerStatusListener
            public final void onServerStatus(String str, int i) {
                ApiManager.this.lambda$new$0$ApiManager(str, i);
            }
        });
        RunningConfig.get().init();
        this.mAsyncExecutor = Executors.newFixedThreadPool(5);
    }

    public /* synthetic */ void lambda$new$0$ApiManager(String appId, int status) {
        ApiListener listener = this.mApiListener;
        if (listener == null) {
            LogUtils.e(TAG, "onServerStatus listener is null");
            return;
        }
        AppProcessor app = getAppProcessor(appId);
        if (app == null) {
            LogUtils.e(TAG, String.format("onServerStatus--not app!!!!   appId:%s ", appId));
        } else {
            app.onServerStatus(listener, appId, status);
        }
    }

    public void setLogLevel(int level) {
        LogUtils.setLogLevel(level);
    }

    public void setMock(boolean enable) {
        if (!Utils.isUserRelease()) {
            if (enable) {
                MockTest.get().init();
            } else {
                MockTest.get().release();
            }
        }
    }

    public void setReTrySubscribeEnable(String appId, boolean enable) {
        SubscribeManager.get().setReTryEnable(appId, enable);
    }

    public synchronized void setApiListener(ApiListener apiListener) {
        this.mApiListener = apiListener;
    }

    public void subscribe(String appId, String module, String subscriber) {
        LogUtils.d(TAG, String.format("subscribe-- appId:%s ,module:%s ,subscriber:%s", appId, module, subscriber));
        SubscribeManager.get().subscribe(appId, module, subscriber);
    }

    public void unSubscribe(String appId, String module, String subscriber) {
        LogUtils.d(TAG, String.format("unSubscribe-- appId:%s ,module:%s ,subscriber:%s", appId, module, subscriber));
        SubscribeManager.get().unSubscribe(appId, module, subscriber);
    }

    public String call(String appId, String module, String method, String param, byte[] blob) {
        long time = System.currentTimeMillis();
        String result = _call(appId, module, method, param, blob);
        long cast = System.currentTimeMillis() - time;
        LogUtils.i(TAG, String.format("call end ipc-time:%s, appId:%s, module:%s ,method:%s ,param:%s ,blob-th:%s, result:%s", Long.valueOf(cast), appId, module, method, LogUtils.stringLog(param), LogUtils.bytesLog(blob), LogUtils.stringLog(result)));
        return result;
    }

    public String callAsync(String appId, String module, String method, String param, byte[] blob) {
        String requestId = createRequestId();
        this.mAsyncExecutor.submit(new AsyncRunnable(appId, module, method, param, blob, requestId));
        LogUtils.i(TAG, String.format("callAsync-- requestId:%s, appId:%s ,method:%s ,param:%s ,blob-th:%s ", requestId, appId, method, LogUtils.stringLog(param), LogUtils.bytesLog(blob)));
        return requestId;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String _call(String appId, String module, String method, String param, byte[] blob) {
        AppProcessor app = getAppProcessor(appId);
        if (app == null) {
            LogUtils.e(TAG, String.format("call--not app!!!!   appId:%s ", appId));
            return null;
        }
        return app.call(appId, module, method, param, blob);
    }

    private boolean receivedType(int type) {
        return type == 0 || type == 201;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onReceived(int type, String appId, String module, String msgId, String data, byte[] blob) {
        long receivedId = createReceivedId();
        LogUtils.i(TAG, String.format("onReceived-- type:%s ,Id:%s, appId:%s ,module:%s ,msgId:%s ,data:%s ,blob-th:%s", Integer.valueOf(type), Long.valueOf(receivedId), appId, module, msgId, LogUtils.stringLog(data), LogUtils.bytesLog(blob)));
        if (!receivedType(type)) {
            LogUtils.e(TAG, String.format("onReceived--type Undefined   appId:%s, type:%s ", appId, Integer.valueOf(type)));
            return;
        }
        ApiListener listener = this.mApiListener;
        if (listener == null) {
            LogUtils.e(TAG, "onReceived listener is null");
            return;
        }
        AppProcessor app = getAppProcessor(appId);
        if (app == null) {
            LogUtils.e(TAG, String.format("onReceived--not app!!!!   appId:%s ", appId));
        } else if (type == 0) {
            if (SubscribeManager.get().check(appId, module)) {
                app.onReceived(listener, receivedId, appId, module, msgId, data, blob);
            } else {
                LogUtils.w(TAG, String.format("onReceived--not subscribe appId:%s ,module:%s,msgId:%s", appId, module, msgId));
            }
        } else {
            app.onReceived(listener, receivedId, appId, module, msgId, data, blob);
        }
    }

    private AppProcessor getAppProcessor(String appId) {
        return ProcessorManager.get().getAppProcessor(appId);
    }

    private synchronized long createReceivedId() {
        long j;
        if (this.mReceivedId == LongCompanionObject.MAX_VALUE) {
            this.mReceivedId = 0L;
        }
        j = this.mReceivedId + 1;
        this.mReceivedId = j;
        return j;
    }

    private synchronized String createRequestId() {
        long j;
        if (this.mAsyncRequestId == LongCompanionObject.MAX_VALUE) {
            this.mAsyncRequestId = 0L;
        }
        j = this.mAsyncRequestId + 1;
        this.mAsyncRequestId = j;
        return String.valueOf(j);
    }

    /* loaded from: classes22.dex */
    private class AsyncRunnable implements Runnable {
        private String appId;
        private byte[] blob;
        private String method;
        private String module;
        private String param;
        private String requestId;
        private long startTime = System.currentTimeMillis();

        AsyncRunnable(String appId, String module, String method, String param, byte[] blob, String requestId) {
            this.appId = appId;
            this.module = module;
            this.method = method;
            this.param = param;
            this.blob = blob;
            this.requestId = requestId;
        }

        @Override // java.lang.Runnable
        public void run() {
            long time = System.currentTimeMillis();
            long idle = time - this.startTime;
            String result = ApiManager.this._call(this.appId, this.module, this.method, this.param, this.blob);
            long ipc = System.currentTimeMillis() - time;
            LogUtils.i(ApiManager.TAG, String.format("callAsync-end requestId:%s, appId:%s ,method:%s ,idle-time:%s,  ipc-time:%s,  result:%s ", this.requestId, this.appId, this.method, Long.valueOf(idle), Long.valueOf(ipc), LogUtils.stringLog(result)));
            ApiListener listener = ApiManager.this.mApiListener;
            if (listener != null) {
                listener.onCallResult(this.requestId, this.appId, this.module, this.method, result);
            } else {
                LogUtils.e(ApiManager.TAG, "onReceived listener is null");
            }
        }
    }
}
