package com.xiaopeng.systemui.utils;

import android.os.Looper;
/* loaded from: classes24.dex */
public class Assert {
    public static void isMainThread() {
        if (!Looper.getMainLooper().isCurrentThread()) {
            throw new IllegalStateException("should be called from the main thread.");
        }
    }

    public static void isNotMainThread() {
        if (Looper.getMainLooper().isCurrentThread()) {
            throw new IllegalStateException("should not be called from the main thread.");
        }
    }
}
