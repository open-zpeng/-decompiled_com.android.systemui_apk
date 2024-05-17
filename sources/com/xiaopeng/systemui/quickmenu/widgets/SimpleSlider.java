package com.xiaopeng.systemui.quickmenu.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.android.systemui.R;
/* loaded from: classes24.dex */
public class SimpleSlider extends View {
    private static final int MAX_LEVEL = 10000;
    private static final int MAX_VALUE = 10;
    private static final int MIN_VALUE = 0;
    private static final int NO_ALPHA = 255;
    public static final int TOUCH_MODE_SEEK = 1;
    public static final int TOUCH_MODE_SLIDE = 0;
    private float mDisabledAlpha;
    private boolean mEnabled;
    private int mMax;
    private boolean mMaxInitialized;
    private int mMin;
    private boolean mMinInitialized;
    private OnSlideChangeListener mOnSlideChangeListener;
    private int mProgress;
    private Drawable mProgressDrawable;
    private int mProgressDrawableRes;
    private int mScaledTouchSlop;
    private float mSlideScale;
    private Drawable mTickMark;
    private boolean mTickMarkAll;
    protected float mTickMarkPadding;
    private boolean mTickMarkProgressEnd;
    private int mTickMarkRes;
    private TouchEventHandler mTouchEventHandler;
    protected int mTouchMode;

    /* loaded from: classes24.dex */
    public interface OnSlideChangeListener {
        void onProgressChanged(SimpleSlider simpleSlider, int i, boolean z);

        void onStartTrackingTouch(SimpleSlider simpleSlider);

        void onStopTrackingTouch(SimpleSlider simpleSlider);
    }

    /* loaded from: classes24.dex */
    public interface TouchEventHandler {
        boolean onTouchEvent(SimpleSlider simpleSlider, MotionEvent motionEvent);
    }

    public SimpleSlider(Context context) {
        this(context, null);
    }

