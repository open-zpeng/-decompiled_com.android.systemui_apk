package com.android.systemui;

import dagger.internal.Factory;
import java.util.Map;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class ContextComponentResolver_Factory implements Factory<ContextComponentResolver> {
    private final Provider<Map<Class<?>, Provider<Object>>> creatorsProvider;

    public ContextComponentResolver_Factory(Provider<Map<Class<?>, Provider<Object>>> creatorsProvider) {
        this.creatorsProvider = creatorsProvider;
    }

    @Override // javax.inject.Provider
    public ContextComponentResolver get() {
        return provideInstance(this.creatorsProvider);
    }

    public static ContextComponentResolver provideInstance(Provider<Map<Class<?>, Provider<Object>>> creatorsProvider) {
        return new ContextComponentResolver(creatorsProvider.get());
    }

    public static ContextComponentResolver_Factory create(Provider<Map<Class<?>, Provider<Object>>> creatorsProvider) {
        return new ContextComponentResolver_Factory(creatorsProvider);
    }

    public static ContextComponentResolver newContextComponentResolver(Map<Class<?>, Provider<Object>> creators) {
        return new ContextComponentResolver(creators);
    }
}
