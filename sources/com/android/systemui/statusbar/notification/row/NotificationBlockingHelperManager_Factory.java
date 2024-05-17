package com.android.systemui.statusbar.notification.row;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class NotificationBlockingHelperManager_Factory implements Factory<NotificationBlockingHelperManager> {
    private final Provider<Context> contextProvider;

    public NotificationBlockingHelperManager_Factory(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override // javax.inject.Provider
    public NotificationBlockingHelperManager get() {
        return provideInstance(this.contextProvider);
    }

    public static NotificationBlockingHelperManager provideInstance(Provider<Context> contextProvider) {
        return new NotificationBlockingHelperManager(contextProvider.get());
    }

    public static NotificationBlockingHelperManager_Factory create(Provider<Context> contextProvider) {
        return new NotificationBlockingHelperManager_Factory(contextProvider);
    }

    public static NotificationBlockingHelperManager newNotificationBlockingHelperManager(Context context) {
        return new NotificationBlockingHelperManager(context);
    }
}
