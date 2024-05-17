package com.xiaopeng.systemui.helper;

import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/* loaded from: classes24.dex */
public class ThreadHelper {
    private static Handler sMainThreadHandler;
    private static final String TAG = ThreadHelper.class.getSimpleName();
    private static final ExecutorService sThreadPool = Executors.newFixedThreadPool(4);

    public static void execute(final Runnable runnable, final Runnable callback, final int priority) {
        try {
            if (!sThreadPool.isShutdown()) {
                sThreadPool.execute(new Runnable() { // from class: com.xiaopeng.systemui.helper.-$$Lambda$ThreadHelper$nsZ1xnSDnPWC1cu-G_vKRWAot_k
                    @Override // java.lang.Runnable
                    public final void run() {
                        ThreadHelper.lambda$execute$0(priority, runnable, callback);
                    }
                });
            }
        } catch (Exception e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$execute$0(int priority, Runnable runnable, Runnable callback) {
        Process.setThreadPriority(priority);
        runnable.run();
        if (callback != null) {
            new Handler(Looper.myLooper()).post(callback);
        }
    }

    public static void runOnMainThread(Runnable runner) {
        runOnMainThreadDelay(runner, 0L);
    }

    public static void runOnMainThreadDelay(Runnable runner, long delayMs) {
        if (sMainThreadHandler == null) {
            sMainThreadHandler = new Handler(Looper.getMainLooper());
        }
        sMainThreadHandler.postDelayed(runner, delayMs);
    }

    public static void execute(Runnable runnable) {
        execute(runnable, null, 10);
    }
}
