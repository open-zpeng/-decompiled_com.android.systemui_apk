package com.android.systemui.statusbar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
/* loaded from: classes21.dex */
public class AlphaOptimizedTextView extends TextView {
    public AlphaOptimizedTextView(Context context) {
        super(context);
    }

    public AlphaOptimizedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlphaOptimizedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AlphaOptimizedTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override // android.widget.TextView, android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }
}
