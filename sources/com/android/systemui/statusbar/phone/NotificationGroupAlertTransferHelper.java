package com.android.systemui.statusbar.phone;

import android.os.SystemClock;
import android.service.notification.StatusBarNotification;
import android.util.ArrayMap;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.AlertingNotificationManager;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import java.util.ArrayList;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class NotificationGroupAlertTransferHelper implements OnHeadsUpChangedListener, StatusBarStateController.StateListener {
    private static final long ALERT_TRANSFER_TIMEOUT = 300;
    private NotificationEntryManager mEntryManager;
    private HeadsUpManager mHeadsUpManager;
    private boolean mIsDozing;
    private final ArrayMap<String, GroupAlertEntry> mGroupAlertEntries = new ArrayMap<>();
    private final ArrayMap<String, PendingAlertInfo> mPendingAlerts = new ArrayMap<>();
    private final NotificationGroupManager mGroupManager = (NotificationGroupManager) Dependency.get(NotificationGroupManager.class);
    private final NotificationGroupManager.OnGroupChangeListener mOnGroupChangeListener = new NotificationGroupManager.OnGroupChangeListener() { // from class: com.android.systemui.statusbar.phone.NotificationGroupAlertTransferHelper.1
        @Override // com.android.systemui.statusbar.phone.NotificationGroupManager.OnGroupChangeListener
        public void onGroupCreated(NotificationGroupManager.NotificationGroup group, String groupKey) {
            NotificationGroupAlertTransferHelper.this.mGroupAlertEntries.put(groupKey, new GroupAlertEntry(group));
        }

        @Override // com.android.systemui.statusbar.phone.NotificationGroupManager.OnGroupChangeListener
        public void onGroupRemoved(NotificationGroupManager.NotificationGroup group, String groupKey) {
            NotificationGroupAlertTransferHelper.this.mGroupAlertEntries.remove(groupKey);
        }

        @Override // com.android.systemui.statusbar.phone.NotificationGroupManager.OnGroupChangeListener
        public void onGroupSuppressionChanged(NotificationGroupManager.NotificationGroup group, boolean suppressed) {
            if (suppressed) {
                if (NotificationGroupAlertTransferHelper.this.mHeadsUpManager.isAlerting(group.summary.key)) {
                    NotificationGroupAlertTransferHelper.this.handleSuppressedSummaryAlerted(group.summary, NotificationGroupAlertTransferHelper.this.mHeadsUpManager);
                }
            } else if (group.summary != null) {
                GroupAlertEntry groupAlertEntry = (GroupAlertEntry) NotificationGroupAlertTransferHelper.this.mGroupAlertEntries.get(NotificationGroupAlertTransferHelper.this.mGroupManager.getGroupKey(group.summary.notification));
                if (groupAlertEntry.mAlertSummaryOnNextAddition) {
                    if (!NotificationGroupAlertTransferHelper.this.mHeadsUpManager.isAlerting(group.summary.key)) {
                        NotificationGroupAlertTransferHelper.this.alertNotificationWhenPossible(group.summary, NotificationGroupAlertTransferHelper.this.mHeadsUpManager);
                    }
                    groupAlertEntry.mAlertSummaryOnNextAddition = false;
                    return;
                }
                NotificationGroupAlertTransferHelper.this.checkShouldTransferBack(groupAlertEntry);
            }
        }
    };
    private final NotificationEntryListener mNotificationEntryListener = new NotificationEntryListener() { // from class: com.android.systemui.statusbar.phone.NotificationGroupAlertTransferHelper.2
        @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
        public void onPendingEntryAdded(NotificationEntry entry) {
            String groupKey = NotificationGroupAlertTransferHelper.this.mGroupManager.getGroupKey(entry.notification);
            GroupAlertEntry groupAlertEntry = (GroupAlertEntry) NotificationGroupAlertTransferHelper.this.mGroupAlertEntries.get(groupKey);
            if (groupAlertEntry != null) {
                NotificationGroupAlertTransferHelper.this.checkShouldTransferBack(groupAlertEntry);
            }
        }

        @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
        public void onEntryReinflated(NotificationEntry entry) {
            PendingAlertInfo alertInfo = (PendingAlertInfo) NotificationGroupAlertTransferHelper.this.mPendingAlerts.remove(entry.key);
            if (alertInfo == null) {
                return;
            }
            if (!alertInfo.isStillValid()) {
                entry.getRow().freeContentViewWhenSafe(NotificationGroupAlertTransferHelper.this.mHeadsUpManager.getContentFlag());
                return;
            }
            NotificationGroupAlertTransferHelper notificationGroupAlertTransferHelper = NotificationGroupAlertTransferHelper.this;
            notificationGroupAlertTransferHelper.alertNotificationWhenPossible(entry, notificationGroupAlertTransferHelper.mHeadsUpManager);
        }

        @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
        public void onEntryRemoved(NotificationEntry entry, NotificationVisibility visibility, boolean removedByUser) {
            NotificationGroupAlertTransferHelper.this.mPendingAlerts.remove(entry.key);
        }
    };

    @Inject
    public NotificationGroupAlertTransferHelper() {
        ((StatusBarStateController) Dependency.get(StatusBarStateController.class)).addCallback(this);
    }

    public void bind(NotificationEntryManager entryManager, NotificationGroupManager groupManager) {
        if (this.mEntryManager != null) {
            throw new IllegalStateException("Already bound.");
        }
        this.mEntryManager = entryManager;
        this.mEntryManager.addNotificationEntryListener(this.mNotificationEntryListener);
        groupManager.addOnGroupChangeListener(this.mOnGroupChangeListener);
    }

    public boolean isAlertTransferPending(NotificationEntry entry) {
        PendingAlertInfo alertInfo = this.mPendingAlerts.get(entry.key);
        return alertInfo != null && alertInfo.isStillValid();
    }

    public void setHeadsUpManager(HeadsUpManager headsUpManager) {
        this.mHeadsUpManager = headsUpManager;
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onStateChanged(int newState) {
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onDozingChanged(boolean isDozing) {
        if (this.mIsDozing != isDozing) {
            for (GroupAlertEntry groupAlertEntry : this.mGroupAlertEntries.values()) {
                groupAlertEntry.mLastAlertTransferTime = 0L;
                groupAlertEntry.mAlertSummaryOnNextAddition = false;
            }
        }
        this.mIsDozing = isDozing;
    }

    @Override // com.android.systemui.statusbar.policy.OnHeadsUpChangedListener
    public void onHeadsUpStateChanged(NotificationEntry entry, boolean isHeadsUp) {
        onAlertStateChanged(entry, isHeadsUp, this.mHeadsUpManager);
    }

    private void onAlertStateChanged(NotificationEntry entry, boolean isAlerting, AlertingNotificationManager alertManager) {
        if (isAlerting && this.mGroupManager.isSummaryOfSuppressedGroup(entry.notification)) {
            handleSuppressedSummaryAlerted(entry, alertManager);
        }
    }

    private int getPendingChildrenNotAlerting(NotificationGroupManager.NotificationGroup group) {
        NotificationEntryManager notificationEntryManager = this.mEntryManager;
        if (notificationEntryManager == null) {
            return 0;
        }
        int number = 0;
        Iterable<NotificationEntry> values = notificationEntryManager.getPendingNotificationsIterator();
        for (NotificationEntry entry : values) {
            if (isPendingNotificationInGroup(entry, group) && onlySummaryAlerts(entry)) {
                number++;
            }
        }
        return number;
    }

    private boolean pendingInflationsWillAddChildren(NotificationGroupManager.NotificationGroup group) {
        NotificationEntryManager notificationEntryManager = this.mEntryManager;
        if (notificationEntryManager == null) {
            return false;
        }
        Iterable<NotificationEntry> values = notificationEntryManager.getPendingNotificationsIterator();
        for (NotificationEntry entry : values) {
            if (isPendingNotificationInGroup(entry, group)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPendingNotificationInGroup(NotificationEntry entry, NotificationGroupManager.NotificationGroup group) {
        String groupKey = this.mGroupManager.getGroupKey(group.summary.notification);
        return this.mGroupManager.isGroupChild(entry.notification) && Objects.equals(this.mGroupManager.getGroupKey(entry.notification), groupKey) && !group.children.containsKey(entry.key);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSuppressedSummaryAlerted(NotificationEntry summary, AlertingNotificationManager alertManager) {
        NotificationEntry child;
        StatusBarNotification sbn = summary.notification;
        GroupAlertEntry groupAlertEntry = this.mGroupAlertEntries.get(this.mGroupManager.getGroupKey(sbn));
        if (!this.mGroupManager.isSummaryOfSuppressedGroup(summary.notification) || !alertManager.isAlerting(sbn.getKey()) || groupAlertEntry == null || pendingInflationsWillAddChildren(groupAlertEntry.mGroup) || (child = this.mGroupManager.getLogicalChildren(summary.notification).iterator().next()) == null || child.getRow().keepInParent() || child.isRowRemoved() || child.isRowDismissed()) {
            return;
        }
        if (!alertManager.isAlerting(child.key) && onlySummaryAlerts(summary)) {
            groupAlertEntry.mLastAlertTransferTime = SystemClock.elapsedRealtime();
        }
        transferAlertState(summary, child, alertManager);
    }

    private void transferAlertState(NotificationEntry fromEntry, NotificationEntry toEntry, AlertingNotificationManager alertManager) {
        alertManager.removeNotification(fromEntry.key, true);
        alertNotificationWhenPossible(toEntry, alertManager);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkShouldTransferBack(GroupAlertEntry groupAlertEntry) {
        if (SystemClock.elapsedRealtime() - groupAlertEntry.mLastAlertTransferTime < ALERT_TRANSFER_TIMEOUT) {
            NotificationEntry summary = groupAlertEntry.mGroup.summary;
            if (!onlySummaryAlerts(summary)) {
                return;
            }
            ArrayList<NotificationEntry> children = this.mGroupManager.getLogicalChildren(summary.notification);
            int numChildren = children.size();
            int numPendingChildren = getPendingChildrenNotAlerting(groupAlertEntry.mGroup);
            int numChildren2 = numChildren + numPendingChildren;
            if (numChildren2 <= 1) {
                return;
            }
            boolean releasedChild = false;
            for (int i = 0; i < children.size(); i++) {
                NotificationEntry entry = children.get(i);
                if (onlySummaryAlerts(entry) && this.mHeadsUpManager.isAlerting(entry.key)) {
                    releasedChild = true;
                    this.mHeadsUpManager.removeNotification(entry.key, true);
                }
                if (this.mPendingAlerts.containsKey(entry.key)) {
                    releasedChild = true;
                    this.mPendingAlerts.get(entry.key).mAbortOnInflation = true;
                }
            }
            if (releasedChild && !this.mHeadsUpManager.isAlerting(summary.key)) {
                boolean notifyImmediately = numChildren2 - numPendingChildren > 1;
                if (notifyImmediately) {
                    alertNotificationWhenPossible(summary, this.mHeadsUpManager);
                } else {
                    groupAlertEntry.mAlertSummaryOnNextAddition = true;
                }
                groupAlertEntry.mLastAlertTransferTime = 0L;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void alertNotificationWhenPossible(NotificationEntry entry, AlertingNotificationManager alertManager) {
        int contentFlag = alertManager.getContentFlag();
        if (!entry.getRow().isInflationFlagSet(contentFlag)) {
            this.mPendingAlerts.put(entry.key, new PendingAlertInfo(entry));
            entry.getRow().updateInflationFlag(contentFlag, true);
            entry.getRow().inflateViews();
        } else if (alertManager.isAlerting(entry.key)) {
            alertManager.updateNotification(entry.key, true);
        } else {
            alertManager.showNotification(entry);
        }
    }

    private boolean onlySummaryAlerts(NotificationEntry entry) {
        return entry.notification.getNotification().getGroupAlertBehavior() == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class PendingAlertInfo {
        boolean mAbortOnInflation;
        final NotificationEntry mEntry;
        final StatusBarNotification mOriginalNotification;

        PendingAlertInfo(NotificationEntry entry) {
            this.mOriginalNotification = entry.notification;
            this.mEntry = entry;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isStillValid() {
            return !this.mAbortOnInflation && this.mEntry.notification.getGroupKey() == this.mOriginalNotification.getGroupKey() && this.mEntry.notification.getNotification().isGroupSummary() == this.mOriginalNotification.getNotification().isGroupSummary();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class GroupAlertEntry {
        boolean mAlertSummaryOnNextAddition;
        final NotificationGroupManager.NotificationGroup mGroup;
        long mLastAlertTransferTime;

        GroupAlertEntry(NotificationGroupManager.NotificationGroup group) {
            this.mGroup = group;
        }
    }
}
