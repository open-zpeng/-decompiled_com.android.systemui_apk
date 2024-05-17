package com.android.systemui.statusbar.notification;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.AppGlobals;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SynchronousUserSwitchObserver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import android.util.ArraySet;
import android.util.Pair;
import com.android.systemui.Dependency;
import com.android.systemui.DockedStackExistsListener;
import com.android.systemui.R;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.SystemUI;
import com.android.systemui.UiOffloadThread;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.notification.InstantAppNotifier;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.util.NotificationChannels;
import java.util.List;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class InstantAppNotifier extends SystemUI implements CommandQueue.Callbacks, KeyguardMonitor.Callback {
    public static final int NUM_TASKS_FOR_INSTANT_APP_INFO = 5;
    private static final String TAG = "InstantAppNotifier";
    private boolean mDockedStackExists;
    private KeyguardMonitor mKeyguardMonitor;
    private final Handler mHandler = new Handler();
    private final UiOffloadThread mUiOffloadThread = (UiOffloadThread) Dependency.get(UiOffloadThread.class);
    private final ArraySet<Pair<String, Integer>> mCurrentNotifs = new ArraySet<>();
    private final SynchronousUserSwitchObserver mUserSwitchListener = new AnonymousClass1();
    private final TaskStackChangeListener mTaskListener = new TaskStackChangeListener() { // from class: com.android.systemui.statusbar.notification.InstantAppNotifier.2
        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onTaskStackChanged() {
            InstantAppNotifier.this.updateForegroundInstantApps();
        }
    };

    @Override // com.android.systemui.SystemUI
    public void start() {
        StatusBarNotification[] activeNotifications;
        this.mKeyguardMonitor = (KeyguardMonitor) Dependency.get(KeyguardMonitor.class);
        try {
            ActivityManager.getService().registerUserSwitchObserver(this.mUserSwitchListener, TAG);
        } catch (RemoteException e) {
        }
        ((CommandQueue) SysUiServiceProvider.getComponent(this.mContext, CommandQueue.class)).addCallback((CommandQueue.Callbacks) this);
        this.mKeyguardMonitor.addCallback(this);
        DockedStackExistsListener.register(new Consumer() { // from class: com.android.systemui.statusbar.notification.-$$Lambda$InstantAppNotifier$QG8UJHrN7yIZpZAc2flF-n_csdY
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                InstantAppNotifier.this.lambda$start$0$InstantAppNotifier((Boolean) obj);
            }
        });
        NotificationManager noMan = (NotificationManager) this.mContext.getSystemService(NotificationManager.class);
        for (StatusBarNotification notification : noMan.getActiveNotifications()) {
            if (notification.getId() == 7) {
                noMan.cancel(notification.getTag(), notification.getId());
            }
        }
    }

    public /* synthetic */ void lambda$start$0$InstantAppNotifier(Boolean exists) {
        this.mDockedStackExists = exists.booleanValue();
        updateForegroundInstantApps();
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void appTransitionStarting(int displayId, long startTime, long duration, boolean forced) {
        if (this.mContext.getDisplayId() == displayId) {
            updateForegroundInstantApps();
        }
    }

    @Override // com.android.systemui.statusbar.policy.KeyguardMonitor.Callback
    public void onKeyguardShowingChanged() {
        updateForegroundInstantApps();
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void preloadRecentApps() {
        updateForegroundInstantApps();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.statusbar.notification.InstantAppNotifier$1  reason: invalid class name */
    /* loaded from: classes21.dex */
    public class AnonymousClass1 extends SynchronousUserSwitchObserver {
        AnonymousClass1() {
        }

        public void onUserSwitching(int newUserId) throws RemoteException {
        }

        public void onUserSwitchComplete(int newUserId) throws RemoteException {
            InstantAppNotifier.this.mHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.notification.-$$Lambda$InstantAppNotifier$1$2maFdVbSGSmI45ss9sfIaHkOm8U
                @Override // java.lang.Runnable
                public final void run() {
                    InstantAppNotifier.AnonymousClass1.this.lambda$onUserSwitchComplete$0$InstantAppNotifier$1();
                }
            });
        }

        public /* synthetic */ void lambda$onUserSwitchComplete$0$InstantAppNotifier$1() {
            InstantAppNotifier.this.updateForegroundInstantApps();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateForegroundInstantApps() {
        final NotificationManager noMan = (NotificationManager) this.mContext.getSystemService(NotificationManager.class);
        final IPackageManager pm = AppGlobals.getPackageManager();
        this.mUiOffloadThread.submit(new Runnable() { // from class: com.android.systemui.statusbar.notification.-$$Lambda$InstantAppNotifier$-jG9Ev-YNY9H1cwQp_C5lfrjo3s
            @Override // java.lang.Runnable
            public final void run() {
                InstantAppNotifier.this.lambda$updateForegroundInstantApps$2$InstantAppNotifier(noMan, pm);
            }
        });
    }

    public /* synthetic */ void lambda$updateForegroundInstantApps$2$InstantAppNotifier(final NotificationManager noMan, IPackageManager pm) {
        int windowingMode;
        ArraySet<Pair<String, Integer>> notifs = new ArraySet<>(this.mCurrentNotifs);
        try {
            ActivityManager.StackInfo focusedStack = ActivityTaskManager.getService().getFocusedStackInfo();
            if (focusedStack != null && ((windowingMode = focusedStack.configuration.windowConfiguration.getWindowingMode()) == 1 || windowingMode == 4)) {
                checkAndPostForStack(focusedStack, notifs, noMan, pm);
            }
            if (this.mDockedStackExists) {
                checkAndPostForPrimaryScreen(notifs, noMan, pm);
            }
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
        notifs.forEach(new Consumer() { // from class: com.android.systemui.statusbar.notification.-$$Lambda$InstantAppNotifier$Z2PXeDEr90IgG8nCbYZnqFPYPNc
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                InstantAppNotifier.this.lambda$updateForegroundInstantApps$1$InstantAppNotifier(noMan, (Pair) obj);
            }
        });
    }

    public /* synthetic */ void lambda$updateForegroundInstantApps$1$InstantAppNotifier(NotificationManager noMan, Pair v) {
        this.mCurrentNotifs.remove(v);
        noMan.cancelAsUser((String) v.first, 7, new UserHandle(((Integer) v.second).intValue()));
    }

    private void checkAndPostForPrimaryScreen(ArraySet<Pair<String, Integer>> notifs, NotificationManager noMan, IPackageManager pm) {
        try {
            ActivityManager.StackInfo info = ActivityTaskManager.getService().getStackInfo(3, 0);
            checkAndPostForStack(info, notifs, noMan, pm);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    private void checkAndPostForStack(ActivityManager.StackInfo info, ArraySet<Pair<String, Integer>> notifs, NotificationManager noMan, IPackageManager pm) {
        if (info == null) {
            return;
        }
        try {
            if (info.topActivity == null) {
                return;
            }
            String pkg = info.topActivity.getPackageName();
            Pair<String, Integer> key = new Pair<>(pkg, Integer.valueOf(info.userId));
            if (!notifs.remove(key)) {
                ApplicationInfo appInfo = pm.getApplicationInfo(pkg, 8192, info.userId);
                if (appInfo.isInstantApp()) {
                    postInstantAppNotif(pkg, info.userId, appInfo, noMan, info.taskIds[info.taskIds.length - 1]);
                }
            }
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    private void postInstantAppNotif(String pkg, int userId, ApplicationInfo appInfo, NotificationManager noMan, int taskId) {
        int i;
        String str;
        Notification.Action action;
        PendingIntent helpCenterIntent;
        Notification.Builder builder;
        PendingIntent helpCenterIntent2;
        ComponentName aiaComponent;
        Bundle extras = new Bundle();
        extras.putString("android.substName", this.mContext.getString(R.string.instant_apps));
        this.mCurrentNotifs.add(new Pair<>(pkg, Integer.valueOf(userId)));
        String helpUrl = this.mContext.getString(R.string.instant_apps_help_url);
        boolean hasHelpUrl = !helpUrl.isEmpty();
        Context context = this.mContext;
        if (hasHelpUrl) {
            i = R.string.instant_apps_message_with_help;
        } else {
            i = R.string.instant_apps_message;
        }
        String message = context.getString(i);
        UserHandle user = UserHandle.of(userId);
        PendingIntent appInfoAction = PendingIntent.getActivityAsUser(this.mContext, 0, new Intent("android.settings.APPLICATION_DETAILS_SETTINGS").setData(Uri.fromParts("package", pkg, null)), 67108864, null, user);
        Notification.Action action2 = new Notification.Action.Builder((Icon) null, this.mContext.getString(R.string.app_info), appInfoAction).build();
        if (hasHelpUrl) {
            str = "android.intent.action.VIEW";
            action = action2;
            helpCenterIntent = PendingIntent.getActivityAsUser(this.mContext, 0, new Intent("android.intent.action.VIEW").setData(Uri.parse(helpUrl)), 67108864, null, user);
        } else {
            str = "android.intent.action.VIEW";
            action = action2;
            helpCenterIntent = null;
        }
        Intent browserIntent = getTaskIntent(taskId, userId);
        Notification.Builder builder2 = new Notification.Builder(this.mContext, NotificationChannels.GENERAL);
        if (browserIntent == null || !browserIntent.isWebIntent()) {
            builder = builder2;
            helpCenterIntent2 = helpCenterIntent;
        } else {
            browserIntent.setComponent(null).setPackage(null).addFlags(512).addFlags(268435456);
            helpCenterIntent2 = helpCenterIntent;
            PendingIntent pendingIntent = PendingIntent.getActivityAsUser(this.mContext, 0, browserIntent, 67108864, null, user);
            try {
                aiaComponent = AppGlobals.getPackageManager().getInstantAppInstallerComponent();
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
                aiaComponent = null;
            }
            Intent goToWebIntent = new Intent().setComponent(aiaComponent).setAction(str).addCategory("android.intent.category.BROWSABLE").addCategory("unique:" + System.currentTimeMillis()).putExtra("android.intent.extra.PACKAGE_NAME", appInfo.packageName).putExtra("android.intent.extra.VERSION_CODE", appInfo.versionCode & Integer.MAX_VALUE).putExtra("android.intent.extra.LONG_VERSION_CODE", appInfo.longVersionCode).putExtra("android.intent.extra.INSTANT_APP_FAILURE", pendingIntent);
            PendingIntent webPendingIntent = PendingIntent.getActivityAsUser(this.mContext, 0, goToWebIntent, 67108864, null, user);
            Notification.Action webAction = new Notification.Action.Builder((Icon) null, this.mContext.getString(R.string.go_to_web), webPendingIntent).build();
            builder = builder2;
            builder.addAction(webAction);
        }
        noMan.notifyAsUser(pkg, 7, builder.addExtras(extras).addAction(action).setContentIntent(helpCenterIntent2).setColor(this.mContext.getColor(R.color.instant_apps_color)).setContentTitle(this.mContext.getString(R.string.instant_apps_title, appInfo.loadLabel(this.mContext.getPackageManager()))).setLargeIcon(Icon.createWithResource(pkg, appInfo.icon)).setSmallIcon(Icon.createWithResource(this.mContext.getPackageName(), R.drawable.instant_icon)).setContentText(message).setStyle(new Notification.BigTextStyle().bigText(message)).setOngoing(true).build(), new UserHandle(userId));
    }

    private Intent getTaskIntent(int taskId, int userId) {
        try {
            List<ActivityManager.RecentTaskInfo> tasks = ActivityTaskManager.getService().getRecentTasks(5, 0, userId).getList();
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).id == taskId) {
                    return tasks.get(i).baseIntent;
                }
            }
            return null;
        } catch (RemoteException e) {
            return null;
        }
    }
}
