package com.android.systemui.statusbar.phone;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class ManagedProfileControllerImpl_Factory implements Factory<ManagedProfileControllerImpl> {
    private final Provider<Context> contextProvider;

    public ManagedProfileControllerImpl_Factory(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override // javax.inject.Provider
    public ManagedProfileControllerImpl get() {
        return provideInstance(this.contextProvider);
    }

    public static ManagedProfileControllerImpl provideInstance(Provider<Context> contextProvider) {
        return new ManagedProfileControllerImpl(contextProvider.get());
    }

    public static ManagedProfileControllerImpl_Factory create(Provider<Context> contextProvider) {
        return new ManagedProfileControllerImpl_Factory(contextProvider);
    }

    public static ManagedProfileControllerImpl newManagedProfileControllerImpl(Context context) {
        return new ManagedProfileControllerImpl(context);
    }
}
