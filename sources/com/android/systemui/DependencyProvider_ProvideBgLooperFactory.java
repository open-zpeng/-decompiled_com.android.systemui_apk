package com.android.systemui;

import android.os.Looper;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
/* loaded from: classes21.dex */
public final class DependencyProvider_ProvideBgLooperFactory implements Factory<Looper> {
    private final DependencyProvider module;

    public DependencyProvider_ProvideBgLooperFactory(DependencyProvider module) {
        this.module = module;
    }

    @Override // javax.inject.Provider
    public Looper get() {
        return provideInstance(this.module);
    }

    public static Looper provideInstance(DependencyProvider module) {
        return proxyProvideBgLooper(module);
    }

    public static DependencyProvider_ProvideBgLooperFactory create(DependencyProvider module) {
        return new DependencyProvider_ProvideBgLooperFactory(module);
    }

    public static Looper proxyProvideBgLooper(DependencyProvider instance) {
        return (Looper) Preconditions.checkNotNull(instance.provideBgLooper(), "Cannot return null from a non-@Nullable @Provides method");
    }
}
