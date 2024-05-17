package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.Log;
import android.util.Pools;
import android.view.DisplayCutout;
import android.view.View;
import android.view.ViewTreeObserver;
import androidx.collection.ArraySet;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dumpable;
import com.android.systemui.R;
import com.android.systemui.ScreenDecorations;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.AlertingNotificationManager;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class HeadsUpManagerPhone extends HeadsUpManager implements Dumpable, VisualStabilityManager.Callback, OnHeadsUpChangedListener, ConfigurationController.ConfigurationListener, StatusBarStateController.StateListener {
    private static final String TAG = "HeadsUpManagerPhone";
    private AnimationStateHandler mAnimationStateHandler;
    private final int mAutoHeadsUpNotificationDecay;
    private final KeyguardBypassController mBypassController;
    private int mDisplayCutoutTouchableRegionSize;
    private HashSet<NotificationEntry> mEntriesToRemoveAfterExpand;
    private ArraySet<NotificationEntry> mEntriesToRemoveWhenReorderingAllowed;
    private final Pools.Pool<HeadsUpEntryPhone> mEntryPool;
    @VisibleForTesting
    final int mExtensionTime;
    private NotificationGroupManager mGroupManager;
    private boolean mHeadsUpGoingAway;
    private int mHeadsUpInset;
    private boolean mIsExpanded;
    private HashSet<String> mKeysToRemoveWhenLeavingKeyguard;
    private boolean mReleaseOnExpandFinish;
    private int mStatusBarHeight;
    private int mStatusBarState;
    private final StatusBarStateController mStatusBarStateController;
    private StatusBarTouchableRegionManager mStatusBarTouchableRegionManager;
    private View mStatusBarWindowView;
    private HashSet<String> mSwipedOutKeys;
    private int[] mTmpTwoArray;
    private Region mTouchableRegion;
    private boolean mTrackingHeadsUp;
    private VisualStabilityManager mVisualStabilityManager;

    /* loaded from: classes21.dex */
    public interface AnimationStateHandler {
        void setHeadsUpGoingAwayAnimationsAllowed(boolean z);
    }

    @Inject
    public HeadsUpManagerPhone(Context context, StatusBarStateController statusBarStateController, KeyguardBypassController bypassController) {
        super(context);
        this.mSwipedOutKeys = new HashSet<>();
        this.mEntriesToRemoveAfterExpand = new HashSet<>();
        this.mKeysToRemoveWhenLeavingKeyguard = new HashSet<>();
        this.mEntriesToRemoveWhenReorderingAllowed = new ArraySet<>();
        this.mTmpTwoArray = new int[2];
        this.mTouchableRegion = new Region();
        this.mEntryPool = new Pools.Pool<HeadsUpEntryPhone>() { // from class: com.android.systemui.statusbar.phone.HeadsUpManagerPhone.1
            private Stack<HeadsUpEntryPhone> mPoolObjects = new Stack<>();

            /* renamed from: acquire */
            public HeadsUpEntryPhone m26acquire() {
                if (!this.mPoolObjects.isEmpty()) {
                    return this.mPoolObjects.pop();
                }
                return new HeadsUpEntryPhone();
            }

            public boolean release(HeadsUpEntryPhone instance) {
                this.mPoolObjects.push(instance);
                return true;
            }
        };
        Resources resources = this.mContext.getResources();
        this.mExtensionTime = resources.getInteger(R.integer.ambient_notification_extension_time);
        this.mAutoHeadsUpNotificationDecay = resources.getInteger(R.integer.auto_heads_up_notification_decay);
        this.mStatusBarStateController = statusBarStateController;
        this.mStatusBarStateController.addCallback(this);
        this.mBypassController = bypassController;
        initResources();
    }

    public void setUp(View statusBarWindowView, NotificationGroupManager groupManager, StatusBar bar, VisualStabilityManager visualStabilityManager) {
        this.mStatusBarWindowView = statusBarWindowView;
        this.mStatusBarTouchableRegionManager = new StatusBarTouchableRegionManager(this.mContext, this, bar, statusBarWindowView);
        this.mGroupManager = groupManager;
        this.mVisualStabilityManager = visualStabilityManager;
        addListener(new OnHeadsUpChangedListener() { // from class: com.android.systemui.statusbar.phone.HeadsUpManagerPhone.2
            @Override // com.android.systemui.statusbar.policy.OnHeadsUpChangedListener
            public void onHeadsUpPinnedModeChanged(boolean hasPinnedNotification) {
                if (Log.isLoggable(HeadsUpManagerPhone.TAG, 5)) {
                    Log.w(HeadsUpManagerPhone.TAG, "onHeadsUpPinnedModeChanged");
                }
                HeadsUpManagerPhone.this.mStatusBarTouchableRegionManager.updateTouchableRegion();
            }
        });
    }

    public void setAnimationStateHandler(AnimationStateHandler handler) {
        this.mAnimationStateHandler = handler;
    }

    private void initResources() {
        Resources resources = this.mContext.getResources();
        this.mStatusBarHeight = resources.getDimensionPixelSize(17105438);
        this.mHeadsUpInset = this.mStatusBarHeight + resources.getDimensionPixelSize(R.dimen.heads_up_status_bar_padding);
        this.mDisplayCutoutTouchableRegionSize = resources.getDimensionPixelSize(17105144);
    }

    @Override // com.android.systemui.statusbar.policy.HeadsUpManager, com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onDensityOrFontScaleChanged() {
        super.onDensityOrFontScaleChanged();
        initResources();
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onOverlayChanged() {
        initResources();
    }

    public boolean shouldSwallowClick(String key) {
        HeadsUpManager.HeadsUpEntry entry = getHeadsUpEntry(key);
        return entry != null && this.mClock.currentTimeMillis() < entry.mPostTime;
    }

    public void onExpandingFinished() {
        if (this.mReleaseOnExpandFinish) {
            releaseAllImmediately();
            this.mReleaseOnExpandFinish = false;
        } else {
            Iterator<NotificationEntry> it = this.mEntriesToRemoveAfterExpand.iterator();
            while (it.hasNext()) {
                NotificationEntry entry = it.next();
                if (isAlerting(entry.key)) {
                    removeAlertEntry(entry.key);
                }
            }
        }
        this.mEntriesToRemoveAfterExpand.clear();
    }

    public void setTrackingHeadsUp(boolean trackingHeadsUp) {
        this.mTrackingHeadsUp = trackingHeadsUp;
    }

    public void setIsPanelExpanded(boolean isExpanded) {
        if (isExpanded != this.mIsExpanded) {
            this.mIsExpanded = isExpanded;
            if (isExpanded) {
                this.mHeadsUpGoingAway = false;
            }
            this.mStatusBarTouchableRegionManager.setIsStatusBarExpanded(isExpanded);
            this.mStatusBarTouchableRegionManager.updateTouchableRegion();
        }
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onStateChanged(int newState) {
        boolean wasKeyguard = this.mStatusBarState == 1;
        boolean isKeyguard = newState == 1;
        this.mStatusBarState = newState;
        if (wasKeyguard && !isKeyguard && this.mKeysToRemoveWhenLeavingKeyguard.size() != 0) {
            String[] keys = (String[]) this.mKeysToRemoveWhenLeavingKeyguard.toArray(new String[0]);
            for (String key : keys) {
                removeAlertEntry(key);
            }
            this.mKeysToRemoveWhenLeavingKeyguard.clear();
        }
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onDozingChanged(boolean isDozing) {
        if (!isDozing) {
            for (AlertingNotificationManager.AlertEntry entry : this.mAlertEntries.values()) {
                entry.updateEntry(true);
            }
        }
    }

    @Override // com.android.systemui.statusbar.policy.HeadsUpManager
    public boolean isEntryAutoHeadsUpped(String key) {
        HeadsUpEntryPhone headsUpEntryPhone = getHeadsUpEntryPhone(key);
        if (headsUpEntryPhone == null) {
            return false;
        }
        return headsUpEntryPhone.isAutoHeadsUp();
    }

    public void setHeadsUpGoingAway(boolean headsUpGoingAway) {
        if (headsUpGoingAway != this.mHeadsUpGoingAway) {
            this.mHeadsUpGoingAway = headsUpGoingAway;
            if (!headsUpGoingAway) {
                this.mStatusBarTouchableRegionManager.updateTouchableRegionAfterLayout();
            } else {
                this.mStatusBarTouchableRegionManager.updateTouchableRegion();
            }
        }
    }

    public boolean isHeadsUpGoingAway() {
        return this.mHeadsUpGoingAway;
    }

    public void setRemoteInputActive(NotificationEntry entry, boolean remoteInputActive) {
        HeadsUpEntryPhone headsUpEntry = getHeadsUpEntryPhone(entry.key);
        if (headsUpEntry != null && headsUpEntry.remoteInputActive != remoteInputActive) {
            headsUpEntry.remoteInputActive = remoteInputActive;
            if (remoteInputActive) {
                headsUpEntry.removeAutoRemovalCallbacks();
            } else {
                headsUpEntry.updateEntry(false);
            }
        }
    }

    public void setMenuShown(NotificationEntry entry, boolean menuShown) {
        HeadsUpManager.HeadsUpEntry headsUpEntry = getHeadsUpEntry(entry.key);
        if ((headsUpEntry instanceof HeadsUpEntryPhone) && entry.isRowPinned()) {
            ((HeadsUpEntryPhone) headsUpEntry).setMenuShownPinned(menuShown);
        }
    }

    public void extendHeadsUp() {
        HeadsUpEntryPhone topEntry = getTopHeadsUpEntryPhone();
        if (topEntry == null) {
            return;
        }
        topEntry.extendPulse();
    }

    @Override // com.android.systemui.statusbar.policy.HeadsUpManager
    public boolean isTrackingHeadsUp() {
        return this.mTrackingHeadsUp;
    }

    @Override // com.android.systemui.statusbar.policy.HeadsUpManager
    public void snooze() {
        super.snooze();
        this.mReleaseOnExpandFinish = true;
    }

    public void addSwipedOutNotification(String key) {
        this.mSwipedOutKeys.add(key);
    }

    @Override // com.android.systemui.statusbar.policy.HeadsUpManager, com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("HeadsUpManagerPhone state:");
        dumpInternal(fd, pw, args);
    }

    public void updateTouchableRegion(ViewTreeObserver.InternalInsetsInfo info) {
        info.setTouchableInsets(3);
        info.touchableRegion.set(calculateTouchableRegion());
    }

    public Region calculateTouchableRegion() {
        NotificationEntry groupSummary;
        NotificationEntry topEntry = getTopEntry();
        if (!hasPinnedHeadsUp() || topEntry == null) {
            this.mTouchableRegion.set(0, 0, this.mStatusBarWindowView.getWidth(), this.mStatusBarHeight);
            updateRegionForNotch(this.mTouchableRegion);
        } else {
            if (topEntry.isChildInGroup() && (groupSummary = this.mGroupManager.getGroupSummary(topEntry.notification)) != null) {
                topEntry = groupSummary;
            }
            ExpandableNotificationRow topRow = topEntry.getRow();
            topRow.getLocationOnScreen(this.mTmpTwoArray);
            int[] iArr = this.mTmpTwoArray;
            int minX = iArr[0];
            int maxX = iArr[0] + topRow.getWidth();
            int height = topRow.getIntrinsicHeight();
            this.mTouchableRegion.set(minX, 0, maxX, this.mHeadsUpInset + height);
        }
        return this.mTouchableRegion;
    }

    private void updateRegionForNotch(Region region) {
        DisplayCutout cutout = this.mStatusBarWindowView.getRootWindowInsets().getDisplayCutout();
        if (cutout == null) {
            return;
        }
        Rect bounds = new Rect();
        ScreenDecorations.DisplayCutoutView.boundsFromDirection(cutout, 48, bounds);
        bounds.offset(0, this.mDisplayCutoutTouchableRegionSize);
        region.union(bounds);
    }

    @Override // com.android.systemui.statusbar.AlertingNotificationManager, com.android.systemui.statusbar.NotificationLifetimeExtender
    public boolean shouldExtendLifetime(NotificationEntry entry) {
        return this.mVisualStabilityManager.isReorderingAllowed() && super.shouldExtendLifetime(entry);
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onConfigChanged(Configuration newConfig) {
        initResources();
    }

    @Override // com.android.systemui.statusbar.notification.VisualStabilityManager.Callback
    public void onReorderingAllowed() {
        this.mAnimationStateHandler.setHeadsUpGoingAwayAnimationsAllowed(false);
        Iterator<NotificationEntry> it = this.mEntriesToRemoveWhenReorderingAllowed.iterator();
        while (it.hasNext()) {
            NotificationEntry entry = it.next();
            if (isAlerting(entry.key)) {
                removeAlertEntry(entry.key);
            }
        }
        this.mEntriesToRemoveWhenReorderingAllowed.clear();
        this.mAnimationStateHandler.setHeadsUpGoingAwayAnimationsAllowed(true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.policy.HeadsUpManager, com.android.systemui.statusbar.AlertingNotificationManager
    public HeadsUpManager.HeadsUpEntry createAlertEntry() {
        return (HeadsUpManager.HeadsUpEntry) this.mEntryPool.acquire();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.policy.HeadsUpManager, com.android.systemui.statusbar.AlertingNotificationManager
    public void onAlertEntryRemoved(AlertingNotificationManager.AlertEntry alertEntry) {
        this.mKeysToRemoveWhenLeavingKeyguard.remove(alertEntry.mEntry.key);
        super.onAlertEntryRemoved(alertEntry);
        this.mEntryPool.release((HeadsUpEntryPhone) alertEntry);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.policy.HeadsUpManager
    public boolean shouldHeadsUpBecomePinned(NotificationEntry entry) {
        boolean pin = this.mStatusBarState == 0 && !this.mIsExpanded;
        if (this.mBypassController.getBypassEnabled()) {
            pin |= this.mStatusBarState == 1;
        }
        return pin || super.shouldHeadsUpBecomePinned(entry);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.policy.HeadsUpManager
    public void dumpInternal(FileDescriptor fd, PrintWriter pw, String[] args) {
        super.dumpInternal(fd, pw, args);
        pw.print("  mBarState=");
        pw.println(this.mStatusBarState);
        pw.print("  mTouchableRegion=");
        pw.println(this.mTouchableRegion);
    }

    private HeadsUpEntryPhone getHeadsUpEntryPhone(String key) {
        return (HeadsUpEntryPhone) this.mAlertEntries.get(key);
    }

    private HeadsUpEntryPhone getTopHeadsUpEntryPhone() {
        return (HeadsUpEntryPhone) getTopHeadsUpEntry();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.AlertingNotificationManager
    public boolean canRemoveImmediately(String key) {
        if (this.mSwipedOutKeys.contains(key)) {
            this.mSwipedOutKeys.remove(key);
            return true;
        }
        HeadsUpEntryPhone headsUpEntry = getHeadsUpEntryPhone(key);
        HeadsUpEntryPhone topEntry = getTopHeadsUpEntryPhone();
        return headsUpEntry == null || headsUpEntry != topEntry || super.canRemoveImmediately(key);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes21.dex */
    public class HeadsUpEntryPhone extends HeadsUpManager.HeadsUpEntry {
        private boolean extended;
        private boolean mIsAutoHeadsUp;
        private boolean mMenuShownPinned;

        protected HeadsUpEntryPhone() {
            super();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.systemui.statusbar.policy.HeadsUpManager.HeadsUpEntry, com.android.systemui.statusbar.AlertingNotificationManager.AlertEntry
        public boolean isSticky() {
            return super.isSticky() || this.mMenuShownPinned;
        }

        @Override // com.android.systemui.statusbar.AlertingNotificationManager.AlertEntry
        public void setEntry(final NotificationEntry entry) {
            Runnable removeHeadsUpRunnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$HeadsUpManagerPhone$HeadsUpEntryPhone$adyrhF30JE9Yr0JaVKYkiAV0Clw
                @Override // java.lang.Runnable
                public final void run() {
                    HeadsUpManagerPhone.HeadsUpEntryPhone.this.lambda$setEntry$0$HeadsUpManagerPhone$HeadsUpEntryPhone(entry);
                }
            };
            setEntry(entry, removeHeadsUpRunnable);
        }

        public /* synthetic */ void lambda$setEntry$0$HeadsUpManagerPhone$HeadsUpEntryPhone(NotificationEntry entry) {
            if (HeadsUpManagerPhone.this.mVisualStabilityManager.isReorderingAllowed() || entry.showingPulsing()) {
                if (HeadsUpManagerPhone.this.mTrackingHeadsUp) {
                    HeadsUpManagerPhone.this.mEntriesToRemoveAfterExpand.add(entry);
                    return;
                } else if (!this.mIsAutoHeadsUp || HeadsUpManagerPhone.this.mStatusBarState != 1) {
                    HeadsUpManagerPhone.this.removeAlertEntry(entry.key);
                    return;
                } else {
                    HeadsUpManagerPhone.this.mKeysToRemoveWhenLeavingKeyguard.add(entry.key);
                    return;
                }
            }
            HeadsUpManagerPhone.this.mEntriesToRemoveWhenReorderingAllowed.add(entry);
            HeadsUpManagerPhone.this.mVisualStabilityManager.addReorderingAllowedCallback(HeadsUpManagerPhone.this);
        }

        @Override // com.android.systemui.statusbar.AlertingNotificationManager.AlertEntry
        public void updateEntry(boolean updatePostTime) {
            this.mIsAutoHeadsUp = this.mEntry.isAutoHeadsUp();
            super.updateEntry(updatePostTime);
            if (HeadsUpManagerPhone.this.mEntriesToRemoveAfterExpand.contains(this.mEntry)) {
                HeadsUpManagerPhone.this.mEntriesToRemoveAfterExpand.remove(this.mEntry);
            }
            if (HeadsUpManagerPhone.this.mEntriesToRemoveWhenReorderingAllowed.contains(this.mEntry)) {
                HeadsUpManagerPhone.this.mEntriesToRemoveWhenReorderingAllowed.remove(this.mEntry);
            }
            HeadsUpManagerPhone.this.mKeysToRemoveWhenLeavingKeyguard.remove(this.mEntry.key);
        }

        @Override // com.android.systemui.statusbar.policy.HeadsUpManager.HeadsUpEntry
        public void setExpanded(boolean expanded) {
            if (this.expanded == expanded) {
                return;
            }
            this.expanded = expanded;
            if (expanded) {
                removeAutoRemovalCallbacks();
            } else {
                updateEntry(false);
            }
        }

        public void setMenuShownPinned(boolean menuShownPinned) {
            if (this.mMenuShownPinned == menuShownPinned) {
                return;
            }
            this.mMenuShownPinned = menuShownPinned;
            if (menuShownPinned) {
                removeAutoRemovalCallbacks();
            } else {
                updateEntry(false);
            }
        }

        @Override // com.android.systemui.statusbar.policy.HeadsUpManager.HeadsUpEntry, com.android.systemui.statusbar.AlertingNotificationManager.AlertEntry
        public void reset() {
            super.reset();
            this.mMenuShownPinned = false;
            this.extended = false;
            this.mIsAutoHeadsUp = false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void extendPulse() {
            if (!this.extended) {
                this.extended = true;
                updateEntry(false);
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.systemui.statusbar.policy.HeadsUpManager.HeadsUpEntry, com.android.systemui.statusbar.AlertingNotificationManager.AlertEntry, java.lang.Comparable
        public int compareTo(AlertingNotificationManager.AlertEntry alertEntry) {
            HeadsUpEntryPhone headsUpEntry = (HeadsUpEntryPhone) alertEntry;
            boolean autoShown = isAutoHeadsUp();
            boolean otherAutoShown = headsUpEntry.isAutoHeadsUp();
            if (autoShown && !otherAutoShown) {
                return 1;
            }
            if (!autoShown && otherAutoShown) {
                return -1;
            }
            return super.compareTo(alertEntry);
        }

        @Override // com.android.systemui.statusbar.policy.HeadsUpManager.HeadsUpEntry, com.android.systemui.statusbar.AlertingNotificationManager.AlertEntry
        protected long calculateFinishTime() {
            return this.mPostTime + getDecayDuration() + (this.extended ? HeadsUpManagerPhone.this.mExtensionTime : 0);
        }

        private int getDecayDuration() {
            return isAutoHeadsUp() ? getRecommendedHeadsUpTimeoutMs(HeadsUpManagerPhone.this.mAutoHeadsUpNotificationDecay) : getRecommendedHeadsUpTimeoutMs(HeadsUpManagerPhone.this.mAutoDismissNotificationDecay);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isAutoHeadsUp() {
            return this.mIsAutoHeadsUp;
        }
    }
}
