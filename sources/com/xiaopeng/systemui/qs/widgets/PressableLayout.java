package com.xiaopeng.systemui.qs.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
/* loaded from: classes24.dex */
public class PressableLayout extends RelativeLayout {
    public PressableLayout(Context context) {
        super(context);
    }

    public PressableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PressableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PressableLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isEnabled()) {
            return super.dispatchTouchEvent(ev);
        }
        int action = ev.getActionMasked();
        if (action != 0) {
            if (action == 1 || action == 3) {
                setPressed(false);
            }
        } else {
            setPressed(true);
        }
        return super.dispatchTouchEvent(ev);
    }
}
