package com.android.systemui;

import android.os.Handler;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
/* loaded from: classes21.dex */
public final class DependencyProvider_ProvideMainHandlerFactory implements Factory<Handler> {
    private final DependencyProvider module;

    public DependencyProvider_ProvideMainHandlerFactory(DependencyProvider module) {
        this.module = module;
    }

    @Override // javax.inject.Provider
    public Handler get() {
        return provideInstance(this.module);
    }

    public static Handler provideInstance(DependencyProvider module) {
        return proxyProvideMainHandler(module);
    }

    public static DependencyProvider_ProvideMainHandlerFactory create(DependencyProvider module) {
        return new DependencyProvider_ProvideMainHandlerFactory(module);
    }

    public static Handler proxyProvideMainHandler(DependencyProvider instance) {
        return (Handler) Preconditions.checkNotNull(instance.provideMainHandler(), "Cannot return null from a non-@Nullable @Provides method");
    }
}
