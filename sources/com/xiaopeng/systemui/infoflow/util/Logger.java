package com.xiaopeng.systemui.infoflow.util;

import android.util.Log;
/* loaded from: classes24.dex */
public class Logger {
    private static final String APP_TAG = "InfoFlow--";
    private static final boolean DEBUG = true;

    public static void v(String tag, String msg) {
        Log.v(APP_TAG + tag, msg);
    }

    public static void d(String tag, String msg) {
        Log.d(APP_TAG + tag, msg);
    }

    public static void d(String tag, String prefix, String msg) {
        Log.d(APP_TAG + tag + prefix, msg);
    }

    public static void w(String tag, String msg) {
        Log.w(APP_TAG + tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.e(APP_TAG + tag, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        Log.e(APP_TAG + tag, msg, tr);
    }

    public static void i(String tag, String msg) {
        Log.i(APP_TAG + tag, msg);
    }
}
