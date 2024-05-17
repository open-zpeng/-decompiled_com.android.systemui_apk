package com.android.systemui.statusbar.notification;

import android.content.Context;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.tuner.TunerService;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class BypassHeadsUpNotifier_Factory implements Factory<BypassHeadsUpNotifier> {
    private final Provider<KeyguardBypassController> bypassControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<HeadsUpManagerPhone> headsUpManagerProvider;
    private final Provider<NotificationMediaManager> mediaManagerProvider;
    private final Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<TunerService> tunerServiceProvider;

    public BypassHeadsUpNotifier_Factory(Provider<Context> contextProvider, Provider<KeyguardBypassController> bypassControllerProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<HeadsUpManagerPhone> headsUpManagerProvider, Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider, Provider<NotificationMediaManager> mediaManagerProvider, Provider<TunerService> tunerServiceProvider) {
        this.contextProvider = contextProvider;
        this.bypassControllerProvider = bypassControllerProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.headsUpManagerProvider = headsUpManagerProvider;
        this.notificationLockscreenUserManagerProvider = notificationLockscreenUserManagerProvider;
        this.mediaManagerProvider = mediaManagerProvider;
        this.tunerServiceProvider = tunerServiceProvider;
    }

    @Override // javax.inject.Provider
    public BypassHeadsUpNotifier get() {
        return provideInstance(this.contextProvider, this.bypassControllerProvider, this.statusBarStateControllerProvider, this.headsUpManagerProvider, this.notificationLockscreenUserManagerProvider, this.mediaManagerProvider, this.tunerServiceProvider);
    }

    public static BypassHeadsUpNotifier provideInstance(Provider<Context> contextProvider, Provider<KeyguardBypassController> bypassControllerProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<HeadsUpManagerPhone> headsUpManagerProvider, Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider, Provider<NotificationMediaManager> mediaManagerProvider, Provider<TunerService> tunerServiceProvider) {
        return new BypassHeadsUpNotifier(contextProvider.get(), bypassControllerProvider.get(), statusBarStateControllerProvider.get(), headsUpManagerProvider.get(), notificationLockscreenUserManagerProvider.get(), mediaManagerProvider.get(), tunerServiceProvider.get());
    }

    public static BypassHeadsUpNotifier_Factory create(Provider<Context> contextProvider, Provider<KeyguardBypassController> bypassControllerProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<HeadsUpManagerPhone> headsUpManagerProvider, Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider, Provider<NotificationMediaManager> mediaManagerProvider, Provider<TunerService> tunerServiceProvider) {
        return new BypassHeadsUpNotifier_Factory(contextProvider, bypassControllerProvider, statusBarStateControllerProvider, headsUpManagerProvider, notificationLockscreenUserManagerProvider, mediaManagerProvider, tunerServiceProvider);
    }

    public static BypassHeadsUpNotifier newBypassHeadsUpNotifier(Context context, KeyguardBypassController bypassController, StatusBarStateController statusBarStateController, HeadsUpManagerPhone headsUpManager, NotificationLockscreenUserManager notificationLockscreenUserManager, NotificationMediaManager mediaManager, TunerService tunerService) {
        return new BypassHeadsUpNotifier(context, bypassController, statusBarStateController, headsUpManager, notificationLockscreenUserManager, mediaManager, tunerService);
    }
}
