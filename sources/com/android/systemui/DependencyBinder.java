package com.android.systemui;

import com.android.systemui.appops.AppOpsController;
import com.android.systemui.appops.AppOpsControllerImpl;
import com.android.systemui.classifier.FalsingManagerProxy;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.VolumeDialogController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.power.PowerNotificationWarnings;
import com.android.systemui.power.PowerUI;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.QSTileHost;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.StatusBarStateControllerImpl;
import com.android.systemui.statusbar.phone.DarkIconDispatcherImpl;
import com.android.systemui.statusbar.phone.ManagedProfileController;
import com.android.systemui.statusbar.phone.ManagedProfileControllerImpl;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.phone.StatusBarIconControllerImpl;
import com.android.systemui.statusbar.phone.StatusBarRemoteInputCallback;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.BatteryControllerImpl;
import com.android.systemui.statusbar.policy.BluetoothController;
import com.android.systemui.statusbar.policy.BluetoothControllerImpl;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.statusbar.policy.CastControllerImpl;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.DeviceProvisionedControllerImpl;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.statusbar.policy.ExtensionControllerImpl;
import com.android.systemui.statusbar.policy.FlashlightController;
import com.android.systemui.statusbar.policy.FlashlightControllerImpl;
import com.android.systemui.statusbar.policy.HotspotController;
import com.android.systemui.statusbar.policy.HotspotControllerImpl;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.statusbar.policy.KeyguardMonitorImpl;
import com.android.systemui.statusbar.policy.LocationController;
import com.android.systemui.statusbar.policy.LocationControllerImpl;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.NetworkControllerImpl;
import com.android.systemui.statusbar.policy.NextAlarmController;
import com.android.systemui.statusbar.policy.NextAlarmControllerImpl;
import com.android.systemui.statusbar.policy.RotationLockController;
import com.android.systemui.statusbar.policy.RotationLockControllerImpl;
import com.android.systemui.statusbar.policy.SecurityController;
import com.android.systemui.statusbar.policy.SecurityControllerImpl;
import com.android.systemui.statusbar.policy.SensorPrivacyController;
import com.android.systemui.statusbar.policy.SensorPrivacyControllerImpl;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.statusbar.policy.UserInfoControllerImpl;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.statusbar.policy.ZenModeControllerImpl;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerServiceImpl;
import com.android.systemui.volume.VolumeDialogControllerImpl;
import dagger.Binds;
import dagger.Module;
@Module
/* loaded from: classes21.dex */
public abstract class DependencyBinder {
    @Binds
    public abstract ActivityStarter provideActivityStarter(ActivityStarterDelegate activityStarterDelegate);

    @Binds
    public abstract AppOpsController provideAppOpsController(AppOpsControllerImpl appOpsControllerImpl);

    @Binds
    public abstract BatteryController provideBatteryController(BatteryControllerImpl batteryControllerImpl);

    @Binds
    public abstract BluetoothController provideBluetoothController(BluetoothControllerImpl bluetoothControllerImpl);

    @Binds
    public abstract CastController provideCastController(CastControllerImpl castControllerImpl);

    @Binds
    public abstract DarkIconDispatcher provideDarkIconDispatcher(DarkIconDispatcherImpl darkIconDispatcherImpl);

    @Binds
    public abstract DeviceProvisionedController provideDeviceProvisionedController(DeviceProvisionedControllerImpl deviceProvisionedControllerImpl);

    @Binds
    public abstract ExtensionController provideExtensionController(ExtensionControllerImpl extensionControllerImpl);

    @Binds
    public abstract FalsingManager provideFalsingmanager(FalsingManagerProxy falsingManagerProxy);

    @Binds
    public abstract FlashlightController provideFlashlightController(FlashlightControllerImpl flashlightControllerImpl);

    @Binds
    public abstract HotspotController provideHotspotController(HotspotControllerImpl hotspotControllerImpl);

    @Binds
    public abstract KeyguardMonitor provideKeyguardMonitor(KeyguardMonitorImpl keyguardMonitorImpl);

    @Binds
    public abstract LocationController provideLocationController(LocationControllerImpl locationControllerImpl);

    @Binds
    public abstract ManagedProfileController provideManagedProfileController(ManagedProfileControllerImpl managedProfileControllerImpl);

    @Binds
    public abstract NetworkController provideNetworkController(NetworkControllerImpl networkControllerImpl);

    @Binds
    public abstract NextAlarmController provideNextAlarmController(NextAlarmControllerImpl nextAlarmControllerImpl);

    @Binds
    public abstract NotificationRemoteInputManager.Callback provideNotificationRemoteInputManager(StatusBarRemoteInputCallback statusBarRemoteInputCallback);

    @Binds
    public abstract QSHost provideQsHost(QSTileHost qSTileHost);

    @Binds
    public abstract RotationLockController provideRotationLockController(RotationLockControllerImpl rotationLockControllerImpl);

    @Binds
    public abstract SecurityController provideSecurityController(SecurityControllerImpl securityControllerImpl);

    @Binds
    public abstract SensorPrivacyController provideSensorPrivacyControllerImpl(SensorPrivacyControllerImpl sensorPrivacyControllerImpl);

    @Binds
    public abstract StatusBarIconController provideStatusBarIconController(StatusBarIconControllerImpl statusBarIconControllerImpl);

    @Binds
    public abstract StatusBarStateController provideStatusBarStateController(StatusBarStateControllerImpl statusBarStateControllerImpl);

    @Binds
    public abstract TunerService provideTunerService(TunerServiceImpl tunerServiceImpl);

    @Binds
    public abstract UserInfoController provideUserInfoContrller(UserInfoControllerImpl userInfoControllerImpl);

    @Binds
    public abstract VolumeDialogController provideVolumeDialogController(VolumeDialogControllerImpl volumeDialogControllerImpl);

    @Binds
    public abstract PowerUI.WarningsUI provideWarningsUi(PowerNotificationWarnings powerNotificationWarnings);

    @Binds
    public abstract ZenModeController provideZenModeController(ZenModeControllerImpl zenModeControllerImpl);
}
