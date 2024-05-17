package com.android.systemui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import com.android.settingslib.Utils;
/* loaded from: classes21.dex */
public class HardwareBgDrawable extends LayerDrawable {
    private final Drawable[] mLayers;
    private final Paint mPaint;
    private int mPoint;
    private boolean mRotatedBackground;
    private final boolean mRoundTop;

    public HardwareBgDrawable(boolean roundTop, boolean roundEnd, Context context) {
        this(roundTop, getLayers(context, roundTop, roundEnd));
    }

    public HardwareBgDrawable(boolean roundTop, Drawable[] layers) {
        super(layers);
        this.mPaint = new Paint();
        if (layers.length != 2) {
            throw new IllegalArgumentException("Need 2 layers");
        }
        this.mRoundTop = roundTop;
        this.mLayers = layers;
    }

    private static Drawable[] getLayers(Context context, boolean roundTop, boolean roundEnd) {
        Drawable[] layers;
        int drawable = roundEnd ? R.drawable.rounded_bg_full : R.drawable.rounded_bg;
        if (roundTop) {
            layers = new Drawable[]{context.getDrawable(drawable).mutate(), context.getDrawable(drawable).mutate()};
        } else {
            Drawable[] drawableArr = new Drawable[2];
            drawableArr[0] = context.getDrawable(drawable).mutate();
            drawableArr[1] = context.getDrawable(roundEnd ? R.drawable.rounded_full_bg_bottom : R.drawable.rounded_bg_bottom).mutate();
            layers = drawableArr;
        }
        layers[1].setTintList(Utils.getColorAttr(context, 16843827));
        return layers;
    }

    public void setCutPoint(int point) {
        this.mPoint = point;
        invalidateSelf();
    }

    public int getCutPoint() {
        return this.mPoint;
    }

    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.mPoint >= 0 && !this.mRotatedBackground) {
            Rect bounds = getBounds();
            int top = bounds.top + this.mPoint;
            if (top > bounds.bottom) {
                top = bounds.bottom;
            }
            if (this.mRoundTop) {
                this.mLayers[0].setBounds(bounds.left, bounds.top, bounds.right, top);
            } else {
                this.mLayers[1].setBounds(bounds.left, top, bounds.right, bounds.bottom);
            }
            if (this.mRoundTop) {
                this.mLayers[1].draw(canvas);
                this.mLayers[0].draw(canvas);
                return;
            }
            this.mLayers[0].draw(canvas);
            this.mLayers[1].draw(canvas);
            return;
        }
        this.mLayers[0].draw(canvas);
    }

    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.mPaint.setAlpha(alpha);
    }

    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }

    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
    public int getOpacity() {
        return -1;
    }

    public void setRotatedBackground(boolean rotatedBackground) {
        this.mRotatedBackground = rotatedBackground;
    }
}
