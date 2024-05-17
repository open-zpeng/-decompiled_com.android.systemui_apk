package com.android.systemui.statusbar.notification;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import androidx.annotation.NonNull;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
/* loaded from: classes21.dex */
public interface NotificationEntryListener {
    default void onPendingEntryAdded(NotificationEntry entry) {
    }

    default void onBeforeNotificationAdded(NotificationEntry entry) {
    }

    default void onNotificationAdded(@NonNull NotificationEntry entry) {
    }

    default void onPreEntryUpdated(NotificationEntry entry) {
    }

    default void onPostEntryUpdated(@NonNull NotificationEntry entry) {
    }

    default void onEntryInflated(NotificationEntry entry, int inflatedFlags) {
    }

    default void onEntryReinflated(NotificationEntry entry) {
    }

    default void onInflationError(StatusBarNotification notification, Exception exception) {
    }

    default void onEntryRemoved(NotificationEntry entry, NotificationVisibility visibility, boolean removedByUser) {
    }

    default void onNotificationRankingUpdated(NotificationListenerService.RankingMap rankingMap) {
    }
}
