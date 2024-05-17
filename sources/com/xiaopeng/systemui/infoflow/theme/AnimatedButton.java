package com.xiaopeng.systemui.infoflow.theme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import com.xiaopeng.libtheme.ThemeViewModel;
@SuppressLint({"AppCompatCustomView"})
/* loaded from: classes24.dex */
public class AnimatedButton extends Button {
    private ThemeViewModel mThemeViewModel;

    public AnimatedButton(Context context) {
        this(context, null);
    }

    public AnimatedButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimatedButton(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AnimatedButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mThemeViewModel = ThemeViewModel.create(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onVisibilityChanged(View changedView, int vis) {
        super.onVisibilityChanged(changedView, vis);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mThemeViewModel.onConfigurationChanged(this, newConfig);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mThemeViewModel.onAttachedToWindow(this);
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mThemeViewModel.onDetachedFromWindow(this);
    }

    @Override // android.view.View
    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
        this.mThemeViewModel.setBackgroundResource(resid);
    }
}
