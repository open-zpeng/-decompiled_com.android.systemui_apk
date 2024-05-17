package com.android.systemui.statusbar.notification;

import android.content.Context;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class NotificationWakeUpCoordinator_Factory implements Factory<NotificationWakeUpCoordinator> {
    private final Provider<KeyguardBypassController> bypassControllerProvider;
    private final Provider<Context> mContextProvider;
    private final Provider<HeadsUpManagerPhone> mHeadsUpManagerPhoneProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;

    public NotificationWakeUpCoordinator_Factory(Provider<Context> mContextProvider, Provider<HeadsUpManagerPhone> mHeadsUpManagerPhoneProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<KeyguardBypassController> bypassControllerProvider) {
        this.mContextProvider = mContextProvider;
        this.mHeadsUpManagerPhoneProvider = mHeadsUpManagerPhoneProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.bypassControllerProvider = bypassControllerProvider;
    }

    @Override // javax.inject.Provider
    public NotificationWakeUpCoordinator get() {
        return provideInstance(this.mContextProvider, this.mHeadsUpManagerPhoneProvider, this.statusBarStateControllerProvider, this.bypassControllerProvider);
    }

    public static NotificationWakeUpCoordinator provideInstance(Provider<Context> mContextProvider, Provider<HeadsUpManagerPhone> mHeadsUpManagerPhoneProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<KeyguardBypassController> bypassControllerProvider) {
        return new NotificationWakeUpCoordinator(mContextProvider.get(), mHeadsUpManagerPhoneProvider.get(), statusBarStateControllerProvider.get(), bypassControllerProvider.get());
    }

    public static NotificationWakeUpCoordinator_Factory create(Provider<Context> mContextProvider, Provider<HeadsUpManagerPhone> mHeadsUpManagerPhoneProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<KeyguardBypassController> bypassControllerProvider) {
        return new NotificationWakeUpCoordinator_Factory(mContextProvider, mHeadsUpManagerPhoneProvider, statusBarStateControllerProvider, bypassControllerProvider);
    }

    public static NotificationWakeUpCoordinator newNotificationWakeUpCoordinator(Context mContext, HeadsUpManagerPhone mHeadsUpManagerPhone, StatusBarStateController statusBarStateController, KeyguardBypassController bypassController) {
        return new NotificationWakeUpCoordinator(mContext, mHeadsUpManagerPhone, statusBarStateController, bypassController);
    }
}
