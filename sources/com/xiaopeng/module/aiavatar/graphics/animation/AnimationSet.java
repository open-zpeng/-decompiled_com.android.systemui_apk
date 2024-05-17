package com.xiaopeng.module.aiavatar.graphics.animation;

import android.graphics.RectF;
import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.internal.LongCompanionObject;
/* loaded from: classes23.dex */
public class AnimationSet extends Animation {
    private static final int PROPERTY_CHANGE_BOUNDS_MASK = 128;
    private static final int PROPERTY_DURATION_MASK = 32;
    private static final int PROPERTY_FILL_AFTER_MASK = 1;
    private static final int PROPERTY_FILL_BEFORE_MASK = 2;
    private static final int PROPERTY_MORPH_MATRIX_MASK = 64;
    private static final int PROPERTY_REPEAT_MODE_MASK = 4;
    private static final int PROPERTY_SHARE_INTERPOLATOR_MASK = 16;
    private static final int PROPERTY_START_OFFSET_MASK = 8;
    private boolean mDirty;
    private boolean mHasAlpha;
    private long mLastEnd;
    private long[] mStoredOffsets;
    private int mFlags = 0;
    private ArrayList<Animation> mAnimations = new ArrayList<>();
    private Transformation mTempTransformation = new Transformation();

    public AnimationSet(boolean shareInterpolator) {
        setFlag(16, shareInterpolator);
        init();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    /* renamed from: clone */
    public AnimationSet mo43clone() throws CloneNotSupportedException {
        AnimationSet animation = (AnimationSet) super.mo43clone();
        animation.mTempTransformation = new Transformation();
        animation.mAnimations = new ArrayList<>();
        int count = this.mAnimations.size();
        ArrayList<Animation> animations = this.mAnimations;
        for (int i = 0; i < count; i++) {
            animation.mAnimations.add(animations.get(i).mo43clone());
        }
        return animation;
    }

    private void setFlag(int mask, boolean value) {
        if (value) {
            this.mFlags |= mask;
        } else {
            this.mFlags &= ~mask;
        }
    }

    private void init() {
        this.mStartTime = 0L;
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public void setFillAfter(boolean fillAfter) {
        this.mFlags |= 1;
        super.setFillAfter(fillAfter);
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public void setFillBefore(boolean fillBefore) {
        this.mFlags |= 2;
        super.setFillBefore(fillBefore);
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public void setRepeatMode(int repeatMode) {
        this.mFlags |= 4;
        super.setRepeatMode(repeatMode);
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public void setStartOffset(long startOffset) {
        this.mFlags |= 8;
        super.setStartOffset(startOffset);
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public boolean hasAlpha() {
        if (this.mDirty) {
            this.mHasAlpha = false;
            this.mDirty = false;
            int count = this.mAnimations.size();
            ArrayList<Animation> animations = this.mAnimations;
            int i = 0;
            while (true) {
                if (i >= count) {
                    break;
                } else if (!animations.get(i).hasAlpha()) {
                    i++;
                } else {
                    this.mHasAlpha = true;
                    break;
                }
            }
        }
        return this.mHasAlpha;
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public void setDuration(long durationMillis) {
        this.mFlags |= 32;
        super.setDuration(durationMillis);
        this.mLastEnd = this.mStartOffset + this.mDuration;
    }

    public void addAnimation(Animation a) {
        this.mAnimations.add(a);
        boolean noMatrix = (this.mFlags & 64) == 0;
        if (noMatrix && a.willChangeTransformationMatrix()) {
            this.mFlags |= 64;
        }
        boolean changeBounds = (this.mFlags & 128) == 0;
        if (changeBounds && a.willChangeBounds()) {
            this.mFlags |= 128;
        }
        if ((this.mFlags & 32) == 32) {
            this.mLastEnd = this.mStartOffset + this.mDuration;
        } else if (this.mAnimations.size() == 1) {
            this.mDuration = a.getStartOffset() + a.getDuration();
            this.mLastEnd = this.mStartOffset + this.mDuration;
        } else {
            this.mLastEnd = Math.max(this.mLastEnd, a.getStartOffset() + a.getDuration());
            this.mDuration = this.mLastEnd - this.mStartOffset;
        }
        this.mDirty = true;
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public void setStartTime(long startTimeMillis) {
        super.setStartTime(startTimeMillis);
        int count = this.mAnimations.size();
        ArrayList<Animation> animations = this.mAnimations;
        for (int i = 0; i < count; i++) {
            Animation a = animations.get(i);
            a.setStartTime(startTimeMillis);
        }
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public long getStartTime() {
        long startTime = LongCompanionObject.MAX_VALUE;
        int count = this.mAnimations.size();
        ArrayList<Animation> animations = this.mAnimations;
        for (int i = 0; i < count; i++) {
            Animation a = animations.get(i);
            startTime = Math.min(startTime, a.getStartTime());
        }
        return startTime;
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public void restrictDuration(long durationMillis) {
        super.restrictDuration(durationMillis);
        ArrayList<Animation> animations = this.mAnimations;
        int count = animations.size();
        for (int i = 0; i < count; i++) {
            animations.get(i).restrictDuration(durationMillis);
        }
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public long getDuration() {
        ArrayList<Animation> animations = this.mAnimations;
        int count = animations.size();
        long duration = 0;
        boolean durationSet = (this.mFlags & 32) == 32;
        if (durationSet) {
            long duration2 = this.mDuration;
            return duration2;
        }
        for (int i = 0; i < count; i++) {
            duration = Math.max(duration, animations.get(i).getDuration());
        }
        return duration;
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public long computeDurationHint() {
        long duration = 0;
        int count = this.mAnimations.size();
        ArrayList<Animation> animations = this.mAnimations;
        for (int i = count - 1; i >= 0; i--) {
            long d = animations.get(i).computeDurationHint();
            if (d > duration) {
                duration = d;
            }
        }
        return duration;
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public void initializeInvalidateRegion(int left, int top, int right, int bottom) {
        RectF region = this.mPreviousRegion;
        region.set(left, top, right, bottom);
        region.inset(-1.0f, -1.0f);
        if (this.mFillBefore) {
            int count = this.mAnimations.size();
            ArrayList<Animation> animations = this.mAnimations;
            Transformation temp = this.mTempTransformation;
            Transformation previousTransformation = this.mPreviousTransformation;
            for (int i = count - 1; i >= 0; i--) {
                Animation a = animations.get(i);
                if (!a.isFillEnabled() || a.getFillBefore() || a.getStartOffset() == 0) {
                    temp.clear();
                    Interpolator interpolator = a.mInterpolator;
                    a.applyTransformation(interpolator != null ? interpolator.getInterpolation(0.0f) : 0.0f, temp);
                    previousTransformation.compose(temp);
                }
            }
        }
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public boolean getTransformation(long currentTime, Transformation t) {
        int count = this.mAnimations.size();
        ArrayList<Animation> animations = this.mAnimations;
        Transformation temp = this.mTempTransformation;
        boolean more = false;
        boolean started = false;
        boolean ended = true;
        t.clear();
        int i = count - 1;
        while (true) {
            boolean z = true;
            if (i < 0) {
                break;
            }
            Animation a = animations.get(i);
            temp.clear();
            more = a.getTransformation(currentTime, temp, getScaleFactor()) || more;
            t.compose(temp);
            started = started || a.hasStarted();
            if (!a.hasEnded() || !ended) {
                z = false;
            }
            ended = z;
            i--;
        }
        if (started && !this.mStarted) {
            if (this.mListener != null) {
                this.mListener.onAnimationStart(this);
            }
            this.mStarted = true;
        }
        if (ended != this.mEnded) {
            if (this.mListener != null) {
                this.mListener.onAnimationEnd(this);
            }
            this.mEnded = ended;
        }
        return more;
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public void scaleCurrentDuration(float scale) {
        ArrayList<Animation> animations = this.mAnimations;
        int count = animations.size();
        for (int i = 0; i < count; i++) {
            animations.get(i).scaleCurrentDuration(scale);
        }
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        boolean durationSet;
        Interpolator interpolator;
        Interpolator interpolator2;
        boolean fillAfterSet;
        super.initialize(width, height, parentWidth, parentHeight);
        boolean durationSet2 = (this.mFlags & 32) == 32;
        boolean fillAfterSet2 = (this.mFlags & 1) == 1;
        boolean fillBeforeSet = (this.mFlags & 2) == 2;
        boolean repeatModeSet = (this.mFlags & 4) == 4;
        boolean shareInterpolator = (this.mFlags & 16) == 16;
        boolean startOffsetSet = (this.mFlags & 8) == 8;
        if (shareInterpolator) {
            ensureInterpolator();
        }
        ArrayList<Animation> children = this.mAnimations;
        int count = children.size();
        long duration = this.mDuration;
        boolean fillAfter = this.mFillAfter;
        boolean fillBefore = this.mFillBefore;
        int repeatMode = this.mRepeatMode;
        Interpolator interpolator3 = this.mInterpolator;
        Interpolator interpolator4 = interpolator3;
        long startOffset = this.mStartOffset;
        long[] storedOffsets = this.mStoredOffsets;
        if (startOffsetSet) {
            if (storedOffsets == null || storedOffsets.length != count) {
                long[] jArr = new long[count];
                this.mStoredOffsets = jArr;
                storedOffsets = jArr;
            }
        } else if (storedOffsets != null) {
            this.mStoredOffsets = null;
            storedOffsets = null;
        }
        int i = 0;
        while (i < count) {
            Animation a = children.get(i);
            if (durationSet2) {
                a.setDuration(duration);
            }
            if (fillAfterSet2) {
                a.setFillAfter(fillAfter);
            }
            if (fillBeforeSet) {
                a.setFillBefore(fillBefore);
            }
            if (repeatModeSet) {
                a.setRepeatMode(repeatMode);
            }
            if (!shareInterpolator) {
                durationSet = durationSet2;
                interpolator = interpolator4;
            } else {
                durationSet = durationSet2;
                interpolator = interpolator4;
                a.setInterpolator(interpolator);
            }
            if (!startOffsetSet) {
                interpolator2 = interpolator;
                fillAfterSet = fillAfterSet2;
            } else {
                long offset = a.getStartOffset();
                interpolator2 = interpolator;
                fillAfterSet = fillAfterSet2;
                a.setStartOffset(offset + startOffset);
                storedOffsets[i] = offset;
            }
            a.initialize(width, height, parentWidth, parentHeight);
            i++;
            fillAfterSet2 = fillAfterSet;
            durationSet2 = durationSet;
            startOffsetSet = startOffsetSet;
            children = children;
            interpolator4 = interpolator2;
        }
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public void reset() {
        super.reset();
        restoreChildrenStartOffset();
    }

    void restoreChildrenStartOffset() {
        long[] offsets = this.mStoredOffsets;
        if (offsets == null) {
            return;
        }
        ArrayList<Animation> children = this.mAnimations;
        int count = children.size();
        for (int i = 0; i < count; i++) {
            children.get(i).setStartOffset(offsets[i]);
        }
    }

    public List<Animation> getAnimations() {
        return this.mAnimations;
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public boolean willChangeTransformationMatrix() {
        return (this.mFlags & 64) == 64;
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public boolean willChangeBounds() {
        return (this.mFlags & 128) == 128;
    }
}
