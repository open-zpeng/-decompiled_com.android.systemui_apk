package com.xiaopeng.systemui.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
/* loaded from: classes24.dex */
public class XTouchAreaUtils {
    public static final Class[] CLASSES = {TextView.class, ImageView.class};
    public static final int MIN_PADDING = 20;

    public static void extendTouchArea(View[] views, ViewGroup viewGroup, int[] padding) {
        for (View view : views) {
            extendTouchArea(view, viewGroup, padding);
        }
    }

    public static void extendTouchArea(View view, ViewGroup viewGroup) {
        extendTouchArea(view, viewGroup, (int[]) null);
    }

    public static void extendTouchArea(View view, ViewGroup viewGroup, int[] padding) {
        if (view == null) {
            return;
        }
        if (padding == null) {
            padding = new int[]{20, 20, 20, 20};
        }
        XTouchTargetUtils.extendViewTouchTarget(view, viewGroup, padding[0], padding[1], padding[2], padding[3]);
    }
}
