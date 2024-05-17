package com.android.systemui.qs.tiles;

import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.HotspotController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class HotspotTile_Factory implements Factory<HotspotTile> {
    private final Provider<DataSaverController> dataSaverControllerProvider;
    private final Provider<QSHost> hostProvider;
    private final Provider<HotspotController> hotspotControllerProvider;

    public HotspotTile_Factory(Provider<QSHost> hostProvider, Provider<HotspotController> hotspotControllerProvider, Provider<DataSaverController> dataSaverControllerProvider) {
        this.hostProvider = hostProvider;
        this.hotspotControllerProvider = hotspotControllerProvider;
        this.dataSaverControllerProvider = dataSaverControllerProvider;
    }

    @Override // javax.inject.Provider
    public HotspotTile get() {
        return provideInstance(this.hostProvider, this.hotspotControllerProvider, this.dataSaverControllerProvider);
    }

    public static HotspotTile provideInstance(Provider<QSHost> hostProvider, Provider<HotspotController> hotspotControllerProvider, Provider<DataSaverController> dataSaverControllerProvider) {
        return new HotspotTile(hostProvider.get(), hotspotControllerProvider.get(), dataSaverControllerProvider.get());
    }

    public static HotspotTile_Factory create(Provider<QSHost> hostProvider, Provider<HotspotController> hotspotControllerProvider, Provider<DataSaverController> dataSaverControllerProvider) {
        return new HotspotTile_Factory(hostProvider, hotspotControllerProvider, dataSaverControllerProvider);
    }

    public static HotspotTile newHotspotTile(QSHost host, HotspotController hotspotController, DataSaverController dataSaverController) {
        return new HotspotTile(host, hotspotController, dataSaverController);
    }
}
