package com.android.systemui.statusbar.phone;

import android.app.ActivityTaskManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.dreams.IDreamManager;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import android.view.RemoteAnimationAdapter;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.ActivityIntentHelper;
import com.android.systemui.Dependency;
import com.android.systemui.EventLogTags;
import com.android.systemui.UiOffloadThread;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.notification.ActivityLaunchAnimator;
import com.android.systemui.statusbar.notification.NotificationActivityStarter;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.NotificationInterruptionStateProvider;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import java.util.Objects;
/* loaded from: classes21.dex */
public class StatusBarNotificationActivityStarter implements NotificationActivityStarter {
    private final ActivityIntentHelper mActivityIntentHelper;
    private final ActivityLaunchAnimator mActivityLaunchAnimator;
    private final ActivityStarter mActivityStarter;
    private final AssistManager mAssistManager;
    private final Handler mBackgroundHandler;
    private final IStatusBarService mBarService;
    private final BubbleController mBubbleController;
    private final CommandQueue mCommandQueue;
    private final Context mContext;
    private final IDreamManager mDreamManager;
    private final NotificationEntryManager mEntryManager;
    private final NotificationGroupManager mGroupManager;
    private final HeadsUpManagerPhone mHeadsUpManager;
    private boolean mIsCollapsingToShowActivityOverLockscreen;
    private final KeyguardManager mKeyguardManager;
    private final KeyguardMonitor mKeyguardMonitor;
    private final LockPatternUtils mLockPatternUtils;
    private final NotificationLockscreenUserManager mLockscreenUserManager;
    private final Handler mMainThreadHandler;
    private final MetricsLogger mMetricsLogger;
    private final NotificationInterruptionStateProvider mNotificationInterruptionStateProvider;
    private final NotificationPanelView mNotificationPanel;
    private final NotificationPresenter mPresenter;
    private final NotificationRemoteInputManager mRemoteInputManager;
    private final ShadeController mShadeController;
    private final StatusBarRemoteInputCallback mStatusBarRemoteInputCallback;
    private final StatusBarStateController mStatusBarStateController;
    private static final String TAG = "NotificationClickHandler";
    protected static final boolean DEBUG = Log.isLoggable(TAG, 3);

    public StatusBarNotificationActivityStarter(Context context, CommandQueue commandQueue, AssistManager assistManager, NotificationPanelView panel, NotificationPresenter presenter, NotificationEntryManager entryManager, HeadsUpManagerPhone headsUpManager, ActivityStarter activityStarter, ActivityLaunchAnimator activityLaunchAnimator, IStatusBarService statusBarService, StatusBarStateController statusBarStateController, KeyguardManager keyguardManager, IDreamManager dreamManager, NotificationRemoteInputManager remoteInputManager, StatusBarRemoteInputCallback remoteInputCallback, NotificationGroupManager groupManager, NotificationLockscreenUserManager lockscreenUserManager, ShadeController shadeController, KeyguardMonitor keyguardMonitor, NotificationInterruptionStateProvider notificationInterruptionStateProvider, MetricsLogger metricsLogger, LockPatternUtils lockPatternUtils, Handler mainThreadHandler, Handler backgroundHandler, ActivityIntentHelper activityIntentHelper, BubbleController bubbleController) {
        this.mContext = context;
        this.mNotificationPanel = panel;
        this.mPresenter = presenter;
        this.mHeadsUpManager = headsUpManager;
        this.mActivityLaunchAnimator = activityLaunchAnimator;
        this.mBarService = statusBarService;
        this.mCommandQueue = commandQueue;
        this.mKeyguardManager = keyguardManager;
        this.mDreamManager = dreamManager;
        this.mRemoteInputManager = remoteInputManager;
        this.mLockscreenUserManager = lockscreenUserManager;
        this.mShadeController = shadeController;
        this.mKeyguardMonitor = keyguardMonitor;
        this.mActivityStarter = activityStarter;
        this.mEntryManager = entryManager;
        this.mStatusBarStateController = statusBarStateController;
        this.mNotificationInterruptionStateProvider = notificationInterruptionStateProvider;
        this.mMetricsLogger = metricsLogger;
        this.mAssistManager = assistManager;
        this.mGroupManager = groupManager;
        this.mLockPatternUtils = lockPatternUtils;
        this.mBackgroundHandler = backgroundHandler;
        this.mEntryManager.addNotificationEntryListener(new NotificationEntryListener() { // from class: com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarter.1
            @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
            public void onPendingEntryAdded(NotificationEntry entry) {
                StatusBarNotificationActivityStarter.this.handleFullScreenIntent(entry);
            }
        });
        this.mStatusBarRemoteInputCallback = remoteInputCallback;
        this.mMainThreadHandler = mainThreadHandler;
        this.mActivityIntentHelper = activityIntentHelper;
        this.mBubbleController = bubbleController;
    }

