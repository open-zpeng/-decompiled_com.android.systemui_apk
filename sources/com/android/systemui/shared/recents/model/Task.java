package com.android.systemui.shared.recents.model;

import android.app.ActivityManager;
import android.app.TaskInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ViewDebug;
import com.android.systemui.shared.recents.utilities.Utilities;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes21.dex */
public class Task {
    public static final String TAG = "Task";
    @ViewDebug.ExportedProperty(category = "recents")
    public int colorBackground;
    @ViewDebug.ExportedProperty(category = "recents")
    public int colorPrimary;
    public Drawable icon;
    @ViewDebug.ExportedProperty(category = "recents")
    public boolean isDockable;
    @ViewDebug.ExportedProperty(category = "recents")
    @Deprecated
    public boolean isLaunchTarget;
    @ViewDebug.ExportedProperty(category = "recents")
    public boolean isLocked;
    @ViewDebug.ExportedProperty(category = "recents")
    @Deprecated
    public boolean isStackTask;
    @ViewDebug.ExportedProperty(category = "recents")
    @Deprecated
    public boolean isSystemApp;
    @ViewDebug.ExportedProperty(deepExport = true, prefix = "key_")
    public TaskKey key;
    @Deprecated
    private ArrayList<TaskCallbacks> mCallbacks;
    @ViewDebug.ExportedProperty(category = "recents")
    @Deprecated
    public int resizeMode;
    public ActivityManager.TaskDescription taskDescription;
    @Deprecated
    public int temporarySortIndexInStack;
    public ThumbnailData thumbnail;
    @ViewDebug.ExportedProperty(category = "recents")
    @Deprecated
    public String title;
    @ViewDebug.ExportedProperty(category = "recents")
    public String titleDescription;
    @ViewDebug.ExportedProperty(category = "recents")
    public ComponentName topActivity;
    @ViewDebug.ExportedProperty(category = "recents")
    @Deprecated
    public boolean useLightOnPrimaryColor;

    @Deprecated
    /* loaded from: classes21.dex */
    public interface TaskCallbacks {
        void onTaskDataLoaded(Task task, ThumbnailData thumbnailData);

        void onTaskDataUnloaded();

        void onTaskWindowingModeChanged();
    }

    /* loaded from: classes21.dex */
    public static class TaskKey {
        @ViewDebug.ExportedProperty(category = "recents")
        public final Intent baseIntent;
        @ViewDebug.ExportedProperty(category = "recents")
        public final int displayId;
        @ViewDebug.ExportedProperty(category = "recents")
        public final int id;
        @ViewDebug.ExportedProperty(category = "recents")
        public long lastActiveTime;
        private int mHashCode;
        public final ComponentName sourceComponent;
        @ViewDebug.ExportedProperty(category = "recents")
        public final int userId;
        @ViewDebug.ExportedProperty(category = "recents")
        public int windowingMode;

        public TaskKey(TaskInfo t) {
            ComponentName sourceComponent;
            if (t.origActivity != null) {
                sourceComponent = t.origActivity;
            } else {
                sourceComponent = t.realActivity;
            }
            this.id = t.taskId;
            this.windowingMode = t.configuration.windowConfiguration.getWindowingMode();
            this.baseIntent = t.baseIntent;
            this.sourceComponent = sourceComponent;
            this.userId = t.userId;
            this.lastActiveTime = t.lastActiveTime;
            this.displayId = t.displayId;
            updateHashCode();
        }

        public TaskKey(int id, int windowingMode, Intent intent, ComponentName sourceComponent, int userId, long lastActiveTime) {
            this.id = id;
            this.windowingMode = windowingMode;
            this.baseIntent = intent;
            this.sourceComponent = sourceComponent;
            this.userId = userId;
            this.lastActiveTime = lastActiveTime;
            this.displayId = 0;
            updateHashCode();
        }

        public TaskKey(int id, int windowingMode, Intent intent, ComponentName sourceComponent, int userId, long lastActiveTime, int displayId) {
            this.id = id;
            this.windowingMode = windowingMode;
            this.baseIntent = intent;
            this.sourceComponent = sourceComponent;
            this.userId = userId;
            this.lastActiveTime = lastActiveTime;
            this.displayId = displayId;
            updateHashCode();
        }

        public void setWindowingMode(int windowingMode) {
            this.windowingMode = windowingMode;
            updateHashCode();
        }

        public ComponentName getComponent() {
            return this.baseIntent.getComponent();
        }

        public String getPackageName() {
            if (this.baseIntent.getComponent() != null) {
                return this.baseIntent.getComponent().getPackageName();
            }
            return this.baseIntent.getPackage();
        }

        public boolean equals(Object o) {
            if (o instanceof TaskKey) {
                TaskKey otherKey = (TaskKey) o;
                return this.id == otherKey.id && this.windowingMode == otherKey.windowingMode && this.userId == otherKey.userId;
            }
            return false;
        }

        public int hashCode() {
            return this.mHashCode;
        }

        public String toString() {
            return "id=" + this.id + " windowingMode=" + this.windowingMode + " user=" + this.userId + " lastActiveTime=" + this.lastActiveTime;
        }

        private void updateHashCode() {
            this.mHashCode = Objects.hash(Integer.valueOf(this.id), Integer.valueOf(this.windowingMode), Integer.valueOf(this.userId));
        }
    }

