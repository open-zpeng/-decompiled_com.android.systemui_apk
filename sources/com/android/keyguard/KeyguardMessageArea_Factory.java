package com.android.keyguard;

import android.content.Context;
import android.util.AttributeSet;
import com.android.systemui.statusbar.policy.ConfigurationController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes19.dex */
public final class KeyguardMessageArea_Factory implements Factory<KeyguardMessageArea> {
    private final Provider<AttributeSet> attrsProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;

    public KeyguardMessageArea_Factory(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<ConfigurationController> configurationControllerProvider) {
        this.contextProvider = contextProvider;
        this.attrsProvider = attrsProvider;
        this.configurationControllerProvider = configurationControllerProvider;
    }

    @Override // javax.inject.Provider
    public KeyguardMessageArea get() {
        return provideInstance(this.contextProvider, this.attrsProvider, this.configurationControllerProvider);
    }

    public static KeyguardMessageArea provideInstance(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<ConfigurationController> configurationControllerProvider) {
        return new KeyguardMessageArea(contextProvider.get(), attrsProvider.get(), configurationControllerProvider.get());
    }

    public static KeyguardMessageArea_Factory create(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<ConfigurationController> configurationControllerProvider) {
        return new KeyguardMessageArea_Factory(contextProvider, attrsProvider, configurationControllerProvider);
    }

    public static KeyguardMessageArea newKeyguardMessageArea(Context context, AttributeSet attrs, ConfigurationController configurationController) {
        return new KeyguardMessageArea(context, attrs, configurationController);
    }
}
