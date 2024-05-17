package com.android.systemui;

import android.os.Handler;
import android.os.Looper;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class DependencyProvider_ProvideBgHandlerFactory implements Factory<Handler> {
    private final Provider<Looper> bgLooperProvider;
    private final DependencyProvider module;

    public DependencyProvider_ProvideBgHandlerFactory(DependencyProvider module, Provider<Looper> bgLooperProvider) {
        this.module = module;
        this.bgLooperProvider = bgLooperProvider;
    }

    @Override // javax.inject.Provider
    public Handler get() {
        return provideInstance(this.module, this.bgLooperProvider);
    }

    public static Handler provideInstance(DependencyProvider module, Provider<Looper> bgLooperProvider) {
        return proxyProvideBgHandler(module, bgLooperProvider.get());
    }

    public static DependencyProvider_ProvideBgHandlerFactory create(DependencyProvider module, Provider<Looper> bgLooperProvider) {
        return new DependencyProvider_ProvideBgHandlerFactory(module, bgLooperProvider);
    }

    public static Handler proxyProvideBgHandler(DependencyProvider instance, Looper bgLooper) {
        return (Handler) Preconditions.checkNotNull(instance.provideBgHandler(bgLooper), "Cannot return null from a non-@Nullable @Provides method");
    }
}
