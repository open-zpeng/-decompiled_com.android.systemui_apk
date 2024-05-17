package com.android.systemui.pip.phone;

import android.graphics.PointF;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import com.android.internal.annotations.VisibleForTesting;
import java.io.PrintWriter;
/* loaded from: classes21.dex */
public class PipTouchState {
    private static final boolean DEBUG = false;
    @VisibleForTesting
    static final long DOUBLE_TAP_TIMEOUT = 200;
    private static final String TAG = "PipTouchHandler";
    private int mActivePointerId;
    private final Runnable mDoubleTapTimeoutCallback;
    private final Handler mHandler;
    private VelocityTracker mVelocityTracker;
    private final ViewConfiguration mViewConfig;
    private long mDownTouchTime = 0;
    private long mLastDownTouchTime = 0;
    private long mUpTouchTime = 0;
    private final PointF mDownTouch = new PointF();
    private final PointF mDownDelta = new PointF();
    private final PointF mLastTouch = new PointF();
    private final PointF mLastDelta = new PointF();
    private final PointF mVelocity = new PointF();
    private boolean mAllowTouches = true;
    private boolean mIsUserInteracting = false;
    private boolean mIsDoubleTap = false;
    private boolean mIsWaitingForDoubleTap = false;
    private boolean mIsDragging = false;
    private boolean mPreviouslyDragging = false;
    private boolean mStartedDragging = false;
    private boolean mAllowDraggingOffscreen = false;

    public PipTouchState(ViewConfiguration viewConfig, Handler handler, Runnable doubleTapTimeoutCallback) {
        this.mViewConfig = viewConfig;
        this.mHandler = handler;
        this.mDoubleTapTimeoutCallback = doubleTapTimeoutCallback;
    }

    public void reset() {
        this.mAllowDraggingOffscreen = false;
        this.mIsDragging = false;
        this.mStartedDragging = false;
        this.mIsUserInteracting = false;
    }

    public void onTouchEvent(MotionEvent ev) {
        int actionMasked = ev.getActionMasked();
        boolean z = false;
        z = false;
        z = false;
        boolean z2 = true;
        if (actionMasked == 0) {
            if (!this.mAllowTouches) {
                return;
            }
            initOrResetVelocityTracker();
            addMovement(ev);
            this.mActivePointerId = ev.getPointerId(0);
            this.mLastTouch.set(ev.getRawX(), ev.getRawY());
            this.mDownTouch.set(this.mLastTouch);
            this.mAllowDraggingOffscreen = true;
            this.mIsUserInteracting = true;
            this.mDownTouchTime = ev.getEventTime();
            if (this.mPreviouslyDragging || this.mDownTouchTime - this.mLastDownTouchTime >= 200) {
                z2 = false;
            }
            this.mIsDoubleTap = z2;
            this.mIsWaitingForDoubleTap = false;
            this.mIsDragging = false;
            this.mLastDownTouchTime = this.mDownTouchTime;
            Runnable runnable = this.mDoubleTapTimeoutCallback;
            if (runnable != null) {
                this.mHandler.removeCallbacks(runnable);
                return;
            }
            return;
        }
        if (actionMasked != 1) {
            if (actionMasked == 2) {
                if (this.mIsUserInteracting) {
                    addMovement(ev);
                    int pointerIndex = ev.findPointerIndex(this.mActivePointerId);
                    if (pointerIndex == -1) {
                        Log.e(TAG, "Invalid active pointer id on MOVE: " + this.mActivePointerId);
                        return;
                    }
                    float x = ev.getRawX(pointerIndex);
                    float y = ev.getRawY(pointerIndex);
                    this.mLastDelta.set(x - this.mLastTouch.x, y - this.mLastTouch.y);
                    this.mDownDelta.set(x - this.mDownTouch.x, y - this.mDownTouch.y);
                    boolean hasMovedBeyondTap = this.mDownDelta.length() > ((float) this.mViewConfig.getScaledTouchSlop());
                    if (!this.mIsDragging) {
                        if (hasMovedBeyondTap) {
                            this.mIsDragging = true;
                            this.mStartedDragging = true;
                        }
                    } else {
                        this.mStartedDragging = false;
                    }
                    this.mLastTouch.set(x, y);
                    return;
                }
                return;
            } else if (actionMasked != 3) {
                if (actionMasked == 6 && this.mIsUserInteracting) {
                    addMovement(ev);
                    int pointerIndex2 = ev.getActionIndex();
                    int pointerId = ev.getPointerId(pointerIndex2);
                    if (pointerId == this.mActivePointerId) {
                        int newPointerIndex = pointerIndex2 == 0 ? 1 : 0;
                        this.mActivePointerId = ev.getPointerId(newPointerIndex);
                        this.mLastTouch.set(ev.getRawX(newPointerIndex), ev.getRawY(newPointerIndex));
                        return;
                    }
                    return;
                }
                return;
            }
        } else if (this.mIsUserInteracting) {
            addMovement(ev);
            this.mVelocityTracker.computeCurrentVelocity(1000, this.mViewConfig.getScaledMaximumFlingVelocity());
            this.mVelocity.set(this.mVelocityTracker.getXVelocity(), this.mVelocityTracker.getYVelocity());
            int pointerIndex3 = ev.findPointerIndex(this.mActivePointerId);
            if (pointerIndex3 == -1) {
                Log.e(TAG, "Invalid active pointer id on UP: " + this.mActivePointerId);
                return;
            }
            this.mUpTouchTime = ev.getEventTime();
            this.mLastTouch.set(ev.getRawX(pointerIndex3), ev.getRawY(pointerIndex3));
            boolean z3 = this.mIsDragging;
            this.mPreviouslyDragging = z3;
            if (!this.mIsDoubleTap && !z3 && this.mUpTouchTime - this.mDownTouchTime < 200) {
                z = true;
            }
            this.mIsWaitingForDoubleTap = z;
        } else {
            return;
        }
        recycleVelocityTracker();
    }

