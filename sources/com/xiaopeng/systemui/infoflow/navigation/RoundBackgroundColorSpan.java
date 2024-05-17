package com.xiaopeng.systemui.infoflow.navigation;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;
/* loaded from: classes24.dex */
public class RoundBackgroundColorSpan extends ReplacementSpan {
    private int bgColor;
    private int textColor;

    public RoundBackgroundColorSpan(int bgColor, int textColor) {
        this.bgColor = bgColor;
        this.textColor = textColor;
    }

    @Override // android.text.style.ReplacementSpan
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return ((int) paint.measureText(text, start, end)) + 24;
    }

    @Override // android.text.style.ReplacementSpan
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        int color1 = paint.getColor();
        paint.setColor(this.bgColor);
        canvas.drawRoundRect(new RectF(x, 5.0f, ((int) paint.measureText(text, start, end)) + 12 + x, 45.0f), 6.0f, 6.0f, paint);
        paint.setColor(this.textColor);
        canvas.drawText(text, start, end, x + 6.0f, 35.0f, paint);
        paint.setColor(color1);
    }
}
