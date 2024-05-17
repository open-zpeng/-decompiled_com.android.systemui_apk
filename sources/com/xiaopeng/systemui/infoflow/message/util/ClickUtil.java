package com.xiaopeng.systemui.infoflow.message.util;
/* loaded from: classes24.dex */
public class ClickUtil {
    private static final int MODE_CHANGE_DURATION = 1000;
    private static final String TAG = ClickUtil.class.getSimpleName();
    private static long lastClickTime;

    public static boolean isChangeModeFastClick() {
        long currentClickTime = System.currentTimeMillis();
        if (currentClickTime - lastClickTime < 1000) {
            return true;
        }
        lastClickTime = currentClickTime;
        return false;
    }
}
