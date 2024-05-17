package com.xiaopeng.systemui.ui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.xiaopeng.libtheme.ThemeViewModel;
/* loaded from: classes24.dex */
public class AlphaOptimizedFrameLayout extends FrameLayout {
    private ThemeViewModel mThemeViewModel;

    public AlphaOptimizedFrameLayout(Context context) {
        this(context, null);
    }

    public AlphaOptimizedFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlphaOptimizedFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AlphaOptimizedFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mThemeViewModel = ThemeViewModel.create(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mThemeViewModel.onConfigurationChanged(this, newConfig);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mThemeViewModel.onAttachedToWindow(this);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mThemeViewModel.onDetachedFromWindow(this);
    }

    @Override // android.view.View
    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
        this.mThemeViewModel.setBackgroundResource(resid);
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }
}
