package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.statusbar.AlertingNotificationManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
/* loaded from: classes21.dex */
public abstract class HeadsUpManager extends AlertingNotificationManager {
    private static final String SETTING_HEADS_UP_SNOOZE_LENGTH_MS = "heads_up_snooze_length_ms";
    private static final String TAG = "HeadsUpManager";
    protected final Context mContext;
    protected boolean mHasPinnedNotification;
    protected int mSnoozeLengthMs;
    private final ArrayMap<String, Long> mSnoozedPackages;
    protected int mTouchAcceptanceDelay;
    protected int mUser;
    protected final HashSet<OnHeadsUpChangedListener> mListeners = new HashSet<>();
    private final AccessibilityManagerWrapper mAccessibilityMgr = (AccessibilityManagerWrapper) Dependency.get(AccessibilityManagerWrapper.class);

    public HeadsUpManager(final Context context) {
        this.mContext = context;
        Resources resources = context.getResources();
        this.mMinimumDisplayTime = resources.getInteger(R.integer.heads_up_notification_minimum_time);
        this.mAutoDismissNotificationDecay = resources.getInteger(R.integer.heads_up_notification_decay);
        this.mTouchAcceptanceDelay = resources.getInteger(R.integer.touch_acceptance_delay);
        this.mSnoozedPackages = new ArrayMap<>();
        int defaultSnoozeLengthMs = resources.getInteger(R.integer.heads_up_default_snooze_length_ms);
        this.mSnoozeLengthMs = Settings.Global.getInt(context.getContentResolver(), SETTING_HEADS_UP_SNOOZE_LENGTH_MS, defaultSnoozeLengthMs);
        ContentObserver settingsObserver = new ContentObserver(this.mHandler) { // from class: com.android.systemui.statusbar.policy.HeadsUpManager.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                int packageSnoozeLengthMs = Settings.Global.getInt(context.getContentResolver(), HeadsUpManager.SETTING_HEADS_UP_SNOOZE_LENGTH_MS, -1);
                if (packageSnoozeLengthMs > -1 && packageSnoozeLengthMs != HeadsUpManager.this.mSnoozeLengthMs) {
                    HeadsUpManager.this.mSnoozeLengthMs = packageSnoozeLengthMs;
                    if (Log.isLoggable(HeadsUpManager.TAG, 2)) {
                        Log.v(HeadsUpManager.TAG, "mSnoozeLengthMs = " + HeadsUpManager.this.mSnoozeLengthMs);
                    }
                }
            }
        };
        context.getContentResolver().registerContentObserver(Settings.Global.getUriFor(SETTING_HEADS_UP_SNOOZE_LENGTH_MS), false, settingsObserver);
    }

    public void addListener(OnHeadsUpChangedListener listener) {
        this.mListeners.add(listener);
    }

    public void removeListener(OnHeadsUpChangedListener listener) {
        this.mListeners.remove(listener);
    }

    @Override // com.android.systemui.statusbar.AlertingNotificationManager
    public void updateNotification(String key, boolean alert) {
        super.updateNotification(key, alert);
        AlertingNotificationManager.AlertEntry alertEntry = getHeadsUpEntry(key);
        if (alert && alertEntry != null) {
            setEntryPinned((HeadsUpEntry) alertEntry, shouldHeadsUpBecomePinned(alertEntry.mEntry));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean shouldHeadsUpBecomePinned(NotificationEntry entry) {
        return hasFullScreenIntent(entry);
    }

    protected boolean hasFullScreenIntent(NotificationEntry entry) {
        return entry.notification.getNotification().fullScreenIntent != null;
    }

    protected void setEntryPinned(HeadsUpEntry headsUpEntry, boolean isPinned) {
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "setEntryPinned: " + isPinned);
        }
        NotificationEntry entry = headsUpEntry.mEntry;
        if (entry.isRowPinned() != isPinned) {
            entry.setRowPinned(isPinned);
            updatePinnedMode();
            Iterator<OnHeadsUpChangedListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                OnHeadsUpChangedListener listener = it.next();
                if (isPinned) {
                    listener.onHeadsUpPinned(entry);
                } else {
                    listener.onHeadsUpUnPinned(entry);
                }
            }
        }
    }

    @Override // com.android.systemui.statusbar.AlertingNotificationManager
    public int getContentFlag() {
        return 4;
    }

    @Override // com.android.systemui.statusbar.AlertingNotificationManager
    protected void onAlertEntryAdded(AlertingNotificationManager.AlertEntry alertEntry) {
        NotificationEntry entry = alertEntry.mEntry;
        entry.setHeadsUp(true);
        setEntryPinned((HeadsUpEntry) alertEntry, shouldHeadsUpBecomePinned(entry));
        Iterator<OnHeadsUpChangedListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            OnHeadsUpChangedListener listener = it.next();
            listener.onHeadsUpStateChanged(entry, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.AlertingNotificationManager
    public void onAlertEntryRemoved(AlertingNotificationManager.AlertEntry alertEntry) {
        NotificationEntry entry = alertEntry.mEntry;
        entry.setHeadsUp(false);
        setEntryPinned((HeadsUpEntry) alertEntry, false);
        Iterator<OnHeadsUpChangedListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            OnHeadsUpChangedListener listener = it.next();
            listener.onHeadsUpStateChanged(entry, false);
        }
        entry.freeContentViewWhenSafe(4);
    }

    protected void updatePinnedMode() {
        boolean hasPinnedNotification = hasPinnedNotificationInternal();
        if (hasPinnedNotification == this.mHasPinnedNotification) {
            return;
        }
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "Pinned mode changed: " + this.mHasPinnedNotification + " -> " + hasPinnedNotification);
        }
        this.mHasPinnedNotification = hasPinnedNotification;
        if (this.mHasPinnedNotification) {
            MetricsLogger.count(this.mContext, "note_peek", 1);
        }
        Iterator<OnHeadsUpChangedListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            OnHeadsUpChangedListener listener = it.next();
            listener.onHeadsUpPinnedModeChanged(hasPinnedNotification);
        }
    }

    public boolean isSnoozed(String packageName) {
        String key = snoozeKey(packageName, this.mUser);
        Long snoozedUntil = this.mSnoozedPackages.get(key);
        if (snoozedUntil != null) {
            if (snoozedUntil.longValue() > this.mClock.currentTimeMillis()) {
                if (Log.isLoggable(TAG, 2)) {
                    Log.v(TAG, key + " snoozed");
                    return true;
                }
                return true;
            }
            this.mSnoozedPackages.remove(packageName);
            return false;
        }
        return false;
    }

    public void snooze() {
        for (String key : this.mAlertEntries.keySet()) {
            AlertingNotificationManager.AlertEntry entry = getHeadsUpEntry(key);
            String packageName = entry.mEntry.notification.getPackageName();
            this.mSnoozedPackages.put(snoozeKey(packageName, this.mUser), Long.valueOf(this.mClock.currentTimeMillis() + this.mSnoozeLengthMs));
        }
    }

    private static String snoozeKey(String packageName, int user) {
        return user + "," + packageName;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HeadsUpEntry getHeadsUpEntry(String key) {
        return (HeadsUpEntry) this.mAlertEntries.get(key);
    }

    public NotificationEntry getTopEntry() {
        HeadsUpEntry topEntry = getTopHeadsUpEntry();
        if (topEntry != null) {
            return topEntry.mEntry;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HeadsUpEntry getTopHeadsUpEntry() {
        if (this.mAlertEntries.isEmpty()) {
            return null;
        }
        HeadsUpEntry topEntry = null;
        for (AlertingNotificationManager.AlertEntry entry : this.mAlertEntries.values()) {
            if (topEntry == null || entry.compareTo((AlertingNotificationManager.AlertEntry) topEntry) < 0) {
                topEntry = (HeadsUpEntry) entry;
            }
        }
        return topEntry;
    }

    public void setUser(int user) {
        this.mUser = user;
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("HeadsUpManager state:");
        dumpInternal(fd, pw, args);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dumpInternal(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.print("  mTouchAcceptanceDelay=");
        pw.println(this.mTouchAcceptanceDelay);
        pw.print("  mSnoozeLengthMs=");
        pw.println(this.mSnoozeLengthMs);
        pw.print("  now=");
        pw.println(this.mClock.currentTimeMillis());
        pw.print("  mUser=");
        pw.println(this.mUser);
        for (AlertingNotificationManager.AlertEntry entry : this.mAlertEntries.values()) {
            pw.print("  HeadsUpEntry=");
            pw.println(entry.mEntry);
        }
        int N = this.mSnoozedPackages.size();
        pw.println("  snoozed packages: " + N);
        for (int i = 0; i < N; i++) {
            pw.print("    ");
            pw.print(this.mSnoozedPackages.valueAt(i));
            pw.print(", ");
            pw.println(this.mSnoozedPackages.keyAt(i));
        }
    }

    public boolean hasPinnedHeadsUp() {
        return this.mHasPinnedNotification;
    }

    private boolean hasPinnedNotificationInternal() {
        for (String key : this.mAlertEntries.keySet()) {
            AlertingNotificationManager.AlertEntry entry = getHeadsUpEntry(key);
            if (entry.mEntry.isRowPinned()) {
                return true;
            }
        }
        return false;
    }

    public void unpinAll(boolean userUnPinned) {
        for (String key : this.mAlertEntries.keySet()) {
            HeadsUpEntry entry = getHeadsUpEntry(key);
            setEntryPinned(entry, false);
            entry.updateEntry(false);
            if (userUnPinned && entry.mEntry != null && entry.mEntry.mustStayOnScreen()) {
                entry.mEntry.setHeadsUpIsVisible();
            }
        }
    }

    public boolean isTrackingHeadsUp() {
        return false;
    }

    public int compare(NotificationEntry a, NotificationEntry b) {
        AlertingNotificationManager.AlertEntry aEntry = getHeadsUpEntry(a.key);
        AlertingNotificationManager.AlertEntry bEntry = getHeadsUpEntry(b.key);
        if (aEntry == null || bEntry == null) {
            return aEntry == null ? 1 : -1;
        }
        return aEntry.compareTo(bEntry);
    }

    public void setExpanded(NotificationEntry entry, boolean expanded) {
        HeadsUpEntry headsUpEntry = getHeadsUpEntry(entry.key);
        if (headsUpEntry != null && entry.isRowPinned()) {
            headsUpEntry.setExpanded(expanded);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.AlertingNotificationManager
    public HeadsUpEntry createAlertEntry() {
        return new HeadsUpEntry();
    }

    public void onDensityOrFontScaleChanged() {
    }

    public boolean isEntryAutoHeadsUpped(String key) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes21.dex */
    public class HeadsUpEntry extends AlertingNotificationManager.AlertEntry {
        protected boolean expanded;
        public boolean remoteInputActive;

        /* JADX INFO: Access modifiers changed from: protected */
        public HeadsUpEntry() {
            super();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.systemui.statusbar.AlertingNotificationManager.AlertEntry
        public boolean isSticky() {
            return (this.mEntry.isRowPinned() && this.expanded) || this.remoteInputActive || HeadsUpManager.this.hasFullScreenIntent(this.mEntry);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.systemui.statusbar.AlertingNotificationManager.AlertEntry, java.lang.Comparable
        public int compareTo(AlertingNotificationManager.AlertEntry alertEntry) {
            HeadsUpEntry headsUpEntry = (HeadsUpEntry) alertEntry;
            boolean isPinned = this.mEntry.isRowPinned();
            boolean otherPinned = headsUpEntry.mEntry.isRowPinned();
            if (!isPinned || otherPinned) {
                if (!isPinned && otherPinned) {
                    return 1;
                }
                boolean selfFullscreen = HeadsUpManager.this.hasFullScreenIntent(this.mEntry);
                boolean otherFullscreen = HeadsUpManager.this.hasFullScreenIntent(headsUpEntry.mEntry);
                if (selfFullscreen && !otherFullscreen) {
                    return -1;
                }
                if (!selfFullscreen && otherFullscreen) {
                    return 1;
                }
                if (this.remoteInputActive && !headsUpEntry.remoteInputActive) {
                    return -1;
                }
                if (!this.remoteInputActive && headsUpEntry.remoteInputActive) {
                    return 1;
                }
                return super.compareTo((AlertingNotificationManager.AlertEntry) headsUpEntry);
            }
            return -1;
        }

        public void setExpanded(boolean expanded) {
            this.expanded = expanded;
        }

        @Override // com.android.systemui.statusbar.AlertingNotificationManager.AlertEntry
        public void reset() {
            super.reset();
            this.expanded = false;
            this.remoteInputActive = false;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.systemui.statusbar.AlertingNotificationManager.AlertEntry
        public long calculatePostTime() {
            return super.calculatePostTime() + HeadsUpManager.this.mTouchAcceptanceDelay;
        }

        @Override // com.android.systemui.statusbar.AlertingNotificationManager.AlertEntry
        protected long calculateFinishTime() {
            return this.mPostTime + getRecommendedHeadsUpTimeoutMs(HeadsUpManager.this.mAutoDismissNotificationDecay);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public int getRecommendedHeadsUpTimeoutMs(int requestedTimeout) {
            return HeadsUpManager.this.mAccessibilityMgr.getRecommendedTimeoutMillis(requestedTimeout, 7);
        }
    }
}
