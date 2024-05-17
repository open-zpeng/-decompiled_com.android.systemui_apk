package com.android.systemui.qs.tiles;

import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.BluetoothController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class BluetoothTile_Factory implements Factory<BluetoothTile> {
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<BluetoothController> bluetoothControllerProvider;
    private final Provider<QSHost> hostProvider;

    public BluetoothTile_Factory(Provider<QSHost> hostProvider, Provider<BluetoothController> bluetoothControllerProvider, Provider<ActivityStarter> activityStarterProvider) {
        this.hostProvider = hostProvider;
        this.bluetoothControllerProvider = bluetoothControllerProvider;
        this.activityStarterProvider = activityStarterProvider;
    }

    @Override // javax.inject.Provider
    public BluetoothTile get() {
        return provideInstance(this.hostProvider, this.bluetoothControllerProvider, this.activityStarterProvider);
    }

    public static BluetoothTile provideInstance(Provider<QSHost> hostProvider, Provider<BluetoothController> bluetoothControllerProvider, Provider<ActivityStarter> activityStarterProvider) {
        return new BluetoothTile(hostProvider.get(), bluetoothControllerProvider.get(), activityStarterProvider.get());
    }

    public static BluetoothTile_Factory create(Provider<QSHost> hostProvider, Provider<BluetoothController> bluetoothControllerProvider, Provider<ActivityStarter> activityStarterProvider) {
        return new BluetoothTile_Factory(hostProvider, bluetoothControllerProvider, activityStarterProvider);
    }

    public static BluetoothTile newBluetoothTile(QSHost host, BluetoothController bluetoothController, ActivityStarter activityStarter) {
        return new BluetoothTile(host, bluetoothController, activityStarter);
    }
}
