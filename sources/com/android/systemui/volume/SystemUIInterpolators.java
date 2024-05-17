package com.android.systemui.volume;

import android.animation.TimeInterpolator;
/* loaded from: classes21.dex */
public class SystemUIInterpolators {

    /* loaded from: classes21.dex */
    public interface Callback {
        void onAnimatingChanged(boolean z);
    }

    /* loaded from: classes21.dex */
    public static final class LogDecelerateInterpolator implements TimeInterpolator {
        private final float mBase;
        private final float mDrift;
        private final float mOutputScale;
        private final float mTimeScale;

        public LogDecelerateInterpolator() {
            this(400.0f, 1.4f, 0.0f);
        }

        private LogDecelerateInterpolator(float base, float timeScale, float drift) {
            this.mBase = base;
            this.mDrift = drift;
            this.mTimeScale = 1.0f / timeScale;
            this.mOutputScale = 1.0f / computeLog(1.0f);
        }

        private float computeLog(float t) {
            return (1.0f - ((float) Math.pow(this.mBase, (-t) * this.mTimeScale))) + (this.mDrift * t);
        }

        @Override // android.animation.TimeInterpolator
        public float getInterpolation(float t) {
            return computeLog(t) * this.mOutputScale;
        }
    }

    /* loaded from: classes21.dex */
    public static final class LogAccelerateInterpolator implements TimeInterpolator {
        private final int mBase;
        private final int mDrift;
        private final float mLogScale;

        public LogAccelerateInterpolator() {
            this(100, 0);
        }

        private LogAccelerateInterpolator(int base, int drift) {
            this.mBase = base;
            this.mDrift = drift;
            this.mLogScale = 1.0f / computeLog(1.0f, this.mBase, this.mDrift);
        }

        private static float computeLog(float t, int base, int drift) {
            return ((float) (-Math.pow(base, -t))) + 1.0f + (drift * t);
        }

        @Override // android.animation.TimeInterpolator
        public float getInterpolation(float t) {
            return 1.0f - (computeLog(1.0f - t, this.mBase, this.mDrift) * this.mLogScale);
        }
    }
}
