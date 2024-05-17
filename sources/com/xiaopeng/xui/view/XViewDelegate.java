package com.xiaopeng.xui.view;

import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;
import com.xiaopeng.libtheme.ThemeViewModel;
/* loaded from: classes25.dex */
public abstract class XViewDelegate {

    /* loaded from: classes25.dex */
    public interface onFontScaleChangeCallback {
        void onFontScaleChanged();
    }

    public abstract ThemeViewModel getThemeViewModel();

    public abstract void onAttachedToWindow();

    public abstract void onConfigurationChanged(Configuration configuration);

    public abstract void onDetachedFromWindow();

    public abstract void onWindowFocusChanged(boolean z);

    public abstract void setFontScaleChangeCallback(onFontScaleChangeCallback onfontscalechangecallback);

    public static XViewDelegate create(View view, AttributeSet attrs) {
        return create(view, attrs, 0, 0);
    }

    public static XViewDelegate create(View view, AttributeSet attrs, int defStyleAttr) {
        return create(view, attrs, defStyleAttr, 0);
    }

    public static XViewDelegate create(View view, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        return new XViewDelegateImpl(view, attrs, defStyleAttr, defStyleRes, null);
    }

    public static XViewDelegate create(View view, AttributeSet attrs, int defStyleAttr, int defStyleRes, Object extras) {
        return new XViewDelegateImpl(view, attrs, defStyleAttr, defStyleRes, extras);
    }
}
