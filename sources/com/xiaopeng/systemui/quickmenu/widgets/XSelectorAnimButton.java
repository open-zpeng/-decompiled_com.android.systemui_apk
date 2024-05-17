package com.xiaopeng.systemui.quickmenu.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.libtheme.ThemeManager;
/* loaded from: classes24.dex */
public class XSelectorAnimButton extends LinearLayout {
    private static final int DEFAULT_PADDING = 4;
    private static final int DEFAULT_TEXT_SIZE = 20;
    private static final long DURATION = 300;
    private static final float LINE_HEIGHT = 50.0f;
    private static final long PRESS_DURATION = 100;
    private static final float SCALE_RULES = 0.97f;
    private float defaultRadius;
    private int mBackground;
    private View.OnClickListener mChildClickListener;
    private float mEndLeft;
    private float mEndLeftRadius;
    private float mEndRightRadius;
    private boolean mIsIcon;
    private int mItemCount;
    private int[] mItemIcons;
    private CharSequence[] mItemText;
    private Paint mLinePaint;
    OnSelectChangedListener mOnSelectChangedListener;
    private Paint mPaint;
    Path mPath;
    private float mPropertyRadius;
    float[] mRadius;
    RectF mRect;
    ObjectAnimator mScaleXAnimator;
    ObjectAnimator mScaleYAnimator;
    private int mSelectTabIndex;
    private float mStartLeft;
    private float mStartLeftRadius;
    private float mStartRightRadius;
    private int mTextColor;
    private final int mTextSize;
    private float mTmpLeft;
    private float mTmpLeftRadius;
    private float mTmpRightRadius;
    private ValueAnimator mValueAnimator;
    private float roundRadius;

    /* loaded from: classes24.dex */
    public interface OnSelectChangedListener {
        void onSelectChanged(XSelectorAnimButton xSelectorAnimButton, int i, boolean z);

        boolean onSelectIntercept(XSelectorAnimButton xSelectorAnimButton, int i, boolean z);
    }

    public XSelectorAnimButton(Context context) {
        this(context, null);
    }

