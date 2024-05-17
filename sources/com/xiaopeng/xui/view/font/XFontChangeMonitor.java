package com.xiaopeng.xui.view.font;

import android.content.Context;
import android.content.res.Configuration;
import com.xiaopeng.xui.view.XViewDelegate;
/* loaded from: classes25.dex */
public class XFontChangeMonitor {
    private Context mContext;
    private float mFontScale;
    private XViewDelegate.onFontScaleChangeCallback mOnFontChangeCallback;

    public XFontChangeMonitor(Context context) {
        this.mContext = context;
        this.mFontScale = context.getResources().getConfiguration().fontScale;
    }

    public void setOnFontScaleChangeCallback(XViewDelegate.onFontScaleChangeCallback onFontChangeCallback) {
        this.mOnFontChangeCallback = onFontChangeCallback;
    }

    public void onConfigurationChanged(Configuration config) {
        checkFont(config);
    }

    private void checkFont(Configuration config) {
        if (this.mFontScale != config.fontScale) {
            this.mFontScale = config.fontScale;
            XViewDelegate.onFontScaleChangeCallback onfontscalechangecallback = this.mOnFontChangeCallback;
            if (onfontscalechangecallback != null) {
                onfontscalechangecallback.onFontScaleChanged();
            }
        }
    }

    public void onAttachedToWindow() {
        checkFont(this.mContext.getResources().getConfiguration());
    }
}
