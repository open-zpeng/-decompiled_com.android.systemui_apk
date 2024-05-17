package com.xiaopeng.lib.apirouter.server;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
/* loaded from: classes22.dex */
class AutoCodeMatcher {
    private static HashMap<String, Pair<IBinder, String>> mapping;
    private static Handler sWorkerHandler;
    private static List<IManifestHandler> sManifestHandlerList = new LinkedList();
    private static volatile boolean sIsManifestInited = false;
    private static Object sLock = new Object();
    private static HandlerThread sWorkerThread = new HandlerThread("AutoCodeMatcher-workerThread");

    static {
        sWorkerThread.start();
        sWorkerHandler = new Handler(sWorkerThread.getLooper());
    }

    public Pair<IBinder, String> match(String service) {
        if (mapping == null) {
            mapping = ManifestHelperMapping.reflectMapping();
            initManifestHandler();
        }
        HashMap<String, Pair<IBinder, String>> hashMap = mapping;
        Pair<IBinder, String> pair = hashMap == null ? null : hashMap.get(service);
        return pair == null ? new Pair<>(null, null) : pair;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void addManifestHandler(final IManifestHandler manifestHandler) {
        sWorkerHandler.post(new Runnable() { // from class: com.xiaopeng.lib.apirouter.server.AutoCodeMatcher.1
            @Override // java.lang.Runnable
            public void run() {
                synchronized (AutoCodeMatcher.sLock) {
                    if (AutoCodeMatcher.sIsManifestInited) {
                        AutoCodeMatcher.addManifestHandlerToMap(IManifestHandler.this.getManifestHelpers());
                    } else if (!AutoCodeMatcher.sManifestHandlerList.contains(IManifestHandler.this)) {
                        AutoCodeMatcher.sManifestHandlerList.add(IManifestHandler.this);
                    }
                }
            }
        });
    }

    private void initManifestHandler() {
        synchronized (sLock) {
            if (!sManifestHandlerList.isEmpty()) {
                for (IManifestHandler manifestHandler : sManifestHandlerList) {
                    initManifestHandler(manifestHandler);
                }
            }
            sIsManifestInited = true;
        }
    }

    private void initManifestHandler(IManifestHandler manifestHandler) {
        if (manifestHandler == null) {
            return;
        }
        IManifestHelper[] manifestHelpers = manifestHandler.getManifestHelpers();
        if (manifestHelpers == null || manifestHelpers.length == 0) {
            Log.i("AutoCodeMatcher", "initManifestHandler manifestHelpers is empty, return");
        } else {
            addManifestHandlerToMap(manifestHelpers);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void addManifestHandlerToMap(IManifestHelper[] manifestHelpers) {
        Log.i("AutoCodeMatcher", "addManifestHandlerToMap:" + manifestHelpers);
        if (mapping == null) {
            mapping = new HashMap<>();
        }
        HashMap<String, Pair<IBinder, String>> currentMapping = mapping;
        for (IManifestHelper manifestHelper : manifestHelpers) {
            try {
                HashMap<String, Pair<IBinder, String>> mapping2 = manifestHelper.getMapping();
                if (mapping2 != null && !mapping2.isEmpty()) {
                    currentMapping.putAll(mapping2);
                }
            } catch (Exception e) {
                Log.e("AutoCodeMatcher", "addManifestHandlerToMap:" + manifestHelper.getClass(), e);
            }
        }
    }
}
