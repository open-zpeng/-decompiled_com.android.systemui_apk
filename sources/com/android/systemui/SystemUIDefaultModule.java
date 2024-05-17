package com.android.systemui;

import android.content.Context;
import androidx.annotation.Nullable;
import com.android.systemui.dock.DockManager;
import com.android.systemui.dock.DockManagerImpl;
import com.android.systemui.power.EnhancedEstimates;
import com.android.systemui.power.EnhancedEstimatesImpl;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl;
import com.android.systemui.statusbar.notification.collection.NotificationData;
import com.android.systemui.statusbar.phone.KeyguardEnvironmentImpl;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
@Module
/* loaded from: classes21.dex */
abstract class SystemUIDefaultModule {
    @Binds
    abstract DockManager bindDockManager(DockManagerImpl dockManagerImpl);

    @Binds
    abstract EnhancedEstimates bindEnhancedEstimates(EnhancedEstimatesImpl enhancedEstimatesImpl);

    @Binds
    abstract NotificationData.KeyguardEnvironment bindKeyguardEnvironment(KeyguardEnvironmentImpl keyguardEnvironmentImpl);

    @Binds
    abstract NotificationLockscreenUserManager bindNotificationLockscreenUserManager(NotificationLockscreenUserManagerImpl notificationLockscreenUserManagerImpl);

    SystemUIDefaultModule() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Provides
    @Named(Dependency.LEAK_REPORT_EMAIL_NAME)
    @Nullable
    @Singleton
    public static String provideLeakReportEmail() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Provides
    @Singleton
    public static ShadeController provideShadeController(Context context) {
        return (ShadeController) SysUiServiceProvider.getComponent(context, StatusBar.class);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Provides
    @Singleton
    @Named(Dependency.ALLOW_NOTIFICATION_LONG_PRESS_NAME)
    public static boolean provideAllowNotificationLongPress() {
        return true;
    }
}
