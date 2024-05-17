package com.xiaopeng.xui.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
/* loaded from: classes25.dex */
public class XActivityUtils {
    private static final String TAG = "ActivityUtils";

    public static void finish(Activity activity) {
        if (activity != null) {
            try {
                boolean isChild = activity.isChild();
                activity.finish();
                boolean isFinishing = activity.isFinishing();
                if (!isFinishing && !isChild) {
                    startHome(activity);
                }
            } catch (Exception e) {
                XLogUtils.w(TAG, "finish e=" + e);
            }
        }
    }

    public static boolean moveTaskToBack(Activity activity, boolean nonRoot) {
        boolean ret = false;
        if (activity != null) {
            try {
                boolean isChild = activity.isChild();
                ret = activity.moveTaskToBack(nonRoot);
                if (!ret && !isChild) {
                    startHome(activity);
                }
            } catch (Exception e) {
                XLogUtils.w(TAG, "moveTaskToBack e=" + e);
            }
        }
        return ret;
    }

    public static void startHome(Context context) {
        try {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            intent.addFlags(270532608);
            context.startActivity(intent);
        } catch (Exception e) {
            XLogUtils.w(TAG, "startHome e=" + e);
        }
    }

    public static void startActivity(Context context, Intent intent) {
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            XLogUtils.w(TAG, "startActivity e=" + e);
        }
    }

    public static void startActivityForResult(Activity activity, Intent intent, int code) {
        try {
            activity.startActivityForResult(intent, code);
        } catch (Exception e) {
            XLogUtils.w(TAG, "startActivityForResult e=" + e);
        }
    }

    @Deprecated
    public static int makeIntentFlag() {
        return 270548992;
    }

    public static int launchIntentFlag() {
        return 270548992;
    }

    public static void enterFullscreen(Activity activity) {
        activity.requestWindowFeature(14);
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(5894);
    }
}
