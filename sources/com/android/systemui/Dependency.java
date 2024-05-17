package com.android.systemui;

import android.app.INotificationManager;
import android.hardware.SensorPrivacyManager;
import android.hardware.display.NightDisplayListener;
import android.os.Handler;
import android.os.Looper;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.view.IWindowManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.util.Preconditions;
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
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
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
import dagger.Subcomponent;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.inject.Inject;
import javax.inject.Named;
/* loaded from: classes21.dex */
public class Dependency {
    public static final String ALLOW_NOTIFICATION_LONG_PRESS_NAME = "allow_notif_longpress";
    private static Dependency sDependency;
    @Inject
    Lazy<AccessibilityController> mAccessibilityController;
    @Inject
    Lazy<AccessibilityManagerWrapper> mAccessibilityManagerWrapper;
    @Inject
    Lazy<ActivityManagerWrapper> mActivityManagerWrapper;
    @Inject
    Lazy<ActivityStarter> mActivityStarter;
    @Inject
    Lazy<ActivityStarterDelegate> mActivityStarterDelegate;
    @Inject
    Lazy<AppOpsController> mAppOpsController;
    @Inject
    Lazy<AssistManager> mAssistManager;
    @Inject
    Lazy<AsyncSensorManager> mAsyncSensorManager;
    @Inject
    Lazy<AutoHideController> mAutoHideController;
    @Inject
    Lazy<BatteryController> mBatteryController;
    @Inject
    @Named(BG_HANDLER_NAME)
    Lazy<Handler> mBgHandler;
    @Inject
    @Named(BG_LOOPER_NAME)
    Lazy<Looper> mBgLooper;
    @Inject
    Lazy<BluetoothController> mBluetoothController;
    @Inject
    Lazy<BubbleController> mBubbleController;
    @Inject
    Lazy<CastController> mCastController;
    @Inject
    Lazy<ChannelEditorDialogController> mChannelEditorDialogController;
    @Inject
    Lazy<ClockManager> mClockManager;
    @Inject
    Lazy<ConfigurationController> mConfigurationController;
    @Inject
    Lazy<DarkIconDispatcher> mDarkIconDispatcher;
    @Inject
    Lazy<DataSaverController> mDataSaverController;
    @Inject
    Lazy<DevicePolicyManagerWrapper> mDevicePolicyManagerWrapper;
    @Inject
    Lazy<DeviceProvisionedController> mDeviceProvisionedController;
    @Inject
    Lazy<DisplayMetrics> mDisplayMetrics;
    @Inject
    Lazy<DockManager> mDockManager;
    @Inject
    Lazy<DumpController> mDumpController;
    @Inject
    Lazy<EnhancedEstimates> mEnhancedEstimates;
    @Inject
    Lazy<ExtensionController> mExtensionController;
    @Inject
    Lazy<FalsingManager> mFalsingManager;
    @Inject
    Lazy<FlashlightController> mFlashlightController;
    @Inject
    Lazy<ForegroundServiceController> mForegroundServiceController;
    @Inject
    Lazy<ForegroundServiceNotificationListener> mForegroundServiceNotificationListener;
    @Inject
    Lazy<FragmentService> mFragmentService;
    @Inject
    Lazy<GarbageMonitor> mGarbageMonitor;
    @Inject
    Lazy<HotspotController> mHotspotController;
    @Inject
    Lazy<INotificationManager> mINotificationManager;
    @Inject
    Lazy<IStatusBarService> mIStatusBarService;
    @Inject
    Lazy<IWindowManager> mIWindowManager;
    @Inject
    Lazy<InitController> mInitController;
    @Inject
    Lazy<KeyguardDismissUtil> mKeyguardDismissUtil;
    @Inject
    Lazy<NotificationData.KeyguardEnvironment> mKeyguardEnvironment;
    @Inject
    Lazy<KeyguardMonitor> mKeyguardMonitor;
    @Inject
    Lazy<LeakDetector> mLeakDetector;
    @Inject
    @Named(LEAK_REPORT_EMAIL_NAME)
    Lazy<String> mLeakReportEmail;
    @Inject
    Lazy<LeakReporter> mLeakReporter;
    @Inject
    Lazy<LightBarController> mLightBarController;
    @Inject
    Lazy<LocalBluetoothManager> mLocalBluetoothManager;
    @Inject
    Lazy<LocationController> mLocationController;
    @Inject
    Lazy<LockscreenGestureLogger> mLockscreenGestureLogger;
    @Inject
    @Named(MAIN_HANDLER_NAME)
    Lazy<Handler> mMainHandler;
    @Inject
    Lazy<ManagedProfileController> mManagedProfileController;
    @Inject
    Lazy<MetricsLogger> mMetricsLogger;
    @Inject
    Lazy<NavigationModeController> mNavBarModeController;
    @Inject
    Lazy<NavigationBarController> mNavigationBarController;
    @Inject
    Lazy<NetworkController> mNetworkController;
    @Inject
    Lazy<NextAlarmController> mNextAlarmController;
    @Inject
    Lazy<NightDisplayListener> mNightDisplayListener;
    @Inject
    Lazy<NotificationAlertingManager> mNotificationAlertingManager;
    @Inject
    Lazy<NotificationBlockingHelperManager> mNotificationBlockingHelperManager;
    @Inject
    Lazy<NotificationEntryManager> mNotificationEntryManager;
    @Inject
    Lazy<NotificationFilter> mNotificationFilter;
    @Inject
    Lazy<NotificationGroupAlertTransferHelper> mNotificationGroupAlertTransferHelper;
    @Inject
    Lazy<NotificationGroupManager> mNotificationGroupManager;
    @Inject
    Lazy<NotificationGutsManager> mNotificationGutsManager;
    @Inject
    Lazy<NotificationInterruptionStateProvider> mNotificationInterruptionStateProvider;
    @Inject
    Lazy<NotificationListener> mNotificationListener;
    @Inject
    Lazy<NotificationLockscreenUserManager> mNotificationLockscreenUserManager;
    @Inject
    Lazy<NotificationLogger> mNotificationLogger;
    @Inject
    Lazy<NotificationMediaManager> mNotificationMediaManager;
    @Inject
    Lazy<NotificationRemoteInputManager> mNotificationRemoteInputManager;
    @Inject
    Lazy<NotificationRemoteInputManager.Callback> mNotificationRemoteInputManagerCallback;
    @Inject
    Lazy<NotificationViewHierarchyManager> mNotificationViewHierarchyManager;
    @Inject
    Lazy<OverviewProxyService> mOverviewProxyService;
    @Inject
    Lazy<PackageManagerWrapper> mPackageManagerWrapper;
    @Inject
    Lazy<PluginDependencyProvider> mPluginDependencyProvider;
    @Inject
    Lazy<PluginManager> mPluginManager;
    @Inject
    Lazy<RemoteInputQuickSettingsDisabler> mRemoteInputQuickSettingsDisabler;
    @Inject
    Lazy<RotationLockController> mRotationLockController;
    @Inject
    Lazy<ScreenLifecycle> mScreenLifecycle;
    @Inject
    Lazy<SecurityController> mSecurityController;
    @Inject
    Lazy<SensorPrivacyController> mSensorPrivacyController;
    @Inject
    Lazy<SensorPrivacyManager> mSensorPrivacyManager;
    @Inject
    Lazy<ShadeController> mShadeController;
    @Inject
    Lazy<SmartReplyConstants> mSmartReplyConstants;
    @Inject
    Lazy<SmartReplyController> mSmartReplyController;
    @Inject
    Lazy<StatusBarIconController> mStatusBarIconController;
    @Inject
    Lazy<StatusBarStateController> mStatusBarStateController;
    @Inject
    Lazy<StatusBarWindowController> mStatusBarWindowController;
    @Inject
    Lazy<SysuiColorExtractor> mSysuiColorExtractor;
    @Inject
    @Named(TIME_TICK_HANDLER_NAME)
    Lazy<Handler> mTimeTickHandler;
    @Inject
    Lazy<TunablePadding.TunablePaddingService> mTunablePaddingService;
    @Inject
    Lazy<TunerService> mTunerService;
    @Inject
    Lazy<UiOffloadThread> mUiOffloadThread;
    @Inject
    Lazy<UserInfoController> mUserInfoController;
    @Inject
    Lazy<UserSwitcherController> mUserSwitcherController;
    @Inject
    Lazy<VibratorHelper> mVibratorHelper;
    @Inject
    Lazy<VisualStabilityManager> mVisualStabilityManager;
    @Inject
    Lazy<VolumeDialogController> mVolumeDialogController;
    @Inject
    Lazy<WakefulnessLifecycle> mWakefulnessLifecycle;
    @Inject
    Lazy<PowerUI.WarningsUI> mWarningsUI;
    @Inject
    Lazy<ZenModeController> mZenModeController;
    public static final String BG_LOOPER_NAME = "background_looper";
    public static final DependencyKey<Looper> BG_LOOPER = new DependencyKey<>(BG_LOOPER_NAME);
    public static final String BG_HANDLER_NAME = "background_handler";
    public static final DependencyKey<Handler> BG_HANDLER = new DependencyKey<>(BG_HANDLER_NAME);
    public static final String TIME_TICK_HANDLER_NAME = "time_tick_handler";
    public static final DependencyKey<Handler> TIME_TICK_HANDLER = new DependencyKey<>(TIME_TICK_HANDLER_NAME);
    public static final String MAIN_HANDLER_NAME = "main_handler";
    public static final DependencyKey<Handler> MAIN_HANDLER = new DependencyKey<>(MAIN_HANDLER_NAME);
    public static final String LEAK_REPORT_EMAIL_NAME = "leak_report_email";
    public static final DependencyKey<String> LEAK_REPORT_EMAIL = new DependencyKey<>(LEAK_REPORT_EMAIL_NAME);
    private final ArrayMap<Object, Object> mDependencies = new ArrayMap<>();
    private final ArrayMap<Object, LazyDependencyCreator> mProviders = new ArrayMap<>();

