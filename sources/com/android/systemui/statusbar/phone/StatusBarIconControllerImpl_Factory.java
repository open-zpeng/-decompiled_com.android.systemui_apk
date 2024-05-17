package com.android.systemui.statusbar.phone;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class StatusBarIconControllerImpl_Factory implements Factory<StatusBarIconControllerImpl> {
    private final Provider<Context> contextProvider;

    public StatusBarIconControllerImpl_Factory(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override // javax.inject.Provider
    public StatusBarIconControllerImpl get() {
        return provideInstance(this.contextProvider);
    }

    public static StatusBarIconControllerImpl provideInstance(Provider<Context> contextProvider) {
        return new StatusBarIconControllerImpl(contextProvider.get());
    }

    public static StatusBarIconControllerImpl_Factory create(Provider<Context> contextProvider) {
        return new StatusBarIconControllerImpl_Factory(contextProvider);
    }

    public static StatusBarIconControllerImpl newStatusBarIconControllerImpl(Context context) {
        return new StatusBarIconControllerImpl(context);
    }
}
