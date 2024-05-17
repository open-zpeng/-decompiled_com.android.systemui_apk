package com.xiaopeng.systemui.quickmenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.android.systemui.R;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.qs.QsLayoutHelper;
import com.xiaopeng.systemui.statusbar.QuickMenuGuide;
import com.xiaopeng.systemui.utils.BIHelper;
/* loaded from: classes24.dex */
public class QuickMenuFloatingView extends FrameLayout implements GestureDetector.OnGestureListener {
    public static final int DIRECT_DOWN = 1;
    public static final int DIRECT_UP = 0;
    public static final String QUICK_PANEL_BI_STATUS_DOWN = "0";
    public static final String QUICK_PANEL_BI_STATUS_UP = "1";
    public static final String QUICK_PANEL_BI_STATUS_UP_AUTO = "2";
    public static final int TIMEOUT_TIME = 10000;
    private String TAG;
    private int TIME_OUT_ANIM;
    private Runnable mDismissRunnable;
    private Runnable mDisplayRunnable;
    private Runnable mDownBIRunnable;
    private boolean mFromTop;
    private GestureDetector mGestureDetector;
    private Handler mHandler;
    private final Boolean mIfUseNewQsPanel;
    private boolean mIsAutoHide;
    private boolean mIsFlingDown;
    private boolean mIsFlingUP;
    private boolean mIsInitScrollPanel;
    int mLastY;
    private int mMaxVelocity;
    boolean mPanelInTapRegion;
    View mPanelView;
    View mPanelViewEmpty;
    private QsLayoutHelper mQsLayoutHelper;
    private ImageView mQuickMenuBar;
    private ViewGroup mQuickMenuBarLayout;
    QuickMenuFloatingVisibleChangeListener mQuickMenuFloatingVisibleChangeListener;
    private ViewGroup mQuickMenuLayout;
    private QuickMenuHolderPresenter mQuickMenuViewHolderPresenter;
    View mRootView;
    private int mScreenHeight;
    public int mScreenId;
    private Runnable mScrollRunnable;
    private ThemeViewModel mThemeViewModel;
    private int mTouchSlop;
    int mTouchX;
    int mTouchY;
    private Runnable mUpBIRunnable;
    private ValueAnimator mValueAnimator;

    /* loaded from: classes24.dex */
    public interface QuickMenuFloatingVisibleChangeListener {
        void visibility(boolean z);

        void willDropDown(boolean z);
    }

    public void autoHide() {
        Log.i(this.TAG, "autoHide");
        if (!isFullScreenPanel() && this.mPanelView.getHeight() == 0) {
            if (this.mQuickMenuFloatingVisibleChangeListener != null) {
                Log.e(this.TAG, "floating view dismiss, this is error!");
                this.mQuickMenuFloatingVisibleChangeListener.visibility(false);
                return;
            }
            return;
        }
        startAnimator(getPanelScrollY(), getPanelHeight());
    }

    private int getPanelScrollY() {
        if (!isFullScreenPanel()) {
            return this.mRootView.getScrollY();
        }
        return getScrollY();
    }

    private int getPanelHeight() {
        if (!isFullScreenPanel()) {
            return this.mPanelView.getHeight();
        }
        return getScreenHeight(this.mContext);
    }

    public QuickMenuFloatingView(Context context, int screenIndex) {
        this(context, null, screenIndex);
    }

    public QuickMenuFloatingView(Context context, AttributeSet attrs, int screenIndex) {
        this(context, attrs, 0, screenIndex);
    }

    public QuickMenuFloatingView(Context context, AttributeSet attrs, int defStyleAttr, int screenIndex) {
        this(context, attrs, defStyleAttr, 0, screenIndex);
    }

