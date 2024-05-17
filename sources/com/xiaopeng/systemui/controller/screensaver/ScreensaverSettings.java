package com.xiaopeng.systemui.controller.screensaver;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import com.xiaopeng.systemui.Logger;
/* loaded from: classes24.dex */
public class ScreensaverSettings {
    public static final String ACTION_SCREENSAVER_EXIT = "com.xiaopeng.broadcast.ACTION_EXIT_SCREENSAVER";
    public static final int DEF_PASSENGER_SCREENSAVER = 1;
    public static final String KEY_PASSENGER_SCREENSAVER = "passenger_screensaver";
    public static final String KEY_PASSENGER_SCREEN_EXTRA_TIME = "passenger_screen_extra_time";
    public static final String KEY_PASSENGER_SCREEN_TIME = "passenger_screen_time";
    public static final String KEY_SYSTEM_GESTURE_EVENT = "key_system_gesture_event";
    public static final int MEDIA_PLAY = 3;
    public static final int SCREENSAVER_OFF = 4;
    public static final int SCREENSAVER_ON = 1;
    public static final String SCREEN_IDLE_CHANGE_ACTION = "com.xiaopeng.broadcast.ACTION_SCREEN_IDLE_CHANGE";
    public static final int SCREEN_OFF = 5;
    public static int SCREEN_OFF_TIMEOUT = 0;
    public static final String SCREEN_STATUS_CHANGE_ACTION = "com.xiaopeng.broadcast.ACTION_SCREEN_STATUS_CHANGE";
    public static final int SHOW_TOAST = 2;
    public static final String TAG = "ScreensaverSettings";
    public static int TOAST_TIMEOUT;
    public static final Uri URI_PASSENGER_SCREENSAVER;
    public static final Uri URI_PASSENGER_SCREEN_EXTRA_TIME;
    public static final Uri URI_PASSENGER_SCREEN_TIME;
    public static final Uri URI_SYSTEM_GESTURE_EVENT;
    public static final int DEF_PASSENGER_SCREEN_TIME = 180000;
    public static int SCREEN_TIMEOUT = DEF_PASSENGER_SCREEN_TIME;
    public static final int DEF_PASSENGER_SCREEN_EXTRA_TIME = 1800000;
    public static int SCREEN_EXTRA_TIMEOUT = DEF_PASSENGER_SCREEN_EXTRA_TIME;

    static {
        int i = SCREEN_TIMEOUT;
        SCREEN_OFF_TIMEOUT = SCREEN_EXTRA_TIMEOUT + i;
        TOAST_TIMEOUT = i - 5000;
        URI_SYSTEM_GESTURE_EVENT = Settings.Secure.getUriFor("key_system_gesture_event");
        URI_PASSENGER_SCREEN_TIME = Settings.System.getUriFor("passenger_screen_time");
        URI_PASSENGER_SCREEN_EXTRA_TIME = Settings.System.getUriFor(KEY_PASSENGER_SCREEN_EXTRA_TIME);
        URI_PASSENGER_SCREENSAVER = Settings.System.getUriFor(KEY_PASSENGER_SCREENSAVER);
    }

    public static int getInt(Context context, String key, int defaultValue) {
        if (context != null) {
            try {
                if (!TextUtils.isEmpty(key)) {
                    ContentResolver resolver = context.getContentResolver();
                    return Settings.System.getIntForUser(resolver, key, defaultValue, -2);
                }
                return 0;
            } catch (Exception e) {
                Logger.i(TAG, "getIntForUser e=" + e);
                return 0;
            }
        }
        return 0;
    }

    public static void putInt(Context context, String key, int value) {
        if (context != null) {
            try {
                if (!TextUtils.isEmpty(key)) {
                    ContentResolver resolver = context.getContentResolver();
                    boolean ret = Settings.System.putIntForUser(resolver, key, value, -2);
                    Logger.d(TAG, "putIntForUser key=" + key + " value=" + value + " ret=" + ret);
                }
            } catch (Exception e) {
                Logger.i(TAG, "putIntForUser e=" + e);
            }
        }
    }
}
