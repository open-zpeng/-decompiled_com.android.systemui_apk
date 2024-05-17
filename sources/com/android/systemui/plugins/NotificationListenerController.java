package com.android.systemui.plugins;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import com.android.systemui.plugins.annotations.DependsOn;
import com.android.systemui.plugins.annotations.ProvidesInterface;
@ProvidesInterface(action = NotificationListenerController.ACTION, version = 1)
@DependsOn(target = NotificationProvider.class)
/* loaded from: classes21.dex */
public interface NotificationListenerController extends Plugin {
    public static final String ACTION = "com.android.systemui.action.PLUGIN_NOTIFICATION_ASSISTANT";
    public static final int VERSION = 1;

    @ProvidesInterface(version = 1)
    /* loaded from: classes21.dex */
    public interface NotificationProvider {
        public static final int VERSION = 1;

        void addNotification(StatusBarNotification statusBarNotification);

        StatusBarNotification[] getActiveNotifications();

        NotificationListenerService.RankingMap getRankingMap();

        void removeNotification(StatusBarNotification statusBarNotification);

        void updateRanking();
    }

    void onListenerConnected(NotificationProvider notificationProvider);

    default boolean onNotificationPosted(StatusBarNotification sbn, NotificationListenerService.RankingMap rankingMap) {
        return false;
    }

    default boolean onNotificationRemoved(StatusBarNotification sbn, NotificationListenerService.RankingMap rankingMap) {
        return false;
    }

    default StatusBarNotification[] getActiveNotifications(StatusBarNotification[] activeNotifications) {
        return activeNotifications;
    }

    default NotificationListenerService.RankingMap getCurrentRanking(NotificationListenerService.RankingMap currentRanking) {
        return currentRanking;
    }
}
