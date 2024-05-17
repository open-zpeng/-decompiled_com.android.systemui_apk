package com.xiaopeng.systemui;

import android.util.Log;
import java.util.HashMap;
/* loaded from: classes24.dex */
public class Logger {
    private static final boolean DEBUG_D = true;
    private static final boolean DEBUG_E = true;
    private static final boolean DEBUG_I = true;
    private static final boolean DEBUG_V = false;
    private static final boolean DEBUG_W = true;
    private static final String TAG = "SysUI-";
    private static final HashMap<String, Long> sLogTime = new HashMap<>();

    public static void i(String tag, String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append(TAG);
        sb.append(tag == null ? "" : tag);
        Log.i(sb.toString(), msg);
    }

    public static void i(String logId, int intervalTime, String tag, String msg) {
        Long time = sLogTime.get(logId);
        long curTime = System.currentTimeMillis();
        if (time != null && curTime - time.longValue() < intervalTime) {
            return;
        }
        sLogTime.put(logId, Long.valueOf(curTime));
        StringBuilder sb = new StringBuilder();
        sb.append(TAG);
        sb.append(tag == null ? "" : tag);
        Log.i(sb.toString(), msg);
    }

    public static void w(String tag, String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append(TAG);
        sb.append(tag == null ? "" : tag);
        Log.w(sb.toString(), msg);
    }

    public static void v(String tag, String msg) {
    }

    public static void d(String tag, String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append(TAG);
        sb.append(tag == null ? "" : tag);
        Log.d(sb.toString(), msg);
    }

    public static void e(String tag, String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append(TAG);
        sb.append(tag == null ? "" : tag);
        Log.e(sb.toString(), msg);
    }

    public static void i(String msg) {
        Log.i(TAG, msg);
    }

    public static void v(String msg) {
    }

    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }
}
