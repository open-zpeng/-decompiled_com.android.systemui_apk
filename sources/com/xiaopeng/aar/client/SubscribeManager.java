package com.xiaopeng.aar.client;

import android.os.Binder;
import android.util.ArraySet;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.aar.client.ipc.Ipc;
import com.xiaopeng.aar.client.ipc.IpcManager;
import com.xiaopeng.aar.utils.HandlerThreadHelper;
import com.xiaopeng.aar.utils.LogUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes22.dex */
public class SubscribeManager {
    private static final String TAG = "Subscribe";
    private final HandlerThreadHelper mHandlerThread;
    private ConcurrentHashMap<String, Integer> mReTryCount;
    private ConcurrentHashMap<String, Boolean> mReTryEnable;
    private HashMap<String, HashMap<String, ArraySet<String>>> mSubscribes;

    /* loaded from: classes22.dex */
    private static class SingletonHolder {
        private static final SubscribeManager INSTANCE = new SubscribeManager();

        private SingletonHolder() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SubscribeManager get() {
        return SingletonHolder.INSTANCE;
    }

    private SubscribeManager() {
        this.mSubscribes = new HashMap<>();
        this.mReTryCount = new ConcurrentHashMap<>();
        this.mReTryEnable = new ConcurrentHashMap<>();
        IpcManager.get().getIpc().addOnServerStatusListener(new Ipc.OnServerStatusListener() { // from class: com.xiaopeng.aar.client.-$$Lambda$SubscribeManager$Z8i45TUdOkTaEoRiNc4-x7LdAyM
            @Override // com.xiaopeng.aar.client.ipc.Ipc.OnServerStatusListener
            public final void onServerStatus(String str, int i) {
                SubscribeManager.this.lambda$new$0$SubscribeManager(str, i);
            }
        });
        this.mHandlerThread = new HandlerThreadHelper("SubscribeManager");
    }

