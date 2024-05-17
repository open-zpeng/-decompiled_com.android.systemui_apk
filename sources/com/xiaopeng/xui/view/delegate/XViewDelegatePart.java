package com.xiaopeng.xui.view.delegate;

import android.content.res.Configuration;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.annotation.NonNull;
/* loaded from: classes25.dex */
public abstract class XViewDelegatePart {
    public abstract void onAttachedToWindow();

    public abstract void onConfigurationChanged(Configuration configuration);

    public abstract void onDetachedFromWindow();

    public static XViewDelegatePart createFont(@NonNull TextView view, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        return XViewDelegateFontScale.create(view, attrs, defStyleAttr, defStyleRes);
    }
}
