package com.android.systemui.statusbar.phone;

import android.content.Context;
import com.android.systemui.UiOffloadThread;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class NavigationModeController_Factory implements Factory<NavigationModeController> {
    private final Provider<Context> contextProvider;
    private final Provider<DeviceProvisionedController> deviceProvisionedControllerProvider;
    private final Provider<UiOffloadThread> uiOffloadThreadProvider;

    public NavigationModeController_Factory(Provider<Context> contextProvider, Provider<DeviceProvisionedController> deviceProvisionedControllerProvider, Provider<UiOffloadThread> uiOffloadThreadProvider) {
        this.contextProvider = contextProvider;
        this.deviceProvisionedControllerProvider = deviceProvisionedControllerProvider;
        this.uiOffloadThreadProvider = uiOffloadThreadProvider;
    }

    @Override // javax.inject.Provider
    public NavigationModeController get() {
        return provideInstance(this.contextProvider, this.deviceProvisionedControllerProvider, this.uiOffloadThreadProvider);
    }

    public static NavigationModeController provideInstance(Provider<Context> contextProvider, Provider<DeviceProvisionedController> deviceProvisionedControllerProvider, Provider<UiOffloadThread> uiOffloadThreadProvider) {
        return new NavigationModeController(contextProvider.get(), deviceProvisionedControllerProvider.get(), uiOffloadThreadProvider.get());
    }

    public static NavigationModeController_Factory create(Provider<Context> contextProvider, Provider<DeviceProvisionedController> deviceProvisionedControllerProvider, Provider<UiOffloadThread> uiOffloadThreadProvider) {
        return new NavigationModeController_Factory(contextProvider, deviceProvisionedControllerProvider, uiOffloadThreadProvider);
    }

    public static NavigationModeController newNavigationModeController(Context context, DeviceProvisionedController deviceProvisionedController, UiOffloadThread uiOffloadThread) {
        return new NavigationModeController(context, deviceProvisionedController, uiOffloadThread);
    }
}
