package com.xiaopeng.systemui.quickmenu.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
/* loaded from: classes24.dex */
public class VerticalSimpleSlider extends SimpleSlider {
    public VerticalSimpleSlider(Context context) {
        super(context);
    }

    public VerticalSimpleSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalSimpleSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider, android.view.View
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override // android.view.View
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider, android.view.View
    public void onDraw(Canvas canvas) {
        canvas.rotate(-90.0f);
        canvas.translate(-getHeight(), 0.0f);
        super.onDraw(canvas);
    }

    @Override // com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider
    protected float getSpacing(int count) {
        return (((getHeight() - getPaddingBottom()) - getPaddingTop()) - (this.mTickMarkPadding * 2.0f)) / count;
    }

    @Override // com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider
    protected int getTickMarkYOffset() {
        return getWidth() >> 1;
    }

    @Override // com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider
    protected float getProgressLength() {
        return (getHeight() - getPaddingBottom()) - getPaddingTop();
    }

    @Override // com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider
    protected float getTouchDownPos(MotionEvent event) {
        return event.getY();
    }

    @Override // com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider
    protected float getTouchProgress(float touchDownPos, MotionEvent event) {
        return Math.round(touchDownPos - event.getY()) / getProgressLength();
    }

    @Override // com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider
    public void updateDrawableBounds() {
        updateDrawableBounds(getHeight(), getWidth());
    }
}
