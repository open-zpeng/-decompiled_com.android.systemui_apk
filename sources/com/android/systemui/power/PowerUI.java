package com.android.systemui.power;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IThermalEventListener;
import android.os.IThermalService;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.Temperature;
import android.provider.Settings;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.fuelgauge.Estimate;
import com.android.settingslib.utils.ThreadUtils;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.SystemUI;
import com.android.systemui.power.PowerUI;
import com.android.systemui.statusbar.phone.StatusBar;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.Future;
/* loaded from: classes21.dex */
public class PowerUI extends SystemUI {
    private static final String BOOT_COUNT_KEY = "boot_count";
    private static final int CHARGE_CYCLE_PERCENT_RESET = 45;
    private static final int MAX_RECENT_TEMPS = 125;
    public static final int NO_ESTIMATE_AVAILABLE = -1;
    private static final String PREFS = "powerui_prefs";
    private static final long TEMPERATURE_INTERVAL = 30000;
    private static final long TEMPERATURE_LOGGING_INTERVAL = 3600000;
    static final long THREE_HOURS_IN_MILLIS = 10800000;
    @VisibleForTesting
    BatteryStateSnapshot mCurrentBatteryStateSnapshot;
    private boolean mEnableSkinTemperatureWarning;
    private boolean mEnableUsbTemperatureAlarm;
    private EnhancedEstimates mEnhancedEstimates;
    @VisibleForTesting
    BatteryStateSnapshot mLastBatteryStateSnapshot;
    private Future mLastShowWarningTask;
    private int mLowBatteryAlertCloseLevel;
    @VisibleForTesting
    boolean mLowWarningShownThisChargeCycle;
    private PowerManager mPowerManager;
    @VisibleForTesting
    boolean mSevereWarningShownThisChargeCycle;
    private IThermalEventListener mSkinThermalEventListener;
    @VisibleForTesting
    IThermalService mThermalService;
    private IThermalEventListener mUsbThermalEventListener;
    private WarningsUI mWarnings;
    static final String TAG = "PowerUI";
    static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private static final long SIX_HOURS_MILLIS = Duration.ofHours(6).toMillis();
    private final Handler mHandler = new Handler();
    @VisibleForTesting
    final Receiver mReceiver = new Receiver();
    private final Configuration mLastConfiguration = new Configuration();
    private int mPlugType = 0;
    private int mInvalidCharger = 0;
    private final int[] mLowBatteryReminderLevels = new int[2];
    private long mScreenOffTime = -1;
    @VisibleForTesting
    int mBatteryLevel = 100;
    @VisibleForTesting
    int mBatteryStatus = 1;

    /* loaded from: classes21.dex */
    public interface WarningsUI {
        void dismissHighTemperatureWarning();

        void dismissInvalidChargerWarning();

        void dismissLowBatteryWarning();

        void dump(PrintWriter printWriter);

        boolean isInvalidChargerWarningShowing();

        void showHighTemperatureWarning();

        void showInvalidChargerWarning();

        void showLowBatteryWarning(boolean z);

        void showThermalShutdownWarning();

        void showUsbHighTemperatureAlarm();

        void update(int i, int i2, long j);

        void updateLowBatteryWarning();

        void updateSnapshot(BatteryStateSnapshot batteryStateSnapshot);

        void userSwitched();
    }

