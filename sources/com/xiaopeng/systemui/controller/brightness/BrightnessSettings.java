package com.xiaopeng.systemui.controller.brightness;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carconfig.option.CfcCarOption;
/* loaded from: classes24.dex */
public class BrightnessSettings {
    public static final boolean BRIGHTNESS_DARK_ENV_SUPPORT;
    public static final int BRIGHTNESS_TO_MAX_PROGRESS = 100;
    public static final int BRIGHTNESS_TO_MAX_VALUE = 255;
    public static final int BRIGHTNESS_TO_MIN_PROGRESS = 1;
    public static final int BRIGHTNESS_TO_MIN_VALUE = 1;
    public static final int CHANGED_FROM_HARDWARE = 2;
    public static final int CHANGED_FROM_MANAGER = 3;
    public static final int CHANGED_FROM_SETTINGS = 1;
    public static final int DEF_CMS_BRIGHTNESS = 70;
    public static final int DEF_ICM_BRIGHTNESS = 70;
    public static final int DEF_ICM_BRIGHTNESS_DARK = 20;
    public static final int DEF_ICM_BRIGHTNESS_DAY = 70;
    public static final int DEF_ICM_BRIGHTNESS_NIGHT = 40;
    public static final int DEF_SCREEN_BRIGHTNESS = 179;
    public static final int DEF_SCREEN_BRIGHTNESS_DARK = 51;
    public static final int DEF_SCREEN_BRIGHTNESS_DAY = 179;
    public static final int DEF_SCREEN_BRIGHTNESS_NIGHT = 102;
    public static final int DISPLAY_MAX;
    public static final boolean FO_HAS_CMS;
    public static final int ICM_BRIGHTNESS_MAX = 100;
    public static final int ICM_BRIGHTNESS_MIN = 1;
    public static final String KEY_BRIGHTNESS_CHANGE = "screen_brightness_change";
    public static final String KEY_BRIGHTNESS_DAY = "screen_brightness_day";
    public static final String KEY_BRIGHTNESS_NIGHT = "screen_brightness_night";
    public static final String KEY_CMS_BRIGHTNESS = "screen_brightness_3";
    public static final String KEY_CMS_BRIGHTNESS_MODE = "screen_brightness_mode_3";
    public static final String KEY_DARK_MODE_ADJUST_TYPE = "screen_brightness_dark_adj_type";
    public static final String KEY_ICM_BRIGHTNESS = "screen_brightness_2";
    public static final String KEY_ICM_BRIGHTNESS_CALLBACK = "screen_brightness_callback_2";
    public static final String KEY_ICM_BRIGHTNESS_DAY = "screen_brightness_day_2";
    public static final String KEY_ICM_BRIGHTNESS_MODE = "screen_brightness_mode_2";
    public static final String KEY_ICM_BRIGHTNESS_NIGHT = "screen_brightness_night_2";
    public static final String KEY_LIGHT_INTENSITY = "autolight";
    public static final String KEY_MEDITATION_MODE = "key_system_meditation_mode";
    public static final String KEY_PASSENGER_SCREEN_BRIGHTNESS = "screen_brightness_1";
    public static final String KEY_PASSENGER_SCREEN_BRIGHTNESS_CHANGE = "screen_brightness_change_1";
    public static final String KEY_PASSENGER_SCREEN_BRIGHTNESS_DAY = "screen_brightness_day_1";
    public static final String KEY_PASSENGER_SCREEN_BRIGHTNESS_MODE = "screen_brightness_mode_1";
    public static final String KEY_PASSENGER_SCREEN_BRIGHTNESS_NIGHT = "screen_brightness_night_1";
    public static final String KEY_PASSENGER_SCREEN_TIME = "passenger_screen_time";
    public static final String KEY_PASSENGER_SCREEN_WINDOW_BRIGHTNESS = "screen_window_brightness_1";
    public static final String KEY_SCREEN_BRIGHTNESS = "screen_brightness";
    public static final String KEY_SCREEN_BRIGHTNESS_CHANGE = "screen_brightness_change_0";
    public static final String KEY_SCREEN_BRIGHTNESS_DARK_STATE = "screen_brightness_dark_state";
    public static final String KEY_SCREEN_BRIGHTNESS_DAY = "screen_brightness_day_0";
    public static final String KEY_SCREEN_BRIGHTNESS_FOR_ICM = "screen_brightness_for_2";
    public static final String KEY_SCREEN_BRIGHTNESS_FOR_MODE = "screen_brightness_mode_0";
    public static final String KEY_SCREEN_BRIGHTNESS_MODE = "screen_brightness_mode";
    public static final String KEY_SCREEN_BRIGHTNESS_NIGHT = "screen_brightness_night_0";
    public static final String KEY_SCREEN_WINDOW_BRIGHTNESS = "screen_window_brightness_0";
    public static final String KEY_SYSTEM_GESTURE_EVENT = "key_system_gesture_event";
    public static final String KEY_SYSTEM_REBOOT_MODE = "key_system_reboot_mode";
    public static final String KEY_THEME_STATE = "key_theme_type";
    public static final String KEY_TWILIGHT_MODE = "ui_night_mode";
    public static final String KEY_WAITING_MODE = "wait_mode";
    public static final String KEY_WINDOW_BRIGHTNESS = "screen_window_brightness";
    public static final int MEDITATION_ENTER = 1;
    public static final int MEDITATION_EXIT = 0;
    public static final int MODE_CUSTOM = 6;
    public static final int MODE_DARK = 3;
    public static final int MODE_DAY = 1;
    public static final int MODE_MEDITATION = 4;
    public static final int MODE_NIGHT = 2;
    public static final int MODE_WAITING = 5;
    public static final String PROP_BRIGHTNESS_DARK_ENV_SUPPORT = "persist.sys.xp.brightness.dark.env.support";
    public static final int STATE_THEME_CHANGED = 2;
    public static final int STATE_THEME_PREPARE = 1;
    public static final int STATE_THEME_UNKNOWN = 0;
    public static final String TAG = "XmartBrightness";
    public static final int TYPE_ADJUST_ICM = 4;
    public static final int TYPE_ADJUST_ICM_MASK = 7;
    public static final int TYPE_ADJUST_MASK = 0;
    public static final int TYPE_ADJUST_PASSENGER_MASK = 6;
    public static final int TYPE_ADJUST_PASSENGER_SCREEN = 2;
    public static final int TYPE_ADJUST_SCREEN = 1;
    public static final int TYPE_ADJUST_SCREEN_MASK = 5;
    public static final int TYPE_CMS = 3;
    public static final int TYPE_ICM = 2;
    public static final int TYPE_PASSENGER_SCREEN = 1;
    public static final int TYPE_SCREEN = 0;
    public static final Uri URI_CMS_BRIGHTNESS;
    public static final Uri URI_CMS_BRIGHTNESS_MODE;
    public static final Uri URI_ICM_BRIGHTNESS;
    public static final Uri URI_ICM_BRIGHTNESS_MODE;
    public static final Uri URI_LIGHT_INTENSITY;
    public static final Uri URI_MEDITATION_MODE;
    public static final Uri URI_PANEL_BRIGHTNESS;
    public static final Uri URI_PASSENGER_BRIGHTNESS;
    public static final Uri URI_PASSENGER_SCREEN_BRIGHTNESS_MODE;
    public static final Uri URI_PASSENGER_SCREEN_TIME;
    public static final Uri URI_SCREEN_BRIGHTNESS;
    public static final Uri URI_SCREEN_BRIGHTNESS_FOR_MODE;
    public static final Uri URI_SCREEN_BRIGHTNESS_MODE;
    public static final Uri URI_SYSTEM_GESTURE_EVENT;
    public static final Uri URI_THEME_STATE;
    public static final Uri URI_TWILIGHT_MODE;
    public static final Uri URI_WAITING_MODE;
    public static final int WAITING_ENTER = 1;
    public static final int WAITING_EXIT = 0;
    static final int[] mCentralIntervals;

