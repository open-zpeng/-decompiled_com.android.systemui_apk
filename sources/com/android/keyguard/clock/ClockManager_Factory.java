package com.android.keyguard.clock;

import android.content.Context;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.dock.DockManager;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.util.InjectionInflationController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes19.dex */
public final class ClockManager_Factory implements Factory<ClockManager> {
    private final Provider<SysuiColorExtractor> colorExtractorProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DockManager> dockManagerProvider;
    private final Provider<InjectionInflationController> injectionInflaterProvider;
    private final Provider<PluginManager> pluginManagerProvider;

    public ClockManager_Factory(Provider<Context> contextProvider, Provider<InjectionInflationController> injectionInflaterProvider, Provider<PluginManager> pluginManagerProvider, Provider<SysuiColorExtractor> colorExtractorProvider, Provider<DockManager> dockManagerProvider) {
        this.contextProvider = contextProvider;
        this.injectionInflaterProvider = injectionInflaterProvider;
        this.pluginManagerProvider = pluginManagerProvider;
        this.colorExtractorProvider = colorExtractorProvider;
        this.dockManagerProvider = dockManagerProvider;
    }

    @Override // javax.inject.Provider
    public ClockManager get() {
        return provideInstance(this.contextProvider, this.injectionInflaterProvider, this.pluginManagerProvider, this.colorExtractorProvider, this.dockManagerProvider);
    }

    public static ClockManager provideInstance(Provider<Context> contextProvider, Provider<InjectionInflationController> injectionInflaterProvider, Provider<PluginManager> pluginManagerProvider, Provider<SysuiColorExtractor> colorExtractorProvider, Provider<DockManager> dockManagerProvider) {
        return new ClockManager(contextProvider.get(), injectionInflaterProvider.get(), pluginManagerProvider.get(), colorExtractorProvider.get(), dockManagerProvider.get());
    }

    public static ClockManager_Factory create(Provider<Context> contextProvider, Provider<InjectionInflationController> injectionInflaterProvider, Provider<PluginManager> pluginManagerProvider, Provider<SysuiColorExtractor> colorExtractorProvider, Provider<DockManager> dockManagerProvider) {
        return new ClockManager_Factory(contextProvider, injectionInflaterProvider, pluginManagerProvider, colorExtractorProvider, dockManagerProvider);
    }

    public static ClockManager newClockManager(Context context, InjectionInflationController injectionInflater, PluginManager pluginManager, SysuiColorExtractor colorExtractor, DockManager dockManager) {
        return new ClockManager(context, injectionInflater, pluginManager, colorExtractor, dockManager);
    }
}
