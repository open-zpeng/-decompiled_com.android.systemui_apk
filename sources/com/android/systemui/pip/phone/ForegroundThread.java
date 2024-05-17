package com.android.systemui.pip.phone;

import android.os.Handler;
import android.os.HandlerThread;
/* loaded from: classes21.dex */
public final class ForegroundThread extends HandlerThread {
    private static Handler sHandler;
    private static ForegroundThread sInstance;

    private ForegroundThread() {
        super("recents.fg");
    }

    private static void ensureThreadLocked() {
        if (sInstance == null) {
            sInstance = new ForegroundThread();
            sInstance.start();
            sHandler = new Handler(sInstance.getLooper());
        }
    }

    public static ForegroundThread get() {
        ForegroundThread foregroundThread;
        synchronized (ForegroundThread.class) {
            ensureThreadLocked();
            foregroundThread = sInstance;
        }
        return foregroundThread;
    }

    public static Handler getHandler() {
        Handler handler;
        synchronized (ForegroundThread.class) {
            ensureThreadLocked();
            handler = sHandler;
        }
        return handler;
    }
}