    static {
        FO_HAS_CMS = CfcCarOption.FO_CMS_SCREEN == 1;
        DISPLAY_MAX = FO_HAS_CMS ? 4 : 3;
        URI_THEME_STATE = Settings.Secure.getUriFor(KEY_THEME_STATE);
        URI_TWILIGHT_MODE = Settings.Secure.getUriFor(KEY_TWILIGHT_MODE);
        URI_MEDITATION_MODE = Settings.Secure.getUriFor("key_system_meditation_mode");
        URI_WAITING_MODE = Settings.Secure.getUriFor(KEY_WAITING_MODE);
        URI_ICM_BRIGHTNESS = Settings.System.getUriFor("screen_brightness_2");
        URI_SCREEN_BRIGHTNESS = Settings.System.getUriFor("screen_brightness");
        URI_PASSENGER_BRIGHTNESS = Settings.System.getUriFor("screen_brightness_1");
        URI_CMS_BRIGHTNESS = Settings.System.getUriFor(KEY_CMS_BRIGHTNESS);
        URI_SCREEN_BRIGHTNESS_MODE = Settings.System.getUriFor(KEY_SCREEN_BRIGHTNESS_MODE);
        URI_SCREEN_BRIGHTNESS_FOR_MODE = Settings.System.getUriFor("screen_brightness_mode_0");
        URI_ICM_BRIGHTNESS_MODE = Settings.System.getUriFor(KEY_ICM_BRIGHTNESS_MODE);
        URI_PASSENGER_SCREEN_BRIGHTNESS_MODE = Settings.System.getUriFor("screen_brightness_mode_1");
        URI_CMS_BRIGHTNESS_MODE = Settings.System.getUriFor(KEY_CMS_BRIGHTNESS_MODE);
        URI_LIGHT_INTENSITY = Settings.System.getUriFor(KEY_LIGHT_INTENSITY);
        URI_PANEL_BRIGHTNESS = Settings.Secure.getUriFor("panel_brightness");
        URI_SYSTEM_GESTURE_EVENT = Settings.Secure.getUriFor("key_system_gesture_event");
        URI_PASSENGER_SCREEN_TIME = Settings.System.getUriFor("passenger_screen_time");
        BRIGHTNESS_DARK_ENV_SUPPORT = SystemProperties.getBoolean(PROP_BRIGHTNESS_DARK_ENV_SUPPORT, true);
        mCentralIntervals = new int[100];
        int i = 0;
        while (true) {
            int[] iArr = mCentralIntervals;
            if (i < iArr.length) {
                iArr[i] = ((int) (i * 2.5656567f)) + 1;
                i++;
            } else {
                return;
            }
        }
    }

    public static int getRealBrightnessByPercent(int percent) {
        if (percent >= 100) {
            return 255;
        }
        if (percent <= 1) {
            return 1;
        }
        return mCentralIntervals[percent - 1];
    }

    public static int getPercentProgressByReal(int realBrightness) {
        if (realBrightness <= 1) {
            return 1;
        }
        if (realBrightness >= 255) {
            return 100;
        }
        int i = 1;
        while (true) {
            int[] iArr = mCentralIntervals;
            if (i >= iArr.length) {
                return 100;
            }
            if (realBrightness >= iArr[i]) {
                i++;
            } else {
                return i;
            }
        }
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
