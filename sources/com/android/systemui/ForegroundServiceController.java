package com.android.systemui;

import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import android.util.ArraySet;
import android.util.SparseArray;
import androidx.mediarouter.media.SystemMediaRouteProvider;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class ForegroundServiceController {
    private final SparseArray<ForegroundServicesUserState> mUserServices = new SparseArray<>();
    private final Object mMutex = new Object();

    public boolean isDisclosureNeededForUser(int userId) {
        synchronized (this.mMutex) {
            ForegroundServicesUserState services = this.mUserServices.get(userId);
            if (services == null) {
                return false;
            }
            return services.isDisclosureNeeded();
        }
    }

    public boolean isSystemAlertWarningNeeded(int userId, String pkg) {
        synchronized (this.mMutex) {
            ForegroundServicesUserState services = this.mUserServices.get(userId);
            if (services == null) {
                return false;
            }
            return services.getStandardLayoutKey(pkg) == null;
        }
    }

    public String getStandardLayoutKey(int userId, String pkg) {
        synchronized (this.mMutex) {
            ForegroundServicesUserState services = this.mUserServices.get(userId);
            if (services == null) {
                return null;
            }
            return services.getStandardLayoutKey(pkg);
        }
    }

    public ArraySet<Integer> getAppOps(int userId, String pkg) {
        synchronized (this.mMutex) {
            ForegroundServicesUserState services = this.mUserServices.get(userId);
            if (services == null) {
                return null;
            }
            return services.getFeatures(pkg);
        }
    }

    public void onAppOpChanged(int code, int uid, String packageName, boolean active) {
        int userId = UserHandle.getUserId(uid);
        synchronized (this.mMutex) {
            ForegroundServicesUserState userServices = this.mUserServices.get(userId);
            if (userServices == null) {
                userServices = new ForegroundServicesUserState();
                this.mUserServices.put(userId, userServices);
            }
            if (active) {
                userServices.addOp(packageName, code);
            } else {
                userServices.removeOp(packageName, code);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean updateUserState(int userId, UserStateUpdateCallback updateCallback, boolean createIfNotFound) {
        synchronized (this.mMutex) {
            ForegroundServicesUserState userState = this.mUserServices.get(userId);
            if (userState == null) {
                if (createIfNotFound) {
                    userState = new ForegroundServicesUserState();
                    this.mUserServices.put(userId, userState);
                } else {
                    return false;
                }
            }
            return updateCallback.updateUserState(userState);
        }
    }

    public boolean isDisclosureNotification(StatusBarNotification sbn) {
        return sbn.getId() == 40 && sbn.getTag() == null && sbn.getPackageName().equals(SystemMediaRouteProvider.PACKAGE_NAME);
    }

    public boolean isSystemAlertNotification(StatusBarNotification sbn) {
        return sbn.getPackageName().equals(SystemMediaRouteProvider.PACKAGE_NAME) && sbn.getTag() != null && sbn.getTag().contains("AlertWindowNotification");
    }

    /* loaded from: classes21.dex */
    interface UserStateUpdateCallback {
        boolean updateUserState(ForegroundServicesUserState foregroundServicesUserState);

        default void userStateNotFound(int userId) {
        }
    }
}
