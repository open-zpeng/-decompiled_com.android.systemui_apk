package com.android.systemui;

import android.content.Context;
import com.android.systemui.statusbar.phone.ShadeController;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class SystemUIDefaultModule_ProvideShadeControllerFactory implements Factory<ShadeController> {
    private final Provider<Context> contextProvider;

    public SystemUIDefaultModule_ProvideShadeControllerFactory(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override // javax.inject.Provider
    public ShadeController get() {
        return provideInstance(this.contextProvider);
    }

    public static ShadeController provideInstance(Provider<Context> contextProvider) {
        return proxyProvideShadeController(contextProvider.get());
    }

    public static SystemUIDefaultModule_ProvideShadeControllerFactory create(Provider<Context> contextProvider) {
        return new SystemUIDefaultModule_ProvideShadeControllerFactory(contextProvider);
    }

    public static ShadeController proxyProvideShadeController(Context context) {
        return (ShadeController) Preconditions.checkNotNull(SystemUIDefaultModule.provideShadeController(context), "Cannot return null from a non-@Nullable @Provides method");
    }
}
