package com.xiaopeng.systemui.infoflow.message.util;

import android.content.Context;
import android.content.SharedPreferences;
/* loaded from: classes24.dex */
public class SharedPreferenceUtil {
    public static final String DATA_PRE_INSTALLED_APPS_INFO = "PreInstalledAppsInfo";
    public static final String DATA_SECOND_SCREEN_APPS_INFO = "SecondScreenAppsINFO";
    public static final String PREF_FILE_NAME = "preferences";

    public static int get(Context context, String fileName, String key, int defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, 0);
        int value = sharedPreferences.getInt(key, defaultValue);
        return value;
    }

    public static long getLong(Context context, String fileName, String key, long defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, 0);
        return sharedPreferences.getLong(key, defaultValue);
    }

    public static boolean getBoolean(Context context, String fileName, String key, boolean defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, 0);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static String getString(Context context, String fileName, String key, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, 0);
        return sharedPreferences.getString(key, defaultValue);
    }

    public static void set(Context context, String fileName, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void set(Context context, String fileName, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void set(Context context, String fileName, String key, long value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void set(Context context, String fileName, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
