package com.android.systemui.statusbar;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class NotificationListener_Factory implements Factory<NotificationListener> {
    private final Provider<Context> contextProvider;

    public NotificationListener_Factory(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override // javax.inject.Provider
    public NotificationListener get() {
        return provideInstance(this.contextProvider);
    }

    public static NotificationListener provideInstance(Provider<Context> contextProvider) {
        return new NotificationListener(contextProvider.get());
    }

    public static NotificationListener_Factory create(Provider<Context> contextProvider) {
        return new NotificationListener_Factory(contextProvider);
    }

    public static NotificationListener newNotificationListener(Context context) {
        return new NotificationListener(context);
    }
}
