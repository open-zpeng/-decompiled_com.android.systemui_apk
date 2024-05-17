package com.android.systemui.statusbar.notification.collection;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.Person;
import android.service.notification.NotificationListenerService;
import android.service.notification.SnoozeCriterion;
import android.service.notification.StatusBarNotification;
import android.util.ArrayMap;
import androidx.mediarouter.media.SystemMediaRouteProvider;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.notification.NotificationFilter;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
/* loaded from: classes21.dex */
public class NotificationData {
    private KeyguardEnvironment mEnvironment;
    private HeadsUpManager mHeadsUpManager;
    private NotificationMediaManager mMediaManager;
    private NotificationListenerService.RankingMap mRankingMap;
    private final NotificationFilter mNotificationFilter = (NotificationFilter) Dependency.get(NotificationFilter.class);
    private final ArrayMap<String, NotificationEntry> mEntries = new ArrayMap<>();
    private final ArrayList<NotificationEntry> mSortedAndFiltered = new ArrayList<>();
    private final NotificationGroupManager mGroupManager = (NotificationGroupManager) Dependency.get(NotificationGroupManager.class);
    private final NotificationListenerService.Ranking mTmpRanking = new NotificationListenerService.Ranking();
    @VisibleForTesting
    protected final Comparator<NotificationEntry> mRankingComparator = new Comparator<NotificationEntry>() { // from class: com.android.systemui.statusbar.notification.collection.NotificationData.1
        private final NotificationListenerService.Ranking mRankingA = new NotificationListenerService.Ranking();
        private final NotificationListenerService.Ranking mRankingB = new NotificationListenerService.Ranking();

        @Override // java.util.Comparator
        public int compare(NotificationEntry a, NotificationEntry b) {
            StatusBarNotification na = a.notification;
            StatusBarNotification nb = b.notification;
            int aImportance = 3;
            int bImportance = 3;
            int aRank = 0;
            int bRank = 0;
            if (NotificationData.this.mRankingMap != null) {
                NotificationData.this.getRanking(a.key, this.mRankingA);
                NotificationData.this.getRanking(b.key, this.mRankingB);
                aImportance = this.mRankingA.getImportance();
                bImportance = this.mRankingB.getImportance();
                aRank = this.mRankingA.getRank();
                bRank = this.mRankingB.getRank();
            }
            String mediaNotification = NotificationData.this.getMediaManager().getMediaNotificationKey();
            boolean aMedia = a.key.equals(mediaNotification) && aImportance > 1;
            boolean bMedia = b.key.equals(mediaNotification) && bImportance > 1;
            boolean aSystemMax = aImportance >= 4 && NotificationData.isSystemNotification(na);
            boolean bSystemMax = bImportance >= 4 && NotificationData.isSystemNotification(nb);
            boolean aHeadsUp = a.getRow().isHeadsUp();
            boolean bHeadsUp = b.getRow().isHeadsUp();
            a.setIsTopBucket(aHeadsUp || aMedia || aSystemMax || a.isHighPriority());
            b.setIsTopBucket(bHeadsUp || bMedia || bSystemMax || b.isHighPriority());
            if (aHeadsUp != bHeadsUp) {
                return aHeadsUp ? -1 : 1;
            } else if (aHeadsUp) {
                return NotificationData.this.mHeadsUpManager.compare(a, b);
            } else {
                if (aMedia != bMedia) {
                    return aMedia ? -1 : 1;
                } else if (aSystemMax != bSystemMax) {
                    return aSystemMax ? -1 : 1;
                } else if (a.isHighPriority() != b.isHighPriority()) {
                    return Boolean.compare(a.isHighPriority(), b.isHighPriority()) * (-1);
                } else {
                    if (aRank != bRank) {
                        return aRank - bRank;
                    }
                    return Long.compare(nb.getNotification().when, na.getNotification().when);
                }
            }
        }
    };

    /* loaded from: classes21.dex */
    public interface KeyguardEnvironment {
        boolean isDeviceProvisioned();

        boolean isNotificationForCurrentProfiles(StatusBarNotification statusBarNotification);
    }

    public void setHeadsUpManager(HeadsUpManager headsUpManager) {
        this.mHeadsUpManager = headsUpManager;
    }

