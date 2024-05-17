package com.android.systemui.statusbar.notification.row;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.INotificationManager;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.metrics.LogMaker;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.notification.logging.NotificationCounters;
import com.android.systemui.statusbar.notification.row.NotificationGuts;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Set;
/* loaded from: classes21.dex */
public class NotificationInfo extends LinearLayout implements NotificationGuts.GutsContent {
    private static final int ACTION_ALERT = 5;
    static final int ACTION_BLOCK = 3;
    static final int ACTION_DELIVER_SILENTLY = 4;
    public static final int ACTION_NONE = 0;
    static final int ACTION_TOGGLE_SILENT = 2;
    static final int ACTION_UNDO = 1;
    private static final int BEHAVIOR_ALERTING = 0;
    private static final int BEHAVIOR_SILENT = 1;
    private static final String TAG = "InfoGuts";
    private String mAppName;
    private OnAppSettingsClickListener mAppSettingsClickListener;
    private int mAppUid;
    private ChannelEditorDialogController mChannelEditorDialogController;
    private CheckSaveListener mCheckSaveListener;
    private Integer mChosenImportance;
    private String mDelegatePkg;
    private String mExitReason;
    private AnimatorSet mExpandAnimation;
    private NotificationGuts mGutsContainer;
    private INotificationManager mINotificationManager;
    private boolean mIsDeviceProvisioned;
    private boolean mIsForBlockingHelper;
    private boolean mIsNonblockable;
    private boolean mIsSingleDefaultChannel;
    private MetricsLogger mMetricsLogger;
    private int mNumUniqueChannelsInRow;
    private View.OnClickListener mOnAlert;
    private View.OnClickListener mOnDeliverSilently;
    private View.OnClickListener mOnDismissSettings;
    private View.OnClickListener mOnKeepShowing;
    private OnSettingsClickListener mOnSettingsClickListener;
    private View.OnClickListener mOnSilent;
    private View.OnClickListener mOnUndo;
    private String mPackageName;
    private Drawable mPkgIcon;
    private PackageManager mPm;
    private boolean mPresentingChannelEditorDialog;
    private boolean mPressedApply;
    private TextView mPriorityDescriptionView;
    private StatusBarNotification mSbn;
    private TextView mSilentDescriptionView;
    private NotificationChannel mSingleNotificationChannel;
    private int mStartingChannelImportance;
    private Set<NotificationChannel> mUniqueChannelsInRow;
    private VisualStabilityManager mVisualStabilityManager;
    private boolean mWasShownHighPriority;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes21.dex */
    private @interface AlertingBehavior {
    }

    /* loaded from: classes21.dex */
    public interface CheckSaveListener {
        void checkSave(Runnable runnable, StatusBarNotification statusBarNotification);
    }

    /* loaded from: classes21.dex */
    public @interface NotificationInfoAction {
    }

    /* loaded from: classes21.dex */
    public interface OnAppSettingsClickListener {
        void onClick(View view, Intent intent);
    }

    /* loaded from: classes21.dex */
    public interface OnSettingsClickListener {
        void onClick(View view, NotificationChannel notificationChannel, int i);
    }

    public /* synthetic */ void lambda$new$0$NotificationInfo(View v) {
        this.mExitReason = NotificationCounters.BLOCKING_HELPER_KEEP_SHOWING;
        this.mChosenImportance = 3;
        applyAlertingBehavior(0, true);
    }

    public /* synthetic */ void lambda$new$1$NotificationInfo(View v) {
        this.mExitReason = NotificationCounters.BLOCKING_HELPER_DELIVER_SILENTLY;
        this.mChosenImportance = 2;
        applyAlertingBehavior(1, true);
    }

    public /* synthetic */ void lambda$new$2$NotificationInfo(View v) {
        this.mPressedApply = true;
        closeControls(v, true);
    }

    public /* synthetic */ void lambda$new$3$NotificationInfo(View v) {
        this.mExitReason = NotificationCounters.BLOCKING_HELPER_KEEP_SHOWING;
        closeControls(v, true);
        this.mMetricsLogger.write(getLogMaker().setCategory(1621).setType(4).setSubtype(5));
    }

    public /* synthetic */ void lambda$new$4$NotificationInfo(View v) {
        handleSaveImportance(4, 5);
    }

