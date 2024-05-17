package com.xiaopeng.lib.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import androidx.slice.core.SliceHints;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes23.dex */
public class ContextUtils {
    public static boolean isTopActivity(Context context, Class clazz) {
        ActivityManager manager = (ActivityManager) context.getSystemService(SliceHints.HINT_ACTIVITY);
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList = manager.getRunningTasks(1);
        if (runningTaskInfoList == null || runningTaskInfoList.size() <= 0) {
            return false;
        }
        ComponentName cn = runningTaskInfoList.get(0).topActivity;
        if (!cn.getClassName().equals(clazz.getName())) {
            return false;
        }
        return true;
    }

    public static boolean isServiceWorked(Context context, Class clazz) {
        ActivityManager manager = (ActivityManager) context.getSystemService(SliceHints.HINT_ACTIVITY);
        ArrayList<ActivityManager.RunningServiceInfo> runningServices = (ArrayList) manager.getRunningServices(100);
        for (int i = 0; i < runningServices.size(); i++) {
            if (runningServices.get(i).service.getClassName().toString().equals(clazz.getName())) {
                return true;
            }
        }
        return false;
    }
}
