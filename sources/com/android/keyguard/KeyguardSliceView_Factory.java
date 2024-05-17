package com.android.keyguard;

import android.content.Context;
import android.util.AttributeSet;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.policy.ConfigurationController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes19.dex */
public final class KeyguardSliceView_Factory implements Factory<KeyguardSliceView> {
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<AttributeSet> attrsProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;

    public KeyguardSliceView_Factory(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<ActivityStarter> activityStarterProvider, Provider<ConfigurationController> configurationControllerProvider) {
        this.contextProvider = contextProvider;
        this.attrsProvider = attrsProvider;
        this.activityStarterProvider = activityStarterProvider;
        this.configurationControllerProvider = configurationControllerProvider;
    }

    @Override // javax.inject.Provider
    public KeyguardSliceView get() {
        return provideInstance(this.contextProvider, this.attrsProvider, this.activityStarterProvider, this.configurationControllerProvider);
    }

    public static KeyguardSliceView provideInstance(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<ActivityStarter> activityStarterProvider, Provider<ConfigurationController> configurationControllerProvider) {
        return new KeyguardSliceView(contextProvider.get(), attrsProvider.get(), activityStarterProvider.get(), configurationControllerProvider.get());
    }

    public static KeyguardSliceView_Factory create(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<ActivityStarter> activityStarterProvider, Provider<ConfigurationController> configurationControllerProvider) {
        return new KeyguardSliceView_Factory(contextProvider, attrsProvider, activityStarterProvider, configurationControllerProvider);
    }

    public static KeyguardSliceView newKeyguardSliceView(Context context, AttributeSet attrs, ActivityStarter activityStarter, ConfigurationController configurationController) {
        return new KeyguardSliceView(context, attrs, activityStarter, configurationController);
    }
}
