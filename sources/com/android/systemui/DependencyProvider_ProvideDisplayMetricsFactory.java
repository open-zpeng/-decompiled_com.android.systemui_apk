package com.android.systemui;

import android.util.DisplayMetrics;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
/* loaded from: classes21.dex */
public final class DependencyProvider_ProvideDisplayMetricsFactory implements Factory<DisplayMetrics> {
    private final DependencyProvider module;

    public DependencyProvider_ProvideDisplayMetricsFactory(DependencyProvider module) {
        this.module = module;
    }

    @Override // javax.inject.Provider
    public DisplayMetrics get() {
        return provideInstance(this.module);
    }

    public static DisplayMetrics provideInstance(DependencyProvider module) {
        return proxyProvideDisplayMetrics(module);
    }

    public static DependencyProvider_ProvideDisplayMetricsFactory create(DependencyProvider module) {
        return new DependencyProvider_ProvideDisplayMetricsFactory(module);
    }

    public static DisplayMetrics proxyProvideDisplayMetrics(DependencyProvider instance) {
        return (DisplayMetrics) Preconditions.checkNotNull(instance.provideDisplayMetrics(), "Cannot return null from a non-@Nullable @Provides method");
    }
}
