package com.android.systemui.statusbar.policy;
/* loaded from: classes21.dex */
public interface DeviceProvisionedController extends CallbackController<DeviceProvisionedListener> {
    int getCurrentUser();

    boolean isDeviceProvisioned();

    boolean isUserSetup(int i);

    default boolean isCurrentUserSetup() {
        return isUserSetup(getCurrentUser());
    }

    /* loaded from: classes21.dex */
    public interface DeviceProvisionedListener {
        default void onDeviceProvisionedChanged() {
        }

        default void onUserSwitched() {
            onUserSetupChanged();
        }

        default void onUserSetupChanged() {
        }
    }
}
