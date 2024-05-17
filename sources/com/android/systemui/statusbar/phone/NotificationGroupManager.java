package com.android.systemui.statusbar.phone;

import android.service.notification.StatusBarNotification;
import android.util.ArraySet;
import android.util.Log;
import com.android.systemui.Dependency;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class NotificationGroupManager implements OnHeadsUpChangedListener, StatusBarStateController.StateListener {
    private static final String TAG = "NotificationGroupManager";
    private HeadsUpManager mHeadsUpManager;
    private boolean mIsUpdatingUnchangedGroup;
    private final HashMap<String, NotificationGroup> mGroupMap = new HashMap<>();
    private final ArraySet<OnGroupChangeListener> mListeners = new ArraySet<>();
    private int mBarState = -1;
    private HashMap<String, StatusBarNotification> mIsolatedEntries = new HashMap<>();
    private BubbleController mBubbleController = null;

    @Inject
    public NotificationGroupManager(StatusBarStateController statusBarStateController) {
        statusBarStateController.addCallback(this);
    }

    private BubbleController getBubbleController() {
        if (this.mBubbleController == null) {
            this.mBubbleController = (BubbleController) Dependency.get(BubbleController.class);
        }
        return this.mBubbleController;
    }

    public void addOnGroupChangeListener(OnGroupChangeListener listener) {
        this.mListeners.add(listener);
    }

    public boolean isGroupExpanded(StatusBarNotification sbn) {
        NotificationGroup group = this.mGroupMap.get(getGroupKey(sbn));
        if (group == null) {
            return false;
        }
        return group.expanded;
    }

    public void setGroupExpanded(StatusBarNotification sbn, boolean expanded) {
        NotificationGroup group = this.mGroupMap.get(getGroupKey(sbn));
        if (group == null) {
            return;
        }
        setGroupExpanded(group, expanded);
    }

    private void setGroupExpanded(NotificationGroup group, boolean expanded) {
        group.expanded = expanded;
        if (group.summary != null) {
            Iterator<OnGroupChangeListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                OnGroupChangeListener listener = it.next();
                listener.onGroupExpansionChanged(group.summary.getRow(), expanded);
            }
        }
    }

    public void onEntryRemoved(NotificationEntry removed) {
        onEntryRemovedInternal(removed, removed.notification);
        this.mIsolatedEntries.remove(removed.key);
    }

    private void onEntryRemovedInternal(NotificationEntry removed, StatusBarNotification sbn) {
        String groupKey = getGroupKey(sbn);
        NotificationGroup group = this.mGroupMap.get(groupKey);
        if (group == null) {
            return;
        }
        if (isGroupChild(sbn)) {
            group.children.remove(removed.key);
        } else {
            group.summary = null;
        }
        updateSuppression(group);
        if (group.children.isEmpty() && group.summary == null) {
            this.mGroupMap.remove(groupKey);
            Iterator<OnGroupChangeListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                OnGroupChangeListener listener = it.next();
                listener.onGroupRemoved(group, groupKey);
            }
        }
    }

    public void onEntryAdded(NotificationEntry added) {
        String str;
        if (added.isRowRemoved()) {
            added.setDebugThrowable(new Throwable());
        }
        StatusBarNotification sbn = added.notification;
        boolean isGroupChild = isGroupChild(sbn);
        String groupKey = getGroupKey(sbn);
        NotificationGroup group = this.mGroupMap.get(groupKey);
        if (group == null) {
            group = new NotificationGroup();
            this.mGroupMap.put(groupKey, group);
            Iterator<OnGroupChangeListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                OnGroupChangeListener listener = it.next();
                listener.onGroupCreated(group, groupKey);
            }
        }
        if (isGroupChild) {
            NotificationEntry existing = group.children.get(added.key);
            if (existing != null && existing != added) {
                Throwable existingThrowable = existing.getDebugThrowable();
                StringBuilder sb = new StringBuilder();
                sb.append("Inconsistent entries found with the same key ");
                sb.append(added.key);
                sb.append("existing removed: ");
                sb.append(existing.isRowRemoved());
                if (existingThrowable != null) {
                    str = Log.getStackTraceString(existingThrowable) + "\n";
                } else {
                    str = "";
                }
                sb.append(str);
                sb.append(" added removed");
                sb.append(added.isRowRemoved());
                Log.wtf(TAG, sb.toString(), new Throwable());
            }
            group.children.put(added.key, added);
            updateSuppression(group);
            return;
        }
        group.summary = added;
        group.expanded = added.areChildrenExpanded();
        updateSuppression(group);
        if (!group.children.isEmpty()) {
            ArrayList<NotificationEntry> childrenCopy = new ArrayList<>(group.children.values());
            Iterator<NotificationEntry> it2 = childrenCopy.iterator();
            while (it2.hasNext()) {
                NotificationEntry child = it2.next();
                onEntryBecomingChild(child);
            }
            Iterator<OnGroupChangeListener> it3 = this.mListeners.iterator();
            while (it3.hasNext()) {
                OnGroupChangeListener listener2 = it3.next();
                listener2.onGroupCreatedFromChildren(group);
            }
        }
    }

    private void onEntryBecomingChild(NotificationEntry entry) {
        if (shouldIsolate(entry)) {
            isolateNotification(entry);
        }
    }

    private void updateSuppression(NotificationGroup group) {
        if (group == null) {
            return;
        }
        int childCount = 0;
        boolean hasBubbles = false;
        for (String key : group.children.keySet()) {
            if (!getBubbleController().isBubbleNotificationSuppressedFromShade(key)) {
                childCount++;
            } else {
                hasBubbles = true;
            }
        }
        boolean prevSuppressed = group.suppressed;
        boolean z = true;
        if (group.summary == null || group.expanded || (childCount != 1 && (childCount != 0 || !group.summary.notification.getNotification().isGroupSummary() || (!hasIsolatedChildren(group) && !hasBubbles)))) {
            z = false;
        }
        group.suppressed = z;
        if (prevSuppressed != group.suppressed) {
            Iterator<OnGroupChangeListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                OnGroupChangeListener listener = it.next();
                if (!this.mIsUpdatingUnchangedGroup) {
                    listener.onGroupSuppressionChanged(group, group.suppressed);
                    listener.onGroupsChanged();
                }
            }
        }
    }

    private boolean hasIsolatedChildren(NotificationGroup group) {
        return getNumberOfIsolatedChildren(group.summary.notification.getGroupKey()) != 0;
    }

    private int getNumberOfIsolatedChildren(String groupKey) {
        int count = 0;
        for (StatusBarNotification sbn : this.mIsolatedEntries.values()) {
            if (sbn.getGroupKey().equals(groupKey) && isIsolated(sbn)) {
                count++;
            }
        }
        return count;
    }

    private NotificationEntry getIsolatedChild(String groupKey) {
        for (StatusBarNotification sbn : this.mIsolatedEntries.values()) {
            if (sbn.getGroupKey().equals(groupKey) && isIsolated(sbn)) {
                return this.mGroupMap.get(sbn.getKey()).summary;
            }
        }
        return null;
    }

    public void onEntryUpdated(NotificationEntry entry, StatusBarNotification oldNotification) {
        String oldKey = oldNotification.getGroupKey();
        String newKey = entry.notification.getGroupKey();
        boolean z = true;
        boolean groupKeysChanged = !oldKey.equals(newKey);
        boolean wasGroupChild = isGroupChild(oldNotification);
        boolean isGroupChild = isGroupChild(entry.notification);
        if (groupKeysChanged || wasGroupChild != isGroupChild) {
            z = false;
        }
        this.mIsUpdatingUnchangedGroup = z;
        if (this.mGroupMap.get(getGroupKey(oldNotification)) != null) {
            onEntryRemovedInternal(entry, oldNotification);
        }
        onEntryAdded(entry);
        this.mIsUpdatingUnchangedGroup = false;
        if (isIsolated(entry.notification)) {
            this.mIsolatedEntries.put(entry.key, entry.notification);
            if (groupKeysChanged) {
                updateSuppression(this.mGroupMap.get(oldKey));
                updateSuppression(this.mGroupMap.get(newKey));
            }
        } else if (!wasGroupChild && isGroupChild) {
            onEntryBecomingChild(entry);
        }
    }

    public boolean isSummaryOfSuppressedGroup(StatusBarNotification sbn) {
        return isGroupSuppressed(getGroupKey(sbn)) && sbn.getNotification().isGroupSummary();
    }

    private boolean isOnlyChild(StatusBarNotification sbn) {
        return !sbn.getNotification().isGroupSummary() && getTotalNumberOfChildren(sbn) == 1;
    }

    public boolean isOnlyChildInGroup(StatusBarNotification sbn) {
        NotificationEntry logicalGroupSummary;
        return (!isOnlyChild(sbn) || (logicalGroupSummary = getLogicalGroupSummary(sbn)) == null || logicalGroupSummary.notification.equals(sbn)) ? false : true;
    }

    private int getTotalNumberOfChildren(StatusBarNotification sbn) {
        int isolatedChildren = getNumberOfIsolatedChildren(sbn.getGroupKey());
        NotificationGroup group = this.mGroupMap.get(sbn.getGroupKey());
        int realChildren = group != null ? group.children.size() : 0;
        return isolatedChildren + realChildren;
    }

    private boolean isGroupSuppressed(String groupKey) {
        NotificationGroup group = this.mGroupMap.get(groupKey);
        return group != null && group.suppressed;
    }

    private void setStatusBarState(int newState) {
        this.mBarState = newState;
        if (this.mBarState == 1) {
            collapseAllGroups();
        }
    }

    public void collapseAllGroups() {
        ArrayList<NotificationGroup> groupCopy = new ArrayList<>(this.mGroupMap.values());
        int size = groupCopy.size();
        for (int i = 0; i < size; i++) {
            NotificationGroup group = groupCopy.get(i);
            if (group.expanded) {
                setGroupExpanded(group, false);
            }
            updateSuppression(group);
        }
    }

    public boolean isChildInGroupWithSummary(StatusBarNotification sbn) {
        NotificationGroup group;
        return (!isGroupChild(sbn) || (group = this.mGroupMap.get(getGroupKey(sbn))) == null || group.summary == null || group.suppressed || group.children.isEmpty()) ? false : true;
    }

    public boolean isSummaryOfGroup(StatusBarNotification sbn) {
        NotificationGroup group;
        return (!isGroupSummary(sbn) || (group = this.mGroupMap.get(getGroupKey(sbn))) == null || group.summary == null || group.children.isEmpty() || !Objects.equals(group.summary.notification, sbn)) ? false : true;
    }

    public NotificationEntry getGroupSummary(StatusBarNotification sbn) {
        return getGroupSummary(getGroupKey(sbn));
    }

    public NotificationEntry getLogicalGroupSummary(StatusBarNotification sbn) {
        return getGroupSummary(sbn.getGroupKey());
    }

    private NotificationEntry getGroupSummary(String groupKey) {
        NotificationGroup group = this.mGroupMap.get(groupKey);
        if (group == null || group.summary == null) {
            return null;
        }
        return group.summary;
    }

    public ArrayList<NotificationEntry> getLogicalChildren(StatusBarNotification summary) {
        NotificationGroup group = this.mGroupMap.get(summary.getGroupKey());
        if (group == null) {
            return null;
        }
        ArrayList<NotificationEntry> children = new ArrayList<>(group.children.values());
        NotificationEntry isolatedChild = getIsolatedChild(summary.getGroupKey());
        if (isolatedChild != null) {
            children.add(isolatedChild);
        }
        return children;
    }

    public void updateSuppression(NotificationEntry entry) {
        NotificationGroup group = this.mGroupMap.get(getGroupKey(entry.notification));
        if (group != null) {
            updateSuppression(group);
        }
    }

    public String getGroupKey(StatusBarNotification sbn) {
        if (isIsolated(sbn)) {
            return sbn.getKey();
        }
        return sbn.getGroupKey();
    }

    public boolean toggleGroupExpansion(StatusBarNotification sbn) {
        NotificationGroup group = this.mGroupMap.get(getGroupKey(sbn));
        if (group == null) {
            return false;
        }
        setGroupExpanded(group, !group.expanded);
        return group.expanded;
    }

    private boolean isIsolated(StatusBarNotification sbn) {
        return this.mIsolatedEntries.containsKey(sbn.getKey());
    }

    public boolean isGroupSummary(StatusBarNotification sbn) {
        if (isIsolated(sbn)) {
            return true;
        }
        return sbn.getNotification().isGroupSummary();
    }

    public boolean isGroupChild(StatusBarNotification sbn) {
        return (isIsolated(sbn) || !sbn.isGroup() || sbn.getNotification().isGroupSummary()) ? false : true;
    }

    @Override // com.android.systemui.statusbar.policy.OnHeadsUpChangedListener
    public void onHeadsUpStateChanged(NotificationEntry entry, boolean isHeadsUp) {
        onAlertStateChanged(entry, isHeadsUp);
    }

    private void onAlertStateChanged(NotificationEntry entry, boolean isAlerting) {
        if (isAlerting) {
            if (shouldIsolate(entry)) {
                isolateNotification(entry);
                return;
            }
            return;
        }
        stopIsolatingNotification(entry);
    }

    private boolean shouldIsolate(NotificationEntry entry) {
        StatusBarNotification sbn = entry.notification;
        NotificationGroup notificationGroup = this.mGroupMap.get(sbn.getGroupKey());
        if (sbn.isGroup() && !sbn.getNotification().isGroupSummary() && this.mHeadsUpManager.isAlerting(entry.key)) {
            return sbn.getNotification().fullScreenIntent != null || notificationGroup == null || !notificationGroup.expanded || isGroupNotFullyVisible(notificationGroup);
        }
        return false;
    }

    private void isolateNotification(NotificationEntry entry) {
        StatusBarNotification sbn = entry.notification;
        onEntryRemovedInternal(entry, entry.notification);
        this.mIsolatedEntries.put(sbn.getKey(), sbn);
        onEntryAdded(entry);
        updateSuppression(this.mGroupMap.get(entry.notification.getGroupKey()));
        Iterator<OnGroupChangeListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            OnGroupChangeListener listener = it.next();
            listener.onGroupsChanged();
        }
    }

    private void stopIsolatingNotification(NotificationEntry entry) {
        StatusBarNotification sbn = entry.notification;
        if (this.mIsolatedEntries.containsKey(sbn.getKey())) {
            onEntryRemovedInternal(entry, entry.notification);
            this.mIsolatedEntries.remove(sbn.getKey());
            onEntryAdded(entry);
            Iterator<OnGroupChangeListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                OnGroupChangeListener listener = it.next();
                listener.onGroupsChanged();
            }
        }
    }

    private boolean isGroupNotFullyVisible(NotificationGroup notificationGroup) {
        return notificationGroup.summary == null || notificationGroup.summary.isGroupNotFullyVisible();
    }

    public void setHeadsUpManager(HeadsUpManager headsUpManager) {
        this.mHeadsUpManager = headsUpManager;
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("GroupManager state:");
        pw.println("  number of groups: " + this.mGroupMap.size());
        for (Map.Entry<String, NotificationGroup> entry : this.mGroupMap.entrySet()) {
            pw.println("\n    key: " + entry.getKey());
            pw.println(entry.getValue());
        }
        pw.println("\n    isolated entries: " + this.mIsolatedEntries.size());
        for (Map.Entry<String, StatusBarNotification> entry2 : this.mIsolatedEntries.entrySet()) {
            pw.print("      ");
            pw.print(entry2.getKey());
            pw.print(", ");
            pw.println(entry2.getValue());
        }
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onStateChanged(int newState) {
        setStatusBarState(newState);
    }

    /* loaded from: classes21.dex */
    public static class NotificationGroup {
        public final HashMap<String, NotificationEntry> children = new HashMap<>();
        public boolean expanded;
        public NotificationEntry summary;
        public boolean suppressed;

        public String toString() {
            String str;
            String str2;
            StringBuilder sb = new StringBuilder();
            sb.append("    summary:\n      ");
            NotificationEntry notificationEntry = this.summary;
            sb.append(notificationEntry != null ? notificationEntry.notification : "null");
            NotificationEntry notificationEntry2 = this.summary;
            if (notificationEntry2 != null && notificationEntry2.getDebugThrowable() != null) {
                str = Log.getStackTraceString(this.summary.getDebugThrowable());
            } else {
                str = "";
            }
            sb.append(str);
            String result = sb.toString();
            String result2 = result + "\n    children size: " + this.children.size();
            for (NotificationEntry child : this.children.values()) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(result2);
                sb2.append("\n      ");
                sb2.append(child.notification);
                if (child.getDebugThrowable() != null) {
                    str2 = Log.getStackTraceString(child.getDebugThrowable());
                } else {
                    str2 = "";
                }
                sb2.append(str2);
                result2 = sb2.toString();
            }
            return result2 + "\n    summary suppressed: " + this.suppressed;
        }
    }

    /* loaded from: classes21.dex */
    public interface OnGroupChangeListener {
        default void onGroupCreated(NotificationGroup group, String groupKey) {
        }

        default void onGroupRemoved(NotificationGroup group, String groupKey) {
        }

        default void onGroupSuppressionChanged(NotificationGroup group, boolean suppressed) {
        }

        default void onGroupExpansionChanged(ExpandableNotificationRow changedRow, boolean expanded) {
        }

        default void onGroupCreatedFromChildren(NotificationGroup group) {
        }

        default void onGroupsChanged() {
        }
    }
}
