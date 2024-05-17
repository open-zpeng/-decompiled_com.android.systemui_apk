package com.android.systemui.bubbles;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ParceledListSlice;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.ZenModeConfig;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import android.util.SparseSetArray;
import android.view.IPinnedStackController;
import android.view.IPinnedStackListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.bubbles.BubbleData;
import com.android.systemui.bubbles.BubbleStackView;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.shared.system.WindowManagerWrapper;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationRemoveInterceptor;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.NotificationInterruptionStateProvider;
import com.android.systemui.statusbar.notification.collection.NotificationData;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.StatusBarWindowController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ZenModeController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class BubbleController implements ConfigurationController.ConfigurationListener {
    static final int DISMISS_ACCESSIBILITY_ACTION = 6;
    static final int DISMISS_AGED = 2;
    static final int DISMISS_BLOCKED = 4;
    static final int DISMISS_GROUP_CANCELLED = 9;
    static final int DISMISS_INVALID_INTENT = 10;
    static final int DISMISS_NOTIF_CANCEL = 5;
    static final int DISMISS_NO_LONGER_BUBBLE = 7;
    static final int DISMISS_TASK_FINISHED = 3;
    static final int DISMISS_USER_CHANGED = 8;
    static final int DISMISS_USER_GESTURE = 1;
    private static final String ENABLE_BUBBLES = "experiment_enable_bubbles";
    public static final int MAX_BUBBLES = 5;
    private static final String TAG = "Bubbles";
    private IStatusBarService mBarService;
    private BubbleData mBubbleData;
    private final BubbleData.Listener mBubbleDataListener;
    private final Context mContext;
    private int mCurrentUserId;
    private final NotificationEntryListener mEntryListener;
    private BubbleExpandListener mExpandListener;
    private final NotificationLockscreenUserManager mNotifUserManager;
    private final NotificationEntryManager mNotificationEntryManager;
    private final NotificationGroupManager mNotificationGroupManager;
    private final NotificationInterruptionStateProvider mNotificationInterruptionStateProvider;
    private int mOrientation;
    private final NotificationRemoveInterceptor mRemoveInterceptor;
    private final SparseSetArray<String> mSavedBubbleKeysPerUser;
    @Nullable
    private BubbleStackView mStackView;
    private BubbleStateChangeListener mStateChangeListener;
    private StatusBarStateListener mStatusBarStateListener;
    private final StatusBarWindowController mStatusBarWindowController;
    @Nullable
    private BubbleStackView.SurfaceSynchronizer mSurfaceSynchronizer;
    private final BubbleTaskStackListener mTaskStackListener;
    private Rect mTempRect;
    private final ZenModeController mZenModeController;

    /* loaded from: classes21.dex */
    public interface BubbleExpandListener {
        void onBubbleExpandChanged(boolean z, String str);
    }

    /* loaded from: classes21.dex */
    public interface BubbleStateChangeListener {
        void onHasBubblesChanged(boolean z);
    }

    @Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.PARAMETER})
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes21.dex */
    @interface DismissReason {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class StatusBarStateListener implements StatusBarStateController.StateListener {
        private int mState;

        private StatusBarStateListener() {
        }

        public int getCurrentState() {
            return this.mState;
        }

        @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
        public void onStateChanged(int newState) {
            this.mState = newState;
            boolean shouldCollapse = this.mState != 0;
            if (shouldCollapse) {
                BubbleController.this.collapseStack();
            }
            BubbleController.this.updateStack();
        }
    }

    @Inject
    public BubbleController(Context context, StatusBarWindowController statusBarWindowController, BubbleData data, ConfigurationController configurationController, NotificationInterruptionStateProvider interruptionStateProvider, ZenModeController zenModeController, NotificationLockscreenUserManager notifUserManager, NotificationGroupManager groupManager) {
        this(context, statusBarWindowController, data, null, configurationController, interruptionStateProvider, zenModeController, notifUserManager, groupManager);
    }

    public BubbleController(Context context, StatusBarWindowController statusBarWindowController, BubbleData data, @Nullable BubbleStackView.SurfaceSynchronizer synchronizer, ConfigurationController configurationController, NotificationInterruptionStateProvider interruptionStateProvider, ZenModeController zenModeController, NotificationLockscreenUserManager notifUserManager, NotificationGroupManager groupManager) {
        this.mTempRect = new Rect();
        this.mOrientation = 0;
        this.mRemoveInterceptor = new NotificationRemoveInterceptor() { // from class: com.android.systemui.bubbles.BubbleController.3
            @Override // com.android.systemui.statusbar.NotificationRemoveInterceptor
            public boolean onNotificationRemoveRequested(String key, int reason) {
                NotificationEntry entry = BubbleController.this.mNotificationEntryManager.getNotificationData().get(key);
                String groupKey = entry != null ? entry.notification.getGroupKey() : null;
                ArrayList<Bubble> bubbleChildren = BubbleController.this.mBubbleData.getBubblesInGroup(groupKey);
                boolean inBubbleData = BubbleController.this.mBubbleData.hasBubbleWithKey(key);
                boolean isSuppressedSummary = BubbleController.this.mBubbleData.isSummarySuppressed(groupKey) && BubbleController.this.mBubbleData.getSummaryKey(groupKey).equals(key);
                boolean isSummary = entry != null && entry.notification.getNotification().isGroupSummary();
                boolean isSummaryOfBubbles = ((!isSuppressedSummary && !isSummary) || bubbleChildren == null || bubbleChildren.isEmpty()) ? false : true;
                if (inBubbleData || isSummaryOfBubbles) {
                    boolean isClearAll = reason == 3;
                    boolean isUserDimiss = reason == 2 || reason == 1;
                    boolean isAppCancel = reason == 8 || reason == 9;
                    boolean isSummaryCancel = reason == 12;
                    boolean userRemovedNotif = !(entry == null || !entry.isRowDismissed() || isAppCancel) || isClearAll || isUserDimiss || isSummaryCancel;
                    if (isSummaryOfBubbles) {
                        return BubbleController.this.handleSummaryRemovalInterception(entry, userRemovedNotif);
                    }
                    Bubble bubble = BubbleController.this.mBubbleData.getBubbleWithKey(key);
                    boolean bubbleExtended = entry != null && entry.isBubble() && userRemovedNotif;
                    if (bubbleExtended) {
                        bubble.setShowInShadeWhenBubble(false);
                        bubble.setShowBubbleDot(false);
                        if (BubbleController.this.mStackView != null) {
                            BubbleController.this.mStackView.updateDotVisibility(entry.key);
                        }
                        BubbleController.this.mNotificationEntryManager.updateNotifications();
                        return true;
                    } else if (userRemovedNotif || entry == null) {
                        return false;
                    } else {
                        BubbleController.this.mBubbleData.notificationEntryRemoved(entry, 5);
                        return false;
                    }
                }
                return false;
            }
        };
        this.mEntryListener = new NotificationEntryListener() { // from class: com.android.systemui.bubbles.BubbleController.4
            @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
            public void onPendingEntryAdded(NotificationEntry entry) {
                if (BubbleController.areBubblesEnabled(BubbleController.this.mContext) && BubbleController.this.mNotificationInterruptionStateProvider.shouldBubbleUp(entry) && BubbleController.canLaunchInActivityView(BubbleController.this.mContext, entry)) {
                    BubbleController.this.updateBubble(entry);
                }
            }

            @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
            public void onPreEntryUpdated(NotificationEntry entry) {
                if (BubbleController.areBubblesEnabled(BubbleController.this.mContext)) {
                    boolean shouldBubble = BubbleController.this.mNotificationInterruptionStateProvider.shouldBubbleUp(entry) && BubbleController.canLaunchInActivityView(BubbleController.this.mContext, entry);
                    if (!shouldBubble && BubbleController.this.mBubbleData.hasBubbleWithKey(entry.key)) {
                        BubbleController.this.removeBubble(entry.key, 7);
                    } else if (shouldBubble) {
                        BubbleController.this.mBubbleData.getBubbleWithKey(entry.key);
                        BubbleController.this.updateBubble(entry);
                    }
                }
            }

            @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
            public void onNotificationRankingUpdated(NotificationListenerService.RankingMap rankingMap) {
                BubbleController.this.mBubbleData.notificationRankingUpdated(rankingMap);
            }
        };
        this.mBubbleDataListener = new BubbleData.Listener() { // from class: com.android.systemui.bubbles.BubbleController.5
            @Override // com.android.systemui.bubbles.BubbleData.Listener
            public void applyUpdate(BubbleData.Update update) {
                if (BubbleController.this.mStackView == null && update.addedBubble != null) {
                    BubbleController.this.ensureStackViewCreated();
                }
                if (BubbleController.this.mStackView == null) {
                    return;
                }
                if (update.addedBubble != null) {
                    BubbleController.this.mStackView.addBubble(update.addedBubble);
                }
                if (update.expandedChanged && !update.expanded) {
                    BubbleController.this.mStackView.setExpanded(false);
                }
                ArrayList<Pair<Bubble, Integer>> removedBubbles = new ArrayList<>(update.removedBubbles);
                Iterator<Pair<Bubble, Integer>> it = removedBubbles.iterator();
                while (it.hasNext()) {
                    Pair<Bubble, Integer> removed = it.next();
                    Bubble bubble = (Bubble) removed.first;
                    int reason = ((Integer) removed.second).intValue();
                    BubbleController.this.mStackView.removeBubble(bubble);
                    if (reason != 8) {
                        if (!BubbleController.this.mBubbleData.hasBubbleWithKey(bubble.getKey()) && !bubble.showInShadeWhenBubble()) {
                            BubbleController.this.mNotificationEntryManager.performRemoveNotification(bubble.getEntry().notification, 0);
                        } else {
                            bubble.getEntry().notification.getNotification().flags &= -4097;
                            try {
                                BubbleController.this.mBarService.onNotificationBubbleChanged(bubble.getKey(), false);
                            } catch (RemoteException e) {
                            }
                        }
                        String groupKey = bubble.getEntry().notification.getGroupKey();
                        if (BubbleController.this.mBubbleData.isSummarySuppressed(groupKey) && BubbleController.this.mBubbleData.getBubblesInGroup(groupKey).isEmpty()) {
                            String notifKey = BubbleController.this.mBubbleData.getSummaryKey(groupKey);
                            BubbleController.this.mBubbleData.removeSuppressedSummary(groupKey);
                            NotificationEntry entry = BubbleController.this.mNotificationEntryManager.getNotificationData().get(notifKey);
                            BubbleController.this.mNotificationEntryManager.performRemoveNotification(entry.notification, 0);
                        }
                        NotificationEntry summary = BubbleController.this.mNotificationGroupManager.getLogicalGroupSummary(bubble.getEntry().notification);
                        if (summary != null) {
                            ArrayList<NotificationEntry> summaryChildren = BubbleController.this.mNotificationGroupManager.getLogicalChildren(summary.notification);
                            boolean isSummaryThisNotif = summary.key.equals(bubble.getEntry().key);
                            if (!isSummaryThisNotif && (summaryChildren == null || summaryChildren.isEmpty())) {
                                BubbleController.this.mNotificationEntryManager.performRemoveNotification(summary.notification, 0);
                            }
                        }
                    }
                }
                if (update.updatedBubble != null) {
                    BubbleController.this.mStackView.updateBubble(update.updatedBubble);
                }
                if (update.orderChanged) {
                    BubbleController.this.mStackView.updateBubbleOrder(update.bubbles);
                }
                if (update.selectionChanged) {
                    BubbleController.this.mStackView.setSelectedBubble(update.selectedBubble);
                    if (update.selectedBubble != null) {
                        BubbleController.this.mNotificationGroupManager.updateSuppression(update.selectedBubble.getEntry());
                    }
                }
                if (update.expandedChanged && update.expanded) {
                    BubbleController.this.mStackView.setExpanded(true);
                }
                BubbleController.this.mNotificationEntryManager.updateNotifications();
                BubbleController.this.updateStack();
            }
        };
        this.mContext = context;
        this.mNotificationInterruptionStateProvider = interruptionStateProvider;
        this.mNotifUserManager = notifUserManager;
        this.mZenModeController = zenModeController;
        this.mZenModeController.addCallback(new ZenModeController.Callback() { // from class: com.android.systemui.bubbles.BubbleController.1
            @Override // com.android.systemui.statusbar.policy.ZenModeController.Callback
            public void onZenChanged(int zen) {
                if (BubbleController.this.mStackView != null) {
                    BubbleController.this.mStackView.updateDots();
                }
            }

            @Override // com.android.systemui.statusbar.policy.ZenModeController.Callback
            public void onConfigChanged(ZenModeConfig config) {
                if (BubbleController.this.mStackView != null) {
                    BubbleController.this.mStackView.updateDots();
                }
            }
        });
        configurationController.addCallback(this);
        this.mBubbleData = data;
        this.mBubbleData.setListener(this.mBubbleDataListener);
        this.mNotificationEntryManager = (NotificationEntryManager) Dependency.get(NotificationEntryManager.class);
        this.mNotificationEntryManager.addNotificationEntryListener(this.mEntryListener);
        this.mNotificationEntryManager.setNotificationRemoveInterceptor(this.mRemoveInterceptor);
        this.mNotificationGroupManager = groupManager;
        this.mNotificationGroupManager.addOnGroupChangeListener(new NotificationGroupManager.OnGroupChangeListener() { // from class: com.android.systemui.bubbles.BubbleController.2
            @Override // com.android.systemui.statusbar.phone.NotificationGroupManager.OnGroupChangeListener
            public void onGroupSuppressionChanged(NotificationGroupManager.NotificationGroup group, boolean suppressed) {
                String groupKey;
                if (group.summary != null) {
                    groupKey = group.summary.notification.getGroupKey();
                } else {
                    groupKey = null;
                }
                if (!suppressed && groupKey != null && BubbleController.this.mBubbleData.isSummarySuppressed(groupKey)) {
                    BubbleController.this.mBubbleData.removeSuppressedSummary(groupKey);
                }
            }
        });
        this.mStatusBarWindowController = statusBarWindowController;
        this.mStatusBarStateListener = new StatusBarStateListener();
        ((StatusBarStateController) Dependency.get(StatusBarStateController.class)).addCallback(this.mStatusBarStateListener);
        this.mTaskStackListener = new BubbleTaskStackListener();
        ActivityManagerWrapper.getInstance().registerTaskStackListener(this.mTaskStackListener);
        try {
            WindowManagerWrapper.getInstance().addPinnedStackListener(new BubblesImeListener());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        this.mSurfaceSynchronizer = synchronizer;
        this.mBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
        this.mSavedBubbleKeysPerUser = new SparseSetArray<>();
        this.mCurrentUserId = this.mNotifUserManager.getCurrentUserId();
        this.mNotifUserManager.addUserChangedListener(new NotificationLockscreenUserManager.UserChangedListener() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleController$c6Q6I6kWtflKxnnWPbsWzJlB8Eo
            @Override // com.android.systemui.statusbar.NotificationLockscreenUserManager.UserChangedListener
            public final void onUserChanged(int i) {
                BubbleController.this.lambda$new$0$BubbleController(i);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$BubbleController(int newUserId) {
        saveBubbles(this.mCurrentUserId);
        this.mBubbleData.dismissAll(8);
        restoreBubbles(newUserId);
        this.mCurrentUserId = newUserId;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ensureStackViewCreated() {
        if (this.mStackView == null) {
            this.mStackView = new BubbleStackView(this.mContext, this.mBubbleData, this.mSurfaceSynchronizer);
            ViewGroup sbv = this.mStatusBarWindowController.getStatusBarView();
            int bubblePosition = sbv.indexOfChild(sbv.findViewById(R.id.scrim_behind)) + 1;
            sbv.addView(this.mStackView, bubblePosition, new FrameLayout.LayoutParams(-1, -1));
            BubbleExpandListener bubbleExpandListener = this.mExpandListener;
            if (bubbleExpandListener != null) {
                this.mStackView.setExpandListener(bubbleExpandListener);
            }
        }
    }

    private void saveBubbles(int userId) {
        this.mSavedBubbleKeysPerUser.remove(userId);
        for (Bubble bubble : this.mBubbleData.getBubbles()) {
            this.mSavedBubbleKeysPerUser.add(userId, bubble.getKey());
        }
    }

    private void restoreBubbles(int userId) {
        NotificationData notificationData = this.mNotificationEntryManager.getNotificationData();
        ArraySet<String> savedBubbleKeys = this.mSavedBubbleKeysPerUser.get(userId);
        if (savedBubbleKeys == null) {
            return;
        }
        Iterator<NotificationEntry> it = notificationData.getNotificationsForCurrentUser().iterator();
        while (it.hasNext()) {
            NotificationEntry e = it.next();
            if (savedBubbleKeys.contains(e.key) && this.mNotificationInterruptionStateProvider.shouldBubbleUp(e) && canLaunchInActivityView(this.mContext, e)) {
                updateBubble(e, true);
            }
        }
        this.mSavedBubbleKeysPerUser.remove(this.mCurrentUserId);
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onUiModeChanged() {
        BubbleStackView bubbleStackView = this.mStackView;
        if (bubbleStackView != null) {
            bubbleStackView.onThemeChanged();
        }
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onOverlayChanged() {
        BubbleStackView bubbleStackView = this.mStackView;
        if (bubbleStackView != null) {
            bubbleStackView.onThemeChanged();
        }
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onConfigChanged(Configuration newConfig) {
        if (this.mStackView != null && newConfig != null && newConfig.orientation != this.mOrientation) {
            this.mOrientation = newConfig.orientation;
            this.mStackView.onOrientationChanged(newConfig.orientation);
        }
    }

    public void setBubbleStateChangeListener(BubbleStateChangeListener listener) {
        this.mStateChangeListener = listener;
    }

    public void setExpandListener(final BubbleExpandListener listener) {
        this.mExpandListener = new BubbleExpandListener() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleController$Dj-2pSkleqD_4pzyUsy7sxAegg4
            @Override // com.android.systemui.bubbles.BubbleController.BubbleExpandListener
            public final void onBubbleExpandChanged(boolean z, String str) {
                BubbleController.this.lambda$setExpandListener$1$BubbleController(listener, z, str);
            }
        };
        BubbleStackView bubbleStackView = this.mStackView;
        if (bubbleStackView != null) {
            bubbleStackView.setExpandListener(this.mExpandListener);
        }
    }

    public /* synthetic */ void lambda$setExpandListener$1$BubbleController(BubbleExpandListener listener, boolean isExpanding, String key) {
        if (listener != null) {
            listener.onBubbleExpandChanged(isExpanding, key);
        }
        this.mStatusBarWindowController.setBubbleExpanded(isExpanding);
    }

    public boolean hasBubbles() {
        if (this.mStackView == null) {
            return false;
        }
        return this.mBubbleData.hasBubbles();
    }

    public boolean isStackExpanded() {
        return this.mBubbleData.isExpanded();
    }

    public void expandStack() {
        this.mBubbleData.setExpanded(true);
    }

    public void collapseStack() {
        this.mBubbleData.setExpanded(false);
    }

    public boolean isBubbleNotificationSuppressedFromShade(String key) {
        boolean isBubbleAndSuppressed = this.mBubbleData.hasBubbleWithKey(key) && !this.mBubbleData.getBubbleWithKey(key).showInShadeWhenBubble();
        NotificationEntry entry = this.mNotificationEntryManager.getNotificationData().get(key);
        String groupKey = entry != null ? entry.notification.getGroupKey() : null;
        boolean isSuppressedSummary = this.mBubbleData.isSummarySuppressed(groupKey);
        boolean isSummary = key.equals(this.mBubbleData.getSummaryKey(groupKey));
        return (isSummary && isSuppressedSummary) || isBubbleAndSuppressed;
    }

    void selectBubble(Bubble bubble) {
        this.mBubbleData.setSelectedBubble(bubble);
    }

    @VisibleForTesting
    void selectBubble(String key) {
        Bubble bubble = this.mBubbleData.getBubbleWithKey(key);
        selectBubble(bubble);
    }

    public void expandStackAndSelectBubble(String notificationKey) {
        Bubble bubble = this.mBubbleData.getBubbleWithKey(notificationKey);
        if (bubble != null) {
            this.mBubbleData.setSelectedBubble(bubble);
            this.mBubbleData.setExpanded(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dismissStack(int reason) {
        this.mBubbleData.dismissAll(reason);
    }

    public void performBackPressIfNeeded() {
        BubbleStackView bubbleStackView = this.mStackView;
        if (bubbleStackView != null) {
            bubbleStackView.performBackPressIfNeeded();
        }
    }

    void updateBubble(NotificationEntry notif) {
        updateBubble(notif, false);
    }

    void updateBubble(NotificationEntry notif, boolean suppressFlyout) {
        if (notif.importance >= 4) {
            notif.setInterruption();
        }
        this.mBubbleData.notificationEntryUpdated(notif, suppressFlyout);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @MainThread
    public void removeBubble(String key, int reason) {
        Bubble bubble = this.mBubbleData.getBubbleWithKey(key);
        if (bubble != null) {
            this.mBubbleData.notificationEntryRemoved(bubble.getEntry(), reason);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean handleSummaryRemovalInterception(NotificationEntry summary, boolean userRemovedNotif) {
        String groupKey = summary.notification.getGroupKey();
        ArrayList<Bubble> bubbleChildren = this.mBubbleData.getBubblesInGroup(groupKey);
        if (userRemovedNotif) {
            for (int i = 0; i < bubbleChildren.size(); i++) {
                Bubble bubbleChild = bubbleChildren.get(i);
                this.mNotificationGroupManager.onEntryRemoved(bubbleChild.getEntry());
                bubbleChild.setShowInShadeWhenBubble(false);
                bubbleChild.setShowBubbleDot(false);
                BubbleStackView bubbleStackView = this.mStackView;
                if (bubbleStackView != null) {
                    bubbleStackView.updateDotVisibility(bubbleChild.getKey());
                }
            }
            this.mNotificationGroupManager.onEntryRemoved(summary);
            boolean isAutogroupSummary = (summary.notification.getNotification().flags & 1024) != 0;
            if (!isAutogroupSummary) {
                this.mBubbleData.addSummaryToSuppress(summary.notification.getGroupKey(), summary.key);
                this.mNotificationEntryManager.updateNotifications();
            }
            return !isAutogroupSummary;
        }
        this.mBubbleData.removeSuppressedSummary(groupKey);
        for (int i2 = 0; i2 < bubbleChildren.size(); i2++) {
            this.mBubbleData.notificationEntryRemoved(bubbleChildren.get(i2).getEntry(), 9);
        }
        return false;
    }

    public void updateStack() {
        if (this.mStackView == null) {
            return;
        }
        boolean hasBubblesShowing = false;
        if (this.mStatusBarStateListener.getCurrentState() == 0 && hasBubbles()) {
            this.mStackView.setVisibility(hasBubbles() ? 0 : 4);
        } else {
            BubbleStackView bubbleStackView = this.mStackView;
            if (bubbleStackView != null) {
                bubbleStackView.setVisibility(4);
            }
        }
        boolean hadBubbles = this.mStatusBarWindowController.getBubblesShowing();
        if (hasBubbles() && this.mStackView.getVisibility() == 0) {
            hasBubblesShowing = true;
        }
        this.mStatusBarWindowController.setBubblesShowing(hasBubblesShowing);
        BubbleStateChangeListener bubbleStateChangeListener = this.mStateChangeListener;
        if (bubbleStateChangeListener != null && hadBubbles != hasBubblesShowing) {
            bubbleStateChangeListener.onHasBubblesChanged(hasBubblesShowing);
        }
        this.mStackView.updateContentDescription();
    }

    public Rect getTouchableRegion() {
        BubbleStackView bubbleStackView = this.mStackView;
        if (bubbleStackView == null || bubbleStackView.getVisibility() != 0) {
            return null;
        }
        this.mStackView.getBoundsOnScreen(this.mTempRect);
        return this.mTempRect;
    }

    public int getExpandedDisplayId(Context context) {
        Bubble bubble = getExpandedBubble(context);
        if (bubble != null) {
            return bubble.getDisplayId();
        }
        return -1;
    }

    @Nullable
    private Bubble getExpandedBubble(Context context) {
        if (this.mStackView == null) {
            return null;
        }
        boolean defaultDisplay = context.getDisplay() != null && context.getDisplay().getDisplayId() == 0;
        Bubble expandedBubble = this.mStackView.getExpandedBubble();
        if (!defaultDisplay || expandedBubble == null || !isStackExpanded() || this.mStatusBarWindowController.getPanelExpanded()) {
            return null;
        }
        return expandedBubble;
    }

    @VisibleForTesting
    BubbleStackView getStackView() {
        return this.mStackView;
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("BubbleController state:");
        this.mBubbleData.dump(fd, pw, args);
        pw.println();
        BubbleStackView bubbleStackView = this.mStackView;
        if (bubbleStackView != null) {
            bubbleStackView.dump(fd, pw, args);
        }
        pw.println();
    }

    static String formatBubblesString(List<Bubble> bubbles, Bubble selected) {
        StringBuilder sb = new StringBuilder();
        Iterator<Bubble> it = bubbles.iterator();
        while (it.hasNext()) {
            Bubble bubble = it.next();
            if (bubble == null) {
                sb.append("   <null> !!!!!\n");
            } else {
                boolean isSelected = bubble == selected;
                Object[] objArr = new Object[4];
                objArr[0] = isSelected ? "->" : "  ";
                objArr[1] = Long.valueOf(bubble.getLastActivity());
                objArr[2] = Integer.valueOf(bubble.isOngoing() ? 1 : 0);
                objArr[3] = bubble.getKey();
                sb.append(String.format("%s Bubble{act=%12d, ongoing=%d, key=%s}\n", objArr));
            }
        }
        return sb.toString();
    }

    @MainThread
    /* loaded from: classes21.dex */
    private class BubbleTaskStackListener extends TaskStackChangeListener {
        private BubbleTaskStackListener() {
        }

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onTaskMovedToFront(ActivityManager.RunningTaskInfo taskInfo) {
            if (BubbleController.this.mStackView != null && taskInfo.displayId == 0 && !BubbleController.this.mStackView.isExpansionAnimating()) {
                BubbleController.this.mBubbleData.setExpanded(false);
            }
        }

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onActivityLaunchOnSecondaryDisplayRerouted() {
            if (BubbleController.this.mStackView != null) {
                BubbleController.this.mBubbleData.setExpanded(false);
            }
        }

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onBackPressedOnTaskRoot(ActivityManager.RunningTaskInfo taskInfo) {
            if (BubbleController.this.mStackView != null) {
                int i = taskInfo.displayId;
                BubbleController bubbleController = BubbleController.this;
                if (i == bubbleController.getExpandedDisplayId(bubbleController.mContext)) {
                    BubbleController.this.mBubbleData.setExpanded(false);
                }
            }
        }

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onSingleTaskDisplayDrawn(int displayId) {
            Bubble expandedBubble;
            if (BubbleController.this.mStackView != null) {
                expandedBubble = BubbleController.this.mStackView.getExpandedBubble();
            } else {
                expandedBubble = null;
            }
            if (expandedBubble != null && expandedBubble.getDisplayId() == displayId) {
                expandedBubble.setContentVisibility(true);
            }
        }

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onSingleTaskDisplayEmpty(int displayId) {
            Bubble expandedBubble;
            if (BubbleController.this.mStackView != null) {
                expandedBubble = BubbleController.this.mStackView.getExpandedBubble();
            } else {
                expandedBubble = null;
            }
            int expandedId = expandedBubble != null ? expandedBubble.getDisplayId() : -1;
            if (BubbleController.this.mStackView != null && BubbleController.this.mStackView.isExpanded() && expandedId == displayId) {
                BubbleController.this.mBubbleData.setExpanded(false);
            }
            BubbleController.this.mBubbleData.notifyDisplayEmpty(displayId);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean areBubblesEnabled(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), ENABLE_BUBBLES, 1) != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean canLaunchInActivityView(Context context, NotificationEntry entry) {
        PendingIntent intent;
        if (entry.getBubbleMetadata() != null) {
            intent = entry.getBubbleMetadata().getIntent();
        } else {
            intent = null;
        }
        if (intent == null) {
            Log.w(TAG, "Unable to create bubble -- no intent");
            return false;
        }
        ActivityInfo info = intent.getIntent().resolveActivityInfo(context.getPackageManager(), 0);
        if (info == null) {
            Log.w(TAG, "Unable to send as bubble -- couldn't find activity info for intent: " + intent);
            return false;
        } else if (!ActivityInfo.isResizeableMode(info.resizeMode)) {
            Log.w(TAG, "Unable to send as bubble -- activity is not resizable for intent: " + intent);
            return false;
        } else if (info.documentLaunchMode != 2) {
            Log.w(TAG, "Unable to send as bubble -- activity is not documentLaunchMode=always for intent: " + intent);
            return false;
        } else if ((info.flags & Integer.MIN_VALUE) == 0) {
            Log.w(TAG, "Unable to send as bubble -- activity is not embeddable for intent: " + intent);
            return false;
        } else {
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class BubblesImeListener extends IPinnedStackListener.Stub {
        private BubblesImeListener() {
        }

        public void onListenerRegistered(IPinnedStackController controller) throws RemoteException {
        }

        public void onMovementBoundsChanged(Rect insetBounds, Rect normalBounds, Rect animatingBounds, boolean fromImeAdjustment, boolean fromShelfAdjustment, int displayRotation) throws RemoteException {
        }

        public void onImeVisibilityChanged(final boolean imeVisible, final int imeHeight) {
            if (BubbleController.this.mStackView != null && BubbleController.this.mStackView.getBubbleCount() > 0) {
                BubbleController.this.mStackView.post(new Runnable() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleController$BubblesImeListener$k3Ccv-01hiK8jFFaKEuMmcHqId4
                    @Override // java.lang.Runnable
                    public final void run() {
                        BubbleController.BubblesImeListener.this.lambda$onImeVisibilityChanged$0$BubbleController$BubblesImeListener(imeVisible, imeHeight);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onImeVisibilityChanged$0$BubbleController$BubblesImeListener(boolean imeVisible, int imeHeight) {
            BubbleController.this.mStackView.onImeVisibilityChanged(imeVisible, imeHeight);
        }

        public void onShelfVisibilityChanged(boolean shelfVisible, int shelfHeight) throws RemoteException {
        }

        public void onMinimizedStateChanged(boolean isMinimized) throws RemoteException {
        }

        public void onActionsChanged(ParceledListSlice actions) throws RemoteException {
        }
    }
}
