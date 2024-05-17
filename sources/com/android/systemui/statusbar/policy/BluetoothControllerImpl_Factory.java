package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.os.Looper;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class BluetoothControllerImpl_Factory implements Factory<BluetoothControllerImpl> {
    private final Provider<Looper> bgLooperProvider;
    private final Provider<Context> contextProvider;
    private final Provider<LocalBluetoothManager> localBluetoothManagerProvider;

    public BluetoothControllerImpl_Factory(Provider<Context> contextProvider, Provider<Looper> bgLooperProvider, Provider<LocalBluetoothManager> localBluetoothManagerProvider) {
        this.contextProvider = contextProvider;
        this.bgLooperProvider = bgLooperProvider;
        this.localBluetoothManagerProvider = localBluetoothManagerProvider;
    }

    @Override // javax.inject.Provider
    public BluetoothControllerImpl get() {
        return provideInstance(this.contextProvider, this.bgLooperProvider, this.localBluetoothManagerProvider);
    }

    public static BluetoothControllerImpl provideInstance(Provider<Context> contextProvider, Provider<Looper> bgLooperProvider, Provider<LocalBluetoothManager> localBluetoothManagerProvider) {
        return new BluetoothControllerImpl(contextProvider.get(), bgLooperProvider.get(), localBluetoothManagerProvider.get());
    }

    public static BluetoothControllerImpl_Factory create(Provider<Context> contextProvider, Provider<Looper> bgLooperProvider, Provider<LocalBluetoothManager> localBluetoothManagerProvider) {
        return new BluetoothControllerImpl_Factory(contextProvider, bgLooperProvider, localBluetoothManagerProvider);
    }

    public static BluetoothControllerImpl newBluetoothControllerImpl(Context context, Looper bgLooper, LocalBluetoothManager localBluetoothManager) {
        return new BluetoothControllerImpl(context, bgLooper, localBluetoothManager);
    }
}
