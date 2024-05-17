package com.android.systemui.bubbles;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import android.util.Pair;
import androidx.annotation.Nullable;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class BubbleData {
    private static final Comparator<Bubble> BUBBLES_BY_SORT_KEY_DESCENDING = Comparator.comparing(new Function() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleData$vPZCImnk7rTPTX1c7nr0PX7FO2o
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            long sortKey;
            sortKey = BubbleData.sortKey((Bubble) obj);
            return Long.valueOf(sortKey);
        }
    }).reversed();
    private static final Comparator<Map.Entry<String, Long>> GROUPS_BY_MAX_SORT_KEY_DESCENDING = Comparator.comparing(new Function() { // from class: com.android.systemui.bubbles.-$$Lambda$JmVH-PWbzq5woEs3Hauzhf2I3Jc
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return (Long) ((Map.Entry) obj).getValue();
        }
    }).reversed();
    private static final int MAX_BUBBLES = 5;
    private static final String TAG = "Bubbles";
    private final Context mContext;
    private boolean mExpanded;
    @Nullable
    private Listener mListener;
    private Bubble mSelectedBubble;
    private NotificationListenerService.Ranking mTmpRanking;
    private TimeSource mTimeSource = new TimeSource() { // from class: com.android.systemui.bubbles.-$$Lambda$0E0fwzH9SS6-aB9lL5npMzupI4Q
        @Override // com.android.systemui.bubbles.BubbleData.TimeSource
        public final long currentTimeMillis() {
            return System.currentTimeMillis();
        }
    };
    private HashMap<String, String> mSuppressedGroupKeys = new HashMap<>();
    private final List<Bubble> mBubbles = new ArrayList();
    private Update mStateChange = new Update(this.mBubbles);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public interface Listener {
        void applyUpdate(Update update);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public interface TimeSource {
        long currentTimeMillis();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public static final class Update {
        @Nullable
        Bubble addedBubble;
        final List<Bubble> bubbles;
        boolean expanded;
        boolean expandedChanged;
        boolean orderChanged;
        final List<Pair<Bubble, Integer>> removedBubbles;
        @Nullable
        Bubble selectedBubble;
        boolean selectionChanged;
        @Nullable
        Bubble updatedBubble;

        private Update(List<Bubble> bubbleOrder) {
            this.removedBubbles = new ArrayList();
            this.bubbles = Collections.unmodifiableList(bubbleOrder);
        }

        boolean anythingChanged() {
            return this.expandedChanged || this.selectionChanged || this.addedBubble != null || this.updatedBubble != null || !this.removedBubbles.isEmpty() || this.orderChanged;
        }

        void bubbleRemoved(Bubble bubbleToRemove, int reason) {
            this.removedBubbles.add(new Pair<>(bubbleToRemove, Integer.valueOf(reason)));
        }
    }

    @Inject
    public BubbleData(Context context) {
        this.mContext = context;
    }

    public boolean hasBubbles() {
        return !this.mBubbles.isEmpty();
    }

    public boolean isExpanded() {
        return this.mExpanded;
    }

    public boolean hasBubbleWithKey(String key) {
        return getBubbleWithKey(key) != null;
    }

    @Nullable
    public Bubble getSelectedBubble() {
        return this.mSelectedBubble;
    }

    public void setExpanded(boolean expanded) {
        setExpandedInternal(expanded);
        dispatchPendingChanges();
    }

    public void setSelectedBubble(Bubble bubble) {
        setSelectedBubbleInternal(bubble);
        dispatchPendingChanges();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notificationEntryUpdated(NotificationEntry entry, boolean suppressFlyout) {
        Bubble bubble = getBubbleWithKey(entry.key);
        boolean suppressFlyout2 = !entry.isVisuallyInterruptive || suppressFlyout;
        if (bubble == null) {
            bubble = new Bubble(this.mContext, entry);
            bubble.setSuppressFlyout(suppressFlyout2);
            doAdd(bubble);
            trim();
        } else {
            bubble.updateEntry(entry);
            bubble.setSuppressFlyout(suppressFlyout2);
            doUpdate(bubble);
        }
        if (bubble.shouldAutoExpand()) {
            setSelectedBubbleInternal(bubble);
            if (!this.mExpanded) {
                setExpandedInternal(true);
            }
        } else if (this.mSelectedBubble == null) {
            setSelectedBubbleInternal(bubble);
        }
        boolean isBubbleExpandedAndSelected = this.mExpanded && this.mSelectedBubble == bubble;
        bubble.setShowInShadeWhenBubble(!isBubbleExpandedAndSelected);
        bubble.setShowBubbleDot(isBubbleExpandedAndSelected ? false : true);
        dispatchPendingChanges();
    }

    public void notificationEntryRemoved(NotificationEntry entry, int reason) {
        doRemove(entry.key, reason);
        dispatchPendingChanges();
    }

    public void notificationRankingUpdated(NotificationListenerService.RankingMap rankingMap) {
        if (this.mTmpRanking == null) {
            this.mTmpRanking = new NotificationListenerService.Ranking();
        }
        String[] orderedKeys = rankingMap.getOrderedKeys();
        for (String key : orderedKeys) {
            if (hasBubbleWithKey(key)) {
                rankingMap.getRanking(key, this.mTmpRanking);
                if (!this.mTmpRanking.canBubble()) {
                    doRemove(key, 4);
                }
            }
        }
        dispatchPendingChanges();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addSummaryToSuppress(String groupKey, String notifKey) {
        this.mSuppressedGroupKeys.put(groupKey, notifKey);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getSummaryKey(String groupKey) {
        return this.mSuppressedGroupKeys.get(groupKey);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeSuppressedSummary(String groupKey) {
        this.mSuppressedGroupKeys.remove(groupKey);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isSummarySuppressed(String groupKey) {
        return this.mSuppressedGroupKeys.containsKey(groupKey);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayList<Bubble> getBubblesInGroup(@Nullable String groupKey) {
        ArrayList<Bubble> bubbleChildren = new ArrayList<>();
        if (groupKey == null) {
            return bubbleChildren;
        }
        for (Bubble b : this.mBubbles) {
            if (groupKey.equals(b.getEntry().notification.getGroupKey())) {
                bubbleChildren.add(b);
            }
        }
        return bubbleChildren;
    }

    private void doAdd(Bubble bubble) {
        int minInsertPoint = 0;
        boolean newGroup = !hasBubbleWithGroupId(bubble.getGroupId());
        if (isExpanded()) {
            minInsertPoint = newGroup ? 0 : findFirstIndexForGroup(bubble.getGroupId());
        }
        if (insertBubble(minInsertPoint, bubble) < this.mBubbles.size() - 1) {
            this.mStateChange.orderChanged = true;
        }
        this.mStateChange.addedBubble = bubble;
        if (!isExpanded()) {
            this.mStateChange.orderChanged |= packGroup(findFirstIndexForGroup(bubble.getGroupId()));
            setSelectedBubbleInternal(this.mBubbles.get(0));
        }
    }

    private void trim() {
        if (this.mBubbles.size() > 5) {
            this.mBubbles.stream().sorted(Comparator.comparingLong(new ToLongFunction() { // from class: com.android.systemui.bubbles.-$$Lambda$x9O8XLDgnXklCbpbq_xgakOvcgY
                @Override // java.util.function.ToLongFunction
                public final long applyAsLong(Object obj) {
                    return ((Bubble) obj).getLastActivity();
                }
            })).filter(new Predicate() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleData$cMHgi74d7w0GcIwOfMT2Vp3u6PQ
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return BubbleData.this.lambda$trim$0$BubbleData((Bubble) obj);
                }
            }).findFirst().ifPresent(new Consumer() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleData$29N_uZXST8y3Cv7BRkVQkHhseh0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    BubbleData.this.lambda$trim$1$BubbleData((Bubble) obj);
                }
            });
        }
    }

    public /* synthetic */ boolean lambda$trim$0$BubbleData(Bubble b) {
        return !b.equals(this.mSelectedBubble);
    }

    public /* synthetic */ void lambda$trim$1$BubbleData(Bubble b) {
        doRemove(b.getKey(), 2);
    }

    private void doUpdate(Bubble bubble) {
        this.mStateChange.updatedBubble = bubble;
        if (!isExpanded()) {
            int prevPos = this.mBubbles.indexOf(bubble);
            this.mBubbles.remove(bubble);
            int newPos = insertBubble(0, bubble);
            if (prevPos != newPos) {
                packGroup(newPos);
                this.mStateChange.orderChanged = true;
            }
            setSelectedBubbleInternal(this.mBubbles.get(0));
        }
    }

    private void doRemove(String key, int reason) {
        int indexToRemove = indexForKey(key);
        if (indexToRemove == -1) {
            return;
        }
        Bubble bubbleToRemove = this.mBubbles.get(indexToRemove);
        if (this.mBubbles.size() == 1) {
            setExpandedInternal(false);
            setSelectedBubbleInternal(null);
        }
        if (indexToRemove < this.mBubbles.size() - 1) {
            this.mStateChange.orderChanged = true;
        }
        this.mBubbles.remove(indexToRemove);
        this.mStateChange.bubbleRemoved(bubbleToRemove, reason);
        if (!isExpanded()) {
            this.mStateChange.orderChanged |= repackAll();
        }
        if (Objects.equals(this.mSelectedBubble, bubbleToRemove)) {
            int newIndex = Math.min(indexToRemove, this.mBubbles.size() - 1);
            Bubble newSelected = this.mBubbles.get(newIndex);
            setSelectedBubbleInternal(newSelected);
        }
        maybeSendDeleteIntent(reason, bubbleToRemove.getEntry());
    }

    public void dismissAll(int reason) {
        if (this.mBubbles.isEmpty()) {
            return;
        }
        setExpandedInternal(false);
        setSelectedBubbleInternal(null);
        while (!this.mBubbles.isEmpty()) {
            Bubble bubble = this.mBubbles.remove(0);
            maybeSendDeleteIntent(reason, bubble.getEntry());
            this.mStateChange.bubbleRemoved(bubble, reason);
        }
        dispatchPendingChanges();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyDisplayEmpty(int displayId) {
        for (Bubble b : this.mBubbles) {
            if (b.getDisplayId() == displayId) {
                if (b.getExpandedView() != null) {
                    b.getExpandedView().notifyDisplayEmpty();
                    return;
                }
                return;
            }
        }
    }

    private void dispatchPendingChanges() {
        if (this.mListener != null && this.mStateChange.anythingChanged()) {
            this.mListener.applyUpdate(this.mStateChange);
        }
        this.mStateChange = new Update(this.mBubbles);
    }

    private void setSelectedBubbleInternal(@Nullable Bubble bubble) {
        if (Objects.equals(bubble, this.mSelectedBubble)) {
            return;
        }
        if (bubble != null && !this.mBubbles.contains(bubble)) {
            Log.e(TAG, "Cannot select bubble which doesn't exist! (" + bubble + ") bubbles=" + this.mBubbles);
            return;
        }
        if (this.mExpanded && bubble != null) {
            bubble.markAsAccessedAt(this.mTimeSource.currentTimeMillis());
        }
        this.mSelectedBubble = bubble;
        Update update = this.mStateChange;
        update.selectedBubble = bubble;
        update.selectionChanged = true;
    }

    private void setExpandedInternal(boolean shouldExpand) {
        if (this.mExpanded == shouldExpand) {
            return;
        }
        if (shouldExpand) {
            if (this.mBubbles.isEmpty()) {
                Log.e(TAG, "Attempt to expand stack when empty!");
                return;
            }
            Bubble bubble = this.mSelectedBubble;
            if (bubble == null) {
                Log.e(TAG, "Attempt to expand stack without selected bubble!");
                return;
            }
            bubble.markAsAccessedAt(this.mTimeSource.currentTimeMillis());
            this.mStateChange.orderChanged |= repackAll();
        } else if (!this.mBubbles.isEmpty()) {
            this.mStateChange.orderChanged |= repackAll();
            if (this.mBubbles.indexOf(this.mSelectedBubble) > 0) {
                if (!this.mSelectedBubble.isOngoing() && this.mBubbles.get(0).isOngoing()) {
                    setSelectedBubbleInternal(this.mBubbles.get(0));
                } else {
                    this.mBubbles.remove(this.mSelectedBubble);
                    this.mBubbles.add(0, this.mSelectedBubble);
                    Update update = this.mStateChange;
                    update.orderChanged = packGroup(0) | update.orderChanged;
                }
            }
        }
        this.mExpanded = shouldExpand;
        Update update2 = this.mStateChange;
        update2.expanded = shouldExpand;
        update2.expandedChanged = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static long sortKey(Bubble bubble) {
        long key = bubble.getLastUpdateTime();
        if (bubble.isOngoing()) {
            return key | 4611686018427387904L;
        }
        return key;
    }

    private int insertBubble(int minPosition, Bubble newBubble) {
        long newBubbleSortKey = sortKey(newBubble);
        String previousGroupId = null;
        for (int pos = minPosition; pos < this.mBubbles.size(); pos++) {
            Bubble bubbleAtPos = this.mBubbles.get(pos);
            String groupIdAtPos = bubbleAtPos.getGroupId();
            boolean atStartOfGroup = !groupIdAtPos.equals(previousGroupId);
            if (atStartOfGroup && newBubbleSortKey > sortKey(bubbleAtPos)) {
                this.mBubbles.add(pos, newBubble);
                return pos;
            }
            previousGroupId = groupIdAtPos;
        }
        this.mBubbles.add(newBubble);
        return this.mBubbles.size() - 1;
    }

    private boolean hasBubbleWithGroupId(final String groupId) {
        return this.mBubbles.stream().anyMatch(new Predicate() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleData$H_9shD4W4k6iZvs8GpmXTAxbTbM
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean equals;
                equals = ((Bubble) obj).getGroupId().equals(groupId);
                return equals;
            }
        });
    }

    private int findFirstIndexForGroup(String appId) {
        for (int i = 0; i < this.mBubbles.size(); i++) {
            Bubble bubbleAtPos = this.mBubbles.get(i);
            if (bubbleAtPos.getGroupId().equals(appId)) {
                return i;
            }
        }
        return 0;
    }

    private boolean packGroup(int position) {
        Bubble groupStart = this.mBubbles.get(position);
        String groupAppId = groupStart.getGroupId();
        List<Bubble> moving = new ArrayList<>();
        for (int i = this.mBubbles.size() - 1; i > position; i--) {
            if (this.mBubbles.get(i).getGroupId().equals(groupAppId)) {
                moving.add(0, this.mBubbles.get(i));
            }
        }
        if (moving.isEmpty()) {
            return false;
        }
        this.mBubbles.removeAll(moving);
        this.mBubbles.addAll(position + 1, moving);
        return true;
    }

    private boolean repackAll() {
        if (this.mBubbles.isEmpty()) {
            return false;
        }
        Map<String, Long> groupLastActivity = new HashMap<>();
        for (Bubble bubble : this.mBubbles) {
            long maxSortKeyForGroup = groupLastActivity.getOrDefault(bubble.getGroupId(), 0L).longValue();
            long sortKeyForBubble = sortKey(bubble);
            if (sortKeyForBubble > maxSortKeyForGroup) {
                groupLastActivity.put(bubble.getGroupId(), Long.valueOf(sortKeyForBubble));
            }
        }
        List<String> groupsByMostRecentActivity = (List) groupLastActivity.entrySet().stream().sorted(GROUPS_BY_MAX_SORT_KEY_DESCENDING).map(new Function() { // from class: com.android.systemui.bubbles.-$$Lambda$CSz_ibwXhtkKNl72Q8tR5oBgkWk
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return (String) ((Map.Entry) obj).getKey();
            }
        }).collect(Collectors.toList());
        final List<Bubble> repacked = new ArrayList<>(this.mBubbles.size());
        for (final String appId : groupsByMostRecentActivity) {
            Stream<Bubble> sorted = this.mBubbles.stream().filter(new Predicate() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleData$dNlU6_h6UYMtjKJV6CpiMlj80Mk
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean equals;
                    equals = ((Bubble) obj).getGroupId().equals(appId);
                    return equals;
                }
            }).sorted(BUBBLES_BY_SORT_KEY_DESCENDING);
            Objects.requireNonNull(repacked);
            sorted.forEachOrdered(new Consumer() { // from class: com.android.systemui.bubbles.-$$Lambda$0tU2wih_2wwdAnw6hE7FT9YuCis
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    repacked.add((Bubble) obj);
                }
            });
        }
        if (repacked.equals(this.mBubbles)) {
            return false;
        }
        this.mBubbles.clear();
        this.mBubbles.addAll(repacked);
        return true;
    }

    private void maybeSendDeleteIntent(int reason, NotificationEntry entry) {
        PendingIntent deleteIntent;
        if (reason == 1) {
            Notification.BubbleMetadata bubbleMetadata = entry.getBubbleMetadata();
            if (bubbleMetadata != null) {
                deleteIntent = bubbleMetadata.getDeleteIntent();
            } else {
                deleteIntent = null;
            }
            if (deleteIntent != null) {
                try {
                    deleteIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    Log.w(TAG, "Failed to send delete intent for bubble with key: " + entry.key);
                }
            }
        }
    }

    private int indexForKey(String key) {
        for (int i = 0; i < this.mBubbles.size(); i++) {
            Bubble bubble = this.mBubbles.get(i);
            if (bubble.getKey().equals(key)) {
                return i;
            }
        }
        return -1;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public List<Bubble> getBubbles() {
        return Collections.unmodifiableList(this.mBubbles);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public Bubble getBubbleWithKey(String key) {
        for (int i = 0; i < this.mBubbles.size(); i++) {
            Bubble bubble = this.mBubbles.get(i);
            if (bubble.getKey().equals(key)) {
                return bubble;
            }
        }
        return null;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    void setTimeSource(TimeSource timeSource) {
        this.mTimeSource = timeSource;
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        String str;
        pw.print("selected: ");
        Bubble bubble = this.mSelectedBubble;
        if (bubble != null) {
            str = bubble.getKey();
        } else {
            str = "null";
        }
        pw.println(str);
        pw.print("expanded: ");
        pw.println(this.mExpanded);
        pw.print("count:    ");
        pw.println(this.mBubbles.size());
        for (Bubble bubble2 : this.mBubbles) {
            bubble2.dump(fd, pw, args);
        }
        pw.print("summaryKeys: ");
        pw.println(this.mSuppressedGroupKeys.size());
        for (String key : this.mSuppressedGroupKeys.keySet()) {
            pw.println("   suppressing: " + key);
        }
    }
}
