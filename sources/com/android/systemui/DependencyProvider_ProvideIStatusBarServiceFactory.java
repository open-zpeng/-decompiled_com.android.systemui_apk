package com.android.systemui;

import com.android.internal.statusbar.IStatusBarService;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
/* loaded from: classes21.dex */
public final class DependencyProvider_ProvideIStatusBarServiceFactory implements Factory<IStatusBarService> {
    private final DependencyProvider module;

    public DependencyProvider_ProvideIStatusBarServiceFactory(DependencyProvider module) {
        this.module = module;
    }

    @Override // javax.inject.Provider
    public IStatusBarService get() {
        return provideInstance(this.module);
    }

    public static IStatusBarService provideInstance(DependencyProvider module) {
        return proxyProvideIStatusBarService(module);
    }

    public static DependencyProvider_ProvideIStatusBarServiceFactory create(DependencyProvider module) {
        return new DependencyProvider_ProvideIStatusBarServiceFactory(module);
    }

    public static IStatusBarService proxyProvideIStatusBarService(DependencyProvider instance) {
        return (IStatusBarService) Preconditions.checkNotNull(instance.provideIStatusBarService(), "Cannot return null from a non-@Nullable @Provides method");
    }
}
