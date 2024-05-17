package com.xiaopeng.systemui.infoflow.effect;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import com.xiaopeng.systemui.infoflow.effect.AbsLinkedSpringEffect;
import java.util.List;
/* loaded from: classes24.dex */
public class TriangleEffect extends AbsLinkedSpringEffect<State> {
    private static final float K0 = 0.5f;
    private static final float K1 = 0.8660254f;
    private boolean mShowFirstLine;
    private final Paint mWaveBlurPaint;
    private final Paint mWavePaint;
    public float maxStrokeWidth;
    public float minStrokeWidth;
    public int widthStyle;

    public TriangleEffect() {
        super(5);
        this.mWavePaint = new Paint(1);
        this.mWaveBlurPaint = new Paint(1);
        this.maxStrokeWidth = 10.0f;
        this.minStrokeWidth = 4.0f;
        this.widthStyle = 1;
        this.mShowFirstLine = true;
        this.mWavePaint.setColor(-1);
        this.mWavePaint.setStrokeWidth(6.0f);
        this.mWavePaint.setStyle(Paint.Style.STROKE);
        this.mWavePaint.setStrokeJoin(Paint.Join.ROUND);
        this.mWavePaint.setStrokeCap(Paint.Cap.ROUND);
        this.mWaveBlurPaint.setColor(-16744705);
        this.mWaveBlurPaint.setStrokeWidth(10.0f);
        this.mWaveBlurPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
        this.mWaveBlurPaint.setMaskFilter(new BlurMaskFilter(16.0f, BlurMaskFilter.Blur.NORMAL));
        this.mWaveBlurPaint.setStyle(Paint.Style.STROKE);
        this.mWaveBlurPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mWaveBlurPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public boolean isShowFirstLine() {
        return this.mShowFirstLine;
    }

    public void setShowFirstLine(boolean show) {
        this.mShowFirstLine = show;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.effect.AbsLinkedSpringEffect
    public State createState(SpringAnimation before, FloatValueHolder floatValueHolder, float amplitudeEnhance, int i) {
        int position = (i * 120) + 50;
        return new State(before, floatValueHolder, amplitudeEnhance, position, position);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.effect.AbsLinkedSpringEffect, com.xiaopeng.systemui.infoflow.effect.AbsEffect
    public void onDraw(Canvas canvas) {
        int centerX;
        int centerY;
        int width;
        super.onDraw(canvas);
        int centerX2 = getWidth() / 2;
        int centerY2 = getHeight() / 2;
        List<State> stateList = getStateList();
        int i = 1;
        int firstIndex = !this.mShowFirstLine;
        int i2 = firstIndex;
        while (i2 < stateList.size()) {
            State state = stateList.get(i2);
            float d0 = state.currentDistance * 0.5f;
            float d1 = state.currentDistance;
            state.maxDistance = Math.max(state.maxDistance, state.currentDistance);
            float d2 = state.currentDistance * K1;
            float leftX = centerX2 - d2;
            float leftY = centerY2 - d0;
            float rightX = centerX2 + d2;
            float rightY = centerY2 - d0;
            float bottomX = centerX2;
            float bottomY = centerY2 + d1;
            int width2 = 8;
            int i3 = this.widthStyle;
            if (i3 != 0) {
                if (i3 != i) {
                    centerX = centerX2;
                    centerY = centerY2;
                } else {
                    float f = this.minStrokeWidth;
                    width2 = (int) (f + ((this.maxStrokeWidth - f) * (1.0f - (state.currentDistance / 300.0f))));
                    centerX = centerX2;
                    centerY = centerY2;
                }
            } else if (state.maxDistance - state.distance == 0) {
                width2 = (int) this.maxStrokeWidth;
                centerX = centerX2;
                centerY = centerY2;
            } else {
                float f2 = this.maxStrokeWidth;
                int i4 = state.currentDistance;
                centerX = centerX2;
                int centerX3 = state.distance;
                int i5 = state.maxDistance;
                centerY = centerY2;
                int centerY3 = state.distance;
                width2 = (int) (f2 - ((f2 - this.minStrokeWidth) * (((i4 - centerX3) * 1.0f) / (i5 - centerY3))));
            }
            this.mWavePaint.setStrokeWidth(width2);
            this.mWaveBlurPaint.setStrokeWidth(width + 4);
            canvas.drawLine(leftX, leftY, rightX, rightY, this.mWavePaint);
            canvas.drawLine(leftX, leftY, rightX, rightY, this.mWaveBlurPaint);
            canvas.drawLine(rightX, rightY, bottomX, bottomY, this.mWavePaint);
            canvas.drawLine(rightX, rightY, bottomX, bottomY, this.mWaveBlurPaint);
            canvas.drawLine(bottomX, bottomY, leftX, leftY, this.mWavePaint);
            canvas.drawLine(bottomX, bottomY, leftX, leftY, this.mWaveBlurPaint);
            i2++;
            centerX2 = centerX;
            centerY2 = centerY;
            i = 1;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.effect.AbsEffect
    public void onSizeChange(int w, int h) {
        super.onSizeChange(w, h);
        int centerX = w >> 1;
        int centerY = h >> 1;
        this.mWavePaint.setShader(new RadialGradient(centerX, centerY, Math.max(10, centerX), this.mWavePaint.getColor(), setColorAlpha(this.mWavePaint.getColor(), 153), Shader.TileMode.CLAMP));
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.Effect
    public void setAlpha(int alpha) {
        this.mWavePaint.setAlpha(alpha);
        this.mWaveBlurPaint.setAlpha(alpha);
    }

    /* loaded from: classes24.dex */
    public static final class State extends AbsLinkedSpringEffect.State {
        public State(SpringAnimation springAnimation, FloatValueHolder floatValueHolder, float amplitudeEnhance, int distance, int currentDistance) {
            super(springAnimation, floatValueHolder, amplitudeEnhance, distance, currentDistance);
        }
    }
}
