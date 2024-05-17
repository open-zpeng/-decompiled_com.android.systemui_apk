package com.android.systemui.statusbar.phone;

import android.content.Context;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class HeadsUpManagerPhone_Factory implements Factory<HeadsUpManagerPhone> {
    private final Provider<KeyguardBypassController> bypassControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;

    public HeadsUpManagerPhone_Factory(Provider<Context> contextProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<KeyguardBypassController> bypassControllerProvider) {
        this.contextProvider = contextProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.bypassControllerProvider = bypassControllerProvider;
    }

    @Override // javax.inject.Provider
    public HeadsUpManagerPhone get() {
        return provideInstance(this.contextProvider, this.statusBarStateControllerProvider, this.bypassControllerProvider);
    }

    public static HeadsUpManagerPhone provideInstance(Provider<Context> contextProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<KeyguardBypassController> bypassControllerProvider) {
        return new HeadsUpManagerPhone(contextProvider.get(), statusBarStateControllerProvider.get(), bypassControllerProvider.get());
    }

    public static HeadsUpManagerPhone_Factory create(Provider<Context> contextProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<KeyguardBypassController> bypassControllerProvider) {
        return new HeadsUpManagerPhone_Factory(contextProvider, statusBarStateControllerProvider, bypassControllerProvider);
    }

    public static HeadsUpManagerPhone newHeadsUpManagerPhone(Context context, StatusBarStateController statusBarStateController, KeyguardBypassController bypassController) {
        return new HeadsUpManagerPhone(context, statusBarStateController, bypassController);
    }
}
