package com.android.systemui.doze;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.KeyValueListParser;
import android.util.Log;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class AlwaysOnDisplayPolicy {
    private static final long DEFAULT_PROX_COOLDOWN_PERIOD_MS = 5000;
    private static final long DEFAULT_PROX_COOLDOWN_TRIGGER_MS = 2000;
    private static final long DEFAULT_PROX_SCREEN_OFF_DELAY_MS = 10000;
    private static final long DEFAULT_WALLPAPER_FADE_OUT_MS = 400;
    private static final long DEFAULT_WALLPAPER_VISIBILITY_MS = 60000;
    static final String KEY_DIMMING_SCRIM_ARRAY = "dimming_scrim_array";
    static final String KEY_PROX_COOLDOWN_PERIOD_MS = "prox_cooldown_period";
    static final String KEY_PROX_COOLDOWN_TRIGGER_MS = "prox_cooldown_trigger";
    static final String KEY_PROX_SCREEN_OFF_DELAY_MS = "prox_screen_off_delay";
    static final String KEY_SCREEN_BRIGHTNESS_ARRAY = "screen_brightness_array";
    static final String KEY_WALLPAPER_FADE_OUT_MS = "wallpaper_fade_out_duration";
    static final String KEY_WALLPAPER_VISIBILITY_MS = "wallpaper_visibility_timeout";
    public static final String TAG = "AlwaysOnDisplayPolicy";
    public int[] dimmingScrimArray;
    private final Context mContext;
    private final KeyValueListParser mParser;
    private SettingsObserver mSettingsObserver;
    public long proxCooldownPeriodMs;
    public long proxCooldownTriggerMs;
    public long proxScreenOffDelayMs;
    public int[] screenBrightnessArray;
    public long wallpaperFadeOutDuration;
    public long wallpaperVisibilityDuration;

    public AlwaysOnDisplayPolicy(Context context) {
        Context context2 = context.getApplicationContext();
        this.mContext = context2;
        this.mParser = new KeyValueListParser(',');
        this.mSettingsObserver = new SettingsObserver(context2.getMainThreadHandler());
        this.mSettingsObserver.observe();
    }

    /* loaded from: classes21.dex */
    private final class SettingsObserver extends ContentObserver {
        private final Uri ALWAYS_ON_DISPLAY_CONSTANTS_URI;

        SettingsObserver(Handler handler) {
            super(handler);
            this.ALWAYS_ON_DISPLAY_CONSTANTS_URI = Settings.Global.getUriFor("always_on_display_constants");
        }

        void observe() {
            ContentResolver resolver = AlwaysOnDisplayPolicy.this.mContext.getContentResolver();
            resolver.registerContentObserver(this.ALWAYS_ON_DISPLAY_CONSTANTS_URI, false, this, -1);
            update(null);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            update(uri);
        }

        public void update(Uri uri) {
            if (uri == null || this.ALWAYS_ON_DISPLAY_CONSTANTS_URI.equals(uri)) {
                Resources resources = AlwaysOnDisplayPolicy.this.mContext.getResources();
                String value = Settings.Global.getString(AlwaysOnDisplayPolicy.this.mContext.getContentResolver(), "always_on_display_constants");
                try {
                    AlwaysOnDisplayPolicy.this.mParser.setString(value);
                } catch (IllegalArgumentException e) {
                    Log.e(AlwaysOnDisplayPolicy.TAG, "Bad AOD constants");
                }
                AlwaysOnDisplayPolicy alwaysOnDisplayPolicy = AlwaysOnDisplayPolicy.this;
                alwaysOnDisplayPolicy.proxScreenOffDelayMs = alwaysOnDisplayPolicy.mParser.getLong(AlwaysOnDisplayPolicy.KEY_PROX_SCREEN_OFF_DELAY_MS, (long) AlwaysOnDisplayPolicy.DEFAULT_PROX_SCREEN_OFF_DELAY_MS);
                AlwaysOnDisplayPolicy alwaysOnDisplayPolicy2 = AlwaysOnDisplayPolicy.this;
                alwaysOnDisplayPolicy2.proxCooldownTriggerMs = alwaysOnDisplayPolicy2.mParser.getLong(AlwaysOnDisplayPolicy.KEY_PROX_COOLDOWN_TRIGGER_MS, 2000L);
                AlwaysOnDisplayPolicy alwaysOnDisplayPolicy3 = AlwaysOnDisplayPolicy.this;
                alwaysOnDisplayPolicy3.proxCooldownPeriodMs = alwaysOnDisplayPolicy3.mParser.getLong(AlwaysOnDisplayPolicy.KEY_PROX_COOLDOWN_PERIOD_MS, (long) AlwaysOnDisplayPolicy.DEFAULT_PROX_COOLDOWN_PERIOD_MS);
                AlwaysOnDisplayPolicy alwaysOnDisplayPolicy4 = AlwaysOnDisplayPolicy.this;
                alwaysOnDisplayPolicy4.wallpaperFadeOutDuration = alwaysOnDisplayPolicy4.mParser.getLong(AlwaysOnDisplayPolicy.KEY_WALLPAPER_FADE_OUT_MS, (long) AlwaysOnDisplayPolicy.DEFAULT_WALLPAPER_FADE_OUT_MS);
                AlwaysOnDisplayPolicy alwaysOnDisplayPolicy5 = AlwaysOnDisplayPolicy.this;
                alwaysOnDisplayPolicy5.wallpaperVisibilityDuration = alwaysOnDisplayPolicy5.mParser.getLong(AlwaysOnDisplayPolicy.KEY_WALLPAPER_VISIBILITY_MS, 60000L);
                AlwaysOnDisplayPolicy alwaysOnDisplayPolicy6 = AlwaysOnDisplayPolicy.this;
                alwaysOnDisplayPolicy6.screenBrightnessArray = alwaysOnDisplayPolicy6.mParser.getIntArray(AlwaysOnDisplayPolicy.KEY_SCREEN_BRIGHTNESS_ARRAY, resources.getIntArray(R.array.config_doze_brightness_sensor_to_brightness));
                AlwaysOnDisplayPolicy alwaysOnDisplayPolicy7 = AlwaysOnDisplayPolicy.this;
                alwaysOnDisplayPolicy7.dimmingScrimArray = alwaysOnDisplayPolicy7.mParser.getIntArray(AlwaysOnDisplayPolicy.KEY_DIMMING_SCRIM_ARRAY, resources.getIntArray(R.array.config_doze_brightness_sensor_to_scrim_opacity));
            }
        }
    }
}
