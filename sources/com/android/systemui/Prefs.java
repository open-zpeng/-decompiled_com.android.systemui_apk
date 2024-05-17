package com.android.systemui;

import android.content.Context;
import android.content.SharedPreferences;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
import java.util.Set;
/* loaded from: classes21.dex */
public final class Prefs {

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes21.dex */
    public @interface Key {
        public static final String COLOR_INVERSION_TILE_LAST_USED = "ColorInversionTileLastUsed";
        public static final String DEBUG_MODE_ENABLED = "debugModeEnabled";
        public static final String DISMISSED_RECENTS_SWIPE_UP_ONBOARDING_COUNT = "DismissedRecentsSwipeUpOnboardingCount";
        public static final String DND_CONFIRMED_ALARM_INTRODUCTION = "DndConfirmedAlarmIntroduction";
        public static final String DND_CONFIRMED_PRIORITY_INTRODUCTION = "DndConfirmedPriorityIntroduction";
        public static final String DND_CONFIRMED_SILENCE_INTRODUCTION = "DndConfirmedSilenceIntroduction";
        public static final String DND_FAVORITE_BUCKET_INDEX = "DndCountdownMinuteIndex";
        public static final String DND_FAVORITE_ZEN = "DndFavoriteZen";
        public static final String DND_NONE_SELECTED = "DndNoneSelected";
        public static final String DND_TILE_COMBINED_ICON = "DndTileCombinedIcon";
        public static final String DND_TILE_VISIBLE = "DndTileVisible";
        public static final String HAS_DISMISSED_RECENTS_QUICK_SCRUB_ONBOARDING_ONCE = "HasDismissedRecentsQuickScrubOnboardingOnce";
        public static final String HAS_SEEN_ODI_CAPTIONS_TOOLTIP = "HasSeenODICaptionsTooltip";
        public static final String HAS_SEEN_RECENTS_QUICK_SCRUB_ONBOARDING = "HasSeenRecentsQuickScrubOnboarding";
        public static final String HAS_SEEN_RECENTS_SWIPE_UP_ONBOARDING = "HasSeenRecentsSwipeUpOnboarding";
        public static final String HOTSPOT_TILE_LAST_USED = "HotspotTileLastUsed";
        @Deprecated
        public static final String OVERVIEW_LAST_STACK_TASK_ACTIVE_TIME = "OverviewLastStackTaskActiveTime";
        public static final String OVERVIEW_OPENED_COUNT = "OverviewOpenedCount";
        public static final String OVERVIEW_OPENED_FROM_HOME_COUNT = "OverviewOpenedFromHomeCount";
        @Deprecated
        public static final String QS_DATA_SAVER_ADDED = "QsDataSaverAdded";
        public static final String QS_DATA_SAVER_DIALOG_SHOWN = "QsDataSaverDialogShown";
        public static final String QS_HAS_TURNED_OFF_MOBILE_DATA = "QsHasTurnedOffMobileData";
        @Deprecated
        public static final String QS_HOTSPOT_ADDED = "QsHotspotAdded";
        @Deprecated
        public static final String QS_INVERT_COLORS_ADDED = "QsInvertColorsAdded";
        public static final String QS_LONG_PRESS_TOOLTIP_SHOWN_COUNT = "QsLongPressTooltipShownCount";
        @Deprecated
        public static final String QS_NIGHTDISPLAY_ADDED = "QsNightDisplayAdded";
        public static final String QS_TILE_SPECS_REVEALED = "QsTileSpecsRevealed";
        @Deprecated
        public static final String QS_WORK_ADDED = "QsWorkAdded";
        public static final String SEEN_MULTI_USER = "HasSeenMultiUser";
        public static final String SEEN_RINGER_GUIDANCE_COUNT = "RingerGuidanceCount";
        public static final String TOUCHED_RINGER_TOGGLE = "TouchedRingerToggle";
    }

    private Prefs() {
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return get(context).getBoolean(key, defaultValue);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        get(context).edit().putBoolean(key, value).apply();
    }

    public static int getInt(Context context, String key, int defaultValue) {
        return get(context).getInt(key, defaultValue);
    }

    public static void putInt(Context context, String key, int value) {
        get(context).edit().putInt(key, value).apply();
    }

    public static long getLong(Context context, String key, long defaultValue) {
        return get(context).getLong(key, defaultValue);
    }

    public static void putLong(Context context, String key, long value) {
        get(context).edit().putLong(key, value).apply();
    }

    public static String getString(Context context, String key, String defaultValue) {
        return get(context).getString(key, defaultValue);
    }

    public static void putString(Context context, String key, String value) {
        get(context).edit().putString(key, value).apply();
    }

    public static void putStringSet(Context context, String key, Set<String> value) {
        get(context).edit().putStringSet(key, value).apply();
    }

    public static Set<String> getStringSet(Context context, String key, Set<String> defaultValue) {
        return get(context).getStringSet(key, defaultValue);
    }

    public static Map<String, ?> getAll(Context context) {
        return get(context).getAll();
    }

    public static void remove(Context context, String key) {
        get(context).edit().remove(key).apply();
    }

    public static void registerListener(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        get(context).registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterListener(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        get(context).unregisterOnSharedPreferenceChangeListener(listener);
    }

    private static SharedPreferences get(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0);
    }
}
