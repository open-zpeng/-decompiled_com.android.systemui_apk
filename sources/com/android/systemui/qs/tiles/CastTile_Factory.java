package com.android.systemui.qs.tiles;

import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.statusbar.policy.NetworkController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class CastTile_Factory implements Factory<CastTile> {
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<CastController> castControllerProvider;
    private final Provider<QSHost> hostProvider;
    private final Provider<KeyguardMonitor> keyguardMonitorProvider;
    private final Provider<NetworkController> networkControllerProvider;

    public CastTile_Factory(Provider<QSHost> hostProvider, Provider<CastController> castControllerProvider, Provider<KeyguardMonitor> keyguardMonitorProvider, Provider<NetworkController> networkControllerProvider, Provider<ActivityStarter> activityStarterProvider) {
        this.hostProvider = hostProvider;
        this.castControllerProvider = castControllerProvider;
        this.keyguardMonitorProvider = keyguardMonitorProvider;
        this.networkControllerProvider = networkControllerProvider;
        this.activityStarterProvider = activityStarterProvider;
    }

    @Override // javax.inject.Provider
    public CastTile get() {
        return provideInstance(this.hostProvider, this.castControllerProvider, this.keyguardMonitorProvider, this.networkControllerProvider, this.activityStarterProvider);
    }

    public static CastTile provideInstance(Provider<QSHost> hostProvider, Provider<CastController> castControllerProvider, Provider<KeyguardMonitor> keyguardMonitorProvider, Provider<NetworkController> networkControllerProvider, Provider<ActivityStarter> activityStarterProvider) {
        return new CastTile(hostProvider.get(), castControllerProvider.get(), keyguardMonitorProvider.get(), networkControllerProvider.get(), activityStarterProvider.get());
    }

    public static CastTile_Factory create(Provider<QSHost> hostProvider, Provider<CastController> castControllerProvider, Provider<KeyguardMonitor> keyguardMonitorProvider, Provider<NetworkController> networkControllerProvider, Provider<ActivityStarter> activityStarterProvider) {
        return new CastTile_Factory(hostProvider, castControllerProvider, keyguardMonitorProvider, networkControllerProvider, activityStarterProvider);
    }

    public static CastTile newCastTile(QSHost host, CastController castController, KeyguardMonitor keyguardMonitor, NetworkController networkController, ActivityStarter activityStarter) {
        return new CastTile(host, castController, keyguardMonitor, networkController, activityStarter);
    }
}
