package com.android.systemui.statusbar.phone;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class DoubleTapHelper {
    private static final long DOUBLETAP_TIMEOUT_MS = 1200;
    private boolean mActivated;
    private final ActivationListener mActivationListener;
    private float mActivationX;
    private float mActivationY;
    private final DoubleTapListener mDoubleTapListener;
    private final DoubleTapLogListener mDoubleTapLogListener;
    private float mDoubleTapSlop;
    private float mDownX;
    private float mDownY;
    private final SlideBackListener mSlideBackListener;
    private Runnable mTapTimeoutRunnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$DoubleTapHelper$GFsC9BR8swazZioXO_-_Yt7_6kU
        @Override // java.lang.Runnable
        public final void run() {
            DoubleTapHelper.this.makeInactive();
        }
    };
    private float mTouchSlop;
    private boolean mTrackTouch;
    private final View mView;

    @FunctionalInterface
    /* loaded from: classes21.dex */
    public interface ActivationListener {
        void onActiveChanged(boolean z);
    }

    @FunctionalInterface
    /* loaded from: classes21.dex */
    public interface DoubleTapListener {
        boolean onDoubleTap();
    }

    @FunctionalInterface
    /* loaded from: classes21.dex */
    public interface DoubleTapLogListener {
        void onDoubleTapLog(boolean z, float f, float f2);
    }

    @FunctionalInterface
    /* loaded from: classes21.dex */
    public interface SlideBackListener {
        boolean onSlideBack();
    }

    public DoubleTapHelper(View view, ActivationListener activationListener, DoubleTapListener doubleTapListener, SlideBackListener slideBackListener, DoubleTapLogListener doubleTapLogListener) {
        this.mTouchSlop = ViewConfiguration.get(view.getContext()).getScaledTouchSlop();
        this.mDoubleTapSlop = view.getResources().getDimension(R.dimen.double_tap_slop);
        this.mView = view;
        this.mActivationListener = activationListener;
        this.mDoubleTapListener = doubleTapListener;
        this.mSlideBackListener = slideBackListener;
        this.mDoubleTapLogListener = doubleTapLogListener;
    }

    public boolean onTouchEvent(MotionEvent event) {
        return onTouchEvent(event, Integer.MAX_VALUE);
    }

    public boolean onTouchEvent(MotionEvent event, int maxTouchableHeight) {
        int action = event.getActionMasked();
        if (action != 0) {
            if (action != 1) {
                if (action != 2) {
                    if (action == 3) {
                        makeInactive();
                        this.mTrackTouch = false;
                    }
                } else if (!isWithinTouchSlop(event)) {
                    makeInactive();
                    this.mTrackTouch = false;
                }
            } else if (isWithinTouchSlop(event)) {
                SlideBackListener slideBackListener = this.mSlideBackListener;
                if (slideBackListener != null && slideBackListener.onSlideBack()) {
                    return true;
                }
                if (!this.mActivated) {
                    makeActive();
                    this.mView.postDelayed(this.mTapTimeoutRunnable, DOUBLETAP_TIMEOUT_MS);
                    this.mActivationX = event.getX();
                    this.mActivationY = event.getY();
                } else {
                    boolean withinDoubleTapSlop = isWithinDoubleTapSlop(event);
                    DoubleTapLogListener doubleTapLogListener = this.mDoubleTapLogListener;
                    if (doubleTapLogListener != null) {
                        doubleTapLogListener.onDoubleTapLog(withinDoubleTapSlop, event.getX() - this.mActivationX, event.getY() - this.mActivationY);
                    }
                    if (withinDoubleTapSlop) {
                        if (!this.mDoubleTapListener.onDoubleTap()) {
                            return false;
                        }
                    } else {
                        makeInactive();
                        this.mTrackTouch = false;
                    }
                }
            } else {
                makeInactive();
                this.mTrackTouch = false;
            }
        } else {
            this.mDownX = event.getX();
            this.mDownY = event.getY();
            this.mTrackTouch = true;
            if (this.mDownY > maxTouchableHeight) {
                this.mTrackTouch = false;
            }
        }
        return this.mTrackTouch;
    }

    private void makeActive() {
        if (!this.mActivated) {
            this.mActivated = true;
            this.mActivationListener.onActiveChanged(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void makeInactive() {
        if (this.mActivated) {
            this.mActivated = false;
            this.mActivationListener.onActiveChanged(false);
        }
    }

    private boolean isWithinTouchSlop(MotionEvent event) {
        return Math.abs(event.getX() - this.mDownX) < this.mTouchSlop && Math.abs(event.getY() - this.mDownY) < this.mTouchSlop;
    }

    public boolean isWithinDoubleTapSlop(MotionEvent event) {
        if (this.mActivated) {
            return Math.abs(event.getX() - this.mActivationX) < this.mDoubleTapSlop && Math.abs(event.getY() - this.mActivationY) < this.mDoubleTapSlop;
        }
        return true;
    }
}
