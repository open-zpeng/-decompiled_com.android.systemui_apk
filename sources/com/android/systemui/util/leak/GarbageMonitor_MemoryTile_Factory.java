package com.android.systemui.util.leak;

import com.android.systemui.qs.QSHost;
import com.android.systemui.util.leak.GarbageMonitor;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class GarbageMonitor_MemoryTile_Factory implements Factory<GarbageMonitor.MemoryTile> {
    private final Provider<QSHost> hostProvider;

    public GarbageMonitor_MemoryTile_Factory(Provider<QSHost> hostProvider) {
        this.hostProvider = hostProvider;
    }

    @Override // javax.inject.Provider
    public GarbageMonitor.MemoryTile get() {
        return provideInstance(this.hostProvider);
    }

    public static GarbageMonitor.MemoryTile provideInstance(Provider<QSHost> hostProvider) {
        return new GarbageMonitor.MemoryTile(hostProvider.get());
    }

    public static GarbageMonitor_MemoryTile_Factory create(Provider<QSHost> hostProvider) {
        return new GarbageMonitor_MemoryTile_Factory(hostProvider);
    }

    public static GarbageMonitor.MemoryTile newMemoryTile(QSHost host) {
        return new GarbageMonitor.MemoryTile(host);
    }
}
