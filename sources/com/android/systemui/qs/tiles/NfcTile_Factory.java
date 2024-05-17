package com.android.systemui.qs.tiles;

import com.android.systemui.qs.QSHost;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class NfcTile_Factory implements Factory<NfcTile> {
    private final Provider<QSHost> hostProvider;

    public NfcTile_Factory(Provider<QSHost> hostProvider) {
        this.hostProvider = hostProvider;
    }

    @Override // javax.inject.Provider
    public NfcTile get() {
        return provideInstance(this.hostProvider);
    }

    public static NfcTile provideInstance(Provider<QSHost> hostProvider) {
        return new NfcTile(hostProvider.get());
    }

    public static NfcTile_Factory create(Provider<QSHost> hostProvider) {
        return new NfcTile_Factory(hostProvider);
    }

    public static NfcTile newNfcTile(QSHost host) {
        return new NfcTile(host);
    }
}
