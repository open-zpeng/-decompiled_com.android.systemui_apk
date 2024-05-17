package com.xiaopeng.systemui.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.systemui.controller.ThemeController;
/* loaded from: classes24.dex */
public class XTabLayout extends AlphaOptimizedLinearLayout {
    private static final int BUTTON_PADDING = 4;
    private static final int BUTTON_RADIUS = 28;
    private static final long DURATION = 300;
    private static final float INDICATOR_DAY_HEIGHT_PERCENT = 1.0f;
    private static final double K = 1.048d;
    private static final double K2 = 4.648d;
    public static final int STYLE_DAY = 1;
    public static final int STYLE_NIGHT = 2;
    private static final String TAG = "XTabLayout";
    private final int MARGIN_DAY;
    private Paint mBlurPaint;
    private Paint mBlurPaint2;
    private View.OnClickListener mChildClickListener;
    private int mCurrentEnd;
    private int mCurrentEnd2;
    private int mCurrentStart;
    private int mCurrentStart2;
    private float mDivideValue;
    private boolean mIndicatorAnimatorEnable;
    private int mIndicatorColor;
    private int mIndicatorColor2;
    private boolean mIndicatorDayNightDiff;
    private float mIndicatorHeight;
    private float mIndicatorMarginBottom;
    private float mIndicatorMaxHeight;
    private float mIndicatorMinHeight;
    private int mIndicatorShadowColor;
    private int mIndicatorShadowColor2;
    private float mIndicatorShadowRadius;
    private float mIndicatorShadowRadius2;
    private float mIndicatorWidth;
    private float mIndicatorWidthPercent;
    private boolean mIsDetachedFromWindow;
    private boolean mIsDetachedNightTheme;
    @Nullable
    private OnTabChangeListener mOnTabChangeListener;
    private int mPaddingNight;
    private Paint mPaint;
    private Paint mPaint2;
    private int mSelectTabIndex;
    private int mStyle;
    private boolean mTabCustomBackground;
    private boolean mTabsBarStyle;
    private int mTempEnd;
    private int mTempEnd2;
    private int mTempStart;
    private int mTempStart2;
    private int[] mTitleIcons;
    private CharSequence[] mTitleString;
    private int mTitleTextColorRes;
    private ColorStateList mTitleTextColorStateList;
    private float mTitleTextSize;
    private int mToEnd;
    private int mToEnd2;
    private int mToStart;
    private int mToStart2;
    private ValueAnimator mValueAnimator;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public interface OnMoveIndicatorListener {
        void onEnd();

        void onStart();
    }

    /* loaded from: classes24.dex */
    public interface OnTabChangeListener {
        boolean onInterceptTabChange(XTabLayout xTabLayout, int i, boolean z, boolean z2);

        void onTabChangeEnd(XTabLayout xTabLayout, int i, boolean z, boolean z2);

        void onTabChangeStart(XTabLayout xTabLayout, int i, boolean z, boolean z2);
    }

    public XTabLayout(Context context) {
        this(context, null);
    }

