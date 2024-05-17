package com.xiaopeng.xui.drawable.progress;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
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
public class XCircularProgressPgDrawable extends Drawable {
    private static final float PROGRESS_STROKE_WIDTH_DEFAULT = 0.0f;
    private boolean mEnableLight;
    private float mInset;
    private float mLightRadius;
    private int mProgressColor;
    private float mStrokeWidth;
    protected Paint mPaint = new Paint();
    private Rect mOutBounds = getBounds();
    private RectF mInnerBounds = new RectF(getBounds());

    public XCircularProgressPgDrawable() {
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
            ta = theme.obtainStyledAttributes(attrs, R.styleable.XCircularProgressPgDrawable, 0, 0);
        } else {
            ta = resources.obtainAttributes(attrs, R.styleable.XCircularProgressPgDrawable);
        }
        this.mProgressColor = ta.getColor(R.styleable.XCircularProgressPgDrawable_progress_color, resources.getColor(R.color.x_circular_progress_primary_color, theme));
        this.mStrokeWidth = ta.getDimension(R.styleable.XCircularProgressPgDrawable_progress_pg_strokeWidth, 0.0f);
        this.mEnableLight = ta.getBoolean(R.styleable.XCircularProgressPgDrawable_progress_enable_light, false);
        this.mInset = ta.getDimensionPixelSize(R.styleable.XCircularProgressPgDrawable_progress_pg_inset, 0);
        this.mLightRadius = ta.getDimensionPixelOffset(R.styleable.XCircularProgressPgDrawable_progress_pg_light_radius, 0);
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
        this.mInnerBounds.set(bounds);
        if (this.mStrokeWidth == 0.0f) {
            this.mStrokeWidth = (this.mOutBounds.width() * 1.0f) / 10.0f;
        }
        float borderInset = this.mStrokeWidth / 2.0f;
        RectF rectF = this.mInnerBounds;
        float f = this.mInset;
        rectF.inset(f + borderInset, f + borderInset);
        this.mPaint.setColor(this.mProgressColor);
        this.mPaint.setStrokeWidth(this.mStrokeWidth);
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
        float sweepAngle = ((getLevel() * 1.0f) / 10000.0f) * 360.0f;
        canvas.drawArc(this.mInnerBounds, -90.0f, sweepAngle, false, this.mPaint);
        canvas.restore();
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onLevelChange(int level) {
        invalidateSelf();
        return super.onLevelChange(level);
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
