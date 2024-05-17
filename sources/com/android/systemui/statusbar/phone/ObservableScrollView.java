package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
/* loaded from: classes21.dex */
public class ObservableScrollView extends ScrollView {
    private boolean mBlockFlinging;
    private boolean mHandlingTouchEvent;
    private int mLastOverscrollAmount;
    private float mLastX;
    private float mLastY;
    private Listener mListener;
    private boolean mTouchCancelled;
    private boolean mTouchEnabled;

    /* loaded from: classes21.dex */
    public interface Listener {
        void onOverscrolled(float f, float f2, int i);

        void onScrollChanged();
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTouchEnabled = true;
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public void setTouchEnabled(boolean touchEnabled) {
        this.mTouchEnabled = touchEnabled;
    }

    public boolean isScrolledToBottom() {
        return getScrollY() == getMaxScrollY();
    }

    public boolean isHandlingTouchEvent() {
        return this.mHandlingTouchEvent;
    }

    private int getMaxScrollY() {
        if (getChildCount() <= 0) {
            return 0;
        }
        View child = getChildAt(0);
        int scrollRange = Math.max(0, child.getHeight() - ((getHeight() - this.mPaddingBottom) - this.mPaddingTop));
        return scrollRange;
    }

    @Override // android.widget.ScrollView, android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        this.mHandlingTouchEvent = true;
        this.mLastX = ev.getX();
        this.mLastY = ev.getY();
        boolean result = super.onTouchEvent(ev);
        this.mHandlingTouchEvent = false;
        return result;
    }

    @Override // android.widget.ScrollView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        this.mHandlingTouchEvent = true;
        this.mLastX = ev.getX();
        this.mLastY = ev.getY();
        boolean result = super.onInterceptTouchEvent(ev);
        this.mHandlingTouchEvent = false;
        return result;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0) {
            if (!this.mTouchEnabled) {
                this.mTouchCancelled = true;
                return false;
            }
            this.mTouchCancelled = false;
        } else if (this.mTouchCancelled) {
            return false;
        } else {
            if (!this.mTouchEnabled) {
                MotionEvent cancel = MotionEvent.obtain(ev);
                cancel.setAction(3);
                super.dispatchTouchEvent(cancel);
                cancel.recycle();
                this.mTouchCancelled = true;
                return false;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override // android.view.View
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        Listener listener = this.mListener;
        if (listener != null) {
            listener.onScrollChanged();
        }
    }

    @Override // android.view.View
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        this.mLastOverscrollAmount = Math.max(0, (scrollY + deltaY) - getMaxScrollY());
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    public void setBlockFlinging(boolean blockFlinging) {
        this.mBlockFlinging = blockFlinging;
    }

    @Override // android.widget.ScrollView
    public void fling(int velocityY) {
        if (!this.mBlockFlinging) {
            super.fling(velocityY);
        }
    }

    @Override // android.widget.ScrollView, android.view.View
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        int i;
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        Listener listener = this.mListener;
        if (listener != null && (i = this.mLastOverscrollAmount) > 0) {
            listener.onOverscrolled(this.mLastX, this.mLastY, i);
        }
    }
}
