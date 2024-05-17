package com.xiaopeng.systemui.qs.widgets;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.xui.view.animation.XBesselCurve3Interpolator;
import com.xiaopeng.xui.widget.XTextView;
/* loaded from: classes24.dex */
public class XTileButton extends FrameLayout {
    public static final int HORIZONTAL = 1;
    private static final long PRESS_DURATION = 200;
    private static float SCALE_RULES = 0.97f;
    public static final int VERTICAL = 0;
    private int mBackground;
    private XBesselCurve3Interpolator mBaselCurveIn;
    private XBesselCurve3Interpolator mBaselCurveOut;
    private int mImage;
    ImageView mImageView;
    private boolean mIsLarge;
    private int mOrientation;
    ImageView mRightCornerSign;
    ObjectAnimator mScaleXAnimator;
    ObjectAnimator mScaleYAnimator;
    private String mText;
    private int mTextColor;
    TextView mTextView;
    ThemeViewModel mThemeViewModel;
    View mTileLayout;
    private int mTitleColor;
    XTextView mTitleView;

    public XTileButton(Context context) {
        this(context, null);
    }

    public XTileButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XTileButton(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public XTileButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mImage = -1;
        this.mBackground = -1;
        this.mBaselCurveIn = new XBesselCurve3Interpolator(0.0f, 0.56f, 0.46f, 1.0f);
        this.mBaselCurveOut = new XBesselCurve3Interpolator(0.76f, 0.0f, 0.24f, 1.0f);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XTileButton, defStyleAttr, defStyleRes);
        this.mImage = a.getResourceId(1, -1);
        this.mBackground = a.getResourceId(0, -1);
        this.mText = a.getString(4);
        this.mTextColor = a.getResourceId(5, -1);
        this.mOrientation = a.getInt(3, 0);
        this.mIsLarge = a.getBoolean(2, false);
        a.recycle();
        this.mThemeViewModel = ThemeViewModel.create(context, attrs, defStyleAttr, defStyleRes, null);
        init();
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (ThemeManager.isThemeChanged(newConfig)) {
            refreshTheme();
        }
    }

    public void setImageLevel(int level) {
        this.mImageView.setImageLevel(level);
    }

    public void refreshTheme() {
        ImageView imageView;
        View view = this.mTileLayout;
        if (view != null && this.mBackground != -1) {
            view.setBackground(getContext().getDrawable(this.mBackground));
        }
        if (this.mTextView != null && this.mTextColor > 0) {
            ColorStateList textColor = getResources().getColorStateList(this.mTextColor, getContext().getTheme());
            this.mTextView.setTextColor(textColor);
        }
        if (this.mTitleView != null && this.mTitleColor > 0) {
            ColorStateList textColor2 = getResources().getColorStateList(this.mTitleColor, getContext().getTheme());
            this.mTitleView.setTextColor(textColor2);
        }
        if (this.mImage != -1 && (imageView = this.mImageView) != null) {
            imageView.setImageDrawable(getContext().getDrawable(this.mImage));
        }
        ImageView imageView2 = this.mRightCornerSign;
        if (imageView2 != null) {
            imageView2.setImageDrawable(getContext().getDrawable(R.drawable.ic_quickmenu_right_corner_sign));
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ObjectAnimator objectAnimator = this.mScaleXAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        ObjectAnimator objectAnimator2 = this.mScaleYAnimator;
        if (objectAnimator2 != null) {
            objectAnimator2.cancel();
        }
    }

    private void init() {
        if (this.mIsLarge) {
            this.mTileLayout = LayoutInflater.from(getContext()).inflate(R.layout.layout_toggle_tile_large, (ViewGroup) this, false);
        } else if (this.mOrientation == 0) {
            this.mTileLayout = LayoutInflater.from(getContext()).inflate(R.layout.layout_toggle_tile_vertical, (ViewGroup) this, false);
        } else {
            this.mTileLayout = LayoutInflater.from(getContext()).inflate(R.layout.layout_toggle_tile_horizontal, (ViewGroup) this, false);
        }
        if (this.mBackground != -1) {
            this.mTileLayout.setBackground(getContext().getDrawable(this.mBackground));
        }
        this.mImageView = (ImageView) this.mTileLayout.findViewById(R.id.btn);
        if (this.mImage != -1) {
            this.mImageView.setImageDrawable(getContext().getDrawable(this.mImage));
        }
        this.mTextView = (TextView) this.mTileLayout.findViewById(R.id.toggle_button_text);
        this.mTextView.setText(this.mText);
        if (this.mTextView != null && this.mTextColor > 0) {
            ColorStateList textColor = getResources().getColorStateList(this.mTextColor, getContext().getTheme());
            this.mTextView.setTextColor(textColor);
        }
        this.mTitleView = (XTextView) this.mTileLayout.findViewById(R.id.toggle_button_title);
        this.mRightCornerSign = (ImageView) this.mTileLayout.findViewById(R.id.right_corner_sign);
        addView(this.mTileLayout);
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setPivotX(w / 2);
        setPivotY(h / 2);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            int action = event.getAction();
            if (action == 0) {
                startAnimate(1.0f, SCALE_RULES, this.mBaselCurveIn);
            } else if (action == 1 || action == 3) {
                startAnimate(SCALE_RULES, 1.0f, this.mBaselCurveOut);
            }
            return super.dispatchTouchEvent(event);
        }
        return true;
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

    public void setImageResource(int iconRes) {
        this.mImage = iconRes;
        if (this.mImage != -1) {
            this.mImageView.setImageDrawable(getContext().getDrawable(this.mImage));
        }
    }

    public void setTextRes(int textRes) {
        this.mTextView.setText(textRes);
    }

    public void setTitleRes(int textRes) {
        this.mTitleView.setText(textRes);
    }

    public void setTitleColor(int color) {
        ColorStateList textColor = getResources().getColorStateList(color, getContext().getTheme());
        this.mTitleView.setTextColor(textColor);
        this.mTitleColor = color;
    }

    public void setBackgroundRes(int backgroundRes) {
        this.mBackground = backgroundRes;
        if (this.mBackground != -1) {
            this.mTileLayout.setBackground(getContext().getDrawable(this.mBackground));
        }
    }

    public void setTextColor(int color) {
        ColorStateList textColor = getResources().getColorStateList(color, getContext().getTheme());
        this.mTextView.setTextColor(textColor);
        this.mTextColor = color;
    }

    @Override // android.view.View
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        setChildSelected(this, selected);
    }

    private void setChildSelected(ViewGroup viewGroup, boolean selected) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                setChildSelected((ViewGroup) view, selected);
            }
            view.setSelected(selected);
        }
    }

    @Override // android.view.View
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setChildEnabled(this, enabled);
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

    public void setRightCornerSignShow() {
        ImageView imageView = this.mRightCornerSign;
        if (imageView != null) {
            imageView.setVisibility(0);
        }
    }

    public void setWidthHeight(int width, int height) {
        ViewGroup.LayoutParams lp = this.mTileLayout.getLayoutParams();
        lp.width = width;
        lp.height = height;
        this.mTileLayout.setLayoutParams(lp);
    }

    public void setLarge() {
        this.mIsLarge = true;
        init();
    }

    public void setImageGone(boolean ifGone) {
        this.mImageView.setVisibility(ifGone ? 8 : 0);
    }
}
