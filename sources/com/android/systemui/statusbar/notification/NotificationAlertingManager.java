package com.android.systemui.statusbar.notification;

import android.app.Notification;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class NotificationAlertingManager {
    private static final String TAG = "NotifAlertManager";
    private HeadsUpManager mHeadsUpManager;
    private final NotificationInterruptionStateProvider mNotificationInterruptionStateProvider;
    private final NotificationListener mNotificationListener;
    private final NotificationRemoteInputManager mRemoteInputManager;
    private final Lazy<ShadeController> mShadeController;
    private final VisualStabilityManager mVisualStabilityManager;

    @Inject
    public NotificationAlertingManager(NotificationEntryManager notificationEntryManager, NotificationRemoteInputManager remoteInputManager, VisualStabilityManager visualStabilityManager, Lazy<ShadeController> shadeController, NotificationInterruptionStateProvider notificationInterruptionStateProvider, NotificationListener notificationListener) {
        this.mRemoteInputManager = remoteInputManager;
        this.mVisualStabilityManager = visualStabilityManager;
        this.mShadeController = shadeController;
        this.mNotificationInterruptionStateProvider = notificationInterruptionStateProvider;
        this.mNotificationListener = notificationListener;
        notificationEntryManager.addNotificationEntryListener(new NotificationEntryListener() { // from class: com.android.systemui.statusbar.notification.NotificationAlertingManager.1
            @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
            public void onEntryInflated(NotificationEntry entry, int inflatedFlags) {
                NotificationAlertingManager.this.showAlertingView(entry, inflatedFlags);
            }

            @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
            public void onPostEntryUpdated(NotificationEntry entry) {
                NotificationAlertingManager.this.updateAlertState(entry);
            }

            @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
            public void onEntryRemoved(NotificationEntry entry, NotificationVisibility visibility, boolean removedByUser) {
                NotificationAlertingManager.this.stopAlerting(entry.key);
            }
        });
    }

    public void setHeadsUpManager(HeadsUpManager headsUpManager) {
        this.mHeadsUpManager = headsUpManager;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showAlertingView(NotificationEntry entry, int inflatedFlags) {
        if ((inflatedFlags & 4) != 0) {
            if (this.mNotificationInterruptionStateProvider.shouldHeadsUp(entry)) {
                this.mHeadsUpManager.showNotification(entry);
                if (!this.mShadeController.get().isDozing()) {
                    setNotificationShown(entry.notification);
                    return;
                }
                return;
            }
            entry.freeContentViewWhenSafe(4);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAlertState(NotificationEntry entry) {
        boolean alertAgain = alertAgain(entry, entry.notification.getNotification());
        boolean shouldAlert = this.mNotificationInterruptionStateProvider.shouldHeadsUp(entry);
        boolean wasAlerting = this.mHeadsUpManager.isAlerting(entry.key);
        if (wasAlerting) {
            if (shouldAlert) {
                this.mHeadsUpManager.updateNotification(entry.key, alertAgain);
            } else if (!this.mHeadsUpManager.isEntryAutoHeadsUpped(entry.key)) {
                this.mHeadsUpManager.removeNotification(entry.key, false);
            }
        } else if (shouldAlert && alertAgain) {
            this.mHeadsUpManager.showNotification(entry);
        }
    }

    public static boolean alertAgain(NotificationEntry oldEntry, Notification newNotification) {
        return oldEntry == null || !oldEntry.hasInterrupted() || (newNotification.flags & 8) == 0;
    }

    private void setNotificationShown(StatusBarNotification n) {
        try {
            this.mNotificationListener.setNotificationsShown(new String[]{n.getKey()});
        } catch (RuntimeException e) {
            Log.d(TAG, "failed setNotificationsShown: ", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopAlerting(String key) {
        if (this.mHeadsUpManager.isAlerting(key)) {
            boolean ignoreEarliestRemovalTime = (this.mRemoteInputManager.getController().isSpinning(key) && !NotificationRemoteInputManager.FORCE_REMOTE_INPUT_HISTORY) || !this.mVisualStabilityManager.isReorderingAllowed();
            this.mHeadsUpManager.removeNotification(key, ignoreEarliestRemovalTime);
        }
    }
}
