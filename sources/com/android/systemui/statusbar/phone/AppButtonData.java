package com.android.systemui.statusbar.phone;

import android.app.ActivityManager;
import java.util.ArrayList;
/* loaded from: classes21.dex */
class AppButtonData {
    public final AppInfo appInfo;
    public boolean pinned;
    public ArrayList<ActivityManager.RecentTaskInfo> tasks;

    public AppButtonData(AppInfo appInfo, boolean pinned) {
        this.appInfo = appInfo;
        this.pinned = pinned;
    }

    public int getTaskCount() {
        ArrayList<ActivityManager.RecentTaskInfo> arrayList = this.tasks;
        if (arrayList == null) {
            return 0;
        }
        return arrayList.size();
    }

    public boolean isEmpty() {
        return !this.pinned && getTaskCount() == 0;
    }

    public void addTask(ActivityManager.RecentTaskInfo task) {
        if (this.tasks == null) {
            this.tasks = new ArrayList<>();
        }
        this.tasks.add(task);
    }

    public void clearTasks() {
        ArrayList<ActivityManager.RecentTaskInfo> arrayList = this.tasks;
        if (arrayList != null) {
            arrayList.clear();
        }
    }
}
