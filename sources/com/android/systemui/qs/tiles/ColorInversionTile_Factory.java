package com.android.systemui.qs.tiles;

import com.android.systemui.qs.QSHost;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class ColorInversionTile_Factory implements Factory<ColorInversionTile> {
    private final Provider<QSHost> hostProvider;

    public ColorInversionTile_Factory(Provider<QSHost> hostProvider) {
        this.hostProvider = hostProvider;
    }

    @Override // javax.inject.Provider
    public ColorInversionTile get() {
        return provideInstance(this.hostProvider);
    }

    public static ColorInversionTile provideInstance(Provider<QSHost> hostProvider) {
        return new ColorInversionTile(hostProvider.get());
    }

    public static ColorInversionTile_Factory create(Provider<QSHost> hostProvider) {
        return new ColorInversionTile_Factory(hostProvider);
    }

    public static ColorInversionTile newColorInversionTile(QSHost host) {
        return new ColorInversionTile(host);
    }
}
