package com.android.systemui;

import com.android.systemui.shared.system.DevicePolicyManagerWrapper;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
/* loaded from: classes21.dex */
public final class DependencyProvider_ProvideDevicePolicyManagerWrapperFactory implements Factory<DevicePolicyManagerWrapper> {
    private final DependencyProvider module;

    public DependencyProvider_ProvideDevicePolicyManagerWrapperFactory(DependencyProvider module) {
        this.module = module;
    }

    @Override // javax.inject.Provider
    public DevicePolicyManagerWrapper get() {
        return provideInstance(this.module);
    }

    public static DevicePolicyManagerWrapper provideInstance(DependencyProvider module) {
        return proxyProvideDevicePolicyManagerWrapper(module);
    }

    public static DependencyProvider_ProvideDevicePolicyManagerWrapperFactory create(DependencyProvider module) {
        return new DependencyProvider_ProvideDevicePolicyManagerWrapperFactory(module);
    }

    public static DevicePolicyManagerWrapper proxyProvideDevicePolicyManagerWrapper(DependencyProvider instance) {
        return (DevicePolicyManagerWrapper) Preconditions.checkNotNull(instance.provideDevicePolicyManagerWrapper(), "Cannot return null from a non-@Nullable @Provides method");
    }
}
