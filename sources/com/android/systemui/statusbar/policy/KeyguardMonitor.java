package com.android.systemui.statusbar.policy;
/* loaded from: classes21.dex */
public interface KeyguardMonitor extends CallbackController<Callback> {
    long calculateGoingToFullShadeDelay();

    long getKeyguardFadingAwayDelay();

    long getKeyguardFadingAwayDuration();

    boolean isKeyguardFadingAway();

    boolean isKeyguardGoingAway();

    boolean isLaunchTransitionFadingAway();

    boolean isOccluded();

    boolean isSecure();

    boolean isShowing();

    default long getShortenedFadingAwayDuration() {
        if (isBypassFadingAnimation()) {
            return getKeyguardFadingAwayDuration();
        }
        return getKeyguardFadingAwayDuration() / 2;
    }

    default boolean isDeviceInteractive() {
        return false;
    }

    default void setLaunchTransitionFadingAway(boolean b) {
    }

    default void notifyKeyguardGoingAway(boolean b) {
    }

    default boolean isBypassFadingAnimation() {
        return false;
    }

    default void notifyKeyguardFadingAway(long delay, long fadeoutDuration, boolean isBypassFading) {
    }

    default void notifyKeyguardDoneFading() {
    }

    default void notifyKeyguardState(boolean showing, boolean methodSecure, boolean occluded) {
    }

    /* loaded from: classes21.dex */
    public interface Callback {
        default void onKeyguardShowingChanged() {
        }

        default void onKeyguardFadingAwayChanged() {
        }
    }
}
