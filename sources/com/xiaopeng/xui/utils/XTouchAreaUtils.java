package com.xiaopeng.xui.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.xiaopeng.xui.view.touch.XTouchTargetUtils;
import java.util.List;
/* loaded from: classes25.dex */
public class XTouchAreaUtils {
    public static final Class[] CLASSES = {TextView.class, ImageView.class};
    public static final int MIN_PADDING = 20;

    public static void extendTouchArea(ViewGroup viewGroup) {
        extendTouchArea(CLASSES, viewGroup);
    }

    public static void extendTouchArea(Class<?>[] classes, ViewGroup viewGroup) {
        extendTouchArea(classes, viewGroup, (int[]) null);
    }

    public static void extendTouchArea(Class<?>[] classes, ViewGroup viewGroup, int[] padding) {
        for (Class<?> cls : classes) {
            List<View> views = XuiUtils.findViewsByType(viewGroup, cls);
            if (views.size() > 0) {
                View[] views1 = new View[views.size()];
                views.toArray(views1);
                extendTouchArea(views1, viewGroup, padding);
            }
        }
    }

    public static void extendTouchArea(View[] views, ViewGroup viewGroup) {
        extendTouchArea(views, viewGroup, (int[]) null);
    }

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

    public static void extendTouchAreaAsParentSameSize(ViewGroup parent) {
        extendTouchAreaAsParentSameSize(CLASSES, parent);
    }

    public static void extendTouchAreaAsParentSameSize(Class<?>[] classes, ViewGroup parent) {
        for (Class<?> cls : classes) {
            List<View> views = XuiUtils.findViewsByType(parent, cls);
            if (views.size() > 0) {
                View[] views1 = new View[views.size()];
                views.toArray(views1);
                extendTouchAreaAsParentSameSize(views1, parent);
            }
        }
    }

    public static void extendTouchAreaAsParentSameSize(View[] views, ViewGroup parent) {
        for (View view : views) {
            extendTouchAreaAsParentSameSize(view, parent);
        }
    }

    public static void extendTouchAreaAsParentSameSize(View view, ViewGroup parent) {
        if (view == null || parent == null) {
            return;
        }
        XTouchTargetUtils.extendTouchAreaAsParentSameSize(view, parent);
    }
}
