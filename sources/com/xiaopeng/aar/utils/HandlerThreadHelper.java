package com.xiaopeng.aar.utils;

import android.os.Handler;
import android.os.HandlerThread;
import androidx.annotation.RestrictTo;
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes22.dex */
public class HandlerThreadHelper {
    private Handler mHandler;
    private HandlerThread mHandlerThread;

    public HandlerThreadHelper(String name) {
        this.mHandlerThread = new HandlerThread(name);
        this.mHandlerThread.start();
        this.mHandler = new Handler(this.mHandlerThread.getLooper());
    }

    public void post(Runnable r) {
        this.mHandler.post(r);
    }

    public void remove(Runnable r) {
        this.mHandler.removeCallbacks(r);
    }

    public void postDelayed(Runnable r, long delayMillis) {
        this.mHandler.postDelayed(r, delayMillis);
    }

    public void destroy() {
        this.mHandlerThread.quit();
    }
}