    private KeyguardEnvironment getEnvironment() {
        if (this.mEnvironment == null) {
            this.mEnvironment = (KeyguardEnvironment) Dependency.get(KeyguardEnvironment.class);
        }
        return this.mEnvironment;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public NotificationMediaManager getMediaManager() {
        if (this.mMediaManager == null) {
            this.mMediaManager = (NotificationMediaManager) Dependency.get(NotificationMediaManager.class);
        }
        return this.mMediaManager;
    }

    public ArrayList<NotificationEntry> getActiveNotifications() {
        return this.mSortedAndFiltered;
    }

    public ArrayList<NotificationEntry> getNotificationsForCurrentUser() {
        ArrayList<NotificationEntry> filteredForUser;
        synchronized (this.mEntries) {
            int len = this.mEntries.size();
            filteredForUser = new ArrayList<>(len);
            for (int i = 0; i < len; i++) {
                NotificationEntry entry = this.mEntries.valueAt(i);
                StatusBarNotification sbn = entry.notification;
                if (getEnvironment().isNotificationForCurrentProfiles(sbn)) {
                    filteredForUser.add(entry);
                }
            }
        }
        return filteredForUser;
    }

    public NotificationEntry get(String key) {
        return this.mEntries.get(key);
    }

    public void add(NotificationEntry entry) {
        synchronized (this.mEntries) {
            this.mEntries.put(entry.notification.getKey(), entry);
        }
        this.mGroupManager.onEntryAdded(entry);
        updateRankingAndSort(this.mRankingMap);
    }

    public NotificationEntry remove(String key, NotificationListenerService.RankingMap ranking) {
        NotificationEntry removed;
        synchronized (this.mEntries) {
            removed = this.mEntries.remove(key);
        }
        if (removed == null) {
            return null;
        }
        if (ranking == null) {
            ranking = this.mRankingMap;
        }
        this.mGroupManager.onEntryRemoved(removed);
        updateRankingAndSort(ranking);
        return removed;
    }

    public void update(NotificationEntry entry, NotificationListenerService.RankingMap ranking, StatusBarNotification notification) {
        updateRanking(ranking);
        StatusBarNotification oldNotification = entry.notification;
        entry.notification = notification;
        this.mGroupManager.onEntryUpdated(entry, oldNotification);
    }

    public void updateRanking(NotificationListenerService.RankingMap ranking) {
        updateRankingAndSort(ranking);
    }

    public void updateAppOp(int appOp, int uid, String pkg, String key, boolean showIcon) {
        synchronized (this.mEntries) {
            int len = this.mEntries.size();
            for (int i = 0; i < len; i++) {
                NotificationEntry entry = this.mEntries.valueAt(i);
                if (uid == entry.notification.getUid() && pkg.equals(entry.notification.getPackageName()) && key.equals(entry.key)) {
                    if (showIcon) {
                        entry.mActiveAppOps.add(Integer.valueOf(appOp));
                    } else {
                        entry.mActiveAppOps.remove(Integer.valueOf(appOp));
                    }
                }
            }
        }
    }

    public boolean isHighPriority(StatusBarNotification statusBarNotification) {
        if (this.mRankingMap != null) {
            getRanking(statusBarNotification.getKey(), this.mTmpRanking);
            if (this.mTmpRanking.getImportance() >= 3 || hasHighPriorityCharacteristics(this.mTmpRanking.getChannel(), statusBarNotification)) {
                return true;
            }
            if (this.mGroupManager.isSummaryOfGroup(statusBarNotification)) {
                ArrayList<NotificationEntry> logicalChildren = this.mGroupManager.getLogicalChildren(statusBarNotification);
                Iterator<NotificationEntry> it = logicalChildren.iterator();
                while (it.hasNext()) {
                    NotificationEntry child = it.next();
                    if (isHighPriority(child.notification)) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }
        return false;
    }

    private boolean hasHighPriorityCharacteristics(NotificationChannel channel, StatusBarNotification statusBarNotification) {
        if (isImportantOngoing(statusBarNotification.getNotification()) || statusBarNotification.getNotification().hasMediaSession() || hasPerson(statusBarNotification.getNotification()) || hasStyle(statusBarNotification.getNotification(), Notification.MessagingStyle.class)) {
            return channel == null || !channel.hasUserSetImportance();
        }
        return false;
    }

    private boolean isImportantOngoing(Notification notification) {
        return notification.isForegroundService() && this.mTmpRanking.getImportance() >= 2;
    }

    private boolean hasStyle(Notification notification, Class targetStyle) {
        Class<? extends Notification.Style> style = notification.getNotificationStyle();
        return targetStyle.equals(style);
    }

    private boolean hasPerson(Notification notification) {
        ArrayList<Person> people;
        if (notification.extras != null) {
            people = notification.extras.getParcelableArrayList("android.people.list");
        } else {
            people = new ArrayList<>();
        }
        return (people == null || people.isEmpty()) ? false : true;
    }

    public boolean isAmbient(String key) {
        if (this.mRankingMap != null) {
            getRanking(key, this.mTmpRanking);
            return this.mTmpRanking.isAmbient();
        }
        return false;
    }

    public int getVisibilityOverride(String key) {
        if (this.mRankingMap != null) {
            getRanking(key, this.mTmpRanking);
            return this.mTmpRanking.getVisibilityOverride();
        }
        return -1000;
    }

    public int getImportance(String key) {
        if (this.mRankingMap != null) {
            getRanking(key, this.mTmpRanking);
            return this.mTmpRanking.getImportance();
        }
        return -1000;
    }

    public String getOverrideGroupKey(String key) {
        if (this.mRankingMap != null) {
            getRanking(key, this.mTmpRanking);
            return this.mTmpRanking.getOverrideGroupKey();
        }
        return null;
    }

    public List<SnoozeCriterion> getSnoozeCriteria(String key) {
        if (this.mRankingMap != null) {
            getRanking(key, this.mTmpRanking);
            return this.mTmpRanking.getSnoozeCriteria();
        }
        return null;
    }

    public NotificationChannel getChannel(String key) {
        if (this.mRankingMap != null) {
            getRanking(key, this.mTmpRanking);
            return this.mTmpRanking.getChannel();
        }
        return null;
    }

    public int getRank(String key) {
        if (this.mRankingMap != null) {
            getRanking(key, this.mTmpRanking);
            return this.mTmpRanking.getRank();
        }
        return 0;
    }

    public boolean shouldHide(String key) {
        if (this.mRankingMap != null) {
            getRanking(key, this.mTmpRanking);
            return this.mTmpRanking.isSuspended();
        }
        return false;
    }

    private void updateRankingAndSort(NotificationListenerService.RankingMap ranking) {
        if (ranking != null) {
            this.mRankingMap = ranking;
            synchronized (this.mEntries) {
                int len = this.mEntries.size();
                for (int i = 0; i < len; i++) {
                    NotificationEntry entry = this.mEntries.valueAt(i);
                    if (getRanking(entry.key, this.mTmpRanking)) {
                        StatusBarNotification oldSbn = entry.notification.cloneLight();
                        String overrideGroupKey = getOverrideGroupKey(entry.key);
                        if (!Objects.equals(oldSbn.getOverrideGroupKey(), overrideGroupKey)) {
                            entry.notification.setOverrideGroupKey(overrideGroupKey);
                            this.mGroupManager.onEntryUpdated(entry, oldSbn);
                        }
                        entry.populateFromRanking(this.mTmpRanking);
                        entry.setIsHighPriority(isHighPriority(entry.notification));
                    }
                }
            }
        }
        filterAndSort();
    }

    @VisibleForTesting
    protected boolean getRanking(String key, NotificationListenerService.Ranking outRanking) {
        return this.mRankingMap.getRanking(key, outRanking);
    }

    public void filterAndSort() {
        this.mSortedAndFiltered.clear();
        synchronized (this.mEntries) {
            int len = this.mEntries.size();
            for (int i = 0; i < len; i++) {
                NotificationEntry entry = this.mEntries.valueAt(i);
                if (!this.mNotificationFilter.shouldFilterOut(entry)) {
                    this.mSortedAndFiltered.add(entry);
                }
            }
        }
        if (this.mSortedAndFiltered.size() == 1) {
            this.mRankingComparator.compare(this.mSortedAndFiltered.get(0), this.mSortedAndFiltered.get(0));
        } else {
            Collections.sort(this.mSortedAndFiltered, this.mRankingComparator);
        }
    }

    public void dump(PrintWriter pw, String indent) {
        int filteredLen = this.mSortedAndFiltered.size();
        pw.print(indent);
        pw.println("active notifications: " + filteredLen);
        int active = 0;
        while (active < filteredLen) {
            NotificationEntry e = this.mSortedAndFiltered.get(active);
            dumpEntry(pw, indent, active, e);
            active++;
        }
        synchronized (this.mEntries) {
            int totalLen = this.mEntries.size();
            pw.print(indent);
            pw.println("inactive notifications: " + (totalLen - active));
            int inactiveCount = 0;
            for (int i = 0; i < totalLen; i++) {
                NotificationEntry entry = this.mEntries.valueAt(i);
                if (!this.mSortedAndFiltered.contains(entry)) {
                    dumpEntry(pw, indent, inactiveCount, entry);
                    inactiveCount++;
                }
            }
        }
    }

    private void dumpEntry(PrintWriter pw, String indent, int i, NotificationEntry e) {
        getRanking(e.key, this.mTmpRanking);
        pw.print(indent);
        pw.println("  [" + i + "] key=" + e.key + " icon=" + e.icon);
        StatusBarNotification n = e.notification;
        pw.print(indent);
        pw.println("      pkg=" + n.getPackageName() + " id=" + n.getId() + " importance=" + this.mTmpRanking.getImportance());
        pw.print(indent);
        StringBuilder sb = new StringBuilder();
        sb.append("      notification=");
        sb.append(n.getNotification());
        pw.println(sb.toString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isSystemNotification(StatusBarNotification sbn) {
        String sbnPackage = sbn.getPackageName();
        return SystemMediaRouteProvider.PACKAGE_NAME.equals(sbnPackage) || "com.android.systemui".equals(sbnPackage);
    }
}