    public XTabLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XTabLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public XTabLayout(android.content.Context r17, @androidx.annotation.Nullable android.util.AttributeSet r18, int r19, int r20) {
        /*
            Method dump skipped, instructions count: 421
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.systemui.ui.widget.XTabLayout.<init>(android.content.Context, android.util.AttributeSet, int, int):void");
    }

    private void log(@Nullable Object obj) {
        Log.d(TAG, NavigationBarInflaterView.KEY_CODE_START + hashCode() + ") " + obj);
    }

    @Nullable
    private View getSelectView() {
        return getChildAt(this.mSelectTabIndex);
    }

    public void setStyle(int style) {
        int[] iArr;
        this.mIsDetachedNightTheme = ThemeController.isNightMode(getContext());
        if (!this.mIndicatorDayNightDiff) {
            style = 1;
        }
        this.mStyle = style;
        if (this.mTabsBarStyle) {
            this.mStyle = 2;
        }
        if (!this.mTabCustomBackground) {
            setBackground(getContext().getDrawable(ThemeController.isNightMode(getContext()) ? R.drawable.x_tabs_layout_background : R.drawable.x_tabs_layout_background_day));
        }
        if (this.mStyle == 2) {
            int i = this.mPaddingNight;
            setPadding(i, 0, i, 0);
        } else {
            setPadding(0, 0, 0, 0);
        }
        if (this.mTitleTextColorRes > 0) {
            for (int i2 = 0; i2 < getChildCount(); i2++) {
                View view = getChildAt(i2);
                if (view instanceof TextView) {
                    ColorStateList textColor = getResources().getColorStateList(this.mTitleTextColorRes, getContext().getTheme());
                    ((TextView) view).setTextColor(textColor);
                } else if ((view instanceof ImageView) && (iArr = this.mTitleIcons) != null && iArr.length > i2) {
                    ((ImageView) view).setImageResource(iArr[i2]);
                }
            }
        }
        moveIndicatorTo(false, null);
    }

    private void init() {
        this.mPaint.setStrokeWidth(0.0f);
        this.mPaint.setColor(this.mIndicatorColor);
        this.mBlurPaint.setStrokeWidth(0.0f);
        this.mBlurPaint.setColor(this.mIndicatorShadowColor);
        if (Build.VERSION.SDK_INT <= 26) {
            setLayerType(1, this.mBlurPaint);
        }
        this.mPaint2.setStrokeWidth(0.0f);
        this.mPaint2.setColor(this.mIndicatorColor2);
        this.mBlurPaint2.setStrokeWidth(0.0f);
        this.mBlurPaint2.setColor(this.mIndicatorShadowColor2);
        if (Build.VERSION.SDK_INT <= 26) {
            setLayerType(1, this.mBlurPaint2);
        }
        CharSequence[] charSequenceArr = this.mTitleString;
        if (charSequenceArr != null && charSequenceArr.length > 0) {
            for (CharSequence title : charSequenceArr) {
                addTab(title);
            }
        } else {
            int[] iArr = this.mTitleIcons;
            if (iArr != null && iArr.length > 0) {
                int i = 0;
                while (true) {
                    int[] iArr2 = this.mTitleIcons;
                    if (i >= iArr2.length) {
                        break;
                    }
                    addTab(iArr2[i]);
                    i++;
                }
            }
        }
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            getChildAt(i2).setOnClickListener(this.mChildClickListener);
        }
        setOnHierarchyChangeListener(new AnonymousClass2());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.xiaopeng.systemui.ui.widget.XTabLayout$2  reason: invalid class name */
    /* loaded from: classes24.dex */
    public class AnonymousClass2 implements ViewGroup.OnHierarchyChangeListener {
        AnonymousClass2() {
        }

        private void refreshIndicator(final boolean change) {
            XTabLayout.this.post(new Runnable() { // from class: com.xiaopeng.systemui.ui.widget.XTabLayout.2.1
                @Override // java.lang.Runnable
                public void run() {
                    XTabLayout.this.moveIndicatorTo(true, new OnMoveIndicatorListener() { // from class: com.xiaopeng.systemui.ui.widget.XTabLayout.2.1.1
                        @Override // com.xiaopeng.systemui.ui.widget.XTabLayout.OnMoveIndicatorListener
                        public void onStart() {
                            if (XTabLayout.this.mOnTabChangeListener != null) {
                                XTabLayout.this.mOnTabChangeListener.onTabChangeStart(XTabLayout.this, XTabLayout.this.mSelectTabIndex, change, false);
                            }
                        }

                        @Override // com.xiaopeng.systemui.ui.widget.XTabLayout.OnMoveIndicatorListener
                        public void onEnd() {
                            if (XTabLayout.this.mOnTabChangeListener != null) {
                                XTabLayout.this.mOnTabChangeListener.onTabChangeEnd(XTabLayout.this, XTabLayout.this.mSelectTabIndex, change, false);
                            }
                        }
                    });
                }
            });
        }

        @Override // android.view.ViewGroup.OnHierarchyChangeListener
        public void onChildViewAdded(View parent, View child) {
            child.setOnClickListener(XTabLayout.this.mChildClickListener);
            if (XTabLayout.this.mSelectTabIndex < 0) {
                XTabLayout xTabLayout = XTabLayout.this;
                xTabLayout.mSelectTabIndex = xTabLayout.indexOfChild(child);
                child.setSelected(true);
            }
            Object tag = child.getTag();
            refreshIndicator(tag == null ? false : ((Boolean) tag).booleanValue());
        }

        @Override // android.view.ViewGroup.OnHierarchyChangeListener
        public void onChildViewRemoved(View parent, View child) {
            child.setOnClickListener(null);
            Object tag = child.getTag();
            refreshIndicator(tag == null ? false : ((Boolean) tag).booleanValue());
        }
    }

    public boolean isIndicatorAnimatorEnable() {
        return this.mIndicatorAnimatorEnable;
    }

    public void setIndicatorAnimatorEnable(boolean indicatorAnimatorEnable) {
        this.mIndicatorAnimatorEnable = indicatorAnimatorEnable;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        if (this.mSelectTabIndex != -1) {
            int i = this.mStyle;
            if (i == 1) {
                drawDayIndicator(canvas);
            } else if (i == 2) {
                drawNightIndicator(canvas);
            }
        }
        super.dispatchDraw(canvas);
    }

    public int addTab(CharSequence title, int index) {
        TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.x_tab_layout_title_view, (ViewGroup) this, false);
        textView.setText(title);
        textView.setTextColor(this.mTitleTextColorStateList);
        textView.setTextSize(0, this.mTitleTextSize);
        textView.setTag(Boolean.valueOf(index == this.mSelectTabIndex));
        int i = this.mSelectTabIndex;
        if (index <= i) {
            this.mSelectTabIndex = i + 1;
        }
        textView.setSoundEffectsEnabled(isSoundEffectsEnabled());
        addView(textView, index);
        return index;
    }

