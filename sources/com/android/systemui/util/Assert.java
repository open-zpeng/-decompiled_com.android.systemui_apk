package com.android.systemui.util;

import android.os.Looper;
import com.android.internal.annotations.VisibleForTesting;
/* loaded from: classes21.dex */
public class Assert {
    @VisibleForTesting
    public static Looper sMainLooper = Looper.getMainLooper();

    public static void isMainThread() {
        if (!sMainLooper.isCurrentThread()) {
            throw new IllegalStateException("should be called from the main thread.");
        }
    }

    public static void isNotMainThread() {
        if (sMainLooper.isCurrentThread()) {
            throw new IllegalStateException("should not be called from the main thread.");
        }
    }
}
