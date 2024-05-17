package com.xiaopeng.systemui.infoflow.egg.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
/* loaded from: classes24.dex */
public class PressTextView extends TextView {
    private float mPressAlpha;

    public void setPressAlpha(float pressAlpha) {
        this.mPressAlpha = pressAlpha;
    }

    public PressTextView(Context context) {
        super(context);
        this.mPressAlpha = 0.5f;
    }

    public PressTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mPressAlpha = 0.5f;
    }

    public PressTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mPressAlpha = 0.5f;
    }

    @Override // android.widget.TextView, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        setAlpha((isPressed() || isFocused() || isSelected()) ? this.mPressAlpha : 1.0f);
    }
}
