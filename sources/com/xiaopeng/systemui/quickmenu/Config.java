package com.xiaopeng.systemui.quickmenu;

import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
/* loaded from: classes24.dex */
public class Config {
    public static final int BRIGHTNESS_TO_MAX_PROGRESS = 100;
    public static final int BRIGHTNESS_TO_MAX_VALUE = getBrightnessMax();
    public static final int BRIGHTNESS_TO_MIN_PROGRESS = 1;
    public static final int BRIGHTNESS_TO_MIN_VALUE = 1;
    public static final int MEDIA_STREAM = 3;

    public static int getBrightnessMax() {
        return ContextUtils.getContext().getResources().getInteger(17694887);
    }
}
