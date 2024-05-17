package com.android.systemui.statusbar.phone;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.view.View;
import android.view.ViewParent;
import com.android.systemui.ActivityIntentHelper;
import com.android.systemui.Dependency;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.NotificationContentView;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class StatusBarRemoteInputCallback implements NotificationRemoteInputManager.Callback, CommandQueue.Callbacks, StatusBarStateController.StateListener {
    private final ActivityIntentHelper mActivityIntentHelper;
    private final CommandQueue mCommandQueue;
    private final Context mContext;
    private int mDisabled2;
    private final NotificationGroupManager mGroupManager;
    private KeyguardManager mKeyguardManager;
    private View mPendingRemoteInputView;
    private View mPendingWorkRemoteInputView;
    private final KeyguardMonitor mKeyguardMonitor = (KeyguardMonitor) Dependency.get(KeyguardMonitor.class);
    private final SysuiStatusBarStateController mStatusBarStateController = (SysuiStatusBarStateController) Dependency.get(StatusBarStateController.class);
    private final NotificationLockscreenUserManager mLockscreenUserManager = (NotificationLockscreenUserManager) Dependency.get(NotificationLockscreenUserManager.class);
    private final ActivityStarter mActivityStarter = (ActivityStarter) Dependency.get(ActivityStarter.class);
    private final ShadeController mShadeController = (ShadeController) Dependency.get(ShadeController.class);
    protected BroadcastReceiver mChallengeReceiver = new ChallengeReceiver();
    private Handler mMainHandler = new Handler();

    @Inject
    public StatusBarRemoteInputCallback(Context context, NotificationGroupManager groupManager) {
        this.mContext = context;
        this.mContext.registerReceiverAsUser(this.mChallengeReceiver, UserHandle.ALL, new IntentFilter("android.intent.action.DEVICE_LOCKED_CHANGED"), null, null);
        this.mStatusBarStateController.addCallback(this);
        this.mKeyguardManager = (KeyguardManager) context.getSystemService(KeyguardManager.class);
        this.mCommandQueue = (CommandQueue) SysUiServiceProvider.getComponent(context, CommandQueue.class);
        this.mCommandQueue.addCallback((CommandQueue.Callbacks) this);
        this.mActivityIntentHelper = new ActivityIntentHelper(this.mContext);
        this.mGroupManager = groupManager;
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onStateChanged(int state) {
        boolean hasPendingRemoteInput = this.mPendingRemoteInputView != null;
        if (state == 0) {
            if ((this.mStatusBarStateController.leaveOpenOnKeyguardHide() || hasPendingRemoteInput) && !this.mStatusBarStateController.isKeyguardRequested()) {
                if (hasPendingRemoteInput) {
                    Handler handler = this.mMainHandler;
                    final View view = this.mPendingRemoteInputView;
                    Objects.requireNonNull(view);
                    handler.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$au9TYywfgPbmO65RQz_jg3-3Qz0
                        @Override // java.lang.Runnable
                        public final void run() {
                            view.callOnClick();
                        }
                    });
                }
                this.mPendingRemoteInputView = null;
            }
        }
    }

    @Override // com.android.systemui.statusbar.NotificationRemoteInputManager.Callback
    public void onLockedRemoteInput(ExpandableNotificationRow row, View clicked) {
        if (!row.isPinned()) {
            this.mStatusBarStateController.setLeaveOpenOnKeyguardHide(true);
        }
        this.mShadeController.showBouncer(true);
        this.mPendingRemoteInputView = clicked;
    }

    protected void onWorkChallengeChanged() {
        this.mLockscreenUserManager.updatePublicMode();
        if (this.mPendingWorkRemoteInputView != null && !this.mLockscreenUserManager.isAnyProfilePublicMode()) {
            Runnable clickPendingViewRunnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBarRemoteInputCallback$R1k7Wh1xlx-jAMn9HjU1lr6mXXE
                @Override // java.lang.Runnable
                public final void run() {
                    StatusBarRemoteInputCallback.this.lambda$onWorkChallengeChanged$2$StatusBarRemoteInputCallback();
                }
            };
            this.mShadeController.postOnShadeExpanded(clickPendingViewRunnable);
            this.mShadeController.instantExpandNotificationsPanel();
        }
    }

    public /* synthetic */ void lambda$onWorkChallengeChanged$2$StatusBarRemoteInputCallback() {
        View pendingWorkRemoteInputView = this.mPendingWorkRemoteInputView;
        if (pendingWorkRemoteInputView == null) {
            return;
        }
        ViewParent p = pendingWorkRemoteInputView.getParent();
        while (!(p instanceof ExpandableNotificationRow)) {
            if (p == null) {
                return;
            }
            p = p.getParent();
        }
        final ExpandableNotificationRow row = (ExpandableNotificationRow) p;
        ViewParent viewParent = row.getParent();
        if (viewParent instanceof NotificationStackScrollLayout) {
            final NotificationStackScrollLayout scrollLayout = (NotificationStackScrollLayout) viewParent;
            row.makeActionsVisibile();
            row.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBarRemoteInputCallback$L_R5DgtrNavZQt2DnmfrB_93PMA
                @Override // java.lang.Runnable
                public final void run() {
                    StatusBarRemoteInputCallback.this.lambda$onWorkChallengeChanged$1$StatusBarRemoteInputCallback(scrollLayout, row);
                }
            });
        }
    }

    public /* synthetic */ void lambda$onWorkChallengeChanged$1$StatusBarRemoteInputCallback(final NotificationStackScrollLayout scrollLayout, ExpandableNotificationRow row) {
        Runnable finishScrollingCallback = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBarRemoteInputCallback$Pf9b4xR3WdydZqpSHpd3WHttUBw
            @Override // java.lang.Runnable
            public final void run() {
                StatusBarRemoteInputCallback.this.lambda$onWorkChallengeChanged$0$StatusBarRemoteInputCallback(scrollLayout);
            }
        };
        if (scrollLayout.scrollTo(row)) {
            scrollLayout.setFinishScrollingCallback(finishScrollingCallback);
        } else {
            finishScrollingCallback.run();
        }
    }

    public /* synthetic */ void lambda$onWorkChallengeChanged$0$StatusBarRemoteInputCallback(NotificationStackScrollLayout scrollLayout) {
        this.mPendingWorkRemoteInputView.callOnClick();
        this.mPendingWorkRemoteInputView = null;
        scrollLayout.setFinishScrollingCallback(null);
    }

    @Override // com.android.systemui.statusbar.NotificationRemoteInputManager.Callback
    public void onMakeExpandedVisibleForRemoteInput(ExpandableNotificationRow row, final View clickedView) {
        if (this.mKeyguardMonitor.isShowing()) {
            onLockedRemoteInput(row, clickedView);
            return;
        }
        if (row.isChildInGroup() && !row.areChildrenExpanded()) {
            this.mGroupManager.toggleGroupExpansion(row.getStatusBarNotification());
        }
        row.setUserExpanded(true);
        NotificationContentView privateLayout = row.getPrivateLayout();
        Objects.requireNonNull(clickedView);
        privateLayout.setOnExpandedVisibleListener(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$MVkYf3B-uVxXy7rxrXvHR4SUXEU
            @Override // java.lang.Runnable
            public final void run() {
                clickedView.performClick();
            }
        });
    }

    @Override // com.android.systemui.statusbar.NotificationRemoteInputManager.Callback
    public void onLockedWorkRemoteInput(int userId, ExpandableNotificationRow row, View clicked) {
        this.mCommandQueue.animateCollapsePanels();
        startWorkChallengeIfNecessary(userId, null, null);
        this.mPendingWorkRemoteInputView = clicked;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean startWorkChallengeIfNecessary(int userId, IntentSender intendSender, String notificationKey) {
        this.mPendingWorkRemoteInputView = null;
        Intent newIntent = this.mKeyguardManager.createConfirmDeviceCredentialIntent(null, null, userId);
        if (newIntent == null) {
            return false;
        }
        Intent callBackIntent = new Intent(NotificationLockscreenUserManager.NOTIFICATION_UNLOCKED_BY_WORK_CHALLENGE_ACTION);
        callBackIntent.putExtra("android.intent.extra.INTENT", intendSender);
        callBackIntent.putExtra("android.intent.extra.INDEX", notificationKey);
        callBackIntent.setPackage(this.mContext.getPackageName());
        PendingIntent callBackPendingIntent = PendingIntent.getBroadcast(this.mContext, 0, callBackIntent, 1409286144);
        newIntent.putExtra("android.intent.extra.INTENT", callBackPendingIntent.getIntentSender());
        try {
            ActivityManager.getService().startConfirmDeviceCredentialIntent(newIntent, (Bundle) null);
            return true;
        } catch (RemoteException e) {
            return true;
        }
    }

    @Override // com.android.systemui.statusbar.NotificationRemoteInputManager.Callback
    public boolean shouldHandleRemoteInput(View view, PendingIntent pendingIntent) {
        return (this.mDisabled2 & 4) != 0;
    }

    @Override // com.android.systemui.statusbar.NotificationRemoteInputManager.Callback
    public boolean handleRemoteViewClick(View view, PendingIntent pendingIntent, final NotificationRemoteInputManager.ClickHandler defaultHandler) {
        boolean isActivity = pendingIntent.isActivity();
        if (isActivity) {
            boolean afterKeyguardGone = this.mActivityIntentHelper.wouldLaunchResolverActivity(pendingIntent.getIntent(), this.mLockscreenUserManager.getCurrentUserId());
            this.mActivityStarter.dismissKeyguardThenExecute(new ActivityStarter.OnDismissAction() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBarRemoteInputCallback$8d3SjU56C80S4rq-vR5b0crRuYY
                @Override // com.android.systemui.plugins.ActivityStarter.OnDismissAction
                public final boolean onDismiss() {
                    return StatusBarRemoteInputCallback.this.lambda$handleRemoteViewClick$3$StatusBarRemoteInputCallback(defaultHandler);
                }
            }, null, afterKeyguardGone);
            return true;
        }
        boolean afterKeyguardGone2 = defaultHandler.handleClick();
        return afterKeyguardGone2;
    }

    public /* synthetic */ boolean lambda$handleRemoteViewClick$3$StatusBarRemoteInputCallback(NotificationRemoteInputManager.ClickHandler defaultHandler) {
        try {
            ActivityManager.getService().resumeAppSwitches();
        } catch (RemoteException e) {
        }
        boolean handled = defaultHandler.handleClick();
        return handled && this.mShadeController.closeShadeIfOpen();
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void disable(int displayId, int state1, int state2, boolean animate) {
        if (displayId == this.mContext.getDisplayId()) {
            this.mDisabled2 = state2;
        }
    }

    /* loaded from: classes21.dex */
    protected class ChallengeReceiver extends BroadcastReceiver {
        protected ChallengeReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int userId = intent.getIntExtra("android.intent.extra.user_handle", -10000);
            if ("android.intent.action.DEVICE_LOCKED_CHANGED".equals(action) && userId != StatusBarRemoteInputCallback.this.mLockscreenUserManager.getCurrentUserId() && StatusBarRemoteInputCallback.this.mLockscreenUserManager.isCurrentProfile(userId)) {
                StatusBarRemoteInputCallback.this.onWorkChallengeChanged();
            }
        }
    }
}
