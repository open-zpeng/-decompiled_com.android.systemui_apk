package com.xiaopeng.module.aiavatar.graphics.animation;

import android.graphics.RectF;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
/* loaded from: classes23.dex */
public abstract class Animation implements Cloneable {
    public static final int ABSOLUTE = 0;
    public static final int INFINITE = -1;
    public static final int RELATIVE_TO_PARENT = 2;
    public static final int RELATIVE_TO_SELF = 1;
    public static final int RESTART = 1;
    public static final int REVERSE = 2;
    public static final int START_ON_FIRST_FRAME = -1;
    public static final int ZORDER_BOTTOM = -1;
    public static final int ZORDER_NORMAL = 0;
    public static final int ZORDER_TOP = 1;
    private int mBackgroundColor;
    long mDuration;
    Interpolator mInterpolator;
    AnimationListener mListener;
    private Handler mListenerHandler;
    private Runnable mOnEnd;
    private Runnable mOnRepeat;
    private Runnable mOnStart;
    long mStartOffset;
    private int mZAdjustment;
    boolean mEnded = false;
    boolean mStarted = false;
    boolean mCycleFlip = false;
    boolean mInitialized = false;
    boolean mFillBefore = true;
    boolean mFillAfter = false;
    boolean mFillEnabled = false;
    long mStartTime = -1;
    long mTotalDeltaTime = -1;
    int mRepeatCount = 0;
    int mRepeated = 0;
    int mRepeatMode = 1;
    private float mScaleFactor = 1.0f;
    private boolean mDetachWallpaper = false;
    private boolean mMore = true;
    private boolean mOneMoreTime = true;
    RectF mPreviousRegion = new RectF();
    RectF mRegion = new RectF();
    Transformation mTransformation = new Transformation();
    Transformation mPreviousTransformation = new Transformation();

    /* loaded from: classes23.dex */
    public interface AnimationListener {
        void onAnimationEnd(Animation animation);

        void onAnimationRepeat(Animation animation);

        void onAnimationStart(Animation animation);
    }

    public Animation() {
        ensureInterpolator();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // 
    /* renamed from: clone */
    public Animation mo43clone() throws CloneNotSupportedException {
        Animation animation = (Animation) super.clone();
        animation.mPreviousRegion = new RectF();
        animation.mRegion = new RectF();
        animation.mTransformation = new Transformation();
        animation.mPreviousTransformation = new Transformation();
        return animation;
    }

    public void reset() {
        this.mPreviousRegion.setEmpty();
        this.mPreviousTransformation.clear();
        this.mInitialized = false;
        this.mCycleFlip = false;
        this.mRepeated = 0;
        this.mMore = true;
        this.mOneMoreTime = true;
        this.mListenerHandler = null;
    }

    public void cancel() {
        if (this.mStarted && !this.mEnded) {
            fireAnimationEnd();
            this.mEnded = true;
        }
        this.mStartTime = Long.MIN_VALUE;
        this.mTotalDeltaTime = 0L;
        this.mOneMoreTime = false;
        this.mMore = false;
    }

    public void detach() {
        if (this.mStarted && !this.mEnded) {
            this.mEnded = true;
            fireAnimationEnd();
        }
    }

    public boolean isInitialized() {
        return this.mInitialized;
    }

    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        reset();
        this.mInitialized = true;
    }

    public void setListenerHandler(Handler handler) {
        if (this.mListenerHandler == null) {
            this.mOnStart = new Runnable() { // from class: com.xiaopeng.module.aiavatar.graphics.animation.Animation.1
                @Override // java.lang.Runnable
                public void run() {
                    if (Animation.this.mListener != null) {
                        Animation.this.mListener.onAnimationStart(Animation.this);
                    }
                }
            };
            this.mOnRepeat = new Runnable() { // from class: com.xiaopeng.module.aiavatar.graphics.animation.Animation.2
                @Override // java.lang.Runnable
                public void run() {
                    if (Animation.this.mListener != null) {
                        Animation.this.mListener.onAnimationRepeat(Animation.this);
                    }
                }
            };
            this.mOnEnd = new Runnable() { // from class: com.xiaopeng.module.aiavatar.graphics.animation.Animation.3
                @Override // java.lang.Runnable
                public void run() {
                    if (Animation.this.mListener != null) {
                        Animation.this.mListener.onAnimationEnd(Animation.this);
                    }
                }
            };
        }
        this.mListenerHandler = handler;
    }