    public PointF getVelocity() {
        return this.mVelocity;
    }

    public PointF getLastTouchPosition() {
        return this.mLastTouch;
    }

    public PointF getLastTouchDelta() {
        return this.mLastDelta;
    }

    public PointF getDownTouchPosition() {
        return this.mDownTouch;
    }

    public PointF getDownTouchDelta() {
        return this.mDownDelta;
    }

    public boolean isDragging() {
        return this.mIsDragging;
    }

    public boolean isUserInteracting() {
        return this.mIsUserInteracting;
    }

    public boolean startedDragging() {
        return this.mStartedDragging;
    }

    public void setAllowTouches(boolean allowTouches) {
        this.mAllowTouches = allowTouches;
        if (this.mIsUserInteracting) {
            reset();
        }
    }

    public void setDisallowDraggingOffscreen() {
        this.mAllowDraggingOffscreen = false;
    }

    public boolean allowDraggingOffscreen() {
        return this.mAllowDraggingOffscreen;
    }

    public boolean isDoubleTap() {
        return this.mIsDoubleTap;
    }

    public boolean isWaitingForDoubleTap() {
        return this.mIsWaitingForDoubleTap;
    }

    public void scheduleDoubleTapTimeoutCallback() {
        if (this.mIsWaitingForDoubleTap) {
            long delay = getDoubleTapTimeoutCallbackDelay();
            this.mHandler.removeCallbacks(this.mDoubleTapTimeoutCallback);
            this.mHandler.postDelayed(this.mDoubleTapTimeoutCallback, delay);
        }
    }

    @VisibleForTesting
    long getDoubleTapTimeoutCallbackDelay() {
        if (this.mIsWaitingForDoubleTap) {
            return Math.max(0L, 200 - (this.mUpTouchTime - this.mDownTouchTime));
        }
        return -1L;
    }

    private void initOrResetVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        } else {
            velocityTracker.clear();
        }
    }

    private void recycleVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    private void addMovement(MotionEvent event) {
        float deltaX = event.getRawX() - event.getX();
        float deltaY = event.getRawY() - event.getY();
        event.offsetLocation(deltaX, deltaY);
        this.mVelocityTracker.addMovement(event);
        event.offsetLocation(-deltaX, -deltaY);
    }

    public void dump(PrintWriter pw, String prefix) {
        String innerPrefix = prefix + "  ";
        pw.println(prefix + TAG);
        pw.println(innerPrefix + "mAllowTouches=" + this.mAllowTouches);
        pw.println(innerPrefix + "mActivePointerId=" + this.mActivePointerId);
        pw.println(innerPrefix + "mDownTouch=" + this.mDownTouch);
        pw.println(innerPrefix + "mDownDelta=" + this.mDownDelta);
        pw.println(innerPrefix + "mLastTouch=" + this.mLastTouch);
        pw.println(innerPrefix + "mLastDelta=" + this.mLastDelta);
        pw.println(innerPrefix + "mVelocity=" + this.mVelocity);
        pw.println(innerPrefix + "mIsUserInteracting=" + this.mIsUserInteracting);
        pw.println(innerPrefix + "mIsDragging=" + this.mIsDragging);
        pw.println(innerPrefix + "mStartedDragging=" + this.mStartedDragging);
        pw.println(innerPrefix + "mAllowDraggingOffscreen=" + this.mAllowDraggingOffscreen);
    }
}
