package com.android.systemui.plugins;

import android.content.Context;
/* loaded from: classes21.dex */
public interface Plugin {
    default int getVersion() {
        return -1;
    }

    default void onCreate(Context sysuiContext, Context pluginContext) {
    }

    default void onDestroy() {
    }
}
