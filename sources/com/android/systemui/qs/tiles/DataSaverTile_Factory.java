package com.android.systemui.qs.tiles;

import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.NetworkController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class DataSaverTile_Factory implements Factory<DataSaverTile> {
    private final Provider<QSHost> hostProvider;
    private final Provider<NetworkController> networkControllerProvider;

    public DataSaverTile_Factory(Provider<QSHost> hostProvider, Provider<NetworkController> networkControllerProvider) {
        this.hostProvider = hostProvider;
        this.networkControllerProvider = networkControllerProvider;
    }

    @Override // javax.inject.Provider
    public DataSaverTile get() {
        return provideInstance(this.hostProvider, this.networkControllerProvider);
    }

    public static DataSaverTile provideInstance(Provider<QSHost> hostProvider, Provider<NetworkController> networkControllerProvider) {
        return new DataSaverTile(hostProvider.get(), networkControllerProvider.get());
    }

    public static DataSaverTile_Factory create(Provider<QSHost> hostProvider, Provider<NetworkController> networkControllerProvider) {
        return new DataSaverTile_Factory(hostProvider, networkControllerProvider);
    }

    public static DataSaverTile newDataSaverTile(QSHost host, NetworkController networkController) {
        return new DataSaverTile(host, networkController);
    }
}
