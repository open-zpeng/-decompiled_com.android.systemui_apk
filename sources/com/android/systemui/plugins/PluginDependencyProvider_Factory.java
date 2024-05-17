package com.android.systemui.plugins;

import com.android.systemui.shared.plugins.PluginManager;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class PluginDependencyProvider_Factory implements Factory<PluginDependencyProvider> {
    private final Provider<PluginManager> managerProvider;

    public PluginDependencyProvider_Factory(Provider<PluginManager> managerProvider) {
        this.managerProvider = managerProvider;
    }

    @Override // javax.inject.Provider
    public PluginDependencyProvider get() {
        return provideInstance(this.managerProvider);
    }

    public static PluginDependencyProvider provideInstance(Provider<PluginManager> managerProvider) {
        return new PluginDependencyProvider(managerProvider.get());
    }

    public static PluginDependencyProvider_Factory create(Provider<PluginManager> managerProvider) {
        return new PluginDependencyProvider_Factory(managerProvider);
    }

    public static PluginDependencyProvider newPluginDependencyProvider(PluginManager manager) {
        return new PluginDependencyProvider(manager);
    }
}
