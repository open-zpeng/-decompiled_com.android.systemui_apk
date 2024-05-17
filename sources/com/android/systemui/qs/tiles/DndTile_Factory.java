package com.android.systemui.qs.tiles;

import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.ZenModeController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class DndTile_Factory implements Factory<DndTile> {
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<QSHost> hostProvider;
    private final Provider<ZenModeController> zenModeControllerProvider;

    public DndTile_Factory(Provider<QSHost> hostProvider, Provider<ZenModeController> zenModeControllerProvider, Provider<ActivityStarter> activityStarterProvider) {
        this.hostProvider = hostProvider;
        this.zenModeControllerProvider = zenModeControllerProvider;
        this.activityStarterProvider = activityStarterProvider;
    }

    @Override // javax.inject.Provider
    public DndTile get() {
        return provideInstance(this.hostProvider, this.zenModeControllerProvider, this.activityStarterProvider);
    }

    public static DndTile provideInstance(Provider<QSHost> hostProvider, Provider<ZenModeController> zenModeControllerProvider, Provider<ActivityStarter> activityStarterProvider) {
        return new DndTile(hostProvider.get(), zenModeControllerProvider.get(), activityStarterProvider.get());
    }

    public static DndTile_Factory create(Provider<QSHost> hostProvider, Provider<ZenModeController> zenModeControllerProvider, Provider<ActivityStarter> activityStarterProvider) {
        return new DndTile_Factory(hostProvider, zenModeControllerProvider, activityStarterProvider);
    }

    public static DndTile newDndTile(QSHost host, ZenModeController zenModeController, ActivityStarter activityStarter) {
        return new DndTile(host, zenModeController, activityStarter);
    }
}
