package com.xiaopeng.systemui.infoflow.message.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;
/* loaded from: classes24.dex */
public class Shadow {
    private float mShadowDx;
    private float mShadowDy;
    private float mShadowRadius;
    private Paint mPaint = new Paint();
    private int mShadowColorAlpha = 255;
    private Path mPath = new Path();

    public Shadow() {
        this.mPaint.setColor(-1);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setShadowLayer(this.mShadowRadius, this.mShadowDx, this.mShadowDy, -16777216);
    }

    public void setShadowDx(float shadowDx) {
        this.mShadowDx = shadowDx;
        updateShadow();
    }

    public void setShadowDy(float shadowDy) {
        this.mShadowDy = shadowDy;
        updateShadow();
    }

    public void setShadowColorAlpha(int alpha) {
        this.mShadowColorAlpha = alpha;
        this.mPaint.setAlpha(alpha);
        updateShadow();
    }

    public void setShadowRadius(float shadowRadius) {
        this.mShadowRadius = shadowRadius;
        updateShadow();
    }

    private void updateShadow() {
        this.mPaint.setShadowLayer(this.mShadowRadius, this.mShadowDx, this.mShadowDy, -16777216);
    }

    public void applyToView(Canvas canvas, View child) {
        canvas.save();
        this.mPath.reset();
        canvas.drawRoundRect(child.getLeft(), child.getTop(), child.getRight(), child.getBottom(), 26.0f, 26.0f, this.mPaint);
        this.mPath.addRoundRect(child.getLeft(), child.getTop(), child.getRight(), child.getBottom(), 26.0f, 26.0f, Path.Direction.CCW);
        canvas.clipPath(this.mPath);
        canvas.restore();
    }
}
