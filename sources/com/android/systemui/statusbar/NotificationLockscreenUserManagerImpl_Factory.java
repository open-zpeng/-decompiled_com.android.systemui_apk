package com.android.systemui.statusbar;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class NotificationLockscreenUserManagerImpl_Factory implements Factory<NotificationLockscreenUserManagerImpl> {
    private final Provider<Context> contextProvider;

    public NotificationLockscreenUserManagerImpl_Factory(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override // javax.inject.Provider
    public NotificationLockscreenUserManagerImpl get() {
        return provideInstance(this.contextProvider);
    }

    public static NotificationLockscreenUserManagerImpl provideInstance(Provider<Context> contextProvider) {
        return new NotificationLockscreenUserManagerImpl(contextProvider.get());
    }

    public static NotificationLockscreenUserManagerImpl_Factory create(Provider<Context> contextProvider) {
        return new NotificationLockscreenUserManagerImpl_Factory(contextProvider);
    }

    public static NotificationLockscreenUserManagerImpl newNotificationLockscreenUserManagerImpl(Context context) {
        return new NotificationLockscreenUserManagerImpl(context);
    }
}
