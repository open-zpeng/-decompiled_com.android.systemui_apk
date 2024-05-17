package com.android.systemui.assist;

import com.android.systemui.assist.AssistHandleBehaviorController;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import java.util.Map;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class AssistModule_ProvideAssistHandleBehaviorControllerMapFactory implements Factory<Map<AssistHandleBehavior, AssistHandleBehaviorController.BehaviorController>> {
    private final Provider<AssistHandleLikeHomeBehavior> likeHomeBehaviorProvider;
    private final Provider<AssistHandleOffBehavior> offBehaviorProvider;
    private final Provider<AssistHandleReminderExpBehavior> reminderExpBehaviorProvider;

    public AssistModule_ProvideAssistHandleBehaviorControllerMapFactory(Provider<AssistHandleOffBehavior> offBehaviorProvider, Provider<AssistHandleLikeHomeBehavior> likeHomeBehaviorProvider, Provider<AssistHandleReminderExpBehavior> reminderExpBehaviorProvider) {
        this.offBehaviorProvider = offBehaviorProvider;
        this.likeHomeBehaviorProvider = likeHomeBehaviorProvider;
        this.reminderExpBehaviorProvider = reminderExpBehaviorProvider;
    }

    @Override // javax.inject.Provider
    public Map<AssistHandleBehavior, AssistHandleBehaviorController.BehaviorController> get() {
        return provideInstance(this.offBehaviorProvider, this.likeHomeBehaviorProvider, this.reminderExpBehaviorProvider);
    }

    public static Map<AssistHandleBehavior, AssistHandleBehaviorController.BehaviorController> provideInstance(Provider<AssistHandleOffBehavior> offBehaviorProvider, Provider<AssistHandleLikeHomeBehavior> likeHomeBehaviorProvider, Provider<AssistHandleReminderExpBehavior> reminderExpBehaviorProvider) {
        return proxyProvideAssistHandleBehaviorControllerMap(offBehaviorProvider.get(), likeHomeBehaviorProvider.get(), reminderExpBehaviorProvider.get());
    }

    public static AssistModule_ProvideAssistHandleBehaviorControllerMapFactory create(Provider<AssistHandleOffBehavior> offBehaviorProvider, Provider<AssistHandleLikeHomeBehavior> likeHomeBehaviorProvider, Provider<AssistHandleReminderExpBehavior> reminderExpBehaviorProvider) {
        return new AssistModule_ProvideAssistHandleBehaviorControllerMapFactory(offBehaviorProvider, likeHomeBehaviorProvider, reminderExpBehaviorProvider);
    }

    public static Map<AssistHandleBehavior, AssistHandleBehaviorController.BehaviorController> proxyProvideAssistHandleBehaviorControllerMap(Object offBehavior, Object likeHomeBehavior, Object reminderExpBehavior) {
        return (Map) Preconditions.checkNotNull(AssistModule.provideAssistHandleBehaviorControllerMap((AssistHandleOffBehavior) offBehavior, (AssistHandleLikeHomeBehavior) likeHomeBehavior, (AssistHandleReminderExpBehavior) reminderExpBehavior), "Cannot return null from a non-@Nullable @Provides method");
    }
}
