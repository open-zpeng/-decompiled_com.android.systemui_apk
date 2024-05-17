package com.android.systemui.qs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;
/* loaded from: classes21.dex */
public class NonInterceptingScrollView extends ScrollView {
    public NonInterceptingScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.widget.ScrollView, android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        if (action == 0 && canScrollVertically(1)) {
            requestDisallowInterceptTouchEvent(true);
        }
        return super.onTouchEvent(ev);
    }
}
