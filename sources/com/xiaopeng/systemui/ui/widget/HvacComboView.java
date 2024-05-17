package com.xiaopeng.systemui.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;
import com.android.systemui.R;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.infoflow.util.OrientationUtil;
import com.xiaopeng.systemui.utils.DataLogUtils;
/* loaded from: classes24.dex */
public class HvacComboView extends AlphaOptimizedRelativeLayout implements View.OnTouchListener {
    public static final int ARROW_BOTTOM = 4;
    public static final int ARROW_LEFT = 1;
    public static final int ARROW_RIGHT = 3;
    public static final int ARROW_TOP = 2;
    private static final long DEFAULT_LONG_CLICK_DELAY = 200;
    private static final long DEFAULT_LONG_PRESS_TIMEOUT = 500;
    private static final long DEFAULT_MULTI_PRESS_TIMEOUT = 300;
    private static final long DEFAULT_SINGLE_CLICK_TIMEOUT = 30;
    public static final int HORIZONTAL = 0;
    private static final float MAX = 32.0f;
    private static final float MIN = 18.0f;
    private static final int MSG_ARROW_CLICK = 100;
    private static final String TAG = "HvacComboView";
    public static final int VERTICAL = 1;
    protected AnimatedImageView mArrowBottom;
    private int mArrowHeight;
    protected AnimatedImageView mArrowLeft;
    private int mArrowMargin;
    protected AnimatedImageView mArrowRight;
    protected AnimatedImageView mArrowTop;
    private boolean mArrowTouched;
    private int mArrowWidth;
    private GestureDetector mGestureDetector;
    private OnGestureListener mGestureListener;
    private Handler mHandler;
    private int mHeight;
    protected AnimatedImageView mHvacAir;
    private AnimatedImageView mHvacAuto;
    private RelativeLayout mHvacContainer;
    private RelativeLayout mHvacOffContainer;
    private RelativeLayout mHvacRoot;
    protected TemperatureTextView mHvacTemperature;
    private AnimatedImageView mHvacWind;
    private AlphaOptimizedLinearLayout mHvacWindContainer;
    private int mOrientation;
    private boolean mPowerOn;
    private float mScrollYDistanceToShowHvacDashboard;
    private boolean mShowAuto;
    private boolean mShowWind;
    private float mTemperature;
    private long mTouchDownMillis;
    private float mTouchEndY;
    private float mTouchStartY;
    private long mTouchUpMillis;
    private boolean mViewClicked;
    private OnViewListener mViewListener;
    private int mWidth;
    private int mWindResource;

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        handleTouchEvent(motionEvent);
        return false;
    }

    /* loaded from: classes24.dex */
    public interface OnGestureListener {
        default void onArrowUpClicked() {
        }

        default void onArrowDownClicked() {
        }

        default boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        default boolean onSingleTapConfirmed(MotionEvent e) {
            return false;
        }

        default boolean onDoubleTap(MotionEvent e) {
            return false;
        }

        default void onLongPress(MotionEvent e) {
        }

        default void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        }

        default void onScrollChanged(boolean isStarted) {
        }

        default boolean onTouchEvent(MotionEvent event) {
            return false;
        }

        default void dispatchTouchEvent(MotionEvent event) {
        }
    }

    public HvacComboView(Context context) {
        super(context);
        this.mPowerOn = false;
        this.mTemperature = 18.0f;
        this.mViewClicked = false;
        this.mArrowTouched = false;
        this.mTouchUpMillis = 0L;
        this.mTouchDownMillis = 0L;
        this.mTouchStartY = 0.0f;
        this.mTouchEndY = 0.0f;
        this.mShowAuto = false;
        this.mShowWind = true;
        this.mOrientation = 1;
        this.mViewListener = new OnViewListener();
        this.mHandler = new Handler() { // from class: com.xiaopeng.systemui.ui.widget.HvacComboView.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 100) {
                    HvacComboView.this.onArrowClicked(msg.arg1);
                }
            }
        };
        this.mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() { // from class: com.xiaopeng.systemui.ui.widget.HvacComboView.2
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onSingleTapUp(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onSingleTapUp e=" + e);
                if (HvacComboView.this.mGestureListener != null) {
                    HvacComboView.this.mGestureListener.onSingleTapUp(e);
                }
                return super.onSingleTapUp(e);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onDown(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onDown");
                return super.onDown(e);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                Logger.d(HvacComboView.TAG, "onLongPress viewClicked =" + HvacComboView.this.mViewClicked + " e=" + e);
                if (HvacComboView.this.mGestureListener != null && !HvacComboView.this.mViewClicked) {
                    HvacComboView.this.mGestureListener.onLongPress(e);
                }
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Logger.d(HvacComboView.TAG, "onScroll distanceX=" + distanceX + " distanceY=" + distanceY);
                if (HvacComboView.this.mGestureListener != null) {
                    HvacComboView.this.mGestureListener.onScroll(e1, e2, distanceX, distanceY);
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Logger.d(HvacComboView.TAG, "onFling");
                return super.onFling(e1, e2, velocityX, velocityY);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public void onShowPress(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onShowPress");
                super.onShowPress(e);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
            public boolean onDoubleTap(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onDoubleTap");
                if (HvacComboView.this.mGestureListener != null) {
                    HvacComboView.this.mGestureListener.onDoubleTap(e);
                }
                return super.onDoubleTap(e);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
            public boolean onDoubleTapEvent(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onDoubleTapEvent");
                return super.onDoubleTapEvent(e);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onSingleTapConfirmed");
                if (HvacComboView.this.mGestureListener != null) {
                    HvacComboView.this.mGestureListener.onSingleTapConfirmed(e);
                }
                return super.onSingleTapConfirmed(e);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnContextClickListener
            public boolean onContextClick(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onContextClick");
                return super.onContextClick(e);
            }
        });
        init(context, null, 0, 0);
    }

    public HvacComboView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mPowerOn = false;
        this.mTemperature = 18.0f;
        this.mViewClicked = false;
        this.mArrowTouched = false;
        this.mTouchUpMillis = 0L;
        this.mTouchDownMillis = 0L;
        this.mTouchStartY = 0.0f;
        this.mTouchEndY = 0.0f;
        this.mShowAuto = false;
        this.mShowWind = true;
        this.mOrientation = 1;
        this.mViewListener = new OnViewListener();
        this.mHandler = new Handler() { // from class: com.xiaopeng.systemui.ui.widget.HvacComboView.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 100) {
                    HvacComboView.this.onArrowClicked(msg.arg1);
                }
            }
        };
        this.mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() { // from class: com.xiaopeng.systemui.ui.widget.HvacComboView.2
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onSingleTapUp(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onSingleTapUp e=" + e);
                if (HvacComboView.this.mGestureListener != null) {
                    HvacComboView.this.mGestureListener.onSingleTapUp(e);
                }
                return super.onSingleTapUp(e);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onDown(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onDown");
                return super.onDown(e);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                Logger.d(HvacComboView.TAG, "onLongPress viewClicked =" + HvacComboView.this.mViewClicked + " e=" + e);
                if (HvacComboView.this.mGestureListener != null && !HvacComboView.this.mViewClicked) {
                    HvacComboView.this.mGestureListener.onLongPress(e);
                }
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Logger.d(HvacComboView.TAG, "onScroll distanceX=" + distanceX + " distanceY=" + distanceY);
                if (HvacComboView.this.mGestureListener != null) {
                    HvacComboView.this.mGestureListener.onScroll(e1, e2, distanceX, distanceY);
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Logger.d(HvacComboView.TAG, "onFling");
                return super.onFling(e1, e2, velocityX, velocityY);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public void onShowPress(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onShowPress");
                super.onShowPress(e);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
            public boolean onDoubleTap(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onDoubleTap");
                if (HvacComboView.this.mGestureListener != null) {
                    HvacComboView.this.mGestureListener.onDoubleTap(e);
                }
                return super.onDoubleTap(e);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
            public boolean onDoubleTapEvent(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onDoubleTapEvent");
                return super.onDoubleTapEvent(e);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onSingleTapConfirmed");
                if (HvacComboView.this.mGestureListener != null) {
                    HvacComboView.this.mGestureListener.onSingleTapConfirmed(e);
                }
                return super.onSingleTapConfirmed(e);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnContextClickListener
            public boolean onContextClick(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onContextClick");
                return super.onContextClick(e);
            }
        });
        init(context, attrs, 0, 0);
    }

    public HvacComboView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mPowerOn = false;
        this.mTemperature = 18.0f;
        this.mViewClicked = false;
        this.mArrowTouched = false;
        this.mTouchUpMillis = 0L;
        this.mTouchDownMillis = 0L;
        this.mTouchStartY = 0.0f;
        this.mTouchEndY = 0.0f;
        this.mShowAuto = false;
        this.mShowWind = true;
        this.mOrientation = 1;
        this.mViewListener = new OnViewListener();
        this.mHandler = new Handler() { // from class: com.xiaopeng.systemui.ui.widget.HvacComboView.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 100) {
                    HvacComboView.this.onArrowClicked(msg.arg1);
                }
            }
        };
        this.mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() { // from class: com.xiaopeng.systemui.ui.widget.HvacComboView.2
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onSingleTapUp(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onSingleTapUp e=" + e);
                if (HvacComboView.this.mGestureListener != null) {
                    HvacComboView.this.mGestureListener.onSingleTapUp(e);
                }
                return super.onSingleTapUp(e);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onDown(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onDown");
                return super.onDown(e);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                Logger.d(HvacComboView.TAG, "onLongPress viewClicked =" + HvacComboView.this.mViewClicked + " e=" + e);
                if (HvacComboView.this.mGestureListener != null && !HvacComboView.this.mViewClicked) {
                    HvacComboView.this.mGestureListener.onLongPress(e);
                }
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Logger.d(HvacComboView.TAG, "onScroll distanceX=" + distanceX + " distanceY=" + distanceY);
                if (HvacComboView.this.mGestureListener != null) {
                    HvacComboView.this.mGestureListener.onScroll(e1, e2, distanceX, distanceY);
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Logger.d(HvacComboView.TAG, "onFling");
                return super.onFling(e1, e2, velocityX, velocityY);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public void onShowPress(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onShowPress");
                super.onShowPress(e);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
            public boolean onDoubleTap(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onDoubleTap");
                if (HvacComboView.this.mGestureListener != null) {
                    HvacComboView.this.mGestureListener.onDoubleTap(e);
                }
                return super.onDoubleTap(e);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
            public boolean onDoubleTapEvent(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onDoubleTapEvent");
                return super.onDoubleTapEvent(e);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onSingleTapConfirmed");
                if (HvacComboView.this.mGestureListener != null) {
                    HvacComboView.this.mGestureListener.onSingleTapConfirmed(e);
                }
                return super.onSingleTapConfirmed(e);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnContextClickListener
            public boolean onContextClick(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onContextClick");
                return super.onContextClick(e);
            }
        });
        init(context, attrs, defStyleAttr, 0);
    }

    public HvacComboView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mPowerOn = false;
        this.mTemperature = 18.0f;
        this.mViewClicked = false;
        this.mArrowTouched = false;
        this.mTouchUpMillis = 0L;
        this.mTouchDownMillis = 0L;
        this.mTouchStartY = 0.0f;
        this.mTouchEndY = 0.0f;
        this.mShowAuto = false;
        this.mShowWind = true;
        this.mOrientation = 1;
        this.mViewListener = new OnViewListener();
        this.mHandler = new Handler() { // from class: com.xiaopeng.systemui.ui.widget.HvacComboView.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 100) {
                    HvacComboView.this.onArrowClicked(msg.arg1);
                }
            }
        };
        this.mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() { // from class: com.xiaopeng.systemui.ui.widget.HvacComboView.2
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onSingleTapUp(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onSingleTapUp e=" + e);
                if (HvacComboView.this.mGestureListener != null) {
                    HvacComboView.this.mGestureListener.onSingleTapUp(e);
                }
                return super.onSingleTapUp(e);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onDown(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onDown");
                return super.onDown(e);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                Logger.d(HvacComboView.TAG, "onLongPress viewClicked =" + HvacComboView.this.mViewClicked + " e=" + e);
                if (HvacComboView.this.mGestureListener != null && !HvacComboView.this.mViewClicked) {
                    HvacComboView.this.mGestureListener.onLongPress(e);
                }
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Logger.d(HvacComboView.TAG, "onScroll distanceX=" + distanceX + " distanceY=" + distanceY);
                if (HvacComboView.this.mGestureListener != null) {
                    HvacComboView.this.mGestureListener.onScroll(e1, e2, distanceX, distanceY);
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Logger.d(HvacComboView.TAG, "onFling");
                return super.onFling(e1, e2, velocityX, velocityY);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public void onShowPress(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onShowPress");
                super.onShowPress(e);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
            public boolean onDoubleTap(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onDoubleTap");
                if (HvacComboView.this.mGestureListener != null) {
                    HvacComboView.this.mGestureListener.onDoubleTap(e);
                }
                return super.onDoubleTap(e);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
            public boolean onDoubleTapEvent(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onDoubleTapEvent");
                return super.onDoubleTapEvent(e);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onSingleTapConfirmed");
                if (HvacComboView.this.mGestureListener != null) {
                    HvacComboView.this.mGestureListener.onSingleTapConfirmed(e);
                }
                return super.onSingleTapConfirmed(e);
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnContextClickListener
            public boolean onContextClick(MotionEvent e) {
                Logger.d(HvacComboView.TAG, "onContextClick");
                return super.onContextClick(e);
            }
        });
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        LayoutInflater.from(context).inflate(R.layout.view_hvac_combo, this);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.HvacComboView, 0, 0);
        this.mWidth = attributes.getDimensionPixelSize(8, 0);
        this.mHeight = attributes.getDimensionPixelSize(4, 0);
        this.mArrowWidth = attributes.getDimensionPixelSize(2, 0);
        this.mArrowHeight = attributes.getDimensionPixelSize(0, 0);
        this.mArrowMargin = attributes.getDimensionPixelSize(1, 0);
        this.mShowAuto = attributes.getBoolean(6, false);
        this.mShowWind = attributes.getBoolean(7, false);
        this.mWindResource = attributes.getResourceId(9, 0);
        this.mOrientation = attributes.getInt(5, 1);
        attributes.recycle();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
        initAttributeSet();
    }

    public void setScrollYDistanceToShowHvacDashboard(float yDist) {
        this.mScrollYDistanceToShowHvacDashboard = yDist;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void initView() {
        this.mHvacRoot = (RelativeLayout) findViewById(R.id.hvac_root);
        this.mHvacAuto = (AnimatedImageView) findViewById(R.id.hvac_auto);
        this.mHvacWind = (AnimatedImageView) findViewById(R.id.hvac_wind);
        this.mHvacAir = (AnimatedImageView) findViewById(R.id.hvac_air);
        this.mHvacTemperature = (TemperatureTextView) findViewById(R.id.hvac_temperature);
        this.mHvacWindContainer = (AlphaOptimizedLinearLayout) findViewById(R.id.hvac_wind_container);
        this.mArrowLeft = (AnimatedImageView) findViewById(R.id.arrow_left);
        this.mArrowTop = (AnimatedImageView) findViewById(R.id.arrow_top);
        this.mArrowRight = (AnimatedImageView) findViewById(R.id.arrow_right);
        this.mArrowBottom = (AnimatedImageView) findViewById(R.id.arrow_bottom);
        this.mHvacContainer = (RelativeLayout) findViewById(R.id.hvac_container);
        this.mHvacOffContainer = (RelativeLayout) findViewById(R.id.hvac_off_container);
        this.mArrowLeft.setOnTouchListener(this.mViewListener);
        this.mArrowTop.setOnTouchListener(this.mViewListener);
        this.mArrowRight.setOnTouchListener(this.mViewListener);
        this.mArrowBottom.setOnTouchListener(this.mViewListener);
        this.mHvacContainer.setOnTouchListener(this);
        this.mHvacOffContainer.setOnTouchListener(this);
    }

    private void initAttributeSet() {
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) this.mHvacRoot.getLayoutParams();
        if (!OrientationUtil.isLandscapeScreen(this.mContext)) {
            int i = this.mWidth;
            if (i == -1) {
                i = rlp.width;
            }
            rlp.width = i;
        }
        int i2 = this.mHeight;
        if (i2 == -1) {
            i2 = rlp.height;
        }
        rlp.height = i2;
        Logger.d(TAG, "initAttributeSet rlp.width=" + rlp.width + " rlp.height=" + rlp.height + " arrowMargin=" + this.mArrowMargin);
        this.mHvacRoot.setLayoutParams(rlp);
        this.mHvacWind.setImageResource(this.mWindResource);
        setAutoVisibility(8);
        setWindVisibility(this.mShowWind ? 0 : 8);
        int i3 = this.mOrientation;
        if (i3 == 0) {
            this.mArrowLeft.setVisibility(0);
            this.mArrowRight.setVisibility(0);
            RelativeLayout.LayoutParams rlp2 = (RelativeLayout.LayoutParams) this.mArrowLeft.getLayoutParams();
            int i4 = this.mArrowWidth;
            if (i4 == -1) {
                i4 = rlp2.width;
            }
            rlp2.width = i4;
            int i5 = this.mArrowHeight;
            if (i5 == -1) {
                i5 = rlp2.height;
            }
            rlp2.height = i5;
            rlp2.rightMargin = this.mArrowMargin;
            this.mArrowLeft.setLayoutParams(rlp2);
            RelativeLayout.LayoutParams rlp3 = (RelativeLayout.LayoutParams) this.mArrowRight.getLayoutParams();
            int i6 = this.mArrowWidth;
            if (i6 == -1) {
                i6 = rlp3.width;
            }
            rlp3.width = i6;
            int i7 = this.mArrowHeight;
            if (i7 == -1) {
                i7 = rlp3.height;
            }
            rlp3.height = i7;
            rlp3.leftMargin = this.mArrowMargin;
            this.mArrowRight.setLayoutParams(rlp3);
        } else if (i3 == 1) {
            this.mArrowTop.setVisibility(0);
            this.mArrowBottom.setVisibility(0);
            RelativeLayout.LayoutParams rlp4 = (RelativeLayout.LayoutParams) this.mArrowTop.getLayoutParams();
            int i8 = this.mArrowWidth;
            if (i8 == -1) {
                i8 = rlp4.width;
            }
            rlp4.width = i8;
            int i9 = this.mArrowHeight;
            if (i9 == -1) {
                i9 = rlp4.height;
            }
            rlp4.height = i9;
            rlp4.bottomMargin = this.mArrowMargin;
            this.mArrowTop.setLayoutParams(rlp4);
            RelativeLayout.LayoutParams rlp5 = (RelativeLayout.LayoutParams) this.mArrowBottom.getLayoutParams();
            int i10 = this.mArrowWidth;
            if (i10 == -1) {
                i10 = rlp5.width;
            }
            rlp5.width = i10;
            int i11 = this.mArrowHeight;
            if (i11 == -1) {
                i11 = rlp5.height;
            }
            rlp5.height = i11;
            rlp5.topMargin = this.mArrowMargin;
            this.mArrowBottom.setLayoutParams(rlp5);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Logger.d(TAG, "dispatchTouchEvent ev=" + ev);
        int action = ev.getAction();
        boolean z = false;
        if (action == 0) {
            this.mTouchDownMillis = SystemClock.uptimeMillis();
            this.mTouchStartY = ev.getY();
            this.mViewClicked = false;
        } else if (action == 1) {
            this.mArrowTouched = false;
            this.mTouchUpMillis = SystemClock.uptimeMillis();
            this.mTouchEndY = ev.getY();
            long touchTime = this.mTouchUpMillis - this.mTouchDownMillis;
            float touchYDist = Math.abs(this.mTouchEndY - this.mTouchStartY);
            if (touchTime < ViewConfiguration.getLongPressTimeout() && touchYDist <= this.mScrollYDistanceToShowHvacDashboard) {
                z = true;
            }
            this.mViewClicked = z;
            Logger.d(TAG, "up : touchTime = " + touchTime + " touchYDist = " + touchYDist + " mViewClicked = " + this.mViewClicked);
        }
        OnGestureListener onGestureListener = this.mGestureListener;
        if (onGestureListener != null) {
            onGestureListener.dispatchTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setListener(OnGestureListener listener) {
        this.mGestureListener = listener;
    }

    public void setText(float text) {
        this.mTemperature = text;
        this.mHvacTemperature.setText(text);
        updateArrowState();
    }

    public void setAuto(boolean auto) {
        setAutoVisibility(auto ? 0 : 8);
    }

    public void setPower(boolean on) {
        this.mPowerOn = on;
        setAlpha(on ? 1.0f : 0.56f);
        Logger.d(TAG, "setPower mOrientation = " + this.mOrientation);
        if (this.mOrientation == 1) {
            Logger.d(TAG, "setPower : " + on);
            this.mHvacOffContainer.setVisibility(on ? 8 : 0);
            this.mHvacContainer.setVisibility(on ? 0 : 8);
        }
    }

    public void setWindLevel(int level) {
        int level2 = this.mPowerOn ? level == 14 ? 11 : level : 0;
        if (level2 == 11) {
            this.mHvacWind.setVisibility(8);
            this.mHvacAir.setImageResource(R.drawable.ic_navbar_hvac_air_small_auto);
            return;
        }
        this.mHvacWind.setVisibility(0);
        this.mHvacWind.setImageLevel(level2);
        this.mHvacAir.setImageResource(R.drawable.ic_navbar_hvac_air_small);
    }

    public void setWindColor(int type) {
        if (type == 1) {
            this.mHvacWind.setImageResource(R.drawable.ic_navbar_hvac_wind_nature);
        } else if (type == 3) {
            this.mHvacWind.setImageResource(R.drawable.ic_navbar_hvac_wind_hot);
        } else {
            this.mHvacWind.setImageResource(R.drawable.ic_navbar_hvac_wind_cold);
        }
    }

    public void setWindResource(int resId) {
        this.mHvacWind.setImageResource(resId);
        this.mHvacWind.setImageDrawable(getResources().getDrawable(resId));
    }

    public void setShowWind(boolean showWind) {
        this.mShowWind = showWind;
    }

    public void setShowAuto(boolean showAuto) {
        this.mShowAuto = showAuto;
    }

    public void setWindVisibility(int visibility) {
        this.mHvacWindContainer.setVisibility(this.mShowWind ? visibility : 8);
    }

    public void setAutoVisibility(int visibility) {
        int windVisibility = 8;
        int autoVisibility = this.mShowAuto ? visibility : 8;
        if (autoVisibility != 0 && this.mShowWind) {
            windVisibility = 0;
        }
        this.mHvacAuto.setVisibility(autoVisibility);
        this.mHvacWindContainer.setVisibility(windVisibility);
    }

    public void setArrowVisibility(int visibility) {
        int i = this.mOrientation;
        if (i == 0) {
            this.mArrowLeft.setVisibility(visibility);
            this.mArrowRight.setVisibility(visibility);
        } else if (i == 1) {
            this.mArrowTop.setVisibility(visibility);
            this.mArrowBottom.setVisibility(visibility);
        }
    }

    private void handleTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action != 0) {
            if (action == 1) {
                OnGestureListener onGestureListener = this.mGestureListener;
                if (onGestureListener != null && this.mViewClicked) {
                    onGestureListener.onSingleTapConfirmed(event);
                    return;
                }
            } else if (action != 3) {
            }
        }
        OnGestureListener onGestureListener2 = this.mGestureListener;
        if (onGestureListener2 != null) {
            onGestureListener2.onTouchEvent(event);
        }
        GestureDetector gestureDetector = this.mGestureDetector;
        if (gestureDetector != null) {
            gestureDetector.onTouchEvent(event);
        }
    }

    public void updateArrowState() {
        float value = this.mTemperature;
        int typeUp = -1;
        int typeDown = -1;
        int i = this.mOrientation;
        if (i == 0) {
            typeUp = 3;
            typeDown = 1;
        } else if (i == 1) {
            typeUp = 2;
            typeDown = 4;
        }
        if (value == 32.0f) {
            setArrowEnabled(typeUp, false);
            setArrowEnabled(typeDown, true);
        }
        if (value == 18.0f) {
            setArrowEnabled(typeUp, true);
            setArrowEnabled(typeDown, false);
        }
        if (value != 18.0f && value != 32.0f) {
            setArrowEnabled(typeUp, true);
            setArrowEnabled(typeDown, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0032, code lost:
        if (r3 != 4) goto L19;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onArrowClicked(int r3) {
        /*
            r2 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "onArrowClicked arrowTouched="
            r0.append(r1)
            boolean r1 = r2.mArrowTouched
            r0.append(r1)
            java.lang.String r1 = " type="
            r0.append(r1)
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "HvacComboView"
            com.xiaopeng.systemui.Logger.d(r1, r0)
            com.xiaopeng.systemui.ui.widget.HvacComboView$OnGestureListener r0 = r2.mGestureListener
            if (r0 == 0) goto L56
            boolean r0 = r2.mArrowTouched
            if (r0 == 0) goto L56
            r0 = 1
            if (r3 == r0) goto L46
            r0 = 2
            if (r3 == r0) goto L35
            r0 = 3
            if (r3 == r0) goto L35
            r0 = 4
            if (r3 == r0) goto L46
            goto L56
        L35:
            com.xiaopeng.systemui.ui.widget.HvacComboView$OnGestureListener r0 = r2.mGestureListener
            r0.onArrowUpClicked()
            float r0 = r2.mTemperature
            r1 = 1107296256(0x42000000, float:32.0)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 < 0) goto L56
            r2.cancelArrowLongPressEvent()
            goto L56
        L46:
            com.xiaopeng.systemui.ui.widget.HvacComboView$OnGestureListener r0 = r2.mGestureListener
            r0.onArrowDownClicked()
            float r0 = r2.mTemperature
            r1 = 1099956224(0x41900000, float:18.0)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 > 0) goto L56
            r2.cancelArrowLongPressEvent()
        L56:
            boolean r0 = r2.mArrowTouched
            if (r0 == 0) goto L5f
            r0 = 200(0xc8, double:9.9E-322)
            r2.sendArrowClickedEvent(r3, r0)
        L5f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.systemui.ui.widget.HvacComboView.onArrowClicked(int):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelArrowLongPressEvent() {
        this.mArrowTouched = false;
        clearArrowClickedEvent();
    }

    private void setArrowEnabled(int type, boolean enable) {
        AnimatedImageView view = null;
        if (type == 1) {
            view = this.mArrowLeft;
        } else if (type == 2) {
            view = this.mArrowTop;
        } else if (type == 3) {
            view = this.mArrowRight;
        } else if (type == 4) {
            view = this.mArrowBottom;
        }
        if (view != null) {
            if (enable != view.isEnabled()) {
                view.setEnabled(enable);
            }
            if (enable != view.isClickable()) {
                view.setClickable(enable);
            }
            if (enable != view.isLongClickable()) {
                view.setLongClickable(enable);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendArrowClickedEvent(int type, long delay) {
        this.mHandler.removeMessages(100);
        Message msg = new Message();
        msg.what = 100;
        msg.arg1 = type;
        this.mHandler.sendMessageDelayed(msg, delay);
    }

    private void clearArrowClickedEvent() {
        this.mHandler.removeMessages(100);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public class OnViewListener implements View.OnTouchListener {
        private OnViewListener() {
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int type = -1;
            switch (view.getId()) {
                case R.id.arrow_bottom /* 2131361963 */:
                    type = 4;
                    break;
                case R.id.arrow_left /* 2131361964 */:
                    type = 1;
                    break;
                case R.id.arrow_right /* 2131361965 */:
                    type = 3;
                    break;
                case R.id.arrow_top /* 2131361966 */:
                    type = 2;
                    break;
            }
            int action = motionEvent.getAction();
            Logger.i(HvacComboView.TAG, "arrow onTouch : " + action);
            if (action == 0) {
                HvacComboView.this.mArrowTouched = true;
                view.setPressed(true);
                HvacComboView.this.post(new Runnable() { // from class: com.xiaopeng.systemui.ui.widget.HvacComboView.OnViewListener.1
                    @Override // java.lang.Runnable
                    public void run() {
                        HvacComboView.this.playSoundEffect(0);
                    }
                });
                HvacComboView.this.sendArrowClickedEvent(type, HvacComboView.DEFAULT_SINGLE_CLICK_TIMEOUT);
            } else if (action == 1 || action == 3) {
                view.setPressed(false);
                HvacComboView.this.cancelArrowLongPressEvent();
                if (action == 1) {
                    DataLogUtils.sendDataLog(DataLogUtils.SYSTEMUI_PAGE_ID, DataLogUtils.HVAC_ID);
                }
            }
            return true;
        }
    }
}
