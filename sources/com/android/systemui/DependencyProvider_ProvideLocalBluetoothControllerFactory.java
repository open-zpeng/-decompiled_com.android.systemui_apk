package com.android.systemui;

import android.content.Context;
import android.os.Handler;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class DependencyProvider_ProvideLocalBluetoothControllerFactory implements Factory<LocalBluetoothManager> {
    private final Provider<Handler> bgHandlerProvider;
    private final Provider<Context> contextProvider;
    private final DependencyProvider module;

    public DependencyProvider_ProvideLocalBluetoothControllerFactory(DependencyProvider module, Provider<Context> contextProvider, Provider<Handler> bgHandlerProvider) {
        this.module = module;
        this.contextProvider = contextProvider;
        this.bgHandlerProvider = bgHandlerProvider;
    }

    @Override // javax.inject.Provider
    public LocalBluetoothManager get() {
        return provideInstance(this.module, this.contextProvider, this.bgHandlerProvider);
    }

    public static LocalBluetoothManager provideInstance(DependencyProvider module, Provider<Context> contextProvider, Provider<Handler> bgHandlerProvider) {
        return proxyProvideLocalBluetoothController(module, contextProvider.get(), bgHandlerProvider.get());
    }

    public static DependencyProvider_ProvideLocalBluetoothControllerFactory create(DependencyProvider module, Provider<Context> contextProvider, Provider<Handler> bgHandlerProvider) {
        return new DependencyProvider_ProvideLocalBluetoothControllerFactory(module, contextProvider, bgHandlerProvider);
    }

    public static LocalBluetoothManager proxyProvideLocalBluetoothController(DependencyProvider instance, Context context, Handler bgHandler) {
        return instance.provideLocalBluetoothController(context, bgHandler);
    }
}
