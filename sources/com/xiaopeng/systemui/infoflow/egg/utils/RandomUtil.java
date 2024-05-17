package com.xiaopeng.systemui.infoflow.egg.utils;

import java.util.Random;
/* loaded from: classes24.dex */
public class RandomUtil {
    private static Random sRandom = new Random();

    public static int nextInt(int n) {
        if (n <= 1) {
            return 0;
        }
        return sRandom.nextInt(n);
    }
}
