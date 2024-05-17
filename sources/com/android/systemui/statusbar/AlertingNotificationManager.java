package com.android.systemui.statusbar;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.statusbar.AlertingNotificationManager;
import com.android.systemui.statusbar.NotificationLifetimeExtender;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;
/* loaded from: classes21.dex */
public abstract class AlertingNotificationManager implements NotificationLifetimeExtender {
    private static final String TAG = "AlertNotifManager";
    protected int mAutoDismissNotificationDecay;
    protected int mMinimumDisplayTime;
    protected NotificationLifetimeExtender.NotificationSafeToRemoveCallback mNotificationLifetimeFinishedCallback;
    protected final Clock mClock = new Clock();
    protected final ArrayMap<String, AlertEntry> mAlertEntries = new ArrayMap<>();
    protected final ArraySet<NotificationEntry> mExtendedLifetimeAlertEntries = new ArraySet<>();
    @VisibleForTesting
    public Handler mHandler = new Handler(Looper.getMainLooper());

    public abstract int getContentFlag();

    protected abstract void onAlertEntryAdded(AlertEntry alertEntry);

    protected abstract void onAlertEntryRemoved(AlertEntry alertEntry);

    public void showNotification(NotificationEntry entry) {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "showNotification");
        }
        addAlertEntry(entry);
        updateNotification(entry.key, true);
        entry.setInterruption();
    }

    public boolean removeNotification(String key, boolean releaseImmediately) {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "removeNotification");
        }
        AlertEntry alertEntry = this.mAlertEntries.get(key);
        if (alertEntry == null) {
            return true;
        }
        if (releaseImmediately || canRemoveImmediately(key)) {
            removeAlertEntry(key);
            return true;
        }
        alertEntry.removeAsSoonAsPossible();
        return false;
    }

    public void updateNotification(String key, boolean alert) {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "updateNotification");
        }
        AlertEntry alertEntry = this.mAlertEntries.get(key);
        if (alertEntry == null) {
            return;
        }
        alertEntry.mEntry.sendAccessibilityEvent(2048);
        if (alert) {
            alertEntry.updateEntry(true);
        }
    }

    public void releaseAllImmediately() {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "releaseAllImmediately");
        }
        ArraySet<String> keysToRemove = new ArraySet<>(this.mAlertEntries.keySet());
        Iterator<String> it = keysToRemove.iterator();
        while (it.hasNext()) {
            String key = it.next();
            removeAlertEntry(key);
        }
    }

    public NotificationEntry getEntry(String key) {
        AlertEntry entry = this.mAlertEntries.get(key);
        if (entry != null) {
            return entry.mEntry;
        }
        return null;
    }

    public Stream<NotificationEntry> getAllEntries() {
        return this.mAlertEntries.values().stream().map(new Function() { // from class: com.android.systemui.statusbar.-$$Lambda$AlertingNotificationManager$p-A8-yzC_BK0PtkudKAmBZE-xfo
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                NotificationEntry notificationEntry;
                notificationEntry = ((AlertingNotificationManager.AlertEntry) obj).mEntry;
                return notificationEntry;
            }
        });
    }

    public boolean hasNotifications() {
        return !this.mAlertEntries.isEmpty();
    }

    public boolean isAlerting(String key) {
        return this.mAlertEntries.containsKey(key);
    }

    protected final void addAlertEntry(NotificationEntry entry) {
        AlertEntry alertEntry = createAlertEntry();
        alertEntry.setEntry(entry);
        this.mAlertEntries.put(entry.key, alertEntry);
        onAlertEntryAdded(alertEntry);
        entry.sendAccessibilityEvent(2048);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void removeAlertEntry(String key) {
        AlertEntry alertEntry = this.mAlertEntries.get(key);
        if (alertEntry == null) {
            return;
        }
        NotificationEntry entry = alertEntry.mEntry;
        this.mAlertEntries.remove(key);
        onAlertEntryRemoved(alertEntry);
        entry.sendAccessibilityEvent(2048);
        alertEntry.reset();
        if (this.mExtendedLifetimeAlertEntries.contains(entry)) {
            NotificationLifetimeExtender.NotificationSafeToRemoveCallback notificationSafeToRemoveCallback = this.mNotificationLifetimeFinishedCallback;
            if (notificationSafeToRemoveCallback != null) {
                notificationSafeToRemoveCallback.onSafeToRemove(key);
            }
            this.mExtendedLifetimeAlertEntries.remove(entry);
        }
    }

    protected AlertEntry createAlertEntry() {
        return new AlertEntry();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean canRemoveImmediately(String key) {
        AlertEntry alertEntry = this.mAlertEntries.get(key);
        return alertEntry == null || alertEntry.wasShownLongEnough() || alertEntry.mEntry.isRowDismissed();
    }

    @Override // com.android.systemui.statusbar.NotificationLifetimeExtender
    public void setCallback(NotificationLifetimeExtender.NotificationSafeToRemoveCallback callback) {
        this.mNotificationLifetimeFinishedCallback = callback;
    }

    @Override // com.android.systemui.statusbar.NotificationLifetimeExtender
    public boolean shouldExtendLifetime(NotificationEntry entry) {
        return !canRemoveImmediately(entry.key);
    }

    @Override // com.android.systemui.statusbar.NotificationLifetimeExtender
    public void setShouldManageLifetime(NotificationEntry entry, boolean shouldExtend) {
        if (shouldExtend) {
            this.mExtendedLifetimeAlertEntries.add(entry);
            AlertEntry alertEntry = this.mAlertEntries.get(entry.key);
            alertEntry.removeAsSoonAsPossible();
            return;
        }
        this.mExtendedLifetimeAlertEntries.remove(entry);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes21.dex */
    public class AlertEntry implements Comparable<AlertEntry> {
        public long mEarliestRemovaltime;
        public NotificationEntry mEntry;
        public long mPostTime;
        protected Runnable mRemoveAlertRunnable;

        /* JADX INFO: Access modifiers changed from: protected */
        public AlertEntry() {
        }

        public /* synthetic */ void lambda$setEntry$0$AlertingNotificationManager$AlertEntry(NotificationEntry entry) {
            AlertingNotificationManager.this.removeAlertEntry(entry.key);
        }

        public void setEntry(final NotificationEntry entry) {
            setEntry(entry, new Runnable() { // from class: com.android.systemui.statusbar.-$$Lambda$AlertingNotificationManager$AlertEntry$H0BO9fDKgUoiMeVuexcatZzpMyY
                @Override // java.lang.Runnable
                public final void run() {
                    AlertingNotificationManager.AlertEntry.this.lambda$setEntry$0$AlertingNotificationManager$AlertEntry(entry);
                }
            });
        }

        public void setEntry(NotificationEntry entry, Runnable removeAlertRunnable) {
            this.mEntry = entry;
            this.mRemoveAlertRunnable = removeAlertRunnable;
            this.mPostTime = calculatePostTime();
            updateEntry(true);
        }

        public void updateEntry(boolean updatePostTime) {
            if (Log.isLoggable(AlertingNotificationManager.TAG, 2)) {
                Log.v(AlertingNotificationManager.TAG, "updateEntry");
            }
            long currentTime = AlertingNotificationManager.this.mClock.currentTimeMillis();
            this.mEarliestRemovaltime = AlertingNotificationManager.this.mMinimumDisplayTime + currentTime;
            if (updatePostTime) {
                this.mPostTime = Math.max(this.mPostTime, currentTime);
            }
            removeAutoRemovalCallbacks();
            if (!isSticky()) {
                long finishTime = calculateFinishTime();
                long removeDelay = Math.max(finishTime - currentTime, AlertingNotificationManager.this.mMinimumDisplayTime);
                AlertingNotificationManager.this.mHandler.postDelayed(this.mRemoveAlertRunnable, removeDelay);
            }
        }

        protected boolean isSticky() {
            return false;
        }

        public boolean wasShownLongEnough() {
            return this.mEarliestRemovaltime < AlertingNotificationManager.this.mClock.currentTimeMillis();
        }

        @Override // java.lang.Comparable
        public int compareTo(AlertEntry alertEntry) {
            long j = this.mPostTime;
            long j2 = alertEntry.mPostTime;
            if (j < j2) {
                return 1;
            }
            if (j == j2) {
                return this.mEntry.key.compareTo(alertEntry.mEntry.key);
            }
            return -1;
        }

        public void reset() {
            this.mEntry = null;
            removeAutoRemovalCallbacks();
            this.mRemoveAlertRunnable = null;
        }

        public void removeAutoRemovalCallbacks() {
            if (this.mRemoveAlertRunnable != null) {
                AlertingNotificationManager.this.mHandler.removeCallbacks(this.mRemoveAlertRunnable);
            }
        }

        public void removeAsSoonAsPossible() {
            if (this.mRemoveAlertRunnable != null) {
                removeAutoRemovalCallbacks();
                AlertingNotificationManager.this.mHandler.postDelayed(this.mRemoveAlertRunnable, this.mEarliestRemovaltime - AlertingNotificationManager.this.mClock.currentTimeMillis());
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public long calculatePostTime() {
            return AlertingNotificationManager.this.mClock.currentTimeMillis();
        }

        protected long calculateFinishTime() {
            return this.mPostTime + AlertingNotificationManager.this.mAutoDismissNotificationDecay;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes21.dex */
    public static final class Clock {
        protected Clock() {
        }

        public long currentTimeMillis() {
            return SystemClock.elapsedRealtime();
        }
    }
}
