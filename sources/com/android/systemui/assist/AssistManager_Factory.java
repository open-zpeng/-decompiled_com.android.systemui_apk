package com.android.systemui.assist;

import android.content.Context;
import com.android.internal.app.AssistUtils;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class AssistManager_Factory implements Factory<AssistManager> {
    private final Provider<AssistUtils> assistUtilsProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DeviceProvisionedController> controllerProvider;
    private final Provider<AssistHandleBehaviorController> handleControllerProvider;
    private final Provider<OverviewProxyService> overviewProxyServiceProvider;

    public AssistManager_Factory(Provider<DeviceProvisionedController> controllerProvider, Provider<Context> contextProvider, Provider<AssistUtils> assistUtilsProvider, Provider<AssistHandleBehaviorController> handleControllerProvider, Provider<ConfigurationController> configurationControllerProvider, Provider<OverviewProxyService> overviewProxyServiceProvider) {
        this.controllerProvider = controllerProvider;
        this.contextProvider = contextProvider;
        this.assistUtilsProvider = assistUtilsProvider;
        this.handleControllerProvider = handleControllerProvider;
        this.configurationControllerProvider = configurationControllerProvider;
        this.overviewProxyServiceProvider = overviewProxyServiceProvider;
    }

    @Override // javax.inject.Provider
    public AssistManager get() {
        return provideInstance(this.controllerProvider, this.contextProvider, this.assistUtilsProvider, this.handleControllerProvider, this.configurationControllerProvider, this.overviewProxyServiceProvider);
    }

    public static AssistManager provideInstance(Provider<DeviceProvisionedController> controllerProvider, Provider<Context> contextProvider, Provider<AssistUtils> assistUtilsProvider, Provider<AssistHandleBehaviorController> handleControllerProvider, Provider<ConfigurationController> configurationControllerProvider, Provider<OverviewProxyService> overviewProxyServiceProvider) {
        return new AssistManager(controllerProvider.get(), contextProvider.get(), assistUtilsProvider.get(), handleControllerProvider.get(), configurationControllerProvider.get(), overviewProxyServiceProvider.get());
    }

    public static AssistManager_Factory create(Provider<DeviceProvisionedController> controllerProvider, Provider<Context> contextProvider, Provider<AssistUtils> assistUtilsProvider, Provider<AssistHandleBehaviorController> handleControllerProvider, Provider<ConfigurationController> configurationControllerProvider, Provider<OverviewProxyService> overviewProxyServiceProvider) {
        return new AssistManager_Factory(controllerProvider, contextProvider, assistUtilsProvider, handleControllerProvider, configurationControllerProvider, overviewProxyServiceProvider);
    }

    public static AssistManager newAssistManager(DeviceProvisionedController controller, Context context, AssistUtils assistUtils, AssistHandleBehaviorController handleController, ConfigurationController configurationController, OverviewProxyService overviewProxyService) {
        return new AssistManager(controller, context, assistUtils, handleController, configurationController, overviewProxyService);
    }
}
