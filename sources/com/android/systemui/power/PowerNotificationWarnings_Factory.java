package com.android.systemui.power;

import android.content.Context;
import com.android.systemui.plugins.ActivityStarter;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class PowerNotificationWarnings_Factory implements Factory<PowerNotificationWarnings> {
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<Context> contextProvider;

    public PowerNotificationWarnings_Factory(Provider<Context> contextProvider, Provider<ActivityStarter> activityStarterProvider) {
        this.contextProvider = contextProvider;
        this.activityStarterProvider = activityStarterProvider;
    }

    @Override // javax.inject.Provider
    public PowerNotificationWarnings get() {
        return provideInstance(this.contextProvider, this.activityStarterProvider);
    }

    public static PowerNotificationWarnings provideInstance(Provider<Context> contextProvider, Provider<ActivityStarter> activityStarterProvider) {
        return new PowerNotificationWarnings(contextProvider.get(), activityStarterProvider.get());
    }

    public static PowerNotificationWarnings_Factory create(Provider<Context> contextProvider, Provider<ActivityStarter> activityStarterProvider) {
        return new PowerNotificationWarnings_Factory(contextProvider, activityStarterProvider);
    }

    public static PowerNotificationWarnings newPowerNotificationWarnings(Context context, ActivityStarter activityStarter) {
        return new PowerNotificationWarnings(context, activityStarter);
    }
}
