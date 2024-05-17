package com.xiaopeng.speech.vui.utils;

import android.app.ActivityThread;
import android.text.TextUtils;
import android.util.Log;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
/* loaded from: classes.dex */
public class LogUtils {
    public static int LOG_LEVEL = 4;
    public static String CTAG = getAppName();
    public static Logger sLogger = new DefaultLogger();
    public static boolean sLogEnable = true;
    public static boolean sWithStackTrace = false;
    public static int LOG_DEBUG_LEVEL = 3;
    public static int LOG_VERBOSE_LEVEL = 2;
    public static int LOG_INFO_LEVEL = 4;

    /* loaded from: classes.dex */
    public interface Logger {
        void logByLevel(int i, String str, String str2, String str3);
    }

    public static boolean isLogEnable() {
        return sLogEnable;
    }

    public static void setLogEnable(boolean enable) {
        sLogEnable = enable;
    }

    public static void setLogLevel(int logLevel) {
        LOG_LEVEL = logLevel;
    }

    public static int getLogLevel() {
        return LOG_LEVEL;
    }

    public static void setLogger(Logger logger) {
        if (logger != null) {
            sLogger = logger;
        }
    }

    public static void setWithStackTraceFlag(boolean withStackTrace) {
        sWithStackTrace = withStackTrace;
    }

    public static boolean isLogLevelEnabled(int logLevel) {
        return LOG_LEVEL <= logLevel && isLogEnable();
    }

    public static void logInfo(String tag, String msg) {
        if (tag == null || tag.length() == 0 || msg == null || msg.length() == 0) {
            return;
        }
        long length = msg.length();
        if (length > 3072) {
            while (msg.length() > 3072) {
                String logContent = msg.substring(0, 3072);
                msg = msg.replace(logContent, "");
                i(tag, "------------" + logContent);
            }
            i(tag, "---------" + msg);
            return;
        }
        i(tag, msg);
    }

    public static void logDebug(String tag, String msg) {
        if (tag == null || tag.length() == 0 || msg == null || msg.length() == 0) {
            return;
        }
        long length = msg.length();
        if (length > 3072) {
            while (msg.length() > 3072) {
                String logContent = msg.substring(0, 3072);
                msg = msg.replace(logContent, "");
                d(tag, "-----------" + logContent);
            }
            d(tag, "--------" + msg);
            return;
        }
        d(tag, msg);
    }

    public static void v(String msg) {
        if (isLogLevelEnabled(2)) {
            doLog(2, null, msg, null, sWithStackTrace);
        }
    }

    public static void v(Object tag, String format) {
        if (isLogLevelEnabled(2)) {
            doLog(2, tag, format, null, sWithStackTrace);
        }
    }

    public static void v(Object tag, String format, Object... args) {
        if (isLogLevelEnabled(2)) {
            doLog(2, tag, String.format(format, args), null, sWithStackTrace);
        }
    }

    public static void v(Object tag, String message, Throwable t) {
        if (isLogLevelEnabled(2)) {
            doLog(2, tag, message, t, sWithStackTrace);
        }
    }

    public static void v(Object tag, Throwable t) {
        if (isLogLevelEnabled(2)) {
            doLog(2, tag, "Exception occurs at", t, sWithStackTrace);
        }
    }

    public static void d(String msg) {
        if (isLogLevelEnabled(3)) {
            doLog(3, null, msg, null, sWithStackTrace);
        }
    }

    public static void d(Object tag, String message) {
        if (isLogLevelEnabled(3)) {
            doLog(3, tag, message, null, sWithStackTrace);
        }
    }

    public static void d(Object tag, String format, Object... args) {
        if (isLogLevelEnabled(3)) {
            doLog(3, tag, String.format(format, args), null, sWithStackTrace);
        }
    }

    public static void d(Object tag, String message, Throwable t) {
        if (isLogLevelEnabled(3)) {
            doLog(3, tag, message, t, sWithStackTrace);
        }
    }

    public static void d(Object tag, Throwable t) {
        if (isLogLevelEnabled(3)) {
            doLog(3, tag, "Exception occurs at", t, sWithStackTrace);
        }
    }

    public static void i(String msg) {
        if (isLogLevelEnabled(4)) {
            doLog(4, null, msg, null, sWithStackTrace);
        }
    }

    public static void i(Object tag, String format) {
        if (isLogLevelEnabled(4)) {
            doLog(4, tag, format, null, sWithStackTrace);
        }
    }

    public static void i(Object tag, String format, Object... args) {
        if (isLogLevelEnabled(4)) {
            doLog(4, tag, String.format(format, args), null, sWithStackTrace);
        }
    }

    public static void i(Object tag, String message, Throwable t) {
        if (isLogLevelEnabled(4)) {
            doLog(4, tag, message, t, sWithStackTrace);
        }
    }

    public static void i(Object tag, Throwable t) {
        if (isLogLevelEnabled(4)) {
            doLog(4, tag, "Exception occurs at", t, sWithStackTrace);
        }
    }

