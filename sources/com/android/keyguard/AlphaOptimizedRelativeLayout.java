package com.android.keyguard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
/* loaded from: classes19.dex */
public class AlphaOptimizedRelativeLayout extends RelativeLayout {
    public AlphaOptimizedRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }
}
