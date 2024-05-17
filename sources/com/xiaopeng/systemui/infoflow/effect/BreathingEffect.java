package com.xiaopeng.systemui.infoflow.effect;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Log;
import androidx.core.math.MathUtils;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.systemui.infoflow.effect.CrestTroughInterpolator;
/* loaded from: classes24.dex */
public class BreathingEffect extends AbsEffect {
    private static final String TAG = "BreathingEffect";
    private float mBreathingWidth1 = 85.0f;
    private float mBreathingHeight1 = 47.0f;
    private float mBreathingWidth2 = 200.0f;
    private float mBreathingHeight2 = 122.0f;
    private Paint mCenter1Paint = new Paint(1);
    private Paint mCenter2Paint = new Paint(1);
    private SpringEffectInterpolator mEffectInterpolator = new SpringEffectInterpolator();
    private float positionPercentageX = 0.5f;
    private float positionPercentageY = 0.5f;
    private long lastUpdateTime = -1;
    private boolean up = true;
    private CrestTroughInterpolator mCrestTroughInterpolator = new CrestTroughInterpolator();

    public BreathingEffect(int color1, int color2) {
        this.mCenter1Paint.setColor(color1);
        this.mCenter1Paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
        this.mCenter1Paint.setMaskFilter(new BlurMaskFilter(34.0f, BlurMaskFilter.Blur.NORMAL));
        this.mCenter1Paint.setStrokeWidth(4.0f);
        this.mCenter1Paint.setStrokeJoin(Paint.Join.ROUND);
        this.mCenter1Paint.setStrokeCap(Paint.Cap.ROUND);
        this.mCenter1Paint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mCenter2Paint.setColor(color2);
        this.mCenter2Paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
        this.mCenter2Paint.setMaskFilter(new BlurMaskFilter(200.0f, BlurMaskFilter.Blur.NORMAL));
        this.mCenter2Paint.setStrokeWidth(4.0f);
        this.mCenter2Paint.setStrokeJoin(Paint.Join.ROUND);
        this.mCenter2Paint.setStrokeCap(Paint.Cap.ROUND);
        this.mCenter2Paint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mEffectInterpolator.setPushStiffness(1500.0f);
        this.mEffectInterpolator.setPullStiffness(50.0f);
        this.mEffectInterpolator.setValueScale(1000.0f);
        this.mCrestTroughInterpolator.addListener(new CrestTroughInterpolator.Listener() { // from class: com.xiaopeng.systemui.infoflow.effect.BreathingEffect.1
            @Override // com.xiaopeng.systemui.infoflow.effect.CrestTroughInterpolator.Listener
            public void onTrough(float value) {
                BreathingEffect.this.mEffectInterpolator.update((float) Math.pow(value, 0.2d));
            }

            @Override // com.xiaopeng.systemui.infoflow.effect.CrestTroughInterpolator.Listener
            public void onCrest(float value) {
                BreathingEffect.this.mEffectInterpolator.update((float) Math.pow(value, 0.2d));
            }
        });
    }

    public void setBreathingColor(int color1, int color2) {
        this.mCenter1Paint.setColor(color1);
        this.mCenter2Paint.setColor(color2);
    }

    public float getBreathingHeight1() {
        return this.mBreathingHeight1;
    }

    public void setBreathingHeight1(float breathingHeight1) {
        this.mBreathingHeight1 = breathingHeight1;
    }

    public float getBreathingHeight2() {
        return this.mBreathingHeight2;
    }

    public void setBreathingHeight2(float breathingHeight2) {
        this.mBreathingHeight2 = breathingHeight2;
    }

    public float getBreathingWidth1() {
        return this.mBreathingWidth1;
    }

    public void setBreathingWidth1(float breathingWidth1) {
        this.mBreathingWidth1 = breathingWidth1;
    }

    public float getBreathingWidth2() {
        return this.mBreathingWidth2;
    }

    public void setBreathingWidth2(float breathingWidth2) {
        this.mBreathingWidth2 = breathingWidth2;
    }

    public void setBreathing1(float breathingWidth1, float breathingHeight1) {
        this.mBreathingWidth1 = breathingWidth1;
        this.mBreathingHeight1 = breathingHeight1;
    }

    public void setBreathing2(float breathingWidth2, float breathingHeight2) {
        this.mBreathingWidth2 = breathingWidth2;
        this.mBreathingHeight2 = breathingHeight2;
    }

    private void log(Object obj) {
        Log.d(TAG, NavigationBarInflaterView.KEY_CODE_START + hashCode() + NavigationBarInflaterView.KEY_CODE_END + obj);
    }

    public float getPositionPercentageX() {
        return this.positionPercentageX;
    }

    public void setPositionPercentageX(float positionPercentageX) {
        this.positionPercentageX = positionPercentageX;
    }

    public float getPositionPercentageY() {
        return this.positionPercentageY;
    }

    public void setPositionPercentageY(float positionPercentageY) {
        this.positionPercentageY = positionPercentageY;
    }

    public void setPositionPercentage(float positionPercentageX, float positionPercentageY) {
        this.positionPercentageX = positionPercentageX;
        this.positionPercentageY = positionPercentageY;
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.AbsEffect
    protected void onDraw(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int x = (int) (width * this.positionPercentageX);
        int y = (int) (height * this.positionPercentageY);
        float value = this.mEffectInterpolator.getValue() / this.mEffectInterpolator.getValueScale();
        int alpha = (int) (MathUtils.clamp(value, 0.2f, 1.0f) * 255.0f);
        this.mCenter2Paint.setAlpha(alpha);
        float hr1 = this.mBreathingWidth1;
        float vr1 = this.mBreathingHeight1;
        canvas.drawOval(x - hr1, y - vr1, x + hr1, y + vr1, this.mCenter1Paint);
        float hr2 = this.mBreathingWidth2;
        float vr2 = this.mBreathingHeight2;
        canvas.drawOval(x - hr2, y - vr2, x + hr2, y + vr2, this.mCenter2Paint);
    }

    public Paint getCenter2Paint() {
        return this.mCenter2Paint;
    }

    public Paint getCenter1Paint() {
        return this.mCenter1Paint;
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.Effect
    public void setAlpha(int alpha) {
        this.mCenter1Paint.setAlpha(alpha);
        this.mCenter2Paint.setAlpha(alpha);
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.AbsEffect, com.xiaopeng.systemui.infoflow.effect.Effect
    public void update(float value, boolean shouldFilter) {
        super.update(value, shouldFilter);
        if (!shouldFilter) {
            this.mCrestTroughInterpolator.update(0.0f);
        } else {
            this.mCrestTroughInterpolator.update(value);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.AbsEffect
    public void onUpdate(float value) {
    }
}
