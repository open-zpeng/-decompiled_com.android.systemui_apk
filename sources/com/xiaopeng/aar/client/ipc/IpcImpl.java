package com.xiaopeng.aar.client.ipc;

import android.net.Uri;
import android.os.Binder;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import com.xiaopeng.aar.Apps;
import com.xiaopeng.aar.client.ipc.Ipc;
import com.xiaopeng.aar.server.ipc.ServerObserver;
import com.xiaopeng.aar.utils.LogUtils;
import com.xiaopeng.lib.apirouter.ApiRouter;
import com.xiaopeng.lib.apirouter.server.ApiPublisherProvider;
import com.xiaopeng.lib.apirouter.server.IManifestHandler;
import com.xiaopeng.lib.apirouter.server.IManifestHelper;
import com.xiaopeng.lib.apirouter.server.ManifestHelper_aar;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes22.dex */
public class IpcImpl implements Ipc {
    private static final String TAG = "Ipc";
    private Ipc.OnServerListener mOnAppListener;
    private ConcurrentHashMap<String, Integer> mPids;
    private String mServerName;
    private final CopyOnWriteArraySet<Ipc.OnServerStatusListener> mStatusListeners;

    /* loaded from: classes22.dex */
    private static class SingletonHolder {
        private static final IpcImpl INSTANCE = new IpcImpl();

        private SingletonHolder() {
        }
    }

    public static IpcImpl get() {
        return SingletonHolder.INSTANCE;
    }

    private IpcImpl() {
        this.mStatusListeners = new CopyOnWriteArraySet<>();
        this.mPids = new ConcurrentHashMap<>();
        LogUtils.d(TAG, "IpcImpl");
        ApiPublisherProvider.addManifestHandler(new IManifestHandler() { // from class: com.xiaopeng.aar.client.ipc.-$$Lambda$IpcImpl$VwPfrwFGfJLRXZNnk3gwVppG5A4
            @Override // com.xiaopeng.lib.apirouter.server.IManifestHandler
            public final IManifestHelper[] getManifestHelpers() {
                return IpcImpl.lambda$new$0();
            }
        });
        this.mServerName = ServerObserver.class.getSimpleName();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ IManifestHelper[] lambda$new$0() {
        return new IManifestHelper[]{new ManifestHelper_aar()};
    }

    @Override // com.xiaopeng.aar.client.ipc.Ipc
    public void setOnServerListener(Ipc.OnServerListener listener) {
        this.mOnAppListener = listener;
    }

    @Override // com.xiaopeng.aar.client.ipc.Ipc
    public void addOnServerStatusListener(@NonNull Ipc.OnServerStatusListener listener) {
        this.mStatusListeners.add(listener);
    }

    @Override // com.xiaopeng.aar.client.ipc.Ipc
    public void removeOnServerStatusListener(@NonNull Ipc.OnServerStatusListener listener) {
        this.mStatusListeners.remove(listener);
    }

    @Override // com.xiaopeng.aar.client.ipc.Ipc
    public String call(String appId, String module, String method, String param, byte[] blob) {
        return callApiRouter(0, appId, module, method, param, blob);
    }

    @Override // com.xiaopeng.aar.client.ipc.Ipc
    public void onReceived(int type, String appId, String module, String msgId, String data, byte[] blob) {
        int status;
        if (type == 202) {
            int pid = Binder.getCallingPid();
            if (this.mPids.get(appId) == null) {
                status = 1;
                this.mPids.put(appId, Integer.valueOf(pid));
            } else {
                status = 2;
            }
            int uid = Binder.getCallingUid();
            LogUtils.i(TAG, String.format("onReceived--SERVER_NOTIFY_STARTED appId:%s, uid:%s, pid:%s, status:%s, %s", appId, Integer.valueOf(uid), Integer.valueOf(pid), Integer.valueOf(status), this.mStatusListeners));
            Iterator<Ipc.OnServerStatusListener> it = this.mStatusListeners.iterator();
            while (it.hasNext()) {
                it.next().onServerStatus(appId, status);
            }
            return;
        }
        if (this.mPids.get(appId) == null) {
            this.mPids.put(appId, Integer.valueOf(Binder.getCallingPid()));
        }
        Ipc.OnServerListener listener = this.mOnAppListener;
        if (listener != null) {
            listener.onEvent(type, appId, module, msgId, data, blob);
        }
    }

    private String callApiRouter(int type, String appId, String module, String method, String param, byte[] blob) {
        String pkgName = Apps.getPackageNames(appId);
        if (TextUtils.isEmpty(pkgName)) {
            LogUtils.e(TAG, String.format("call--appId : %s,  pkg is null", appId));
            return null;
        }
        LogUtils.d(TAG, String.format("callApiRouter %s ,type: %s , module: %s, method: %s", pkgName, Integer.valueOf(type), module, method));
        try {
            Uri.Builder builder = new Uri.Builder();
            StringBuilder sb = new StringBuilder();
            sb.append(pkgName);
            sb.append(".");
            try {
                sb.append(this.mServerName);
                Uri.Builder builder2 = builder.authority(sb.toString()).appendQueryParameter(VuiConstants.ELEMENT_TYPE, String.valueOf(type)).appendQueryParameter("module", module).appendQueryParameter("method", method).appendQueryParameter("param", param);
                if (blob == null) {
                    Uri targetUrl = builder2.path("call").build();
                    return (String) ApiRouter.route(targetUrl);
                }
                Uri targetUrl2 = builder2.path("callBlob").build();
                return (String) ApiRouter.route(targetUrl2, blob);
            } catch (Exception e) {
                e = e;
                LogUtils.e(TAG, String.format("callApiRouter error pkg:%s ,type: %s , module: %s, method: %s, param:%s, blob%s", pkgName, Integer.valueOf(type), module, method, param, LogUtils.bytesLog(blob)));
                e.printStackTrace();
                return null;
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    @Override // com.xiaopeng.aar.client.ipc.Ipc
    public boolean subscribe(String appId, String module) {
        String result = callApiRouter(101, appId, module, null, null, null);
        return "1".equals(result);
    }

    @Override // com.xiaopeng.aar.client.ipc.Ipc
    public void unSubscribe(String appId, String module) {
        callApiRouter(102, appId, module, null, null, null);
    }

    @Override // com.xiaopeng.aar.client.ipc.Ipc
    public boolean subscribes(String appId, String modules) {
        String result = callApiRouter(103, appId, modules, null, null, null);
        return "1".equals(result);
    }

    @Override // com.xiaopeng.aar.client.ipc.Ipc
    public void unSubscribes(String appId, String modules) {
        callApiRouter(104, appId, modules, null, null, null);
    }
}
