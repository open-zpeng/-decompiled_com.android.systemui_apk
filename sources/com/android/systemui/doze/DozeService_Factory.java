package com.android.systemui.doze;

import dagger.internal.Factory;
/* loaded from: classes21.dex */
public final class DozeService_Factory implements Factory<DozeService> {
    private static final DozeService_Factory INSTANCE = new DozeService_Factory();

    @Override // javax.inject.Provider
    public DozeService get() {
        return provideInstance();
    }

    public static DozeService provideInstance() {
        return new DozeService();
    }

    public static DozeService_Factory create() {
        return INSTANCE;
    }

    public static DozeService newDozeService() {
        return new DozeService();
    }
}
