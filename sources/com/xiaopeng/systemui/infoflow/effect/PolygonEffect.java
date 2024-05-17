package com.xiaopeng.systemui.infoflow.effect;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import com.xiaopeng.systemui.infoflow.effect.AbsLinkedSpringEffect;
import java.util.List;
/* loaded from: classes24.dex */
public class PolygonEffect extends AbsLinkedSpringEffect<State> {
    private float mPositionPercentageX;
    private float mPositionPercentageY;
    private boolean mShowFirstLine;
    private final Paint mWaveBlurPaint;
    private final Paint mWavePaint;
    private final Path mWavePath;
    public float maxStrokeWidth;
    public float minStrokeWidth;
    public int widthStyle;

    public PolygonEffect() {
        super(6);
        this.mWavePath = new Path();
        this.mWavePaint = new Paint(1);
        this.mWaveBlurPaint = new Paint(1);
        this.maxStrokeWidth = 10.0f;
        this.minStrokeWidth = 4.0f;
        this.widthStyle = 1;
        this.mPositionPercentageX = 0.5f;
        this.mPositionPercentageY = 0.5f;
        this.mShowFirstLine = true;
        this.mWavePaint.setColor(-1);
        this.mWavePaint.setStrokeWidth(6.0f);
        this.mWavePaint.setStyle(Paint.Style.STROKE);
        this.mWavePaint.setStrokeJoin(Paint.Join.ROUND);
        this.mWavePaint.setStrokeCap(Paint.Cap.ROUND);
        this.mWaveBlurPaint.setColor(-16718218);
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
        int position = (i * 65) + 50;
        return new State(before, floatValueHolder, amplitudeEnhance, position, position, 12);
    }

    public void setPositionPercentage(float positionPercentageX, float positionPercentageY) {
        this.mPositionPercentageX = positionPercentageX;
        this.mPositionPercentageY = positionPercentageY;
    }

    public float getPositionPercentageX() {
        return this.mPositionPercentageX;
    }

    public void setPositionPercentageX(float positionPercentageX) {
        this.mPositionPercentageX = positionPercentageX;
    }

    public float getPositionPercentageY() {
        return this.mPositionPercentageY;
    }

    public void setPositionPercentageY(float positionPercentageY) {
        this.mPositionPercentageY = positionPercentageY;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.effect.AbsLinkedSpringEffect, com.xiaopeng.systemui.infoflow.effect.AbsEffect
    public void onDraw(Canvas canvas) {
        int width;
        super.onDraw(canvas);
        int offsetX = getWidth() / 2;
        int offsetY = getHeight() / 2;
        canvas.translate(getWidth() * (this.mPositionPercentageX - 0.5f), getHeight() * (this.mPositionPercentageY - 0.5f));
        canvas.rotate(45.0f, offsetX, offsetY);
        List<State> stateList = getStateList();
        int i = 1;
        int i2 = !this.mShowFirstLine;
        while (i2 < stateList.size()) {
            State state = stateList.get(i2);
            int r = state.roundRadius;
            int distance = state.currentDistance;
            state.maxDistance = Math.max(state.maxDistance, state.currentDistance);
            int width2 = 8;
            int i3 = this.widthStyle;
            if (i3 != 0) {
                if (i3 == i) {
                    float f = this.minStrokeWidth;
                    width2 = (int) (f + ((this.maxStrokeWidth - f) * (1.0f - (state.currentDistance / 300.0f))));
                }
            } else if (state.maxDistance - state.distance == 0) {
                width2 = (int) this.maxStrokeWidth;
            } else {
                float f2 = this.maxStrokeWidth;
                width2 = (int) (f2 - ((f2 - this.minStrokeWidth) * (((state.currentDistance - state.distance) * 1.0f) / (state.maxDistance - state.distance))));
            }
            this.mWavePaint.setStrokeWidth(width2);
            this.mWaveBlurPaint.setStrokeWidth(width + 4);
            canvas.drawRoundRect(offsetX - distance, offsetY - distance, offsetX + distance, offsetY + distance, r, r, this.mWavePaint);
            canvas.drawRoundRect(offsetX - distance, offsetY - distance, offsetX + distance, offsetY + distance, r, r, this.mWaveBlurPaint);
            i2++;
            stateList = stateList;
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
        private final int roundRadius;

        public State(SpringAnimation springAnimation, FloatValueHolder floatValueHolder, float amplitudeEnhance, int distance, int currentDistance, int roundRadius) {
            super(springAnimation, floatValueHolder, amplitudeEnhance, distance, currentDistance);
            this.roundRadius = roundRadius;
        }

        public String toString() {
            return "State{floatValueHolder=" + this.floatValueHolder.hashCode() + ", distance=" + this.distance + ", currentDistance=" + this.currentDistance + '}';
        }
    }
}
