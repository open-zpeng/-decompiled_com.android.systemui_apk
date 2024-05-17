package com.xiaopeng.systemui.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import androidx.core.widget.ScrollerCompat;
import com.android.systemui.R;
/* loaded from: classes24.dex */
public class ValuePicker extends View {
    private static final boolean DEFAULT_CURRENT_ITEM_INDEX_EFFECT = false;
    private static final int DEFAULT_DIVIDER_COLOR = -695533;
    private static final int DEFAULT_DIVIDER_HEIGHT = 2;
    private static final int DEFAULT_DIVIDER_MARGIN_HORIZONTAL = 0;
    private static final int DEFAULT_INTERVAL_REVISE_DURATION = 300;
    private static final int DEFAULT_ITEM_PADDING_DP_H = 5;
    private static final int DEFAULT_ITEM_PADDING_DP_V = 2;
    private static final int DEFAULT_MARGIN_END_OF_HINT_DP = 8;
    private static final int DEFAULT_MARGIN_START_OF_HINT_DP = 8;
    private static final int DEFAULT_MAX_SCROLL_BY_INDEX_DURATION = 600;
    private static final int DEFAULT_MIN_SCROLL_BY_INDEX_DURATION = 300;
    private static final boolean DEFAULT_RESPOND_CHANGE_IN_MAIN_THREAD = true;
    private static final boolean DEFAULT_RESPOND_CHANGE_ON_DETACH = false;
    private static final int DEFAULT_SHOW_COUNT = 3;
    private static final boolean DEFAULT_SHOW_DIVIDER = true;
    private static final int DEFAULT_TEXT_COLOR_NORMAL = -13421773;
    private static final int DEFAULT_TEXT_COLOR_SELECTED = -695533;
    private static final int DEFAULT_TEXT_SIZE_HINT_SP = 14;
    private static final int DEFAULT_TEXT_SIZE_NORMAL_SP = 14;
    private static final int DEFAULT_TEXT_SIZE_SELECTED_SP = 16;
    private static final boolean DEFAULT_WRAP_SELECTOR_WHEEL = true;
    private static final int HANDLER_INTERVAL_REFRESH = 32;
    private static final int HANDLER_WHAT_LISTENER_VALUE_CHANGED = 2;
    private static final int HANDLER_WHAT_REFRESH = 1;
    private static final int HANDLER_WHAT_REQUEST_LAYOUT = 3;
    private static final String TAG = ValuePicker.class.getSimpleName();
    private static final String TEXT_ELLIPSIZE_END = "end";
    private static final String TEXT_ELLIPSIZE_MIDDLE = "middle";
    private static final String TEXT_ELLIPSIZE_START = "start";
    private float currY;
    private float dividerY0;
    private float dividerY1;
    private float downY;
    private float downYGlobal;
    private String mAlterHint;
    private CharSequence[] mAlterTextArrayWithMeasureHint;
    private CharSequence[] mAlterTextArrayWithoutMeasureHint;
    private int mCurrDrawFirstItemIndex;
    private int mCurrDrawFirstItemY;
    private int mCurrDrawGlobalY;
    private boolean mCurrentItemIndexEffect;
    private String[] mDisplayedValues;
    private int mDividerColor;
    private int mDividerHeight;
    private int mDividerIndex0;
    private int mDividerIndex1;
    private int mDividerMarginL;
    private int mDividerMarginR;
    private String mEmptyItemHint;
    private boolean mFlagMayPress;
    private float mFriction;
    private Handler mHandlerInMainThread;
    private Handler mHandlerInNewThread;
    private HandlerThread mHandlerThread;
    private boolean mHasInit;
    private String mHintText;
    private int mInScrollingPickedNewValue;
    private int mInScrollingPickedOldValue;
    private int mItemHeight;
    private int mItemPaddingHorizontal;
    private int mItemPaddingVertical;
    private int mMarginEndOfHint;
    private int mMarginStartOfHint;
    private int mMaxHeightOfDisplayedValues;
    private int mMaxShowIndex;
    private int mMaxValue;
    private int mMaxWidthOfAlterArrayWithMeasureHint;
    private int mMaxWidthOfAlterArrayWithoutMeasureHint;
    private int mMaxWidthOfDisplayedValues;
    private int mMinShowIndex;
    private int mMinValue;
    private int mMiniVelocityFling;
    private int mNotWrapLimitYBottom;
    private int mNotWrapLimitYTop;
    private OnScrollListener mOnScrollListener;
    private OnValueChangeListener mOnValueChangeListener;
    private OnValueChangeListenerInScrolling mOnValueChangeListenerInScrolling;
    private OnValueChangeListenerRelativeToRaw mOnValueChangeListenerRaw;
    private Paint mPaintDivider;
    private Paint mPaintHint;
    private TextPaint mPaintText;
    private boolean mPendingWrapToLinear;
    private int mPrevPickedIndex;
    private boolean mRespondChangeInMainThread;
    private boolean mRespondChangeOnDetach;
    private int mScaledTouchSlop;
    private int mScrollState;
    private ScrollerCompat mScroller;
    private int mShadowDirection;
    private int mShowCount;
    private boolean mShowDivider;
    private int mSpecModeH;
    private int mSpecModeW;
    private int mTextColorHint;
    private int mTextColorNormal;
    private int mTextColorSelected;
    private String mTextEllipsize;
    private int mTextSizeHint;
    private float mTextSizeHintCenterYOffset;
    private int mTextSizeNormal;
    private float mTextSizeNormalCenterYOffset;
    private int mTextSizeSelected;
    private float mTextSizeSelectedCenterYOffset;
    private VelocityTracker mVelocityTracker;
    private float mViewCenterX;
    private int mViewHeight;
    private int mViewWidth;
    private int mWidthOfAlterHint;
    private int mWidthOfHintText;
    private boolean mWrapSelectorWheel;
    private boolean mWrapSelectorWheelCheck;

    /* loaded from: classes24.dex */
    public interface OnScrollListener {
        public static final int SCROLL_STATE_FLING = 2;
        public static final int SCROLL_STATE_IDLE = 0;
        public static final int SCROLL_STATE_TOUCH_SCROLL = 1;

        void onScrollStateChange(ValuePicker valuePicker, int i);
    }

    /* loaded from: classes24.dex */
    public interface OnValueChangeListener {
        void onTouchDown();

        void onTouchUp();

        void onValueChange(ValuePicker valuePicker, int i, int i2);
    }

    /* loaded from: classes24.dex */
    public interface OnValueChangeListenerInScrolling {
        void onValueChangeInScrolling(ValuePicker valuePicker, int i, int i2);
    }

    /* loaded from: classes24.dex */
    public interface OnValueChangeListenerRelativeToRaw {
        void onValueChangeRelativeToRaw(ValuePicker valuePicker, int i, int i2, String[] strArr);
    }

    public ValuePicker(Context context) {
        super(context);
        this.mTextColorNormal = DEFAULT_TEXT_COLOR_NORMAL;
        this.mTextColorSelected = -695533;
        this.mTextColorHint = -695533;
        this.mTextSizeNormal = 0;
        this.mTextSizeSelected = 0;
        this.mTextSizeHint = 0;
        this.mWidthOfHintText = 0;
        this.mWidthOfAlterHint = 0;
        this.mMarginStartOfHint = 0;
        this.mMarginEndOfHint = 0;
        this.mItemPaddingVertical = 0;
        this.mItemPaddingHorizontal = 0;
        this.mDividerColor = -695533;
        this.mDividerHeight = 2;
        this.mDividerMarginL = 0;
        this.mDividerMarginR = 0;
        this.mShowCount = 3;
        this.mDividerIndex0 = 0;
        this.mDividerIndex1 = 0;
        this.mMinShowIndex = -1;
        this.mMaxShowIndex = -1;
        this.mMinValue = 0;
        this.mMaxValue = 0;
        this.mMaxWidthOfDisplayedValues = 0;
        this.mMaxHeightOfDisplayedValues = 0;
        this.mMaxWidthOfAlterArrayWithMeasureHint = 0;
        this.mMaxWidthOfAlterArrayWithoutMeasureHint = 0;
        this.mPrevPickedIndex = 0;
        this.mMiniVelocityFling = 150;
        this.mScaledTouchSlop = 8;
        this.mFriction = 1.0f;
        this.mTextSizeNormalCenterYOffset = 0.0f;
        this.mTextSizeSelectedCenterYOffset = 0.0f;
        this.mTextSizeHintCenterYOffset = 0.0f;
        this.mShowDivider = true;
        this.mWrapSelectorWheel = true;
        this.mCurrentItemIndexEffect = false;
        this.mHasInit = false;
        this.mWrapSelectorWheelCheck = true;
        this.mPendingWrapToLinear = false;
        this.mRespondChangeOnDetach = false;
        this.mRespondChangeInMainThread = true;
        this.mPaintDivider = new Paint();
        this.mPaintText = new TextPaint();
        this.mPaintHint = new Paint();
        this.mScrollState = 0;
        this.downYGlobal = 0.0f;
        this.downY = 0.0f;
        this.currY = 0.0f;
        this.mFlagMayPress = false;
        this.mCurrDrawFirstItemIndex = 0;
        this.mCurrDrawFirstItemY = 0;
        this.mCurrDrawGlobalY = 0;
        this.mSpecModeW = 0;
        this.mSpecModeH = 0;
        init(context);
    }

