package com.xiaopeng.aar.utils;

import android.util.Log;
import androidx.annotation.RestrictTo;
import java.util.HashMap;
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes22.dex */
public class LogUtils {
    private static final boolean DEBUG = false;
    public static final int LOG_D_LEVEL = 3;
    public static final int LOG_E_LEVEL = 6;
    public static final int LOG_I_LEVEL = 4;
    public static final int LOG_W_LEVEL = 5;
    private static String TAG = "aar=";
    private static int LOG_LEVEL = 4;
    private static HashMap<String, Integer> sHashMap = new HashMap<>();
    private static int sLength = 256;

    public static void setLogTagLevel(String tag, Integer level) {
        sHashMap.put(tag, level);
    }

    public static void clearTagLevel() {
        sHashMap.clear();
    }

    public static void setLogTag(String tag) {
        TAG += tag;
    }

    public static void setLogLevel(int logLevel) {
        LOG_LEVEL = logLevel;
    }

    private static boolean isLogTagEnabled(String tag, int level) {
        Integer b = sHashMap.get(tag);
        return b == null || level >= b.intValue();
    }

    private static boolean isLogLevelEnabled(int logLevel) {
        return LOG_LEVEL <= logLevel;
    }

    public static void setLength(int length) {
        if (length <= 0) {
            return;
        }
        sLength = length;
    }

    public static String stringLog(String data) {
        if (data == null) {
            return "";
        }
        int length = data.length();
        int i = sLength;
        return length > i ? data.substring(0, i) : data;
    }

    public static String bytesLog(byte[] blob) {
        return blob == null ? "" : String.valueOf(blob.length);
    }

    public static void d(String tag, String msg) {
        if (isLogTagEnabled(tag, 3) && isLogLevelEnabled(3)) {
            StringBuilder sb = new StringBuilder();
            sb.append(TAG);
            sb.append(tag == null ? "" : tag);
            Log.d(sb.toString(), stackTraceLog(msg));
        }
    }

    public static void d(String tag, String msg, int level) {
        if (isLogTagEnabled(tag, 3) && isLogLevelEnabled(3)) {
            StringBuilder sb = new StringBuilder();
            sb.append(TAG);
            sb.append(tag == null ? "" : tag);
            Log.d(sb.toString(), stackTraceLog(msg, level));
        }
    }

    public static void i(String tag, String msg) {
        if (isLogTagEnabled(tag, 4) && isLogLevelEnabled(4)) {
            StringBuilder sb = new StringBuilder();
            sb.append(TAG);
            sb.append(tag == null ? "" : tag);
            Log.i(sb.toString(), stackTraceLog(msg));
        }
    }

    public static void i(String tag, String msg, int level) {
        if (isLogTagEnabled(tag, 4) && isLogLevelEnabled(4)) {
            StringBuilder sb = new StringBuilder();
            sb.append(TAG);
            sb.append(tag == null ? "" : tag);
            Log.i(sb.toString(), stackTraceLog(msg, level));
        }
    }

    public static void w(String tag, String msg) {
        if (isLogTagEnabled(tag, 5) && isLogLevelEnabled(5)) {
            StringBuilder sb = new StringBuilder();
            sb.append(TAG);
            sb.append(tag == null ? "" : tag);
            Log.w(sb.toString(), stackTraceLog(msg));
        }
    }

    public static void e(String tag, String msg) {
        if (isLogTagEnabled(tag, 6) && isLogLevelEnabled(6)) {
            StringBuilder sb = new StringBuilder();
            sb.append(TAG);
            sb.append(tag == null ? "" : tag);
            Log.e(sb.toString(), stackTraceLog(msg));
        }
    }

    private static String stackTraceLog(String log) {
        return log + "--" + Thread.currentThread();
    }

    private static String stackTraceLog(String log, int level) {
        return log + "--" + Thread.currentThread();
    }
}
