package com.xiaopeng.module.aiavatar.graphics.animation;

import android.util.Log;
/* loaded from: classes23.dex */
public class TranslateAnimation extends Animation {
    private float mFromXDelta;
    private int mFromXType;
    private float mFromXValue;
    private float mFromYDelta;
    private int mFromYType;
    private float mFromYValue;
    private float mFromZDelta;
    private int mFromZType;
    private float mFromZValue;
    private float mToXDelta;
    private int mToXType;
    private float mToXValue;
    private float mToYDelta;
    private int mToYType;
    private float mToYValue;
    private float mToZDelta;
    private int mToZType;
    private float mToZValue;

    public TranslateAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta, float fromZDelta, float toZDelta) {
        this.mFromXType = 0;
        this.mToXType = 0;
        this.mFromYType = 0;
        this.mToYType = 0;
        this.mFromZType = 0;
        this.mToZType = 0;
        this.mFromXValue = 0.0f;
        this.mToXValue = 0.0f;
        this.mFromYValue = 0.0f;
        this.mToYValue = 0.0f;
        this.mFromZValue = 0.0f;
        this.mToZValue = 0.0f;
        this.mFromXValue = fromXDelta;
        this.mToXValue = toXDelta;
        this.mFromYValue = fromYDelta;
        this.mToYValue = toYDelta;
        this.mFromZValue = fromZDelta;
        this.mToZValue = toZDelta;
        this.mFromXType = 0;
        this.mToXType = 0;
        this.mFromYType = 0;
        this.mToYType = 0;
        this.mFromZType = 0;
        this.mToZType = 0;
    }

    public TranslateAnimation(int fromXType, float fromXValue, int toXType, float toXValue, int fromYType, float fromYValue, int toYType, float toYValue, int fromZType, float fromZValue, int toZType, float toZValue) {
        this.mFromXType = 0;
        this.mToXType = 0;
        this.mFromYType = 0;
        this.mToYType = 0;
        this.mFromZType = 0;
        this.mToZType = 0;
        this.mFromXValue = 0.0f;
        this.mToXValue = 0.0f;
        this.mFromYValue = 0.0f;
        this.mToYValue = 0.0f;
        this.mFromZValue = 0.0f;
        this.mToZValue = 0.0f;
        this.mFromXValue = fromXValue;
        this.mToXValue = toXValue;
        this.mFromYValue = fromYValue;
        this.mToYValue = toYValue;
        this.mFromZValue = fromZValue;
        this.mToZValue = toZValue;
        this.mFromXType = fromXType;
        this.mToXType = toXType;
        this.mFromYType = fromYType;
        this.mToYType = toYType;
        this.mFromZType = fromZType;
        this.mToZType = toZType;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public void applyTransformation(float interpolatedTime, Transformation t) {
        float dx = this.mFromXValue;
        float dy = this.mFromYValue;
        float dz = this.mFromZValue;
        float f = this.mFromXDelta;
        float f2 = this.mToXValue;
        if (f != f2) {
            dx = this.mFromXValue + ((f2 - f) * interpolatedTime);
        }
        float f3 = this.mFromYValue;
        float f4 = this.mToYValue;
        if (f3 != f4) {
            dy = f3 + ((f4 - f3) * interpolatedTime);
        }
        float f5 = this.mFromZValue;
        float f6 = this.mToZValue;
        if (f5 != f6) {
            dz = f5 + ((f6 - f5) * interpolatedTime);
        }
        if (interpolatedTime < 1.0f) {
            Log.i("amimation", "x:\t" + dx + "y:\t" + dy + "z:\t" + dz + "interpolatedTime:" + interpolatedTime);
        }
        t.getMatrix().translate(dx, dy, dz);
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        this.mFromXDelta = resolveSize(this.mFromXType, this.mFromXValue, width, parentWidth);
        this.mToXDelta = resolveSize(this.mToXType, this.mToXValue, width, parentWidth);
        this.mFromYDelta = resolveSize(this.mFromYType, this.mFromYValue, height, parentHeight);
        this.mToYDelta = resolveSize(this.mToYType, this.mToYValue, height, parentHeight);
        this.mFromZDelta = resolveSize(this.mFromZType, this.mFromZValue, height, parentHeight);
        this.mToZDelta = resolveSize(this.mToZType, this.mToZValue, height, parentHeight);
    }
}
