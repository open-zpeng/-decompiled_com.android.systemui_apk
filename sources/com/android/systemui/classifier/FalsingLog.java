package com.android.systemui.classifier;

import android.os.Build;
import android.os.SystemProperties;
import android.util.Log;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
/* loaded from: classes21.dex */
public class FalsingLog {
    public static final boolean ENABLED = SystemProperties.getBoolean("debug.falsing_log", Build.IS_DEBUGGABLE);
    private static final boolean LOGCAT = SystemProperties.getBoolean("debug.falsing_logcat", false);
    private static final int MAX_SIZE = SystemProperties.getInt("debug.falsing_log_size", 100);
    private static final String TAG = "FalsingLog";
    public static final boolean VERBOSE = false;
    private static FalsingLog sInstance;
    private final ArrayDeque<String> mLog = new ArrayDeque<>(MAX_SIZE);
    private final SimpleDateFormat mFormat = new SimpleDateFormat("MM-dd HH:mm:ss", Locale.US);

    private FalsingLog() {
    }

    public static void v(String tag, String s) {
    }

    public static void i(String tag, String s) {
        if (LOGCAT) {
            Log.i(TAG, tag + "\t" + s);
        }
        log("I", tag, s);
    }

    public static void wLogcat(String tag, String s) {
        Log.w(TAG, tag + "\t" + s);
        log("W", tag, s);
    }

    public static void w(String tag, String s) {
        if (LOGCAT) {
            Log.w(TAG, tag + "\t" + s);
        }
        log("W", tag, s);
    }

    public static void e(String tag, String s) {
        if (LOGCAT) {
            Log.e(TAG, tag + "\t" + s);
        }
        log("E", tag, s);
    }

    public static synchronized void log(String level, String tag, String s) {
        synchronized (FalsingLog.class) {
            if (ENABLED) {
                if (sInstance == null) {
                    sInstance = new FalsingLog();
                }
                if (sInstance.mLog.size() >= MAX_SIZE) {
                    sInstance.mLog.removeFirst();
                }
                String entry = sInstance.mFormat.format(new Date()) + " " + level + " " + tag + " " + s;
                sInstance.mLog.add(entry);
            }
        }
    }

