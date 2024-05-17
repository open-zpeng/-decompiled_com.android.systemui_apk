package com.android.systemui;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import com.android.internal.colorextraction.ColorExtractor;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.ViewMediatorCallback;
import com.android.systemui.keyguard.DismissCallbackRegistry;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.screenshot.ScreenshotNotificationSmartActionsProvider;
import com.android.systemui.statusbar.KeyguardIndicationController;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.ScrimView;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.KeyguardBouncer;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.LockIcon;
import com.android.systemui.statusbar.phone.LockscreenWallpaper;
import com.android.systemui.statusbar.phone.NotificationIconAreaController;
import com.android.systemui.statusbar.phone.ScrimController;
import com.android.systemui.statusbar.phone.ScrimState;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.phone.UnlockMethodCache;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.volume.VolumeDialogComponent;
import dagger.Module;
import dagger.Provides;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class SystemUIFactory {
    private static final String TAG = "SystemUIFactory";
    static SystemUIFactory mFactory;
    protected SystemUIRootComponent mRootComponent;

    public static <T extends SystemUIFactory> T getInstance() {
        return (T) mFactory;
    }

    public static void createFromConfig(Context context) {
        String clsName = context.getString(R.string.config_systemUIFactoryComponent);
        if (clsName == null || clsName.length() == 0) {
            throw new RuntimeException("No SystemUIFactory component configured");
        }
        try {
            Class<?> cls = context.getClassLoader().loadClass(clsName);
            mFactory = (SystemUIFactory) cls.newInstance();
            mFactory.init(context);
        } catch (Throwable t) {
            Log.w(TAG, "Error creating SystemUIFactory component: " + clsName, t);
            throw new RuntimeException(t);
        }
    }

    private void init(Context context) {
        this.mRootComponent = buildSystemUIRootComponent(context);
        Dependency.initDependencies(this.mRootComponent);
    }

    protected SystemUIRootComponent buildSystemUIRootComponent(Context context) {
        return DaggerSystemUIRootComponent.builder().dependencyProvider(new DependencyProvider()).contextHolder(new ContextHolder(context)).build();
    }

    public SystemUIRootComponent getRootComponent() {
        return this.mRootComponent;
    }

    public StatusBarKeyguardViewManager createStatusBarKeyguardViewManager(Context context, ViewMediatorCallback viewMediatorCallback, LockPatternUtils lockPatternUtils) {
        return new StatusBarKeyguardViewManager(context, viewMediatorCallback, lockPatternUtils);
    }

    public ScreenshotNotificationSmartActionsProvider createScreenshotNotificationSmartActionsProvider(Context context, Executor executor, Handler uiHandler) {
        return new ScreenshotNotificationSmartActionsProvider();
    }

    public KeyguardBouncer createKeyguardBouncer(Context context, ViewMediatorCallback callback, LockPatternUtils lockPatternUtils, ViewGroup container, DismissCallbackRegistry dismissCallbackRegistry, KeyguardBouncer.BouncerExpansionCallback expansionCallback, FalsingManager falsingManager, KeyguardBypassController bypassController) {
        return new KeyguardBouncer(context, callback, lockPatternUtils, container, dismissCallbackRegistry, falsingManager, expansionCallback, UnlockMethodCache.getInstance(context), KeyguardUpdateMonitor.getInstance(context), bypassController, new Handler(Looper.getMainLooper()));
    }

    public ScrimController createScrimController(ScrimView scrimBehind, ScrimView scrimInFront, LockscreenWallpaper lockscreenWallpaper, TriConsumer<ScrimState, Float, ColorExtractor.GradientColors> scrimStateListener, Consumer<Integer> scrimVisibleListener, DozeParameters dozeParameters, AlarmManager alarmManager, KeyguardMonitor keyguardMonitor) {
        return new ScrimController(scrimBehind, scrimInFront, scrimStateListener, scrimVisibleListener, dozeParameters, alarmManager, keyguardMonitor);
    }

    public NotificationIconAreaController createNotificationIconAreaController(Context context, StatusBar statusBar, NotificationWakeUpCoordinator wakeUpCoordinator, KeyguardBypassController keyguardBypassController, StatusBarStateController statusBarStateController) {
        return new NotificationIconAreaController(context, statusBar, statusBarStateController, wakeUpCoordinator, keyguardBypassController, (NotificationMediaManager) Dependency.get(NotificationMediaManager.class));
    }

    public KeyguardIndicationController createKeyguardIndicationController(Context context, ViewGroup indicationArea, LockIcon lockIcon) {
        return new KeyguardIndicationController(context, indicationArea, lockIcon);
    }

    public VolumeDialogComponent createVolumeDialogComponent(SystemUI systemUi, Context context) {
        return new VolumeDialogComponent(systemUi, context);
    }

    @Module
    /* loaded from: classes21.dex */
    public static class ContextHolder {
        private Context mContext;

        public ContextHolder(Context context) {
            this.mContext = context;
        }

        @Provides
        public Context provideContext() {
            return this.mContext;
        }
    }
}
