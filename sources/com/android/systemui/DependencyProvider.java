package com.android.systemui;

import android.app.INotificationManager;
import android.content.Context;
import android.hardware.SensorPrivacyManager;
import android.hardware.display.NightDisplayListener;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.DisplayMetrics;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.statusbar.IStatusBarService;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.systemui.plugins.PluginInitializerImpl;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.shared.plugins.PluginManagerImpl;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.DevicePolicyManagerWrapper;
import com.android.systemui.shared.system.PackageManagerWrapper;
import com.android.systemui.statusbar.NavigationBarController;
import com.android.systemui.statusbar.phone.AutoHideController;
import com.android.systemui.statusbar.phone.ConfigurationControllerImpl;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.util.leak.LeakDetector;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
@Module
/* loaded from: classes21.dex */
public class DependencyProvider {
    @Provides
    @Singleton
    @Named(Dependency.TIME_TICK_HANDLER_NAME)
    public Handler provideHandler() {
        HandlerThread thread = new HandlerThread("TimeTick");
        thread.start();
        return new Handler(thread.getLooper());
    }

    @Provides
    @Singleton
    @Named(Dependency.BG_LOOPER_NAME)
    public Looper provideBgLooper() {
        HandlerThread thread = new HandlerThread("SysUiBg", 10);
        thread.start();
        return thread.getLooper();
    }

    @Provides
    @Singleton
    @Named(Dependency.BG_HANDLER_NAME)
    public Handler provideBgHandler(@Named("background_looper") Looper bgLooper) {
        return new Handler(bgLooper);
    }

    @Provides
    @Singleton
    @Named(Dependency.MAIN_HANDLER_NAME)
    public Handler provideMainHandler() {
        return new Handler(Looper.getMainLooper());
    }

    @Provides
    @Singleton
    public DataSaverController provideDataSaverController(NetworkController networkController) {
        return networkController.getDataSaverController();
    }

    @Provides
    @Singleton
    public LocalBluetoothManager provideLocalBluetoothController(Context context, @Named("background_handler") Handler bgHandler) {
        return LocalBluetoothManager.create(context, bgHandler, UserHandle.ALL);
    }

    @Provides
    @Singleton
    public MetricsLogger provideMetricsLogger() {
        return new MetricsLogger();
    }

    @Provides
    @Singleton
    public IWindowManager provideIWindowManager() {
        return WindowManagerGlobal.getWindowManagerService();
    }

    @Provides
    @Singleton
    public IStatusBarService provideIStatusBarService() {
        return IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
    }

    @Provides
    @Singleton
    public INotificationManager provideINotificationManager() {
        return INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
    }

    @Provides
    @Singleton
    public DisplayMetrics provideDisplayMetrics() {
        return new DisplayMetrics();
    }

    @Provides
    @Singleton
    public SensorPrivacyManager provideSensorPrivacyManager(Context context) {
        return (SensorPrivacyManager) context.getSystemService(SensorPrivacyManager.class);
    }

    @Provides
    @Singleton
    public LeakDetector provideLeakDetector() {
        return LeakDetector.create();
    }

    @Provides
    @Singleton
    public NightDisplayListener provideNightDisplayListener(Context context, @Named("background_handler") Handler bgHandler) {
        return new NightDisplayListener(context, bgHandler);
    }

    @Provides
    @Singleton
    public PluginManager providePluginManager(Context context) {
        return new PluginManagerImpl(context, new PluginInitializerImpl());
    }

    @Provides
    @Singleton
    public NavigationBarController provideNavigationBarController(Context context, @Named("main_handler") Handler mainHandler) {
        return new NavigationBarController(context, mainHandler);
    }

    @Provides
    @Singleton
    public ConfigurationController provideConfigurationController(Context context) {
        return new ConfigurationControllerImpl(context);
    }

    @Provides
    @Singleton
    public AutoHideController provideAutoHideController(Context context, @Named("main_handler") Handler mainHandler) {
        return new AutoHideController(context, mainHandler);
    }

    @Provides
    @Singleton
    public ActivityManagerWrapper provideActivityManagerWrapper() {
        return ActivityManagerWrapper.getInstance();
    }

    @Provides
    @Singleton
    public DevicePolicyManagerWrapper provideDevicePolicyManagerWrapper() {
        return DevicePolicyManagerWrapper.getInstance();
    }

    @Provides
    @Singleton
    public PackageManagerWrapper providePackageManagerWrapper() {
        return PackageManagerWrapper.getInstance();
    }
}