    @Override // com.android.systemui.statusbar.notification.NotificationActivityStarter
    public void onNotificationClicked(final StatusBarNotification sbn, final ExpandableNotificationRow row) {
        PendingIntent pendingIntent;
        final RemoteInputController controller = this.mRemoteInputManager.getController();
        if (controller.isRemoteInputActive(row.getEntry()) && !TextUtils.isEmpty(row.getActiveRemoteInputText())) {
            controller.closeRemoteInputs();
            return;
        }
        Notification notification = sbn.getNotification();
        if (notification.contentIntent != null) {
            pendingIntent = notification.contentIntent;
        } else {
            pendingIntent = notification.fullScreenIntent;
        }
        final PendingIntent intent = pendingIntent;
        boolean isBubble = row.getEntry().isBubble();
        if (intent == null && !isBubble) {
            Log.e(TAG, "onNotificationClicked called for non-clickable notification!");
            return;
        }
        final String notificationKey = sbn.getKey();
        boolean z = false;
        final boolean isActivityIntent = (intent == null || !intent.isActivity() || isBubble) ? false : true;
        boolean afterKeyguardGone = isActivityIntent && this.mActivityIntentHelper.wouldLaunchResolverActivity(intent.getIntent(), this.mLockscreenUserManager.getCurrentUserId());
        final boolean wasOccluded = this.mShadeController.isOccluded();
        if (this.mKeyguardMonitor.isShowing() && intent != null && this.mActivityIntentHelper.wouldShowOverLockscreen(intent.getIntent(), this.mLockscreenUserManager.getCurrentUserId())) {
            z = true;
        }
        final boolean showOverLockscreen = z;
        ActivityStarter.OnDismissAction postKeyguardAction = new ActivityStarter.OnDismissAction() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBarNotificationActivityStarter$yPRbQ0J1oyZW5IHADurUixbhRxg
            @Override // com.android.systemui.plugins.ActivityStarter.OnDismissAction
            public final boolean onDismiss() {
                return StatusBarNotificationActivityStarter.this.lambda$onNotificationClicked$0$StatusBarNotificationActivityStarter(sbn, row, controller, intent, notificationKey, isActivityIntent, wasOccluded, showOverLockscreen);
            }
        };
        if (showOverLockscreen) {
            this.mIsCollapsingToShowActivityOverLockscreen = true;
            postKeyguardAction.onDismiss();
            return;
        }
        this.mActivityStarter.dismissKeyguardThenExecute(postKeyguardAction, null, afterKeyguardGone);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:23:0x006d  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0079  */
    /* renamed from: handleNotificationClickAfterKeyguardDismissed */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean lambda$onNotificationClicked$0$StatusBarNotificationActivityStarter(final android.service.notification.StatusBarNotification r17, final com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r18, final com.android.systemui.statusbar.RemoteInputController r19, final android.app.PendingIntent r20, final java.lang.String r21, final boolean r22, final boolean r23, boolean r24) {
        /*
            r16 = this;
            r10 = r16
            r11 = r17
            com.android.systemui.statusbar.phone.HeadsUpManagerPhone r0 = r10.mHeadsUpManager
            r12 = 1
            if (r0 == 0) goto L2e
            r13 = r21
            boolean r0 = r0.isAlerting(r13)
            if (r0 == 0) goto L2b
            com.android.systemui.statusbar.NotificationPresenter r0 = r10.mPresenter
            boolean r0 = r0.isPresenterFullyCollapsed()
            if (r0 == 0) goto L1f
            r14 = r18
            com.android.systemui.statusbar.policy.HeadsUpUtil.setIsClickedHeadsUpNotification(r14, r12)
            goto L21
        L1f:
            r14 = r18
        L21:
            com.android.systemui.statusbar.phone.HeadsUpManagerPhone r0 = r10.mHeadsUpManager
            java.lang.String r1 = r17.getKey()
            r0.removeNotification(r1, r12)
            goto L32
        L2b:
            r14 = r18
            goto L32
        L2e:
            r14 = r18
            r13 = r21
        L32:
            r0 = 0
            boolean r1 = shouldAutoCancel(r17)
            if (r1 == 0) goto L52
            com.android.systemui.statusbar.phone.NotificationGroupManager r1 = r10.mGroupManager
            boolean r1 = r1.isOnlyChildInGroup(r11)
            if (r1 == 0) goto L52
            com.android.systemui.statusbar.phone.NotificationGroupManager r1 = r10.mGroupManager
            com.android.systemui.statusbar.notification.collection.NotificationEntry r1 = r1.getLogicalGroupSummary(r11)
            android.service.notification.StatusBarNotification r1 = r1.notification
            boolean r2 = shouldAutoCancel(r1)
            if (r2 == 0) goto L52
            r0 = r1
            r15 = r0
            goto L53
        L52:
            r15 = r0
        L53:
            r9 = r15
            com.android.systemui.statusbar.phone.-$$Lambda$StatusBarNotificationActivityStarter$H4WWHLQEcsZwa09U0GneoOwngZE r8 = new com.android.systemui.statusbar.phone.-$$Lambda$StatusBarNotificationActivityStarter$H4WWHLQEcsZwa09U0GneoOwngZE
            r0 = r8
            r1 = r16
            r2 = r17
            r3 = r18
            r4 = r19
            r5 = r20
            r6 = r21
            r7 = r22
            r12 = r8
            r8 = r23
            r0.<init>()
            if (r24 == 0) goto L79
            com.android.systemui.statusbar.phone.ShadeController r0 = r10.mShadeController
            r0.addPostCollapseAction(r12)
            com.android.systemui.statusbar.phone.ShadeController r0 = r10.mShadeController
            r1 = 1
            r0.collapsePanel(r1)
            goto L99
        L79:
            com.android.systemui.statusbar.policy.KeyguardMonitor r0 = r10.mKeyguardMonitor
            boolean r0 = r0.isShowing()
            if (r0 == 0) goto L94
            com.android.systemui.statusbar.phone.ShadeController r0 = r10.mShadeController
            boolean r0 = r0.isOccluded()
            if (r0 == 0) goto L94
            com.android.systemui.statusbar.phone.ShadeController r0 = r10.mShadeController
            r0.addAfterKeyguardGoneRunnable(r12)
            com.android.systemui.statusbar.phone.ShadeController r0 = r10.mShadeController
            r0.collapsePanel()
            goto L99
        L94:
            android.os.Handler r0 = r10.mBackgroundHandler
            r0.postAtFrontOfQueue(r12)
        L99:
            com.android.systemui.statusbar.phone.NotificationPanelView r0 = r10.mNotificationPanel
            boolean r0 = r0.isFullyCollapsed()
            r1 = 1
            r0 = r0 ^ r1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarter.lambda$onNotificationClicked$0$StatusBarNotificationActivityStarter(android.service.notification.StatusBarNotification, com.android.systemui.statusbar.notification.row.ExpandableNotificationRow, com.android.systemui.statusbar.RemoteInputController, android.app.PendingIntent, java.lang.String, boolean, boolean, boolean):boolean");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Can't wrap try/catch for region: R(20:1|2|3|(2:5|(2:11|12))|14|(1:16)(1:48)|17|(11:21|(1:23)(1:45)|(1:44)|26|(1:28)|29|30|31|(3:(1:34)|35|(1:39))|40|41)|46|(0)(0)|(0)|44|26|(0)|29|30|31|(0)|40|41) */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0075  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0079  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x0095  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x00cc  */
    /* renamed from: handleNotificationClickAfterPanelCollapsed */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void lambda$handleNotificationClickAfterKeyguardDismissed$1$StatusBarNotificationActivityStarter(android.service.notification.StatusBarNotification r16, com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r17, com.android.systemui.statusbar.RemoteInputController r18, android.app.PendingIntent r19, java.lang.String r20, boolean r21, boolean r22, android.service.notification.StatusBarNotification r23) {
        /*
            r15 = this;
            r7 = r15
            r8 = r20
            r9 = r23
            android.app.IActivityManager r0 = android.app.ActivityManager.getService()     // Catch: android.os.RemoteException -> Ld
            r0.resumeAppSwitches()     // Catch: android.os.RemoteException -> Ld
            goto Le
        Ld:
            r0 = move-exception
        Le:
            if (r21 == 0) goto L38
            android.os.UserHandle r0 = r19.getCreatorUserHandle()
            int r0 = r0.getIdentifier()
            com.android.internal.widget.LockPatternUtils r1 = r7.mLockPatternUtils
            boolean r1 = r1.isSeparateProfileChallengeEnabled(r0)
            if (r1 == 0) goto L38
            android.app.KeyguardManager r1 = r7.mKeyguardManager
            boolean r1 = r1.isDeviceLocked(r0)
            if (r1 == 0) goto L38
            com.android.systemui.statusbar.phone.StatusBarRemoteInputCallback r1 = r7.mStatusBarRemoteInputCallback
            android.content.IntentSender r2 = r19.getIntentSender()
            boolean r1 = r1.startWorkChallengeIfNecessary(r0, r2, r8)
            if (r1 == 0) goto L38
            r15.collapseOnMainThread()
            return
        L38:
            r0 = 0
            com.android.systemui.statusbar.notification.collection.NotificationEntry r10 = r17.getEntry()
            boolean r11 = r10.isBubble()
            r1 = 0
            java.lang.CharSequence r2 = r10.remoteInputText
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L4e
            java.lang.CharSequence r1 = r10.remoteInputText
            r12 = r1
            goto L4f
        L4e:
            r12 = r1
        L4f:
            boolean r1 = android.text.TextUtils.isEmpty(r12)
            if (r1 != 0) goto L70
            java.lang.String r1 = r10.key
            r13 = r18
            boolean r1 = r13.isSpinning(r1)
            if (r1 != 0) goto L72
            android.content.Intent r1 = new android.content.Intent
            r1.<init>()
            java.lang.String r2 = r12.toString()
            java.lang.String r3 = "android.remoteInputDraft"
            android.content.Intent r0 = r1.putExtra(r3, r2)
            r14 = r0
            goto L73
        L70:
            r13 = r18
        L72:
            r14 = r0
        L73:
            if (r11 == 0) goto L79
            r15.expandBubbleStackOnMainThread(r8)
            goto L86
        L79:
            r1 = r15
            r2 = r19
            r3 = r14
            r4 = r17
            r5 = r22
            r6 = r21
            r1.startNotificationIntent(r2, r3, r4, r5, r6)
        L86:
            if (r21 != 0) goto L8a
            if (r11 == 0) goto L8f
        L8a:
            com.android.systemui.assist.AssistManager r0 = r7.mAssistManager
            r0.hideAssist()
        L8f:
            boolean r0 = r15.shouldCollapse()
            if (r0 == 0) goto L98
            r15.collapseOnMainThread()
        L98:
            com.android.systemui.statusbar.notification.NotificationEntryManager r0 = r7.mEntryManager
            com.android.systemui.statusbar.notification.collection.NotificationData r0 = r0.getNotificationData()
            java.util.ArrayList r0 = r0.getActiveNotifications()
            int r1 = r0.size()
            com.android.systemui.statusbar.notification.NotificationEntryManager r0 = r7.mEntryManager
            com.android.systemui.statusbar.notification.collection.NotificationData r0 = r0.getNotificationData()
            int r2 = r0.getRank(r8)
            com.android.systemui.statusbar.notification.NotificationEntryManager r0 = r7.mEntryManager
            com.android.systemui.statusbar.notification.collection.NotificationData r0 = r0.getNotificationData()
            com.android.systemui.statusbar.notification.collection.NotificationEntry r0 = r0.get(r8)
            com.android.internal.statusbar.NotificationVisibility$NotificationLocation r3 = com.android.systemui.statusbar.notification.logging.NotificationLogger.getNotificationLocation(r0)
            r0 = 1
            com.android.internal.statusbar.NotificationVisibility r4 = com.android.internal.statusbar.NotificationVisibility.obtain(r8, r2, r1, r0, r3)
            com.android.internal.statusbar.IStatusBarService r0 = r7.mBarService     // Catch: android.os.RemoteException -> Lc9
            r0.onNotificationClick(r8, r4)     // Catch: android.os.RemoteException -> Lc9
            goto Lca
        Lc9:
            r0 = move-exception
        Lca:
            if (r11 != 0) goto Le2
            if (r9 == 0) goto Ld1
            r15.removeNotification(r9)
        Ld1:
            boolean r0 = shouldAutoCancel(r16)
            if (r0 != 0) goto Ldf
            com.android.systemui.statusbar.NotificationRemoteInputManager r0 = r7.mRemoteInputManager
            boolean r0 = r0.isNotificationKeptForRemoteInputHistory(r8)
            if (r0 == 0) goto Le2
        Ldf:
            r15.removeNotification(r16)
        Le2:
            r0 = 0
            r7.mIsCollapsingToShowActivityOverLockscreen = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarter.lambda$handleNotificationClickAfterKeyguardDismissed$1$StatusBarNotificationActivityStarter(android.service.notification.StatusBarNotification, com.android.systemui.statusbar.notification.row.ExpandableNotificationRow, com.android.systemui.statusbar.RemoteInputController, android.app.PendingIntent, java.lang.String, boolean, boolean, android.service.notification.StatusBarNotification):void");
    }

    private void expandBubbleStackOnMainThread(final String notificationKey) {
        if (Looper.getMainLooper().isCurrentThread()) {
            this.mBubbleController.expandStackAndSelectBubble(notificationKey);
        } else {
            this.mMainThreadHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBarNotificationActivityStarter$SAG_ctHvOhll_OxtSg-OBbXZGGw
                @Override // java.lang.Runnable
                public final void run() {
                    StatusBarNotificationActivityStarter.this.lambda$expandBubbleStackOnMainThread$2$StatusBarNotificationActivityStarter(notificationKey);
                }
            });
        }
    }

    public /* synthetic */ void lambda$expandBubbleStackOnMainThread$2$StatusBarNotificationActivityStarter(String notificationKey) {
        this.mBubbleController.expandStackAndSelectBubble(notificationKey);
    }

    private void startNotificationIntent(PendingIntent intent, Intent fillInIntent, ExpandableNotificationRow row, boolean wasOccluded, boolean isActivityIntent) {
        RemoteAnimationAdapter adapter = this.mActivityLaunchAnimator.getLaunchAnimation(row, wasOccluded);
        if (adapter != null) {
            try {
                ActivityTaskManager.getService().registerRemoteAnimationForNextActivityStart(intent.getCreatorPackage(), adapter);
            } catch (PendingIntent.CanceledException | RemoteException e) {
                Log.w(TAG, "Sending contentIntent failed: " + e);
                return;
            }
        }
        int launchResult = intent.sendAndReturnResult(this.mContext, 0, fillInIntent, null, null, null, StatusBar.getActivityOptions(adapter));
        this.mActivityLaunchAnimator.setLaunchResult(launchResult, isActivityIntent);
    }

    @Override // com.android.systemui.statusbar.notification.NotificationActivityStarter
    public void startNotificationGutsIntent(final Intent intent, final int appUid, final ExpandableNotificationRow row) {
        this.mActivityStarter.dismissKeyguardThenExecute(new ActivityStarter.OnDismissAction() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBarNotificationActivityStarter$cyhnCXwOFANppGr5Crfg0gR112k
            @Override // com.android.systemui.plugins.ActivityStarter.OnDismissAction
            public final boolean onDismiss() {
                return StatusBarNotificationActivityStarter.this.lambda$startNotificationGutsIntent$5$StatusBarNotificationActivityStarter(intent, row, appUid);
            }
        }, null, false);
    }

    public /* synthetic */ boolean lambda$startNotificationGutsIntent$5$StatusBarNotificationActivityStarter(final Intent intent, final ExpandableNotificationRow row, final int appUid) {
        AsyncTask.execute(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBarNotificationActivityStarter$SrsXjl_aP_YXf0BoPG0DcKfnIqA
            @Override // java.lang.Runnable
            public final void run() {
                StatusBarNotificationActivityStarter.this.lambda$startNotificationGutsIntent$4$StatusBarNotificationActivityStarter(intent, row, appUid);
            }
        });
        return true;
    }

