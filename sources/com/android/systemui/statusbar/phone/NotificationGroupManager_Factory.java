package com.android.systemui.statusbar.phone;

import com.android.systemui.plugins.statusbar.StatusBarStateController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class NotificationGroupManager_Factory implements Factory<NotificationGroupManager> {
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;

    public NotificationGroupManager_Factory(Provider<StatusBarStateController> statusBarStateControllerProvider) {
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
    }

    @Override // javax.inject.Provider
    public NotificationGroupManager get() {
        return provideInstance(this.statusBarStateControllerProvider);
    }

    public static NotificationGroupManager provideInstance(Provider<StatusBarStateController> statusBarStateControllerProvider) {
        return new NotificationGroupManager(statusBarStateControllerProvider.get());
    }

    public static NotificationGroupManager_Factory create(Provider<StatusBarStateController> statusBarStateControllerProvider) {
        return new NotificationGroupManager_Factory(statusBarStateControllerProvider);
    }

    public static NotificationGroupManager newNotificationGroupManager(StatusBarStateController statusBarStateController) {
        return new NotificationGroupManager(statusBarStateController);
    }
}
