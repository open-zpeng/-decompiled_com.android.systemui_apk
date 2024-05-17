package com.android.systemui.pip.phone;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.IActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import android.util.Pair;
/* loaded from: classes21.dex */
public class PipUtils {
    private static final String TAG = "PipUtils";

    public static Pair<ComponentName, Integer> getTopPinnedActivity(Context context, IActivityManager activityManager) {
        try {
            String sysUiPackageName = context.getPackageName();
            ActivityManager.StackInfo pinnedStackInfo = ActivityTaskManager.getService().getStackInfo(2, 0);
            if (pinnedStackInfo != null && pinnedStackInfo.taskIds != null && pinnedStackInfo.taskIds.length > 0) {
                for (int i = pinnedStackInfo.taskNames.length - 1; i >= 0; i--) {
                    ComponentName cn = ComponentName.unflattenFromString(pinnedStackInfo.taskNames[i]);
                    if (cn != null && !cn.getPackageName().equals(sysUiPackageName)) {
                        return new Pair<>(cn, Integer.valueOf(pinnedStackInfo.taskUserIds[i]));
                    }
                }
            }
        } catch (RemoteException e) {
            Log.w(TAG, "Unable to get pinned stack.");
        }
        return new Pair<>(null, 0);
    }
}
