package com.android.systemui.statusbar.notification.stack;

import com.android.systemui.statusbar.phone.KeyguardBypassController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class NotificationRoundnessManager_Factory implements Factory<NotificationRoundnessManager> {
    private final Provider<KeyguardBypassController> keyguardBypassControllerProvider;

    public NotificationRoundnessManager_Factory(Provider<KeyguardBypassController> keyguardBypassControllerProvider) {
        this.keyguardBypassControllerProvider = keyguardBypassControllerProvider;
    }

    @Override // javax.inject.Provider
    public NotificationRoundnessManager get() {
        return provideInstance(this.keyguardBypassControllerProvider);
    }

    public static NotificationRoundnessManager provideInstance(Provider<KeyguardBypassController> keyguardBypassControllerProvider) {
        return new NotificationRoundnessManager(keyguardBypassControllerProvider.get());
    }

    public static NotificationRoundnessManager_Factory create(Provider<KeyguardBypassController> keyguardBypassControllerProvider) {
        return new NotificationRoundnessManager_Factory(keyguardBypassControllerProvider);
    }

    public static NotificationRoundnessManager newNotificationRoundnessManager(KeyguardBypassController keyguardBypassController) {
        return new NotificationRoundnessManager(keyguardBypassController);
    }
}
