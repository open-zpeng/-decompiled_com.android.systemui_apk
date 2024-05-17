package com.android.systemui.statusbar.policy;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class FlashlightControllerImpl_Factory implements Factory<FlashlightControllerImpl> {
    private final Provider<Context> contextProvider;

    public FlashlightControllerImpl_Factory(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override // javax.inject.Provider
    public FlashlightControllerImpl get() {
        return provideInstance(this.contextProvider);
    }

    public static FlashlightControllerImpl provideInstance(Provider<Context> contextProvider) {
        return new FlashlightControllerImpl(contextProvider.get());
    }

    public static FlashlightControllerImpl_Factory create(Provider<Context> contextProvider) {
        return new FlashlightControllerImpl_Factory(contextProvider);
    }

    public static FlashlightControllerImpl newFlashlightControllerImpl(Context context) {
        return new FlashlightControllerImpl(context);
    }
}
