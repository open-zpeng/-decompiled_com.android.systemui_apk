package com.android.systemui.assist;

import android.os.Handler;
import androidx.slice.Clock;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.PackageManagerWrapper;
import dagger.Lazy;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class AssistHandleReminderExpBehavior_Factory implements Factory<AssistHandleReminderExpBehavior> {
    private final Provider<ActivityManagerWrapper> activityManagerWrapperProvider;
    private final Provider<Clock> clockProvider;
    private final Provider<DeviceConfigHelper> deviceConfigHelperProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<OverviewProxyService> overviewProxyServiceProvider;
    private final Provider<PackageManagerWrapper> packageManagerWrapperProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider;

    public AssistHandleReminderExpBehavior_Factory(Provider<Clock> clockProvider, Provider<Handler> handlerProvider, Provider<DeviceConfigHelper> deviceConfigHelperProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<ActivityManagerWrapper> activityManagerWrapperProvider, Provider<OverviewProxyService> overviewProxyServiceProvider, Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider, Provider<PackageManagerWrapper> packageManagerWrapperProvider) {
        this.clockProvider = clockProvider;
        this.handlerProvider = handlerProvider;
        this.deviceConfigHelperProvider = deviceConfigHelperProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.activityManagerWrapperProvider = activityManagerWrapperProvider;
        this.overviewProxyServiceProvider = overviewProxyServiceProvider;
        this.wakefulnessLifecycleProvider = wakefulnessLifecycleProvider;
        this.packageManagerWrapperProvider = packageManagerWrapperProvider;
    }

    @Override // javax.inject.Provider
    public AssistHandleReminderExpBehavior get() {
        return provideInstance(this.clockProvider, this.handlerProvider, this.deviceConfigHelperProvider, this.statusBarStateControllerProvider, this.activityManagerWrapperProvider, this.overviewProxyServiceProvider, this.wakefulnessLifecycleProvider, this.packageManagerWrapperProvider);
    }

    public static AssistHandleReminderExpBehavior provideInstance(Provider<Clock> clockProvider, Provider<Handler> handlerProvider, Provider<DeviceConfigHelper> deviceConfigHelperProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<ActivityManagerWrapper> activityManagerWrapperProvider, Provider<OverviewProxyService> overviewProxyServiceProvider, Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider, Provider<PackageManagerWrapper> packageManagerWrapperProvider) {
        return new AssistHandleReminderExpBehavior(clockProvider.get(), handlerProvider.get(), deviceConfigHelperProvider.get(), DoubleCheck.lazy(statusBarStateControllerProvider), DoubleCheck.lazy(activityManagerWrapperProvider), DoubleCheck.lazy(overviewProxyServiceProvider), DoubleCheck.lazy(wakefulnessLifecycleProvider), DoubleCheck.lazy(packageManagerWrapperProvider));
    }

    public static AssistHandleReminderExpBehavior_Factory create(Provider<Clock> clockProvider, Provider<Handler> handlerProvider, Provider<DeviceConfigHelper> deviceConfigHelperProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<ActivityManagerWrapper> activityManagerWrapperProvider, Provider<OverviewProxyService> overviewProxyServiceProvider, Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider, Provider<PackageManagerWrapper> packageManagerWrapperProvider) {
        return new AssistHandleReminderExpBehavior_Factory(clockProvider, handlerProvider, deviceConfigHelperProvider, statusBarStateControllerProvider, activityManagerWrapperProvider, overviewProxyServiceProvider, wakefulnessLifecycleProvider, packageManagerWrapperProvider);
    }

    public static AssistHandleReminderExpBehavior newAssistHandleReminderExpBehavior(Clock clock, Handler handler, DeviceConfigHelper deviceConfigHelper, Lazy<StatusBarStateController> statusBarStateController, Lazy<ActivityManagerWrapper> activityManagerWrapper, Lazy<OverviewProxyService> overviewProxyService, Lazy<WakefulnessLifecycle> wakefulnessLifecycle, Lazy<PackageManagerWrapper> packageManagerWrapper) {
        return new AssistHandleReminderExpBehavior(clock, handler, deviceConfigHelper, statusBarStateController, activityManagerWrapper, overviewProxyService, wakefulnessLifecycle, packageManagerWrapper);
    }
}
