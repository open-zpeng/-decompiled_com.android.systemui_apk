package com.android.systemui.statusbar;

import android.animation.Animator;
import android.content.Context;
import android.util.Log;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import com.android.systemui.Interpolators;
import com.android.systemui.statusbar.notification.NotificationUtils;
/* loaded from: classes21.dex */
public class FlingAnimationUtils {
    private static final float HIGH_VELOCITY_DP_PER_SECOND = 3000.0f;
    private static final float LINEAR_OUT_FASTER_IN_X2 = 0.5f;
    private static final float LINEAR_OUT_FASTER_IN_Y2_MAX = 0.5f;
    private static final float LINEAR_OUT_FASTER_IN_Y2_MIN = 0.4f;
    private static final float LINEAR_OUT_SLOW_IN_START_GRADIENT = 0.75f;
    private static final float LINEAR_OUT_SLOW_IN_X2 = 0.35f;
    private static final float LINEAR_OUT_SLOW_IN_X2_MAX = 0.68f;
    private static final float MIN_VELOCITY_DP_PER_SECOND = 250.0f;
    private static final String TAG = "FlingAnimationUtils";
    private AnimatorProperties mAnimatorProperties;
    private float mCachedStartGradient;
    private float mCachedVelocityFactor;
    private float mHighVelocityPxPerSecond;
    private PathInterpolator mInterpolator;
    private float mLinearOutSlowInX2;
    private float mMaxLengthSeconds;
    private float mMinVelocityPxPerSecond;
    private final float mSpeedUpFactor;
    private final float mY2;

    public FlingAnimationUtils(Context ctx, float maxLengthSeconds) {
        this(ctx, maxLengthSeconds, 0.0f);
    }

    public FlingAnimationUtils(Context ctx, float maxLengthSeconds, float speedUpFactor) {
        this(ctx, maxLengthSeconds, speedUpFactor, -1.0f, 1.0f);
    }

    public FlingAnimationUtils(Context ctx, float maxLengthSeconds, float speedUpFactor, float x2, float y2) {
        this.mAnimatorProperties = new AnimatorProperties();
        this.mCachedStartGradient = -1.0f;
        this.mCachedVelocityFactor = -1.0f;
        this.mMaxLengthSeconds = maxLengthSeconds;
        this.mSpeedUpFactor = speedUpFactor;
        if (x2 < 0.0f) {
            this.mLinearOutSlowInX2 = NotificationUtils.interpolate(LINEAR_OUT_SLOW_IN_X2, LINEAR_OUT_SLOW_IN_X2_MAX, this.mSpeedUpFactor);
        } else {
            this.mLinearOutSlowInX2 = x2;
        }
        this.mY2 = y2;
        this.mMinVelocityPxPerSecond = ctx.getResources().getDisplayMetrics().density * MIN_VELOCITY_DP_PER_SECOND;
        this.mHighVelocityPxPerSecond = ctx.getResources().getDisplayMetrics().density * HIGH_VELOCITY_DP_PER_SECOND;
    }

    public void apply(Animator animator, float currValue, float endValue, float velocity) {
        apply(animator, currValue, endValue, velocity, Math.abs(endValue - currValue));
    }

    public void apply(ViewPropertyAnimator animator, float currValue, float endValue, float velocity) {
        apply(animator, currValue, endValue, velocity, Math.abs(endValue - currValue));
    }

    public void apply(Animator animator, float currValue, float endValue, float velocity, float maxDistance) {
        AnimatorProperties properties = getProperties(currValue, endValue, velocity, maxDistance);
        animator.setDuration(properties.duration);
        animator.setInterpolator(properties.interpolator);
    }

    public void apply(ViewPropertyAnimator animator, float currValue, float endValue, float velocity, float maxDistance) {
        AnimatorProperties properties = getProperties(currValue, endValue, velocity, maxDistance);
        animator.setDuration(properties.duration);
        animator.setInterpolator(properties.interpolator);
    }

    private AnimatorProperties getProperties(float currValue, float endValue, float velocity, float maxDistance) {
        float maxLengthSeconds = (float) (this.mMaxLengthSeconds * Math.sqrt(Math.abs(endValue - currValue) / maxDistance));
        float diff = Math.abs(endValue - currValue);
        float velAbs = Math.abs(velocity);
        float velocityFactor = this.mSpeedUpFactor != 0.0f ? Math.min(velAbs / HIGH_VELOCITY_DP_PER_SECOND, 1.0f) : 1.0f;
        float startGradient = NotificationUtils.interpolate(0.75f, this.mY2 / this.mLinearOutSlowInX2, velocityFactor);
        float durationSeconds = (startGradient * diff) / velAbs;
        Interpolator slowInInterpolator = getInterpolator(startGradient, velocityFactor);
        if (durationSeconds <= maxLengthSeconds) {
            this.mAnimatorProperties.interpolator = slowInInterpolator;
        } else if (velAbs >= this.mMinVelocityPxPerSecond) {
            durationSeconds = maxLengthSeconds;
            VelocityInterpolator velocityInterpolator = new VelocityInterpolator(durationSeconds, velAbs, diff);
            InterpolatorInterpolator superInterpolator = new InterpolatorInterpolator(velocityInterpolator, slowInInterpolator, Interpolators.LINEAR_OUT_SLOW_IN);
            this.mAnimatorProperties.interpolator = superInterpolator;
        } else {
            durationSeconds = maxLengthSeconds;
            this.mAnimatorProperties.interpolator = Interpolators.FAST_OUT_SLOW_IN;
        }
        AnimatorProperties animatorProperties = this.mAnimatorProperties;
        animatorProperties.duration = 1000.0f * durationSeconds;
        return animatorProperties;
    }

