package com.android.systemui.util;

import com.android.systemui.SystemUIRootComponent;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class InjectionInflationController_Factory implements Factory<InjectionInflationController> {
    private final Provider<SystemUIRootComponent> rootComponentProvider;

    public InjectionInflationController_Factory(Provider<SystemUIRootComponent> rootComponentProvider) {
        this.rootComponentProvider = rootComponentProvider;
    }

    @Override // javax.inject.Provider
    public InjectionInflationController get() {
        return provideInstance(this.rootComponentProvider);
    }

    public static InjectionInflationController provideInstance(Provider<SystemUIRootComponent> rootComponentProvider) {
        return new InjectionInflationController(rootComponentProvider.get());
    }

    public static InjectionInflationController_Factory create(Provider<SystemUIRootComponent> rootComponentProvider) {
        return new InjectionInflationController_Factory(rootComponentProvider);
    }

    public static InjectionInflationController newInjectionInflationController(SystemUIRootComponent rootComponent) {
        return new InjectionInflationController(rootComponent);
    }
}
