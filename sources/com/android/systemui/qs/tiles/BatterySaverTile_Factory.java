package com.android.systemui.qs.tiles;

import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.BatteryController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class BatterySaverTile_Factory implements Factory<BatterySaverTile> {
    private final Provider<BatteryController> batteryControllerProvider;
    private final Provider<QSHost> hostProvider;

    public BatterySaverTile_Factory(Provider<QSHost> hostProvider, Provider<BatteryController> batteryControllerProvider) {
        this.hostProvider = hostProvider;
        this.batteryControllerProvider = batteryControllerProvider;
    }

    @Override // javax.inject.Provider
    public BatterySaverTile get() {
        return provideInstance(this.hostProvider, this.batteryControllerProvider);
    }

    public static BatterySaverTile provideInstance(Provider<QSHost> hostProvider, Provider<BatteryController> batteryControllerProvider) {
        return new BatterySaverTile(hostProvider.get(), batteryControllerProvider.get());
    }

    public static BatterySaverTile_Factory create(Provider<QSHost> hostProvider, Provider<BatteryController> batteryControllerProvider) {
        return new BatterySaverTile_Factory(hostProvider, batteryControllerProvider);
    }

    public static BatterySaverTile newBatterySaverTile(QSHost host, BatteryController batteryController) {
        return new BatterySaverTile(host, batteryController);
    }
}
