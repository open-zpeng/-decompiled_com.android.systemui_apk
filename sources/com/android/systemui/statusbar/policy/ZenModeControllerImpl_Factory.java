package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.os.Handler;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class ZenModeControllerImpl_Factory implements Factory<ZenModeControllerImpl> {
    private final Provider<Context> contextProvider;
    private final Provider<Handler> handlerProvider;

    public ZenModeControllerImpl_Factory(Provider<Context> contextProvider, Provider<Handler> handlerProvider) {
        this.contextProvider = contextProvider;
        this.handlerProvider = handlerProvider;
    }

    @Override // javax.inject.Provider
    public ZenModeControllerImpl get() {
        return provideInstance(this.contextProvider, this.handlerProvider);
    }

    public static ZenModeControllerImpl provideInstance(Provider<Context> contextProvider, Provider<Handler> handlerProvider) {
        return new ZenModeControllerImpl(contextProvider.get(), handlerProvider.get());
    }

    public static ZenModeControllerImpl_Factory create(Provider<Context> contextProvider, Provider<Handler> handlerProvider) {
        return new ZenModeControllerImpl_Factory(contextProvider, handlerProvider);
    }

    public static ZenModeControllerImpl newZenModeControllerImpl(Context context, Handler handler) {
        return new ZenModeControllerImpl(context, handler);
    }
}
