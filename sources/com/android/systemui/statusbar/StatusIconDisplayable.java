package com.android.systemui.statusbar;

import com.android.systemui.plugins.DarkIconDispatcher;
/* loaded from: classes21.dex */
public interface StatusIconDisplayable extends DarkIconDispatcher.DarkReceiver {
    String getSlot();

    int getVisibleState();

    boolean isIconVisible();

    void setDecorColor(int i);

    void setStaticDrawableColor(int i);

    void setVisibleState(int i, boolean z);

    default void setVisibleState(int state) {
        setVisibleState(state, false);
    }

    default boolean isIconBlocked() {
        return false;
    }
}
