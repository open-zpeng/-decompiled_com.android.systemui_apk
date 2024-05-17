package com.android.systemui.appops;

import java.util.List;
/* loaded from: classes21.dex */
public interface AppOpsController {

    /* loaded from: classes21.dex */
    public interface Callback {
        void onActiveStateChanged(int i, int i2, String str, boolean z);
    }

    void addCallback(int[] iArr, Callback callback);

    List<AppOpItem> getActiveAppOps();

    List<AppOpItem> getActiveAppOpsForUser(int i);

    void removeCallback(int[] iArr, Callback callback);
}
