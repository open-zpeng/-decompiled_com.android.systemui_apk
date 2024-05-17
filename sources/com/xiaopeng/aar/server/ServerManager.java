package com.xiaopeng.aar.server;

import android.content.Context;
import android.text.TextUtils;
import android.util.ArraySet;
import androidx.annotation.NonNull;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.aar.Apps;
import com.xiaopeng.aar.BuildConfig;
import com.xiaopeng.aar.server.ipc.IpcServer;
import com.xiaopeng.aar.server.ipc.IpcServerManager;
import com.xiaopeng.aar.utils.HandlerThreadHelper;
import com.xiaopeng.aar.utils.LogUtils;
import com.xiaopeng.aar.utils.Utils;
import java.util.Arrays;
import java.util.Iterator;
/* loaded from: classes22.dex */
public class ServerManager {
    private static final String TAG = "SerMg";
    private HandlerThreadHelper mHandlerThreadHelper;
    private boolean mInit;
    private LockHelper mLockHelper;
    private ServerConfig mServerConfig;
    private ServerListener mServerListener;
    private final ArraySet<String> mSubscribeModules;

    private ServerManager() {
        this.mSubscribeModules = new ArraySet<>();
        LogUtils.setLogTag("sdks-");
        IpcServerManager.get().init();
        IpcServerManager.get().getIpc().setOnClientListener(new IpcServer.OnClientListener() { // from class: com.xiaopeng.aar.server.ServerManager.1
            @Override // com.xiaopeng.aar.server.ipc.IpcServer.OnClientListener
            public String onCall(int type, String module, String method, String param, byte[] blob) {
                return ServerManager.this.call(type, module, method, param, blob);
            }

            @Override // com.xiaopeng.aar.server.ipc.IpcServer.OnClientListener
            public void onClientDied() {
                LogUtils.w(ServerManager.TAG, "napa is dead !!! onUnSubscribe all module ");
                ServerConfig config = ServerManager.this.mServerConfig;
                if (config != null && config.isAutoUnSubscribeWhenNaPaDied()) {
                    synchronized (ServerManager.this.mSubscribeModules) {
                        Iterator it = ServerManager.this.mSubscribeModules.iterator();
                        while (it.hasNext()) {
                            String module = (String) it.next();
                            ServerManager.this.unSubscribe(module);
                        }
                    }
                }
            }
        });
    }

    /* loaded from: classes22.dex */
    private static class SingletonHolder {
        private static final ServerManager INSTANCE = new ServerManager();

        private SingletonHolder() {
        }
    }

    public static ServerManager get() {
        return SingletonHolder.INSTANCE;
    }

    public synchronized void initConfig(ServerConfig config) {
        if (config == null) {
            LogUtils.w(TAG, "initConfig config is null !!!");
        } else if (this.mServerConfig != null) {
            LogUtils.w(TAG, "initConfig config already initialized !!!");
        } else {
            this.mServerConfig = config;
            if (config.isWaitInit()) {
                this.mLockHelper = new LockHelper(config.getWaitTimeout());
            }
            IpcServerManager.get().getIpc().setTargetPackage(config.getTargetPackage());
            LogUtils.setLogLevel(config.getLogLevel());
            LogUtils.setLength(config.getLogLength());
            LogUtils.i(TAG, "initConfig " + config.toString());
        }
    }

    public synchronized void init(@NonNull final Context context) {
        if (this.mInit) {
            LogUtils.w(TAG, "init already initialized  !!!");
            return;
        }
        this.mInit = true;
        String appId = Apps.getAppId(context.getApplicationContext());
        if (appId == null) {
            LogUtils.e(TAG, "init appId is null !!!");
        } else {
            LogUtils.i(TAG, "init appId : " + appId + " ," + BuildConfig.BUILD_VERSION);
            StringBuilder sb = new StringBuilder();
            sb.append(appId);
            sb.append("-");
            LogUtils.setLogTag(sb.toString());
        }
        IpcServerManager.get().getIpc().setAppId(appId);
        if (this.mLockHelper != null) {
            this.mLockHelper.unLock();
        }
        this.mHandlerThreadHelper = new HandlerThreadHelper(appId == null ? "server" : appId);
        this.mHandlerThreadHelper.postDelayed(new Runnable() { // from class: com.xiaopeng.aar.server.-$$Lambda$ServerManager$6zh9kEHThXbIyHgDo2As8jN9LKE
            @Override // java.lang.Runnable
            public final void run() {
                ServerManager.this.lambda$init$0$ServerManager(context);
            }
        }, 500L);
    }

