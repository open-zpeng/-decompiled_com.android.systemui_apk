package com.android.systemui.statusbar.notification;

import android.content.Context;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.policy.BatteryController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class NotificationInterruptionStateProvider_Factory implements Factory<NotificationInterruptionStateProvider> {
    private final Provider<BatteryController> batteryControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<NotificationFilter> filterProvider;
    private final Provider<StatusBarStateController> stateControllerProvider;

    public NotificationInterruptionStateProvider_Factory(Provider<Context> contextProvider, Provider<NotificationFilter> filterProvider, Provider<StatusBarStateController> stateControllerProvider, Provider<BatteryController> batteryControllerProvider) {
        this.contextProvider = contextProvider;
        this.filterProvider = filterProvider;
        this.stateControllerProvider = stateControllerProvider;
        this.batteryControllerProvider = batteryControllerProvider;
    }

    @Override // javax.inject.Provider
    public NotificationInterruptionStateProvider get() {
        return provideInstance(this.contextProvider, this.filterProvider, this.stateControllerProvider, this.batteryControllerProvider);
    }

    public static NotificationInterruptionStateProvider provideInstance(Provider<Context> contextProvider, Provider<NotificationFilter> filterProvider, Provider<StatusBarStateController> stateControllerProvider, Provider<BatteryController> batteryControllerProvider) {
        return new NotificationInterruptionStateProvider(contextProvider.get(), filterProvider.get(), stateControllerProvider.get(), batteryControllerProvider.get());
    }

    public static NotificationInterruptionStateProvider_Factory create(Provider<Context> contextProvider, Provider<NotificationFilter> filterProvider, Provider<StatusBarStateController> stateControllerProvider, Provider<BatteryController> batteryControllerProvider) {
        return new NotificationInterruptionStateProvider_Factory(contextProvider, filterProvider, stateControllerProvider, batteryControllerProvider);
    }

    public static NotificationInterruptionStateProvider newNotificationInterruptionStateProvider(Context context, NotificationFilter filter, StatusBarStateController stateController, BatteryController batteryController) {
        return new NotificationInterruptionStateProvider(context, filter, stateController, batteryController);
    }
}
