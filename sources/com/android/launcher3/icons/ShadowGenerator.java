package com.android.launcher3.icons;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
/* loaded from: classes19.dex */
public class ShadowGenerator {
    private static final int AMBIENT_SHADOW_ALPHA = 30;
    public static final float BLUR_FACTOR = 0.010416667f;
    private static final float HALF_DISTANCE = 0.5f;
    private static final int KEY_SHADOW_ALPHA = 61;
    public static final float KEY_SHADOW_DISTANCE = 0.020833334f;
    private final BlurMaskFilter mDefaultBlurMaskFilter;
    private final int mIconSize;
    private final Paint mBlurPaint = new Paint(3);
    private final Paint mDrawPaint = new Paint(3);

    public ShadowGenerator(int iconSize) {
        this.mIconSize = iconSize;
        this.mDefaultBlurMaskFilter = new BlurMaskFilter(this.mIconSize * 0.010416667f, BlurMaskFilter.Blur.NORMAL);
    }

    public synchronized void recreateIcon(Bitmap icon, Canvas out) {
        recreateIcon(icon, this.mDefaultBlurMaskFilter, 30, 61, out);
    }

    public synchronized void recreateIcon(Bitmap icon, BlurMaskFilter blurMaskFilter, int ambientAlpha, int keyAlpha, Canvas out) {
        int[] offset = new int[2];
        this.mBlurPaint.setMaskFilter(blurMaskFilter);
        Bitmap shadow = icon.extractAlpha(this.mBlurPaint, offset);
        this.mDrawPaint.setAlpha(ambientAlpha);
        out.drawBitmap(shadow, offset[0], offset[1], this.mDrawPaint);
        this.mDrawPaint.setAlpha(keyAlpha);
        out.drawBitmap(shadow, offset[0], offset[1] + (this.mIconSize * 0.020833334f), this.mDrawPaint);
        this.mDrawPaint.setAlpha(255);
        out.drawBitmap(icon, 0.0f, 0.0f, this.mDrawPaint);
    }

    public static float getScaleForBounds(RectF bounds) {
        float scale = 1.0f;
        float minSide = Math.min(Math.min(bounds.left, bounds.right), bounds.top);
        if (minSide < 0.010416667f) {
            scale = 0.48958334f / (0.5f - minSide);
        }
        if (bounds.bottom < 0.03125f) {
            return Math.min(scale, (0.5f - 0.03125f) / (0.5f - bounds.bottom));
        }
        return scale;
    }

    /* loaded from: classes19.dex */
    public static class Builder {
        public final int color;
        public float keyShadowDistance;
        public float radius;
        public float shadowBlur;
        public final RectF bounds = new RectF();
        public int ambientShadowAlpha = 30;
        public int keyShadowAlpha = 61;

        public Builder(int color) {
            this.color = color;
        }

        public Builder setupBlurForSize(int height) {
            this.shadowBlur = (height * 1.0f) / 24.0f;
            this.keyShadowDistance = (height * 1.0f) / 16.0f;
            return this;
        }

        public Bitmap createPill(int width, int height) {
            return createPill(width, height, height / 2.0f);
        }

        public Bitmap createPill(int width, int height, float r) {
            this.radius = r;
            int centerX = Math.round((width / 2.0f) + this.shadowBlur);
            int centerY = Math.round(this.radius + this.shadowBlur + this.keyShadowDistance);
            int center = Math.max(centerX, centerY);
            this.bounds.set(0.0f, 0.0f, width, height);
            this.bounds.offsetTo(center - (width / 2.0f), center - (height / 2.0f));
            int size = center * 2;
            Bitmap result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            drawShadow(new Canvas(result));
            return result;
        }

        public void drawShadow(Canvas c) {
            Paint p = new Paint(3);
            p.setColor(this.color);
            p.setShadowLayer(this.shadowBlur, 0.0f, this.keyShadowDistance, GraphicsUtils.setColorAlphaBound(-16777216, this.keyShadowAlpha));
            RectF rectF = this.bounds;
            float f = this.radius;
            c.drawRoundRect(rectF, f, f, p);
            p.setShadowLayer(this.shadowBlur, 0.0f, 0.0f, GraphicsUtils.setColorAlphaBound(-16777216, this.ambientShadowAlpha));
            RectF rectF2 = this.bounds;
            float f2 = this.radius;
            c.drawRoundRect(rectF2, f2, f2, p);
            if (Color.alpha(this.color) < 255) {
                p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                p.clearShadowLayer();
                p.setColor(-16777216);
                RectF rectF3 = this.bounds;
                float f3 = this.radius;
                c.drawRoundRect(rectF3, f3, f3, p);
                p.setXfermode(null);
                p.setColor(this.color);
                RectF rectF4 = this.bounds;
                float f4 = this.radius;
                c.drawRoundRect(rectF4, f4, f4, p);
            }
        }
    }
}