    @Subcomponent
    /* loaded from: classes21.dex */
    public interface DependencyInjector {
        void createSystemUI(Dependency dependency);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public interface LazyDependencyCreator<T> {
        T createDependency();
    }

    public static void initDependencies(SystemUIRootComponent rootComponent) {
        if (sDependency != null) {
            return;
        }
        sDependency = new Dependency();
        rootComponent.createDependency().createSystemUI(sDependency);
        sDependency.start();
    }

    protected void start() {
        ArrayMap<Object, LazyDependencyCreator> arrayMap = this.mProviders;
        DependencyKey<Handler> dependencyKey = TIME_TICK_HANDLER;
        final Lazy<Handler> lazy = this.mTimeTickHandler;
        Objects.requireNonNull(lazy);
        arrayMap.put(dependencyKey, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap2 = this.mProviders;
        DependencyKey<Looper> dependencyKey2 = BG_LOOPER;
        final Lazy<Looper> lazy2 = this.mBgLooper;
        Objects.requireNonNull(lazy2);
        arrayMap2.put(dependencyKey2, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap3 = this.mProviders;
        DependencyKey<Handler> dependencyKey3 = BG_HANDLER;
        final Lazy<Handler> lazy3 = this.mBgHandler;
        Objects.requireNonNull(lazy3);
        arrayMap3.put(dependencyKey3, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap4 = this.mProviders;
        DependencyKey<Handler> dependencyKey4 = MAIN_HANDLER;
        final Lazy<Handler> lazy4 = this.mMainHandler;
        Objects.requireNonNull(lazy4);
        arrayMap4.put(dependencyKey4, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap5 = this.mProviders;
        final Lazy<ActivityStarter> lazy5 = this.mActivityStarter;
        Objects.requireNonNull(lazy5);
        arrayMap5.put(ActivityStarter.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap6 = this.mProviders;
        final Lazy<ActivityStarterDelegate> lazy6 = this.mActivityStarterDelegate;
        Objects.requireNonNull(lazy6);
        arrayMap6.put(ActivityStarterDelegate.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap7 = this.mProviders;
        final Lazy<AsyncSensorManager> lazy7 = this.mAsyncSensorManager;
        Objects.requireNonNull(lazy7);
        arrayMap7.put(AsyncSensorManager.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap8 = this.mProviders;
        final Lazy<BluetoothController> lazy8 = this.mBluetoothController;
        Objects.requireNonNull(lazy8);
        arrayMap8.put(BluetoothController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap9 = this.mProviders;
        final Lazy<SensorPrivacyManager> lazy9 = this.mSensorPrivacyManager;
        Objects.requireNonNull(lazy9);
        arrayMap9.put(SensorPrivacyManager.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap10 = this.mProviders;
        final Lazy<LocationController> lazy10 = this.mLocationController;
        Objects.requireNonNull(lazy10);
        arrayMap10.put(LocationController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap11 = this.mProviders;
        final Lazy<RotationLockController> lazy11 = this.mRotationLockController;
        Objects.requireNonNull(lazy11);
        arrayMap11.put(RotationLockController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap12 = this.mProviders;
        final Lazy<NetworkController> lazy12 = this.mNetworkController;
        Objects.requireNonNull(lazy12);
        arrayMap12.put(NetworkController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap13 = this.mProviders;
        final Lazy<ZenModeController> lazy13 = this.mZenModeController;
        Objects.requireNonNull(lazy13);
        arrayMap13.put(ZenModeController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap14 = this.mProviders;
        final Lazy<HotspotController> lazy14 = this.mHotspotController;
        Objects.requireNonNull(lazy14);
        arrayMap14.put(HotspotController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap15 = this.mProviders;
        final Lazy<CastController> lazy15 = this.mCastController;
        Objects.requireNonNull(lazy15);
        arrayMap15.put(CastController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap16 = this.mProviders;
        final Lazy<FlashlightController> lazy16 = this.mFlashlightController;
        Objects.requireNonNull(lazy16);
        arrayMap16.put(FlashlightController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap17 = this.mProviders;
        final Lazy<KeyguardMonitor> lazy17 = this.mKeyguardMonitor;
        Objects.requireNonNull(lazy17);
        arrayMap17.put(KeyguardMonitor.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap18 = this.mProviders;
        final Lazy<UserSwitcherController> lazy18 = this.mUserSwitcherController;
        Objects.requireNonNull(lazy18);
        arrayMap18.put(UserSwitcherController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap19 = this.mProviders;
        final Lazy<UserInfoController> lazy19 = this.mUserInfoController;
        Objects.requireNonNull(lazy19);
        arrayMap19.put(UserInfoController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap20 = this.mProviders;
        final Lazy<BatteryController> lazy20 = this.mBatteryController;
        Objects.requireNonNull(lazy20);
        arrayMap20.put(BatteryController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap21 = this.mProviders;
        final Lazy<NightDisplayListener> lazy21 = this.mNightDisplayListener;
        Objects.requireNonNull(lazy21);
        arrayMap21.put(NightDisplayListener.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap22 = this.mProviders;
        final Lazy<ManagedProfileController> lazy22 = this.mManagedProfileController;
        Objects.requireNonNull(lazy22);
        arrayMap22.put(ManagedProfileController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap23 = this.mProviders;
        final Lazy<NextAlarmController> lazy23 = this.mNextAlarmController;
        Objects.requireNonNull(lazy23);
        arrayMap23.put(NextAlarmController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap24 = this.mProviders;
        final Lazy<DataSaverController> lazy24 = this.mDataSaverController;
        Objects.requireNonNull(lazy24);
        arrayMap24.put(DataSaverController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap25 = this.mProviders;
        final Lazy<AccessibilityController> lazy25 = this.mAccessibilityController;
        Objects.requireNonNull(lazy25);
        arrayMap25.put(AccessibilityController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap26 = this.mProviders;
        final Lazy<DeviceProvisionedController> lazy26 = this.mDeviceProvisionedController;
        Objects.requireNonNull(lazy26);
        arrayMap26.put(DeviceProvisionedController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap27 = this.mProviders;
        final Lazy<PluginManager> lazy27 = this.mPluginManager;
        Objects.requireNonNull(lazy27);
        arrayMap27.put(PluginManager.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap28 = this.mProviders;
        final Lazy<AssistManager> lazy28 = this.mAssistManager;
        Objects.requireNonNull(lazy28);
        arrayMap28.put(AssistManager.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap29 = this.mProviders;
        final Lazy<SecurityController> lazy29 = this.mSecurityController;
        Objects.requireNonNull(lazy29);
        arrayMap29.put(SecurityController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap30 = this.mProviders;
        final Lazy<LeakDetector> lazy30 = this.mLeakDetector;
        Objects.requireNonNull(lazy30);
        arrayMap30.put(LeakDetector.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap31 = this.mProviders;
        DependencyKey<String> dependencyKey5 = LEAK_REPORT_EMAIL;
        final Lazy<String> lazy31 = this.mLeakReportEmail;
        Objects.requireNonNull(lazy31);
        arrayMap31.put(dependencyKey5, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap32 = this.mProviders;
        final Lazy<LeakReporter> lazy32 = this.mLeakReporter;
        Objects.requireNonNull(lazy32);
        arrayMap32.put(LeakReporter.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap33 = this.mProviders;
        final Lazy<GarbageMonitor> lazy33 = this.mGarbageMonitor;
        Objects.requireNonNull(lazy33);
        arrayMap33.put(GarbageMonitor.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap34 = this.mProviders;
        final Lazy<TunerService> lazy34 = this.mTunerService;
        Objects.requireNonNull(lazy34);
        arrayMap34.put(TunerService.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap35 = this.mProviders;
        final Lazy<StatusBarWindowController> lazy35 = this.mStatusBarWindowController;
        Objects.requireNonNull(lazy35);
        arrayMap35.put(StatusBarWindowController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap36 = this.mProviders;
        final Lazy<DarkIconDispatcher> lazy36 = this.mDarkIconDispatcher;
        Objects.requireNonNull(lazy36);
        arrayMap36.put(DarkIconDispatcher.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap37 = this.mProviders;
        final Lazy<ConfigurationController> lazy37 = this.mConfigurationController;
        Objects.requireNonNull(lazy37);
        arrayMap37.put(ConfigurationController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap38 = this.mProviders;
        final Lazy<StatusBarIconController> lazy38 = this.mStatusBarIconController;
        Objects.requireNonNull(lazy38);
        arrayMap38.put(StatusBarIconController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap39 = this.mProviders;
        final Lazy<ScreenLifecycle> lazy39 = this.mScreenLifecycle;
        Objects.requireNonNull(lazy39);
        arrayMap39.put(ScreenLifecycle.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap40 = this.mProviders;
        final Lazy<WakefulnessLifecycle> lazy40 = this.mWakefulnessLifecycle;
        Objects.requireNonNull(lazy40);
        arrayMap40.put(WakefulnessLifecycle.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap41 = this.mProviders;
        final Lazy<FragmentService> lazy41 = this.mFragmentService;
        Objects.requireNonNull(lazy41);
        arrayMap41.put(FragmentService.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap42 = this.mProviders;
        final Lazy<ExtensionController> lazy42 = this.mExtensionController;
        Objects.requireNonNull(lazy42);
        arrayMap42.put(ExtensionController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap43 = this.mProviders;
        final Lazy<PluginDependencyProvider> lazy43 = this.mPluginDependencyProvider;
        Objects.requireNonNull(lazy43);
        arrayMap43.put(PluginDependencyProvider.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap44 = this.mProviders;
        final Lazy<LocalBluetoothManager> lazy44 = this.mLocalBluetoothManager;
        Objects.requireNonNull(lazy44);
        arrayMap44.put(LocalBluetoothManager.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap45 = this.mProviders;
        final Lazy<VolumeDialogController> lazy45 = this.mVolumeDialogController;
        Objects.requireNonNull(lazy45);
        arrayMap45.put(VolumeDialogController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap46 = this.mProviders;
        final Lazy<MetricsLogger> lazy46 = this.mMetricsLogger;
        Objects.requireNonNull(lazy46);
        arrayMap46.put(MetricsLogger.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap47 = this.mProviders;
        final Lazy<AccessibilityManagerWrapper> lazy47 = this.mAccessibilityManagerWrapper;
        Objects.requireNonNull(lazy47);
        arrayMap47.put(AccessibilityManagerWrapper.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap48 = this.mProviders;
        final Lazy<SysuiColorExtractor> lazy48 = this.mSysuiColorExtractor;
        Objects.requireNonNull(lazy48);
        arrayMap48.put(SysuiColorExtractor.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap49 = this.mProviders;
        final Lazy<TunablePadding.TunablePaddingService> lazy49 = this.mTunablePaddingService;
        Objects.requireNonNull(lazy49);
        arrayMap49.put(TunablePadding.TunablePaddingService.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap50 = this.mProviders;
        final Lazy<ForegroundServiceController> lazy50 = this.mForegroundServiceController;
        Objects.requireNonNull(lazy50);
        arrayMap50.put(ForegroundServiceController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap51 = this.mProviders;
        final Lazy<UiOffloadThread> lazy51 = this.mUiOffloadThread;
        Objects.requireNonNull(lazy51);
        arrayMap51.put(UiOffloadThread.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap52 = this.mProviders;
        final Lazy<PowerUI.WarningsUI> lazy52 = this.mWarningsUI;
        Objects.requireNonNull(lazy52);
        arrayMap52.put(PowerUI.WarningsUI.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap53 = this.mProviders;
        final Lazy<LightBarController> lazy53 = this.mLightBarController;
        Objects.requireNonNull(lazy53);
        arrayMap53.put(LightBarController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap54 = this.mProviders;
        final Lazy<IWindowManager> lazy54 = this.mIWindowManager;
        Objects.requireNonNull(lazy54);
        arrayMap54.put(IWindowManager.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap55 = this.mProviders;
        final Lazy<OverviewProxyService> lazy55 = this.mOverviewProxyService;
        Objects.requireNonNull(lazy55);
        arrayMap55.put(OverviewProxyService.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap56 = this.mProviders;
        final Lazy<NavigationModeController> lazy56 = this.mNavBarModeController;
        Objects.requireNonNull(lazy56);
        arrayMap56.put(NavigationModeController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap57 = this.mProviders;
        final Lazy<EnhancedEstimates> lazy57 = this.mEnhancedEstimates;
        Objects.requireNonNull(lazy57);
        arrayMap57.put(EnhancedEstimates.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap58 = this.mProviders;
        final Lazy<VibratorHelper> lazy58 = this.mVibratorHelper;
        Objects.requireNonNull(lazy58);
        arrayMap58.put(VibratorHelper.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap59 = this.mProviders;
        final Lazy<IStatusBarService> lazy59 = this.mIStatusBarService;
        Objects.requireNonNull(lazy59);
        arrayMap59.put(IStatusBarService.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap60 = this.mProviders;
        final Lazy<DisplayMetrics> lazy60 = this.mDisplayMetrics;
        Objects.requireNonNull(lazy60);
        arrayMap60.put(DisplayMetrics.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap61 = this.mProviders;
        final Lazy<LockscreenGestureLogger> lazy61 = this.mLockscreenGestureLogger;
        Objects.requireNonNull(lazy61);
        arrayMap61.put(LockscreenGestureLogger.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap62 = this.mProviders;
        final Lazy<NotificationData.KeyguardEnvironment> lazy62 = this.mKeyguardEnvironment;
        Objects.requireNonNull(lazy62);
        arrayMap62.put(NotificationData.KeyguardEnvironment.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap63 = this.mProviders;
        final Lazy<ShadeController> lazy63 = this.mShadeController;
        Objects.requireNonNull(lazy63);
        arrayMap63.put(ShadeController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap64 = this.mProviders;
        final Lazy<NotificationRemoteInputManager.Callback> lazy64 = this.mNotificationRemoteInputManagerCallback;
        Objects.requireNonNull(lazy64);
        arrayMap64.put(NotificationRemoteInputManager.Callback.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap65 = this.mProviders;
        final Lazy<InitController> lazy65 = this.mInitController;
        Objects.requireNonNull(lazy65);
        arrayMap65.put(InitController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap66 = this.mProviders;
        final Lazy<AppOpsController> lazy66 = this.mAppOpsController;
        Objects.requireNonNull(lazy66);
        arrayMap66.put(AppOpsController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap67 = this.mProviders;
        final Lazy<NavigationBarController> lazy67 = this.mNavigationBarController;
        Objects.requireNonNull(lazy67);
        arrayMap67.put(NavigationBarController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap68 = this.mProviders;
        final Lazy<StatusBarStateController> lazy68 = this.mStatusBarStateController;
        Objects.requireNonNull(lazy68);
        arrayMap68.put(StatusBarStateController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap69 = this.mProviders;
        final Lazy<NotificationLockscreenUserManager> lazy69 = this.mNotificationLockscreenUserManager;
        Objects.requireNonNull(lazy69);
        arrayMap69.put(NotificationLockscreenUserManager.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap70 = this.mProviders;
        final Lazy<VisualStabilityManager> lazy70 = this.mVisualStabilityManager;
        Objects.requireNonNull(lazy70);
        arrayMap70.put(VisualStabilityManager.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap71 = this.mProviders;
        final Lazy<NotificationGroupManager> lazy71 = this.mNotificationGroupManager;
        Objects.requireNonNull(lazy71);
        arrayMap71.put(NotificationGroupManager.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap72 = this.mProviders;
        final Lazy<NotificationGroupAlertTransferHelper> lazy72 = this.mNotificationGroupAlertTransferHelper;
        Objects.requireNonNull(lazy72);
        arrayMap72.put(NotificationGroupAlertTransferHelper.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap73 = this.mProviders;
        final Lazy<NotificationMediaManager> lazy73 = this.mNotificationMediaManager;
        Objects.requireNonNull(lazy73);
        arrayMap73.put(NotificationMediaManager.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap74 = this.mProviders;
        final Lazy<NotificationGutsManager> lazy74 = this.mNotificationGutsManager;
        Objects.requireNonNull(lazy74);
        arrayMap74.put(NotificationGutsManager.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap75 = this.mProviders;
        final Lazy<NotificationBlockingHelperManager> lazy75 = this.mNotificationBlockingHelperManager;
        Objects.requireNonNull(lazy75);
        arrayMap75.put(NotificationBlockingHelperManager.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap76 = this.mProviders;
        final Lazy<NotificationRemoteInputManager> lazy76 = this.mNotificationRemoteInputManager;
        Objects.requireNonNull(lazy76);
        arrayMap76.put(NotificationRemoteInputManager.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap77 = this.mProviders;
        final Lazy<SmartReplyConstants> lazy77 = this.mSmartReplyConstants;
        Objects.requireNonNull(lazy77);
        arrayMap77.put(SmartReplyConstants.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap78 = this.mProviders;
        final Lazy<NotificationListener> lazy78 = this.mNotificationListener;
        Objects.requireNonNull(lazy78);
        arrayMap78.put(NotificationListener.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap79 = this.mProviders;
        final Lazy<NotificationLogger> lazy79 = this.mNotificationLogger;
        Objects.requireNonNull(lazy79);
        arrayMap79.put(NotificationLogger.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap80 = this.mProviders;
        final Lazy<NotificationViewHierarchyManager> lazy80 = this.mNotificationViewHierarchyManager;
        Objects.requireNonNull(lazy80);
        arrayMap80.put(NotificationViewHierarchyManager.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap81 = this.mProviders;
        final Lazy<NotificationFilter> lazy81 = this.mNotificationFilter;
        Objects.requireNonNull(lazy81);
        arrayMap81.put(NotificationFilter.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap82 = this.mProviders;
        final Lazy<NotificationInterruptionStateProvider> lazy82 = this.mNotificationInterruptionStateProvider;
        Objects.requireNonNull(lazy82);
        arrayMap82.put(NotificationInterruptionStateProvider.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap83 = this.mProviders;
        final Lazy<KeyguardDismissUtil> lazy83 = this.mKeyguardDismissUtil;
        Objects.requireNonNull(lazy83);
        arrayMap83.put(KeyguardDismissUtil.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap84 = this.mProviders;
        final Lazy<SmartReplyController> lazy84 = this.mSmartReplyController;
        Objects.requireNonNull(lazy84);
        arrayMap84.put(SmartReplyController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap85 = this.mProviders;
        final Lazy<RemoteInputQuickSettingsDisabler> lazy85 = this.mRemoteInputQuickSettingsDisabler;
        Objects.requireNonNull(lazy85);
        arrayMap85.put(RemoteInputQuickSettingsDisabler.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap86 = this.mProviders;
        final Lazy<BubbleController> lazy86 = this.mBubbleController;
        Objects.requireNonNull(lazy86);
        arrayMap86.put(BubbleController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap87 = this.mProviders;
        final Lazy<NotificationEntryManager> lazy87 = this.mNotificationEntryManager;
        Objects.requireNonNull(lazy87);
        arrayMap87.put(NotificationEntryManager.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap88 = this.mProviders;
        final Lazy<NotificationAlertingManager> lazy88 = this.mNotificationAlertingManager;
        Objects.requireNonNull(lazy88);
        arrayMap88.put(NotificationAlertingManager.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap89 = this.mProviders;
        final Lazy<ForegroundServiceNotificationListener> lazy89 = this.mForegroundServiceNotificationListener;
        Objects.requireNonNull(lazy89);
        arrayMap89.put(ForegroundServiceNotificationListener.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap90 = this.mProviders;
        final Lazy<ClockManager> lazy90 = this.mClockManager;
        Objects.requireNonNull(lazy90);
        arrayMap90.put(ClockManager.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap91 = this.mProviders;
        final Lazy<ActivityManagerWrapper> lazy91 = this.mActivityManagerWrapper;
        Objects.requireNonNull(lazy91);
        arrayMap91.put(ActivityManagerWrapper.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap92 = this.mProviders;
        final Lazy<DevicePolicyManagerWrapper> lazy92 = this.mDevicePolicyManagerWrapper;
        Objects.requireNonNull(lazy92);
        arrayMap92.put(DevicePolicyManagerWrapper.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap93 = this.mProviders;
        final Lazy<PackageManagerWrapper> lazy93 = this.mPackageManagerWrapper;
        Objects.requireNonNull(lazy93);
        arrayMap93.put(PackageManagerWrapper.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap94 = this.mProviders;
        final Lazy<SensorPrivacyController> lazy94 = this.mSensorPrivacyController;
        Objects.requireNonNull(lazy94);
        arrayMap94.put(SensorPrivacyController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap95 = this.mProviders;
        final Lazy<DumpController> lazy95 = this.mDumpController;
        Objects.requireNonNull(lazy95);
        arrayMap95.put(DumpController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap96 = this.mProviders;
        final Lazy<DockManager> lazy96 = this.mDockManager;
        Objects.requireNonNull(lazy96);
        arrayMap96.put(DockManager.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap97 = this.mProviders;
        final Lazy<ChannelEditorDialogController> lazy97 = this.mChannelEditorDialogController;
        Objects.requireNonNull(lazy97);
        arrayMap97.put(ChannelEditorDialogController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap98 = this.mProviders;
        final Lazy<INotificationManager> lazy98 = this.mINotificationManager;
        Objects.requireNonNull(lazy98);
        arrayMap98.put(INotificationManager.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap99 = this.mProviders;
        final Lazy<FalsingManager> lazy99 = this.mFalsingManager;
        Objects.requireNonNull(lazy99);
        arrayMap99.put(FalsingManager.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap100 = this.mProviders;
        final Lazy<AutoHideController> lazy100 = this.mAutoHideController;
        Objects.requireNonNull(lazy100);
        arrayMap100.put(AutoHideController.class, new LazyDependencyCreator() { // from class: com.android.systemui.-$$Lambda$Vs-MsjQwuYhfrxzUr7AqZvcfoH4
            @Override // com.android.systemui.Dependency.LazyDependencyCreator
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        sDependency = this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void staticDump(FileDescriptor fd, PrintWriter pw, String[] args) {
        sDependency.dump(fd, pw, args);
    }

    public synchronized void dump(final FileDescriptor fd, final PrintWriter pw, final String[] args) {
        final String controller;
        getDependency(DumpController.class);
        if (args != null && args.length > 1) {
            controller = args[1].toLowerCase();
        } else {
            controller = null;
        }
        if (controller != null) {
            pw.println("Dumping controller=" + controller + NavigationBarInflaterView.KEY_IMAGE_DELIM);
        } else {
            pw.println("Dumping existing controllers:");
        }
        this.mDependencies.values().stream().filter(new Predicate() { // from class: com.android.systemui.-$$Lambda$Dependency$nA5ayadwqBW4bgzyvl5eaXT_aUY
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Dependency.lambda$dump$0(controller, obj);
            }
        }).forEach(new Consumer() { // from class: com.android.systemui.-$$Lambda$Dependency$txwQ8DNTPzffiYtSV5jsVTL0RAU
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((Dumpable) obj).dump(fd, pw, args);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$dump$0(String controller, Object obj) {
        return (obj instanceof Dumpable) && (controller == null || obj.getClass().getName().toLowerCase().endsWith(controller));
    }

    protected final <T> T getDependency(Class<T> cls) {
        return (T) getDependencyInner(cls);
    }

    protected final <T> T getDependency(DependencyKey<T> key) {
        return (T) getDependencyInner(key);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private synchronized <T> T getDependencyInner(Object key) {
        T obj;
        obj = (T) this.mDependencies.get(key);
        if (obj == null) {
            obj = createDependency(key);
            this.mDependencies.put(key, obj);
        }
        return obj;
    }

    @VisibleForTesting
    protected <T> T createDependency(Object cls) {
        Preconditions.checkArgument((cls instanceof DependencyKey) || (cls instanceof Class));
        LazyDependencyCreator<T> provider = this.mProviders.get(cls);
        if (provider == null) {
            throw new IllegalArgumentException("Unsupported dependency " + cls + ". " + this.mProviders.size() + " providers known.");
        }
        return provider.createDependency();
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <T> void destroyDependency(Class<T> cls, Consumer<T> destroy) {
        Object remove = this.mDependencies.remove(cls);
        if (remove != null && destroy != 0) {
            destroy.accept(remove);
        }
    }

    public static void clearDependencies() {
        sDependency = null;
    }

    public static <T> void destroy(Class<T> cls, Consumer<T> destroy) {
        sDependency.destroyDependency(cls, destroy);
    }

    @Deprecated
    public static <T> T get(Class<T> cls) {
        return (T) sDependency.getDependency(cls);
    }

    @Deprecated
    public static <T> T get(DependencyKey<T> cls) {
        return (T) sDependency.getDependency(cls);
    }

    /* loaded from: classes21.dex */
    public static final class DependencyKey<V> {
        private final String mDisplayName;

        public DependencyKey(String displayName) {
            this.mDisplayName = displayName;
        }

        public String toString() {
            return this.mDisplayName;
        }
    }
}
