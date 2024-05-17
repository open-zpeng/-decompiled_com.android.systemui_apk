package com.android.systemui.plugins;

import com.android.systemui.plugins.annotations.ProvidesInterface;
@ProvidesInterface(version = 1)
/* loaded from: classes21.dex */
public class PluginDependency {
    public static final int VERSION = 1;
    static DependencyProvider sProvider;

    /* loaded from: classes21.dex */
    static abstract class DependencyProvider {
        abstract <T> T get(Plugin plugin, Class<T> cls);
    }

    public static <T> T get(Plugin p, Class<T> cls) {
        return (T) sProvider.get(p, cls);
    }
}
