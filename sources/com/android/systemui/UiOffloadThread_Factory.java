package com.android.systemui;

import dagger.internal.Factory;
/* loaded from: classes21.dex */
public final class UiOffloadThread_Factory implements Factory<UiOffloadThread> {
    private static final UiOffloadThread_Factory INSTANCE = new UiOffloadThread_Factory();

    @Override // javax.inject.Provider
    public UiOffloadThread get() {
        return provideInstance();
    }

    public static UiOffloadThread provideInstance() {
        return new UiOffloadThread();
    }

    public static UiOffloadThread_Factory create() {
        return INSTANCE;
    }

    public static UiOffloadThread newUiOffloadThread() {
        return new UiOffloadThread();
    }
}
