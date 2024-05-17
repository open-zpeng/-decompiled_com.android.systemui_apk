package com.android.systemui;

import android.content.Context;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.KeyguardLiftController;
import com.android.systemui.util.AsyncSensorManager;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class SystemUIModule_ProvideKeyguardLiftControllerFactory implements Factory<KeyguardLiftController> {
    private final Provider<AsyncSensorManager> asyncSensorManagerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;

    public SystemUIModule_ProvideKeyguardLiftControllerFactory(Provider<Context> contextProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<AsyncSensorManager> asyncSensorManagerProvider) {
        this.contextProvider = contextProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.asyncSensorManagerProvider = asyncSensorManagerProvider;
    }

    @Override // javax.inject.Provider
    public KeyguardLiftController get() {
        return provideInstance(this.contextProvider, this.statusBarStateControllerProvider, this.asyncSensorManagerProvider);
    }

    public static KeyguardLiftController provideInstance(Provider<Context> contextProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<AsyncSensorManager> asyncSensorManagerProvider) {
        return proxyProvideKeyguardLiftController(contextProvider.get(), statusBarStateControllerProvider.get(), asyncSensorManagerProvider.get());
    }

    public static SystemUIModule_ProvideKeyguardLiftControllerFactory create(Provider<Context> contextProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<AsyncSensorManager> asyncSensorManagerProvider) {
        return new SystemUIModule_ProvideKeyguardLiftControllerFactory(contextProvider, statusBarStateControllerProvider, asyncSensorManagerProvider);
    }

    public static KeyguardLiftController proxyProvideKeyguardLiftController(Context context, StatusBarStateController statusBarStateController, AsyncSensorManager asyncSensorManager) {
        return SystemUIModule.provideKeyguardLiftController(context, statusBarStateController, asyncSensorManager);
    }
}