    public /* synthetic */ void lambda$init$0$ServerManager(Context context) {
        Context context1 = context.getApplicationContext();
        ServerConfig serverConfig = this.mServerConfig;
        if (serverConfig != null && serverConfig.isKeepAlive()) {
            IpcServerManager.get().getIpc().keepAlive(context1);
        }
        boolean isUserRelease = Utils.isUserRelease();
        if (!isUserRelease) {
            RunningConfig.get().init(context1);
        }
        if (!isUserRelease) {
            ServerConfig serverConfig2 = this.mServerConfig;
            if (serverConfig2 == null || serverConfig2.isUseMock()) {
                MockTest.get().init(context1);
            }
        }
    }

    public void setServerListener(ServerListener listener) {
        this.mServerListener = listener;
    }

    private void subscribes(String modules) {
        if (TextUtils.isEmpty(modules)) {
            return;
        }
        String[] ms = modules.split(NavigationBarInflaterView.GRAVITY_SEPARATOR);
        LogUtils.d(TAG, "subscribes: " + Arrays.toString(ms));
        for (String m : ms) {
            subscribe(m);
        }
    }

    private void subscribe(String module) {
        boolean isAdd;
        LogUtils.d(TAG, String.format("subscribe-- module:%s", module));
        ServerListener listener = this.mServerListener;
        if (listener != null) {
            synchronized (this.mSubscribeModules) {
                isAdd = this.mSubscribeModules.add(module);
            }
            if (!isAdd) {
                LogUtils.w(TAG, String.format("subscribe--repeat module:%s", module));
                return;
            }
            try {
                listener.onSubscribe(module);
            } catch (Exception e) {
                LogUtils.e(TAG, "onSubscribe exception=" + e.getMessage());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unSubscribe(String module) {
        boolean isRemove;
        LogUtils.d(TAG, String.format("unSubscribe-- module:%s", module));
        ServerListener listener = this.mServerListener;
        if (listener != null) {
            synchronized (this.mSubscribeModules) {
                isRemove = this.mSubscribeModules.remove(module);
            }
            if (!isRemove) {
                LogUtils.w(TAG, String.format("unSubscribe--repeat module:%s", module));
                return;
            }
            try {
                listener.onUnSubscribe(module);
            } catch (Exception e) {
                LogUtils.e(TAG, "onUnSubscribe exception=" + e.getMessage());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String call(int type, String module, String method, String param, byte[] blob) {
        LockHelper lockHelper = this.mLockHelper;
        if (lockHelper != null) {
            lockHelper.checkLock();
        }
        LogUtils.i(TAG, String.format("call-- type:%s ,module:%s ,method:%s ,param:%s ,blob-th:%s", Integer.valueOf(type), module, method, LogUtils.stringLog(param), LogUtils.bytesLog(blob)));
        ServerListener listener = this.mServerListener;
        if (listener == null) {
            LogUtils.e(TAG, "call listener is null");
            return null;
        } else if (type == 0) {
            try {
                return listener.onCall(module, method, param, blob);
            } catch (Exception e) {
                LogUtils.e(TAG, "onCall exception=" + e.getMessage());
                return null;
            }
        } else {
            switch (type) {
                case 101:
                    subscribe(module);
                    return "1";
                case 102:
                    unSubscribe(module);
                    return "1";
                case 103:
                    subscribes(module);
                    return "1";
                default:
                    return null;
            }
        }
    }

    public void send(@NonNull String module, @NonNull String msgId, String data, byte[] blob) {
        send(false, module, msgId, data, blob);
    }

    public void send(boolean ignoreSubscribe, @NonNull String module, @NonNull String msgId, String data, byte[] blob) {
        if (ignoreSubscribe || !interceptEnable()) {
            send(true, 201, module, msgId, data, blob);
        } else if (interceptSend(module)) {
            LogUtils.w(TAG, String.format("send intercept!!!!!! module:%s ,msgId:%s ,data:%s ,blob-th:%s", module, msgId, LogUtils.stringLog(data), LogUtils.bytesLog(blob)));
        } else {
            send(true, 0, module, msgId, data, blob);
        }
    }

    public void send(SendBuilder builder) {
        if (builder == null || builder.module == null || builder.msgId == null) {
            LogUtils.w(TAG, "send builder is null !!!!!!!");
            return;
        }
        int type = 0;
        if (builder.ignoreSubscribe || !interceptEnable()) {
            type = 201;
        } else if (interceptSend(builder.module)) {
            LogUtils.w(TAG, String.format("send intercept!!!!!! module:%s ,msgId:%s ,data:%s ,blob-th:%s", builder.module, builder.msgId, LogUtils.stringLog(builder.data), LogUtils.bytesLog(builder.blob)));
            return;
        }
        send(builder.logEnable, type, builder.module, builder.msgId, builder.data, builder.blob);
    }

    private void send(final boolean logEnable, final int type, final String module, final String msgId, final String data, final byte[] blob) {
        HandlerThreadHelper handler = this.mHandlerThreadHelper;
        ServerConfig config = this.mServerConfig;
        if (config != null && config.isSendAsync() && handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.aar.server.-$$Lambda$ServerManager$-MSdAtiKqqj1WMwYbgcqnj1d5WE
                @Override // java.lang.Runnable
                public final void run() {
                    ServerManager.lambda$send$1(logEnable, type, module, msgId, data, blob);
                }
            });
            return;
        }
        if (logEnable) {
            LogUtils.i(TAG, String.format("send  type %s, module:%s ,msgId:%s ,data:%s ,blob-th:%s", Integer.valueOf(type), module, msgId, LogUtils.stringLog(data), LogUtils.bytesLog(blob)));
        }
        IpcServerManager.get().getIpc().send(type, module, msgId, data, blob);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$send$1(boolean logEnable, int type, String module, String msgId, String data, byte[] blob) {
        if (logEnable) {
            LogUtils.i(TAG, String.format("send  type %s, module:%s ,msgId:%s ,data:%s ,blob-th:%s", Integer.valueOf(type), module, msgId, LogUtils.stringLog(data), LogUtils.bytesLog(blob)));
        }
        IpcServerManager.get().getIpc().send(type, module, msgId, data, blob);
    }

    private boolean interceptEnable() {
        ServerConfig config = this.mServerConfig;
        return config != null && config.isInterceptSendWhenNotSubscribed();
    }

    private boolean interceptSend(String module) {
        synchronized (this.mSubscribeModules) {
            if (!this.mSubscribeModules.contains(module)) {
                return true;
            }
            return false;
        }
    }

    /* loaded from: classes22.dex */
    public static class SendBuilder {
        private byte[] blob;
        private String data;
        private boolean ignoreSubscribe;
        private boolean logEnable;
        private String module;
        private String msgId;

        public SendBuilder setIgnoreSubscribe(boolean ignoreSubscribe) {
            this.ignoreSubscribe = ignoreSubscribe;
            return this;
        }

        public SendBuilder setModule(String module) {
            this.module = module;
            return this;
        }

        public SendBuilder setMsgId(String msgId) {
            this.msgId = msgId;
            return this;
        }

        public SendBuilder setData(String data) {
            this.data = data;
            return this;
        }

        public SendBuilder setBlob(byte[] blob) {
            this.blob = blob;
            return this;
        }

        public SendBuilder setLogEnable(boolean logEnable) {
            this.logEnable = logEnable;
            return this;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes22.dex */
    public static class LockHelper {
        private static final int WAIT_MAX_TIMEOUT = 3000;
        private static final int WAIT_TIMEOUT = 100;
        private int mTimeout;
        private final Object mLock = new Object();
        private boolean mInit = false;

        LockHelper(int timeout) {
            this.mTimeout = 100;
            if (timeout > 0) {
                this.mTimeout = timeout;
            }
            if (this.mTimeout > 3000) {
                this.mTimeout = 3000;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void unLock() {
            synchronized (this.mLock) {
                this.mInit = true;
                this.mLock.notifyAll();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void checkLock() {
            if (!this.mInit) {
                synchronized (this.mLock) {
                    if (!this.mInit) {
                        LogUtils.i(ServerManager.TAG, "wait.......");
                        try {
                            this.mLock.wait(this.mTimeout);
                        } catch (InterruptedException e) {
                            LogUtils.w(ServerManager.TAG, "checkLock " + e.getMessage());
                        }
                    }
                }
            }
        }
    }
}
