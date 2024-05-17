package com.android.keyguard.clock;

import android.content.res.Resources;
import android.util.MathUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.R;
/* loaded from: classes19.dex */
class SmallClockPosition {
    private final int mBurnInOffsetY;
    private float mDarkAmount;
    private final int mKeyguardLockHeight;
    private final int mKeyguardLockPadding;
    private final int mStatusBarHeight;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SmallClockPosition(Resources res) {
        this(res.getDimensionPixelSize(R.dimen.status_bar_height), res.getDimensionPixelSize(R.dimen.keyguard_lock_padding), res.getDimensionPixelSize(R.dimen.keyguard_lock_height), res.getDimensionPixelSize(R.dimen.burn_in_prevention_offset_y));
    }

    @VisibleForTesting
    SmallClockPosition(int statusBarHeight, int lockPadding, int lockHeight, int burnInY) {
        this.mStatusBarHeight = statusBarHeight;
        this.mKeyguardLockPadding = lockPadding;
        this.mKeyguardLockHeight = lockHeight;
        this.mBurnInOffsetY = burnInY;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDarkAmount(float darkAmount) {
        this.mDarkAmount = darkAmount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getPreferredY() {
        int i = this.mStatusBarHeight;
        int i2 = this.mKeyguardLockHeight;
        int i3 = this.mKeyguardLockPadding;
        int aodY = i + i2 + (i3 * 2) + this.mBurnInOffsetY;
        int lockY = i + i2 + (i3 * 2);
        return (int) MathUtils.lerp(lockY, aodY, this.mDarkAmount);
    }
}
