package com.xiaopeng.systemui.quickmenu;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;
/* loaded from: classes24.dex */
public class ScrollerViewGroup extends RelativeLayout {
    private static final String TAG = "ScrollerViewGroup";
    private boolean isOpen;
    private boolean isScrollUp;
    private GestureDetector mGestureDetector;
    private boolean mIsPrintLog;
    private OnDragListener mOnDragListener;
    private float mPrevY;
    private Scroller mScroller;
    public State mState;

    /* loaded from: classes24.dex */
    public interface OnDragListener {
        void onScrollerClose();

        void onScrollerOpen();

        void onScrollerPercent(float f);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes24.dex */
    public enum State {
        Null,
        SingleTapUp,
        Scroll,
        Fling
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.i(TAG, "onFinishInflate");
    }

    public ScrollerViewGroup(Context context) {
        this(context, null);
    }

    public ScrollerViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mState = State.Null;
        this.isScrollUp = false;
        this.isOpen = false;
        this.mIsPrintLog = false;
        init(context);
    }

    private void init(Context context) {
        Log.i(TAG, "init");
        this.mScroller = new Scroller(context, new LinearInterpolator());
        this.mGestureDetector = new GestureDetector(context, new CustomGestureListener());
    }

    public void onResume() {
        Log.i(TAG, "onResume");
        this.mIsPrintLog = true;
        this.mState = State.Null;
        if (!this.isOpen) {
            float scrollerFinalY = this.mScroller.getFinalY();
            Log.i(TAG, "onResume scrollerFinalY = " + scrollerFinalY);
            if (scrollerFinalY == 0.0f) {
                Scroller scroller = this.mScroller;
                scroller.startScroll(scroller.getFinalX(), 1200, 0, -1200);
                invalidate();
            } else {
                openMenu("onResume");
            }
            postDelayed(new Runnable() { // from class: com.xiaopeng.systemui.quickmenu.-$$Lambda$ScrollerViewGroup$WTZubRUBb72vTDld7ugcgw7tXPE
                @Override // java.lang.Runnable
                public final void run() {
                    ScrollerViewGroup.this.lambda$onResume$0$ScrollerViewGroup();
                }
            }, this.mScroller.getDuration());
        }
        this.isOpen = true;
    }

    public /* synthetic */ void lambda$onResume$0$ScrollerViewGroup() {
        Log.i(TAG, "onResume postDelayed " + getVisibility());
        scrollTo(0, 0);
        OnDragListener onDragListener = this.mOnDragListener;
        if (onDragListener != null) {
            onDragListener.onScrollerPercent(0.0f);
        }
        postInvalidate();
    }

    public boolean isScrollFinished() {
        return this.mScroller.isFinished();
    }

