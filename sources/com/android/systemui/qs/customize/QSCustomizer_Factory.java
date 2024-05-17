package com.android.systemui.qs.customize;

import android.content.Context;
import android.util.AttributeSet;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.statusbar.phone.LightBarController;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class QSCustomizer_Factory implements Factory<QSCustomizer> {
    private final Provider<AttributeSet> attrsProvider;
    private final Provider<Context> contextProvider;
    private final Provider<KeyguardMonitor> keyguardMonitorProvider;
    private final Provider<LightBarController> lightBarControllerProvider;
    private final Provider<ScreenLifecycle> screenLifecycleProvider;

    public QSCustomizer_Factory(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<LightBarController> lightBarControllerProvider, Provider<KeyguardMonitor> keyguardMonitorProvider, Provider<ScreenLifecycle> screenLifecycleProvider) {
        this.contextProvider = contextProvider;
        this.attrsProvider = attrsProvider;
        this.lightBarControllerProvider = lightBarControllerProvider;
        this.keyguardMonitorProvider = keyguardMonitorProvider;
        this.screenLifecycleProvider = screenLifecycleProvider;
    }

    @Override // javax.inject.Provider
    public QSCustomizer get() {
        return provideInstance(this.contextProvider, this.attrsProvider, this.lightBarControllerProvider, this.keyguardMonitorProvider, this.screenLifecycleProvider);
    }

    public static QSCustomizer provideInstance(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<LightBarController> lightBarControllerProvider, Provider<KeyguardMonitor> keyguardMonitorProvider, Provider<ScreenLifecycle> screenLifecycleProvider) {
        return new QSCustomizer(contextProvider.get(), attrsProvider.get(), lightBarControllerProvider.get(), keyguardMonitorProvider.get(), screenLifecycleProvider.get());
    }

    public static QSCustomizer_Factory create(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<LightBarController> lightBarControllerProvider, Provider<KeyguardMonitor> keyguardMonitorProvider, Provider<ScreenLifecycle> screenLifecycleProvider) {
        return new QSCustomizer_Factory(contextProvider, attrsProvider, lightBarControllerProvider, keyguardMonitorProvider, screenLifecycleProvider);
    }

    public static QSCustomizer newQSCustomizer(Context context, AttributeSet attrs, LightBarController lightBarController, KeyguardMonitor keyguardMonitor, ScreenLifecycle screenLifecycle) {
        return new QSCustomizer(context, attrs, lightBarController, keyguardMonitor, screenLifecycle);
    }
}
