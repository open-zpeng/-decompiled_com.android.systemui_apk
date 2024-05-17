package com.android.systemui.qs;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class AutoAddTracker_Factory implements Factory<AutoAddTracker> {
    private final Provider<Context> contextProvider;

    public AutoAddTracker_Factory(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override // javax.inject.Provider
    public AutoAddTracker get() {
        return provideInstance(this.contextProvider);
    }

    public static AutoAddTracker provideInstance(Provider<Context> contextProvider) {
        return new AutoAddTracker(contextProvider.get());
    }

    public static AutoAddTracker_Factory create(Provider<Context> contextProvider) {
        return new AutoAddTracker_Factory(contextProvider);
    }

    public static AutoAddTracker newAutoAddTracker(Context context) {
        return new AutoAddTracker(context);
    }
}
