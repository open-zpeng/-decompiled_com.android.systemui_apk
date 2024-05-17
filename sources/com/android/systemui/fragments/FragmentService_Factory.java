package com.android.systemui.fragments;

import com.android.systemui.SystemUIRootComponent;
import com.android.systemui.statusbar.policy.ConfigurationController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class FragmentService_Factory implements Factory<FragmentService> {
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<SystemUIRootComponent> rootComponentProvider;

    public FragmentService_Factory(Provider<SystemUIRootComponent> rootComponentProvider, Provider<ConfigurationController> configurationControllerProvider) {
        this.rootComponentProvider = rootComponentProvider;
        this.configurationControllerProvider = configurationControllerProvider;
    }

    @Override // javax.inject.Provider
    public FragmentService get() {
        return provideInstance(this.rootComponentProvider, this.configurationControllerProvider);
    }

    public static FragmentService provideInstance(Provider<SystemUIRootComponent> rootComponentProvider, Provider<ConfigurationController> configurationControllerProvider) {
        return new FragmentService(rootComponentProvider.get(), configurationControllerProvider.get());
    }

    public static FragmentService_Factory create(Provider<SystemUIRootComponent> rootComponentProvider, Provider<ConfigurationController> configurationControllerProvider) {
        return new FragmentService_Factory(rootComponentProvider, configurationControllerProvider);
    }

    public static FragmentService newFragmentService(SystemUIRootComponent rootComponent, ConfigurationController configurationController) {
        return new FragmentService(rootComponent, configurationController);
    }
}
