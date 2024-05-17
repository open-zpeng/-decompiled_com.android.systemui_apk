package com.android.systemui.qs.tiles;

import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.NetworkController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class WifiTile_Factory implements Factory<WifiTile> {
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<QSHost> hostProvider;
    private final Provider<NetworkController> networkControllerProvider;

    public WifiTile_Factory(Provider<QSHost> hostProvider, Provider<NetworkController> networkControllerProvider, Provider<ActivityStarter> activityStarterProvider) {
        this.hostProvider = hostProvider;
        this.networkControllerProvider = networkControllerProvider;
        this.activityStarterProvider = activityStarterProvider;
    }

    @Override // javax.inject.Provider
    public WifiTile get() {
        return provideInstance(this.hostProvider, this.networkControllerProvider, this.activityStarterProvider);
    }

    public static WifiTile provideInstance(Provider<QSHost> hostProvider, Provider<NetworkController> networkControllerProvider, Provider<ActivityStarter> activityStarterProvider) {
        return new WifiTile(hostProvider.get(), networkControllerProvider.get(), activityStarterProvider.get());
    }

    public static WifiTile_Factory create(Provider<QSHost> hostProvider, Provider<NetworkController> networkControllerProvider, Provider<ActivityStarter> activityStarterProvider) {
        return new WifiTile_Factory(hostProvider, networkControllerProvider, activityStarterProvider);
    }

    public static WifiTile newWifiTile(QSHost host, NetworkController networkController, ActivityStarter activityStarter) {
        return new WifiTile(host, networkController, activityStarter);
    }
}
