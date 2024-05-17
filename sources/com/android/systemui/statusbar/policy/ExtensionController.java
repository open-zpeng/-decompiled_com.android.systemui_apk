package com.android.systemui.statusbar.policy;

import android.content.Context;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
/* loaded from: classes21.dex */
public interface ExtensionController {

    /* loaded from: classes21.dex */
    public interface Extension<T> {
        void addCallback(Consumer<T> consumer);

        void clearItem(boolean z);

        void destroy();

        T get();

        Context getContext();

        T reload();
    }

    /* loaded from: classes21.dex */
    public interface ExtensionBuilder<T> {
        Extension build();

        ExtensionBuilder<T> withCallback(Consumer<T> consumer);

        ExtensionBuilder<T> withDefault(Supplier<T> supplier);

        ExtensionBuilder<T> withFeature(String str, Supplier<T> supplier);

        <P extends T> ExtensionBuilder<T> withPlugin(Class<P> cls);

        <P extends T> ExtensionBuilder<T> withPlugin(Class<P> cls, String str);

        <P> ExtensionBuilder<T> withPlugin(Class<P> cls, String str, PluginConverter<T, P> pluginConverter);

        ExtensionBuilder<T> withTunerFactory(TunerFactory<T> tunerFactory);

        ExtensionBuilder<T> withUiMode(int i, Supplier<T> supplier);
    }

    /* loaded from: classes21.dex */
    public interface PluginConverter<T, P> {
        T getInterfaceFromPlugin(P p);
    }

    /* loaded from: classes21.dex */
    public interface TunerFactory<T> {
        T create(Map<String, String> map);

        String[] keys();
    }

    <T> ExtensionBuilder<T> newExtension(Class<T> cls);
}
