package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.os.Handler;
import com.android.systemui.plugins.ActivityStarter;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class UserSwitcherController_Factory implements Factory<UserSwitcherController> {
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<Context> contextProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<KeyguardMonitor> keyguardMonitorProvider;

    public UserSwitcherController_Factory(Provider<Context> contextProvider, Provider<KeyguardMonitor> keyguardMonitorProvider, Provider<Handler> handlerProvider, Provider<ActivityStarter> activityStarterProvider) {
        this.contextProvider = contextProvider;
        this.keyguardMonitorProvider = keyguardMonitorProvider;
        this.handlerProvider = handlerProvider;
        this.activityStarterProvider = activityStarterProvider;
    }

    @Override // javax.inject.Provider
    public UserSwitcherController get() {
        return provideInstance(this.contextProvider, this.keyguardMonitorProvider, this.handlerProvider, this.activityStarterProvider);
    }

    public static UserSwitcherController provideInstance(Provider<Context> contextProvider, Provider<KeyguardMonitor> keyguardMonitorProvider, Provider<Handler> handlerProvider, Provider<ActivityStarter> activityStarterProvider) {
        return new UserSwitcherController(contextProvider.get(), keyguardMonitorProvider.get(), handlerProvider.get(), activityStarterProvider.get());
    }

    public static UserSwitcherController_Factory create(Provider<Context> contextProvider, Provider<KeyguardMonitor> keyguardMonitorProvider, Provider<Handler> handlerProvider, Provider<ActivityStarter> activityStarterProvider) {
        return new UserSwitcherController_Factory(contextProvider, keyguardMonitorProvider, handlerProvider, activityStarterProvider);
    }

    public static UserSwitcherController newUserSwitcherController(Context context, KeyguardMonitor keyguardMonitor, Handler handler, ActivityStarter activityStarter) {
        return new UserSwitcherController(context, keyguardMonitor, handler, activityStarter);
    }
}
