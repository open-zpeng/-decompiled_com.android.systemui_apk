package com.android.systemui.classifier;

import android.content.Context;
import android.os.Handler;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.util.ProximitySensor;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class FalsingManagerProxy_Factory implements Factory<FalsingManagerProxy> {
    private final Provider<Context> contextProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<PluginManager> pluginManagerProvider;
    private final Provider<ProximitySensor> proximitySensorProvider;

    public FalsingManagerProxy_Factory(Provider<Context> contextProvider, Provider<PluginManager> pluginManagerProvider, Provider<Handler> handlerProvider, Provider<ProximitySensor> proximitySensorProvider) {
        this.contextProvider = contextProvider;
        this.pluginManagerProvider = pluginManagerProvider;
        this.handlerProvider = handlerProvider;
        this.proximitySensorProvider = proximitySensorProvider;
    }

    @Override // javax.inject.Provider
    public FalsingManagerProxy get() {
        return provideInstance(this.contextProvider, this.pluginManagerProvider, this.handlerProvider, this.proximitySensorProvider);
    }

    public static FalsingManagerProxy provideInstance(Provider<Context> contextProvider, Provider<PluginManager> pluginManagerProvider, Provider<Handler> handlerProvider, Provider<ProximitySensor> proximitySensorProvider) {
        return new FalsingManagerProxy(contextProvider.get(), pluginManagerProvider.get(), handlerProvider.get(), proximitySensorProvider.get());
    }

    public static FalsingManagerProxy_Factory create(Provider<Context> contextProvider, Provider<PluginManager> pluginManagerProvider, Provider<Handler> handlerProvider, Provider<ProximitySensor> proximitySensorProvider) {
        return new FalsingManagerProxy_Factory(contextProvider, pluginManagerProvider, handlerProvider, proximitySensorProvider);
    }

    public static FalsingManagerProxy newFalsingManagerProxy(Context context, PluginManager pluginManager, Handler handler, ProximitySensor proximitySensor) {
        return new FalsingManagerProxy(context, pluginManager, handler, proximitySensor);
    }
}
