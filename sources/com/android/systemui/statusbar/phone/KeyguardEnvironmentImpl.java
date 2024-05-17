package com.android.systemui.statusbar.phone;

import android.service.notification.StatusBarNotification;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.notification.collection.NotificationData;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class KeyguardEnvironmentImpl implements NotificationData.KeyguardEnvironment {
    private static final String TAG = "KeyguardEnvironmentImpl";
    private final NotificationLockscreenUserManager mLockscreenUserManager = (NotificationLockscreenUserManager) Dependency.get(NotificationLockscreenUserManager.class);
    private final DeviceProvisionedController mDeviceProvisionedController = (DeviceProvisionedController) Dependency.get(DeviceProvisionedController.class);

    @Override // com.android.systemui.statusbar.notification.collection.NotificationData.KeyguardEnvironment
    public boolean isDeviceProvisioned() {
        return this.mDeviceProvisionedController.isDeviceProvisioned();
    }

    @Override // com.android.systemui.statusbar.notification.collection.NotificationData.KeyguardEnvironment
    public boolean isNotificationForCurrentProfiles(StatusBarNotification n) {
        int notificationUserId = n.getUserId();
        return this.mLockscreenUserManager.isCurrentProfile(notificationUserId);
    }
}
