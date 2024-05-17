package com.android.launcher3.icons;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.DrawableWrapper;
import android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;
/* loaded from: classes19.dex */
public class FixedScaleDrawable extends DrawableWrapper {
    private static final float LEGACY_ICON_SCALE = 0.46669f;
    private float mScaleX;
    private float mScaleY;

    public FixedScaleDrawable() {
        super(new ColorDrawable());
        this.mScaleX = LEGACY_ICON_SCALE;
        this.mScaleY = LEGACY_ICON_SCALE;
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        int saveCount = canvas.save();
        canvas.scale(this.mScaleX, this.mScaleY, getBounds().exactCenterX(), getBounds().exactCenterY());
        super.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) {
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) {
    }

    public void setScale(float scale) {
        float h = getIntrinsicHeight();
        float w = getIntrinsicWidth();
        this.mScaleX = scale * LEGACY_ICON_SCALE;
        this.mScaleY = LEGACY_ICON_SCALE * scale;
        if (h > w && w > 0.0f) {
            this.mScaleX *= w / h;
        } else if (w > h && h > 0.0f) {
            this.mScaleY *= h / w;
        }
    }
}
