package com.android.systemui.qs;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.android.systemui.DumpController;
import com.android.systemui.qs.tileimpl.QSFactoryImpl;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.statusbar.phone.AutoTileManager;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.tuner.TunerService;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class QSTileHost_Factory implements Factory<QSTileHost> {
    private final Provider<AutoTileManager> autoTilesProvider;
    private final Provider<Looper> bgLooperProvider;
    private final Provider<Context> contextProvider;
    private final Provider<QSFactoryImpl> defaultFactoryProvider;
    private final Provider<DumpController> dumpControllerProvider;
    private final Provider<StatusBarIconController> iconControllerProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<PluginManager> pluginManagerProvider;
    private final Provider<TunerService> tunerServiceProvider;

    public QSTileHost_Factory(Provider<Context> contextProvider, Provider<StatusBarIconController> iconControllerProvider, Provider<QSFactoryImpl> defaultFactoryProvider, Provider<Handler> mainHandlerProvider, Provider<Looper> bgLooperProvider, Provider<PluginManager> pluginManagerProvider, Provider<TunerService> tunerServiceProvider, Provider<AutoTileManager> autoTilesProvider, Provider<DumpController> dumpControllerProvider) {
        this.contextProvider = contextProvider;
        this.iconControllerProvider = iconControllerProvider;
        this.defaultFactoryProvider = defaultFactoryProvider;
        this.mainHandlerProvider = mainHandlerProvider;
        this.bgLooperProvider = bgLooperProvider;
        this.pluginManagerProvider = pluginManagerProvider;
        this.tunerServiceProvider = tunerServiceProvider;
        this.autoTilesProvider = autoTilesProvider;
        this.dumpControllerProvider = dumpControllerProvider;
    }

    @Override // javax.inject.Provider
    public QSTileHost get() {
        return provideInstance(this.contextProvider, this.iconControllerProvider, this.defaultFactoryProvider, this.mainHandlerProvider, this.bgLooperProvider, this.pluginManagerProvider, this.tunerServiceProvider, this.autoTilesProvider, this.dumpControllerProvider);
    }

    public static QSTileHost provideInstance(Provider<Context> contextProvider, Provider<StatusBarIconController> iconControllerProvider, Provider<QSFactoryImpl> defaultFactoryProvider, Provider<Handler> mainHandlerProvider, Provider<Looper> bgLooperProvider, Provider<PluginManager> pluginManagerProvider, Provider<TunerService> tunerServiceProvider, Provider<AutoTileManager> autoTilesProvider, Provider<DumpController> dumpControllerProvider) {
        return new QSTileHost(contextProvider.get(), iconControllerProvider.get(), defaultFactoryProvider.get(), mainHandlerProvider.get(), bgLooperProvider.get(), pluginManagerProvider.get(), tunerServiceProvider.get(), autoTilesProvider, dumpControllerProvider.get());
    }

    public static QSTileHost_Factory create(Provider<Context> contextProvider, Provider<StatusBarIconController> iconControllerProvider, Provider<QSFactoryImpl> defaultFactoryProvider, Provider<Handler> mainHandlerProvider, Provider<Looper> bgLooperProvider, Provider<PluginManager> pluginManagerProvider, Provider<TunerService> tunerServiceProvider, Provider<AutoTileManager> autoTilesProvider, Provider<DumpController> dumpControllerProvider) {
        return new QSTileHost_Factory(contextProvider, iconControllerProvider, defaultFactoryProvider, mainHandlerProvider, bgLooperProvider, pluginManagerProvider, tunerServiceProvider, autoTilesProvider, dumpControllerProvider);
    }

    public static QSTileHost newQSTileHost(Context context, StatusBarIconController iconController, QSFactoryImpl defaultFactory, Handler mainHandler, Looper bgLooper, PluginManager pluginManager, TunerService tunerService, Provider<AutoTileManager> autoTiles, DumpController dumpController) {
        return new QSTileHost(context, iconController, defaultFactory, mainHandler, bgLooper, pluginManager, tunerService, autoTiles, dumpController);
    }
}
