package com.android.systemui.statusbar.policy;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class CastControllerImpl_Factory implements Factory<CastControllerImpl> {
    private final Provider<Context> contextProvider;

    public CastControllerImpl_Factory(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override // javax.inject.Provider
    public CastControllerImpl get() {
        return provideInstance(this.contextProvider);
    }

    public static CastControllerImpl provideInstance(Provider<Context> contextProvider) {
        return new CastControllerImpl(contextProvider.get());
    }

    public static CastControllerImpl_Factory create(Provider<Context> contextProvider) {
        return new CastControllerImpl_Factory(contextProvider);
    }

    public static CastControllerImpl newCastControllerImpl(Context context) {
        return new CastControllerImpl(context);
    }
}
