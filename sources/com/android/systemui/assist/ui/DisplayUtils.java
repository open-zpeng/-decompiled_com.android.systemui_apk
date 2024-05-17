package com.android.systemui.assist.ui;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
/* loaded from: classes21.dex */
public class DisplayUtils {
    public static int convertDpToPx(float dp, Context context) {
        Display d = context.getDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        d.getRealMetrics(dm);
        return (int) Math.ceil(dm.density * dp);
    }

    public static int getWidth(Context context) {
        Display d = context.getDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        d.getRealMetrics(dm);
        int rotation = d.getRotation();
        if (rotation == 0 || rotation == 2) {
            return dm.widthPixels;
        }
        return dm.heightPixels;
    }

    public static int getHeight(Context context) {
        Display d = context.getDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        d.getRealMetrics(dm);
        int rotation = d.getRotation();
        if (rotation == 0 || rotation == 2) {
            return dm.heightPixels;
        }
        return dm.widthPixels;
    }

    public static int getCornerRadiusBottom(Context context) {
        int radius = 0;
        int resourceId = context.getResources().getIdentifier("config_rounded_mask_size_bottom", "dimen", "com.android.systemui");
        if (resourceId > 0) {
            radius = context.getResources().getDimensionPixelSize(resourceId);
        }
        if (radius == 0) {
            int radius2 = getCornerRadiusDefault(context);
            return radius2;
        }
        return radius;
    }

    public static int getCornerRadiusTop(Context context) {
        int radius = 0;
        int resourceId = context.getResources().getIdentifier("config_rounded_mask_size_top", "dimen", "com.android.systemui");
        if (resourceId > 0) {
            radius = context.getResources().getDimensionPixelSize(resourceId);
        }
        if (radius == 0) {
            int radius2 = getCornerRadiusDefault(context);
            return radius2;
        }
        return radius;
    }

    private static int getCornerRadiusDefault(Context context) {
        int resourceId = context.getResources().getIdentifier("config_rounded_mask_size", "dimen", "com.android.systemui");
        if (resourceId <= 0) {
            return 0;
        }
        int radius = context.getResources().getDimensionPixelSize(resourceId);
        return radius;
    }
}
