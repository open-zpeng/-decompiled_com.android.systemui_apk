package com.android.systemui.statusbar.notification;

import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import android.util.ArraySet;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.internal.util.Preconditions;
import com.android.systemui.ForegroundServiceController;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
/* loaded from: classes21.dex */
public class NotificationListController {
    private final DeviceProvisionedController mDeviceProvisionedController;
    private final NotificationEntryManager mEntryManager;
    private final ForegroundServiceController mForegroundServiceController;
    private final NotificationListContainer mListContainer;
    private final NotificationEntryListener mEntryListener = new NotificationEntryListener() { // from class: com.android.systemui.statusbar.notification.NotificationListController.1
        @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
        public void onEntryRemoved(NotificationEntry entry, NotificationVisibility visibility, boolean removedByUser) {
            NotificationListController.this.mListContainer.cleanUpViewStateForEntry(entry);
        }

        @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
        public void onBeforeNotificationAdded(NotificationEntry entry) {
            NotificationListController.this.tagForeground(entry.notification);
        }
    };
    private final DeviceProvisionedController.DeviceProvisionedListener mDeviceProvisionedListener = new DeviceProvisionedController.DeviceProvisionedListener() { // from class: com.android.systemui.statusbar.notification.NotificationListController.2
        @Override // com.android.systemui.statusbar.policy.DeviceProvisionedController.DeviceProvisionedListener
        public void onDeviceProvisionedChanged() {
            NotificationListController.this.mEntryManager.updateNotifications();
        }
    };

    public NotificationListController(NotificationEntryManager entryManager, NotificationListContainer listContainer, ForegroundServiceController foregroundServiceController, DeviceProvisionedController deviceProvisionedController) {
        this.mEntryManager = (NotificationEntryManager) Preconditions.checkNotNull(entryManager);
        this.mListContainer = (NotificationListContainer) Preconditions.checkNotNull(listContainer);
        this.mForegroundServiceController = (ForegroundServiceController) Preconditions.checkNotNull(foregroundServiceController);
        this.mDeviceProvisionedController = (DeviceProvisionedController) Preconditions.checkNotNull(deviceProvisionedController);
    }

    public void bind() {
        this.mEntryManager.addNotificationEntryListener(this.mEntryListener);
        this.mDeviceProvisionedController.addCallback(this.mDeviceProvisionedListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void tagForeground(StatusBarNotification notification) {
        ArraySet<Integer> activeOps = this.mForegroundServiceController.getAppOps(notification.getUserId(), notification.getPackageName());
        if (activeOps != null) {
            int len = activeOps.size();
            for (int i = 0; i < len; i++) {
                updateNotificationsForAppOp(activeOps.valueAt(i).intValue(), notification.getUid(), notification.getPackageName(), true);
            }
        }
    }

    public void updateNotificationsForAppOp(int appOp, int uid, String pkg, boolean showIcon) {
        String foregroundKey = this.mForegroundServiceController.getStandardLayoutKey(UserHandle.getUserId(uid), pkg);
        if (foregroundKey != null) {
            this.mEntryManager.getNotificationData().updateAppOp(appOp, uid, pkg, foregroundKey, showIcon);
            this.mEntryManager.updateNotifications();
        }
    }
}
