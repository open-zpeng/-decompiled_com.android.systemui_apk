package com.xiaopeng.module.aiavatar.helper;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
/* loaded from: classes23.dex */
public class ThreadUtils {
    private static Handler sWorkerHandler;
    protected static Handler sMainHandler = new Handler(Looper.getMainLooper());
    private static HandlerThread sWorkerThread = new HandlerThread("avatar_work_thread");

    static {
        sWorkerThread.start();
        sWorkerHandler = new Handler(sWorkerThread.getLooper());
    }

    public static void postWorker(Runnable task) {
        if (task != null) {
            sWorkerHandler.post(task);
        }
    }

    public static void postWorker(Runnable task, long delay) {
        if (task != null) {
            sWorkerHandler.postDelayed(task, delay);
        }
    }

    public static void removeWorker(Runnable task) {
        if (task != null) {
            sWorkerHandler.removeCallbacks(task);
        }
    }

    public static void postMain(Runnable task) {
        if (task != null) {
            sMainHandler.post(task);
        }
    }

    public static void postMain(Runnable task, long delay) {
        if (task != null) {
            sMainHandler.postDelayed(task, delay);
        }
    }

    public static void removeMain(Runnable task) {
        if (task != null) {
            sMainHandler.removeCallbacks(task);
        }
    }
}
