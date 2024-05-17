package com.android.systemui.statusbar.phone;

import android.content.res.Resources;
import android.util.MathUtils;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.doze.util.BurnInHelperKt;
import com.android.systemui.statusbar.notification.NotificationUtils;
/* loaded from: classes21.dex */
public class KeyguardClockPositionAlgorithm {
    private static float CLOCK_HEIGHT_WEIGHT = 0.7f;
    private int mBurnInPreventionOffsetX;
    private int mBurnInPreventionOffsetY;
    private boolean mBypassEnabled;
    private int mClockNotificationsMargin;
    private int mClockPreferredY;
    private int mContainerTopPadding;
    private float mDarkAmount;
    private float mEmptyDragAmount;
    private boolean mHasCustomClock;
    private boolean mHasVisibleNotifs;
    private int mHeight;
    private int mKeyguardStatusHeight;
    private int mMaxShadeBottom;
    private int mMinTopMargin;
    private int mNotificationStackHeight;
    private float mPanelExpansion;
    private int mUnlockedStackScrollerPadding;

    /* loaded from: classes21.dex */
    public static class Result {
        public float clockAlpha;
        public int clockX;
        public int clockY;
        public int stackScrollerPadding;
        public int stackScrollerPaddingExpanded;
    }

    public void loadDimens(Resources res) {
        this.mClockNotificationsMargin = res.getDimensionPixelSize(R.dimen.keyguard_clock_notifications_margin);
        this.mContainerTopPadding = Math.max(res.getDimensionPixelSize(R.dimen.keyguard_clock_top_margin), res.getDimensionPixelSize(R.dimen.keyguard_lock_height) + res.getDimensionPixelSize(R.dimen.keyguard_lock_padding) + res.getDimensionPixelSize(R.dimen.keyguard_clock_lock_margin));
        this.mBurnInPreventionOffsetX = res.getDimensionPixelSize(R.dimen.burn_in_prevention_offset_x);
        this.mBurnInPreventionOffsetY = res.getDimensionPixelSize(R.dimen.burn_in_prevention_offset_y);
    }

    public void setup(int minTopMargin, int maxShadeBottom, int notificationStackHeight, float panelExpansion, int parentHeight, int keyguardStatusHeight, int clockPreferredY, boolean hasCustomClock, boolean hasVisibleNotifs, float dark, float emptyDragAmount, boolean bypassEnabled, int unlockedStackScrollerPadding) {
        this.mMinTopMargin = this.mContainerTopPadding + minTopMargin;
        this.mMaxShadeBottom = maxShadeBottom;
        this.mNotificationStackHeight = notificationStackHeight;
        this.mPanelExpansion = panelExpansion;
        this.mHeight = parentHeight;
        this.mKeyguardStatusHeight = keyguardStatusHeight;
        this.mClockPreferredY = clockPreferredY;
        this.mHasCustomClock = hasCustomClock;
        this.mHasVisibleNotifs = hasVisibleNotifs;
        this.mDarkAmount = dark;
        this.mEmptyDragAmount = emptyDragAmount;
        this.mBypassEnabled = bypassEnabled;
        this.mUnlockedStackScrollerPadding = unlockedStackScrollerPadding;
    }

    public void run(Result result) {
        int y = getClockY(this.mPanelExpansion);
        result.clockY = y;
        result.clockAlpha = getClockAlpha(y);
        result.stackScrollerPadding = this.mBypassEnabled ? this.mUnlockedStackScrollerPadding : this.mKeyguardStatusHeight + y;
        result.stackScrollerPaddingExpanded = this.mBypassEnabled ? this.mUnlockedStackScrollerPadding : getClockY(1.0f) + this.mKeyguardStatusHeight;
        result.clockX = (int) NotificationUtils.interpolate(0.0f, burnInPreventionOffsetX(), this.mDarkAmount);
    }

    public float getMinStackScrollerPadding() {
        return this.mBypassEnabled ? this.mUnlockedStackScrollerPadding : this.mMinTopMargin + this.mKeyguardStatusHeight + this.mClockNotificationsMargin;
    }

    private int getMaxClockY() {
        return ((this.mHeight / 2) - this.mKeyguardStatusHeight) - this.mClockNotificationsMargin;
    }

    private int getPreferredClockY() {
        return this.mClockPreferredY;
    }

    private int getExpandedPreferredClockY() {
        return (!this.mHasCustomClock || (this.mHasVisibleNotifs && !this.mBypassEnabled)) ? getExpandedClockPosition() : getPreferredClockY();
    }

    public int getExpandedClockPosition() {
        int i = this.mMaxShadeBottom;
        int i2 = this.mMinTopMargin;
        int availableHeight = i - i2;
        int containerCenter = (availableHeight / 2) + i2;
        float y = ((containerCenter - (this.mKeyguardStatusHeight * CLOCK_HEIGHT_WEIGHT)) - this.mClockNotificationsMargin) - (this.mNotificationStackHeight / 2);
        if (y < i2) {
            y = i2;
        }
        float maxClockY = getMaxClockY();
        if (y > maxClockY) {
            y = maxClockY;
        }
        return (int) y;
    }

    private int getClockY(float panelExpansion) {
        float clockYDark = (this.mHasCustomClock ? getPreferredClockY() : getMaxClockY()) + burnInPreventionOffsetY();
        float clockYDark2 = MathUtils.max(0.0f, clockYDark);
        float clockYRegular = getExpandedPreferredClockY();
        float clockYBouncer = -this.mKeyguardStatusHeight;
        float shadeExpansion = Interpolators.FAST_OUT_LINEAR_IN.getInterpolation(panelExpansion);
        float clockY = MathUtils.lerp(clockYBouncer, clockYRegular, shadeExpansion);
        float clockYDark3 = MathUtils.lerp(clockYBouncer, clockYDark2, shadeExpansion);
        float darkAmount = (!this.mBypassEnabled || this.mHasCustomClock) ? this.mDarkAmount : 1.0f;
        return (int) (MathUtils.lerp(clockY, clockYDark3, darkAmount) + this.mEmptyDragAmount);
    }

    private float getClockAlpha(int y) {
        float alphaKeyguard = Math.max(0.0f, y / Math.max(1.0f, getClockY(1.0f)));
        return MathUtils.lerp(Interpolators.ACCELERATE.getInterpolation(alphaKeyguard), 1.0f, this.mDarkAmount);
    }

    private float burnInPreventionOffsetY() {
        return BurnInHelperKt.getBurnInOffset(this.mBurnInPreventionOffsetY * 2, false) - this.mBurnInPreventionOffsetY;
    }

    private float burnInPreventionOffsetX() {
        return BurnInHelperKt.getBurnInOffset(this.mBurnInPreventionOffsetX * 2, true) - this.mBurnInPreventionOffsetX;
    }
}
