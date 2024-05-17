package com.android.systemui.statusbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
/* loaded from: classes21.dex */
public class BackDropView extends FrameLayout {
    private Runnable mOnVisibilityChangedRunnable;

    public BackDropView(Context context) {
        super(context);
    }

    public BackDropView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BackDropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BackDropView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override // android.view.View
    protected void onVisibilityChanged(View changedView, int visibility) {
        Runnable runnable;
        super.onVisibilityChanged(changedView, visibility);
        if (changedView == this && (runnable = this.mOnVisibilityChangedRunnable) != null) {
            runnable.run();
        }
    }

    public void setOnVisibilityChangedRunnable(Runnable runnable) {
        this.mOnVisibilityChangedRunnable = runnable;
    }
}
