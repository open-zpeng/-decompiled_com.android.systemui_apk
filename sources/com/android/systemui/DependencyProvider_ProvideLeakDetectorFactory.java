package com.android.systemui;

import com.android.systemui.util.leak.LeakDetector;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
/* loaded from: classes21.dex */
public final class DependencyProvider_ProvideLeakDetectorFactory implements Factory<LeakDetector> {
    private final DependencyProvider module;

    public DependencyProvider_ProvideLeakDetectorFactory(DependencyProvider module) {
        this.module = module;
    }

    @Override // javax.inject.Provider
    public LeakDetector get() {
        return provideInstance(this.module);
    }

    public static LeakDetector provideInstance(DependencyProvider module) {
        return proxyProvideLeakDetector(module);
    }

    public static DependencyProvider_ProvideLeakDetectorFactory create(DependencyProvider module) {
        return new DependencyProvider_ProvideLeakDetectorFactory(module);
    }

    public static LeakDetector proxyProvideLeakDetector(DependencyProvider instance) {
        return (LeakDetector) Preconditions.checkNotNull(instance.provideLeakDetector(), "Cannot return null from a non-@Nullable @Provides method");
    }
}
