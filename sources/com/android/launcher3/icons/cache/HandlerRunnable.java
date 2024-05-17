package com.android.launcher3.icons.cache;

import android.os.Handler;
/* loaded from: classes19.dex */
public abstract class HandlerRunnable implements Runnable {
    private final Runnable mEndRunnable;
    private final Handler mHandler;
    private boolean mEnded = false;
    private boolean mCanceled = false;

    public HandlerRunnable(Handler handler, Runnable endRunnable) {
        this.mHandler = handler;
        this.mEndRunnable = endRunnable;
    }

    public void cancel() {
        this.mHandler.removeCallbacks(this);
        this.mCanceled = true;
        onEnd();
    }

    protected boolean isCanceled() {
        return this.mCanceled;
    }

    public void onEnd() {
        if (!this.mEnded) {
            this.mEnded = true;
            Runnable runnable = this.mEndRunnable;
            if (runnable != null) {
                runnable.run();
            }
        }
    }
}
