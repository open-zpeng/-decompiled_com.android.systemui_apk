package com.android.systemui.statusbar.notification;

import android.os.Handler;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class VisualStabilityManager_Factory implements Factory<VisualStabilityManager> {
    private final Provider<Handler> handlerProvider;
    private final Provider<NotificationEntryManager> notificationEntryManagerProvider;

    public VisualStabilityManager_Factory(Provider<NotificationEntryManager> notificationEntryManagerProvider, Provider<Handler> handlerProvider) {
        this.notificationEntryManagerProvider = notificationEntryManagerProvider;
        this.handlerProvider = handlerProvider;
    }

    @Override // javax.inject.Provider
    public VisualStabilityManager get() {
        return provideInstance(this.notificationEntryManagerProvider, this.handlerProvider);
    }

    public static VisualStabilityManager provideInstance(Provider<NotificationEntryManager> notificationEntryManagerProvider, Provider<Handler> handlerProvider) {
        return new VisualStabilityManager(notificationEntryManagerProvider.get(), handlerProvider.get());
    }

    public static VisualStabilityManager_Factory create(Provider<NotificationEntryManager> notificationEntryManagerProvider, Provider<Handler> handlerProvider) {
        return new VisualStabilityManager_Factory(notificationEntryManagerProvider, handlerProvider);
    }

    public static VisualStabilityManager newVisualStabilityManager(NotificationEntryManager notificationEntryManager, Handler handler) {
        return new VisualStabilityManager(notificationEntryManager, handler);
    }
}
