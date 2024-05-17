package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.os.Looper;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class LocationControllerImpl_Factory implements Factory<LocationControllerImpl> {
    private final Provider<Looper> bgLooperProvider;
    private final Provider<Context> contextProvider;

    public LocationControllerImpl_Factory(Provider<Context> contextProvider, Provider<Looper> bgLooperProvider) {
        this.contextProvider = contextProvider;
        this.bgLooperProvider = bgLooperProvider;
    }

    @Override // javax.inject.Provider
    public LocationControllerImpl get() {
        return provideInstance(this.contextProvider, this.bgLooperProvider);
    }

    public static LocationControllerImpl provideInstance(Provider<Context> contextProvider, Provider<Looper> bgLooperProvider) {
        return new LocationControllerImpl(contextProvider.get(), bgLooperProvider.get());
    }

    public static LocationControllerImpl_Factory create(Provider<Context> contextProvider, Provider<Looper> bgLooperProvider) {
        return new LocationControllerImpl_Factory(contextProvider, bgLooperProvider);
    }

    public static LocationControllerImpl newLocationControllerImpl(Context context, Looper bgLooper) {
        return new LocationControllerImpl(context, bgLooper);
    }
}
