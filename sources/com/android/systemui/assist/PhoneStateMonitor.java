package com.android.systemui.assist;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import androidx.annotation.Nullable;
import com.android.systemui.Dependency;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.PackageManagerWrapper;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.statusbar.phone.StatusBar;
import java.util.ArrayList;
import java.util.List;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes21.dex */
public final class PhoneStateMonitor {
    private static final String[] DEFAULT_HOME_CHANGE_ACTIONS = {PackageManagerWrapper.ACTION_PREFERRED_ACTIVITY_CHANGED, "android.intent.action.BOOT_COMPLETED", "android.intent.action.PACKAGE_ADDED", "android.intent.action.PACKAGE_CHANGED", "android.intent.action.PACKAGE_REMOVED"};
    private static final int PHONE_STATE_ALL_APPS = 7;
    private static final int PHONE_STATE_AOD1 = 1;
    private static final int PHONE_STATE_AOD2 = 2;
    private static final int PHONE_STATE_APP_DEFAULT = 8;
    private static final int PHONE_STATE_APP_FULLSCREEN = 10;
    private static final int PHONE_STATE_APP_IMMERSIVE = 9;
    private static final int PHONE_STATE_BOUNCER = 3;
    private static final int PHONE_STATE_HOME = 5;
    private static final int PHONE_STATE_OVERVIEW = 6;
    private static final int PHONE_STATE_UNLOCKED_LOCKSCREEN = 4;
    private final Context mContext;
    @Nullable
    private ComponentName mDefaultHome;
    private boolean mLauncherShowing;
    private final StatusBarStateController mStatusBarStateController = (StatusBarStateController) Dependency.get(StatusBarStateController.class);

    static /* synthetic */ ComponentName access$100() {
        return getCurrentDefaultHome();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PhoneStateMonitor(Context context) {
        String[] strArr;
        this.mContext = context;
        ActivityManagerWrapper activityManagerWrapper = ActivityManagerWrapper.getInstance();
        this.mDefaultHome = getCurrentDefaultHome();
        IntentFilter intentFilter = new IntentFilter();
        for (String action : DEFAULT_HOME_CHANGE_ACTIONS) {
            intentFilter.addAction(action);
        }
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.systemui.assist.PhoneStateMonitor.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                PhoneStateMonitor.this.mDefaultHome = PhoneStateMonitor.access$100();
            }
        }, intentFilter);
        this.mLauncherShowing = isLauncherShowing(activityManagerWrapper.getRunningTask());
        activityManagerWrapper.registerTaskStackListener(new TaskStackChangeListener() { // from class: com.android.systemui.assist.PhoneStateMonitor.2
            @Override // com.android.systemui.shared.system.TaskStackChangeListener
            public void onTaskMovedToFront(ActivityManager.RunningTaskInfo taskInfo) {
                PhoneStateMonitor phoneStateMonitor = PhoneStateMonitor.this;
                phoneStateMonitor.mLauncherShowing = phoneStateMonitor.isLauncherShowing(taskInfo);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getPhoneState() {
        if (isShadeFullscreen()) {
            int phoneState = getPhoneLockscreenState();
            return phoneState;
        } else if (this.mLauncherShowing) {
            int phoneState2 = getPhoneLauncherState();
            return phoneState2;
        } else {
            int phoneState3 = getPhoneAppState();
            return phoneState3;
        }
    }

    @Nullable
    private static ComponentName getCurrentDefaultHome() {
        List<ResolveInfo> homeActivities = new ArrayList<>();
        ComponentName defaultHome = PackageManagerWrapper.getInstance().getHomeActivities(homeActivities);
        if (defaultHome != null) {
            return defaultHome;
        }
        int topPriority = Integer.MIN_VALUE;
        ComponentName topComponent = null;
        for (ResolveInfo resolveInfo : homeActivities) {
            if (resolveInfo.priority > topPriority) {
                topComponent = resolveInfo.activityInfo.getComponentName();
                topPriority = resolveInfo.priority;
            } else if (resolveInfo.priority == topPriority) {
                topComponent = null;
            }
        }
        return topComponent;
    }

    private int getPhoneLockscreenState() {
        if (isDozing()) {
            return 1;
        }
        if (isBouncerShowing()) {
            return 3;
        }
        if (isKeyguardLocked()) {
            return 2;
        }
        return 4;
    }

    private int getPhoneLauncherState() {
        if (isLauncherInOverview()) {
            return 6;
        }
        if (isLauncherInAllApps()) {
            return 7;
        }
        return 5;
    }

    private int getPhoneAppState() {
        if (isAppImmersive()) {
            return 9;
        }
        if (isAppFullscreen()) {
            return 10;
        }
        return 8;
    }

    private boolean isShadeFullscreen() {
        int statusBarState = this.mStatusBarStateController.getState();
        return statusBarState == 1 || statusBarState == 2;
    }

    private boolean isDozing() {
        return this.mStatusBarStateController.isDozing();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isLauncherShowing(ActivityManager.RunningTaskInfo runningTaskInfo) {
        if (runningTaskInfo == null) {
            return false;
        }
        return runningTaskInfo.topActivity.equals(this.mDefaultHome);
    }

    private boolean isAppImmersive() {
        return ((StatusBar) SysUiServiceProvider.getComponent(this.mContext, StatusBar.class)).inImmersiveMode();
    }

    private boolean isAppFullscreen() {
        return ((StatusBar) SysUiServiceProvider.getComponent(this.mContext, StatusBar.class)).inFullscreenMode();
    }

    private boolean isBouncerShowing() {
        StatusBar statusBar = (StatusBar) SysUiServiceProvider.getComponent(this.mContext, StatusBar.class);
        return statusBar != null && statusBar.isBouncerShowing();
    }

    private boolean isKeyguardLocked() {
        KeyguardManager keyguardManager = (KeyguardManager) this.mContext.getSystemService(KeyguardManager.class);
        return keyguardManager != null && keyguardManager.isKeyguardLocked();
    }

    private boolean isLauncherInOverview() {
        return false;
    }

    private boolean isLauncherInAllApps() {
        return false;
    }
}
