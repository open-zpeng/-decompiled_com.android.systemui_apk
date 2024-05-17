package com.android.systemui.util.wakelock;

import com.android.internal.util.Preconditions;
/* loaded from: classes21.dex */
public class SettableWakeLock {
    private boolean mAcquired;
    private final WakeLock mInner;
    private final String mWhy;

    public SettableWakeLock(WakeLock inner, String why) {
        Preconditions.checkNotNull(inner, "inner wakelock required");
        this.mInner = inner;
        this.mWhy = why;
    }

    public synchronized boolean isAcquired() {
        return this.mAcquired;
    }

    public synchronized void setAcquired(boolean acquired) {
        if (this.mAcquired != acquired) {
            if (acquired) {
                this.mInner.acquire(this.mWhy);
            } else {
                this.mInner.release(this.mWhy);
            }
            this.mAcquired = acquired;
        }
    }
}
