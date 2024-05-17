package com.android.systemui.statusbar.notification;

import android.app.AppGlobals;
import android.content.pm.IPackageManager;
import android.os.RemoteException;
import android.service.notification.StatusBarNotification;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dependency;
import com.android.systemui.ForegroundServiceController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.notification.collection.NotificationData;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.statusbar.phone.StatusBar;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class NotificationFilter {
    private NotificationData.KeyguardEnvironment mEnvironment;
    private ForegroundServiceController mFsc;
    private final NotificationGroupManager mGroupManager = (NotificationGroupManager) Dependency.get(NotificationGroupManager.class);
    private ShadeController mShadeController;
    private NotificationLockscreenUserManager mUserManager;

    private NotificationData.KeyguardEnvironment getEnvironment() {
        if (this.mEnvironment == null) {
            this.mEnvironment = (NotificationData.KeyguardEnvironment) Dependency.get(NotificationData.KeyguardEnvironment.class);
        }
        return this.mEnvironment;
    }

    private ShadeController getShadeController() {
        if (this.mShadeController == null) {
            this.mShadeController = (ShadeController) Dependency.get(ShadeController.class);
        }
        return this.mShadeController;
    }

    private ForegroundServiceController getFsc() {
        if (this.mFsc == null) {
            this.mFsc = (ForegroundServiceController) Dependency.get(ForegroundServiceController.class);
        }
        return this.mFsc;
    }

    private NotificationLockscreenUserManager getUserManager() {
        if (this.mUserManager == null) {
            this.mUserManager = (NotificationLockscreenUserManager) Dependency.get(NotificationLockscreenUserManager.class);
        }
        return this.mUserManager;
    }

    public boolean shouldFilterOut(NotificationEntry entry) {
        String[] apps;
        StatusBarNotification sbn = entry.notification;
        if ((getEnvironment().isDeviceProvisioned() || showNotificationEvenIfUnprovisioned(sbn)) && getEnvironment().isNotificationForCurrentProfiles(sbn)) {
            if (getUserManager().isLockscreenPublicMode(sbn.getUserId()) && (sbn.getNotification().visibility == -1 || getUserManager().shouldHideNotifications(sbn.getUserId()) || getUserManager().shouldHideNotifications(sbn.getKey()))) {
                return true;
            }
            if (getShadeController().isDozing() && entry.shouldSuppressAmbient()) {
                return true;
            }
            if ((getShadeController().isDozing() || !entry.shouldSuppressNotificationList()) && !entry.suspended) {
                if (StatusBar.ENABLE_CHILD_NOTIFICATIONS || !this.mGroupManager.isChildInGroupWithSummary(sbn)) {
                    if (!getFsc().isDisclosureNotification(sbn) || getFsc().isDisclosureNeededForUser(sbn.getUserId())) {
                        return getFsc().isSystemAlertNotification(sbn) && (apps = sbn.getNotification().extras.getStringArray("android.foregroundApps")) != null && apps.length >= 1 && !getFsc().isSystemAlertWarningNeeded(sbn.getUserId(), apps[0]);
                    }
                    return true;
                }
                return true;
            }
            return true;
        }
        return true;
    }

    private static boolean showNotificationEvenIfUnprovisioned(StatusBarNotification sbn) {
        return showNotificationEvenIfUnprovisioned(AppGlobals.getPackageManager(), sbn);
    }

    @VisibleForTesting
    static boolean showNotificationEvenIfUnprovisioned(IPackageManager packageManager, StatusBarNotification sbn) {
        return checkUidPermission(packageManager, "android.permission.NOTIFICATION_DURING_SETUP", sbn.getUid()) == 0 && sbn.getNotification().extras.getBoolean("android.allowDuringSetup");
    }

    private static int checkUidPermission(IPackageManager packageManager, String permission, int uid) {
        try {
            return packageManager.checkUidPermission(permission, uid);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
