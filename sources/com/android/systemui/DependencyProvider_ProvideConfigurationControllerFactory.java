package com.android.systemui;

import android.content.Context;
import com.android.systemui.statusbar.policy.ConfigurationController;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class DependencyProvider_ProvideConfigurationControllerFactory implements Factory<ConfigurationController> {
    private final Provider<Context> contextProvider;
    private final DependencyProvider module;

    public DependencyProvider_ProvideConfigurationControllerFactory(DependencyProvider module, Provider<Context> contextProvider) {
        this.module = module;
        this.contextProvider = contextProvider;
    }

    @Override // javax.inject.Provider
    public ConfigurationController get() {
        return provideInstance(this.module, this.contextProvider);
    }

    public static ConfigurationController provideInstance(DependencyProvider module, Provider<Context> contextProvider) {
        return proxyProvideConfigurationController(module, contextProvider.get());
    }

    public static DependencyProvider_ProvideConfigurationControllerFactory create(DependencyProvider module, Provider<Context> contextProvider) {
        return new DependencyProvider_ProvideConfigurationControllerFactory(module, contextProvider);
    }

    public static ConfigurationController proxyProvideConfigurationController(DependencyProvider instance, Context context) {
        return (ConfigurationController) Preconditions.checkNotNull(instance.provideConfigurationController(context), "Cannot return null from a non-@Nullable @Provides method");
    }
}
