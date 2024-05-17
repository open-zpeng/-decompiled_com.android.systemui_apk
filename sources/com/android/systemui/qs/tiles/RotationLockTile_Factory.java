package com.android.systemui.qs.tiles;

import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.RotationLockController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class RotationLockTile_Factory implements Factory<RotationLockTile> {
    private final Provider<QSHost> hostProvider;
    private final Provider<RotationLockController> rotationLockControllerProvider;

    public RotationLockTile_Factory(Provider<QSHost> hostProvider, Provider<RotationLockController> rotationLockControllerProvider) {
        this.hostProvider = hostProvider;
        this.rotationLockControllerProvider = rotationLockControllerProvider;
    }

    @Override // javax.inject.Provider
    public RotationLockTile get() {
        return provideInstance(this.hostProvider, this.rotationLockControllerProvider);
    }

    public static RotationLockTile provideInstance(Provider<QSHost> hostProvider, Provider<RotationLockController> rotationLockControllerProvider) {
        return new RotationLockTile(hostProvider.get(), rotationLockControllerProvider.get());
    }

    public static RotationLockTile_Factory create(Provider<QSHost> hostProvider, Provider<RotationLockController> rotationLockControllerProvider) {
        return new RotationLockTile_Factory(hostProvider, rotationLockControllerProvider);
    }

    public static RotationLockTile newRotationLockTile(QSHost host, RotationLockController rotationLockController) {
        return new RotationLockTile(host, rotationLockController);
    }
}
