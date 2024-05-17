package com.xiaopeng.systemui.infoflow.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.theme.AnimatedTextView;
/* loaded from: classes24.dex */
public class MarqueeTextView extends AnimatedTextView {
    private static final int FIRST_SCROLL_DELAY_DEFAULT = 1000;
    private static final int ROLLING_INTERVAL_DEFAULT = 10000;
    public static final int SCROLL_FOREVER = 100;
    public static final int SCROLL_ONCE = 101;
    private static final String TAG = "MarqueeTextView";
    private boolean mFirst;
    private int mFirstScrollDelay;
    private boolean mPaused;
    private int mRollingInterval;
    private int mScrollMode;
    private Scroller mScroller;
    private int mXPaused;

    public MarqueeTextView(Context context) {
        this(context, null);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mXPaused = 0;
        this.mPaused = true;
        this.mFirst = true;
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyle) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MarqueeTextView);
        this.mRollingInterval = typedArray.getInt(1, 10000);
        this.mScrollMode = typedArray.getInt(2, 100);
        this.mFirstScrollDelay = typedArray.getInt(0, 1000);
        typedArray.recycle();
        setSingleLine();
        setEllipsize(null);
    }

    public void startScroll() {
        this.mXPaused = 0;
        this.mPaused = true;
        this.mFirst = true;
        resumeScroll();
    }

    public void resumeScroll() {
        if (!this.mPaused) {
            return;
        }
        setHorizontallyScrolling(true);
        if (this.mScroller == null) {
            this.mScroller = new Scroller(getContext(), new LinearInterpolator());
            setScroller(this.mScroller);
        }
        int scrollingLen = calculateScrollingLen();
        final int distance = scrollingLen - this.mXPaused;
        final int duration = Double.valueOf(((this.mRollingInterval * distance) * 1.0d) / scrollingLen).intValue();
        if (this.mFirst) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.widget.MarqueeTextView.1
                @Override // java.lang.Runnable
                public void run() {
                    MarqueeTextView.this.mScroller.startScroll(MarqueeTextView.this.mXPaused, 0, distance, 0, duration);
                    MarqueeTextView.this.invalidate();
                    MarqueeTextView.this.mPaused = false;
                }
            }, this.mFirstScrollDelay);
            return;
        }
        this.mScroller.startScroll(this.mXPaused, 0, distance, 0, duration);
        invalidate();
        this.mPaused = false;
    }

    public void pauseScroll() {
        Scroller scroller = this.mScroller;
        if (scroller == null || this.mPaused) {
            return;
        }
        this.mPaused = true;
        this.mXPaused = scroller.getCurrX();
        this.mScroller.abortAnimation();
    }

    public void stopScroll() {
        Scroller scroller = this.mScroller;
        if (scroller == null) {
            return;
        }
        this.mPaused = true;
        scroller.startScroll(0, 0, 0, 0, 0);
    }

    private int calculateScrollingLen() {
        TextPaint tp = getPaint();
        Rect rect = new Rect();
        String strTxt = getText().toString();
        tp.getTextBounds(strTxt, 0, strTxt.length(), rect);
        return rect.width();
    }

    @Override // android.widget.TextView, android.view.View
    public void computeScroll() {
        super.computeScroll();
        Scroller scroller = this.mScroller;
        if (scroller != null && scroller.isFinished() && !this.mPaused) {
            if (this.mScrollMode == 101) {
                stopScroll();
                return;
            }
            this.mPaused = true;
            this.mXPaused = getWidth() * (-1);
            this.mFirst = false;
            resumeScroll();
        }
    }

    public int getRndDuration() {
        return this.mRollingInterval;
    }

    public void setRndDuration(int duration) {
        this.mRollingInterval = duration;
    }

    public void setScrollMode(int mode) {
        this.mScrollMode = mode;
    }

    public int getScrollMode() {
        return this.mScrollMode;
    }

    public void setScrollFirstDelay(int delay) {
        this.mFirstScrollDelay = delay;
    }

    public int getScrollFirstDelay() {
        return this.mFirstScrollDelay;
    }

    public boolean isPaused() {
        return this.mPaused;
    }
}
