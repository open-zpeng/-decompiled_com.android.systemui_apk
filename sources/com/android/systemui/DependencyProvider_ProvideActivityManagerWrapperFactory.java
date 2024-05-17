package com.android.systemui;

import com.android.systemui.shared.system.ActivityManagerWrapper;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
/* loaded from: classes21.dex */
public final class DependencyProvider_ProvideActivityManagerWrapperFactory implements Factory<ActivityManagerWrapper> {
    private final DependencyProvider module;

    public DependencyProvider_ProvideActivityManagerWrapperFactory(DependencyProvider module) {
        this.module = module;
    }

    @Override // javax.inject.Provider
    public ActivityManagerWrapper get() {
        return provideInstance(this.module);
    }

    public static ActivityManagerWrapper provideInstance(DependencyProvider module) {
        return proxyProvideActivityManagerWrapper(module);
    }

    public static DependencyProvider_ProvideActivityManagerWrapperFactory create(DependencyProvider module) {
        return new DependencyProvider_ProvideActivityManagerWrapperFactory(module);
    }

    public static ActivityManagerWrapper proxyProvideActivityManagerWrapper(DependencyProvider instance) {
        return (ActivityManagerWrapper) Preconditions.checkNotNull(instance.provideActivityManagerWrapper(), "Cannot return null from a non-@Nullable @Provides method");
    }
}
