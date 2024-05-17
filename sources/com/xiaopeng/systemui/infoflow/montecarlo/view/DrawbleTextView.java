package com.xiaopeng.systemui.infoflow.montecarlo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.xiaopeng.systemui.infoflow.theme.AnimatedTextView;
/* loaded from: classes24.dex */
public class DrawbleTextView extends AnimatedTextView {
    public DrawbleTextView(Context context) {
        super(context);
    }

    public DrawbleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawbleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DrawbleTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        if (drawables != null) {
            Drawable drawableLeft = drawables[0];
            Drawable drawableRight = drawables[2];
            float textWidth = getPaint().measureText(getText().toString());
            if (drawableLeft != null) {
                setGravity(8388627);
                float contentWidth = getCompoundDrawablePadding() + textWidth + drawableLeft.getIntrinsicWidth();
                if (getWidth() - contentWidth > 0.0f) {
                    canvas.translate((((getWidth() - contentWidth) - getPaddingRight()) - getPaddingLeft()) / 2.0f, 0.0f);
                }
            }
            if (drawableRight != null) {
                setGravity(8388629);
                float contentWidth2 = getCompoundDrawablePadding() + textWidth + drawableRight.getIntrinsicWidth();
                if (getWidth() - contentWidth2 > 0.0f) {
                    canvas.translate((-(((getWidth() - contentWidth2) - getPaddingRight()) - getPaddingLeft())) / 2.0f, 0.0f);
                }
            }
            if (drawableRight == null && drawableLeft == null) {
                setGravity(17);
            }
        }
        super.onDraw(canvas);
    }
}
