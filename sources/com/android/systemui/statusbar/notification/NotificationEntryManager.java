package com.android.systemui.statusbar.notification;

import android.content.Context;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.statusbar.NotificationLifetimeExtender;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationRemoveInterceptor;
import com.android.systemui.statusbar.NotificationUiAdjustment;
import com.android.systemui.statusbar.NotificationUpdateHandler;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.notification.collection.NotificationData;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.NotificationRowBinder;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.row.NotificationContentInflater;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.util.leak.LeakDetector;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class NotificationEntryManager implements Dumpable, NotificationContentInflater.InflationCallback, NotificationUpdateHandler, VisualStabilityManager.Callback {
    public static final int UNDEFINED_DISMISS_REASON = 0;
    private NotificationRowBinder mNotificationRowBinder;
    private NotificationPresenter mPresenter;
    private NotificationRemoteInputManager mRemoteInputManager;
    private NotificationRemoveInterceptor mRemoveInterceptor;
    private static final String TAG = "NotificationEntryMgr";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    @VisibleForTesting
    protected final HashMap<String, NotificationEntry> mPendingNotifications = new HashMap<>();
    private final Map<NotificationEntry, NotificationLifetimeExtender> mRetainedNotifications = new ArrayMap();
    @VisibleForTesting
    final ArrayList<NotificationLifetimeExtender> mNotificationLifetimeExtenders = new ArrayList<>();
    private final List<NotificationEntryListener> mNotificationEntryListeners = new ArrayList();
    @VisibleForTesting
    protected NotificationData mNotificationData = new NotificationData();

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("NotificationEntryManager state:");
        pw.print("  mPendingNotifications=");
        if (this.mPendingNotifications.size() == 0) {
            pw.println("null");
        } else {
            for (NotificationEntry entry : this.mPendingNotifications.values()) {
                pw.println(entry.notification);
            }
        }
        pw.println("  Lifetime-extended notifications:");
        if (this.mRetainedNotifications.isEmpty()) {
            pw.println("    None");
            return;
        }
        for (Map.Entry<NotificationEntry, NotificationLifetimeExtender> entry2 : this.mRetainedNotifications.entrySet()) {
            pw.println("    " + entry2.getKey().notification + " retained by " + entry2.getValue().getClass().getName());
        }
    }

    @Inject
    public NotificationEntryManager(Context context) {
    }

    public void addNotificationEntryListener(NotificationEntryListener listener) {
        this.mNotificationEntryListeners.add(listener);
    }

    public void setNotificationRemoveInterceptor(NotificationRemoveInterceptor interceptor) {
        this.mRemoveInterceptor = interceptor;
    }

    private NotificationRemoteInputManager getRemoteInputManager() {
        if (this.mRemoteInputManager == null) {
            this.mRemoteInputManager = (NotificationRemoteInputManager) Dependency.get(NotificationRemoteInputManager.class);
        }
        return this.mRemoteInputManager;
    }

    public void setRowBinder(NotificationRowBinder notificationRowBinder) {
        this.mNotificationRowBinder = notificationRowBinder;
    }

    public void setUpWithPresenter(NotificationPresenter presenter, NotificationListContainer listContainer, HeadsUpManager headsUpManager) {
        this.mPresenter = presenter;
        this.mNotificationData.setHeadsUpManager(headsUpManager);
    }

    public void addNotificationLifetimeExtenders(List<NotificationLifetimeExtender> extenders) {
        for (NotificationLifetimeExtender extender : extenders) {
            addNotificationLifetimeExtender(extender);
        }
    }

    public void addNotificationLifetimeExtender(NotificationLifetimeExtender extender) {
        this.mNotificationLifetimeExtenders.add(extender);
        extender.setCallback(new NotificationLifetimeExtender.NotificationSafeToRemoveCallback() { // from class: com.android.systemui.statusbar.notification.-$$Lambda$NotificationEntryManager$B9Rprc7VWCrqKYHxmFbKGPst6oI
            @Override // com.android.systemui.statusbar.NotificationLifetimeExtender.NotificationSafeToRemoveCallback
            public final void onSafeToRemove(String str) {
                NotificationEntryManager.this.lambda$addNotificationLifetimeExtender$0$NotificationEntryManager(str);
            }
        });
    }

    public /* synthetic */ void lambda$addNotificationLifetimeExtender$0$NotificationEntryManager(String key) {
        removeNotification(key, null, 0);
    }

    public NotificationData getNotificationData() {
        return this.mNotificationData;
    }

    @Override // com.android.systemui.statusbar.notification.VisualStabilityManager.Callback
    public void onReorderingAllowed() {
        updateNotifications();
    }

    public void performRemoveNotification(StatusBarNotification n, int reason) {
        NotificationVisibility nv = obtainVisibility(n.getKey());
        removeNotificationInternal(n.getKey(), null, nv, false, true, reason);
    }

    private NotificationVisibility obtainVisibility(String key) {
        int rank = this.mNotificationData.getRank(key);
        int count = this.mNotificationData.getActiveNotifications().size();
        NotificationVisibility.NotificationLocation location = NotificationLogger.getNotificationLocation(getNotificationData().get(key));
        return NotificationVisibility.obtain(key, rank, count, true, location);
    }

    private void abortExistingInflation(String key) {
        if (this.mPendingNotifications.containsKey(key)) {
            NotificationEntry entry = this.mPendingNotifications.get(key);
            entry.abortTask();
            this.mPendingNotifications.remove(key);
        }
        NotificationEntry addedEntry = this.mNotificationData.get(key);
        if (addedEntry != null) {
            addedEntry.abortTask();
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.NotificationContentInflater.InflationCallback
    public void handleInflationException(StatusBarNotification n, Exception e) {
        removeNotificationInternal(n.getKey(), null, null, true, false, 4);
        for (NotificationEntryListener listener : this.mNotificationEntryListeners) {
            listener.onInflationError(n, e);
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.NotificationContentInflater.InflationCallback
    public void onAsyncInflationFinished(NotificationEntry entry, int inflatedFlags) {
        this.mPendingNotifications.remove(entry.key);
        if (!entry.isRowRemoved()) {
            boolean isNew = this.mNotificationData.get(entry.key) == null;
            if (isNew) {
                for (NotificationEntryListener listener : this.mNotificationEntryListeners) {
                    listener.onEntryInflated(entry, inflatedFlags);
                }
                this.mNotificationData.add(entry);
                for (NotificationEntryListener listener2 : this.mNotificationEntryListeners) {
                    listener2.onBeforeNotificationAdded(entry);
                }
                updateNotifications();
                for (NotificationEntryListener listener3 : this.mNotificationEntryListeners) {
                    listener3.onNotificationAdded(entry);
                }
                return;
            }
            for (NotificationEntryListener listener4 : this.mNotificationEntryListeners) {
                listener4.onEntryReinflated(entry);
            }
        }
    }

    @Override // com.android.systemui.statusbar.NotificationUpdateHandler
    public void removeNotification(String key, NotificationListenerService.RankingMap ranking, int reason) {
        removeNotificationInternal(key, ranking, obtainVisibility(key), false, false, reason);
    }

    private void removeNotificationInternal(String key, NotificationListenerService.RankingMap ranking, NotificationVisibility visibility, boolean forceRemove, boolean removedByUser, int reason) {
        NotificationEntry pendingEntry;
        NotificationRemoveInterceptor notificationRemoveInterceptor = this.mRemoveInterceptor;
        if (notificationRemoveInterceptor != null && notificationRemoveInterceptor.onNotificationRemoveRequested(key, reason)) {
            return;
        }
        NotificationEntry entry = this.mNotificationData.get(key);
        boolean lifetimeExtended = false;
        if (entry == null && (pendingEntry = this.mPendingNotifications.get(key)) != null) {
            Iterator<NotificationLifetimeExtender> it = this.mNotificationLifetimeExtenders.iterator();
            while (it.hasNext()) {
                NotificationLifetimeExtender extender = it.next();
                if (extender.shouldExtendLifetimeForPendingNotification(pendingEntry)) {
                    extendLifetime(pendingEntry, extender);
                    lifetimeExtended = true;
                }
            }
        }
        if (!lifetimeExtended) {
            abortExistingInflation(key);
        }
        if (entry != null) {
            boolean entryDismissed = entry.isRowDismissed();
            if (!forceRemove && !entryDismissed) {
                Iterator<NotificationLifetimeExtender> it2 = this.mNotificationLifetimeExtenders.iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        break;
                    }
                    NotificationLifetimeExtender extender2 = it2.next();
                    if (extender2.shouldExtendLifetime(entry)) {
                        extendLifetime(entry, extender2);
                        lifetimeExtended = true;
                        break;
                    }
                }
            }
            if (!lifetimeExtended) {
                cancelLifetimeExtension(entry);
                if (entry.rowExists()) {
                    entry.removeRow();
                }
                handleGroupSummaryRemoved(key);
                this.mNotificationData.remove(key, ranking);
                updateNotifications();
                ((LeakDetector) Dependency.get(LeakDetector.class)).trackGarbage(entry);
                boolean removedByUser2 = removedByUser | entryDismissed;
                for (NotificationEntryListener listener : this.mNotificationEntryListeners) {
                    listener.onEntryRemoved(entry, visibility, removedByUser2);
                }
            }
        }
    }

    private void handleGroupSummaryRemoved(String key) {
        List<NotificationEntry> childEntries;
        NotificationEntry entry = this.mNotificationData.get(key);
        if (entry != null && entry.rowExists() && entry.isSummaryWithChildren()) {
            if ((entry.notification.getOverrideGroupKey() != null && !entry.isRowDismissed()) || (childEntries = entry.getChildren()) == null) {
                return;
            }
            for (int i = 0; i < childEntries.size(); i++) {
                NotificationEntry childEntry = childEntries.get(i);
                boolean keepForReply = false;
                boolean isForeground = (entry.notification.getNotification().flags & 64) != 0;
                keepForReply = (getRemoteInputManager().shouldKeepForRemoteInputHistory(childEntry) || getRemoteInputManager().shouldKeepForSmartReplyHistory(childEntry)) ? true : true;
                if (!isForeground && !keepForReply) {
                    childEntry.setKeepInParent(true);
                    childEntry.removeRow();
                }
            }
        }
    }

    private void addNotificationInternal(final StatusBarNotification notification, NotificationListenerService.RankingMap rankingMap) throws InflationException {
        String key = notification.getKey();
        if (DEBUG) {
            Log.d(TAG, "addNotification key=" + key);
        }
        this.mNotificationData.updateRanking(rankingMap);
        NotificationListenerService.Ranking ranking = new NotificationListenerService.Ranking();
        rankingMap.getRanking(key, ranking);
        NotificationEntry entry = new NotificationEntry(notification, ranking);
        ((LeakDetector) Dependency.get(LeakDetector.class)).trackInstance(entry);
        requireBinder().inflateViews(entry, new Runnable() { // from class: com.android.systemui.statusbar.notification.-$$Lambda$NotificationEntryManager$lOGPG9l6kx5UZEzr26g7h2LQR6w
            @Override // java.lang.Runnable
            public final void run() {
                NotificationEntryManager.this.lambda$addNotificationInternal$1$NotificationEntryManager(notification);
            }
        });
        abortExistingInflation(key);
        this.mPendingNotifications.put(key, entry);
        for (NotificationEntryListener listener : this.mNotificationEntryListeners) {
            listener.onPendingEntryAdded(entry);
        }
    }

    public /* synthetic */ void lambda$addNotificationInternal$1$NotificationEntryManager(StatusBarNotification notification) {
        performRemoveNotification(notification, 2);
    }

    @Override // com.android.systemui.statusbar.NotificationUpdateHandler
    public void addNotification(StatusBarNotification notification, NotificationListenerService.RankingMap ranking) {
        try {
            addNotificationInternal(notification, ranking);
        } catch (InflationException e) {
            handleInflationException(notification, e);
        }
    }

    private void updateNotificationInternal(final StatusBarNotification notification, NotificationListenerService.RankingMap ranking) throws InflationException {
        if (DEBUG) {
            Log.d(TAG, "updateNotification(" + notification + NavigationBarInflaterView.KEY_CODE_END);
        }
        String key = notification.getKey();
        abortExistingInflation(key);
        NotificationEntry entry = this.mNotificationData.get(key);
        if (entry == null) {
            return;
        }
        cancelLifetimeExtension(entry);
        this.mNotificationData.update(entry, ranking, notification);
        for (NotificationEntryListener listener : this.mNotificationEntryListeners) {
            listener.onPreEntryUpdated(entry);
        }
        requireBinder().inflateViews(entry, new Runnable() { // from class: com.android.systemui.statusbar.notification.-$$Lambda$NotificationEntryManager$RJEcTAo4cuGvAgvl2zrMgzSF4kM
            @Override // java.lang.Runnable
            public final void run() {
                NotificationEntryManager.this.lambda$updateNotificationInternal$2$NotificationEntryManager(notification);
            }
        });
        updateNotifications();
        if (DEBUG) {
            boolean isForCurrentUser = ((NotificationData.KeyguardEnvironment) Dependency.get(NotificationData.KeyguardEnvironment.class)).isNotificationForCurrentProfiles(notification);
            StringBuilder sb = new StringBuilder();
            sb.append("notification is ");
            sb.append(isForCurrentUser ? "" : "not ");
            sb.append("for you");
            Log.d(TAG, sb.toString());
        }
        for (NotificationEntryListener listener2 : this.mNotificationEntryListeners) {
            listener2.onPostEntryUpdated(entry);
        }
    }

    public /* synthetic */ void lambda$updateNotificationInternal$2$NotificationEntryManager(StatusBarNotification notification) {
        performRemoveNotification(notification, 2);
    }

    @Override // com.android.systemui.statusbar.NotificationUpdateHandler
    public void updateNotification(StatusBarNotification notification, NotificationListenerService.RankingMap ranking) {
        try {
            updateNotificationInternal(notification, ranking);
        } catch (InflationException e) {
            handleInflationException(notification, e);
        }
    }

    public void updateNotifications() {
        this.mNotificationData.filterAndSort();
        NotificationPresenter notificationPresenter = this.mPresenter;
        if (notificationPresenter != null) {
            notificationPresenter.updateNotificationViews();
        }
    }

    @Override // com.android.systemui.statusbar.NotificationUpdateHandler
    public void updateNotificationRanking(NotificationListenerService.RankingMap rankingMap) {
        List<NotificationEntry> entries = new ArrayList<>();
        entries.addAll(this.mNotificationData.getActiveNotifications());
        entries.addAll(this.mPendingNotifications.values());
        ArrayMap<String, NotificationUiAdjustment> oldAdjustments = new ArrayMap<>();
        ArrayMap<String, Integer> oldImportances = new ArrayMap<>();
        for (NotificationEntry entry : entries) {
            NotificationUiAdjustment adjustment = NotificationUiAdjustment.extractFromNotificationEntry(entry);
            oldAdjustments.put(entry.key, adjustment);
            oldImportances.put(entry.key, Integer.valueOf(entry.importance));
        }
        this.mNotificationData.updateRanking(rankingMap);
        updateRankingOfPendingNotifications(rankingMap);
        for (NotificationEntry entry2 : entries) {
            requireBinder().onNotificationRankingUpdated(entry2, oldImportances.get(entry2.key), oldAdjustments.get(entry2.key), NotificationUiAdjustment.extractFromNotificationEntry(entry2));
        }
        updateNotifications();
        for (NotificationEntryListener listener : this.mNotificationEntryListeners) {
            listener.onNotificationRankingUpdated(rankingMap);
        }
    }

    private void updateRankingOfPendingNotifications(NotificationListenerService.RankingMap rankingMap) {
        if (rankingMap == null) {
            return;
        }
        NotificationListenerService.Ranking tmpRanking = new NotificationListenerService.Ranking();
        for (NotificationEntry pendingNotification : this.mPendingNotifications.values()) {
            rankingMap.getRanking(pendingNotification.key, tmpRanking);
            pendingNotification.populateFromRanking(tmpRanking);
        }
    }

    public Iterable<NotificationEntry> getPendingNotificationsIterator() {
        return this.mPendingNotifications.values();
    }

    private void extendLifetime(NotificationEntry entry, NotificationLifetimeExtender extender) {
        NotificationLifetimeExtender activeExtender = this.mRetainedNotifications.get(entry);
        if (activeExtender != null && activeExtender != extender) {
            activeExtender.setShouldManageLifetime(entry, false);
        }
        this.mRetainedNotifications.put(entry, extender);
        extender.setShouldManageLifetime(entry, true);
    }

    private void cancelLifetimeExtension(NotificationEntry entry) {
        NotificationLifetimeExtender activeExtender = this.mRetainedNotifications.remove(entry);
        if (activeExtender != null) {
            activeExtender.setShouldManageLifetime(entry, false);
        }
    }

    private NotificationRowBinder requireBinder() {
        NotificationRowBinder notificationRowBinder = this.mNotificationRowBinder;
        if (notificationRowBinder == null) {
            throw new RuntimeException("You must initialize NotificationEntryManager by callingsetRowBinder() before using.");
        }
        return notificationRowBinder;
    }
}
