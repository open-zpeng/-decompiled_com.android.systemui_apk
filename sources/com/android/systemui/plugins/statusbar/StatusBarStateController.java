package com.android.systemui.plugins.statusbar;

import com.android.systemui.plugins.annotations.DependsOn;
import com.android.systemui.plugins.annotations.ProvidesInterface;
@ProvidesInterface(version = 1)
@DependsOn(target = StateListener.class)
/* loaded from: classes21.dex */
public interface StatusBarStateController {
    public static final int VERSION = 1;

    void addCallback(StateListener stateListener);

    float getDozeAmount();

    int getState();

    boolean isDozing();

    void removeCallback(StateListener stateListener);

    @ProvidesInterface(version = 1)
    /* loaded from: classes21.dex */
    public interface StateListener {
        public static final int VERSION = 1;

        default void onStatePreChange(int oldState, int newState) {
        }

        default void onStatePostChange() {
        }

        default void onStateChanged(int newState) {
        }

        default void onDozingChanged(boolean isDozing) {
        }

        default void onDozeAmountChanged(float linear, float eased) {
        }

        default void onSystemUiVisibilityChanged(int visibility) {
        }

        default void onPulsingChanged(boolean pulsing) {
        }
    }
}
