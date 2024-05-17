package com.android.systemui.qs.tiles;

import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.phone.ManagedProfileController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class WorkModeTile_Factory implements Factory<WorkModeTile> {
    private final Provider<QSHost> hostProvider;
    private final Provider<ManagedProfileController> managedProfileControllerProvider;

    public WorkModeTile_Factory(Provider<QSHost> hostProvider, Provider<ManagedProfileController> managedProfileControllerProvider) {
        this.hostProvider = hostProvider;
        this.managedProfileControllerProvider = managedProfileControllerProvider;
    }

    @Override // javax.inject.Provider
    public WorkModeTile get() {
        return provideInstance(this.hostProvider, this.managedProfileControllerProvider);
    }

    public static WorkModeTile provideInstance(Provider<QSHost> hostProvider, Provider<ManagedProfileController> managedProfileControllerProvider) {
        return new WorkModeTile(hostProvider.get(), managedProfileControllerProvider.get());
    }

    public static WorkModeTile_Factory create(Provider<QSHost> hostProvider, Provider<ManagedProfileController> managedProfileControllerProvider) {
        return new WorkModeTile_Factory(hostProvider, managedProfileControllerProvider);
    }

    public static WorkModeTile newWorkModeTile(QSHost host, ManagedProfileController managedProfileController) {
        return new WorkModeTile(host, managedProfileController);
    }
}
