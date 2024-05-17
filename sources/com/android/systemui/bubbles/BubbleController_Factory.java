package com.android.systemui.bubbles;

import android.content.Context;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.notification.NotificationInterruptionStateProvider;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.StatusBarWindowController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ZenModeController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class BubbleController_Factory implements Factory<BubbleController> {
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<BubbleData> dataProvider;
    private final Provider<NotificationGroupManager> groupManagerProvider;
    private final Provider<NotificationInterruptionStateProvider> interruptionStateProvider;
    private final Provider<NotificationLockscreenUserManager> notifUserManagerProvider;
    private final Provider<StatusBarWindowController> statusBarWindowControllerProvider;
    private final Provider<ZenModeController> zenModeControllerProvider;

    public BubbleController_Factory(Provider<Context> contextProvider, Provider<StatusBarWindowController> statusBarWindowControllerProvider, Provider<BubbleData> dataProvider, Provider<ConfigurationController> configurationControllerProvider, Provider<NotificationInterruptionStateProvider> interruptionStateProvider, Provider<ZenModeController> zenModeControllerProvider, Provider<NotificationLockscreenUserManager> notifUserManagerProvider, Provider<NotificationGroupManager> groupManagerProvider) {
        this.contextProvider = contextProvider;
        this.statusBarWindowControllerProvider = statusBarWindowControllerProvider;
        this.dataProvider = dataProvider;
        this.configurationControllerProvider = configurationControllerProvider;
        this.interruptionStateProvider = interruptionStateProvider;
        this.zenModeControllerProvider = zenModeControllerProvider;
        this.notifUserManagerProvider = notifUserManagerProvider;
        this.groupManagerProvider = groupManagerProvider;
    }

    @Override // javax.inject.Provider
    public BubbleController get() {
        return provideInstance(this.contextProvider, this.statusBarWindowControllerProvider, this.dataProvider, this.configurationControllerProvider, this.interruptionStateProvider, this.zenModeControllerProvider, this.notifUserManagerProvider, this.groupManagerProvider);
    }

    public static BubbleController provideInstance(Provider<Context> contextProvider, Provider<StatusBarWindowController> statusBarWindowControllerProvider, Provider<BubbleData> dataProvider, Provider<ConfigurationController> configurationControllerProvider, Provider<NotificationInterruptionStateProvider> interruptionStateProvider, Provider<ZenModeController> zenModeControllerProvider, Provider<NotificationLockscreenUserManager> notifUserManagerProvider, Provider<NotificationGroupManager> groupManagerProvider) {
        return new BubbleController(contextProvider.get(), statusBarWindowControllerProvider.get(), dataProvider.get(), configurationControllerProvider.get(), interruptionStateProvider.get(), zenModeControllerProvider.get(), notifUserManagerProvider.get(), groupManagerProvider.get());
    }

    public static BubbleController_Factory create(Provider<Context> contextProvider, Provider<StatusBarWindowController> statusBarWindowControllerProvider, Provider<BubbleData> dataProvider, Provider<ConfigurationController> configurationControllerProvider, Provider<NotificationInterruptionStateProvider> interruptionStateProvider, Provider<ZenModeController> zenModeControllerProvider, Provider<NotificationLockscreenUserManager> notifUserManagerProvider, Provider<NotificationGroupManager> groupManagerProvider) {
        return new BubbleController_Factory(contextProvider, statusBarWindowControllerProvider, dataProvider, configurationControllerProvider, interruptionStateProvider, zenModeControllerProvider, notifUserManagerProvider, groupManagerProvider);
    }

    public static BubbleController newBubbleController(Context context, StatusBarWindowController statusBarWindowController, BubbleData data, ConfigurationController configurationController, NotificationInterruptionStateProvider interruptionStateProvider, ZenModeController zenModeController, NotificationLockscreenUserManager notifUserManager, NotificationGroupManager groupManager) {
        return new BubbleController(context, statusBarWindowController, data, configurationController, interruptionStateProvider, zenModeController, notifUserManager, groupManager);
    }
}
