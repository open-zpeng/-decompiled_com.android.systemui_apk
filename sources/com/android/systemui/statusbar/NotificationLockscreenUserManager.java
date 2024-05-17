package com.android.systemui.statusbar;

import android.content.pm.UserInfo;
import android.util.SparseArray;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
/* loaded from: classes21.dex */
public interface NotificationLockscreenUserManager {
    public static final String NOTIFICATION_UNLOCKED_BY_WORK_CHALLENGE_ACTION = "com.android.systemui.statusbar.work_challenge_unlocked_notification_action";
    public static final String PERMISSION_SELF = "com.android.systemui.permission.SELF";

    /* loaded from: classes21.dex */
    public interface UserChangedListener {
        void onUserChanged(int i);
    }

    void addUserChangedListener(UserChangedListener userChangedListener);

    SparseArray<UserInfo> getCurrentProfiles();

    int getCurrentUserId();

    boolean isAnyProfilePublicMode();

    boolean isCurrentProfile(int i);

    boolean isLockscreenPublicMode(int i);

    boolean needsRedaction(NotificationEntry notificationEntry);

    void setLockscreenPublicMode(boolean z, int i);

    void setUpWithPresenter(NotificationPresenter notificationPresenter);

    boolean shouldAllowLockscreenRemoteInput();

    boolean shouldHideNotifications(int i);

    boolean shouldHideNotifications(String str);

    boolean shouldShowLockscreenNotifications();

    boolean shouldShowOnKeyguard(NotificationEntry notificationEntry);

    void updatePublicMode();

    boolean userAllowsPrivateNotificationsInPublic(int i);

    default boolean needsSeparateWorkChallenge(int userId) {
        return false;
    }
}
