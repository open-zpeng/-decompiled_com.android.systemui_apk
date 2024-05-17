package com.xiaopeng.systemui.utils;

import android.util.Log;
/* loaded from: classes24.dex */
public class Logger {
    private static final String APP_TAG = "XSystemUI--";
    private static final boolean DEBUG = false;

    public static void v(String tag, String msg) {
    }

    public static void d(String tag, String msg) {
    }

    public static void w(String tag, String msg) {
    }

    public static void e(String tag, String msg) {
        Log.e(APP_TAG + tag, msg);
    }

    public static void i(String tag, String msg) {
        Log.i(APP_TAG + tag, msg);
    }
}
