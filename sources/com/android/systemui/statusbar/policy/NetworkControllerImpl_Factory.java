package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.os.Looper;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class NetworkControllerImpl_Factory implements Factory<NetworkControllerImpl> {
    private final Provider<Looper> bgLooperProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DeviceProvisionedController> deviceProvisionedControllerProvider;

    public NetworkControllerImpl_Factory(Provider<Context> contextProvider, Provider<Looper> bgLooperProvider, Provider<DeviceProvisionedController> deviceProvisionedControllerProvider) {
        this.contextProvider = contextProvider;
        this.bgLooperProvider = bgLooperProvider;
        this.deviceProvisionedControllerProvider = deviceProvisionedControllerProvider;
    }

    @Override // javax.inject.Provider
    public NetworkControllerImpl get() {
        return provideInstance(this.contextProvider, this.bgLooperProvider, this.deviceProvisionedControllerProvider);
    }

    public static NetworkControllerImpl provideInstance(Provider<Context> contextProvider, Provider<Looper> bgLooperProvider, Provider<DeviceProvisionedController> deviceProvisionedControllerProvider) {
        return new NetworkControllerImpl(contextProvider.get(), bgLooperProvider.get(), deviceProvisionedControllerProvider.get());
    }

    public static NetworkControllerImpl_Factory create(Provider<Context> contextProvider, Provider<Looper> bgLooperProvider, Provider<DeviceProvisionedController> deviceProvisionedControllerProvider) {
        return new NetworkControllerImpl_Factory(contextProvider, bgLooperProvider, deviceProvisionedControllerProvider);
    }

    public static NetworkControllerImpl newNetworkControllerImpl(Context context, Looper bgLooper, DeviceProvisionedController deviceProvisionedController) {
        return new NetworkControllerImpl(context, bgLooper, deviceProvisionedController);
    }
}
