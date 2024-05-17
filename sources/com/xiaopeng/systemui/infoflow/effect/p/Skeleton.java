package com.xiaopeng.systemui.infoflow.effect.p;

import android.animation.TimeInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;
import java.util.Arrays;
/* loaded from: classes24.dex */
public class Skeleton {
    private float[] mCurrentValues;
    private float[] mFromValues;
    private Listener mListener;
    private float mProgress;
    private float[] mToValues;
    private float mSpeed = 0.12f;
    private boolean mMoving = false;
    private TimeInterpolator mInterpolator = new AccelerateDecelerateInterpolator();

    /* loaded from: classes24.dex */
    public interface Listener {
        void onMoveEnd();
    }

    public Skeleton(float[] currentValues) {
        this.mCurrentValues = currentValues;
        float[] fArr = this.mCurrentValues;
        this.mFromValues = Arrays.copyOf(fArr, fArr.length);
        float[] fArr2 = this.mCurrentValues;
        this.mToValues = Arrays.copyOf(fArr2, fArr2.length);
    }

    public void setInterpolator(TimeInterpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public void setMoveToValues(float[] toValues) {
        int length = toValues.length;
        float[] fArr = this.mCurrentValues;
        if (length != fArr.length) {
            throw new IllegalArgumentException("toValues length error. toValues.length = " + toValues.length + ", mCurrentValues.length = " + this.mCurrentValues.length);
        }
        float[] fArr2 = this.mFromValues;
        System.arraycopy(fArr, 0, fArr2, 0, fArr2.length);
        this.mToValues = toValues;
        this.mProgress = 0.0f;
    }

    public boolean isMoving() {
        return this.mMoving;
    }

    public float getProgress() {
        return this.mProgress;
    }

    public boolean move() {
        float progress;
        float f = this.mProgress;
        if (f >= 1.0f) {
            Listener listener = this.mListener;
            if (listener != null) {
                listener.onMoveEnd();
            }
            this.mMoving = false;
            return false;
        }
        this.mProgress = f + this.mSpeed;
        float f2 = this.mProgress;
        if (f2 >= 1.0f) {
            this.mProgress = 1.0f;
            float[] fArr = this.mToValues;
            float[] fArr2 = this.mCurrentValues;
            System.arraycopy(fArr, 0, fArr2, 0, fArr2.length);
            Listener listener2 = this.mListener;
            if (listener2 != null) {
                listener2.onMoveEnd();
            }
            this.mMoving = false;
            return false;
        }
        int length = this.mCurrentValues.length;
        TimeInterpolator timeInterpolator = this.mInterpolator;
        if (timeInterpolator != null) {
            progress = timeInterpolator.getInterpolation(f2);
        } else {
            progress = this.mProgress;
        }
        for (int i = 0; i < length; i++) {
            float[] fArr3 = this.mCurrentValues;
            float[] fArr4 = this.mFromValues;
            fArr3[i] = fArr4[i] + ((this.mToValues[i] - fArr4[i]) * progress);
        }
        this.mMoving = true;
        return true;
    }

    public void setSpeed(float speed) {
        this.mSpeed = speed;
    }

    public float[] getToValues() {
        return this.mToValues;
    }

    public float[] getCurrentValues() {
        return this.mCurrentValues;
    }
}
