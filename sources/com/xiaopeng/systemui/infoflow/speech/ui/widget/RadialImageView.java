package com.xiaopeng.systemui.infoflow.speech.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.systemui.infoflow.theme.AnimatedImageView;
@SuppressLint({"AppCompatCustomView"})
/* loaded from: classes24.dex */
public class RadialImageView extends AnimatedImageView {
    private float mAlphaValue;
    private int mCurrentColor;
    private Paint mPaint;
    private int[] mRadiaBgColors;
    private int[] mRadiaBgColorsDay;
    private int[] mRadiaBgColorsNight;
    private RectF mRectF;

    public RadialImageView(Context context) {
        super(context);
        this.mCurrentColor = 0;
        this.mAlphaValue = 0.4f;
        initView();
    }

    public RadialImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mCurrentColor = 0;
        this.mAlphaValue = 0.4f;
        initView();
    }

    public RadialImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mCurrentColor = 0;
        this.mAlphaValue = 0.4f;
        initView();
    }

    private void initView() {
        this.mPaint = new Paint();
        this.mRectF = new RectF();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        setBoundTop(canvas);
        setRadialBg(canvas);
        super.onDraw(canvas);
    }

    private void setRadialBg(Canvas canvas) {
        if (this.mRadiaBgColors != null) {
            int mColor = getColorWithAlpha(this.mAlphaValue, this.mCurrentColor);
            this.mRectF.top = getTop();
            this.mRectF.left = getLeft();
            this.mRectF.right = getRight();
            this.mRectF.bottom = getBottom();
            if (this.mCurrentColor != 0) {
                this.mRadiaBgColors[0] = mColor;
            }
            RadialGradient radialGradient = new RadialGradient(this.mRectF.right / 2.0f, this.mRectF.bottom, getHeight(), this.mRadiaBgColors, (float[]) null, Shader.TileMode.CLAMP);
            this.mPaint.setShader(radialGradient);
            this.mPaint.setAntiAlias(true);
            canvas.drawRect(this.mRectF, this.mPaint);
            canvas.save();
        }
    }

    public void setRadialBg(float alphaValue, int[] colorsDay, int[] radiaBgColorsNight) {
        this.mRadiaBgColorsDay = colorsDay;
        this.mRadiaBgColorsNight = radiaBgColorsNight;
        if (!ThemeManager.isNightMode(this.mContext)) {
            this.mRadiaBgColors = this.mRadiaBgColorsDay;
        } else {
            this.mRadiaBgColors = this.mRadiaBgColorsNight;
        }
        this.mAlphaValue = alphaValue;
    }

    public void setRadialBg(int... colors) {
        this.mRadiaBgColors = colors;
    }

    public void updateBgByProgress(int progress) {
        int[] iArr = this.mRadiaBgColors;
        if (iArr != null) {
            int radiaBgColor = iArr[0];
            iArr[0] = Color.argb((progress * 255) / 100, Color.red(radiaBgColor), Color.green(radiaBgColor), Color.blue(radiaBgColor));
            invalidate();
        }
    }

    private void setBoundTop(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        Path clipPath = new Path();
        float[] radii = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        radii[0] = 24;
        radii[1] = 24;
        radii[2] = 24;
        radii[3] = 24;
        clipPath.addRoundRect(new RectF(0.0f, 0.0f, width, height), radii, Path.Direction.CW);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, 3));
        canvas.clipPath(clipPath);
    }

    public void refreshColor(int currentColor) {
        this.mCurrentColor = currentColor;
        invalidate();
    }

    private int getColorWithAlpha(float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (255.0f * alpha))) << 24;
        int rgb = 16777215 & baseColor;
        return a + rgb;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.theme.AnimatedImageView, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (ThemeManager.isThemeChanged(newConfig)) {
            int[] iArr = this.mRadiaBgColors;
            int[] iArr2 = this.mRadiaBgColorsDay;
            if (iArr == iArr2) {
                this.mRadiaBgColors = this.mRadiaBgColorsNight;
            } else {
                this.mRadiaBgColors = iArr2;
            }
            invalidate();
        }
    }

    public void setRadialBg(int[] radiaBgColorsDay, int[] radiaBgColorsNight) {
        this.mRadiaBgColorsDay = radiaBgColorsDay;
        this.mRadiaBgColorsNight = radiaBgColorsNight;
        if (!ThemeManager.isNightMode(this.mContext)) {
            this.mRadiaBgColors = this.mRadiaBgColorsDay;
        } else {
            this.mRadiaBgColors = this.mRadiaBgColorsNight;
        }
    }
}
