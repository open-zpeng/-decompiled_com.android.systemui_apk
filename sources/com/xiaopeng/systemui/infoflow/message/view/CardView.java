package com.xiaopeng.systemui.infoflow.message.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.xiaopeng.systemui.infoflow.widget.layer.CardAngleLayout;
/* loaded from: classes24.dex */
public class CardView extends CardAngleLayout {
    private static final String TAG = "CardView";
    private final float SCALE_RATIO;
    private View.OnFocusChangeListener mOnFocusChangeListener;

    public CardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.SCALE_RATIO = 0.98f;
    }

    @Override // com.xiaopeng.systemui.infoflow.widget.ShimmerLayout, com.xiaopeng.systemui.infoflow.widget.IFocusView
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        View.OnFocusChangeListener onFocusChangeListener = this.mOnFocusChangeListener;
        if (onFocusChangeListener != null) {
            onFocusChangeListener.onFocusChange(this, focused);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.widget.layer.CardAngleLayout, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override // com.xiaopeng.systemui.infoflow.widget.layer.CardAngleLayout, com.xiaopeng.systemui.infoflow.widget.ShimmerLayout, android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override // android.view.View
    public void setOnFocusChangeListener(View.OnFocusChangeListener onFocusChangeListener) {
        this.mOnFocusChangeListener = onFocusChangeListener;
    }

    @Override // android.view.View
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        scaleCard(pressed);
    }

    private void scaleCard(boolean pressed) {
        float ration = pressed ? 0.98f : 1.0f;
        setScaleX(ration);
        setScaleY(ration);
    }
}
