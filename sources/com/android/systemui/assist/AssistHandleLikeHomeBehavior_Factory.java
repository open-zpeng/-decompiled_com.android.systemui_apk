package com.android.systemui.assist;

import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.recents.OverviewProxyService;
import dagger.Lazy;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class AssistHandleLikeHomeBehavior_Factory implements Factory<AssistHandleLikeHomeBehavior> {
    private final Provider<OverviewProxyService> overviewProxyServiceProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider;

    public AssistHandleLikeHomeBehavior_Factory(Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider, Provider<OverviewProxyService> overviewProxyServiceProvider) {
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.wakefulnessLifecycleProvider = wakefulnessLifecycleProvider;
        this.overviewProxyServiceProvider = overviewProxyServiceProvider;
    }

    @Override // javax.inject.Provider
    public AssistHandleLikeHomeBehavior get() {
        return provideInstance(this.statusBarStateControllerProvider, this.wakefulnessLifecycleProvider, this.overviewProxyServiceProvider);
    }

    public static AssistHandleLikeHomeBehavior provideInstance(Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider, Provider<OverviewProxyService> overviewProxyServiceProvider) {
        return new AssistHandleLikeHomeBehavior(DoubleCheck.lazy(statusBarStateControllerProvider), DoubleCheck.lazy(wakefulnessLifecycleProvider), DoubleCheck.lazy(overviewProxyServiceProvider));
    }

    public static AssistHandleLikeHomeBehavior_Factory create(Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider, Provider<OverviewProxyService> overviewProxyServiceProvider) {
        return new AssistHandleLikeHomeBehavior_Factory(statusBarStateControllerProvider, wakefulnessLifecycleProvider, overviewProxyServiceProvider);
    }

    public static AssistHandleLikeHomeBehavior newAssistHandleLikeHomeBehavior(Lazy<StatusBarStateController> statusBarStateController, Lazy<WakefulnessLifecycle> wakefulnessLifecycle, Lazy<OverviewProxyService> overviewProxyService) {
        return new AssistHandleLikeHomeBehavior(statusBarStateController, wakefulnessLifecycle, overviewProxyService);
    }
}
