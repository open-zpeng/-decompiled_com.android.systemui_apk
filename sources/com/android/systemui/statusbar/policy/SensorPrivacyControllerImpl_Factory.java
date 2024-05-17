package com.android.systemui.statusbar.policy;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class SensorPrivacyControllerImpl_Factory implements Factory<SensorPrivacyControllerImpl> {
    private final Provider<Context> contextProvider;

    public SensorPrivacyControllerImpl_Factory(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override // javax.inject.Provider
    public SensorPrivacyControllerImpl get() {
        return provideInstance(this.contextProvider);
    }

    public static SensorPrivacyControllerImpl provideInstance(Provider<Context> contextProvider) {
        return new SensorPrivacyControllerImpl(contextProvider.get());
    }

    public static SensorPrivacyControllerImpl_Factory create(Provider<Context> contextProvider) {
        return new SensorPrivacyControllerImpl_Factory(contextProvider);
    }

    public static SensorPrivacyControllerImpl newSensorPrivacyControllerImpl(Context context) {
        return new SensorPrivacyControllerImpl(context);
    }
}
