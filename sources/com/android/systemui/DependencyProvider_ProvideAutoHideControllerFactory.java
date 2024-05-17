package com.android.systemui;

import android.content.Context;
import android.os.Handler;
import com.android.systemui.statusbar.phone.AutoHideController;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class DependencyProvider_ProvideAutoHideControllerFactory implements Factory<AutoHideController> {
    private final Provider<Context> contextProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final DependencyProvider module;

    public DependencyProvider_ProvideAutoHideControllerFactory(DependencyProvider module, Provider<Context> contextProvider, Provider<Handler> mainHandlerProvider) {
        this.module = module;
        this.contextProvider = contextProvider;
        this.mainHandlerProvider = mainHandlerProvider;
    }

    @Override // javax.inject.Provider
    public AutoHideController get() {
        return provideInstance(this.module, this.contextProvider, this.mainHandlerProvider);
    }

    public static AutoHideController provideInstance(DependencyProvider module, Provider<Context> contextProvider, Provider<Handler> mainHandlerProvider) {
        return proxyProvideAutoHideController(module, contextProvider.get(), mainHandlerProvider.get());
    }

    public static DependencyProvider_ProvideAutoHideControllerFactory create(DependencyProvider module, Provider<Context> contextProvider, Provider<Handler> mainHandlerProvider) {
        return new DependencyProvider_ProvideAutoHideControllerFactory(module, contextProvider, mainHandlerProvider);
    }

    public static AutoHideController proxyProvideAutoHideController(DependencyProvider instance, Context context, Handler mainHandler) {
        return (AutoHideController) Preconditions.checkNotNull(instance.provideAutoHideController(context, mainHandler), "Cannot return null from a non-@Nullable @Provides method");
    }
}
