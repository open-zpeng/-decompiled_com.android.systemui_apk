package com.android.systemui;

import android.view.View;
import android.widget.TextView;
/* loaded from: classes21.dex */
public class FontSizeUtils {
    public static final float LARGE_TEXT_SCALE = 1.3f;

    public static void updateFontSize(View parent, int viewId, int dimensId) {
        updateFontSize((TextView) parent.findViewById(viewId), dimensId);
    }

    public static void updateFontSize(TextView v, int dimensId) {
        if (v != null) {
            v.setTextSize(0, v.getResources().getDimensionPixelSize(dimensId));
        }
    }
}
