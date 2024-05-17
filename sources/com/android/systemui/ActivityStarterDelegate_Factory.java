package com.android.systemui;

import dagger.internal.Factory;
/* loaded from: classes21.dex */
public final class ActivityStarterDelegate_Factory implements Factory<ActivityStarterDelegate> {
    private static final ActivityStarterDelegate_Factory INSTANCE = new ActivityStarterDelegate_Factory();

    @Override // javax.inject.Provider
    public ActivityStarterDelegate get() {
        return provideInstance();
    }

    public static ActivityStarterDelegate provideInstance() {
        return new ActivityStarterDelegate();
    }

    public static ActivityStarterDelegate_Factory create() {
        return INSTANCE;
    }

    public static ActivityStarterDelegate newActivityStarterDelegate() {
        return new ActivityStarterDelegate();
    }
}
