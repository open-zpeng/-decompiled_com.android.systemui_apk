package com.android.systemui;

import dagger.internal.Factory;
/* loaded from: classes21.dex */
public final class ForegroundServiceController_Factory implements Factory<ForegroundServiceController> {
    private static final ForegroundServiceController_Factory INSTANCE = new ForegroundServiceController_Factory();

    @Override // javax.inject.Provider
    public ForegroundServiceController get() {
        return provideInstance();
    }

    public static ForegroundServiceController provideInstance() {
        return new ForegroundServiceController();
    }

    public static ForegroundServiceController_Factory create() {
        return INSTANCE;
    }

    public static ForegroundServiceController newForegroundServiceController() {
        return new ForegroundServiceController();
    }
}
