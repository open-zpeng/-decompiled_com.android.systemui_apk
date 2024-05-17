package com.xiaopeng.systemui.infoflow.effect;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
/* loaded from: classes24.dex */
public class SpringEffectInterpolator implements EffectInterpolator {
    private static final float DEFAULT_DAMPING_RATIO = 0.858f;
    private static final float DEFAULT_SCALE = 40.0f;
    private static final float DEFAULT_STIFFNESS = 3329.0f;
    private float mValueScale = DEFAULT_SCALE;
    private int mVelocityDirection = 1;
    private FloatValueHolder mFloatValueHolder = new FloatValueHolder();
    private float mMaxValue = -10000.0f;
    private float mMinValue = 10000.0f;
    private boolean mAutoReduction = true;
    private float mPushStiffness = 1500.0f;
    private float mPullStiffness = 200.0f;
    private SpringAnimation mSpringAnimation = new SpringAnimation(this.mFloatValueHolder);

    public SpringEffectInterpolator() {
        SpringForce springForce = new SpringForce();
        springForce.setStiffness(DEFAULT_STIFFNESS);
        springForce.setDampingRatio(DEFAULT_DAMPING_RATIO);
        this.mSpringAnimation.setSpring(springForce);
        this.mSpringAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: com.xiaopeng.systemui.infoflow.effect.SpringEffectInterpolator.1
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                if (SpringEffectInterpolator.this.mAutoReduction && value != 0.0f) {
                    SpringEffectInterpolator.this.mSpringAnimation.getSpring().setStiffness(SpringEffectInterpolator.this.mPullStiffness);
                    SpringEffectInterpolator.this.mSpringAnimation.animateToFinalPosition(0.0f);
                    SpringEffectInterpolator.this.mSpringAnimation.start();
                }
            }
        });
        this.mSpringAnimation.setStartValue(0.0f);
    }

    public SpringAnimation getSpringAnimation() {
        return this.mSpringAnimation;
    }

    public boolean isAutoReduction() {
        return this.mAutoReduction;
    }

    public void setAutoReduction(boolean autoReduction) {
        this.mAutoReduction = autoReduction;
    }

    public float getValueScale() {
        return this.mValueScale;
    }

    public void setValueScale(float valueScale) {
        this.mValueScale = valueScale;
    }

    public int getVelocityDirection() {
        return this.mVelocityDirection;
    }

    public void setVelocityDirection(int velocityDirection) {
        this.mVelocityDirection = velocityDirection;
    }

    public float getStiffness() {
        return this.mSpringAnimation.getSpring().getStiffness();
    }

    public void setStiffness(float stiffness) {
        this.mSpringAnimation.getSpring().setStiffness(stiffness);
    }

    public float getDampingRatio() {
        return this.mSpringAnimation.getSpring().getDampingRatio();
    }

    public void setDampingRatio(float dampingRatio) {
        this.mSpringAnimation.getSpring().setDampingRatio(dampingRatio);
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.EffectInterpolator
    public float getMaxValue() {
        return this.mMaxValue;
    }

    public void setMaxValue(float maxValue) {
        this.mMaxValue = maxValue;
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.EffectInterpolator
    public float getMinValue() {
        return this.mMinValue;
    }

    public void setMinValue(float minValue) {
        this.mMinValue = minValue;
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.EffectInterpolator
    public float getValue() {
        float value = this.mFloatValueHolder.getValue();
        this.mMaxValue = Math.max(this.mMaxValue, value);
        this.mMinValue = Math.min(this.mMinValue, value);
        return value;
    }

    public FloatValueHolder getFloatValueHolder() {
        return this.mFloatValueHolder;
    }

    public float getPullStiffness() {
        return this.mPullStiffness;
    }

    public void setPullStiffness(float pullStiffness) {
        this.mPullStiffness = pullStiffness;
    }

    public float getPushStiffness() {
        return this.mPushStiffness;
    }

    public void setPushStiffness(float pushStiffness) {
        this.mPushStiffness = pushStiffness;
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.EffectInterpolator
    public void update(float value) {
        this.mSpringAnimation.getSpring().setStiffness(this.mPushStiffness);
        this.mSpringAnimation.animateToFinalPosition(this.mValueScale * value * this.mVelocityDirection);
        this.mSpringAnimation.start();
    }
}