    public Task() {
        this.mCallbacks = new ArrayList<>();
    }

    public static Task from(TaskKey taskKey, TaskInfo taskInfo, boolean isLocked) {
        ActivityManager.TaskDescription td = taskInfo.taskDescription;
        return new Task(taskKey, td != null ? td.getPrimaryColor() : 0, td != null ? td.getBackgroundColor() : 0, taskInfo.supportsSplitScreenMultiWindow, isLocked, td, taskInfo.topActivity);
    }

    public Task(TaskKey key) {
        this.mCallbacks = new ArrayList<>();
        this.key = key;
        this.taskDescription = new ActivityManager.TaskDescription();
    }

    public Task(TaskKey key, int colorPrimary, int colorBackground, boolean isDockable, boolean isLocked, ActivityManager.TaskDescription taskDescription, ComponentName topActivity) {
        this.mCallbacks = new ArrayList<>();
        this.key = key;
        this.colorPrimary = colorPrimary;
        this.colorBackground = colorBackground;
        this.taskDescription = taskDescription;
        this.isDockable = isDockable;
        this.isLocked = isLocked;
        this.topActivity = topActivity;
    }

    @Deprecated
    public Task(TaskKey key, Drawable icon, ThumbnailData thumbnail, String title, String titleDescription, int colorPrimary, int colorBackground, boolean isLaunchTarget, boolean isStackTask, boolean isSystemApp, boolean isDockable, ActivityManager.TaskDescription taskDescription, int resizeMode, ComponentName topActivity, boolean isLocked) {
        this.mCallbacks = new ArrayList<>();
        this.key = key;
        this.icon = icon;
        this.thumbnail = thumbnail;
        this.title = title;
        this.titleDescription = titleDescription;
        this.colorPrimary = colorPrimary;
        this.colorBackground = colorBackground;
        this.useLightOnPrimaryColor = Utilities.computeContrastBetweenColors(this.colorPrimary, -1) > 3.0f;
        this.taskDescription = taskDescription;
        this.isLaunchTarget = isLaunchTarget;
        this.isStackTask = isStackTask;
        this.isSystemApp = isSystemApp;
        this.isDockable = isDockable;
        this.resizeMode = resizeMode;
        this.topActivity = topActivity;
        this.isLocked = isLocked;
    }

    @Deprecated
    public void copyFrom(Task o) {
        this.key = o.key;
        this.icon = o.icon;
        this.thumbnail = o.thumbnail;
        this.title = o.title;
        this.titleDescription = o.titleDescription;
        this.colorPrimary = o.colorPrimary;
        this.colorBackground = o.colorBackground;
        this.useLightOnPrimaryColor = o.useLightOnPrimaryColor;
        this.taskDescription = o.taskDescription;
        this.isLaunchTarget = o.isLaunchTarget;
        this.isStackTask = o.isStackTask;
        this.isSystemApp = o.isSystemApp;
        this.isDockable = o.isDockable;
        this.resizeMode = o.resizeMode;
        this.isLocked = o.isLocked;
        this.topActivity = o.topActivity;
    }

    @Deprecated
    public void addCallback(TaskCallbacks cb) {
        if (!this.mCallbacks.contains(cb)) {
            this.mCallbacks.add(cb);
        }
    }

    @Deprecated
    public void removeCallback(TaskCallbacks cb) {
        this.mCallbacks.remove(cb);
    }

    @Deprecated
    public void setWindowingMode(int windowingMode) {
        this.key.setWindowingMode(windowingMode);
        int callbackCount = this.mCallbacks.size();
        for (int i = 0; i < callbackCount; i++) {
            this.mCallbacks.get(i).onTaskWindowingModeChanged();
        }
    }

    @Deprecated
    public void notifyTaskDataLoaded(ThumbnailData thumbnailData, Drawable applicationIcon) {
        this.icon = applicationIcon;
        this.thumbnail = thumbnailData;
        int callbackCount = this.mCallbacks.size();
        for (int i = 0; i < callbackCount; i++) {
            this.mCallbacks.get(i).onTaskDataLoaded(this, thumbnailData);
        }
    }

    @Deprecated
    public void notifyTaskDataUnloaded(Drawable defaultApplicationIcon) {
        this.icon = defaultApplicationIcon;
        this.thumbnail = null;
        for (int i = this.mCallbacks.size() - 1; i >= 0; i--) {
            this.mCallbacks.get(i).onTaskDataUnloaded();
        }
    }

    public ComponentName getTopComponent() {
        ComponentName componentName = this.topActivity;
        if (componentName != null) {
            return componentName;
        }
        return this.key.baseIntent.getComponent();
    }

    public boolean equals(Object o) {
        Task t = (Task) o;
        return this.key.equals(t.key);
    }

    public String toString() {
        return NavigationBarInflaterView.SIZE_MOD_START + this.key.toString() + "] " + this.title;
    }

    public void dump(String prefix, PrintWriter writer) {
        writer.print(prefix);
        writer.print(this.key);
        if (!this.isDockable) {
            writer.print(" dockable=N");
        }
        if (this.isLaunchTarget) {
            writer.print(" launchTarget=Y");
        }
        if (this.isLocked) {
            writer.print(" locked=Y");
        }
        writer.print(" ");
        writer.print(this.title);
        writer.println();
    }
}
