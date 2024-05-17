package com.android.systemui.statusbar.policy;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class KeyguardMonitorImpl_Factory implements Factory<KeyguardMonitorImpl> {
    private final Provider<Context> contextProvider;

    public KeyguardMonitorImpl_Factory(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override // javax.inject.Provider
    public KeyguardMonitorImpl get() {
        return provideInstance(this.contextProvider);
    }

    public static KeyguardMonitorImpl provideInstance(Provider<Context> contextProvider) {
        return new KeyguardMonitorImpl(contextProvider.get());
    }

    public static KeyguardMonitorImpl_Factory create(Provider<Context> contextProvider) {
        return new KeyguardMonitorImpl_Factory(contextProvider);
    }

    public static KeyguardMonitorImpl newKeyguardMonitorImpl(Context context) {
        return new KeyguardMonitorImpl(context);
    }
}
