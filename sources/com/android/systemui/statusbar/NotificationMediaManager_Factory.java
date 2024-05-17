package com.android.systemui.statusbar;

import android.content.Context;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.statusbar.phone.StatusBarWindowController;
import dagger.Lazy;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class NotificationMediaManager_Factory implements Factory<NotificationMediaManager> {
    private final Provider<Context> contextProvider;
    private final Provider<KeyguardBypassController> keyguardBypassControllerProvider;
    private final Provider<MediaArtworkProcessor> mediaArtworkProcessorProvider;
    private final Provider<NotificationEntryManager> notificationEntryManagerProvider;
    private final Provider<ShadeController> shadeControllerProvider;
    private final Provider<StatusBarWindowController> statusBarWindowControllerProvider;

    public NotificationMediaManager_Factory(Provider<Context> contextProvider, Provider<ShadeController> shadeControllerProvider, Provider<StatusBarWindowController> statusBarWindowControllerProvider, Provider<NotificationEntryManager> notificationEntryManagerProvider, Provider<MediaArtworkProcessor> mediaArtworkProcessorProvider, Provider<KeyguardBypassController> keyguardBypassControllerProvider) {
        this.contextProvider = contextProvider;
        this.shadeControllerProvider = shadeControllerProvider;
        this.statusBarWindowControllerProvider = statusBarWindowControllerProvider;
        this.notificationEntryManagerProvider = notificationEntryManagerProvider;
        this.mediaArtworkProcessorProvider = mediaArtworkProcessorProvider;
        this.keyguardBypassControllerProvider = keyguardBypassControllerProvider;
    }

    @Override // javax.inject.Provider
    public NotificationMediaManager get() {
        return provideInstance(this.contextProvider, this.shadeControllerProvider, this.statusBarWindowControllerProvider, this.notificationEntryManagerProvider, this.mediaArtworkProcessorProvider, this.keyguardBypassControllerProvider);
    }

    public static NotificationMediaManager provideInstance(Provider<Context> contextProvider, Provider<ShadeController> shadeControllerProvider, Provider<StatusBarWindowController> statusBarWindowControllerProvider, Provider<NotificationEntryManager> notificationEntryManagerProvider, Provider<MediaArtworkProcessor> mediaArtworkProcessorProvider, Provider<KeyguardBypassController> keyguardBypassControllerProvider) {
        return new NotificationMediaManager(contextProvider.get(), DoubleCheck.lazy(shadeControllerProvider), DoubleCheck.lazy(statusBarWindowControllerProvider), notificationEntryManagerProvider.get(), mediaArtworkProcessorProvider.get(), keyguardBypassControllerProvider.get());
    }

    public static NotificationMediaManager_Factory create(Provider<Context> contextProvider, Provider<ShadeController> shadeControllerProvider, Provider<StatusBarWindowController> statusBarWindowControllerProvider, Provider<NotificationEntryManager> notificationEntryManagerProvider, Provider<MediaArtworkProcessor> mediaArtworkProcessorProvider, Provider<KeyguardBypassController> keyguardBypassControllerProvider) {
        return new NotificationMediaManager_Factory(contextProvider, shadeControllerProvider, statusBarWindowControllerProvider, notificationEntryManagerProvider, mediaArtworkProcessorProvider, keyguardBypassControllerProvider);
    }

    public static NotificationMediaManager newNotificationMediaManager(Context context, Lazy<ShadeController> shadeController, Lazy<StatusBarWindowController> statusBarWindowController, NotificationEntryManager notificationEntryManager, MediaArtworkProcessor mediaArtworkProcessor, KeyguardBypassController keyguardBypassController) {
        return new NotificationMediaManager(context, shadeController, statusBarWindowController, notificationEntryManager, mediaArtworkProcessor, keyguardBypassController);
    }
}
