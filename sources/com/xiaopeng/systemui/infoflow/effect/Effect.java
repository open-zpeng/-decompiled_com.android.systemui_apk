package com.xiaopeng.systemui.infoflow.effect;

import android.graphics.Canvas;
/* loaded from: classes24.dex */
public interface Effect {
    boolean isEnable();

    void onPause();

    void onResume();

    void performDraw(Canvas canvas);

    void setAlpha(int i);

    void setEnable(boolean z);

    void update(float f, boolean z);
}
