package com.android.systemui.plugins;

import com.android.systemui.plugins.annotations.ProvidesInterface;
@ProvidesInterface(action = DozeServicePlugin.ACTION, version = 1)
/* loaded from: classes21.dex */
public interface DozeServicePlugin extends Plugin {
    public static final String ACTION = "com.android.systemui.action.PLUGIN_DOZE";
    public static final int VERSION = 1;

    /* loaded from: classes21.dex */
    public interface RequestDoze {
        void onRequestHideDoze();

        void onRequestShowDoze();
    }

    void onDreamingStarted();

    void onDreamingStopped();

    void setDozeRequester(RequestDoze requestDoze);
}
