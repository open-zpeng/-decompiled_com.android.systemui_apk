package com.android.systemui.statusbar.phone;

import dagger.internal.Factory;
/* loaded from: classes21.dex */
public final class NotificationGroupAlertTransferHelper_Factory implements Factory<NotificationGroupAlertTransferHelper> {
    private static final NotificationGroupAlertTransferHelper_Factory INSTANCE = new NotificationGroupAlertTransferHelper_Factory();

    @Override // javax.inject.Provider
    public NotificationGroupAlertTransferHelper get() {
        return provideInstance();
    }

    public static NotificationGroupAlertTransferHelper provideInstance() {
        return new NotificationGroupAlertTransferHelper();
    }

    public static NotificationGroupAlertTransferHelper_Factory create() {
        return INSTANCE;
    }

    public static NotificationGroupAlertTransferHelper newNotificationGroupAlertTransferHelper() {
        return new NotificationGroupAlertTransferHelper();
    }
}
