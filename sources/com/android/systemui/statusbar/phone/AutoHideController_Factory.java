package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.os.Handler;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class AutoHideController_Factory implements Factory<AutoHideController> {
    private final Provider<Context> contextProvider;
    private final Provider<Handler> handlerProvider;

    public AutoHideController_Factory(Provider<Context> contextProvider, Provider<Handler> handlerProvider) {
        this.contextProvider = contextProvider;
        this.handlerProvider = handlerProvider;
    }

    @Override // javax.inject.Provider
    public AutoHideController get() {
        return provideInstance(this.contextProvider, this.handlerProvider);
    }

    public static AutoHideController provideInstance(Provider<Context> contextProvider, Provider<Handler> handlerProvider) {
        return new AutoHideController(contextProvider.get(), handlerProvider.get());
    }

    public static AutoHideController_Factory create(Provider<Context> contextProvider, Provider<Handler> handlerProvider) {
        return new AutoHideController_Factory(contextProvider, handlerProvider);
    }

    public static AutoHideController newAutoHideController(Context context, Handler handler) {
        return new AutoHideController(context, handler);
    }
}
