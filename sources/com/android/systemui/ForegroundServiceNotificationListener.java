package com.android.systemui;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.ForegroundServiceController;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class ForegroundServiceNotificationListener {
    private static final boolean DBG = false;
    private static final String TAG = "FgServiceController";
    private final Context mContext;
    private final ForegroundServiceController mForegroundServiceController;

    @Inject
    public ForegroundServiceNotificationListener(Context context, ForegroundServiceController foregroundServiceController, NotificationEntryManager notificationEntryManager) {
        this.mContext = context;
        this.mForegroundServiceController = foregroundServiceController;
        notificationEntryManager.addNotificationEntryListener(new NotificationEntryListener() { // from class: com.android.systemui.ForegroundServiceNotificationListener.1
            @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
            public void onPendingEntryAdded(NotificationEntry entry) {
                ForegroundServiceNotificationListener.this.addNotification(entry.notification, entry.importance);
            }

            @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
            public void onPostEntryUpdated(NotificationEntry entry) {
                ForegroundServiceNotificationListener.this.updateNotification(entry.notification, entry.importance);
            }

            @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
            public void onEntryRemoved(NotificationEntry entry, NotificationVisibility visibility, boolean removedByUser) {
                ForegroundServiceNotificationListener.this.removeNotification(entry.notification);
            }
        });
        notificationEntryManager.addNotificationLifetimeExtender(new ForegroundServiceLifetimeExtender());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addNotification(StatusBarNotification sbn, int importance) {
        updateNotification(sbn, importance);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeNotification(final StatusBarNotification sbn) {
        this.mForegroundServiceController.updateUserState(sbn.getUserId(), new ForegroundServiceController.UserStateUpdateCallback() { // from class: com.android.systemui.ForegroundServiceNotificationListener.2
            @Override // com.android.systemui.ForegroundServiceController.UserStateUpdateCallback
            public boolean updateUserState(ForegroundServicesUserState userState) {
                if (ForegroundServiceNotificationListener.this.mForegroundServiceController.isDisclosureNotification(sbn)) {
                    userState.setRunningServices(null, 0L);
                    return true;
                }
                return userState.removeNotification(sbn.getPackageName(), sbn.getKey());
            }

            @Override // com.android.systemui.ForegroundServiceController.UserStateUpdateCallback
            public void userStateNotFound(int userId) {
            }
        }, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNotification(final StatusBarNotification sbn, final int newImportance) {
        this.mForegroundServiceController.updateUserState(sbn.getUserId(), new ForegroundServiceController.UserStateUpdateCallback() { // from class: com.android.systemui.-$$Lambda$ForegroundServiceNotificationListener$AqXxERjDYAcDwpvtlCENdvbntCk
            @Override // com.android.systemui.ForegroundServiceController.UserStateUpdateCallback
            public final boolean updateUserState(ForegroundServicesUserState foregroundServicesUserState) {
                return ForegroundServiceNotificationListener.this.lambda$updateNotification$0$ForegroundServiceNotificationListener(sbn, newImportance, foregroundServicesUserState);
            }
        }, true);
    }

    public /* synthetic */ boolean lambda$updateNotification$0$ForegroundServiceNotificationListener(StatusBarNotification sbn, int newImportance, ForegroundServicesUserState userState) {
        if (this.mForegroundServiceController.isDisclosureNotification(sbn)) {
            Bundle extras = sbn.getNotification().extras;
            if (extras != null) {
                String[] svcs = extras.getStringArray("android.foregroundApps");
                userState.setRunningServices(svcs, sbn.getNotification().when);
            }
        } else {
            userState.removeNotification(sbn.getPackageName(), sbn.getKey());
            if ((sbn.getNotification().flags & 64) != 0) {
                if (newImportance > 1) {
                    userState.addImportantNotification(sbn.getPackageName(), sbn.getKey());
                }
                Notification.Builder builder = Notification.Builder.recoverBuilder(this.mContext, sbn.getNotification());
                if (builder.usesStandardHeader()) {
                    userState.addStandardLayoutNotification(sbn.getPackageName(), sbn.getKey());
                }
            }
        }
        return true;
    }
}
