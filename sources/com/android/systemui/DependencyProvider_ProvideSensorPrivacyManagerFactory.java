package com.android.systemui;

import android.content.Context;
import android.hardware.SensorPrivacyManager;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class DependencyProvider_ProvideSensorPrivacyManagerFactory implements Factory<SensorPrivacyManager> {
    private final Provider<Context> contextProvider;
    private final DependencyProvider module;

    public DependencyProvider_ProvideSensorPrivacyManagerFactory(DependencyProvider module, Provider<Context> contextProvider) {
        this.module = module;
        this.contextProvider = contextProvider;
    }

    @Override // javax.inject.Provider
    public SensorPrivacyManager get() {
        return provideInstance(this.module, this.contextProvider);
    }

    public static SensorPrivacyManager provideInstance(DependencyProvider module, Provider<Context> contextProvider) {
        return proxyProvideSensorPrivacyManager(module, contextProvider.get());
    }

    public static DependencyProvider_ProvideSensorPrivacyManagerFactory create(DependencyProvider module, Provider<Context> contextProvider) {
        return new DependencyProvider_ProvideSensorPrivacyManagerFactory(module, contextProvider);
    }

    public static SensorPrivacyManager proxyProvideSensorPrivacyManager(DependencyProvider instance, Context context) {
        return (SensorPrivacyManager) Preconditions.checkNotNull(instance.provideSensorPrivacyManager(context), "Cannot return null from a non-@Nullable @Provides method");
    }
}
