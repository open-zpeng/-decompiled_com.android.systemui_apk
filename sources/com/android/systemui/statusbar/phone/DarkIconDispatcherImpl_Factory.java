package com.android.systemui.statusbar.phone;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class DarkIconDispatcherImpl_Factory implements Factory<DarkIconDispatcherImpl> {
    private final Provider<Context> contextProvider;

    public DarkIconDispatcherImpl_Factory(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override // javax.inject.Provider
    public DarkIconDispatcherImpl get() {
        return provideInstance(this.contextProvider);
    }

    public static DarkIconDispatcherImpl provideInstance(Provider<Context> contextProvider) {
        return new DarkIconDispatcherImpl(contextProvider.get());
    }

    public static DarkIconDispatcherImpl_Factory create(Provider<Context> contextProvider) {
        return new DarkIconDispatcherImpl_Factory(contextProvider);
    }

    public static DarkIconDispatcherImpl newDarkIconDispatcherImpl(Context context) {
        return new DarkIconDispatcherImpl(context);
    }
}
