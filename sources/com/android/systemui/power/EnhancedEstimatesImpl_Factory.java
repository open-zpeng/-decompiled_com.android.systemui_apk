package com.android.systemui.power;

import dagger.internal.Factory;
/* loaded from: classes21.dex */
public final class EnhancedEstimatesImpl_Factory implements Factory<EnhancedEstimatesImpl> {
    private static final EnhancedEstimatesImpl_Factory INSTANCE = new EnhancedEstimatesImpl_Factory();

    @Override // javax.inject.Provider
    public EnhancedEstimatesImpl get() {
        return provideInstance();
    }

    public static EnhancedEstimatesImpl provideInstance() {
        return new EnhancedEstimatesImpl();
    }

    public static EnhancedEstimatesImpl_Factory create() {
        return INSTANCE;
    }

    public static EnhancedEstimatesImpl newEnhancedEstimatesImpl() {
        return new EnhancedEstimatesImpl();
    }
}
