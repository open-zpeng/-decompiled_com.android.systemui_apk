package com.android.systemui.volume;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class VolumeDialogControllerImpl_Factory implements Factory<VolumeDialogControllerImpl> {
    private final Provider<Context> contextProvider;

    public VolumeDialogControllerImpl_Factory(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override // javax.inject.Provider
    public VolumeDialogControllerImpl get() {
        return provideInstance(this.contextProvider);
    }

    public static VolumeDialogControllerImpl provideInstance(Provider<Context> contextProvider) {
        return new VolumeDialogControllerImpl(contextProvider.get());
    }

    public static VolumeDialogControllerImpl_Factory create(Provider<Context> contextProvider) {
        return new VolumeDialogControllerImpl_Factory(contextProvider);
    }

    public static VolumeDialogControllerImpl newVolumeDialogControllerImpl(Context context) {
        return new VolumeDialogControllerImpl(context);
    }
}
