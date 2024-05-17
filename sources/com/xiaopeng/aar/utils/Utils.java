package com.xiaopeng.aar.utils;

import android.os.Build;
import android.text.TextUtils;
import androidx.annotation.RestrictTo;
import java.nio.ByteBuffer;
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes22.dex */
public class Utils {
    public static boolean isUserRelease() {
        return "user".equalsIgnoreCase(Build.TYPE);
    }

    public static boolean isEngVersion() {
        return "eng".equalsIgnoreCase(Build.TYPE);
    }

    public static boolean isUserDebugVersion() {
        return "userdebug".equalsIgnoreCase(Build.TYPE);
    }

    public static boolean isDebuggableVersion() {
        return isEngVersion() || isUserDebugVersion();
    }

    public static int parse(String s) {
        if (!TextUtils.isEmpty(s)) {
            try {
                return Integer.parseInt(s);
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }

    public static double parseDouble(String s) {
        if (!TextUtils.isEmpty(s)) {
            try {
                return Double.parseDouble(s);
            } catch (Exception e) {
                return 0.0d;
            }
        }
        return 0.0d;
    }

    public static long parseLong(String s) {
        if (!TextUtils.isEmpty(s)) {
            try {
                return Long.parseLong(s);
            } catch (Exception e) {
                return 0L;
            }
        }
        return 0L;
    }

    public static int parse16(String s) {
        if (!TextUtils.isEmpty(s)) {
            try {
                return Integer.parseInt(s, 16);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    public static ByteBuffer byte2Buffer(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return ByteBuffer.wrap(bytes);
    }

    public static byte[] buffer2Byte(ByteBuffer buffer) {
        if (buffer == null) {
            return null;
        }
        return buffer.array();
    }
}
