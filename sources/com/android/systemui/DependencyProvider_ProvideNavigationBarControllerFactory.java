package com.android.systemui;

import android.content.Context;
import android.os.Handler;
import com.android.systemui.statusbar.NavigationBarController;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class DependencyProvider_ProvideNavigationBarControllerFactory implements Factory<NavigationBarController> {
    private final Provider<Context> contextProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final DependencyProvider module;

    public DependencyProvider_ProvideNavigationBarControllerFactory(DependencyProvider module, Provider<Context> contextProvider, Provider<Handler> mainHandlerProvider) {
        this.module = module;
        this.contextProvider = contextProvider;
        this.mainHandlerProvider = mainHandlerProvider;
    }

    @Override // javax.inject.Provider
    public NavigationBarController get() {
        return provideInstance(this.module, this.contextProvider, this.mainHandlerProvider);
    }

    public static NavigationBarController provideInstance(DependencyProvider module, Provider<Context> contextProvider, Provider<Handler> mainHandlerProvider) {
        return proxyProvideNavigationBarController(module, contextProvider.get(), mainHandlerProvider.get());
    }

    public static DependencyProvider_ProvideNavigationBarControllerFactory create(DependencyProvider module, Provider<Context> contextProvider, Provider<Handler> mainHandlerProvider) {
        return new DependencyProvider_ProvideNavigationBarControllerFactory(module, contextProvider, mainHandlerProvider);
    }

    public static NavigationBarController proxyProvideNavigationBarController(DependencyProvider instance, Context context, Handler mainHandler) {
        return (NavigationBarController) Preconditions.checkNotNull(instance.provideNavigationBarController(context, mainHandler), "Cannot return null from a non-@Nullable @Provides method");
    }
}
