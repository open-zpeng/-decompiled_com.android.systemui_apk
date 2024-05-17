package com.android.systemui.qs.touch;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
/* loaded from: classes21.dex */
public class SwipeDetector {
    private static final float ANIMATION_DURATION = 1200.0f;
    private static final boolean DBG = false;
    public static final int DIRECTION_BOTH = 3;
    public static final int DIRECTION_NEGATIVE = 2;
    public static final int DIRECTION_POSITIVE = 1;
    public static final float RELEASE_VELOCITY_PX_MS = 1.0f;
    public static final float SCROLL_VELOCITY_DAMPENING_RC = 15.915494f;
    private static final String TAG = "SwipeDetector";
    protected int mActivePointerId;
    private long mCurrentMillis;
    private final Direction mDir;
    private float mDisplacement;
    private final PointF mDownPos;
    private boolean mIgnoreSlopWhenSettling;
    private float mLastDisplacement;
    private final PointF mLastPos;
    private final Listener mListener;
    private int mScrollConditions;
    private ScrollState mState;
    private float mSubtractDisplacement;
    private final float mTouchSlop;
    private float mVelocity;
    public static final Direction VERTICAL = new Direction() { // from class: com.android.systemui.qs.touch.SwipeDetector.1
        @Override // com.android.systemui.qs.touch.SwipeDetector.Direction
        float getDisplacement(MotionEvent ev, int pointerIndex, PointF refPoint) {
            return ev.getY(pointerIndex) - refPoint.y;
        }

        @Override // com.android.systemui.qs.touch.SwipeDetector.Direction
        float getActiveTouchSlop(MotionEvent ev, int pointerIndex, PointF downPos) {
            return Math.abs(ev.getX(pointerIndex) - downPos.x);
        }
    };
    public static final Direction HORIZONTAL = new Direction() { // from class: com.android.systemui.qs.touch.SwipeDetector.2
        @Override // com.android.systemui.qs.touch.SwipeDetector.Direction
        float getDisplacement(MotionEvent ev, int pointerIndex, PointF refPoint) {
            return ev.getX(pointerIndex) - refPoint.x;
        }

        @Override // com.android.systemui.qs.touch.SwipeDetector.Direction
        float getActiveTouchSlop(MotionEvent ev, int pointerIndex, PointF downPos) {
            return Math.abs(ev.getY(pointerIndex) - downPos.y);
        }
    };

    /* loaded from: classes21.dex */
    public static abstract class Direction {
        abstract float getActiveTouchSlop(MotionEvent motionEvent, int i, PointF pointF);

        abstract float getDisplacement(MotionEvent motionEvent, int i, PointF pointF);
    }

    /* loaded from: classes21.dex */
    public interface Listener {
        boolean onDrag(float f, float f2);

        void onDragEnd(float f, boolean z);

        void onDragStart(boolean z);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public enum ScrollState {
        IDLE,
        DRAGGING,
        SETTLING
    }

    private void setState(ScrollState newState) {
        if (newState == ScrollState.DRAGGING) {
            initializeDragging();
            if (this.mState == ScrollState.IDLE) {
                reportDragStart(false);
            } else if (this.mState == ScrollState.SETTLING) {
                reportDragStart(true);
            }
        }
        if (newState == ScrollState.SETTLING) {
            reportDragEnd();
        }
        this.mState = newState;
    }

    public boolean isDraggingOrSettling() {
        return this.mState == ScrollState.DRAGGING || this.mState == ScrollState.SETTLING;
    }

    public boolean isIdleState() {
        return this.mState == ScrollState.IDLE;
    }

    public boolean isSettlingState() {
        return this.mState == ScrollState.SETTLING;
    }

    public boolean isDraggingState() {
        return this.mState == ScrollState.DRAGGING;
    }

    public SwipeDetector(@NonNull Context context, @NonNull Listener l, @NonNull Direction dir) {
        this(ViewConfiguration.get(context).getScaledTouchSlop(), l, dir);
    }

    @VisibleForTesting
    protected SwipeDetector(float touchSlope, @NonNull Listener l, @NonNull Direction dir) {
        this.mActivePointerId = -1;
        this.mState = ScrollState.IDLE;
        this.mDownPos = new PointF();
        this.mLastPos = new PointF();
        this.mTouchSlop = touchSlope;
        this.mListener = l;
        this.mDir = dir;
    }

    public void setDetectableScrollConditions(int scrollDirectionFlags, boolean ignoreSlop) {
        this.mScrollConditions = scrollDirectionFlags;
        this.mIgnoreSlopWhenSettling = ignoreSlop;
    }

