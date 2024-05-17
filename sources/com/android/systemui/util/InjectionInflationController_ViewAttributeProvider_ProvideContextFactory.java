package com.android.systemui.util;

import android.content.Context;
import com.android.systemui.util.InjectionInflationController;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
/* loaded from: classes21.dex */
public final class InjectionInflationController_ViewAttributeProvider_ProvideContextFactory implements Factory<Context> {
    private final InjectionInflationController.ViewAttributeProvider module;

    public InjectionInflationController_ViewAttributeProvider_ProvideContextFactory(InjectionInflationController.ViewAttributeProvider module) {
        this.module = module;
    }

    @Override // javax.inject.Provider
    public Context get() {
        return provideInstance(this.module);
    }

    public static Context provideInstance(InjectionInflationController.ViewAttributeProvider module) {
        return proxyProvideContext(module);
    }

    public static InjectionInflationController_ViewAttributeProvider_ProvideContextFactory create(InjectionInflationController.ViewAttributeProvider module) {
        return new InjectionInflationController_ViewAttributeProvider_ProvideContextFactory(module);
    }

    public static Context proxyProvideContext(InjectionInflationController.ViewAttributeProvider instance) {
        return (Context) Preconditions.checkNotNull(instance.provideContext(), "Cannot return null from a non-@Nullable @Provides method");
    }
}
