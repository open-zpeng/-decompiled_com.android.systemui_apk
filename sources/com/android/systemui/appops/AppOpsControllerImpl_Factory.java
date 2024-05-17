package com.android.systemui.appops;

import android.content.Context;
import android.os.Looper;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class AppOpsControllerImpl_Factory implements Factory<AppOpsControllerImpl> {
    private final Provider<Looper> bgLooperProvider;
    private final Provider<Context> contextProvider;

    public AppOpsControllerImpl_Factory(Provider<Context> contextProvider, Provider<Looper> bgLooperProvider) {
        this.contextProvider = contextProvider;
        this.bgLooperProvider = bgLooperProvider;
    }

    @Override // javax.inject.Provider
    public AppOpsControllerImpl get() {
        return provideInstance(this.contextProvider, this.bgLooperProvider);
    }

    public static AppOpsControllerImpl provideInstance(Provider<Context> contextProvider, Provider<Looper> bgLooperProvider) {
        return new AppOpsControllerImpl(contextProvider.get(), bgLooperProvider.get());
    }

    public static AppOpsControllerImpl_Factory create(Provider<Context> contextProvider, Provider<Looper> bgLooperProvider) {
        return new AppOpsControllerImpl_Factory(contextProvider, bgLooperProvider);
    }

    public static AppOpsControllerImpl newAppOpsControllerImpl(Context context, Looper bgLooper) {
        return new AppOpsControllerImpl(context, bgLooper);
    }
}
