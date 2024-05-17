package com.android.systemui.statusbar.phone;

import com.android.internal.logging.MetricsLogger;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.statusbar.policy.AccessibilityManagerWrapper;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class NavigationBarFragment_Factory implements Factory<NavigationBarFragment> {
    private final Provider<AccessibilityManagerWrapper> accessibilityManagerWrapperProvider;
    private final Provider<AssistManager> assistManagerProvider;
    private final Provider<DeviceProvisionedController> deviceProvisionedControllerProvider;
    private final Provider<MetricsLogger> metricsLoggerProvider;
    private final Provider<NavigationModeController> navigationModeControllerProvider;
    private final Provider<OverviewProxyService> overviewProxyServiceProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;

    public NavigationBarFragment_Factory(Provider<AccessibilityManagerWrapper> accessibilityManagerWrapperProvider, Provider<DeviceProvisionedController> deviceProvisionedControllerProvider, Provider<MetricsLogger> metricsLoggerProvider, Provider<AssistManager> assistManagerProvider, Provider<OverviewProxyService> overviewProxyServiceProvider, Provider<NavigationModeController> navigationModeControllerProvider, Provider<StatusBarStateController> statusBarStateControllerProvider) {
        this.accessibilityManagerWrapperProvider = accessibilityManagerWrapperProvider;
        this.deviceProvisionedControllerProvider = deviceProvisionedControllerProvider;
        this.metricsLoggerProvider = metricsLoggerProvider;
        this.assistManagerProvider = assistManagerProvider;
        this.overviewProxyServiceProvider = overviewProxyServiceProvider;
        this.navigationModeControllerProvider = navigationModeControllerProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
    }

    @Override // javax.inject.Provider
    public NavigationBarFragment get() {
        return provideInstance(this.accessibilityManagerWrapperProvider, this.deviceProvisionedControllerProvider, this.metricsLoggerProvider, this.assistManagerProvider, this.overviewProxyServiceProvider, this.navigationModeControllerProvider, this.statusBarStateControllerProvider);
    }

    public static NavigationBarFragment provideInstance(Provider<AccessibilityManagerWrapper> accessibilityManagerWrapperProvider, Provider<DeviceProvisionedController> deviceProvisionedControllerProvider, Provider<MetricsLogger> metricsLoggerProvider, Provider<AssistManager> assistManagerProvider, Provider<OverviewProxyService> overviewProxyServiceProvider, Provider<NavigationModeController> navigationModeControllerProvider, Provider<StatusBarStateController> statusBarStateControllerProvider) {
        return new NavigationBarFragment(accessibilityManagerWrapperProvider.get(), deviceProvisionedControllerProvider.get(), metricsLoggerProvider.get(), assistManagerProvider.get(), overviewProxyServiceProvider.get(), navigationModeControllerProvider.get(), statusBarStateControllerProvider.get());
    }

    public static NavigationBarFragment_Factory create(Provider<AccessibilityManagerWrapper> accessibilityManagerWrapperProvider, Provider<DeviceProvisionedController> deviceProvisionedControllerProvider, Provider<MetricsLogger> metricsLoggerProvider, Provider<AssistManager> assistManagerProvider, Provider<OverviewProxyService> overviewProxyServiceProvider, Provider<NavigationModeController> navigationModeControllerProvider, Provider<StatusBarStateController> statusBarStateControllerProvider) {
        return new NavigationBarFragment_Factory(accessibilityManagerWrapperProvider, deviceProvisionedControllerProvider, metricsLoggerProvider, assistManagerProvider, overviewProxyServiceProvider, navigationModeControllerProvider, statusBarStateControllerProvider);
    }

    public static NavigationBarFragment newNavigationBarFragment(AccessibilityManagerWrapper accessibilityManagerWrapper, DeviceProvisionedController deviceProvisionedController, MetricsLogger metricsLogger, AssistManager assistManager, OverviewProxyService overviewProxyService, NavigationModeController navigationModeController, StatusBarStateController statusBarStateController) {
        return new NavigationBarFragment(accessibilityManagerWrapper, deviceProvisionedController, metricsLogger, assistManager, overviewProxyService, navigationModeController, statusBarStateController);
    }
}
