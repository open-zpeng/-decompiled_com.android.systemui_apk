package com.xiaopeng.systemui.infoflow.effect.p;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
/* loaded from: classes24.dex */
class Eyelid extends AbsParallaxPart {
    float anglePadding;
    Skeleton skeleton;
    int style;
    float eyelidAngle = 0.0f;
    float angleSpeed = 1.0f;
    int colorFrom = -4370;
    int colorTo = -21846;
    float sizeScale = 1.0f;
    Paint eyelidPaint = new Paint(1);

    /* JADX INFO: Access modifiers changed from: package-private */
    public Eyelid() {
        this.eyelidPaint.setStrokeWidth(4.5f);
        this.eyelidPaint.setStyle(Paint.Style.STROKE);
        this.eyelidPaint.setStrokeCap(Paint.Cap.ROUND);
        this.eyelidPaint.setStrokeJoin(Paint.Join.ROUND);
        this.skeleton = new Skeleton(new float[]{24.0f, 0.0f, 24.0f, 0.0f});
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setColor(int colorFrom, int colorTo) {
        this.colorFrom = colorFrom;
        this.colorTo = colorTo;
        updateShader();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateShader() {
        float eyelidDownRadius = Math.max(1.0f, this.skeleton.getCurrentValues()[1]);
        this.eyelidPaint.setShader(new RadialGradient(this.centerX + eyelidDownRadius, this.centerY - eyelidDownRadius, eyelidDownRadius * 2.0f, this.colorFrom, this.colorTo, Shader.TileMode.CLAMP));
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.p.Part
    public void setAlpha(int alpha) {
        this.eyelidPaint.setAlpha(alpha);
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.p.Part
    public void setStyle(int style) {
        this.style = style;
        if (style == 0) {
            this.skeleton.setMoveToValues(new float[]{24.0f, 0.0f, 24.0f, 0.0f});
        } else if (style == 1) {
            this.skeleton.setMoveToValues(new float[]{24.0f, 0.0f, 12.0f, 3.0f});
        } else if (style == 2) {
            this.skeleton.setMoveToValues(new float[]{24.0f, 0.0f, 24.0f, 0.0f});
        } else if (style != 3) {
            if (style == 4) {
                this.skeleton.setMoveToValues(new float[]{0.0f, 0.0f, 0.0f, 0.0f});
            }
        } else {
            this.skeleton.setMoveToValues(new float[]{24.0f, 0.0f, 24.0f, 0.0f});
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.p.AbsParallaxPart
    public void onDraw(Canvas canvas) {
        this.skeleton.move();
        float eyelidUpRadius = this.skeleton.getCurrentValues()[0] * this.sizeScale;
        float eyelidDownRadius = this.skeleton.getCurrentValues()[2] * this.sizeScale;
        if (eyelidUpRadius == eyelidDownRadius) {
            this.eyelidAngle += this.angleSpeed;
            if (this.eyelidAngle >= 360.0f) {
                this.eyelidAngle = 0.0f;
            }
        } else {
            this.eyelidAngle = 0.0f;
        }
        float offsetY = this.skeleton.getCurrentValues()[1];
        float left = this.centerX - eyelidUpRadius;
        float top = (this.centerY - eyelidUpRadius) + offsetY;
        float right = this.centerX + eyelidUpRadius;
        float bottom = this.centerY + eyelidUpRadius + offsetY;
        float f = this.anglePadding;
        float hfPadding = f / 2.0f;
        float sweepAngle = 180.0f - f;
        float startAngle = this.eyelidAngle + hfPadding + 180.0f;
        canvas.drawArc(left, top, right, bottom, startAngle, sweepAngle, false, this.eyelidPaint);
        float offsetY2 = this.skeleton.getCurrentValues()[3];
        float left2 = this.centerX - eyelidDownRadius;
        float top2 = (this.centerY - eyelidDownRadius) + offsetY2;
        float right2 = this.centerX + eyelidDownRadius;
        float bottom2 = this.centerY + eyelidDownRadius + offsetY2;
        float f2 = this.anglePadding;
        float hfPadding2 = f2 / 2.0f;
        float sweepAngle2 = 180.0f - f2;
        float startAngle2 = this.eyelidAngle + hfPadding2;
        canvas.drawArc(left2, top2, right2, bottom2, startAngle2, sweepAngle2, false, this.eyelidPaint);
    }
}
