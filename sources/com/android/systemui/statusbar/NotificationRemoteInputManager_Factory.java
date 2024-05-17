package com.android.systemui.statusbar;

import android.content.Context;
import android.os.Handler;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.phone.ShadeController;
import dagger.Lazy;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class NotificationRemoteInputManager_Factory implements Factory<NotificationRemoteInputManager> {
    private final Provider<Context> contextProvider;
    private final Provider<NotificationLockscreenUserManager> lockscreenUserManagerProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<NotificationEntryManager> notificationEntryManagerProvider;
    private final Provider<ShadeController> shadeControllerProvider;
    private final Provider<SmartReplyController> smartReplyControllerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;

    public NotificationRemoteInputManager_Factory(Provider<Context> contextProvider, Provider<NotificationLockscreenUserManager> lockscreenUserManagerProvider, Provider<SmartReplyController> smartReplyControllerProvider, Provider<NotificationEntryManager> notificationEntryManagerProvider, Provider<ShadeController> shadeControllerProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<Handler> mainHandlerProvider) {
        this.contextProvider = contextProvider;
        this.lockscreenUserManagerProvider = lockscreenUserManagerProvider;
        this.smartReplyControllerProvider = smartReplyControllerProvider;
        this.notificationEntryManagerProvider = notificationEntryManagerProvider;
        this.shadeControllerProvider = shadeControllerProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.mainHandlerProvider = mainHandlerProvider;
    }

    @Override // javax.inject.Provider
    public NotificationRemoteInputManager get() {
        return provideInstance(this.contextProvider, this.lockscreenUserManagerProvider, this.smartReplyControllerProvider, this.notificationEntryManagerProvider, this.shadeControllerProvider, this.statusBarStateControllerProvider, this.mainHandlerProvider);
    }

    public static NotificationRemoteInputManager provideInstance(Provider<Context> contextProvider, Provider<NotificationLockscreenUserManager> lockscreenUserManagerProvider, Provider<SmartReplyController> smartReplyControllerProvider, Provider<NotificationEntryManager> notificationEntryManagerProvider, Provider<ShadeController> shadeControllerProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<Handler> mainHandlerProvider) {
        return new NotificationRemoteInputManager(contextProvider.get(), lockscreenUserManagerProvider.get(), smartReplyControllerProvider.get(), notificationEntryManagerProvider.get(), DoubleCheck.lazy(shadeControllerProvider), statusBarStateControllerProvider.get(), mainHandlerProvider.get());
    }

    public static NotificationRemoteInputManager_Factory create(Provider<Context> contextProvider, Provider<NotificationLockscreenUserManager> lockscreenUserManagerProvider, Provider<SmartReplyController> smartReplyControllerProvider, Provider<NotificationEntryManager> notificationEntryManagerProvider, Provider<ShadeController> shadeControllerProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<Handler> mainHandlerProvider) {
        return new NotificationRemoteInputManager_Factory(contextProvider, lockscreenUserManagerProvider, smartReplyControllerProvider, notificationEntryManagerProvider, shadeControllerProvider, statusBarStateControllerProvider, mainHandlerProvider);
    }

    public static NotificationRemoteInputManager newNotificationRemoteInputManager(Context context, NotificationLockscreenUserManager lockscreenUserManager, SmartReplyController smartReplyController, NotificationEntryManager notificationEntryManager, Lazy<ShadeController> shadeController, StatusBarStateController statusBarStateController, Handler mainHandler) {
        return new NotificationRemoteInputManager(context, lockscreenUserManager, smartReplyController, notificationEntryManager, shadeController, statusBarStateController, mainHandler);
    }
}
