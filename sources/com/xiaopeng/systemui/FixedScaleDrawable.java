package com.xiaopeng.systemui;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
@TargetApi(24)
/* loaded from: classes24.dex */
public class FixedScaleDrawable extends DrawableWrapper {
    private static final float LEGACY_ICON_SCALE = 0.67f;
    private static final String TAG = "FixedScaleDrawable";
    private float mScaleX;
    private float mScaleY;

    public FixedScaleDrawable(Drawable drawable) {
        super(drawable);
        this.mScaleX = LEGACY_ICON_SCALE;
        this.mScaleY = LEGACY_ICON_SCALE;
    }

    public FixedScaleDrawable() {
        this(new ColorDrawable());
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        int saveCount = canvas.save();
        canvas.scale(this.mScaleX, this.mScaleY, getBounds().exactCenterX(), getBounds().exactCenterY());
        super.draw(canvas);
        canvas.restoreToCount(saveCount);
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
