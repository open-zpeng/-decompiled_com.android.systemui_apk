package com.android.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerSaveState;
import android.util.Log;
import androidx.annotation.Nullable;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.fuelgauge.BatterySaverUtils;
import com.android.settingslib.fuelgauge.Estimate;
import com.android.settingslib.utils.PowerUtil;
import com.android.systemui.DemoMode;
import com.android.systemui.Dependency;
import com.android.systemui.power.EnhancedEstimates;
import com.android.systemui.statusbar.policy.BatteryController;
import com.xiaopeng.speech.protocol.event.OOBEEvent;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class BatteryControllerImpl extends BroadcastReceiver implements BatteryController {
    public static final String ACTION_LEVEL_TEST = "com.android.systemui.BATTERY_LEVEL_TEST";
    private static final int UPDATE_GRANULARITY_MSEC = 60000;
    protected boolean mAodPowerSave;
    private final ArrayList<BatteryController.BatteryStateChangeCallback> mChangeCallbacks;
    protected boolean mCharged;
    protected boolean mCharging;
    private final Context mContext;
    private boolean mDemoMode;
    private Estimate mEstimate;
    private final EnhancedEstimates mEstimates;
    private final ArrayList<BatteryController.EstimateFetchCompletion> mFetchCallbacks;
    private boolean mFetchingEstimate;
    private final Handler mHandler;
    private boolean mHasReceivedBattery;
    protected int mLevel;
    protected boolean mPluggedIn;
    private final PowerManager mPowerManager;
    protected boolean mPowerSave;
    private boolean mTestmode;
    private static final String TAG = "BatteryController";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);

    @Inject
    public BatteryControllerImpl(Context context, EnhancedEstimates enhancedEstimates) {
        this(context, enhancedEstimates, (PowerManager) context.getSystemService(PowerManager.class));
    }

    @VisibleForTesting
    BatteryControllerImpl(Context context, EnhancedEstimates enhancedEstimates, PowerManager powerManager) {
        this.mChangeCallbacks = new ArrayList<>();
        this.mFetchCallbacks = new ArrayList<>();
        this.mTestmode = false;
        this.mHasReceivedBattery = false;
        this.mFetchingEstimate = false;
        this.mContext = context;
        this.mHandler = new Handler();
        this.mPowerManager = powerManager;
        this.mEstimates = enhancedEstimates;
        registerReceiver();
        updatePowerSave();
        updateEstimate();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.BATTERY_CHANGED");
        filter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
        filter.addAction("android.os.action.POWER_SAVE_MODE_CHANGING");
        filter.addAction(ACTION_LEVEL_TEST);
        this.mContext.registerReceiver(this, filter);
    }

    @Override // com.android.systemui.statusbar.policy.BatteryController, com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("BatteryController state:");
        pw.print("  mLevel=");
        pw.println(this.mLevel);
        pw.print("  mPluggedIn=");
        pw.println(this.mPluggedIn);
        pw.print("  mCharging=");
        pw.println(this.mCharging);
        pw.print("  mCharged=");
        pw.println(this.mCharged);
        pw.print("  mPowerSave=");
        pw.println(this.mPowerSave);
    }

    @Override // com.android.systemui.statusbar.policy.BatteryController
    public void setPowerSaveMode(boolean powerSave) {
        BatterySaverUtils.setPowerSaveMode(this.mContext, powerSave, true);
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void addCallback(BatteryController.BatteryStateChangeCallback cb) {
        synchronized (this.mChangeCallbacks) {
            this.mChangeCallbacks.add(cb);
        }
        if (this.mHasReceivedBattery) {
            cb.onBatteryLevelChanged(this.mLevel, this.mPluggedIn, this.mCharging);
            cb.onPowerSaveChanged(this.mPowerSave);
        }
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void removeCallback(BatteryController.BatteryStateChangeCallback cb) {
        synchronized (this.mChangeCallbacks) {
            this.mChangeCallbacks.remove(cb);
        }
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        boolean z = true;
        if (!action.equals("android.intent.action.BATTERY_CHANGED")) {
            if (action.equals("android.os.action.POWER_SAVE_MODE_CHANGED")) {
                updatePowerSave();
            } else if (action.equals("android.os.action.POWER_SAVE_MODE_CHANGING")) {
                setPowerSave(intent.getBooleanExtra("mode", false));
            } else if (action.equals(ACTION_LEVEL_TEST)) {
                this.mTestmode = true;
                this.mHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.policy.BatteryControllerImpl.1
                    int saveLevel;
                    boolean savePlugged;
                    int curLevel = 0;
                    int incr = 1;
                    Intent dummy = new Intent("android.intent.action.BATTERY_CHANGED");

                    {
                        this.saveLevel = BatteryControllerImpl.this.mLevel;
                        this.savePlugged = BatteryControllerImpl.this.mPluggedIn;
                    }

                    @Override // java.lang.Runnable
                    public void run() {
                        int i = this.curLevel;
                        if (i < 0) {
                            BatteryControllerImpl.this.mTestmode = false;
                            this.dummy.putExtra("level", this.saveLevel);
                            this.dummy.putExtra("plugged", this.savePlugged);
                            this.dummy.putExtra("testmode", false);
                        } else {
                            this.dummy.putExtra("level", i);
                            this.dummy.putExtra("plugged", this.incr > 0 ? 1 : 0);
                            this.dummy.putExtra("testmode", true);
                        }
                        context.sendBroadcast(this.dummy);
                        if (BatteryControllerImpl.this.mTestmode) {
                            int i2 = this.curLevel;
                            int i3 = this.incr;
                            this.curLevel = i2 + i3;
                            if (this.curLevel == 100) {
                                this.incr = i3 * (-1);
                            }
                            BatteryControllerImpl.this.mHandler.postDelayed(this, 200L);
                        }
                    }
                });
            }
        } else if (!this.mTestmode || intent.getBooleanExtra("testmode", false)) {
            this.mHasReceivedBattery = true;
            this.mLevel = (int) ((intent.getIntExtra("level", 0) * 100.0f) / intent.getIntExtra("scale", 100));
            this.mPluggedIn = intent.getIntExtra("plugged", 0) != 0;
            int status = intent.getIntExtra("status", 1);
            this.mCharged = status == 5;
            if (!this.mCharged && status != 2) {
                z = false;
            }
            this.mCharging = z;
            fireBatteryLevelChanged();
        }
    }

    @Override // com.android.systemui.statusbar.policy.BatteryController
    public boolean isPowerSave() {
        return this.mPowerSave;
    }

    @Override // com.android.systemui.statusbar.policy.BatteryController
    public boolean isAodPowerSave() {
        return this.mAodPowerSave;
    }

    @Override // com.android.systemui.statusbar.policy.BatteryController
    public void getEstimatedTimeRemainingString(BatteryController.EstimateFetchCompletion completion) {
        synchronized (this.mFetchCallbacks) {
            this.mFetchCallbacks.add(completion);
        }
        updateEstimateInBackground();
    }

    @Nullable
    private String generateTimeRemainingString() {
        synchronized (this.mFetchCallbacks) {
            if (this.mEstimate == null) {
                return null;
            }
            NumberFormat.getPercentInstance().format(this.mLevel / 100.0d);
            return PowerUtil.getBatteryRemainingShortStringFormatted(this.mContext, this.mEstimate.getEstimateMillis());
        }
    }

    private void updateEstimateInBackground() {
        if (this.mFetchingEstimate) {
            return;
        }
        this.mFetchingEstimate = true;
        ((Handler) Dependency.get(Dependency.BG_HANDLER)).post(new Runnable() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$BatteryControllerImpl$Q2m5_jQFbUIrN5-x5MkihyCoos8
            @Override // java.lang.Runnable
            public final void run() {
                BatteryControllerImpl.this.lambda$updateEstimateInBackground$0$BatteryControllerImpl();
            }
        });
    }

    public /* synthetic */ void lambda$updateEstimateInBackground$0$BatteryControllerImpl() {
        synchronized (this.mFetchCallbacks) {
            this.mEstimate = null;
            if (this.mEstimates.isHybridNotificationEnabled()) {
                updateEstimate();
            }
        }
        this.mFetchingEstimate = false;
        ((Handler) Dependency.get(Dependency.MAIN_HANDLER)).post(new Runnable() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$BatteryControllerImpl$xVvPxv9usTpbGvWx3jH4_VH1nvI
            @Override // java.lang.Runnable
            public final void run() {
                BatteryControllerImpl.this.notifyEstimateFetchCallbacks();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyEstimateFetchCallbacks() {
        synchronized (this.mFetchCallbacks) {
            String estimate = generateTimeRemainingString();
            Iterator<BatteryController.EstimateFetchCompletion> it = this.mFetchCallbacks.iterator();
            while (it.hasNext()) {
                BatteryController.EstimateFetchCompletion completion = it.next();
                completion.onBatteryRemainingEstimateRetrieved(estimate);
            }
            this.mFetchCallbacks.clear();
        }
    }

    private void updateEstimate() {
        this.mEstimate = Estimate.getCachedEstimateIfAvailable(this.mContext);
        if (this.mEstimate == null) {
            this.mEstimate = this.mEstimates.getEstimate();
            Estimate estimate = this.mEstimate;
            if (estimate != null) {
                Estimate.storeCachedEstimate(this.mContext, estimate);
            }
        }
    }

    private void updatePowerSave() {
        setPowerSave(this.mPowerManager.isPowerSaveMode());
    }

    private void setPowerSave(boolean powerSave) {
        if (powerSave == this.mPowerSave) {
            return;
        }
        this.mPowerSave = powerSave;
        PowerSaveState state = this.mPowerManager.getPowerSaveState(14);
        this.mAodPowerSave = state.batterySaverEnabled;
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("Power save is ");
            sb.append(this.mPowerSave ? "on" : "off");
            Log.d(TAG, sb.toString());
        }
        firePowerSaveChanged();
    }

    protected void fireBatteryLevelChanged() {
        synchronized (this.mChangeCallbacks) {
            int N = this.mChangeCallbacks.size();
            for (int i = 0; i < N; i++) {
                this.mChangeCallbacks.get(i).onBatteryLevelChanged(this.mLevel, this.mPluggedIn, this.mCharging);
            }
        }
    }

    private void firePowerSaveChanged() {
        synchronized (this.mChangeCallbacks) {
            int N = this.mChangeCallbacks.size();
            for (int i = 0; i < N; i++) {
                this.mChangeCallbacks.get(i).onPowerSaveChanged(this.mPowerSave);
            }
        }
    }

    @Override // com.android.systemui.DemoMode
    public void dispatchDemoCommand(String command, Bundle args) {
        if (!this.mDemoMode && command.equals("enter")) {
            this.mDemoMode = true;
            this.mContext.unregisterReceiver(this);
        } else if (this.mDemoMode && command.equals(DemoMode.COMMAND_EXIT)) {
            this.mDemoMode = false;
            registerReceiver();
            updatePowerSave();
        } else if (this.mDemoMode && command.equals(DemoMode.COMMAND_BATTERY)) {
            String level = args.getString("level");
            String plugged = args.getString("plugged");
            String powerSave = args.getString("powersave");
            if (level != null) {
                this.mLevel = Math.min(Math.max(Integer.parseInt(level), 0), 100);
            }
            if (plugged != null) {
                this.mPluggedIn = Boolean.parseBoolean(plugged);
            }
            if (powerSave != null) {
                this.mPowerSave = powerSave.equals(OOBEEvent.STRING_TRUE);
                firePowerSaveChanged();
            }
            fireBatteryLevelChanged();
        }
    }
}
