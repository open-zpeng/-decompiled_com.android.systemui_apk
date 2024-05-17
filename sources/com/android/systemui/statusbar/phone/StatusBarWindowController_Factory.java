package com.android.systemui.statusbar.phone;

import android.content.Context;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class StatusBarWindowController_Factory implements Factory<StatusBarWindowController> {
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<KeyguardBypassController> keyguardBypassControllerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;

    public StatusBarWindowController_Factory(Provider<Context> contextProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<ConfigurationController> configurationControllerProvider, Provider<KeyguardBypassController> keyguardBypassControllerProvider) {
        this.contextProvider = contextProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.configurationControllerProvider = configurationControllerProvider;
        this.keyguardBypassControllerProvider = keyguardBypassControllerProvider;
    }

    @Override // javax.inject.Provider
    public StatusBarWindowController get() {
        return provideInstance(this.contextProvider, this.statusBarStateControllerProvider, this.configurationControllerProvider, this.keyguardBypassControllerProvider);
    }

    public static StatusBarWindowController provideInstance(Provider<Context> contextProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<ConfigurationController> configurationControllerProvider, Provider<KeyguardBypassController> keyguardBypassControllerProvider) {
        return new StatusBarWindowController(contextProvider.get(), statusBarStateControllerProvider.get(), configurationControllerProvider.get(), keyguardBypassControllerProvider.get());
    }

    public static StatusBarWindowController_Factory create(Provider<Context> contextProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<ConfigurationController> configurationControllerProvider, Provider<KeyguardBypassController> keyguardBypassControllerProvider) {
        return new StatusBarWindowController_Factory(contextProvider, statusBarStateControllerProvider, configurationControllerProvider, keyguardBypassControllerProvider);
    }

    public static StatusBarWindowController newStatusBarWindowController(Context context, StatusBarStateController statusBarStateController, ConfigurationController configurationController, KeyguardBypassController keyguardBypassController) {
        return new StatusBarWindowController(context, statusBarStateController, configurationController, keyguardBypassController);
    }
}
