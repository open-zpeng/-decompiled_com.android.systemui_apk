package com.xiaopeng.systemui.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.widget.TextView;
import com.xiaopeng.libtheme.ThemeViewModel;
@SuppressLint({"AppCompatCustomView"})
/* loaded from: classes24.dex */
public class AnimatedTextView extends TextView {
    private static final String TAG = "AnimatedTextView";
    private float fontScale;
    private ThemeViewModel mThemeViewModel;

    public AnimatedTextView(Context context) {
        this(context, null);
    }

    public AnimatedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimatedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AnimatedTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.fontScale = context.getResources().getConfiguration().fontScale;
        this.mThemeViewModel = ThemeViewModel.create(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateTextScaleX(newConfig);
        this.mThemeViewModel.onConfigurationChanged(this, newConfig);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mThemeViewModel.onAttachedToWindow(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mThemeViewModel.onDetachedFromWindow(this);
    }

    @Override // android.view.View
    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
        this.mThemeViewModel.setBackgroundResource(resid);
    }

    @Override // android.widget.TextView
    public void setTextColor(int color) {
        super.setTextColor(color);
    }

    private void updateTextScaleX(Configuration newConfig) {
        if (newConfig != null && this.fontScale != newConfig.fontScale) {
            float f = newConfig.fontScale;
            this.fontScale = f;
            setTextScaleX(f);
        }
    }
}
