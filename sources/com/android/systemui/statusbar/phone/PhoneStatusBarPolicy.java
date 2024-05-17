package com.android.systemui.statusbar.phone;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.AlarmManager;
import android.app.SynchronousUserSwitchObserver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserManager;
import android.service.notification.ZenModeConfig;
import android.telecom.TelecomManager;
import android.text.format.DateFormat;
import android.util.Log;
import com.android.internal.telephony.IccCardConstants;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.UiOffloadThread;
import com.android.systemui.globalactions.GlobalActionsDialog;
import com.android.systemui.qs.tiles.DndTile;
import com.android.systemui.qs.tiles.RotationLockTile;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.phone.PhoneStatusBarPolicy;
import com.android.systemui.statusbar.policy.BluetoothController;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.HotspotController;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.statusbar.policy.LocationController;
import com.android.systemui.statusbar.policy.NextAlarmController;
import com.android.systemui.statusbar.policy.RotationLockController;
import com.android.systemui.statusbar.policy.SensorPrivacyController;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.xiaopeng.speech.speechwidget.ListWidget;
import java.util.Locale;
/* loaded from: classes21.dex */
public class PhoneStatusBarPolicy implements BluetoothController.Callback, CommandQueue.Callbacks, RotationLockController.RotationLockControllerCallback, DataSaverController.Listener, ZenModeController.Callback, DeviceProvisionedController.DeviceProvisionedListener, KeyguardMonitor.Callback, LocationController.LocationChangeCallback {
    public static final int LOCATION_STATUS_ICON_ID = 17303110;
    private final AlarmManager mAlarmManager;
    private final Context mContext;
    private boolean mCurrentUserSetup;
    private final StatusBarIconController mIconController;
    private AlarmManager.AlarmClockInfo mNextAlarm;
    private final String mSlotAlarmClock;
    private final String mSlotBluetooth;
    private final String mSlotCast;
    private final String mSlotDataSaver;
    private final String mSlotHeadset;
    private final String mSlotHotspot;
    private final String mSlotLocation;
    private final String mSlotManagedProfile;
    private final String mSlotRotate;
    private final String mSlotSensorsOff;
    private final String mSlotTty;
    private final String mSlotVolume;
    private final String mSlotZen;
    private final UserManager mUserManager;
    private boolean mVolumeVisible;
    private boolean mZenVisible;
    private static final String TAG = "PhoneStatusBarPolicy";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private final Handler mHandler = new Handler();
    private final UiOffloadThread mUiOffloadThread = (UiOffloadThread) Dependency.get(UiOffloadThread.class);
    IccCardConstants.State mSimState = IccCardConstants.State.READY;
    private boolean mManagedProfileIconVisible = false;
    private final SynchronousUserSwitchObserver mUserSwitchListener = new AnonymousClass1();
    private final HotspotController.Callback mHotspotCallback = new HotspotController.Callback() { // from class: com.android.systemui.statusbar.phone.PhoneStatusBarPolicy.2
        @Override // com.android.systemui.statusbar.policy.HotspotController.Callback
        public void onHotspotChanged(boolean enabled, int numDevices) {
            PhoneStatusBarPolicy.this.mIconController.setIconVisibility(PhoneStatusBarPolicy.this.mSlotHotspot, enabled);
        }
    };
    private final CastController.Callback mCastCallback = new CastController.Callback() { // from class: com.android.systemui.statusbar.phone.PhoneStatusBarPolicy.3
        @Override // com.android.systemui.statusbar.policy.CastController.Callback
        public void onCastDevicesChanged() {
            PhoneStatusBarPolicy.this.updateCast();
        }
    };
    private final NextAlarmController.NextAlarmChangeCallback mNextAlarmCallback = new NextAlarmController.NextAlarmChangeCallback() { // from class: com.android.systemui.statusbar.phone.PhoneStatusBarPolicy.4
        @Override // com.android.systemui.statusbar.policy.NextAlarmController.NextAlarmChangeCallback
        public void onNextAlarmChanged(AlarmManager.AlarmClockInfo nextAlarm) {
            PhoneStatusBarPolicy.this.mNextAlarm = nextAlarm;
            PhoneStatusBarPolicy.this.updateAlarm();
        }
    };
    private final SensorPrivacyController.OnSensorPrivacyChangedListener mSensorPrivacyListener = new AnonymousClass5();
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() { // from class: com.android.systemui.statusbar.phone.PhoneStatusBarPolicy.6
        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            char c;
            String action = intent.getAction();
            switch (action.hashCode()) {
                case -1676458352:
                    if (action.equals("android.intent.action.HEADSET_PLUG")) {
                        c = 7;
                        break;
                    }
                    c = 65535;
                    break;
                case -1238404651:
                    if (action.equals("android.intent.action.MANAGED_PROFILE_UNAVAILABLE")) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                case -864107122:
                    if (action.equals("android.intent.action.MANAGED_PROFILE_AVAILABLE")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case -229777127:
                    if (action.equals("android.intent.action.SIM_STATE_CHANGED")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 100931828:
                    if (action.equals("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 1051344550:
                    if (action.equals("android.telecom.action.CURRENT_TTY_MODE_CHANGED")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case 1051477093:
                    if (action.equals("android.intent.action.MANAGED_PROFILE_REMOVED")) {
                        c = 6;
                        break;
                    }
                    c = 65535;
                    break;
                case 2070024785:
                    if (action.equals("android.media.RINGER_MODE_CHANGED")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                case 1:
                    PhoneStatusBarPolicy.this.updateVolumeZen();
                    return;
                case 2:
                    if (!intent.getBooleanExtra("rebroadcastOnUnlock", false)) {
                        PhoneStatusBarPolicy.this.updateSimState(intent);
                        return;
                    }
                    return;
                case 3:
                    PhoneStatusBarPolicy.this.updateTTY(intent.getIntExtra("android.telecom.intent.extra.CURRENT_TTY_MODE", 0));
                    return;
                case 4:
                case 5:
                case 6:
                    PhoneStatusBarPolicy.this.updateManagedProfile();
                    return;
                case 7:
                    PhoneStatusBarPolicy.this.updateHeadsetPlug(intent);
                    return;
                default:
                    return;
            }
        }
    };
    private Runnable mRemoveCastIconRunnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.PhoneStatusBarPolicy.7
        @Override // java.lang.Runnable
        public void run() {
            if (PhoneStatusBarPolicy.DEBUG) {
                Log.v(PhoneStatusBarPolicy.TAG, "updateCast: hiding icon NOW");
            }
            PhoneStatusBarPolicy.this.mIconController.setIconVisibility(PhoneStatusBarPolicy.this.mSlotCast, false);
        }
    };
    private final CastController mCast = (CastController) Dependency.get(CastController.class);
    private final HotspotController mHotspot = (HotspotController) Dependency.get(HotspotController.class);
    private BluetoothController mBluetooth = (BluetoothController) Dependency.get(BluetoothController.class);
    private final NextAlarmController mNextAlarmController = (NextAlarmController) Dependency.get(NextAlarmController.class);
    private final UserInfoController mUserInfoController = (UserInfoController) Dependency.get(UserInfoController.class);
    private final RotationLockController mRotationLockController = (RotationLockController) Dependency.get(RotationLockController.class);
    private final DataSaverController mDataSaver = (DataSaverController) Dependency.get(DataSaverController.class);
    private final ZenModeController mZenController = (ZenModeController) Dependency.get(ZenModeController.class);
    private final DeviceProvisionedController mProvisionedController = (DeviceProvisionedController) Dependency.get(DeviceProvisionedController.class);
    private final KeyguardMonitor mKeyguardMonitor = (KeyguardMonitor) Dependency.get(KeyguardMonitor.class);
    private final LocationController mLocationController = (LocationController) Dependency.get(LocationController.class);
    private final SensorPrivacyController mSensorPrivacyController = (SensorPrivacyController) Dependency.get(SensorPrivacyController.class);

    public PhoneStatusBarPolicy(Context context, StatusBarIconController iconController) {
        this.mContext = context;
        this.mIconController = iconController;
        this.mAlarmManager = (AlarmManager) context.getSystemService("alarm");
        this.mUserManager = (UserManager) this.mContext.getSystemService("user");
        this.mSlotCast = context.getString(17041083);
        this.mSlotHotspot = context.getString(17041090);
        this.mSlotBluetooth = context.getString(17041081);
        this.mSlotTty = context.getString(17041106);
        this.mSlotZen = context.getString(17041110);
        this.mSlotVolume = context.getString(17041107);
        this.mSlotAlarmClock = context.getString(17041079);
        this.mSlotManagedProfile = context.getString(17041093);
        this.mSlotRotate = context.getString(17041100);
        this.mSlotHeadset = context.getString(17041089);
        this.mSlotDataSaver = context.getString(17041087);
        this.mSlotLocation = context.getString(17041092);
        this.mSlotSensorsOff = context.getString(17041102);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.RINGER_MODE_CHANGED");
        filter.addAction("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION");
        filter.addAction("android.intent.action.HEADSET_PLUG");
        filter.addAction("android.intent.action.SIM_STATE_CHANGED");
        filter.addAction("android.telecom.action.CURRENT_TTY_MODE_CHANGED");
        filter.addAction("android.intent.action.MANAGED_PROFILE_AVAILABLE");
        filter.addAction("android.intent.action.MANAGED_PROFILE_UNAVAILABLE");
        filter.addAction("android.intent.action.MANAGED_PROFILE_REMOVED");
        this.mContext.registerReceiver(this.mIntentReceiver, filter, null, this.mHandler);
        try {
            ActivityManager.getService().registerUserSwitchObserver(this.mUserSwitchListener, TAG);
        } catch (RemoteException e) {
        }
        updateTTY();
        updateBluetooth();
        this.mIconController.setIcon(this.mSlotAlarmClock, R.drawable.stat_sys_alarm, null);
        this.mIconController.setIconVisibility(this.mSlotAlarmClock, false);
        this.mIconController.setIcon(this.mSlotZen, R.drawable.stat_sys_dnd, null);
        this.mIconController.setIconVisibility(this.mSlotZen, false);
        this.mIconController.setIcon(this.mSlotVolume, R.drawable.stat_sys_ringer_vibrate, null);
        this.mIconController.setIconVisibility(this.mSlotVolume, false);
        updateVolumeZen();
        this.mIconController.setIcon(this.mSlotCast, R.drawable.stat_sys_cast, null);
        this.mIconController.setIconVisibility(this.mSlotCast, false);
        this.mIconController.setIcon(this.mSlotHotspot, R.drawable.stat_sys_hotspot, this.mContext.getString(R.string.accessibility_status_bar_hotspot));
        this.mIconController.setIconVisibility(this.mSlotHotspot, this.mHotspot.isHotspotEnabled());
        this.mIconController.setIcon(this.mSlotManagedProfile, R.drawable.stat_sys_managed_profile_status, this.mContext.getString(R.string.accessibility_managed_profile));
        this.mIconController.setIconVisibility(this.mSlotManagedProfile, this.mManagedProfileIconVisible);
        this.mIconController.setIcon(this.mSlotDataSaver, R.drawable.stat_sys_data_saver, context.getString(R.string.accessibility_data_saver_on));
        this.mIconController.setIconVisibility(this.mSlotDataSaver, false);
        this.mIconController.setIcon(this.mSlotLocation, LOCATION_STATUS_ICON_ID, this.mContext.getString(R.string.accessibility_location_active));
        this.mIconController.setIconVisibility(this.mSlotLocation, false);
        this.mIconController.setIcon(this.mSlotSensorsOff, R.drawable.stat_sys_sensors_off, this.mContext.getString(R.string.accessibility_sensors_off_active));
        this.mIconController.setIconVisibility(this.mSlotSensorsOff, this.mSensorPrivacyController.isSensorPrivacyEnabled());
        this.mRotationLockController.addCallback(this);
        this.mBluetooth.addCallback(this);
        this.mProvisionedController.addCallback(this);
        this.mZenController.addCallback(this);
        this.mCast.addCallback(this.mCastCallback);
        this.mHotspot.addCallback(this.mHotspotCallback);
        this.mNextAlarmController.addCallback(this.mNextAlarmCallback);
        this.mDataSaver.addCallback(this);
        this.mKeyguardMonitor.addCallback(this);
        this.mSensorPrivacyController.addCallback(this.mSensorPrivacyListener);
        this.mLocationController.addCallback(this);
        ((CommandQueue) SysUiServiceProvider.getComponent(this.mContext, CommandQueue.class)).addCallback((CommandQueue.Callbacks) this);
    }

    @Override // com.android.systemui.statusbar.policy.ZenModeController.Callback
    public void onZenChanged(int zen) {
        updateVolumeZen();
    }

    @Override // com.android.systemui.statusbar.policy.ZenModeController.Callback
    public void onConfigChanged(ZenModeConfig config) {
        updateVolumeZen();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAlarm() {
        AlarmManager.AlarmClockInfo alarm = this.mAlarmManager.getNextAlarmClock(-2);
        boolean z = true;
        boolean hasAlarm = alarm != null && alarm.getTriggerTime() > 0;
        int zen = this.mZenController.getZen();
        boolean zenNone = zen == 2;
        this.mIconController.setIcon(this.mSlotAlarmClock, zenNone ? R.drawable.stat_sys_alarm_dim : R.drawable.stat_sys_alarm, buildAlarmContentDescription());
        StatusBarIconController statusBarIconController = this.mIconController;
        String str = this.mSlotAlarmClock;
        if (!this.mCurrentUserSetup || !hasAlarm) {
            z = false;
        }
        statusBarIconController.setIconVisibility(str, z);
    }

    private String buildAlarmContentDescription() {
        AlarmManager.AlarmClockInfo alarmClockInfo = this.mNextAlarm;
        if (alarmClockInfo == null) {
            return this.mContext.getString(R.string.status_bar_alarm);
        }
        return formatNextAlarm(alarmClockInfo, this.mContext);
    }

    private static String formatNextAlarm(AlarmManager.AlarmClockInfo info, Context context) {
        if (info == null) {
            return "";
        }
        String skeleton = DateFormat.is24HourFormat(context, ActivityManager.getCurrentUser()) ? "EHm" : "Ehma";
        String pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), skeleton);
        String dateString = DateFormat.format(pattern, info.getTriggerTime()).toString();
        return context.getString(R.string.accessibility_quick_settings_alarm, dateString);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void updateSimState(Intent intent) {
        String stateExtra = intent.getStringExtra("ss");
        if ("ABSENT".equals(stateExtra)) {
            this.mSimState = IccCardConstants.State.ABSENT;
        } else if ("CARD_IO_ERROR".equals(stateExtra)) {
            this.mSimState = IccCardConstants.State.CARD_IO_ERROR;
        } else if ("CARD_RESTRICTED".equals(stateExtra)) {
            this.mSimState = IccCardConstants.State.CARD_RESTRICTED;
        } else if ("READY".equals(stateExtra)) {
            this.mSimState = IccCardConstants.State.READY;
        } else if ("LOCKED".equals(stateExtra)) {
            String lockedReason = intent.getStringExtra(GlobalActionsDialog.SYSTEM_DIALOG_REASON_KEY);
            if ("PIN".equals(lockedReason)) {
                this.mSimState = IccCardConstants.State.PIN_REQUIRED;
            } else if ("PUK".equals(lockedReason)) {
                this.mSimState = IccCardConstants.State.PUK_REQUIRED;
            } else {
                this.mSimState = IccCardConstants.State.NETWORK_LOCKED;
            }
        } else {
            this.mSimState = IccCardConstants.State.UNKNOWN;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void updateVolumeZen() {
        AudioManager audioManager = (AudioManager) this.mContext.getSystemService(ListWidget.EXTRA_TYPE_AUDIO);
        boolean zenVisible = false;
        int zenIconId = 0;
        String zenDescription = null;
        boolean volumeVisible = false;
        int volumeIconId = 0;
        String volumeDescription = null;
        int zen = this.mZenController.getZen();
        if (DndTile.isVisible(this.mContext) || DndTile.isCombinedIcon(this.mContext)) {
            zenVisible = zen != 0;
            zenIconId = R.drawable.stat_sys_dnd;
            zenDescription = this.mContext.getString(R.string.quick_settings_dnd_label);
        } else if (zen == 2) {
            zenVisible = true;
            zenIconId = R.drawable.stat_sys_dnd;
            zenDescription = this.mContext.getString(R.string.interruption_level_none);
        } else if (zen == 1) {
            zenVisible = true;
            zenIconId = R.drawable.stat_sys_dnd;
            zenDescription = this.mContext.getString(R.string.interruption_level_priority);
        }
        if (!ZenModeConfig.isZenOverridingRinger(zen, this.mZenController.getConsolidatedPolicy())) {
            if (audioManager.getRingerModeInternal() == 1) {
                volumeVisible = true;
                volumeIconId = R.drawable.stat_sys_ringer_vibrate;
                volumeDescription = this.mContext.getString(R.string.accessibility_ringer_vibrate);
            } else if (audioManager.getRingerModeInternal() == 0) {
                volumeVisible = true;
                volumeIconId = R.drawable.stat_sys_ringer_silent;
                volumeDescription = this.mContext.getString(R.string.accessibility_ringer_silent);
            }
        }
        if (zenVisible) {
            this.mIconController.setIcon(this.mSlotZen, zenIconId, zenDescription);
        }
        if (zenVisible != this.mZenVisible) {
            this.mIconController.setIconVisibility(this.mSlotZen, zenVisible);
            this.mZenVisible = zenVisible;
        }
        if (volumeVisible) {
            this.mIconController.setIcon(this.mSlotVolume, volumeIconId, volumeDescription);
        }
        if (volumeVisible != this.mVolumeVisible) {
            this.mIconController.setIconVisibility(this.mSlotVolume, volumeVisible);
            this.mVolumeVisible = volumeVisible;
        }
        updateAlarm();
    }

    @Override // com.android.systemui.statusbar.policy.BluetoothController.Callback
    public void onBluetoothDevicesChanged() {
        updateBluetooth();
    }

    @Override // com.android.systemui.statusbar.policy.BluetoothController.Callback
    public void onBluetoothStateChange(boolean enabled) {
        updateBluetooth();
    }

    private final void updateBluetooth() {
        int iconId = R.drawable.stat_sys_data_bluetooth_connected;
        String contentDescription = this.mContext.getString(R.string.accessibility_quick_settings_bluetooth_on);
        boolean bluetoothVisible = false;
        BluetoothController bluetoothController = this.mBluetooth;
        if (bluetoothController != null && bluetoothController.isBluetoothConnected() && (this.mBluetooth.isBluetoothAudioActive() || !this.mBluetooth.isBluetoothAudioProfileOnly())) {
            contentDescription = this.mContext.getString(R.string.accessibility_bluetooth_connected);
            bluetoothVisible = this.mBluetooth.isBluetoothEnabled();
        }
        this.mIconController.setIcon(this.mSlotBluetooth, iconId, contentDescription);
        this.mIconController.setIconVisibility(this.mSlotBluetooth, bluetoothVisible);
    }

    private final void updateTTY() {
        TelecomManager telecomManager = (TelecomManager) this.mContext.getSystemService("telecom");
        if (telecomManager == null) {
            updateTTY(0);
        } else {
            updateTTY(telecomManager.getCurrentTtyMode());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void updateTTY(int currentTtyMode) {
        boolean enabled = currentTtyMode != 0;
        if (DEBUG) {
            Log.v(TAG, "updateTTY: enabled: " + enabled);
        }
        if (enabled) {
            if (DEBUG) {
                Log.v(TAG, "updateTTY: set TTY on");
            }
            this.mIconController.setIcon(this.mSlotTty, R.drawable.stat_sys_tty_mode, this.mContext.getString(R.string.accessibility_tty_enabled));
            this.mIconController.setIconVisibility(this.mSlotTty, true);
            return;
        }
        if (DEBUG) {
            Log.v(TAG, "updateTTY: set TTY off");
        }
        this.mIconController.setIconVisibility(this.mSlotTty, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:5:0x0012  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateCast() {
        /*
            r7 = this;
            r0 = 0
            com.android.systemui.statusbar.policy.CastController r1 = r7.mCast
            java.util.List r1 = r1.getCastDevices()
            java.util.Iterator r1 = r1.iterator()
        Lb:
            boolean r2 = r1.hasNext()
            r3 = 1
            if (r2 == 0) goto L25
            java.lang.Object r2 = r1.next()
            com.android.systemui.statusbar.policy.CastController$CastDevice r2 = (com.android.systemui.statusbar.policy.CastController.CastDevice) r2
            int r4 = r2.state
            if (r4 == r3) goto L23
            int r4 = r2.state
            r5 = 2
            if (r4 != r5) goto L22
            goto L23
        L22:
            goto Lb
        L23:
            r0 = 1
        L25:
            boolean r1 = com.android.systemui.statusbar.phone.PhoneStatusBarPolicy.DEBUG
            java.lang.String r2 = "PhoneStatusBarPolicy"
            if (r1 == 0) goto L3f
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r4 = "updateCast: isCasting: "
            r1.append(r4)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            android.util.Log.v(r2, r1)
        L3f:
            android.os.Handler r1 = r7.mHandler
            java.lang.Runnable r4 = r7.mRemoveCastIconRunnable
            r1.removeCallbacks(r4)
            if (r0 == 0) goto L61
            com.android.systemui.statusbar.phone.StatusBarIconController r1 = r7.mIconController
            java.lang.String r2 = r7.mSlotCast
            int r4 = com.android.systemui.R.drawable.stat_sys_cast
            android.content.Context r5 = r7.mContext
            int r6 = com.android.systemui.R.string.accessibility_casting
            java.lang.String r5 = r5.getString(r6)
            r1.setIcon(r2, r4, r5)
            com.android.systemui.statusbar.phone.StatusBarIconController r1 = r7.mIconController
            java.lang.String r2 = r7.mSlotCast
            r1.setIconVisibility(r2, r3)
            goto L73
        L61:
            boolean r1 = com.android.systemui.statusbar.phone.PhoneStatusBarPolicy.DEBUG
            if (r1 == 0) goto L6a
            java.lang.String r1 = "updateCast: hiding icon in 3 sec..."
            android.util.Log.v(r2, r1)
        L6a:
            android.os.Handler r1 = r7.mHandler
            java.lang.Runnable r2 = r7.mRemoveCastIconRunnable
            r3 = 3000(0xbb8, double:1.482E-320)
            r1.postDelayed(r2, r3)
        L73:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.PhoneStatusBarPolicy.updateCast():void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateManagedProfile() {
        this.mUiOffloadThread.submit(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$PhoneStatusBarPolicy$0YjhmxnSstzZ2dpboZJyd_6m3ZY
            @Override // java.lang.Runnable
            public final void run() {
                PhoneStatusBarPolicy.this.lambda$updateManagedProfile$1$PhoneStatusBarPolicy();
            }
        });
    }

    public /* synthetic */ void lambda$updateManagedProfile$1$PhoneStatusBarPolicy() {
        try {
            int userId = ActivityTaskManager.getService().getLastResumedActivityUserId();
            final boolean isManagedProfile = this.mUserManager.isManagedProfile(userId);
            this.mHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$PhoneStatusBarPolicy$XFUG-8Il5dqSkLb-5SFSNbiYcXg
                @Override // java.lang.Runnable
                public final void run() {
                    PhoneStatusBarPolicy.this.lambda$updateManagedProfile$0$PhoneStatusBarPolicy(isManagedProfile);
                }
            });
        } catch (RemoteException e) {
            Log.w(TAG, "updateManagedProfile: ", e);
        }
    }

    public /* synthetic */ void lambda$updateManagedProfile$0$PhoneStatusBarPolicy(boolean isManagedProfile) {
        boolean showIcon;
        if (isManagedProfile && (!this.mKeyguardMonitor.isShowing() || this.mKeyguardMonitor.isOccluded())) {
            showIcon = true;
            this.mIconController.setIcon(this.mSlotManagedProfile, R.drawable.stat_sys_managed_profile_status, this.mContext.getString(R.string.accessibility_managed_profile));
        } else {
            showIcon = false;
        }
        if (this.mManagedProfileIconVisible != showIcon) {
            this.mIconController.setIconVisibility(this.mSlotManagedProfile, showIcon);
            this.mManagedProfileIconVisible = showIcon;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.statusbar.phone.PhoneStatusBarPolicy$1  reason: invalid class name */
    /* loaded from: classes21.dex */
    public class AnonymousClass1 extends SynchronousUserSwitchObserver {
        AnonymousClass1() {
        }

        public /* synthetic */ void lambda$onUserSwitching$0$PhoneStatusBarPolicy$1() {
            PhoneStatusBarPolicy.this.mUserInfoController.reloadUserInfo();
        }

        public void onUserSwitching(int newUserId) throws RemoteException {
            PhoneStatusBarPolicy.this.mHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$PhoneStatusBarPolicy$1$4_BI5ieR2ylfAj9z5SwNfbqaqk4
                @Override // java.lang.Runnable
                public final void run() {
                    PhoneStatusBarPolicy.AnonymousClass1.this.lambda$onUserSwitching$0$PhoneStatusBarPolicy$1();
                }
            });
        }

        public void onUserSwitchComplete(int newUserId) throws RemoteException {
            PhoneStatusBarPolicy.this.mHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$PhoneStatusBarPolicy$1$lONTSmykfPe64DIHRuLayVCRwlI
                @Override // java.lang.Runnable
                public final void run() {
                    PhoneStatusBarPolicy.AnonymousClass1.this.lambda$onUserSwitchComplete$1$PhoneStatusBarPolicy$1();
                }
            });
        }

        public /* synthetic */ void lambda$onUserSwitchComplete$1$PhoneStatusBarPolicy$1() {
            PhoneStatusBarPolicy.this.updateAlarm();
            PhoneStatusBarPolicy.this.updateManagedProfile();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.statusbar.phone.PhoneStatusBarPolicy$5  reason: invalid class name */
    /* loaded from: classes21.dex */
    public class AnonymousClass5 implements SensorPrivacyController.OnSensorPrivacyChangedListener {
        AnonymousClass5() {
        }

        @Override // com.android.systemui.statusbar.policy.SensorPrivacyController.OnSensorPrivacyChangedListener
        public void onSensorPrivacyChanged(final boolean enabled) {
            PhoneStatusBarPolicy.this.mHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$PhoneStatusBarPolicy$5$UApHxsPG0BIvDnX5FCFYX6op1Fs
                @Override // java.lang.Runnable
                public final void run() {
                    PhoneStatusBarPolicy.AnonymousClass5.this.lambda$onSensorPrivacyChanged$0$PhoneStatusBarPolicy$5(enabled);
                }
            });
        }

        public /* synthetic */ void lambda$onSensorPrivacyChanged$0$PhoneStatusBarPolicy$5(boolean enabled) {
            PhoneStatusBarPolicy.this.mIconController.setIconVisibility(PhoneStatusBarPolicy.this.mSlotSensorsOff, enabled);
        }
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void appTransitionStarting(int displayId, long startTime, long duration, boolean forced) {
        if (this.mContext.getDisplayId() == displayId) {
            updateManagedProfile();
        }
    }

    @Override // com.android.systemui.statusbar.policy.KeyguardMonitor.Callback
    public void onKeyguardShowingChanged() {
        updateManagedProfile();
    }

    @Override // com.android.systemui.statusbar.policy.DeviceProvisionedController.DeviceProvisionedListener
    public void onUserSetupChanged() {
        DeviceProvisionedController deviceProvisionedController = this.mProvisionedController;
        boolean userSetup = deviceProvisionedController.isUserSetup(deviceProvisionedController.getCurrentUser());
        if (this.mCurrentUserSetup == userSetup) {
            return;
        }
        this.mCurrentUserSetup = userSetup;
        updateAlarm();
    }

    @Override // com.android.systemui.statusbar.policy.RotationLockController.RotationLockControllerCallback
    public void onRotationLockStateChanged(boolean rotationLocked, boolean affordanceVisible) {
        boolean portrait = RotationLockTile.isCurrentOrientationLockPortrait(this.mRotationLockController, this.mContext);
        if (rotationLocked) {
            if (portrait) {
                this.mIconController.setIcon(this.mSlotRotate, R.drawable.stat_sys_rotate_portrait, this.mContext.getString(R.string.accessibility_rotation_lock_on_portrait));
            } else {
                this.mIconController.setIcon(this.mSlotRotate, R.drawable.stat_sys_rotate_landscape, this.mContext.getString(R.string.accessibility_rotation_lock_on_landscape));
            }
            this.mIconController.setIconVisibility(this.mSlotRotate, true);
            return;
        }
        this.mIconController.setIconVisibility(this.mSlotRotate, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateHeadsetPlug(Intent intent) {
        int i;
        boolean connected = intent.getIntExtra("state", 0) != 0;
        boolean hasMic = intent.getIntExtra("microphone", 0) != 0;
        if (!connected) {
            this.mIconController.setIconVisibility(this.mSlotHeadset, false);
            return;
        }
        Context context = this.mContext;
        if (hasMic) {
            i = R.string.accessibility_status_bar_headset;
        } else {
            i = R.string.accessibility_status_bar_headphones;
        }
        String contentDescription = context.getString(i);
        this.mIconController.setIcon(this.mSlotHeadset, hasMic ? R.drawable.stat_sys_headset_mic : R.drawable.stat_sys_headset, contentDescription);
        this.mIconController.setIconVisibility(this.mSlotHeadset, true);
    }

    @Override // com.android.systemui.statusbar.policy.DataSaverController.Listener
    public void onDataSaverChanged(boolean isDataSaving) {
        this.mIconController.setIconVisibility(this.mSlotDataSaver, isDataSaving);
    }

    @Override // com.android.systemui.statusbar.policy.LocationController.LocationChangeCallback
    public void onLocationActiveChanged(boolean active) {
        updateLocation();
    }

    private void updateLocation() {
        if (this.mLocationController.isLocationActive()) {
            this.mIconController.setIconVisibility(this.mSlotLocation, true);
        } else {
            this.mIconController.setIconVisibility(this.mSlotLocation, false);
        }
    }
}