    private boolean shouldScrollStart(MotionEvent ev, int pointerIndex) {
        if (Math.max(this.mDir.getActiveTouchSlop(ev, pointerIndex, this.mDownPos), this.mTouchSlop) > Math.abs(this.mDisplacement)) {
            return false;
        }
        return ((this.mScrollConditions & 2) > 0 && this.mDisplacement > 0.0f) || ((this.mScrollConditions & 1) > 0 && this.mDisplacement < 0.0f);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        int actionMasked = ev.getActionMasked();
        if (actionMasked == 0) {
            this.mActivePointerId = ev.getPointerId(0);
            this.mDownPos.set(ev.getX(), ev.getY());
            this.mLastPos.set(this.mDownPos);
            this.mLastDisplacement = 0.0f;
            this.mDisplacement = 0.0f;
            this.mVelocity = 0.0f;
            if (this.mState == ScrollState.SETTLING && this.mIgnoreSlopWhenSettling) {
                setState(ScrollState.DRAGGING);
            }
        } else {
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    int pointerIndex = ev.findPointerIndex(this.mActivePointerId);
                    if (pointerIndex != -1) {
                        this.mDisplacement = this.mDir.getDisplacement(ev, pointerIndex, this.mDownPos);
                        computeVelocity(this.mDir.getDisplacement(ev, pointerIndex, this.mLastPos), ev.getEventTime());
                        if (this.mState != ScrollState.DRAGGING && shouldScrollStart(ev, pointerIndex)) {
                            setState(ScrollState.DRAGGING);
                        }
                        if (this.mState == ScrollState.DRAGGING) {
                            reportDragging();
                        }
                        this.mLastPos.set(ev.getX(pointerIndex), ev.getY(pointerIndex));
                    }
                } else if (actionMasked != 3) {
                    if (actionMasked == 6) {
                        int ptrIdx = ev.getActionIndex();
                        int ptrId = ev.getPointerId(ptrIdx);
                        if (ptrId == this.mActivePointerId) {
                            int newPointerIdx = ptrIdx == 0 ? 1 : 0;
                            this.mDownPos.set(ev.getX(newPointerIdx) - (this.mLastPos.x - this.mDownPos.x), ev.getY(newPointerIdx) - (this.mLastPos.y - this.mDownPos.y));
                            this.mLastPos.set(ev.getX(newPointerIdx), ev.getY(newPointerIdx));
                            this.mActivePointerId = ev.getPointerId(newPointerIdx);
                        }
                    }
                }
            }
            if (this.mState == ScrollState.DRAGGING) {
                setState(ScrollState.SETTLING);
            }
        }
        return true;
    }

    public void finishedScrolling() {
        setState(ScrollState.IDLE);
    }

    private boolean reportDragStart(boolean recatch) {
        this.mListener.onDragStart(!recatch);
        return true;
    }

    private void initializeDragging() {
        if (this.mState == ScrollState.SETTLING && this.mIgnoreSlopWhenSettling) {
            this.mSubtractDisplacement = 0.0f;
        }
        if (this.mDisplacement > 0.0f) {
            this.mSubtractDisplacement = this.mTouchSlop;
        } else {
            this.mSubtractDisplacement = -this.mTouchSlop;
        }
    }

    private boolean reportDragging() {
        float f = this.mDisplacement;
        if (f != this.mLastDisplacement) {
            this.mLastDisplacement = f;
            return this.mListener.onDrag(f - this.mSubtractDisplacement, this.mVelocity);
        }
        return true;
    }

    private void reportDragEnd() {
        Listener listener = this.mListener;
        float f = this.mVelocity;
        listener.onDragEnd(f, Math.abs(f) > 1.0f);
    }

    public float computeVelocity(float delta, long currentMillis) {
        long previousMillis = this.mCurrentMillis;
        this.mCurrentMillis = currentMillis;
        float deltaTimeMillis = (float) (this.mCurrentMillis - previousMillis);
        float velocity = deltaTimeMillis > 0.0f ? delta / deltaTimeMillis : 0.0f;
        if (Math.abs(this.mVelocity) < 0.001f) {
            this.mVelocity = velocity;
        } else {
            float alpha = computeDampeningFactor(deltaTimeMillis);
            this.mVelocity = interpolate(this.mVelocity, velocity, alpha);
        }
        float alpha2 = this.mVelocity;
        return alpha2;
    }

    private static float computeDampeningFactor(float deltaTime) {
        return deltaTime / (15.915494f + deltaTime);
    }

    private static float interpolate(float from, float to, float alpha) {
        return ((1.0f - alpha) * from) + (alpha * to);
    }

    public static long calculateDuration(float velocity, float progressNeeded) {
        float velocityDivisor = Math.max(2.0f, Math.abs(0.5f * velocity));
        float travelDistance = Math.max(0.2f, progressNeeded);
        long duration = Math.max(100.0f, (ANIMATION_DURATION / velocityDivisor) * travelDistance);
        return duration;
    }
}
