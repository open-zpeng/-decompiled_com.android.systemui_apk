package com.xiaopeng.speech.vui.event;

import android.content.Context;
import androidx.recyclerview.widget.LinearSmoothScroller;
/* loaded from: classes.dex */
public class EndSmoothScroller extends LinearSmoothScroller {
    public EndSmoothScroller(Context context) {
        super(context);
    }

    @Override // androidx.recyclerview.widget.LinearSmoothScroller
    protected int getHorizontalSnapPreference() {
        return 1;
    }

    @Override // androidx.recyclerview.widget.LinearSmoothScroller
    protected int getVerticalSnapPreference() {
        return 1;
    }
}
