package com.android.systemui.statusbar.notification.collection;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.util.NotificationMessagingUtil;
import com.android.internal.util.Preconditions;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.UiOffloadThread;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationUiAdjustment;
import com.android.systemui.statusbar.notification.InflationException;
import com.android.systemui.statusbar.notification.NotificationClicker;
import com.android.systemui.statusbar.notification.NotificationInterruptionStateProvider;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.NotificationContentInflater;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager;
import com.android.systemui.statusbar.notification.row.RowInflaterTask;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import java.util.Objects;
/* loaded from: classes21.dex */
public class NotificationRowBinderImpl implements NotificationRowBinder {
    private static final String TAG = "NotificationViewManager";
    private final boolean mAllowLongPress;
    private BindRowCallback mBindRowCallback;
    private final Context mContext;
    private HeadsUpManager mHeadsUpManager;
    private NotificationContentInflater.InflationCallback mInflationCallback;
    private final KeyguardBypassController mKeyguardBypassController;
    private NotificationListContainer mListContainer;
    private final NotificationMessagingUtil mMessagingUtil;
    private NotificationClicker mNotificationClicker;
    private ExpandableNotificationRow.OnAppOpsClickListener mOnAppOpsClickListener;
    private NotificationPresenter mPresenter;
    private NotificationRemoteInputManager mRemoteInputManager;
    private final StatusBarStateController mStatusBarStateController;
    private final NotificationGroupManager mGroupManager = (NotificationGroupManager) Dependency.get(NotificationGroupManager.class);
    private final NotificationGutsManager mGutsManager = (NotificationGutsManager) Dependency.get(NotificationGutsManager.class);
    private final UiOffloadThread mUiOffloadThread = (UiOffloadThread) Dependency.get(UiOffloadThread.class);
    private final NotificationInterruptionStateProvider mNotificationInterruptionStateProvider = (NotificationInterruptionStateProvider) Dependency.get(NotificationInterruptionStateProvider.class);
    private final ExpandableNotificationRow.ExpansionLogger mExpansionLogger = new ExpandableNotificationRow.ExpansionLogger() { // from class: com.android.systemui.statusbar.notification.collection.-$$Lambda$NotificationRowBinderImpl$yERtDMu_MSEd5-3CM8yc051ZJLU
        @Override // com.android.systemui.statusbar.notification.row.ExpandableNotificationRow.ExpansionLogger
        public final void logNotificationExpansion(String str, boolean z, boolean z2) {
            NotificationRowBinderImpl.this.logNotificationExpansion(str, z, z2);
        }
    };
    private final NotificationLogger mNotificationLogger = (NotificationLogger) Dependency.get(NotificationLogger.class);

    /* loaded from: classes21.dex */
    public interface BindRowCallback {
        void onBindRow(NotificationEntry notificationEntry, PackageManager packageManager, StatusBarNotification statusBarNotification, ExpandableNotificationRow expandableNotificationRow);
    }

    public NotificationRowBinderImpl(Context context, boolean allowLongPress, KeyguardBypassController keyguardBypassController, StatusBarStateController statusBarStateController) {
        this.mContext = context;
        this.mMessagingUtil = new NotificationMessagingUtil(context);
        this.mAllowLongPress = allowLongPress;
        this.mKeyguardBypassController = keyguardBypassController;
        this.mStatusBarStateController = statusBarStateController;
    }

    private NotificationRemoteInputManager getRemoteInputManager() {
        if (this.mRemoteInputManager == null) {
            this.mRemoteInputManager = (NotificationRemoteInputManager) Dependency.get(NotificationRemoteInputManager.class);
        }
        return this.mRemoteInputManager;
    }

