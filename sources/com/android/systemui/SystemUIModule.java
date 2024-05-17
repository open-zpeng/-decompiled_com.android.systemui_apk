package com.android.systemui;

import android.content.Context;
import com.android.systemui.assist.AssistModule;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.KeyguardLiftController;
import com.android.systemui.util.AsyncSensorManager;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
@Module(includes = {AssistModule.class})
/* loaded from: classes21.dex */
public abstract class SystemUIModule {
    /* JADX INFO: Access modifiers changed from: package-private */
    @Provides
    @Singleton
    public static KeyguardLiftController provideKeyguardLiftController(Context context, StatusBarStateController statusBarStateController, AsyncSensorManager asyncSensorManager) {
        if (!context.getPackageManager().hasSystemFeature("android.hardware.biometrics.face")) {
            return null;
        }
        return new KeyguardLiftController(context, statusBarStateController, asyncSensorManager);
    }
}
