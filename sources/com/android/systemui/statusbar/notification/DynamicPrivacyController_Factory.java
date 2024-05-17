package com.android.systemui.statusbar.notification;

import android.content.Context;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class DynamicPrivacyController_Factory implements Factory<DynamicPrivacyController> {
    private final Provider<Context> contextProvider;
    private final Provider<KeyguardMonitor> keyguardMonitorProvider;
    private final Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider;
    private final Provider<StatusBarStateController> stateControllerProvider;

    public DynamicPrivacyController_Factory(Provider<Context> contextProvider, Provider<KeyguardMonitor> keyguardMonitorProvider, Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider, Provider<StatusBarStateController> stateControllerProvider) {
        this.contextProvider = contextProvider;
        this.keyguardMonitorProvider = keyguardMonitorProvider;
        this.notificationLockscreenUserManagerProvider = notificationLockscreenUserManagerProvider;
        this.stateControllerProvider = stateControllerProvider;
    }

    @Override // javax.inject.Provider
    public DynamicPrivacyController get() {
        return provideInstance(this.contextProvider, this.keyguardMonitorProvider, this.notificationLockscreenUserManagerProvider, this.stateControllerProvider);
    }

    public static DynamicPrivacyController provideInstance(Provider<Context> contextProvider, Provider<KeyguardMonitor> keyguardMonitorProvider, Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider, Provider<StatusBarStateController> stateControllerProvider) {
        return new DynamicPrivacyController(contextProvider.get(), keyguardMonitorProvider.get(), notificationLockscreenUserManagerProvider.get(), stateControllerProvider.get());
    }

    public static DynamicPrivacyController_Factory create(Provider<Context> contextProvider, Provider<KeyguardMonitor> keyguardMonitorProvider, Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider, Provider<StatusBarStateController> stateControllerProvider) {
        return new DynamicPrivacyController_Factory(contextProvider, keyguardMonitorProvider, notificationLockscreenUserManagerProvider, stateControllerProvider);
    }

    public static DynamicPrivacyController newDynamicPrivacyController(Context context, KeyguardMonitor keyguardMonitor, NotificationLockscreenUserManager notificationLockscreenUserManager, StatusBarStateController stateController) {
        return new DynamicPrivacyController(context, keyguardMonitor, notificationLockscreenUserManager, stateController);
    }
}