    public void setUpWithPresenter(NotificationPresenter presenter, NotificationListContainer listContainer, HeadsUpManager headsUpManager, NotificationContentInflater.InflationCallback inflationCallback, BindRowCallback bindRowCallback) {
        this.mPresenter = presenter;
        this.mListContainer = listContainer;
        this.mHeadsUpManager = headsUpManager;
        this.mInflationCallback = inflationCallback;
        this.mBindRowCallback = bindRowCallback;
        final NotificationGutsManager notificationGutsManager = this.mGutsManager;
        Objects.requireNonNull(notificationGutsManager);
        this.mOnAppOpsClickListener = new ExpandableNotificationRow.OnAppOpsClickListener() { // from class: com.android.systemui.statusbar.notification.collection.-$$Lambda$oy9pBf4KjrW7ZRpgHkpOCIaDYlg
            @Override // com.android.systemui.statusbar.notification.row.ExpandableNotificationRow.OnAppOpsClickListener
            public final boolean onClick(View view, int i, int i2, NotificationMenuRowPlugin.MenuItem menuItem) {
                return NotificationGutsManager.this.openGuts(view, i, i2, menuItem);
            }
        };
    }

    public void setNotificationClicker(NotificationClicker clicker) {
        this.mNotificationClicker = clicker;
    }

    @Override // com.android.systemui.statusbar.notification.collection.NotificationRowBinder
    public void inflateViews(final NotificationEntry entry, final Runnable onDismissRunnable) throws InflationException {
        ViewGroup parent = this.mListContainer.getViewParentForNotification(entry);
        final PackageManager pmUser = StatusBar.getPackageManagerForUser(this.mContext, entry.notification.getUser().getIdentifier());
        final StatusBarNotification sbn = entry.notification;
        if (entry.rowExists()) {
            entry.updateIcons(this.mContext, sbn);
            entry.reset();
            updateNotification(entry, pmUser, sbn, entry.getRow());
            entry.getRow().setOnDismissRunnable(onDismissRunnable);
            return;
        }
        entry.createIcons(this.mContext, sbn);
        new RowInflaterTask().inflate(this.mContext, parent, entry, new RowInflaterTask.RowInflationFinishedListener() { // from class: com.android.systemui.statusbar.notification.collection.-$$Lambda$NotificationRowBinderImpl$0PyN32FQZaNZKR9hi21mLsFCxgE
            @Override // com.android.systemui.statusbar.notification.row.RowInflaterTask.RowInflationFinishedListener
            public final void onInflationFinished(ExpandableNotificationRow expandableNotificationRow) {
                NotificationRowBinderImpl.this.lambda$inflateViews$0$NotificationRowBinderImpl(entry, pmUser, sbn, onDismissRunnable, expandableNotificationRow);
            }
        });
    }

    public /* synthetic */ void lambda$inflateViews$0$NotificationRowBinderImpl(NotificationEntry entry, PackageManager pmUser, StatusBarNotification sbn, Runnable onDismissRunnable, ExpandableNotificationRow row) {
        bindRow(entry, pmUser, sbn, row, onDismissRunnable);
        updateNotification(entry, pmUser, sbn, row);
    }