    public static synchronized void dump(PrintWriter pw) {
        synchronized (FalsingLog.class) {
            pw.println("FALSING LOG:");
            if (!ENABLED) {
                pw.println("Disabled, to enable: setprop debug.falsing_log 1");
                pw.println();
                return;
            }
            if (sInstance != null && !sInstance.mLog.isEmpty()) {
                Iterator<String> it = sInstance.mLog.iterator();
                while (it.hasNext()) {
                    String s = it.next();
                    pw.println(s);
                }
                pw.println();
                return;
            }
            pw.println("<empty>");
            pw.println();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:21:0x007b, code lost:
        if (r4 == null) goto L18;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static synchronized void wtf(java.lang.String r8, java.lang.String r9, java.lang.Throwable r10) {
        /*
            java.lang.Class<com.android.systemui.classifier.FalsingLog> r0 = com.android.systemui.classifier.FalsingLog.class
            monitor-enter(r0)
            boolean r1 = com.android.systemui.classifier.FalsingLog.ENABLED     // Catch: java.lang.Throwable -> Laf
            if (r1 != 0) goto L9
            monitor-exit(r0)
            return
        L9:
            e(r8, r9)     // Catch: java.lang.Throwable -> Laf
            android.app.Application r1 = android.app.ActivityThread.currentApplication()     // Catch: java.lang.Throwable -> Laf
            java.lang.String r2 = ""
            boolean r3 = android.os.Build.IS_DEBUGGABLE     // Catch: java.lang.Throwable -> Laf
            if (r3 == 0) goto L85
            if (r1 == 0) goto L85
            java.io.File r3 = new java.io.File     // Catch: java.lang.Throwable -> Laf
            java.io.File r4 = r1.getDataDir()     // Catch: java.lang.Throwable -> Laf
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Laf
            r5.<init>()     // Catch: java.lang.Throwable -> Laf
            java.lang.String r6 = "falsing-"
            r5.append(r6)     // Catch: java.lang.Throwable -> Laf
            java.text.SimpleDateFormat r6 = new java.text.SimpleDateFormat     // Catch: java.lang.Throwable -> Laf
            java.lang.String r7 = "yyyy-MM-dd-HH-mm-ss"
            r6.<init>(r7)     // Catch: java.lang.Throwable -> Laf
            java.util.Date r7 = new java.util.Date     // Catch: java.lang.Throwable -> Laf
            r7.<init>()     // Catch: java.lang.Throwable -> Laf
            java.lang.String r6 = r6.format(r7)     // Catch: java.lang.Throwable -> Laf
            r5.append(r6)     // Catch: java.lang.Throwable -> Laf
            java.lang.String r6 = ".txt"
            r5.append(r6)     // Catch: java.lang.Throwable -> Laf
            java.lang.String r5 = r5.toString()     // Catch: java.lang.Throwable -> Laf
            r3.<init>(r4, r5)     // Catch: java.lang.Throwable -> Laf
            r4 = 0
            java.io.PrintWriter r5 = new java.io.PrintWriter     // Catch: java.lang.Throwable -> L70 java.io.IOException -> L72
            r5.<init>(r3)     // Catch: java.lang.Throwable -> L70 java.io.IOException -> L72
            r4 = r5
            dump(r4)     // Catch: java.lang.Throwable -> L70 java.io.IOException -> L72
            r4.close()     // Catch: java.lang.Throwable -> L70 java.io.IOException -> L72
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L70 java.io.IOException -> L72
            r5.<init>()     // Catch: java.lang.Throwable -> L70 java.io.IOException -> L72
            java.lang.String r6 = "Log written to "
            r5.append(r6)     // Catch: java.lang.Throwable -> L70 java.io.IOException -> L72
            java.lang.String r6 = r3.getAbsolutePath()     // Catch: java.lang.Throwable -> L70 java.io.IOException -> L72
            r5.append(r6)     // Catch: java.lang.Throwable -> L70 java.io.IOException -> L72
            java.lang.String r5 = r5.toString()     // Catch: java.lang.Throwable -> L70 java.io.IOException -> L72
            r2 = r5
        L6c:
            r4.close()     // Catch: java.lang.Throwable -> Laf
            goto L7e
        L70:
            r5 = move-exception
            goto L7f
        L72:
            r5 = move-exception
            java.lang.String r6 = "FalsingLog"
            java.lang.String r7 = "Unable to write falsing log"
            android.util.Log.e(r6, r7, r5)     // Catch: java.lang.Throwable -> L70
            if (r4 == 0) goto L7e
            goto L6c
        L7e:
            goto L8c
        L7f:
            if (r4 == 0) goto L84
            r4.close()     // Catch: java.lang.Throwable -> Laf
        L84:
            throw r5     // Catch: java.lang.Throwable -> Laf
        L85:
            java.lang.String r3 = "FalsingLog"
            java.lang.String r4 = "Unable to write log, build must be debuggable."
            android.util.Log.e(r3, r4)     // Catch: java.lang.Throwable -> Laf
        L8c:
            java.lang.String r3 = "FalsingLog"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Laf
            r4.<init>()     // Catch: java.lang.Throwable -> Laf
            r4.append(r8)     // Catch: java.lang.Throwable -> Laf
            java.lang.String r5 = " "
            r4.append(r5)     // Catch: java.lang.Throwable -> Laf
            r4.append(r9)     // Catch: java.lang.Throwable -> Laf
            java.lang.String r5 = "; "
            r4.append(r5)     // Catch: java.lang.Throwable -> Laf
            r4.append(r2)     // Catch: java.lang.Throwable -> Laf
            java.lang.String r4 = r4.toString()     // Catch: java.lang.Throwable -> Laf
            android.util.Log.wtf(r3, r4, r10)     // Catch: java.lang.Throwable -> Laf
            monitor-exit(r0)
            return
        Laf:
            r8 = move-exception
            monitor-exit(r0)
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.classifier.FalsingLog.wtf(java.lang.String, java.lang.String, java.lang.Throwable):void");
    }
}
