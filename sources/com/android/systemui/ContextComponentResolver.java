package com.android.systemui;

import java.util.Map;
import javax.inject.Inject;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public class ContextComponentResolver implements ContextComponentHelper {
    private final Map<Class<?>, Provider<Object>> mCreators;

    /* JADX INFO: Access modifiers changed from: package-private */
    @Inject
    public ContextComponentResolver(Map<Class<?>, Provider<Object>> creators) {
        this.mCreators = creators;
    }

    @Override // com.android.systemui.ContextComponentHelper
    public <T> T resolve(String className) {
        for (Map.Entry<Class<?>, Provider<Object>> p : this.mCreators.entrySet()) {
            if (p.getKey().getName().equals(className)) {
                return (T) p.getValue().get();
            }
        }
        return null;
    }
}
