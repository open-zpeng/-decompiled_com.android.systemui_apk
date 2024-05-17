package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.util.AttributeSet;
import com.android.systemui.dock.DockManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.policy.AccessibilityController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class LockIcon_Factory implements Factory<LockIcon> {
    private final Provider<AccessibilityController> accessibilityControllerProvider;
    private final Provider<AttributeSet> attrsProvider;
    private final Provider<KeyguardBypassController> bypassControllerProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DockManager> dockManagerProvider;
    private final Provider<HeadsUpManagerPhone> headsUpManagerProvider;
    private final Provider<KeyguardMonitor> keyguardMonitorProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<NotificationWakeUpCoordinator> wakeUpCoordinatorProvider;

    public LockIcon_Factory(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<ConfigurationController> configurationControllerProvider, Provider<AccessibilityController> accessibilityControllerProvider, Provider<KeyguardBypassController> bypassControllerProvider, Provider<NotificationWakeUpCoordinator> wakeUpCoordinatorProvider, Provider<KeyguardMonitor> keyguardMonitorProvider, Provider<DockManager> dockManagerProvider, Provider<HeadsUpManagerPhone> headsUpManagerProvider) {
        this.contextProvider = contextProvider;
        this.attrsProvider = attrsProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.configurationControllerProvider = configurationControllerProvider;
        this.accessibilityControllerProvider = accessibilityControllerProvider;
        this.bypassControllerProvider = bypassControllerProvider;
        this.wakeUpCoordinatorProvider = wakeUpCoordinatorProvider;
        this.keyguardMonitorProvider = keyguardMonitorProvider;
        this.dockManagerProvider = dockManagerProvider;
        this.headsUpManagerProvider = headsUpManagerProvider;
    }

    @Override // javax.inject.Provider
    public LockIcon get() {
        return provideInstance(this.contextProvider, this.attrsProvider, this.statusBarStateControllerProvider, this.configurationControllerProvider, this.accessibilityControllerProvider, this.bypassControllerProvider, this.wakeUpCoordinatorProvider, this.keyguardMonitorProvider, this.dockManagerProvider, this.headsUpManagerProvider);
    }

    public static LockIcon provideInstance(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<ConfigurationController> configurationControllerProvider, Provider<AccessibilityController> accessibilityControllerProvider, Provider<KeyguardBypassController> bypassControllerProvider, Provider<NotificationWakeUpCoordinator> wakeUpCoordinatorProvider, Provider<KeyguardMonitor> keyguardMonitorProvider, Provider<DockManager> dockManagerProvider, Provider<HeadsUpManagerPhone> headsUpManagerProvider) {
        return new LockIcon(contextProvider.get(), attrsProvider.get(), statusBarStateControllerProvider.get(), configurationControllerProvider.get(), accessibilityControllerProvider.get(), bypassControllerProvider.get(), wakeUpCoordinatorProvider.get(), keyguardMonitorProvider.get(), dockManagerProvider.get(), headsUpManagerProvider.get());
    }

    public static LockIcon_Factory create(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<ConfigurationController> configurationControllerProvider, Provider<AccessibilityController> accessibilityControllerProvider, Provider<KeyguardBypassController> bypassControllerProvider, Provider<NotificationWakeUpCoordinator> wakeUpCoordinatorProvider, Provider<KeyguardMonitor> keyguardMonitorProvider, Provider<DockManager> dockManagerProvider, Provider<HeadsUpManagerPhone> headsUpManagerProvider) {
        return new LockIcon_Factory(contextProvider, attrsProvider, statusBarStateControllerProvider, configurationControllerProvider, accessibilityControllerProvider, bypassControllerProvider, wakeUpCoordinatorProvider, keyguardMonitorProvider, dockManagerProvider, headsUpManagerProvider);
    }

    public static LockIcon newLockIcon(Context context, AttributeSet attrs, StatusBarStateController statusBarStateController, ConfigurationController configurationController, AccessibilityController accessibilityController, KeyguardBypassController bypassController, NotificationWakeUpCoordinator wakeUpCoordinator, KeyguardMonitor keyguardMonitor, DockManager dockManager, HeadsUpManagerPhone headsUpManager) {
        return new LockIcon(context, attrs, statusBarStateController, configurationController, accessibilityController, bypassController, wakeUpCoordinator, keyguardMonitor, dockManager, headsUpManager);
    }
}
