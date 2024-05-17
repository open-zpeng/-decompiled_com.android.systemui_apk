package com.xiaopeng.systemui.infoflow.theme;

import android.content.Context;
import android.util.AttributeSet;
/* loaded from: classes24.dex */
public class MarqueeTextView extends AnimatedTextView {
    public MarqueeTextView(Context context) {
        super(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override // android.view.View
    public boolean isFocused() {
        return true;
    }
}
