package com.android.systemui.qs.tiles;

import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class UiModeNightTile_Factory implements Factory<UiModeNightTile> {
    private final Provider<BatteryController> batteryControllerProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<QSHost> hostProvider;

    public UiModeNightTile_Factory(Provider<QSHost> hostProvider, Provider<ConfigurationController> configurationControllerProvider, Provider<BatteryController> batteryControllerProvider) {
        this.hostProvider = hostProvider;
        this.configurationControllerProvider = configurationControllerProvider;
        this.batteryControllerProvider = batteryControllerProvider;
    }

    @Override // javax.inject.Provider
    public UiModeNightTile get() {
        return provideInstance(this.hostProvider, this.configurationControllerProvider, this.batteryControllerProvider);
    }

    public static UiModeNightTile provideInstance(Provider<QSHost> hostProvider, Provider<ConfigurationController> configurationControllerProvider, Provider<BatteryController> batteryControllerProvider) {
        return new UiModeNightTile(hostProvider.get(), configurationControllerProvider.get(), batteryControllerProvider.get());
    }

    public static UiModeNightTile_Factory create(Provider<QSHost> hostProvider, Provider<ConfigurationController> configurationControllerProvider, Provider<BatteryController> batteryControllerProvider) {
        return new UiModeNightTile_Factory(hostProvider, configurationControllerProvider, batteryControllerProvider);
    }

    public static UiModeNightTile newUiModeNightTile(QSHost host, ConfigurationController configurationController, BatteryController batteryController) {
        return new UiModeNightTile(host, configurationController, batteryController);
    }
}
