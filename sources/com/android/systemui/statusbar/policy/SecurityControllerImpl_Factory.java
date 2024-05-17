package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.os.Handler;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class SecurityControllerImpl_Factory implements Factory<SecurityControllerImpl> {
    private final Provider<Handler> bgHandlerProvider;
    private final Provider<Context> contextProvider;

    public SecurityControllerImpl_Factory(Provider<Context> contextProvider, Provider<Handler> bgHandlerProvider) {
        this.contextProvider = contextProvider;
        this.bgHandlerProvider = bgHandlerProvider;
    }

    @Override // javax.inject.Provider
    public SecurityControllerImpl get() {
        return provideInstance(this.contextProvider, this.bgHandlerProvider);
    }

    public static SecurityControllerImpl provideInstance(Provider<Context> contextProvider, Provider<Handler> bgHandlerProvider) {
        return new SecurityControllerImpl(contextProvider.get(), bgHandlerProvider.get());
    }

    public static SecurityControllerImpl_Factory create(Provider<Context> contextProvider, Provider<Handler> bgHandlerProvider) {
        return new SecurityControllerImpl_Factory(contextProvider, bgHandlerProvider);
    }

    public static SecurityControllerImpl newSecurityControllerImpl(Context context, Handler bgHandler) {
        return new SecurityControllerImpl(context, bgHandler);
    }
}