    public ValuePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTextColorNormal = DEFAULT_TEXT_COLOR_NORMAL;
        this.mTextColorSelected = -695533;
        this.mTextColorHint = -695533;
        this.mTextSizeNormal = 0;
        this.mTextSizeSelected = 0;
        this.mTextSizeHint = 0;
        this.mWidthOfHintText = 0;
        this.mWidthOfAlterHint = 0;
        this.mMarginStartOfHint = 0;
        this.mMarginEndOfHint = 0;
        this.mItemPaddingVertical = 0;
        this.mItemPaddingHorizontal = 0;
        this.mDividerColor = -695533;
        this.mDividerHeight = 2;
        this.mDividerMarginL = 0;
        this.mDividerMarginR = 0;
        this.mShowCount = 3;
        this.mDividerIndex0 = 0;
        this.mDividerIndex1 = 0;
        this.mMinShowIndex = -1;
        this.mMaxShowIndex = -1;
        this.mMinValue = 0;
        this.mMaxValue = 0;
        this.mMaxWidthOfDisplayedValues = 0;
        this.mMaxHeightOfDisplayedValues = 0;
        this.mMaxWidthOfAlterArrayWithMeasureHint = 0;
        this.mMaxWidthOfAlterArrayWithoutMeasureHint = 0;
        this.mPrevPickedIndex = 0;
        this.mMiniVelocityFling = 150;
        this.mScaledTouchSlop = 8;
        this.mFriction = 1.0f;
        this.mTextSizeNormalCenterYOffset = 0.0f;
        this.mTextSizeSelectedCenterYOffset = 0.0f;
        this.mTextSizeHintCenterYOffset = 0.0f;
        this.mShowDivider = true;
        this.mWrapSelectorWheel = true;
        this.mCurrentItemIndexEffect = false;
        this.mHasInit = false;
        this.mWrapSelectorWheelCheck = true;
        this.mPendingWrapToLinear = false;
        this.mRespondChangeOnDetach = false;
        this.mRespondChangeInMainThread = true;
        this.mPaintDivider = new Paint();
        this.mPaintText = new TextPaint();
        this.mPaintHint = new Paint();
        this.mScrollState = 0;
        this.downYGlobal = 0.0f;
        this.downY = 0.0f;
        this.currY = 0.0f;
        this.mFlagMayPress = false;
        this.mCurrDrawFirstItemIndex = 0;
        this.mCurrDrawFirstItemY = 0;
        this.mCurrDrawGlobalY = 0;
        this.mSpecModeW = 0;
        this.mSpecModeH = 0;
        initAttr(context, attrs);
        init(context);
    }

    public ValuePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTextColorNormal = DEFAULT_TEXT_COLOR_NORMAL;
        this.mTextColorSelected = -695533;
        this.mTextColorHint = -695533;
        this.mTextSizeNormal = 0;
        this.mTextSizeSelected = 0;
        this.mTextSizeHint = 0;
        this.mWidthOfHintText = 0;
        this.mWidthOfAlterHint = 0;
        this.mMarginStartOfHint = 0;
        this.mMarginEndOfHint = 0;
        this.mItemPaddingVertical = 0;
        this.mItemPaddingHorizontal = 0;
        this.mDividerColor = -695533;
        this.mDividerHeight = 2;
        this.mDividerMarginL = 0;
        this.mDividerMarginR = 0;
        this.mShowCount = 3;
        this.mDividerIndex0 = 0;
        this.mDividerIndex1 = 0;
        this.mMinShowIndex = -1;
        this.mMaxShowIndex = -1;
        this.mMinValue = 0;
        this.mMaxValue = 0;
        this.mMaxWidthOfDisplayedValues = 0;
        this.mMaxHeightOfDisplayedValues = 0;
        this.mMaxWidthOfAlterArrayWithMeasureHint = 0;
        this.mMaxWidthOfAlterArrayWithoutMeasureHint = 0;
        this.mPrevPickedIndex = 0;
        this.mMiniVelocityFling = 150;
        this.mScaledTouchSlop = 8;
        this.mFriction = 1.0f;
        this.mTextSizeNormalCenterYOffset = 0.0f;
        this.mTextSizeSelectedCenterYOffset = 0.0f;
        this.mTextSizeHintCenterYOffset = 0.0f;
        this.mShowDivider = true;
        this.mWrapSelectorWheel = true;
        this.mCurrentItemIndexEffect = false;
        this.mHasInit = false;
        this.mWrapSelectorWheelCheck = true;
        this.mPendingWrapToLinear = false;
        this.mRespondChangeOnDetach = false;
        this.mRespondChangeInMainThread = true;
        this.mPaintDivider = new Paint();
        this.mPaintText = new TextPaint();
        this.mPaintHint = new Paint();
        this.mScrollState = 0;
        this.downYGlobal = 0.0f;
        this.downY = 0.0f;
        this.currY = 0.0f;
        this.mFlagMayPress = false;
        this.mCurrDrawFirstItemIndex = 0;
        this.mCurrDrawFirstItemY = 0;
        this.mCurrDrawGlobalY = 0;
        this.mSpecModeW = 0;
        this.mSpecModeH = 0;
        initAttr(context, attrs);
        init(context);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ValuePicker);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == 17) {
                this.mShowCount = a.getInt(attr, 3);
            } else if (attr == 3) {
                this.mDividerColor = a.getColor(attr, -695533);
            } else if (attr == 4) {
                this.mDividerHeight = a.getDimensionPixelSize(attr, 2);
            } else if (attr == 5) {
                this.mDividerMarginL = a.getDimensionPixelSize(attr, 0);
            } else if (attr == 6) {
                this.mDividerMarginR = a.getDimensionPixelSize(attr, 0);
            } else if (attr == 19) {
                this.mDisplayedValues = convertCharSequenceArrayToStringArray(a.getTextArray(attr));
            } else if (attr == 21) {
                this.mTextColorNormal = a.getColor(attr, DEFAULT_TEXT_COLOR_NORMAL);
            } else if (attr == 22) {
                this.mTextColorSelected = a.getColor(attr, -695533);
            } else if (attr == 20) {
                this.mTextColorHint = a.getColor(attr, -695533);
            } else if (attr == 25) {
                this.mTextSizeNormal = a.getDimensionPixelSize(attr, sp2px(context, 14.0f));
            } else if (attr == 26) {
                this.mTextSizeSelected = a.getDimensionPixelSize(attr, sp2px(context, 16.0f));
            } else if (attr == 24) {
                this.mTextSizeHint = a.getDimensionPixelSize(attr, sp2px(context, 14.0f));
            } else if (attr == 14) {
                this.mMinValue = a.getInteger(attr, 0);
            } else if (attr == 13) {
                this.mMaxValue = a.getInteger(attr, 0);
            } else if (attr == 27) {
                this.mWrapSelectorWheel = a.getBoolean(attr, true);
            } else if (attr == 18) {
                this.mShowDivider = a.getBoolean(attr, true);
            } else if (attr == 8) {
                this.mHintText = a.getString(attr);
            } else if (attr == 0) {
                this.mAlterHint = a.getString(attr);
            } else if (attr == 7) {
                this.mEmptyItemHint = a.getString(attr);
            } else if (attr == 12) {
                this.mMarginStartOfHint = a.getDimensionPixelSize(attr, dp2px(context, 8.0f));
            } else if (attr == 11) {
                this.mMarginEndOfHint = a.getDimensionPixelSize(attr, dp2px(context, 8.0f));
            } else if (attr == 10) {
                this.mItemPaddingVertical = a.getDimensionPixelSize(attr, dp2px(context, 2.0f));
            } else if (attr == 9) {
                this.mItemPaddingHorizontal = a.getDimensionPixelSize(attr, dp2px(context, 5.0f));
            } else if (attr == 1) {
                this.mAlterTextArrayWithMeasureHint = a.getTextArray(attr);
            } else if (attr == 2) {
                this.mAlterTextArrayWithoutMeasureHint = a.getTextArray(attr);
            } else if (attr == 16) {
                this.mRespondChangeOnDetach = a.getBoolean(attr, false);
            } else if (attr == 15) {
                this.mRespondChangeInMainThread = a.getBoolean(attr, true);
            } else if (attr == 23) {
                this.mTextEllipsize = a.getString(attr);
            }
        }
        a.recycle();
    }

    private void init(Context context) {
        this.mScroller = ScrollerCompat.create(context);
        this.mMiniVelocityFling = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
        this.mScaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        if (this.mTextSizeNormal == 0) {
            this.mTextSizeNormal = sp2px(context, 14.0f);
        }
        if (this.mTextSizeSelected == 0) {
            this.mTextSizeSelected = sp2px(context, 16.0f);
        }
        if (this.mTextSizeHint == 0) {
            this.mTextSizeHint = sp2px(context, 14.0f);
        }
        if (this.mMarginStartOfHint == 0) {
            this.mMarginStartOfHint = dp2px(context, 8.0f);
        }
        if (this.mMarginEndOfHint == 0) {
            this.mMarginEndOfHint = dp2px(context, 8.0f);
        }
        this.mPaintDivider.setColor(this.mDividerColor);
        this.mPaintDivider.setAntiAlias(true);
        this.mPaintDivider.setStyle(Paint.Style.STROKE);
        this.mPaintDivider.setStrokeWidth(this.mDividerHeight);
        this.mPaintText.setColor(this.mTextColorNormal);
        this.mPaintText.setAntiAlias(true);
        this.mPaintText.setTextAlign(Paint.Align.CENTER);
        this.mPaintHint.setColor(this.mTextColorHint);
        this.mPaintHint.setAntiAlias(true);
        this.mPaintHint.setTextAlign(Paint.Align.CENTER);
        this.mPaintHint.setTextSize(this.mTextSizeHint);
        int i = this.mShowCount;
        if (i % 2 == 0) {
            this.mShowCount = i + 1;
        }
        if (this.mMinShowIndex == -1 || this.mMaxShowIndex == -1) {
            updateValueForInit();
        }
        initHandler();
    }

    private void initHandler() {
        this.mHandlerThread = new HandlerThread("HandlerThread-For-Refreshing");
        this.mHandlerThread.start();
        this.mHandlerInNewThread = new Handler(this.mHandlerThread.getLooper()) { // from class: com.xiaopeng.systemui.ui.widget.ValuePicker.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                int willPickIndex;
                int i = msg.what;
                if (i != 1) {
                    if (i == 2) {
                        ValuePicker.this.respondPickedValueChanged(msg.arg1, msg.arg2, msg.obj);
                    }
                } else if (!ValuePicker.this.mScroller.isFinished()) {
                    if (ValuePicker.this.mScrollState == 0) {
                        ValuePicker.this.onScrollStateChange(1);
                    }
                    ValuePicker.this.mHandlerInNewThread.sendMessageDelayed(ValuePicker.this.getMsg(1, 0, 0, msg.obj), 32L);
                } else {
                    int duration = 0;
                    if (ValuePicker.this.mCurrDrawFirstItemY != 0) {
                        if (ValuePicker.this.mScrollState == 0) {
                            ValuePicker.this.onScrollStateChange(1);
                        }
                        if (ValuePicker.this.mCurrDrawFirstItemY < (-ValuePicker.this.mItemHeight) / 2) {
                            duration = (int) (((ValuePicker.this.mItemHeight + ValuePicker.this.mCurrDrawFirstItemY) * 300.0f) / ValuePicker.this.mItemHeight);
                            ValuePicker.this.mScroller.startScroll(0, ValuePicker.this.mCurrDrawGlobalY, 0, ValuePicker.this.mCurrDrawFirstItemY + ValuePicker.this.mItemHeight, duration * 3);
                            ValuePicker valuePicker = ValuePicker.this;
                            willPickIndex = valuePicker.getWillPickIndexByGlobalY(valuePicker.mCurrDrawGlobalY + ValuePicker.this.mItemHeight + ValuePicker.this.mCurrDrawFirstItemY);
                        } else {
                            duration = (int) (((-ValuePicker.this.mCurrDrawFirstItemY) * 300.0f) / ValuePicker.this.mItemHeight);
                            ValuePicker.this.mScroller.startScroll(0, ValuePicker.this.mCurrDrawGlobalY, 0, ValuePicker.this.mCurrDrawFirstItemY, duration * 3);
                            ValuePicker valuePicker2 = ValuePicker.this;
                            willPickIndex = valuePicker2.getWillPickIndexByGlobalY(valuePicker2.mCurrDrawGlobalY + ValuePicker.this.mCurrDrawFirstItemY);
                        }
                        ValuePicker.this.postInvalidate();
                    } else {
                        ValuePicker.this.onScrollStateChange(0);
                        ValuePicker valuePicker3 = ValuePicker.this;
                        willPickIndex = valuePicker3.getWillPickIndexByGlobalY(valuePicker3.mCurrDrawGlobalY);
                    }
                    ValuePicker valuePicker4 = ValuePicker.this;
                    Message changeMsg = valuePicker4.getMsg(2, valuePicker4.mPrevPickedIndex, willPickIndex, msg.obj);
                    if (ValuePicker.this.mRespondChangeInMainThread) {
                        ValuePicker.this.mHandlerInMainThread.sendMessageDelayed(changeMsg, duration * 2);
                    } else {
                        ValuePicker.this.mHandlerInNewThread.sendMessageDelayed(changeMsg, duration * 2);
                    }
                }
            }
        };
        this.mHandlerInMainThread = new Handler() { // from class: com.xiaopeng.systemui.ui.widget.ValuePicker.2
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int i = msg.what;
                if (i == 2) {
                    ValuePicker.this.respondPickedValueChanged(msg.arg1, msg.arg2, msg.obj);
                } else if (i == 3) {
                    ValuePicker.this.requestLayout();
                }
            }
        };
    }

    private void respondPickedValueChangedInScrolling(int oldVal, int newVal) {
        this.mOnValueChangeListenerInScrolling.onValueChangeInScrolling(this, oldVal, newVal);
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        updateMaxWHOfDisplayedValues(false);
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mViewWidth = w;
        this.mViewHeight = h;
        this.mItemHeight = this.mViewHeight / this.mShowCount;
        this.mViewCenterX = ((this.mViewWidth + getPaddingLeft()) - getPaddingRight()) / 2.0f;
        int defaultValue = 0;
        if (getOneRecycleSize() > 1) {
            if (this.mHasInit) {
                defaultValue = getValue() - this.mMinValue;
            } else if (this.mCurrentItemIndexEffect) {
                defaultValue = this.mCurrDrawFirstItemIndex + ((this.mShowCount - 1) / 2);
            } else {
                defaultValue = 0;
            }
        }
        correctPositionByDefaultValue(defaultValue, this.mWrapSelectorWheel && this.mWrapSelectorWheelCheck);
        updateFontAttr();
        updateNotWrapYLimit();
        updateDividerAttr();
        this.mHasInit = true;
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        HandlerThread handlerThread = this.mHandlerThread;
        if (handlerThread == null || !handlerThread.isAlive()) {
            initHandler();
        }
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mHandlerThread.quit();
        if (this.mItemHeight == 0) {
            return;
        }
        if (!this.mScroller.isFinished()) {
            this.mScroller.abortAnimation();
            this.mCurrDrawGlobalY = this.mScroller.getCurrY();
            calculateFirstItemParameterByGlobalY();
            int i = this.mCurrDrawFirstItemY;
            if (i != 0) {
                int i2 = this.mItemHeight;
                if (i < (-i2) / 2) {
                    this.mCurrDrawGlobalY = this.mCurrDrawGlobalY + i2 + i;
                } else {
                    this.mCurrDrawGlobalY += i;
                }
                calculateFirstItemParameterByGlobalY();
            }
            onScrollStateChange(0);
        }
        int currPickedIndex = getWillPickIndexByGlobalY(this.mCurrDrawGlobalY);
        int i3 = this.mPrevPickedIndex;
        if (currPickedIndex != i3 && this.mRespondChangeOnDetach) {
            try {
                if (this.mOnValueChangeListener != null) {
                    this.mOnValueChangeListener.onValueChange(this, i3 + this.mMinValue, this.mMinValue + currPickedIndex);
                }
                if (this.mOnValueChangeListenerRaw != null) {
                    this.mOnValueChangeListenerRaw.onValueChangeRelativeToRaw(this, this.mPrevPickedIndex, currPickedIndex, this.mDisplayedValues);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.mPrevPickedIndex = currPickedIndex;
    }

    public int getOneRecycleSize() {
        return (this.mMaxShowIndex - this.mMinShowIndex) + 1;
    }

    public int getRawContentSize() {
        String[] strArr = this.mDisplayedValues;
        if (strArr != null) {
            return strArr.length;
        }
        return 0;
    }

    public void setDisplayedValuesAndPickedIndex(String[] newDisplayedValues, int pickedIndex, boolean needRefresh) {
        stopScrolling();
        if (newDisplayedValues == null) {
            throw new IllegalArgumentException("newDisplayedValues should not be null.");
        }
        if (pickedIndex < 0) {
            throw new IllegalArgumentException("pickedIndex should not be negative, now pickedIndex is " + pickedIndex);
        }
        updateContent(newDisplayedValues);
        updateMaxWHOfDisplayedValues(true);
        updateNotWrapYLimit();
        updateValue();
        this.mPrevPickedIndex = this.mMinShowIndex + pickedIndex;
        correctPositionByDefaultValue(pickedIndex, this.mWrapSelectorWheel && this.mWrapSelectorWheelCheck);
        if (needRefresh) {
            this.mHandlerInNewThread.sendMessageDelayed(getMsg(1), 0L);
            postInvalidate();
        }
    }

    public void setDisplayedValues(String[] newDisplayedValues, boolean needRefresh) {
        setDisplayedValuesAndPickedIndex(newDisplayedValues, 0, needRefresh);
    }

    public void setDisplayedValues(String[] newDisplayedValues) {
        stopRefreshing();
        stopScrolling();
        if (newDisplayedValues == null) {
            throw new IllegalArgumentException("newDisplayedValues should not be null.");
        }
        boolean z = true;
        if ((this.mMaxValue - this.mMinValue) + 1 > newDisplayedValues.length) {
            throw new IllegalArgumentException("mMaxValue - mMinValue + 1 should not be greater than mDisplayedValues.length, now ((mMaxValue - mMinValue + 1) is " + ((this.mMaxValue - this.mMinValue) + 1) + " newDisplayedValues.length is " + newDisplayedValues.length + ", you need to set MaxValue and MinValue before setDisplayedValues(String[])");
        }
        updateContent(newDisplayedValues);
        updateMaxWHOfDisplayedValues(true);
        this.mPrevPickedIndex = this.mMinShowIndex + 0;
        if (!this.mWrapSelectorWheel || !this.mWrapSelectorWheelCheck) {
            z = false;
        }
        correctPositionByDefaultValue(0, z);
        postInvalidate();
        this.mHandlerInMainThread.sendEmptyMessage(3);
    }

    public String[] getDisplayedValues() {
        return this.mDisplayedValues;
    }

    public void setWrapSelectorWheel(boolean wrapSelectorWheel) {
        if (this.mWrapSelectorWheel != wrapSelectorWheel) {
            if (!wrapSelectorWheel) {
                if (this.mScrollState == 0) {
                    internalSetWrapToLinear();
                    return;
                } else {
                    this.mPendingWrapToLinear = true;
                    return;
                }
            }
            this.mWrapSelectorWheel = wrapSelectorWheel;
            updateWrapStateByContent();
            postInvalidate();
        }
    }

    public void smoothScrollToValue(int toValue) {
        smoothScrollToValue(getValue(), toValue, true);
    }

    public void smoothScrollToValue(int toValue, boolean needRespond) {
        if (this.mItemHeight <= 0) {
            return;
        }
        smoothScrollToValue(getValue(), toValue, needRespond);
    }

    public void smoothScrollToValue(int fromValue, int toValue) {
        smoothScrollToValue(fromValue, toValue, true);
    }

    public void smoothScrollToValue(int fromValue, int toValue, boolean needRespond) {
        int deltaIndex;
        boolean z = true;
        int fromValue2 = refineValueByLimit(fromValue, this.mMinValue, this.mMaxValue, this.mWrapSelectorWheel && this.mWrapSelectorWheelCheck);
        int i = this.mMinValue;
        int i2 = this.mMaxValue;
        if (!this.mWrapSelectorWheel || !this.mWrapSelectorWheelCheck) {
            z = false;
        }
        int toValue2 = refineValueByLimit(toValue, i, i2, z);
        if (this.mWrapSelectorWheel && this.mWrapSelectorWheelCheck) {
            deltaIndex = toValue2 - fromValue2;
            int halfOneRecycleSize = getOneRecycleSize() / 2;
            if (deltaIndex < (-halfOneRecycleSize) || halfOneRecycleSize < deltaIndex) {
                int oneRecycleSize = getOneRecycleSize();
                deltaIndex = deltaIndex > 0 ? deltaIndex - oneRecycleSize : oneRecycleSize + deltaIndex;
            }
        } else {
            deltaIndex = toValue2 - fromValue2;
        }
        setValue(fromValue2);
        if (fromValue2 == toValue2) {
            return;
        }
        scrollByIndexSmoothly(deltaIndex, needRespond);
    }

    public void refreshByNewDisplayedValues(String[] display) {
        int minValue = getMinValue();
        int oldMaxValue = getMaxValue();
        int oldSpan = (oldMaxValue - minValue) + 1;
        int newMaxValue = display.length - 1;
        int newSpan = (newMaxValue - minValue) + 1;
        if (newSpan > oldSpan) {
            setDisplayedValues(display);
            setMaxValue(newMaxValue);
            return;
        }
        setMaxValue(newMaxValue);
        setDisplayedValues(display);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void respondPickedValueChanged(int oldVal, int newVal, Object respondChange) {
        onScrollStateChange(0);
        if (oldVal != newVal && (respondChange == null || !(respondChange instanceof Boolean) || ((Boolean) respondChange).booleanValue())) {
            OnValueChangeListener onValueChangeListener = this.mOnValueChangeListener;
            if (onValueChangeListener != null) {
                int i = this.mMinValue;
                onValueChangeListener.onValueChange(this, oldVal + i, i + newVal);
            }
            OnValueChangeListenerRelativeToRaw onValueChangeListenerRelativeToRaw = this.mOnValueChangeListenerRaw;
            if (onValueChangeListenerRelativeToRaw != null) {
                onValueChangeListenerRelativeToRaw.onValueChangeRelativeToRaw(this, oldVal, newVal, this.mDisplayedValues);
            }
        }
        this.mPrevPickedIndex = newVal;
        if (this.mPendingWrapToLinear) {
            this.mPendingWrapToLinear = false;
            internalSetWrapToLinear();
        }
    }

    private void scrollByIndexSmoothly(int deltaIndex) {
        scrollByIndexSmoothly(deltaIndex, true);
    }

    private void scrollByIndexSmoothly(int deltaIndex, boolean needRespond) {
        int dy;
        int duration;
        if (!this.mWrapSelectorWheel || !this.mWrapSelectorWheelCheck) {
            int willPickRawIndex = getPickedIndexRelativeToRaw();
            int i = willPickRawIndex + deltaIndex;
            int i2 = this.mMaxShowIndex;
            if (i > i2) {
                deltaIndex = i2 - willPickRawIndex;
            } else {
                int i3 = willPickRawIndex + deltaIndex;
                int i4 = this.mMinShowIndex;
                if (i3 < i4) {
                    deltaIndex = i4 - willPickRawIndex;
                }
            }
        }
        int willPickRawIndex2 = this.mCurrDrawFirstItemY;
        int i5 = this.mItemHeight;
        if (willPickRawIndex2 < (-i5) / 2) {
            dy = i5 + willPickRawIndex2;
            int duration2 = (int) (((willPickRawIndex2 + i5) * 300.0f) / i5);
            if (deltaIndex < 0) {
                duration = (-duration2) - (deltaIndex * 300);
            } else {
                duration = (deltaIndex * 300) + duration2;
            }
        } else {
            dy = this.mCurrDrawFirstItemY;
            int duration3 = (int) (((-willPickRawIndex2) * 300.0f) / i5);
            if (deltaIndex < 0) {
                duration = duration3 - (deltaIndex * 300);
            } else {
                duration = (deltaIndex * 300) + duration3;
            }
        }
        int dy2 = (this.mItemHeight * deltaIndex) + dy;
        if (duration < 300) {
            duration = 300;
        }
        if (duration > DEFAULT_MAX_SCROLL_BY_INDEX_DURATION) {
            duration = DEFAULT_MAX_SCROLL_BY_INDEX_DURATION;
        }
        this.mScroller.startScroll(0, this.mCurrDrawGlobalY, 0, dy2, duration);
        if (needRespond) {
            this.mHandlerInNewThread.sendMessageDelayed(getMsg(1), duration / 4);
        } else {
            this.mHandlerInNewThread.sendMessageDelayed(getMsg(1, 0, 0, new Boolean(needRespond)), duration / 4);
        }
        postInvalidate();
    }

    public int getMinValue() {
        return this.mMinValue;
    }

    public int getMaxValue() {
        return this.mMaxValue;
    }

    public void setMinValue(int minValue) {
        this.mMinValue = minValue;
        this.mMinShowIndex = 0;
        updateNotWrapYLimit();
    }

    public void setMaxValue(int maxValue) {
        String[] strArr = this.mDisplayedValues;
        if (strArr == null) {
            throw new NullPointerException("mDisplayedValues should not be null");
        }
        int i = this.mMinValue;
        if ((maxValue - i) + 1 > strArr.length) {
            throw new IllegalArgumentException("(maxValue - mMinValue + 1) should not be greater than mDisplayedValues.length now  (maxValue - mMinValue + 1) is " + ((maxValue - this.mMinValue) + 1) + " and mDisplayedValues.length is " + this.mDisplayedValues.length);
        }
        this.mMaxValue = maxValue;
        int i2 = this.mMaxValue - i;
        int i3 = this.mMinShowIndex;
        this.mMaxShowIndex = i2 + i3;
        setMinAndMaxShowIndex(i3, this.mMaxShowIndex);
        updateNotWrapYLimit();
    }

    public void setValue(int value) {
        int i = this.mMinValue;
        if (value < i) {
            throw new IllegalArgumentException("should not set a value less than mMinValue, value is " + value);
        } else if (value > this.mMaxValue) {
            throw new IllegalArgumentException("should not set a value greater than mMaxValue, value is " + value);
        } else {
            setPickedIndexRelativeToRaw(value - i);
        }
    }

    public int getValue() {
        return getPickedIndexRelativeToRaw() + this.mMinValue;
    }

    public String getContentByCurrValue() {
        return this.mDisplayedValues[getValue() - this.mMinValue];
    }

    public boolean getWrapSelectorWheel() {
        return this.mWrapSelectorWheel;
    }

    public boolean getWrapSelectorWheelAbsolutely() {
        return this.mWrapSelectorWheel && this.mWrapSelectorWheelCheck;
    }

    public void setHintText(String hintText) {
        if (isStringEqual(this.mHintText, hintText)) {
            return;
        }
        this.mHintText = hintText;
        this.mTextSizeHintCenterYOffset = getTextCenterYOffset(this.mPaintHint.getFontMetrics());
        this.mWidthOfHintText = getTextWidth(this.mHintText, this.mPaintHint);
        this.mHandlerInMainThread.sendEmptyMessage(3);
    }

    public void setPickedIndexRelativeToMin(int pickedIndexToMin) {
        if (pickedIndexToMin >= 0 && pickedIndexToMin < getOneRecycleSize()) {
            this.mPrevPickedIndex = this.mMinShowIndex + pickedIndexToMin;
            correctPositionByDefaultValue(pickedIndexToMin, this.mWrapSelectorWheel && this.mWrapSelectorWheelCheck);
            postInvalidate();
        }
    }

    public void setNormalTextColor(int normalTextColor) {
        int color = getResources().getColor(normalTextColor, getContext().getTheme());
        if (this.mTextColorNormal == color) {
            return;
        }
        this.mTextColorNormal = color;
        postInvalidate();
    }

    public void setSelectedTextColor(int selectedTextColor) {
        int color = getResources().getColor(selectedTextColor, getContext().getTheme());
        if (this.mTextColorSelected == color) {
            return;
        }
        this.mTextColorSelected = color;
        postInvalidate();
    }

    public void setHintTextColor(int hintTextColor) {
        int color = getResources().getColor(hintTextColor, getContext().getTheme());
        if (this.mTextColorHint == color) {
            return;
        }
        this.mTextColorHint = color;
        this.mPaintHint.setColor(this.mTextColorHint);
        postInvalidate();
    }

    public void setDividerColor(int dividerColor) {
        int color = getResources().getColor(dividerColor, getContext().getTheme());
        if (this.mDividerColor == color) {
            return;
        }
        this.mDividerColor = color;
        this.mPaintDivider.setColor(this.mDividerColor);
        postInvalidate();
    }

    public void setPickedIndexRelativeToRaw(int pickedIndexToRaw) {
        int i = this.mMinShowIndex;
        if (i > -1 && i <= pickedIndexToRaw && pickedIndexToRaw <= this.mMaxShowIndex) {
            this.mPrevPickedIndex = pickedIndexToRaw;
            correctPositionByDefaultValue(pickedIndexToRaw - i, this.mWrapSelectorWheel && this.mWrapSelectorWheelCheck);
            postInvalidate();
        }
    }

    public int getPickedIndexRelativeToRaw() {
        int i = this.mCurrDrawFirstItemY;
        if (i != 0) {
            int i2 = this.mItemHeight;
            if (i < (-i2) / 2) {
                int willPickIndex = getWillPickIndexByGlobalY(this.mCurrDrawGlobalY + i2 + i);
                return willPickIndex;
            }
            int willPickIndex2 = getWillPickIndexByGlobalY(this.mCurrDrawGlobalY + i);
            return willPickIndex2;
        }
        int willPickIndex3 = this.mCurrDrawGlobalY;
        return getWillPickIndexByGlobalY(willPickIndex3);
    }

    public void setMinAndMaxShowIndex(int minShowIndex, int maxShowIndex) {
        setMinAndMaxShowIndex(minShowIndex, maxShowIndex, true);
    }

    public void setMinAndMaxShowIndex(int minShowIndex, int maxShowIndex, boolean needRefresh) {
        if (minShowIndex > maxShowIndex) {
            throw new IllegalArgumentException("minShowIndex should be less than maxShowIndex, minShowIndex is " + minShowIndex + ", maxShowIndex is " + maxShowIndex + ".");
        }
        String[] strArr = this.mDisplayedValues;
        if (strArr == null) {
            throw new IllegalArgumentException("mDisplayedValues should not be null, you need to set mDisplayedValues first.");
        }
        if (minShowIndex < 0) {
            throw new IllegalArgumentException("minShowIndex should not be less than 0, now minShowIndex is " + minShowIndex);
        }
        boolean z = true;
        if (minShowIndex > strArr.length - 1) {
            throw new IllegalArgumentException("minShowIndex should not be greater than (mDisplayedValues.length - 1), now (mDisplayedValues.length - 1) is " + (this.mDisplayedValues.length - 1) + " minShowIndex is " + minShowIndex);
        } else if (maxShowIndex < 0) {
            throw new IllegalArgumentException("maxShowIndex should not be less than 0, now maxShowIndex is " + maxShowIndex);
        } else if (maxShowIndex > strArr.length - 1) {
            throw new IllegalArgumentException("maxShowIndex should not be greater than (mDisplayedValues.length - 1), now (mDisplayedValues.length - 1) is " + (this.mDisplayedValues.length - 1) + " maxShowIndex is " + maxShowIndex);
        } else {
            this.mMinShowIndex = minShowIndex;
            this.mMaxShowIndex = maxShowIndex;
            if (needRefresh) {
                this.mPrevPickedIndex = this.mMinShowIndex + 0;
                if (!this.mWrapSelectorWheel || !this.mWrapSelectorWheelCheck) {
                    z = false;
                }
                correctPositionByDefaultValue(0, z);
                postInvalidate();
            }
        }
    }

    public void setFriction(float friction) {
        if (friction <= 0.0f) {
            throw new IllegalArgumentException("you should set a a positive float friction, now friction is " + friction);
        }
        ViewConfiguration.get(getContext());
        this.mFriction = ViewConfiguration.getScrollFriction() / friction;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onScrollStateChange(int scrollState) {
        if (this.mScrollState == scrollState) {
            return;
        }
        this.mScrollState = scrollState;
        OnScrollListener onScrollListener = this.mOnScrollListener;
        if (onScrollListener != null) {
            onScrollListener.onScrollStateChange(this, scrollState);
        }
    }

    public void setOnScrollListener(OnScrollListener listener) {
        this.mOnScrollListener = listener;
    }

    public void setOnValueChangedListener(OnValueChangeListener listener) {
        this.mOnValueChangeListener = listener;
    }

    public void setOnValueChangedListenerRelativeToRaw(OnValueChangeListenerRelativeToRaw listener) {
        this.mOnValueChangeListenerRaw = listener;
    }

    public void setOnValueChangeListenerInScrolling(OnValueChangeListenerInScrolling listener) {
        this.mOnValueChangeListenerInScrolling = listener;
    }

    public void setContentTextTypeface(Typeface typeface) {
        this.mPaintText.setTypeface(typeface);
    }

    public void setHintTextTypeface(Typeface typeface) {
        this.mPaintHint.setTypeface(typeface);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getWillPickIndexByGlobalY(int globalY) {
        int i = this.mItemHeight;
        boolean z = false;
        if (i == 0) {
            return 0;
        }
        int willPickIndex = (globalY / i) + (this.mShowCount / 2);
        int oneRecycleSize = getOneRecycleSize();
        if (this.mWrapSelectorWheel && this.mWrapSelectorWheelCheck) {
            z = true;
        }
        int index = getIndexByRawIndex(willPickIndex, oneRecycleSize, z);
        if (index >= 0 && index < getOneRecycleSize()) {
            return this.mMinShowIndex + index;
        }
        throw new IllegalArgumentException("getWillPickIndexByGlobalY illegal index : " + index + " getOneRecycleSize() : " + getOneRecycleSize() + " mWrapSelectorWheel : " + this.mWrapSelectorWheel);
    }

    private int getIndexByRawIndex(int index, int size, boolean wrap) {
        if (size <= 0) {
            return 0;
        }
        if (wrap) {
            int index2 = index % size;
            if (index2 < 0) {
                return index2 + size;
            }
            return index2;
        }
        return index;
    }

    private void internalSetWrapToLinear() {
        int rawIndex = getPickedIndexRelativeToRaw();
        correctPositionByDefaultValue(rawIndex - this.mMinShowIndex, false);
        this.mWrapSelectorWheel = false;
        postInvalidate();
    }

    private void updateDividerAttr() {
        int i = this.mShowCount;
        this.mDividerIndex0 = i / 2;
        int i2 = this.mDividerIndex0;
        this.mDividerIndex1 = i2 + 1;
        int i3 = this.mViewHeight;
        this.dividerY0 = (i2 * i3) / i;
        this.dividerY1 = (this.mDividerIndex1 * i3) / i;
        if (this.mDividerMarginL < 0) {
            this.mDividerMarginL = 0;
        }
        if (this.mDividerMarginR < 0) {
            this.mDividerMarginR = 0;
        }
        if (this.mDividerMarginL + this.mDividerMarginR != 0 && getPaddingLeft() + this.mDividerMarginL >= (this.mViewWidth - getPaddingRight()) - this.mDividerMarginR) {
            int paddingLeft = getPaddingLeft() + this.mDividerMarginL + getPaddingRight();
            int i4 = this.mDividerMarginR;
            int surplusMargin = (paddingLeft + i4) - this.mViewWidth;
            int i5 = this.mDividerMarginL;
            this.mDividerMarginL = (int) (i5 - ((surplusMargin * i5) / (i5 + i4)));
            this.mDividerMarginR = (int) (i4 - ((surplusMargin * i4) / (this.mDividerMarginL + i4)));
        }
    }

    private void updateFontAttr() {
        int i = this.mTextSizeNormal;
        int i2 = this.mItemHeight;
        if (i > i2) {
            this.mTextSizeNormal = i2;
        }
        int i3 = this.mTextSizeSelected;
        int i4 = this.mItemHeight;
        if (i3 > i4) {
            this.mTextSizeSelected = i4;
        }
        Paint paint = this.mPaintHint;
        if (paint == null) {
            throw new IllegalArgumentException("mPaintHint should not be null.");
        }
        paint.setTextSize(this.mTextSizeHint);
        this.mTextSizeHintCenterYOffset = getTextCenterYOffset(this.mPaintHint.getFontMetrics());
        this.mWidthOfHintText = getTextWidth(this.mHintText, this.mPaintHint);
        TextPaint textPaint = this.mPaintText;
        if (textPaint == null) {
            throw new IllegalArgumentException("mPaintText should not be null.");
        }
        textPaint.setTextSize(this.mTextSizeSelected);
        this.mTextSizeSelectedCenterYOffset = getTextCenterYOffset(this.mPaintText.getFontMetrics());
        this.mPaintText.setTextSize(this.mTextSizeNormal);
        this.mTextSizeNormalCenterYOffset = getTextCenterYOffset(this.mPaintText.getFontMetrics());
    }

    private void updateNotWrapYLimit() {
        this.mNotWrapLimitYTop = 0;
        this.mNotWrapLimitYBottom = (-this.mShowCount) * this.mItemHeight;
        if (this.mDisplayedValues != null) {
            int oneRecycleSize = getOneRecycleSize();
            int i = this.mShowCount;
            int i2 = this.mItemHeight;
            this.mNotWrapLimitYTop = ((oneRecycleSize - (i / 2)) - 1) * i2;
            this.mNotWrapLimitYBottom = (-(i / 2)) * i2;
        }
    }

    private int limitY(int currDrawGlobalYPreferred) {
        if (this.mWrapSelectorWheel && this.mWrapSelectorWheelCheck) {
            return currDrawGlobalYPreferred;
        }
        if (currDrawGlobalYPreferred < this.mNotWrapLimitYBottom) {
            return this.mNotWrapLimitYBottom;
        }
        if (currDrawGlobalYPreferred > this.mNotWrapLimitYTop) {
            return this.mNotWrapLimitYTop;
        }
        return currDrawGlobalYPreferred;
    }

    /* JADX WARN: Code restructure failed: missing block: B:25:0x0060, code lost:
        if (r1 < r5) goto L26;
     */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.view.MotionEvent r21) {
        /*
            Method dump skipped, instructions count: 271
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.systemui.ui.widget.ValuePicker.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void click(MotionEvent event) {
        float y = event.getY();
        for (int i = 0; i < this.mShowCount; i++) {
            int i2 = this.mItemHeight;
            if (i2 * i <= y && y < i2 * (i + 1)) {
                clickItem(i);
                return;
            }
        }
    }

    private void clickItem(int showCountIndex) {
        int i;
        if (showCountIndex >= 0 && showCountIndex < (i = this.mShowCount)) {
            scrollByIndexSmoothly(showCountIndex - (i / 2));
        }
    }

    private float getTextCenterYOffset(Paint.FontMetrics fontMetrics) {
        if (fontMetrics == null) {
            return 0.0f;
        }
        return Math.abs(fontMetrics.top + fontMetrics.bottom) / 2.0f;
    }

    private void correctPositionByDefaultValue(int defaultPickedIndex, boolean wrap) {
        this.mCurrDrawFirstItemIndex = defaultPickedIndex - ((this.mShowCount - 1) / 2);
        this.mCurrDrawFirstItemIndex = getIndexByRawIndex(this.mCurrDrawFirstItemIndex, getOneRecycleSize(), wrap);
        int i = this.mItemHeight;
        if (i == 0) {
            this.mCurrentItemIndexEffect = true;
            return;
        }
        int i2 = this.mCurrDrawFirstItemIndex;
        this.mCurrDrawGlobalY = i * i2;
        this.mInScrollingPickedOldValue = i2 + (this.mShowCount / 2);
        this.mInScrollingPickedOldValue %= getOneRecycleSize();
        int i3 = this.mInScrollingPickedOldValue;
        if (i3 < 0) {
            this.mInScrollingPickedOldValue = i3 + getOneRecycleSize();
        }
        this.mInScrollingPickedNewValue = this.mInScrollingPickedOldValue;
        calculateFirstItemParameterByGlobalY();
    }

    @Override // android.view.View
    public void computeScroll() {
        if (this.mItemHeight != 0 && this.mScroller.computeScrollOffset()) {
            this.mCurrDrawGlobalY = this.mScroller.getCurrY();
            calculateFirstItemParameterByGlobalY();
            postInvalidate();
        }
    }

    private void calculateFirstItemParameterByGlobalY() {
        this.mCurrDrawFirstItemIndex = (int) Math.floor(this.mCurrDrawGlobalY / this.mItemHeight);
        int i = this.mCurrDrawGlobalY;
        int i2 = this.mCurrDrawFirstItemIndex;
        int i3 = this.mItemHeight;
        this.mCurrDrawFirstItemY = -(i - (i2 * i3));
        if (this.mOnValueChangeListenerInScrolling != null) {
            if ((-this.mCurrDrawFirstItemY) > i3 / 2) {
                this.mInScrollingPickedNewValue = i2 + 1 + (this.mShowCount / 2);
            } else {
                this.mInScrollingPickedNewValue = i2 + (this.mShowCount / 2);
            }
            this.mInScrollingPickedNewValue %= getOneRecycleSize();
            int i4 = this.mInScrollingPickedNewValue;
            if (i4 < 0) {
                this.mInScrollingPickedNewValue = i4 + getOneRecycleSize();
            }
            int i5 = this.mInScrollingPickedOldValue;
            int i6 = this.mInScrollingPickedNewValue;
            if (i5 != i6) {
                respondPickedValueChangedInScrolling(i5, i6);
            }
            this.mInScrollingPickedOldValue = this.mInScrollingPickedNewValue;
        }
    }

    private void releaseVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.clear();
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    private void updateMaxWHOfDisplayedValues(boolean needRequestLayout) {
        updateMaxWidthOfDisplayedValues();
        updateMaxHeightOfDisplayedValues();
        if (needRequestLayout) {
            if (this.mSpecModeW == Integer.MIN_VALUE || this.mSpecModeH == Integer.MIN_VALUE) {
                this.mHandlerInMainThread.sendEmptyMessage(3);
            }
        }
    }

    private int measureWidth(int measureSpec) {
        int specMode = View.MeasureSpec.getMode(measureSpec);
        this.mSpecModeW = specMode;
        int specSize = View.MeasureSpec.getSize(measureSpec);
        if (specMode == 1073741824) {
            return specSize;
        }
        int marginOfHint = Math.max(this.mWidthOfHintText, this.mWidthOfAlterHint) == 0 ? 0 : this.mMarginEndOfHint;
        int gapOfHint = Math.max(this.mWidthOfHintText, this.mWidthOfAlterHint) != 0 ? this.mMarginStartOfHint : 0;
        int maxWidth = Math.max(this.mMaxWidthOfAlterArrayWithMeasureHint, Math.max(this.mMaxWidthOfDisplayedValues, this.mMaxWidthOfAlterArrayWithoutMeasureHint) + ((Math.max(this.mWidthOfHintText, this.mWidthOfAlterHint) + gapOfHint + marginOfHint + (this.mItemPaddingHorizontal * 2)) * 2));
        int result = getPaddingLeft() + getPaddingRight() + maxWidth;
        if (specMode == Integer.MIN_VALUE) {
            return Math.min(result, specSize);
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int specMode = View.MeasureSpec.getMode(measureSpec);
        this.mSpecModeH = specMode;
        int specSize = View.MeasureSpec.getSize(measureSpec);
        if (specMode == 1073741824) {
            return specSize;
        }
        int maxHeight = this.mShowCount * (this.mMaxHeightOfDisplayedValues + (this.mItemPaddingVertical * 2));
        int result = getPaddingTop() + getPaddingBottom() + maxHeight;
        if (specMode == Integer.MIN_VALUE) {
            return Math.min(result, specSize);
        }
        return result;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawContent(canvas);
        drawLine(canvas);
        drawHint(canvas);
    }

    private void drawContent(Canvas canvas) {
        int textColor;
        float textSize;
        float textSizeCenterYOffset;
        int i;
        float fraction = 0.0f;
        for (int i2 = 0; i2 < this.mShowCount + 1; i2++) {
            float y = this.mCurrDrawFirstItemY + (this.mItemHeight * i2);
            int index = getIndexByRawIndex(this.mCurrDrawFirstItemIndex + i2, getOneRecycleSize(), this.mWrapSelectorWheel && this.mWrapSelectorWheelCheck);
            int textColor2 = this.mShowCount;
            if (i2 == textColor2 / 2) {
                fraction = (this.mCurrDrawFirstItemY + i) / this.mItemHeight;
                textColor = getEvaluateColor(fraction, this.mTextColorNormal, this.mTextColorSelected);
                textSize = getEvaluateSize(fraction, this.mTextSizeNormal, this.mTextSizeSelected);
                textSizeCenterYOffset = getEvaluateSize(fraction, this.mTextSizeNormalCenterYOffset, this.mTextSizeSelectedCenterYOffset);
            } else if (i2 == (textColor2 / 2) + 1) {
                int textColor3 = getEvaluateColor(1.0f - fraction, this.mTextColorNormal, this.mTextColorSelected);
                float textSize2 = getEvaluateSize(1.0f - fraction, this.mTextSizeNormal, this.mTextSizeSelected);
                textSizeCenterYOffset = getEvaluateSize(1.0f - fraction, this.mTextSizeNormalCenterYOffset, this.mTextSizeSelectedCenterYOffset);
                textColor = textColor3;
                textSize = textSize2;
            } else {
                textColor = this.mTextColorNormal;
                textSize = this.mTextSizeNormal;
                textSizeCenterYOffset = this.mTextSizeNormalCenterYOffset;
            }
            if (i2 == 0) {
                float fraction2 = -this.mCurrDrawFirstItemY;
                int i3 = this.mItemHeight;
                if (fraction2 > i3 / 2.0f) {
                    fraction2 = i3 / 2.0f;
                }
                fraction = (fraction2 * 2.0f) / this.mItemHeight;
                textColor = getEvaluateAlpha(fraction, this.mTextColorNormal);
            }
            if (i2 == this.mShowCount) {
                int i4 = this.mItemHeight;
                float fraction3 = this.mCurrDrawFirstItemY + i4;
                if (fraction3 > i4 / 2.0f) {
                    fraction3 = i4 / 2.0f;
                }
                float fraction4 = (2.0f * fraction3) / this.mItemHeight;
                textColor = getEvaluateAlpha(fraction4, this.mTextColorNormal);
                fraction = fraction4;
            }
            this.mPaintText.setColor(textColor);
            this.mPaintText.setTextSize(textSize);
            if (index >= 0 && index < getOneRecycleSize()) {
                CharSequence str = this.mDisplayedValues[this.mMinShowIndex + index];
                if (this.mTextEllipsize != null) {
                    str = TextUtils.ellipsize(str, this.mPaintText, getWidth() - (this.mItemPaddingHorizontal * 2), getEllipsizeType());
                }
                this.mPaintText.setColor(getShadowTextColor(textColor));
                int i5 = this.mShadowDirection;
                if (i5 == 1) {
                    canvas.drawText(str.toString(), this.mViewCenterX - 10.0f, (this.mItemHeight / 2) + y + textSizeCenterYOffset, this.mPaintText);
                } else if (i5 == 2) {
                    canvas.drawText(str.toString(), this.mViewCenterX + 10.0f, (this.mItemHeight / 2) + y + textSizeCenterYOffset, this.mPaintText);
                }
                this.mPaintText.setColor(textColor);
                canvas.drawText(str.toString(), this.mViewCenterX, (this.mItemHeight / 2) + y + textSizeCenterYOffset, this.mPaintText);
            } else if (!TextUtils.isEmpty(this.mEmptyItemHint)) {
                this.mPaintText.setColor(getShadowTextColor(textColor));
                int i6 = this.mShadowDirection;
                if (i6 == 1) {
                    canvas.drawText(this.mEmptyItemHint, this.mViewCenterX - 10.0f, (this.mItemHeight / 2) + y + textSizeCenterYOffset, this.mPaintText);
                } else if (i6 == 2) {
                    canvas.drawText(this.mEmptyItemHint, this.mViewCenterX + 10.0f, (this.mItemHeight / 2) + y + textSizeCenterYOffset, this.mPaintText);
                }
                this.mPaintText.setColor(textColor);
                canvas.drawText(this.mEmptyItemHint, this.mViewCenterX, (this.mItemHeight / 2) + y + textSizeCenterYOffset, this.mPaintText);
            }
        }
    }

    public void setShadowDirection(int direction) {
        this.mShadowDirection = direction;
        invalidate();
    }

    public int getShadowTextColor(int textColor) {
        return Color.argb((int) (Color.alpha(textColor) * 0.06f), Color.red(textColor), Color.green(textColor), Color.blue(textColor));
    }

    private TextUtils.TruncateAt getEllipsizeType() {
        char c;
        String str = this.mTextEllipsize;
        int hashCode = str.hashCode();
        if (hashCode == -1074341483) {
            if (str.equals(TEXT_ELLIPSIZE_MIDDLE)) {
                c = 1;
            }
            c = 65535;
        } else if (hashCode != 100571) {
            if (hashCode == 109757538 && str.equals(TEXT_ELLIPSIZE_START)) {
                c = 0;
            }
            c = 65535;
        } else {
            if (str.equals("end")) {
                c = 2;
            }
            c = 65535;
        }
        if (c != 0) {
            if (c != 1) {
                if (c == 2) {
                    return TextUtils.TruncateAt.END;
                }
                throw new IllegalArgumentException("Illegal text ellipsize type.");
            }
            return TextUtils.TruncateAt.MIDDLE;
        }
        return TextUtils.TruncateAt.START;
    }

    private void drawLine(Canvas canvas) {
        if (this.mShowDivider) {
            canvas.drawLine(getPaddingLeft() + this.mDividerMarginL, this.dividerY0, (this.mViewWidth - getPaddingRight()) - this.mDividerMarginR, this.dividerY0, this.mPaintDivider);
            canvas.drawLine(getPaddingLeft() + this.mDividerMarginL, this.dividerY1, (this.mViewWidth - getPaddingRight()) - this.mDividerMarginR, this.dividerY1, this.mPaintDivider);
        }
    }

    private void drawHint(Canvas canvas) {
        if (TextUtils.isEmpty(this.mHintText)) {
            return;
        }
        canvas.drawText(this.mHintText, this.mViewCenterX + ((this.mMaxWidthOfDisplayedValues + this.mWidthOfHintText) / 2) + this.mMarginStartOfHint, ((this.dividerY0 + this.dividerY1) / 2.0f) + this.mTextSizeHintCenterYOffset, this.mPaintHint);
    }

    private void updateMaxWidthOfDisplayedValues() {
        float savedTextSize = this.mPaintText.getTextSize();
        this.mPaintText.setTextSize(this.mTextSizeSelected);
        this.mMaxWidthOfDisplayedValues = getMaxWidthOfTextArray(this.mDisplayedValues, this.mPaintText);
        this.mMaxWidthOfAlterArrayWithMeasureHint = getMaxWidthOfTextArray(this.mAlterTextArrayWithMeasureHint, this.mPaintText);
        this.mMaxWidthOfAlterArrayWithoutMeasureHint = getMaxWidthOfTextArray(this.mAlterTextArrayWithoutMeasureHint, this.mPaintText);
        this.mPaintText.setTextSize(this.mTextSizeHint);
        this.mWidthOfAlterHint = getTextWidth(this.mAlterHint, this.mPaintText);
        this.mPaintText.setTextSize(savedTextSize);
    }

    private int getMaxWidthOfTextArray(CharSequence[] array, Paint paint) {
        if (array == null) {
            return 0;
        }
        int maxWidth = 0;
        for (CharSequence item : array) {
            if (item != null) {
                int itemWidth = getTextWidth(item, paint);
                maxWidth = Math.max(itemWidth, maxWidth);
            }
        }
        return maxWidth;
    }

    private int getTextWidth(CharSequence text, Paint paint) {
        if (!TextUtils.isEmpty(text)) {
            return (int) (paint.measureText(text.toString()) + 0.5f);
        }
        return 0;
    }

    private void updateMaxHeightOfDisplayedValues() {
        float savedTextSize = this.mPaintText.getTextSize();
        this.mPaintText.setTextSize(this.mTextSizeSelected);
        this.mMaxHeightOfDisplayedValues = (int) ((this.mPaintText.getFontMetrics().bottom - this.mPaintText.getFontMetrics().top) + 0.5d);
        this.mPaintText.setTextSize(savedTextSize);
    }

    private void updateContentAndIndex(String[] newDisplayedValues) {
        this.mMinShowIndex = 0;
        this.mMaxShowIndex = newDisplayedValues.length - 1;
        this.mDisplayedValues = newDisplayedValues;
        updateWrapStateByContent();
    }

    private void updateContent(String[] newDisplayedValues) {
        this.mDisplayedValues = newDisplayedValues;
        updateWrapStateByContent();
    }

    private void updateValue() {
        inflateDisplayedValuesIfNull();
        updateWrapStateByContent();
        this.mMinShowIndex = 0;
        this.mMaxShowIndex = this.mDisplayedValues.length - 1;
    }

    private void updateValueForInit() {
        inflateDisplayedValuesIfNull();
        updateWrapStateByContent();
        if (this.mMinShowIndex == -1) {
            this.mMinShowIndex = 0;
        }
        if (this.mMaxShowIndex == -1) {
            this.mMaxShowIndex = this.mDisplayedValues.length - 1;
        }
        setMinAndMaxShowIndex(this.mMinShowIndex, this.mMaxShowIndex, false);
    }

    private void inflateDisplayedValuesIfNull() {
        if (this.mDisplayedValues == null) {
            this.mDisplayedValues = new String[1];
            this.mDisplayedValues[0] = "0";
        }
    }

    private void updateWrapStateByContent() {
        this.mWrapSelectorWheelCheck = this.mDisplayedValues.length > this.mShowCount;
    }

    private int refineValueByLimit(int value, int minValue, int maxValue, boolean wrap) {
        if (wrap) {
            if (value > maxValue) {
                return (((value - maxValue) % getOneRecycleSize()) + minValue) - 1;
            }
            if (value < minValue) {
                return ((value - minValue) % getOneRecycleSize()) + maxValue + 1;
            }
            return value;
        } else if (value > maxValue) {
            return maxValue;
        } else {
            if (value < minValue) {
                return minValue;
            }
            return value;
        }
    }

    private void stopRefreshing() {
        Handler handler = this.mHandlerInNewThread;
        if (handler != null) {
            handler.removeMessages(1);
        }
    }

    public void stopScrolling() {
        ScrollerCompat scrollerCompat = this.mScroller;
        if (scrollerCompat != null && !scrollerCompat.isFinished()) {
            ScrollerCompat scrollerCompat2 = this.mScroller;
            scrollerCompat2.startScroll(0, scrollerCompat2.getCurrY(), 0, 0, 1);
            this.mScroller.abortAnimation();
            postInvalidate();
        }
    }

    public void stopScrollingAndCorrectPosition() {
        stopScrolling();
        Handler handler = this.mHandlerInNewThread;
        if (handler != null) {
            handler.sendMessageDelayed(getMsg(1), 0L);
        }
    }

    private Message getMsg(int what) {
        return getMsg(what, 0, 0, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Message getMsg(int what, int arg1, int arg2, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.obj = obj;
        return msg;
    }

    private boolean isStringEqual(String a, String b) {
        if (a == null) {
            if (b == null) {
                return true;
            }
            return false;
        }
        return a.equals(b);
    }

    private int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) ((spValue * fontScale) + 0.5f);
    }

    private int dp2px(Context context, float dpValue) {
        float densityScale = context.getResources().getDisplayMetrics().density;
        return (int) ((dpValue * densityScale) + 0.5f);
    }

    private int getEvaluateColor(float fraction, int startColor, int endColor) {
        int sA = (startColor & (-16777216)) >>> 24;
        int sR = (startColor & 16711680) >>> 16;
        int sG = (startColor & 65280) >>> 8;
        int sB = (startColor & 255) >>> 0;
        int eA = ((-16777216) & endColor) >>> 24;
        int eR = (16711680 & endColor) >>> 16;
        int eG = (65280 & endColor) >>> 8;
        int eB = (endColor & 255) >>> 0;
        int a = (int) (sA + ((eA - sA) * fraction));
        int r = (int) (sR + ((eR - sR) * fraction));
        int g = (int) (sG + ((eG - sG) * fraction));
        int b = (int) (sB + ((eB - sB) * fraction));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private int getEvaluateAlpha(float fraction, int startColor) {
        int sA = ((-16777216) & startColor) >>> 24;
        int sR = (16711680 & startColor) >>> 16;
        int sG = (65280 & startColor) >>> 8;
        int sB = (startColor & 255) >>> 0;
        int a = (int) (sA - (sA * fraction));
        return (a << 24) | (sR << 16) | (sG << 8) | sB;
    }

    private float getEvaluateSize(float fraction, float startSize, float endSize) {
        return ((endSize - startSize) * fraction) + startSize;
    }

    private String[] convertCharSequenceArrayToStringArray(CharSequence[] charSequences) {
        if (charSequences == null) {
            return null;
        }
        String[] ret = new String[charSequences.length];
        for (int i = 0; i < charSequences.length; i++) {
            ret[i] = charSequences[i].toString();
        }
        return ret;
    }
}
