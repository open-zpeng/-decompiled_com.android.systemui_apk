package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.os.Handler;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class DeviceProvisionedControllerImpl_Factory implements Factory<DeviceProvisionedControllerImpl> {
    private final Provider<Context> contextProvider;
    private final Provider<Handler> mainHandlerProvider;

    public DeviceProvisionedControllerImpl_Factory(Provider<Context> contextProvider, Provider<Handler> mainHandlerProvider) {
        this.contextProvider = contextProvider;
        this.mainHandlerProvider = mainHandlerProvider;
    }

    @Override // javax.inject.Provider
    public DeviceProvisionedControllerImpl get() {
        return provideInstance(this.contextProvider, this.mainHandlerProvider);
    }

    public static DeviceProvisionedControllerImpl provideInstance(Provider<Context> contextProvider, Provider<Handler> mainHandlerProvider) {
        return new DeviceProvisionedControllerImpl(contextProvider.get(), mainHandlerProvider.get());
    }

    public static DeviceProvisionedControllerImpl_Factory create(Provider<Context> contextProvider, Provider<Handler> mainHandlerProvider) {
        return new DeviceProvisionedControllerImpl_Factory(contextProvider, mainHandlerProvider);
    }

    public static DeviceProvisionedControllerImpl newDeviceProvisionedControllerImpl(Context context, Handler mainHandler) {
        return new DeviceProvisionedControllerImpl(context, mainHandler);
    }
}
