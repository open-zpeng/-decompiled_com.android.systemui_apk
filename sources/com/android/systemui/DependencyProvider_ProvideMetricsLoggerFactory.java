package com.android.systemui;

import com.android.internal.logging.MetricsLogger;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
/* loaded from: classes21.dex */
public final class DependencyProvider_ProvideMetricsLoggerFactory implements Factory<MetricsLogger> {
    private final DependencyProvider module;

    public DependencyProvider_ProvideMetricsLoggerFactory(DependencyProvider module) {
        this.module = module;
    }

    @Override // javax.inject.Provider
    public MetricsLogger get() {
        return provideInstance(this.module);
    }

    public static MetricsLogger provideInstance(DependencyProvider module) {
        return proxyProvideMetricsLogger(module);
    }

    public static DependencyProvider_ProvideMetricsLoggerFactory create(DependencyProvider module) {
        return new DependencyProvider_ProvideMetricsLoggerFactory(module);
    }

    public static MetricsLogger proxyProvideMetricsLogger(DependencyProvider instance) {
        return (MetricsLogger) Preconditions.checkNotNull(instance.provideMetricsLogger(), "Cannot return null from a non-@Nullable @Provides method");
    }
}
