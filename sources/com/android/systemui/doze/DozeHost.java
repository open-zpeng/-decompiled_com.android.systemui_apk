package com.android.systemui.doze;
/* loaded from: classes21.dex */
public interface DozeHost {

    /* loaded from: classes21.dex */
    public interface PulseCallback {
        void onPulseFinished();

        void onPulseStarted();
    }

    void addCallback(Callback callback);

    void dozeTimeTick();

    void extendPulse(int i);

    boolean isBlockingDoze();

    boolean isPowerSaveActive();

    boolean isProvisioned();

    boolean isPulsingBlocked();

    void onIgnoreTouchWhilePulsing(boolean z);

    void onSlpiTap(float f, float f2);

    void pulseWhileDozing(PulseCallback pulseCallback, int i);

    void removeCallback(Callback callback);

    void setAnimateScreenOff(boolean z);

    void setAnimateWakeup(boolean z);

    void setDozeScreenBrightness(int i);

    void startDozing();

    void stopDozing();

    void stopPulsing();

    default void setAodDimmingScrim(float scrimOpacity) {
    }

    default void prepareForGentleWakeUp() {
    }

    /* loaded from: classes21.dex */
    public interface Callback {
        default void onNotificationAlerted(Runnable onPulseSuppressedListener) {
        }

        default void onPowerSaveChanged(boolean active) {
        }
    }
}
