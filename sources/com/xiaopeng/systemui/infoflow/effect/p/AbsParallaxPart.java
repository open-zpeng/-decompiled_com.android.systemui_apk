package com.xiaopeng.systemui.infoflow.effect.p;

import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
/* loaded from: classes24.dex */
abstract class AbsParallaxPart implements Part {
    float centerOffsetX;
    float centerOffsetY;
    float centerX;
    float centerY;
    float postX;
    float postY;
    float rotateX;
    float rotateY;
    float rotateZ;
    float translateX;
    float translateXOffset;
    float translateY;
    float translateYOffset;
    float translateZ;
    float translateZOffset;
    float postScaleX = 1.0f;
    float postScaleY = 1.0f;
    private Camera camera = new Camera();
    private Matrix matrix = new Matrix();

    abstract void onDraw(Canvas canvas);

    void onDrawBefore(Canvas canvas) {
    }

    void onDrawAfter(Canvas canvas) {
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.p.Part
    public final void draw(Canvas canvas) {
        int saveCount = canvas.save();
        onDrawBefore(canvas);
        this.camera.save();
        this.camera.rotate(this.rotateX, this.rotateY, this.rotateZ);
        this.camera.translate(this.translateX + this.translateXOffset, this.translateY + this.translateYOffset, this.translateZ + this.translateZOffset);
        this.matrix.reset();
        this.camera.getMatrix(this.matrix);
        this.camera.restore();
        this.matrix.preTranslate(-this.centerX, -this.centerY);
        this.matrix.postScale(this.postScaleX, this.postScaleY, this.postX, this.postY);
        this.matrix.postTranslate(this.centerX, this.centerY);
        canvas.concat(this.matrix);
        onDraw(canvas);
        onDrawAfter(canvas);
        canvas.restoreToCount(saveCount);
    }
}