    @Override // com.android.systemui.SystemUI
    public void start() {
        this.mPowerManager = (PowerManager) this.mContext.getSystemService("power");
        this.mScreenOffTime = this.mPowerManager.isScreenOn() ? -1L : SystemClock.elapsedRealtime();
        this.mWarnings = (WarningsUI) Dependency.get(WarningsUI.class);
        this.mEnhancedEstimates = (EnhancedEstimates) Dependency.get(EnhancedEstimates.class);
        this.mLastConfiguration.setTo(this.mContext.getResources().getConfiguration());
        ContentObserver obs = new ContentObserver(this.mHandler) { // from class: com.android.systemui.power.PowerUI.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                PowerUI.this.updateBatteryWarningLevels();
            }
        };
        ContentResolver resolver = this.mContext.getContentResolver();
        resolver.registerContentObserver(Settings.Global.getUriFor("low_power_trigger_level"), false, obs, -1);
        updateBatteryWarningLevels();
        this.mReceiver.init();
        showWarnOnThermalShutdown();
        resolver.registerContentObserver(Settings.Global.getUriFor("show_temperature_warning"), false, new ContentObserver(this.mHandler) { // from class: com.android.systemui.power.PowerUI.2
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                PowerUI.this.doSkinThermalEventListenerRegistration();
            }
        });
        resolver.registerContentObserver(Settings.Global.getUriFor("show_usb_temperature_alarm"), false, new ContentObserver(this.mHandler) { // from class: com.android.systemui.power.PowerUI.3
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                PowerUI.this.doUsbThermalEventListenerRegistration();
            }
        });
        initThermalEventListeners();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.SystemUI
    public void onConfigurationChanged(Configuration newConfig) {
        if ((this.mLastConfiguration.updateFrom(newConfig) & 3) != 0) {
            this.mHandler.post(new Runnable() { // from class: com.android.systemui.power.-$$Lambda$PowerUI$QV7l9YjJI0jIQa7PQUr5PFep9Kg
                @Override // java.lang.Runnable
                public final void run() {
                    PowerUI.this.initThermalEventListeners();
                }
            });
        }
    }

    void updateBatteryWarningLevels() {
        int critLevel = this.mContext.getResources().getInteger(17694763);
        int warnLevel = this.mContext.getResources().getInteger(17694828);
        if (warnLevel < critLevel) {
            warnLevel = critLevel;
        }
        int[] iArr = this.mLowBatteryReminderLevels;
        iArr[0] = warnLevel;
        iArr[1] = critLevel;
        this.mLowBatteryAlertCloseLevel = iArr[0] + this.mContext.getResources().getInteger(17694827);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int findBatteryLevelBucket(int level) {
        if (level >= this.mLowBatteryAlertCloseLevel) {
            return 1;
        }
        int[] iArr = this.mLowBatteryReminderLevels;
        if (level > iArr[0]) {
            return 0;
        }
        int N = iArr.length;
        for (int i = N - 1; i >= 0; i--) {
            if (level <= this.mLowBatteryReminderLevels[i]) {
                return (-1) - i;
            }
        }
        throw new RuntimeException("not possible!");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* loaded from: classes21.dex */
    public final class Receiver extends BroadcastReceiver {
        Receiver() {
        }

        public void init() {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
            filter.addAction("android.intent.action.BATTERY_CHANGED");
            filter.addAction("android.intent.action.SCREEN_OFF");
            filter.addAction("android.intent.action.SCREEN_ON");
            filter.addAction("android.intent.action.USER_SWITCHED");
            PowerUI.this.mContext.registerReceiver(this, filter, null, PowerUI.this.mHandler);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.os.action.POWER_SAVE_MODE_CHANGED".equals(action)) {
                ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.systemui.power.-$$Lambda$PowerUI$Receiver$r1RcZjs8DVXWaC4Afqm8W0WAvm8
                    @Override // java.lang.Runnable
                    public final void run() {
                        PowerUI.Receiver.this.lambda$onReceive$0$PowerUI$Receiver();
                    }
                });
            } else if (!"android.intent.action.BATTERY_CHANGED".equals(action)) {
                if ("android.intent.action.SCREEN_OFF".equals(action)) {
                    PowerUI.this.mScreenOffTime = SystemClock.elapsedRealtime();
                } else if ("android.intent.action.SCREEN_ON".equals(action)) {
                    PowerUI.this.mScreenOffTime = -1L;
                } else if ("android.intent.action.USER_SWITCHED".equals(action)) {
                    PowerUI.this.mWarnings.userSwitched();
                } else {
                    Slog.w(PowerUI.TAG, "unknown intent: " + intent);
                }
            } else {
                int oldBatteryLevel = PowerUI.this.mBatteryLevel;
                PowerUI.this.mBatteryLevel = intent.getIntExtra("level", 100);
                int oldBatteryStatus = PowerUI.this.mBatteryStatus;
                PowerUI.this.mBatteryStatus = intent.getIntExtra("status", 1);
                int oldPlugType = PowerUI.this.mPlugType;
                PowerUI.this.mPlugType = intent.getIntExtra("plugged", 1);
                int oldInvalidCharger = PowerUI.this.mInvalidCharger;
                PowerUI.this.mInvalidCharger = intent.getIntExtra("invalid_charger", 0);
                PowerUI powerUI = PowerUI.this;
                powerUI.mLastBatteryStateSnapshot = powerUI.mCurrentBatteryStateSnapshot;
                final boolean plugged = PowerUI.this.mPlugType != 0;
                boolean oldPlugged = oldPlugType != 0;
                int oldBucket = PowerUI.this.findBatteryLevelBucket(oldBatteryLevel);
                PowerUI powerUI2 = PowerUI.this;
                final int bucket = powerUI2.findBatteryLevelBucket(powerUI2.mBatteryLevel);
                if (PowerUI.DEBUG) {
                    Slog.d(PowerUI.TAG, "buckets   ....." + PowerUI.this.mLowBatteryAlertCloseLevel + " .. " + PowerUI.this.mLowBatteryReminderLevels[0] + " .. " + PowerUI.this.mLowBatteryReminderLevels[1]);
                    StringBuilder sb = new StringBuilder();
                    sb.append("level          ");
                    sb.append(oldBatteryLevel);
                    sb.append(" --> ");
                    sb.append(PowerUI.this.mBatteryLevel);
                    Slog.d(PowerUI.TAG, sb.toString());
                    Slog.d(PowerUI.TAG, "status         " + oldBatteryStatus + " --> " + PowerUI.this.mBatteryStatus);
                    Slog.d(PowerUI.TAG, "plugType       " + oldPlugType + " --> " + PowerUI.this.mPlugType);
                    Slog.d(PowerUI.TAG, "invalidCharger " + oldInvalidCharger + " --> " + PowerUI.this.mInvalidCharger);
                    Slog.d(PowerUI.TAG, "bucket         " + oldBucket + " --> " + bucket);
                    Slog.d(PowerUI.TAG, "plugged        " + oldPlugged + " --> " + plugged);
                }
                PowerUI.this.mWarnings.update(PowerUI.this.mBatteryLevel, bucket, PowerUI.this.mScreenOffTime);
                if (oldInvalidCharger == 0 && PowerUI.this.mInvalidCharger != 0) {
                    Slog.d(PowerUI.TAG, "showing invalid charger warning");
                    PowerUI.this.mWarnings.showInvalidChargerWarning();
                    return;
                }
                if (oldInvalidCharger == 0 || PowerUI.this.mInvalidCharger != 0) {
                    if (PowerUI.this.mWarnings.isInvalidChargerWarningShowing()) {
                        if (PowerUI.DEBUG) {
                            Slog.d(PowerUI.TAG, "Bad Charger");
                            return;
                        }
                        return;
                    }
                } else {
                    PowerUI.this.mWarnings.dismissInvalidChargerWarning();
                }
                if (PowerUI.this.mLastShowWarningTask != null) {
                    PowerUI.this.mLastShowWarningTask.cancel(true);
                    if (PowerUI.DEBUG) {
                        Slog.d(PowerUI.TAG, "cancelled task");
                    }
                }
                PowerUI.this.mLastShowWarningTask = ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.systemui.power.-$$Lambda$PowerUI$Receiver$YHQ7eAdH8G2eZkWaBryO-zqzv1I
                    @Override // java.lang.Runnable
                    public final void run() {
                        PowerUI.Receiver.this.lambda$onReceive$1$PowerUI$Receiver(plugged, bucket);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onReceive$0$PowerUI$Receiver() {
            if (PowerUI.this.mPowerManager.isPowerSaveMode()) {
                PowerUI.this.mWarnings.dismissLowBatteryWarning();
            }
        }

        public /* synthetic */ void lambda$onReceive$1$PowerUI$Receiver(boolean plugged, int bucket) {
            PowerUI.this.maybeShowBatteryWarningV2(plugged, bucket);
        }
    }

    protected void maybeShowBatteryWarningV2(boolean plugged, int bucket) {
        boolean hybridEnabled = this.mEnhancedEstimates.isHybridNotificationEnabled();
        boolean isPowerSaverMode = this.mPowerManager.isPowerSaveMode();
        if (DEBUG) {
            Slog.d(TAG, "evaluating which notification to show");
        }
        if (!hybridEnabled) {
            boolean hybridEnabled2 = DEBUG;
            if (hybridEnabled2) {
                Slog.d(TAG, "using standard");
            }
            int i = this.mBatteryLevel;
            int i2 = this.mBatteryStatus;
            int[] iArr = this.mLowBatteryReminderLevels;
            this.mCurrentBatteryStateSnapshot = new BatteryStateSnapshot(i, isPowerSaverMode, plugged, bucket, i2, iArr[1], iArr[0]);
        } else {
            if (DEBUG) {
                Slog.d(TAG, "using hybrid");
            }
            Estimate estimate = refreshEstimateIfNeeded();
            int i3 = this.mBatteryLevel;
            int i4 = this.mBatteryStatus;
            int[] iArr2 = this.mLowBatteryReminderLevels;
            this.mCurrentBatteryStateSnapshot = new BatteryStateSnapshot(i3, isPowerSaverMode, plugged, bucket, i4, iArr2[1], iArr2[0], estimate.getEstimateMillis(), estimate.getAverageDischargeTime(), this.mEnhancedEstimates.getSevereWarningThreshold(), this.mEnhancedEstimates.getLowWarningThreshold(), estimate.isBasedOnUsage(), this.mEnhancedEstimates.getLowWarningEnabled());
        }
        this.mWarnings.updateSnapshot(this.mCurrentBatteryStateSnapshot);
        if (this.mCurrentBatteryStateSnapshot.isHybrid()) {
            maybeShowHybridWarning(this.mCurrentBatteryStateSnapshot, this.mLastBatteryStateSnapshot);
        } else {
            maybeShowBatteryWarning(this.mCurrentBatteryStateSnapshot, this.mLastBatteryStateSnapshot);
        }
    }

    @VisibleForTesting
    Estimate refreshEstimateIfNeeded() {
        BatteryStateSnapshot batteryStateSnapshot = this.mLastBatteryStateSnapshot;
        if (batteryStateSnapshot == null || batteryStateSnapshot.getTimeRemainingMillis() == -1 || this.mBatteryLevel != this.mLastBatteryStateSnapshot.getBatteryLevel()) {
            Estimate estimate = this.mEnhancedEstimates.getEstimate();
            if (DEBUG) {
                Slog.d(TAG, "updated estimate: " + estimate.getEstimateMillis());
            }
            return estimate;
        }
        return new Estimate(this.mLastBatteryStateSnapshot.getTimeRemainingMillis(), this.mLastBatteryStateSnapshot.isBasedOnUsage(), this.mLastBatteryStateSnapshot.getAverageTimeToDischargeMillis());
    }

    @VisibleForTesting
    void maybeShowHybridWarning(BatteryStateSnapshot currentSnapshot, BatteryStateSnapshot lastSnapshot) {
        boolean z = false;
        if (currentSnapshot.getBatteryLevel() >= 45 && currentSnapshot.getTimeRemainingMillis() > SIX_HOURS_MILLIS) {
            this.mLowWarningShownThisChargeCycle = false;
            this.mSevereWarningShownThisChargeCycle = false;
            if (DEBUG) {
                Slog.d(TAG, "Charge cycle reset! Can show warnings again");
            }
        }
        if (currentSnapshot.getBucket() != lastSnapshot.getBucket() || lastSnapshot.getPlugged()) {
            z = true;
        }
        boolean playSound = z;
        if (shouldShowHybridWarning(currentSnapshot)) {
            this.mWarnings.showLowBatteryWarning(playSound);
            if (currentSnapshot.getTimeRemainingMillis() <= currentSnapshot.getSevereThresholdMillis() || currentSnapshot.getBatteryLevel() <= currentSnapshot.getSevereLevelThreshold()) {
                this.mSevereWarningShownThisChargeCycle = true;
                this.mLowWarningShownThisChargeCycle = true;
                if (DEBUG) {
                    Slog.d(TAG, "Severe warning marked as shown this cycle");
                    return;
                }
                return;
            }
            Slog.d(TAG, "Low warning marked as shown this cycle");
            this.mLowWarningShownThisChargeCycle = true;
        } else if (shouldDismissHybridWarning(currentSnapshot)) {
            if (DEBUG) {
                Slog.d(TAG, "Dismissing warning");
            }
            this.mWarnings.dismissLowBatteryWarning();
        } else {
            if (DEBUG) {
                Slog.d(TAG, "Updating warning");
            }
            this.mWarnings.updateLowBatteryWarning();
        }
    }

    @VisibleForTesting
    boolean shouldShowHybridWarning(BatteryStateSnapshot snapshot) {
        boolean canShow = false;
        if (snapshot.getPlugged() || snapshot.getBatteryStatus() == 1) {
            StringBuilder sb = new StringBuilder();
            sb.append("can't show warning due to - plugged: ");
            sb.append(snapshot.getPlugged());
            sb.append(" status unknown: ");
            sb.append(snapshot.getBatteryStatus() == 1);
            Slog.d(TAG, sb.toString());
            return false;
        }
        boolean canShowWarning = snapshot.isLowWarningEnabled() && !this.mLowWarningShownThisChargeCycle && !snapshot.isPowerSaver() && (snapshot.getTimeRemainingMillis() < snapshot.getLowThresholdMillis() || snapshot.getBatteryLevel() <= snapshot.getLowLevelThreshold());
        boolean canShowSevereWarning = !this.mSevereWarningShownThisChargeCycle && (snapshot.getTimeRemainingMillis() < snapshot.getSevereThresholdMillis() || snapshot.getBatteryLevel() <= snapshot.getSevereLevelThreshold());
        if (canShowWarning || canShowSevereWarning) {
            canShow = true;
        }
        if (DEBUG) {
            Slog.d(TAG, "Enhanced trigger is: " + canShow + "\nwith battery snapshot: mLowWarningShownThisChargeCycle: " + this.mLowWarningShownThisChargeCycle + " mSevereWarningShownThisChargeCycle: " + this.mSevereWarningShownThisChargeCycle + "\n" + snapshot.toString());
        }
        return canShow;
    }

    @VisibleForTesting
    boolean shouldDismissHybridWarning(BatteryStateSnapshot snapshot) {
        return snapshot.getPlugged() || snapshot.getTimeRemainingMillis() > snapshot.getLowThresholdMillis();
    }

    protected void maybeShowBatteryWarning(BatteryStateSnapshot currentSnapshot, BatteryStateSnapshot lastSnapshot) {
        boolean playSound = currentSnapshot.getBucket() != lastSnapshot.getBucket() || lastSnapshot.getPlugged();
        if (shouldShowLowBatteryWarning(currentSnapshot, lastSnapshot)) {
            this.mWarnings.showLowBatteryWarning(playSound);
        } else if (shouldDismissLowBatteryWarning(currentSnapshot, lastSnapshot)) {
            this.mWarnings.dismissLowBatteryWarning();
        } else {
            this.mWarnings.updateLowBatteryWarning();
        }
    }

    @VisibleForTesting
    boolean shouldShowLowBatteryWarning(BatteryStateSnapshot currentSnapshot, BatteryStateSnapshot lastSnapshot) {
        return (currentSnapshot.getPlugged() || currentSnapshot.isPowerSaver() || (currentSnapshot.getBucket() >= lastSnapshot.getBucket() && !lastSnapshot.getPlugged()) || currentSnapshot.getBucket() >= 0 || currentSnapshot.getBatteryStatus() == 1) ? false : true;
    }

    @VisibleForTesting
    boolean shouldDismissLowBatteryWarning(BatteryStateSnapshot currentSnapshot, BatteryStateSnapshot lastSnapshot) {
        return currentSnapshot.isPowerSaver() || currentSnapshot.getPlugged() || (currentSnapshot.getBucket() > lastSnapshot.getBucket() && currentSnapshot.getBucket() > 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initThermalEventListeners() {
        doSkinThermalEventListenerRegistration();
        doUsbThermalEventListenerRegistration();
    }

    @VisibleForTesting
    synchronized void doSkinThermalEventListenerRegistration() {
        boolean oldEnableSkinTemperatureWarning = this.mEnableSkinTemperatureWarning;
        boolean ret = false;
        boolean z = true;
        this.mEnableSkinTemperatureWarning = Settings.Global.getInt(this.mContext.getContentResolver(), "show_temperature_warning", this.mContext.getResources().getInteger(R.integer.config_showTemperatureWarning)) != 0;
        if (this.mEnableSkinTemperatureWarning != oldEnableSkinTemperatureWarning) {
            try {
                if (this.mSkinThermalEventListener == null) {
                    this.mSkinThermalEventListener = new SkinThermalEventListener();
                }
                if (this.mThermalService == null) {
                    this.mThermalService = IThermalService.Stub.asInterface(ServiceManager.getService("thermalservice"));
                }
                if (this.mEnableSkinTemperatureWarning) {
                    ret = this.mThermalService.registerThermalEventListenerWithType(this.mSkinThermalEventListener, 3);
                } else {
                    ret = this.mThermalService.unregisterThermalEventListener(this.mSkinThermalEventListener);
                }
            } catch (RemoteException e) {
                Slog.e(TAG, "Exception while (un)registering skin thermal event listener.", e);
            }
            if (!ret) {
                if (this.mEnableSkinTemperatureWarning) {
                    z = false;
                }
                this.mEnableSkinTemperatureWarning = z;
                Slog.e(TAG, "Failed to register or unregister skin thermal event listener.");
            }
        }
    }

    @VisibleForTesting
    synchronized void doUsbThermalEventListenerRegistration() {
        boolean oldEnableUsbTemperatureAlarm = this.mEnableUsbTemperatureAlarm;
        boolean ret = false;
        boolean z = true;
        this.mEnableUsbTemperatureAlarm = Settings.Global.getInt(this.mContext.getContentResolver(), "show_usb_temperature_alarm", this.mContext.getResources().getInteger(R.integer.config_showUsbPortAlarm)) != 0;
        if (this.mEnableUsbTemperatureAlarm != oldEnableUsbTemperatureAlarm) {
            try {
                if (this.mUsbThermalEventListener == null) {
                    this.mUsbThermalEventListener = new UsbThermalEventListener();
                }
                if (this.mThermalService == null) {
                    this.mThermalService = IThermalService.Stub.asInterface(ServiceManager.getService("thermalservice"));
                }
                if (this.mEnableUsbTemperatureAlarm) {
                    ret = this.mThermalService.registerThermalEventListenerWithType(this.mUsbThermalEventListener, 4);
                } else {
                    ret = this.mThermalService.unregisterThermalEventListener(this.mUsbThermalEventListener);
                }
            } catch (RemoteException e) {
                Slog.e(TAG, "Exception while (un)registering usb thermal event listener.", e);
            }
            if (!ret) {
                if (this.mEnableUsbTemperatureAlarm) {
                    z = false;
                }
                this.mEnableUsbTemperatureAlarm = z;
                Slog.e(TAG, "Failed to register or unregister usb thermal event listener.");
            }
        }
    }

    private void showWarnOnThermalShutdown() {
        int bootCount = -1;
        int lastReboot = this.mContext.getSharedPreferences(PREFS, 0).getInt(BOOT_COUNT_KEY, -1);
        try {
            bootCount = Settings.Global.getInt(this.mContext.getContentResolver(), BOOT_COUNT_KEY);
        } catch (Settings.SettingNotFoundException e) {
            Slog.e(TAG, "Failed to read system boot count from Settings.Global.BOOT_COUNT");
        }
        if (bootCount > lastReboot) {
            this.mContext.getSharedPreferences(PREFS, 0).edit().putInt(BOOT_COUNT_KEY, bootCount).apply();
            if (this.mPowerManager.getLastShutdownReason() == 4) {
                this.mWarnings.showThermalShutdownWarning();
            }
        }
    }

    @Override // com.android.systemui.SystemUI
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.print("mLowBatteryAlertCloseLevel=");
        pw.println(this.mLowBatteryAlertCloseLevel);
        pw.print("mLowBatteryReminderLevels=");
        pw.println(Arrays.toString(this.mLowBatteryReminderLevels));
        pw.print("mBatteryLevel=");
        pw.println(Integer.toString(this.mBatteryLevel));
        pw.print("mBatteryStatus=");
        pw.println(Integer.toString(this.mBatteryStatus));
        pw.print("mPlugType=");
        pw.println(Integer.toString(this.mPlugType));
        pw.print("mInvalidCharger=");
        pw.println(Integer.toString(this.mInvalidCharger));
        pw.print("mScreenOffTime=");
        pw.print(this.mScreenOffTime);
        if (this.mScreenOffTime >= 0) {
            pw.print(" (");
            pw.print(SystemClock.elapsedRealtime() - this.mScreenOffTime);
            pw.print(" ago)");
        }
        pw.println();
        pw.print("soundTimeout=");
        pw.println(Settings.Global.getInt(this.mContext.getContentResolver(), "low_battery_sound_timeout", 0));
        pw.print("bucket: ");
        pw.println(Integer.toString(findBatteryLevelBucket(this.mBatteryLevel)));
        pw.print("mEnableSkinTemperatureWarning=");
        pw.println(this.mEnableSkinTemperatureWarning);
        pw.print("mEnableUsbTemperatureAlarm=");
        pw.println(this.mEnableUsbTemperatureAlarm);
        this.mWarnings.dump(pw);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* loaded from: classes21.dex */
    public final class SkinThermalEventListener extends IThermalEventListener.Stub {
        SkinThermalEventListener() {
        }

        public void notifyThrottling(Temperature temp) {
            int status = temp.getStatus();
            if (status < 5) {
                PowerUI.this.mWarnings.dismissHighTemperatureWarning();
                return;
            }
            StatusBar statusBar = (StatusBar) PowerUI.this.getComponent(StatusBar.class);
            if (statusBar != null && !statusBar.isDeviceInVrMode()) {
                PowerUI.this.mWarnings.showHighTemperatureWarning();
                Slog.d(PowerUI.TAG, "SkinThermalEventListener: notifyThrottling was called , current skin status = " + status + ", temperature = " + temp.getValue());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* loaded from: classes21.dex */
    public final class UsbThermalEventListener extends IThermalEventListener.Stub {
        UsbThermalEventListener() {
        }

        public void notifyThrottling(Temperature temp) {
            int status = temp.getStatus();
            if (status >= 5) {
                PowerUI.this.mWarnings.showUsbHighTemperatureAlarm();
                Slog.d(PowerUI.TAG, "UsbThermalEventListener: notifyThrottling was called , current usb port status = " + status + ", temperature = " + temp.getValue());
            }
        }
    }
}
