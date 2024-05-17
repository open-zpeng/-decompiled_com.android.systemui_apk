package com.xiaopeng.module.aiavatar.graphics.animation;

import android.os.SystemClock;
/* loaded from: classes23.dex */
public class AnimationUtils {
    private static final int SEQUENTIALLY = 1;
    private static final int TOGETHER = 0;

    public static long currentAnimationTimeMillis() {
        return SystemClock.uptimeMillis();
    }
}
