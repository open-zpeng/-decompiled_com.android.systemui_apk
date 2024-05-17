package com.android.systemui.statusbar;

import android.content.Context;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.notification.stack.NotificationRoundnessManager;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class PulseExpansionHandler_Factory implements Factory<PulseExpansionHandler> {
    private final Provider<KeyguardBypassController> bypassControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<HeadsUpManagerPhone> headsUpManagerProvider;
    private final Provider<NotificationRoundnessManager> roundnessManagerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<NotificationWakeUpCoordinator> wakeUpCoordinatorProvider;

    public PulseExpansionHandler_Factory(Provider<Context> contextProvider, Provider<NotificationWakeUpCoordinator> wakeUpCoordinatorProvider, Provider<KeyguardBypassController> bypassControllerProvider, Provider<HeadsUpManagerPhone> headsUpManagerProvider, Provider<NotificationRoundnessManager> roundnessManagerProvider, Provider<StatusBarStateController> statusBarStateControllerProvider) {
        this.contextProvider = contextProvider;
        this.wakeUpCoordinatorProvider = wakeUpCoordinatorProvider;
        this.bypassControllerProvider = bypassControllerProvider;
        this.headsUpManagerProvider = headsUpManagerProvider;
        this.roundnessManagerProvider = roundnessManagerProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
    }

    @Override // javax.inject.Provider
    public PulseExpansionHandler get() {
        return provideInstance(this.contextProvider, this.wakeUpCoordinatorProvider, this.bypassControllerProvider, this.headsUpManagerProvider, this.roundnessManagerProvider, this.statusBarStateControllerProvider);
    }

    public static PulseExpansionHandler provideInstance(Provider<Context> contextProvider, Provider<NotificationWakeUpCoordinator> wakeUpCoordinatorProvider, Provider<KeyguardBypassController> bypassControllerProvider, Provider<HeadsUpManagerPhone> headsUpManagerProvider, Provider<NotificationRoundnessManager> roundnessManagerProvider, Provider<StatusBarStateController> statusBarStateControllerProvider) {
        return new PulseExpansionHandler(contextProvider.get(), wakeUpCoordinatorProvider.get(), bypassControllerProvider.get(), headsUpManagerProvider.get(), roundnessManagerProvider.get(), statusBarStateControllerProvider.get());
    }

    public static PulseExpansionHandler_Factory create(Provider<Context> contextProvider, Provider<NotificationWakeUpCoordinator> wakeUpCoordinatorProvider, Provider<KeyguardBypassController> bypassControllerProvider, Provider<HeadsUpManagerPhone> headsUpManagerProvider, Provider<NotificationRoundnessManager> roundnessManagerProvider, Provider<StatusBarStateController> statusBarStateControllerProvider) {
        return new PulseExpansionHandler_Factory(contextProvider, wakeUpCoordinatorProvider, bypassControllerProvider, headsUpManagerProvider, roundnessManagerProvider, statusBarStateControllerProvider);
    }

    public static PulseExpansionHandler newPulseExpansionHandler(Context context, NotificationWakeUpCoordinator wakeUpCoordinator, KeyguardBypassController bypassController, HeadsUpManagerPhone headsUpManager, NotificationRoundnessManager roundnessManager, StatusBarStateController statusBarStateController) {
        return new PulseExpansionHandler(context, wakeUpCoordinator, bypassController, headsUpManager, roundnessManager, statusBarStateController);
    }
}