    private Interpolator getInterpolator(float startGradient, float velocityFactor) {
        if (Float.isNaN(velocityFactor)) {
            Log.e(TAG, "Invalid velocity factor", new Throwable());
            return Interpolators.LINEAR_OUT_SLOW_IN;
        }
        if (startGradient != this.mCachedStartGradient || velocityFactor != this.mCachedVelocityFactor) {
            float speedup = this.mSpeedUpFactor * (1.0f - velocityFactor);
            float y1 = speedup * startGradient;
            float x2 = this.mLinearOutSlowInX2;
            float y2 = this.mY2;
            try {
                this.mInterpolator = new PathInterpolator(speedup, y1, x2, y2);
                this.mCachedStartGradient = startGradient;
                this.mCachedVelocityFactor = velocityFactor;
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Illegal path with x1=" + speedup + " y1=" + y1 + " x2=" + x2 + " y2=" + y2, e);
            }
        }
        return this.mInterpolator;
    }

    public void applyDismissing(Animator animator, float currValue, float endValue, float velocity, float maxDistance) {
        AnimatorProperties properties = getDismissingProperties(currValue, endValue, velocity, maxDistance);
        animator.setDuration(properties.duration);
        animator.setInterpolator(properties.interpolator);
    }

    public void applyDismissing(ViewPropertyAnimator animator, float currValue, float endValue, float velocity, float maxDistance) {
        AnimatorProperties properties = getDismissingProperties(currValue, endValue, velocity, maxDistance);
        animator.setDuration(properties.duration);
        animator.setInterpolator(properties.interpolator);
    }

    private AnimatorProperties getDismissingProperties(float currValue, float endValue, float velocity, float maxDistance) {
        float maxLengthSeconds = (float) (this.mMaxLengthSeconds * Math.pow(Math.abs(endValue - currValue) / maxDistance, 0.5d));
        float diff = Math.abs(endValue - currValue);
        float velAbs = Math.abs(velocity);
        float y2 = calculateLinearOutFasterInY2(velAbs);
        float startGradient = y2 / 0.5f;
        Interpolator mLinearOutFasterIn = new PathInterpolator(0.0f, 0.0f, 0.5f, y2);
        float durationSeconds = (startGradient * diff) / velAbs;
        if (durationSeconds <= maxLengthSeconds) {
            this.mAnimatorProperties.interpolator = mLinearOutFasterIn;
        } else if (velAbs >= this.mMinVelocityPxPerSecond) {
            durationSeconds = maxLengthSeconds;
            VelocityInterpolator velocityInterpolator = new VelocityInterpolator(durationSeconds, velAbs, diff);
            InterpolatorInterpolator superInterpolator = new InterpolatorInterpolator(velocityInterpolator, mLinearOutFasterIn, Interpolators.LINEAR_OUT_SLOW_IN);
            this.mAnimatorProperties.interpolator = superInterpolator;
        } else {
            durationSeconds = maxLengthSeconds;
            this.mAnimatorProperties.interpolator = Interpolators.FAST_OUT_LINEAR_IN;
        }
        AnimatorProperties animatorProperties = this.mAnimatorProperties;
        animatorProperties.duration = 1000.0f * durationSeconds;
        return animatorProperties;
    }

    private float calculateLinearOutFasterInY2(float velocity) {
        float f = this.mMinVelocityPxPerSecond;
        float t = Math.max(0.0f, Math.min(1.0f, (velocity - f) / (this.mHighVelocityPxPerSecond - f)));
        return ((1.0f - t) * LINEAR_OUT_FASTER_IN_Y2_MIN) + (0.5f * t);
    }

    public float getMinVelocityPxPerSecond() {
        return this.mMinVelocityPxPerSecond;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static final class InterpolatorInterpolator implements Interpolator {
        private Interpolator mCrossfader;
        private Interpolator mInterpolator1;
        private Interpolator mInterpolator2;

        InterpolatorInterpolator(Interpolator interpolator1, Interpolator interpolator2, Interpolator crossfader) {
            this.mInterpolator1 = interpolator1;
            this.mInterpolator2 = interpolator2;
            this.mCrossfader = crossfader;
        }

        @Override // android.animation.TimeInterpolator
        public float getInterpolation(float input) {
            float t = this.mCrossfader.getInterpolation(input);
            return ((1.0f - t) * this.mInterpolator1.getInterpolation(input)) + (this.mInterpolator2.getInterpolation(input) * t);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static final class VelocityInterpolator implements Interpolator {
        private float mDiff;
        private float mDurationSeconds;
        private float mVelocity;

        private VelocityInterpolator(float durationSeconds, float velocity, float diff) {
            this.mDurationSeconds = durationSeconds;
            this.mVelocity = velocity;
            this.mDiff = diff;
        }

        @Override // android.animation.TimeInterpolator
        public float getInterpolation(float input) {
            float time = this.mDurationSeconds * input;
            return (this.mVelocity * time) / this.mDiff;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class AnimatorProperties {
        long duration;
        Interpolator interpolator;

        private AnimatorProperties() {
        }
    }
}
