package com.android.systemui.qs.tiles;

import com.android.systemui.qs.QSHost;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class NightDisplayTile_Factory implements Factory<NightDisplayTile> {
    private final Provider<QSHost> hostProvider;

    public NightDisplayTile_Factory(Provider<QSHost> hostProvider) {
        this.hostProvider = hostProvider;
    }

    @Override // javax.inject.Provider
    public NightDisplayTile get() {
        return provideInstance(this.hostProvider);
    }

    public static NightDisplayTile provideInstance(Provider<QSHost> hostProvider) {
        return new NightDisplayTile(hostProvider.get());
    }

    public static NightDisplayTile_Factory create(Provider<QSHost> hostProvider) {
        return new NightDisplayTile_Factory(hostProvider);
    }

    public static NightDisplayTile newNightDisplayTile(QSHost host) {
        return new NightDisplayTile(host);
    }
}