    private void bindRow(NotificationEntry entry, PackageManager pmUser, StatusBarNotification sbn, ExpandableNotificationRow row, Runnable onDismissRunnable) {
        row.setExpansionLogger(this.mExpansionLogger, entry.notification.getKey());
        row.setBypassController(this.mKeyguardBypassController);
        row.setStatusBarStateController(this.mStatusBarStateController);
        row.setGroupManager(this.mGroupManager);
        row.setHeadsUpManager(this.mHeadsUpManager);
        row.setOnExpandClickListener(this.mPresenter);
        row.setInflationCallback(this.mInflationCallback);
        if (this.mAllowLongPress) {
            final NotificationGutsManager notificationGutsManager = this.mGutsManager;
            Objects.requireNonNull(notificationGutsManager);
            row.setLongPressListener(new ExpandableNotificationRow.LongPressListener() { // from class: com.android.systemui.statusbar.notification.collection.-$$Lambda$0lGYUT66Z7cr4TZs4rdZ8M7DQkw
                @Override // com.android.systemui.statusbar.notification.row.ExpandableNotificationRow.LongPressListener
                public final boolean onLongPress(View view, int i, int i2, NotificationMenuRowPlugin.MenuItem menuItem) {
                    return NotificationGutsManager.this.openGuts(view, i, i2, menuItem);
                }
            });
        }
        this.mListContainer.bindRow(row);
        getRemoteInputManager().bindRow(row);
        String pkg = sbn.getPackageName();
        String appname = pkg;
        try {
            ApplicationInfo info = pmUser.getApplicationInfo(pkg, 8704);
            if (info != null) {
                appname = String.valueOf(pmUser.getApplicationLabel(info));
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        row.setAppName(appname);
        row.setOnDismissRunnable(onDismissRunnable);
        row.setDescendantFocusability(393216);
        if (NotificationRemoteInputManager.ENABLE_REMOTE_INPUT) {
            row.setDescendantFocusability(131072);
        }
        row.setAppOpsOnClickListener(this.mOnAppOpsClickListener);
        this.mBindRowCallback.onBindRow(entry, pmUser, sbn, row);
    }

    @Override // com.android.systemui.statusbar.notification.collection.NotificationRowBinder
    public void onNotificationRankingUpdated(NotificationEntry entry, Integer oldImportance, NotificationUiAdjustment oldAdjustment, NotificationUiAdjustment newAdjustment) {
        if (NotificationUiAdjustment.needReinflate(oldAdjustment, newAdjustment)) {
            if (entry.rowExists()) {
                entry.reset();
                PackageManager pmUser = StatusBar.getPackageManagerForUser(this.mContext, entry.notification.getUser().getIdentifier());
                updateNotification(entry, pmUser, entry.notification, entry.getRow());
            }
        } else if (oldImportance != null && entry.importance != oldImportance.intValue() && entry.rowExists()) {
            entry.getRow().onNotificationRankingUpdated();
        }
    }

    private void updateNotification(NotificationEntry entry, PackageManager pmUser, StatusBarNotification sbn, ExpandableNotificationRow row) {
        row.setIsLowPriority(entry.ambient);
        boolean useIncreasedHeadsUp = false;
        try {
            ApplicationInfo info = pmUser.getApplicationInfo(sbn.getPackageName(), 0);
            entry.targetSdk = info.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException ex) {
            Log.e(TAG, "Failed looking up ApplicationInfo for " + sbn.getPackageName(), ex);
        }
        row.setLegacy(entry.targetSdk >= 9 && entry.targetSdk < 21);
        entry.setIconTag(R.id.icon_is_pre_L, Boolean.valueOf(entry.targetSdk < 21));
        entry.autoRedacted = entry.notification.getNotification().publicVersion == null;
        entry.setRow(row);
        row.setOnActivatedListener(this.mPresenter);
        boolean useIncreasedCollapsedHeight = this.mMessagingUtil.isImportantMessaging(sbn, entry.importance);
        if (useIncreasedCollapsedHeight && !this.mPresenter.isPresenterFullyCollapsed()) {
            useIncreasedHeadsUp = true;
        }
        row.setUseIncreasedCollapsedHeight(useIncreasedCollapsedHeight);
        row.setUseIncreasedHeadsUpHeight(useIncreasedHeadsUp);
        row.setEntry(entry);
        if (this.mNotificationInterruptionStateProvider.shouldHeadsUp(entry)) {
            row.updateInflationFlag(4, true);
        }
        row.setNeedsRedaction(((NotificationLockscreenUserManager) Dependency.get(NotificationLockscreenUserManager.class)).needsRedaction(entry));
        row.inflateViews();
        ((NotificationClicker) Preconditions.checkNotNull(this.mNotificationClicker)).register(row, sbn);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logNotificationExpansion(String key, boolean userAction, boolean expanded) {
        this.mNotificationLogger.onExpansionChanged(key, userAction, expanded);
    }
}
