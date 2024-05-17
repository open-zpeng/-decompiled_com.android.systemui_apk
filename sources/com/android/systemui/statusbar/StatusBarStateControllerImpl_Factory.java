package com.android.systemui.statusbar;

import dagger.internal.Factory;
/* loaded from: classes21.dex */
public final class StatusBarStateControllerImpl_Factory implements Factory<StatusBarStateControllerImpl> {
    private static final StatusBarStateControllerImpl_Factory INSTANCE = new StatusBarStateControllerImpl_Factory();

    @Override // javax.inject.Provider
    public StatusBarStateControllerImpl get() {
        return provideInstance();
    }

    public static StatusBarStateControllerImpl provideInstance() {
        return new StatusBarStateControllerImpl();
    }

    public static StatusBarStateControllerImpl_Factory create() {
        return INSTANCE;
    }

    public static StatusBarStateControllerImpl newStatusBarStateControllerImpl() {
        return new StatusBarStateControllerImpl();
    }
}
