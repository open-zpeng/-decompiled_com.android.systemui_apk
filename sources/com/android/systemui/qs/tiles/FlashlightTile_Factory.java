package com.android.systemui.qs.tiles;

import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.FlashlightController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class FlashlightTile_Factory implements Factory<FlashlightTile> {
    private final Provider<FlashlightController> flashlightControllerProvider;
    private final Provider<QSHost> hostProvider;

    public FlashlightTile_Factory(Provider<QSHost> hostProvider, Provider<FlashlightController> flashlightControllerProvider) {
        this.hostProvider = hostProvider;
        this.flashlightControllerProvider = flashlightControllerProvider;
    }

    @Override // javax.inject.Provider
    public FlashlightTile get() {
        return provideInstance(this.hostProvider, this.flashlightControllerProvider);
    }

    public static FlashlightTile provideInstance(Provider<QSHost> hostProvider, Provider<FlashlightController> flashlightControllerProvider) {
        return new FlashlightTile(hostProvider.get(), flashlightControllerProvider.get());
    }

    public static FlashlightTile_Factory create(Provider<QSHost> hostProvider, Provider<FlashlightController> flashlightControllerProvider) {
        return new FlashlightTile_Factory(hostProvider, flashlightControllerProvider);
    }

    public static FlashlightTile newFlashlightTile(QSHost host, FlashlightController flashlightController) {
        return new FlashlightTile(host, flashlightController);
    }
}
