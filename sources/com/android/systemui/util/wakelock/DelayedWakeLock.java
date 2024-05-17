package com.android.systemui.util.wakelock;

import android.os.Handler;
/* loaded from: classes21.dex */
public class DelayedWakeLock implements WakeLock {
    private static final long RELEASE_DELAY_MS = 100;
    private static final String TO_STRING_PREFIX = "[DelayedWakeLock] ";
    private final Handler mHandler;
    private final WakeLock mInner;

    public DelayedWakeLock(Handler h, WakeLock inner) {
        this.mHandler = h;
        this.mInner = inner;
    }

    @Override // com.android.systemui.util.wakelock.WakeLock
    public void acquire(String why) {
        this.mInner.acquire(why);
    }

    public /* synthetic */ void lambda$release$0$DelayedWakeLock(String why) {
        this.mInner.release(why);
    }

    @Override // com.android.systemui.util.wakelock.WakeLock
    public void release(final String why) {
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.systemui.util.wakelock.-$$Lambda$DelayedWakeLock$aTG9u0wfrNahXJF_VixBxfvFqfg
            @Override // java.lang.Runnable
            public final void run() {
                DelayedWakeLock.this.lambda$release$0$DelayedWakeLock(why);
            }
        }, RELEASE_DELAY_MS);
    }

    @Override // com.android.systemui.util.wakelock.WakeLock
    public Runnable wrap(Runnable r) {
        return WakeLock.wrapImpl(this, r);
    }

    public String toString() {
        return TO_STRING_PREFIX + this.mInner;
    }
}
