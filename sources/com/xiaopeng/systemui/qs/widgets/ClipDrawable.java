package com.xiaopeng.systemui.qs.widgets;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.util.AttributeSet;
import com.android.systemui.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes24.dex */
public class ClipDrawable extends DrawableWrapper {
    private static final int MAX_LEVEL = 10000;
    private static final String TAG = "XClipDrawable";
    protected Path mClipPath;
    protected int mRadiusX;
    protected int mRadiusY;

    public ClipDrawable(Drawable dr) {
        super(dr);
        init();
    }

    public ClipDrawable() {
        this(null);
    }

    private void init() {
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        TypedArray ta;
        super.inflate(r, parser, attrs, theme);
        if (theme != null) {
            ta = theme.obtainStyledAttributes(attrs, R.styleable.ClipDrawable, 0, 0);
        } else {
            ta = r.obtainAttributes(attrs, R.styleable.ClipDrawable);
        }
        Drawable drawable = ta.getDrawable(0);
        if (drawable != null) {
            setDrawable(drawable);
        } else {
            inflateChildDrawable(r, parser, attrs, theme);
        }
        this.mRadiusX = ta.getDimensionPixelOffset(1, 0);
        this.mRadiusY = ta.getDimensionPixelOffset(2, 0);
        ta.recycle();
    }

    private void inflateChildDrawable(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        Drawable dr = null;
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1 || (type == 3 && parser.getDepth() <= outerDepth)) {
                break;
            } else if (type == 2) {
                dr = Drawable.createFromXmlInner(r, parser, attrs, theme);
            }
        }
        if (dr != null) {
            setDrawable(dr);
        }
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    protected boolean onLevelChange(int level) {
        super.onLevelChange(level);
        invalidateSelf();
        return true;
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        int level = getLevel();
        int w = bounds.width();
        int h = bounds.height();
        this.mClipPath = generateRoundRect(bounds.left, bounds.top, bounds.left + (w - (((10000 - level) * w) / 10000)), bounds.top + h, this.mRadiusX, this.mRadiusY);
        drawSliderRect(this.mClipPath, canvas);
    }

    protected void drawSliderRect(Path sliderPath, Canvas canvas) {
        Drawable dr = getDrawable();
        canvas.save();
        canvas.clipPath(sliderPath);
        dr.draw(canvas);
        canvas.restore();
    }

    public static Path generateRoundRect(float left, float top, float right, float bottom, float rx, float ry) {
        Path path = new Path();
        float[] radii = {0.0f, 0.0f, rx, ry, rx, ry, 0.0f, 0.0f};
        path.addRoundRect(left, top, right, bottom, radii, Path.Direction.CW);
        return path;
    }
}
