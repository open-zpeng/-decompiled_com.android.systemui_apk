package com.android.systemui;

import android.os.Handler;
import android.os.Looper;
import android.util.ArraySet;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.statusbar.NotificationLifetimeExtender;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
/* loaded from: classes21.dex */
public class ForegroundServiceLifetimeExtender implements NotificationLifetimeExtender {
    @VisibleForTesting
    static final int MIN_FGS_TIME_MS = 5000;
    private static final String TAG = "FGSLifetimeExtender";
    private NotificationLifetimeExtender.NotificationSafeToRemoveCallback mNotificationSafeToRemoveCallback;
    private ArraySet<NotificationEntry> mManagedEntries = new ArraySet<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override // com.android.systemui.statusbar.NotificationLifetimeExtender
    public void setCallback(NotificationLifetimeExtender.NotificationSafeToRemoveCallback callback) {
        this.mNotificationSafeToRemoveCallback = callback;
    }

    @Override // com.android.systemui.statusbar.NotificationLifetimeExtender
    public boolean shouldExtendLifetime(NotificationEntry entry) {
        if ((entry.notification.getNotification().flags & 64) == 0) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        return currentTime - entry.notification.getPostTime() < 5000;
    }

    @Override // com.android.systemui.statusbar.NotificationLifetimeExtender
    public boolean shouldExtendLifetimeForPendingNotification(NotificationEntry entry) {
        return shouldExtendLifetime(entry);
    }

    @Override // com.android.systemui.statusbar.NotificationLifetimeExtender
    public void setShouldManageLifetime(final NotificationEntry entry, boolean shouldManage) {
        if (!shouldManage) {
            this.mManagedEntries.remove(entry);
            return;
        }
        this.mManagedEntries.add(entry);
        Runnable r = new Runnable() { // from class: com.android.systemui.-$$Lambda$ForegroundServiceLifetimeExtender$-eZMtetouaKnxc7j2jqc6zpz_AA
            @Override // java.lang.Runnable
            public final void run() {
                ForegroundServiceLifetimeExtender.this.lambda$setShouldManageLifetime$0$ForegroundServiceLifetimeExtender(entry);
            }
        };
        long delayAmt = 5000 - (System.currentTimeMillis() - entry.notification.getPostTime());
        this.mHandler.postDelayed(r, delayAmt);
    }

    public /* synthetic */ void lambda$setShouldManageLifetime$0$ForegroundServiceLifetimeExtender(NotificationEntry entry) {
        if (this.mManagedEntries.contains(entry)) {
            this.mManagedEntries.remove(entry);
            NotificationLifetimeExtender.NotificationSafeToRemoveCallback notificationSafeToRemoveCallback = this.mNotificationSafeToRemoveCallback;
            if (notificationSafeToRemoveCallback != null) {
                notificationSafeToRemoveCallback.onSafeToRemove(entry.key);
            }
        }
    }
}
