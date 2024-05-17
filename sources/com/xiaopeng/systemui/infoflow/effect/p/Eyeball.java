package com.xiaopeng.systemui.infoflow.effect.p;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import com.xiaopeng.systemui.infoflow.effect.p.Skeleton;
/* loaded from: classes24.dex */
class Eyeball extends AbsParallaxPart {
    float anglePadding;
    Paint eyeballPaint;
    final LoopValue happyX;
    final LoopValue happyY;
    final boolean shapeRTL;
    Skeleton shapeSkeleton;
    int style;
    Skeleton widthSkeleton;
    Skeleton winkSkeleton;
    int colorFrom = -4370;
    int colorTo = -21846;
    LoopValue loveScale = new LoopValue(8.0f, 16.0f, 0.05f);

    /* JADX INFO: Access modifiers changed from: package-private */
    public Eyeball(boolean leftEye) {
        this.shapeRTL = leftEye;
        if (this.shapeRTL) {
            this.happyX = new LoopValue(0.0f, 3.0f, 0.37f);
            this.happyX.delay = 0.25f;
            this.happyY = new LoopValue(4.7f, 7.7f, 0.47f);
        } else {
            this.happyX = new LoopValue(2.0f, 5.0f, 0.27f);
            this.happyY = new LoopValue(2.0f, 5.0f, 0.47f);
        }
        this.eyeballPaint = new Paint(1);
        this.eyeballPaint.setStrokeWidth(4.5f);
        this.eyeballPaint.setStrokeCap(Paint.Cap.ROUND);
        this.eyeballPaint.setStrokeJoin(Paint.Join.ROUND);
        this.shapeSkeleton = new Skeleton(new float[]{0.0f, -9.0f, 0.0f, 0.0f, 0.0f, 9.0f});
        this.widthSkeleton = new Skeleton(new float[]{4.5f});
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void wink() {
        if (this.winkSkeleton == null) {
            this.winkSkeleton = new Skeleton(new float[]{1.0f});
            this.winkSkeleton.setSpeed(0.2f);
            this.winkSkeleton.setListener(new Skeleton.Listener() { // from class: com.xiaopeng.systemui.infoflow.effect.p.Eyeball.1
                @Override // com.xiaopeng.systemui.infoflow.effect.p.Skeleton.Listener
                public void onMoveEnd() {
                    float value = Eyeball.this.winkSkeleton.getCurrentValues()[0];
                    if (value != 1.0f) {
                        Eyeball.this.winkSkeleton.setMoveToValues(new float[]{1.0f});
                    }
                }
            });
        }
        this.winkSkeleton.setMoveToValues(new float[]{0.0f});
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.p.Part
    public void setAlpha(int alpha) {
        this.eyeballPaint.setAlpha(alpha);
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.p.Part
    public void setStyle(int style) {
        this.style = style;
        if (style == 0) {
            this.shapeSkeleton.setMoveToValues(new float[]{0.0f, -5.0f, 0.0f, 0.0f, 0.0f, 5.0f});
            this.widthSkeleton.setMoveToValues(new float[]{6.0f});
        } else if (style == 1) {
            this.shapeSkeleton.setMoveToValues(new float[]{0.0f, -9.0f, 0.0f, 0.0f, 0.0f, 9.0f});
            this.widthSkeleton.setMoveToValues(new float[]{10.0f});
        } else if (style == 2) {
            if (this.shapeRTL) {
                this.shapeSkeleton.setMoveToValues(new float[]{-4.0f, -9.0f, -15.0f, 0.0f, -4.0f, 9.0f});
            } else {
                this.shapeSkeleton.setMoveToValues(new float[]{-4.0f, -9.0f, 9.0f, 0.0f, -4.0f, 9.0f});
            }
            if (this.shapeRTL) {
                this.happyX.progress = 0.25f;
            } else {
                this.happyX.progress = 0.0f;
            }
            this.widthSkeleton.setMoveToValues(new float[]{3.0f});
        } else if (style == 3) {
            this.shapeSkeleton.setMoveToValues(new float[]{0.0f, -5.0f, 0.0f, 0.0f, 0.0f, 5.0f});
            this.widthSkeleton.setMoveToValues(new float[]{6.0f});
        } else if (style == 4) {
            this.shapeSkeleton.setMoveToValues(new float[]{0.0f, -5.0f, 0.0f, 0.0f, 0.0f, 5.0f});
            this.widthSkeleton.setMoveToValues(new float[]{3.0f});
        }
    }

    public void setColor(int colorFrom, int colorTo) {
        this.colorFrom = colorFrom;
        this.colorTo = colorTo;
        updateShader();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateShader() {
        this.eyeballPaint.setShader(new RadialGradient(10.0f + this.centerX, this.centerY, 40.0f, this.colorFrom, this.colorTo, Shader.TileMode.MIRROR));
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.p.AbsParallaxPart
    public void onDraw(Canvas canvas) {
        this.shapeSkeleton.move();
        this.widthSkeleton.move();
        this.eyeballPaint.setStrokeWidth(this.widthSkeleton.getCurrentValues()[0]);
        int i = this.style;
        if (i == 4) {
            float r = this.loveScale.move();
            float l0 = ((float) Math.pow(2.0d, 0.5d)) * r;
            float l1 = l0 / 2.0f;
            this.eyeballPaint.setStyle(Paint.Style.STROKE);
            float circleX = this.centerX - l1;
            float circleY = this.centerY - l1;
            float left = circleX - r;
            float top = circleY - r;
            float right = circleX + r;
            float bottom = circleY + r;
            canvas.drawArc(left, top, right, bottom, 135.0f, 180.0f, false, this.eyeballPaint);
            float ax = this.centerX - l0;
            float ay = this.centerY;
            float bx = this.centerX;
            float by = this.centerY + l0;
            float cx = this.centerX + l0;
            float cy = this.centerY;
            canvas.drawLine(ax, ay, bx, by, this.eyeballPaint);
            canvas.drawLine(bx, by, cx, cy, this.eyeballPaint);
            float circleX2 = this.centerX + l1;
            float circleY2 = this.centerY - l1;
            float left2 = circleX2 - r;
            float top2 = circleY2 - r;
            float right2 = circleX2 + r;
            float bottom2 = circleY2 + r;
            canvas.drawArc(left2, top2, right2, bottom2, 225.0f, 180.0f, false, this.eyeballPaint);
            return;
        }
        if (i == 2) {
            float x = this.happyX.move();
            float y = this.happyY.move();
            canvas.translate(x, y);
        }
        this.eyeballPaint.setStyle(Paint.Style.FILL);
        float[] currentValues = this.shapeSkeleton.getCurrentValues();
        float k = 1.0f;
        Skeleton skeleton = this.winkSkeleton;
        if (skeleton != null) {
            skeleton.move();
            k = this.winkSkeleton.getCurrentValues()[0];
        }
        float eyeballCenterX = currentValues[2];
        float eyeballCenterY = currentValues[3];
        float eyeballStartX = ((currentValues[0] - eyeballCenterX) * k) + eyeballCenterX;
        float eyeballStartY = eyeballCenterY + ((currentValues[1] - eyeballCenterY) * k);
        float eyeballEndX = ((currentValues[4] - eyeballCenterX) * k) + eyeballCenterX;
        float eyeballEndY = eyeballCenterY + ((currentValues[5] - eyeballCenterY) * k);
        canvas.drawLine(eyeballStartX + this.centerX, eyeballStartY + this.centerY, eyeballCenterX + this.centerX, eyeballCenterY + this.centerY, this.eyeballPaint);
        canvas.drawLine(eyeballCenterX + this.centerX, eyeballCenterY + this.centerY, eyeballEndX + this.centerX, eyeballEndY + this.centerY, this.eyeballPaint);
    }
}
