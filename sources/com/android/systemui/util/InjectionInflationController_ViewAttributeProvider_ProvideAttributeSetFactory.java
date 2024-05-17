package com.android.systemui.util;

import android.util.AttributeSet;
import com.android.systemui.util.InjectionInflationController;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
/* loaded from: classes21.dex */
public final class InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory implements Factory<AttributeSet> {
    private final InjectionInflationController.ViewAttributeProvider module;

    public InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory(InjectionInflationController.ViewAttributeProvider module) {
        this.module = module;
    }

    @Override // javax.inject.Provider
    public AttributeSet get() {
        return provideInstance(this.module);
    }

    public static AttributeSet provideInstance(InjectionInflationController.ViewAttributeProvider module) {
        return proxyProvideAttributeSet(module);
    }

    public static InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory create(InjectionInflationController.ViewAttributeProvider module) {
        return new InjectionInflationController_ViewAttributeProvider_ProvideAttributeSetFactory(module);
    }

    public static AttributeSet proxyProvideAttributeSet(InjectionInflationController.ViewAttributeProvider instance) {
        return (AttributeSet) Preconditions.checkNotNull(instance.provideAttributeSet(), "Cannot return null from a non-@Nullable @Provides method");
    }
}
