package com.android.systemui;

import android.app.INotificationManager;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
/* loaded from: classes21.dex */
public final class DependencyProvider_ProvideINotificationManagerFactory implements Factory<INotificationManager> {
    private final DependencyProvider module;

    public DependencyProvider_ProvideINotificationManagerFactory(DependencyProvider module) {
        this.module = module;
    }

    @Override // javax.inject.Provider
    public INotificationManager get() {
        return provideInstance(this.module);
    }

    public static INotificationManager provideInstance(DependencyProvider module) {
        return proxyProvideINotificationManager(module);
    }

    public static DependencyProvider_ProvideINotificationManagerFactory create(DependencyProvider module) {
        return new DependencyProvider_ProvideINotificationManagerFactory(module);
    }

    public static INotificationManager proxyProvideINotificationManager(DependencyProvider instance) {
        return (INotificationManager) Preconditions.checkNotNull(instance.provideINotificationManager(), "Cannot return null from a non-@Nullable @Provides method");
    }
}
