package com.xiaopeng.xui.drawable.progress;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.graphics.XLightPaint;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes25.dex */
public class XCircularProgressIndeterminateDrawable extends Drawable {
    private static final float ARC_START_ANGLE = 8.0f;
    private static final float ARC_SWEEP_ANGLE = 340.0f;
    private static final float STROKE_WIDTH_DEFAULT = 0.0f;
    private boolean mEnableLight;
    private int mEndColor;
    private float mInset;
    private float mLightRadius;
    private int mStartColor;
    private float mStrokeWidth;
    protected Paint mPaint = new Paint();
    private Rect mBounds = getBounds();
    private RectF mInnerBounds = new RectF(getBounds());

    public XCircularProgressIndeterminateDrawable() {
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setDither(true);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(@NonNull Resources r, @NonNull XmlPullParser parser, @NonNull AttributeSet attrs) throws IOException, XmlPullParserException {
        super.inflate(r, parser, attrs);
        updateAttr(r, attrs, null);
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(@NonNull Resources r, @NonNull XmlPullParser parser, @NonNull AttributeSet attrs, @Nullable Resources.Theme theme) throws IOException, XmlPullParserException {
        super.inflate(r, parser, attrs, theme);
        updateAttr(r, attrs, theme);
    }

    private void updateAttr(Resources resources, AttributeSet attrs, Resources.Theme theme) {
        TypedArray ta;
        if (theme != null) {
            ta = theme.obtainStyledAttributes(attrs, R.styleable.XCircularProgressIndeterminateDrawable, 0, 0);
        } else {
            ta = resources.obtainAttributes(attrs, R.styleable.XCircularProgressIndeterminateDrawable);
        }
        this.mStartColor = ta.getColor(R.styleable.XCircularProgressIndeterminateDrawable_progress_indeterminate_startColor, resources.getColor(17170445, theme));
        this.mEndColor = ta.getColor(R.styleable.XCircularProgressIndeterminateDrawable_progress_indeterminate_endColor, resources.getColor(R.color.x_circular_progress_primary_color, theme));
        this.mStrokeWidth = ta.getDimension(R.styleable.XCircularProgressIndeterminateDrawable_progress_indeterminate_strokeWidth, 0.0f);
        this.mEnableLight = ta.getBoolean(R.styleable.XCircularProgressIndeterminateDrawable_progress_indeterminate_enable_light, false);
        this.mInset = ta.getDimensionPixelSize(R.styleable.XCircularProgressIndeterminateDrawable_progress_indeterminate_inset, 0);
        this.mLightRadius = ta.getDimensionPixelOffset(R.styleable.XCircularProgressIndeterminateDrawable_progress_indeterminate_light_radius, 0);
        ta.recycle();
    }

    public void setStrokeWidth(float strokeWidth) {
        this.mStrokeWidth = strokeWidth;
    }

    public void setInset(float inset) {
        this.mInset = inset;
    }

    public void setLightRadius(float lightRadius) {
        this.mLightRadius = lightRadius;
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        int[] progressColor = {this.mStartColor, this.mEndColor};
        SweepGradient sweepGradient = new SweepGradient(this.mBounds.centerX(), this.mBounds.centerY(), progressColor, (float[]) null);
        this.mPaint.setShader(sweepGradient);
        if (this.mStrokeWidth == 0.0f) {
            int width = this.mBounds.width();
            this.mStrokeWidth = width / 10.0f;
        }
        this.mPaint.setStrokeWidth(this.mStrokeWidth);
        float borderInset = this.mStrokeWidth / 2.0f;
        this.mInnerBounds.set(bounds);
        RectF rectF = this.mInnerBounds;
        float f = this.mInset;
        rectF.inset(f + borderInset, f + borderInset);
        if (this.mEnableLight) {
            if (this.mLightRadius == 0.0f) {
                this.mLightRadius = this.mStrokeWidth;
            }
            XLightPaint XLightPaint = new XLightPaint(this.mPaint);
            XLightPaint.setLightRadius(this.mLightRadius);
            XLightPaint.apply();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(@NonNull Canvas canvas) {
        canvas.save();
        canvas.drawArc(this.mInnerBounds, ARC_START_ANGLE, ARC_SWEEP_ANGLE, false, this.mPaint);
        canvas.restore();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.mPaint.setAlpha(alpha);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }
}
