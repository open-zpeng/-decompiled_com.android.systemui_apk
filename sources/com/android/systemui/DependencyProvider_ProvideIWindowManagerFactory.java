package com.android.systemui;

import android.view.IWindowManager;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
/* loaded from: classes21.dex */
public final class DependencyProvider_ProvideIWindowManagerFactory implements Factory<IWindowManager> {
    private final DependencyProvider module;

    public DependencyProvider_ProvideIWindowManagerFactory(DependencyProvider module) {
        this.module = module;
    }

    @Override // javax.inject.Provider
    public IWindowManager get() {
        return provideInstance(this.module);
    }

    public static IWindowManager provideInstance(DependencyProvider module) {
        return proxyProvideIWindowManager(module);
    }

    public static DependencyProvider_ProvideIWindowManagerFactory create(DependencyProvider module) {
        return new DependencyProvider_ProvideIWindowManagerFactory(module);
    }

    public static IWindowManager proxyProvideIWindowManager(DependencyProvider instance) {
        return (IWindowManager) Preconditions.checkNotNull(instance.provideIWindowManager(), "Cannot return null from a non-@Nullable @Provides method");
    }
}
