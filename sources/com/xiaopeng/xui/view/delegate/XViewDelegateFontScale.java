package com.xiaopeng.xui.view.delegate;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.xiaopeng.xpui.R;
/* loaded from: classes25.dex */
public class XViewDelegateFontScale extends XViewDelegatePart {
    private float mComplexToFloat;
    private float mFontScale;
    private TextView mTextView;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static XViewDelegateFontScale create(@NonNull TextView view, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs == null) {
            return null;
        }
        Resources.Theme theme = view.getContext().getTheme();
        TypedArray a = view.getContext().obtainStyledAttributes(attrs, R.styleable.XFontSize, defStyleAttr, defStyleRes);
        boolean isDynamicFontSizeChangeEnable = a.getBoolean(R.styleable.XFontSize_x_dynamic_font_scale_change_enable, true);
        a.recycle();
        if (!isDynamicFontSizeChangeEnable) {
            return null;
        }
        boolean isTextSize = false;
        int textSizeUnit = 0;
        float complexToFloat = 0.0f;
        TypedArray a2 = theme.obtainStyledAttributes(attrs, new int[]{16842901}, defStyleAttr, defStyleRes);
        if (a2.hasValue(0)) {
            TypedValue value = new TypedValue();
            a2.getValue(0, value);
            textSizeUnit = value.getComplexUnit();
            complexToFloat = TypedValue.complexToFloat(value.data);
            isTextSize = true;
        }
        a2.recycle();
        if (!isTextSize) {
            TypedArray a3 = theme.obtainStyledAttributes(attrs, new int[]{16842804}, defStyleAttr, defStyleRes);
            int ap = a3.getResourceId(0, -1);
            a3.recycle();
            TypedArray appearance = ap != -1 ? theme.obtainStyledAttributes(ap, new int[]{16842901}) : null;
            if (appearance != null) {
                if (appearance.hasValue(0)) {
                    TypedValue value2 = new TypedValue();
                    appearance.getValue(0, value2);
                    textSizeUnit = value2.getComplexUnit();
                    complexToFloat = TypedValue.complexToFloat(value2.data);
                }
                appearance.recycle();
            }
        }
        if (textSizeUnit != 2) {
            return null;
        }
        return new XViewDelegateFontScale(view, complexToFloat);
    }

    private XViewDelegateFontScale(TextView textView, float complexToFloat) {
        this.mTextView = textView;
        this.mComplexToFloat = complexToFloat;
        this.mFontScale = textView.getContext().getResources().getConfiguration().fontScale;
    }

    @Override // com.xiaopeng.xui.view.delegate.XViewDelegatePart
    public void onConfigurationChanged(Configuration config) {
        checkFont(config, "onConfigurationChanged");
    }

    private void checkFont(Configuration config, String from) {
        if (this.mFontScale != config.fontScale) {
            this.mFontScale = config.fontScale;
            this.mTextView.setTextSize(this.mComplexToFloat);
        }
    }

    @Override // com.xiaopeng.xui.view.delegate.XViewDelegatePart
    public void onAttachedToWindow() {
        checkFont(this.mTextView.getResources().getConfiguration(), "onAttachedToWindow");
    }

    @Override // com.xiaopeng.xui.view.delegate.XViewDelegatePart
    public void onDetachedFromWindow() {
    }
}
