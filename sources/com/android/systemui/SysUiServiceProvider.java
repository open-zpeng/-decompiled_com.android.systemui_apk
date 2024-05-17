package com.android.systemui;

import android.content.Context;
/* loaded from: classes21.dex */
public interface SysUiServiceProvider {
    <T> T getComponent(Class<T> cls);

    static <T> T getComponent(Context context, Class<T> interfaceType) {
        return (T) ((SysUiServiceProvider) context.getApplicationContext()).getComponent(interfaceType);
    }
}
