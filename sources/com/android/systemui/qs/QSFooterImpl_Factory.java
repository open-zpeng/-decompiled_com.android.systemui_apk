package com.android.systemui.qs;

import android.content.Context;
import android.util.AttributeSet;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.UserInfoController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class QSFooterImpl_Factory implements Factory<QSFooterImpl> {
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<AttributeSet> attrsProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DeviceProvisionedController> deviceProvisionedControllerProvider;
    private final Provider<UserInfoController> userInfoControllerProvider;

    public QSFooterImpl_Factory(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<ActivityStarter> activityStarterProvider, Provider<UserInfoController> userInfoControllerProvider, Provider<DeviceProvisionedController> deviceProvisionedControllerProvider) {
        this.contextProvider = contextProvider;
        this.attrsProvider = attrsProvider;
        this.activityStarterProvider = activityStarterProvider;
        this.userInfoControllerProvider = userInfoControllerProvider;
        this.deviceProvisionedControllerProvider = deviceProvisionedControllerProvider;
    }

    @Override // javax.inject.Provider
    public QSFooterImpl get() {
        return provideInstance(this.contextProvider, this.attrsProvider, this.activityStarterProvider, this.userInfoControllerProvider, this.deviceProvisionedControllerProvider);
    }

    public static QSFooterImpl provideInstance(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<ActivityStarter> activityStarterProvider, Provider<UserInfoController> userInfoControllerProvider, Provider<DeviceProvisionedController> deviceProvisionedControllerProvider) {
        return new QSFooterImpl(contextProvider.get(), attrsProvider.get(), activityStarterProvider.get(), userInfoControllerProvider.get(), deviceProvisionedControllerProvider.get());
    }

    public static QSFooterImpl_Factory create(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<ActivityStarter> activityStarterProvider, Provider<UserInfoController> userInfoControllerProvider, Provider<DeviceProvisionedController> deviceProvisionedControllerProvider) {
        return new QSFooterImpl_Factory(contextProvider, attrsProvider, activityStarterProvider, userInfoControllerProvider, deviceProvisionedControllerProvider);
    }

    public static QSFooterImpl newQSFooterImpl(Context context, AttributeSet attrs, ActivityStarter activityStarter, UserInfoController userInfoController, DeviceProvisionedController deviceProvisionedController) {
        return new QSFooterImpl(context, attrs, activityStarter, userInfoController, deviceProvisionedController);
    }
}
