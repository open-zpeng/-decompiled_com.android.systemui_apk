package com.android.systemui.statusbar.phone;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.service.notification.StatusBarNotification;
import android.service.vr.IVrManager;
import android.service.vr.IVrStateCallbacks;
import android.util.Slog;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.internal.widget.MessagingGroup;
import com.android.internal.widget.MessagingMessage;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.Dependency;
import com.android.systemui.ForegroundServiceNotificationListener;
import com.android.systemui.InitController;
import com.android.systemui.R;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationViewHierarchyManager;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.notification.AboveShelfObserver;
import com.android.systemui.statusbar.notification.ActivityLaunchAnimator;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.notification.NotificationAlertingManager;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.NotificationInterruptionStateProvider;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.NotificationRowBinderImpl;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager;
import com.android.systemui.statusbar.notification.row.NotificationInfo;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.phone.StatusBarNotificationPresenter;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.BooleanSupplier;
/* loaded from: classes21.dex */
public class StatusBarNotificationPresenter implements NotificationPresenter, ConfigurationController.ConfigurationListener, NotificationRowBinderImpl.BindRowCallback {
    private static final String TAG = "StatusBarNotificationPresenter";
    private final AboveShelfObserver mAboveShelfObserver;
    private final AccessibilityManager mAccessibilityManager;
    private final ActivityLaunchAnimator mActivityLaunchAnimator;
    private final IStatusBarService mBarService;
    private final CommandQueue mCommandQueue;
    private final Context mContext;
    private boolean mDispatchUiModeChangeOnUserSwitched;
    private final DozeScrimController mDozeScrimController;
    private final DynamicPrivacyController mDynamicPrivacyController;
    private final HeadsUpManagerPhone mHeadsUpManager;
    private final KeyguardManager mKeyguardManager;
    private final int mMaxAllowedKeyguardNotifications;
    private int mMaxKeyguardNotifications;
    private final NotificationPanelView mNotificationPanel;
    private TextView mNotificationPanelDebugText;
    private boolean mReinflateNotificationsOnUserSwitched;
    private final ScrimController mScrimController;
    private final UnlockMethodCache mUnlockMethodCache;
    protected boolean mVrMode;
    private final LockscreenGestureLogger mLockscreenGestureLogger = (LockscreenGestureLogger) Dependency.get(LockscreenGestureLogger.class);
    private final ShadeController mShadeController = (ShadeController) Dependency.get(ShadeController.class);
    private final ActivityStarter mActivityStarter = (ActivityStarter) Dependency.get(ActivityStarter.class);
    private final KeyguardMonitor mKeyguardMonitor = (KeyguardMonitor) Dependency.get(KeyguardMonitor.class);
    private final NotificationViewHierarchyManager mViewHierarchyManager = (NotificationViewHierarchyManager) Dependency.get(NotificationViewHierarchyManager.class);
    private final NotificationLockscreenUserManager mLockscreenUserManager = (NotificationLockscreenUserManager) Dependency.get(NotificationLockscreenUserManager.class);
    private final SysuiStatusBarStateController mStatusBarStateController = (SysuiStatusBarStateController) Dependency.get(StatusBarStateController.class);
    private final NotificationEntryManager mEntryManager = (NotificationEntryManager) Dependency.get(NotificationEntryManager.class);
    private final NotificationInterruptionStateProvider mNotificationInterruptionStateProvider = (NotificationInterruptionStateProvider) Dependency.get(NotificationInterruptionStateProvider.class);
    private final NotificationMediaManager mMediaManager = (NotificationMediaManager) Dependency.get(NotificationMediaManager.class);
    private final VisualStabilityManager mVisualStabilityManager = (VisualStabilityManager) Dependency.get(VisualStabilityManager.class);
    private final NotificationGutsManager mGutsManager = (NotificationGutsManager) Dependency.get(NotificationGutsManager.class);
    private final IVrStateCallbacks mVrStateCallbacks = new IVrStateCallbacks.Stub() { // from class: com.android.systemui.statusbar.phone.StatusBarNotificationPresenter.2
        public void onVrStateChanged(boolean enabled) {
            StatusBarNotificationPresenter.this.mVrMode = enabled;
        }
    };
    private final NotificationInfo.CheckSaveListener mCheckSaveListener = new AnonymousClass3();
    private final NotificationGutsManager.OnSettingsClickListener mOnSettingsClickListener = new NotificationGutsManager.OnSettingsClickListener() { // from class: com.android.systemui.statusbar.phone.StatusBarNotificationPresenter.4
        @Override // com.android.systemui.statusbar.notification.row.NotificationGutsManager.OnSettingsClickListener
        public void onSettingsClick(String key) {
            try {
                StatusBarNotificationPresenter.this.mBarService.onNotificationSettingsViewed(key);
            } catch (RemoteException e) {
            }
        }
    };

