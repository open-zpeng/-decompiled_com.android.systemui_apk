package com.xiaopeng.module.aiavatar.graphics.animation;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
/* loaded from: classes23.dex */
public class AnimationControler {
    private float mDuration;
    private float mFromX;
    private float mStartTime;
    private float mToX;
    public static final Vector3 X_VECTOR = new Vector3(1.0f, 0.0f, 0.0f);
    public static final Vector3 Y_VECTOR = new Vector3(0.0f, 1.0f, 0.0f);
    public static final Vector3 Z_VECTOR = new Vector3(0.0f, 0.0f, 1.0f);
    public static final Vector3 OR_VECTOR = new Vector3(0.0f, 0.0f, 0.0f);
    public static float TOUCH_UP_ANIMATION_DURATION = 1800.0f;
    private Matrix4 mTempMatrix4 = new Matrix4();
    private boolean mOnAnimationFlag = false;
    private Matrix4 mResult = new Matrix4();
    private Vector3 mCenter = new Vector3();
    private float mMaxAngle = 40.0f;

    private void setFrom(float from) {
        this.mFromX = from;
    }

    private void setTo(float to) {
        this.mToX = to;
    }

    public boolean getState() {
        return this.mOnAnimationFlag;
    }

    public AnimationControler() {
        this.mDuration = 1000.0f;
        this.mDuration = TOUCH_UP_ANIMATION_DURATION;
    }

    public Matrix4 onAnimation(float deltaTime) {
        if (!this.mOnAnimationFlag) {
            return null;
        }
        float fromX = this.mFromX;
        float toX = this.mToX;
        float stepTime = ((float) AnimationUtils.currentAnimationTimeMillis()) - this.mStartTime;
        float t = stepTime / this.mDuration;
        float curAngle = ((toX - fromX) * t) + fromX;
        float newY = MathUtils.sinDeg(curAngle);
        float t2 = Math.max(0.0f, Math.min(t, 1.0f));
        Application application = Gdx.app;
        application.log("animation", "当前角度:\t" + curAngle);
        float angle = this.mMaxAngle * (1.0f - t2) * newY;
        float yTran = ((1.0f - t2) * newY) + 1.5f;
        this.mCenter.set(OR_VECTOR);
        this.mTempMatrix4.idt();
        this.mTempMatrix4.translate(0.0f, yTran, 0.0f);
        this.mCenter.mul(this.mTempMatrix4);
        this.mResult.idt();
        this.mResult.translate(this.mCenter);
        this.mResult.rotate(Z_VECTOR, angle);
        this.mResult.translate(-this.mCenter.x, -this.mCenter.y, -this.mCenter.z);
        if (t2 == 1.0f) {
            this.mOnAnimationFlag = false;
        }
        return this.mResult;
    }

    public void startAnimation(int screenX, int screenY, int button, Vector3 downPosition, float speed) {
        float speed2 = Math.abs(speed);
        float mDurationIncrement = (speed2 - 1200.0f) * 0.45f;
        this.mDuration = 1800.0f + mDurationIncrement;
        float angleIncrement = (speed2 - 1200.0f) * 0.45f;
        this.mMaxAngle = ((speed2 - 1200.0f) * 0.01f) + 40.0f;
        if (screenX - downPosition.x > 0.0f) {
            this.mFromX = 0.0f;
            this.mToX = (-1080.0f) - angleIncrement;
        } else {
            this.mFromX = 0.0f;
            this.mToX = 1080.0f + angleIncrement;
        }
        this.mOnAnimationFlag = true;
        this.mStartTime = (float) AnimationUtils.currentAnimationTimeMillis();
    }
}
