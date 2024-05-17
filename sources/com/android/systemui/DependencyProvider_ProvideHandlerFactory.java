package com.android.systemui;

import android.os.Handler;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
/* loaded from: classes21.dex */
public final class DependencyProvider_ProvideHandlerFactory implements Factory<Handler> {
    private final DependencyProvider module;

    public DependencyProvider_ProvideHandlerFactory(DependencyProvider module) {
        this.module = module;
    }

    @Override // javax.inject.Provider
    public Handler get() {
        return provideInstance(this.module);
    }

    public static Handler provideInstance(DependencyProvider module) {
        return proxyProvideHandler(module);
    }

    public static DependencyProvider_ProvideHandlerFactory create(DependencyProvider module) {
        return new DependencyProvider_ProvideHandlerFactory(module);
    }

    public static Handler proxyProvideHandler(DependencyProvider instance) {
        return (Handler) Preconditions.checkNotNull(instance.provideHandler(), "Cannot return null from a non-@Nullable @Provides method");
    }
}
