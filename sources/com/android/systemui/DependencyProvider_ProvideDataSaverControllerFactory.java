package com.android.systemui;

import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.NetworkController;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class DependencyProvider_ProvideDataSaverControllerFactory implements Factory<DataSaverController> {
    private final DependencyProvider module;
    private final Provider<NetworkController> networkControllerProvider;

    public DependencyProvider_ProvideDataSaverControllerFactory(DependencyProvider module, Provider<NetworkController> networkControllerProvider) {
        this.module = module;
        this.networkControllerProvider = networkControllerProvider;
    }

    @Override // javax.inject.Provider
    public DataSaverController get() {
        return provideInstance(this.module, this.networkControllerProvider);
    }

    public static DataSaverController provideInstance(DependencyProvider module, Provider<NetworkController> networkControllerProvider) {
        return proxyProvideDataSaverController(module, networkControllerProvider.get());
    }

    public static DependencyProvider_ProvideDataSaverControllerFactory create(DependencyProvider module, Provider<NetworkController> networkControllerProvider) {
        return new DependencyProvider_ProvideDataSaverControllerFactory(module, networkControllerProvider);
    }

    public static DataSaverController proxyProvideDataSaverController(DependencyProvider instance, NetworkController networkController) {
        return (DataSaverController) Preconditions.checkNotNull(instance.provideDataSaverController(networkController), "Cannot return null from a non-@Nullable @Provides method");
    }
}
