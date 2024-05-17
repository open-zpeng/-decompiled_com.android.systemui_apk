package com.xiaopeng.aar.utils;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.RestrictTo;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes22.dex */
public class ThreadUtils {

    /* loaded from: classes22.dex */
    public static class UI {
        private static volatile Thread sMainThread;
        private static volatile Handler sMainThreadHandler;

        public static boolean isMainThread() {
            if (sMainThread == null) {
                synchronized (UI.class) {
                    if (sMainThread == null) {
                        sMainThread = Looper.getMainLooper().getThread();
                    }
                }
            }
            return Thread.currentThread() == sMainThread;
        }

        public static Handler getUiThreadHandler() {
            if (sMainThreadHandler == null) {
                synchronized (UI.class) {
                    if (sMainThreadHandler == null) {
                        sMainThreadHandler = new Handler(Looper.getMainLooper());
                    }
                }
            }
            return sMainThreadHandler;
        }

        public static void post(Runnable runnable) {
            if (isMainThread()) {
                runnable.run();
            } else {
                getUiThreadHandler().post(runnable);
            }
        }

        public static void postDelay(Runnable runner, long delayMs) {
            getUiThreadHandler().postDelayed(runner, delayMs);
        }

        public static void removeCallbacks(Runnable runner) {
            getUiThreadHandler().removeCallbacks(runner);
        }
    }

    /* loaded from: classes22.dex */
    public static class SINGLE {
        private static volatile ExecutorService sExecutor;

        public static void post(Runnable runnable) {
            if (sExecutor == null) {
                synchronized (SINGLE.class) {
                    if (sExecutor == null) {
                        sExecutor = Executors.newSingleThreadExecutor();
                    }
                }
            }
            sExecutor.execute(runnable);
        }
    }

    /* loaded from: classes22.dex */
    public static class MULTI {
        private static volatile ExecutorService sExecutor;

        public static void post(Runnable runnable) {
            if (sExecutor == null) {
                synchronized (MULTI.class) {
                    if (sExecutor == null) {
                        sExecutor = Executors.newFixedThreadPool(3);
                    }
                }
            }
            sExecutor.execute(runnable);
        }
    }
}
