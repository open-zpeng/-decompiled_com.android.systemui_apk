package com.android.systemui.assist;

import android.content.Context;
import android.os.Handler;
import com.android.internal.app.AssistUtils;
import com.android.systemui.DumpController;
import com.android.systemui.ScreenDecorations;
import com.android.systemui.assist.AssistHandleBehaviorController;
import com.android.systemui.statusbar.phone.NavigationModeController;
import dagger.internal.Factory;
import java.util.Map;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class AssistHandleBehaviorController_Factory implements Factory<AssistHandleBehaviorController> {
    private final Provider<AssistUtils> assistUtilsProvider;
    private final Provider<Map<AssistHandleBehavior, AssistHandleBehaviorController.BehaviorController>> behaviorMapProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DeviceConfigHelper> deviceConfigHelperProvider;
    private final Provider<DumpController> dumpControllerProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<NavigationModeController> navigationModeControllerProvider;
    private final Provider<ScreenDecorations> screenDecorationsProvider;

    public AssistHandleBehaviorController_Factory(Provider<Context> contextProvider, Provider<AssistUtils> assistUtilsProvider, Provider<Handler> handlerProvider, Provider<ScreenDecorations> screenDecorationsProvider, Provider<DeviceConfigHelper> deviceConfigHelperProvider, Provider<Map<AssistHandleBehavior, AssistHandleBehaviorController.BehaviorController>> behaviorMapProvider, Provider<NavigationModeController> navigationModeControllerProvider, Provider<DumpController> dumpControllerProvider) {
        this.contextProvider = contextProvider;
        this.assistUtilsProvider = assistUtilsProvider;
        this.handlerProvider = handlerProvider;
        this.screenDecorationsProvider = screenDecorationsProvider;
        this.deviceConfigHelperProvider = deviceConfigHelperProvider;
        this.behaviorMapProvider = behaviorMapProvider;
        this.navigationModeControllerProvider = navigationModeControllerProvider;
        this.dumpControllerProvider = dumpControllerProvider;
    }

    @Override // javax.inject.Provider
    public AssistHandleBehaviorController get() {
        return provideInstance(this.contextProvider, this.assistUtilsProvider, this.handlerProvider, this.screenDecorationsProvider, this.deviceConfigHelperProvider, this.behaviorMapProvider, this.navigationModeControllerProvider, this.dumpControllerProvider);
    }

    public static AssistHandleBehaviorController provideInstance(Provider<Context> contextProvider, Provider<AssistUtils> assistUtilsProvider, Provider<Handler> handlerProvider, Provider<ScreenDecorations> screenDecorationsProvider, Provider<DeviceConfigHelper> deviceConfigHelperProvider, Provider<Map<AssistHandleBehavior, AssistHandleBehaviorController.BehaviorController>> behaviorMapProvider, Provider<NavigationModeController> navigationModeControllerProvider, Provider<DumpController> dumpControllerProvider) {
        return new AssistHandleBehaviorController(contextProvider.get(), assistUtilsProvider.get(), handlerProvider.get(), screenDecorationsProvider, deviceConfigHelperProvider.get(), behaviorMapProvider.get(), navigationModeControllerProvider.get(), dumpControllerProvider.get());
    }

    public static AssistHandleBehaviorController_Factory create(Provider<Context> contextProvider, Provider<AssistUtils> assistUtilsProvider, Provider<Handler> handlerProvider, Provider<ScreenDecorations> screenDecorationsProvider, Provider<DeviceConfigHelper> deviceConfigHelperProvider, Provider<Map<AssistHandleBehavior, AssistHandleBehaviorController.BehaviorController>> behaviorMapProvider, Provider<NavigationModeController> navigationModeControllerProvider, Provider<DumpController> dumpControllerProvider) {
        return new AssistHandleBehaviorController_Factory(contextProvider, assistUtilsProvider, handlerProvider, screenDecorationsProvider, deviceConfigHelperProvider, behaviorMapProvider, navigationModeControllerProvider, dumpControllerProvider);
    }

    public static AssistHandleBehaviorController newAssistHandleBehaviorController(Context context, AssistUtils assistUtils, Handler handler, Provider<ScreenDecorations> screenDecorations, DeviceConfigHelper deviceConfigHelper, Map<AssistHandleBehavior, AssistHandleBehaviorController.BehaviorController> behaviorMap, NavigationModeController navigationModeController, DumpController dumpController) {
        return new AssistHandleBehaviorController(context, assistUtils, handler, screenDecorations, deviceConfigHelper, behaviorMap, navigationModeController, dumpController);
    }
}
