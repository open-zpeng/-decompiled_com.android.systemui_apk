package com.xiaopeng.systemui.infoflow.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/* loaded from: classes24.dex */
public final class ThreadUtils {
    public static final int THREAD_BACKGROUND = 0;
    public static final int THREAD_ICM = 3;
    public static final int THREAD_NORMAL = 2;
    public static final int THREAD_UI = 1;
    private static Handler sBackgroundHandler;
    private static HandlerThread sBackgroundThread;
    private static Handler sMainThreadHandler;
    private static Handler sNormalHandler;
    private static HandlerThread sNormalThread;
    private static final String TAG = ThreadUtils.class.getSimpleName();
    private static final ExecutorService sThreadPool = Executors.newFixedThreadPool(4);
    private static final ExecutorService sIcmThreadPool = Executors.newFixedThreadPool(1);
    private static final ExecutorService sSingleThreadPool = Executors.newFixedThreadPool(1);
    private static final HashMap<Object, RunnableMap> sRunnableCache = new HashMap<>();

    public static void execute(final Runnable runnable, final Runnable callback, final int priority) {
        try {
            if (!sThreadPool.isShutdown()) {
                sThreadPool.execute(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.util.-$$Lambda$ThreadUtils$LccE0A_BBQ0f3reMT_hyACukSgw
                    @Override // java.lang.Runnable
                    public final void run() {
                        ThreadUtils.lambda$execute$0(priority, runnable, callback);
                    }
                });
            }
        } catch (Exception e) {
            Logger.e(TAG, null, e);
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

    public static void executeIcmControl(final Runnable runnable) {
        try {
            if (!sIcmThreadPool.isShutdown()) {
                sIcmThreadPool.execute(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.util.-$$Lambda$ThreadUtils$UKhIS8Hlsm0va3WPUdU2iQO7zTc
                    @Override // java.lang.Runnable
                    public final void run() {
                        ThreadUtils.lambda$executeIcmControl$1(runnable);
                    }
                });
            }
        } catch (Exception e) {
            Logger.e(TAG, null, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$executeIcmControl$1(Runnable runnable) {
        Process.setThreadPriority(5);
        runnable.run();
    }

    public static void executeSingleThread(final Runnable runnable) {
        try {
            if (!sSingleThreadPool.isShutdown()) {
                sSingleThreadPool.execute(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.util.-$$Lambda$ThreadUtils$-lfyTSxa2xeu7J0CZnhqX-rFM6M
                    @Override // java.lang.Runnable
                    public final void run() {
                        ThreadUtils.lambda$executeSingleThread$2(runnable);
                    }
                });
            }
        } catch (Exception e) {
            Logger.e(TAG, null, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$executeSingleThread$2(Runnable runnable) {
        Process.setThreadPriority(5);
        runnable.run();
    }

    public static void runOnMainThread(Runnable runner) {
        runOnMainThreadDelay(runner, 0L);
    }

    public static void runOnMainThreadDelay(Runnable runner, long delayMs) {
        if (sMainThreadHandler == null) {
            createMainThread();
        }
        sMainThreadHandler.postDelayed(runner, delayMs);
    }

    public static void execute(Runnable runnable) {
        execute(runnable, null, 5);
    }

    public static void postDelayed(int threadType, Runnable task, long delayMillis) {
        doPost(threadType, null, task, null, false, delayMillis);
    }

    private static void doPost(int threadType, final Runnable preCallback, final Runnable task, final Runnable postCallback, final boolean callbackToMainThread, long delayMillis) {
        Handler handler;
        Looper myLooper;
        if (task != null) {
            if (threadType != 0) {
                if (threadType != 1) {
                    if (threadType == 2) {
                        if (sNormalThread == null) {
                            createNormalThread();
                        }
                        Handler handler2 = sNormalHandler;
                        handler = handler2;
                    } else {
                        handler = sMainThreadHandler;
                    }
                } else {
                    Handler handler3 = sMainThreadHandler;
                    if (handler3 == null) {
                        createMainThread();
                    }
                    Handler handler4 = sMainThreadHandler;
                    handler = handler4;
                }
            } else {
                if (sBackgroundThread == null) {
                    createBackgroundThread();
                }
                Handler handler5 = sBackgroundHandler;
                handler = handler5;
            }
            if (callbackToMainThread) {
                myLooper = null;
            } else {
                Looper myLooper2 = Looper.myLooper();
                if (myLooper2 != null) {
                    myLooper = myLooper2;
                } else {
                    Looper myLooper3 = sMainThreadHandler.getLooper();
                    myLooper = myLooper3;
                }
            }
            final Looper finalLopper = myLooper;
            final Handler finalHandler = handler;
            final Runnable postRunnable = new Runnable() { // from class: com.xiaopeng.systemui.infoflow.util.-$$Lambda$ThreadUtils$GhU8onpLsdQ5tOmqpS08044gHTE
                @Override // java.lang.Runnable
                public final void run() {
                    ThreadUtils.lambda$doPost$3(task, postCallback, callbackToMainThread, finalLopper);
                }
            };
            Runnable realRunnable = new Runnable() { // from class: com.xiaopeng.systemui.infoflow.util.-$$Lambda$ThreadUtils$BhieDmv5L8PNq7L7pvq_IT03NaQ
                @Override // java.lang.Runnable
                public final void run() {
                    ThreadUtils.lambda$doPost$6(preCallback, callbackToMainThread, finalLopper, finalHandler, postRunnable);
                }
            };
            handler.postDelayed(realRunnable, delayMillis);
            synchronized (sRunnableCache) {
                if (preCallback == null) {
                    sRunnableCache.put(task, new RunnableMap(realRunnable, Integer.valueOf(threadType)));
                } else {
                    sRunnableCache.put(task, new RunnableMap(postRunnable, Integer.valueOf(threadType)));
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$doPost$3(Runnable task, Runnable postCallback, boolean callbackToMainThread, Looper finalLopper) {
        try {
            task.run();
        } catch (Throwable e) {
            Logger.e(TAG, null, e);
        }
        if (postCallback != null) {
            if (!callbackToMainThread && finalLopper != sMainThreadHandler.getLooper()) {
                new Handler(finalLopper).post(postCallback);
            } else {
                sMainThreadHandler.post(postCallback);
            }
        }
        try {
            sRunnableCache.remove(task);
        } catch (Throwable e2) {
            Logger.e(TAG, null, e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$doPost$6(final Runnable preCallback, boolean callbackToMainThread, Looper finalLopper, final Handler finalHandler, final Runnable postRunnable) {
        if (preCallback != null) {
            if (!callbackToMainThread && finalLopper != sMainThreadHandler.getLooper()) {
                new Handler(finalLopper).post(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.util.-$$Lambda$ThreadUtils$eupACvAC4_S3OFxW2FUqGW0q34A
                    @Override // java.lang.Runnable
                    public final void run() {
                        ThreadUtils.lambda$doPost$4(preCallback, finalHandler, postRunnable);
                    }
                });
                return;
            } else {
                sMainThreadHandler.post(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.util.-$$Lambda$ThreadUtils$syN-tPOz83OKNLVtbiFAOXgRusA
                    @Override // java.lang.Runnable
                    public final void run() {
                        ThreadUtils.lambda$doPost$5(preCallback, finalHandler, postRunnable);
                    }
                });
                return;
            }
        }
        postRunnable.run();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$doPost$4(Runnable preCallback, Handler finalHandler, Runnable postRunnable) {
        preCallback.run();
        finalHandler.post(postRunnable);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$doPost$5(Runnable preCallback, Handler finalHandler, Runnable postRunnable) {
        preCallback.run();
        finalHandler.post(postRunnable);
    }

    public static void removeRunnable(Runnable task) {
        RunnableMap map;
        Runnable realRunnable;
        Handler handler;
        if (task != null && (map = sRunnableCache.get(task)) != null && (realRunnable = map.getRunnable()) != null) {
            int type = map.getType();
            if (type == 0) {
                Handler handler2 = sBackgroundHandler;
                if (handler2 != null) {
                    handler2.removeCallbacks(realRunnable);
                }
            } else if (type == 1) {
                Handler handler3 = sMainThreadHandler;
                if (handler3 != null) {
                    handler3.removeCallbacks(realRunnable);
                }
            } else if (type == 2 && (handler = sNormalHandler) != null) {
                handler.removeCallbacks(realRunnable);
            }
            try {
                sRunnableCache.remove(task);
            } catch (Throwable e) {
                Logger.e(TAG, null, e);
            }
        }
    }

    private static synchronized void createMainThread() {
        synchronized (ThreadUtils.class) {
            if (sMainThreadHandler == null) {
                sMainThreadHandler = new Handler(Looper.getMainLooper());
            }
        }
    }

    private static synchronized void createBackgroundThread() {
        synchronized (ThreadUtils.class) {
            if (sBackgroundThread == null) {
                sBackgroundThread = new HandlerThread("BackgroundHandler", 10);
                sBackgroundThread.start();
                sBackgroundHandler = new Handler(sBackgroundThread.getLooper());
            }
        }
    }

    private static synchronized void createNormalThread() {
        synchronized (ThreadUtils.class) {
            if (sNormalThread == null) {
                sNormalThread = new HandlerThread("NormalHandler", 0);
                sNormalThread.start();
                sNormalHandler = new Handler(sNormalThread.getLooper());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class RunnableMap {
        private Runnable mRunnable;
        private Integer mType;

        RunnableMap(Runnable runnable, Integer type) {
            this.mRunnable = runnable;
            this.mType = type;
        }

        Runnable getRunnable() {
            return this.mRunnable;
        }

        public int getType() {
            return this.mType.intValue();
        }
    }
}
