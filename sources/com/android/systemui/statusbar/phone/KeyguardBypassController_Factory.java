package com.android.systemui.statusbar.phone;

import android.content.Context;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.tuner.TunerService;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class KeyguardBypassController_Factory implements Factory<KeyguardBypassController> {
    private final Provider<Context> contextProvider;
    private final Provider<NotificationLockscreenUserManager> lockscreenUserManagerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<TunerService> tunerServiceProvider;

    public KeyguardBypassController_Factory(Provider<Context> contextProvider, Provider<TunerService> tunerServiceProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<NotificationLockscreenUserManager> lockscreenUserManagerProvider) {
        this.contextProvider = contextProvider;
        this.tunerServiceProvider = tunerServiceProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.lockscreenUserManagerProvider = lockscreenUserManagerProvider;
    }

    @Override // javax.inject.Provider
    public KeyguardBypassController get() {
        return provideInstance(this.contextProvider, this.tunerServiceProvider, this.statusBarStateControllerProvider, this.lockscreenUserManagerProvider);
    }

    public static KeyguardBypassController provideInstance(Provider<Context> contextProvider, Provider<TunerService> tunerServiceProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<NotificationLockscreenUserManager> lockscreenUserManagerProvider) {
        return new KeyguardBypassController(contextProvider.get(), tunerServiceProvider.get(), statusBarStateControllerProvider.get(), lockscreenUserManagerProvider.get());
    }

    public static KeyguardBypassController_Factory create(Provider<Context> contextProvider, Provider<TunerService> tunerServiceProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<NotificationLockscreenUserManager> lockscreenUserManagerProvider) {
        return new KeyguardBypassController_Factory(contextProvider, tunerServiceProvider, statusBarStateControllerProvider, lockscreenUserManagerProvider);
    }

    public static KeyguardBypassController newKeyguardBypassController(Context context, TunerService tunerService, StatusBarStateController statusBarStateController, NotificationLockscreenUserManager lockscreenUserManager) {
        return new KeyguardBypassController(context, tunerService, statusBarStateController, lockscreenUserManager);
    }
}
