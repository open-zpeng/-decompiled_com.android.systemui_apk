package com.xiaopeng.module.aiavatar.graphics.animation;

import android.content.Context;
import android.util.AttributeSet;
/* loaded from: classes23.dex */
public class ValueAnimation extends android.view.animation.Animation {
    private float mCurentValue;
    private float mFromValue;
    private float mToValue;

    public ValueAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ValueAnimation(float from, float to) {
        this.mFromValue = from;
        this.mToValue = to;
    }

    @Override // android.view.animation.Animation
    protected void applyTransformation(float interpolatedTime, android.view.animation.Transformation t) {
        float value = this.mFromValue;
        t.setAlpha(((this.mToValue - value) * interpolatedTime) + value);
    }

    @Override // android.view.animation.Animation
    public boolean willChangeTransformationMatrix() {
        return false;
    }

    @Override // android.view.animation.Animation
    public boolean willChangeBounds() {
        return false;
    }

    public void setmCurentValue(float mCurentValue) {
        this.mCurentValue = mCurentValue;
    }

    public float getmCurentValue() {
        return this.mCurentValue;
    }
}
