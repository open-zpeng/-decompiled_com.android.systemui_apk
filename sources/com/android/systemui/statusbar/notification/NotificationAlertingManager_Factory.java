package com.android.systemui.statusbar.notification;

import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.phone.ShadeController;
import dagger.Lazy;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class NotificationAlertingManager_Factory implements Factory<NotificationAlertingManager> {
    private final Provider<NotificationEntryManager> notificationEntryManagerProvider;
    private final Provider<NotificationInterruptionStateProvider> notificationInterruptionStateProvider;
    private final Provider<NotificationListener> notificationListenerProvider;
    private final Provider<NotificationRemoteInputManager> remoteInputManagerProvider;
    private final Provider<ShadeController> shadeControllerProvider;
    private final Provider<VisualStabilityManager> visualStabilityManagerProvider;

    public NotificationAlertingManager_Factory(Provider<NotificationEntryManager> notificationEntryManagerProvider, Provider<NotificationRemoteInputManager> remoteInputManagerProvider, Provider<VisualStabilityManager> visualStabilityManagerProvider, Provider<ShadeController> shadeControllerProvider, Provider<NotificationInterruptionStateProvider> notificationInterruptionStateProvider, Provider<NotificationListener> notificationListenerProvider) {
        this.notificationEntryManagerProvider = notificationEntryManagerProvider;
        this.remoteInputManagerProvider = remoteInputManagerProvider;
        this.visualStabilityManagerProvider = visualStabilityManagerProvider;
        this.shadeControllerProvider = shadeControllerProvider;
        this.notificationInterruptionStateProvider = notificationInterruptionStateProvider;
        this.notificationListenerProvider = notificationListenerProvider;
    }

    @Override // javax.inject.Provider
    public NotificationAlertingManager get() {
        return provideInstance(this.notificationEntryManagerProvider, this.remoteInputManagerProvider, this.visualStabilityManagerProvider, this.shadeControllerProvider, this.notificationInterruptionStateProvider, this.notificationListenerProvider);
    }

    public static NotificationAlertingManager provideInstance(Provider<NotificationEntryManager> notificationEntryManagerProvider, Provider<NotificationRemoteInputManager> remoteInputManagerProvider, Provider<VisualStabilityManager> visualStabilityManagerProvider, Provider<ShadeController> shadeControllerProvider, Provider<NotificationInterruptionStateProvider> notificationInterruptionStateProvider, Provider<NotificationListener> notificationListenerProvider) {
        return new NotificationAlertingManager(notificationEntryManagerProvider.get(), remoteInputManagerProvider.get(), visualStabilityManagerProvider.get(), DoubleCheck.lazy(shadeControllerProvider), notificationInterruptionStateProvider.get(), notificationListenerProvider.get());
    }

    public static NotificationAlertingManager_Factory create(Provider<NotificationEntryManager> notificationEntryManagerProvider, Provider<NotificationRemoteInputManager> remoteInputManagerProvider, Provider<VisualStabilityManager> visualStabilityManagerProvider, Provider<ShadeController> shadeControllerProvider, Provider<NotificationInterruptionStateProvider> notificationInterruptionStateProvider, Provider<NotificationListener> notificationListenerProvider) {
        return new NotificationAlertingManager_Factory(notificationEntryManagerProvider, remoteInputManagerProvider, visualStabilityManagerProvider, shadeControllerProvider, notificationInterruptionStateProvider, notificationListenerProvider);
    }

    public static NotificationAlertingManager newNotificationAlertingManager(NotificationEntryManager notificationEntryManager, NotificationRemoteInputManager remoteInputManager, VisualStabilityManager visualStabilityManager, Lazy<ShadeController> shadeController, NotificationInterruptionStateProvider notificationInterruptionStateProvider, NotificationListener notificationListener) {
        return new NotificationAlertingManager(notificationEntryManager, remoteInputManager, visualStabilityManager, shadeController, notificationInterruptionStateProvider, notificationListener);
    }
}
