package com.android.systemui.qs;

import android.content.Context;
import android.util.AttributeSet;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.policy.NetworkController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class QSCarrierGroup_Factory implements Factory<QSCarrierGroup> {
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<AttributeSet> attrsProvider;
    private final Provider<Context> contextProvider;
    private final Provider<NetworkController> networkControllerProvider;

    public QSCarrierGroup_Factory(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<NetworkController> networkControllerProvider, Provider<ActivityStarter> activityStarterProvider) {
        this.contextProvider = contextProvider;
        this.attrsProvider = attrsProvider;
        this.networkControllerProvider = networkControllerProvider;
        this.activityStarterProvider = activityStarterProvider;
    }

    @Override // javax.inject.Provider
    public QSCarrierGroup get() {
        return provideInstance(this.contextProvider, this.attrsProvider, this.networkControllerProvider, this.activityStarterProvider);
    }

    public static QSCarrierGroup provideInstance(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<NetworkController> networkControllerProvider, Provider<ActivityStarter> activityStarterProvider) {
        return new QSCarrierGroup(contextProvider.get(), attrsProvider.get(), networkControllerProvider.get(), activityStarterProvider.get());
    }

    public static QSCarrierGroup_Factory create(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<NetworkController> networkControllerProvider, Provider<ActivityStarter> activityStarterProvider) {
        return new QSCarrierGroup_Factory(contextProvider, attrsProvider, networkControllerProvider, activityStarterProvider);
    }

    public static QSCarrierGroup newQSCarrierGroup(Context context, AttributeSet attrs, NetworkController networkController, ActivityStarter activityStarter) {
        return new QSCarrierGroup(context, attrs, networkController, activityStarter);
    }
}
