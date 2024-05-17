package com.xiaopeng.systemui.qs.widgets;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import androidx.core.math.MathUtils;
import com.android.systemui.R;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.xui.view.animation.XBesselCurve3Interpolator;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes24.dex */
public class QcSliderLayout extends RelativeLayout {
    private static final int DEFAULT_BG_COLOR = 1555808977;
    private static final int DEFAULT_BG_CORNER_RADIUS = 32;
    private static final int DEFAULT_SLIDER_COLOR = 16777215;
    private static final int DEFAULT_SLIDER_INNER_CORNER_RADIUS = 27;
    private static final int DEFAULT_SLIDER_OUTER_CORNER_RADIUS = 5;
    private static final int MAX_LEVEL = 10000;
    private static final int MAX_VALUE = 100;
    private static final int MIN_VALUE = 0;
    private static final int NO_ALPHA = 255;
    private static final long PRESS_DURATION = 200;
    private static float SCALE_RULES = 0.97f;
    public static final int SLIDE_FROM_BOTTOM = 3;
    public static final int SLIDE_FROM_LEFT = 0;
    public static final int SLIDE_FROM_RIGHT = 2;
    public static final int SLIDE_FROM_TOP = 1;
    private static final String TAG = "QcSliderLayout";
    private XBesselCurve3Interpolator mBaselCurveIn;
    private XBesselCurve3Interpolator mBaselCurveOut;
    private int mBgColor;
    private int mBgColorId;
    private RectF mBounds;
    private Rect mContentBounds;
    private int mCornerRadius;
    private float mDisabledAlpha;
    private boolean mDispatchTouchDirectly;
    private boolean mDragging;
    private boolean mEnabled;
    private int mInset;
    private float mLastMoveX;
    private float mLastMoveY;
    private boolean mLimitTouchOnly;
    private boolean mMaxInitialized;
    private int mMaxLevel;
    private int mMaxLimit;
    private boolean mMaxLimitInit;
    private float mMaxPercentLimit;
    private int mMaxProgress;
    private boolean mMinInitialized;
    private int mMinLimit;
    private boolean mMinLimitInit;
    private float mMinPercentLimit;
    private int mMinProgress;
    private OnSlideChangeListener mOnSlideChangeListener;
    private int mProgress;
    private QcSliderDrawable mQcSliderDrawable;
    ObjectAnimator mScaleXAnimator;
    ObjectAnimator mScaleYAnimator;
    private float mSlideScale;
    private int mSliderColor;
    private int mSliderColorId;
    private int mSliderFrom;
    private int mSliderInnerCornerRadius;
    private int mSliderInset;
    private int mSliderOuterCornerRadius;
    private ThemeViewModel mThemeViewModel;
    private int mTouchSlop;
    private float mTouchX;
    private float mTouchY;

    /* loaded from: classes24.dex */
    public interface OnSlideChangeListener {
        void onProgressChanged(QcSliderLayout qcSliderLayout, int i);

        void onStartTrackingTouch(QcSliderLayout qcSliderLayout);

        void onStopTrackingTouch(QcSliderLayout qcSliderLayout);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes24.dex */
    public @interface SliderFrom {
    }

    public QcSliderLayout(Context context) {
        this(context, null);
    }

    public QcSliderLayout(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.QcSliderLayout);
    }

