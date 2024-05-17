package com.android.systemui;

import android.content.Context;
import android.hardware.display.NightDisplayListener;
import android.os.Handler;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class DependencyProvider_ProvideNightDisplayListenerFactory implements Factory<NightDisplayListener> {
    private final Provider<Handler> bgHandlerProvider;
    private final Provider<Context> contextProvider;
    private final DependencyProvider module;

    public DependencyProvider_ProvideNightDisplayListenerFactory(DependencyProvider module, Provider<Context> contextProvider, Provider<Handler> bgHandlerProvider) {
        this.module = module;
        this.contextProvider = contextProvider;
        this.bgHandlerProvider = bgHandlerProvider;
    }

    @Override // javax.inject.Provider
    public NightDisplayListener get() {
        return provideInstance(this.module, this.contextProvider, this.bgHandlerProvider);
    }

    public static NightDisplayListener provideInstance(DependencyProvider module, Provider<Context> contextProvider, Provider<Handler> bgHandlerProvider) {
        return proxyProvideNightDisplayListener(module, contextProvider.get(), bgHandlerProvider.get());
    }

    public static DependencyProvider_ProvideNightDisplayListenerFactory create(DependencyProvider module, Provider<Context> contextProvider, Provider<Handler> bgHandlerProvider) {
        return new DependencyProvider_ProvideNightDisplayListenerFactory(module, contextProvider, bgHandlerProvider);
    }

    public static NightDisplayListener proxyProvideNightDisplayListener(DependencyProvider instance, Context context, Handler bgHandler) {
        return (NightDisplayListener) Preconditions.checkNotNull(instance.provideNightDisplayListener(context, bgHandler), "Cannot return null from a non-@Nullable @Provides method");
    }
}
