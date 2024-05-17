package com.xiaopeng.systemui.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.provider.Settings;
import android.text.TextUtils;
import java.util.List;
import java.util.Locale;
/* loaded from: classes24.dex */
public class Utils {
    public static final String CN = "zh_CN";
    public static final String EN = "en";
    public static final int FAST_CLICK_JUDGE_TIME = 500;
    private static final String TAG = "Utils";
    private static long lastClickTime;

    public static boolean isFastClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static String getLanguage() {
        Locale l = Locale.getDefault();
        if (l != null) {
            String language = l.toString();
            if (CN.equals(language) || EN.equals(language)) {
                return language;
            }
        }
        return CN;
    }

    public static boolean isChineseLanguage() {
        String strLan;
        Locale l = Locale.getDefault();
        if (l != null && (strLan = l.toString()) != null && CN.equals(strLan)) {
            return true;
        }
        return false;
    }

    public static void putInt(Context context, String key, int value) {
        if (context != null && !TextUtils.isEmpty(key)) {
            ContentResolver resolver = context.getContentResolver();
            boolean ret = Settings.System.putIntForUser(resolver, key, value, -2);
            com.xiaopeng.systemui.Logger.d(TAG, "putIntForUser key=" + key + " value=" + value + " ret=" + ret);
        }
    }

    public static void startAvatarViewService(Context context, ServiceConnection serviceConnection) {
        Intent intent = new Intent();
        intent.setClassName("com.xiaopeng.aiassistant", "com.xiaopeng.aiavatarview.AvatarViewService");
        context.bindService(intent, serviceConnection, 1);
    }

    public static int convertTemperatureToProgress(float temperature, int scale) {
        return (int) ((temperature / 0.5f) * scale);
    }

    public static float convertProgressToTemperature(int progress, int scale) {
        if (progress > 640 || progress < 360) {
            return 18.0f;
        }
        float temperature = (progress / scale) * 0.5f;
        if (progress % scale > 0 && temperature == 18.0f) {
            return 18.5f;
        }
        return temperature;
    }

    public static String listToString(List<String> list, String symbol) {
        if (list != null && !list.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    sb.append(symbol);
                }
                sb.append(list.get(i));
            }
            return sb.toString();
        }
        return null;
    }
}
