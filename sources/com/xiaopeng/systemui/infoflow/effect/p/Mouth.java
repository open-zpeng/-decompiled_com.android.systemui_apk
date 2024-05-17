package com.xiaopeng.systemui.infoflow.effect.p;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
/* loaded from: classes24.dex */
class Mouth extends AbsParallaxPart {
    Skeleton skeleton;
    int style;
    int colorFrom = -4370;
    int colorTo = -21846;
    Path path = new Path();
    float scaleSize = 1.0f;
    Paint paint = new Paint(1);

    /* JADX INFO: Access modifiers changed from: package-private */
    public Mouth() {
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setColor(-13108);
        this.skeleton = new Skeleton(new float[]{0.0f, 0.0f, 16.0f});
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setColor(int colorFrom, int colorTo) {
        this.colorFrom = colorFrom;
        this.colorTo = colorTo;
        updateShader();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateShader() {
        float[] currentValues = this.skeleton.getToValues();
        float radius = Math.max(currentValues[0] + currentValues[1], currentValues[2]);
        this.paint.setShader(new RadialGradient(this.centerX, this.centerY - currentValues[1], Math.max(radius, 40.0f), this.colorFrom, this.colorTo, Shader.TileMode.MIRROR));
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.p.Part
    public void setAlpha(int alpha) {
        this.paint.setAlpha(alpha);
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.p.Part
    public void setStyle(int style) {
        this.style = style;
        if (style == 0) {
            this.skeleton.setMoveToValues(new float[]{0.0f, 0.0f, 0.0f});
            this.centerOffsetY = 40.0f;
        } else if (style == 1) {
            this.skeleton.setMoveToValues(new float[]{0.0f, 20.0f, 36.0f});
            this.centerOffsetY = 30.0f;
        } else if (style == 2) {
            this.skeleton.setMoveToValues(new float[]{0.0f, 0.0f, 0.0f});
            this.centerOffsetY = 40.0f;
        } else if (style == 3) {
            this.skeleton.setMoveToValues(new float[]{12.0f, 12.0f, 12.0f});
            this.centerOffsetY = 40.0f;
        } else if (style == 4) {
            this.skeleton.setMoveToValues(new float[]{0.0f, 0.0f, 0.0f});
            this.centerOffsetY = 40.0f;
        }
        updateShader();
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.p.AbsParallaxPart
    public void onDraw(Canvas canvas) {
        this.path.reset();
        this.skeleton.move();
        float[] currentValues = this.skeleton.getCurrentValues();
        float f = currentValues[2];
        float f2 = this.scaleSize;
        float w = f * f2;
        float upHeight = currentValues[0] * f2;
        float downHeight = currentValues[1] * f2;
        this.path.moveTo(this.centerX + this.centerOffsetX, this.centerY + this.centerOffsetY);
        this.path.rMoveTo(-w, 0.0f);
        if (downHeight > 0.0f) {
            this.path.rQuadTo(0.0f, downHeight, w, downHeight);
            this.path.rQuadTo(w, 0.0f, w, -downHeight);
        }
        if (upHeight > 0.0f) {
            this.path.rQuadTo(0.0f, -upHeight, -w, -upHeight);
            this.path.rQuadTo(-w, 0.0f, -w, upHeight);
        }
        canvas.drawPath(this.path, this.paint);
    }
}
