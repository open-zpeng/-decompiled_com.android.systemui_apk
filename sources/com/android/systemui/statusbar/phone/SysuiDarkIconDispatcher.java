package com.android.systemui.statusbar.phone;

import com.android.systemui.Dumpable;
import com.android.systemui.plugins.DarkIconDispatcher;
/* loaded from: classes21.dex */
public interface SysuiDarkIconDispatcher extends DarkIconDispatcher, Dumpable {
    LightBarTransitionsController getTransitionsController();
}