    public void setInterpolator(Interpolator i) {
        this.mInterpolator = i;
    }

    public void setStartOffset(long startOffset) {
        this.mStartOffset = startOffset;
    }

    public void setDuration(long durationMillis) {
        if (durationMillis < 0) {
            throw new IllegalArgumentException("Animation duration cannot be negative");
        }
        this.mDuration = durationMillis;
    }

    public void restrictDuration(long durationMillis) {
        long j = this.mStartOffset;
        if (j > durationMillis) {
            this.mStartOffset = durationMillis;
            this.mDuration = 0L;
            this.mRepeatCount = 0;
            return;
        }
        long dur = this.mDuration + j;
        if (dur > durationMillis) {
            this.mDuration = durationMillis - j;
            dur = durationMillis;
        }
        if (this.mDuration <= 0) {
            this.mDuration = 0L;
            this.mRepeatCount = 0;
            return;
        }
        int i = this.mRepeatCount;
        if (i < 0 || i > durationMillis || i * dur > durationMillis) {
            this.mRepeatCount = ((int) (durationMillis / dur)) - 1;
            if (this.mRepeatCount < 0) {
                this.mRepeatCount = 0;
            }
        }
    }

    public void scaleCurrentDuration(float scale) {
        this.mDuration = ((float) this.mDuration) * scale;
        this.mStartOffset = ((float) this.mStartOffset) * scale;
    }

    public void setStartTime(long startTimeMillis) {
        this.mStartTime = startTimeMillis;
        this.mTotalDeltaTime = 0L;
        this.mEnded = false;
        this.mStarted = false;
        this.mCycleFlip = false;
        this.mRepeated = 0;
        this.mMore = true;
    }

    public void start() {
        setStartTime(-1L);
    }

    public void startNow() {
        setStartTime(AnimationUtils.currentAnimationTimeMillis());
    }

    public void setRepeatMode(int repeatMode) {
        this.mRepeatMode = repeatMode;
    }

    public void setRepeatCount(int repeatCount) {
        if (repeatCount < 0) {
            repeatCount = -1;
        }
        this.mRepeatCount = repeatCount;
    }

    public boolean isFillEnabled() {
        return this.mFillEnabled;
    }

    public void setFillEnabled(boolean fillEnabled) {
        this.mFillEnabled = fillEnabled;
    }

    public void setFillBefore(boolean fillBefore) {
        this.mFillBefore = fillBefore;
    }

    public void setFillAfter(boolean fillAfter) {
        this.mFillAfter = fillAfter;
    }

    public void setZAdjustment(int zAdjustment) {
        this.mZAdjustment = zAdjustment;
    }

