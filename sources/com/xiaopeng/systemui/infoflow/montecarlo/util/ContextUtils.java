package com.xiaopeng.systemui.infoflow.montecarlo.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import androidx.annotation.NonNull;
import java.lang.ref.WeakReference;
/* loaded from: classes24.dex */
public class ContextUtils {
    private static WeakReference<Context> sContext = null;
    private static WeakReference<Application> sApp = null;
    private static WeakReference<Activity> sActivity = null;

    public static void initApp(@NonNull Application app) {
        sApp = new WeakReference<>(app);
    }

    public static Application getApp() {
        Application app = sApp.get();
        if (app != null) {
            return app;
        }
        throw new IllegalArgumentException("ContextUtils sApp should not be null");
    }

    public static void initContext(@NonNull Context context) {
        sContext = new WeakReference<>(context);
    }

    public static Context getContext() {
        Context context = sContext.get();
        if (context != null) {
            return context;
        }
        throw new IllegalArgumentException("ContextUtils sContext should not be null");
    }

    public static void initActivity(@NonNull Activity activity) {
        sActivity = new WeakReference<>(activity);
    }

    public static Activity getActivity() {
        Activity activity = sActivity.get();
        if (activity != null) {
            return activity;
        }
        return null;
    }

    public static String getString(int resId) {
        Context context = sContext.get();
        if (context != null) {
            return context.getString(resId);
        }
        throw new IllegalArgumentException("ContextUtils sContext should not be null");
    }

    public static CharSequence getText(int resId) {
        Context context = sContext.get();
        if (context != null) {
            return context.getText(resId);
        }
        throw new IllegalArgumentException("ContextUtils sContext should not be null");
    }

    public static String getString(int id, Object... formatArgs) {
        Context context = sContext.get();
        if (context != null) {
            return context.getString(id, formatArgs);
        }
        throw new IllegalArgumentException("ContextUtils sContext should not be null");
    }

    public static int getColor(int resColorInt) {
        Context context = sContext.get();
        if (context != null) {
            return context.getResources().getColor(resColorInt);
        }
        throw new IllegalArgumentException("ContextUtils sContext should not be null");
    }

    public static ColorStateList getColorStateList(int resColorInt) {
        Context context = sContext.get();
        if (context != null) {
            return context.getResources().getColorStateList(resColorInt);
        }
        throw new IllegalArgumentException("ContextUtils sContext should not be null");
    }

    public static int getDimensionPixelSize(int dimensionId) {
        Context context = sContext.get();
        if (context != null) {
            return context.getResources().getDimensionPixelSize(dimensionId);
        }
        throw new IllegalArgumentException("ContextUtils sContext should not be null");
    }

    public static Drawable getDrawable(int drawableId) {
        Context context = sContext.get();
        if (context != null) {
            return context.getResources().getDrawable(drawableId);
        }
        throw new IllegalArgumentException("ContextUtils sContext should not be null");
    }

    public static String[] getStringArray(int resId) {
        Context context = sContext.get();
        if (context != null) {
            return context.getResources().getStringArray(resId);
        }
        throw new IllegalArgumentException("ContextUtils sContext should not be null");
    }

    public static int getInt(int resId) {
        Context context = sContext.get();
        if (context != null) {
            return context.getResources().getInteger(resId);
        }
        throw new IllegalArgumentException("ContextUtils sContext should not be null");
    }

    public static float getFloat(int resId) {
        Context context = sContext.get();
        if (context != null) {
            TypedValue outValue = new TypedValue();
            context.getResources().getValue(resId, outValue, true);
            return outValue.getFloat();
        }
        throw new IllegalArgumentException("ContextUtils sContext should not be null");
    }

    public static float getFraction(int resId) {
        Context context = sContext.get();
        if (context != null) {
            return context.getResources().getFraction(resId, 1, 1);
        }
        throw new IllegalArgumentException("ContextUtils sContext should not be null");
    }

    public static float getDensity() {
        Context context = sContext.get();
        if (context != null) {
            return context.getResources().getDisplayMetrics().density;
        }
        throw new IllegalArgumentException("ContextUtils sContext should not be null");
    }

    public static int getDensityDpi() {
        Context context = sContext.get();
        if (context != null) {
            return context.getResources().getDisplayMetrics().densityDpi;
        }
        throw new IllegalArgumentException("ContextUtils sContext should not be null");
    }

    public static int getScreenWidth() {
        Context context = sContext.get();
        if (context != null) {
            return context.getResources().getDisplayMetrics().widthPixels;
        }
        throw new IllegalArgumentException("ContextUtils sContext should not be null");
    }

    public static int getScreenHeight() {
        Context context = sContext.get();
        if (context != null) {
            return context.getResources().getDisplayMetrics().heightPixels;
        }
        throw new IllegalArgumentException("ContextUtils sContext should not be null");
    }
}
