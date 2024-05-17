package com.android.systemui.shared.system;

import android.app.ActivityManager;
import android.app.TaskInfo;
import android.content.ComponentName;
/* loaded from: classes21.dex */
public class TaskInfoCompat {
    public static int getUserId(TaskInfo info) {
        return info.userId;
    }

    public static int getActivityType(TaskInfo info) {
        return info.configuration.windowConfiguration.getActivityType();
    }

    public static int getWindowingMode(TaskInfo info) {
        return info.configuration.windowConfiguration.getWindowingMode();
    }

    public static boolean supportsSplitScreenMultiWindow(TaskInfo info) {
        return info.supportsSplitScreenMultiWindow;
    }

    public static ComponentName getTopActivity(TaskInfo info) {
        return info.topActivity;
    }

    public static ActivityManager.TaskDescription getTaskDescription(TaskInfo info) {
        return info.taskDescription;
    }
}
