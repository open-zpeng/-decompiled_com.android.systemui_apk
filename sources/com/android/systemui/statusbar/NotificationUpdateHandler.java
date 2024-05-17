package com.android.systemui.statusbar;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
/* loaded from: classes21.dex */
public interface NotificationUpdateHandler {
    void addNotification(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap);

    void removeNotification(String str, NotificationListenerService.RankingMap rankingMap, int i);

    void updateNotification(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap);

    void updateNotificationRanking(NotificationListenerService.RankingMap rankingMap);
}
