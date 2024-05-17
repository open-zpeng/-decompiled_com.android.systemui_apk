package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.service.notification.ZenModeConfig;
import android.text.format.DateFormat;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dumpable;
import com.android.systemui.qs.GlobalSetting;
import com.android.systemui.settings.CurrentUserTracker;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.util.Utils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class ZenModeControllerImpl extends CurrentUserTracker implements ZenModeController, Dumpable {
    private final AlarmManager mAlarmManager;
    private final ArrayList<ZenModeController.Callback> mCallbacks;
    private final Object mCallbacksLock;
    private ZenModeConfig mConfig;
    private final GlobalSetting mConfigSetting;
    private NotificationManager.Policy mConsolidatedNotificationPolicy;
    private final Context mContext;
    private final GlobalSetting mModeSetting;
    private final NotificationManager mNoMan;
    private final BroadcastReceiver mReceiver;
    private boolean mRegistered;
    private final SetupObserver mSetupObserver;
    private int mUserId;
    private final UserManager mUserManager;
    private int mZenMode;
    private long mZenUpdateTime;
    private static final String TAG = "ZenModeController";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);

    @Inject
    public ZenModeControllerImpl(Context context, @Named("main_handler") Handler handler) {
        super(context);
        this.mCallbacks = new ArrayList<>();
        this.mCallbacksLock = new Object();
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.systemui.statusbar.policy.ZenModeControllerImpl.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if ("android.app.action.NEXT_ALARM_CLOCK_CHANGED".equals(intent.getAction())) {
                    ZenModeControllerImpl.this.fireNextAlarmChanged();
                }
                if ("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED".equals(intent.getAction())) {
                    ZenModeControllerImpl.this.fireEffectsSuppressorChanged();
                }
            }
        };
        this.mContext = context;
        this.mModeSetting = new GlobalSetting(this.mContext, handler, "zen_mode") { // from class: com.android.systemui.statusbar.policy.ZenModeControllerImpl.1
            @Override // com.android.systemui.qs.GlobalSetting
            protected void handleValueChanged(int value) {
                ZenModeControllerImpl.this.updateZenMode(value);
                ZenModeControllerImpl.this.fireZenChanged(value);
            }
        };
        this.mConfigSetting = new GlobalSetting(this.mContext, handler, "zen_mode_config_etag") { // from class: com.android.systemui.statusbar.policy.ZenModeControllerImpl.2
            @Override // com.android.systemui.qs.GlobalSetting
            protected void handleValueChanged(int value) {
                ZenModeControllerImpl.this.updateZenModeConfig();
            }
        };
        this.mNoMan = (NotificationManager) context.getSystemService("notification");
        this.mModeSetting.setListening(true);
        updateZenMode(this.mModeSetting.getValue());
        this.mConfigSetting.setListening(true);
        updateZenModeConfig();
        updateConsolidatedNotificationPolicy();
        this.mAlarmManager = (AlarmManager) context.getSystemService("alarm");
        this.mSetupObserver = new SetupObserver(handler);
        this.mSetupObserver.register();
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
        startTracking();
    }

    @Override // com.android.systemui.statusbar.policy.ZenModeController
    public boolean isVolumeRestricted() {
        return this.mUserManager.hasUserRestriction("no_adjust_volume", new UserHandle(this.mUserId));
    }

    @Override // com.android.systemui.statusbar.policy.ZenModeController
    public boolean areNotificationsHiddenInShade() {
        return (this.mZenMode == 0 || (this.mConsolidatedNotificationPolicy.suppressedVisualEffects & 256) == 0) ? false : true;
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void addCallback(ZenModeController.Callback callback) {
        synchronized (this.mCallbacksLock) {
            this.mCallbacks.add(callback);
        }
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void removeCallback(ZenModeController.Callback callback) {
        synchronized (this.mCallbacksLock) {
            this.mCallbacks.remove(callback);
        }
    }

    @Override // com.android.systemui.statusbar.policy.ZenModeController
    public int getZen() {
        return this.mZenMode;
    }

    @Override // com.android.systemui.statusbar.policy.ZenModeController
    public void setZen(int zen, Uri conditionId, String reason) {
        this.mNoMan.setZenMode(zen, conditionId, reason);
    }

    @Override // com.android.systemui.statusbar.policy.ZenModeController
    public boolean isZenAvailable() {
        return this.mSetupObserver.isDeviceProvisioned() && this.mSetupObserver.isUserSetup();
    }

    @Override // com.android.systemui.statusbar.policy.ZenModeController
    public ZenModeConfig.ZenRule getManualRule() {
        ZenModeConfig zenModeConfig = this.mConfig;
        if (zenModeConfig == null) {
            return null;
        }
        return zenModeConfig.manualRule;
    }

    @Override // com.android.systemui.statusbar.policy.ZenModeController
    public ZenModeConfig getConfig() {
        return this.mConfig;
    }

    @Override // com.android.systemui.statusbar.policy.ZenModeController
    public NotificationManager.Policy getConsolidatedPolicy() {
        return this.mConsolidatedNotificationPolicy;
    }

    @Override // com.android.systemui.statusbar.policy.ZenModeController
    public long getNextAlarm() {
        AlarmManager.AlarmClockInfo info = this.mAlarmManager.getNextAlarmClock(this.mUserId);
        if (info != null) {
            return info.getTriggerTime();
        }
        return 0L;
    }

    @Override // com.android.systemui.settings.CurrentUserTracker
    public void onUserSwitched(int userId) {
        this.mUserId = userId;
        if (this.mRegistered) {
            this.mContext.unregisterReceiver(this.mReceiver);
        }
        IntentFilter filter = new IntentFilter("android.app.action.NEXT_ALARM_CLOCK_CHANGED");
        filter.addAction("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED");
        this.mContext.registerReceiverAsUser(this.mReceiver, new UserHandle(this.mUserId), filter, null, null);
        this.mRegistered = true;
        this.mSetupObserver.register();
    }

    @Override // com.android.systemui.statusbar.policy.ZenModeController
    public ComponentName getEffectsSuppressor() {
        return NotificationManager.from(this.mContext).getEffectsSuppressor();
    }

    @Override // com.android.systemui.statusbar.policy.ZenModeController
    public boolean isCountdownConditionSupported() {
        return NotificationManager.from(this.mContext).isSystemConditionProviderEnabled("countdown");
    }

    @Override // com.android.systemui.statusbar.policy.ZenModeController
    public int getCurrentUser() {
        return ActivityManager.getCurrentUser();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fireNextAlarmChanged() {
        synchronized (this.mCallbacksLock) {
            Utils.safeForeach(this.mCallbacks, new Consumer() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$ZenModeControllerImpl$6_S_aAoRd9fsiJr9D0TIwCJGb6M
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((ZenModeController.Callback) obj).onNextAlarmChanged();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fireEffectsSuppressorChanged() {
        synchronized (this.mCallbacksLock) {
            Utils.safeForeach(this.mCallbacks, new Consumer() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$ZenModeControllerImpl$SV0AVEr3ZD6I5F0ZOAtC6EOyn-k
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((ZenModeController.Callback) obj).onEffectsSupressorChanged();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fireZenChanged(final int zen) {
        synchronized (this.mCallbacksLock) {
            Utils.safeForeach(this.mCallbacks, new Consumer() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$ZenModeControllerImpl$d6ICAgvR9KT8NKs4p-zRwBgYI2g
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((ZenModeController.Callback) obj).onZenChanged(zen);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fireZenAvailableChanged(final boolean available) {
        synchronized (this.mCallbacksLock) {
            Utils.safeForeach(this.mCallbacks, new Consumer() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$ZenModeControllerImpl$SZ6Og1sK4NAner-jv0COJMr2bCU
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((ZenModeController.Callback) obj).onZenAvailableChanged(available);
                }
            });
        }
    }

    private void fireManualRuleChanged(final ZenModeConfig.ZenRule rule) {
        synchronized (this.mCallbacksLock) {
            Utils.safeForeach(this.mCallbacks, new Consumer() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$ZenModeControllerImpl$8iaDxlkHjmysoUP7KwjUaBzkBiQ
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((ZenModeController.Callback) obj).onManualRuleChanged(rule);
                }
            });
        }
    }

    private void fireConsolidatedPolicyChanged(final NotificationManager.Policy policy) {
        synchronized (this.mCallbacksLock) {
            Utils.safeForeach(this.mCallbacks, new Consumer() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$ZenModeControllerImpl$8ESweSQi2XbEG_Qu7VUYzDq1Zcs
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((ZenModeController.Callback) obj).onConsolidatedPolicyChanged(policy);
                }
            });
        }
    }

    @VisibleForTesting
    protected void fireConfigChanged(final ZenModeConfig config) {
        synchronized (this.mCallbacksLock) {
            Utils.safeForeach(this.mCallbacks, new Consumer() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$ZenModeControllerImpl$idmtZJFosRgAGQLYktOBo_UGp5E
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((ZenModeController.Callback) obj).onConfigChanged(config);
                }
            });
        }
    }

    @VisibleForTesting
    protected void updateZenMode(int mode) {
        this.mZenMode = mode;
        this.mZenUpdateTime = System.currentTimeMillis();
    }

    @VisibleForTesting
    protected void updateConsolidatedNotificationPolicy() {
        NotificationManager.Policy policy = this.mNoMan.getConsolidatedNotificationPolicy();
        if (!Objects.equals(policy, this.mConsolidatedNotificationPolicy)) {
            this.mConsolidatedNotificationPolicy = policy;
            fireConsolidatedPolicyChanged(policy);
        }
    }

    @VisibleForTesting
    protected void updateZenModeConfig() {
        ZenModeConfig config = this.mNoMan.getZenModeConfig();
        if (Objects.equals(config, this.mConfig)) {
            return;
        }
        ZenModeConfig zenModeConfig = this.mConfig;
        ZenModeConfig.ZenRule oldRule = zenModeConfig != null ? zenModeConfig.manualRule : null;
        this.mConfig = config;
        this.mZenUpdateTime = System.currentTimeMillis();
        fireConfigChanged(config);
        ZenModeConfig.ZenRule newRule = config != null ? config.manualRule : null;
        if (!Objects.equals(oldRule, newRule)) {
            fireManualRuleChanged(newRule);
        }
        NotificationManager.Policy consolidatedPolicy = this.mNoMan.getConsolidatedNotificationPolicy();
        if (!Objects.equals(consolidatedPolicy, this.mConsolidatedNotificationPolicy)) {
            this.mConsolidatedNotificationPolicy = consolidatedPolicy;
            fireConsolidatedPolicyChanged(consolidatedPolicy);
        }
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("ZenModeControllerImpl:");
        pw.println("  mZenMode=" + this.mZenMode);
        pw.println("  mConfig=" + this.mConfig);
        pw.println("  mConsolidatedNotificationPolicy=" + this.mConsolidatedNotificationPolicy);
        pw.println("  mZenUpdateTime=" + ((Object) DateFormat.format("MM-dd HH:mm:ss", this.mZenUpdateTime)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public final class SetupObserver extends ContentObserver {
        private boolean mRegistered;
        private final ContentResolver mResolver;

        public SetupObserver(Handler handler) {
            super(handler);
            this.mResolver = ZenModeControllerImpl.this.mContext.getContentResolver();
        }

        public boolean isUserSetup() {
            return Settings.Secure.getIntForUser(this.mResolver, "user_setup_complete", 0, ZenModeControllerImpl.this.mUserId) != 0;
        }

        public boolean isDeviceProvisioned() {
            return Settings.Global.getInt(this.mResolver, "device_provisioned", 0) != 0;
        }

        public void register() {
            if (this.mRegistered) {
                this.mResolver.unregisterContentObserver(this);
            }
            this.mResolver.registerContentObserver(Settings.Global.getUriFor("device_provisioned"), false, this);
            this.mResolver.registerContentObserver(Settings.Secure.getUriFor("user_setup_complete"), false, this, ZenModeControllerImpl.this.mUserId);
            this.mRegistered = true;
            ZenModeControllerImpl zenModeControllerImpl = ZenModeControllerImpl.this;
            zenModeControllerImpl.fireZenAvailableChanged(zenModeControllerImpl.isZenAvailable());
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            if (Settings.Global.getUriFor("device_provisioned").equals(uri) || Settings.Secure.getUriFor("user_setup_complete").equals(uri)) {
                ZenModeControllerImpl zenModeControllerImpl = ZenModeControllerImpl.this;
                zenModeControllerImpl.fireZenAvailableChanged(zenModeControllerImpl.isZenAvailable());
            }
        }
    }
}
