package com.xiaopeng.xui.view;

import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.view.XViewDelegate;
import com.xiaopeng.xui.view.delegate.XViewDelegatePart;
import com.xiaopeng.xui.view.font.XFontChangeMonitor;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes25.dex */
class XViewDelegateImpl extends XViewDelegate {
    private ThemeViewModel mThemeViewModel;
    private View mView;
    private XFontChangeMonitor mXFontChangeMonitor;
    private ArrayList<XViewDelegatePart> mXViewDelegateParts = new ArrayList<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    public XViewDelegateImpl(@NonNull View view, AttributeSet attrs, int defStyleAttr, int defStyleRes, Object extras) {
        XViewDelegatePart font;
        this.mView = view;
        if (Xui.isFontScaleDynamicChangeEnable() && (view instanceof TextView) && (font = XViewDelegatePart.createFont((TextView) view, attrs, defStyleAttr, defStyleRes)) != null) {
            this.mXViewDelegateParts.add(font);
        }
        if (!view.isInEditMode()) {
            this.mThemeViewModel = ThemeViewModel.create(view.getContext(), attrs, defStyleAttr, defStyleRes, extras);
        }
    }

    @Override // com.xiaopeng.xui.view.XViewDelegate
    public void onConfigurationChanged(Configuration config) {
        Iterator<XViewDelegatePart> it = this.mXViewDelegateParts.iterator();
        while (it.hasNext()) {
            XViewDelegatePart delegate = it.next();
            delegate.onConfigurationChanged(config);
        }
        ThemeViewModel themeViewModel = this.mThemeViewModel;
        if (themeViewModel != null) {
            themeViewModel.onConfigurationChanged(this.mView, config);
        }
        XFontChangeMonitor xFontChangeMonitor = this.mXFontChangeMonitor;
        if (xFontChangeMonitor != null) {
            xFontChangeMonitor.onConfigurationChanged(config);
        }
    }

    @Override // com.xiaopeng.xui.view.XViewDelegate
    public void onAttachedToWindow() {
        Iterator<XViewDelegatePart> it = this.mXViewDelegateParts.iterator();
        while (it.hasNext()) {
            XViewDelegatePart delegate = it.next();
            delegate.onAttachedToWindow();
        }
        ThemeViewModel themeViewModel = this.mThemeViewModel;
        if (themeViewModel != null) {
            themeViewModel.onAttachedToWindow(this.mView);
        }
        XFontChangeMonitor xFontChangeMonitor = this.mXFontChangeMonitor;
        if (xFontChangeMonitor != null) {
            xFontChangeMonitor.onAttachedToWindow();
        }
    }

    @Override // com.xiaopeng.xui.view.XViewDelegate
    public void onDetachedFromWindow() {
        Iterator<XViewDelegatePart> it = this.mXViewDelegateParts.iterator();
        while (it.hasNext()) {
            XViewDelegatePart delegate = it.next();
            delegate.onDetachedFromWindow();
        }
        ThemeViewModel themeViewModel = this.mThemeViewModel;
        if (themeViewModel != null) {
            themeViewModel.onDetachedFromWindow(this.mView);
        }
    }

    @Override // com.xiaopeng.xui.view.XViewDelegate
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        ThemeViewModel themeViewModel = this.mThemeViewModel;
        if (themeViewModel != null) {
            themeViewModel.onWindowFocusChanged(this.mView, hasWindowFocus);
        }
    }

    @Override // com.xiaopeng.xui.view.XViewDelegate
    public void setFontScaleChangeCallback(XViewDelegate.onFontScaleChangeCallback callback) {
        if (callback != null && this.mXFontChangeMonitor == null) {
            this.mXFontChangeMonitor = new XFontChangeMonitor(this.mView.getContext());
        }
        this.mXFontChangeMonitor.setOnFontScaleChangeCallback(callback);
    }

    @Override // com.xiaopeng.xui.view.XViewDelegate
    public ThemeViewModel getThemeViewModel() {
        return this.mThemeViewModel;
    }
}