    public void onStop() {
        Log.i(TAG, "onStop");
        this.isScrollUp = false;
        this.isOpen = false;
        this.mPrevY = 0.0f;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        this.mGestureDetector.onTouchEvent(event);
        if (event.getAction() == 1) {
            onUp();
        }
        return true;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.mIsPrintLog) {
            Log.i(TAG, "dispatchDraw");
            this.mIsPrintLog = false;
        }
    }

    public void onUp() {
        if (this.mState == State.SingleTapUp || this.mState == State.Fling) {
            return;
        }
        int scrollY = getScrollY();
        Log.i(TAG, "ACTION_UP scrollY = " + scrollY);
        if (scrollY == 0) {
            return;
        }
        if (scrollY < getMeasuredHeight() / 2) {
            openMenu("onUp");
        } else {
            closeMenu("onUp");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes24.dex */
    public class CustomGestureListener implements GestureDetector.OnGestureListener {
        CustomGestureListener() {
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public boolean onDown(MotionEvent e) {
            Log.i(ScrollerViewGroup.TAG, "onDown");
            return false;
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public void onShowPress(MotionEvent e) {
            Log.i(ScrollerViewGroup.TAG, "onShowPress");
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public boolean onSingleTapUp(MotionEvent e) {
            Log.i(ScrollerViewGroup.TAG, "onSingleTapUp");
            ScrollerViewGroup.this.mState = State.SingleTapUp;
            ScrollerViewGroup.this.closeMenu("onSingleTapUp");
            return false;
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            ScrollerViewGroup.this.mState = State.Scroll;
            Log.i(ScrollerViewGroup.TAG, "onScroll distanceY = " + distanceY);
            if (ScrollerViewGroup.this.mScroller.getFinalY() + distanceY < 0.0f) {
                ScrollerViewGroup.this.mPrevY = 0.0f;
                distanceY = -ScrollerViewGroup.this.mScroller.getFinalY();
            }
            ScrollerViewGroup.this.smoothSlideViewBy(0, (int) distanceY, 250);
            float firstY = e1.getY();
            float currentY = e2.getY();
            Log.i(ScrollerViewGroup.TAG, "onScroll firstTouch = " + firstY + " & mPrevY = " + ScrollerViewGroup.this.mPrevY + " & currentY = " + currentY);
            if (ScrollerViewGroup.this.mPrevY != 0.0f) {
                if (currentY < ScrollerViewGroup.this.mPrevY) {
                    ScrollerViewGroup.this.isScrollUp = true;
                } else {
                    ScrollerViewGroup.this.isScrollUp = false;
                }
            } else if (currentY < firstY) {
                ScrollerViewGroup.this.isScrollUp = true;
            } else {
                ScrollerViewGroup.this.isScrollUp = false;
            }
            Log.i(ScrollerViewGroup.TAG, "onScroll isScrollUp = " + ScrollerViewGroup.this.isScrollUp);
            ScrollerViewGroup.this.mPrevY = currentY;
            return false;
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public void onLongPress(MotionEvent e) {
            Log.i(ScrollerViewGroup.TAG, "onLongPress");
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float down = e1.getY();
            float up = e2.getY();
            Log.i(ScrollerViewGroup.TAG, "onFling isScrollUp = " + ScrollerViewGroup.this.isScrollUp + " & down = " + down + " & up = " + up);
            if (down > up && ScrollerViewGroup.this.isScrollUp) {
                ScrollerViewGroup.this.mState = State.Fling;
                Log.i(ScrollerViewGroup.TAG, "onFling close");
                ScrollerViewGroup.this.closeMenu("onFling");
                return true;
            }
            return true;
        }
    }

    public void smoothSlideViewTo(int fx, int fy, int duration) {
        Log.i(TAG, "smoothSlideViewTo");
        int dx = fx - this.mScroller.getFinalX();
        int dy = fy - this.mScroller.getFinalY();
        smoothSlideViewBy(dx, dy, duration);
    }

    public void smoothSlideViewBy(int dx, int dy, int duration) {
        Log.i(TAG, "smoothSlideViewBy startScroll");
        Scroller scroller = this.mScroller;
        scroller.startScroll(scroller.getFinalX(), this.mScroller.getFinalY(), dx, dy, duration);
        invalidate();
    }

    @Override // android.view.View
    public void computeScroll() {
        if (this.mScroller.computeScrollOffset()) {
            Log.i(TAG, "computeScroll mScroller.getCurrY() = " + this.mScroller.getCurrY());
            scrollTo(this.mScroller.getCurrX(), this.mScroller.getCurrY());
            float percent = ((float) this.mScroller.getCurrY()) / ((float) (getMeasuredHeight() / 3));
            OnDragListener onDragListener = this.mOnDragListener;
            if (onDragListener != null) {
                onDragListener.onScrollerPercent(percent);
            }
            postInvalidate();
        }
    }

    private void openMenu(String from) {
        Log.i(TAG, "openMenu " + from);
        smoothSlideViewTo(0, 0, 250);
        this.mPrevY = 0.0f;
        open();
    }

    public void closeMenu(String from) {
        if (!this.isOpen) {
            return;
        }
        this.isOpen = false;
        Log.i(TAG, "closeMenu " + from);
        int heigth = getMeasuredHeight();
        smoothSlideViewTo(0, heigth, 500);
        new Handler().postDelayed(new Runnable() { // from class: com.xiaopeng.systemui.quickmenu.ScrollerViewGroup.1
            @Override // java.lang.Runnable
            public void run() {
                ScrollerViewGroup.this.close();
            }
        }, 300L);
    }

    private void open() {
        OnDragListener onDragListener = this.mOnDragListener;
        if (onDragListener != null) {
            onDragListener.onScrollerOpen();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void close() {
        if (this.mOnDragListener != null) {
            this.mScroller.abortAnimation();
            this.mOnDragListener.onScrollerClose();
        }
    }

    public void setOnDragListener(OnDragListener listener) {
        this.mOnDragListener = listener;
    }
}
