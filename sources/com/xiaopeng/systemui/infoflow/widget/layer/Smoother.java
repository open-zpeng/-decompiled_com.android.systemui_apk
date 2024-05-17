package com.xiaopeng.systemui.infoflow.widget.layer;

import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
/* loaded from: classes24.dex */
public class Smoother implements Runnable {
    private CallBack mCallBack;
    private float mCurrX;
    private float mCurrY;
    private float mDeltaX;
    private float mDeltaY;
    private int mDuration;
    private float mDurationReciprocal;
    private float mFinalX;
    private float mFinalY;
    private Interpolator mInterpolator;
    private long mStartTime;
    private float mStartX;
    private float mStartY;
    private boolean mFinished = true;
    private Interpolator mDefaultInterpolator = new LinearInterpolator();

    /* loaded from: classes24.dex */
    public interface CallBack {
        void onCallBack(float f, float f2);
    }

    public Smoother(CallBack callBack) {
        this.mCallBack = callBack;
    }

    public void start(float startX, float startY, float dx, float dy, int duration) {
        this.mDuration = duration;
        this.mStartTime = SystemClock.uptimeMillis();
        this.mStartX = startX;
        this.mStartY = startY;
        this.mFinalX = startX + dx;
        this.mFinalY = startY + dy;
        this.mDeltaX = dx;
        this.mDeltaY = dy;
        this.mDurationReciprocal = 1.0f / this.mDuration;
        if (this.mInterpolator == null) {
            this.mInterpolator = this.mDefaultInterpolator;
        }
        this.mFinished = false;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    public boolean computeOffset() {
        if (this.mFinished) {
            return false;
        }
        long timePassed = SystemClock.uptimeMillis() - this.mStartTime;
        if (timePassed < this.mDuration) {
            float x = this.mInterpolator.getInterpolation(((float) timePassed) * this.mDurationReciprocal);
            this.mCurrX = this.mStartX + (this.mDeltaX * x);
            this.mCurrY = this.mStartY + (this.mDeltaY * x);
        } else {
            this.mCurrX = this.mFinalX;
            this.mCurrY = this.mFinalY;
            this.mFinished = true;
        }
        return true;
    }

    public void stop() {
        this.mFinished = true;
    }

    public void abort() {
        this.mCurrX = this.mFinalX;
        this.mCurrY = this.mFinalY;
        this.mFinished = true;
    }

    public final boolean isFinished() {
        return this.mFinished;
    }

    public final float getCurrX() {
        return this.mCurrX;
    }

    public final float getCurrY() {
        return this.mCurrY;
    }

    @Override // java.lang.Runnable
    public void run() {
        CallBack callBack;
        if (computeOffset() && (callBack = this.mCallBack) != null) {
            callBack.onCallBack(this.mCurrX, this.mCurrY);
        }
    }
}
