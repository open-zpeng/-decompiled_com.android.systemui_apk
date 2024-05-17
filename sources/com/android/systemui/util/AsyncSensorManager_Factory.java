package com.android.systemui.util;

import android.content.Context;
import com.android.systemui.shared.plugins.PluginManager;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class AsyncSensorManager_Factory implements Factory<AsyncSensorManager> {
    private final Provider<Context> contextProvider;
    private final Provider<PluginManager> pluginManagerProvider;

    public AsyncSensorManager_Factory(Provider<Context> contextProvider, Provider<PluginManager> pluginManagerProvider) {
        this.contextProvider = contextProvider;
        this.pluginManagerProvider = pluginManagerProvider;
    }

    @Override // javax.inject.Provider
    public AsyncSensorManager get() {
        return provideInstance(this.contextProvider, this.pluginManagerProvider);
    }

    public static AsyncSensorManager provideInstance(Provider<Context> contextProvider, Provider<PluginManager> pluginManagerProvider) {
        return new AsyncSensorManager(contextProvider.get(), pluginManagerProvider.get());
    }

    public static AsyncSensorManager_Factory create(Provider<Context> contextProvider, Provider<PluginManager> pluginManagerProvider) {
        return new AsyncSensorManager_Factory(contextProvider, pluginManagerProvider);
    }

    public static AsyncSensorManager newAsyncSensorManager(Context context, PluginManager pluginManager) {
        return new AsyncSensorManager(context, pluginManager);
    }
}
