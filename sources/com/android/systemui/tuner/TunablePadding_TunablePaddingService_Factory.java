package com.android.systemui.tuner;

import com.android.systemui.tuner.TunablePadding;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class TunablePadding_TunablePaddingService_Factory implements Factory<TunablePadding.TunablePaddingService> {
    private final Provider<TunerService> tunerServiceProvider;

    public TunablePadding_TunablePaddingService_Factory(Provider<TunerService> tunerServiceProvider) {
        this.tunerServiceProvider = tunerServiceProvider;
    }

    @Override // javax.inject.Provider
    public TunablePadding.TunablePaddingService get() {
        return provideInstance(this.tunerServiceProvider);
    }

    public static TunablePadding.TunablePaddingService provideInstance(Provider<TunerService> tunerServiceProvider) {
        return new TunablePadding.TunablePaddingService(tunerServiceProvider.get());
    }

    public static TunablePadding_TunablePaddingService_Factory create(Provider<TunerService> tunerServiceProvider) {
        return new TunablePadding_TunablePaddingService_Factory(tunerServiceProvider);
    }

    public static TunablePadding.TunablePaddingService newTunablePaddingService(TunerService tunerService) {
        return new TunablePadding.TunablePaddingService(tunerService);
    }
}
