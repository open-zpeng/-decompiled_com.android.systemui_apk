package com.xiaopeng.systemui.infoflow.speech.utils;

import android.os.Trace;
/* loaded from: classes24.dex */
public class TraceUtils {
    private static boolean isDebug = false;

    public static void alwaysTraceBegin(String methodName) {
        if (isDebug) {
            Trace.beginSection(methodName);
        }
    }

    public static void alwaysTraceEnd() {
        if (isDebug) {
            Trace.endSection();
        }
    }
}
