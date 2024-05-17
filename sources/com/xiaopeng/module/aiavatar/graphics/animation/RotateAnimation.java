package com.xiaopeng.module.aiavatar.graphics.animation;

import android.util.Log;
/* loaded from: classes23.dex */
public class RotateAnimation extends Animation {
    private float mFromDegrees;
    private float mPivotX;
    private int mPivotXType;
    private float mPivotXValue;
    private float mPivotY;
    private int mPivotYType;
    private float mPivotYValue;
    private float mPivotZ;
    private int mPivotZType;
    private float mPivotZValue;
    private float mToDegrees;

    public RotateAnimation(float fromDegrees, float toDegrees) {
        this.mPivotXType = 0;
        this.mPivotYType = 0;
        this.mPivotZType = 0;
        this.mPivotXValue = 0.0f;
        this.mPivotYValue = 0.0f;
        this.mPivotZValue = 0.0f;
        this.mFromDegrees = fromDegrees;
        this.mToDegrees = toDegrees;
        this.mPivotX = 0.0f;
        this.mPivotY = 0.0f;
        this.mPivotZ = 0.0f;
    }

    public RotateAnimation(float fromDegrees, float toDegrees, float pivotX, float pivotY, float pivotZ) {
        this.mPivotXType = 0;
        this.mPivotYType = 0;
        this.mPivotZType = 0;
        this.mPivotXValue = 0.0f;
        this.mPivotYValue = 0.0f;
        this.mPivotZValue = 0.0f;
        this.mFromDegrees = fromDegrees;
        this.mToDegrees = toDegrees;
        this.mPivotXType = 0;
        this.mPivotYType = 0;
        this.mPivotZType = 0;
        this.mPivotXValue = pivotX;
        this.mPivotYValue = pivotY;
        this.mPivotZValue = pivotZ;
        initializePivotPoint();
    }

    public RotateAnimation(float fromDegrees, float toDegrees, int pivotXType, float pivotXValue, int pivotYType, float pivotYValue, int pivotZType, float pivotZValue) {
        this.mPivotXType = 0;
        this.mPivotYType = 0;
        this.mPivotZType = 0;
        this.mPivotXValue = 0.0f;
        this.mPivotYValue = 0.0f;
        this.mPivotZValue = 0.0f;
        this.mFromDegrees = fromDegrees;
        this.mToDegrees = toDegrees;
        this.mPivotXValue = pivotXValue;
        this.mPivotXType = pivotXType;
        this.mPivotYValue = pivotYValue;
        this.mPivotYType = pivotYType;
        this.mPivotZValue = pivotZValue;
        this.mPivotZType = pivotZType;
        initializePivotPoint();
    }

    private void initializePivotPoint() {
        if (this.mPivotXType == 0) {
            this.mPivotX = this.mPivotXValue;
        }
        if (this.mPivotYType == 0) {
            this.mPivotY = this.mPivotYValue;
        }
        if (this.mPivotZType == 0) {
            this.mPivotZ = this.mPivotZValue;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public void applyTransformation(float interpolatedTime, Transformation t) {
        float f = this.mFromDegrees;
        float degrees = f + ((this.mToDegrees - f) * interpolatedTime);
        float scale = getScaleFactor();
        if (interpolatedTime < 1.0f && interpolatedTime > 0.0f) {
            Log.i("animation", "开始:" + this.mFromDegrees + "结束:" + this.mToDegrees + "angle:\t" + degrees + "Time:" + interpolatedTime);
        }
        t.getMatrix().rotate(this.mPivotX * scale, this.mPivotY * scale, this.mPivotZ * scale, degrees);
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        this.mPivotX = resolveSize(this.mPivotXType, this.mPivotXValue, width, parentWidth);
        this.mPivotY = resolveSize(this.mPivotYType, this.mPivotYValue, height, parentHeight);
        this.mPivotZ = resolveSize(this.mPivotZType, this.mPivotZValue, height, parentHeight);
    }
}
