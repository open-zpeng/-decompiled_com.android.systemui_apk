package com.android.systemui.statusbar;

import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class SmartReplyController_Factory implements Factory<SmartReplyController> {
    private final Provider<NotificationEntryManager> entryManagerProvider;
    private final Provider<IStatusBarService> statusBarServiceProvider;

    public SmartReplyController_Factory(Provider<NotificationEntryManager> entryManagerProvider, Provider<IStatusBarService> statusBarServiceProvider) {
        this.entryManagerProvider = entryManagerProvider;
        this.statusBarServiceProvider = statusBarServiceProvider;
    }

    @Override // javax.inject.Provider
    public SmartReplyController get() {
        return provideInstance(this.entryManagerProvider, this.statusBarServiceProvider);
    }

    public static SmartReplyController provideInstance(Provider<NotificationEntryManager> entryManagerProvider, Provider<IStatusBarService> statusBarServiceProvider) {
        return new SmartReplyController(entryManagerProvider.get(), statusBarServiceProvider.get());
    }

    public static SmartReplyController_Factory create(Provider<NotificationEntryManager> entryManagerProvider, Provider<IStatusBarService> statusBarServiceProvider) {
        return new SmartReplyController_Factory(entryManagerProvider, statusBarServiceProvider);
    }

    public static SmartReplyController newSmartReplyController(NotificationEntryManager entryManager, IStatusBarService statusBarService) {
        return new SmartReplyController(entryManager, statusBarService);
    }
}
