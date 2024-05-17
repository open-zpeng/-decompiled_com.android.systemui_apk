package com.android.systemui.statusbar.policy;
/* loaded from: classes21.dex */
public interface LocationController extends CallbackController<LocationChangeCallback> {
    boolean isLocationActive();

    boolean isLocationEnabled();

    boolean setLocationEnabled(boolean z);

    /* loaded from: classes21.dex */
    public interface LocationChangeCallback {
        default void onLocationActiveChanged(boolean active) {
        }

        default void onLocationSettingsChanged(boolean locationEnabled) {
        }
    }
}