    public QcSliderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.QcSliderLayout);
    }

    public QcSliderLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        this.mMaxLevel = 10000;
        this.mProgress = 0;
        this.mMinProgress = 0;
        this.mMaxProgress = 100;
        this.mMaxInitialized = false;
        this.mMinInitialized = false;
        this.mMinLimit = -1;
        this.mMaxLimit = -1;
        this.mMinLimitInit = false;
        this.mMaxLimitInit = false;
        this.mMinPercentLimit = 0.0f;
        this.mMaxPercentLimit = 1.0f;
        this.mLimitTouchOnly = false;
        this.mSlideScale = 1.0f;
        this.mDispatchTouchDirectly = false;
        this.mDragging = false;
        this.mSliderFrom = 0;
        this.mInset = 0;
        this.mSliderInset = 0;
        this.mBaselCurveIn = new XBesselCurve3Interpolator(0.0f, 0.56f, 0.46f, 1.0f);
        this.mBaselCurveOut = new XBesselCurve3Interpolator(0.76f, 0.0f, 0.24f, 1.0f);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setMarginHorizontal(int margin) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
        lp.leftMargin = margin;
        lp.rightMargin = margin;
    }

    public void setMarginVertical(int margin) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
        lp.topMargin = margin;
        lp.bottomMargin = margin;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        setEnabled(this.mEnabled);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        setLayerType(1, null);
        this.mThemeViewModel = ThemeViewModel.create(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.QcSliderLayout, defStyleAttr, defStyleRes);
        this.mSliderFrom = a.getInt(5, 0);
        this.mBgColor = a.getColor(2, DEFAULT_BG_COLOR);
        this.mSliderColor = a.getColor(7, 16777215);
        this.mBgColorId = a.getResourceId(2, 0);
        this.mSliderColorId = a.getResourceId(7, 0);
        this.mCornerRadius = a.getDimensionPixelSize(3, 32);
        this.mSliderOuterCornerRadius = a.getDimensionPixelSize(10, 5);
        this.mSliderInnerCornerRadius = a.getDimensionPixelSize(8, 27);
        this.mInset = a.getDimensionPixelSize(4, 0);
        this.mSliderInset = a.getDimensionPixelSize(9, 0);
        this.mSlideScale = a.getFloat(6, 1.0f);
        this.mEnabled = a.getBoolean(0, true);
        this.mDisabledAlpha = a.getFloat(1, 0.36f);
        a.recycle();
        setWillNotDraw(false);
        this.mContentBounds = new Rect();
        this.mBounds = new RectF();
        ViewConfiguration config = ViewConfiguration.get(context);
        this.mTouchSlop = config.getScaledTouchSlop();
        QcSliderDrawableBuilder sliderFrom = new QcSliderDrawableBuilder().setColor(this.mBgColor, this.mSliderColor).setCornerRadius(this.mCornerRadius).setSliderOuterCornerRadius(this.mSliderOuterCornerRadius).setSliderInnerCornerRadius(this.mSliderInnerCornerRadius).setSliderFrom(this.mSliderFrom);
        int i = this.mSliderInset;
        QcSliderDrawableBuilder sliderInset = sliderFrom.setSliderInset(i, i);
        int i2 = this.mInset;
        this.mQcSliderDrawable = sliderInset.setInset(i2, i2).build();
        this.mQcSliderDrawable.setCallback(this);
    }

    public int getMin() {
        return this.mMinProgress;
    }

    public void setMin(int minProgress) {
        if (this.mMaxInitialized && minProgress > this.mMaxProgress) {
            minProgress = this.mMaxProgress;
        }
        this.mMinInitialized = true;
        if (this.mMaxInitialized && minProgress != this.mMinProgress) {
            this.mMinProgress = minProgress;
            postInvalidate();
            if (this.mProgress < minProgress) {
                this.mProgress = minProgress;
            }
            setProgress(this.mProgress);
            return;
        }
        this.mMinProgress = minProgress;
    }

    public int getMax() {
        return this.mMaxProgress;
    }

    public void setMax(int maxProgress) {
        if (this.mMinInitialized && maxProgress < this.mMinProgress) {
            maxProgress = this.mMinProgress;
        }
        this.mMaxInitialized = true;
        if (this.mMinInitialized && maxProgress != this.mMaxProgress) {
            this.mMaxProgress = maxProgress;
            postInvalidate();
            if (this.mProgress > maxProgress) {
                this.mProgress = maxProgress;
            }
            setProgress(this.mProgress);
            return;
        }
        this.mMaxProgress = maxProgress;
    }

    public void setMinLimit(int minLimit) {
        assetLimit(minLimit);
        if (!this.mMinLimitInit || minLimit != this.mMinLimit) {
            this.mMinLimit = minLimit;
            this.mMinPercentLimit = (minLimit - getMin()) / getRange();
            if (!this.mLimitTouchOnly && this.mProgress < minLimit) {
                setProgress(minLimit);
            }
        }
    }

    public void setMaxLimit(int maxLimit) {
        assetLimit(maxLimit);
        if (!this.mMaxLimitInit || maxLimit != this.mMaxLimit) {
            this.mMaxLimit = maxLimit;
            this.mMaxPercentLimit = (maxLimit - getMin()) / getRange();
            if (!this.mLimitTouchOnly && this.mProgress > maxLimit) {
                setProgress(maxLimit);
            }
        }
    }

    private void assetLimit(int limit) {
        if (limit > getMax()) {
            throw new IllegalArgumentException(String.format("limit(%s) is larger than max(%s), Please set max first", Integer.valueOf(limit), Integer.valueOf(getMax())));
        }
        if (limit < getMin()) {
            throw new IllegalArgumentException(String.format("limit(%s) is less than min(%s), Please set min first", Integer.valueOf(limit), Integer.valueOf(getMin())));
        }
    }

    public void setLimitTouchOnly(boolean limitTouchOnly) {
        this.mLimitTouchOnly = limitTouchOnly;
    }

    public int getRange() {
        return this.mMaxProgress - this.mMinProgress;
    }

    public int getProgress() {
        return this.mProgress;
    }

    public void setSliderFrom(int sliderFrom) {
        this.mSliderFrom = sliderFrom;
    }

    public void setOnSlideChangeListener(OnSlideChangeListener onSlideChangeListener) {
        this.mOnSlideChangeListener = onSlideChangeListener;
    }

    public void setProgress(int progress) {
        int progress2 = limitProgress(progress, !this.mLimitTouchOnly);
        Log.d(TAG, "setProgress=" + progress2 + ", max=" + this.mMaxProgress + ", min=" + this.mMinProgress);
        float percent = convertProgress(progress2);
        setDrawablePercent(limitDrawablePercent(percent, this.mLimitTouchOnly ^ true));
        this.mProgress = progress2;
    }

    public void setColor(int bgColor, int sliderColor) {
        Context context = getContext();
        this.mBgColorId = bgColor;
        this.mSliderColorId = sliderColor;
        if (context != null) {
            this.mBgColor = context.getColor(this.mBgColorId);
            this.mSliderColor = context.getColor(this.mSliderColorId);
        }
        QcSliderDrawable qcSliderDrawable = this.mQcSliderDrawable;
        if (qcSliderDrawable != null) {
            qcSliderDrawable.setColor(this.mBgColor, this.mSliderColor);
            invalidateDrawable(this.mQcSliderDrawable);
        }
    }

    public void setColor(int sliderColor) {
        this.mSliderColor = sliderColor;
        QcSliderDrawable qcSliderDrawable = this.mQcSliderDrawable;
        if (qcSliderDrawable != null) {
            qcSliderDrawable.setColor(this.mBgColor, sliderColor);
            invalidateDrawable(this.mQcSliderDrawable);
        }
    }

    public float getSlideScale() {
        return this.mSlideScale;
    }

    public void setSlideScale(float slideScale) {
        this.mSlideScale = slideScale;
    }

    public void setDispatchTouchDirectly(boolean dispatchTouchDirectly) {
        this.mDispatchTouchDirectly = dispatchTouchDirectly;
    }

    @Override // android.view.View
    public void setEnabled(boolean enabled) {
        int count = getChildCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                child.setEnabled(enabled);
            }
        }
        super.setEnabled(enabled);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void drawableStateChanged() {
        QcSliderDrawable qcSliderDrawable = this.mQcSliderDrawable;
        if (qcSliderDrawable != null) {
            qcSliderDrawable.setAlpha(isEnabled() ? 255 : (int) (this.mDisabledAlpha * 255.0f));
        }
        super.drawableStateChanged();
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mThemeViewModel.onConfigurationChanged(this, newConfig);
        if (ThemeManager.isThemeChanged(newConfig)) {
            refreshTheme();
        }
    }

    public void refreshTheme() {
        setColor(this.mBgColorId, this.mSliderColorId);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mThemeViewModel.onAttachedToWindow(this);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mThemeViewModel.onDetachedFromWindow(this);
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable who) {
        return who == this.mQcSliderDrawable || super.verifyDrawable(who);
    }

    @Override // android.view.ViewGroup, android.view.View
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

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0016, code lost:
        if (r0 != 3) goto L11;
     */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.view.MotionEvent r10) {
        /*
            r9 = this;
            boolean r0 = r9.isEnabled()
            r1 = 1
            if (r0 != 0) goto L8
            return r1
        L8:
            int r0 = r10.getActionMasked()
            r2 = 1065353216(0x3f800000, float:1.0)
            if (r0 == 0) goto Lb3
            if (r0 == r1) goto La1
            r3 = 3
            r4 = 2
            if (r0 == r4) goto L1a
            if (r0 == r3) goto La1
            goto Lcf
        L1a:
            float r2 = r10.getX()
            float r5 = r10.getY()
            int r6 = r9.mSliderFrom
            r7 = 0
            if (r6 == 0) goto L2c
            if (r6 != r4) goto L2a
            goto L2c
        L2a:
            r4 = r7
            goto L34
        L2c:
            float r4 = r9.mTouchX
            float r4 = r2 - r4
            float r4 = java.lang.Math.abs(r4)
        L34:
            int r6 = r9.mSliderFrom
            if (r6 == r1) goto L3a
            if (r6 != r3) goto L42
        L3a:
            float r3 = r9.mTouchY
            float r3 = r5 - r3
            float r7 = java.lang.Math.abs(r3)
        L42:
            r3 = r7
            boolean r6 = r9.mDragging
            if (r6 != 0) goto L7d
            int r6 = r9.mTouchSlop
            float r7 = (float) r6
            int r7 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r7 > 0) goto L7d
            float r6 = (float) r6
            int r6 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r6 <= 0) goto L54
            goto L7d
        L54:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "onTouchEvent: move ignore, deltaX:"
            r6.append(r7)
            r6.append(r4)
            java.lang.String r7 = ", deltaY:"
            r6.append(r7)
            r6.append(r3)
            java.lang.String r7 = ", slop:"
            r6.append(r7)
            int r7 = r9.mTouchSlop
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            java.lang.String r7 = "QcSliderLayout"
            android.util.Log.i(r7, r6)
            goto Lcf
        L7d:
            android.view.ViewParent r6 = r9.getParent()
            r6.requestDisallowInterceptTouchEvent(r1)
            float r6 = r9.mLastMoveX
            float r6 = r2 - r6
            float r7 = r9.mLastMoveY
            float r7 = r5 - r7
            boolean r8 = r9.mDragging
            if (r8 != 0) goto L95
            r9.mDragging = r1
            r9.onStartTrackingTouch()
        L95:
            float r8 = r9.mSlideScale
            float r6 = r6 * r8
            float r7 = r7 * r8
            r9.trackMove(r6, r7)
            r9.mLastMoveX = r2
            r9.mLastMoveY = r5
            goto Lcf
        La1:
            boolean r3 = r9.mDragging
            if (r3 == 0) goto Lab
            r3 = 0
            r9.mDragging = r3
            r9.onStopTrackingTouch()
        Lab:
            float r3 = com.xiaopeng.systemui.qs.widgets.QcSliderLayout.SCALE_RULES
            com.xiaopeng.xui.view.animation.XBesselCurve3Interpolator r4 = r9.mBaselCurveOut
            r9.startAnimate(r3, r2, r4)
            goto Lcf
        Lb3:
            float r3 = r10.getX()
            r9.mTouchX = r3
            float r3 = r10.getY()
            r9.mTouchY = r3
            float r3 = r9.mTouchX
            r9.mLastMoveX = r3
            float r3 = r9.mTouchY
            r9.mLastMoveY = r3
            float r3 = com.xiaopeng.systemui.qs.widgets.QcSliderLayout.SCALE_RULES
            com.xiaopeng.xui.view.animation.XBesselCurve3Interpolator r4 = r9.mBaselCurveIn
            r9.startAnimate(r2, r3, r4)
        Lcf:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.systemui.qs.widgets.QcSliderLayout.onTouchEvent(android.view.MotionEvent):boolean");
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mBounds.set(0.0f, 0.0f, w, h);
        this.mContentBounds.set(0, 0, w, h);
        this.mQcSliderDrawable.setBounds(this.mContentBounds);
        setPivotX(w / 2);
        setPivotY(h / 2);
    }

    private void startAnimate(float beginValue, float endValue, Interpolator interpolator) {
        this.mScaleXAnimator = ObjectAnimator.ofFloat(this, "scaleX", beginValue, endValue);
        this.mScaleYAnimator = ObjectAnimator.ofFloat(this, "scaleY", beginValue, endValue);
        this.mScaleXAnimator.setDuration(200L);
        this.mScaleYAnimator.setDuration(200L);
        this.mScaleXAnimator.setInterpolator(interpolator);
        this.mScaleYAnimator.setInterpolator(interpolator);
        this.mScaleXAnimator.start();
        this.mScaleYAnimator.start();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mQcSliderDrawable.draw(canvas);
        activateChildren();
    }

    private void activateChildren() {
        Rect bounds = this.mQcSliderDrawable.getSliderBounds();
        int i = this.mSliderFrom;
        if (i == 0) {
            activateChildFromLeft(this, bounds.right, 0);
        } else if (i == 1) {
            activateChildFromTop(this, bounds.bottom, 0);
        } else if (i == 2) {
            activateChildFromRight(this, bounds.left, 0);
        } else if (i == 3) {
            activateChildFromBottom(this, bounds.top, 0);
        }
    }

    private void activateChildFromLeft(ViewGroup parent, float sliderRight, int offset) {
        int count = parent.getChildCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                View child = parent.getChildAt(i);
                if (child instanceof ViewGroup) {
                    activateChildFromLeft((ViewGroup) child, sliderRight, child.getLeft());
                } else if (child.getLeft() + offset < sliderRight) {
                    child.setActivated(true);
                } else {
                    child.setActivated(false);
                }
            }
        }
    }

    private void activateChildFromRight(ViewGroup parent, float sliderLeft, int offset) {
        int count = parent.getChildCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                View child = parent.getChildAt(i);
                if (child instanceof ViewGroup) {
                    activateChildFromRight((ViewGroup) child, sliderLeft, child.getLeft());
                } else if (shouldActivate(child) && child.getRight() + offset > sliderLeft) {
                    child.setActivated(true);
                } else {
                    child.setActivated(false);
                }
            }
        }
    }

    private void activateChildFromTop(ViewGroup parent, float sliderBottom, int offset) {
        int count = parent.getChildCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                View child = parent.getChildAt(i);
                if (child instanceof ViewGroup) {
                    activateChildFromTop((ViewGroup) child, sliderBottom, child.getTop());
                } else if (child.getBottom() + offset < sliderBottom) {
                    child.setActivated(true);
                } else {
                    child.setActivated(false);
                }
            }
        }
    }

    private void activateChildFromBottom(ViewGroup parent, float sliderTop, int offset) {
        int count = parent.getChildCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                View child = parent.getChildAt(i);
                if (child instanceof ViewGroup) {
                    activateChildFromBottom((ViewGroup) child, sliderTop, child.getTop());
                } else if (child.getBottom() + offset > sliderTop) {
                    child.setActivated(true);
                } else {
                    child.setActivated(false);
                }
            }
        }
    }

    private boolean shouldActivate(View child) {
        return true;
    }

    private void trackMove(float deltaX, float deltaY) {
        Rect bounds = this.mQcSliderDrawable.getContainerBounds();
        float currentPercent = this.mQcSliderDrawable.getLevel() / 10000.0f;
        float percent = currentPercent;
        int i = this.mSliderFrom;
        if (i == 0) {
            percent = currentPercent + (deltaX / bounds.width());
        } else if (i == 1) {
            percent = currentPercent + (deltaY / bounds.height());
        } else if (i == 2) {
            percent = currentPercent - (deltaX / bounds.width());
        } else if (i == 3) {
            percent = currentPercent - (deltaY / bounds.height());
        }
        float percent2 = limitDrawablePercent(percent, true);
        int newProgress = convertPercent(percent2, false);
        if (this.mDispatchTouchDirectly) {
            Log.d(TAG, "trackMove: dispatchTouchDirectly, newProgress=" + newProgress + ", oldProgress=" + this.mProgress + ", percent=" + percent2);
            OnSlideChangeListener onSlideChangeListener = this.mOnSlideChangeListener;
            if (onSlideChangeListener != null) {
                onSlideChangeListener.onProgressChanged(this, newProgress);
                return;
            }
            return;
        }
        Log.d(TAG, "trackMove: newProgress=" + newProgress + ", oldProgress=" + this.mProgress + ", percent=" + percent2);
        setDrawablePercent(percent2);
        if (newProgress != this.mProgress) {
            this.mProgress = newProgress;
            OnSlideChangeListener onSlideChangeListener2 = this.mOnSlideChangeListener;
            if (onSlideChangeListener2 != null) {
                onSlideChangeListener2.onProgressChanged(this, this.mProgress);
            }
        }
    }

    private void onStartTrackingTouch() {
        OnSlideChangeListener onSlideChangeListener = this.mOnSlideChangeListener;
        if (onSlideChangeListener != null) {
            onSlideChangeListener.onStartTrackingTouch(this);
        }
    }

    private void onStopTrackingTouch() {
        OnSlideChangeListener onSlideChangeListener = this.mOnSlideChangeListener;
        if (onSlideChangeListener != null) {
            onSlideChangeListener.onStopTrackingTouch(this);
        }
    }

    private void setDrawablePercent(float percent) {
        int level = (int) (this.mMaxLevel * percent);
        Log.v(TAG, "setDrawablePercent: percent=" + percent + ", currentLevel=" + this.mQcSliderDrawable.getLevel() + ", level=" + level);
        boolean changed = this.mQcSliderDrawable.setLevel(level);
        StringBuilder sb = new StringBuilder();
        sb.append("setDrawablePercent: levelChanged=");
        sb.append(changed);
        Log.v(TAG, sb.toString());
    }

    private float limitDrawablePercent(float percent, boolean limit) {
        float min = !limit ? 0.0f : this.mMinPercentLimit;
        float max = !limit ? 1.0f : this.mMaxPercentLimit;
        return MathUtils.clamp(percent, min, max);
    }

    private int limitProgress(int progress, boolean limit) {
        int min = (limit && this.mMinLimitInit) ? this.mMinLimit : this.mMinProgress;
        int max = (limit && this.mMaxLimitInit) ? this.mMaxLimit : this.mMaxProgress;
        return MathUtils.clamp(progress, min, max);
    }

    private int convertPercent(float percent, boolean wrapFloor) {
        float progress = (getRange() * percent) + getMin();
        double d = progress;
        int progressInt = (int) (wrapFloor ? Math.floor(d) : Math.ceil(d));
        return limitProgress(progressInt, true);
    }

    private float convertProgress(int progress) {
        int range = getRange();
        if (range > 0) {
            return (progress - getMin()) / range;
        }
        return 0.0f;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes24.dex */
    public static class QcSliderDrawable extends Drawable {
        private static final int MAX_LEVEL = 10000;
        private static final String TAG = "QcSliderDrawable";
        private int mBgColor;
        private Paint mBgPaint;
        private float mBgRadius;
        private Rect mContainerBounds;
        private boolean mEnabled;
        private int mInsetX;
        private int mInsetY;
        private Paint mPaint;
        private Bitmap mSliderBitmap;
        private Rect mSliderBounds;
        private Canvas mSliderCanvas;
        private int mSliderColor;
        private int mSliderFrom;
        private float mSliderInnerRadius;
        private int mSliderInsetX;
        private int mSliderInsetY;
        private float mSliderOuterRadius;
        private Paint mSliderPaint;
        private BitmapShader mSliderShader;

        QcSliderDrawable() {
            this.mBgRadius = 0.0f;
            this.mSliderOuterRadius = 0.0f;
            this.mSliderInnerRadius = 0.0f;
            this.mSliderFrom = 0;
            this.mSliderInsetX = 0;
            this.mSliderInsetY = 0;
            this.mInsetX = 0;
            this.mInsetY = 0;
            this.mPaint = new Paint(5);
            this.mSliderPaint = new Paint(5);
            this.mBgPaint = new Paint(1);
            this.mSliderBounds = new Rect();
            this.mContainerBounds = new Rect();
        }

        QcSliderDrawable(int bgColor, int sliderColor) {
            this();
            setColor(bgColor, sliderColor);
        }

        void setCornerRadius(float radius) {
            if (radius < 0.0f) {
                radius = 0.0f;
            }
            this.mBgRadius = radius;
        }

        void setSliderOuterCornerRadius(float radius) {
            if (radius < 0.0f) {
                radius = 0.0f;
            }
            this.mSliderOuterRadius = radius;
        }

        void setSliderInnerRadius(float radius) {
            if (radius < 0.0f) {
                radius = 0.0f;
            }
            this.mSliderInnerRadius = radius;
        }

        void setColor(int bgColor, int sliderColor) {
            this.mBgColor = bgColor;
            this.mSliderColor = sliderColor;
            this.mBgPaint.setColor(this.mBgColor);
            this.mSliderPaint.setColor(this.mSliderColor);
        }

        void setSliderFrom(int edge) {
            this.mSliderFrom = edge;
        }

        void setSliderInset(int insetX, int insetY) {
            this.mSliderInsetX = insetX;
            this.mSliderInsetY = insetY;
        }

        void setInset(int insetX, int insetY) {
            this.mInsetX = insetX;
            this.mInsetY = insetY;
        }

        Rect getContainerBounds() {
            return this.mContainerBounds;
        }

        Rect getSliderBounds() {
            return this.mSliderBounds;
        }

        @Override // android.graphics.drawable.Drawable
        protected boolean onStateChange(int[] state) {
            boolean enabled = false;
            int length = state.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                int s = state[i];
                if (s != 16842910) {
                    i++;
                } else {
                    enabled = true;
                    break;
                }
            }
            boolean changed = this.mEnabled != enabled;
            this.mEnabled = enabled;
            return changed;
        }

        @Override // android.graphics.drawable.Drawable
        protected boolean onLevelChange(int level) {
            if (this.mContainerBounds != null) {
                refreshSliderBounds(level);
                invalidateSelf();
                return true;
            }
            return super.onLevelChange(level);
        }

        private void refreshSliderBounds(int level) {
            if (this.mContainerBounds.isEmpty() && getBounds().isEmpty()) {
                return;
            }
            Rect bounds = this.mContainerBounds;
            if (bounds == null) {
                bounds = getBounds();
            }
            float scale = level / 10000.0f;
            int i = this.mSliderFrom;
            if (i == 0) {
                int x = (int) (bounds.left + (bounds.width() * scale));
                this.mSliderBounds.set(bounds.left, bounds.top, Math.min(x, bounds.right), bounds.bottom);
                Log.d(TAG, "refreshSliderBounds: level=" + level + " scale=" + scale + " width=" + bounds.width() + " x=" + x + " bounds=" + bounds);
            } else if (i == 1) {
                int y = (int) (bounds.top + (bounds.height() * scale));
                this.mSliderBounds.set(bounds.left, bounds.top, bounds.right, Math.min(y, bounds.bottom));
                Log.d(TAG, "refreshSliderBounds: level=" + level + " scale=" + scale + " height=" + bounds.height() + " y=" + y + " bounds=" + bounds);
            } else if (i == 2) {
                int x2 = (int) (bounds.right - (bounds.width() * scale));
                this.mSliderBounds.set(Math.max(x2, bounds.left), bounds.top, bounds.right, bounds.bottom);
                Log.d(TAG, "refreshSliderBounds: level=" + level + " scale=" + scale + " width=" + bounds.width() + " x=" + x2 + " bounds=" + bounds);
            } else if (i == 3) {
                int y2 = (int) (bounds.bottom - (bounds.height() * scale));
                this.mSliderBounds.set(bounds.left, Math.max(y2, bounds.top), bounds.right, bounds.bottom);
                Log.d(TAG, "refreshSliderBounds: level=" + level + " scale=" + scale + " height=" + bounds.height() + " y=" + y2 + " bounds=" + bounds);
            }
        }

        @Override // android.graphics.drawable.Drawable
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            this.mSliderBitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888);
            Canvas canvas = this.mSliderCanvas;
            if (canvas == null) {
                this.mSliderCanvas = new Canvas(this.mSliderBitmap);
            } else {
                canvas.setBitmap(this.mSliderBitmap);
            }
            this.mSliderShader = new BitmapShader(this.mSliderBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            bounds.inset(this.mInsetX, this.mInsetY);
            this.mContainerBounds.set(bounds);
            this.mContainerBounds.inset(this.mSliderInsetX, this.mSliderInsetY);
            refreshSliderBounds(getLevel());
        }

        @Override // android.graphics.drawable.Drawable
        public void getOutline(Outline outline) {
            Rect bounds = getBounds();
            float rad = 0.0f;
            float f = this.mBgRadius;
            if (f > 0.0f) {
                rad = Math.min(f, Math.min(bounds.width(), bounds.height()) * 0.5f);
            }
            if (rad > 0.0f) {
                outline.setRoundRect(bounds, rad);
            } else {
                outline.setRect(bounds);
            }
        }

        @Override // android.graphics.drawable.Drawable
        public Rect getDirtyBounds() {
            return this.mContainerBounds;
        }

        @Override // android.graphics.drawable.Drawable
        public void invalidateSelf() {
            Drawable.Callback callback = getCallback();
            if (callback == null) {
                Log.d(TAG, "invalidateSelf: callback=null");
            } else {
                Log.d(TAG, "invalidateSelf: callback className=" + callback.getClass().getSimpleName() + " hashCode=" + callback.hashCode());
            }
            super.invalidateSelf();
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            Rect bounds = getBounds();
            if (this.mSliderCanvas != null) {
                this.mSliderBitmap.eraseColor(0);
                int sc = this.mSliderCanvas.save();
                drawRect(this.mSliderCanvas, this.mSliderInnerRadius, this.mSliderBounds, this.mSliderPaint);
                this.mSliderCanvas.restoreToCount(sc);
            }
            int count = canvas.save();
            drawRect(canvas, this.mBgRadius, bounds, this.mBgPaint);
            this.mPaint.setShader(this.mSliderShader);
            drawRect(canvas, this.mSliderOuterRadius, this.mContainerBounds, this.mPaint);
            if (count != 0) {
                canvas.restoreToCount(count);
            }
        }

        private void drawRect(Canvas canvas, float cornerRadius, Rect bounds, Paint paint) {
            if (cornerRadius > 0.0f) {
                float rad = Math.min(cornerRadius, Math.min(bounds.width(), bounds.height()) * 0.5f);
                canvas.drawRoundRect(bounds.left, bounds.top, bounds.right, bounds.bottom, rad, rad, paint);
                return;
            }
            canvas.drawRect(bounds, paint);
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
            this.mPaint.setAlpha(alpha);
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
            this.mPaint.setColorFilter(colorFilter);
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -3;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes24.dex */
    public static class QcSliderDrawableBuilder {
        private int mBgColor;
        private int mSliderColor;
        private float mBgRadius = 0.0f;
        private float mSliderOuterRadius = 0.0f;
        private float mSliderInnerRadius = 0.0f;
        private int mSliderFrom = 0;
        private int mSliderInsetX = 0;
        private int mSliderInsetY = 0;
        private int mInsetX = 0;
        private int mInsetY = 0;

        QcSliderDrawableBuilder() {
        }

        QcSliderDrawableBuilder setColor(int bgColor, int sliderColor) {
            this.mBgColor = bgColor;
            this.mSliderColor = sliderColor;
            return this;
        }

        QcSliderDrawableBuilder setCornerRadius(float radius) {
            if (radius < 0.0f) {
                radius = 0.0f;
            }
            this.mBgRadius = radius;
            return this;
        }

        QcSliderDrawableBuilder setSliderOuterCornerRadius(float radius) {
            if (radius < 0.0f) {
                radius = 0.0f;
            }
            this.mSliderOuterRadius = radius;
            return this;
        }

        QcSliderDrawableBuilder setSliderInnerCornerRadius(float radius) {
            if (radius < 0.0f) {
                radius = 0.0f;
            }
            this.mSliderInnerRadius = radius;
            return this;
        }

        QcSliderDrawableBuilder setSliderFrom(int edge) {
            this.mSliderFrom = edge;
            return this;
        }

        QcSliderDrawableBuilder setSliderInset(int insetX, int insetY) {
            this.mSliderInsetX = insetX;
            this.mSliderInsetY = insetY;
            return this;
        }

        QcSliderDrawableBuilder setInset(int insetX, int insetY) {
            this.mInsetX = insetX;
            this.mInsetY = insetY;
            return this;
        }

        QcSliderDrawable build() {
            QcSliderDrawable drawable = new QcSliderDrawable(this.mBgColor, this.mSliderColor);
            drawable.setCornerRadius(this.mBgRadius);
            drawable.setSliderOuterCornerRadius(this.mSliderOuterRadius);
            drawable.setSliderInnerRadius(this.mSliderInnerRadius);
            drawable.setSliderFrom(this.mSliderFrom);
            drawable.setSliderInset(this.mSliderInsetX, this.mSliderInsetY);
            drawable.setInset(this.mInsetX, this.mInsetY);
            return drawable;
        }
    }
}
