package com.android.systemui;

import android.app.INotificationManager;
import android.hardware.SensorPrivacyManager;
import android.hardware.display.NightDisplayListener;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.IWindowManager;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.statusbar.IStatusBarService;
import com.android.keyguard.clock.ClockManager;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.systemui.appops.AppOpsController;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.dock.DockManager;
import com.android.systemui.fragments.FragmentService;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.PluginDependencyProvider;
import com.android.systemui.plugins.VolumeDialogController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.power.EnhancedEstimates;
import com.android.systemui.power.PowerUI;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.DevicePolicyManagerWrapper;
import com.android.systemui.shared.system.PackageManagerWrapper;
import com.android.systemui.statusbar.NavigationBarController;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationViewHierarchyManager;
import com.android.systemui.statusbar.SmartReplyController;
import com.android.systemui.statusbar.VibratorHelper;
import com.android.systemui.statusbar.notification.NotificationAlertingManager;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.NotificationFilter;
import com.android.systemui.statusbar.notification.NotificationInterruptionStateProvider;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.notification.collection.NotificationData;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.row.ChannelEditorDialogController;
import com.android.systemui.statusbar.notification.row.NotificationBlockingHelperManager;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager;
import com.android.systemui.statusbar.phone.AutoHideController;
import com.android.systemui.statusbar.phone.KeyguardDismissUtil;
import com.android.systemui.statusbar.phone.LightBarController;
import com.android.systemui.statusbar.phone.LockscreenGestureLogger;
import com.android.systemui.statusbar.phone.ManagedProfileController;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.statusbar.phone.NotificationGroupAlertTransferHelper;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.phone.StatusBarWindowController;
import com.android.systemui.statusbar.policy.AccessibilityController;
import com.android.systemui.statusbar.policy.AccessibilityManagerWrapper;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.BluetoothController;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.statusbar.policy.FlashlightController;
import com.android.systemui.statusbar.policy.HotspotController;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.statusbar.policy.LocationController;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.NextAlarmController;
import com.android.systemui.statusbar.policy.RemoteInputQuickSettingsDisabler;
import com.android.systemui.statusbar.policy.RotationLockController;
import com.android.systemui.statusbar.policy.SecurityController;
import com.android.systemui.statusbar.policy.SensorPrivacyController;
import com.android.systemui.statusbar.policy.SmartReplyConstants;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.tuner.TunablePadding;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.AsyncSensorManager;
import com.android.systemui.util.leak.GarbageMonitor;
import com.android.systemui.util.leak.LeakDetector;
import com.android.systemui.util.leak.LeakReporter;
import dagger.Lazy;
import dagger.MembersInjector;
import dagger.internal.DoubleCheck;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class Dependency_MembersInjector implements MembersInjector<Dependency> {
    private final Provider<AccessibilityController> mAccessibilityControllerProvider;
    private final Provider<AccessibilityManagerWrapper> mAccessibilityManagerWrapperProvider;
    private final Provider<ActivityManagerWrapper> mActivityManagerWrapperProvider;
    private final Provider<ActivityStarterDelegate> mActivityStarterDelegateProvider;
    private final Provider<ActivityStarter> mActivityStarterProvider;
    private final Provider<AppOpsController> mAppOpsControllerProvider;
    private final Provider<AssistManager> mAssistManagerProvider;
    private final Provider<AsyncSensorManager> mAsyncSensorManagerProvider;
    private final Provider<AutoHideController> mAutoHideControllerProvider;
    private final Provider<BatteryController> mBatteryControllerProvider;
    private final Provider<Handler> mBgHandlerProvider;
    private final Provider<Looper> mBgLooperProvider;
    private final Provider<BluetoothController> mBluetoothControllerProvider;
    private final Provider<BubbleController> mBubbleControllerProvider;
    private final Provider<CastController> mCastControllerProvider;
    private final Provider<ChannelEditorDialogController> mChannelEditorDialogControllerProvider;
    private final Provider<ClockManager> mClockManagerProvider;
    private final Provider<ConfigurationController> mConfigurationControllerProvider;
    private final Provider<DarkIconDispatcher> mDarkIconDispatcherProvider;
    private final Provider<DataSaverController> mDataSaverControllerProvider;
    private final Provider<DevicePolicyManagerWrapper> mDevicePolicyManagerWrapperProvider;
    private final Provider<DeviceProvisionedController> mDeviceProvisionedControllerProvider;
    private final Provider<DisplayMetrics> mDisplayMetricsProvider;
    private final Provider<DockManager> mDockManagerProvider;
    private final Provider<DumpController> mDumpControllerProvider;
    private final Provider<EnhancedEstimates> mEnhancedEstimatesProvider;
    private final Provider<ExtensionController> mExtensionControllerProvider;
    private final Provider<FalsingManager> mFalsingManagerProvider;
    private final Provider<FlashlightController> mFlashlightControllerProvider;
    private final Provider<ForegroundServiceController> mForegroundServiceControllerProvider;
    private final Provider<ForegroundServiceNotificationListener> mForegroundServiceNotificationListenerProvider;
    private final Provider<FragmentService> mFragmentServiceProvider;
    private final Provider<GarbageMonitor> mGarbageMonitorProvider;
    private final Provider<HotspotController> mHotspotControllerProvider;
    private final Provider<INotificationManager> mINotificationManagerProvider;
    private final Provider<IStatusBarService> mIStatusBarServiceProvider;
    private final Provider<IWindowManager> mIWindowManagerProvider;
    private final Provider<InitController> mInitControllerProvider;
    private final Provider<KeyguardDismissUtil> mKeyguardDismissUtilProvider;
    private final Provider<NotificationData.KeyguardEnvironment> mKeyguardEnvironmentProvider;
    private final Provider<KeyguardMonitor> mKeyguardMonitorProvider;
    private final Provider<LeakDetector> mLeakDetectorProvider;
    private final Provider<String> mLeakReportEmailProvider;
    private final Provider<LeakReporter> mLeakReporterProvider;
    private final Provider<LightBarController> mLightBarControllerProvider;
    private final Provider<LocalBluetoothManager> mLocalBluetoothManagerProvider;
    private final Provider<LocationController> mLocationControllerProvider;
    private final Provider<LockscreenGestureLogger> mLockscreenGestureLoggerProvider;
    private final Provider<Handler> mMainHandlerProvider;
    private final Provider<ManagedProfileController> mManagedProfileControllerProvider;
    private final Provider<MetricsLogger> mMetricsLoggerProvider;
    private final Provider<NavigationModeController> mNavBarModeControllerProvider;
    private final Provider<NavigationBarController> mNavigationBarControllerProvider;
    private final Provider<NetworkController> mNetworkControllerProvider;
    private final Provider<NextAlarmController> mNextAlarmControllerProvider;
    private final Provider<NightDisplayListener> mNightDisplayListenerProvider;
    private final Provider<NotificationAlertingManager> mNotificationAlertingManagerProvider;
    private final Provider<NotificationBlockingHelperManager> mNotificationBlockingHelperManagerProvider;
    private final Provider<NotificationEntryManager> mNotificationEntryManagerProvider;
    private final Provider<NotificationFilter> mNotificationFilterProvider;
    private final Provider<NotificationGroupAlertTransferHelper> mNotificationGroupAlertTransferHelperProvider;
    private final Provider<NotificationGroupManager> mNotificationGroupManagerProvider;
    private final Provider<NotificationGutsManager> mNotificationGutsManagerProvider;
    private final Provider<NotificationInterruptionStateProvider> mNotificationInterruptionStateProvider;
    private final Provider<NotificationListener> mNotificationListenerProvider;
    private final Provider<NotificationLockscreenUserManager> mNotificationLockscreenUserManagerProvider;
    private final Provider<NotificationLogger> mNotificationLoggerProvider;
    private final Provider<NotificationMediaManager> mNotificationMediaManagerProvider;
    private final Provider<NotificationRemoteInputManager.Callback> mNotificationRemoteInputManagerCallbackProvider;
    private final Provider<NotificationRemoteInputManager> mNotificationRemoteInputManagerProvider;
    private final Provider<NotificationViewHierarchyManager> mNotificationViewHierarchyManagerProvider;
    private final Provider<OverviewProxyService> mOverviewProxyServiceProvider;
    private final Provider<PackageManagerWrapper> mPackageManagerWrapperProvider;
    private final Provider<PluginDependencyProvider> mPluginDependencyProvider;
    private final Provider<PluginManager> mPluginManagerProvider;
    private final Provider<RemoteInputQuickSettingsDisabler> mRemoteInputQuickSettingsDisablerProvider;
    private final Provider<RotationLockController> mRotationLockControllerProvider;
    private final Provider<ScreenLifecycle> mScreenLifecycleProvider;
    private final Provider<SecurityController> mSecurityControllerProvider;
    private final Provider<SensorPrivacyController> mSensorPrivacyControllerProvider;
    private final Provider<SensorPrivacyManager> mSensorPrivacyManagerProvider;
    private final Provider<ShadeController> mShadeControllerProvider;
    private final Provider<SmartReplyConstants> mSmartReplyConstantsProvider;
    private final Provider<SmartReplyController> mSmartReplyControllerProvider;
    private final Provider<StatusBarIconController> mStatusBarIconControllerProvider;
    private final Provider<StatusBarStateController> mStatusBarStateControllerProvider;
    private final Provider<StatusBarWindowController> mStatusBarWindowControllerProvider;
    private final Provider<SysuiColorExtractor> mSysuiColorExtractorProvider;
    private final Provider<Handler> mTimeTickHandlerProvider;
    private final Provider<TunablePadding.TunablePaddingService> mTunablePaddingServiceProvider;
    private final Provider<TunerService> mTunerServiceProvider;
    private final Provider<UiOffloadThread> mUiOffloadThreadProvider;
    private final Provider<UserInfoController> mUserInfoControllerProvider;
    private final Provider<UserSwitcherController> mUserSwitcherControllerProvider;
    private final Provider<VibratorHelper> mVibratorHelperProvider;
    private final Provider<VisualStabilityManager> mVisualStabilityManagerProvider;
    private final Provider<VolumeDialogController> mVolumeDialogControllerProvider;
    private final Provider<WakefulnessLifecycle> mWakefulnessLifecycleProvider;
    private final Provider<PowerUI.WarningsUI> mWarningsUIProvider;
    private final Provider<ZenModeController> mZenModeControllerProvider;

    public Dependency_MembersInjector(Provider<ActivityStarter> mActivityStarterProvider, Provider<ActivityStarterDelegate> mActivityStarterDelegateProvider, Provider<AsyncSensorManager> mAsyncSensorManagerProvider, Provider<BluetoothController> mBluetoothControllerProvider, Provider<LocationController> mLocationControllerProvider, Provider<RotationLockController> mRotationLockControllerProvider, Provider<NetworkController> mNetworkControllerProvider, Provider<ZenModeController> mZenModeControllerProvider, Provider<HotspotController> mHotspotControllerProvider, Provider<CastController> mCastControllerProvider, Provider<FlashlightController> mFlashlightControllerProvider, Provider<UserSwitcherController> mUserSwitcherControllerProvider, Provider<UserInfoController> mUserInfoControllerProvider, Provider<KeyguardMonitor> mKeyguardMonitorProvider, Provider<BatteryController> mBatteryControllerProvider, Provider<NightDisplayListener> mNightDisplayListenerProvider, Provider<ManagedProfileController> mManagedProfileControllerProvider, Provider<NextAlarmController> mNextAlarmControllerProvider, Provider<DataSaverController> mDataSaverControllerProvider, Provider<AccessibilityController> mAccessibilityControllerProvider, Provider<DeviceProvisionedController> mDeviceProvisionedControllerProvider, Provider<PluginManager> mPluginManagerProvider, Provider<AssistManager> mAssistManagerProvider, Provider<SecurityController> mSecurityControllerProvider, Provider<LeakDetector> mLeakDetectorProvider, Provider<LeakReporter> mLeakReporterProvider, Provider<GarbageMonitor> mGarbageMonitorProvider, Provider<TunerService> mTunerServiceProvider, Provider<StatusBarWindowController> mStatusBarWindowControllerProvider, Provider<DarkIconDispatcher> mDarkIconDispatcherProvider, Provider<ConfigurationController> mConfigurationControllerProvider, Provider<StatusBarIconController> mStatusBarIconControllerProvider, Provider<ScreenLifecycle> mScreenLifecycleProvider, Provider<WakefulnessLifecycle> mWakefulnessLifecycleProvider, Provider<FragmentService> mFragmentServiceProvider, Provider<ExtensionController> mExtensionControllerProvider, Provider<PluginDependencyProvider> mPluginDependencyProvider, Provider<LocalBluetoothManager> mLocalBluetoothManagerProvider, Provider<VolumeDialogController> mVolumeDialogControllerProvider, Provider<MetricsLogger> mMetricsLoggerProvider, Provider<AccessibilityManagerWrapper> mAccessibilityManagerWrapperProvider, Provider<SysuiColorExtractor> mSysuiColorExtractorProvider, Provider<TunablePadding.TunablePaddingService> mTunablePaddingServiceProvider, Provider<ForegroundServiceController> mForegroundServiceControllerProvider, Provider<UiOffloadThread> mUiOffloadThreadProvider, Provider<PowerUI.WarningsUI> mWarningsUIProvider, Provider<LightBarController> mLightBarControllerProvider, Provider<IWindowManager> mIWindowManagerProvider, Provider<OverviewProxyService> mOverviewProxyServiceProvider, Provider<NavigationModeController> mNavBarModeControllerProvider, Provider<EnhancedEstimates> mEnhancedEstimatesProvider, Provider<VibratorHelper> mVibratorHelperProvider, Provider<IStatusBarService> mIStatusBarServiceProvider, Provider<DisplayMetrics> mDisplayMetricsProvider, Provider<LockscreenGestureLogger> mLockscreenGestureLoggerProvider, Provider<NotificationData.KeyguardEnvironment> mKeyguardEnvironmentProvider, Provider<ShadeController> mShadeControllerProvider, Provider<NotificationRemoteInputManager.Callback> mNotificationRemoteInputManagerCallbackProvider, Provider<InitController> mInitControllerProvider, Provider<AppOpsController> mAppOpsControllerProvider, Provider<NavigationBarController> mNavigationBarControllerProvider, Provider<StatusBarStateController> mStatusBarStateControllerProvider, Provider<NotificationLockscreenUserManager> mNotificationLockscreenUserManagerProvider, Provider<NotificationGroupAlertTransferHelper> mNotificationGroupAlertTransferHelperProvider, Provider<NotificationGroupManager> mNotificationGroupManagerProvider, Provider<VisualStabilityManager> mVisualStabilityManagerProvider, Provider<NotificationGutsManager> mNotificationGutsManagerProvider, Provider<NotificationMediaManager> mNotificationMediaManagerProvider, Provider<NotificationBlockingHelperManager> mNotificationBlockingHelperManagerProvider, Provider<NotificationRemoteInputManager> mNotificationRemoteInputManagerProvider, Provider<SmartReplyConstants> mSmartReplyConstantsProvider, Provider<NotificationListener> mNotificationListenerProvider, Provider<NotificationLogger> mNotificationLoggerProvider, Provider<NotificationViewHierarchyManager> mNotificationViewHierarchyManagerProvider, Provider<NotificationFilter> mNotificationFilterProvider, Provider<NotificationInterruptionStateProvider> mNotificationInterruptionStateProvider, Provider<KeyguardDismissUtil> mKeyguardDismissUtilProvider, Provider<SmartReplyController> mSmartReplyControllerProvider, Provider<RemoteInputQuickSettingsDisabler> mRemoteInputQuickSettingsDisablerProvider, Provider<BubbleController> mBubbleControllerProvider, Provider<NotificationEntryManager> mNotificationEntryManagerProvider, Provider<NotificationAlertingManager> mNotificationAlertingManagerProvider, Provider<SensorPrivacyManager> mSensorPrivacyManagerProvider, Provider<AutoHideController> mAutoHideControllerProvider, Provider<ForegroundServiceNotificationListener> mForegroundServiceNotificationListenerProvider, Provider<Looper> mBgLooperProvider, Provider<Handler> mBgHandlerProvider, Provider<Handler> mMainHandlerProvider, Provider<Handler> mTimeTickHandlerProvider, Provider<String> mLeakReportEmailProvider, Provider<ClockManager> mClockManagerProvider, Provider<ActivityManagerWrapper> mActivityManagerWrapperProvider, Provider<DevicePolicyManagerWrapper> mDevicePolicyManagerWrapperProvider, Provider<PackageManagerWrapper> mPackageManagerWrapperProvider, Provider<SensorPrivacyController> mSensorPrivacyControllerProvider, Provider<DumpController> mDumpControllerProvider, Provider<DockManager> mDockManagerProvider, Provider<ChannelEditorDialogController> mChannelEditorDialogControllerProvider, Provider<INotificationManager> mINotificationManagerProvider, Provider<FalsingManager> mFalsingManagerProvider) {
        this.mActivityStarterProvider = mActivityStarterProvider;
        this.mActivityStarterDelegateProvider = mActivityStarterDelegateProvider;
        this.mAsyncSensorManagerProvider = mAsyncSensorManagerProvider;
        this.mBluetoothControllerProvider = mBluetoothControllerProvider;
        this.mLocationControllerProvider = mLocationControllerProvider;
        this.mRotationLockControllerProvider = mRotationLockControllerProvider;
        this.mNetworkControllerProvider = mNetworkControllerProvider;
        this.mZenModeControllerProvider = mZenModeControllerProvider;
        this.mHotspotControllerProvider = mHotspotControllerProvider;
        this.mCastControllerProvider = mCastControllerProvider;
        this.mFlashlightControllerProvider = mFlashlightControllerProvider;
        this.mUserSwitcherControllerProvider = mUserSwitcherControllerProvider;
        this.mUserInfoControllerProvider = mUserInfoControllerProvider;
        this.mKeyguardMonitorProvider = mKeyguardMonitorProvider;
        this.mBatteryControllerProvider = mBatteryControllerProvider;
        this.mNightDisplayListenerProvider = mNightDisplayListenerProvider;
        this.mManagedProfileControllerProvider = mManagedProfileControllerProvider;
        this.mNextAlarmControllerProvider = mNextAlarmControllerProvider;
        this.mDataSaverControllerProvider = mDataSaverControllerProvider;
        this.mAccessibilityControllerProvider = mAccessibilityControllerProvider;
        this.mDeviceProvisionedControllerProvider = mDeviceProvisionedControllerProvider;
        this.mPluginManagerProvider = mPluginManagerProvider;
        this.mAssistManagerProvider = mAssistManagerProvider;
        this.mSecurityControllerProvider = mSecurityControllerProvider;
        this.mLeakDetectorProvider = mLeakDetectorProvider;
        this.mLeakReporterProvider = mLeakReporterProvider;
        this.mGarbageMonitorProvider = mGarbageMonitorProvider;
        this.mTunerServiceProvider = mTunerServiceProvider;
        this.mStatusBarWindowControllerProvider = mStatusBarWindowControllerProvider;
        this.mDarkIconDispatcherProvider = mDarkIconDispatcherProvider;
        this.mConfigurationControllerProvider = mConfigurationControllerProvider;
        this.mStatusBarIconControllerProvider = mStatusBarIconControllerProvider;
        this.mScreenLifecycleProvider = mScreenLifecycleProvider;
        this.mWakefulnessLifecycleProvider = mWakefulnessLifecycleProvider;
        this.mFragmentServiceProvider = mFragmentServiceProvider;
        this.mExtensionControllerProvider = mExtensionControllerProvider;
        this.mPluginDependencyProvider = mPluginDependencyProvider;
        this.mLocalBluetoothManagerProvider = mLocalBluetoothManagerProvider;
        this.mVolumeDialogControllerProvider = mVolumeDialogControllerProvider;
        this.mMetricsLoggerProvider = mMetricsLoggerProvider;
        this.mAccessibilityManagerWrapperProvider = mAccessibilityManagerWrapperProvider;
        this.mSysuiColorExtractorProvider = mSysuiColorExtractorProvider;
        this.mTunablePaddingServiceProvider = mTunablePaddingServiceProvider;
        this.mForegroundServiceControllerProvider = mForegroundServiceControllerProvider;
        this.mUiOffloadThreadProvider = mUiOffloadThreadProvider;
        this.mWarningsUIProvider = mWarningsUIProvider;
        this.mLightBarControllerProvider = mLightBarControllerProvider;
        this.mIWindowManagerProvider = mIWindowManagerProvider;
        this.mOverviewProxyServiceProvider = mOverviewProxyServiceProvider;
        this.mNavBarModeControllerProvider = mNavBarModeControllerProvider;
        this.mEnhancedEstimatesProvider = mEnhancedEstimatesProvider;
        this.mVibratorHelperProvider = mVibratorHelperProvider;
        this.mIStatusBarServiceProvider = mIStatusBarServiceProvider;
        this.mDisplayMetricsProvider = mDisplayMetricsProvider;
        this.mLockscreenGestureLoggerProvider = mLockscreenGestureLoggerProvider;
        this.mKeyguardEnvironmentProvider = mKeyguardEnvironmentProvider;
        this.mShadeControllerProvider = mShadeControllerProvider;
        this.mNotificationRemoteInputManagerCallbackProvider = mNotificationRemoteInputManagerCallbackProvider;
        this.mInitControllerProvider = mInitControllerProvider;
        this.mAppOpsControllerProvider = mAppOpsControllerProvider;
        this.mNavigationBarControllerProvider = mNavigationBarControllerProvider;
        this.mStatusBarStateControllerProvider = mStatusBarStateControllerProvider;
        this.mNotificationLockscreenUserManagerProvider = mNotificationLockscreenUserManagerProvider;
        this.mNotificationGroupAlertTransferHelperProvider = mNotificationGroupAlertTransferHelperProvider;
        this.mNotificationGroupManagerProvider = mNotificationGroupManagerProvider;
        this.mVisualStabilityManagerProvider = mVisualStabilityManagerProvider;
        this.mNotificationGutsManagerProvider = mNotificationGutsManagerProvider;
        this.mNotificationMediaManagerProvider = mNotificationMediaManagerProvider;
        this.mNotificationBlockingHelperManagerProvider = mNotificationBlockingHelperManagerProvider;
        this.mNotificationRemoteInputManagerProvider = mNotificationRemoteInputManagerProvider;
        this.mSmartReplyConstantsProvider = mSmartReplyConstantsProvider;
        this.mNotificationListenerProvider = mNotificationListenerProvider;
        this.mNotificationLoggerProvider = mNotificationLoggerProvider;
        this.mNotificationViewHierarchyManagerProvider = mNotificationViewHierarchyManagerProvider;
        this.mNotificationFilterProvider = mNotificationFilterProvider;
        this.mNotificationInterruptionStateProvider = mNotificationInterruptionStateProvider;
        this.mKeyguardDismissUtilProvider = mKeyguardDismissUtilProvider;
        this.mSmartReplyControllerProvider = mSmartReplyControllerProvider;
        this.mRemoteInputQuickSettingsDisablerProvider = mRemoteInputQuickSettingsDisablerProvider;
        this.mBubbleControllerProvider = mBubbleControllerProvider;
        this.mNotificationEntryManagerProvider = mNotificationEntryManagerProvider;
        this.mNotificationAlertingManagerProvider = mNotificationAlertingManagerProvider;
        this.mSensorPrivacyManagerProvider = mSensorPrivacyManagerProvider;
        this.mAutoHideControllerProvider = mAutoHideControllerProvider;
        this.mForegroundServiceNotificationListenerProvider = mForegroundServiceNotificationListenerProvider;
        this.mBgLooperProvider = mBgLooperProvider;
        this.mBgHandlerProvider = mBgHandlerProvider;
        this.mMainHandlerProvider = mMainHandlerProvider;
        this.mTimeTickHandlerProvider = mTimeTickHandlerProvider;
        this.mLeakReportEmailProvider = mLeakReportEmailProvider;
        this.mClockManagerProvider = mClockManagerProvider;
        this.mActivityManagerWrapperProvider = mActivityManagerWrapperProvider;
        this.mDevicePolicyManagerWrapperProvider = mDevicePolicyManagerWrapperProvider;
        this.mPackageManagerWrapperProvider = mPackageManagerWrapperProvider;
        this.mSensorPrivacyControllerProvider = mSensorPrivacyControllerProvider;
        this.mDumpControllerProvider = mDumpControllerProvider;
        this.mDockManagerProvider = mDockManagerProvider;
        this.mChannelEditorDialogControllerProvider = mChannelEditorDialogControllerProvider;
        this.mINotificationManagerProvider = mINotificationManagerProvider;
        this.mFalsingManagerProvider = mFalsingManagerProvider;
    }

    public static MembersInjector<Dependency> create(Provider<ActivityStarter> mActivityStarterProvider, Provider<ActivityStarterDelegate> mActivityStarterDelegateProvider, Provider<AsyncSensorManager> mAsyncSensorManagerProvider, Provider<BluetoothController> mBluetoothControllerProvider, Provider<LocationController> mLocationControllerProvider, Provider<RotationLockController> mRotationLockControllerProvider, Provider<NetworkController> mNetworkControllerProvider, Provider<ZenModeController> mZenModeControllerProvider, Provider<HotspotController> mHotspotControllerProvider, Provider<CastController> mCastControllerProvider, Provider<FlashlightController> mFlashlightControllerProvider, Provider<UserSwitcherController> mUserSwitcherControllerProvider, Provider<UserInfoController> mUserInfoControllerProvider, Provider<KeyguardMonitor> mKeyguardMonitorProvider, Provider<BatteryController> mBatteryControllerProvider, Provider<NightDisplayListener> mNightDisplayListenerProvider, Provider<ManagedProfileController> mManagedProfileControllerProvider, Provider<NextAlarmController> mNextAlarmControllerProvider, Provider<DataSaverController> mDataSaverControllerProvider, Provider<AccessibilityController> mAccessibilityControllerProvider, Provider<DeviceProvisionedController> mDeviceProvisionedControllerProvider, Provider<PluginManager> mPluginManagerProvider, Provider<AssistManager> mAssistManagerProvider, Provider<SecurityController> mSecurityControllerProvider, Provider<LeakDetector> mLeakDetectorProvider, Provider<LeakReporter> mLeakReporterProvider, Provider<GarbageMonitor> mGarbageMonitorProvider, Provider<TunerService> mTunerServiceProvider, Provider<StatusBarWindowController> mStatusBarWindowControllerProvider, Provider<DarkIconDispatcher> mDarkIconDispatcherProvider, Provider<ConfigurationController> mConfigurationControllerProvider, Provider<StatusBarIconController> mStatusBarIconControllerProvider, Provider<ScreenLifecycle> mScreenLifecycleProvider, Provider<WakefulnessLifecycle> mWakefulnessLifecycleProvider, Provider<FragmentService> mFragmentServiceProvider, Provider<ExtensionController> mExtensionControllerProvider, Provider<PluginDependencyProvider> mPluginDependencyProvider, Provider<LocalBluetoothManager> mLocalBluetoothManagerProvider, Provider<VolumeDialogController> mVolumeDialogControllerProvider, Provider<MetricsLogger> mMetricsLoggerProvider, Provider<AccessibilityManagerWrapper> mAccessibilityManagerWrapperProvider, Provider<SysuiColorExtractor> mSysuiColorExtractorProvider, Provider<TunablePadding.TunablePaddingService> mTunablePaddingServiceProvider, Provider<ForegroundServiceController> mForegroundServiceControllerProvider, Provider<UiOffloadThread> mUiOffloadThreadProvider, Provider<PowerUI.WarningsUI> mWarningsUIProvider, Provider<LightBarController> mLightBarControllerProvider, Provider<IWindowManager> mIWindowManagerProvider, Provider<OverviewProxyService> mOverviewProxyServiceProvider, Provider<NavigationModeController> mNavBarModeControllerProvider, Provider<EnhancedEstimates> mEnhancedEstimatesProvider, Provider<VibratorHelper> mVibratorHelperProvider, Provider<IStatusBarService> mIStatusBarServiceProvider, Provider<DisplayMetrics> mDisplayMetricsProvider, Provider<LockscreenGestureLogger> mLockscreenGestureLoggerProvider, Provider<NotificationData.KeyguardEnvironment> mKeyguardEnvironmentProvider, Provider<ShadeController> mShadeControllerProvider, Provider<NotificationRemoteInputManager.Callback> mNotificationRemoteInputManagerCallbackProvider, Provider<InitController> mInitControllerProvider, Provider<AppOpsController> mAppOpsControllerProvider, Provider<NavigationBarController> mNavigationBarControllerProvider, Provider<StatusBarStateController> mStatusBarStateControllerProvider, Provider<NotificationLockscreenUserManager> mNotificationLockscreenUserManagerProvider, Provider<NotificationGroupAlertTransferHelper> mNotificationGroupAlertTransferHelperProvider, Provider<NotificationGroupManager> mNotificationGroupManagerProvider, Provider<VisualStabilityManager> mVisualStabilityManagerProvider, Provider<NotificationGutsManager> mNotificationGutsManagerProvider, Provider<NotificationMediaManager> mNotificationMediaManagerProvider, Provider<NotificationBlockingHelperManager> mNotificationBlockingHelperManagerProvider, Provider<NotificationRemoteInputManager> mNotificationRemoteInputManagerProvider, Provider<SmartReplyConstants> mSmartReplyConstantsProvider, Provider<NotificationListener> mNotificationListenerProvider, Provider<NotificationLogger> mNotificationLoggerProvider, Provider<NotificationViewHierarchyManager> mNotificationViewHierarchyManagerProvider, Provider<NotificationFilter> mNotificationFilterProvider, Provider<NotificationInterruptionStateProvider> mNotificationInterruptionStateProvider, Provider<KeyguardDismissUtil> mKeyguardDismissUtilProvider, Provider<SmartReplyController> mSmartReplyControllerProvider, Provider<RemoteInputQuickSettingsDisabler> mRemoteInputQuickSettingsDisablerProvider, Provider<BubbleController> mBubbleControllerProvider, Provider<NotificationEntryManager> mNotificationEntryManagerProvider, Provider<NotificationAlertingManager> mNotificationAlertingManagerProvider, Provider<SensorPrivacyManager> mSensorPrivacyManagerProvider, Provider<AutoHideController> mAutoHideControllerProvider, Provider<ForegroundServiceNotificationListener> mForegroundServiceNotificationListenerProvider, Provider<Looper> mBgLooperProvider, Provider<Handler> mBgHandlerProvider, Provider<Handler> mMainHandlerProvider, Provider<Handler> mTimeTickHandlerProvider, Provider<String> mLeakReportEmailProvider, Provider<ClockManager> mClockManagerProvider, Provider<ActivityManagerWrapper> mActivityManagerWrapperProvider, Provider<DevicePolicyManagerWrapper> mDevicePolicyManagerWrapperProvider, Provider<PackageManagerWrapper> mPackageManagerWrapperProvider, Provider<SensorPrivacyController> mSensorPrivacyControllerProvider, Provider<DumpController> mDumpControllerProvider, Provider<DockManager> mDockManagerProvider, Provider<ChannelEditorDialogController> mChannelEditorDialogControllerProvider, Provider<INotificationManager> mINotificationManagerProvider, Provider<FalsingManager> mFalsingManagerProvider) {
        return new Dependency_MembersInjector(mActivityStarterProvider, mActivityStarterDelegateProvider, mAsyncSensorManagerProvider, mBluetoothControllerProvider, mLocationControllerProvider, mRotationLockControllerProvider, mNetworkControllerProvider, mZenModeControllerProvider, mHotspotControllerProvider, mCastControllerProvider, mFlashlightControllerProvider, mUserSwitcherControllerProvider, mUserInfoControllerProvider, mKeyguardMonitorProvider, mBatteryControllerProvider, mNightDisplayListenerProvider, mManagedProfileControllerProvider, mNextAlarmControllerProvider, mDataSaverControllerProvider, mAccessibilityControllerProvider, mDeviceProvisionedControllerProvider, mPluginManagerProvider, mAssistManagerProvider, mSecurityControllerProvider, mLeakDetectorProvider, mLeakReporterProvider, mGarbageMonitorProvider, mTunerServiceProvider, mStatusBarWindowControllerProvider, mDarkIconDispatcherProvider, mConfigurationControllerProvider, mStatusBarIconControllerProvider, mScreenLifecycleProvider, mWakefulnessLifecycleProvider, mFragmentServiceProvider, mExtensionControllerProvider, mPluginDependencyProvider, mLocalBluetoothManagerProvider, mVolumeDialogControllerProvider, mMetricsLoggerProvider, mAccessibilityManagerWrapperProvider, mSysuiColorExtractorProvider, mTunablePaddingServiceProvider, mForegroundServiceControllerProvider, mUiOffloadThreadProvider, mWarningsUIProvider, mLightBarControllerProvider, mIWindowManagerProvider, mOverviewProxyServiceProvider, mNavBarModeControllerProvider, mEnhancedEstimatesProvider, mVibratorHelperProvider, mIStatusBarServiceProvider, mDisplayMetricsProvider, mLockscreenGestureLoggerProvider, mKeyguardEnvironmentProvider, mShadeControllerProvider, mNotificationRemoteInputManagerCallbackProvider, mInitControllerProvider, mAppOpsControllerProvider, mNavigationBarControllerProvider, mStatusBarStateControllerProvider, mNotificationLockscreenUserManagerProvider, mNotificationGroupAlertTransferHelperProvider, mNotificationGroupManagerProvider, mVisualStabilityManagerProvider, mNotificationGutsManagerProvider, mNotificationMediaManagerProvider, mNotificationBlockingHelperManagerProvider, mNotificationRemoteInputManagerProvider, mSmartReplyConstantsProvider, mNotificationListenerProvider, mNotificationLoggerProvider, mNotificationViewHierarchyManagerProvider, mNotificationFilterProvider, mNotificationInterruptionStateProvider, mKeyguardDismissUtilProvider, mSmartReplyControllerProvider, mRemoteInputQuickSettingsDisablerProvider, mBubbleControllerProvider, mNotificationEntryManagerProvider, mNotificationAlertingManagerProvider, mSensorPrivacyManagerProvider, mAutoHideControllerProvider, mForegroundServiceNotificationListenerProvider, mBgLooperProvider, mBgHandlerProvider, mMainHandlerProvider, mTimeTickHandlerProvider, mLeakReportEmailProvider, mClockManagerProvider, mActivityManagerWrapperProvider, mDevicePolicyManagerWrapperProvider, mPackageManagerWrapperProvider, mSensorPrivacyControllerProvider, mDumpControllerProvider, mDockManagerProvider, mChannelEditorDialogControllerProvider, mINotificationManagerProvider, mFalsingManagerProvider);
    }

    @Override // dagger.MembersInjector
    public void injectMembers(Dependency instance) {
        injectMActivityStarter(instance, DoubleCheck.lazy(this.mActivityStarterProvider));
        injectMActivityStarterDelegate(instance, DoubleCheck.lazy(this.mActivityStarterDelegateProvider));
        injectMAsyncSensorManager(instance, DoubleCheck.lazy(this.mAsyncSensorManagerProvider));
        injectMBluetoothController(instance, DoubleCheck.lazy(this.mBluetoothControllerProvider));
        injectMLocationController(instance, DoubleCheck.lazy(this.mLocationControllerProvider));
        injectMRotationLockController(instance, DoubleCheck.lazy(this.mRotationLockControllerProvider));
        injectMNetworkController(instance, DoubleCheck.lazy(this.mNetworkControllerProvider));
        injectMZenModeController(instance, DoubleCheck.lazy(this.mZenModeControllerProvider));
        injectMHotspotController(instance, DoubleCheck.lazy(this.mHotspotControllerProvider));
        injectMCastController(instance, DoubleCheck.lazy(this.mCastControllerProvider));
        injectMFlashlightController(instance, DoubleCheck.lazy(this.mFlashlightControllerProvider));
        injectMUserSwitcherController(instance, DoubleCheck.lazy(this.mUserSwitcherControllerProvider));
        injectMUserInfoController(instance, DoubleCheck.lazy(this.mUserInfoControllerProvider));
        injectMKeyguardMonitor(instance, DoubleCheck.lazy(this.mKeyguardMonitorProvider));
        injectMBatteryController(instance, DoubleCheck.lazy(this.mBatteryControllerProvider));
        injectMNightDisplayListener(instance, DoubleCheck.lazy(this.mNightDisplayListenerProvider));
        injectMManagedProfileController(instance, DoubleCheck.lazy(this.mManagedProfileControllerProvider));
        injectMNextAlarmController(instance, DoubleCheck.lazy(this.mNextAlarmControllerProvider));
        injectMDataSaverController(instance, DoubleCheck.lazy(this.mDataSaverControllerProvider));
        injectMAccessibilityController(instance, DoubleCheck.lazy(this.mAccessibilityControllerProvider));
        injectMDeviceProvisionedController(instance, DoubleCheck.lazy(this.mDeviceProvisionedControllerProvider));
        injectMPluginManager(instance, DoubleCheck.lazy(this.mPluginManagerProvider));
        injectMAssistManager(instance, DoubleCheck.lazy(this.mAssistManagerProvider));
        injectMSecurityController(instance, DoubleCheck.lazy(this.mSecurityControllerProvider));
        injectMLeakDetector(instance, DoubleCheck.lazy(this.mLeakDetectorProvider));
        injectMLeakReporter(instance, DoubleCheck.lazy(this.mLeakReporterProvider));
        injectMGarbageMonitor(instance, DoubleCheck.lazy(this.mGarbageMonitorProvider));
        injectMTunerService(instance, DoubleCheck.lazy(this.mTunerServiceProvider));
        injectMStatusBarWindowController(instance, DoubleCheck.lazy(this.mStatusBarWindowControllerProvider));
        injectMDarkIconDispatcher(instance, DoubleCheck.lazy(this.mDarkIconDispatcherProvider));
        injectMConfigurationController(instance, DoubleCheck.lazy(this.mConfigurationControllerProvider));
        injectMStatusBarIconController(instance, DoubleCheck.lazy(this.mStatusBarIconControllerProvider));
        injectMScreenLifecycle(instance, DoubleCheck.lazy(this.mScreenLifecycleProvider));
        injectMWakefulnessLifecycle(instance, DoubleCheck.lazy(this.mWakefulnessLifecycleProvider));
        injectMFragmentService(instance, DoubleCheck.lazy(this.mFragmentServiceProvider));
        injectMExtensionController(instance, DoubleCheck.lazy(this.mExtensionControllerProvider));
        injectMPluginDependencyProvider(instance, DoubleCheck.lazy(this.mPluginDependencyProvider));
        injectMLocalBluetoothManager(instance, DoubleCheck.lazy(this.mLocalBluetoothManagerProvider));
        injectMVolumeDialogController(instance, DoubleCheck.lazy(this.mVolumeDialogControllerProvider));
        injectMMetricsLogger(instance, DoubleCheck.lazy(this.mMetricsLoggerProvider));
        injectMAccessibilityManagerWrapper(instance, DoubleCheck.lazy(this.mAccessibilityManagerWrapperProvider));
        injectMSysuiColorExtractor(instance, DoubleCheck.lazy(this.mSysuiColorExtractorProvider));
        injectMTunablePaddingService(instance, DoubleCheck.lazy(this.mTunablePaddingServiceProvider));
        injectMForegroundServiceController(instance, DoubleCheck.lazy(this.mForegroundServiceControllerProvider));
        injectMUiOffloadThread(instance, DoubleCheck.lazy(this.mUiOffloadThreadProvider));
        injectMWarningsUI(instance, DoubleCheck.lazy(this.mWarningsUIProvider));
        injectMLightBarController(instance, DoubleCheck.lazy(this.mLightBarControllerProvider));
        injectMIWindowManager(instance, DoubleCheck.lazy(this.mIWindowManagerProvider));
        injectMOverviewProxyService(instance, DoubleCheck.lazy(this.mOverviewProxyServiceProvider));
        injectMNavBarModeController(instance, DoubleCheck.lazy(this.mNavBarModeControllerProvider));
        injectMEnhancedEstimates(instance, DoubleCheck.lazy(this.mEnhancedEstimatesProvider));
        injectMVibratorHelper(instance, DoubleCheck.lazy(this.mVibratorHelperProvider));
        injectMIStatusBarService(instance, DoubleCheck.lazy(this.mIStatusBarServiceProvider));
        injectMDisplayMetrics(instance, DoubleCheck.lazy(this.mDisplayMetricsProvider));
        injectMLockscreenGestureLogger(instance, DoubleCheck.lazy(this.mLockscreenGestureLoggerProvider));
        injectMKeyguardEnvironment(instance, DoubleCheck.lazy(this.mKeyguardEnvironmentProvider));
        injectMShadeController(instance, DoubleCheck.lazy(this.mShadeControllerProvider));
        injectMNotificationRemoteInputManagerCallback(instance, DoubleCheck.lazy(this.mNotificationRemoteInputManagerCallbackProvider));
        injectMInitController(instance, DoubleCheck.lazy(this.mInitControllerProvider));
        injectMAppOpsController(instance, DoubleCheck.lazy(this.mAppOpsControllerProvider));
        injectMNavigationBarController(instance, DoubleCheck.lazy(this.mNavigationBarControllerProvider));
        injectMStatusBarStateController(instance, DoubleCheck.lazy(this.mStatusBarStateControllerProvider));
        injectMNotificationLockscreenUserManager(instance, DoubleCheck.lazy(this.mNotificationLockscreenUserManagerProvider));
        injectMNotificationGroupAlertTransferHelper(instance, DoubleCheck.lazy(this.mNotificationGroupAlertTransferHelperProvider));
        injectMNotificationGroupManager(instance, DoubleCheck.lazy(this.mNotificationGroupManagerProvider));
        injectMVisualStabilityManager(instance, DoubleCheck.lazy(this.mVisualStabilityManagerProvider));
        injectMNotificationGutsManager(instance, DoubleCheck.lazy(this.mNotificationGutsManagerProvider));
        injectMNotificationMediaManager(instance, DoubleCheck.lazy(this.mNotificationMediaManagerProvider));
        injectMNotificationBlockingHelperManager(instance, DoubleCheck.lazy(this.mNotificationBlockingHelperManagerProvider));
        injectMNotificationRemoteInputManager(instance, DoubleCheck.lazy(this.mNotificationRemoteInputManagerProvider));
        injectMSmartReplyConstants(instance, DoubleCheck.lazy(this.mSmartReplyConstantsProvider));
        injectMNotificationListener(instance, DoubleCheck.lazy(this.mNotificationListenerProvider));
        injectMNotificationLogger(instance, DoubleCheck.lazy(this.mNotificationLoggerProvider));
        injectMNotificationViewHierarchyManager(instance, DoubleCheck.lazy(this.mNotificationViewHierarchyManagerProvider));
        injectMNotificationFilter(instance, DoubleCheck.lazy(this.mNotificationFilterProvider));
        injectMNotificationInterruptionStateProvider(instance, DoubleCheck.lazy(this.mNotificationInterruptionStateProvider));
        injectMKeyguardDismissUtil(instance, DoubleCheck.lazy(this.mKeyguardDismissUtilProvider));
        injectMSmartReplyController(instance, DoubleCheck.lazy(this.mSmartReplyControllerProvider));
        injectMRemoteInputQuickSettingsDisabler(instance, DoubleCheck.lazy(this.mRemoteInputQuickSettingsDisablerProvider));
        injectMBubbleController(instance, DoubleCheck.lazy(this.mBubbleControllerProvider));
        injectMNotificationEntryManager(instance, DoubleCheck.lazy(this.mNotificationEntryManagerProvider));
        injectMNotificationAlertingManager(instance, DoubleCheck.lazy(this.mNotificationAlertingManagerProvider));
        injectMSensorPrivacyManager(instance, DoubleCheck.lazy(this.mSensorPrivacyManagerProvider));
        injectMAutoHideController(instance, DoubleCheck.lazy(this.mAutoHideControllerProvider));
        injectMForegroundServiceNotificationListener(instance, DoubleCheck.lazy(this.mForegroundServiceNotificationListenerProvider));
        injectMBgLooper(instance, DoubleCheck.lazy(this.mBgLooperProvider));
        injectMBgHandler(instance, DoubleCheck.lazy(this.mBgHandlerProvider));
        injectMMainHandler(instance, DoubleCheck.lazy(this.mMainHandlerProvider));
        injectMTimeTickHandler(instance, DoubleCheck.lazy(this.mTimeTickHandlerProvider));
        injectMLeakReportEmail(instance, DoubleCheck.lazy(this.mLeakReportEmailProvider));
        injectMClockManager(instance, DoubleCheck.lazy(this.mClockManagerProvider));
        injectMActivityManagerWrapper(instance, DoubleCheck.lazy(this.mActivityManagerWrapperProvider));
        injectMDevicePolicyManagerWrapper(instance, DoubleCheck.lazy(this.mDevicePolicyManagerWrapperProvider));
        injectMPackageManagerWrapper(instance, DoubleCheck.lazy(this.mPackageManagerWrapperProvider));
        injectMSensorPrivacyController(instance, DoubleCheck.lazy(this.mSensorPrivacyControllerProvider));
        injectMDumpController(instance, DoubleCheck.lazy(this.mDumpControllerProvider));
        injectMDockManager(instance, DoubleCheck.lazy(this.mDockManagerProvider));
        injectMChannelEditorDialogController(instance, DoubleCheck.lazy(this.mChannelEditorDialogControllerProvider));
        injectMINotificationManager(instance, DoubleCheck.lazy(this.mINotificationManagerProvider));
        injectMFalsingManager(instance, DoubleCheck.lazy(this.mFalsingManagerProvider));
    }

    public static void injectMActivityStarter(Dependency instance, Lazy<ActivityStarter> mActivityStarter) {
        instance.mActivityStarter = mActivityStarter;
    }

    public static void injectMActivityStarterDelegate(Dependency instance, Lazy<ActivityStarterDelegate> mActivityStarterDelegate) {
        instance.mActivityStarterDelegate = mActivityStarterDelegate;
    }

    public static void injectMAsyncSensorManager(Dependency instance, Lazy<AsyncSensorManager> mAsyncSensorManager) {
        instance.mAsyncSensorManager = mAsyncSensorManager;
    }

    public static void injectMBluetoothController(Dependency instance, Lazy<BluetoothController> mBluetoothController) {
        instance.mBluetoothController = mBluetoothController;
    }

    public static void injectMLocationController(Dependency instance, Lazy<LocationController> mLocationController) {
        instance.mLocationController = mLocationController;
    }

    public static void injectMRotationLockController(Dependency instance, Lazy<RotationLockController> mRotationLockController) {
        instance.mRotationLockController = mRotationLockController;
    }

    public static void injectMNetworkController(Dependency instance, Lazy<NetworkController> mNetworkController) {
        instance.mNetworkController = mNetworkController;
    }

    public static void injectMZenModeController(Dependency instance, Lazy<ZenModeController> mZenModeController) {
        instance.mZenModeController = mZenModeController;
    }

    public static void injectMHotspotController(Dependency instance, Lazy<HotspotController> mHotspotController) {
        instance.mHotspotController = mHotspotController;
    }

    public static void injectMCastController(Dependency instance, Lazy<CastController> mCastController) {
        instance.mCastController = mCastController;
    }

    public static void injectMFlashlightController(Dependency instance, Lazy<FlashlightController> mFlashlightController) {
        instance.mFlashlightController = mFlashlightController;
    }

    public static void injectMUserSwitcherController(Dependency instance, Lazy<UserSwitcherController> mUserSwitcherController) {
        instance.mUserSwitcherController = mUserSwitcherController;
    }

    public static void injectMUserInfoController(Dependency instance, Lazy<UserInfoController> mUserInfoController) {
        instance.mUserInfoController = mUserInfoController;
    }

    public static void injectMKeyguardMonitor(Dependency instance, Lazy<KeyguardMonitor> mKeyguardMonitor) {
        instance.mKeyguardMonitor = mKeyguardMonitor;
    }

    public static void injectMBatteryController(Dependency instance, Lazy<BatteryController> mBatteryController) {
        instance.mBatteryController = mBatteryController;
    }

    public static void injectMNightDisplayListener(Dependency instance, Lazy<NightDisplayListener> mNightDisplayListener) {
        instance.mNightDisplayListener = mNightDisplayListener;
    }

    public static void injectMManagedProfileController(Dependency instance, Lazy<ManagedProfileController> mManagedProfileController) {
        instance.mManagedProfileController = mManagedProfileController;
    }

    public static void injectMNextAlarmController(Dependency instance, Lazy<NextAlarmController> mNextAlarmController) {
        instance.mNextAlarmController = mNextAlarmController;
    }

    public static void injectMDataSaverController(Dependency instance, Lazy<DataSaverController> mDataSaverController) {
        instance.mDataSaverController = mDataSaverController;
    }

    public static void injectMAccessibilityController(Dependency instance, Lazy<AccessibilityController> mAccessibilityController) {
        instance.mAccessibilityController = mAccessibilityController;
    }

    public static void injectMDeviceProvisionedController(Dependency instance, Lazy<DeviceProvisionedController> mDeviceProvisionedController) {
        instance.mDeviceProvisionedController = mDeviceProvisionedController;
    }

    public static void injectMPluginManager(Dependency instance, Lazy<PluginManager> mPluginManager) {
        instance.mPluginManager = mPluginManager;
    }

    public static void injectMAssistManager(Dependency instance, Lazy<AssistManager> mAssistManager) {
        instance.mAssistManager = mAssistManager;
    }

    public static void injectMSecurityController(Dependency instance, Lazy<SecurityController> mSecurityController) {
        instance.mSecurityController = mSecurityController;
    }

    public static void injectMLeakDetector(Dependency instance, Lazy<LeakDetector> mLeakDetector) {
        instance.mLeakDetector = mLeakDetector;
    }

    public static void injectMLeakReporter(Dependency instance, Lazy<LeakReporter> mLeakReporter) {
        instance.mLeakReporter = mLeakReporter;
    }

    public static void injectMGarbageMonitor(Dependency instance, Lazy<GarbageMonitor> mGarbageMonitor) {
        instance.mGarbageMonitor = mGarbageMonitor;
    }

    public static void injectMTunerService(Dependency instance, Lazy<TunerService> mTunerService) {
        instance.mTunerService = mTunerService;
    }

    public static void injectMStatusBarWindowController(Dependency instance, Lazy<StatusBarWindowController> mStatusBarWindowController) {
        instance.mStatusBarWindowController = mStatusBarWindowController;
    }

    public static void injectMDarkIconDispatcher(Dependency instance, Lazy<DarkIconDispatcher> mDarkIconDispatcher) {
        instance.mDarkIconDispatcher = mDarkIconDispatcher;
    }

    public static void injectMConfigurationController(Dependency instance, Lazy<ConfigurationController> mConfigurationController) {
        instance.mConfigurationController = mConfigurationController;
    }

    public static void injectMStatusBarIconController(Dependency instance, Lazy<StatusBarIconController> mStatusBarIconController) {
        instance.mStatusBarIconController = mStatusBarIconController;
    }

    public static void injectMScreenLifecycle(Dependency instance, Lazy<ScreenLifecycle> mScreenLifecycle) {
        instance.mScreenLifecycle = mScreenLifecycle;
    }

    public static void injectMWakefulnessLifecycle(Dependency instance, Lazy<WakefulnessLifecycle> mWakefulnessLifecycle) {
        instance.mWakefulnessLifecycle = mWakefulnessLifecycle;
    }

    public static void injectMFragmentService(Dependency instance, Lazy<FragmentService> mFragmentService) {
        instance.mFragmentService = mFragmentService;
    }

    public static void injectMExtensionController(Dependency instance, Lazy<ExtensionController> mExtensionController) {
        instance.mExtensionController = mExtensionController;
    }

    public static void injectMPluginDependencyProvider(Dependency instance, Lazy<PluginDependencyProvider> mPluginDependencyProvider) {
        instance.mPluginDependencyProvider = mPluginDependencyProvider;
    }

    public static void injectMLocalBluetoothManager(Dependency instance, Lazy<LocalBluetoothManager> mLocalBluetoothManager) {
        instance.mLocalBluetoothManager = mLocalBluetoothManager;
    }

    public static void injectMVolumeDialogController(Dependency instance, Lazy<VolumeDialogController> mVolumeDialogController) {
        instance.mVolumeDialogController = mVolumeDialogController;
    }

    public static void injectMMetricsLogger(Dependency instance, Lazy<MetricsLogger> mMetricsLogger) {
        instance.mMetricsLogger = mMetricsLogger;
    }

    public static void injectMAccessibilityManagerWrapper(Dependency instance, Lazy<AccessibilityManagerWrapper> mAccessibilityManagerWrapper) {
        instance.mAccessibilityManagerWrapper = mAccessibilityManagerWrapper;
    }

    public static void injectMSysuiColorExtractor(Dependency instance, Lazy<SysuiColorExtractor> mSysuiColorExtractor) {
        instance.mSysuiColorExtractor = mSysuiColorExtractor;
    }

    public static void injectMTunablePaddingService(Dependency instance, Lazy<TunablePadding.TunablePaddingService> mTunablePaddingService) {
        instance.mTunablePaddingService = mTunablePaddingService;
    }

    public static void injectMForegroundServiceController(Dependency instance, Lazy<ForegroundServiceController> mForegroundServiceController) {
        instance.mForegroundServiceController = mForegroundServiceController;
    }

    public static void injectMUiOffloadThread(Dependency instance, Lazy<UiOffloadThread> mUiOffloadThread) {
        instance.mUiOffloadThread = mUiOffloadThread;
    }

    public static void injectMWarningsUI(Dependency instance, Lazy<PowerUI.WarningsUI> mWarningsUI) {
        instance.mWarningsUI = mWarningsUI;
    }

    public static void injectMLightBarController(Dependency instance, Lazy<LightBarController> mLightBarController) {
        instance.mLightBarController = mLightBarController;
    }

    public static void injectMIWindowManager(Dependency instance, Lazy<IWindowManager> mIWindowManager) {
        instance.mIWindowManager = mIWindowManager;
    }

    public static void injectMOverviewProxyService(Dependency instance, Lazy<OverviewProxyService> mOverviewProxyService) {
        instance.mOverviewProxyService = mOverviewProxyService;
    }

    public static void injectMNavBarModeController(Dependency instance, Lazy<NavigationModeController> mNavBarModeController) {
        instance.mNavBarModeController = mNavBarModeController;
    }

    public static void injectMEnhancedEstimates(Dependency instance, Lazy<EnhancedEstimates> mEnhancedEstimates) {
        instance.mEnhancedEstimates = mEnhancedEstimates;
    }

    public static void injectMVibratorHelper(Dependency instance, Lazy<VibratorHelper> mVibratorHelper) {
        instance.mVibratorHelper = mVibratorHelper;
    }

    public static void injectMIStatusBarService(Dependency instance, Lazy<IStatusBarService> mIStatusBarService) {
        instance.mIStatusBarService = mIStatusBarService;
    }

    public static void injectMDisplayMetrics(Dependency instance, Lazy<DisplayMetrics> mDisplayMetrics) {
        instance.mDisplayMetrics = mDisplayMetrics;
    }

    public static void injectMLockscreenGestureLogger(Dependency instance, Lazy<LockscreenGestureLogger> mLockscreenGestureLogger) {
        instance.mLockscreenGestureLogger = mLockscreenGestureLogger;
    }

    public static void injectMKeyguardEnvironment(Dependency instance, Lazy<NotificationData.KeyguardEnvironment> mKeyguardEnvironment) {
        instance.mKeyguardEnvironment = mKeyguardEnvironment;
    }

    public static void injectMShadeController(Dependency instance, Lazy<ShadeController> mShadeController) {
        instance.mShadeController = mShadeController;
    }

    public static void injectMNotificationRemoteInputManagerCallback(Dependency instance, Lazy<NotificationRemoteInputManager.Callback> mNotificationRemoteInputManagerCallback) {
        instance.mNotificationRemoteInputManagerCallback = mNotificationRemoteInputManagerCallback;
    }

    public static void injectMInitController(Dependency instance, Lazy<InitController> mInitController) {
        instance.mInitController = mInitController;
    }

    public static void injectMAppOpsController(Dependency instance, Lazy<AppOpsController> mAppOpsController) {
        instance.mAppOpsController = mAppOpsController;
    }

    public static void injectMNavigationBarController(Dependency instance, Lazy<NavigationBarController> mNavigationBarController) {
        instance.mNavigationBarController = mNavigationBarController;
    }

    public static void injectMStatusBarStateController(Dependency instance, Lazy<StatusBarStateController> mStatusBarStateController) {
        instance.mStatusBarStateController = mStatusBarStateController;
    }

    public static void injectMNotificationLockscreenUserManager(Dependency instance, Lazy<NotificationLockscreenUserManager> mNotificationLockscreenUserManager) {
        instance.mNotificationLockscreenUserManager = mNotificationLockscreenUserManager;
    }

    public static void injectMNotificationGroupAlertTransferHelper(Dependency instance, Lazy<NotificationGroupAlertTransferHelper> mNotificationGroupAlertTransferHelper) {
        instance.mNotificationGroupAlertTransferHelper = mNotificationGroupAlertTransferHelper;
    }

    public static void injectMNotificationGroupManager(Dependency instance, Lazy<NotificationGroupManager> mNotificationGroupManager) {
        instance.mNotificationGroupManager = mNotificationGroupManager;
    }

    public static void injectMVisualStabilityManager(Dependency instance, Lazy<VisualStabilityManager> mVisualStabilityManager) {
        instance.mVisualStabilityManager = mVisualStabilityManager;
    }

    public static void injectMNotificationGutsManager(Dependency instance, Lazy<NotificationGutsManager> mNotificationGutsManager) {
        instance.mNotificationGutsManager = mNotificationGutsManager;
    }

    public static void injectMNotificationMediaManager(Dependency instance, Lazy<NotificationMediaManager> mNotificationMediaManager) {
        instance.mNotificationMediaManager = mNotificationMediaManager;
    }

    public static void injectMNotificationBlockingHelperManager(Dependency instance, Lazy<NotificationBlockingHelperManager> mNotificationBlockingHelperManager) {
        instance.mNotificationBlockingHelperManager = mNotificationBlockingHelperManager;
    }

    public static void injectMNotificationRemoteInputManager(Dependency instance, Lazy<NotificationRemoteInputManager> mNotificationRemoteInputManager) {
        instance.mNotificationRemoteInputManager = mNotificationRemoteInputManager;
    }

    public static void injectMSmartReplyConstants(Dependency instance, Lazy<SmartReplyConstants> mSmartReplyConstants) {
        instance.mSmartReplyConstants = mSmartReplyConstants;
    }

    public static void injectMNotificationListener(Dependency instance, Lazy<NotificationListener> mNotificationListener) {
        instance.mNotificationListener = mNotificationListener;
    }

    public static void injectMNotificationLogger(Dependency instance, Lazy<NotificationLogger> mNotificationLogger) {
        instance.mNotificationLogger = mNotificationLogger;
    }

    public static void injectMNotificationViewHierarchyManager(Dependency instance, Lazy<NotificationViewHierarchyManager> mNotificationViewHierarchyManager) {
        instance.mNotificationViewHierarchyManager = mNotificationViewHierarchyManager;
    }

    public static void injectMNotificationFilter(Dependency instance, Lazy<NotificationFilter> mNotificationFilter) {
        instance.mNotificationFilter = mNotificationFilter;
    }

    public static void injectMNotificationInterruptionStateProvider(Dependency instance, Lazy<NotificationInterruptionStateProvider> mNotificationInterruptionStateProvider) {
        instance.mNotificationInterruptionStateProvider = mNotificationInterruptionStateProvider;
    }

    public static void injectMKeyguardDismissUtil(Dependency instance, Lazy<KeyguardDismissUtil> mKeyguardDismissUtil) {
        instance.mKeyguardDismissUtil = mKeyguardDismissUtil;
    }

    public static void injectMSmartReplyController(Dependency instance, Lazy<SmartReplyController> mSmartReplyController) {
        instance.mSmartReplyController = mSmartReplyController;
    }

    public static void injectMRemoteInputQuickSettingsDisabler(Dependency instance, Lazy<RemoteInputQuickSettingsDisabler> mRemoteInputQuickSettingsDisabler) {
        instance.mRemoteInputQuickSettingsDisabler = mRemoteInputQuickSettingsDisabler;
    }

    public static void injectMBubbleController(Dependency instance, Lazy<BubbleController> mBubbleController) {
        instance.mBubbleController = mBubbleController;
    }

    public static void injectMNotificationEntryManager(Dependency instance, Lazy<NotificationEntryManager> mNotificationEntryManager) {
        instance.mNotificationEntryManager = mNotificationEntryManager;
    }

    public static void injectMNotificationAlertingManager(Dependency instance, Lazy<NotificationAlertingManager> mNotificationAlertingManager) {
        instance.mNotificationAlertingManager = mNotificationAlertingManager;
    }

    public static void injectMSensorPrivacyManager(Dependency instance, Lazy<SensorPrivacyManager> mSensorPrivacyManager) {
        instance.mSensorPrivacyManager = mSensorPrivacyManager;
    }

    public static void injectMAutoHideController(Dependency instance, Lazy<AutoHideController> mAutoHideController) {
        instance.mAutoHideController = mAutoHideController;
    }

    public static void injectMForegroundServiceNotificationListener(Dependency instance, Lazy<ForegroundServiceNotificationListener> mForegroundServiceNotificationListener) {
        instance.mForegroundServiceNotificationListener = mForegroundServiceNotificationListener;
    }

    public static void injectMBgLooper(Dependency instance, Lazy<Looper> mBgLooper) {
        instance.mBgLooper = mBgLooper;
    }

    public static void injectMBgHandler(Dependency instance, Lazy<Handler> mBgHandler) {
        instance.mBgHandler = mBgHandler;
    }

    public static void injectMMainHandler(Dependency instance, Lazy<Handler> mMainHandler) {
        instance.mMainHandler = mMainHandler;
    }

    public static void injectMTimeTickHandler(Dependency instance, Lazy<Handler> mTimeTickHandler) {
        instance.mTimeTickHandler = mTimeTickHandler;
    }

    public static void injectMLeakReportEmail(Dependency instance, Lazy<String> mLeakReportEmail) {
        instance.mLeakReportEmail = mLeakReportEmail;
    }

    public static void injectMClockManager(Dependency instance, Lazy<ClockManager> mClockManager) {
        instance.mClockManager = mClockManager;
    }

    public static void injectMActivityManagerWrapper(Dependency instance, Lazy<ActivityManagerWrapper> mActivityManagerWrapper) {
        instance.mActivityManagerWrapper = mActivityManagerWrapper;
    }

    public static void injectMDevicePolicyManagerWrapper(Dependency instance, Lazy<DevicePolicyManagerWrapper> mDevicePolicyManagerWrapper) {
        instance.mDevicePolicyManagerWrapper = mDevicePolicyManagerWrapper;
    }

    public static void injectMPackageManagerWrapper(Dependency instance, Lazy<PackageManagerWrapper> mPackageManagerWrapper) {
        instance.mPackageManagerWrapper = mPackageManagerWrapper;
    }

    public static void injectMSensorPrivacyController(Dependency instance, Lazy<SensorPrivacyController> mSensorPrivacyController) {
        instance.mSensorPrivacyController = mSensorPrivacyController;
    }

    public static void injectMDumpController(Dependency instance, Lazy<DumpController> mDumpController) {
        instance.mDumpController = mDumpController;
    }

    public static void injectMDockManager(Dependency instance, Lazy<DockManager> mDockManager) {
        instance.mDockManager = mDockManager;
    }

    public static void injectMChannelEditorDialogController(Dependency instance, Lazy<ChannelEditorDialogController> mChannelEditorDialogController) {
        instance.mChannelEditorDialogController = mChannelEditorDialogController;
    }

    public static void injectMINotificationManager(Dependency instance, Lazy<INotificationManager> mINotificationManager) {
        instance.mINotificationManager = mINotificationManager;
    }

    public static void injectMFalsingManager(Dependency instance, Lazy<FalsingManager> mFalsingManager) {
        instance.mFalsingManager = mFalsingManager;
    }
}
