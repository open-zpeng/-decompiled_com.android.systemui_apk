package com.android.launcher3.icons;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.util.Log;
import android.view.ViewDebug;
import com.android.launcher3.icons.ShadowGenerator;
/* loaded from: classes19.dex */
public class DotRenderer {
    private static final float SIZE_PERCENTAGE = 0.228f;
    private static final String TAG = "DotRenderer";
    private final Bitmap mBackgroundWithShadow;
    private final float mBitmapOffset;
    private final Paint mCirclePaint = new Paint(3);
    private final float mCircleRadius;
    private final float[] mLeftDotPosition;
    private final float[] mRightDotPosition;

    /* loaded from: classes19.dex */
    public static class DrawParams {
        @ViewDebug.ExportedProperty(category = "notification dot", formatToHexString = true)
        public int color;
        @ViewDebug.ExportedProperty(category = "notification dot")
        public Rect iconBounds = new Rect();
        @ViewDebug.ExportedProperty(category = "notification dot")
        public boolean leftAlign;
        @ViewDebug.ExportedProperty(category = "notification dot")
        public float scale;
    }

    public DotRenderer(int iconSizePx, Path iconShapePath, int pathSize) {
        int size = Math.round(iconSizePx * SIZE_PERCENTAGE);
        ShadowGenerator.Builder builder = new ShadowGenerator.Builder(0);
        builder.ambientShadowAlpha = 88;
        this.mBackgroundWithShadow = builder.setupBlurForSize(size).createPill(size, size);
        this.mCircleRadius = builder.radius;
        this.mBitmapOffset = (-this.mBackgroundWithShadow.getHeight()) * 0.5f;
        this.mLeftDotPosition = getPathPoint(iconShapePath, pathSize, -1.0f);
        this.mRightDotPosition = getPathPoint(iconShapePath, pathSize, 1.0f);
    }

    private static float[] getPathPoint(Path path, float size, float direction) {
        float halfSize = size / 2.0f;
        float x = (direction * halfSize) + halfSize;
        Path trianglePath = new Path();
        trianglePath.moveTo(halfSize, halfSize);
        trianglePath.lineTo((1.0f * direction) + x, 0.0f);
        trianglePath.lineTo(x, -1.0f);
        trianglePath.close();
        trianglePath.op(path, Path.Op.INTERSECT);
        new PathMeasure(trianglePath, false).getPosTan(0.0f, pos, null);
        float[] pos = {pos[0] / size, pos[1] / size};
        return pos;
    }

    public float[] getLeftDotPosition() {
        return this.mLeftDotPosition;
    }

    public float[] getRightDotPosition() {
        return this.mRightDotPosition;
    }

    public void draw(Canvas canvas, DrawParams params) {
        float offsetX;
        if (params == null) {
            Log.e(TAG, "Invalid null argument(s) passed in call to draw.");
            return;
        }
        canvas.save();
        Rect iconBounds = params.iconBounds;
        float[] dotPosition = params.leftAlign ? this.mLeftDotPosition : this.mRightDotPosition;
        float dotCenterX = iconBounds.left + (iconBounds.width() * dotPosition[0]);
        float dotCenterY = iconBounds.top + (iconBounds.height() * dotPosition[1]);
        Rect canvasBounds = canvas.getClipBounds();
        if (params.leftAlign) {
            offsetX = Math.max(0.0f, canvasBounds.left - (this.mBitmapOffset + dotCenterX));
        } else {
            offsetX = Math.min(0.0f, canvasBounds.right - (dotCenterX - this.mBitmapOffset));
        }
        float offsetY = Math.max(0.0f, canvasBounds.top - (this.mBitmapOffset + dotCenterY));
        canvas.translate(dotCenterX + offsetX, dotCenterY + offsetY);
        canvas.scale(params.scale, params.scale);
        this.mCirclePaint.setColor(-16777216);
        Bitmap bitmap = this.mBackgroundWithShadow;
        float f = this.mBitmapOffset;
        canvas.drawBitmap(bitmap, f, f, this.mCirclePaint);
        this.mCirclePaint.setColor(params.color);
        canvas.drawCircle(0.0f, 0.0f, this.mCircleRadius, this.mCirclePaint);
        canvas.restore();
    }
}