    public /* synthetic */ void lambda$startNotificationGutsIntent$4$StatusBarNotificationActivityStarter(Intent intent, ExpandableNotificationRow row, int appUid) {
        int launchResult = TaskStackBuilder.create(this.mContext).addNextIntentWithParentStack(intent).startActivities(StatusBar.getActivityOptions(this.mActivityLaunchAnimator.getLaunchAnimation(row, this.mShadeController.isOccluded())), new UserHandle(UserHandle.getUserId(appUid)));
        this.mActivityLaunchAnimator.setLaunchResult(launchResult, true);
        if (shouldCollapse()) {
            this.mMainThreadHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBarNotificationActivityStarter$ZBMwRNC8tX8dffchdtumyW_afiA
                @Override // java.lang.Runnable
                public final void run() {
                    StatusBarNotificationActivityStarter.this.lambda$startNotificationGutsIntent$3$StatusBarNotificationActivityStarter();
                }
            });
        }
    }

    public /* synthetic */ void lambda$startNotificationGutsIntent$3$StatusBarNotificationActivityStarter() {
        this.mCommandQueue.animateCollapsePanels(2, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFullScreenIntent(NotificationEntry entry) {
        if (this.mNotificationInterruptionStateProvider.shouldLaunchFullScreenIntentWhenAdded(entry)) {
            if (shouldSuppressFullScreenIntent(entry)) {
                if (DEBUG) {
                    Log.d(TAG, "No Fullscreen intent: suppressed by DND: " + entry.key);
                }
            } else if (entry.importance < 4) {
                if (DEBUG) {
                    Log.d(TAG, "No Fullscreen intent: not important enough: " + entry.key);
                }
            } else {
                ((UiOffloadThread) Dependency.get(UiOffloadThread.class)).submit(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBarNotificationActivityStarter$r9RsnGtfcZuVJem-yK82SlR0x7o
                    @Override // java.lang.Runnable
                    public final void run() {
                        StatusBarNotificationActivityStarter.this.lambda$handleFullScreenIntent$6$StatusBarNotificationActivityStarter();
                    }
                });
                if (DEBUG) {
                    Log.d(TAG, "Notification has fullScreenIntent; sending fullScreenIntent");
                }
                try {
                    EventLog.writeEvent((int) EventLogTags.SYSUI_FULLSCREEN_NOTIFICATION, entry.key);
                    entry.notification.getNotification().fullScreenIntent.send();
                    entry.notifyFullScreenIntentLaunched();
                    this.mMetricsLogger.count("note_fullscreen", 1);
                } catch (PendingIntent.CanceledException e) {
                }
            }
        }
    }

    public /* synthetic */ void lambda$handleFullScreenIntent$6$StatusBarNotificationActivityStarter() {
        try {
            this.mDreamManager.awaken();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override // com.android.systemui.statusbar.notification.NotificationActivityStarter
    public boolean isCollapsingToShowActivityOverLockscreen() {
        return this.mIsCollapsingToShowActivityOverLockscreen;
    }

    private static boolean shouldAutoCancel(StatusBarNotification sbn) {
        int flags = sbn.getNotification().flags;
        return (flags & 16) == 16 && (flags & 64) == 0;
    }

    private void collapseOnMainThread() {
        if (Looper.getMainLooper().isCurrentThread()) {
            this.mShadeController.collapsePanel();
            return;
        }
        Handler handler = this.mMainThreadHandler;
        final ShadeController shadeController = this.mShadeController;
        Objects.requireNonNull(shadeController);
        handler.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$XDmf1V0qHGBRkx-V63RRNIpOXuQ
            @Override // java.lang.Runnable
            public final void run() {
                ShadeController.this.collapsePanel();
            }
        });
    }

    private boolean shouldCollapse() {
        return (this.mStatusBarStateController.getState() == 0 && this.mActivityLaunchAnimator.isAnimationPending()) ? false : true;
    }

    private boolean shouldSuppressFullScreenIntent(NotificationEntry entry) {
        if (this.mPresenter.isDeviceInVrMode()) {
            return true;
        }
        return entry.shouldSuppressFullScreenIntent();
    }

    private void removeNotification(final StatusBarNotification notification) {
        this.mMainThreadHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBarNotificationActivityStarter$SjtS704WtC3nfx3PLxg0F_Agha4
            @Override // java.lang.Runnable
            public final void run() {
                StatusBarNotificationActivityStarter.this.lambda$removeNotification$8$StatusBarNotificationActivityStarter(notification);
            }
        });
    }

    public /* synthetic */ void lambda$removeNotification$8$StatusBarNotificationActivityStarter(final StatusBarNotification notification) {
        Runnable removeRunnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBarNotificationActivityStarter$66g6EDAhWCxfzMKE6qPXX_8qGwI
            @Override // java.lang.Runnable
            public final void run() {
                StatusBarNotificationActivityStarter.this.lambda$removeNotification$7$StatusBarNotificationActivityStarter(notification);
            }
        };
        if (this.mPresenter.isCollapsing()) {
            this.mShadeController.addPostCollapseAction(removeRunnable);
        } else {
            removeRunnable.run();
        }
    }

    public /* synthetic */ void lambda$removeNotification$7$StatusBarNotificationActivityStarter(StatusBarNotification notification) {
        this.mEntryManager.performRemoveNotification(notification, 1);
    }
}
