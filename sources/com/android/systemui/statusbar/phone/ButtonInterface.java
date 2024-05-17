package com.android.systemui.statusbar.phone;

import android.graphics.drawable.Drawable;
/* loaded from: classes21.dex */
public interface ButtonInterface {
    void abortCurrentGesture();

    void setDarkIntensity(float f);

    void setDelayTouchFeedback(boolean z);

    void setImageDrawable(Drawable drawable);

    void setVertical(boolean z);
}
