package com.xiaopeng.xui.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;
import java.util.HashMap;
/* loaded from: classes25.dex */
public class XFontUtils {
    private static final String BASE_FONT_DIR = "font/";
    private static final String BASE_SYSTEM_FONT_DIR = "/system/fonts/";
    public static final String FONT_XP_MEDIUM = "Xpeng-Medium.ttf";
    public static final String FONT_XP_NUMBER = "Xpeng-Number.ttf";
    public static final String FONT_XP_NUMBER_BOLD = "Xpeng-Number-Bold.ttf";
    private static HashMap<String, Typeface> fonts = new HashMap<>();

    public static Typeface getAssetsTypeface(Context context, String name) {
        Typeface typeface = fonts.get(name);
        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), getFontPath(name));
                if (typeface != null) {
                    fonts.put(name, typeface);
                }
            } catch (Exception e) {
            }
        }
        return typeface;
    }

    public static Typeface getSystemTypeface(Context context, String name) {
        Typeface typeface = fonts.get(name);
        if (typeface == null) {
            try {
                typeface = Typeface.createFromFile(getSystemFontPath(name));
            } catch (Exception e) {
            }
            fonts.put(name, typeface);
        }
        return typeface;
    }

    private static String getFontPath(String name) {
        return BASE_FONT_DIR + name;
    }

    private static String getSystemFontPath(String name) {
        return BASE_SYSTEM_FONT_DIR + name;
    }

    public static void applyTypeface(TextView view, String font) {
        applyTypeface(view, font, false);
    }

    public static void applyTypeface(TextView view, String font, boolean includeFontPadding) {
        if (view == null) {
            return;
        }
        Typeface typeface = getSystemTypeface(view.getContext(), font);
        if (typeface == null) {
            typeface = getAssetsTypeface(view.getContext(), font);
        }
        if (typeface != null) {
            view.setTypeface(typeface);
            view.setIncludeFontPadding(includeFontPadding);
        }
    }
}
