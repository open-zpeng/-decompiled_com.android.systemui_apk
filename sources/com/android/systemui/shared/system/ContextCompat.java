package com.android.systemui.shared.system;

import android.content.Context;
/* loaded from: classes21.dex */
public class ContextCompat {
    private final Context mWrapped;

    public ContextCompat(Context context) {
        this.mWrapped = context;
    }

    public int getUserId() {
        return this.mWrapped.getUserId();
    }
}
