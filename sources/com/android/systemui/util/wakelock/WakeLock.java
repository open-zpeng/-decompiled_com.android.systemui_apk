package com.android.systemui.util.wakelock;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;
import androidx.annotation.VisibleForTesting;
import java.util.HashMap;
/* loaded from: classes21.dex */
public interface WakeLock {
    public static final long DEFAULT_MAX_TIMEOUT = 20000;
    public static final String REASON_WRAP = "wrap";
    public static final String TAG = "WakeLock";

    void acquire(String str);

    void release(String str);

    Runnable wrap(Runnable runnable);

    static WakeLock createPartial(Context context, String tag) {
        return createPartial(context, tag, DEFAULT_MAX_TIMEOUT);
    }

    static WakeLock createPartial(Context context, String tag, long maxTimeout) {
        return wrap(createPartialInner(context, tag), maxTimeout);
    }

    @VisibleForTesting
    static PowerManager.WakeLock createPartialInner(Context context, String tag) {
        return ((PowerManager) context.getSystemService(PowerManager.class)).newWakeLock(1, tag);
    }

    static Runnable wrapImpl(final WakeLock w, final Runnable r) {
        w.acquire(REASON_WRAP);
        return new Runnable() { // from class: com.android.systemui.util.wakelock.-$$Lambda$WakeLock$Rdut1DSGlHtP-OM8Y87P7galvtM
            @Override // java.lang.Runnable
            public final void run() {
                WakeLock.lambda$wrapImpl$0(r, w);
            }
        };
    }

    static /* synthetic */ void lambda$wrapImpl$0(Runnable r, WakeLock w) {
        try {
            r.run();
        } finally {
            w.release(REASON_WRAP);
        }
    }

    @VisibleForTesting
    static WakeLock wrap(final PowerManager.WakeLock inner, final long maxTimeout) {
        return new WakeLock() { // from class: com.android.systemui.util.wakelock.WakeLock.1
            private final HashMap<String, Integer> mActiveClients = new HashMap<>();

            @Override // com.android.systemui.util.wakelock.WakeLock
            public void acquire(String why) {
                this.mActiveClients.putIfAbsent(why, 0);
                HashMap<String, Integer> hashMap = this.mActiveClients;
                hashMap.put(why, Integer.valueOf(hashMap.get(why).intValue() + 1));
                inner.acquire(maxTimeout);
            }

            @Override // com.android.systemui.util.wakelock.WakeLock
            public void release(String why) {
                Integer count = this.mActiveClients.get(why);
                if (count == null) {
                    Log.wtf(WakeLock.TAG, "Releasing WakeLock with invalid reason: " + why, new Throwable());
                } else if (count.intValue() == 1) {
                    this.mActiveClients.remove(why);
                } else {
                    this.mActiveClients.put(why, Integer.valueOf(count.intValue() - 1));
                }
                inner.release();
            }

            @Override // com.android.systemui.util.wakelock.WakeLock
            public Runnable wrap(Runnable runnable) {
                return WakeLock.wrapImpl(this, runnable);
            }

            public String toString() {
                return "active clients= " + this.mActiveClients.toString();
            }
        };
    }
}