    public QuickMenuFloatingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int screenIndex) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mFromTop = true;
        this.mIsAutoHide = false;
        this.TAG = QuickMenuFloatingView.class.getSimpleName();
        this.mIsFlingUP = false;
        this.mIsFlingDown = false;
        this.mGestureDetector = new GestureDetector(ContextUtils.getContext(), this);
        this.mHandler = new Handler();
        this.mIfUseNewQsPanel = Boolean.valueOf(CarModelsManager.getFeature().isSupportNewQs());
        this.mDownBIRunnable = new Runnable() { // from class: com.xiaopeng.systemui.quickmenu.QuickMenuFloatingView.1
            @Override // java.lang.Runnable
            public void run() {
                Log.i(QuickMenuFloatingView.this.TAG, "mDownBIRunnable");
                if (QuickMenuFloatingView.this.mFromTop) {
                    BIHelper.sendBIData(BIHelper.ID.panel, BIHelper.Type.panel, BIHelper.Action.close, QuickMenuFloatingView.this.mScreenId == 0 ? BIHelper.Screen.main : BIHelper.Screen.second, "0");
                    QuickMenuFloatingView.this.mFromTop = false;
                }
            }
        };
        this.mUpBIRunnable = new Runnable() { // from class: com.xiaopeng.systemui.quickmenu.QuickMenuFloatingView.2
            @Override // java.lang.Runnable
            public void run() {
                Log.i(QuickMenuFloatingView.this.TAG, "mUpBIRunnable");
                if (!QuickMenuFloatingView.this.mFromTop) {
                    BIHelper.sendBIData(BIHelper.ID.panel, BIHelper.Type.panel, BIHelper.Action.close, QuickMenuFloatingView.this.mScreenId == 0 ? BIHelper.Screen.main : BIHelper.Screen.second, QuickMenuFloatingView.this.mIsAutoHide ? "2" : "1");
                    QuickMenuFloatingView.this.mFromTop = true;
                    QuickMenuFloatingView.this.mIsAutoHide = false;
                }
            }
        };
        this.mDismissRunnable = new Runnable() { // from class: com.xiaopeng.systemui.quickmenu.QuickMenuFloatingView.3
            @Override // java.lang.Runnable
            public void run() {
                Log.i(QuickMenuFloatingView.this.TAG, "mDismissRunnable autoHide");
                QuickMenuFloatingView.this.mIsAutoHide = true;
                QuickMenuFloatingView.this.autoHide();
            }
        };
        this.TIME_OUT_ANIM = 250;
        this.mIsInitScrollPanel = false;
        this.mScrollRunnable = new Runnable() { // from class: com.xiaopeng.systemui.quickmenu.QuickMenuFloatingView.6
            @Override // java.lang.Runnable
            public void run() {
                if (!QuickMenuFloatingView.this.isFullScreenPanel()) {
                    String str = QuickMenuFloatingView.this.TAG;
                    Log.d(str, "mScrollRunnable floating view  panelHeight:" + QuickMenuFloatingView.this.mPanelView.getHeight() + " " + QuickMenuFloatingView.this.getScrollY() + " " + QuickMenuFloatingView.this.mRootView.getScrollY());
                    QuickMenuFloatingView.this.mRootView.scrollTo(0, QuickMenuFloatingView.this.mPanelView.getHeight());
                    QuickMenuFloatingView.this.mIsInitScrollPanel = true;
                }
            }
        };
        this.mDisplayRunnable = new Runnable() { // from class: com.xiaopeng.systemui.quickmenu.QuickMenuFloatingView.7
            @Override // java.lang.Runnable
            public void run() {
                if (!QuickMenuFloatingView.this.isFullScreenPanel()) {
                    String str = QuickMenuFloatingView.this.TAG;
                    Log.d(str, "mAnimRunnable floating view  panelHeight:" + QuickMenuFloatingView.this.mPanelView.getHeight() + " " + QuickMenuFloatingView.this.getScrollY() + " " + QuickMenuFloatingView.this.mRootView.getScrollY() + " mIsInitScrollPanel:" + QuickMenuFloatingView.this.mIsInitScrollPanel);
                    QuickMenuFloatingView quickMenuFloatingView = QuickMenuFloatingView.this;
                    quickMenuFloatingView.startAnimator(quickMenuFloatingView.mRootView.getScrollY(), 0);
                }
            }
        };
        this.mPanelInTapRegion = false;
        this.mScreenHeight = 0;
        this.TAG += "_" + screenIndex;
        this.mThemeViewModel = ThemeViewModel.create(context, attrs, defStyleAttr, defStyleRes);
        this.mThemeViewModel.setCallback(new ThemeViewModel.OnCallback() { // from class: com.xiaopeng.systemui.quickmenu.QuickMenuFloatingView.4
            @Override // com.xiaopeng.libtheme.ThemeViewModel.OnCallback
            public void onThemeChanged() {
                Log.d(QuickMenuFloatingView.this.TAG, "onThemeChanged callback");
                QuickMenuFloatingView.this.themeChanged();
            }
        });
        if (this.mIfUseNewQsPanel.booleanValue()) {
            this.mQsLayoutHelper = new QsLayoutHelper(context);
        } else {
            this.mQuickMenuViewHolderPresenter = QuickMenuHolderPresenter.getInstance();
        }
        init(context, screenIndex);
    }

    public void init(Context context, int screenId) {
        Log.i(this.TAG, "onCreate");
        this.mRootView = LayoutInflater.from(context).inflate(R.layout.activity_quick_menu, (ViewGroup) null);
        addView(this.mRootView);
        this.mQuickMenuLayout = (ViewGroup) this.mRootView.findViewById(R.id.quick_menu_layout);
        this.mQuickMenuBar = (ImageView) this.mRootView.findViewById(R.id.quick_menu_bar_iv);
        this.mQuickMenuBarLayout = (ViewGroup) this.mRootView.findViewById(R.id.quick_menu_bar);
        this.mPanelView = this.mRootView.findViewById(R.id.quick_menu_panel);
        this.mPanelViewEmpty = this.mRootView.findViewById(R.id.quick_menu_panel_empty);
        View view = this.mPanelViewEmpty;
        if (view != null) {
            view.setOnTouchListener(new View.OnTouchListener() { // from class: com.xiaopeng.systemui.quickmenu.QuickMenuFloatingView.5
                private boolean alwaysInTapRegion;
                private int emptyTouchY;

                @Override // android.view.View.OnTouchListener
                public boolean onTouch(View view2, MotionEvent event) {
                    Log.d(QuickMenuFloatingView.this.TAG, "quickmenu click empty");
                    String str = QuickMenuFloatingView.this.TAG;
                    Log.d(str, "empty onTouchEvent event:" + event);
                    int y = (int) event.getY();
                    int action = event.getAction();
                    if (action == 0) {
                        if (QuickMenuFloatingView.this.getScrollY() != 0) {
                            Log.d(QuickMenuFloatingView.this.TAG, "empty onTouchEvent moved and down");
                            this.alwaysInTapRegion = false;
                        } else {
                            this.alwaysInTapRegion = true;
                        }
                        this.emptyTouchY = y;
                    } else if (action != 1) {
                        if (action == 2 && Math.abs(this.emptyTouchY - y) > QuickMenuFloatingView.this.mTouchSlop) {
                            this.alwaysInTapRegion = false;
                        }
                    } else {
                        String str2 = QuickMenuFloatingView.this.TAG;
                        Log.d(str2, "empty onTouchEvent ACTION_UP mAlwaysInTapRegion:" + this.alwaysInTapRegion);
                        if (this.alwaysInTapRegion) {
                            QuickMenuFloatingView.this.autoHide();
                        }
                    }
                    return true;
                }
            });
        }
        this.mScreenId = screenId;
        if (this.mIfUseNewQsPanel.booleanValue()) {
            View view2 = this.mQsLayoutHelper.getQsLayout(screenId);
            this.mQuickMenuLayout.addView(view2);
        } else {
            View view3 = this.mQuickMenuViewHolderPresenter.initView(context, this, screenId);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-2, -2);
            lp.addRule(14);
            this.mQuickMenuLayout.addView(view3, lp);
        }
        ViewConfiguration config = ViewConfiguration.get(context);
        this.mTouchSlop = config.getScaledTouchSlop();
        this.mMaxVelocity = config.getScaledMinimumFlingVelocity();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        View view;
        super.onAttachedToWindow();
        ThemeViewModel themeViewModel = this.mThemeViewModel;
        if (themeViewModel != null) {
            themeViewModel.onAttachedToWindow(this);
        }
        Log.d(this.TAG, "onAttachedToWindow");
        if (!this.mIfUseNewQsPanel.booleanValue()) {
            QuickMenuHolderPresenter quickMenuHolderPresenter = this.mQuickMenuViewHolderPresenter;
            quickMenuHolderPresenter.onStart("quickmenu" + this.mScreenId);
        }
        timeOut("onAttachedToWindow");
        if (!isFullScreenPanel() && (view = this.mRootView) != null) {
            view.post(this.mScrollRunnable);
        } else {
            this.mIsInitScrollPanel = true;
        }
        themeChanged();
    }

    public void joinTouchEvent(MotionEvent event) {
        onTouchEvent(event);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0) {
            cancelTimeOut("ACTION_DOWN");
        } else if (ev.getAction() == 1) {
            timeOut("ACTION_UP");
            String str = this.TAG;
            Log.i(str, "dispatchTouchEvent ACTION_UP floating view dismiss!! " + getVisibility() + " " + getPanelScrollY() + " " + getPanelHeight());
            if ((getVisibility() != 0 || getPanelScrollY() >= getPanelHeight() || isPanelAreaZero()) && this.mQuickMenuFloatingVisibleChangeListener != null) {
                Log.i(this.TAG, "dispatchTouchEvent ACTION_UP floating view dismiss!! ");
                this.mQuickMenuFloatingVisibleChangeListener.visibility(false);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isPanelAreaZero() {
        return !isFullScreenPanel() ? this.mPanelView.getHeight() == 0 && this.mPanelView.getWidth() == 0 : getHeight() == 0 && getWidth() == 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isFullScreenPanel() {
        return this.mPanelViewEmpty == null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scrollPanelTo(int x, int y) {
        if (!isFullScreenPanel()) {
            this.mRootView.scrollTo(0, y);
            scrollTo(0, 0);
            return;
        }
        scrollTo(0, y);
    }

    private int getTouchScale() {
        if (!isFullScreenPanel()) {
            return 10;
        }
        return 6;
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x002b, code lost:
        if (r3 != 6) goto L12;
     */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.view.MotionEvent r11) {
        /*
            Method dump skipped, instructions count: 475
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.systemui.quickmenu.QuickMenuFloatingView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void startAnimator(final int currentY, final int targetY) {
        if (currentY == targetY) {
            Log.d(this.TAG, "startAnimator but no target return!");
            return;
        }
        String str = this.TAG;
        Log.d(str, "startAnimator currentY:" + currentY + " targetY:" + targetY);
        ValueAnimator valueAnimator = this.mValueAnimator;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            this.mValueAnimator.cancel();
        }
        this.mValueAnimator = ValueAnimator.ofInt(currentY, targetY);
        this.mValueAnimator.setDuration(this.TIME_OUT_ANIM);
        this.mValueAnimator.setInterpolator(new LinearInterpolator());
        this.mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.systemui.quickmenu.QuickMenuFloatingView.8
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                int moveY = ((Integer) animation.getAnimatedValue()).intValue();
                QuickMenuFloatingView.this.scrollPanelTo(0, moveY);
                QuickMenuFloatingView.this.updateAlpha(currentY - targetY >= 0 ? 1 : 0, moveY);
            }
        });
        this.mValueAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.xiaopeng.systemui.quickmenu.QuickMenuFloatingView.9
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                QuickMenuFloatingView.this.animatorEnd(currentY, targetY);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                QuickMenuFloatingView.this.animatorEnd(currentY, targetY);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                String str2 = QuickMenuFloatingView.this.TAG;
                Log.d(str2, "animation start " + targetY);
                if (QuickMenuFloatingView.this.mQuickMenuFloatingVisibleChangeListener != null) {
                    QuickMenuFloatingView.this.mQuickMenuFloatingVisibleChangeListener.willDropDown(targetY == 0);
                }
            }
        });
        this.mValueAnimator.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void animatorEnd(int currentY, int targetY) {
        scrollPanelTo(0, targetY);
        updateAlpha(currentY - targetY >= 0 ? 1 : 0, targetY);
        this.mValueAnimator.removeAllUpdateListeners();
        this.mValueAnimator.removeAllListeners();
        this.mValueAnimator = null;
    }

    public void cancelAnimator() {
        ValueAnimator valueAnimator = this.mValueAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    private float getAlphaDriver() {
        if (!isFullScreenPanel()) {
            return 0.96875f;
        }
        return 0.75f;
    }

    private float getAlphaSpan() {
        if (!isFullScreenPanel()) {
            return CarModelsManager.getConfig().getQuickMenuOpaquePos();
        }
        return 0.5f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAlpha(int direct, int currentScrollY) {
        int panelHeight = getPanelHeight();
        if (panelHeight == 0) {
            return;
        }
        float alpha = ((panelHeight * getAlphaDriver()) - currentScrollY) / (panelHeight * getAlphaSpan());
        String str = this.TAG;
        Log.d(str, "updateAlpha " + alpha + " " + currentScrollY + " panelHeight:" + panelHeight);
        if (alpha > 1.0f) {
            alpha = 1.0f;
        }
        if (alpha < 0.0f) {
            alpha = 0.0f;
        }
        if (alpha > 0.0f) {
            QuickMenuGuide.getInstance().disableQuickMenuGuide();
        }
        this.mQuickMenuLayout.setAlpha(alpha);
        this.mQuickMenuBar.setAlpha(alpha);
        ViewGroup viewGroup = this.mQuickMenuBarLayout;
        if (viewGroup != null) {
            viewGroup.setAlpha(alpha);
        }
        View view = this.mRootView;
        if (view != null) {
            view.setAlpha(alpha);
        }
        if (currentScrollY == panelHeight && direct == 0) {
            if (this.mQuickMenuFloatingVisibleChangeListener != null) {
                Log.d(this.TAG, "floating view dismiss");
                this.mQuickMenuFloatingVisibleChangeListener.visibility(false);
            }
            this.mHandler.removeCallbacks(this.mUpBIRunnable);
            this.mHandler.postDelayed(this.mUpBIRunnable, 200L);
        } else if (alpha == 1.0f && getPanelScrollY() == 0 && direct == 1) {
            this.mHandler.removeCallbacks(this.mDownBIRunnable);
            this.mHandler.postDelayed(this.mDownBIRunnable, 200L);
        }
    }

    @Override // android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ThemeViewModel themeViewModel = this.mThemeViewModel;
        if (themeViewModel != null) {
            themeViewModel.onConfigurationChanged(this, newConfig);
        }
        String str = this.TAG;
        Log.d(str, "quickmenu isThemeChanged:" + ThemeManager.isThemeChanged(newConfig));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void themeChanged() {
        Log.d(this.TAG, "quickmenu themeChanged refresh");
        if (!isFullScreenPanel()) {
            this.mRootView.setBackground(getContext().getDrawable(R.drawable.quick_menu_panel_mask_selector));
            this.mPanelView.setBackground(getContext().getDrawable(R.drawable.quick_menu_bg_bottom_selector));
            this.mQuickMenuBar.setBackground(getContext().getDrawable(R.drawable.quick_menu_bar_background));
        } else {
            this.mQuickMenuLayout.setBackground(getContext().getDrawable(R.drawable.bg_qs_rootview));
        }
        if (!this.mIfUseNewQsPanel.booleanValue()) {
            this.mQuickMenuViewHolderPresenter.themeChanged();
        }
    }

    private void cancelTimeOut(String from) {
        this.mHandler.removeCallbacks(this.mDismissRunnable);
        log("cancelTimeOut " + from);
    }

    private void timeOut(String from) {
        cancelTimeOut(" timeOut reset");
        this.mHandler.postDelayed(this.mDismissRunnable, 10000L);
        log("timeOut " + from);
    }

    private void log(String msg) {
        Log.d(this.TAG, msg);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(this.TAG, "onDetachedFromWindow");
        ThemeViewModel themeViewModel = this.mThemeViewModel;
        if (themeViewModel != null) {
            themeViewModel.onDetachedFromWindow(this);
        }
        cancelAnimator();
        if (!this.mIfUseNewQsPanel.booleanValue()) {
            QuickMenuHolderPresenter quickMenuHolderPresenter = this.mQuickMenuViewHolderPresenter;
            quickMenuHolderPresenter.onStop("quickmenu" + this.mScreenId);
            this.mQuickMenuViewHolderPresenter.onDestroy();
        }
        this.mHandler.removeCallbacks(this.mDismissRunnable);
        this.mHandler.removeCallbacks(this.mDownBIRunnable);
        View view = this.mRootView;
        if (view != null) {
            view.removeCallbacks(this.mScrollRunnable);
            this.mRootView.removeCallbacks(this.mDisplayRunnable);
        }
        this.mIsInitScrollPanel = false;
    }

    public void dismissFloatingView() {
        this.mHandler.post(this.mDismissRunnable);
    }

    public void addQuickMenuFloatingAlphaListener(QuickMenuFloatingVisibleChangeListener listener) {
        this.mQuickMenuFloatingVisibleChangeListener = listener;
    }

    public void removeQuickMenuFloatingAlphaListener() {
        this.mQuickMenuFloatingVisibleChangeListener = null;
    }

    public int getScreenHeight(Context context) {
        if (this.mScreenHeight == 0) {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager wm = (WindowManager) context.getSystemService("window");
            wm.getDefaultDisplay().getRealMetrics(metrics);
            this.mScreenHeight = metrics.heightPixels;
        }
        return this.mScreenHeight;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onShowPress(MotionEvent e) {
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onLongPress(MotionEvent e) {
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d(this.TAG, String.format("Fling e2: %f; velocityX: %f; velocityY: %f", Float.valueOf(e2.getRawX()), Float.valueOf(velocityX), Float.valueOf(velocityY)));
        if (velocityY < -1000.0f) {
            this.mIsFlingUP = true;
        } else if (velocityY > 500.0f) {
            this.mIsFlingDown = true;
        }
        return true;
    }
}
