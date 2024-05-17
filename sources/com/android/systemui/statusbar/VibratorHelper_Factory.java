package com.android.systemui.statusbar;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class VibratorHelper_Factory implements Factory<VibratorHelper> {
    private final Provider<Context> contextProvider;

    public VibratorHelper_Factory(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override // javax.inject.Provider
    public VibratorHelper get() {
        return provideInstance(this.contextProvider);
    }

    public static VibratorHelper provideInstance(Provider<Context> contextProvider) {
        return new VibratorHelper(contextProvider.get());
    }

    public static VibratorHelper_Factory create(Provider<Context> contextProvider) {
        return new VibratorHelper_Factory(contextProvider);
    }

    public static VibratorHelper newVibratorHelper(Context context) {
        return new VibratorHelper(context);
    }
}
