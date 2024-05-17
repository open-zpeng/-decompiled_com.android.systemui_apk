package com.xiaopeng.xui;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.xiaopeng.xpui.BuildConfig;
import com.xiaopeng.xui.drawable.shimmer.XShimmer;
import com.xiaopeng.xui.utils.XLogUtils;
import java.lang.reflect.Method;
/* loaded from: classes25.dex */
public class Xui {
    private static Application mApp;
    private static String mPackageName;
    private static boolean sDialogFullScreen;
    private static boolean sFontScaleDynamicChangeEnable;
    private static boolean sSharedDisplay;
    private static boolean sVuiEnable;

    public static void init(Application app) {
        mApp = app;
        XShimmer.msGlobalEnable = false;
        String enable = getSystemProperties("persist.sys.xp.shared_display.enable");
        if ("1".equals(enable)) {
            sSharedDisplay = true;
        }
        mPackageName = app.getPackageName();
        Log.i("xpui", sSharedDisplay + "," + BuildConfig.BUILD_VERSION);
    }

    public static Context getContext() {
        Application application = mApp;
        if (application == null) {
            throw new RuntimeException("Xui must be call Xui#init()!");
        }
        return application;
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

    public static boolean isVuiEnable() {
        return sVuiEnable;
    }

    public static void setVuiEnable(boolean vuiEnable) {
        sVuiEnable = vuiEnable;
    }

    public static boolean isFontScaleDynamicChangeEnable() {
        return sFontScaleDynamicChangeEnable;
    }

    public static void setFontScaleDynamicChangeEnable(boolean fontScaleDynamicChangeEnable) {
        sFontScaleDynamicChangeEnable = fontScaleDynamicChangeEnable;
    }

    public static boolean isDialogFullScreen() {
        return sDialogFullScreen;
    }

    public static boolean isSharedDisplay() {
        return sSharedDisplay;
    }

    public static void setDialogFullScreen(boolean dialogFullScreen) {
        sDialogFullScreen = dialogFullScreen;
    }

    public static String getPackageName() {
        return mPackageName;
    }

    public static void setLogLevel(int level) {
        XLogUtils.setLogLevel(level);
    }

    public static void release() {
    }

    public static void clear() {
    }
}
