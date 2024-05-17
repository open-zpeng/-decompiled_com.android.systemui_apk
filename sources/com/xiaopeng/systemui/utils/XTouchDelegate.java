package com.xiaopeng.systemui.utils;

import android.graphics.Rect;
import android.view.TouchDelegate;
import android.view.View;
/* loaded from: classes24.dex */
public class XTouchDelegate extends TouchDelegate {
    private View mDelegateViewHold;

    public XTouchDelegate(Rect bounds, View delegateView) {
        super(bounds, delegateView);
        this.mDelegateViewHold = delegateView;
    }

    public View getDelegateViewHold() {
        return this.mDelegateViewHold;
    }
}
