package com.android.systemui.statusbar.policy;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class RemoteInputQuickSettingsDisabler_Factory implements Factory<RemoteInputQuickSettingsDisabler> {
    private final Provider<ConfigurationController> configControllerProvider;
    private final Provider<Context> contextProvider;

    public RemoteInputQuickSettingsDisabler_Factory(Provider<Context> contextProvider, Provider<ConfigurationController> configControllerProvider) {
        this.contextProvider = contextProvider;
        this.configControllerProvider = configControllerProvider;
    }

    @Override // javax.inject.Provider
    public RemoteInputQuickSettingsDisabler get() {
        return provideInstance(this.contextProvider, this.configControllerProvider);
    }

    public static RemoteInputQuickSettingsDisabler provideInstance(Provider<Context> contextProvider, Provider<ConfigurationController> configControllerProvider) {
        return new RemoteInputQuickSettingsDisabler(contextProvider.get(), configControllerProvider.get());
    }

    public static RemoteInputQuickSettingsDisabler_Factory create(Provider<Context> contextProvider, Provider<ConfigurationController> configControllerProvider) {
        return new RemoteInputQuickSettingsDisabler_Factory(contextProvider, configControllerProvider);
    }

    public static RemoteInputQuickSettingsDisabler newRemoteInputQuickSettingsDisabler(Context context, ConfigurationController configController) {
        return new RemoteInputQuickSettingsDisabler(context, configController);
    }
}
