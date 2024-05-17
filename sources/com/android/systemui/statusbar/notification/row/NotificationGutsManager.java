package com.android.systemui.statusbar.notification.row;

import android.app.INotificationManager;
import android.app.NotificationChannel;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationLifetimeExtender;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.StatusBarStateControllerImpl;
import com.android.systemui.statusbar.notification.NotificationActivityStarter;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.AppOpsInfo;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.NotificationGuts;
import com.android.systemui.statusbar.notification.row.NotificationInfo;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class NotificationGutsManager implements Dumpable, NotificationLifetimeExtender {
    private static final String EXTRA_FRAGMENT_ARG_KEY = ":settings:fragment_args_key";
    public static final String EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":settings:show_fragment_args";
    private static final String TAG = "NotificationGutsManager";
    private final AccessibilityManager mAccessibilityManager;
    private NotificationInfo.CheckSaveListener mCheckSaveListener;
    private final Context mContext;
    private NotificationMenuRowPlugin.MenuItem mGutsMenuItem;
    @VisibleForTesting
    protected String mKeyToRemoveOnGutsClosed;
    private NotificationListContainer mListContainer;
    private NotificationActivityStarter mNotificationActivityStarter;
    private NotificationGuts mNotificationGutsExposed;
    private NotificationLifetimeExtender.NotificationSafeToRemoveCallback mNotificationLifetimeFinishedCallback;
    private OnSettingsClickListener mOnSettingsClickListener;
    private Runnable mOpenRunnable;
    private NotificationPresenter mPresenter;
    private StatusBar mStatusBar;
    private final VisualStabilityManager mVisualStabilityManager;
    private final MetricsLogger mMetricsLogger = (MetricsLogger) Dependency.get(MetricsLogger.class);
    private final NotificationLockscreenUserManager mLockscreenUserManager = (NotificationLockscreenUserManager) Dependency.get(NotificationLockscreenUserManager.class);
    private final StatusBarStateController mStatusBarStateController = (StatusBarStateController) Dependency.get(StatusBarStateController.class);
    private final DeviceProvisionedController mDeviceProvisionedController = (DeviceProvisionedController) Dependency.get(DeviceProvisionedController.class);

    /* loaded from: classes21.dex */
    public interface OnSettingsClickListener {
        void onSettingsClick(String str);
    }

    @Inject
    public NotificationGutsManager(Context context, VisualStabilityManager visualStabilityManager) {
        this.mContext = context;
        this.mVisualStabilityManager = visualStabilityManager;
        this.mAccessibilityManager = (AccessibilityManager) this.mContext.getSystemService("accessibility");
    }

    public void setUpWithPresenter(NotificationPresenter presenter, NotificationListContainer listContainer, NotificationInfo.CheckSaveListener checkSave, OnSettingsClickListener onSettingsClick) {
        this.mPresenter = presenter;
        this.mListContainer = listContainer;
        this.mCheckSaveListener = checkSave;
        this.mOnSettingsClickListener = onSettingsClick;
        this.mStatusBar = (StatusBar) SysUiServiceProvider.getComponent(this.mContext, StatusBar.class);
    }

    public void setNotificationActivityStarter(NotificationActivityStarter notificationActivityStarter) {
        this.mNotificationActivityStarter = notificationActivityStarter;
    }

    public void onDensityOrFontScaleChanged(NotificationEntry entry) {
        setExposedGuts(entry.getGuts());
        bindGuts(entry.getRow());
    }

    private void startAppNotificationSettingsActivity(String packageName, int appUid, NotificationChannel channel, ExpandableNotificationRow row) {
        Intent intent = new Intent("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.putExtra("android.provider.extra.APP_PACKAGE", packageName);
        intent.putExtra("app_uid", appUid);
        if (channel != null) {
            Bundle args = new Bundle();
            intent.putExtra(EXTRA_FRAGMENT_ARG_KEY, channel.getId());
            args.putString(EXTRA_FRAGMENT_ARG_KEY, channel.getId());
            intent.putExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS, args);
        }
        this.mNotificationActivityStarter.startNotificationGutsIntent(intent, appUid, row);
    }

    private void startAppDetailsSettingsActivity(String packageName, int appUid, NotificationChannel channel, ExpandableNotificationRow row) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", packageName, null));
        intent.putExtra("android.provider.extra.APP_PACKAGE", packageName);
        intent.putExtra("app_uid", appUid);
        if (channel != null) {
            intent.putExtra(EXTRA_FRAGMENT_ARG_KEY, channel.getId());
        }
        this.mNotificationActivityStarter.startNotificationGutsIntent(intent, appUid, row);
    }

    protected void startAppOpsSettingsActivity(String pkg, int uid, ArraySet<Integer> ops, ExpandableNotificationRow row) {
        if (ops.contains(24)) {
            if (ops.contains(26) || ops.contains(27)) {
                startAppDetailsSettingsActivity(pkg, uid, null, row);
                return;
            }
            Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION");
            intent.setData(Uri.fromParts("package", pkg, null));
            this.mNotificationActivityStarter.startNotificationGutsIntent(intent, uid, row);
        } else if (ops.contains(26) || ops.contains(27)) {
            Intent intent2 = new Intent("android.intent.action.MANAGE_APP_PERMISSIONS");
            intent2.putExtra("android.intent.extra.PACKAGE_NAME", pkg);
            this.mNotificationActivityStarter.startNotificationGutsIntent(intent2, uid, row);
        }
    }

    private boolean bindGuts(ExpandableNotificationRow row) {
        row.ensureGutsInflated();
        return bindGuts(row, this.mGutsMenuItem);
    }

    @VisibleForTesting
    protected boolean bindGuts(final ExpandableNotificationRow row, NotificationMenuRowPlugin.MenuItem item) {
        final StatusBarNotification sbn = row.getStatusBarNotification();
        row.setGutsView(item);
        row.setTag(sbn.getPackageName());
        row.getGuts().setClosedListener(new NotificationGuts.OnGutsClosedListener() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$NotificationGutsManager$lbHSFb83h5SRmJTPUlzactX7_1Q
            @Override // com.android.systemui.statusbar.notification.row.NotificationGuts.OnGutsClosedListener
            public final void onGutsClosed(NotificationGuts notificationGuts) {
                NotificationGutsManager.this.lambda$bindGuts$0$NotificationGutsManager(row, sbn, notificationGuts);
            }
        });
        View gutsView = item.getGutsView();
        try {
            if (gutsView instanceof NotificationSnooze) {
                initializeSnoozeView(row, (NotificationSnooze) gutsView);
                return true;
            } else if (gutsView instanceof AppOpsInfo) {
                initializeAppOpsInfo(row, (AppOpsInfo) gutsView);
                return true;
            } else if (gutsView instanceof NotificationInfo) {
                initializeNotificationInfo(row, (NotificationInfo) gutsView);
                return true;
            } else {
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "error binding guts", e);
            return false;
        }
    }

    public /* synthetic */ void lambda$bindGuts$0$NotificationGutsManager(ExpandableNotificationRow row, StatusBarNotification sbn, NotificationGuts g) {
        row.onGutsClosed();
        if (!g.willBeRemoved() && !row.isRemoved()) {
            this.mListContainer.onHeightChanged(row, !this.mPresenter.isPresenterFullyCollapsed());
        }
        if (this.mNotificationGutsExposed == g) {
            this.mNotificationGutsExposed = null;
            this.mGutsMenuItem = null;
        }
        String key = sbn.getKey();
        if (key.equals(this.mKeyToRemoveOnGutsClosed)) {
            this.mKeyToRemoveOnGutsClosed = null;
            NotificationLifetimeExtender.NotificationSafeToRemoveCallback notificationSafeToRemoveCallback = this.mNotificationLifetimeFinishedCallback;
            if (notificationSafeToRemoveCallback != null) {
                notificationSafeToRemoveCallback.onSafeToRemove(key);
            }
        }
    }

    private void initializeSnoozeView(final ExpandableNotificationRow row, NotificationSnooze notificationSnoozeView) {
        NotificationGuts guts = row.getGuts();
        StatusBarNotification sbn = row.getStatusBarNotification();
        notificationSnoozeView.setSnoozeListener(this.mListContainer.getSwipeActionHelper());
        notificationSnoozeView.setStatusBarNotification(sbn);
        notificationSnoozeView.setSnoozeOptions(row.getEntry().snoozeCriteria);
        guts.setHeightChangedListener(new NotificationGuts.OnHeightChangedListener() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$NotificationGutsManager$xtHxMW6jrIgJGugFgxSSg6aT080
            @Override // com.android.systemui.statusbar.notification.row.NotificationGuts.OnHeightChangedListener
            public final void onHeightChanged(NotificationGuts notificationGuts) {
                NotificationGutsManager.this.lambda$initializeSnoozeView$1$NotificationGutsManager(row, notificationGuts);
            }
        });
    }

    public /* synthetic */ void lambda$initializeSnoozeView$1$NotificationGutsManager(ExpandableNotificationRow row, NotificationGuts g) {
        this.mListContainer.onHeightChanged(row, row.isShown());
    }

    private void initializeAppOpsInfo(final ExpandableNotificationRow row, AppOpsInfo appOpsInfoView) {
        final NotificationGuts guts = row.getGuts();
        StatusBarNotification sbn = row.getStatusBarNotification();
        UserHandle userHandle = sbn.getUser();
        PackageManager pmUser = StatusBar.getPackageManagerForUser(this.mContext, userHandle.getIdentifier());
        AppOpsInfo.OnSettingsClickListener onSettingsClick = new AppOpsInfo.OnSettingsClickListener() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$NotificationGutsManager$QUX76CVRNteGCzCinyuNeuYX3tU
            @Override // com.android.systemui.statusbar.notification.row.AppOpsInfo.OnSettingsClickListener
            public final void onClick(View view, String str, int i, ArraySet arraySet) {
                NotificationGutsManager.this.lambda$initializeAppOpsInfo$2$NotificationGutsManager(guts, row, view, str, i, arraySet);
            }
        };
        if (!row.getEntry().mActiveAppOps.isEmpty()) {
            appOpsInfoView.bindGuts(pmUser, onSettingsClick, sbn, row.getEntry().mActiveAppOps);
        }
    }

    public /* synthetic */ void lambda$initializeAppOpsInfo$2$NotificationGutsManager(NotificationGuts guts, ExpandableNotificationRow row, View v, String pkg, int uid, ArraySet ops) {
        this.mMetricsLogger.action(1346);
        guts.resetFalsingCheck();
        startAppOpsSettingsActivity(pkg, uid, ops, row);
    }

    @VisibleForTesting
    void initializeNotificationInfo(final ExpandableNotificationRow row, NotificationInfo notificationInfoView) throws Exception {
        final NotificationGuts guts = row.getGuts();
        final StatusBarNotification sbn = row.getStatusBarNotification();
        final String packageName = sbn.getPackageName();
        NotificationInfo.OnSettingsClickListener onSettingsClick = null;
        UserHandle userHandle = sbn.getUser();
        PackageManager pmUser = StatusBar.getPackageManagerForUser(this.mContext, userHandle.getIdentifier());
        INotificationManager iNotificationManager = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
        NotificationInfo.OnAppSettingsClickListener onAppSettingsClick = new NotificationInfo.OnAppSettingsClickListener() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$NotificationGutsManager$5sbilrrQIt_lf--8k9ZdwNLn-js
            @Override // com.android.systemui.statusbar.notification.row.NotificationInfo.OnAppSettingsClickListener
            public final void onClick(View view, Intent intent) {
                NotificationGutsManager.this.lambda$initializeNotificationInfo$3$NotificationGutsManager(guts, sbn, row, view, intent);
            }
        };
        boolean isForBlockingHelper = row.isBlockingHelperShowing();
        if (!userHandle.equals(UserHandle.ALL) || this.mLockscreenUserManager.getCurrentUserId() == 0) {
            onSettingsClick = new NotificationInfo.OnSettingsClickListener() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$NotificationGutsManager$Q50_8sHdIRaYdx4NmoW9bex_4-o
                @Override // com.android.systemui.statusbar.notification.row.NotificationInfo.OnSettingsClickListener
                public final void onClick(View view, NotificationChannel notificationChannel, int i) {
                    NotificationGutsManager.this.lambda$initializeNotificationInfo$4$NotificationGutsManager(guts, sbn, packageName, row, view, notificationChannel, i);
                }
            };
        }
        notificationInfoView.bindNotification(pmUser, iNotificationManager, this.mVisualStabilityManager, packageName, row.getEntry().channel, row.getUniqueChannels(), sbn, this.mCheckSaveListener, onSettingsClick, onAppSettingsClick, this.mDeviceProvisionedController.isDeviceProvisioned(), row.getIsNonblockable(), isForBlockingHelper, row.getEntry().importance, row.getEntry().isHighPriority());
    }

    public /* synthetic */ void lambda$initializeNotificationInfo$3$NotificationGutsManager(NotificationGuts guts, StatusBarNotification sbn, ExpandableNotificationRow row, View v, Intent intent) {
        this.mMetricsLogger.action(206);
        guts.resetFalsingCheck();
        this.mNotificationActivityStarter.startNotificationGutsIntent(intent, sbn.getUid(), row);
    }

    public /* synthetic */ void lambda$initializeNotificationInfo$4$NotificationGutsManager(NotificationGuts guts, StatusBarNotification sbn, String packageName, ExpandableNotificationRow row, View v, NotificationChannel channel, int appUid) {
        this.mMetricsLogger.action(205);
        guts.resetFalsingCheck();
        this.mOnSettingsClickListener.onSettingsClick(sbn.getKey());
        startAppNotificationSettingsActivity(packageName, appUid, channel, row);
    }

    public void closeAndSaveGuts(boolean removeLeavebehinds, boolean force, boolean removeControls, int x, int y, boolean resetMenu) {
        NotificationGuts notificationGuts = this.mNotificationGutsExposed;
        if (notificationGuts != null) {
            notificationGuts.removeCallbacks(this.mOpenRunnable);
            this.mNotificationGutsExposed.closeControls(removeLeavebehinds, removeControls, x, y, force);
        }
        if (resetMenu) {
            this.mListContainer.resetExposedMenuView(false, true);
        }
    }

    public NotificationGuts getExposedGuts() {
        return this.mNotificationGutsExposed;
    }

    public void setExposedGuts(NotificationGuts guts) {
        this.mNotificationGutsExposed = guts;
    }

    public ExpandableNotificationRow.LongPressListener getNotificationLongClicker() {
        return new ExpandableNotificationRow.LongPressListener() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$0lGYUT66Z7cr4TZs4rdZ8M7DQkw
            @Override // com.android.systemui.statusbar.notification.row.ExpandableNotificationRow.LongPressListener
            public final boolean onLongPress(View view, int i, int i2, NotificationMenuRowPlugin.MenuItem menuItem) {
                return NotificationGutsManager.this.openGuts(view, i, i2, menuItem);
            }
        };
    }

    public boolean openGuts(final View view, final int x, final int y, final NotificationMenuRowPlugin.MenuItem menuItem) {
        if (menuItem.getGutsView() instanceof NotificationInfo) {
            StatusBarStateController statusBarStateController = this.mStatusBarStateController;
            if (statusBarStateController instanceof StatusBarStateControllerImpl) {
                ((StatusBarStateControllerImpl) statusBarStateController).setLeaveOpenOnKeyguardHide(true);
            }
            Runnable r = new Runnable() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$NotificationGutsManager$ujQD4EmV_laISDNVyhlRbAQC7J4
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationGutsManager.this.lambda$openGuts$6$NotificationGutsManager(view, x, y, menuItem);
                }
            };
            this.mStatusBar.executeRunnableDismissingKeyguard(r, null, false, true, true);
            return true;
        }
        return lambda$openGuts$5$NotificationGutsManager(view, x, y, menuItem);
    }

    public /* synthetic */ void lambda$openGuts$6$NotificationGutsManager(final View view, final int x, final int y, final NotificationMenuRowPlugin.MenuItem menuItem) {
        ((Handler) Dependency.get(Dependency.MAIN_HANDLER)).post(new Runnable() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$NotificationGutsManager$5tF5K6xQWa5hR-dlcAaoNBN2vKM
            @Override // java.lang.Runnable
            public final void run() {
                NotificationGutsManager.this.lambda$openGuts$5$NotificationGutsManager(view, x, y, menuItem);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* renamed from: openGutsInternal */
    public boolean lambda$openGuts$5$NotificationGutsManager(View view, final int x, final int y, final NotificationMenuRowPlugin.MenuItem menuItem) {
        if (view instanceof ExpandableNotificationRow) {
            if (view.getWindowToken() == null) {
                Log.e(TAG, "Trying to show notification guts, but not attached to window");
                return false;
            }
            final ExpandableNotificationRow row = (ExpandableNotificationRow) view;
            view.performHapticFeedback(0);
            if (row.areGutsExposed()) {
                closeAndSaveGuts(false, false, true, -1, -1, true);
                return false;
            }
            row.ensureGutsInflated();
            final NotificationGuts guts = row.getGuts();
            this.mNotificationGutsExposed = guts;
            if (bindGuts(row, menuItem) && guts != null) {
                guts.setVisibility(4);
                this.mOpenRunnable = new Runnable() { // from class: com.android.systemui.statusbar.notification.row.NotificationGutsManager.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (row.getWindowToken() == null) {
                            Log.e(NotificationGutsManager.TAG, "Trying to show notification guts in post(), but not attached to window");
                            return;
                        }
                        guts.setVisibility(0);
                        boolean needsFalsingProtection = NotificationGutsManager.this.mStatusBarStateController.getState() == 1 && !NotificationGutsManager.this.mAccessibilityManager.isTouchExplorationEnabled();
                        NotificationGuts notificationGuts = guts;
                        boolean z = !row.isBlockingHelperShowing();
                        int i = x;
                        int i2 = y;
                        final ExpandableNotificationRow expandableNotificationRow = row;
                        Objects.requireNonNull(expandableNotificationRow);
                        notificationGuts.openControls(z, i, i2, needsFalsingProtection, new Runnable() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$IONSGD9gxXDD_zwBcDGw5yfu2Rc
                            @Override // java.lang.Runnable
                            public final void run() {
                                ExpandableNotificationRow.this.onGutsOpened();
                            }
                        });
                        row.closeRemoteInput();
                        NotificationGutsManager.this.mListContainer.onHeightChanged(row, true);
                        NotificationGutsManager.this.mGutsMenuItem = menuItem;
                    }
                };
                guts.post(this.mOpenRunnable);
                return true;
            }
            return false;
        }
        return false;
    }

    @Override // com.android.systemui.statusbar.NotificationLifetimeExtender
    public void setCallback(NotificationLifetimeExtender.NotificationSafeToRemoveCallback callback) {
        this.mNotificationLifetimeFinishedCallback = callback;
    }

    @Override // com.android.systemui.statusbar.NotificationLifetimeExtender
    public boolean shouldExtendLifetime(NotificationEntry entry) {
        return (entry == null || this.mNotificationGutsExposed == null || entry.getGuts() == null || this.mNotificationGutsExposed != entry.getGuts() || this.mNotificationGutsExposed.isLeavebehind()) ? false : true;
    }

    @Override // com.android.systemui.statusbar.NotificationLifetimeExtender
    public void setShouldManageLifetime(NotificationEntry entry, boolean shouldExtend) {
        if (shouldExtend) {
            this.mKeyToRemoveOnGutsClosed = entry.key;
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "Keeping notification because it's showing guts. " + entry.key);
                return;
            }
            return;
        }
        String str = this.mKeyToRemoveOnGutsClosed;
        if (str != null && str.equals(entry.key)) {
            this.mKeyToRemoveOnGutsClosed = null;
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "Notification that was kept for guts was updated. " + entry.key);
            }
        }
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("NotificationGutsManager state:");
        pw.print("  mKeyToRemoveOnGutsClosed: ");
        pw.println(this.mKeyToRemoveOnGutsClosed);
    }
}
