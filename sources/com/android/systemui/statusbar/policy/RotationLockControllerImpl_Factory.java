package com.android.systemui.statusbar.policy;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class RotationLockControllerImpl_Factory implements Factory<RotationLockControllerImpl> {
    private final Provider<Context> contextProvider;

    public RotationLockControllerImpl_Factory(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override // javax.inject.Provider
    public RotationLockControllerImpl get() {
        return provideInstance(this.contextProvider);
    }

    public static RotationLockControllerImpl provideInstance(Provider<Context> contextProvider) {
        return new RotationLockControllerImpl(contextProvider.get());
    }

    public static RotationLockControllerImpl_Factory create(Provider<Context> contextProvider) {
        return new RotationLockControllerImpl_Factory(contextProvider);
    }

    public static RotationLockControllerImpl newRotationLockControllerImpl(Context context) {
        return new RotationLockControllerImpl(context);
    }
}
