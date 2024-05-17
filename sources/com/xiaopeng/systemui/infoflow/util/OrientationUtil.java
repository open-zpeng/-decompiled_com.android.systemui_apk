package com.xiaopeng.systemui.infoflow.util;

import android.content.Context;
/* loaded from: classes24.dex */
public class OrientationUtil {
    public static boolean isLandscapeScreen(Context context) {
        return context.getResources().getConfiguration().orientation == 2;
    }
}
