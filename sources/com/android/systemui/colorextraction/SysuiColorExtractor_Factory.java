package com.android.systemui.colorextraction;

import android.content.Context;
import com.android.systemui.statusbar.policy.ConfigurationController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class SysuiColorExtractor_Factory implements Factory<SysuiColorExtractor> {
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;

    public SysuiColorExtractor_Factory(Provider<Context> contextProvider, Provider<ConfigurationController> configurationControllerProvider) {
        this.contextProvider = contextProvider;
        this.configurationControllerProvider = configurationControllerProvider;
    }

    @Override // javax.inject.Provider
    public SysuiColorExtractor get() {
        return provideInstance(this.contextProvider, this.configurationControllerProvider);
    }

    public static SysuiColorExtractor provideInstance(Provider<Context> contextProvider, Provider<ConfigurationController> configurationControllerProvider) {
        return new SysuiColorExtractor(contextProvider.get(), configurationControllerProvider.get());
    }

    public static SysuiColorExtractor_Factory create(Provider<Context> contextProvider, Provider<ConfigurationController> configurationControllerProvider) {
        return new SysuiColorExtractor_Factory(contextProvider, configurationControllerProvider);
    }

    public static SysuiColorExtractor newSysuiColorExtractor(Context context, ConfigurationController configurationController) {
        return new SysuiColorExtractor(context, configurationController);
    }
}
