package com.android.systemui.statusbar.phone;

import android.content.Context;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.policy.BatteryController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class LightBarController_Factory implements Factory<LightBarController> {
    private final Provider<BatteryController> batteryControllerProvider;
    private final Provider<Context> ctxProvider;
    private final Provider<DarkIconDispatcher> darkIconDispatcherProvider;

    public LightBarController_Factory(Provider<Context> ctxProvider, Provider<DarkIconDispatcher> darkIconDispatcherProvider, Provider<BatteryController> batteryControllerProvider) {
        this.ctxProvider = ctxProvider;
        this.darkIconDispatcherProvider = darkIconDispatcherProvider;
        this.batteryControllerProvider = batteryControllerProvider;
    }

    @Override // javax.inject.Provider
    public LightBarController get() {
        return provideInstance(this.ctxProvider, this.darkIconDispatcherProvider, this.batteryControllerProvider);
    }

    public static LightBarController provideInstance(Provider<Context> ctxProvider, Provider<DarkIconDispatcher> darkIconDispatcherProvider, Provider<BatteryController> batteryControllerProvider) {
        return new LightBarController(ctxProvider.get(), darkIconDispatcherProvider.get(), batteryControllerProvider.get());
    }

    public static LightBarController_Factory create(Provider<Context> ctxProvider, Provider<DarkIconDispatcher> darkIconDispatcherProvider, Provider<BatteryController> batteryControllerProvider) {
        return new LightBarController_Factory(ctxProvider, darkIconDispatcherProvider, batteryControllerProvider);
    }

    public static LightBarController newLightBarController(Context ctx, DarkIconDispatcher darkIconDispatcher, BatteryController batteryController) {
        return new LightBarController(ctx, darkIconDispatcher, batteryController);
    }
}
