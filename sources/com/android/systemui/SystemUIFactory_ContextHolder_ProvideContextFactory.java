package com.android.systemui;

import android.content.Context;
import com.android.systemui.SystemUIFactory;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
/* loaded from: classes21.dex */
public final class SystemUIFactory_ContextHolder_ProvideContextFactory implements Factory<Context> {
    private final SystemUIFactory.ContextHolder module;

    public SystemUIFactory_ContextHolder_ProvideContextFactory(SystemUIFactory.ContextHolder module) {
        this.module = module;
    }

    @Override // javax.inject.Provider
    public Context get() {
        return provideInstance(this.module);
    }

    public static Context provideInstance(SystemUIFactory.ContextHolder module) {
        return proxyProvideContext(module);
    }

    public static SystemUIFactory_ContextHolder_ProvideContextFactory create(SystemUIFactory.ContextHolder module) {
        return new SystemUIFactory_ContextHolder_ProvideContextFactory(module);
    }

    public static Context proxyProvideContext(SystemUIFactory.ContextHolder instance) {
        return (Context) Preconditions.checkNotNull(instance.provideContext(), "Cannot return null from a non-@Nullable @Provides method");
    }
}
