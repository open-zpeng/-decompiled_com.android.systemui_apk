package com.android.systemui.shared.system;

import android.content.Context;
import android.content.res.Resources;
import android.view.ViewConfiguration;
import com.android.internal.policy.ScreenDecorationsUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.StringJoiner;
/* loaded from: classes21.dex */
public class QuickStepContract {
    public static final String ACTION_ENABLE_GESTURE_NAV = "com.android.systemui.ENABLE_GESTURE_NAV";
    public static final String ACTION_ENABLE_GESTURE_NAV_RESULT = "com.android.systemui.action.ENABLE_GESTURE_NAV_RESULT";
    public static final String EXTRA_RESULT_INTENT = "com.android.systemui.EXTRA_RESULT_INTENT";
    public static final String KEY_EXTRA_INPUT_MONITOR = "extra_input_monitor";
    public static final String KEY_EXTRA_SUPPORTS_WINDOW_CORNERS = "extra_supports_window_corners";
    public static final String KEY_EXTRA_SYSUI_PROXY = "extra_sysui_proxy";
    public static final String KEY_EXTRA_WINDOW_CORNER_RADIUS = "extra_window_corner_radius";
    public static final String NAV_BAR_MODE_2BUTTON_OVERLAY = "com.android.internal.systemui.navbar.twobutton";
    public static final String NAV_BAR_MODE_3BUTTON_OVERLAY = "com.android.internal.systemui.navbar.threebutton";
    public static final String NAV_BAR_MODE_GESTURAL_OVERLAY = "com.android.internal.systemui.navbar.gestural";
    public static final float QUICKSTEP_TOUCH_SLOP_RATIO = 3.0f;
    public static final int SYSUI_STATE_A11Y_BUTTON_CLICKABLE = 16;
    public static final int SYSUI_STATE_A11Y_BUTTON_LONG_CLICKABLE = 32;
    public static final int SYSUI_STATE_ASSIST_GESTURE_CONSTRAINED = 4096;
    public static final int SYSUI_STATE_BOUNCER_SHOWING = 8;
    public static final int SYSUI_STATE_HOME_DISABLED = 256;
    public static final int SYSUI_STATE_NAV_BAR_HIDDEN = 2;
    public static final int SYSUI_STATE_NOTIFICATION_PANEL_EXPANDED = 4;
    public static final int SYSUI_STATE_OVERVIEW_DISABLED = 128;
    public static final int SYSUI_STATE_QUICK_SETTINGS_EXPANDED = 2048;
    public static final int SYSUI_STATE_SCREEN_PINNING = 1;
    public static final int SYSUI_STATE_SEARCH_DISABLED = 1024;
    public static final int SYSUI_STATE_STATUS_BAR_KEYGUARD_SHOWING = 64;
    public static final int SYSUI_STATE_STATUS_BAR_KEYGUARD_SHOWING_OCCLUDED = 512;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes21.dex */
    public @interface SystemUiStateFlags {
    }

    public static String getSystemUiStateString(int flags) {
        StringJoiner str = new StringJoiner("|");
        str.add((flags & 1) != 0 ? "screen_pinned" : "");
        str.add((flags & 128) != 0 ? "overview_disabled" : "");
        str.add((flags & 256) != 0 ? "home_disabled" : "");
        str.add((flags & 1024) != 0 ? "search_disabled" : "");
        str.add((flags & 2) != 0 ? "navbar_hidden" : "");
        str.add((flags & 4) != 0 ? "notif_visible" : "");
        str.add((flags & 2048) != 0 ? "qs_visible" : "");
        str.add((flags & 64) != 0 ? "keygrd_visible" : "");
        str.add((flags & 512) != 0 ? "keygrd_occluded" : "");
        str.add((flags & 8) != 0 ? "bouncer_visible" : "");
        str.add((flags & 16) != 0 ? "a11y_click" : "");
        str.add((flags & 32) != 0 ? "a11y_long_click" : "");
        str.add((flags & 4096) != 0 ? "asst_gesture_constrain" : "");
        return str.toString();
    }

    public static final float getQuickStepTouchSlopPx(Context context) {
        return ViewConfiguration.get(context).getScaledTouchSlop() * 3.0f;
    }

    public static int getQuickStepDragSlopPx() {
        return convertDpToPixel(10.0f);
    }

    public static int getQuickStepTouchSlopPx() {
        return convertDpToPixel(24.0f);
    }

    public static int getQuickScrubTouchSlopPx() {
        return convertDpToPixel(24.0f);
    }

    private static int convertDpToPixel(float dp) {
        return (int) (Resources.getSystem().getDisplayMetrics().density * dp);
    }

    public static boolean isAssistantGestureDisabled(int sysuiStateFlags) {
        if ((sysuiStateFlags & 3083) != 0) {
            return true;
        }
        return (sysuiStateFlags & 4) != 0 && (sysuiStateFlags & 64) == 0;
    }

    public static boolean isBackGestureDisabled(int sysuiStateFlags) {
        return (sysuiStateFlags & 8) == 0 && (sysuiStateFlags & 70) != 0;
    }

    public static boolean isGesturalMode(int mode) {
        return mode == 2;
    }

    public static boolean isSwipeUpMode(int mode) {
        return mode == 1;
    }

    public static boolean isLegacyMode(int mode) {
        return mode == 0;
    }

    public static float getWindowCornerRadius(Resources resources) {
        return ScreenDecorationsUtils.getWindowCornerRadius(resources);
    }

    public static boolean supportsRoundedCornersOnWindows(Resources resources) {
        return ScreenDecorationsUtils.supportsRoundedCornersOnWindows(resources);
    }
}
