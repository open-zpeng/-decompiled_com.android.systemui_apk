package com.android.systemui.qs;

import android.content.Context;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.policy.RemoteInputQuickSettingsDisabler;
import com.android.systemui.util.InjectionInflationController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class QSFragment_Factory implements Factory<QSFragment> {
    private final Provider<Context> contextProvider;
    private final Provider<InjectionInflationController> injectionInflaterProvider;
    private final Provider<QSTileHost> qsTileHostProvider;
    private final Provider<RemoteInputQuickSettingsDisabler> remoteInputQsDisablerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;

    public QSFragment_Factory(Provider<RemoteInputQuickSettingsDisabler> remoteInputQsDisablerProvider, Provider<InjectionInflationController> injectionInflaterProvider, Provider<Context> contextProvider, Provider<QSTileHost> qsTileHostProvider, Provider<StatusBarStateController> statusBarStateControllerProvider) {
        this.remoteInputQsDisablerProvider = remoteInputQsDisablerProvider;
        this.injectionInflaterProvider = injectionInflaterProvider;
        this.contextProvider = contextProvider;
        this.qsTileHostProvider = qsTileHostProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
    }

    @Override // javax.inject.Provider
    public QSFragment get() {
        return provideInstance(this.remoteInputQsDisablerProvider, this.injectionInflaterProvider, this.contextProvider, this.qsTileHostProvider, this.statusBarStateControllerProvider);
    }

    public static QSFragment provideInstance(Provider<RemoteInputQuickSettingsDisabler> remoteInputQsDisablerProvider, Provider<InjectionInflationController> injectionInflaterProvider, Provider<Context> contextProvider, Provider<QSTileHost> qsTileHostProvider, Provider<StatusBarStateController> statusBarStateControllerProvider) {
        return new QSFragment(remoteInputQsDisablerProvider.get(), injectionInflaterProvider.get(), contextProvider.get(), qsTileHostProvider.get(), statusBarStateControllerProvider.get());
    }

    public static QSFragment_Factory create(Provider<RemoteInputQuickSettingsDisabler> remoteInputQsDisablerProvider, Provider<InjectionInflationController> injectionInflaterProvider, Provider<Context> contextProvider, Provider<QSTileHost> qsTileHostProvider, Provider<StatusBarStateController> statusBarStateControllerProvider) {
        return new QSFragment_Factory(remoteInputQsDisablerProvider, injectionInflaterProvider, contextProvider, qsTileHostProvider, statusBarStateControllerProvider);
    }

    public static QSFragment newQSFragment(RemoteInputQuickSettingsDisabler remoteInputQsDisabler, InjectionInflationController injectionInflater, Context context, QSTileHost qsTileHost, StatusBarStateController statusBarStateController) {
        return new QSFragment(remoteInputQsDisabler, injectionInflater, context, qsTileHost, statusBarStateController);
    }
}
