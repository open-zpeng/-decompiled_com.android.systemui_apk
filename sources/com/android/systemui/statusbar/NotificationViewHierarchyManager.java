package com.android.systemui.statusbar;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Trace;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.R;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.util.Assert;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class NotificationViewHierarchyManager implements DynamicPrivacyController.Listener {
    private static final String TAG = "NotificationViewHierarchyManager";
    private final boolean mAlwaysExpandNonGroupedNotification;
    private final BubbleController mBubbleController;
    private final KeyguardBypassController mBypassController;
    private final DynamicPrivacyController mDynamicPrivacyController;
    private final NotificationEntryManager mEntryManager;
    protected final NotificationGroupManager mGroupManager;
    private final Handler mHandler;
    private boolean mIsHandleDynamicPrivacyChangeScheduled;
    private NotificationListContainer mListContainer;
    protected final NotificationLockscreenUserManager mLockscreenUserManager;
    private boolean mPerformingUpdate;
    private NotificationPresenter mPresenter;
    private final Lazy<ShadeController> mShadeController;
    private final SysuiStatusBarStateController mStatusBarStateController;
    private final HashMap<ExpandableNotificationRow, List<ExpandableNotificationRow>> mTmpChildOrderMap = new HashMap<>();
    protected final VisualStabilityManager mVisualStabilityManager;

    @Inject
    public NotificationViewHierarchyManager(Context context, @Named("main_handler") Handler mainHandler, NotificationLockscreenUserManager notificationLockscreenUserManager, NotificationGroupManager groupManager, VisualStabilityManager visualStabilityManager, StatusBarStateController statusBarStateController, NotificationEntryManager notificationEntryManager, Lazy<ShadeController> shadeController, KeyguardBypassController bypassController, BubbleController bubbleController, DynamicPrivacyController privacyController) {
        this.mHandler = mainHandler;
        this.mLockscreenUserManager = notificationLockscreenUserManager;
        this.mBypassController = bypassController;
        this.mGroupManager = groupManager;
        this.mVisualStabilityManager = visualStabilityManager;
        this.mStatusBarStateController = (SysuiStatusBarStateController) statusBarStateController;
        this.mEntryManager = notificationEntryManager;
        this.mShadeController = shadeController;
        Resources res = context.getResources();
        this.mAlwaysExpandNonGroupedNotification = res.getBoolean(R.bool.config_alwaysExpandNonGroupedNotifications);
        this.mBubbleController = bubbleController;
        this.mDynamicPrivacyController = privacyController;
        privacyController.addListener(this);
    }

    public void setUpWithPresenter(NotificationPresenter presenter, NotificationListContainer listContainer) {
        this.mPresenter = presenter;
        this.mListContainer = listContainer;
    }

    public void updateNotificationViews() {
        ArrayList<NotificationEntry> activeNotifications;
        Assert.isMainThread();
        beginUpdate();
        ArrayList<NotificationEntry> activeNotifications2 = this.mEntryManager.getNotificationData().getActiveNotifications();
        ArrayList<ExpandableNotificationRow> toShow = new ArrayList<>(activeNotifications2.size());
        int N = activeNotifications2.size();
        int i = 0;
        while (true) {
            boolean deviceSensitive = false;
            if (i >= N) {
                break;
            }
            NotificationEntry ent = activeNotifications2.get(i);
            if (ent.isRowDismissed() || ent.isRowRemoved()) {
                activeNotifications = activeNotifications2;
            } else if (this.mBubbleController.isBubbleNotificationSuppressedFromShade(ent.key)) {
                activeNotifications = activeNotifications2;
            } else {
                int userId = ent.notification.getUserId();
                int currentUserId = this.mLockscreenUserManager.getCurrentUserId();
                boolean devicePublic = this.mLockscreenUserManager.isLockscreenPublicMode(currentUserId);
                boolean userPublic = devicePublic || this.mLockscreenUserManager.isLockscreenPublicMode(userId);
                if (userPublic && this.mDynamicPrivacyController.isDynamicallyUnlocked() && (userId == currentUserId || userId == -1 || !this.mLockscreenUserManager.needsSeparateWorkChallenge(userId))) {
                    userPublic = false;
                }
                boolean needsRedaction = this.mLockscreenUserManager.needsRedaction(ent);
                boolean sensitive = userPublic && needsRedaction;
                if (devicePublic && !this.mLockscreenUserManager.userAllowsPrivateNotificationsInPublic(currentUserId)) {
                    deviceSensitive = true;
                }
                ent.setSensitive(sensitive, deviceSensitive);
                ent.getRow().setNeedsRedaction(needsRedaction);
                if (this.mGroupManager.isChildInGroupWithSummary(ent.notification)) {
                    NotificationEntry summary = this.mGroupManager.getGroupSummary(ent.notification);
                    List<ExpandableNotificationRow> orderedChildren = this.mTmpChildOrderMap.get(summary.getRow());
                    if (orderedChildren != null) {
                        activeNotifications = activeNotifications2;
                    } else {
                        orderedChildren = new ArrayList();
                        activeNotifications = activeNotifications2;
                        this.mTmpChildOrderMap.put(summary.getRow(), orderedChildren);
                    }
                    orderedChildren.add(ent.getRow());
                } else {
                    activeNotifications = activeNotifications2;
                    toShow.add(ent.getRow());
                }
            }
            i++;
            activeNotifications2 = activeNotifications;
        }
        ArrayList<ExpandableNotificationRow> viewsToRemove = new ArrayList<>();
        for (int i2 = 0; i2 < this.mListContainer.getContainerChildCount(); i2++) {
            View child = this.mListContainer.getContainerChildAt(i2);
            if (!toShow.contains(child) && (child instanceof ExpandableNotificationRow)) {
                ExpandableNotificationRow row = (ExpandableNotificationRow) child;
                if (!row.isBlockingHelperShowing()) {
                    viewsToRemove.add((ExpandableNotificationRow) child);
                }
            }
        }
        Iterator<ExpandableNotificationRow> it = viewsToRemove.iterator();
        while (it.hasNext()) {
            ExpandableNotificationRow viewToRemove = it.next();
            if (this.mGroupManager.isChildInGroupWithSummary(viewToRemove.getStatusBarNotification())) {
                this.mListContainer.setChildTransferInProgress(true);
            }
            if (viewToRemove.isSummaryWithChildren()) {
                viewToRemove.removeAllChildren();
            }
            this.mListContainer.removeContainerView(viewToRemove);
            this.mListContainer.setChildTransferInProgress(false);
        }
        removeNotificationChildren();
        int i3 = 0;
        while (i3 < toShow.size()) {
            ExpandableNotificationRow v = toShow.get(i3);
            if (v.getParent() == null) {
                this.mVisualStabilityManager.notifyViewAddition(v);
                this.mListContainer.addContainerView(v);
            } else if (!this.mListContainer.containsView(v)) {
                toShow.remove(v);
                i3--;
            }
            i3++;
        }
        addNotificationChildrenAndSort();
        int j = 0;
        for (int i4 = 0; i4 < this.mListContainer.getContainerChildCount(); i4++) {
            View child2 = this.mListContainer.getContainerChildAt(i4);
            if ((child2 instanceof ExpandableNotificationRow) && !((ExpandableNotificationRow) child2).isBlockingHelperShowing()) {
                ExpandableNotificationRow targetChild = toShow.get(j);
                if (child2 != targetChild) {
                    if (!this.mVisualStabilityManager.canReorderNotification(targetChild)) {
                        this.mVisualStabilityManager.addReorderingAllowedCallback(this.mEntryManager);
                    } else {
                        this.mListContainer.changeViewPosition(targetChild, i4);
                    }
                }
                j++;
            }
        }
        this.mVisualStabilityManager.onReorderingFinished();
        this.mTmpChildOrderMap.clear();
        updateRowStatesInternal();
        this.mListContainer.onNotificationViewUpdateFinished();
        endUpdate();
    }

    private void addNotificationChildrenAndSort() {
        boolean orderChanged = false;
        for (int i = 0; i < this.mListContainer.getContainerChildCount(); i++) {
            View view = this.mListContainer.getContainerChildAt(i);
            if (view instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow parent = (ExpandableNotificationRow) view;
                List<ExpandableNotificationRow> children = parent.getNotificationChildren();
                List<ExpandableNotificationRow> orderedChildren = this.mTmpChildOrderMap.get(parent);
                for (int childIndex = 0; orderedChildren != null && childIndex < orderedChildren.size(); childIndex++) {
                    ExpandableNotificationRow childView = orderedChildren.get(childIndex);
                    if (children == null || !children.contains(childView)) {
                        if (childView.getParent() != null) {
                            Log.wtf(TAG, "trying to add a notification child that already has a parent. class:" + childView.getParent().getClass() + "\n child: " + childView);
                            ((ViewGroup) childView.getParent()).removeView(childView);
                        }
                        this.mVisualStabilityManager.notifyViewAddition(childView);
                        parent.addChildNotification(childView, childIndex);
                        this.mListContainer.notifyGroupChildAdded(childView);
                    }
                }
                orderChanged |= parent.applyChildOrder(orderedChildren, this.mVisualStabilityManager, this.mEntryManager);
            }
        }
        if (orderChanged) {
            this.mListContainer.generateChildOrderChangedEvent();
        }
    }

    private void removeNotificationChildren() {
        ArrayList<ExpandableNotificationRow> toRemove = new ArrayList<>();
        for (int i = 0; i < this.mListContainer.getContainerChildCount(); i++) {
            View view = this.mListContainer.getContainerChildAt(i);
            if (view instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow parent = (ExpandableNotificationRow) view;
                List<ExpandableNotificationRow> children = parent.getNotificationChildren();
                List<ExpandableNotificationRow> orderedChildren = this.mTmpChildOrderMap.get(parent);
                if (children != null) {
                    toRemove.clear();
                    for (ExpandableNotificationRow childRow : children) {
                        if (orderedChildren == null || !orderedChildren.contains(childRow)) {
                            if (!childRow.keepInParent()) {
                                toRemove.add(childRow);
                            }
                        }
                    }
                    Iterator<ExpandableNotificationRow> it = toRemove.iterator();
                    while (it.hasNext()) {
                        ExpandableNotificationRow remove = it.next();
                        parent.removeChildNotification(remove);
                        if (this.mEntryManager.getNotificationData().get(remove.getStatusBarNotification().getKey()) == null) {
                            this.mListContainer.notifyGroupChildRemoved(remove, parent.getChildrenContainer());
                        }
                    }
                }
            }
        }
    }

    public void updateRowStates() {
        Assert.isMainThread();
        beginUpdate();
        updateRowStatesInternal();
        endUpdate();
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void updateRowStatesInternal() {
        NotificationEntry summary;
        Trace.beginSection("NotificationViewHierarchyManager#updateRowStates");
        int N = this.mListContainer.getContainerChildCount();
        int visibleNotifications = 0;
        int i = 0;
        boolean z = true;
        boolean onKeyguard = this.mStatusBarStateController.getState() == 1;
        int maxNotifications = -1;
        if (onKeyguard && !this.mBypassController.getBypassEnabled()) {
            maxNotifications = this.mPresenter.getMaxNotificationsWhileLocked(true);
        }
        this.mListContainer.setMaxDisplayedNotifications(maxNotifications);
        Stack<ExpandableNotificationRow> stack = new Stack<>();
        for (int i2 = N - 1; i2 >= 0; i2--) {
            View child = this.mListContainer.getContainerChildAt(i2);
            if (child instanceof ExpandableNotificationRow) {
                stack.push((ExpandableNotificationRow) child);
            }
        }
        while (!stack.isEmpty()) {
            ExpandableNotificationRow row = stack.pop();
            NotificationEntry entry = row.getEntry();
            boolean isChildNotification = this.mGroupManager.isChildInGroupWithSummary(entry.notification);
            row.setOnKeyguard(onKeyguard);
            if (!onKeyguard) {
                row.setSystemExpanded((this.mAlwaysExpandNonGroupedNotification || !(visibleNotifications != 0 || isChildNotification || row.isLowPriority())) ? z ? 1 : 0 : i);
            }
            int userId = entry.notification.getUserId();
            int i3 = (!this.mGroupManager.isSummaryOfSuppressedGroup(entry.notification) || entry.isRowRemoved()) ? i : z ? 1 : 0;
            boolean showOnKeyguard = this.mLockscreenUserManager.shouldShowOnKeyguard(entry);
            if (!showOnKeyguard && this.mGroupManager.isChildInGroupWithSummary(entry.notification) && (summary = this.mGroupManager.getLogicalGroupSummary(entry.notification)) != null && this.mLockscreenUserManager.shouldShowOnKeyguard(summary)) {
                showOnKeyguard = true;
            }
            if (i3 != 0 || this.mLockscreenUserManager.shouldHideNotifications(userId) || (onKeyguard && !showOnKeyguard)) {
                entry.getRow().setVisibility(8);
            } else {
                int i4 = entry.getRow().getVisibility() == 8 ? z ? 1 : 0 : i;
                if (i4 != 0) {
                    entry.getRow().setVisibility(i);
                }
                if (!isChildNotification && !entry.getRow().isRemoved()) {
                    if (i4 != 0) {
                        NotificationListContainer notificationListContainer = this.mListContainer;
                        ExpandableNotificationRow row2 = entry.getRow();
                        if (showOnKeyguard) {
                            z = false;
                        }
                        notificationListContainer.generateAddAnimation(row2, z);
                    }
                    visibleNotifications++;
                }
            }
            if (row.isSummaryWithChildren()) {
                List<ExpandableNotificationRow> notificationChildren = row.getNotificationChildren();
                int size = notificationChildren.size();
                for (int i5 = size - 1; i5 >= 0; i5--) {
                    stack.push(notificationChildren.get(i5));
                }
            }
            row.showAppOpsIcons(entry.mActiveAppOps);
            row.setLastAudiblyAlertedMs(entry.lastAudiblyAlertedMs);
            i = 0;
            z = true;
        }
        Trace.beginSection("NotificationPresenter#onUpdateRowStates");
        this.mPresenter.onUpdateRowStates();
        Trace.endSection();
        Trace.endSection();
    }

    @Override // com.android.systemui.statusbar.notification.DynamicPrivacyController.Listener
    public void onDynamicPrivacyChanged() {
        if (this.mPerformingUpdate) {
            Log.w(TAG, "onDynamicPrivacyChanged made a re-entrant call");
        }
        if (!this.mIsHandleDynamicPrivacyChangeScheduled) {
            this.mIsHandleDynamicPrivacyChangeScheduled = true;
            this.mHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.-$$Lambda$NotificationViewHierarchyManager$VZHW9NMJkqBLUXo3lkuiamxmEXo
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationViewHierarchyManager.this.onHandleDynamicPrivacyChanged();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onHandleDynamicPrivacyChanged() {
        this.mIsHandleDynamicPrivacyChangeScheduled = false;
        updateNotificationViews();
    }

    private void beginUpdate() {
        if (this.mPerformingUpdate) {
            Log.wtf(TAG, "Re-entrant code during update", new Exception());
        }
        this.mPerformingUpdate = true;
    }

    private void endUpdate() {
        if (!this.mPerformingUpdate) {
            Log.wtf(TAG, "Manager state has become desynced", new Exception());
        }
        this.mPerformingUpdate = false;
    }
}
