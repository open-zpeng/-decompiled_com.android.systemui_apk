package com.android.systemui.qs.tiles;

import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.NetworkController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class CellularTile_Factory implements Factory<CellularTile> {
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<QSHost> hostProvider;
    private final Provider<NetworkController> networkControllerProvider;

    public CellularTile_Factory(Provider<QSHost> hostProvider, Provider<NetworkController> networkControllerProvider, Provider<ActivityStarter> activityStarterProvider) {
        this.hostProvider = hostProvider;
        this.networkControllerProvider = networkControllerProvider;
        this.activityStarterProvider = activityStarterProvider;
    }

    @Override // javax.inject.Provider
    public CellularTile get() {
        return provideInstance(this.hostProvider, this.networkControllerProvider, this.activityStarterProvider);
    }

    public static CellularTile provideInstance(Provider<QSHost> hostProvider, Provider<NetworkController> networkControllerProvider, Provider<ActivityStarter> activityStarterProvider) {
        return new CellularTile(hostProvider.get(), networkControllerProvider.get(), activityStarterProvider.get());
    }

    public static CellularTile_Factory create(Provider<QSHost> hostProvider, Provider<NetworkController> networkControllerProvider, Provider<ActivityStarter> activityStarterProvider) {
        return new CellularTile_Factory(hostProvider, networkControllerProvider, activityStarterProvider);
    }

    public static CellularTile newCellularTile(QSHost host, NetworkController networkController, ActivityStarter activityStarter) {
        return new CellularTile(host, networkController, activityStarter);
    }
}