    public XSelectorAnimButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.roundRadius = 16.0f;
        this.defaultRadius = 4.0f;
        this.mPropertyRadius = -1.0f;
        this.mBackground = -1;
        this.mRect = new RectF();
        this.mRadius = new float[8];
        this.mIsIcon = false;
        this.mSelectTabIndex = -1;
        this.mPaint = new Paint(1);
        this.mLinePaint = new Paint(1);
        this.mPath = new Path();
        this.mChildClickListener = new View.OnClickListener() { // from class: com.xiaopeng.systemui.quickmenu.widgets.XSelectorAnimButton.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if ((XSelectorAnimButton.this.mValueAnimator != null && XSelectorAnimButton.this.mValueAnimator.isRunning()) || XSelectorAnimButton.this.indexOfChild(v) == XSelectorAnimButton.this.mSelectTabIndex) {
                    return;
                }
                if (XSelectorAnimButton.this.mOnSelectChangedListener != null) {
                    OnSelectChangedListener onSelectChangedListener = XSelectorAnimButton.this.mOnSelectChangedListener;
                    XSelectorAnimButton xSelectorAnimButton = XSelectorAnimButton.this;
                    if (onSelectChangedListener.onSelectIntercept(xSelectorAnimButton, xSelectorAnimButton.indexOfChild(v), true)) {
                        return;
                    }
                }
                XSelectorAnimButton xSelectorAnimButton2 = XSelectorAnimButton.this;
                xSelectorAnimButton2.selectTab(xSelectorAnimButton2.indexOfChild(v), true, true);
                if (XSelectorAnimButton.this.mOnSelectChangedListener != null) {
                    OnSelectChangedListener onSelectChangedListener2 = XSelectorAnimButton.this.mOnSelectChangedListener;
                    XSelectorAnimButton xSelectorAnimButton3 = XSelectorAnimButton.this;
                    onSelectChangedListener2.onSelectChanged(xSelectorAnimButton3, xSelectorAnimButton3.indexOfChild(v), true);
                }
            }
        };
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XSelectorButton);
        int icons = a.getResourceId(3, 0);
        if (icons != 0) {
            TypedArray typedArray = getResources().obtainTypedArray(icons);
            this.mItemIcons = new int[typedArray.length()];
            for (int i = 0; i < typedArray.length(); i++) {
                this.mItemIcons[i] = typedArray.getResourceId(i, -1);
            }
            typedArray.recycle();
            this.mIsIcon = true;
        }
        this.mItemText = a.getTextArray(4);
        this.mTextSize = a.getDimensionPixelSize(0, 20);
        this.mTextColor = a.getResourceId(1, -1);
        this.mBackground = a.getResourceId(6, -1);
        this.mPropertyRadius = a.getDimensionPixelSize(5, -1);
        float f = this.mPropertyRadius;
        if (f != -1.0f) {
            this.defaultRadius = f;
            this.roundRadius = f;
        }
        a.recycle();
        setWillNotDraw(false);
        init(context);
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setPivotX(w / 2);
        setPivotY(h / 2);
        this.mScaleXAnimator = ObjectAnimator.ofFloat(this, "scaleX", 1.0f, SCALE_RULES);
        this.mScaleYAnimator = ObjectAnimator.ofFloat(this, "scaleY", 1.0f, SCALE_RULES);
        this.mScaleXAnimator.setDuration(PRESS_DURATION);
        this.mScaleYAnimator.setDuration(PRESS_DURATION);
        this.mScaleXAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        this.mScaleYAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        RectF rectF = this.mRect;
        float f = this.mTmpLeft;
        rectF.left = f;
        rectF.right = f + getItemWidth();
        this.mRect.top = getPaddingTop();
        this.mRect.bottom = getHeight() - getPaddingBottom();
        startAnimate(false);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            int action = event.getAction();
            if (action == 0) {
                this.mScaleXAnimator.start();
                this.mScaleYAnimator.start();
            } else if (action == 1 || action == 3) {
                this.mScaleXAnimator.reverse();
                this.mScaleYAnimator.reverse();
            }
            return super.dispatchTouchEvent(event);
        }
        return true;
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (ThemeManager.isThemeChanged(newConfig)) {
            refreshTheme();
            invalidate();
        }
    }

    public void refreshTheme() {
        if (this.mBackground != -1) {
            setBackground(getContext().getDrawable(this.mBackground));
        } else {
            setBackground(getContext().getDrawable(R.drawable.radiobutton_background));
        }
        this.mLinePaint.setColor(getContext().getColor(R.color.divider_line));
        if (this.mPropertyRadius != -1.0f) {
            this.mPaint.setColor(getContext().getColor(R.color.quick_menu_ui_checked_color));
        } else {
            this.mPaint.setColor(getContext().getColor(R.color.radiobutton_checked_color));
        }
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if ((view instanceof TextView) && view != null && this.mTextColor > 0) {
                ColorStateList textColor = getResources().getColorStateList(this.mTextColor, getContext().getTheme());
                ((TextView) view).setTextColor(textColor);
            }
            if (view instanceof ImageView) {
                ((ImageView) view).setImageResource(this.mItemIcons[i]);
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ValueAnimator valueAnimator = this.mValueAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ObjectAnimator objectAnimator = this.mScaleXAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        ObjectAnimator objectAnimator2 = this.mScaleYAnimator;
        if (objectAnimator2 != null) {
            objectAnimator2.cancel();
        }
    }

    private void init(Context context) {
        this.mLinePaint.setStrokeWidth(1.0f);
        this.mLinePaint.setColor(getContext().getColor(R.color.divider_line));
        this.mPaint.setStrokeWidth(0.0f);
        if (this.mPropertyRadius != -1.0f) {
            this.mPaint.setColor(getContext().getColor(R.color.quick_menu_ui_checked_color));
        } else {
            this.mPaint.setColor(getContext().getColor(R.color.radiobutton_checked_color));
        }
        this.mItemCount = this.mIsIcon ? this.mItemIcons.length : this.mItemText.length;
        if (this.mItemCount < 1) {
            throw new IllegalArgumentException("The number of items cannot be less than 1! ");
        }
        if (this.mBackground != -1) {
            setBackground(getContext().getDrawable(this.mBackground));
        } else {
            setBackground(context.getDrawable(R.drawable.radiobutton_background));
        }
        setPadding(4, 4, 4, 4);
        setLayoutDirection(0);
        setGravity(17);
        for (int i = 0; i < this.mItemCount; i++) {
            if (this.mIsIcon) {
                addChild(i, this.mItemIcons[i]);
            } else {
                addChild(i, this.mItemText[i]);
            }
        }
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            getChildAt(i2).setOnClickListener(this.mChildClickListener);
        }
    }

    private void addChild(int index, CharSequence text) {
        TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.x_tab_layout_title_view, (ViewGroup) this, false);
        textView.setText(text);
        if (this.mTextColor > 0) {
            ColorStateList textColor = getResources().getColorStateList(this.mTextColor, getContext().getTheme());
            textView.setTextColor(textColor);
        }
        textView.setTextSize(0, this.mTextSize);
        textView.setSoundEffectsEnabled(isSoundEffectsEnabled());
        addView(textView, index);
    }

    private void addChild(int index, int drawable) {
        ImageView imageView = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.x_selector_button_icon, (ViewGroup) this, false);
        imageView.setImageResource(drawable);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setSoundEffectsEnabled(isSoundEffectsEnabled());
        addView(imageView, index);
    }

    public int getTabCount() {
        return getChildCount();
    }

    private View getSelectView() {
        return getChildAt(this.mSelectTabIndex);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void selectTab(int index, boolean animator, boolean fromUser) {
        if (index >= getTabCount() || index < 0 || index == this.mSelectTabIndex) {
            return;
        }
        View targetView = getChildAt(index);
        View currentView = getSelectView();
        if (targetView != currentView) {
            if (targetView != null) {
                targetView.setSelected(true);
            }
            if (currentView != null) {
                currentView.setSelected(false);
            }
            this.mSelectTabIndex = index;
        }
        startAnimate(animator);
    }

    private void startAnimate(boolean animator) {
        computeEnd();
        if (animator) {
            if (this.mValueAnimator == null) {
                this.mValueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
                this.mValueAnimator.setDuration(DURATION);
                this.mValueAnimator.setInterpolator(new DecelerateInterpolator());
                this.mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.systemui.quickmenu.widgets.XSelectorAnimButton.2
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = ((Float) animation.getAnimatedValue()).floatValue();
                        XSelectorAnimButton xSelectorAnimButton = XSelectorAnimButton.this;
                        xSelectorAnimButton.mTmpLeft = ((xSelectorAnimButton.mEndLeft - XSelectorAnimButton.this.mStartLeft) * value) + XSelectorAnimButton.this.mStartLeft;
                        XSelectorAnimButton xSelectorAnimButton2 = XSelectorAnimButton.this;
                        xSelectorAnimButton2.mTmpLeftRadius = ((xSelectorAnimButton2.mEndLeftRadius - XSelectorAnimButton.this.mStartLeftRadius) * value) + XSelectorAnimButton.this.mStartLeftRadius;
                        XSelectorAnimButton xSelectorAnimButton3 = XSelectorAnimButton.this;
                        xSelectorAnimButton3.mTmpRightRadius = ((xSelectorAnimButton3.mEndRightRadius - XSelectorAnimButton.this.mStartRightRadius) * value) + XSelectorAnimButton.this.mStartRightRadius;
                        XSelectorAnimButton.this.invalidate();
                    }
                });
                this.mValueAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.xiaopeng.systemui.quickmenu.widgets.XSelectorAnimButton.3
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        XSelectorAnimButton.this.correctionData();
                    }
                });
            }
            this.mValueAnimator.start();
            return;
        }
        correctionData();
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void correctionData() {
        float f = this.mEndLeft;
        this.mTmpLeft = f;
        this.mStartLeft = f;
        float f2 = this.mEndLeftRadius;
        this.mTmpLeftRadius = f2;
        float f3 = this.mEndRightRadius;
        this.mTmpRightRadius = f3;
        this.mStartLeftRadius = f2;
        this.mStartRightRadius = f3;
    }

    private void computeStart() {
        int i = this.mSelectTabIndex;
        if (i < 0) {
            this.mStartLeft = 0.0f;
        } else if (i >= 0 && getWidth() > 0) {
            this.mStartLeft = (this.mSelectTabIndex * getItemWidth()) + getPaddingLeft();
        }
    }

    private void computeEnd() {
        int i = this.mSelectTabIndex;
        if (i < 0) {
            this.mEndLeft = 0.0f;
        } else if (i >= 0 && getWidth() > 0) {
            this.mEndLeft = (this.mSelectTabIndex * getItemWidth()) + getPaddingLeft();
        }
        int i2 = this.mSelectTabIndex;
        if (i2 == 0) {
            this.mEndLeftRadius = this.roundRadius;
            this.mEndRightRadius = this.defaultRadius;
        } else if (i2 == this.mItemCount - 1) {
            this.mEndLeftRadius = this.defaultRadius;
            this.mEndRightRadius = this.roundRadius;
        } else {
            float f = this.defaultRadius;
            this.mEndLeftRadius = f;
            this.mEndRightRadius = f;
        }
    }

    private float getItemWidth() {
        return ((getWidth() - getPaddingLeft()) - getPaddingRight()) / this.mItemCount;
    }

    private float getRightLineX() {
        return (getWidth() - getPaddingRight()) - getItemWidth();
    }

    private float getLeftLineX() {
        return getPaddingLeft() + getItemWidth();
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        RectF rectF = this.mRect;
        float f = this.mTmpLeft;
        rectF.left = f;
        rectF.right = f + getItemWidth() + (getPaddingRight() / 2);
        this.mPath.rewind();
        float[] fArr = this.mRadius;
        float f2 = this.mTmpLeftRadius;
        fArr[1] = f2;
        fArr[0] = f2;
        float f3 = this.mTmpRightRadius;
        fArr[3] = f3;
        fArr[2] = f3;
        fArr[5] = f3;
        fArr[4] = f3;
        fArr[7] = f2;
        fArr[6] = f2;
        this.mPath.addRoundRect(this.mRect, fArr, Path.Direction.CW);
        int i = this.mSelectTabIndex;
        if (i != -1) {
            if (i == 0) {
                canvas.drawPath(this.mPath, this.mPaint);
            } else if (i == this.mItemCount - 1) {
                canvas.drawPath(this.mPath, this.mPaint);
            } else {
                canvas.drawPath(this.mPath, this.mPaint);
            }
        }
        super.dispatchDraw(canvas);
    }

    public void setEnableView(boolean enable) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setEnabled(enable);
        }
    }

    @Override // android.view.View
    public void setEnabled(boolean enabled) {
        setEnabled(enabled, true);
    }

    public void setEnabled(boolean enabled, boolean containChild) {
        super.setEnabled(enabled);
        if (containChild) {
            setChildEnabled(this, enabled);
        }
    }

    private void setChildEnabled(ViewGroup viewGroup, boolean enabled) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                setChildEnabled((ViewGroup) view, enabled);
            }
            view.setEnabled(enabled);
        }
    }

    public void setSelectorIndex(int index) {
        selectTab(index, true, false);
    }

    public void setAllUnSelect() {
        View view = getSelectView();
        if (view != null) {
            view.setSelected(false);
        }
        this.mSelectTabIndex = -1;
        invalidate();
    }

    public void setOnSelectChangedListener(OnSelectChangedListener listener) {
        this.mOnSelectChangedListener = listener;
    }

    public void setBackgroundRes(int res) {
        if (res != -1) {
            setBackground(getContext().getDrawable(res));
            this.mBackground = res;
        }
    }
}
