package com.xiaopeng.xui.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
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
public class XToggleDrawable extends Drawable {
    private static final int BRIGHTNESS_DEFAULT = 0;
    private static final float LIGHT_RADIUS_DEFAULT = 0.0f;
    private static final float LIGHT_STRENGTH_DEFAULT = 0.5f;
    private static final int TOGGLE_STYLE_DEFAULT = 0;
    private float mRoundRadius;
    private XLightPaint mXLightPaint;
    private Paint mPaint = new Paint(1);
    private boolean mChecked = false;

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
            ta = theme.obtainStyledAttributes(attrs, R.styleable.XToggleDrawable, 0, 0);
        } else {
            ta = resources.obtainAttributes(attrs, R.styleable.XToggleDrawable);
        }
        int defaultColor = resources.getColor(R.color.x_theme_primary_normal, theme);
        int color = ta.getColor(R.styleable.XToggleDrawable_toggle_stroke_color, defaultColor);
        float strokeWidth = ta.getDimension(R.styleable.XToggleDrawable_toggle_stroke_width, resources.getDimension(R.dimen.x_toggle_stroke_size));
        boolean enableLight = ta.getBoolean(R.styleable.XToggleDrawable_toggle_enable_light, false);
        float lightRadius = ta.getDimension(R.styleable.XToggleDrawable_toggle_light_radius, 0.0f);
        int brightness = ta.getInt(R.styleable.XToggleDrawable_toggle_brightness, 0);
        float lightStrength = ta.getFloat(R.styleable.XToggleDrawable_toggle_light_strength, 0.5f);
        int lightType = ta.getInt(R.styleable.XToggleDrawable_toggle_light_type, 0);
        int lightColor = ta.getColor(R.styleable.XToggleDrawable_toggle_light_color, defaultColor);
        int toggleStyle = ta.getInt(R.styleable.XToggleDrawable_toggle_style, 0);
        this.mRoundRadius = ta.getDimension(R.styleable.XToggleDrawable_toggle_round_radius, resources.getDimension(R.dimen.x_toggle_rect_round_radius));
        ta.recycle();
        this.mPaint.setColor(color);
        this.mPaint.setStrokeWidth(strokeWidth);
        if (toggleStyle == 0) {
            this.mPaint.setStyle(Paint.Style.STROKE);
        } else if (toggleStyle == 1) {
            this.mPaint.setStyle(Paint.Style.FILL);
        }
        if (enableLight) {
            this.mXLightPaint = new XLightPaint(this.mPaint);
            this.mXLightPaint.setLightRadius(lightRadius);
            this.mXLightPaint.setBrightness(brightness);
            this.mXLightPaint.setLightColor(lightColor);
            this.mXLightPaint.setLightStrength(lightStrength);
            this.mXLightPaint.setLightType(lightType);
            this.mXLightPaint.apply();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(@NonNull Canvas canvas) {
        if (this.mChecked) {
            int count = canvas.save();
            Rect bounds = getBounds();
            float radius = this.mRoundRadius;
            canvas.drawRoundRect(bounds.left, bounds.top, bounds.right, bounds.bottom, radius, radius, this.mPaint);
            canvas.restoreToCount(count);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.mPaint.setAlpha(alpha);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] state) {
        boolean isChecked = false;
        int length = state.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            int s = state[i];
            if (s != 16842912) {
                i++;
            } else {
                isChecked = true;
                break;
            }
        }
        boolean changed = this.mChecked != isChecked;
        this.mChecked = isChecked;
        return changed;
    }
}
