package com.android.systemui.statusbar.policy;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class AccessibilityManagerWrapper_Factory implements Factory<AccessibilityManagerWrapper> {
    private final Provider<Context> contextProvider;

    public AccessibilityManagerWrapper_Factory(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override // javax.inject.Provider
    public AccessibilityManagerWrapper get() {
        return provideInstance(this.contextProvider);
    }

    public static AccessibilityManagerWrapper provideInstance(Provider<Context> contextProvider) {
        return new AccessibilityManagerWrapper(contextProvider.get());
    }

    public static AccessibilityManagerWrapper_Factory create(Provider<Context> contextProvider) {
        return new AccessibilityManagerWrapper_Factory(contextProvider);
    }

    public static AccessibilityManagerWrapper newAccessibilityManagerWrapper(Context context) {
        return new AccessibilityManagerWrapper(context);
    }
}
