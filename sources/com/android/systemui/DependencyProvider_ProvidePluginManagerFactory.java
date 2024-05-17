package com.android.systemui;

import android.content.Context;
import com.android.systemui.shared.plugins.PluginManager;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class DependencyProvider_ProvidePluginManagerFactory implements Factory<PluginManager> {
    private final Provider<Context> contextProvider;
    private final DependencyProvider module;

    public DependencyProvider_ProvidePluginManagerFactory(DependencyProvider module, Provider<Context> contextProvider) {
        this.module = module;
        this.contextProvider = contextProvider;
    }

    @Override // javax.inject.Provider
    public PluginManager get() {
        return provideInstance(this.module, this.contextProvider);
    }

    public static PluginManager provideInstance(DependencyProvider module, Provider<Context> contextProvider) {
        return proxyProvidePluginManager(module, contextProvider.get());
    }

    public static DependencyProvider_ProvidePluginManagerFactory create(DependencyProvider module, Provider<Context> contextProvider) {
        return new DependencyProvider_ProvidePluginManagerFactory(module, contextProvider);
    }

    public static PluginManager proxyProvidePluginManager(DependencyProvider instance, Context context) {
        return (PluginManager) Preconditions.checkNotNull(instance.providePluginManager(context), "Cannot return null from a non-@Nullable @Provides method");
    }
}
