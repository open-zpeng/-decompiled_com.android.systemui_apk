package com.xiaopeng.systemui.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import com.xiaopeng.libtheme.ThemeViewModel;
@SuppressLint({"AppCompatCustomView"})
/* loaded from: classes24.dex */
public class AnimatedImageView extends ImageView {
    private static final String TAG = "AnimatedImageView";
    private ThemeViewModel mThemeViewModel;
    private int mUiMode;

    public AnimatedImageView(Context context) {
        this(context, null);
    }

    public AnimatedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimatedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AnimatedImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mUiMode = 0;
        this.mThemeViewModel = ThemeViewModel.create(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override // android.view.View
    protected void onVisibilityChanged(View changedView, int vis) {
        super.onVisibilityChanged(changedView, vis);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.mUiMode != newConfig.uiMode) {
            this.mUiMode = newConfig.uiMode;
            this.mThemeViewModel.onConfigurationChanged(this, newConfig);
        }
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mThemeViewModel.onAttachedToWindow(this);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mThemeViewModel.onDetachedFromWindow(this);
    }

    @Override // android.view.View
    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
        this.mThemeViewModel.setBackgroundResource(resid);
    }

    @Override // android.widget.ImageView
    public void setImageResource(int resid) {
        super.setImageResource(resid);
        this.mThemeViewModel.setImageResource(resid);
    }

    @Override // android.widget.ImageView
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
    }
}