    public SimpleSlider(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.SimpleSlider);
    }

    public SimpleSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.SimpleSlider);
    }

    public SimpleSlider(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mProgress = 0;
        this.mMin = 0;
        this.mMax = 10;
        this.mMaxInitialized = false;
        this.mMinInitialized = false;
        this.mSlideScale = 1.0f;
        this.mProgressDrawableRes = -1;
        this.mTickMarkRes = -1;
        this.mTickMarkAll = false;
        this.mTickMarkPadding = 0.0f;
        this.mTickMarkProgressEnd = false;
        this.mTouchMode = 0;
        this.mEnabled = true;
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        setEnabled(this.mEnabled);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SimpleSlider, defStyleAttr, defStyleRes);
        this.mProgressDrawableRes = a.getResourceId(4, this.mProgressDrawableRes);
        setProgressDrawable(a.getDrawable(4));
        this.mTickMarkRes = a.getResourceId(5, this.mTickMarkRes);
        Drawable tickMark = a.getDrawable(5);
        setTickMark(tickMark);
        this.mTickMarkAll = a.getBoolean(8, this.mTickMarkAll);
        this.mTickMarkPadding = a.getDimension(10, this.mTickMarkPadding);
        this.mTickMarkProgressEnd = a.getBoolean(9, this.mTickMarkProgressEnd);
        this.mSlideScale = a.getFloat(7, this.mSlideScale);
        this.mEnabled = a.getBoolean(0, this.mEnabled);
        this.mDisabledAlpha = a.getFloat(1, 0.36f);
        setMin(a.getInt(6, this.mMin));
        setMax(a.getInt(2, this.mMax));
        setProgress(a.getInt(3, this.mProgress));
        this.mTouchMode = a.getInt(11, this.mTouchMode);
        this.mTouchEventHandler = generateTouchEventHandler(this.mTouchMode);
        a.recycle();
        ViewConfiguration config = ViewConfiguration.get(context);
        this.mScaledTouchSlop = config.getScaledTouchSlop();
    }

    protected TouchEventHandler generateTouchEventHandler(int touchMode) {
        if (touchMode == 1) {
            return new SeekHandler();
        }
        return new SlideHandler();
    }

    @Override // android.view.View
    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
        Drawable drawable = this.mProgressDrawable;
        if (drawable != null) {
            drawable.setHotspot(x, y);
        }
    }

    int getScaledTouchSlop() {
        return this.mScaledTouchSlop;
    }

    public void setTouchEventHandler(TouchEventHandler touchEventHandler) {
        this.mTouchEventHandler = touchEventHandler;
    }

    public void refreshVisual() {
        if (this.mProgressDrawableRes != -1) {
            setProgressDrawable(getContext().getDrawable(this.mProgressDrawableRes));
        }
        if (this.mTickMarkRes != -1) {
            setTickMark(getContext().getDrawable(this.mTickMarkRes));
        }
        int range = getRange();
        float scale = range > 0 ? (getProgress() - getMin()) / range : 0.0f;
        setVisualProgress(scale);
    }

    public int getMin() {
        return this.mMin;
    }

    public void setMin(int minProgress) {
        if (this.mMaxInitialized && minProgress > this.mMax) {
            minProgress = this.mMax;
        }
        this.mMinInitialized = true;
        if (this.mMaxInitialized && minProgress != this.mMin) {
            this.mMin = minProgress;
            postInvalidate();
            if (this.mProgress < minProgress) {
                this.mProgress = minProgress;
            }
            setProgress(this.mProgress);
            return;
        }
        this.mMin = minProgress;
    }

    public int getMax() {
        return this.mMax;
    }

    public void setMax(int maxProgress) {
        if (this.mMinInitialized && maxProgress < this.mMin) {
            maxProgress = this.mMin;
        }
        this.mMaxInitialized = true;
        if (this.mMinInitialized && maxProgress != this.mMax) {
            this.mMax = maxProgress;
            postInvalidate();
            if (this.mProgress > maxProgress) {
                this.mProgress = maxProgress;
            }
            setProgress(this.mProgress);
            return;
        }
        this.mMax = maxProgress;
    }

    public int getRange() {
        return this.mMax - this.mMin;
    }

    public int getProgress() {
        return this.mProgress;
    }

    public void setOnSlideChangeListener(OnSlideChangeListener onSlideChangeListener) {
        this.mOnSlideChangeListener = onSlideChangeListener;
    }

    public void setProgress(int progress) {
        setProgressInternal(progress, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setProgressInternal(int progress, boolean fromUser) {
        int progress2 = limitProgress(progress);
        this.mProgress = progress2;
        int i = this.mMax;
        int i2 = this.mMin;
        int range = i - i2;
        float scale = range > 0 ? (progress2 - i2) / range : 0.0f;
        setVisualProgress(scale);
        OnSlideChangeListener onSlideChangeListener = this.mOnSlideChangeListener;
        if (onSlideChangeListener != null) {
            onSlideChangeListener.onProgressChanged(this, progress2, fromUser);
        }
    }

    public float getSlideScale() {
        return this.mSlideScale;
    }

    public void setSlideScale(float slideScale) {
        this.mSlideScale = slideScale;
    }

    public void setProgressDrawable(Drawable d) {
        Drawable drawable = this.mProgressDrawable;
        if (drawable != d) {
            if (drawable != null) {
                drawable.setCallback(null);
                unscheduleDrawable(this.mProgressDrawable);
            }
            this.mProgressDrawable = d;
            if (d != null) {
                d.setCallback(this);
                d.setLayoutDirection(getLayoutDirection());
                if (d.isStateful()) {
                    d.setState(getDrawableState());
                }
                updateDrawableBounds();
                updateDrawableState();
            }
        }
    }

    public void updateDrawableBounds() {
        updateDrawableBounds(getWidth(), getHeight());
    }

    public void setTickMark(Drawable tickMark) {
        Drawable drawable = this.mTickMark;
        if (drawable != null) {
            drawable.setCallback(null);
        }
        this.mTickMark = tickMark;
        if (tickMark != null) {
            tickMark.setCallback(this);
            tickMark.setLayoutDirection(getLayoutDirection());
            if (tickMark.isStateful()) {
                tickMark.setState(getDrawableState());
            }
        }
        invalidate();
    }

    public Drawable getTickMark() {
        return this.mTickMark;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateDrawableBounds(int w, int h) {
        int w2 = w - (getPaddingRight() + getPaddingLeft());
        int h2 = h - (getPaddingTop() + getPaddingBottom());
        Drawable drawable = this.mProgressDrawable;
        if (drawable != null) {
            drawable.setBounds(0, 0, w2, h2);
        }
    }

    private void updateDrawableState() {
        int[] state = getDrawableState();
        boolean changed = false;
        Drawable sliderDrawable = this.mProgressDrawable;
        if (sliderDrawable != null && sliderDrawable.isStateful()) {
            changed = sliderDrawable.setState(state);
        }
        if (changed) {
            invalidate();
        }
    }

    protected void setVisualProgress(float progress) {
        Drawable d = this.mProgressDrawable;
        if ((d instanceof LayerDrawable) && (d = ((LayerDrawable) d).findDrawableByLayerId(16908301)) == null) {
            d = this.mProgressDrawable;
        }
        if (d != null) {
            int level = (int) (10000.0f * progress);
            d.setLevel(level);
            return;
        }
        invalidate();
    }

    @Override // android.view.View
    protected void drawableStateChanged() {
        Drawable drawable = this.mProgressDrawable;
        if (drawable != null) {
            drawable.setAlpha(isEnabled() ? 255 : (int) (this.mDisabledAlpha * 255.0f));
        }
        Drawable drawable2 = this.mTickMark;
        if (drawable2 != null) {
            drawable2.setAlpha(isEnabled() ? 255 : (int) (this.mDisabledAlpha * 255.0f));
        }
        super.drawableStateChanged();
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable who) {
        return who == this.mProgressDrawable || super.verifyDrawable(who);
    }

    @Override // android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isEnabled()) {
            int action = ev.getActionMasked();
            if (action == 0) {
                setPressed(true);
            } else if (action == 1 || action == 3) {
                setPressed(false);
            }
            return super.dispatchTouchEvent(ev);
        }
        return true;
    }

    @Override // android.view.View
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        TouchEventHandler touchEventHandler = this.mTouchEventHandler;
        if (touchEventHandler != null) {
            return touchEventHandler.onTouchEvent(this, event);
        }
        return super.onTouchEvent(event);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void attemptClaimDrag() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateDrawableBounds(w, h);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        Drawable drawable = this.mProgressDrawable;
        if (drawable != null) {
            drawable.draw(canvas);
        }
        drawTickMarks(canvas);
    }

    protected void drawTickMarks(Canvas canvas) {
        Drawable tickMark = getTickMark();
        if (tickMark != null) {
            int count = getMax() - getMin();
            if (count > 1) {
                int w = tickMark.getIntrinsicWidth();
                int h = tickMark.getIntrinsicHeight();
                int halfW = w >= 0 ? w / 2 : 1;
                int halfH = h >= 0 ? h / 2 : 1;
                tickMark.setBounds(-halfW, -halfH, halfW, halfH);
                float spacing = getSpacing(count);
                int saveCount = canvas.save();
                canvas.translate(getPaddingLeft(), getTickMarkYOffset());
                canvas.translate(this.mTickMarkPadding + spacing, 0.0f);
                for (int i = 1; i < count && (this.mTickMarkAll || ((!this.mTickMarkProgressEnd || i <= this.mProgress) && (this.mTickMarkProgressEnd || i < this.mProgress))); i++) {
                    tickMark.draw(canvas);
                    canvas.translate(spacing, 0.0f);
                }
                canvas.restoreToCount(saveCount);
            }
        }
    }

    protected float getSpacing(int count) {
        return (getProgressLength() - (this.mTickMarkPadding * 2.0f)) / count;
    }

    protected int getTickMarkYOffset() {
        return getHeight() >> 1;
    }

    protected float getProgressLength() {
        return (getWidth() - getPaddingLeft()) - getPaddingRight();
    }

    protected float getTouchDownPos(MotionEvent event) {
        return event.getX();
    }

    protected float getTouchProgress(float touchDownPos, MotionEvent event) {
        return Math.round(event.getX() - touchDownPos) / getProgressLength();
    }

    protected void onStartTrackingTouch() {
        OnSlideChangeListener onSlideChangeListener = this.mOnSlideChangeListener;
        if (onSlideChangeListener != null) {
            onSlideChangeListener.onStartTrackingTouch(this);
        }
    }

    protected void onStopTrackingTouch() {
        OnSlideChangeListener onSlideChangeListener = this.mOnSlideChangeListener;
        if (onSlideChangeListener != null) {
            onSlideChangeListener.onStopTrackingTouch(this);
        }
    }

    private int limitProgress(int progress) {
        int min = this.mMin;
        int max = this.mMax;
        return Math.min(max, Math.max(progress, min));
    }

    public boolean isInScrollingContainer() {
        for (ViewParent p = getParent(); p != null && (p instanceof ViewGroup); p = p.getParent()) {
            if (((ViewGroup) p).shouldDelayChildPressedState()) {
                return true;
            }
        }
        return false;
    }

    /* loaded from: classes24.dex */
    public static class SlideHandler implements TouchEventHandler {
        private boolean mIsDragging;
        private SimpleSlider mSimpleSlider;
        private float mTouchDownPos;
        private int mTouchDownProgress;

        @Override // com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider.TouchEventHandler
        public boolean onTouchEvent(SimpleSlider simpleSlider, MotionEvent event) {
            this.mSimpleSlider = simpleSlider;
            return handleSlide(event);
        }

        /* JADX WARN: Code restructure failed: missing block: B:8:0x000d, code lost:
            if (r0 != 3) goto L8;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private boolean handleSlide(android.view.MotionEvent r5) {
            /*
                r4 = this;
                int r0 = r5.getActionMasked()
                r1 = 1
                if (r0 == 0) goto L55
                if (r0 == r1) goto L48
                r2 = 2
                if (r0 == r2) goto L10
                r2 = 3
                if (r0 == r2) goto L48
                goto L5e
            L10:
                boolean r2 = r4.mIsDragging
                if (r2 == 0) goto L18
                r4.trackTouchEvent(r5)
                goto L5e
            L18:
                com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider r2 = r4.mSimpleSlider
                float r2 = r2.getTouchDownPos(r5)
                float r3 = r4.mTouchDownPos
                float r2 = r2 - r3
                float r2 = java.lang.Math.abs(r2)
                com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider r3 = r4.mSimpleSlider
                int r3 = r3.getScaledTouchSlop()
                float r3 = (float) r3
                int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r2 <= 0) goto L5e
                com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider r2 = r4.mSimpleSlider
                int r2 = r2.getProgress()
                r4.mTouchDownProgress = r2
                r4.mIsDragging = r1
                com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider r2 = r4.mSimpleSlider
                r2.onStartTrackingTouch()
                r4.trackTouchEvent(r5)
                com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider r2 = r4.mSimpleSlider
                com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider.access$000(r2)
                goto L5e
            L48:
                boolean r2 = r4.mIsDragging
                if (r2 == 0) goto L5e
                com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider r2 = r4.mSimpleSlider
                r2.onStopTrackingTouch()
                r2 = 0
                r4.mIsDragging = r2
                goto L5e
            L55:
                com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider r2 = r4.mSimpleSlider
                float r2 = r2.getTouchDownPos(r5)
                r4.mTouchDownPos = r2
            L5e:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider.SlideHandler.handleSlide(android.view.MotionEvent):boolean");
        }

        private void trackTouchEvent(MotionEvent event) {
            float scale = this.mSimpleSlider.getTouchProgress(this.mTouchDownPos, event) * this.mSimpleSlider.getSlideScale();
            int range = this.mSimpleSlider.getMax() - this.mSimpleSlider.getMin();
            float progress = this.mTouchDownProgress + (range * scale) + this.mSimpleSlider.getMin();
            this.mSimpleSlider.setProgressInternal(Math.round(progress), true);
        }
    }

    /* loaded from: classes24.dex */
    public static class SeekHandler implements TouchEventHandler {
        private boolean mIsDragging;
        private SimpleSlider mSimpleSlider;
        private float mTouchDownX;

        @Override // com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider.TouchEventHandler
        public boolean onTouchEvent(SimpleSlider simpleSlider, MotionEvent event) {
            this.mSimpleSlider = simpleSlider;
            return handleSeekTouch(event);
        }

        private boolean handleSeekTouch(MotionEvent event) {
            int action = event.getAction();
            if (action != 0) {
                if (action != 1) {
                    if (action == 2) {
                        if (this.mIsDragging) {
                            trackTouchEvent(event);
                        } else {
                            float x = event.getX();
                            if (Math.abs(x - this.mTouchDownX) > this.mSimpleSlider.getScaledTouchSlop()) {
                                onStartTrackingTouch();
                                trackTouchEvent(event);
                                this.mSimpleSlider.attemptClaimDrag();
                            }
                        }
                    } else if (action == 3 && this.mIsDragging) {
                        onStopTrackingTouch();
                    }
                } else if (this.mIsDragging) {
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                } else {
                    onStartTrackingTouch();
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                }
            } else if (this.mSimpleSlider.isInScrollingContainer()) {
                this.mTouchDownX = event.getX();
            } else {
                onStartTrackingTouch();
                trackTouchEvent(event);
                this.mSimpleSlider.attemptClaimDrag();
            }
            return true;
        }

        private void onStartTrackingTouch() {
            this.mIsDragging = true;
            this.mSimpleSlider.onStartTrackingTouch();
        }

        private void onStopTrackingTouch() {
            this.mIsDragging = false;
            this.mSimpleSlider.onStopTrackingTouch();
        }

        private void trackTouchEvent(MotionEvent event) {
            int x = Math.round(event.getX());
            int width = this.mSimpleSlider.getWidth();
            int availableWidth = (width - this.mSimpleSlider.getPaddingLeft()) - this.mSimpleSlider.getPaddingRight();
            float scale = x / availableWidth;
            int range = this.mSimpleSlider.getMax() - this.mSimpleSlider.getMin();
            float progress = 0.0f + (range * scale) + this.mSimpleSlider.getMin();
            this.mSimpleSlider.setProgressInternal(Math.round(progress), true);
        }
    }
}
