package com.android.systemui.assist;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import androidx.slice.Clock;
import com.android.internal.app.AssistUtils;
import com.android.systemui.ScreenDecorations;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.assist.AssistHandleBehaviorController;
import dagger.Module;
import dagger.Provides;
import java.util.EnumMap;
import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;
@Module
/* loaded from: classes21.dex */
public abstract class AssistModule {
    static final String ASSIST_HANDLE_THREAD_NAME = "assist_handle_thread";
    static final String UPTIME_NAME = "uptime";

    /* JADX INFO: Access modifiers changed from: package-private */
    @Provides
    @Singleton
    @Named(ASSIST_HANDLE_THREAD_NAME)
    public static Handler provideBackgroundHandler() {
        HandlerThread backgroundHandlerThread = new HandlerThread("AssistHandleThread");
        backgroundHandlerThread.start();
        return backgroundHandlerThread.getThreadHandler();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Provides
    @Singleton
    public static Map<AssistHandleBehavior, AssistHandleBehaviorController.BehaviorController> provideAssistHandleBehaviorControllerMap(AssistHandleOffBehavior offBehavior, AssistHandleLikeHomeBehavior likeHomeBehavior, AssistHandleReminderExpBehavior reminderExpBehavior) {
        Map<AssistHandleBehavior, AssistHandleBehaviorController.BehaviorController> map = new EnumMap<>(AssistHandleBehavior.class);
        map.put(AssistHandleBehavior.OFF, offBehavior);
        map.put(AssistHandleBehavior.LIKE_HOME, likeHomeBehavior);
        map.put(AssistHandleBehavior.REMINDER_EXP, reminderExpBehavior);
        return map;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Provides
    public static ScreenDecorations provideScreenDecorations(Context context) {
        return (ScreenDecorations) SysUiServiceProvider.getComponent(context, ScreenDecorations.class);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Provides
    @Singleton
    public static AssistUtils provideAssistUtils(Context context) {
        return new AssistUtils(context);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Provides
    @Singleton
    @Named(UPTIME_NAME)
    public static Clock provideSystemClock() {
        return new Clock() { // from class: com.android.systemui.assist.-$$Lambda$WyKlJnsW9STKD48w13qf39m-FKI
            @Override // androidx.slice.Clock
            public final long currentTimeMillis() {
                return SystemClock.uptimeMillis();
            }
        };
    }
}
