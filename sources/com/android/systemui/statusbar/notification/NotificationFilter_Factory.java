package com.android.systemui.statusbar.notification;

import dagger.internal.Factory;
/* loaded from: classes21.dex */
public final class NotificationFilter_Factory implements Factory<NotificationFilter> {
    private static final NotificationFilter_Factory INSTANCE = new NotificationFilter_Factory();

    @Override // javax.inject.Provider
    public NotificationFilter get() {
        return provideInstance();
    }

    public static NotificationFilter provideInstance() {
        return new NotificationFilter();
    }

    public static NotificationFilter_Factory create() {
        return INSTANCE;
    }

    public static NotificationFilter newNotificationFilter() {
        return new NotificationFilter();
    }
}
