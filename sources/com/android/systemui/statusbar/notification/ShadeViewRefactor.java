package com.android.systemui.statusbar.notification;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@Retention(RetentionPolicy.SOURCE)
/* loaded from: classes21.dex */
public @interface ShadeViewRefactor {

    /* loaded from: classes21.dex */
    public enum RefactorComponent {
        ADAPTER,
        LAYOUT_ALGORITHM,
        STATE_RESOLVER,
        DECORATOR,
        INPUT,
        COORDINATOR,
        SHADE_VIEW
    }

    RefactorComponent value();
}
