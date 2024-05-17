package com.android.systemui.recents;

import android.content.Context;
import com.android.systemui.statusbar.NavigationBarController;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.statusbar.phone.StatusBarWindowController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class OverviewProxyService_Factory implements Factory<OverviewProxyService> {
    private final Provider<Context> contextProvider;
    private final Provider<NavigationBarController> navBarControllerProvider;
    private final Provider<NavigationModeController> navModeControllerProvider;
    private final Provider<DeviceProvisionedController> provisionControllerProvider;
    private final Provider<StatusBarWindowController> statusBarWinControllerProvider;

    public OverviewProxyService_Factory(Provider<Context> contextProvider, Provider<DeviceProvisionedController> provisionControllerProvider, Provider<NavigationBarController> navBarControllerProvider, Provider<NavigationModeController> navModeControllerProvider, Provider<StatusBarWindowController> statusBarWinControllerProvider) {
        this.contextProvider = contextProvider;
        this.provisionControllerProvider = provisionControllerProvider;
        this.navBarControllerProvider = navBarControllerProvider;
        this.navModeControllerProvider = navModeControllerProvider;
        this.statusBarWinControllerProvider = statusBarWinControllerProvider;
    }

    @Override // javax.inject.Provider
    public OverviewProxyService get() {
        return provideInstance(this.contextProvider, this.provisionControllerProvider, this.navBarControllerProvider, this.navModeControllerProvider, this.statusBarWinControllerProvider);
    }

    public static OverviewProxyService provideInstance(Provider<Context> contextProvider, Provider<DeviceProvisionedController> provisionControllerProvider, Provider<NavigationBarController> navBarControllerProvider, Provider<NavigationModeController> navModeControllerProvider, Provider<StatusBarWindowController> statusBarWinControllerProvider) {
        return new OverviewProxyService(contextProvider.get(), provisionControllerProvider.get(), navBarControllerProvider.get(), navModeControllerProvider.get(), statusBarWinControllerProvider.get());
    }

    public static OverviewProxyService_Factory create(Provider<Context> contextProvider, Provider<DeviceProvisionedController> provisionControllerProvider, Provider<NavigationBarController> navBarControllerProvider, Provider<NavigationModeController> navModeControllerProvider, Provider<StatusBarWindowController> statusBarWinControllerProvider) {
        return new OverviewProxyService_Factory(contextProvider, provisionControllerProvider, navBarControllerProvider, navModeControllerProvider, statusBarWinControllerProvider);
    }

    public static OverviewProxyService newOverviewProxyService(Context context, DeviceProvisionedController provisionController, NavigationBarController navBarController, NavigationModeController navModeController, StatusBarWindowController statusBarWinController) {
        return new OverviewProxyService(context, provisionController, navBarController, navModeController, statusBarWinController);
    }
}