    public void setBackgroundColor(int bg) {
        this.mBackgroundColor = bg;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getScaleFactor() {
        return this.mScaleFactor;
    }

    public void setDetachWallpaper(boolean detachWallpaper) {
        this.mDetachWallpaper = detachWallpaper;
    }

    public Interpolator getInterpolator() {
        return this.mInterpolator;
    }

    public long getStartTime() {
        return this.mStartTime;
    }

    public float getTotalDeltaTime() {
        return (float) this.mTotalDeltaTime;
    }

    public long getDuration() {
        return this.mDuration;
    }

    public long getStartOffset() {
        return this.mStartOffset;
    }

    public int getRepeatMode() {
        return this.mRepeatMode;
    }

    public int getRepeatCount() {
        return this.mRepeatCount;
    }

    public boolean getFillBefore() {
        return this.mFillBefore;
    }

    public boolean getFillAfter() {
        return this.mFillAfter;
    }

    public int getZAdjustment() {
        return this.mZAdjustment;
    }

    public int getBackgroundColor() {
        return this.mBackgroundColor;
    }

    public boolean getDetachWallpaper() {
        return this.mDetachWallpaper;
    }

    public boolean willChangeTransformationMatrix() {
        return true;
    }

    public boolean willChangeBounds() {
        return true;
    }

    public void setAnimationListener(AnimationListener listener) {
        this.mListener = listener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void ensureInterpolator() {
        if (this.mInterpolator == null) {
            this.mInterpolator = new AccelerateDecelerateInterpolator();
        }
    }

    public long computeDurationHint() {
        return (getStartOffset() + getDuration()) * (getRepeatCount() + 1);
    }

    public boolean getTransformation(long currentTime, Transformation outTransformation) {
        float normalizedTime;
        if (this.mStartTime == -1) {
            this.mStartTime = currentTime;
            this.mTotalDeltaTime = 0L;
        }
        long startOffset = getStartOffset();
        long duration = this.mDuration;
        if (duration != 0) {
            long j = this.mStartTime;
            normalizedTime = ((float) (currentTime - (j + startOffset))) / ((float) duration);
            this.mTotalDeltaTime = currentTime - j;
        } else {
            normalizedTime = currentTime < this.mStartTime ? 0.0f : 1.0f;
            this.mTotalDeltaTime = 0L;
        }
        boolean expired = normalizedTime >= 1.0f;
        this.mMore = !expired;
        if (!this.mFillEnabled) {
            normalizedTime = Math.max(Math.min(normalizedTime, 1.0f), 0.0f);
        }
        if ((normalizedTime >= 0.0f || this.mFillBefore) && (normalizedTime <= 1.0f || this.mFillAfter)) {
            if (!this.mStarted) {
                fireAnimationStart();
                this.mStarted = true;
            }
            if (this.mFillEnabled) {
                normalizedTime = Math.max(Math.min(normalizedTime, 1.0f), 0.0f);
            }
            if (this.mCycleFlip) {
                normalizedTime = 1.0f - normalizedTime;
            }
            float interpolatedTime = this.mInterpolator.getInterpolation(normalizedTime);
            applyTransformation(interpolatedTime, outTransformation);
        }
        if (expired) {
            int i = this.mRepeatCount;
            int i2 = this.mRepeated;
            if (i == i2) {
                if (!this.mEnded) {
                    this.mEnded = true;
                    fireAnimationEnd();
                }
            } else {
                if (i > 0) {
                    this.mRepeated = i2 + 1;
                }
                if (this.mRepeatMode == 2) {
                    this.mCycleFlip = !this.mCycleFlip;
                }
                this.mStartTime = -1L;
                this.mMore = true;
                fireAnimationRepeat();
            }
        }
        if (!this.mMore && this.mOneMoreTime) {
            this.mOneMoreTime = false;
            return true;
        }
        return this.mMore;
    }

    public boolean getTransformation(float deltaTime, Transformation outTransformation) {
        float normalizedTime;
        if (this.mStartTime == -1) {
            this.mStartTime = System.nanoTime();
            this.mTotalDeltaTime = 0L;
        }
        long startOffset = getStartOffset();
        long duration = this.mDuration;
        this.mTotalDeltaTime = ((float) this.mTotalDeltaTime) + deltaTime;
        if (duration == 0) {
            normalizedTime = ((float) System.nanoTime()) + deltaTime < ((float) this.mStartTime) ? 0.0f : 1.0f;
            this.mTotalDeltaTime = 0L;
        } else {
            normalizedTime = ((float) (this.mTotalDeltaTime - startOffset)) / ((float) duration);
            if (normalizedTime < 1.0f) {
                Log.d("tyler.tang", "mTotalDeltaTime = " + this.mTotalDeltaTime + ", normalizedTime = " + normalizedTime + ", startOffSet = " + startOffset);
            }
        }
        boolean expired = normalizedTime >= 1.0f;
        this.mMore = !expired;
        if (!this.mFillEnabled) {
            normalizedTime = Math.max(Math.min(normalizedTime, 1.0f), 0.0f);
        }
        if ((normalizedTime >= 0.0f || this.mFillBefore) && (normalizedTime <= 1.0f || this.mFillAfter)) {
            if (!this.mStarted) {
                fireAnimationStart();
                this.mStarted = true;
            }
            if (this.mFillEnabled) {
                normalizedTime = Math.max(Math.min(normalizedTime, 1.0f), 0.0f);
            }
            if (this.mCycleFlip) {
                normalizedTime = 1.0f - normalizedTime;
            }
            float interpolatedTime = this.mInterpolator.getInterpolation(normalizedTime);
            applyTransformation(interpolatedTime, outTransformation);
        }
        if (expired) {
            int i = this.mRepeatCount;
            int i2 = this.mRepeated;
            if (i == i2) {
                if (!this.mEnded) {
                    this.mEnded = true;
                    fireAnimationEnd();
                }
            } else {
                if (i > 0) {
                    this.mRepeated = i2 + 1;
                }
                if (this.mRepeatMode == 2) {
                    this.mCycleFlip = !this.mCycleFlip;
                }
                this.mStartTime = -1L;
                this.mTotalDeltaTime = 0L;
                this.mMore = true;
                fireAnimationRepeat();
            }
        }
        if (!this.mMore && this.mOneMoreTime) {
            this.mOneMoreTime = false;
            return true;
        }
        return this.mMore;
    }

    private void fireAnimationStart() {
        AnimationListener animationListener = this.mListener;
        if (animationListener != null) {
            Handler handler = this.mListenerHandler;
            if (handler != null) {
                handler.postAtFrontOfQueue(this.mOnStart);
            } else {
                animationListener.onAnimationStart(this);
            }
        }
    }

    private void fireAnimationRepeat() {
        AnimationListener animationListener = this.mListener;
        if (animationListener != null) {
            Handler handler = this.mListenerHandler;
            if (handler != null) {
                handler.postAtFrontOfQueue(this.mOnRepeat);
            } else {
                animationListener.onAnimationRepeat(this);
            }
        }
    }

    private void fireAnimationEnd() {
        AnimationListener animationListener = this.mListener;
        if (animationListener != null) {
            Handler handler = this.mListenerHandler;
            if (handler != null) {
                handler.postAtFrontOfQueue(this.mOnEnd);
            } else {
                animationListener.onAnimationEnd(this);
            }
        }
    }

    public boolean getTransformation(long currentTime, Transformation outTransformation, float scale) {
        this.mScaleFactor = scale;
        return getTransformation(currentTime, outTransformation);
    }

    public boolean hasStarted() {
        return this.mStarted;
    }

    public boolean hasEnded() {
        return this.mEnded;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void applyTransformation(float interpolatedTime, Transformation t) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float resolveSize(int type, float value, int size, int parentSize) {
        if (type != 0) {
            if (type != 1) {
                if (type == 2) {
                    return parentSize * value;
                }
                return value;
            }
            return size * value;
        }
        return value;
    }

    public void initializeInvalidateRegion(int left, int top, int right, int bottom) {
        RectF region = this.mPreviousRegion;
        region.set(left, top, right, bottom);
        region.inset(-1.0f, -1.0f);
        if (this.mFillBefore) {
            Transformation previousTransformation = this.mPreviousTransformation;
            applyTransformation(this.mInterpolator.getInterpolation(0.0f), previousTransformation);
        }
    }

    public boolean hasAlpha() {
        return false;
    }

    /* loaded from: classes23.dex */
    protected static class Description {
        public int type;
        public float value;

        protected Description() {
        }

        static Description parseValue(TypedValue value) {
            Description d = new Description();
            if (value == null) {
                d.type = 0;
                d.value = 0.0f;
            } else if (value.type == 6) {
                d.type = (value.data & 15) == 1 ? 2 : 1;
                d.value = TypedValue.complexToFloat(value.data);
                return d;
            } else if (value.type == 4) {
                d.type = 0;
                d.value = value.getFloat();
                return d;
            } else if (value.type >= 16 && value.type <= 31) {
                d.type = 0;
                d.value = value.data;
                return d;
            }
            d.type = 0;
            d.value = 0.0f;
            return d;
        }
    }
}
