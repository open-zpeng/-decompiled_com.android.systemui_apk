package com.android.systemui.statusbar.notification;

import android.app.Notification;
import android.content.Context;
import android.database.ContentObserver;
import android.hardware.display.AmbientDisplayConfiguration;
import android.os.Handler;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.service.dreams.IDreamManager;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class NotificationInterruptionStateProvider {
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_HEADS_UP = true;
    private static final boolean ENABLE_HEADS_UP = true;
    private static final String SETTING_HEADS_UP_TICKER = "ticker_gets_heads_up";
    private static final String TAG = "InterruptionStateProvider";
    private final AmbientDisplayConfiguration mAmbientDisplayConfiguration;
    private final BatteryController mBatteryController;
    private final Context mContext;
    private boolean mDisableNotificationAlerts;
    private final IDreamManager mDreamManager;
    private HeadsUpManager mHeadsUpManager;
    private ContentObserver mHeadsUpObserver;
    private HeadsUpSuppressor mHeadsUpSuppressor;
    private final NotificationFilter mNotificationFilter;
    private final PowerManager mPowerManager;
    private NotificationPresenter mPresenter;
    private final StatusBarStateController mStatusBarStateController;
    @VisibleForTesting
    protected boolean mUseHeadsUp;

    /* loaded from: classes21.dex */
    public interface HeadsUpSuppressor {
        boolean canHeadsUp(NotificationEntry notificationEntry, StatusBarNotification statusBarNotification);
    }

    @Inject
    public NotificationInterruptionStateProvider(Context context, NotificationFilter filter, StatusBarStateController stateController, BatteryController batteryController) {
        this(context, (PowerManager) context.getSystemService("power"), IDreamManager.Stub.asInterface(ServiceManager.checkService("dreams")), new AmbientDisplayConfiguration(context), filter, batteryController, stateController);
    }

    @VisibleForTesting
    protected NotificationInterruptionStateProvider(Context context, PowerManager powerManager, IDreamManager dreamManager, AmbientDisplayConfiguration ambientDisplayConfiguration, NotificationFilter notificationFilter, BatteryController batteryController, StatusBarStateController statusBarStateController) {
        this.mUseHeadsUp = false;
        this.mContext = context;
        this.mPowerManager = powerManager;
        this.mDreamManager = dreamManager;
        this.mBatteryController = batteryController;
        this.mAmbientDisplayConfiguration = ambientDisplayConfiguration;
        this.mNotificationFilter = notificationFilter;
        this.mStatusBarStateController = statusBarStateController;
    }

    public void setUpWithPresenter(NotificationPresenter notificationPresenter, HeadsUpManager headsUpManager, HeadsUpSuppressor headsUpSuppressor) {
        setUpWithPresenter(notificationPresenter, headsUpManager, headsUpSuppressor, new ContentObserver((Handler) Dependency.get(Dependency.MAIN_HANDLER)) { // from class: com.android.systemui.statusbar.notification.NotificationInterruptionStateProvider.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                boolean wasUsing = NotificationInterruptionStateProvider.this.mUseHeadsUp;
                NotificationInterruptionStateProvider notificationInterruptionStateProvider = NotificationInterruptionStateProvider.this;
                boolean z = false;
                if (!notificationInterruptionStateProvider.mDisableNotificationAlerts && Settings.Global.getInt(NotificationInterruptionStateProvider.this.mContext.getContentResolver(), "heads_up_notifications_enabled", 0) != 0) {
                    z = true;
                }
                notificationInterruptionStateProvider.mUseHeadsUp = z;
                StringBuilder sb = new StringBuilder();
                sb.append("heads up is ");
                sb.append(NotificationInterruptionStateProvider.this.mUseHeadsUp ? VuiConstants.ELEMENT_ENABLED : "disabled");
                Log.d(NotificationInterruptionStateProvider.TAG, sb.toString());
                if (wasUsing != NotificationInterruptionStateProvider.this.mUseHeadsUp && !NotificationInterruptionStateProvider.this.mUseHeadsUp) {
                    Log.d(NotificationInterruptionStateProvider.TAG, "dismissing any existing heads up notification on disable event");
                    NotificationInterruptionStateProvider.this.mHeadsUpManager.releaseAllImmediately();
                }
            }
        });
    }

    public void setUpWithPresenter(NotificationPresenter notificationPresenter, HeadsUpManager headsUpManager, HeadsUpSuppressor headsUpSuppressor, ContentObserver observer) {
        this.mPresenter = notificationPresenter;
        this.mHeadsUpManager = headsUpManager;
        this.mHeadsUpSuppressor = headsUpSuppressor;
        this.mHeadsUpObserver = observer;
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("heads_up_notifications_enabled"), true, this.mHeadsUpObserver);
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor(SETTING_HEADS_UP_TICKER), true, this.mHeadsUpObserver);
        this.mHeadsUpObserver.onChange(true);
    }

    public boolean shouldBubbleUp(NotificationEntry entry) {
        StatusBarNotification sbn = entry.notification;
        if (canAlertCommon(entry) && canAlertAwakeCommon(entry) && entry.canBubble && entry.isBubble()) {
            Notification n = sbn.getNotification();
            return (n.getBubbleMetadata() == null || n.getBubbleMetadata().getIntent() == null) ? false : true;
        }
        return false;
    }

    public boolean shouldHeadsUp(NotificationEntry entry) {
        if (this.mStatusBarStateController.isDozing()) {
            return shouldHeadsUpWhenDozing(entry);
        }
        return shouldHeadsUpWhenAwake(entry);
    }

    private boolean shouldHeadsUpWhenAwake(NotificationEntry entry) {
        StatusBarNotification sbn = entry.notification;
        if (!this.mUseHeadsUp) {
            Log.d(TAG, "No heads up: no huns");
            return false;
        } else if (canAlertCommon(entry) && canAlertAwakeCommon(entry)) {
            boolean inShade = this.mStatusBarStateController.getState() == 0;
            if (entry.isBubble() && inShade) {
                Log.d(TAG, "No heads up: in unlocked shade where notification is shown as a bubble: " + sbn.getKey());
                return false;
            } else if (entry.shouldSuppressPeek()) {
                Log.d(TAG, "No heads up: suppressed by DND: " + sbn.getKey());
                return false;
            } else if (entry.importance < 4) {
                Log.d(TAG, "No heads up: unimportant notification: " + sbn.getKey());
                return false;
            } else {
                boolean isDreaming = false;
                try {
                    isDreaming = this.mDreamManager.isDreaming();
                } catch (RemoteException e) {
                    Log.e(TAG, "Failed to query dream manager.", e);
                }
                boolean inUse = this.mPowerManager.isScreenOn() && !isDreaming;
                if (!inUse) {
                    Log.d(TAG, "No heads up: not in use: " + sbn.getKey());
                    return false;
                } else if (this.mHeadsUpSuppressor.canHeadsUp(entry, sbn)) {
                    return true;
                } else {
                    Log.d(TAG, "No heads up: aborted by suppressor: " + sbn.getKey());
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    private boolean shouldHeadsUpWhenDozing(NotificationEntry entry) {
        StatusBarNotification sbn = entry.notification;
        if (!this.mAmbientDisplayConfiguration.pulseOnNotificationEnabled(-2)) {
            Log.d(TAG, "No pulsing: disabled by setting: " + sbn.getKey());
            return false;
        } else if (this.mBatteryController.isAodPowerSave()) {
            Log.d(TAG, "No pulsing: disabled by battery saver: " + sbn.getKey());
            return false;
        } else if (!canAlertCommon(entry)) {
            Log.d(TAG, "No pulsing: notification shouldn't alert: " + sbn.getKey());
            return false;
        } else if (entry.shouldSuppressAmbient()) {
            Log.d(TAG, "No pulsing: ambient effect suppressed: " + sbn.getKey());
            return false;
        } else if (entry.importance < 3) {
            Log.d(TAG, "No pulsing: not important enough: " + sbn.getKey());
            return false;
        } else {
            return true;
        }
    }

    @VisibleForTesting
    public boolean canAlertCommon(NotificationEntry entry) {
        StatusBarNotification sbn = entry.notification;
        if (this.mNotificationFilter.shouldFilterOut(entry)) {
            Log.d(TAG, "No alerting: filtered notification: " + sbn.getKey());
            return false;
        } else if (sbn.isGroup() && sbn.getNotification().suppressAlertingDueToGrouping()) {
            Log.d(TAG, "No alerting: suppressed due to group alert behavior");
            return false;
        } else {
            return true;
        }
    }

    @VisibleForTesting
    public boolean canAlertAwakeCommon(NotificationEntry entry) {
        StatusBarNotification sbn = entry.notification;
        if (this.mPresenter.isDeviceInVrMode()) {
            Log.d(TAG, "No alerting: no huns or vr mode");
            return false;
        } else if (isSnoozedPackage(sbn)) {
            Log.d(TAG, "No alerting: snoozed package: " + sbn.getKey());
            return false;
        } else if (entry.hasJustLaunchedFullScreenIntent()) {
            Log.d(TAG, "No alerting: recent fullscreen: " + sbn.getKey());
            return false;
        } else {
            return true;
        }
    }

    private boolean isSnoozedPackage(StatusBarNotification sbn) {
        return this.mHeadsUpManager.isSnoozed(sbn.getPackageName());
    }

    public void setDisableNotificationAlerts(boolean disableNotificationAlerts) {
        this.mDisableNotificationAlerts = disableNotificationAlerts;
        this.mHeadsUpObserver.onChange(true);
    }

    @VisibleForTesting
    public boolean areNotificationAlertsDisabled() {
        return this.mDisableNotificationAlerts;
    }

    @VisibleForTesting
    public boolean getUseHeadsUp() {
        return this.mUseHeadsUp;
    }

    protected NotificationPresenter getPresenter() {
        return this.mPresenter;
    }

    public boolean shouldLaunchFullScreenIntentWhenAdded(NotificationEntry entry) {
        return entry.notification.getNotification().fullScreenIntent != null && (!shouldHeadsUp(entry) || this.mStatusBarStateController.getState() == 1);
    }
}
