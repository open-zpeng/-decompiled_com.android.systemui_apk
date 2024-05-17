package com.xiaopeng.xui.widget.toggle;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.vui.commons.VuiElementType;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.drawable.XLoadingDrawable;
import com.xiaopeng.xui.widget.XRelativeLayout;
/* loaded from: classes25.dex */
public class XToggleLayout extends XRelativeLayout implements Checkable {
    private static final int[] CHECKED_STATE_SET = {16842912};
    private static final int NO_ALPHA = 255;
    private int mBgId;
    private boolean mBroadcasting;
    private boolean mCheckStarting;
    private boolean mChecked;
    private float mDisabledAlpha;
    private boolean mEnabled;
    private Drawable mIndicatorDrawable;
    private boolean mLoading;
    private XLoadingDrawable mLoadingDrawable;
    private OnCheckedChangeListener mOnCheckedChangeListener;

    /* loaded from: classes25.dex */
    public interface OnCheckedChangeListener {
        void onCheckedChanged(XToggleLayout xToggleLayout, boolean z);

        boolean onInterceptClickCheck(XToggleLayout xToggleLayout);
    }

    public XToggleLayout(Context context) {
        this(context, null);
    }

    public XToggleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.XToggleLayout_Fill);
    }

    public XToggleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.XToggleLayout_Fill);
    }

    public XToggleLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mEnabled = true;
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        setClickable(true);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.XToggleLayout, defStyleAttr, defStyleRes);
        this.mChecked = ta.getBoolean(R.styleable.XToggleLayout_android_checked, false);
        this.mEnabled = ta.getBoolean(R.styleable.XToggleLayout_android_enabled, true);
        super.setEnabled(this.mEnabled);
        this.mDisabledAlpha = ta.getFloat(R.styleable.XToggleLayout_android_disabledAlpha, 0.5f);
        boolean loading = ta.getBoolean(R.styleable.XToggleLayout_loading, false);
        setLoading(loading);
        this.mBgId = ta.getResourceId(R.styleable.XToggleLayout_android_background, 0);
        ta.recycle();
        if (this.mXViewDelegate != null && this.mXViewDelegate.getThemeViewModel() != null) {
            this.mXViewDelegate.getThemeViewModel().setCallback(new ThemeViewModel.OnCallback() { // from class: com.xiaopeng.xui.widget.toggle.-$$Lambda$TotOehcMyM7yuAbECOWjlcf4gEo
                @Override // com.xiaopeng.libtheme.ThemeViewModel.OnCallback
                public final void onThemeChanged() {
                    XToggleLayout.this.updateThemeResource();
                }
            });
        }
        setVuiElementType(VuiElementType.SWITCH);
    }

    public void setLoading(boolean loading) {
        if (this.mLoading != loading) {
            this.mLoading = loading;
            if (this.mLoading) {
                setEnabled(false);
                if (this.mLoadingDrawable == null) {
                    this.mLoadingDrawable = new XLoadingDrawable();
                    this.mLoadingDrawable.onConfigurationChanged(getContext(), null);
                    this.mLoadingDrawable.setCallback(this);
                    this.mLoadingDrawable.setType(1);
                    int width = getMeasuredWidth();
                    int height = getMeasuredHeight();
                    if (width != 0 && height != 0) {
                        this.mLoadingDrawable.setBounds(0, 0, width, height);
                    }
                }
                invalidate();
                return;
            }
            XLoadingDrawable xLoadingDrawable = this.mLoadingDrawable;
            if (xLoadingDrawable != null) {
                xLoadingDrawable.cancelAnimations();
            }
            setEnabled(true);
        }
    }

    public boolean isLoading() {
        return this.mLoading;
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        XLoadingDrawable xLoadingDrawable = this.mLoadingDrawable;
        if (xLoadingDrawable != null) {
            xLoadingDrawable.setBounds(0, 0, w, h);
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        updateReferenceToIndicatorDrawable(getBackground());
        refreshChildrenState();
    }

    @Override // android.view.View
    public void setBackground(Drawable background) {
        super.setBackground(background);
        updateReferenceToIndicatorDrawable(background);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable drawable = this.mIndicatorDrawable;
        if (drawable != null) {
            drawable.setAlpha(isEnabled() ? 255 : (int) (this.mDisabledAlpha * 255.0f));
        }
    }

    private void refreshChildrenState() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child instanceof Checkable) {
                Checkable checkable = (Checkable) child;
                checkable.setChecked(this.mChecked);
                child.setEnabled(this.mEnabled);
            }
        }
    }

    private void checkChildren() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child instanceof Checkable) {
                Checkable checkable = (Checkable) child;
                checkable.setChecked(this.mChecked);
            }
        }
    }

    private void enableChildren() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child instanceof Checkable) {
                child.setEnabled(this.mEnabled);
            }
        }
    }

    @Override // android.view.View
    protected boolean verifyDrawable(@NonNull Drawable who) {
        return who == this.mLoadingDrawable || super.verifyDrawable(who);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        if (this.mLoading) {
            XLoadingDrawable xLoadingDrawable = this.mLoadingDrawable;
            if (xLoadingDrawable != null) {
                xLoadingDrawable.draw(canvas);
                return;
            }
            return;
        }
        super.dispatchDraw(canvas);
    }

    public void setOnCheckedChangeListener(@Nullable OnCheckedChangeListener listener) {
        this.mOnCheckedChangeListener = listener;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    private void updateReferenceToIndicatorDrawable(Drawable backgroundDrawable) {
        if (backgroundDrawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) backgroundDrawable;
            this.mIndicatorDrawable = layerDrawable.findDrawableByLayerId(16908311);
            return;
        }
        this.mIndicatorDrawable = null;
    }

    @Override // android.view.View
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (this.mEnabled != enabled) {
            this.mEnabled = enabled;
            enableChildren();
        }
    }

    public void setChecked(boolean checked) {
        if (this.mCheckStarting) {
            throw new IllegalStateException("Cannot change check state in onInterceptClickCheck");
        }
        if (this.mChecked != checked) {
            this.mChecked = checked;
            checkChildren();
            refreshDrawableState();
            if (this.mBroadcasting) {
                return;
            }
            this.mBroadcasting = true;
            OnCheckedChangeListener onCheckedChangeListener = this.mOnCheckedChangeListener;
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChanged(this, this.mChecked);
            }
            this.mBroadcasting = false;
            updateVui(this);
        }
    }

    @Override // android.view.View
    public boolean performClick() {
        boolean intercept = false;
        this.mCheckStarting = true;
        OnCheckedChangeListener onCheckedChangeListener = this.mOnCheckedChangeListener;
        if (onCheckedChangeListener != null) {
            intercept = onCheckedChangeListener.onInterceptClickCheck(this);
        }
        this.mCheckStarting = false;
        if (!intercept) {
            toggle();
        }
        boolean handled = super.performClick();
        if (!handled) {
            playSoundEffect(0);
        }
        return handled;
    }

    @Override // android.widget.Checkable
    public boolean isChecked() {
        return this.mChecked;
    }

    @Override // android.widget.Checkable
    public void toggle() {
        setChecked(!this.mChecked);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XRelativeLayout, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        XLoadingDrawable xLoadingDrawable = this.mLoadingDrawable;
        if (xLoadingDrawable != null) {
            xLoadingDrawable.onConfigurationChanged(getContext(), newConfig);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XRelativeLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        XLoadingDrawable xLoadingDrawable = this.mLoadingDrawable;
        if (xLoadingDrawable != null) {
            xLoadingDrawable.onConfigurationChanged(getContext(), getResources().getConfiguration());
            this.mLoadingDrawable.cancelAnimations();
            invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XRelativeLayout, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        XLoadingDrawable xLoadingDrawable = this.mLoadingDrawable;
        if (xLoadingDrawable != null) {
            xLoadingDrawable.cancelAnimations();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateThemeResource() {
        setBackground(getContext().getDrawable(this.mBgId));
        refreshDrawableState();
    }
}
