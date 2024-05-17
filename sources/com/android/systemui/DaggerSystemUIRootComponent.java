package com.android.systemui;

import android.app.INotificationManager;
import android.hardware.SensorPrivacyManager;
import android.hardware.display.NightDisplayListener;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.IWindowManager;
import androidx.slice.Clock;
import com.android.internal.app.AssistUtils;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.statusbar.IStatusBarService;
import com.android.keyguard.KeyguardClockSwitch;
import com.android.keyguard.KeyguardMessageArea;
import com.android.keyguard.KeyguardSliceView;
import com.android.keyguard.clock.ClockManager;
import com.android.keyguard.clock.ClockManager_Factory;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.systemui.Dependency;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.appops.AppOpsControllerImpl;
import com.android.systemui.appops.AppOpsControllerImpl_Factory;
import com.android.systemui.assist.AssistHandleBehaviorController;
import com.android.systemui.assist.AssistHandleBehaviorController_Factory;
import com.android.systemui.assist.AssistHandleLikeHomeBehavior_Factory;
import com.android.systemui.assist.AssistHandleOffBehavior_Factory;
import com.android.systemui.assist.AssistHandleReminderExpBehavior_Factory;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.assist.AssistManager_Factory;
import com.android.systemui.assist.AssistModule_ProvideAssistHandleBehaviorControllerMapFactory;
import com.android.systemui.assist.AssistModule_ProvideAssistUtilsFactory;
import com.android.systemui.assist.AssistModule_ProvideBackgroundHandlerFactory;
import com.android.systemui.assist.AssistModule_ProvideScreenDecorationsFactory;
import com.android.systemui.assist.AssistModule_ProvideSystemClockFactory;
import com.android.systemui.assist.DeviceConfigHelper;
import com.android.systemui.assist.DeviceConfigHelper_Factory;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.bubbles.BubbleController_Factory;
import com.android.systemui.bubbles.BubbleData;
import com.android.systemui.bubbles.BubbleData_Factory;
import com.android.systemui.classifier.FalsingManagerProxy;
import com.android.systemui.classifier.FalsingManagerProxy_Factory;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.colorextraction.SysuiColorExtractor_Factory;
import com.android.systemui.dock.DockManager;
import com.android.systemui.dock.DockManagerImpl;
import com.android.systemui.dock.DockManagerImpl_Factory;
import com.android.systemui.doze.DozeService;
import com.android.systemui.doze.DozeService_Factory;
import com.android.systemui.fragments.FragmentService;
import com.android.systemui.fragments.FragmentService_Factory;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.keyguard.ScreenLifecycle_Factory;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.keyguard.WakefulnessLifecycle_Factory;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.PluginDependencyProvider;
import com.android.systemui.plugins.PluginDependencyProvider_Factory;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.power.EnhancedEstimatesImpl;
import com.android.systemui.power.EnhancedEstimatesImpl_Factory;
import com.android.systemui.power.PowerNotificationWarnings;
import com.android.systemui.power.PowerNotificationWarnings_Factory;
import com.android.systemui.qs.AutoAddTracker_Factory;
import com.android.systemui.qs.QSCarrierGroup;
import com.android.systemui.qs.QSFooterImpl;
import com.android.systemui.qs.QSFragment;
import com.android.systemui.qs.QSPanel;
import com.android.systemui.qs.QSTileHost;
import com.android.systemui.qs.QSTileHost_Factory;
import com.android.systemui.qs.QuickQSPanel;
import com.android.systemui.qs.QuickStatusBarHeader;
import com.android.systemui.qs.customize.QSCustomizer;
import com.android.systemui.qs.tileimpl.QSFactoryImpl;
import com.android.systemui.qs.tileimpl.QSFactoryImpl_Factory;
import com.android.systemui.qs.tiles.AirplaneModeTile_Factory;
import com.android.systemui.qs.tiles.BatterySaverTile_Factory;
import com.android.systemui.qs.tiles.BluetoothTile_Factory;
import com.android.systemui.qs.tiles.CastTile_Factory;
import com.android.systemui.qs.tiles.CellularTile_Factory;
import com.android.systemui.qs.tiles.ColorInversionTile_Factory;
import com.android.systemui.qs.tiles.DataSaverTile_Factory;
import com.android.systemui.qs.tiles.DndTile_Factory;
import com.android.systemui.qs.tiles.FlashlightTile_Factory;
import com.android.systemui.qs.tiles.HotspotTile_Factory;
import com.android.systemui.qs.tiles.LocationTile_Factory;
import com.android.systemui.qs.tiles.NfcTile_Factory;
import com.android.systemui.qs.tiles.NightDisplayTile_Factory;
import com.android.systemui.qs.tiles.RotationLockTile_Factory;
import com.android.systemui.qs.tiles.UiModeNightTile_Factory;
import com.android.systemui.qs.tiles.UserTile_Factory;
import com.android.systemui.qs.tiles.WifiTile_Factory;
import com.android.systemui.qs.tiles.WorkModeTile_Factory;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.recents.OverviewProxyService_Factory;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.DevicePolicyManagerWrapper;
import com.android.systemui.shared.system.PackageManagerWrapper;
import com.android.systemui.statusbar.MediaArtworkProcessor;
import com.android.systemui.statusbar.MediaArtworkProcessor_Factory;
import com.android.systemui.statusbar.NavigationBarController;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.NotificationListener_Factory;
import com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl;
import com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl_Factory;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.NotificationMediaManager_Factory;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationRemoteInputManager_Factory;
import com.android.systemui.statusbar.NotificationShelf;
import com.android.systemui.statusbar.NotificationViewHierarchyManager;
import com.android.systemui.statusbar.NotificationViewHierarchyManager_Factory;
import com.android.systemui.statusbar.PulseExpansionHandler;
import com.android.systemui.statusbar.PulseExpansionHandler_Factory;
import com.android.systemui.statusbar.SmartReplyController;
import com.android.systemui.statusbar.SmartReplyController_Factory;
import com.android.systemui.statusbar.StatusBarStateControllerImpl;
import com.android.systemui.statusbar.StatusBarStateControllerImpl_Factory;
import com.android.systemui.statusbar.VibratorHelper;
import com.android.systemui.statusbar.VibratorHelper_Factory;
import com.android.systemui.statusbar.notification.BypassHeadsUpNotifier;
import com.android.systemui.statusbar.notification.BypassHeadsUpNotifier_Factory;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.notification.DynamicPrivacyController_Factory;
import com.android.systemui.statusbar.notification.NotificationAlertingManager;
import com.android.systemui.statusbar.notification.NotificationAlertingManager_Factory;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.NotificationEntryManager_Factory;
import com.android.systemui.statusbar.notification.NotificationFilter;
import com.android.systemui.statusbar.notification.NotificationFilter_Factory;
import com.android.systemui.statusbar.notification.NotificationInterruptionStateProvider;
import com.android.systemui.statusbar.notification.NotificationInterruptionStateProvider_Factory;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator_Factory;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.notification.VisualStabilityManager_Factory;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.logging.NotificationLogger_ExpansionStateLogger_Factory;
import com.android.systemui.statusbar.notification.logging.NotificationLogger_Factory;
import com.android.systemui.statusbar.notification.row.ChannelEditorDialogController;
import com.android.systemui.statusbar.notification.row.ChannelEditorDialogController_Factory;
import com.android.systemui.statusbar.notification.row.NotificationBlockingHelperManager;
import com.android.systemui.statusbar.notification.row.NotificationBlockingHelperManager_Factory;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager_Factory;
import com.android.systemui.statusbar.notification.stack.NotificationRoundnessManager;
import com.android.systemui.statusbar.notification.stack.NotificationRoundnessManager_Factory;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.phone.AutoHideController;
import com.android.systemui.statusbar.phone.AutoTileManager_Factory;
import com.android.systemui.statusbar.phone.DarkIconDispatcherImpl;
import com.android.systemui.statusbar.phone.DarkIconDispatcherImpl_Factory;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone_Factory;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.KeyguardBypassController_Factory;
import com.android.systemui.statusbar.phone.KeyguardDismissUtil;
import com.android.systemui.statusbar.phone.KeyguardDismissUtil_Factory;
import com.android.systemui.statusbar.phone.KeyguardEnvironmentImpl;
import com.android.systemui.statusbar.phone.KeyguardEnvironmentImpl_Factory;
import com.android.systemui.statusbar.phone.KeyguardLiftController;
import com.android.systemui.statusbar.phone.LightBarController;
import com.android.systemui.statusbar.phone.LightBarController_Factory;
import com.android.systemui.statusbar.phone.LockIcon;
import com.android.systemui.statusbar.phone.LockscreenGestureLogger;
import com.android.systemui.statusbar.phone.LockscreenGestureLogger_Factory;
import com.android.systemui.statusbar.phone.ManagedProfileControllerImpl;
import com.android.systemui.statusbar.phone.ManagedProfileControllerImpl_Factory;
import com.android.systemui.statusbar.phone.NavigationBarFragment;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.statusbar.phone.NavigationModeController_Factory;
import com.android.systemui.statusbar.phone.NotificationGroupAlertTransferHelper;
import com.android.systemui.statusbar.phone.NotificationGroupAlertTransferHelper_Factory;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.NotificationGroupManager_Factory;
import com.android.systemui.statusbar.phone.NotificationPanelView;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.phone.StatusBarIconControllerImpl;
import com.android.systemui.statusbar.phone.StatusBarIconControllerImpl_Factory;
import com.android.systemui.statusbar.phone.StatusBarRemoteInputCallback;
import com.android.systemui.statusbar.phone.StatusBarRemoteInputCallback_Factory;
import com.android.systemui.statusbar.phone.StatusBarWindowController;
import com.android.systemui.statusbar.phone.StatusBarWindowController_Factory;
import com.android.systemui.statusbar.phone.StatusBar_MembersInjector;
import com.android.systemui.statusbar.policy.AccessibilityController;
import com.android.systemui.statusbar.policy.AccessibilityController_Factory;
import com.android.systemui.statusbar.policy.AccessibilityManagerWrapper;
import com.android.systemui.statusbar.policy.AccessibilityManagerWrapper_Factory;
import com.android.systemui.statusbar.policy.BatteryControllerImpl;
import com.android.systemui.statusbar.policy.BatteryControllerImpl_Factory;
import com.android.systemui.statusbar.policy.BluetoothControllerImpl;
import com.android.systemui.statusbar.policy.BluetoothControllerImpl_Factory;
import com.android.systemui.statusbar.policy.CastControllerImpl;
import com.android.systemui.statusbar.policy.CastControllerImpl_Factory;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.DeviceProvisionedControllerImpl;
import com.android.systemui.statusbar.policy.DeviceProvisionedControllerImpl_Factory;
import com.android.systemui.statusbar.policy.ExtensionControllerImpl;
import com.android.systemui.statusbar.policy.ExtensionControllerImpl_Factory;
import com.android.systemui.statusbar.policy.FlashlightControllerImpl;
import com.android.systemui.statusbar.policy.FlashlightControllerImpl_Factory;
import com.android.systemui.statusbar.policy.HotspotControllerImpl;
import com.android.systemui.statusbar.policy.HotspotControllerImpl_Factory;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.statusbar.policy.KeyguardMonitorImpl;
import com.android.systemui.statusbar.policy.KeyguardMonitorImpl_Factory;
import com.android.systemui.statusbar.policy.LocationControllerImpl;
import com.android.systemui.statusbar.policy.LocationControllerImpl_Factory;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.NetworkControllerImpl;
import com.android.systemui.statusbar.policy.NetworkControllerImpl_Factory;
import com.android.systemui.statusbar.policy.NextAlarmController;
import com.android.systemui.statusbar.policy.NextAlarmControllerImpl;
import com.android.systemui.statusbar.policy.NextAlarmControllerImpl_Factory;
import com.android.systemui.statusbar.policy.RemoteInputQuickSettingsDisabler;
import com.android.systemui.statusbar.policy.RemoteInputQuickSettingsDisabler_Factory;
import com.android.systemui.statusbar.policy.RotationLockControllerImpl;
import com.android.systemui.statusbar.policy.RotationLockControllerImpl_Factory;
import com.android.systemui.statusbar.policy.SecurityControllerImpl;
import com.android.systemui.statusbar.policy.SecurityControllerImpl_Factory;
import com.android.systemui.statusbar.policy.SensorPrivacyControllerImpl;
import com.android.systemui.statusbar.policy.SensorPrivacyControllerImpl_Factory;
import com.android.systemui.statusbar.policy.SmartReplyConstants;
import com.android.systemui.statusbar.policy.SmartReplyConstants_Factory;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.statusbar.policy.UserInfoControllerImpl;
import com.android.systemui.statusbar.policy.UserInfoControllerImpl_Factory;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.statusbar.policy.UserSwitcherController_Factory;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.statusbar.policy.ZenModeControllerImpl;
import com.android.systemui.statusbar.policy.ZenModeControllerImpl_Factory;
import com.android.systemui.tuner.TunablePadding;
import com.android.systemui.tuner.TunablePadding_TunablePaddingService_Factory;
import com.android.systemui.tuner.TunerServiceImpl;
import com.android.systemui.tuner.TunerServiceImpl_Factory;
import com.android.systemui.util.AsyncSensorManager;
import com.android.systemui.util.AsyncSensorManager_Factory;
import com.android.systemui.util.InjectionInflationController;
import com.android.systemui.util.InjectionInflationController_Factory;
import com.android.systemui.util.InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory;
import com.android.systemui.util.InjectionInflationController_ViewAttributeProvider_ProvideContextFactory;
import com.android.systemui.util.ProximitySensor_Factory;
import com.android.systemui.util.leak.GarbageMonitor;
import com.android.systemui.util.leak.GarbageMonitor_Factory;
import com.android.systemui.util.leak.GarbageMonitor_MemoryTile_Factory;
import com.android.systemui.util.leak.LeakDetector;
import com.android.systemui.util.leak.LeakReporter;
import com.android.systemui.util.leak.LeakReporter_Factory;
import com.android.systemui.volume.VolumeDialogControllerImpl;
import com.android.systemui.volume.VolumeDialogControllerImpl_Factory;
import dagger.internal.DelegateFactory;
import dagger.internal.DoubleCheck;
import dagger.internal.InstanceFactory;
import dagger.internal.Preconditions;
import java.util.Collections;
import java.util.Map;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class DaggerSystemUIRootComponent implements SystemUIRootComponent {
    private Provider<AccessibilityController> accessibilityControllerProvider;
    private Provider<AccessibilityManagerWrapper> accessibilityManagerWrapperProvider;
    private Provider<ActivityStarterDelegate> activityStarterDelegateProvider;
    private AirplaneModeTile_Factory airplaneModeTileProvider;
    private Provider<AppOpsControllerImpl> appOpsControllerImplProvider;
    private Provider<AssistHandleBehaviorController> assistHandleBehaviorControllerProvider;
    private Provider assistHandleLikeHomeBehaviorProvider;
    private Provider assistHandleOffBehaviorProvider;
    private Provider assistHandleReminderExpBehaviorProvider;
    private Provider<AssistManager> assistManagerProvider;
    private Provider<AsyncSensorManager> asyncSensorManagerProvider;
    private AutoAddTracker_Factory autoAddTrackerProvider;
    private AutoTileManager_Factory autoTileManagerProvider;
    private Provider<BatteryControllerImpl> batteryControllerImplProvider;
    private BatterySaverTile_Factory batterySaverTileProvider;
    private Provider<BluetoothControllerImpl> bluetoothControllerImplProvider;
    private BluetoothTile_Factory bluetoothTileProvider;
    private Provider<BubbleController> bubbleControllerProvider;
    private Provider<BubbleData> bubbleDataProvider;
    private Provider<BypassHeadsUpNotifier> bypassHeadsUpNotifierProvider;
    private Provider<CastControllerImpl> castControllerImplProvider;
    private CastTile_Factory castTileProvider;
    private CellularTile_Factory cellularTileProvider;
    private Provider<ChannelEditorDialogController> channelEditorDialogControllerProvider;
    private Provider<ClockManager> clockManagerProvider;
    private ColorInversionTile_Factory colorInversionTileProvider;
    private SystemUIFactory.ContextHolder contextHolder;
    private Provider<DarkIconDispatcherImpl> darkIconDispatcherImplProvider;
    private DataSaverTile_Factory dataSaverTileProvider;
    private Provider<DeviceConfigHelper> deviceConfigHelperProvider;
    private Provider<DeviceProvisionedControllerImpl> deviceProvisionedControllerImplProvider;
    private DndTile_Factory dndTileProvider;
    private Provider<DockManagerImpl> dockManagerImplProvider;
    private Provider<DumpController> dumpControllerProvider;
    private Provider<DynamicPrivacyController> dynamicPrivacyControllerProvider;
    private Provider<EnhancedEstimatesImpl> enhancedEstimatesImplProvider;
    private NotificationLogger_ExpansionStateLogger_Factory expansionStateLoggerProvider;
    private Provider<ExtensionControllerImpl> extensionControllerImplProvider;
    private Provider<FalsingManagerProxy> falsingManagerProxyProvider;
    private Provider<FlashlightControllerImpl> flashlightControllerImplProvider;
    private FlashlightTile_Factory flashlightTileProvider;
    private Provider<ForegroundServiceController> foregroundServiceControllerProvider;
    private Provider<ForegroundServiceNotificationListener> foregroundServiceNotificationListenerProvider;
    private Provider<FragmentService> fragmentServiceProvider;
    private Provider<GarbageMonitor> garbageMonitorProvider;
    private Provider<HeadsUpManagerPhone> headsUpManagerPhoneProvider;
    private Provider<HotspotControllerImpl> hotspotControllerImplProvider;
    private HotspotTile_Factory hotspotTileProvider;
    private Provider<InitController> initControllerProvider;
    private Provider<InjectionInflationController> injectionInflationControllerProvider;
    private Provider<KeyguardBypassController> keyguardBypassControllerProvider;
    private Provider<KeyguardDismissUtil> keyguardDismissUtilProvider;
    private Provider<KeyguardEnvironmentImpl> keyguardEnvironmentImplProvider;
    private Provider<KeyguardMonitorImpl> keyguardMonitorImplProvider;
    private Provider<LeakReporter> leakReporterProvider;
    private Provider<LightBarController> lightBarControllerProvider;
    private Provider<LocationControllerImpl> locationControllerImplProvider;
    private LocationTile_Factory locationTileProvider;
    private Provider<LockscreenGestureLogger> lockscreenGestureLoggerProvider;
    private Provider<ManagedProfileControllerImpl> managedProfileControllerImplProvider;
    private Provider<MediaArtworkProcessor> mediaArtworkProcessorProvider;
    private GarbageMonitor_MemoryTile_Factory memoryTileProvider;
    private Provider<NavigationModeController> navigationModeControllerProvider;
    private Provider<NetworkControllerImpl> networkControllerImplProvider;
    private Provider<NextAlarmControllerImpl> nextAlarmControllerImplProvider;
    private NfcTile_Factory nfcTileProvider;
    private NightDisplayTile_Factory nightDisplayTileProvider;
    private Provider<NotificationAlertingManager> notificationAlertingManagerProvider;
    private Provider<NotificationBlockingHelperManager> notificationBlockingHelperManagerProvider;
    private Provider<NotificationEntryManager> notificationEntryManagerProvider;
    private Provider<NotificationFilter> notificationFilterProvider;
    private Provider<NotificationGroupAlertTransferHelper> notificationGroupAlertTransferHelperProvider;
    private Provider<NotificationGroupManager> notificationGroupManagerProvider;
    private Provider<NotificationGutsManager> notificationGutsManagerProvider;
    private Provider<NotificationInterruptionStateProvider> notificationInterruptionStateProvider;
    private Provider<NotificationListener> notificationListenerProvider;
    private Provider<NotificationLockscreenUserManagerImpl> notificationLockscreenUserManagerImplProvider;
    private Provider<NotificationLogger> notificationLoggerProvider;
    private Provider<NotificationMediaManager> notificationMediaManagerProvider;
    private Provider<NotificationRemoteInputManager> notificationRemoteInputManagerProvider;
    private Provider<NotificationRoundnessManager> notificationRoundnessManagerProvider;
    private Provider<NotificationViewHierarchyManager> notificationViewHierarchyManagerProvider;
    private Provider<NotificationWakeUpCoordinator> notificationWakeUpCoordinatorProvider;
    private Provider<OverviewProxyService> overviewProxyServiceProvider;
    private Provider<PluginDependencyProvider> pluginDependencyProvider;
    private Provider<PowerNotificationWarnings> powerNotificationWarningsProvider;
    private Provider<ActivityManagerWrapper> provideActivityManagerWrapperProvider;
    private Provider<Boolean> provideAllowNotificationLongPressProvider;
    private Provider provideAssistHandleBehaviorControllerMapProvider;
    private Provider<AssistUtils> provideAssistUtilsProvider;
    private Provider<AutoHideController> provideAutoHideControllerProvider;
    private Provider<Handler> provideBackgroundHandlerProvider;
    private Provider<Handler> provideBgHandlerProvider;
    private Provider<Looper> provideBgLooperProvider;
    private Provider<ConfigurationController> provideConfigurationControllerProvider;
    private SystemUIFactory_ContextHolder_ProvideContextFactory provideContextProvider;
    private Provider<DataSaverController> provideDataSaverControllerProvider;
    private Provider<DevicePolicyManagerWrapper> provideDevicePolicyManagerWrapperProvider;
    private Provider<DisplayMetrics> provideDisplayMetricsProvider;
    private Provider<Handler> provideHandlerProvider;
    private Provider<INotificationManager> provideINotificationManagerProvider;
    private Provider<IStatusBarService> provideIStatusBarServiceProvider;
    private Provider<IWindowManager> provideIWindowManagerProvider;
    private Provider<KeyguardLiftController> provideKeyguardLiftControllerProvider;
    private Provider<LeakDetector> provideLeakDetectorProvider;
    private Provider<String> provideLeakReportEmailProvider;
    private Provider<LocalBluetoothManager> provideLocalBluetoothControllerProvider;
    private Provider<Handler> provideMainHandlerProvider;
    private Provider<MetricsLogger> provideMetricsLoggerProvider;
    private Provider<NavigationBarController> provideNavigationBarControllerProvider;
    private Provider<NightDisplayListener> provideNightDisplayListenerProvider;
    private Provider<PackageManagerWrapper> providePackageManagerWrapperProvider;
    private Provider<PluginManager> providePluginManagerProvider;
    private AssistModule_ProvideScreenDecorationsFactory provideScreenDecorationsProvider;
    private Provider<SensorPrivacyManager> provideSensorPrivacyManagerProvider;
    private Provider<ShadeController> provideShadeControllerProvider;
    private Provider<Clock> provideSystemClockProvider;
    private ProximitySensor_Factory proximitySensorProvider;
    private Provider<PulseExpansionHandler> pulseExpansionHandlerProvider;
    private Provider<QSFactoryImpl> qSFactoryImplProvider;
    private Provider<QSTileHost> qSTileHostProvider;
    private Provider<RemoteInputQuickSettingsDisabler> remoteInputQuickSettingsDisablerProvider;
    private Provider<RotationLockControllerImpl> rotationLockControllerImplProvider;
    private RotationLockTile_Factory rotationLockTileProvider;
    private Provider<ScreenLifecycle> screenLifecycleProvider;
    private Provider<SecurityControllerImpl> securityControllerImplProvider;
    private Provider<SensorPrivacyControllerImpl> sensorPrivacyControllerImplProvider;
    private Provider<SmartReplyConstants> smartReplyConstantsProvider;
    private Provider<SmartReplyController> smartReplyControllerProvider;
    private Provider<StatusBarIconControllerImpl> statusBarIconControllerImplProvider;
    private Provider<StatusBarRemoteInputCallback> statusBarRemoteInputCallbackProvider;
    private Provider<StatusBarStateControllerImpl> statusBarStateControllerImplProvider;
    private Provider<StatusBarWindowController> statusBarWindowControllerProvider;
    private Provider<SystemUIRootComponent> systemUIRootComponentProvider;
    private Provider<SysuiColorExtractor> sysuiColorExtractorProvider;
    private Provider<TunablePadding.TunablePaddingService> tunablePaddingServiceProvider;
    private Provider<TunerServiceImpl> tunerServiceImplProvider;
    private UiModeNightTile_Factory uiModeNightTileProvider;
    private Provider<UiOffloadThread> uiOffloadThreadProvider;
    private Provider<UserInfoControllerImpl> userInfoControllerImplProvider;
    private Provider<UserSwitcherController> userSwitcherControllerProvider;
    private UserTile_Factory userTileProvider;
    private Provider<VibratorHelper> vibratorHelperProvider;
    private Provider<VisualStabilityManager> visualStabilityManagerProvider;
    private Provider<VolumeDialogControllerImpl> volumeDialogControllerImplProvider;
    private Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider;
    private WifiTile_Factory wifiTileProvider;
    private WorkModeTile_Factory workModeTileProvider;
    private Provider<ZenModeControllerImpl> zenModeControllerImplProvider;

    private DaggerSystemUIRootComponent(Builder builder) {
        initialize(builder);
        initialize2(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    private Map<Class<?>, Provider<Object>> getMapOfClassOfAndProviderOfObject() {
        return Collections.singletonMap(DozeService.class, DozeService_Factory.create());
    }

    private ContextComponentResolver getContextComponentResolver() {
        return new ContextComponentResolver(getMapOfClassOfAndProviderOfObject());
    }

    private void initialize(Builder builder) {
        this.provideContextProvider = SystemUIFactory_ContextHolder_ProvideContextFactory.create(builder.contextHolder);
        this.provideConfigurationControllerProvider = DoubleCheck.provider(DependencyProvider_ProvideConfigurationControllerFactory.create(builder.dependencyProvider, this.provideContextProvider));
        this.provideBgLooperProvider = DoubleCheck.provider(DependencyProvider_ProvideBgLooperFactory.create(builder.dependencyProvider));
        this.provideLeakDetectorProvider = DoubleCheck.provider(DependencyProvider_ProvideLeakDetectorFactory.create(builder.dependencyProvider));
        this.provideLeakReportEmailProvider = DoubleCheck.provider(SystemUIDefaultModule_ProvideLeakReportEmailFactory.create());
        this.leakReporterProvider = DoubleCheck.provider(LeakReporter_Factory.create(this.provideContextProvider, this.provideLeakDetectorProvider, this.provideLeakReportEmailProvider));
        this.garbageMonitorProvider = DoubleCheck.provider(GarbageMonitor_Factory.create(this.provideContextProvider, this.provideBgLooperProvider, this.provideLeakDetectorProvider, this.leakReporterProvider));
        this.provideAllowNotificationLongPressProvider = DoubleCheck.provider(SystemUIDefaultModule_ProvideAllowNotificationLongPressFactory.create());
        this.activityStarterDelegateProvider = DoubleCheck.provider(ActivityStarterDelegate_Factory.create());
        this.providePluginManagerProvider = DoubleCheck.provider(DependencyProvider_ProvidePluginManagerFactory.create(builder.dependencyProvider, this.provideContextProvider));
        this.asyncSensorManagerProvider = DoubleCheck.provider(AsyncSensorManager_Factory.create(this.provideContextProvider, this.providePluginManagerProvider));
        this.provideBgHandlerProvider = DoubleCheck.provider(DependencyProvider_ProvideBgHandlerFactory.create(builder.dependencyProvider, this.provideBgLooperProvider));
        this.provideLocalBluetoothControllerProvider = DoubleCheck.provider(DependencyProvider_ProvideLocalBluetoothControllerFactory.create(builder.dependencyProvider, this.provideContextProvider, this.provideBgHandlerProvider));
        this.bluetoothControllerImplProvider = DoubleCheck.provider(BluetoothControllerImpl_Factory.create(this.provideContextProvider, this.provideBgLooperProvider, this.provideLocalBluetoothControllerProvider));
        this.locationControllerImplProvider = DoubleCheck.provider(LocationControllerImpl_Factory.create(this.provideContextProvider, this.provideBgLooperProvider));
        this.rotationLockControllerImplProvider = DoubleCheck.provider(RotationLockControllerImpl_Factory.create(this.provideContextProvider));
        this.provideMainHandlerProvider = DoubleCheck.provider(DependencyProvider_ProvideMainHandlerFactory.create(builder.dependencyProvider));
        this.deviceProvisionedControllerImplProvider = DoubleCheck.provider(DeviceProvisionedControllerImpl_Factory.create(this.provideContextProvider, this.provideMainHandlerProvider));
        this.networkControllerImplProvider = DoubleCheck.provider(NetworkControllerImpl_Factory.create(this.provideContextProvider, this.provideBgLooperProvider, this.deviceProvisionedControllerImplProvider));
        this.zenModeControllerImplProvider = DoubleCheck.provider(ZenModeControllerImpl_Factory.create(this.provideContextProvider, this.provideMainHandlerProvider));
        this.hotspotControllerImplProvider = DoubleCheck.provider(HotspotControllerImpl_Factory.create(this.provideContextProvider, this.provideMainHandlerProvider));
        this.castControllerImplProvider = DoubleCheck.provider(CastControllerImpl_Factory.create(this.provideContextProvider));
        this.flashlightControllerImplProvider = DoubleCheck.provider(FlashlightControllerImpl_Factory.create(this.provideContextProvider));
        this.keyguardMonitorImplProvider = DoubleCheck.provider(KeyguardMonitorImpl_Factory.create(this.provideContextProvider));
        this.userSwitcherControllerProvider = DoubleCheck.provider(UserSwitcherController_Factory.create(this.provideContextProvider, this.keyguardMonitorImplProvider, this.provideMainHandlerProvider, this.activityStarterDelegateProvider));
        this.userInfoControllerImplProvider = DoubleCheck.provider(UserInfoControllerImpl_Factory.create(this.provideContextProvider));
        this.enhancedEstimatesImplProvider = DoubleCheck.provider(EnhancedEstimatesImpl_Factory.create());
        this.batteryControllerImplProvider = DoubleCheck.provider(BatteryControllerImpl_Factory.create(this.provideContextProvider, this.enhancedEstimatesImplProvider));
        this.provideNightDisplayListenerProvider = DoubleCheck.provider(DependencyProvider_ProvideNightDisplayListenerFactory.create(builder.dependencyProvider, this.provideContextProvider, this.provideBgHandlerProvider));
        this.managedProfileControllerImplProvider = DoubleCheck.provider(ManagedProfileControllerImpl_Factory.create(this.provideContextProvider));
        this.nextAlarmControllerImplProvider = DoubleCheck.provider(NextAlarmControllerImpl_Factory.create(this.provideContextProvider));
        this.provideDataSaverControllerProvider = DoubleCheck.provider(DependencyProvider_ProvideDataSaverControllerFactory.create(builder.dependencyProvider, this.networkControllerImplProvider));
        this.accessibilityControllerProvider = DoubleCheck.provider(AccessibilityController_Factory.create(this.provideContextProvider));
        this.provideAssistUtilsProvider = DoubleCheck.provider(AssistModule_ProvideAssistUtilsFactory.create(this.provideContextProvider));
        this.provideBackgroundHandlerProvider = DoubleCheck.provider(AssistModule_ProvideBackgroundHandlerFactory.create());
        this.provideScreenDecorationsProvider = AssistModule_ProvideScreenDecorationsFactory.create(this.provideContextProvider);
        this.deviceConfigHelperProvider = DoubleCheck.provider(DeviceConfigHelper_Factory.create());
        this.assistHandleOffBehaviorProvider = DoubleCheck.provider(AssistHandleOffBehavior_Factory.create());
        this.statusBarStateControllerImplProvider = DoubleCheck.provider(StatusBarStateControllerImpl_Factory.create());
        this.wakefulnessLifecycleProvider = DoubleCheck.provider(WakefulnessLifecycle_Factory.create());
        this.provideNavigationBarControllerProvider = DoubleCheck.provider(DependencyProvider_ProvideNavigationBarControllerFactory.create(builder.dependencyProvider, this.provideContextProvider, this.provideMainHandlerProvider));
        this.uiOffloadThreadProvider = DoubleCheck.provider(UiOffloadThread_Factory.create());
        this.navigationModeControllerProvider = DoubleCheck.provider(NavigationModeController_Factory.create(this.provideContextProvider, this.deviceProvisionedControllerImplProvider, this.uiOffloadThreadProvider));
        this.tunerServiceImplProvider = DoubleCheck.provider(TunerServiceImpl_Factory.create(this.provideContextProvider, this.provideMainHandlerProvider, this.provideLeakDetectorProvider));
        this.notificationLockscreenUserManagerImplProvider = DoubleCheck.provider(NotificationLockscreenUserManagerImpl_Factory.create(this.provideContextProvider));
        this.keyguardBypassControllerProvider = DoubleCheck.provider(KeyguardBypassController_Factory.create(this.provideContextProvider, this.tunerServiceImplProvider, this.statusBarStateControllerImplProvider, this.notificationLockscreenUserManagerImplProvider));
        this.statusBarWindowControllerProvider = DoubleCheck.provider(StatusBarWindowController_Factory.create(this.provideContextProvider, this.statusBarStateControllerImplProvider, this.provideConfigurationControllerProvider, this.keyguardBypassControllerProvider));
        this.overviewProxyServiceProvider = DoubleCheck.provider(OverviewProxyService_Factory.create(this.provideContextProvider, this.deviceProvisionedControllerImplProvider, this.provideNavigationBarControllerProvider, this.navigationModeControllerProvider, this.statusBarWindowControllerProvider));
        this.assistHandleLikeHomeBehaviorProvider = DoubleCheck.provider(AssistHandleLikeHomeBehavior_Factory.create(this.statusBarStateControllerImplProvider, this.wakefulnessLifecycleProvider, this.overviewProxyServiceProvider));
        this.provideSystemClockProvider = DoubleCheck.provider(AssistModule_ProvideSystemClockFactory.create());
        this.provideActivityManagerWrapperProvider = DoubleCheck.provider(DependencyProvider_ProvideActivityManagerWrapperFactory.create(builder.dependencyProvider));
        this.providePackageManagerWrapperProvider = DoubleCheck.provider(DependencyProvider_ProvidePackageManagerWrapperFactory.create(builder.dependencyProvider));
        this.assistHandleReminderExpBehaviorProvider = DoubleCheck.provider(AssistHandleReminderExpBehavior_Factory.create(this.provideSystemClockProvider, this.provideBackgroundHandlerProvider, this.deviceConfigHelperProvider, this.statusBarStateControllerImplProvider, this.provideActivityManagerWrapperProvider, this.overviewProxyServiceProvider, this.wakefulnessLifecycleProvider, this.providePackageManagerWrapperProvider));
        this.provideAssistHandleBehaviorControllerMapProvider = DoubleCheck.provider(AssistModule_ProvideAssistHandleBehaviorControllerMapFactory.create(this.assistHandleOffBehaviorProvider, this.assistHandleLikeHomeBehaviorProvider, this.assistHandleReminderExpBehaviorProvider));
        this.dumpControllerProvider = DoubleCheck.provider(DumpController_Factory.create());
        this.assistHandleBehaviorControllerProvider = DoubleCheck.provider(AssistHandleBehaviorController_Factory.create(this.provideContextProvider, this.provideAssistUtilsProvider, this.provideBackgroundHandlerProvider, this.provideScreenDecorationsProvider, this.deviceConfigHelperProvider, this.provideAssistHandleBehaviorControllerMapProvider, this.navigationModeControllerProvider, this.dumpControllerProvider));
        this.assistManagerProvider = DoubleCheck.provider(AssistManager_Factory.create(this.deviceProvisionedControllerImplProvider, this.provideContextProvider, this.provideAssistUtilsProvider, this.assistHandleBehaviorControllerProvider, this.provideConfigurationControllerProvider, this.overviewProxyServiceProvider));
        this.securityControllerImplProvider = DoubleCheck.provider(SecurityControllerImpl_Factory.create(this.provideContextProvider, this.provideBgHandlerProvider));
        this.darkIconDispatcherImplProvider = DoubleCheck.provider(DarkIconDispatcherImpl_Factory.create(this.provideContextProvider));
        this.statusBarIconControllerImplProvider = DoubleCheck.provider(StatusBarIconControllerImpl_Factory.create(this.provideContextProvider));
        this.screenLifecycleProvider = DoubleCheck.provider(ScreenLifecycle_Factory.create());
        this.systemUIRootComponentProvider = InstanceFactory.create(this);
        this.fragmentServiceProvider = DoubleCheck.provider(FragmentService_Factory.create(this.systemUIRootComponentProvider, this.provideConfigurationControllerProvider));
        this.extensionControllerImplProvider = DoubleCheck.provider(ExtensionControllerImpl_Factory.create(this.provideContextProvider, this.provideLeakDetectorProvider, this.providePluginManagerProvider, this.tunerServiceImplProvider, this.provideConfigurationControllerProvider));
        this.pluginDependencyProvider = DoubleCheck.provider(PluginDependencyProvider_Factory.create(this.providePluginManagerProvider));
        this.volumeDialogControllerImplProvider = DoubleCheck.provider(VolumeDialogControllerImpl_Factory.create(this.provideContextProvider));
        this.provideMetricsLoggerProvider = DoubleCheck.provider(DependencyProvider_ProvideMetricsLoggerFactory.create(builder.dependencyProvider));
        this.accessibilityManagerWrapperProvider = DoubleCheck.provider(AccessibilityManagerWrapper_Factory.create(this.provideContextProvider));
        this.sysuiColorExtractorProvider = DoubleCheck.provider(SysuiColorExtractor_Factory.create(this.provideContextProvider, this.provideConfigurationControllerProvider));
        this.tunablePaddingServiceProvider = DoubleCheck.provider(TunablePadding_TunablePaddingService_Factory.create(this.tunerServiceImplProvider));
        this.foregroundServiceControllerProvider = DoubleCheck.provider(ForegroundServiceController_Factory.create());
        this.powerNotificationWarningsProvider = DoubleCheck.provider(PowerNotificationWarnings_Factory.create(this.provideContextProvider, this.activityStarterDelegateProvider));
        this.lightBarControllerProvider = DoubleCheck.provider(LightBarController_Factory.create(this.provideContextProvider, this.darkIconDispatcherImplProvider, this.batteryControllerImplProvider));
        this.provideIWindowManagerProvider = DoubleCheck.provider(DependencyProvider_ProvideIWindowManagerFactory.create(builder.dependencyProvider));
        this.vibratorHelperProvider = DoubleCheck.provider(VibratorHelper_Factory.create(this.provideContextProvider));
        this.provideIStatusBarServiceProvider = DoubleCheck.provider(DependencyProvider_ProvideIStatusBarServiceFactory.create(builder.dependencyProvider));
        this.provideDisplayMetricsProvider = DoubleCheck.provider(DependencyProvider_ProvideDisplayMetricsFactory.create(builder.dependencyProvider));
        this.lockscreenGestureLoggerProvider = DoubleCheck.provider(LockscreenGestureLogger_Factory.create());
        this.keyguardEnvironmentImplProvider = DoubleCheck.provider(KeyguardEnvironmentImpl_Factory.create());
        this.provideShadeControllerProvider = DoubleCheck.provider(SystemUIDefaultModule_ProvideShadeControllerFactory.create(this.provideContextProvider));
        this.notificationGroupManagerProvider = DoubleCheck.provider(NotificationGroupManager_Factory.create(this.statusBarStateControllerImplProvider));
        this.statusBarRemoteInputCallbackProvider = DoubleCheck.provider(StatusBarRemoteInputCallback_Factory.create(this.provideContextProvider, this.notificationGroupManagerProvider));
        this.initControllerProvider = DoubleCheck.provider(InitController_Factory.create());
        this.appOpsControllerImplProvider = DoubleCheck.provider(AppOpsControllerImpl_Factory.create(this.provideContextProvider, this.provideBgLooperProvider));
        this.notificationGroupAlertTransferHelperProvider = DoubleCheck.provider(NotificationGroupAlertTransferHelper_Factory.create());
        this.notificationEntryManagerProvider = DoubleCheck.provider(NotificationEntryManager_Factory.create(this.provideContextProvider));
        this.visualStabilityManagerProvider = DoubleCheck.provider(VisualStabilityManager_Factory.create(this.notificationEntryManagerProvider, this.provideMainHandlerProvider));
        this.notificationGutsManagerProvider = DoubleCheck.provider(NotificationGutsManager_Factory.create(this.provideContextProvider, this.visualStabilityManagerProvider));
        this.mediaArtworkProcessorProvider = DoubleCheck.provider(MediaArtworkProcessor_Factory.create());
        this.notificationMediaManagerProvider = DoubleCheck.provider(NotificationMediaManager_Factory.create(this.provideContextProvider, this.provideShadeControllerProvider, this.statusBarWindowControllerProvider, this.notificationEntryManagerProvider, this.mediaArtworkProcessorProvider, this.keyguardBypassControllerProvider));
        this.notificationBlockingHelperManagerProvider = DoubleCheck.provider(NotificationBlockingHelperManager_Factory.create(this.provideContextProvider));
        this.smartReplyControllerProvider = DoubleCheck.provider(SmartReplyController_Factory.create(this.notificationEntryManagerProvider, this.provideIStatusBarServiceProvider));
        this.notificationRemoteInputManagerProvider = DoubleCheck.provider(NotificationRemoteInputManager_Factory.create(this.provideContextProvider, this.notificationLockscreenUserManagerImplProvider, this.smartReplyControllerProvider, this.notificationEntryManagerProvider, this.provideShadeControllerProvider, this.statusBarStateControllerImplProvider, this.provideMainHandlerProvider));
        this.smartReplyConstantsProvider = DoubleCheck.provider(SmartReplyConstants_Factory.create(this.provideMainHandlerProvider, this.provideContextProvider));
        this.notificationListenerProvider = DoubleCheck.provider(NotificationListener_Factory.create(this.provideContextProvider));
        this.expansionStateLoggerProvider = NotificationLogger_ExpansionStateLogger_Factory.create(this.uiOffloadThreadProvider);
        this.notificationLoggerProvider = DoubleCheck.provider(NotificationLogger_Factory.create(this.notificationListenerProvider, this.uiOffloadThreadProvider, this.notificationEntryManagerProvider, this.statusBarStateControllerImplProvider, this.expansionStateLoggerProvider));
        this.bubbleDataProvider = DoubleCheck.provider(BubbleData_Factory.create(this.provideContextProvider));
        this.notificationFilterProvider = DoubleCheck.provider(NotificationFilter_Factory.create());
        this.notificationInterruptionStateProvider = DoubleCheck.provider(NotificationInterruptionStateProvider_Factory.create(this.provideContextProvider, this.notificationFilterProvider, this.statusBarStateControllerImplProvider, this.batteryControllerImplProvider));
    }

    private void initialize2(Builder builder) {
        this.bubbleControllerProvider = DoubleCheck.provider(BubbleController_Factory.create(this.provideContextProvider, this.statusBarWindowControllerProvider, this.bubbleDataProvider, this.provideConfigurationControllerProvider, this.notificationInterruptionStateProvider, this.zenModeControllerImplProvider, this.notificationLockscreenUserManagerImplProvider, this.notificationGroupManagerProvider));
        this.dynamicPrivacyControllerProvider = DoubleCheck.provider(DynamicPrivacyController_Factory.create(this.provideContextProvider, this.keyguardMonitorImplProvider, this.notificationLockscreenUserManagerImplProvider, this.statusBarStateControllerImplProvider));
        this.notificationViewHierarchyManagerProvider = DoubleCheck.provider(NotificationViewHierarchyManager_Factory.create(this.provideContextProvider, this.provideMainHandlerProvider, this.notificationLockscreenUserManagerImplProvider, this.notificationGroupManagerProvider, this.visualStabilityManagerProvider, this.statusBarStateControllerImplProvider, this.notificationEntryManagerProvider, this.provideShadeControllerProvider, this.keyguardBypassControllerProvider, this.bubbleControllerProvider, this.dynamicPrivacyControllerProvider));
        this.keyguardDismissUtilProvider = DoubleCheck.provider(KeyguardDismissUtil_Factory.create());
        this.remoteInputQuickSettingsDisablerProvider = DoubleCheck.provider(RemoteInputQuickSettingsDisabler_Factory.create(this.provideContextProvider, this.provideConfigurationControllerProvider));
        this.notificationAlertingManagerProvider = DoubleCheck.provider(NotificationAlertingManager_Factory.create(this.notificationEntryManagerProvider, this.notificationRemoteInputManagerProvider, this.visualStabilityManagerProvider, this.provideShadeControllerProvider, this.notificationInterruptionStateProvider, this.notificationListenerProvider));
        this.provideSensorPrivacyManagerProvider = DoubleCheck.provider(DependencyProvider_ProvideSensorPrivacyManagerFactory.create(builder.dependencyProvider, this.provideContextProvider));
        this.provideAutoHideControllerProvider = DoubleCheck.provider(DependencyProvider_ProvideAutoHideControllerFactory.create(builder.dependencyProvider, this.provideContextProvider, this.provideMainHandlerProvider));
        this.foregroundServiceNotificationListenerProvider = DoubleCheck.provider(ForegroundServiceNotificationListener_Factory.create(this.provideContextProvider, this.foregroundServiceControllerProvider, this.notificationEntryManagerProvider));
        this.provideHandlerProvider = DoubleCheck.provider(DependencyProvider_ProvideHandlerFactory.create(builder.dependencyProvider));
        this.injectionInflationControllerProvider = DoubleCheck.provider(InjectionInflationController_Factory.create(this.systemUIRootComponentProvider));
        this.dockManagerImplProvider = DoubleCheck.provider(DockManagerImpl_Factory.create());
        this.clockManagerProvider = DoubleCheck.provider(ClockManager_Factory.create(this.provideContextProvider, this.injectionInflationControllerProvider, this.providePluginManagerProvider, this.sysuiColorExtractorProvider, this.dockManagerImplProvider));
        this.provideDevicePolicyManagerWrapperProvider = DoubleCheck.provider(DependencyProvider_ProvideDevicePolicyManagerWrapperFactory.create(builder.dependencyProvider));
        this.sensorPrivacyControllerImplProvider = DoubleCheck.provider(SensorPrivacyControllerImpl_Factory.create(this.provideContextProvider));
        this.provideINotificationManagerProvider = DoubleCheck.provider(DependencyProvider_ProvideINotificationManagerFactory.create(builder.dependencyProvider));
        this.channelEditorDialogControllerProvider = DoubleCheck.provider(ChannelEditorDialogController_Factory.create(this.provideContextProvider, this.provideINotificationManagerProvider));
        this.proximitySensorProvider = ProximitySensor_Factory.create(this.provideContextProvider, this.asyncSensorManagerProvider);
        this.falsingManagerProxyProvider = DoubleCheck.provider(FalsingManagerProxy_Factory.create(this.provideContextProvider, this.providePluginManagerProvider, this.provideMainHandlerProvider, this.proximitySensorProvider));
        this.headsUpManagerPhoneProvider = DoubleCheck.provider(HeadsUpManagerPhone_Factory.create(this.provideContextProvider, this.statusBarStateControllerImplProvider, this.keyguardBypassControllerProvider));
        this.notificationWakeUpCoordinatorProvider = DoubleCheck.provider(NotificationWakeUpCoordinator_Factory.create(this.provideContextProvider, this.headsUpManagerPhoneProvider, this.statusBarStateControllerImplProvider, this.keyguardBypassControllerProvider));
        this.notificationRoundnessManagerProvider = DoubleCheck.provider(NotificationRoundnessManager_Factory.create(this.keyguardBypassControllerProvider));
        this.pulseExpansionHandlerProvider = DoubleCheck.provider(PulseExpansionHandler_Factory.create(this.provideContextProvider, this.notificationWakeUpCoordinatorProvider, this.keyguardBypassControllerProvider, this.headsUpManagerPhoneProvider, this.notificationRoundnessManagerProvider, this.statusBarStateControllerImplProvider));
        this.bypassHeadsUpNotifierProvider = DoubleCheck.provider(BypassHeadsUpNotifier_Factory.create(this.provideContextProvider, this.keyguardBypassControllerProvider, this.statusBarStateControllerImplProvider, this.headsUpManagerPhoneProvider, this.notificationLockscreenUserManagerImplProvider, this.notificationMediaManagerProvider, this.tunerServiceImplProvider));
        this.provideKeyguardLiftControllerProvider = DoubleCheck.provider(SystemUIModule_ProvideKeyguardLiftControllerFactory.create(this.provideContextProvider, this.statusBarStateControllerImplProvider, this.asyncSensorManagerProvider));
        this.contextHolder = builder.contextHolder;
        this.qSTileHostProvider = new DelegateFactory();
        this.wifiTileProvider = WifiTile_Factory.create(this.qSTileHostProvider, this.networkControllerImplProvider, this.activityStarterDelegateProvider);
        this.bluetoothTileProvider = BluetoothTile_Factory.create(this.qSTileHostProvider, this.bluetoothControllerImplProvider, this.activityStarterDelegateProvider);
        this.cellularTileProvider = CellularTile_Factory.create(this.qSTileHostProvider, this.networkControllerImplProvider, this.activityStarterDelegateProvider);
        this.dndTileProvider = DndTile_Factory.create(this.qSTileHostProvider, this.zenModeControllerImplProvider, this.activityStarterDelegateProvider);
        this.colorInversionTileProvider = ColorInversionTile_Factory.create(this.qSTileHostProvider);
        this.airplaneModeTileProvider = AirplaneModeTile_Factory.create(this.qSTileHostProvider, this.activityStarterDelegateProvider);
        this.workModeTileProvider = WorkModeTile_Factory.create(this.qSTileHostProvider, this.managedProfileControllerImplProvider);
        this.rotationLockTileProvider = RotationLockTile_Factory.create(this.qSTileHostProvider, this.rotationLockControllerImplProvider);
        this.flashlightTileProvider = FlashlightTile_Factory.create(this.qSTileHostProvider, this.flashlightControllerImplProvider);
        this.locationTileProvider = LocationTile_Factory.create(this.qSTileHostProvider, this.locationControllerImplProvider, this.keyguardMonitorImplProvider, this.activityStarterDelegateProvider);
        this.castTileProvider = CastTile_Factory.create(this.qSTileHostProvider, this.castControllerImplProvider, this.keyguardMonitorImplProvider, this.networkControllerImplProvider, this.activityStarterDelegateProvider);
        this.hotspotTileProvider = HotspotTile_Factory.create(this.qSTileHostProvider, this.hotspotControllerImplProvider, this.provideDataSaverControllerProvider);
        this.userTileProvider = UserTile_Factory.create(this.qSTileHostProvider, this.userSwitcherControllerProvider, this.userInfoControllerImplProvider);
        this.batterySaverTileProvider = BatterySaverTile_Factory.create(this.qSTileHostProvider, this.batteryControllerImplProvider);
        this.dataSaverTileProvider = DataSaverTile_Factory.create(this.qSTileHostProvider, this.networkControllerImplProvider);
        this.nightDisplayTileProvider = NightDisplayTile_Factory.create(this.qSTileHostProvider);
        this.nfcTileProvider = NfcTile_Factory.create(this.qSTileHostProvider);
        this.memoryTileProvider = GarbageMonitor_MemoryTile_Factory.create(this.qSTileHostProvider);
        this.uiModeNightTileProvider = UiModeNightTile_Factory.create(this.qSTileHostProvider, this.provideConfigurationControllerProvider, this.batteryControllerImplProvider);
        this.qSFactoryImplProvider = DoubleCheck.provider(QSFactoryImpl_Factory.create(this.wifiTileProvider, this.bluetoothTileProvider, this.cellularTileProvider, this.dndTileProvider, this.colorInversionTileProvider, this.airplaneModeTileProvider, this.workModeTileProvider, this.rotationLockTileProvider, this.flashlightTileProvider, this.locationTileProvider, this.castTileProvider, this.hotspotTileProvider, this.userTileProvider, this.batterySaverTileProvider, this.dataSaverTileProvider, this.nightDisplayTileProvider, this.nfcTileProvider, this.memoryTileProvider, this.uiModeNightTileProvider));
        this.autoAddTrackerProvider = AutoAddTracker_Factory.create(this.provideContextProvider);
        this.autoTileManagerProvider = AutoTileManager_Factory.create(this.provideContextProvider, this.autoAddTrackerProvider, this.qSTileHostProvider, this.provideBgHandlerProvider, this.hotspotControllerImplProvider, this.provideDataSaverControllerProvider, this.managedProfileControllerImplProvider, this.provideNightDisplayListenerProvider, this.castControllerImplProvider);
        DelegateFactory qSTileHostProviderDelegate = (DelegateFactory) this.qSTileHostProvider;
        this.qSTileHostProvider = DoubleCheck.provider(QSTileHost_Factory.create(this.provideContextProvider, this.statusBarIconControllerImplProvider, this.qSFactoryImplProvider, this.provideMainHandlerProvider, this.provideBgLooperProvider, this.providePluginManagerProvider, this.tunerServiceImplProvider, this.autoTileManagerProvider, this.dumpControllerProvider));
        qSTileHostProviderDelegate.setDelegatedProvider(this.qSTileHostProvider);
    }

    @Override // com.android.systemui.SystemUIRootComponent
    public ConfigurationController getConfigurationController() {
        return this.provideConfigurationControllerProvider.get();
    }

    @Override // com.android.systemui.SystemUIRootComponent
    public GarbageMonitor createGarbageMonitor() {
        return this.garbageMonitorProvider.get();
    }

    @Override // com.android.systemui.SystemUIRootComponent
    public boolean allowNotificationLongPressName() {
        return this.provideAllowNotificationLongPressProvider.get().booleanValue();
    }

    @Override // com.android.systemui.SystemUIRootComponent
    public void inject(SystemUIAppComponentFactory factory) {
        injectSystemUIAppComponentFactory(factory);
    }

    @Override // com.android.systemui.SystemUIRootComponent
    public Dependency.DependencyInjector createDependency() {
        return new DependencyInjectorImpl();
    }

    @Override // com.android.systemui.SystemUIRootComponent
    public StatusBar.StatusBarInjector getStatusBarInjector() {
        return new StatusBarInjectorImpl();
    }

    @Override // com.android.systemui.SystemUIRootComponent
    public FragmentService.FragmentCreator createFragmentCreator() {
        return new FragmentCreatorImpl();
    }

    @Override // com.android.systemui.SystemUIRootComponent
    public InjectionInflationController.ViewCreator createViewCreator() {
        return new ViewCreatorImpl();
    }

    private SystemUIAppComponentFactory injectSystemUIAppComponentFactory(SystemUIAppComponentFactory instance) {
        SystemUIAppComponentFactory_MembersInjector.injectMComponentHelper(instance, getContextComponentResolver());
        return instance;
    }

    /* loaded from: classes21.dex */
    public static final class Builder {
        private SystemUIFactory.ContextHolder contextHolder;
        private DependencyProvider dependencyProvider;

        private Builder() {
        }

        public SystemUIRootComponent build() {
            if (this.dependencyProvider == null) {
                this.dependencyProvider = new DependencyProvider();
            }
            if (this.contextHolder == null) {
                throw new IllegalStateException(SystemUIFactory.ContextHolder.class.getCanonicalName() + " must be set");
            }
            return new DaggerSystemUIRootComponent(this);
        }

        public Builder dependencyProvider(DependencyProvider dependencyProvider) {
            this.dependencyProvider = (DependencyProvider) Preconditions.checkNotNull(dependencyProvider);
            return this;
        }

        public Builder contextHolder(SystemUIFactory.ContextHolder contextHolder) {
            this.contextHolder = (SystemUIFactory.ContextHolder) Preconditions.checkNotNull(contextHolder);
            return this;
        }
    }

    /* loaded from: classes21.dex */
    private final class DependencyInjectorImpl implements Dependency.DependencyInjector {
        private DependencyInjectorImpl() {
        }

        @Override // com.android.systemui.Dependency.DependencyInjector
        public void createSystemUI(Dependency dependency) {
            injectDependency(dependency);
        }

        private Dependency injectDependency(Dependency instance) {
            Dependency_MembersInjector.injectMActivityStarter(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.activityStarterDelegateProvider));
            Dependency_MembersInjector.injectMActivityStarterDelegate(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.activityStarterDelegateProvider));
            Dependency_MembersInjector.injectMAsyncSensorManager(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.asyncSensorManagerProvider));
            Dependency_MembersInjector.injectMBluetoothController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.bluetoothControllerImplProvider));
            Dependency_MembersInjector.injectMLocationController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.locationControllerImplProvider));
            Dependency_MembersInjector.injectMRotationLockController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.rotationLockControllerImplProvider));
            Dependency_MembersInjector.injectMNetworkController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.networkControllerImplProvider));
            Dependency_MembersInjector.injectMZenModeController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.zenModeControllerImplProvider));
            Dependency_MembersInjector.injectMHotspotController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.hotspotControllerImplProvider));
            Dependency_MembersInjector.injectMCastController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.castControllerImplProvider));
            Dependency_MembersInjector.injectMFlashlightController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.flashlightControllerImplProvider));
            Dependency_MembersInjector.injectMUserSwitcherController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.userSwitcherControllerProvider));
            Dependency_MembersInjector.injectMUserInfoController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.userInfoControllerImplProvider));
            Dependency_MembersInjector.injectMKeyguardMonitor(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.keyguardMonitorImplProvider));
            Dependency_MembersInjector.injectMBatteryController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.batteryControllerImplProvider));
            Dependency_MembersInjector.injectMNightDisplayListener(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideNightDisplayListenerProvider));
            Dependency_MembersInjector.injectMManagedProfileController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.managedProfileControllerImplProvider));
            Dependency_MembersInjector.injectMNextAlarmController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.nextAlarmControllerImplProvider));
            Dependency_MembersInjector.injectMDataSaverController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideDataSaverControllerProvider));
            Dependency_MembersInjector.injectMAccessibilityController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.accessibilityControllerProvider));
            Dependency_MembersInjector.injectMDeviceProvisionedController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.deviceProvisionedControllerImplProvider));
            Dependency_MembersInjector.injectMPluginManager(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.providePluginManagerProvider));
            Dependency_MembersInjector.injectMAssistManager(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.assistManagerProvider));
            Dependency_MembersInjector.injectMSecurityController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.securityControllerImplProvider));
            Dependency_MembersInjector.injectMLeakDetector(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideLeakDetectorProvider));
            Dependency_MembersInjector.injectMLeakReporter(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.leakReporterProvider));
            Dependency_MembersInjector.injectMGarbageMonitor(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.garbageMonitorProvider));
            Dependency_MembersInjector.injectMTunerService(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.tunerServiceImplProvider));
            Dependency_MembersInjector.injectMStatusBarWindowController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.statusBarWindowControllerProvider));
            Dependency_MembersInjector.injectMDarkIconDispatcher(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.darkIconDispatcherImplProvider));
            Dependency_MembersInjector.injectMConfigurationController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideConfigurationControllerProvider));
            Dependency_MembersInjector.injectMStatusBarIconController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.statusBarIconControllerImplProvider));
            Dependency_MembersInjector.injectMScreenLifecycle(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.screenLifecycleProvider));
            Dependency_MembersInjector.injectMWakefulnessLifecycle(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.wakefulnessLifecycleProvider));
            Dependency_MembersInjector.injectMFragmentService(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.fragmentServiceProvider));
            Dependency_MembersInjector.injectMExtensionController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.extensionControllerImplProvider));
            Dependency_MembersInjector.injectMPluginDependencyProvider(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.pluginDependencyProvider));
            Dependency_MembersInjector.injectMLocalBluetoothManager(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideLocalBluetoothControllerProvider));
            Dependency_MembersInjector.injectMVolumeDialogController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.volumeDialogControllerImplProvider));
            Dependency_MembersInjector.injectMMetricsLogger(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideMetricsLoggerProvider));
            Dependency_MembersInjector.injectMAccessibilityManagerWrapper(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.accessibilityManagerWrapperProvider));
            Dependency_MembersInjector.injectMSysuiColorExtractor(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.sysuiColorExtractorProvider));
            Dependency_MembersInjector.injectMTunablePaddingService(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.tunablePaddingServiceProvider));
            Dependency_MembersInjector.injectMForegroundServiceController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.foregroundServiceControllerProvider));
            Dependency_MembersInjector.injectMUiOffloadThread(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.uiOffloadThreadProvider));
            Dependency_MembersInjector.injectMWarningsUI(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.powerNotificationWarningsProvider));
            Dependency_MembersInjector.injectMLightBarController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.lightBarControllerProvider));
            Dependency_MembersInjector.injectMIWindowManager(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideIWindowManagerProvider));
            Dependency_MembersInjector.injectMOverviewProxyService(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.overviewProxyServiceProvider));
            Dependency_MembersInjector.injectMNavBarModeController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.navigationModeControllerProvider));
            Dependency_MembersInjector.injectMEnhancedEstimates(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.enhancedEstimatesImplProvider));
            Dependency_MembersInjector.injectMVibratorHelper(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.vibratorHelperProvider));
            Dependency_MembersInjector.injectMIStatusBarService(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideIStatusBarServiceProvider));
            Dependency_MembersInjector.injectMDisplayMetrics(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideDisplayMetricsProvider));
            Dependency_MembersInjector.injectMLockscreenGestureLogger(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.lockscreenGestureLoggerProvider));
            Dependency_MembersInjector.injectMKeyguardEnvironment(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.keyguardEnvironmentImplProvider));
            Dependency_MembersInjector.injectMShadeController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideShadeControllerProvider));
            Dependency_MembersInjector.injectMNotificationRemoteInputManagerCallback(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.statusBarRemoteInputCallbackProvider));
            Dependency_MembersInjector.injectMInitController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.initControllerProvider));
            Dependency_MembersInjector.injectMAppOpsController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.appOpsControllerImplProvider));
            Dependency_MembersInjector.injectMNavigationBarController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideNavigationBarControllerProvider));
            Dependency_MembersInjector.injectMStatusBarStateController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.statusBarStateControllerImplProvider));
            Dependency_MembersInjector.injectMNotificationLockscreenUserManager(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.notificationLockscreenUserManagerImplProvider));
            Dependency_MembersInjector.injectMNotificationGroupAlertTransferHelper(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.notificationGroupAlertTransferHelperProvider));
            Dependency_MembersInjector.injectMNotificationGroupManager(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.notificationGroupManagerProvider));
            Dependency_MembersInjector.injectMVisualStabilityManager(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.visualStabilityManagerProvider));
            Dependency_MembersInjector.injectMNotificationGutsManager(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.notificationGutsManagerProvider));
            Dependency_MembersInjector.injectMNotificationMediaManager(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.notificationMediaManagerProvider));
            Dependency_MembersInjector.injectMNotificationBlockingHelperManager(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.notificationBlockingHelperManagerProvider));
            Dependency_MembersInjector.injectMNotificationRemoteInputManager(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.notificationRemoteInputManagerProvider));
            Dependency_MembersInjector.injectMSmartReplyConstants(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.smartReplyConstantsProvider));
            Dependency_MembersInjector.injectMNotificationListener(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.notificationListenerProvider));
            Dependency_MembersInjector.injectMNotificationLogger(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.notificationLoggerProvider));
            Dependency_MembersInjector.injectMNotificationViewHierarchyManager(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.notificationViewHierarchyManagerProvider));
            Dependency_MembersInjector.injectMNotificationFilter(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.notificationFilterProvider));
            Dependency_MembersInjector.injectMNotificationInterruptionStateProvider(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.notificationInterruptionStateProvider));
            Dependency_MembersInjector.injectMKeyguardDismissUtil(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.keyguardDismissUtilProvider));
            Dependency_MembersInjector.injectMSmartReplyController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.smartReplyControllerProvider));
            Dependency_MembersInjector.injectMRemoteInputQuickSettingsDisabler(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.remoteInputQuickSettingsDisablerProvider));
            Dependency_MembersInjector.injectMBubbleController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.bubbleControllerProvider));
            Dependency_MembersInjector.injectMNotificationEntryManager(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.notificationEntryManagerProvider));
            Dependency_MembersInjector.injectMNotificationAlertingManager(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.notificationAlertingManagerProvider));
            Dependency_MembersInjector.injectMSensorPrivacyManager(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideSensorPrivacyManagerProvider));
            Dependency_MembersInjector.injectMAutoHideController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideAutoHideControllerProvider));
            Dependency_MembersInjector.injectMForegroundServiceNotificationListener(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.foregroundServiceNotificationListenerProvider));
            Dependency_MembersInjector.injectMBgLooper(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideBgLooperProvider));
            Dependency_MembersInjector.injectMBgHandler(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideBgHandlerProvider));
            Dependency_MembersInjector.injectMMainHandler(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideMainHandlerProvider));
            Dependency_MembersInjector.injectMTimeTickHandler(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideHandlerProvider));
            Dependency_MembersInjector.injectMLeakReportEmail(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideLeakReportEmailProvider));
            Dependency_MembersInjector.injectMClockManager(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.clockManagerProvider));
            Dependency_MembersInjector.injectMActivityManagerWrapper(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideActivityManagerWrapperProvider));
            Dependency_MembersInjector.injectMDevicePolicyManagerWrapper(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideDevicePolicyManagerWrapperProvider));
            Dependency_MembersInjector.injectMPackageManagerWrapper(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.providePackageManagerWrapperProvider));
            Dependency_MembersInjector.injectMSensorPrivacyController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.sensorPrivacyControllerImplProvider));
            Dependency_MembersInjector.injectMDumpController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.dumpControllerProvider));
            Dependency_MembersInjector.injectMDockManager(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.dockManagerImplProvider));
            Dependency_MembersInjector.injectMChannelEditorDialogController(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.channelEditorDialogControllerProvider));
            Dependency_MembersInjector.injectMINotificationManager(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.provideINotificationManagerProvider));
            Dependency_MembersInjector.injectMFalsingManager(instance, DoubleCheck.lazy(DaggerSystemUIRootComponent.this.falsingManagerProxyProvider));
            return instance;
        }
    }

    /* loaded from: classes21.dex */
    private final class StatusBarInjectorImpl implements StatusBar.StatusBarInjector {
        private StatusBarInjectorImpl() {
        }

        @Override // com.android.systemui.statusbar.phone.StatusBar.StatusBarInjector
        public void createStatusBar(StatusBar statusbar) {
            injectStatusBar(statusbar);
        }

        private StatusBar injectStatusBar(StatusBar instance) {
            StatusBar_MembersInjector.injectMInjectionInflater(instance, (InjectionInflationController) DaggerSystemUIRootComponent.this.injectionInflationControllerProvider.get());
            StatusBar_MembersInjector.injectMPulseExpansionHandler(instance, (PulseExpansionHandler) DaggerSystemUIRootComponent.this.pulseExpansionHandlerProvider.get());
            StatusBar_MembersInjector.injectMWakeUpCoordinator(instance, (NotificationWakeUpCoordinator) DaggerSystemUIRootComponent.this.notificationWakeUpCoordinatorProvider.get());
            StatusBar_MembersInjector.injectMKeyguardBypassController(instance, (KeyguardBypassController) DaggerSystemUIRootComponent.this.keyguardBypassControllerProvider.get());
            StatusBar_MembersInjector.injectMHeadsUpManager(instance, (HeadsUpManagerPhone) DaggerSystemUIRootComponent.this.headsUpManagerPhoneProvider.get());
            StatusBar_MembersInjector.injectMDynamicPrivacyController(instance, (DynamicPrivacyController) DaggerSystemUIRootComponent.this.dynamicPrivacyControllerProvider.get());
            StatusBar_MembersInjector.injectMBypassHeadsUpNotifier(instance, (BypassHeadsUpNotifier) DaggerSystemUIRootComponent.this.bypassHeadsUpNotifierProvider.get());
            StatusBar_MembersInjector.injectMKeyguardLiftController(instance, (KeyguardLiftController) DaggerSystemUIRootComponent.this.provideKeyguardLiftControllerProvider.get());
            StatusBar_MembersInjector.injectMAllowNotificationLongPress(instance, ((Boolean) DaggerSystemUIRootComponent.this.provideAllowNotificationLongPressProvider.get()).booleanValue());
            return instance;
        }
    }

    /* loaded from: classes21.dex */
    private final class FragmentCreatorImpl implements FragmentService.FragmentCreator {
        private FragmentCreatorImpl() {
        }

        @Override // com.android.systemui.fragments.FragmentService.FragmentCreator
        public NavigationBarFragment createNavigationBarFragment() {
            return new NavigationBarFragment((AccessibilityManagerWrapper) DaggerSystemUIRootComponent.this.accessibilityManagerWrapperProvider.get(), (DeviceProvisionedController) DaggerSystemUIRootComponent.this.deviceProvisionedControllerImplProvider.get(), (MetricsLogger) DaggerSystemUIRootComponent.this.provideMetricsLoggerProvider.get(), (AssistManager) DaggerSystemUIRootComponent.this.assistManagerProvider.get(), (OverviewProxyService) DaggerSystemUIRootComponent.this.overviewProxyServiceProvider.get(), (NavigationModeController) DaggerSystemUIRootComponent.this.navigationModeControllerProvider.get(), (StatusBarStateController) DaggerSystemUIRootComponent.this.statusBarStateControllerImplProvider.get());
        }

        @Override // com.android.systemui.fragments.FragmentService.FragmentCreator
        public QSFragment createQSFragment() {
            return new QSFragment((RemoteInputQuickSettingsDisabler) DaggerSystemUIRootComponent.this.remoteInputQuickSettingsDisablerProvider.get(), (InjectionInflationController) DaggerSystemUIRootComponent.this.injectionInflationControllerProvider.get(), SystemUIFactory_ContextHolder_ProvideContextFactory.proxyProvideContext(DaggerSystemUIRootComponent.this.contextHolder), (QSTileHost) DaggerSystemUIRootComponent.this.qSTileHostProvider.get(), (StatusBarStateController) DaggerSystemUIRootComponent.this.statusBarStateControllerImplProvider.get());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public final class ViewCreatorImpl implements InjectionInflationController.ViewCreator {
        private ViewCreatorImpl() {
        }

        @Override // com.android.systemui.util.InjectionInflationController.ViewCreator
        public InjectionInflationController.ViewInstanceCreator createInstanceCreator(InjectionInflationController.ViewAttributeProvider attributeProvider) {
            return new ViewInstanceCreatorImpl(attributeProvider);
        }

        /* loaded from: classes21.dex */
        private final class ViewInstanceCreatorImpl implements InjectionInflationController.ViewInstanceCreator {
            private InjectionInflationController.ViewAttributeProvider viewAttributeProvider;

            private ViewInstanceCreatorImpl(InjectionInflationController.ViewAttributeProvider attributeProvider) {
                initialize(attributeProvider);
            }

            private void initialize(InjectionInflationController.ViewAttributeProvider attributeProvider) {
                this.viewAttributeProvider = (InjectionInflationController.ViewAttributeProvider) Preconditions.checkNotNull(attributeProvider);
            }

            @Override // com.android.systemui.util.InjectionInflationController.ViewInstanceCreator
            public QuickStatusBarHeader createQsHeader() {
                return new QuickStatusBarHeader(InjectionInflationController_ViewAttributeProvider_ProvideContextFactory.proxyProvideContext(this.viewAttributeProvider), InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory.proxyProvideAttributeSet(this.viewAttributeProvider), (NextAlarmController) DaggerSystemUIRootComponent.this.nextAlarmControllerImplProvider.get(), (ZenModeController) DaggerSystemUIRootComponent.this.zenModeControllerImplProvider.get(), (StatusBarIconController) DaggerSystemUIRootComponent.this.statusBarIconControllerImplProvider.get(), (ActivityStarter) DaggerSystemUIRootComponent.this.activityStarterDelegateProvider.get());
            }

            @Override // com.android.systemui.util.InjectionInflationController.ViewInstanceCreator
            public QSFooterImpl createQsFooter() {
                return new QSFooterImpl(InjectionInflationController_ViewAttributeProvider_ProvideContextFactory.proxyProvideContext(this.viewAttributeProvider), InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory.proxyProvideAttributeSet(this.viewAttributeProvider), (ActivityStarter) DaggerSystemUIRootComponent.this.activityStarterDelegateProvider.get(), (UserInfoController) DaggerSystemUIRootComponent.this.userInfoControllerImplProvider.get(), (DeviceProvisionedController) DaggerSystemUIRootComponent.this.deviceProvisionedControllerImplProvider.get());
            }

            @Override // com.android.systemui.util.InjectionInflationController.ViewInstanceCreator
            public NotificationStackScrollLayout createNotificationStackScrollLayout() {
                return new NotificationStackScrollLayout(InjectionInflationController_ViewAttributeProvider_ProvideContextFactory.proxyProvideContext(this.viewAttributeProvider), InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory.proxyProvideAttributeSet(this.viewAttributeProvider), ((Boolean) DaggerSystemUIRootComponent.this.provideAllowNotificationLongPressProvider.get()).booleanValue(), (NotificationRoundnessManager) DaggerSystemUIRootComponent.this.notificationRoundnessManagerProvider.get(), (DynamicPrivacyController) DaggerSystemUIRootComponent.this.dynamicPrivacyControllerProvider.get(), (ConfigurationController) DaggerSystemUIRootComponent.this.provideConfigurationControllerProvider.get(), (ActivityStarter) DaggerSystemUIRootComponent.this.activityStarterDelegateProvider.get(), (StatusBarStateController) DaggerSystemUIRootComponent.this.statusBarStateControllerImplProvider.get(), (HeadsUpManagerPhone) DaggerSystemUIRootComponent.this.headsUpManagerPhoneProvider.get(), (KeyguardBypassController) DaggerSystemUIRootComponent.this.keyguardBypassControllerProvider.get(), (FalsingManager) DaggerSystemUIRootComponent.this.falsingManagerProxyProvider.get());
            }

            @Override // com.android.systemui.util.InjectionInflationController.ViewInstanceCreator
            public NotificationPanelView createPanelView() {
                return new NotificationPanelView(InjectionInflationController_ViewAttributeProvider_ProvideContextFactory.proxyProvideContext(this.viewAttributeProvider), InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory.proxyProvideAttributeSet(this.viewAttributeProvider), (InjectionInflationController) DaggerSystemUIRootComponent.this.injectionInflationControllerProvider.get(), (NotificationWakeUpCoordinator) DaggerSystemUIRootComponent.this.notificationWakeUpCoordinatorProvider.get(), (PulseExpansionHandler) DaggerSystemUIRootComponent.this.pulseExpansionHandlerProvider.get(), (DynamicPrivacyController) DaggerSystemUIRootComponent.this.dynamicPrivacyControllerProvider.get(), (KeyguardBypassController) DaggerSystemUIRootComponent.this.keyguardBypassControllerProvider.get(), (FalsingManager) DaggerSystemUIRootComponent.this.falsingManagerProxyProvider.get());
            }

            @Override // com.android.systemui.util.InjectionInflationController.ViewInstanceCreator
            public QSCarrierGroup createQSCarrierGroup() {
                return new QSCarrierGroup(InjectionInflationController_ViewAttributeProvider_ProvideContextFactory.proxyProvideContext(this.viewAttributeProvider), InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory.proxyProvideAttributeSet(this.viewAttributeProvider), (NetworkController) DaggerSystemUIRootComponent.this.networkControllerImplProvider.get(), (ActivityStarter) DaggerSystemUIRootComponent.this.activityStarterDelegateProvider.get());
            }

            @Override // com.android.systemui.util.InjectionInflationController.ViewInstanceCreator
            public NotificationShelf creatNotificationShelf() {
                return new NotificationShelf(InjectionInflationController_ViewAttributeProvider_ProvideContextFactory.proxyProvideContext(this.viewAttributeProvider), InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory.proxyProvideAttributeSet(this.viewAttributeProvider), (KeyguardBypassController) DaggerSystemUIRootComponent.this.keyguardBypassControllerProvider.get());
            }

            @Override // com.android.systemui.util.InjectionInflationController.ViewInstanceCreator
            public KeyguardClockSwitch createKeyguardClockSwitch() {
                return new KeyguardClockSwitch(InjectionInflationController_ViewAttributeProvider_ProvideContextFactory.proxyProvideContext(this.viewAttributeProvider), InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory.proxyProvideAttributeSet(this.viewAttributeProvider), (StatusBarStateController) DaggerSystemUIRootComponent.this.statusBarStateControllerImplProvider.get(), (SysuiColorExtractor) DaggerSystemUIRootComponent.this.sysuiColorExtractorProvider.get(), (ClockManager) DaggerSystemUIRootComponent.this.clockManagerProvider.get());
            }

            @Override // com.android.systemui.util.InjectionInflationController.ViewInstanceCreator
            public KeyguardSliceView createKeyguardSliceView() {
                return new KeyguardSliceView(InjectionInflationController_ViewAttributeProvider_ProvideContextFactory.proxyProvideContext(this.viewAttributeProvider), InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory.proxyProvideAttributeSet(this.viewAttributeProvider), (ActivityStarter) DaggerSystemUIRootComponent.this.activityStarterDelegateProvider.get(), (ConfigurationController) DaggerSystemUIRootComponent.this.provideConfigurationControllerProvider.get());
            }

            @Override // com.android.systemui.util.InjectionInflationController.ViewInstanceCreator
            public KeyguardMessageArea createKeyguardMessageArea() {
                return new KeyguardMessageArea(InjectionInflationController_ViewAttributeProvider_ProvideContextFactory.proxyProvideContext(this.viewAttributeProvider), InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory.proxyProvideAttributeSet(this.viewAttributeProvider), (ConfigurationController) DaggerSystemUIRootComponent.this.provideConfigurationControllerProvider.get());
            }

            @Override // com.android.systemui.util.InjectionInflationController.ViewInstanceCreator
            public LockIcon createLockIcon() {
                return new LockIcon(InjectionInflationController_ViewAttributeProvider_ProvideContextFactory.proxyProvideContext(this.viewAttributeProvider), InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory.proxyProvideAttributeSet(this.viewAttributeProvider), (StatusBarStateController) DaggerSystemUIRootComponent.this.statusBarStateControllerImplProvider.get(), (ConfigurationController) DaggerSystemUIRootComponent.this.provideConfigurationControllerProvider.get(), (AccessibilityController) DaggerSystemUIRootComponent.this.accessibilityControllerProvider.get(), (KeyguardBypassController) DaggerSystemUIRootComponent.this.keyguardBypassControllerProvider.get(), (NotificationWakeUpCoordinator) DaggerSystemUIRootComponent.this.notificationWakeUpCoordinatorProvider.get(), (KeyguardMonitor) DaggerSystemUIRootComponent.this.keyguardMonitorImplProvider.get(), (DockManager) DaggerSystemUIRootComponent.this.dockManagerImplProvider.get(), (HeadsUpManagerPhone) DaggerSystemUIRootComponent.this.headsUpManagerPhoneProvider.get());
            }

            @Override // com.android.systemui.util.InjectionInflationController.ViewInstanceCreator
            public QSPanel createQSPanel() {
                return new QSPanel(InjectionInflationController_ViewAttributeProvider_ProvideContextFactory.proxyProvideContext(this.viewAttributeProvider), InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory.proxyProvideAttributeSet(this.viewAttributeProvider), (DumpController) DaggerSystemUIRootComponent.this.dumpControllerProvider.get());
            }

            @Override // com.android.systemui.util.InjectionInflationController.ViewInstanceCreator
            public QuickQSPanel createQuickQSPanel() {
                return new QuickQSPanel(InjectionInflationController_ViewAttributeProvider_ProvideContextFactory.proxyProvideContext(this.viewAttributeProvider), InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory.proxyProvideAttributeSet(this.viewAttributeProvider), (DumpController) DaggerSystemUIRootComponent.this.dumpControllerProvider.get());
            }

            @Override // com.android.systemui.util.InjectionInflationController.ViewInstanceCreator
            public QSCustomizer createQSCustomizer() {
                return new QSCustomizer(SystemUIFactory_ContextHolder_ProvideContextFactory.proxyProvideContext(DaggerSystemUIRootComponent.this.contextHolder), InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory.proxyProvideAttributeSet(this.viewAttributeProvider), (LightBarController) DaggerSystemUIRootComponent.this.lightBarControllerProvider.get(), (KeyguardMonitor) DaggerSystemUIRootComponent.this.keyguardMonitorImplProvider.get(), (ScreenLifecycle) DaggerSystemUIRootComponent.this.screenLifecycleProvider.get());
            }
        }
    }
}
