package com.xiaopeng.systemui.infoflow.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.systemui.infoflow.theme.AnimatedImageView;
import com.xiaopeng.systemui.infoflow.util.Logger;
@SuppressLint({"AppCompatCustomView"})
/* loaded from: classes24.dex */
public class MusicDayBgImageView extends AnimatedImageView {
    private static final String TAG = "MusicDayBgImageView";

    public MusicDayBgImageView(Context context) {
        this(context, null);
    }

    public MusicDayBgImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicDayBgImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MusicDayBgImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.theme.AnimatedImageView, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        boolean isThemeChanged = ThemeManager.isThemeChanged(newConfig);
        if (isThemeChanged) {
            boolean isNight = ThemeManager.isNightMode(getContext());
            Logger.d(TAG, "onConfigurationChanged isNight--" + isNight);
            setVisibility(isNight ? 8 : 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.theme.AnimatedImageView, android.widget.ImageView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        boolean isNight = ThemeManager.isNightMode(getContext());
        Logger.d(TAG, "onAttachedToWindow isNight--" + isNight);
        setVisibility(isNight ? 8 : 0);
    }
}