    public StatusBarNotificationPresenter(Context context, NotificationPanelView panel, HeadsUpManagerPhone headsUp, StatusBarWindowView statusBarWindow, ViewGroup stackScroller, DozeScrimController dozeScrimController, ScrimController scrimController, ActivityLaunchAnimator activityLaunchAnimator, DynamicPrivacyController dynamicPrivacyController, NotificationAlertingManager notificationAlertingManager, final NotificationRowBinderImpl notificationRowBinder) {
        this.mContext = context;
        this.mNotificationPanel = panel;
        this.mHeadsUpManager = headsUp;
        this.mDynamicPrivacyController = dynamicPrivacyController;
        this.mCommandQueue = (CommandQueue) SysUiServiceProvider.getComponent(context, CommandQueue.class);
        this.mAboveShelfObserver = new AboveShelfObserver(stackScroller);
        this.mActivityLaunchAnimator = activityLaunchAnimator;
        this.mAboveShelfObserver.setListener((AboveShelfObserver.HasViewAboveShelfChangedListener) statusBarWindow.findViewById(R.id.notification_container_parent));
        this.mAccessibilityManager = (AccessibilityManager) context.getSystemService(AccessibilityManager.class);
        this.mDozeScrimController = dozeScrimController;
        this.mScrimController = scrimController;
        this.mUnlockMethodCache = UnlockMethodCache.getInstance(this.mContext);
        this.mKeyguardManager = (KeyguardManager) context.getSystemService(KeyguardManager.class);
        this.mMaxAllowedKeyguardNotifications = context.getResources().getInteger(R.integer.keyguard_max_notification_count);
        this.mBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
        IVrManager vrManager = IVrManager.Stub.asInterface(ServiceManager.getService("vrmanager"));
        if (vrManager != null) {
            try {
                vrManager.registerListener(this.mVrStateCallbacks);
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to register VR mode state listener: " + e);
            }
        }
        final NotificationRemoteInputManager remoteInputManager = (NotificationRemoteInputManager) Dependency.get(NotificationRemoteInputManager.class);
        remoteInputManager.setUpWithCallback((NotificationRemoteInputManager.Callback) Dependency.get(NotificationRemoteInputManager.Callback.class), this.mNotificationPanel.createRemoteInputDelegate());
        remoteInputManager.getController().addCallback((RemoteInputController.Callback) Dependency.get(StatusBarWindowController.class));
        final NotificationListContainer notifListContainer = (NotificationListContainer) stackScroller;
        ((InitController) Dependency.get(InitController.class)).addPostInitTask(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBarNotificationPresenter$jgw9sgV4IaJBnAReag6kP8VBfUI
            @Override // java.lang.Runnable
            public final void run() {
                StatusBarNotificationPresenter.this.lambda$new$0$StatusBarNotificationPresenter(notifListContainer, remoteInputManager, notificationRowBinder);
            }
        });
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this);
        notificationAlertingManager.setHeadsUpManager(this.mHeadsUpManager);
    }

    public /* synthetic */ void lambda$new$0$StatusBarNotificationPresenter(NotificationListContainer notifListContainer, NotificationRemoteInputManager remoteInputManager, NotificationRowBinderImpl notificationRowBinder) {
        NotificationEntryListener notificationEntryListener = new NotificationEntryListener() { // from class: com.android.systemui.statusbar.phone.StatusBarNotificationPresenter.1
            @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
            public void onNotificationAdded(NotificationEntry entry) {
                StatusBarNotificationPresenter.this.mShadeController.updateAreThereNotifications();
            }

            @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
            public void onPostEntryUpdated(NotificationEntry entry) {
                StatusBarNotificationPresenter.this.mShadeController.updateAreThereNotifications();
            }

            @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
            public void onEntryRemoved(NotificationEntry entry, NotificationVisibility visibility, boolean removedByUser) {
                StatusBarNotificationPresenter.this.onNotificationRemoved(entry.key, entry.notification);
                if (removedByUser) {
                    StatusBarNotificationPresenter.this.maybeEndAmbientPulse();
                }
            }
        };
        this.mViewHierarchyManager.setUpWithPresenter(this, notifListContainer);
        this.mEntryManager.setUpWithPresenter(this, notifListContainer, this.mHeadsUpManager);
        this.mEntryManager.addNotificationEntryListener(notificationEntryListener);
        this.mEntryManager.addNotificationLifetimeExtender(this.mHeadsUpManager);
        this.mEntryManager.addNotificationLifetimeExtender(this.mGutsManager);
        this.mEntryManager.addNotificationLifetimeExtenders(remoteInputManager.getLifetimeExtenders());
        notificationRowBinder.setUpWithPresenter(this, notifListContainer, this.mHeadsUpManager, this.mEntryManager, this);
        this.mNotificationInterruptionStateProvider.setUpWithPresenter(this, this.mHeadsUpManager, new NotificationInterruptionStateProvider.HeadsUpSuppressor() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$zehGIehk9xIdDvIsyl4sWX9YxIM
            @Override // com.android.systemui.statusbar.notification.NotificationInterruptionStateProvider.HeadsUpSuppressor
            public final boolean canHeadsUp(NotificationEntry notificationEntry, StatusBarNotification statusBarNotification) {
                return StatusBarNotificationPresenter.this.canHeadsUp(notificationEntry, statusBarNotification);
            }
        });
        this.mLockscreenUserManager.setUpWithPresenter(this);
        this.mMediaManager.setUpWithPresenter(this);
        this.mVisualStabilityManager.setUpWithPresenter(this);
        this.mGutsManager.setUpWithPresenter(this, notifListContainer, this.mCheckSaveListener, this.mOnSettingsClickListener);
        Dependency.get(ForegroundServiceNotificationListener.class);
        onUserSwitched(this.mLockscreenUserManager.getCurrentUserId());
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onDensityOrFontScaleChanged() {
        MessagingMessage.dropCache();
        MessagingGroup.dropCache();
        if (!KeyguardUpdateMonitor.getInstance(this.mContext).isSwitchingUser()) {
            updateNotificationsOnDensityOrFontScaleChanged();
        } else {
            this.mReinflateNotificationsOnUserSwitched = true;
        }
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onUiModeChanged() {
        if (!KeyguardUpdateMonitor.getInstance(this.mContext).isSwitchingUser()) {
            updateNotificationOnUiModeChanged();
        } else {
            this.mDispatchUiModeChangeOnUserSwitched = true;
        }
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onOverlayChanged() {
        onDensityOrFontScaleChanged();
    }

    private void updateNotificationOnUiModeChanged() {
        ArrayList<NotificationEntry> userNotifications = this.mEntryManager.getNotificationData().getNotificationsForCurrentUser();
        for (int i = 0; i < userNotifications.size(); i++) {
            NotificationEntry entry = userNotifications.get(i);
            ExpandableNotificationRow row = entry.getRow();
            if (row != null) {
                row.onUiModeChanged();
            }
        }
    }

    private void updateNotificationsOnDensityOrFontScaleChanged() {
        ArrayList<NotificationEntry> userNotifications = this.mEntryManager.getNotificationData().getNotificationsForCurrentUser();
        for (int i = 0; i < userNotifications.size(); i++) {
            NotificationEntry entry = userNotifications.get(i);
            entry.onDensityOrFontScaleChanged();
            boolean exposedGuts = entry.areGutsExposed();
            if (exposedGuts) {
                this.mGutsManager.onDensityOrFontScaleChanged(entry);
            }
        }
    }

    @Override // com.android.systemui.statusbar.NotificationPresenter
    public boolean isCollapsing() {
        return this.mNotificationPanel.isCollapsing() || this.mActivityLaunchAnimator.isAnimationPending() || this.mActivityLaunchAnimator.isAnimationRunning();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void maybeEndAmbientPulse() {
        if (this.mNotificationPanel.hasPulsingNotifications() && !this.mHeadsUpManager.hasNotifications()) {
            this.mDozeScrimController.pulseOutNow();
        }
    }

    @Override // com.android.systemui.statusbar.NotificationPresenter
    public void updateNotificationViews() {
        if (this.mScrimController == null) {
            return;
        }
        if (isCollapsing()) {
            this.mShadeController.addPostCollapseAction(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$gi2ID0w-NOAgPDK3Aiflyq0eAFU
                @Override // java.lang.Runnable
                public final void run() {
                    StatusBarNotificationPresenter.this.updateNotificationViews();
                }
            });
            return;
        }
        this.mViewHierarchyManager.updateNotificationViews();
        this.mNotificationPanel.updateNotificationViews();
    }

    public void onNotificationRemoved(String key, StatusBarNotification old) {
        if (old != null && !hasActiveNotifications() && !this.mNotificationPanel.isTracking() && !this.mNotificationPanel.isQsExpanded()) {
            if (this.mStatusBarStateController.getState() == 0) {
                this.mCommandQueue.animateCollapsePanels();
            } else if (this.mStatusBarStateController.getState() == 2 && !isCollapsing()) {
                this.mShadeController.goToKeyguard();
            }
        }
        this.mShadeController.updateAreThereNotifications();
    }

    public boolean hasActiveNotifications() {
        return !this.mEntryManager.getNotificationData().getActiveNotifications().isEmpty();
    }

    public boolean canHeadsUp(NotificationEntry entry, StatusBarNotification sbn) {
        if (this.mShadeController.isOccluded()) {
            NotificationLockscreenUserManager notificationLockscreenUserManager = this.mLockscreenUserManager;
            boolean devicePublic = notificationLockscreenUserManager.isLockscreenPublicMode(notificationLockscreenUserManager.getCurrentUserId());
            boolean userPublic = devicePublic || this.mLockscreenUserManager.isLockscreenPublicMode(sbn.getUserId());
            boolean needsRedaction = this.mLockscreenUserManager.needsRedaction(entry);
            if (userPublic && needsRedaction) {
                return false;
            }
        }
        if (this.mCommandQueue.panelsEnabled()) {
            if (sbn.getNotification().fullScreenIntent != null) {
                if (this.mAccessibilityManager.isTouchExplorationEnabled()) {
                    return false;
                }
                return !this.mKeyguardMonitor.isShowing() || this.mShadeController.isOccluded();
            }
            return true;
        }
        return false;
    }

    @Override // com.android.systemui.statusbar.NotificationPresenter
    public void onUserSwitched(int newUserId) {
        this.mHeadsUpManager.setUser(newUserId);
        this.mCommandQueue.animateCollapsePanels();
        if (this.mReinflateNotificationsOnUserSwitched) {
            updateNotificationsOnDensityOrFontScaleChanged();
            this.mReinflateNotificationsOnUserSwitched = false;
        }
        if (this.mDispatchUiModeChangeOnUserSwitched) {
            updateNotificationOnUiModeChanged();
            this.mDispatchUiModeChangeOnUserSwitched = false;
        }
        updateNotificationViews();
        this.mMediaManager.clearCurrentMediaNotification();
        this.mShadeController.setLockscreenUser(newUserId);
        updateMediaMetaData(true, false);
    }

    @Override // com.android.systemui.statusbar.notification.collection.NotificationRowBinderImpl.BindRowCallback
    public void onBindRow(NotificationEntry entry, PackageManager pmUser, StatusBarNotification sbn, ExpandableNotificationRow row) {
        row.setAboveShelfChangedListener(this.mAboveShelfObserver);
        final UnlockMethodCache unlockMethodCache = this.mUnlockMethodCache;
        Objects.requireNonNull(unlockMethodCache);
        row.setSecureStateProvider(new BooleanSupplier() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$xngHwpynxyZFSiQxF9iZw-c24-s
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                return UnlockMethodCache.this.canSkipBouncer();
            }
        });
    }

    @Override // com.android.systemui.statusbar.NotificationPresenter
    public boolean isPresenterFullyCollapsed() {
        return this.mNotificationPanel.isFullyCollapsed();
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView.OnActivatedListener
    public void onActivated(ActivatableNotificationView view) {
        onActivated();
        if (view != null) {
            this.mNotificationPanel.setActivatedChild(view);
        }
    }

    public void onActivated() {
        this.mLockscreenGestureLogger.write(192, 0, 0);
        this.mNotificationPanel.showTransientIndication(R.string.notification_tap_again);
        ActivatableNotificationView previousView = this.mNotificationPanel.getActivatedChild();
        if (previousView != null) {
            previousView.makeInactive(true);
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView.OnActivatedListener
    public void onActivationReset(ActivatableNotificationView view) {
        if (view == this.mNotificationPanel.getActivatedChild()) {
            this.mNotificationPanel.setActivatedChild(null);
            this.mShadeController.onActivationReset();
        }
    }

    @Override // com.android.systemui.statusbar.NotificationPresenter
    public void updateMediaMetaData(boolean metaDataChanged, boolean allowEnterAnimation) {
        this.mMediaManager.updateMediaMetaData(metaDataChanged, allowEnterAnimation);
    }

    @Override // com.android.systemui.statusbar.NotificationPresenter
    public int getMaxNotificationsWhileLocked(boolean recompute) {
        if (recompute) {
            this.mMaxKeyguardNotifications = Math.max(1, this.mNotificationPanel.computeMaxKeyguardNotifications(this.mMaxAllowedKeyguardNotifications));
            return this.mMaxKeyguardNotifications;
        }
        return this.mMaxKeyguardNotifications;
    }

    @Override // com.android.systemui.statusbar.NotificationPresenter
    public void onUpdateRowStates() {
        this.mNotificationPanel.onUpdateRowStates();
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableNotificationRow.OnExpandClickListener
    public void onExpandClicked(NotificationEntry clickedEntry, boolean nowExpanded) {
        this.mHeadsUpManager.setExpanded(clickedEntry, nowExpanded);
        if (nowExpanded) {
            if (this.mStatusBarStateController.getState() == 1) {
                this.mShadeController.goToLockedShade(clickedEntry.getRow());
            } else if (clickedEntry.isSensitive() && this.mDynamicPrivacyController.isInLockedDownShade()) {
                this.mStatusBarStateController.setLeaveOpenOnKeyguardHide(true);
                this.mActivityStarter.dismissKeyguardThenExecute(new ActivityStarter.OnDismissAction() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBarNotificationPresenter$kVrBvDo577RHxcwdetzp8ypANEY
                    @Override // com.android.systemui.plugins.ActivityStarter.OnDismissAction
                    public final boolean onDismiss() {
                        return StatusBarNotificationPresenter.lambda$onExpandClicked$1();
                    }
                }, null, false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$onExpandClicked$1() {
        return false;
    }

    @Override // com.android.systemui.statusbar.NotificationPresenter
    public boolean isDeviceInVrMode() {
        return this.mVrMode;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onLockedNotificationImportanceChange(ActivityStarter.OnDismissAction dismissAction) {
        this.mStatusBarStateController.setLeaveOpenOnKeyguardHide(true);
        this.mActivityStarter.dismissKeyguardThenExecute(dismissAction, null, true);
    }

    /* renamed from: com.android.systemui.statusbar.phone.StatusBarNotificationPresenter$3  reason: invalid class name */
    /* loaded from: classes21.dex */
    class AnonymousClass3 implements NotificationInfo.CheckSaveListener {
        AnonymousClass3() {
        }

        @Override // com.android.systemui.statusbar.notification.row.NotificationInfo.CheckSaveListener
        public void checkSave(final Runnable saveImportance, StatusBarNotification sbn) {
            if (StatusBarNotificationPresenter.this.mLockscreenUserManager.isLockscreenPublicMode(sbn.getUser().getIdentifier()) && StatusBarNotificationPresenter.this.mKeyguardManager.isKeyguardLocked()) {
                StatusBarNotificationPresenter.this.onLockedNotificationImportanceChange(new ActivityStarter.OnDismissAction() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBarNotificationPresenter$3$840XxWS_RWDahgeIBbiGSPlNZGs
                    @Override // com.android.systemui.plugins.ActivityStarter.OnDismissAction
                    public final boolean onDismiss() {
                        return StatusBarNotificationPresenter.AnonymousClass3.lambda$checkSave$0(saveImportance);
                    }
                });
            } else {
                saveImportance.run();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ boolean lambda$checkSave$0(Runnable saveImportance) {
            saveImportance.run();
            return true;
        }
    }
}
