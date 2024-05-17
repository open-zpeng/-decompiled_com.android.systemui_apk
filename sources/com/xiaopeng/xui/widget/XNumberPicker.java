package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;
import androidx.annotation.CallSuper;
import androidx.appcompat.widget.AppCompatEditText;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.sound.XSoundEffectManager;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;
/* loaded from: classes25.dex */
public class XNumberPicker extends XLinearLayout {
    private static final long DEFAULT_LONG_PRESS_UPDATE_INTERVAL = 300;
    private static final int DEFAULT_TEXT_SIZE = 20;
    private static final int LAYOUT_ROUNDING_OFFSET = 5;
    private static final int SELECTED_TEXT_LAYOUT_OFFSET = 0;
    private static final int SELECTOR_ADJUSTMENT_DURATION_MILLIS = 800;
    private static final int SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT = 8;
    private static final int SELECTOR_MIDDLE_ITEM_INDEX = 2;
    private static final int SELECTOR_WHEEL_ITEM_COUNT = 5;
    private static final int SIZE_UNSPECIFIED = -1;
    private static final int SNAP_SCROLL_DURATION = 300;
    private static final int SYMBOL_LAYOUT_OFFSET = 35;
    private static final int SYMBOL_WIDTH = 6;
    private static final int TEXT_LAYOUT_DEFAULT = 1;
    private static final int TEXT_LAYOUT_LEFT = 0;
    private static final int TEXT_LAYOUT_MIDDLE = 1;
    private static final int TEXT_LAYOUT_RIGHT = 2;
    private static final float TOP_AND_BOTTOM_FADING_EDGE_STRENGTH = 0.9f;
    private static final int UNSCALED_DEFAULT_SELECTION_DIVIDERS_DISTANCE = 48;
    private static final int UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT = 2;
    private final Scroller mAdjustScroller;
    private BeginSoftInputOnLongPressCommand mBeginSoftInputOnLongPressCommand;
    private int mBottomSelectionDividerBottom;
    private ChangeCurrentByOneFromLongPressCommand mChangeCurrentByOneFromLongPressCommand;
    private final boolean mComputeMaxWidth;
    private int mCurrentScrollOffset;
    private String[] mDisplayedValues;
    private final Scroller mFlingScroller;
    private Formatter mFormatter;
    private boolean mHideWheelUntilFocused;
    private boolean mIgnoreMoveEvents;
    private int mInitialScrollOffset;
    private long mLastDownEventTime;
    private float mLastDownEventY;
    private float mLastDownOrMoveEventY;
    private int mLastHandledDownDpadKeyCode;
    private int mLastHoveredChildVirtualViewId;
    private long mLongPressUpdateInterval;
    private final int mMaxHeight;
    private int mMaxValue;
    private int mMaxWidth;
    private int mMaximumFlingVelocity;
    private final int mMinHeight;
    private int mMinValue;
    private final int mMinWidth;
    private int mMinimumFlingVelocity;
    private OnScrollListener mOnScrollListener;
    private OnValueChangeListener mOnValueChangeListener;
    private boolean mPerformClickOnTap;
    private final PressedStateHelper mPressedStateHelper;
    private int mPreviousScrollerY;
    private int mScrollState;
    private int mSelectedSelectorElementHeight;
    private final int mSelectedTextSize;
    private final Drawable mSelectionDivider;
    private final int mSelectionDividerHeight;
    private final int mSelectionDividersDistance;
    private int mSelectorElementHeight;
    private final SparseArray<String> mSelectorIndexToStringCache;
    private final int[] mSelectorIndices;
    private int mSelectorTextGapHeight;
    private final Paint mSelectorWheelPaint;
    private final int mSolidColor;
    private final Drawable mSymbol;
    private ColorStateList mTextColors;
    private int mTextLayout;
    private int mTextLayoutMargin;
    private final int mTextSize;
    private int mTopSelectionDividerTop;
    private int mTouchSlop;
    private int mValue;
    private VelocityTracker mVelocityTracker;
    private int mWidth;
    private boolean mWrapSelectorWheel;
    private boolean mWrapSelectorWheelPreferred;
    private static final TwoDigitFormatter sTwoDigitFormatter = new TwoDigitFormatter();
    private static final char[] DIGIT_CHARACTERS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 1632, 1633, 1634, 1635, 1636, 1637, 1638, 1639, 1640, 1641, 1776, 1777, 1778, 1779, 1780, 1781, 1782, 1783, 1784, 1785, 2406, 2407, 2408, 2409, 2410, 2411, 2412, 2413, 2414, 2415, 2534, 2535, 2536, 2537, 2538, 2539, 2540, 2541, 2542, 2543, 3302, 3303, 3304, 3305, 3306, 3307, 3308, 3309, 3310, 3311};

    /* loaded from: classes25.dex */
    public interface Formatter {
        String format(int i);
    }

    /* loaded from: classes25.dex */
    public interface OnScrollListener {
        public static final int SCROLL_STATE_FLING = 2;
        public static final int SCROLL_STATE_IDLE = 0;
        public static final int SCROLL_STATE_TOUCH_SCROLL = 1;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes25.dex */
        public @interface ScrollState {
        }

        void onScrollStateChange(XNumberPicker xNumberPicker, int i);
    }

    /* loaded from: classes25.dex */
    public interface OnValueChangeListener {
        void onValueChange(XNumberPicker xNumberPicker, int i, int i2);
    }

    /* loaded from: classes25.dex */
    private static class TwoDigitFormatter implements Formatter {
        java.util.Formatter mFmt;
        char mZeroDigit;
        final StringBuilder mBuilder = new StringBuilder();
        final Object[] mArgs = new Object[1];

        TwoDigitFormatter() {
            Locale locale = Locale.getDefault();
            init(locale);
        }

        private void init(Locale locale) {
            this.mFmt = createFormatter(locale);
            this.mZeroDigit = getZeroDigit(locale);
        }

        @Override // com.xiaopeng.xui.widget.XNumberPicker.Formatter
        public String format(int value) {
            Locale currentLocale = Locale.getDefault();
            if (this.mZeroDigit != getZeroDigit(currentLocale)) {
                init(currentLocale);
            }
            this.mArgs[0] = Integer.valueOf(value);
            StringBuilder sb = this.mBuilder;
            sb.delete(0, sb.length());
            this.mFmt.format("%02d", this.mArgs);
            return this.mFmt.toString();
        }

        private static char getZeroDigit(Locale locale) {
            return '0';
        }

        private java.util.Formatter createFormatter(Locale locale) {
            return new java.util.Formatter(this.mBuilder, locale);
        }
    }

    public static Formatter getTwoDigitFormatter() {
        return sTwoDigitFormatter;
    }

    public XNumberPicker(Context context) {
        this(context, null);
    }

    public XNumberPicker(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.XNumberPicker);
    }

    public XNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.XNumberPicker);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v3 */
    /* JADX WARN: Type inference failed for: r2v4, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r2v5 */
    public XNumberPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        boolean z;
        ?? r2;
        int i;
        int i2;
        this.mWrapSelectorWheelPreferred = true;
        this.mLongPressUpdateInterval = DEFAULT_LONG_PRESS_UPDATE_INTERVAL;
        this.mSelectorIndexToStringCache = new SparseArray<>();
        this.mSelectorIndices = new int[5];
        this.mInitialScrollOffset = Integer.MIN_VALUE;
        this.mScrollState = 0;
        this.mLastHandledDownDpadKeyCode = -1;
        TypedArray attributesArray = context.obtainStyledAttributes(attrs, R.styleable.XNumberPicker, defStyleAttr, defStyleRes);
        this.mHideWheelUntilFocused = attributesArray.getBoolean(R.styleable.XNumberPicker_np_hideWheelUntilFocused, false);
        this.mTextLayout = attributesArray.getInt(R.styleable.XNumberPicker_np_textLayout, 1);
        this.mSolidColor = attributesArray.getColor(R.styleable.XNumberPicker_np_solidColor, 0);
        Drawable selectionDivider = attributesArray.getDrawable(R.styleable.XNumberPicker_np_selectionDivider);
        if (selectionDivider != null) {
            selectionDivider.setCallback(this);
            selectionDivider.setLayoutDirection(getLayoutDirection());
            if (selectionDivider.isStateful()) {
                selectionDivider.setState(getDrawableState());
            }
        }
        this.mSelectionDivider = selectionDivider;
        this.mSymbol = attributesArray.getDrawable(R.styleable.XNumberPicker_np_symbol);
        int defSelectionDividerHeight = (int) TypedValue.applyDimension(1, 2.0f, getResources().getDisplayMetrics());
        this.mSelectionDividerHeight = attributesArray.getDimensionPixelSize(R.styleable.XNumberPicker_np_selectionDividerHeight, defSelectionDividerHeight);
        int defSelectionDividerDistance = (int) TypedValue.applyDimension(1, 48.0f, getResources().getDisplayMetrics());
        this.mSelectionDividersDistance = attributesArray.getDimensionPixelSize(R.styleable.XNumberPicker_np_selectionDividersDistance, defSelectionDividerDistance);
        this.mTextLayoutMargin = attributesArray.getDimensionPixelSize(R.styleable.XNumberPicker_np_text_layout_margin, 0);
        this.mMinHeight = attributesArray.getDimensionPixelSize(R.styleable.XNumberPicker_np_internalMinHeight, -1);
        this.mMaxHeight = attributesArray.getDimensionPixelSize(R.styleable.XNumberPicker_np_internalMaxHeight, -1);
        int i3 = this.mMinHeight;
        if (i3 == -1 || (i2 = this.mMaxHeight) == -1 || i3 <= i2) {
            this.mMinWidth = attributesArray.getDimensionPixelSize(R.styleable.XNumberPicker_np_internalMinWidth, -1);
            this.mMaxWidth = attributesArray.getDimensionPixelSize(R.styleable.XNumberPicker_np_internalMaxWidth, -1);
            int i4 = this.mMinWidth;
            if (i4 == -1 || (i = this.mMaxWidth) == -1 || i4 <= i) {
                if (this.mMaxWidth == -1) {
                    z = true;
                } else {
                    z = false;
                }
                this.mComputeMaxWidth = z;
                this.mTextSize = attributesArray.getDimensionPixelSize(R.styleable.XNumberPicker_android_textSize, 20);
                this.mSelectedTextSize = attributesArray.getDimensionPixelSize(R.styleable.XNumberPicker_np_selectedTextSize, 20);
                this.mTextColors = attributesArray.getColorStateList(R.styleable.XNumberPicker_android_textColor);
                if (this.mTextColors == null) {
                    this.mTextColors = ColorStateList.valueOf(context.getColor(17170444));
                }
                attributesArray.recycle();
                this.mPressedStateHelper = new PressedStateHelper();
                setWillNotDraw(false);
                ViewConfiguration configuration = ViewConfiguration.get(context);
                this.mTouchSlop = configuration.getScaledTouchSlop();
                this.mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
                this.mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity() / 8;
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                int i5 = this.mTextLayout;
                if (i5 != 0) {
                    if (i5 == 1) {
                        paint.setTextAlign(Paint.Align.CENTER);
                    } else if (i5 == 2) {
                        paint.setTextAlign(Paint.Align.RIGHT);
                    }
                } else {
                    paint.setTextAlign(Paint.Align.LEFT);
                }
                paint.setTextSize(Math.max(this.mTextSize, this.mSelectedTextSize));
                int color = this.mTextColors.getDefaultColor();
                paint.setColor(color);
                paint.setTypeface(Typeface.create(getResources().getString(R.string.x_font_typeface_medium), 0));
                this.mSelectorWheelPaint = paint;
                this.mFlingScroller = new Scroller(getContext(), null, true);
                this.mAdjustScroller = new Scroller(getContext(), new DecelerateInterpolator(2.5f));
                if (getImportantForAccessibility() != 0) {
                    r2 = 1;
                } else {
                    r2 = 1;
                    setImportantForAccessibility(1);
                }
                if (getFocusable() == 16) {
                    setFocusable((int) r2);
                    setFocusableInTouchMode(r2);
                    return;
                }
                return;
            }
            throw new IllegalArgumentException("minWidth > maxWidth");
        }
        throw new IllegalArgumentException("minHeight > maxHeight");
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            initializeSelectorWheel();
            initializeFadingEdges();
            int height = getHeight();
            int i = this.mSelectionDividersDistance;
            int i2 = this.mSelectionDividerHeight;
            this.mTopSelectionDividerTop = (((height - i) / 2) - i2) + 5;
            this.mBottomSelectionDividerBottom = this.mTopSelectionDividerTop + (i2 * 2) + i;
        }
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int newWidthMeasureSpec = makeMeasureSpec(widthMeasureSpec, this.mMaxWidth);
        int newHeightMeasureSpec = makeMeasureSpec(heightMeasureSpec, this.mMaxHeight);
        super.onMeasure(newWidthMeasureSpec, newHeightMeasureSpec);
        int widthSize = resolveSizeAndStateRespectingMinSize(this.mMinWidth, getMeasuredWidth(), widthMeasureSpec);
        this.mWidth = widthSize;
        int heightSize = resolveSizeAndStateRespectingMinSize(this.mMinHeight, getMeasuredHeight(), heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
    }

    private boolean moveToFinalScrollerPosition(Scroller scroller) {
        scroller.forceFinished(true);
        int amountToScroll = scroller.getFinalY() - scroller.getCurrY();
        int futureScrollOffset = (this.mCurrentScrollOffset + amountToScroll) % this.mSelectorElementHeight;
        int overshootAdjustment = this.mInitialScrollOffset - futureScrollOffset;
        if (overshootAdjustment == 0) {
            return false;
        }
        int abs = Math.abs(overshootAdjustment);
        int i = this.mSelectorElementHeight;
        if (abs > i / 2) {
            if (overshootAdjustment > 0) {
                overshootAdjustment -= i;
            } else {
                overshootAdjustment += i;
            }
        }
        scrollBy(0, amountToScroll + overshootAdjustment);
        return true;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            int action = event.getActionMasked();
            if (action == 0) {
                removeAllCallbacks();
                float y = event.getY();
                this.mLastDownEventY = y;
                this.mLastDownOrMoveEventY = y;
                this.mLastDownEventTime = event.getEventTime();
                this.mIgnoreMoveEvents = false;
                this.mPerformClickOnTap = false;
                float f = this.mLastDownEventY;
                if (f < this.mTopSelectionDividerTop) {
                    if (this.mScrollState == 0) {
                        this.mPressedStateHelper.buttonPressDelayed(2);
                    }
                } else if (f > this.mBottomSelectionDividerBottom && this.mScrollState == 0) {
                    this.mPressedStateHelper.buttonPressDelayed(1);
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                if (!this.mFlingScroller.isFinished()) {
                    this.mFlingScroller.forceFinished(true);
                    this.mAdjustScroller.forceFinished(true);
                    onScrollStateChange(0);
                } else if (!this.mAdjustScroller.isFinished()) {
                    this.mFlingScroller.forceFinished(true);
                    this.mAdjustScroller.forceFinished(true);
                } else {
                    float f2 = this.mLastDownEventY;
                    if (f2 < this.mTopSelectionDividerTop) {
                        postChangeCurrentByOneFromLongPress(false, ViewConfiguration.getLongPressTimeout());
                    } else if (f2 > this.mBottomSelectionDividerBottom) {
                        postChangeCurrentByOneFromLongPress(true, ViewConfiguration.getLongPressTimeout());
                    } else {
                        this.mPerformClickOnTap = true;
                        postBeginSoftInputOnLongPressCommand();
                    }
                }
                return true;
            }
            return false;
        }
        return false;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            if (this.mVelocityTracker == null) {
                this.mVelocityTracker = VelocityTracker.obtain();
            }
            this.mVelocityTracker.addMovement(event);
            int action = event.getActionMasked();
            if (action != 1) {
                if (action == 2 && !this.mIgnoreMoveEvents) {
                    float currentMoveY = event.getY();
                    if (this.mScrollState != 1) {
                        int deltaDownY = (int) Math.abs(currentMoveY - this.mLastDownEventY);
                        if (deltaDownY > this.mTouchSlop) {
                            removeAllCallbacks();
                            onScrollStateChange(1);
                        }
                    } else {
                        int deltaMoveY = (int) (currentMoveY - this.mLastDownOrMoveEventY);
                        scrollBy(0, deltaMoveY);
                        invalidate();
                    }
                    this.mLastDownOrMoveEventY = currentMoveY;
                }
            } else {
                removeBeginSoftInputCommand();
                removeChangeCurrentByOneFromLongPress();
                this.mPressedStateHelper.cancel();
                VelocityTracker velocityTracker = this.mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, this.mMaximumFlingVelocity);
                int initialVelocity = (int) velocityTracker.getYVelocity();
                if (Math.abs(initialVelocity) > this.mMinimumFlingVelocity) {
                    fling(initialVelocity);
                    onScrollStateChange(2);
                } else {
                    int eventY = (int) event.getY();
                    int deltaMoveY2 = (int) Math.abs(eventY - this.mLastDownEventY);
                    long deltaTime = event.getEventTime() - this.mLastDownEventTime;
                    if (deltaMoveY2 <= this.mTouchSlop && deltaTime < ViewConfiguration.getTapTimeout()) {
                        if (!this.mPerformClickOnTap) {
                            int selectorIndexOffset = (eventY / this.mSelectorElementHeight) - 2;
                            if (selectorIndexOffset > 0) {
                                changeValueByOne(true);
                                this.mPressedStateHelper.buttonTapped(1);
                            } else if (selectorIndexOffset < 0) {
                                changeValueByOne(false);
                                this.mPressedStateHelper.buttonTapped(2);
                            }
                        } else {
                            this.mPerformClickOnTap = false;
                            performClick();
                        }
                    } else {
                        ensureScrollWheelAdjusted();
                    }
                    onScrollStateChange(0);
                }
                this.mVelocityTracker.recycle();
                this.mVelocityTracker = null;
            }
            return true;
        }
        return false;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == 1 || action == 3) {
            removeAllCallbacks();
        }
        return super.dispatchTouchEvent(event);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XLinearLayout, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (ThemeManager.isThemeChanged(newConfig)) {
            this.mTextColors = getContext().getResources().getColorStateList(R.color.x_number_picker_text_color, null);
            postInvalidate();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == 19 || keyCode == 20) {
            int action = event.getAction();
            if (action == 0) {
                if (!this.mWrapSelectorWheel) {
                    if (keyCode == 20) {
                    }
                }
                requestFocus();
                this.mLastHandledDownDpadKeyCode = keyCode;
                removeAllCallbacks();
                if (this.mFlingScroller.isFinished()) {
                    changeValueByOne(keyCode == 20);
                }
                return true;
            } else if (action == 1 && this.mLastHandledDownDpadKeyCode == keyCode) {
                this.mLastHandledDownDpadKeyCode = -1;
                return true;
            }
        } else if (keyCode == 23 || keyCode == 66) {
            removeAllCallbacks();
        }
        return super.dispatchKeyEvent(event);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTrackballEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == 1 || action == 3) {
            removeAllCallbacks();
        }
        return super.dispatchTrackballEvent(event);
    }

    @Override // android.view.View
    public void computeScroll() {
        Scroller scroller = this.mFlingScroller;
        if (scroller.isFinished()) {
            scroller = this.mAdjustScroller;
            if (scroller.isFinished()) {
                return;
            }
        }
        scroller.computeScrollOffset();
        int currentScrollerY = scroller.getCurrY();
        if (this.mPreviousScrollerY == 0) {
            this.mPreviousScrollerY = scroller.getStartY();
        }
        scrollBy(0, currentScrollerY - this.mPreviousScrollerY);
        this.mPreviousScrollerY = currentScrollerY;
        if (scroller.isFinished()) {
            onScrollerFinished(scroller);
        } else {
            invalidate();
        }
    }

    @Override // android.view.View
    public void scrollBy(int x, int y) {
        int i;
        int[] selectorIndices = this.mSelectorIndices;
        int startScrollOffset = this.mCurrentScrollOffset;
        if (!this.mWrapSelectorWheel && y > 0 && selectorIndices[2] <= this.mMinValue) {
            this.mCurrentScrollOffset = this.mInitialScrollOffset;
        } else if (!this.mWrapSelectorWheel && y < 0 && selectorIndices[2] >= this.mMaxValue) {
            this.mCurrentScrollOffset = this.mInitialScrollOffset;
        } else {
            this.mCurrentScrollOffset += y;
            while (true) {
                int i2 = this.mCurrentScrollOffset;
                int i3 = i2 - this.mInitialScrollOffset;
                int i4 = this.mSelectorTextGapHeight;
                if (i3 <= i4) {
                    break;
                }
                int decrement = (i4 * 2) + 1;
                this.mCurrentScrollOffset = i2 - decrement;
                decrementSelectorIndices(selectorIndices);
                setValueInternal(selectorIndices[2], true);
                if (!this.mWrapSelectorWheel && selectorIndices[2] <= this.mMinValue) {
                    this.mCurrentScrollOffset = this.mInitialScrollOffset;
                }
            }
            while (true) {
                i = this.mCurrentScrollOffset;
                int i5 = i - this.mInitialScrollOffset;
                int i6 = this.mSelectorTextGapHeight;
                if (i5 >= (-i6)) {
                    break;
                }
                int increment = (i6 * 2) + 1;
                this.mCurrentScrollOffset = i + increment;
                incrementSelectorIndices(selectorIndices);
                setValueInternal(selectorIndices[2], true);
                if (!this.mWrapSelectorWheel && selectorIndices[2] >= this.mMaxValue) {
                    this.mCurrentScrollOffset = this.mInitialScrollOffset;
                }
            }
            if (startScrollOffset != i) {
                onScrollChanged(0, i, 0, startScrollOffset);
            }
        }
    }

    @Override // android.view.View
    protected int computeVerticalScrollOffset() {
        return this.mCurrentScrollOffset;
    }

    @Override // android.view.View
    protected int computeVerticalScrollRange() {
        return ((this.mMaxValue - this.mMinValue) + 1) * this.mSelectorElementHeight;
    }

    @Override // android.view.View
    protected int computeVerticalScrollExtent() {
        return getHeight();
    }

    @Override // android.view.View
    public int getSolidColor() {
        return this.mSolidColor;
    }

    public void setOnValueChangedListener(OnValueChangeListener onValueChangedListener) {
        this.mOnValueChangeListener = onValueChangedListener;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
    }

    public void setFormatter(Formatter formatter) {
        if (formatter == this.mFormatter) {
            return;
        }
        this.mFormatter = formatter;
        initializeSelectorWheelIndices();
    }

    public void setValue(int value) {
        setValueInternal(value, false);
    }

    @Override // android.view.View
    public boolean performClick() {
        return super.performClick();
    }

    @Override // android.view.View
    public boolean performLongClick() {
        return super.performLongClick();
    }

    private void tryComputeMaxWidth() {
        if (!this.mComputeMaxWidth) {
            return;
        }
        int maxTextWidth = 0;
        String[] strArr = this.mDisplayedValues;
        if (strArr == null) {
            float maxDigitWidth = 0.0f;
            for (int i = 0; i <= 9; i++) {
                float digitWidth = this.mSelectorWheelPaint.measureText(formatNumberWithLocale(i));
                if (digitWidth > maxDigitWidth) {
                    maxDigitWidth = digitWidth;
                }
            }
            int numberOfDigits = 0;
            for (int current = this.mMaxValue; current > 0; current /= 10) {
                numberOfDigits++;
            }
            maxTextWidth = (int) (numberOfDigits * maxDigitWidth);
        } else {
            int length = strArr.length;
            for (String mDisplayedValue : strArr) {
                float textWidth = this.mSelectorWheelPaint.measureText(mDisplayedValue);
                if (textWidth > maxTextWidth) {
                    maxTextWidth = (int) textWidth;
                }
            }
        }
        if (this.mMaxWidth != maxTextWidth) {
            int i2 = this.mMinWidth;
            if (maxTextWidth > i2) {
                this.mMaxWidth = maxTextWidth;
            } else {
                this.mMaxWidth = i2;
            }
            invalidate();
        }
    }

    public boolean getWrapSelectorWheel() {
        return this.mWrapSelectorWheel;
    }

    public void setWrapSelectorWheel(boolean wrapSelectorWheel) {
        this.mWrapSelectorWheelPreferred = wrapSelectorWheel;
        updateWrapSelectorWheel();
    }

    private void updateWrapSelectorWheel() {
        boolean z = true;
        boolean wrappingAllowed = (this.mMaxValue - this.mMinValue) + 1 >= this.mSelectorIndices.length;
        if (!wrappingAllowed || !this.mWrapSelectorWheelPreferred) {
            z = false;
        }
        this.mWrapSelectorWheel = z;
    }

    public void setOnLongPressUpdateInterval(long intervalMillis) {
        this.mLongPressUpdateInterval = intervalMillis;
    }

    public int getValue() {
        return this.mValue;
    }

    public int getMinValue() {
        return this.mMinValue;
    }

    public void setMinValue(int minValue) {
        if (this.mMinValue == minValue) {
            return;
        }
        if (minValue < 0) {
            throw new IllegalArgumentException("minValue must be >= 0");
        }
        this.mMinValue = minValue;
        int i = this.mMinValue;
        if (i > this.mValue) {
            this.mValue = i;
        }
        updateWrapSelectorWheel();
        invalidate();
    }

    public int getMaxValue() {
        return this.mMaxValue;
    }

    public void setMaxValue(int maxValue) {
        if (this.mMaxValue == maxValue) {
            return;
        }
        if (maxValue < 0) {
            throw new IllegalArgumentException("maxValue must be >= 0");
        }
        this.mMaxValue = maxValue;
        int i = this.mMaxValue;
        if (i < this.mValue) {
            this.mValue = i;
        }
        updateWrapSelectorWheel();
        invalidate();
    }

    public String[] getDisplayedValues() {
        return this.mDisplayedValues;
    }

    public void setDisplayedValues(String[] displayedValues) {
        if (this.mDisplayedValues == displayedValues) {
            return;
        }
        this.mDisplayedValues = displayedValues;
        initializeSelectorWheelIndices();
        tryComputeMaxWidth();
    }

    public CharSequence getDisplayedValueForCurrentSelection() {
        return this.mSelectorIndexToStringCache.get(getValue());
    }

    @Override // android.view.View
    protected float getTopFadingEdgeStrength() {
        return TOP_AND_BOTTOM_FADING_EDGE_STRENGTH;
    }

    @Override // android.view.View
    protected float getBottomFadingEdgeStrength() {
        return TOP_AND_BOTTOM_FADING_EDGE_STRENGTH;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XLinearLayout, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeAllCallbacks();
    }

    @Override // android.view.ViewGroup, android.view.View
    @CallSuper
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable selectionDivider = this.mSelectionDivider;
        if (selectionDivider != null && selectionDivider.isStateful() && selectionDivider.setState(getDrawableState())) {
            invalidateDrawable(selectionDivider);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    @CallSuper
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.mSelectionDivider;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onDraw(Canvas canvas) {
        Drawable drawable;
        int i;
        float x;
        float x2;
        boolean showSelectorWheel = !this.mHideWheelUntilFocused || hasFocus();
        float y = this.mCurrentScrollOffset;
        int[] selectorIndices = this.mSelectorIndices;
        for (int i2 = 0; i2 < selectorIndices.length; i2++) {
            int selectorIndex = selectorIndices[i2];
            String scrollSelectorValue = this.mSelectorIndexToStringCache.get(selectorIndex);
            if (showSelectorWheel) {
                if (i2 == 2) {
                    int color = this.mTextColors.getColorForState(SELECTED_STATE_SET, -16777216);
                    this.mSelectorWheelPaint.setColor(color);
                    this.mSelectorWheelPaint.setTextSize(this.mSelectedTextSize);
                    int i3 = this.mTextLayout;
                    if (i3 == 0) {
                        x2 = this.mTextLayoutMargin;
                    } else if (i3 != 2) {
                        x2 = (getRight() - getLeft()) / 2.0f;
                    } else {
                        x2 = (getRight() - getLeft()) - this.mTextLayoutMargin;
                    }
                    canvas.drawText(scrollSelectorValue, x2, y, this.mSelectorWheelPaint);
                } else {
                    int color2 = this.mTextColors.getDefaultColor();
                    this.mSelectorWheelPaint.setColor(color2);
                    this.mSelectorWheelPaint.setTextSize(this.mTextSize);
                    int i4 = this.mTextLayout;
                    if (i4 == 0) {
                        x = this.mTextLayoutMargin;
                    } else if (i4 != 2) {
                        x = (getRight() - getLeft()) / 2.0f;
                    } else {
                        x = (getRight() - getLeft()) - this.mTextLayoutMargin;
                    }
                    canvas.drawText(scrollSelectorValue, x, y, this.mSelectorWheelPaint);
                }
            }
            if (i2 == 1) {
                i = this.mSelectedSelectorElementHeight;
            } else {
                i = this.mSelectorElementHeight;
            }
            y += i;
        }
        if (showSelectorWheel && (drawable = this.mSelectionDivider) != null) {
            int topOfTopDivider = this.mTopSelectionDividerTop;
            int bottomOfTopDivider = this.mSelectionDividerHeight + topOfTopDivider;
            drawable.setBounds(0, topOfTopDivider, this.mWidth, bottomOfTopDivider);
            this.mSelectionDivider.draw(canvas);
            int bottomOfBottomDivider = this.mBottomSelectionDividerBottom;
            int topOfBottomDivider = bottomOfBottomDivider - this.mSelectionDividerHeight;
            this.mSelectionDivider.setBounds(0, topOfBottomDivider, this.mWidth, bottomOfBottomDivider);
            this.mSelectionDivider.draw(canvas);
            Drawable drawable2 = this.mSymbol;
            if (drawable2 != null) {
                int topOfTopSymbol = ((bottomOfTopDivider * 2) + topOfBottomDivider) / 3;
                int bottomOfBottomSymbol = ((topOfBottomDivider * 2) + bottomOfTopDivider) / 3;
                int i5 = this.mTextLayout;
                if (i5 != 0) {
                    if (i5 == 2) {
                        drawable2.setBounds(35, topOfTopSymbol, 41, topOfTopSymbol + 6);
                        this.mSymbol.draw(canvas);
                        this.mSymbol.setBounds(35, bottomOfBottomSymbol - 6, 41, bottomOfBottomSymbol);
                        this.mSymbol.draw(canvas);
                        return;
                    }
                    return;
                }
                int i6 = this.mWidth;
                drawable2.setBounds(i6 - 6, topOfTopSymbol, i6, topOfTopSymbol + 6);
                this.mSymbol.draw(canvas);
                Drawable drawable3 = this.mSymbol;
                int i7 = this.mWidth;
                drawable3.setBounds(i7 - 6, bottomOfBottomSymbol - 6, i7, bottomOfBottomSymbol);
                this.mSymbol.draw(canvas);
            }
        }
    }

    private int makeMeasureSpec(int measureSpec, int maxSize) {
        if (maxSize == -1) {
            return measureSpec;
        }
        int size = View.MeasureSpec.getSize(measureSpec);
        int mode = View.MeasureSpec.getMode(measureSpec);
        if (mode != Integer.MIN_VALUE) {
            if (mode != 0) {
                if (mode == 1073741824) {
                    return measureSpec;
                }
                throw new IllegalArgumentException("Unknown measure mode: " + mode);
            }
            return View.MeasureSpec.makeMeasureSpec(maxSize, 1073741824);
        }
        return View.MeasureSpec.makeMeasureSpec(Math.min(size, maxSize), 1073741824);
    }

    private int resolveSizeAndStateRespectingMinSize(int minSize, int measuredSize, int measureSpec) {
        if (minSize != -1) {
            int desiredWidth = Math.max(minSize, measuredSize);
            return resolveSizeAndState(desiredWidth, measureSpec, 0);
        }
        return measuredSize;
    }

    private void initializeSelectorWheelIndices() {
        this.mSelectorIndexToStringCache.clear();
        int[] selectorIndices = this.mSelectorIndices;
        int current = getValue();
        for (int i = 0; i < this.mSelectorIndices.length; i++) {
            int selectorIndex = (i - 2) + current;
            if (this.mWrapSelectorWheel) {
                selectorIndex = getWrappedSelectorIndex(selectorIndex);
            }
            selectorIndices[i] = selectorIndex;
            ensureCachedScrollSelectorValue(selectorIndices[i]);
        }
    }

    private void setValueInternal(int current, boolean notifyChange) {
        int current2;
        if (this.mValue == current) {
            return;
        }
        if (this.mWrapSelectorWheel) {
            current2 = getWrappedSelectorIndex(current);
        } else {
            current2 = Math.min(Math.max(current, this.mMinValue), this.mMaxValue);
        }
        int previous = this.mValue;
        this.mValue = current2;
        if (notifyChange) {
            XSoundEffectManager.get().play(2);
            notifyChange(previous, current2);
        }
        initializeSelectorWheelIndices();
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeValueByOne(boolean increment) {
        if (!moveToFinalScrollerPosition(this.mFlingScroller)) {
            moveToFinalScrollerPosition(this.mAdjustScroller);
        }
        this.mPreviousScrollerY = 0;
        if (increment) {
            this.mFlingScroller.startScroll(0, 0, 0, -this.mSelectorElementHeight, 300);
        } else {
            this.mFlingScroller.startScroll(0, 0, 0, this.mSelectorElementHeight, 300);
        }
        invalidate();
    }

    private void initializeSelectorWheel() {
        initializeSelectorWheelIndices();
        int[] selectorIndices = this.mSelectorIndices;
        int totalTextHeight = ((selectorIndices.length - 1) * this.mTextSize) + this.mSelectedTextSize;
        float totalTextGapHeight = (getBottom() - getTop()) - totalTextHeight;
        int textGapCount = selectorIndices.length;
        this.mSelectorTextGapHeight = (int) ((totalTextGapHeight / textGapCount) + 0.5f);
        int i = this.mTextSize;
        int i2 = this.mSelectorTextGapHeight;
        this.mSelectorElementHeight = i + i2;
        this.mSelectedSelectorElementHeight = this.mSelectedTextSize + i2;
        this.mInitialScrollOffset = i2 + (i / 2);
        this.mCurrentScrollOffset = this.mInitialScrollOffset;
    }

    private void initializeFadingEdges() {
        setVerticalFadingEdgeEnabled(true);
        setFadingEdgeLength(((getBottom() - getTop()) - this.mTextSize) / 2);
    }

    private void onScrollerFinished(Scroller scroller) {
        if (scroller == this.mFlingScroller) {
            ensureScrollWheelAdjusted();
            onScrollStateChange(0);
        }
    }

    private void onScrollStateChange(int scrollState) {
        if (this.mScrollState == scrollState) {
            return;
        }
        this.mScrollState = scrollState;
        OnScrollListener onScrollListener = this.mOnScrollListener;
        if (onScrollListener != null) {
            onScrollListener.onScrollStateChange(this, scrollState);
        }
    }

    private void fling(int velocityY) {
        this.mPreviousScrollerY = 0;
        if (velocityY > 0) {
            this.mFlingScroller.fling(0, 0, 0, velocityY, 0, 0, 0, Integer.MAX_VALUE);
        } else {
            this.mFlingScroller.fling(0, Integer.MAX_VALUE, 0, velocityY, 0, 0, 0, Integer.MAX_VALUE);
        }
        invalidate();
    }

    private int getWrappedSelectorIndex(int selectorIndex) {
        int i = this.mMaxValue;
        if (selectorIndex > i) {
            int i2 = this.mMinValue;
            return (i2 + ((selectorIndex - i) % (i - i2))) - 1;
        }
        int i3 = this.mMinValue;
        if (selectorIndex < i3) {
            return (i - ((i3 - selectorIndex) % (i - i3))) + 1;
        }
        return selectorIndex;
    }

    private void incrementSelectorIndices(int[] selectorIndices) {
        if (selectorIndices.length - 1 >= 0) {
            System.arraycopy(selectorIndices, 1, selectorIndices, 0, selectorIndices.length - 1);
        }
        int nextScrollSelectorIndex = selectorIndices[selectorIndices.length - 2] + 1;
        if (this.mWrapSelectorWheel && nextScrollSelectorIndex > this.mMaxValue) {
            nextScrollSelectorIndex = this.mMinValue;
        }
        selectorIndices[selectorIndices.length - 1] = nextScrollSelectorIndex;
        ensureCachedScrollSelectorValue(nextScrollSelectorIndex);
    }

    private void decrementSelectorIndices(int[] selectorIndices) {
        if (selectorIndices.length - 1 >= 0) {
            System.arraycopy(selectorIndices, 0, selectorIndices, 1, selectorIndices.length - 1);
        }
        int nextScrollSelectorIndex = selectorIndices[1] - 1;
        if (this.mWrapSelectorWheel && nextScrollSelectorIndex < this.mMinValue) {
            nextScrollSelectorIndex = this.mMaxValue;
        }
        selectorIndices[0] = nextScrollSelectorIndex;
        ensureCachedScrollSelectorValue(nextScrollSelectorIndex);
    }

    private void ensureCachedScrollSelectorValue(int selectorIndex) {
        String scrollSelectorValue;
        SparseArray<String> cache = this.mSelectorIndexToStringCache;
        String scrollSelectorValue2 = cache.get(selectorIndex);
        if (scrollSelectorValue2 != null) {
            return;
        }
        int i = this.mMinValue;
        if (selectorIndex < i || selectorIndex > this.mMaxValue) {
            scrollSelectorValue = "";
        } else {
            String[] strArr = this.mDisplayedValues;
            if (strArr != null) {
                int displayedValueIndex = selectorIndex - i;
                scrollSelectorValue = strArr[displayedValueIndex];
            } else {
                scrollSelectorValue = formatNumber(selectorIndex);
            }
        }
        cache.put(selectorIndex, scrollSelectorValue);
    }

    private String formatNumber(int value) {
        Formatter formatter = this.mFormatter;
        return formatter != null ? formatter.format(value) : formatNumberWithLocale(value);
    }

    private void notifyChange(int previous, int current) {
        OnValueChangeListener onValueChangeListener = this.mOnValueChangeListener;
        if (onValueChangeListener != null) {
            onValueChangeListener.onValueChange(this, previous, this.mValue);
        }
    }

    private void postChangeCurrentByOneFromLongPress(boolean increment, long delayMillis) {
        ChangeCurrentByOneFromLongPressCommand changeCurrentByOneFromLongPressCommand = this.mChangeCurrentByOneFromLongPressCommand;
        if (changeCurrentByOneFromLongPressCommand == null) {
            this.mChangeCurrentByOneFromLongPressCommand = new ChangeCurrentByOneFromLongPressCommand();
        } else {
            removeCallbacks(changeCurrentByOneFromLongPressCommand);
        }
        this.mChangeCurrentByOneFromLongPressCommand.setStep(increment);
        postDelayed(this.mChangeCurrentByOneFromLongPressCommand, delayMillis);
    }

    private void removeChangeCurrentByOneFromLongPress() {
        ChangeCurrentByOneFromLongPressCommand changeCurrentByOneFromLongPressCommand = this.mChangeCurrentByOneFromLongPressCommand;
        if (changeCurrentByOneFromLongPressCommand != null) {
            removeCallbacks(changeCurrentByOneFromLongPressCommand);
        }
    }

    private void postBeginSoftInputOnLongPressCommand() {
        BeginSoftInputOnLongPressCommand beginSoftInputOnLongPressCommand = this.mBeginSoftInputOnLongPressCommand;
        if (beginSoftInputOnLongPressCommand == null) {
            this.mBeginSoftInputOnLongPressCommand = new BeginSoftInputOnLongPressCommand();
        } else {
            removeCallbacks(beginSoftInputOnLongPressCommand);
        }
        postDelayed(this.mBeginSoftInputOnLongPressCommand, ViewConfiguration.getLongPressTimeout());
    }

    private void removeBeginSoftInputCommand() {
        BeginSoftInputOnLongPressCommand beginSoftInputOnLongPressCommand = this.mBeginSoftInputOnLongPressCommand;
        if (beginSoftInputOnLongPressCommand != null) {
            removeCallbacks(beginSoftInputOnLongPressCommand);
        }
    }

    private void removeAllCallbacks() {
        ChangeCurrentByOneFromLongPressCommand changeCurrentByOneFromLongPressCommand = this.mChangeCurrentByOneFromLongPressCommand;
        if (changeCurrentByOneFromLongPressCommand != null) {
            removeCallbacks(changeCurrentByOneFromLongPressCommand);
        }
        BeginSoftInputOnLongPressCommand beginSoftInputOnLongPressCommand = this.mBeginSoftInputOnLongPressCommand;
        if (beginSoftInputOnLongPressCommand != null) {
            removeCallbacks(beginSoftInputOnLongPressCommand);
        }
        this.mPressedStateHelper.cancel();
    }

    private int getSelectedPos(String value) {
        if (this.mDisplayedValues == null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
            }
        } else {
            for (int i = 0; i < this.mDisplayedValues.length; i++) {
                value = value.toLowerCase();
                if (this.mDisplayedValues[i].toLowerCase().startsWith(value)) {
                    return this.mMinValue + i;
                }
            }
            try {
                int i2 = Integer.parseInt(value);
                return i2;
            } catch (NumberFormatException e2) {
            }
        }
        return this.mMinValue;
    }

    private boolean ensureScrollWheelAdjusted() {
        int deltaY = this.mInitialScrollOffset - this.mCurrentScrollOffset;
        if (deltaY == 0) {
            return false;
        }
        this.mPreviousScrollerY = 0;
        int abs = Math.abs(deltaY);
        int i = this.mSelectorElementHeight;
        if (abs > i / 2) {
            if (deltaY > 0) {
                i = -i;
            }
            deltaY += i;
        }
        this.mAdjustScroller.startScroll(0, 0, 0, deltaY, 800);
        invalidate();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes25.dex */
    public class PressedStateHelper implements Runnable {
        public static final int BUTTON_DECREMENT = 2;
        public static final int BUTTON_INCREMENT = 1;
        private final int MODE_PRESS = 1;
        private final int MODE_TAPPED = 2;
        private int mManagedButton;
        private int mMode;

        PressedStateHelper() {
        }

        public void cancel() {
            this.mMode = 0;
            this.mManagedButton = 0;
            XNumberPicker.this.removeCallbacks(this);
        }

        public void buttonPressDelayed(int button) {
            cancel();
            this.mMode = 1;
            this.mManagedButton = button;
            XNumberPicker.this.postDelayed(this, ViewConfiguration.getTapTimeout());
        }

        public void buttonTapped(int button) {
            cancel();
            this.mMode = 2;
            this.mManagedButton = button;
            XNumberPicker.this.post(this);
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.mMode == 1) {
                int i = this.mManagedButton;
                if (i == 1) {
                    XNumberPicker xNumberPicker = XNumberPicker.this;
                    xNumberPicker.invalidate(0, xNumberPicker.mBottomSelectionDividerBottom, XNumberPicker.this.getRight(), XNumberPicker.this.getBottom());
                } else if (i == 2) {
                    XNumberPicker xNumberPicker2 = XNumberPicker.this;
                    xNumberPicker2.invalidate(0, 0, xNumberPicker2.getRight(), XNumberPicker.this.mTopSelectionDividerTop);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes25.dex */
    public class ChangeCurrentByOneFromLongPressCommand implements Runnable {
        private boolean mIncrement;

        ChangeCurrentByOneFromLongPressCommand() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setStep(boolean increment) {
            this.mIncrement = increment;
        }

        @Override // java.lang.Runnable
        public void run() {
            XNumberPicker.this.changeValueByOne(this.mIncrement);
            XNumberPicker xNumberPicker = XNumberPicker.this;
            xNumberPicker.postDelayed(this, xNumberPicker.mLongPressUpdateInterval);
        }
    }

    /* loaded from: classes25.dex */
    public static class CustomEditText extends AppCompatEditText {
        public CustomEditText(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override // android.widget.TextView
        public void onEditorAction(int actionCode) {
            super.onEditorAction(actionCode);
            if (actionCode == 6) {
                clearFocus();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes25.dex */
    public class BeginSoftInputOnLongPressCommand implements Runnable {
        BeginSoftInputOnLongPressCommand() {
        }

        @Override // java.lang.Runnable
        public void run() {
            XNumberPicker.this.performLongClick();
        }
    }

    private static String formatNumberWithLocale(int value) {
        return String.format(Locale.getDefault(), "%d", Integer.valueOf(value));
    }
}
