package com.xiaopeng.xui.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import com.xiaopeng.xui.Xui;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes25.dex */
public class XuiUtils {
    private static volatile DisplayMetrics sDisplayMetrics;

    public static int getScreenWidth() {
        return Xui.getContext().getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Xui.getContext().getResources().getDisplayMetrics().heightPixels;
    }

    public static int getDisplayWidth() {
        return getDisplayMetrics().widthPixels;
    }

    private static DisplayMetrics getDisplayMetrics() {
        if (sDisplayMetrics == null) {
            synchronized (XuiUtils.class) {
                if (sDisplayMetrics == null) {
                    sDisplayMetrics = new DisplayMetrics();
                    WindowManager windowManager = (WindowManager) Xui.getContext().getSystemService("window");
                    if (windowManager != null) {
                        windowManager.getDefaultDisplay().getRealMetrics(sDisplayMetrics);
                    }
                }
            }
        }
        return sDisplayMetrics;
    }

    public static int getDisplayHeight() {
        return getDisplayMetrics().heightPixels;
    }

    public static int dip2px(float dipValue) {
        float density = Xui.getContext().getResources().getDisplayMetrics().density;
        return (int) ((dipValue * density) + 0.5f);
    }

    public static int dip2px(Context context, @DimenRes int id) {
        float scale = context.getResources().getDisplayMetrics().density;
        float value = context.getResources().getDimension(id);
        return (int) ((value * scale) + 0.5f);
    }

    public static int px2dip(float pxValue) {
        float density = Xui.getContext().getResources().getDisplayMetrics().density;
        return (int) ((pxValue / density) + 0.5f);
    }

    public static int sp2px(float spValue) {
        float fontScale = Xui.getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) ((spValue * fontScale) + 0.5f);
    }

    public static int px2sp(float pxValue) {
        float fontScale = Xui.getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) ((pxValue / fontScale) + 0.5f);
    }

    public static int getColor(@ColorRes int resColor, @Nullable Resources.Theme theme) {
        return Xui.getContext().getResources().getColor(resColor, theme);
    }

    public static float getFontScale() {
        return Xui.getContext().getResources().getDisplayMetrics().scaledDensity;
    }

    public static List<View> findViewsByType(ViewGroup viewGroup, Class cls) {
        List<View> results = new ArrayList<>();
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (cls.isInstance(child)) {
                results.add(child);
            } else if (child instanceof ViewGroup) {
                results.addAll(findViewsByType((ViewGroup) child, cls));
            }
        }
        return results;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public static String formatVisibility(int visibility) {
        if (visibility != 0) {
            if (visibility != 4) {
                if (visibility == 8) {
                    return "gone";
                }
                return null;
            }
            return "invisible";
        }
        return "visible";
    }
}
