package com.xiaopeng.systemui.infoflow.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedLinearLayout;
/* loaded from: classes24.dex */
public class SplitLinearLayout extends AlphaOptimizedLinearLayout {
    private static final String TAG = "SplitLinearLayout";
    private int mActionNum;
    private Paint mPaint;

    public SplitLinearLayout(Context context) {
        this(context, null);
    }

    public SplitLinearLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SplitLinearLayout(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        TypedArray a = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.SplitLinearLayout, 0, 0);
        try {
            this.mActionNum = a.getInt(0, 0);
            a.recycle();
            init();
        } catch (Throwable th) {
            a.recycle();
            throw th;
        }
    }

    private void init() {
        this.mPaint = new Paint();
        this.mPaint.setColor(Color.parseColor("#757D8D"));
        this.mPaint.setAntiAlias(true);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawSplitLine(canvas);
    }

    private void drawSplitLine(Canvas canvas) {
        int i = this.mActionNum;
        if (i > 1) {
            if (i == 2) {
                canvas.drawLine(240.0f, 22.0f, 241.0f, 42.0f, this.mPaint);
                return;
            }
            canvas.drawLine(160.0f, 22.0f, 161.0f, 42.0f, this.mPaint);
            canvas.drawLine(320.0f, 22.0f, 321.0f, 42.0f, this.mPaint);
        }
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setActionNum(int actionNum) {
        this.mActionNum = actionNum;
        invalidate();
    }
}