    private void handleSaveImportance(final int action, final int metricsSubtype) {
        Runnable saveImportance = new Runnable() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$NotificationInfo$VsGw7yinvO7eM-lSnQkbAlXJJig
            @Override // java.lang.Runnable
            public final void run() {
                NotificationInfo.this.lambda$handleSaveImportance$5$NotificationInfo(action, metricsSubtype);
            }
        };
        CheckSaveListener checkSaveListener = this.mCheckSaveListener;
        if (checkSaveListener != null) {
            checkSaveListener.checkSave(saveImportance, this.mSbn);
        } else {
            saveImportance.run();
        }
    }

    public /* synthetic */ void lambda$handleSaveImportance$5$NotificationInfo(int action, int metricsSubtype) {
        saveImportanceAndExitReason(action);
        if (this.mIsForBlockingHelper) {
            swapContent(action, true);
            this.mMetricsLogger.write(getLogMaker().setCategory(1621).setType(4).setSubtype(metricsSubtype));
        }
    }

    public /* synthetic */ void lambda$new$6$NotificationInfo(View v) {
        this.mExitReason = NotificationCounters.BLOCKING_HELPER_DISMISSED;
        if (this.mIsForBlockingHelper) {
            logBlockingHelperCounter(NotificationCounters.BLOCKING_HELPER_UNDO);
            this.mMetricsLogger.write(getLogMaker().setCategory(1621).setType(5).setSubtype(7));
        } else {
            this.mMetricsLogger.write(importanceChangeLogMaker().setType(5));
        }
        saveImportanceAndExitReason(1);
        swapContent(1, true);
    }

    public NotificationInfo(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mPresentingChannelEditorDialog = false;
        this.mExitReason = NotificationCounters.BLOCKING_HELPER_DISMISSED;
        this.mOnAlert = new View.OnClickListener() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$NotificationInfo$-lxdNUTZhRsTq1qLdFuCftTaKsI
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                NotificationInfo.this.lambda$new$0$NotificationInfo(view);
            }
        };
        this.mOnSilent = new View.OnClickListener() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$NotificationInfo$x1Q8n0IIdzsrzqhyaxjftYvWg5M
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                NotificationInfo.this.lambda$new$1$NotificationInfo(view);
            }
        };
        this.mOnDismissSettings = new View.OnClickListener() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$NotificationInfo$p3qjyEUB89vA_NRs8XRVogtSM4k
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                NotificationInfo.this.lambda$new$2$NotificationInfo(view);
            }
        };
        this.mOnKeepShowing = new View.OnClickListener() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$NotificationInfo$j11VBERw7GgslLu77BlqWodFHxk
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                NotificationInfo.this.lambda$new$3$NotificationInfo(view);
            }
        };
        this.mOnDeliverSilently = new View.OnClickListener() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$NotificationInfo$QY1Am5dutJVQ0eHq5s0Z3dSQLu8
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                NotificationInfo.this.lambda$new$4$NotificationInfo(view);
            }
        };
        this.mOnUndo = new View.OnClickListener() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$NotificationInfo$kcLdDg1SWpjkhvCX9FbMHOcKdX8
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                NotificationInfo.this.lambda$new$6$NotificationInfo(view);
            }
        };
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mPriorityDescriptionView = (TextView) findViewById(R.id.alert_summary);
        this.mSilentDescriptionView = (TextView) findViewById(R.id.silence_summary);
    }

    @VisibleForTesting
    void bindNotification(PackageManager pm, INotificationManager iNotificationManager, VisualStabilityManager visualStabilityManager, String pkg, NotificationChannel notificationChannel, Set<NotificationChannel> uniqueChannelsInRow, StatusBarNotification sbn, CheckSaveListener checkSaveListener, OnSettingsClickListener onSettingsClick, OnAppSettingsClickListener onAppSettingsClick, boolean isDeviceProvisioned, boolean isNonblockable, int importance, boolean wasShownHighPriority) throws RemoteException {
        bindNotification(pm, iNotificationManager, visualStabilityManager, pkg, notificationChannel, uniqueChannelsInRow, sbn, checkSaveListener, onSettingsClick, onAppSettingsClick, isDeviceProvisioned, isNonblockable, false, importance, wasShownHighPriority);
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x008f, code lost:
        if (r2 == 1) goto L9;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void bindNotification(android.content.pm.PackageManager r17, android.app.INotificationManager r18, com.android.systemui.statusbar.notification.VisualStabilityManager r19, java.lang.String r20, android.app.NotificationChannel r21, java.util.Set<android.app.NotificationChannel> r22, android.service.notification.StatusBarNotification r23, com.android.systemui.statusbar.notification.row.NotificationInfo.CheckSaveListener r24, com.android.systemui.statusbar.notification.row.NotificationInfo.OnSettingsClickListener r25, com.android.systemui.statusbar.notification.row.NotificationInfo.OnAppSettingsClickListener r26, boolean r27, boolean r28, boolean r29, int r30, boolean r31) throws android.os.RemoteException {
        /*
            r16 = this;
            r0 = r16
            r1 = r20
            r2 = r18
            r0.mINotificationManager = r2
            java.lang.Class<com.android.internal.logging.MetricsLogger> r3 = com.android.internal.logging.MetricsLogger.class
            java.lang.Object r3 = com.android.systemui.Dependency.get(r3)
            com.android.internal.logging.MetricsLogger r3 = (com.android.internal.logging.MetricsLogger) r3
            r0.mMetricsLogger = r3
            r3 = r19
            r0.mVisualStabilityManager = r3
            java.lang.Class<com.android.systemui.statusbar.notification.row.ChannelEditorDialogController> r4 = com.android.systemui.statusbar.notification.row.ChannelEditorDialogController.class
            java.lang.Object r4 = com.android.systemui.Dependency.get(r4)
            com.android.systemui.statusbar.notification.row.ChannelEditorDialogController r4 = (com.android.systemui.statusbar.notification.row.ChannelEditorDialogController) r4
            r0.mChannelEditorDialogController = r4
            r0.mPackageName = r1
            r4 = r22
            r0.mUniqueChannelsInRow = r4
            int r5 = r22.size()
            r0.mNumUniqueChannelsInRow = r5
            r5 = r23
            r0.mSbn = r5
            r6 = r17
            r0.mPm = r6
            r7 = r26
            r0.mAppSettingsClickListener = r7
            java.lang.String r8 = r0.mPackageName
            r0.mAppName = r8
            r8 = r24
            r0.mCheckSaveListener = r8
            r9 = r25
            r0.mOnSettingsClickListener = r9
            r10 = r21
            r0.mSingleNotificationChannel = r10
            android.app.NotificationChannel r11 = r0.mSingleNotificationChannel
            int r11 = r11.getImportance()
            r0.mStartingChannelImportance = r11
            r11 = r31
            r0.mWasShownHighPriority = r11
            r12 = r28
            r0.mIsNonblockable = r12
            r13 = r29
            r0.mIsForBlockingHelper = r13
            android.service.notification.StatusBarNotification r14 = r0.mSbn
            int r14 = r14.getUid()
            r0.mAppUid = r14
            android.service.notification.StatusBarNotification r14 = r0.mSbn
            java.lang.String r14 = r14.getOpPkg()
            r0.mDelegatePkg = r14
            r14 = r27
            r0.mIsDeviceProvisioned = r14
            android.app.INotificationManager r15 = r0.mINotificationManager
            int r2 = r0.mAppUid
            r3 = 0
            int r2 = r15.getNumNotificationChannelsForPackage(r1, r2, r3)
            int r15 = r0.mNumUniqueChannelsInRow
            if (r15 == 0) goto Lb0
            r3 = 1
            if (r15 != r3) goto L92
            android.app.NotificationChannel r15 = r0.mSingleNotificationChannel
            java.lang.String r15 = r15.getId()
            java.lang.String r3 = "miscellaneous"
            boolean r3 = r15.equals(r3)
            if (r3 == 0) goto L92
            r3 = 1
            if (r2 != r3) goto L92
            goto L93
        L92:
            r3 = 0
        L93:
            r0.mIsSingleDefaultChannel = r3
            r16.bindHeader()
            r16.bindChannelDetails()
            boolean r3 = r0.mIsForBlockingHelper
            if (r3 == 0) goto La3
            r16.bindBlockingHelper()
            goto La6
        La3:
            r16.bindInlineControls()
        La6:
            com.android.internal.logging.MetricsLogger r3 = r0.mMetricsLogger
            android.metrics.LogMaker r15 = r16.notificationControlsLogMaker()
            r3.write(r15)
            return
        Lb0:
            java.lang.IllegalArgumentException r3 = new java.lang.IllegalArgumentException
            java.lang.String r15 = "bindNotification requires at least one channel"
            r3.<init>(r15)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.row.NotificationInfo.bindNotification(android.content.pm.PackageManager, android.app.INotificationManager, com.android.systemui.statusbar.notification.VisualStabilityManager, java.lang.String, android.app.NotificationChannel, java.util.Set, android.service.notification.StatusBarNotification, com.android.systemui.statusbar.notification.row.NotificationInfo$CheckSaveListener, com.android.systemui.statusbar.notification.row.NotificationInfo$OnSettingsClickListener, com.android.systemui.statusbar.notification.row.NotificationInfo$OnAppSettingsClickListener, boolean, boolean, boolean, int, boolean):void");
    }

    private void bindBlockingHelper() {
        findViewById(R.id.inline_controls).setVisibility(8);
        findViewById(R.id.blocking_helper).setVisibility(0);
        findViewById(R.id.undo).setOnClickListener(this.mOnUndo);
        View turnOffButton = findViewById(R.id.blocking_helper_turn_off_notifications);
        turnOffButton.setOnClickListener(getSettingsOnClickListener());
        turnOffButton.setVisibility(turnOffButton.hasOnClickListeners() ? 0 : 8);
        TextView keepShowing = (TextView) findViewById(R.id.keep_showing);
        keepShowing.setOnClickListener(this.mOnKeepShowing);
        View deliverSilently = findViewById(R.id.deliver_silently);
        deliverSilently.setOnClickListener(this.mOnDeliverSilently);
    }

    private void bindInlineControls() {
        findViewById(R.id.inline_controls).setVisibility(0);
        int i = 8;
        findViewById(R.id.blocking_helper).setVisibility(8);
        if (this.mIsNonblockable) {
            findViewById(R.id.non_configurable_text).setVisibility(0);
            findViewById(R.id.non_configurable_multichannel_text).setVisibility(8);
            findViewById(R.id.interruptiveness_settings).setVisibility(8);
            ((TextView) findViewById(R.id.done)).setText(R.string.inline_done_button);
            findViewById(R.id.turn_off_notifications).setVisibility(8);
        } else if (this.mNumUniqueChannelsInRow > 1) {
            findViewById(R.id.non_configurable_text).setVisibility(8);
            findViewById(R.id.interruptiveness_settings).setVisibility(8);
            findViewById(R.id.non_configurable_multichannel_text).setVisibility(0);
        } else {
            findViewById(R.id.non_configurable_text).setVisibility(8);
            findViewById(R.id.non_configurable_multichannel_text).setVisibility(8);
            findViewById(R.id.interruptiveness_settings).setVisibility(0);
        }
        View turnOffButton = findViewById(R.id.turn_off_notifications);
        turnOffButton.setOnClickListener(getTurnOffNotificationsClickListener());
        if (turnOffButton.hasOnClickListeners() && !this.mIsNonblockable) {
            i = 0;
        }
        turnOffButton.setVisibility(i);
        View done = findViewById(R.id.done);
        done.setOnClickListener(this.mOnDismissSettings);
        View silent = findViewById(R.id.silence);
        View alert = findViewById(R.id.alert);
        silent.setOnClickListener(this.mOnSilent);
        alert.setOnClickListener(this.mOnAlert);
        applyAlertingBehavior(1 ^ (this.mWasShownHighPriority ? 1 : 0), false);
    }

    private void bindHeader() {
        this.mPkgIcon = null;
        try {
            ApplicationInfo info = this.mPm.getApplicationInfo(this.mPackageName, 795136);
            if (info != null) {
                this.mAppName = String.valueOf(this.mPm.getApplicationLabel(info));
                this.mPkgIcon = this.mPm.getApplicationIcon(info);
            }
        } catch (PackageManager.NameNotFoundException e) {
            this.mPkgIcon = this.mPm.getDefaultActivityIcon();
        }
        ((ImageView) findViewById(R.id.pkgicon)).setImageDrawable(this.mPkgIcon);
        ((TextView) findViewById(R.id.pkgname)).setText(this.mAppName);
        bindDelegate();
        View settingsLinkView = findViewById(R.id.app_settings);
        final Intent settingsIntent = getAppSettingsIntent(this.mPm, this.mPackageName, this.mSingleNotificationChannel, this.mSbn.getId(), this.mSbn.getTag());
        if (settingsIntent != null && !TextUtils.isEmpty(this.mSbn.getNotification().getSettingsText())) {
            settingsLinkView.setVisibility(0);
            settingsLinkView.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$NotificationInfo$tDyIQiSMKPJwexxQ_nMHuNF9Llk
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    NotificationInfo.this.lambda$bindHeader$7$NotificationInfo(settingsIntent, view);
                }
            });
        } else {
            settingsLinkView.setVisibility(8);
        }
        View settingsButton = findViewById(R.id.info);
        settingsButton.setOnClickListener(getSettingsOnClickListener());
        settingsButton.setVisibility(settingsButton.hasOnClickListeners() ? 0 : 8);
    }

    public /* synthetic */ void lambda$bindHeader$7$NotificationInfo(Intent settingsIntent, View view) {
        this.mAppSettingsClickListener.onClick(view, settingsIntent);
    }

    private View.OnClickListener getSettingsOnClickListener() {
        if (this.mAppUid >= 0 && this.mOnSettingsClickListener != null && this.mIsDeviceProvisioned) {
            final int appUidF = this.mAppUid;
            return new View.OnClickListener() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$NotificationInfo$0-pyRe_YAFdEij18HwbLfjxZpe4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    NotificationInfo.this.lambda$getSettingsOnClickListener$8$NotificationInfo(appUidF, view);
                }
            };
        }
        return null;
    }

    public /* synthetic */ void lambda$getSettingsOnClickListener$8$NotificationInfo(int appUidF, View view) {
        logBlockingHelperCounter(NotificationCounters.BLOCKING_HELPER_NOTIF_SETTINGS);
        this.mOnSettingsClickListener.onClick(view, this.mNumUniqueChannelsInRow > 1 ? null : this.mSingleNotificationChannel, appUidF);
    }

    private View.OnClickListener getTurnOffNotificationsClickListener() {
        return new View.OnClickListener() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$NotificationInfo$lHPMwwhdR5jQxtA0fAOND0eRPUs
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                NotificationInfo.this.lambda$getTurnOffNotificationsClickListener$10$NotificationInfo(view);
            }
        };
    }

    public /* synthetic */ void lambda$getTurnOffNotificationsClickListener$10$NotificationInfo(View view) {
        ChannelEditorDialogController channelEditorDialogController;
        if (!this.mPresentingChannelEditorDialog && (channelEditorDialogController = this.mChannelEditorDialogController) != null) {
            this.mPresentingChannelEditorDialog = true;
            channelEditorDialogController.prepareDialogForApp(this.mAppName, this.mPackageName, this.mAppUid, this.mUniqueChannelsInRow, this.mPkgIcon, this.mOnSettingsClickListener);
            this.mChannelEditorDialogController.setOnFinishListener(new OnChannelEditorDialogFinishedListener() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$NotificationInfo$tUHhb1UHSajuu5XM15HG0P0_DVY
                @Override // com.android.systemui.statusbar.notification.row.OnChannelEditorDialogFinishedListener
                public final void onChannelEditorDialogFinished() {
                    NotificationInfo.this.lambda$getTurnOffNotificationsClickListener$9$NotificationInfo();
                }
            });
            this.mChannelEditorDialogController.show();
        }
    }

    public /* synthetic */ void lambda$getTurnOffNotificationsClickListener$9$NotificationInfo() {
        this.mPresentingChannelEditorDialog = false;
        closeControls(this, false);
    }

    private void bindChannelDetails() throws RemoteException {
        bindName();
        bindGroup();
    }

    private void bindName() {
        TextView channelName = (TextView) findViewById(R.id.channel_name);
        if (this.mIsSingleDefaultChannel || this.mNumUniqueChannelsInRow > 1) {
            channelName.setVisibility(8);
        } else {
            channelName.setText(this.mSingleNotificationChannel.getName());
        }
    }

    private void bindDelegate() {
        TextView delegateView = (TextView) findViewById(R.id.delegate_name);
        TextView dividerView = (TextView) findViewById(R.id.pkg_divider);
        if (!TextUtils.equals(this.mPackageName, this.mDelegatePkg)) {
            delegateView.setVisibility(0);
            dividerView.setVisibility(0);
            return;
        }
        delegateView.setVisibility(8);
        dividerView.setVisibility(8);
    }

    private void bindGroup() throws RemoteException {
        NotificationChannelGroup notificationChannelGroup;
        CharSequence groupName = null;
        NotificationChannel notificationChannel = this.mSingleNotificationChannel;
        if (notificationChannel != null && notificationChannel.getGroup() != null && (notificationChannelGroup = this.mINotificationManager.getNotificationChannelGroupForPackage(this.mSingleNotificationChannel.getGroup(), this.mPackageName, this.mAppUid)) != null) {
            groupName = notificationChannelGroup.getName();
        }
        TextView groupNameView = (TextView) findViewById(R.id.group_name);
        if (groupName != null) {
            groupNameView.setText(groupName);
            groupNameView.setVisibility(0);
            return;
        }
        groupNameView.setVisibility(8);
    }

    @VisibleForTesting
    void logBlockingHelperCounter(String counterTag) {
        if (this.mIsForBlockingHelper) {
            this.mMetricsLogger.count(counterTag, 1);
        }
    }

    private void saveImportance() {
        if (!this.mIsNonblockable || this.mExitReason != NotificationCounters.BLOCKING_HELPER_STOP_NOTIFICATIONS) {
            if (this.mChosenImportance == null) {
                this.mChosenImportance = Integer.valueOf(this.mStartingChannelImportance);
            }
            updateImportance();
        }
    }

    private void updateImportance() {
        if (this.mChosenImportance != null) {
            this.mMetricsLogger.write(importanceChangeLogMaker());
            int newImportance = this.mChosenImportance.intValue();
            if (this.mStartingChannelImportance != -1000 && ((this.mWasShownHighPriority && this.mChosenImportance.intValue() >= 3) || (!this.mWasShownHighPriority && this.mChosenImportance.intValue() < 3))) {
                newImportance = this.mStartingChannelImportance;
            }
            Handler bgHandler = new Handler((Looper) Dependency.get(Dependency.BG_LOOPER));
            bgHandler.post(new UpdateImportanceRunnable(this.mINotificationManager, this.mPackageName, this.mAppUid, this.mNumUniqueChannelsInRow == 1 ? this.mSingleNotificationChannel : null, this.mStartingChannelImportance, newImportance));
            this.mVisualStabilityManager.temporarilyAllowReordering();
        }
    }

    private void applyAlertingBehavior(int behavior, boolean userTriggered) {
        if (userTriggered) {
            TransitionSet transition = new TransitionSet();
            transition.setOrdering(0);
            transition.addTransition(new Fade(2)).addTransition(new ChangeBounds()).addTransition(new Fade(1).setStartDelay(150L).setDuration(200L).setInterpolator(Interpolators.FAST_OUT_SLOW_IN));
            transition.setDuration(350L);
            transition.setInterpolator((TimeInterpolator) Interpolators.FAST_OUT_SLOW_IN);
            TransitionManager.beginDelayedTransition(this, transition);
        }
        final View alert = findViewById(R.id.alert);
        final View silence = findViewById(R.id.silence);
        if (behavior != 0) {
            if (behavior == 1) {
                this.mSilentDescriptionView.setVisibility(0);
                this.mPriorityDescriptionView.setVisibility(8);
                post(new Runnable() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$NotificationInfo$JpCUeqH4NTR-eIw4dQ3BiR4iSog
                    @Override // java.lang.Runnable
                    public final void run() {
                        NotificationInfo.lambda$applyAlertingBehavior$12(alert, silence);
                    }
                });
            } else {
                throw new IllegalArgumentException("Unrecognized alerting behavior: " + behavior);
            }
        } else {
            this.mPriorityDescriptionView.setVisibility(0);
            this.mSilentDescriptionView.setVisibility(8);
            post(new Runnable() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$NotificationInfo$z1_znMHrt0xHowqdvbrSsphINf0
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationInfo.lambda$applyAlertingBehavior$11(alert, silence);
                }
            });
        }
        boolean isAChange = this.mWasShownHighPriority != (behavior == 0);
        TextView done = (TextView) findViewById(R.id.done);
        done.setText(isAChange ? R.string.inline_ok_button : R.string.inline_done_button);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$applyAlertingBehavior$11(View alert, View silence) {
        alert.setSelected(true);
        silence.setSelected(false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$applyAlertingBehavior$12(View alert, View silence) {
        alert.setSelected(false);
        silence.setSelected(true);
    }

    private void saveImportanceAndExitReason(@NotificationInfoAction int action) {
        if (action == 1) {
            this.mChosenImportance = Integer.valueOf(this.mStartingChannelImportance);
        } else if (action == 4) {
            this.mExitReason = NotificationCounters.BLOCKING_HELPER_DELIVER_SILENTLY;
            this.mChosenImportance = Integer.valueOf(this.mWasShownHighPriority ? 2 : this.mStartingChannelImportance);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private void swapContent(@NotificationInfoAction int action, boolean animate) {
        final boolean isUndo;
        int i;
        int i2;
        int i3;
        AnimatorSet animatorSet = this.mExpandAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        final View blockingHelper = findViewById(R.id.blocking_helper);
        final ViewGroup confirmation = (ViewGroup) findViewById(R.id.confirmation);
        TextView confirmationText = (TextView) findViewById(R.id.confirmation_text);
        saveImportanceAndExitReason(action);
        if (action != 1) {
            if (action == 4) {
                confirmationText.setText(R.string.notification_channel_silenced);
            } else {
                throw new IllegalArgumentException();
            }
        }
        if (action == 1) {
            isUndo = true;
        } else {
            isUndo = false;
        }
        int i4 = 8;
        if (isUndo) {
            i = 0;
        } else {
            i = 8;
        }
        blockingHelper.setVisibility(i);
        View findViewById = findViewById(R.id.channel_info);
        if (isUndo) {
            i2 = 0;
        } else {
            i2 = 8;
        }
        findViewById.setVisibility(i2);
        View findViewById2 = findViewById(R.id.header);
        if (isUndo) {
            i3 = 0;
        } else {
            i3 = 8;
        }
        findViewById2.setVisibility(i3);
        if (!isUndo) {
            i4 = 0;
        }
        confirmation.setVisibility(i4);
        if (animate) {
            Property property = View.ALPHA;
            float[] fArr = new float[2];
            fArr[0] = blockingHelper.getAlpha();
            fArr[1] = isUndo ? 1.0f : 0.0f;
            ObjectAnimator promptAnim = ObjectAnimator.ofFloat(blockingHelper, property, fArr);
            promptAnim.setInterpolator(isUndo ? Interpolators.ALPHA_IN : Interpolators.ALPHA_OUT);
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[2];
            fArr2[0] = confirmation.getAlpha();
            fArr2[1] = isUndo ? 0.0f : 1.0f;
            ObjectAnimator confirmAnim = ObjectAnimator.ofFloat(confirmation, property2, fArr2);
            confirmAnim.setInterpolator(isUndo ? Interpolators.ALPHA_OUT : Interpolators.ALPHA_IN);
            this.mExpandAnimation = new AnimatorSet();
            this.mExpandAnimation.playTogether(promptAnim, confirmAnim);
            this.mExpandAnimation.setDuration(150L);
            this.mExpandAnimation.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.notification.row.NotificationInfo.1
                boolean mCancelled = false;

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    this.mCancelled = true;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (!this.mCancelled) {
                        blockingHelper.setVisibility(isUndo ? 0 : 8);
                        confirmation.setVisibility(isUndo ? 8 : 0);
                    }
                }
            });
            this.mExpandAnimation.start();
        }
        NotificationGuts notificationGuts = this.mGutsContainer;
        if (notificationGuts != null) {
            notificationGuts.resetFalsingCheck();
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.NotificationGuts.GutsContent
    public void onFinishedClosing() {
        Integer num = this.mChosenImportance;
        if (num != null) {
            this.mStartingChannelImportance = num.intValue();
        }
        this.mExitReason = NotificationCounters.BLOCKING_HELPER_DISMISSED;
        if (this.mIsForBlockingHelper) {
            bindBlockingHelper();
        } else {
            bindInlineControls();
        }
        this.mMetricsLogger.write(notificationControlsLogMaker().setType(2));
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        if (this.mGutsContainer != null && event.getEventType() == 32) {
            if (this.mGutsContainer.isExposed()) {
                event.getText().add(this.mContext.getString(R.string.notification_channel_controls_opened_accessibility, this.mAppName));
            } else {
                event.getText().add(this.mContext.getString(R.string.notification_channel_controls_closed_accessibility, this.mAppName));
            }
        }
    }

    private Intent getAppSettingsIntent(PackageManager pm, String packageName, NotificationChannel channel, int id, String tag) {
        Intent intent = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.NOTIFICATION_PREFERENCES").setPackage(packageName);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 65536);
        if (resolveInfos == null || resolveInfos.size() == 0 || resolveInfos.get(0) == null) {
            return null;
        }
        ActivityInfo activityInfo = resolveInfos.get(0).activityInfo;
        intent.setClassName(activityInfo.packageName, activityInfo.name);
        if (channel != null) {
            intent.putExtra("android.intent.extra.CHANNEL_ID", channel.getId());
        }
        intent.putExtra("android.intent.extra.NOTIFICATION_ID", id);
        intent.putExtra("android.intent.extra.NOTIFICATION_TAG", tag);
        return intent;
    }

    @VisibleForTesting
    void closeControls(View v, boolean save) {
        int[] parentLoc = new int[2];
        int[] targetLoc = new int[2];
        this.mGutsContainer.getLocationOnScreen(parentLoc);
        v.getLocationOnScreen(targetLoc);
        int centerX = v.getWidth() / 2;
        int centerY = v.getHeight() / 2;
        int x = (targetLoc[0] - parentLoc[0]) + centerX;
        int y = (targetLoc[1] - parentLoc[1]) + centerY;
        this.mGutsContainer.closeControls(x, y, save, false);
    }

    @Override // com.android.systemui.statusbar.notification.row.NotificationGuts.GutsContent
    public void setGutsParent(NotificationGuts guts) {
        this.mGutsContainer = guts;
    }

    @Override // com.android.systemui.statusbar.notification.row.NotificationGuts.GutsContent
    public boolean willBeRemoved() {
        return false;
    }

    @Override // com.android.systemui.statusbar.notification.row.NotificationGuts.GutsContent
    public boolean shouldBeSaved() {
        return this.mPressedApply;
    }

    @Override // com.android.systemui.statusbar.notification.row.NotificationGuts.GutsContent
    public View getContentView() {
        return this;
    }

    @Override // com.android.systemui.statusbar.notification.row.NotificationGuts.GutsContent
    public boolean handleCloseControls(boolean save, boolean force) {
        ChannelEditorDialogController channelEditorDialogController;
        if (this.mPresentingChannelEditorDialog && (channelEditorDialogController = this.mChannelEditorDialogController) != null) {
            this.mPresentingChannelEditorDialog = false;
            channelEditorDialogController.setOnFinishListener(null);
            this.mChannelEditorDialogController.close();
        }
        if (save) {
            saveImportance();
        }
        logBlockingHelperCounter(this.mExitReason);
        return false;
    }

    @Override // com.android.systemui.statusbar.notification.row.NotificationGuts.GutsContent
    public int getActualHeight() {
        return getHeight();
    }

    @VisibleForTesting
    public boolean isAnimating() {
        AnimatorSet animatorSet = this.mExpandAnimation;
        return animatorSet != null && animatorSet.isRunning();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class UpdateImportanceRunnable implements Runnable {
        private final int mAppUid;
        private final NotificationChannel mChannelToUpdate;
        private final int mCurrentImportance;
        private final INotificationManager mINotificationManager;
        private final int mNewImportance;
        private final String mPackageName;

        public UpdateImportanceRunnable(INotificationManager notificationManager, String packageName, int appUid, NotificationChannel channelToUpdate, int currentImportance, int newImportance) {
            this.mINotificationManager = notificationManager;
            this.mPackageName = packageName;
            this.mAppUid = appUid;
            this.mChannelToUpdate = channelToUpdate;
            this.mCurrentImportance = currentImportance;
            this.mNewImportance = newImportance;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                if (this.mChannelToUpdate != null) {
                    this.mChannelToUpdate.setImportance(this.mNewImportance);
                    this.mChannelToUpdate.lockFields(4);
                    this.mINotificationManager.updateNotificationChannelForPackage(this.mPackageName, this.mAppUid, this.mChannelToUpdate);
                } else {
                    this.mINotificationManager.setNotificationsEnabledWithImportanceLockForPackage(this.mPackageName, this.mAppUid, this.mNewImportance >= this.mCurrentImportance);
                }
            } catch (RemoteException e) {
                Log.e(NotificationInfo.TAG, "Unable to update notification importance", e);
            }
        }
    }

    private LogMaker getLogMaker() {
        StatusBarNotification statusBarNotification = this.mSbn;
        return statusBarNotification == null ? new LogMaker(1621) : statusBarNotification.getLogMaker().setCategory(1621);
    }

    private LogMaker importanceChangeLogMaker() {
        Integer num = this.mChosenImportance;
        Integer chosenImportance = Integer.valueOf(num != null ? num.intValue() : this.mStartingChannelImportance);
        return getLogMaker().setCategory(291).setType(4).setSubtype(chosenImportance.intValue() - this.mStartingChannelImportance);
    }

    private LogMaker notificationControlsLogMaker() {
        int i = 1;
        LogMaker type = getLogMaker().setCategory(204).setType(1);
        if (!this.mIsForBlockingHelper) {
            i = 0;
        }
        return type.setSubtype(i);
    }
}
