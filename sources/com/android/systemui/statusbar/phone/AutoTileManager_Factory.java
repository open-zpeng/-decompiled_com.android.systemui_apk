package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.hardware.display.NightDisplayListener;
import android.os.Handler;
import com.android.systemui.qs.AutoAddTracker;
import com.android.systemui.qs.QSTileHost;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.HotspotController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class AutoTileManager_Factory implements Factory<AutoTileManager> {
    private final Provider<AutoAddTracker> autoAddTrackerProvider;
    private final Provider<CastController> castControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DataSaverController> dataSaverControllerProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<QSTileHost> hostProvider;
    private final Provider<HotspotController> hotspotControllerProvider;
    private final Provider<ManagedProfileController> managedProfileControllerProvider;
    private final Provider<NightDisplayListener> nightDisplayListenerProvider;

    public AutoTileManager_Factory(Provider<Context> contextProvider, Provider<AutoAddTracker> autoAddTrackerProvider, Provider<QSTileHost> hostProvider, Provider<Handler> handlerProvider, Provider<HotspotController> hotspotControllerProvider, Provider<DataSaverController> dataSaverControllerProvider, Provider<ManagedProfileController> managedProfileControllerProvider, Provider<NightDisplayListener> nightDisplayListenerProvider, Provider<CastController> castControllerProvider) {
        this.contextProvider = contextProvider;
        this.autoAddTrackerProvider = autoAddTrackerProvider;
        this.hostProvider = hostProvider;
        this.handlerProvider = handlerProvider;
        this.hotspotControllerProvider = hotspotControllerProvider;
        this.dataSaverControllerProvider = dataSaverControllerProvider;
        this.managedProfileControllerProvider = managedProfileControllerProvider;
        this.nightDisplayListenerProvider = nightDisplayListenerProvider;
        this.castControllerProvider = castControllerProvider;
    }

    @Override // javax.inject.Provider
    public AutoTileManager get() {
        return provideInstance(this.contextProvider, this.autoAddTrackerProvider, this.hostProvider, this.handlerProvider, this.hotspotControllerProvider, this.dataSaverControllerProvider, this.managedProfileControllerProvider, this.nightDisplayListenerProvider, this.castControllerProvider);
    }

    public static AutoTileManager provideInstance(Provider<Context> contextProvider, Provider<AutoAddTracker> autoAddTrackerProvider, Provider<QSTileHost> hostProvider, Provider<Handler> handlerProvider, Provider<HotspotController> hotspotControllerProvider, Provider<DataSaverController> dataSaverControllerProvider, Provider<ManagedProfileController> managedProfileControllerProvider, Provider<NightDisplayListener> nightDisplayListenerProvider, Provider<CastController> castControllerProvider) {
        return new AutoTileManager(contextProvider.get(), autoAddTrackerProvider.get(), hostProvider.get(), handlerProvider.get(), hotspotControllerProvider.get(), dataSaverControllerProvider.get(), managedProfileControllerProvider.get(), nightDisplayListenerProvider.get(), castControllerProvider.get());
    }

    public static AutoTileManager_Factory create(Provider<Context> contextProvider, Provider<AutoAddTracker> autoAddTrackerProvider, Provider<QSTileHost> hostProvider, Provider<Handler> handlerProvider, Provider<HotspotController> hotspotControllerProvider, Provider<DataSaverController> dataSaverControllerProvider, Provider<ManagedProfileController> managedProfileControllerProvider, Provider<NightDisplayListener> nightDisplayListenerProvider, Provider<CastController> castControllerProvider) {
        return new AutoTileManager_Factory(contextProvider, autoAddTrackerProvider, hostProvider, handlerProvider, hotspotControllerProvider, dataSaverControllerProvider, managedProfileControllerProvider, nightDisplayListenerProvider, castControllerProvider);
    }

    public static AutoTileManager newAutoTileManager(Context context, AutoAddTracker autoAddTracker, QSTileHost host, Handler handler, HotspotController hotspotController, DataSaverController dataSaverController, ManagedProfileController managedProfileController, NightDisplayListener nightDisplayListener, CastController castController) {
        return new AutoTileManager(context, autoAddTracker, host, handler, hotspotController, dataSaverController, managedProfileController, nightDisplayListener, castController);
    }
}