    public /* synthetic */ void lambda$new$0$SubscribeManager(String appId, int status) {
        if (status == 1 || status == 2) {
            int uid = Binder.getCallingUid();
            int pid = Binder.getCallingPid();
            LogUtils.i(TAG, String.format("serverStart-- appId:%s, uid:%s, pid:%s", appId, Integer.valueOf(uid), Integer.valueOf(pid)));
            lambda$subscribeMulIpc$5$SubscribeManager(appId);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized boolean check(String appId, String module) {
        HashMap<String, ArraySet<String>> map = this.mSubscribes.get(appId);
        boolean z = false;
        if (map != null && !map.isEmpty()) {
            ArraySet<String> list = map.get(module);
            if (list != null) {
                if (!list.isEmpty()) {
                    z = true;
                }
            }
            return z;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: reSubscribe */
    public synchronized void lambda$subscribeMulIpc$5$SubscribeManager(final String appId) {
        HashMap<String, ArraySet<String>> map = this.mSubscribes.get(appId);
        if (map != null && !map.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, ArraySet<String>> item : map.entrySet()) {
                String module = item.getKey();
                ArraySet<String> list = item.getValue();
                if (list != null && list.size() > 0) {
                    sb.append(module);
                    sb.append(NavigationBarInflaterView.GRAVITY_SEPARATOR);
                }
            }
            if (sb.length() > 0) {
                this.mHandlerThread.post(new Runnable() { // from class: com.xiaopeng.aar.client.-$$Lambda$SubscribeManager$Wq4C419CP6y7vAkn6JqgVliIAu8
                    @Override // java.lang.Runnable
                    public final void run() {
                        SubscribeManager.this.lambda$reSubscribe$1$SubscribeManager(appId, sb);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$reSubscribe$1$SubscribeManager(String appId, StringBuilder sb) {
        subscribeMulIpc(appId, sb.toString());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void subscribe(final String appId, final String module, String subscriber) {
        HashMap<String, ArraySet<String>> map = this.mSubscribes.get(appId);
        if (map == null) {
            map = new HashMap<>();
            this.mSubscribes.put(appId, map);
        }
        ArraySet<String> list = map.get(module);
        if (list == null) {
            list = new ArraySet<>();
            map.put(module, list);
        }
        if (list.isEmpty()) {
            this.mHandlerThread.post(new Runnable() { // from class: com.xiaopeng.aar.client.-$$Lambda$SubscribeManager$MIMVAZnFvrTDA8Mn4jKxWoZcR_M
                @Override // java.lang.Runnable
                public final void run() {
                    SubscribeManager.this.lambda$subscribe$2$SubscribeManager(appId, module);
                }
            });
        }
        list.add(subscriber);
        LogUtils.d(TAG, String.format("subscribe-- appId:%s,module:%s，subscriber:%s", appId, module, subscriber));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void unSubscribe(final String appId, final String module, String subscriber) {
        HashMap<String, ArraySet<String>> map = this.mSubscribes.get(appId);
        if (map != null && !map.isEmpty()) {
            ArraySet<String> list = map.get(module);
            if (list != null && !list.isEmpty()) {
                list.remove(subscriber);
                if (list.isEmpty()) {
                    this.mHandlerThread.post(new Runnable() { // from class: com.xiaopeng.aar.client.-$$Lambda$SubscribeManager$NzuyK2h3caMtdzf9EZpjdvmsekQ
                        @Override // java.lang.Runnable
                        public final void run() {
                            SubscribeManager.this.lambda$unSubscribe$3$SubscribeManager(appId, module);
                        }
                    });
                }
                LogUtils.d(TAG, String.format("unSubscribe-- appId:%s,module:%s，subscriber:%s", appId, module, subscriber));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: subscribeIpc */
    public void lambda$subscribe$2$SubscribeManager(String appId, String module) {
        LogUtils.i(TAG, String.format("subscribeIpc-- appId:%s ,module:%s", appId, module));
        boolean result = IpcManager.get().getIpc().subscribe(appId, module);
        if (result) {
            String key = appId + module;
            this.mReTryCount.remove(key);
            return;
        }
        reTrySubscribe(appId, module);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setReTryEnable(String appId, boolean enable) {
        LogUtils.i(TAG, String.format("setReTryEnable-- appId:%s ,enable:%s", appId, Boolean.valueOf(enable)));
        this.mReTryEnable.put(appId, Boolean.valueOf(enable));
    }

    private synchronized void reTrySubscribe(final String appId, final String module) {
        Boolean enable = this.mReTryEnable.get(appId);
        if (enable != null && enable.booleanValue()) {
            HashMap<String, ArraySet<String>> map = this.mSubscribes.get(appId);
            if (map != null && !map.isEmpty()) {
                ArraySet<String> list = map.get(module);
                if (list != null && !list.isEmpty()) {
                    String key = appId + module;
                    long delayTime = reTryCount(key) * 1000;
                    LogUtils.w(TAG, String.format("subscribeIpc  retry-- appId:%s ,module:%s ,delayTime:%s", appId, module, Long.valueOf(delayTime)));
                    this.mHandlerThread.postDelayed(new Runnable() { // from class: com.xiaopeng.aar.client.-$$Lambda$SubscribeManager$agMThdQWNyfq6X1f5kL6y8EOg6I
                        @Override // java.lang.Runnable
                        public final void run() {
                            SubscribeManager.this.lambda$reTrySubscribe$4$SubscribeManager(appId, module);
                        }
                    }, delayTime);
                    return;
                }
                LogUtils.i(TAG, String.format("reTrySubscribe--list null appId:%s ,module:%s", appId, module));
                return;
            }
            LogUtils.i(TAG, String.format("reTrySubscribe--map null appId:%s ,module:%s", appId, module));
            return;
        }
        LogUtils.i(TAG, String.format("reTrySubscribe--not enable appId:%s ,module:%s", appId, module));
    }

    private void subscribeMulIpc(final String appId, String module) {
        LogUtils.i(TAG, String.format("subscribeMulIpc-- appId:%s ,module:%s", appId, module));
        boolean result = IpcManager.get().getIpc().subscribes(appId, module);
        if (result) {
            this.mReTryCount.remove(appId);
            return;
        }
        Boolean enable = this.mReTryEnable.get(appId);
        if (enable == null || !enable.booleanValue()) {
            LogUtils.i(TAG, String.format("subscribeMulIpc--not enable appId:%s ,module:%s", appId, module));
            return;
        }
        long delayTime = reTryCount(appId) * 1000;
        LogUtils.w(TAG, String.format("subscribeMulIpc  retry-- appId:%s ,module:%s ,delayTime:%s", appId, module, Long.valueOf(delayTime)));
        this.mHandlerThread.postDelayed(new Runnable() { // from class: com.xiaopeng.aar.client.-$$Lambda$SubscribeManager$5KnyVPRgeb1vFrh54gCln5MwKck
            @Override // java.lang.Runnable
            public final void run() {
                SubscribeManager.this.lambda$subscribeMulIpc$5$SubscribeManager(appId);
            }
        }, delayTime);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: unSubscribeIpc */
    public void lambda$unSubscribe$3$SubscribeManager(String appId, String module) {
        LogUtils.i(TAG, String.format("unSubscribeIpc-- appId:%s ,module:%s", appId, module));
        IpcManager.get().getIpc().unSubscribe(appId, module);
    }

    private int reTryCount(String key) {
        Integer count;
        Integer count2 = this.mReTryCount.get(key);
        if (count2 != null) {
            count = Integer.valueOf(count2.intValue() + 1);
        } else {
            count = 1;
        }
        this.mReTryCount.put(key, count);
        return count.intValue();
    }
}
