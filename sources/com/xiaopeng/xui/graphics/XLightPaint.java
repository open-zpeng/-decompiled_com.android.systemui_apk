package com.xiaopeng.xui.graphics;

import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes25.dex */
public class XLightPaint {
    public static final int LIGHT_TYPE_BLUR_MASK_FILTER = 0;
    public static final int LIGHT_TYPE_SHADOW_LAYER = 1;
    private static float MAX_LIGHT_RADIUS = 200.0f;
    private BlurMaskFilter mBlurMaskFilter;
    private LightingColorFilter mLightingColorFilter;
    private Paint mPaint;
    @ColorInt
    private int mLightColor = -1;
    private int mLightAlpha = 255;
    private int mBrightness = 0;
    @FloatRange(from = 0.0d, to = 1.0d)
    private float mLightStrength = 1.0f;
    private int mLightType = 0;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes25.dex */
    public @interface LightType {
    }

    public XLightPaint(Paint paint) {
        this.mPaint = paint;
    }

    public void setLightType(int lightType) {
        this.mLightType = lightType;
    }

    public void setLightRadius(float maxLightRadius) {
        MAX_LIGHT_RADIUS = maxLightRadius;
    }

    public void setBrightness(@IntRange(from = 0, to = 255) int add) {
        this.mBrightness = add;
    }

    public void setLightColor(@ColorInt int lightColor) {
        this.mLightColor = lightColor;
    }

    public void setLightStrength(@FloatRange(from = 0.0d, to = 1.0d) float lightStrength) {
        this.mLightStrength = lightStrength;
    }

    public void apply() {
        float f = MAX_LIGHT_RADIUS;
        float f2 = this.mLightStrength;
        float radius = f * f2;
        int i = this.mBrightness;
        if (i > 0) {
            int color = (int) (f2 * i);
            this.mLightingColorFilter = new LightingColorFilter(-1, Color.argb(this.mLightAlpha, color, color, color));
            this.mPaint.setColorFilter(this.mLightingColorFilter);
        }
        int color2 = this.mLightType;
        if (color2 == 0) {
            if (radius > 0.0f) {
                this.mBlurMaskFilter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.SOLID);
                this.mPaint.setMaskFilter(this.mBlurMaskFilter);
                return;
            }
            this.mPaint.setMaskFilter(null);
        } else if (color2 == 1) {
            int i2 = this.mLightColor;
            if (i2 == -1) {
                throw new IllegalArgumentException("Please set light color.");
            }
            this.mPaint.setShadowLayer(radius, 0.0f, 0.0f, i2);
        }
    }

    public Paint getPaint() {
        return this.mPaint;
    }
}
