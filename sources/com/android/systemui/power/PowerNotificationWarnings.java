package com.android.systemui.power;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.Annotation;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.util.Slog;
import android.view.View;
import androidx.annotation.VisibleForTesting;
import com.android.settingslib.Utils;
import com.android.settingslib.fuelgauge.BatterySaverUtils;
import com.android.settingslib.utils.PowerUtil;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.SystemUI;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.power.PowerUI;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.util.NotificationChannels;
import com.android.systemui.volume.Events;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class PowerNotificationWarnings implements PowerUI.WarningsUI {
    private static final String ACTION_AUTO_SAVER_NO_THANKS = "PNW.autoSaverNoThanks";
    private static final String ACTION_CLICKED_TEMP_WARNING = "PNW.clickedTempWarning";
    private static final String ACTION_CLICKED_THERMAL_SHUTDOWN_WARNING = "PNW.clickedThermalShutdownWarning";
    private static final String ACTION_DISMISSED_TEMP_WARNING = "PNW.dismissedTempWarning";
    private static final String ACTION_DISMISSED_THERMAL_SHUTDOWN_WARNING = "PNW.dismissedThermalShutdownWarning";
    private static final String ACTION_DISMISSED_WARNING = "PNW.dismissedWarning";
    private static final String ACTION_DISMISS_AUTO_SAVER_SUGGESTION = "PNW.dismissAutoSaverSuggestion";
    private static final String ACTION_ENABLE_AUTO_SAVER = "PNW.enableAutoSaver";
    private static final String ACTION_SHOW_AUTO_SAVER_SUGGESTION = "PNW.autoSaverSuggestion";
    private static final String ACTION_SHOW_BATTERY_SETTINGS = "PNW.batterySettings";
    private static final String ACTION_SHOW_START_SAVER_CONFIRMATION = "PNW.startSaverConfirmation";
    private static final String ACTION_START_SAVER = "PNW.startSaver";
    private static final String BATTERY_SAVER_DESCRIPTION_URL_KEY = "url";
    public static final String BATTERY_SAVER_SCHEDULE_SCREEN_INTENT_ACTION = "com.android.settings.BATTERY_SAVER_SCHEDULE_SETTINGS";
    public static final String EXTRA_CONFIRM_ONLY = "extra_confirm_only";
    private static final String SETTINGS_ACTION_OPEN_BATTERY_SAVER_SETTING = "android.settings.BATTERY_SAVER_SETTINGS";
    private static final int SHOWING_AUTO_SAVER_SUGGESTION = 4;
    private static final int SHOWING_INVALID_CHARGER = 3;
    private static final int SHOWING_NOTHING = 0;
    private static final int SHOWING_WARNING = 1;
    private static final String TAG = "PowerUI.Notification";
    private static final String TAG_AUTO_SAVER = "auto_saver";
    private static final String TAG_BATTERY = "low_battery";
    private static final String TAG_TEMPERATURE = "high_temp";
    private ActivityStarter mActivityStarter;
    private int mBatteryLevel;
    private int mBucket;
    private final Context mContext;
    private BatteryStateSnapshot mCurrentBatterySnapshot;
    private SystemUIDialog mHighTempDialog;
    private boolean mHighTempWarning;
    private boolean mInvalidCharger;
    private final KeyguardManager mKeyguard;
    private final NotificationManager mNoMan;
    private boolean mPlaySound;
    private final PowerManager mPowerMan;
    private SystemUIDialog mSaverConfirmation;
    private SystemUIDialog mSaverEnabledConfirmation;
    private long mScreenOffTime;
    private boolean mShowAutoSaverSuggestion;
    private int mShowing;
    private SystemUIDialog mThermalShutdownDialog;
    @VisibleForTesting
    SystemUIDialog mUsbHighTempDialog;
    private boolean mWarning;
    private long mWarningTriggerTimeMs;
    private static final boolean DEBUG = PowerUI.DEBUG;
    private static final String[] SHOWING_STRINGS = {"SHOWING_NOTHING", "SHOWING_WARNING", "SHOWING_SAVER", "SHOWING_INVALID_CHARGER", "SHOWING_AUTO_SAVER_SUGGESTION"};
    private static final AudioAttributes AUDIO_ATTRIBUTES = new AudioAttributes.Builder().setContentType(4).setUsage(13).build();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Receiver mReceiver = new Receiver();
    private final Intent mOpenBatterySettings = settings("android.intent.action.POWER_USAGE_SUMMARY");

    @Inject
    public PowerNotificationWarnings(Context context, ActivityStarter activityStarter) {
        this.mContext = context;
        this.mNoMan = (NotificationManager) this.mContext.getSystemService(NotificationManager.class);
        this.mPowerMan = (PowerManager) context.getSystemService("power");
        this.mKeyguard = (KeyguardManager) this.mContext.getSystemService(KeyguardManager.class);
        this.mReceiver.init();
        this.mActivityStarter = activityStarter;
    }

    @Override // com.android.systemui.power.PowerUI.WarningsUI
    public void dump(PrintWriter pw) {
        pw.print("mWarning=");
        pw.println(this.mWarning);
        pw.print("mPlaySound=");
        pw.println(this.mPlaySound);
        pw.print("mInvalidCharger=");
        pw.println(this.mInvalidCharger);
        pw.print("mShowing=");
        pw.println(SHOWING_STRINGS[this.mShowing]);
        pw.print("mSaverConfirmation=");
        pw.println(this.mSaverConfirmation != null ? "not null" : null);
        pw.print("mSaverEnabledConfirmation=");
        pw.print("mHighTempWarning=");
        pw.println(this.mHighTempWarning);
        pw.print("mHighTempDialog=");
        pw.println(this.mHighTempDialog != null ? "not null" : null);
        pw.print("mThermalShutdownDialog=");
        pw.println(this.mThermalShutdownDialog != null ? "not null" : null);
        pw.print("mUsbHighTempDialog=");
        pw.println(this.mUsbHighTempDialog == null ? null : "not null");
    }

    private int getLowBatteryAutoTriggerDefaultLevel() {
        return this.mContext.getResources().getInteger(17694826);
    }

    @Override // com.android.systemui.power.PowerUI.WarningsUI
    public void update(int batteryLevel, int bucket, long screenOffTime) {
        this.mBatteryLevel = batteryLevel;
        if (bucket >= 0) {
            this.mWarningTriggerTimeMs = 0L;
        } else if (bucket < this.mBucket) {
            this.mWarningTriggerTimeMs = System.currentTimeMillis();
        }
        this.mBucket = bucket;
        this.mScreenOffTime = screenOffTime;
    }

    @Override // com.android.systemui.power.PowerUI.WarningsUI
    public void updateSnapshot(BatteryStateSnapshot snapshot) {
        this.mCurrentBatterySnapshot = snapshot;
    }

    private void updateNotification() {
        if (DEBUG) {
            Slog.d(TAG, "updateNotification mWarning=" + this.mWarning + " mPlaySound=" + this.mPlaySound + " mInvalidCharger=" + this.mInvalidCharger);
        }
        if (this.mInvalidCharger) {
            showInvalidChargerNotification();
            this.mShowing = 3;
        } else if (this.mWarning) {
            showWarningNotification();
            this.mShowing = 1;
        } else if (this.mShowAutoSaverSuggestion) {
            if (this.mShowing != 4) {
                showAutoSaverSuggestionNotification();
            }
            this.mShowing = 4;
        } else {
            this.mNoMan.cancelAsUser(TAG_BATTERY, 2, UserHandle.ALL);
            this.mNoMan.cancelAsUser(TAG_BATTERY, 3, UserHandle.ALL);
            this.mNoMan.cancelAsUser(TAG_AUTO_SAVER, 49, UserHandle.ALL);
            this.mShowing = 0;
        }
    }

    private void showInvalidChargerNotification() {
        Notification.Builder nb = new Notification.Builder(this.mContext, NotificationChannels.ALERTS).setSmallIcon(R.drawable.ic_power_low).setWhen(0L).setShowWhen(false).setOngoing(true).setContentTitle(this.mContext.getString(R.string.invalid_charger_title)).setContentText(this.mContext.getString(R.string.invalid_charger_text)).setColor(this.mContext.getColor(17170460));
        SystemUI.overrideNotificationAppName(this.mContext, nb, false);
        Notification n = nb.build();
        this.mNoMan.cancelAsUser(TAG_BATTERY, 3, UserHandle.ALL);
        this.mNoMan.notifyAsUser(TAG_BATTERY, 2, n, UserHandle.ALL);
    }

    protected void showWarningNotification() {
        String contentText;
        String percentage = NumberFormat.getPercentInstance().format(this.mCurrentBatterySnapshot.getBatteryLevel() / 100.0d);
        String title = this.mContext.getString(R.string.battery_low_title);
        if (this.mCurrentBatterySnapshot.isHybrid()) {
            contentText = getHybridContentString(percentage);
        } else {
            contentText = this.mContext.getString(R.string.battery_low_percent_format, percentage);
        }
        Notification.Builder nb = new Notification.Builder(this.mContext, NotificationChannels.BATTERY).setSmallIcon(R.drawable.ic_power_low).setWhen(this.mWarningTriggerTimeMs).setShowWhen(false).setContentText(contentText).setContentTitle(title).setOnlyAlertOnce(true).setDeleteIntent(pendingBroadcast(ACTION_DISMISSED_WARNING)).setStyle(new Notification.BigTextStyle().bigText(contentText)).setVisibility(1);
        if (hasBatterySettings()) {
            nb.setContentIntent(pendingBroadcast(ACTION_SHOW_BATTERY_SETTINGS));
        }
        if (!this.mCurrentBatterySnapshot.isHybrid() || this.mBucket < 0 || this.mCurrentBatterySnapshot.getTimeRemainingMillis() < this.mCurrentBatterySnapshot.getSevereThresholdMillis()) {
            nb.setColor(Utils.getColorAttrDefaultColor(this.mContext, 16844099));
        }
        if (!this.mPowerMan.isPowerSaveMode()) {
            nb.addAction(0, this.mContext.getString(R.string.battery_saver_start_action), pendingBroadcast(ACTION_START_SAVER));
        }
        nb.setOnlyAlertOnce(true ^ this.mPlaySound);
        this.mPlaySound = false;
        SystemUI.overrideNotificationAppName(this.mContext, nb, false);
        Notification n = nb.build();
        this.mNoMan.cancelAsUser(TAG_BATTERY, 2, UserHandle.ALL);
        this.mNoMan.notifyAsUser(TAG_BATTERY, 3, n, UserHandle.ALL);
    }

    private void showAutoSaverSuggestionNotification() {
        Notification.Builder nb = new Notification.Builder(this.mContext, NotificationChannels.HINTS).setSmallIcon(R.drawable.ic_power_saver).setWhen(0L).setShowWhen(false).setContentTitle(this.mContext.getString(R.string.auto_saver_title)).setContentText(this.mContext.getString(R.string.auto_saver_text));
        nb.setContentIntent(pendingBroadcast(ACTION_ENABLE_AUTO_SAVER));
        nb.setDeleteIntent(pendingBroadcast(ACTION_DISMISS_AUTO_SAVER_SUGGESTION));
        nb.addAction(0, this.mContext.getString(R.string.no_auto_saver_action), pendingBroadcast(ACTION_AUTO_SAVER_NO_THANKS));
        SystemUI.overrideNotificationAppName(this.mContext, nb, false);
        Notification n = nb.build();
        this.mNoMan.notifyAsUser(TAG_AUTO_SAVER, 49, n, UserHandle.ALL);
    }

    private String getHybridContentString(String percentage) {
        return PowerUtil.getBatteryRemainingStringFormatted(this.mContext, this.mCurrentBatterySnapshot.getTimeRemainingMillis(), percentage, this.mCurrentBatterySnapshot.isBasedOnUsage());
    }

    private PendingIntent pendingBroadcast(String action) {
        return PendingIntent.getBroadcastAsUser(this.mContext, 0, new Intent(action).setPackage(this.mContext.getPackageName()).setFlags(268435456), 0, UserHandle.CURRENT);
    }

    private static Intent settings(String action) {
        return new Intent(action).setFlags(1551892480);
    }

    @Override // com.android.systemui.power.PowerUI.WarningsUI
    public boolean isInvalidChargerWarningShowing() {
        return this.mInvalidCharger;
    }

    @Override // com.android.systemui.power.PowerUI.WarningsUI
    public void dismissHighTemperatureWarning() {
        if (!this.mHighTempWarning) {
            return;
        }
        dismissHighTemperatureWarningInternal();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissHighTemperatureWarningInternal() {
        this.mNoMan.cancelAsUser(TAG_TEMPERATURE, 4, UserHandle.ALL);
        this.mHighTempWarning = false;
    }

    @Override // com.android.systemui.power.PowerUI.WarningsUI
    public void showHighTemperatureWarning() {
        if (this.mHighTempWarning) {
            return;
        }
        this.mHighTempWarning = true;
        Notification.Builder nb = new Notification.Builder(this.mContext, NotificationChannels.ALERTS).setSmallIcon(R.drawable.ic_device_thermostat_24).setWhen(0L).setShowWhen(false).setContentTitle(this.mContext.getString(R.string.high_temp_title)).setContentText(this.mContext.getString(R.string.high_temp_notif_message)).setVisibility(1).setContentIntent(pendingBroadcast(ACTION_CLICKED_TEMP_WARNING)).setDeleteIntent(pendingBroadcast(ACTION_DISMISSED_TEMP_WARNING)).setColor(Utils.getColorAttrDefaultColor(this.mContext, 16844099));
        SystemUI.overrideNotificationAppName(this.mContext, nb, false);
        Notification n = nb.build();
        this.mNoMan.notifyAsUser(TAG_TEMPERATURE, 4, n, UserHandle.ALL);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showHighTemperatureDialog() {
        if (this.mHighTempDialog != null) {
            return;
        }
        SystemUIDialog d = new SystemUIDialog(this.mContext);
        d.setIconAttribute(16843605);
        d.setTitle(R.string.high_temp_title);
        d.setMessage(R.string.high_temp_dialog_message);
        d.setPositiveButton(17039370, null);
        d.setShowForAllUsers(true);
        d.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.systemui.power.-$$Lambda$PowerNotificationWarnings$PU_JpsxNcz7jXGNa_DRkuMbEWwU
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                PowerNotificationWarnings.this.lambda$showHighTemperatureDialog$0$PowerNotificationWarnings(dialogInterface);
            }
        });
        d.show();
        this.mHighTempDialog = d;
    }

    public /* synthetic */ void lambda$showHighTemperatureDialog$0$PowerNotificationWarnings(DialogInterface dialog) {
        this.mHighTempDialog = null;
    }

    @VisibleForTesting
    void dismissThermalShutdownWarning() {
        this.mNoMan.cancelAsUser(TAG_TEMPERATURE, 39, UserHandle.ALL);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showThermalShutdownDialog() {
        if (this.mThermalShutdownDialog != null) {
            return;
        }
        SystemUIDialog d = new SystemUIDialog(this.mContext);
        d.setIconAttribute(16843605);
        d.setTitle(R.string.thermal_shutdown_title);
        d.setMessage(R.string.thermal_shutdown_dialog_message);
        d.setPositiveButton(17039370, null);
        d.setShowForAllUsers(true);
        d.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.systemui.power.-$$Lambda$PowerNotificationWarnings$O5nkGS5PG2ihQrXqunpOO_aZDms
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                PowerNotificationWarnings.this.lambda$showThermalShutdownDialog$1$PowerNotificationWarnings(dialogInterface);
            }
        });
        d.show();
        this.mThermalShutdownDialog = d;
    }

    public /* synthetic */ void lambda$showThermalShutdownDialog$1$PowerNotificationWarnings(DialogInterface dialog) {
        this.mThermalShutdownDialog = null;
    }

    @Override // com.android.systemui.power.PowerUI.WarningsUI
    public void showThermalShutdownWarning() {
        Notification.Builder nb = new Notification.Builder(this.mContext, NotificationChannels.ALERTS).setSmallIcon(R.drawable.ic_device_thermostat_24).setWhen(0L).setShowWhen(false).setContentTitle(this.mContext.getString(R.string.thermal_shutdown_title)).setContentText(this.mContext.getString(R.string.thermal_shutdown_message)).setVisibility(1).setContentIntent(pendingBroadcast(ACTION_CLICKED_THERMAL_SHUTDOWN_WARNING)).setDeleteIntent(pendingBroadcast(ACTION_DISMISSED_THERMAL_SHUTDOWN_WARNING)).setColor(Utils.getColorAttrDefaultColor(this.mContext, 16844099));
        SystemUI.overrideNotificationAppName(this.mContext, nb, false);
        Notification n = nb.build();
        this.mNoMan.notifyAsUser(TAG_TEMPERATURE, 39, n, UserHandle.ALL);
    }

    @Override // com.android.systemui.power.PowerUI.WarningsUI
    public void showUsbHighTemperatureAlarm() {
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.power.-$$Lambda$PowerNotificationWarnings$BgW0sVGH4tN6GoBK_M1noXhk8wA
            @Override // java.lang.Runnable
            public final void run() {
                PowerNotificationWarnings.this.lambda$showUsbHighTemperatureAlarm$2$PowerNotificationWarnings();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: showUsbHighTemperatureAlarmInternal */
    public void lambda$showUsbHighTemperatureAlarm$2$PowerNotificationWarnings() {
        if (this.mUsbHighTempDialog != null) {
            return;
        }
        SystemUIDialog d = new SystemUIDialog(this.mContext, R.style.Theme_SystemUI_Dialog_Alert);
        d.setCancelable(false);
        d.setIconAttribute(16843605);
        d.setTitle(R.string.high_temp_alarm_title);
        d.setShowForAllUsers(true);
        d.setMessage(this.mContext.getString(R.string.high_temp_alarm_notify_message, ""));
        d.setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.systemui.power.-$$Lambda$PowerNotificationWarnings$wL6F1WmvK9p9dyYXQnu9ScZBxSA
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                PowerNotificationWarnings.this.lambda$showUsbHighTemperatureAlarmInternal$3$PowerNotificationWarnings(dialogInterface, i);
            }
        });
        d.setNegativeButton(R.string.high_temp_alarm_help_care_steps, new DialogInterface.OnClickListener() { // from class: com.android.systemui.power.-$$Lambda$PowerNotificationWarnings$dkzsXROJlAvy2zSj_OYf-kxpKFc
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                PowerNotificationWarnings.this.lambda$showUsbHighTemperatureAlarmInternal$5$PowerNotificationWarnings(dialogInterface, i);
            }
        });
        d.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.systemui.power.-$$Lambda$PowerNotificationWarnings$_C7Tc72CcSASuMrkeFnJC4Oj07o
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                PowerNotificationWarnings.this.lambda$showUsbHighTemperatureAlarmInternal$6$PowerNotificationWarnings(dialogInterface);
            }
        });
        d.getWindow().addFlags(2097280);
        d.show();
        this.mUsbHighTempDialog = d;
        Events.writeEvent(this.mContext, 19, 3, Boolean.valueOf(this.mKeyguard.isKeyguardLocked()));
    }

    public /* synthetic */ void lambda$showUsbHighTemperatureAlarmInternal$3$PowerNotificationWarnings(DialogInterface dialogInterface, int which) {
        this.mUsbHighTempDialog = null;
    }

    public /* synthetic */ void lambda$showUsbHighTemperatureAlarmInternal$5$PowerNotificationWarnings(DialogInterface dialogInterface, int which) {
        String contextString = this.mContext.getString(R.string.high_temp_alarm_help_url);
        Intent helpIntent = new Intent();
        helpIntent.setClassName("com.android.settings", "com.android.settings.HelpTrampoline");
        helpIntent.putExtra("android.intent.extra.TEXT", contextString);
        ((ActivityStarter) Dependency.get(ActivityStarter.class)).startActivity(helpIntent, true, new ActivityStarter.Callback() { // from class: com.android.systemui.power.-$$Lambda$PowerNotificationWarnings$aDUeHG-2fyaQA2OArgzN2VFmIKQ
            @Override // com.android.systemui.plugins.ActivityStarter.Callback
            public final void onActivityStarted(int i) {
                PowerNotificationWarnings.this.lambda$showUsbHighTemperatureAlarmInternal$4$PowerNotificationWarnings(i);
            }
        });
    }

    public /* synthetic */ void lambda$showUsbHighTemperatureAlarmInternal$4$PowerNotificationWarnings(int resultCode) {
        this.mUsbHighTempDialog = null;
    }

    public /* synthetic */ void lambda$showUsbHighTemperatureAlarmInternal$6$PowerNotificationWarnings(DialogInterface dialogInterface) {
        this.mUsbHighTempDialog = null;
        Events.writeEvent(this.mContext, 20, 9, Boolean.valueOf(this.mKeyguard.isKeyguardLocked()));
    }

    @Override // com.android.systemui.power.PowerUI.WarningsUI
    public void updateLowBatteryWarning() {
        updateNotification();
    }

    @Override // com.android.systemui.power.PowerUI.WarningsUI
    public void dismissLowBatteryWarning() {
        if (DEBUG) {
            Slog.d(TAG, "dismissing low battery warning: level=" + this.mBatteryLevel);
        }
        dismissLowBatteryNotification();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissLowBatteryNotification() {
        if (this.mWarning) {
            Slog.i(TAG, "dismissing low battery notification");
        }
        this.mWarning = false;
        updateNotification();
    }

    private boolean hasBatterySettings() {
        return this.mOpenBatterySettings.resolveActivity(this.mContext.getPackageManager()) != null;
    }

    @Override // com.android.systemui.power.PowerUI.WarningsUI
    public void showLowBatteryWarning(boolean playSound) {
        Slog.i(TAG, "show low battery warning: level=" + this.mBatteryLevel + " [" + this.mBucket + "] playSound=" + playSound);
        this.mPlaySound = playSound;
        this.mWarning = true;
        updateNotification();
    }

    @Override // com.android.systemui.power.PowerUI.WarningsUI
    public void dismissInvalidChargerWarning() {
        dismissInvalidChargerNotification();
    }

    private void dismissInvalidChargerNotification() {
        if (this.mInvalidCharger) {
            Slog.i(TAG, "dismissing invalid charger notification");
        }
        this.mInvalidCharger = false;
        updateNotification();
    }

    @Override // com.android.systemui.power.PowerUI.WarningsUI
    public void showInvalidChargerWarning() {
        this.mInvalidCharger = true;
        updateNotification();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showAutoSaverSuggestion() {
        this.mShowAutoSaverSuggestion = true;
        updateNotification();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissAutoSaverSuggestion() {
        this.mShowAutoSaverSuggestion = false;
        updateNotification();
    }

    @Override // com.android.systemui.power.PowerUI.WarningsUI
    public void userSwitched() {
        updateNotification();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showStartSaverConfirmation(Bundle extras) {
        if (this.mSaverConfirmation != null) {
            return;
        }
        SystemUIDialog d = new SystemUIDialog(this.mContext);
        boolean confirmOnly = extras.getBoolean("extra_confirm_only");
        final int batterySaverTriggerMode = extras.getInt(BatterySaverUtils.EXTRA_POWER_SAVE_MODE_TRIGGER, 0);
        final int batterySaverTriggerLevel = extras.getInt(BatterySaverUtils.EXTRA_POWER_SAVE_MODE_TRIGGER_LEVEL, 0);
        d.setMessage(getBatterySaverDescription());
        if (isEnglishLocale()) {
            d.setMessageHyphenationFrequency(0);
        }
        d.setMessageMovementMethod(LinkMovementMethod.getInstance());
        if (confirmOnly) {
            d.setTitle(R.string.battery_saver_confirmation_title_generic);
            d.setPositiveButton(17039802, new DialogInterface.OnClickListener() { // from class: com.android.systemui.power.-$$Lambda$PowerNotificationWarnings$i9YMNbne4kaewl8DwiUWlEIhHLU
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PowerNotificationWarnings.this.lambda$showStartSaverConfirmation$7$PowerNotificationWarnings(batterySaverTriggerMode, batterySaverTriggerLevel, dialogInterface, i);
                }
            });
        } else {
            d.setTitle(R.string.battery_saver_confirmation_title);
            d.setPositiveButton(R.string.battery_saver_confirmation_ok, new DialogInterface.OnClickListener() { // from class: com.android.systemui.power.-$$Lambda$PowerNotificationWarnings$Uf-fCz3D5JaMRKgj_soLcPpUL04
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PowerNotificationWarnings.this.lambda$showStartSaverConfirmation$8$PowerNotificationWarnings(dialogInterface, i);
                }
            });
            d.setNegativeButton(17039360, null);
        }
        d.setShowForAllUsers(true);
        d.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.systemui.power.-$$Lambda$PowerNotificationWarnings$AE5LLn9E8Dx1b7_xgN4SxgDN7R4
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                PowerNotificationWarnings.this.lambda$showStartSaverConfirmation$9$PowerNotificationWarnings(dialogInterface);
            }
        });
        d.show();
        this.mSaverConfirmation = d;
    }

    public /* synthetic */ void lambda$showStartSaverConfirmation$7$PowerNotificationWarnings(int batterySaverTriggerMode, int batterySaverTriggerLevel, DialogInterface dialog, int which) {
        ContentResolver resolver = this.mContext.getContentResolver();
        Settings.Global.putInt(resolver, "automatic_power_save_mode", batterySaverTriggerMode);
        Settings.Global.putInt(resolver, "low_power_trigger_level", batterySaverTriggerLevel);
        Settings.Secure.putInt(resolver, "low_power_warning_acknowledged", 1);
    }

    public /* synthetic */ void lambda$showStartSaverConfirmation$8$PowerNotificationWarnings(DialogInterface dialog, int which) {
        setSaverMode(true, false);
    }

    public /* synthetic */ void lambda$showStartSaverConfirmation$9$PowerNotificationWarnings(DialogInterface dialog) {
        this.mSaverConfirmation = null;
    }

    private boolean isEnglishLocale() {
        return Objects.equals(Locale.getDefault().getLanguage(), Locale.ENGLISH.getLanguage());
    }

    private CharSequence getBatterySaverDescription() {
        Annotation[] annotationArr;
        String learnMoreUrl = this.mContext.getText(R.string.help_uri_battery_saver_learn_more_link_target).toString();
        if (TextUtils.isEmpty(learnMoreUrl)) {
            return this.mContext.getText(17039601);
        }
        CharSequence rawText = this.mContext.getText(17039602);
        SpannableString message = new SpannableString(rawText);
        SpannableStringBuilder builder = new SpannableStringBuilder(message);
        for (Annotation annotation : (Annotation[]) message.getSpans(0, message.length(), Annotation.class)) {
            String key = annotation.getValue();
            if ("url".equals(key)) {
                int start = message.getSpanStart(annotation);
                int end = message.getSpanEnd(annotation);
                URLSpan urlSpan = new URLSpan(learnMoreUrl) { // from class: com.android.systemui.power.PowerNotificationWarnings.1
                    @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                    }

                    @Override // android.text.style.URLSpan, android.text.style.ClickableSpan
                    public void onClick(View widget) {
                        if (PowerNotificationWarnings.this.mSaverConfirmation != null) {
                            PowerNotificationWarnings.this.mSaverConfirmation.dismiss();
                        }
                        PowerNotificationWarnings.this.mContext.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS").setFlags(268435456));
                        Uri uri = Uri.parse(getURL());
                        Context context = widget.getContext();
                        Intent intent = new Intent("android.intent.action.VIEW", uri).setFlags(268435456);
                        try {
                            context.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Log.w(PowerNotificationWarnings.TAG, "Activity was not found for intent, " + intent.toString());
                        }
                    }
                };
                builder.setSpan(urlSpan, start, end, message.getSpanFlags(urlSpan));
            }
        }
        return builder;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSaverMode(boolean mode, boolean needFirstTimeWarning) {
        BatterySaverUtils.setPowerSaveMode(this.mContext, mode, needFirstTimeWarning);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startBatterySaverSchedulePage() {
        Intent intent = new Intent(BATTERY_SAVER_SCHEDULE_SCREEN_INTENT_ACTION);
        intent.setFlags(268468224);
        this.mActivityStarter.startActivity(intent, true);
    }

    /* loaded from: classes21.dex */
    private final class Receiver extends BroadcastReceiver {
        private Receiver() {
        }

        public void init() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(PowerNotificationWarnings.ACTION_SHOW_BATTERY_SETTINGS);
            filter.addAction(PowerNotificationWarnings.ACTION_START_SAVER);
            filter.addAction(PowerNotificationWarnings.ACTION_DISMISSED_WARNING);
            filter.addAction(PowerNotificationWarnings.ACTION_CLICKED_TEMP_WARNING);
            filter.addAction(PowerNotificationWarnings.ACTION_DISMISSED_TEMP_WARNING);
            filter.addAction(PowerNotificationWarnings.ACTION_CLICKED_THERMAL_SHUTDOWN_WARNING);
            filter.addAction(PowerNotificationWarnings.ACTION_DISMISSED_THERMAL_SHUTDOWN_WARNING);
            filter.addAction("PNW.startSaverConfirmation");
            filter.addAction("PNW.autoSaverSuggestion");
            filter.addAction(PowerNotificationWarnings.ACTION_ENABLE_AUTO_SAVER);
            filter.addAction(PowerNotificationWarnings.ACTION_AUTO_SAVER_NO_THANKS);
            filter.addAction(PowerNotificationWarnings.ACTION_DISMISS_AUTO_SAVER_SUGGESTION);
            PowerNotificationWarnings.this.mContext.registerReceiverAsUser(this, UserHandle.ALL, filter, "android.permission.DEVICE_POWER", PowerNotificationWarnings.this.mHandler);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Slog.i(PowerNotificationWarnings.TAG, "Received " + action);
            if (action.equals(PowerNotificationWarnings.ACTION_SHOW_BATTERY_SETTINGS)) {
                PowerNotificationWarnings.this.dismissLowBatteryNotification();
                PowerNotificationWarnings.this.mContext.startActivityAsUser(PowerNotificationWarnings.this.mOpenBatterySettings, UserHandle.CURRENT);
            } else if (action.equals(PowerNotificationWarnings.ACTION_START_SAVER)) {
                PowerNotificationWarnings.this.setSaverMode(true, true);
                PowerNotificationWarnings.this.dismissLowBatteryNotification();
            } else if (action.equals("PNW.startSaverConfirmation")) {
                PowerNotificationWarnings.this.dismissLowBatteryNotification();
                PowerNotificationWarnings.this.showStartSaverConfirmation(intent.getExtras());
            } else if (action.equals(PowerNotificationWarnings.ACTION_DISMISSED_WARNING)) {
                PowerNotificationWarnings.this.dismissLowBatteryWarning();
            } else if (PowerNotificationWarnings.ACTION_CLICKED_TEMP_WARNING.equals(action)) {
                PowerNotificationWarnings.this.dismissHighTemperatureWarningInternal();
                PowerNotificationWarnings.this.showHighTemperatureDialog();
            } else if (PowerNotificationWarnings.ACTION_DISMISSED_TEMP_WARNING.equals(action)) {
                PowerNotificationWarnings.this.dismissHighTemperatureWarningInternal();
            } else if (PowerNotificationWarnings.ACTION_CLICKED_THERMAL_SHUTDOWN_WARNING.equals(action)) {
                PowerNotificationWarnings.this.dismissThermalShutdownWarning();
                PowerNotificationWarnings.this.showThermalShutdownDialog();
            } else if (PowerNotificationWarnings.ACTION_DISMISSED_THERMAL_SHUTDOWN_WARNING.equals(action)) {
                PowerNotificationWarnings.this.dismissThermalShutdownWarning();
            } else if ("PNW.autoSaverSuggestion".equals(action)) {
                PowerNotificationWarnings.this.showAutoSaverSuggestion();
            } else if (PowerNotificationWarnings.ACTION_DISMISS_AUTO_SAVER_SUGGESTION.equals(action)) {
                PowerNotificationWarnings.this.dismissAutoSaverSuggestion();
            } else if (PowerNotificationWarnings.ACTION_ENABLE_AUTO_SAVER.equals(action)) {
                PowerNotificationWarnings.this.dismissAutoSaverSuggestion();
                PowerNotificationWarnings.this.startBatterySaverSchedulePage();
            } else if (PowerNotificationWarnings.ACTION_AUTO_SAVER_NO_THANKS.equals(action)) {
                PowerNotificationWarnings.this.dismissAutoSaverSuggestion();
                BatterySaverUtils.suppressAutoBatterySaver(context);
            }
        }
    }
}
