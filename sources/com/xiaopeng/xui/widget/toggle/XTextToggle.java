package com.xiaopeng.xui.widget.toggle;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.CheckedTextView;
import android.widget.RelativeLayout;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.view.XViewDelegate;
import com.xiaopeng.xui.view.font.XFontScaleHelper;
@Deprecated
/* loaded from: classes25.dex */
public class XTextToggle extends XToggleLayout {
    private CheckedTextView mContentTextView;
    private int mDrawableStartResId;
    private int mTextColorResId;
    private CharSequence mTextOff;
    private CharSequence mTextOn;

    public XTextToggle(Context context) {
        this(context, null);
    }

    public XTextToggle(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.XToggleLayout_Fill_TextToggle);
    }

    public XTextToggle(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.XToggleLayout_Fill_TextToggle);
    }

    public XTextToggle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Resources.Theme theme = context.getTheme();
        TypedArray ta = theme.obtainStyledAttributes(attrs, R.styleable.XTextToggle, defStyleAttr, defStyleRes);
        int textSize = ta.getDimensionPixelSize(R.styleable.XTextToggle_android_textSize, 15);
        final XFontScaleHelper xFontScaleHelper = XFontScaleHelper.create(ta, R.styleable.XTextToggle_android_textSize);
        if (xFontScaleHelper != null && this.mXViewDelegate != null) {
            this.mXViewDelegate.setFontScaleChangeCallback(new XViewDelegate.onFontScaleChangeCallback() { // from class: com.xiaopeng.xui.widget.toggle.-$$Lambda$XTextToggle$i_pFBXkvhpm2MTmIiwcFb8dK81E
                @Override // com.xiaopeng.xui.view.XViewDelegate.onFontScaleChangeCallback
                public final void onFontScaleChanged() {
                    XTextToggle.this.lambda$new$0$XTextToggle(xFontScaleHelper);
                }
            });
        }
        ColorStateList colorStateList = ta.getColorStateList(R.styleable.XTextToggle_android_textColor);
        this.mTextColorResId = ta.getResourceId(R.styleable.XTextToggle_android_textColor, 0);
        this.mContentTextView = new CheckedTextView(context);
        this.mContentTextView.setTextColor(colorStateList);
        this.mContentTextView.setTextSize(0, textSize);
        this.mContentTextView.setGravity(16);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-2, -2);
        lp.addRule(13);
        addView(this.mContentTextView, lp);
        Drawable drawableStart = ta.getDrawable(R.styleable.XTextToggle_android_drawableStart);
        int drawablePadding = ta.getDimensionPixelSize(R.styleable.XTextToggle_android_drawablePadding, 0);
        if (drawableStart != null) {
            this.mContentTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, (Drawable) null, (Drawable) null, (Drawable) null);
            this.mContentTextView.setCompoundDrawablePadding(drawablePadding);
        }
        this.mDrawableStartResId = ta.getResourceId(R.styleable.XTextToggle_android_drawableStart, 0);
        this.mTextOn = ta.getText(R.styleable.XTextToggle_android_textOn);
        this.mTextOff = ta.getText(R.styleable.XTextToggle_android_textOff);
        syncTextState();
        ta.recycle();
    }

    public /* synthetic */ void lambda$new$0$XTextToggle(XFontScaleHelper xFontScaleHelper) {
        xFontScaleHelper.refreshTextSize(this.mContentTextView);
    }

    public CharSequence getTextOn() {
        return this.mTextOn;
    }

    public void setTextOn(CharSequence textOn) {
        this.mTextOn = textOn;
    }

    public CharSequence getTextOff() {
        return this.mTextOff;
    }

    public void setTextOff(CharSequence textOff) {
        this.mTextOff = textOff;
    }

    @Override // com.xiaopeng.xui.widget.toggle.XToggleLayout, android.widget.Checkable
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        syncTextState();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.toggle.XToggleLayout
    public void updateThemeResource() {
        if (this.mTextColorResId != 0) {
            this.mContentTextView.setTextColor(getContext().getColorStateList(this.mTextColorResId));
        }
        if (this.mDrawableStartResId != 0) {
            this.mContentTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(getContext().getDrawable(this.mDrawableStartResId), (Drawable) null, (Drawable) null, (Drawable) null);
        }
        super.updateThemeResource();
    }

    private void syncTextState() {
        CharSequence charSequence;
        CharSequence charSequence2;
        boolean checked = isChecked();
        if (checked && (charSequence2 = this.mTextOn) != null) {
            setText(charSequence2);
        } else if (!checked && (charSequence = this.mTextOff) != null) {
            setText(charSequence);
        }
    }

    public void setText(CharSequence text) {
        CheckedTextView checkedTextView = this.mContentTextView;
        if (checkedTextView != null) {
            checkedTextView.setText(text);
        }
    }
}
