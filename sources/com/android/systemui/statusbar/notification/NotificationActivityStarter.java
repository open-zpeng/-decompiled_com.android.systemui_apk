package com.android.systemui.statusbar.notification;

import android.content.Intent;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
/* loaded from: classes21.dex */
public interface NotificationActivityStarter {
    void onNotificationClicked(StatusBarNotification statusBarNotification, ExpandableNotificationRow expandableNotificationRow);

    void startNotificationGutsIntent(Intent intent, int i, ExpandableNotificationRow expandableNotificationRow);

    default boolean isCollapsingToShowActivityOverLockscreen() {
        return false;
    }
}
