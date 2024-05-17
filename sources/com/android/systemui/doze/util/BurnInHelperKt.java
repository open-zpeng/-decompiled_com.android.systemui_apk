package com.android.systemui.doze.util;

import android.util.MathUtils;
import kotlin.Metadata;
/* compiled from: BurnInHelper.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u001a\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\u001a\u0016\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\b\u001a \u0010\t\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u00012\u0006\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u000b\u001a\u00020\u0001H\u0002\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000¨\u0006\f"}, d2 = {"BURN_IN_PREVENTION_PERIOD_X", "", "BURN_IN_PREVENTION_PERIOD_Y", "MILLIS_PER_MINUTES", "getBurnInOffset", "", "amplitude", "xAxis", "", "zigzag", "x", "period", "name"}, k = 2, mv = {1, 1, 13})
/* loaded from: classes21.dex */
public final class BurnInHelperKt {
    private static final float BURN_IN_PREVENTION_PERIOD_X = 83.0f;
    private static final float BURN_IN_PREVENTION_PERIOD_Y = 521.0f;
    private static final float MILLIS_PER_MINUTES = 60000.0f;

    public static final int getBurnInOffset(int amplitude, boolean xAxis) {
        return (int) zigzag(((float) System.currentTimeMillis()) / MILLIS_PER_MINUTES, amplitude, xAxis ? BURN_IN_PREVENTION_PERIOD_X : BURN_IN_PREVENTION_PERIOD_Y);
    }

    private static final float zigzag(float x, float amplitude, float period) {
        float f = 2;
        float xprime = (x % period) / (period / f);
        float interpolationAmount = xprime <= ((float) 1) ? xprime : f - xprime;
        return MathUtils.lerp(0.0f, amplitude, interpolationAmount);
    }
}
