package com.xiaopeng.systemui.infoflow.effect.p;

import android.graphics.Canvas;
/* loaded from: classes24.dex */
public interface Part {
    public static final int STYLE_HAPPY = 2;
    public static final int STYLE_LAUGHING = 1;
    public static final int STYLE_LOVE = 4;
    public static final int STYLE_NORMAL = 0;
    public static final int STYLE_SHOCK = 3;

    void draw(Canvas canvas);

    void setAlpha(int i);

    void setStyle(int i);
}
