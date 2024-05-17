package com.android.systemui.statusbar.policy;

import android.content.Context;
import com.android.systemui.power.EnhancedEstimates;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class BatteryControllerImpl_Factory implements Factory<BatteryControllerImpl> {
    private final Provider<Context> contextProvider;
    private final Provider<EnhancedEstimates> enhancedEstimatesProvider;

    public BatteryControllerImpl_Factory(Provider<Context> contextProvider, Provider<EnhancedEstimates> enhancedEstimatesProvider) {
        this.contextProvider = contextProvider;
        this.enhancedEstimatesProvider = enhancedEstimatesProvider;
    }

    @Override // javax.inject.Provider
    public BatteryControllerImpl get() {
        return provideInstance(this.contextProvider, this.enhancedEstimatesProvider);
    }

    public static BatteryControllerImpl provideInstance(Provider<Context> contextProvider, Provider<EnhancedEstimates> enhancedEstimatesProvider) {
        return new BatteryControllerImpl(contextProvider.get(), enhancedEstimatesProvider.get());
    }

    public static BatteryControllerImpl_Factory create(Provider<Context> contextProvider, Provider<EnhancedEstimates> enhancedEstimatesProvider) {
        return new BatteryControllerImpl_Factory(contextProvider, enhancedEstimatesProvider);
    }

    public static BatteryControllerImpl newBatteryControllerImpl(Context context, EnhancedEstimates enhancedEstimates) {
        return new BatteryControllerImpl(context, enhancedEstimates);
    }
}
