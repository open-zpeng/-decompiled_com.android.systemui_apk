package com.android.systemui.util;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class ProximitySensor_Factory implements Factory<ProximitySensor> {
    private final Provider<Context> contextProvider;
    private final Provider<AsyncSensorManager> sensorManagerProvider;

    public ProximitySensor_Factory(Provider<Context> contextProvider, Provider<AsyncSensorManager> sensorManagerProvider) {
        this.contextProvider = contextProvider;
        this.sensorManagerProvider = sensorManagerProvider;
    }

    @Override // javax.inject.Provider
    public ProximitySensor get() {
        return provideInstance(this.contextProvider, this.sensorManagerProvider);
    }

    public static ProximitySensor provideInstance(Provider<Context> contextProvider, Provider<AsyncSensorManager> sensorManagerProvider) {
        return new ProximitySensor(contextProvider.get(), sensorManagerProvider.get());
    }

    public static ProximitySensor_Factory create(Provider<Context> contextProvider, Provider<AsyncSensorManager> sensorManagerProvider) {
        return new ProximitySensor_Factory(contextProvider, sensorManagerProvider);
    }

    public static ProximitySensor newProximitySensor(Context context, AsyncSensorManager sensorManager) {
        return new ProximitySensor(context, sensorManager);
    }
}
