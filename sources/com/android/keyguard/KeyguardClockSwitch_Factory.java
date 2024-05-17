package com.android.keyguard;

import android.content.Context;
import android.util.AttributeSet;
import com.android.keyguard.clock.ClockManager;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes19.dex */
public final class KeyguardClockSwitch_Factory implements Factory<KeyguardClockSwitch> {
    private final Provider<AttributeSet> attrsProvider;
    private final Provider<ClockManager> clockManagerProvider;
    private final Provider<SysuiColorExtractor> colorExtractorProvider;
    private final Provider<Context> contextProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;

    public KeyguardClockSwitch_Factory(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<SysuiColorExtractor> colorExtractorProvider, Provider<ClockManager> clockManagerProvider) {
        this.contextProvider = contextProvider;
        this.attrsProvider = attrsProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.colorExtractorProvider = colorExtractorProvider;
        this.clockManagerProvider = clockManagerProvider;
    }

    @Override // javax.inject.Provider
    public KeyguardClockSwitch get() {
        return provideInstance(this.contextProvider, this.attrsProvider, this.statusBarStateControllerProvider, this.colorExtractorProvider, this.clockManagerProvider);
    }

    public static KeyguardClockSwitch provideInstance(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<SysuiColorExtractor> colorExtractorProvider, Provider<ClockManager> clockManagerProvider) {
        return new KeyguardClockSwitch(contextProvider.get(), attrsProvider.get(), statusBarStateControllerProvider.get(), colorExtractorProvider.get(), clockManagerProvider.get());
    }

    public static KeyguardClockSwitch_Factory create(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<SysuiColorExtractor> colorExtractorProvider, Provider<ClockManager> clockManagerProvider) {
        return new KeyguardClockSwitch_Factory(contextProvider, attrsProvider, statusBarStateControllerProvider, colorExtractorProvider, clockManagerProvider);
    }

    public static KeyguardClockSwitch newKeyguardClockSwitch(Context context, AttributeSet attrs, StatusBarStateController statusBarStateController, SysuiColorExtractor colorExtractor, ClockManager clockManager) {
        return new KeyguardClockSwitch(context, attrs, statusBarStateController, colorExtractor, clockManager);
    }
}
