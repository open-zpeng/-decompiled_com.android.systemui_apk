package com.android.systemui.shared.plugins;

import android.text.TextUtils;
import com.android.systemui.plugins.Plugin;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.plugins.annotations.ProvidesInterface;
/* loaded from: classes21.dex */
public interface PluginManager {
    public static final String NOTIFICATION_CHANNEL_ID = "ALR";
    public static final String PLUGIN_CHANGED = "com.android.systemui.action.PLUGIN_CHANGED";

    <T extends Plugin> void addPluginListener(PluginListener<T> pluginListener, Class<?> cls);

    <T extends Plugin> void addPluginListener(PluginListener<T> pluginListener, Class<?> cls, boolean z);

    <T extends Plugin> void addPluginListener(String str, PluginListener<T> pluginListener, Class<?> cls);

    <T extends Plugin> void addPluginListener(String str, PluginListener<T> pluginListener, Class cls, boolean z);

    <T> boolean dependsOn(Plugin plugin, Class<T> cls);

    <T extends Plugin> T getOneShotPlugin(Class<T> cls);

    <T extends Plugin> T getOneShotPlugin(String str, Class<?> cls);

    String[] getWhitelistedPlugins();

    void removePluginListener(PluginListener<?> pluginListener);

    /* loaded from: classes21.dex */
    public static class Helper {
        public static <P> String getAction(Class<P> cls) {
            ProvidesInterface info = (ProvidesInterface) cls.getDeclaredAnnotation(ProvidesInterface.class);
            if (info == null) {
                throw new RuntimeException(cls + " doesn't provide an interface");
            } else if (TextUtils.isEmpty(info.action())) {
                throw new RuntimeException(cls + " doesn't provide an action");
            } else {
                return info.action();
            }
        }
    }
}
