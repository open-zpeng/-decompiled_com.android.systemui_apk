package com.android.systemui.statusbar.phone;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class StatusBarRemoteInputCallback_Factory implements Factory<StatusBarRemoteInputCallback> {
    private final Provider<Context> contextProvider;
    private final Provider<NotificationGroupManager> groupManagerProvider;

    public StatusBarRemoteInputCallback_Factory(Provider<Context> contextProvider, Provider<NotificationGroupManager> groupManagerProvider) {
        this.contextProvider = contextProvider;
        this.groupManagerProvider = groupManagerProvider;
    }

    @Override // javax.inject.Provider
    public StatusBarRemoteInputCallback get() {
        return provideInstance(this.contextProvider, this.groupManagerProvider);
    }

    public static StatusBarRemoteInputCallback provideInstance(Provider<Context> contextProvider, Provider<NotificationGroupManager> groupManagerProvider) {
        return new StatusBarRemoteInputCallback(contextProvider.get(), groupManagerProvider.get());
    }

    public static StatusBarRemoteInputCallback_Factory create(Provider<Context> contextProvider, Provider<NotificationGroupManager> groupManagerProvider) {
        return new StatusBarRemoteInputCallback_Factory(contextProvider, groupManagerProvider);
    }

    public static StatusBarRemoteInputCallback newStatusBarRemoteInputCallback(Context context, NotificationGroupManager groupManager) {
        return new StatusBarRemoteInputCallback(context, groupManager);
    }
}
