package com.xiaopeng.speech.common.util;

import android.app.ActivityThread;
import android.content.Context;
/* loaded from: classes23.dex */
public class ResourceUtils {
    public static String getString(int resId) {
        return getContext().getString(resId);
    }

    public static Context getContext() {
        return ActivityThread.currentApplication();
    }
}
