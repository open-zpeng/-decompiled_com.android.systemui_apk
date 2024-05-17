package com.xiaopeng.systemui.infoflow.effect.p;

import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.xiaopeng.systemui.infoflow.effect.AbsEffect;
/* loaded from: classes24.dex */
public class PEffect extends AbsEffect {
    public static final int STYLE_HAPPY = 2;
    public static final int STYLE_LAUGHING = 1;
    public static final int STYLE_LOVE = 4;
    public static final int STYLE_NORMAL = 0;
    public static final int STYLE_SHOCK = 3;
    private Eyeball mLeftEyeball;
    private Eyelid mLeftEyelid;
    private Mouth mMouth;
    private Eyeball mRightEyeball;
    private Eyelid mRightEyelid;
    private float mRotateXProgress = 0.5f;
    private float mRotateXDirection = 1.0f;
    private float mRotateXSpeed = 0.007f;
    private float mRotateYProgress = 0.5f;
    private float mRotateYDirection = 1.0f;
    private float mRotateYSpeed = 0.012f;
    private float mPositionPercentageX = 0.5f;
    private float mPositionPercentageY = 0.5f;
    private float mMouthRotateXRangeMin = -14.0f;
    private float mMouthRotateXRangeMax = 14.0f;
    private float mMouthRotateYRangeMin = -18.0f;
    private float mMouthRotateYRangeMax = 18.0f;
    private float mLeftEyelidRotateXRangeMin = -14.0f;
    private float mLeftEyelidRotateXRangeMax = 14.0f;
    private float mLeftEyelidRotateYRangeMin = -18.0f;
    private float mLeftEyelidRotateYRangeMax = 18.0f;
    private float mRightEyelidRotateXRangeMin = -14.0f;
    private float mRightEyelidRotateXRangeMax = 14.0f;
    private float mRightEyelidRotateYRangeMin = -18.0f;
    private float mRightEyelidRotateYRangeMax = 18.0f;
    private float mLeftEyeballRotateXRangeMin = -14.0f;
    private float mLeftEyeballRotateXRangeMax = 14.0f;
    private float mLeftEyeballRotateYRangeMin = -20.0f;
    private float mLeftEyeballRotateYRangeMax = 20.0f;
    private float mRightEyeballRotateXRangeMin = -14.0f;
    private float mRightEyeballRotateXRangeMax = 14.0f;
    private float mRightEyeballRotateYRangeMin = -20.0f;
    private float mRightEyeballRotateYRangeMax = 20.0f;
    private float mRotateXMinValue = 0.0f;
    private float mRotateXMaxValue = 1000.0f;
    private float mRotateXMaxValueBase = 1000.0f;
    private float mRotateYMinValue = 0.0f;
    private float mRotateYMaxValue = 500.0f;
    private float mRotateYMaxValueBase = 500.0f;
    private float mRotateXRandomK = 0.4f;
    private float mRotateYRandomK = 0.4f;
    private FloatValueHolder mRotateXFloatValueHolderFollow = new FloatValueHolder();
    private FloatValueHolder mRotateXFloatValueHolder = new FloatValueHolder();
    private FloatValueHolder mRotateYFloatValueHolderFollow = new FloatValueHolder();
    private FloatValueHolder mRotateYFloatValueHolder = new FloatValueHolder();
    private SpringAnimation mRotateXSpringAnimationFollow = new SpringAnimation(this.mRotateXFloatValueHolderFollow);
    private SpringAnimation mRotateXSpringAnimation = new SpringAnimation(this.mRotateXFloatValueHolder);
    private SpringAnimation mRotateYSpringAnimationFollow = new SpringAnimation(this.mRotateYFloatValueHolderFollow);
    private SpringAnimation mRotateYSpringAnimation = new SpringAnimation(this.mRotateYFloatValueHolder);
    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    public PEffect() {
        this.mRotateXSpringAnimationFollow.setSpring(createSpringForce());
        this.mRotateXSpringAnimation.setSpring(createSpringForce());
        this.mRotateXSpringAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: com.xiaopeng.systemui.infoflow.effect.p.PEffect.1
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                if (value >= PEffect.this.mRotateXMaxValue) {
                    PEffect.this.mRotateXSpringAnimation.animateToFinalPosition(PEffect.this.mRotateXMinValue);
                    return;
                }
                PEffect pEffect = PEffect.this;
                pEffect.mRotateXMaxValue = pEffect.mRotateXMaxValueBase * PEffect.getRandomValue(1.0f - (PEffect.this.mRotateXRandomK / 2.0f), (PEffect.this.mRotateXRandomK / 2.0f) + 1.0f);
                PEffect.this.mRotateXSpringAnimation.animateToFinalPosition(PEffect.this.mRotateXMaxValue);
            }
        });
        this.mRotateXSpringAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: com.xiaopeng.systemui.infoflow.effect.p.PEffect.2
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                PEffect.this.mRotateXSpringAnimationFollow.animateToFinalPosition(value);
            }
        });
        this.mRotateXSpringAnimation.animateToFinalPosition(this.mRotateXMinValue);
        this.mRotateXSpringAnimation.start();
        this.mRotateYSpringAnimationFollow.setSpring(createSpringForce());
        this.mRotateYSpringAnimation.setSpring(createSpringForce());
        this.mRotateYSpringAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: com.xiaopeng.systemui.infoflow.effect.p.PEffect.3
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                if (value >= PEffect.this.mRotateYMaxValue) {
                    PEffect.this.mRotateYSpringAnimation.animateToFinalPosition(PEffect.this.mRotateYMinValue);
                    return;
                }
                PEffect pEffect = PEffect.this;
                pEffect.mRotateYMaxValue = pEffect.mRotateYMaxValueBase * PEffect.getRandomValue(1.0f - (PEffect.this.mRotateYRandomK / 2.0f), (PEffect.this.mRotateYRandomK / 2.0f) + 1.0f);
                PEffect.this.mRotateYSpringAnimation.animateToFinalPosition(PEffect.this.mRotateYMaxValue);
            }
        });
        this.mRotateYSpringAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: com.xiaopeng.systemui.infoflow.effect.p.PEffect.4
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                PEffect.this.mRotateYSpringAnimationFollow.animateToFinalPosition(value);
            }
        });
        this.mRotateYSpringAnimation.animateToFinalPosition(this.mRotateYMinValue);
        this.mRotateYSpringAnimation.start();
        this.mLeftEyelid = new Eyelid();
        Eyelid eyelid = this.mLeftEyelid;
        eyelid.translateZ = 30.0f;
        eyelid.postScaleX = 1.4f;
        eyelid.postScaleY = 1.4f;
        this.mRightEyelid = new Eyelid();
        Eyelid eyelid2 = this.mRightEyelid;
        eyelid2.translateZ = 30.0f;
        eyelid2.postScaleX = 1.4f;
        eyelid2.postScaleY = 1.4f;
        this.mLeftEyeball = new Eyeball(false);
        Eyeball eyeball = this.mLeftEyeball;
        eyeball.translateZ = 60.0f;
        eyeball.postScaleX = 1.4f;
        eyeball.postScaleY = 1.4f;
        this.mRightEyeball = new Eyeball(true);
        Eyeball eyeball2 = this.mRightEyeball;
        eyeball2.translateZ = 60.0f;
        eyeball2.loveScale.delay = 0.58f;
        Eyeball eyeball3 = this.mRightEyeball;
        eyeball3.postScaleX = 1.4f;
        eyeball3.postScaleY = 1.4f;
        this.mMouth = new Mouth();
        Mouth mouth = this.mMouth;
        mouth.translateZ = 30.0f;
        mouth.postScaleX = 1.4f;
        mouth.postScaleY = 1.4f;
    }

    private static float calculateValue(float min, float max, float progress) {
        return ((max - min) * progress) + min;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float getRandomValue(float min, float max) {
        return (float) (min + (Math.random() * (max - min)));
    }

    private SpringForce createSpringForce() {
        SpringForce springForce = new SpringForce();
        springForce.setStiffness(50.0f);
        springForce.setDampingRatio(1.0f);
        return springForce;
    }

    private void runOnMainThread(Runnable runnable) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            this.mMainHandler.post(runnable);
        } else {
            runnable.run();
        }
    }

    public void setColor(int colorFrom, int colorTo) {
        this.mLeftEyelid.setColor(colorFrom, colorTo);
        this.mRightEyelid.setColor(colorFrom, colorTo);
        this.mLeftEyeball.setColor(colorFrom, colorTo);
        this.mRightEyeball.setColor(colorFrom, colorTo);
        this.mMouth.setColor(colorFrom, colorTo);
    }

    public void wink() {
        this.mLeftEyeball.wink();
        this.mRightEyeball.wink();
    }

    public int getStyleCount() {
        return 5;
    }

    public void setStyle(int style) {
        this.mLeftEyelid.setStyle(style);
        this.mRightEyelid.setStyle(style);
        if (style == 1) {
            this.mLeftEyeball.translateZOffset = this.mLeftEyelid.translateZ - this.mLeftEyeball.translateZ;
            this.mRightEyeball.translateZOffset = this.mRightEyelid.translateZ - this.mRightEyeball.translateZ;
        } else {
            this.mLeftEyeball.translateZOffset = 0.0f;
            this.mRightEyeball.translateZOffset = 0.0f;
        }
        this.mLeftEyeball.setStyle(style);
        this.mRightEyeball.setStyle(style);
        this.mMouth.setStyle(style);
    }

    public void setPositionPercentage(float positionPercentageX, float positionPercentageY) {
        this.mPositionPercentageX = positionPercentageX;
        this.mPositionPercentageY = positionPercentageY;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.effect.AbsEffect
    public void onSizeChange(int w, int h) {
        super.onSizeChange(w, h);
        Eyelid eyelid = this.mLeftEyelid;
        eyelid.centerX = (w >> 1) - 40;
        eyelid.centerY = h >> 1;
        eyelid.anglePadding = 30.0f;
        eyelid.updateShader();
        Eyeball eyeball = this.mLeftEyeball;
        eyeball.centerX = (w >> 1) - 40;
        eyeball.centerY = h >> 1;
        eyeball.anglePadding = 30.0f;
        eyeball.updateShader();
        Eyelid eyelid2 = this.mRightEyelid;
        eyelid2.centerX = (w >> 1) + 40;
        eyelid2.centerY = h >> 1;
        eyelid2.anglePadding = 30.0f;
        eyelid2.updateShader();
        Eyeball eyeball2 = this.mRightEyeball;
        eyeball2.centerX = (w >> 1) + 40;
        eyeball2.centerY = h >> 1;
        eyeball2.anglePadding = 30.0f;
        eyeball2.updateShader();
        Mouth mouth = this.mMouth;
        mouth.centerX = w >> 1;
        mouth.centerY = h >> 1;
        mouth.updateShader();
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.AbsEffect
    protected void onDraw(Canvas canvas) {
        canvas.translate(getWidth() * (this.mPositionPercentageX - 0.5f), getHeight() * (this.mPositionPercentageY - 0.5f));
        this.mRotateXProgress = this.mRotateXMinValue + (this.mRotateXFloatValueHolderFollow.getValue() / (this.mRotateXMaxValue - this.mRotateXMinValue));
        this.mRotateYProgress = this.mRotateYMinValue + (this.mRotateYFloatValueHolderFollow.getValue() / (this.mRotateYMaxValue - this.mRotateYMinValue));
        this.mLeftEyelid.rotateX = calculateValue(this.mLeftEyelidRotateXRangeMin, this.mLeftEyelidRotateXRangeMax, this.mRotateXProgress);
        this.mRightEyelid.rotateX = calculateValue(this.mRightEyelidRotateXRangeMin, this.mRightEyelidRotateXRangeMax, this.mRotateXProgress);
        this.mLeftEyeball.rotateX = calculateValue(this.mLeftEyeballRotateXRangeMin, this.mLeftEyeballRotateXRangeMax, this.mRotateXProgress);
        this.mRightEyeball.rotateX = calculateValue(this.mRightEyeballRotateXRangeMin, this.mRightEyeballRotateXRangeMax, this.mRotateXProgress);
        this.mMouth.rotateX = calculateValue(this.mMouthRotateXRangeMin, this.mMouthRotateXRangeMax, this.mRotateXProgress);
        this.mLeftEyelid.rotateY = calculateValue(this.mLeftEyelidRotateYRangeMin, this.mLeftEyelidRotateYRangeMax, this.mRotateYProgress);
        this.mRightEyelid.rotateY = calculateValue(this.mRightEyelidRotateYRangeMin, this.mRightEyelidRotateYRangeMax, this.mRotateYProgress);
        this.mLeftEyeball.rotateY = calculateValue(this.mLeftEyeballRotateYRangeMin, this.mLeftEyeballRotateYRangeMax, this.mRotateYProgress);
        this.mRightEyeball.rotateY = calculateValue(this.mRightEyeballRotateYRangeMin, this.mRightEyeballRotateYRangeMax, this.mRotateYProgress);
        this.mMouth.rotateY = calculateValue(this.mMouthRotateYRangeMin, this.mMouthRotateYRangeMax, this.mRotateYProgress);
        this.mLeftEyeball.draw(canvas);
        this.mRightEyeball.draw(canvas);
        this.mLeftEyelid.draw(canvas);
        this.mRightEyelid.draw(canvas);
        this.mMouth.draw(canvas);
    }

    public float getRightEyeballDelay() {
        return this.mRightEyeball.loveScale.delay;
    }

    public void setRightEyeballDelay(float delay) {
        this.mRightEyeball.loveScale.delay = delay;
    }

    public float getRotateXSpeed() {
        return this.mRotateXSpeed;
    }

    public void setRotateXSpeed(float rotateXSpeed) {
        if (rotateXSpeed <= 0.0f) {
            return;
        }
        this.mRotateXSpeed = rotateXSpeed;
        this.mRotateXSpringAnimationFollow.getSpring().setStiffness((rotateXSpeed / 0.1f) * 100.0f);
        this.mRotateXSpringAnimation.getSpring().setStiffness((rotateXSpeed / 0.1f) * 100.0f);
    }

    public float getRotateYSpeed() {
        return this.mRotateYSpeed;
    }

    public void setRotateYSpeed(float rotateYSpeed) {
        if (rotateYSpeed <= 0.0f) {
            return;
        }
        this.mRotateYSpringAnimationFollow.getSpring().setStiffness((rotateYSpeed / 0.1f) * 100.0f);
        this.mRotateYSpringAnimation.getSpring().setStiffness((rotateYSpeed / 0.1f) * 100.0f);
        this.mRotateYSpeed = rotateYSpeed;
    }

    public float getLeftEyelidSizeScale() {
        return this.mLeftEyelid.sizeScale;
    }

    public void setLeftEyelidSizeScale(float scale) {
        this.mLeftEyelid.sizeScale = scale;
    }

    public float getMouthScaleSize() {
        return this.mMouth.scaleSize;
    }

    public void setMouthScaleSize(float scaleSize) {
        this.mMouth.scaleSize = scaleSize;
    }

    public float getRightEyelidSizeScale() {
        return this.mRightEyelid.sizeScale;
    }

    public void setRightEyelidSizeScale(float scale) {
        this.mRightEyelid.sizeScale = scale;
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

    public float getMouthRotateZ() {
        return this.mMouth.rotateZ;
    }

    public void setMouthRotateZ(float value) {
        this.mMouth.rotateZ = value;
    }

    public float getLeftEyeballRotateZ() {
        return this.mLeftEyeball.rotateZ;
    }

    public void setLeftEyeballRotateZ(float value) {
        this.mLeftEyeball.rotateZ = value;
    }

    public float getRightEyeballRotateZ() {
        return this.mRightEyeball.rotateZ;
    }

    public void setRightEyeballRotateZ(float value) {
        this.mRightEyeball.rotateZ = value;
    }

    public float getLeftEyelidRotateZ() {
        return this.mLeftEyelid.rotateZ;
    }

    public void setLeftEyelidRotateZ(float value) {
        this.mLeftEyelid.rotateZ = value;
    }

    public float getRightEyelidRotateZ() {
        return this.mRightEyelid.rotateZ;
    }

    public void setRightEyelidRotateZ(float value) {
        this.mRightEyelid.rotateZ = value;
    }

    public float getMouthTranslateZ() {
        return this.mMouth.translateZ;
    }

    public void setMouthTranslateZ(float value) {
        this.mMouth.translateZ = value;
    }

    public float getLeftEyeballTranslateZ() {
        return this.mLeftEyeball.translateZ;
    }

    public void setLeftEyeballTranslateZ(float value) {
        this.mLeftEyeball.translateZ = value;
    }

    public float getRightEyeballTranslateZ() {
        return this.mRightEyeball.translateZ;
    }

    public void setRightEyeballTranslateZ(float value) {
        this.mRightEyeball.translateZ = value;
    }

    public float getLeftEyelidTranslateZ() {
        return this.mLeftEyelid.translateZ;
    }

    public void setLeftEyelidTranslateZ(float value) {
        this.mLeftEyelid.translateZ = value;
    }

    public float getRightEyelidTranslateZ() {
        return this.mRightEyelid.translateZ;
    }

    public void setRightEyelidTranslateZ(float value) {
        this.mRightEyelid.translateZ = value;
    }

    public float getMouthPostScaleY() {
        return this.mMouth.postScaleY;
    }

    public void setMouthPostScaleY(float value) {
        this.mMouth.postScaleY = value;
    }

    public float getLeftEyeballPostScaleY() {
        return this.mLeftEyeball.postScaleY;
    }

    public void setLeftEyeballPostScaleY(float value) {
        this.mLeftEyeball.postScaleY = value;
    }

    public float getRightEyeballPostScaleY() {
        return this.mRightEyeball.postScaleY;
    }

    public void setRightEyeballPostScaleY(float value) {
        this.mRightEyeball.postScaleY = value;
    }

    public float getLeftEyelidPostScaleY() {
        return this.mLeftEyelid.postScaleY;
    }

    public void setLeftEyelidPostScaleY(float value) {
        this.mLeftEyelid.postScaleY = value;
    }

    public float getRightEyelidPostScaleY() {
        return this.mRightEyelid.postScaleY;
    }

    public void setRightEyelidPostScaleY(float value) {
        this.mRightEyelid.postScaleY = value;
    }

    public float getMouthPostScaleX() {
        return this.mMouth.postScaleX;
    }

    public void setMouthPostScaleX(float value) {
        this.mMouth.postScaleX = value;
    }

    public float getLeftEyeballPostScaleX() {
        return this.mLeftEyeball.postScaleX;
    }

    public void setLeftEyeballPostScaleX(float value) {
        this.mLeftEyeball.postScaleX = value;
    }

    public float getRightEyeballPostScaleX() {
        return this.mRightEyeball.postScaleX;
    }

    public void setRightEyeballPostScaleX(float value) {
        this.mRightEyeball.postScaleX = value;
    }

    public float getLeftEyelidPostScaleX() {
        return this.mLeftEyelid.postScaleX;
    }

    public void setLeftEyelidPostScaleX(float value) {
        this.mLeftEyelid.postScaleX = value;
    }

    public float getRightEyelidPostScaleX() {
        return this.mRightEyelid.postScaleX;
    }

    public void setRightEyelidPostScaleX(float value) {
        this.mRightEyelid.postScaleX = value;
    }

    public float getMouthRotateXRangeMin() {
        return this.mMouthRotateXRangeMin;
    }

    public void setMouthRotateXRangeMin(float mouthRotateXRangeMin) {
        this.mMouthRotateXRangeMin = mouthRotateXRangeMin;
    }

    public float getMouthRotateXRangeMax() {
        return this.mMouthRotateXRangeMax;
    }

    public void setMouthRotateXRangeMax(float mouthRotateXRangeMax) {
        this.mMouthRotateXRangeMax = mouthRotateXRangeMax;
    }

    public float getMouthRotateYRangeMin() {
        return this.mMouthRotateYRangeMin;
    }

    public void setMouthRotateYRangeMin(float mouthRotateYRangeMin) {
        this.mMouthRotateYRangeMin = mouthRotateYRangeMin;
    }

    public float getMouthRotateYRangeMax() {
        return this.mMouthRotateYRangeMax;
    }

    public void setMouthRotateYRangeMax(float mouthRotateYRangeMax) {
        this.mMouthRotateYRangeMax = mouthRotateYRangeMax;
    }

    public float getLeftEyelidRotateXRangeMin() {
        return this.mLeftEyelidRotateXRangeMin;
    }

    public void setLeftEyelidRotateXRangeMin(float leftEyelidRotateXRangeMin) {
        this.mLeftEyelidRotateXRangeMin = leftEyelidRotateXRangeMin;
    }

    public float getLeftEyelidRotateXRangeMax() {
        return this.mLeftEyelidRotateXRangeMax;
    }

    public void setLeftEyelidRotateXRangeMax(float leftEyelidRotateXRangeMax) {
        this.mLeftEyelidRotateXRangeMax = leftEyelidRotateXRangeMax;
    }

    public float getLeftEyelidRotateYRangeMin() {
        return this.mLeftEyelidRotateYRangeMin;
    }

    public void setLeftEyelidRotateYRangeMin(float leftEyelidRotateYRangeMin) {
        this.mLeftEyelidRotateYRangeMin = leftEyelidRotateYRangeMin;
    }

    public float getLeftEyelidRotateYRangeMax() {
        return this.mLeftEyelidRotateYRangeMax;
    }

    public void setLeftEyelidRotateYRangeMax(float leftEyelidRotateYRangeMax) {
        this.mLeftEyelidRotateYRangeMax = leftEyelidRotateYRangeMax;
    }

    public float getRightEyelidRotateXRangeMin() {
        return this.mRightEyelidRotateXRangeMin;
    }

    public void setRightEyelidRotateXRangeMin(float rightEyelidRotateXRangeMin) {
        this.mRightEyelidRotateXRangeMin = rightEyelidRotateXRangeMin;
    }

    public float getRightEyelidRotateXRangeMax() {
        return this.mRightEyelidRotateXRangeMax;
    }

    public void setRightEyelidRotateXRangeMax(float rightEyelidRotateXRangeMax) {
        this.mRightEyelidRotateXRangeMax = rightEyelidRotateXRangeMax;
    }

    public float getRightEyelidRotateYRangeMin() {
        return this.mRightEyelidRotateYRangeMin;
    }

    public void setRightEyelidRotateYRangeMin(float rightEyelidRotateYRangeMin) {
        this.mRightEyelidRotateYRangeMin = rightEyelidRotateYRangeMin;
    }

    public float getRightEyelidRotateYRangeMax() {
        return this.mRightEyelidRotateYRangeMax;
    }

    public void setRightEyelidRotateYRangeMax(float rightEyelidRotateYRangeMax) {
        this.mRightEyelidRotateYRangeMax = rightEyelidRotateYRangeMax;
    }

    public float getLeftEyeballRotateXRangeMin() {
        return this.mLeftEyeballRotateXRangeMin;
    }

    public void setLeftEyeballRotateXRangeMin(float leftEyeballRotateXRangeMin) {
        this.mLeftEyeballRotateXRangeMin = leftEyeballRotateXRangeMin;
    }

    public float getLeftEyeballRotateXRangeMax() {
        return this.mLeftEyeballRotateXRangeMax;
    }

    public void setLeftEyeballRotateXRangeMax(float leftEyeballRotateXRangeMax) {
        this.mLeftEyeballRotateXRangeMax = leftEyeballRotateXRangeMax;
    }

    public float getLeftEyeballRotateYRangeMin() {
        return this.mLeftEyeballRotateYRangeMin;
    }

    public void setLeftEyeballRotateYRangeMin(float leftEyeballRotateYRangeMin) {
        this.mLeftEyeballRotateYRangeMin = leftEyeballRotateYRangeMin;
    }

    public float getLeftEyeballRotateYRangeMax() {
        return this.mLeftEyeballRotateYRangeMax;
    }

    public void setLeftEyeballRotateYRangeMax(float leftEyeballRotateYRangeMax) {
        this.mLeftEyeballRotateYRangeMax = leftEyeballRotateYRangeMax;
    }

    public float getRightEyeballRotateXRangeMin() {
        return this.mRightEyeballRotateXRangeMin;
    }

    public void setRightEyeballRotateXRangeMin(float rightEyeballRotateXRangeMin) {
        this.mRightEyeballRotateXRangeMin = rightEyeballRotateXRangeMin;
    }

    public float getRightEyeballRotateXRangeMax() {
        return this.mRightEyeballRotateXRangeMax;
    }

    public void setRightEyeballRotateXRangeMax(float rightEyeballRotateXRangeMax) {
        this.mRightEyeballRotateXRangeMax = rightEyeballRotateXRangeMax;
    }

    public float getRightEyeballRotateYRangeMin() {
        return this.mRightEyeballRotateYRangeMin;
    }

    public void setRightEyeballRotateYRangeMin(float rightEyeballRotateYRangeMin) {
        this.mRightEyeballRotateYRangeMin = rightEyeballRotateYRangeMin;
    }

    public float getRightEyeballRotateYRangeMax() {
        return this.mRightEyeballRotateYRangeMax;
    }

    public void setRightEyeballRotateYRangeMax(float rightEyeballRotateYRangeMax) {
        this.mRightEyeballRotateYRangeMax = rightEyeballRotateYRangeMax;
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.Effect
    public void setAlpha(int alpha) {
        this.mLeftEyelid.setAlpha(alpha);
        this.mLeftEyeball.setAlpha(alpha);
        this.mRightEyelid.setAlpha(alpha);
        this.mRightEyeball.setAlpha(alpha);
        this.mMouth.setAlpha(alpha);
    }
}
