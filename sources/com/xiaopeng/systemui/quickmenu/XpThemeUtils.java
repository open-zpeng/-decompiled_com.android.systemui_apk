package com.xiaopeng.systemui.quickmenu;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.Window;
import com.xiaopeng.libtheme.ThemeManager;
/* loaded from: classes24.dex */
public class XpThemeUtils {
    public static boolean isThemeChanged(Configuration newConfig) {
        if (newConfig != null) {
            return ThemeManager.isThemeChanged(newConfig);
        }
        return false;
    }

    public static void setThemeChanged(Context context, View rootView, String xml, Configuration newConfig) {
        ThemeManager.create(context, rootView, xml, null).onConfigurationChanged(newConfig);
    }

    public static void setDayNightMode(Context context, int daynightMode) {
        UiModeManager uim = (UiModeManager) context.getSystemService("uimode");
        uim.applyDayNightMode(daynightMode);
    }

    public static int getDayNightAutoMode(Context context) {
        UiModeManager uim = (UiModeManager) context.getSystemService("uimode");
        return uim.getDayNightAutoMode();
    }

    public static int getDayNightMode(Context context) {
        UiModeManager uim = (UiModeManager) context.getSystemService("uimode");
        return uim.getDayNightMode();
    }

    public static void setWindowBackgroundResource(Configuration newConfig, Window window, int res) {
        ThemeManager.setWindowBackgroundResource(newConfig, window, res);
    }

    public static void setThemeMode(Context context, int themeId) {
        UiModeManager uim = (UiModeManager) context.getSystemService("uimode");
        uim.applyThemeMode(themeId);
    }

    public static void setThemeMode(Context context, String style) {
        UiModeManager uim = (UiModeManager) context.getSystemService("uimode");
        uim.applyThemeStyle(style);
    }

    public static int getThemeMode(Context context) {
        UiModeManager uim = (UiModeManager) context.getSystemService("uimode");
        return uim.getThemeMode();
    }
}
