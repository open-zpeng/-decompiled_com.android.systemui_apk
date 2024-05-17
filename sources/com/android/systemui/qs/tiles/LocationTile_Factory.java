package com.android.systemui.qs.tiles;

import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.statusbar.policy.LocationController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class LocationTile_Factory implements Factory<LocationTile> {
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<QSHost> hostProvider;
    private final Provider<KeyguardMonitor> keyguardMonitorProvider;
    private final Provider<LocationController> locationControllerProvider;

    public LocationTile_Factory(Provider<QSHost> hostProvider, Provider<LocationController> locationControllerProvider, Provider<KeyguardMonitor> keyguardMonitorProvider, Provider<ActivityStarter> activityStarterProvider) {
        this.hostProvider = hostProvider;
        this.locationControllerProvider = locationControllerProvider;
        this.keyguardMonitorProvider = keyguardMonitorProvider;
        this.activityStarterProvider = activityStarterProvider;
    }

    @Override // javax.inject.Provider
    public LocationTile get() {
        return provideInstance(this.hostProvider, this.locationControllerProvider, this.keyguardMonitorProvider, this.activityStarterProvider);
    }

    public static LocationTile provideInstance(Provider<QSHost> hostProvider, Provider<LocationController> locationControllerProvider, Provider<KeyguardMonitor> keyguardMonitorProvider, Provider<ActivityStarter> activityStarterProvider) {
        return new LocationTile(hostProvider.get(), locationControllerProvider.get(), keyguardMonitorProvider.get(), activityStarterProvider.get());
    }

    public static LocationTile_Factory create(Provider<QSHost> hostProvider, Provider<LocationController> locationControllerProvider, Provider<KeyguardMonitor> keyguardMonitorProvider, Provider<ActivityStarter> activityStarterProvider) {
        return new LocationTile_Factory(hostProvider, locationControllerProvider, keyguardMonitorProvider, activityStarterProvider);
    }

    public static LocationTile newLocationTile(QSHost host, LocationController locationController, KeyguardMonitor keyguardMonitor, ActivityStarter activityStarter) {
        return new LocationTile(host, locationController, keyguardMonitor, activityStarter);
    }
}
