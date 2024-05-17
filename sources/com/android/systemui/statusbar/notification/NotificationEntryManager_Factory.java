package com.android.systemui.statusbar.notification;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class NotificationEntryManager_Factory implements Factory<NotificationEntryManager> {
    private final Provider<Context> contextProvider;

    public NotificationEntryManager_Factory(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override // javax.inject.Provider
    public NotificationEntryManager get() {
        return provideInstance(this.contextProvider);
    }

    public static NotificationEntryManager provideInstance(Provider<Context> contextProvider) {
        return new NotificationEntryManager(contextProvider.get());
    }

    public static NotificationEntryManager_Factory create(Provider<Context> contextProvider) {
        return new NotificationEntryManager_Factory(contextProvider);
    }

    public static NotificationEntryManager newNotificationEntryManager(Context context) {
        return new NotificationEntryManager(context);
    }
}
