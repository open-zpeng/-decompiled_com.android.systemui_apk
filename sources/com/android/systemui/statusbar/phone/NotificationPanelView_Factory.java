package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.util.AttributeSet;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.PulseExpansionHandler;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.util.InjectionInflationController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class NotificationPanelView_Factory implements Factory<NotificationPanelView> {
    private final Provider<AttributeSet> attrsProvider;
    private final Provider<KeyguardBypassController> bypassControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<NotificationWakeUpCoordinator> coordinatorProvider;
    private final Provider<DynamicPrivacyController> dynamicPrivacyControllerProvider;
    private final Provider<FalsingManager> falsingManagerProvider;
    private final Provider<InjectionInflationController> injectionInflationControllerProvider;
    private final Provider<PulseExpansionHandler> pulseExpansionHandlerProvider;

    public NotificationPanelView_Factory(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<InjectionInflationController> injectionInflationControllerProvider, Provider<NotificationWakeUpCoordinator> coordinatorProvider, Provider<PulseExpansionHandler> pulseExpansionHandlerProvider, Provider<DynamicPrivacyController> dynamicPrivacyControllerProvider, Provider<KeyguardBypassController> bypassControllerProvider, Provider<FalsingManager> falsingManagerProvider) {
        this.contextProvider = contextProvider;
        this.attrsProvider = attrsProvider;
        this.injectionInflationControllerProvider = injectionInflationControllerProvider;
        this.coordinatorProvider = coordinatorProvider;
        this.pulseExpansionHandlerProvider = pulseExpansionHandlerProvider;
        this.dynamicPrivacyControllerProvider = dynamicPrivacyControllerProvider;
        this.bypassControllerProvider = bypassControllerProvider;
        this.falsingManagerProvider = falsingManagerProvider;
    }

    @Override // javax.inject.Provider
    public NotificationPanelView get() {
        return provideInstance(this.contextProvider, this.attrsProvider, this.injectionInflationControllerProvider, this.coordinatorProvider, this.pulseExpansionHandlerProvider, this.dynamicPrivacyControllerProvider, this.bypassControllerProvider, this.falsingManagerProvider);
    }

    public static NotificationPanelView provideInstance(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<InjectionInflationController> injectionInflationControllerProvider, Provider<NotificationWakeUpCoordinator> coordinatorProvider, Provider<PulseExpansionHandler> pulseExpansionHandlerProvider, Provider<DynamicPrivacyController> dynamicPrivacyControllerProvider, Provider<KeyguardBypassController> bypassControllerProvider, Provider<FalsingManager> falsingManagerProvider) {
        return new NotificationPanelView(contextProvider.get(), attrsProvider.get(), injectionInflationControllerProvider.get(), coordinatorProvider.get(), pulseExpansionHandlerProvider.get(), dynamicPrivacyControllerProvider.get(), bypassControllerProvider.get(), falsingManagerProvider.get());
    }

    public static NotificationPanelView_Factory create(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<InjectionInflationController> injectionInflationControllerProvider, Provider<NotificationWakeUpCoordinator> coordinatorProvider, Provider<PulseExpansionHandler> pulseExpansionHandlerProvider, Provider<DynamicPrivacyController> dynamicPrivacyControllerProvider, Provider<KeyguardBypassController> bypassControllerProvider, Provider<FalsingManager> falsingManagerProvider) {
        return new NotificationPanelView_Factory(contextProvider, attrsProvider, injectionInflationControllerProvider, coordinatorProvider, pulseExpansionHandlerProvider, dynamicPrivacyControllerProvider, bypassControllerProvider, falsingManagerProvider);
    }

    public static NotificationPanelView newNotificationPanelView(Context context, AttributeSet attrs, InjectionInflationController injectionInflationController, NotificationWakeUpCoordinator coordinator, PulseExpansionHandler pulseExpansionHandler, DynamicPrivacyController dynamicPrivacyController, KeyguardBypassController bypassController, FalsingManager falsingManager) {
        return new NotificationPanelView(context, attrs, injectionInflationController, coordinator, pulseExpansionHandler, dynamicPrivacyController, bypassController, falsingManager);
    }
}