    public static void w(String msg) {
        if (isLogLevelEnabled(5)) {
            doLog(5, null, msg, null, sWithStackTrace);
        }
    }

    public static void w(Object tag, String message) {
        if (isLogLevelEnabled(5)) {
            doLog(5, tag, message, null, sWithStackTrace);
        }
    }

    public static void w(Object tag, String format, Object... args) {
        if (isLogLevelEnabled(5)) {
            doLog(5, tag, String.format(format, args), null, sWithStackTrace);
        }
    }

    public static void w(Object tag, String message, Throwable t) {
        if (isLogLevelEnabled(5)) {
            doLog(5, tag, message, t, sWithStackTrace);
        }
    }

    public static void w(Object tag, Throwable t) {
        if (isLogLevelEnabled(5)) {
            doLog(5, tag, "Exception occurs at", t, sWithStackTrace);
        }
    }

    public static void e(String msg) {
        if (isLogLevelEnabled(6)) {
            doLog(6, null, msg, null, sWithStackTrace);
        }
    }

    public static void e(Object tag, String message) {
        if (isLogLevelEnabled(6)) {
            doLog(6, tag, message, null, sWithStackTrace);
        }
    }

    public static void e(Object tag, String format, Object... args) {
        if (isLogLevelEnabled(6)) {
            doLog(6, tag, String.format(format, args), null, sWithStackTrace);
        }
    }

    public static void e(Object tag, String message, Throwable t) {
        if (isLogLevelEnabled(6)) {
            doLog(6, tag, message, t, sWithStackTrace);
        }
    }

    public static void e(Object tag, Throwable t) {
        if (isLogLevelEnabled(6)) {
            doLog(6, tag, "Exception occurs at", t, sWithStackTrace);
        }
    }

    public static void log(int logLevel, Object tag, String message, Throwable t, boolean needStackTrace) {
        if (isLogLevelEnabled(logLevel)) {
            doLog(logLevel, tag, message, t, needStackTrace);
        }
    }

    private static void doLog(int logLevel, Object tag, String message, Throwable t, boolean needStackTrace) {
        String fileName = null;
        int lineNumber = 0;
        if (needStackTrace) {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            StackTraceElement element = null;
            if (stackTraceElements != null && stackTraceElements.length > 4) {
                element = stackTraceElements[4];
            }
            if (element != null) {
                fileName = element.getFileName();
                lineNumber = element.getLineNumber();
            }
        }
        String msg = message;
        if (needStackTrace || t != null) {
            msg = msgForTextLog(fileName, lineNumber, message, t, needStackTrace);
        }
        String tagName = objClassName(tag);
        if (tagName == null) {
            if (TextUtils.isEmpty(fileName)) {
                tagName = CTAG;
            } else {
                tagName = fileName;
            }
        }
        logByLevel(logLevel, tagName, msg);
    }

    private static void logByLevel(int logLevel, String tag, String msg) {
        sLogger.logByLevel(logLevel, msg, tag, null);
    }

    private static String msgForTextLog(String filename, int line, String msg, Throwable t, boolean needStackTrace) {
        StringBuilder sb = new StringBuilder();
        sb.append(msg);
        if (needStackTrace) {
            sb.append(" (T:");
            sb.append(Thread.currentThread().getId());
            sb.append(NavigationBarInflaterView.KEY_CODE_END);
            if (CTAG != null) {
                sb.append("(C:");
                sb.append(CTAG);
                sb.append(NavigationBarInflaterView.KEY_CODE_END);
            }
            sb.append("at (");
            sb.append(filename == null ? "" : filename);
            sb.append(NavigationBarInflaterView.KEY_IMAGE_DELIM);
            sb.append(line);
            sb.append(NavigationBarInflaterView.KEY_CODE_END);
        }
        if (t != null) {
            sb.append('\n');
            sb.append(Log.getStackTraceString(t));
        }
        return sb.toString();
    }

    private static String objClassName(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        return (obj instanceof Class ? (Class) obj : obj.getClass()).getSimpleName();
    }

    private static String getAppName() {
        String packageName = ActivityThread.currentApplication().getPackageName();
        String[] strings = packageName.split("\\.");
        return strings[strings.length - 1];
    }

    /* loaded from: classes.dex */
    public static class DefaultLogger implements Logger {
        @Override // com.xiaopeng.speech.vui.utils.LogUtils.Logger
        public void logByLevel(int type, String msg, String TAG, String fileName) {
            if (type == 2) {
                Log.v(TAG, msg);
            } else if (type == 3) {
                Log.d(TAG, msg);
                String str = "DEBUG: " + msg;
            } else if (type == 4) {
                Log.i(TAG, msg);
                String str2 = "INFO: " + msg;
            } else if (type == 5) {
                Log.w(TAG, msg);
                String str3 = "WARN: " + msg;
            } else if (type == 6) {
                Log.e(TAG, msg);
                String str4 = "ERROR: " + msg;
            }
        }
    }
}
