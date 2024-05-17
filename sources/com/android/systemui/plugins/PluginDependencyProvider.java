package com.android.systemui.plugins;

import android.util.ArrayMap;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.PluginDependency;
import com.android.systemui.shared.plugins.PluginManager;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class PluginDependencyProvider extends PluginDependency.DependencyProvider {
    private final ArrayMap<Class<?>, Object> mDependencies = new ArrayMap<>();
    private final PluginManager mManager;

    @Inject
    public PluginDependencyProvider(PluginManager manager) {
        this.mManager = manager;
        PluginDependency.sProvider = this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T> void allowPluginDependency(Class<T> cls) {
        allowPluginDependency(cls, Dependency.get(cls));
    }

    public <T> void allowPluginDependency(Class<T> cls, T obj) {
        synchronized (this.mDependencies) {
            this.mDependencies.put(cls, obj);
        }
    }

    @Override // com.android.systemui.plugins.PluginDependency.DependencyProvider
    <T> T get(Plugin p, Class<T> cls) {
        T t;
        if (!this.mManager.dependsOn(p, cls)) {
            throw new IllegalArgumentException(p.getClass() + " does not depend on " + cls);
        }
        synchronized (this.mDependencies) {
            if (!this.mDependencies.containsKey(cls)) {
                throw new IllegalArgumentException("Unknown dependency " + cls);
            }
            t = (T) this.mDependencies.get(cls);
        }
        return t;
    }
}
