package com.xiaopeng.xui.theme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.libtheme.ThemeView;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
/* loaded from: classes25.dex */
public class XThemeManager {
    public static void setWindowBackgroundResource(Configuration newConfig, Window window, int resId) {
        ThemeManager.setWindowBackgroundResource(newConfig, window, resId);
    }

    public static void setWindowBackgroundDrawable(Configuration newConfig, Window window, Drawable drawable) {
        ThemeManager.setWindowBackgroundDrawable(newConfig, window, drawable);
    }

    public static boolean isThemeChanged(Configuration newConfig) {
        return ThemeManager.isThemeChanged(newConfig);
    }

    public static void onConfigurationChanged(Configuration newConfig, Context context, View root, String xml, List<ThemeView> list) {
        ThemeManager theme = ThemeManager.create(context, root, xml, list);
        theme.onConfigurationChanged(newConfig);
    }

    public static int getDatNightMode(Context context) {
        return ThemeManager.getDayNightMode(context);
    }

    public static boolean isNight(Context context) {
        return isNightMode(context);
    }

    public static boolean isNight(Configuration config) {
        return config != null && (config.uiMode & 48) == 32;
    }

    public static HashMap<String, Integer> resolveAttribute(Context context, AttributeSet attrs) {
        return ThemeManager.resolveAttribute(context, attrs);
    }

    public static void updateAttribute(View view, HashMap<String, Integer> map) {
        ThemeManager.updateAttribute(view, map);
    }

    public static int getThemeMode(Context context) {
        return ThemeManager.getThemeMode(context);
    }

    public static boolean isNightMode(Context context) {
        return ThemeManager.isNightMode(context);
    }

    public static String getThemeStyle() {
        String style = getSystemProperties("persist.sys.theme.style");
        return style == null ? "" : style;
    }

    @SuppressLint({"PrivateApi"})
    private static String getSystemProperties(String key) {
        try {
            Class cls = Class.forName("android.os.SystemProperties");
            Method m = cls.getDeclaredMethod("get", String.class);
            return (String) m.invoke(null, key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
