package com.xiaopeng.xui.widget.slider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import com.xiaopeng.xpui.R;
/* loaded from: classes25.dex */
class IndicatorDrawable extends Drawable {
    private static final int TEXT_PADDING = 24;
    Drawable bgDay9;
    Drawable bgDayDisable9;
    Drawable bgNight9;
    Drawable bgNightDisable9;
    private Rect bonds;
    private float indicatorCenter;
    private boolean isEnabled;
    private boolean isNight;
    private int slideWidth;
    private int textWidth;
    private static int MAX_INDICATOR_SIZE = 116;
    private static float INDICATOR_TEXT_SIZE = 24.0f;
    private static int MIN_INDICATOR_SIZE = 56;
    private static int INDICATOR_TEXT_VERTICAL = 10;
    private static int TEXT_PADDING_TOP = 37;
    private final Paint textPaint = new Paint(1);
    private String indicatorText = "";

    public IndicatorDrawable(Context context) {
        float f = this.indicatorCenter;
        int i = MIN_INDICATOR_SIZE;
        int i2 = INDICATOR_TEXT_VERTICAL;
        this.bonds = new Rect((int) (f - (i / 2)), i2, (int) (f + (i / 2)), i2 + 36);
        this.textPaint.setTextSize(INDICATOR_TEXT_SIZE);
        this.textPaint.setStyle(Paint.Style.FILL);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.bgDay9 = ContextCompat.getDrawable(context, R.drawable.x_slider_tag);
        this.bgNight9 = ContextCompat.getDrawable(context, R.drawable.x_slider_tag_night);
        this.bgDayDisable9 = ContextCompat.getDrawable(context, R.drawable.x_slider_tag);
        this.bgNightDisable9 = ContextCompat.getDrawable(context, R.drawable.x_slider_tag_night_disable);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        this.bgDay9.setBounds(this.bonds);
        this.bgDay9.draw(canvas);
        this.textPaint.setColor(this.isEnabled ? -1 : 1560281087);
        canvas.drawText(this.indicatorText, (this.bonds.left + this.bonds.right) / 2.0f, TEXT_PADDING_TOP, this.textPaint);
    }

    public void updateCenter(float center, String text, boolean isNight, int slideWidth) {
        this.isNight = isNight;
        this.indicatorText = text;
        this.indicatorCenter = center;
        this.textWidth = (int) this.textPaint.measureText(text);
        this.slideWidth = slideWidth;
        resetBounds();
        invalidateSelf();
    }

    public void refreshTheme(Context context) {
        this.bgDay9 = ContextCompat.getDrawable(context, R.drawable.x_slider_tag);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return 0;
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] state) {
        boolean changed = this.bgDay9.setState(state);
        return changed;
    }

    public void draw(Canvas canvas, boolean isNight, boolean enabled) {
        this.isNight = isNight;
        this.isEnabled = enabled;
        draw(canvas);
    }

    private void resetBounds() {
        int specifyWidth = Math.max(this.textWidth + 24, MIN_INDICATOR_SIZE);
        float f = this.indicatorCenter;
        float offsetStart = f - (specifyWidth / 2.0f);
        int i = this.slideWidth;
        float offsetEnd = (i - f) - (specifyWidth / 2.0f);
        if (offsetStart < 0.0f) {
            Rect rect = this.bonds;
            rect.left = 0;
            rect.right = specifyWidth;
        } else if (offsetEnd < 0.0f) {
            Rect rect2 = this.bonds;
            rect2.left = i - specifyWidth;
            rect2.right = i;
        } else {
            Rect rect3 = this.bonds;
            rect3.left = (int) (f - (specifyWidth / 2));
            rect3.right = (int) (f + (specifyWidth / 2));
        }
    }
}