    public int addTab(int iconId, int index) {
        AnimatedImageView imageView = new AnimatedImageView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -1, 1.0f);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageResource(iconId);
        imageView.setTag(Boolean.valueOf(index == this.mSelectTabIndex));
        int i = this.mSelectTabIndex;
        if (index <= i) {
            this.mSelectTabIndex = i + 1;
        }
        imageView.setSoundEffectsEnabled(isSoundEffectsEnabled());
        addView(imageView, index);
        return index;
    }

    public boolean isTabClickable(int index) {
        return getChildAt(index).isClickable();
    }

    public boolean isAllTabClickable() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (!view.isClickable()) {
                return false;
            }
        }
        return true;
    }

    public void setAllTabClickable(boolean clickable) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            view.setClickable(clickable);
        }
    }

    public void setTabClickable(int index, boolean clickable) {
        getChildAt(index).setClickable(clickable);
    }

    public int addTab(CharSequence title) {
        int index = getChildCount();
        return addTab(title, index);
    }

    public int addTab(int iconId) {
        int index = getChildCount();
        return addTab(iconId, index);
    }

    public void removeTab(int index, int selectIndex) {
        if (index < getTabCount() && selectIndex < getTabCount()) {
            if (index == selectIndex) {
                removeTab(index);
                return;
            }
            boolean change = selectIndex == this.mSelectTabIndex;
            if (change) {
                getChildAt(this.mSelectTabIndex).setSelected(false);
                getChildAt(selectIndex).setSelected(true);
            }
            this.mSelectTabIndex = selectIndex;
            getChildAt(index).setTag(Boolean.valueOf(change));
            removeViewAt(index);
        }
    }

    public void removeTab(int index) {
        View targetView = getChildAt(index);
        if (targetView == null) {
            throw new IllegalArgumentException("targetView is not exits. index = " + index + ", tabCount = " + getChildCount());
        }
        boolean change = this.mSelectTabIndex == index;
        if (this.mSelectTabIndex < getTabCount() - 1) {
            getChildAt(this.mSelectTabIndex).setSelected(false);
            getChildAt(index).setTag(Boolean.valueOf(change));
            removeViewAt(index);
            getChildAt(this.mSelectTabIndex).setSelected(true);
            return;
        }
        getChildAt(this.mSelectTabIndex).setSelected(false);
        this.mSelectTabIndex--;
        getChildAt(index).setTag(Boolean.valueOf(change));
        removeViewAt(index);
        View view = getChildAt(this.mSelectTabIndex);
        if (view != null) {
            view.setSelected(true);
        }
    }

    public void selectedNoneTab(boolean animator, final boolean needCallback) {
        View view = getSelectView();
        if (view != null) {
            view.setSelected(false);
        }
        this.mSelectTabIndex = -1;
        moveIndicatorTo(animator, new OnMoveIndicatorListener() { // from class: com.xiaopeng.systemui.ui.widget.XTabLayout.3
            @Override // com.xiaopeng.systemui.ui.widget.XTabLayout.OnMoveIndicatorListener
            public void onStart() {
                if (needCallback && XTabLayout.this.mOnTabChangeListener != null) {
                    XTabLayout.this.mOnTabChangeListener.onTabChangeStart(XTabLayout.this, -1, true, false);
                }
            }

            @Override // com.xiaopeng.systemui.ui.widget.XTabLayout.OnMoveIndicatorListener
            public void onEnd() {
                if (needCallback && XTabLayout.this.mOnTabChangeListener != null) {
                    XTabLayout.this.mOnTabChangeListener.onTabChangeEnd(XTabLayout.this, -1, true, false);
                }
            }
        });
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void selectTab(final int index, boolean animator, final boolean fromUser) {
        boolean shouldNotifyTabChange;
        log("selectTab: index = [" + index + "], animator = [" + animator + "], fromUser = [" + fromUser + "],mSelectTabIndex:" + this.mSelectTabIndex);
        if (index >= getTabCount() || index < 0 || index == this.mSelectTabIndex) {
            return;
        }
        OnTabChangeListener onTabChangeListener = this.mOnTabChangeListener;
        if (onTabChangeListener != null && onTabChangeListener.onInterceptTabChange(this, index, true, fromUser)) {
            return;
        }
        final View targetView = getChildAt(index);
        View currentView = getSelectView();
        if (targetView != currentView) {
            if (targetView != null) {
                targetView.setSelected(true);
            }
            if (currentView != null) {
                currentView.setSelected(false);
            }
            this.mSelectTabIndex = index;
            shouldNotifyTabChange = true;
        } else {
            shouldNotifyTabChange = false;
        }
        final boolean z = shouldNotifyTabChange;
        moveIndicatorTo(animator, new OnMoveIndicatorListener() { // from class: com.xiaopeng.systemui.ui.widget.XTabLayout.4
            @Override // com.xiaopeng.systemui.ui.widget.XTabLayout.OnMoveIndicatorListener
            public void onStart() {
                if (z && XTabLayout.this.mOnTabChangeListener != null) {
                    if (targetView == null) {
                        XTabLayout.this.mOnTabChangeListener.onTabChangeStart(XTabLayout.this, -1, true, fromUser);
                    } else {
                        XTabLayout.this.mOnTabChangeListener.onTabChangeStart(XTabLayout.this, index, true, fromUser);
                    }
                }
            }

            @Override // com.xiaopeng.systemui.ui.widget.XTabLayout.OnMoveIndicatorListener
            public void onEnd() {
                if (z && XTabLayout.this.mOnTabChangeListener != null) {
                    if (targetView == null) {
                        XTabLayout.this.mOnTabChangeListener.onTabChangeEnd(XTabLayout.this, -1, true, fromUser);
                    } else {
                        XTabLayout.this.mOnTabChangeListener.onTabChangeEnd(XTabLayout.this, index, true, fromUser);
                    }
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.ui.widget.AlphaOptimizedLinearLayout, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        log("isThemeChanged:" + ThemeController.isThemeChanged(newConfig) + ",isNight:" + ThemeController.isNightMode(getContext()));
        if (ThemeController.isThemeChanged(newConfig)) {
            setStyle(ThemeController.isNightMode(getContext()) ? 2 : 1);
        }
    }

    private int getTabViewStart(int selectedTabIndex) {
        if (selectedTabIndex < 0 || getWidth() <= 0) {
            return 0;
        }
        if (this.mStyle == 2) {
            int tabWidth = (getWidth() - (this.mPaddingNight * 2)) / getTabCount();
            int start = (selectedTabIndex * tabWidth) + getIndicatorOffset(tabWidth) + this.mPaddingNight;
            return start;
        }
        int start2 = (selectedTabIndex * (getWidth() / getTabCount())) + 0;
        return start2;
    }

    private int getTabViewEnd(int selectedTabIndex) {
        if (selectedTabIndex < 0 || getWidth() <= 0) {
            return 0;
        }
        if (this.mStyle == 2) {
            int tabWidth = (getWidth() - (this.mPaddingNight * 2)) / getTabCount();
            int end = (((selectedTabIndex + 1) * tabWidth) - getIndicatorOffset(tabWidth)) + this.mPaddingNight;
            return end;
        }
        int tabWidth2 = getWidth();
        int end2 = ((selectedTabIndex + 1) * (tabWidth2 / getTabCount())) + 0;
        return end2;
    }

    private int getIndicatorOffset(int tabWidth) {
        float offset;
        float f = this.mIndicatorWidth;
        if (f != 0.0f) {
            offset = (tabWidth - f) / 2.0f;
        } else {
            float f2 = (1.0f - this.mIndicatorWidthPercent) / 2.0f;
            offset = tabWidth * f2;
        }
        return (int) offset;
    }

    private void getIndicatorPosition() {
        int selectedTabIndex = getSelectedTabIndex();
        if (selectedTabIndex < 0) {
            this.mToStart = 0;
            this.mToEnd = 0;
            this.mToStart2 = 0;
            this.mToEnd2 = 0;
            return;
        }
        this.mToStart = getTabViewStart(selectedTabIndex);
        this.mToEnd = getTabViewEnd(selectedTabIndex);
        this.mToStart2 = this.mToStart;
        this.mToEnd2 = this.mToEnd;
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void moveIndicatorTo(boolean animator, @Nullable final OnMoveIndicatorListener listener) {
        if (getTabCount() <= 0) {
            return;
        }
        boolean animator2 = animator && this.mIndicatorAnimatorEnable;
        getIndicatorPosition();
        if (animator2) {
            if (this.mValueAnimator == null) {
                this.mValueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
                this.mValueAnimator.setDuration(DURATION);
                this.mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.systemui.ui.widget.XTabLayout.5
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = ((Float) animation.getAnimatedValue()).floatValue();
                        float p = Math.min(value, XTabLayout.this.mDivideValue) / XTabLayout.this.mDivideValue;
                        if (value < XTabLayout.this.mDivideValue) {
                            XTabLayout xTabLayout = XTabLayout.this;
                            xTabLayout.mIndicatorHeight = (int) (xTabLayout.mIndicatorMaxHeight - ((value / XTabLayout.this.mDivideValue) * (XTabLayout.this.mIndicatorMaxHeight - XTabLayout.this.mIndicatorMinHeight)));
                        } else {
                            XTabLayout xTabLayout2 = XTabLayout.this;
                            xTabLayout2.mIndicatorHeight = (int) (xTabLayout2.mIndicatorMaxHeight + ((((value - XTabLayout.this.mDivideValue) / (1.0f - XTabLayout.this.mDivideValue)) - 1.0f) * (XTabLayout.this.mIndicatorMaxHeight - XTabLayout.this.mIndicatorMinHeight)));
                        }
                        if (XTabLayout.this.mToStart > XTabLayout.this.mCurrentStart) {
                            XTabLayout xTabLayout3 = XTabLayout.this;
                            xTabLayout3.mTempStart = (int) (xTabLayout3.mCurrentStart + (Math.pow(value, XTabLayout.K) * (XTabLayout.this.mToStart - XTabLayout.this.mCurrentStart)));
                            XTabLayout xTabLayout4 = XTabLayout.this;
                            xTabLayout4.mTempEnd = (int) (xTabLayout4.mCurrentEnd + ((XTabLayout.this.mToEnd - XTabLayout.this.mCurrentEnd) * p));
                        } else {
                            XTabLayout xTabLayout5 = XTabLayout.this;
                            xTabLayout5.mTempStart = (int) (xTabLayout5.mCurrentStart + ((XTabLayout.this.mToStart - XTabLayout.this.mCurrentStart) * p));
                            XTabLayout xTabLayout6 = XTabLayout.this;
                            xTabLayout6.mTempEnd = (int) (xTabLayout6.mCurrentEnd + (Math.pow(value, XTabLayout.K) * (XTabLayout.this.mToEnd - XTabLayout.this.mCurrentEnd)));
                        }
                        if (XTabLayout.this.mToStart2 > XTabLayout.this.mCurrentStart2) {
                            XTabLayout xTabLayout7 = XTabLayout.this;
                            xTabLayout7.mTempStart2 = (int) (xTabLayout7.mCurrentStart2 + (Math.pow(value, XTabLayout.K2) * (XTabLayout.this.mToStart2 - XTabLayout.this.mCurrentStart2)));
                            XTabLayout xTabLayout8 = XTabLayout.this;
                            xTabLayout8.mTempEnd2 = (int) (xTabLayout8.mCurrentEnd2 + ((XTabLayout.this.mToEnd2 - XTabLayout.this.mCurrentEnd2) * p));
                        } else {
                            XTabLayout xTabLayout9 = XTabLayout.this;
                            xTabLayout9.mTempStart2 = (int) (xTabLayout9.mCurrentStart2 + ((XTabLayout.this.mToStart2 - XTabLayout.this.mCurrentStart2) * p));
                            XTabLayout xTabLayout10 = XTabLayout.this;
                            xTabLayout10.mTempEnd2 = (int) (xTabLayout10.mCurrentEnd2 + (Math.pow(value, XTabLayout.K2) * (XTabLayout.this.mToEnd2 - XTabLayout.this.mCurrentEnd2)));
                        }
                        XTabLayout.this.invalidate();
                    }
                });
                this.mValueAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.xiaopeng.systemui.ui.widget.XTabLayout.6
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        XTabLayout.this.assignPosition();
                        XTabLayout.this.invalidate();
                    }
                });
                this.mValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            }
            this.mValueAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.xiaopeng.systemui.ui.widget.XTabLayout.7
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    OnMoveIndicatorListener onMoveIndicatorListener = listener;
                    if (onMoveIndicatorListener != null) {
                        onMoveIndicatorListener.onStart();
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    XTabLayout.this.mValueAnimator.removeListener(this);
                    OnMoveIndicatorListener onMoveIndicatorListener = listener;
                    if (onMoveIndicatorListener != null) {
                        onMoveIndicatorListener.onEnd();
                    }
                }
            });
            this.mValueAnimator.start();
            return;
        }
        assignPosition();
        if (listener != null) {
            listener.onStart();
            listener.onEnd();
        }
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void assignPosition() {
        this.mCurrentStart = this.mToStart;
        this.mCurrentEnd = this.mToEnd;
        this.mCurrentStart2 = this.mToStart2;
        this.mCurrentEnd2 = this.mToEnd2;
        int i = this.mCurrentStart;
        this.mTempStart = i;
        this.mTempEnd = this.mCurrentEnd;
        this.mTempStart2 = i;
        this.mTempEnd2 = this.mCurrentEnd2;
    }

    public void updateTabTitle(int index, String title) {
        View childView = getChildAt(index);
        if (childView instanceof TextView) {
            ((TextView) childView).setText(title);
        }
    }

    public CharSequence getTabTitle(int index) {
        View view = getChildAt(index);
        if (view instanceof TextView) {
            TextView tab = (TextView) getChildAt(index);
            return tab.getText();
        }
        return "";
    }

    public void updateTabTitle(int index, @StringRes int title) {
        View childView = getChildAt(index);
        if (childView instanceof TextView) {
            ((TextView) childView).setText(title);
        }
    }

    @Override // android.view.View
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).setEnabled(enabled);
        }
        int i2 = Build.VERSION.SDK_INT;
        if (i2 > 26) {
            int alpha = enabled ? 255 : 92;
            this.mPaint.setAlpha(alpha);
            this.mBlurPaint.setAlpha(alpha);
            this.mPaint2.setAlpha(alpha);
            this.mBlurPaint2.setAlpha(alpha);
            invalidate();
        }
    }

    public void setEnabled(boolean enable, int position) {
        int childCount = getChildCount();
        View view = getChildAt(position);
        if (position < childCount && view != null) {
            view.setEnabled(enable);
            invalidate();
        }
    }

    public boolean isEnabled(int position) {
        int childCount = getChildCount();
        View view = getChildAt(position);
        if (position < childCount && view != null) {
            return view.isEnabled();
        }
        return false;
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        post(new Runnable() { // from class: com.xiaopeng.systemui.ui.widget.XTabLayout.8
            @Override // java.lang.Runnable
            public void run() {
                XTabLayout.this.moveIndicatorTo(false, null);
            }
        });
    }

    public void selectTab(int index, boolean animator) {
        selectTab(index, animator, false);
    }

    public int getSelectedTabIndex() {
        return this.mSelectTabIndex;
    }

    public void selectTab(int index) {
        selectTab(index, true);
    }

    public int getTabCount() {
        return getChildCount();
    }

    private void drawDayIndicator(Canvas canvas) {
        if (!ThemeManager.isNightMode(getContext())) {
            this.mPaint.setMaskFilter(null);
        }
        this.mPaint.setAlpha(isEnabled(this.mSelectTabIndex) ? 255 : 92);
        float indicatorHeight = getHeight() * 1.0f;
        float f = indicatorHeight / 2.0f;
        float top = getHeight() >> 1;
        int i = this.mTempStart;
        int i2 = this.mTempEnd;
        if (i < i2) {
            float diff = indicatorHeight / 2.0f;
            canvas.drawRoundRect(i + 4, (top - diff) + 4.0f, i2, (top + diff) - 4.0f, 28.0f, 28.0f, this.mPaint);
            return;
        }
        float diff2 = indicatorHeight / 2.0f;
        canvas.drawRoundRect(i2 + 4, (top - diff2) + 4.0f, i, (top + diff2) - 4.0f, 28.0f, 28.0f, this.mPaint);
    }

    private void drawNightIndicator(Canvas canvas) {
        if (ThemeManager.isNightMode(getContext())) {
            this.mPaint.setMaskFilter(new BlurMaskFilter(this.mIndicatorShadowRadius, BlurMaskFilter.Blur.SOLID));
            this.mPaint2.setMaskFilter(new BlurMaskFilter(this.mIndicatorShadowRadius2, BlurMaskFilter.Blur.SOLID));
        } else {
            this.mPaint.setMaskFilter(null);
            this.mPaint2.setMaskFilter(null);
        }
        this.mPaint.setAlpha(isEnabled(this.mSelectTabIndex) ? 255 : 92);
        this.mPaint2.setAlpha(isEnabled(this.mSelectTabIndex) ? 255 : 92);
        float f = this.mIndicatorHeight / 2.0f;
        float bottom = getHeight() - this.mIndicatorMarginBottom;
        if (this.mTempStart2 < this.mTempEnd2) {
            float diff = Math.max(this.mIndicatorHeight, 1.0f);
            canvas.drawRoundRect(this.mTempStart2 + 4, (bottom - diff) + 4.0f, this.mTempEnd2 - 4, bottom - 4.0f, 28.0f, 28.0f, this.mPaint2);
            return;
        }
        float diff2 = this.mIndicatorHeight;
        canvas.drawRoundRect(this.mTempEnd2 + 4, (bottom - Math.max(diff2, 1.0f)) + 4.0f, this.mTempStart2 - 4, bottom - 4.0f, 28.0f, 28.0f, this.mPaint2);
    }

    protected int dp(int dp) {
        return (int) TypedValue.applyDimension(1, dp, getResources().getDisplayMetrics());
    }

    protected float dpF(float dp) {
        return TypedValue.applyDimension(1, dp, getResources().getDisplayMetrics());
    }

    public void setOnTabChangeListener(@Nullable OnTabChangeListener onTabChangeListener) {
        this.mOnTabChangeListener = onTabChangeListener;
    }

    /* loaded from: classes24.dex */
    public static abstract class OnTabChangeListenerAdapter implements OnTabChangeListener {
        @Override // com.xiaopeng.systemui.ui.widget.XTabLayout.OnTabChangeListener
        public boolean onInterceptTabChange(XTabLayout tabLayout, int index, boolean tabChange, boolean fromUser) {
            return false;
        }

        @Override // com.xiaopeng.systemui.ui.widget.XTabLayout.OnTabChangeListener
        public void onTabChangeStart(XTabLayout tabLayout, int index, boolean tabChange, boolean fromUser) {
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.ui.widget.AlphaOptimizedLinearLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        log("onAttachedToWindow:" + ThemeController.isNightMode(getContext()));
        if (this.mIsDetachedFromWindow && this.mIsDetachedNightTheme != ThemeController.isNightMode(getContext())) {
            this.mIsDetachedFromWindow = false;
            setStyle(ThemeController.isNightMode(getContext()) ? 2 : 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.ui.widget.AlphaOptimizedLinearLayout, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mIsDetachedFromWindow = true;
        this.mIsDetachedNightTheme = ThemeController.isNightMode(getContext());
    }

    @Override // android.view.View
    public void setSoundEffectsEnabled(boolean soundEffectsEnabled) {
        super.setSoundEffectsEnabled(soundEffectsEnabled);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (view != null) {
                view.setSoundEffectsEnabled(soundEffectsEnabled);
            }
        }
    }
}
