package com.android.systemui.statusbar.policy;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class NextAlarmControllerImpl_Factory implements Factory<NextAlarmControllerImpl> {
    private final Provider<Context> contextProvider;

    public NextAlarmControllerImpl_Factory(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override // javax.inject.Provider
    public NextAlarmControllerImpl get() {
        return provideInstance(this.contextProvider);
    }

    public static NextAlarmControllerImpl provideInstance(Provider<Context> contextProvider) {
        return new NextAlarmControllerImpl(contextProvider.get());
    }

    public static NextAlarmControllerImpl_Factory create(Provider<Context> contextProvider) {
        return new NextAlarmControllerImpl_Factory(contextProvider);
    }

    public static NextAlarmControllerImpl newNextAlarmControllerImpl(Context context) {
        return new NextAlarmControllerImpl(context);
    }
}
