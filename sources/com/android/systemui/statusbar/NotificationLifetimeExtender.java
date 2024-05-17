package com.android.systemui.statusbar;

import androidx.annotation.NonNull;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
/* loaded from: classes21.dex */
public interface NotificationLifetimeExtender {

    /* loaded from: classes21.dex */
    public interface NotificationSafeToRemoveCallback {
        void onSafeToRemove(String str);
    }

    void setCallback(@NonNull NotificationSafeToRemoveCallback notificationSafeToRemoveCallback);

    void setShouldManageLifetime(@NonNull NotificationEntry notificationEntry, boolean z);

    boolean shouldExtendLifetime(@NonNull NotificationEntry notificationEntry);

    default boolean shouldExtendLifetimeForPendingNotification(@NonNull NotificationEntry pendingEntry) {
        return false;
    }
}
