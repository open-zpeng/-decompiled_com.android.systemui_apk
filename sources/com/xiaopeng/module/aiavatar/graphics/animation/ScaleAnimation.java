package com.xiaopeng.module.aiavatar.graphics.animation;

import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
/* loaded from: classes23.dex */
public class ScaleAnimation extends Animation {
    private float mFromX;
    private float mFromY;
    private float mFromZ;
    private float mToX;
    private float mToY;
    private float mToZ;
    private float mCurrentScale = 1.0f;
    private final Resources mResources = null;

    public ScaleAnimation(float fromX, float toX, float fromY, float toY, float fromZ, float toZ) {
        this.mFromX = fromX;
        this.mToX = toX;
        this.mFromY = fromY;
        this.mToY = toY;
        this.mFromZ = fromZ;
        this.mToZ = toZ;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public void applyTransformation(float interpolatedTime, Transformation t) {
        float sx = 1.0f;
        float sy = 1.0f;
        float sz = 1.0f;
        getScaleFactor();
        if (this.mFromX != 1.0f || this.mToX != 1.0f) {
            float f = this.mFromX;
            sx = f + ((this.mToX - f) * interpolatedTime);
        }
        if (this.mFromY != 1.0f || this.mToY != 1.0f) {
            float f2 = this.mFromY;
            sy = f2 + ((this.mToY - f2) * interpolatedTime);
        }
        if (this.mFromZ != 1.0f || this.mToZ != 1.0f) {
            float f3 = this.mFromZ;
            sz = f3 + ((this.mToZ - f3) * interpolatedTime);
        }
        Log.d("KEVIN", "sx = " + sx + ", sy = " + sy + ", sz = " + sz);
        t.getMatrix().scl(sx, sy, sz);
        this.mCurrentScale = sx;
    }

    public float getCurrentScale() {
        return this.mCurrentScale;
    }

    float resolveScale(float scale, int type, int data, int size, int psize) {
        float targetSize;
        if (type == 6) {
            targetSize = TypedValue.complexToFraction(data, size, psize);
        } else if (type == 5) {
            targetSize = TypedValue.complexToDimension(data, this.mResources.getDisplayMetrics());
        } else {
            return scale;
        }
        if (size == 0) {
            return 1.0f;
        }
        return targetSize / size;
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    /* renamed from: clone */
    public ScaleAnimation mo43clone() {
        try {
            ScaleAnimation result = (ScaleAnimation) super.mo43clone();
            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
