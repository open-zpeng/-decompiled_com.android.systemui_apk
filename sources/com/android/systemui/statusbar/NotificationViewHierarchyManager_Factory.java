package com.android.systemui.statusbar;

import android.content.Context;
import android.os.Handler;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.ShadeController;
import dagger.Lazy;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class NotificationViewHierarchyManager_Factory implements Factory<NotificationViewHierarchyManager> {
    private final Provider<BubbleController> bubbleControllerProvider;
    private final Provider<KeyguardBypassController> bypassControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<NotificationGroupManager> groupManagerProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<NotificationEntryManager> notificationEntryManagerProvider;
    private final Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider;
    private final Provider<DynamicPrivacyController> privacyControllerProvider;
    private final Provider<ShadeController> shadeControllerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<VisualStabilityManager> visualStabilityManagerProvider;

    public NotificationViewHierarchyManager_Factory(Provider<Context> contextProvider, Provider<Handler> mainHandlerProvider, Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider, Provider<NotificationGroupManager> groupManagerProvider, Provider<VisualStabilityManager> visualStabilityManagerProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<NotificationEntryManager> notificationEntryManagerProvider, Provider<ShadeController> shadeControllerProvider, Provider<KeyguardBypassController> bypassControllerProvider, Provider<BubbleController> bubbleControllerProvider, Provider<DynamicPrivacyController> privacyControllerProvider) {
        this.contextProvider = contextProvider;
        this.mainHandlerProvider = mainHandlerProvider;
        this.notificationLockscreenUserManagerProvider = notificationLockscreenUserManagerProvider;
        this.groupManagerProvider = groupManagerProvider;
        this.visualStabilityManagerProvider = visualStabilityManagerProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.notificationEntryManagerProvider = notificationEntryManagerProvider;
        this.shadeControllerProvider = shadeControllerProvider;
        this.bypassControllerProvider = bypassControllerProvider;
        this.bubbleControllerProvider = bubbleControllerProvider;
        this.privacyControllerProvider = privacyControllerProvider;
    }

    @Override // javax.inject.Provider
    public NotificationViewHierarchyManager get() {
        return provideInstance(this.contextProvider, this.mainHandlerProvider, this.notificationLockscreenUserManagerProvider, this.groupManagerProvider, this.visualStabilityManagerProvider, this.statusBarStateControllerProvider, this.notificationEntryManagerProvider, this.shadeControllerProvider, this.bypassControllerProvider, this.bubbleControllerProvider, this.privacyControllerProvider);
    }

    public static NotificationViewHierarchyManager provideInstance(Provider<Context> contextProvider, Provider<Handler> mainHandlerProvider, Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider, Provider<NotificationGroupManager> groupManagerProvider, Provider<VisualStabilityManager> visualStabilityManagerProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<NotificationEntryManager> notificationEntryManagerProvider, Provider<ShadeController> shadeControllerProvider, Provider<KeyguardBypassController> bypassControllerProvider, Provider<BubbleController> bubbleControllerProvider, Provider<DynamicPrivacyController> privacyControllerProvider) {
        return new NotificationViewHierarchyManager(contextProvider.get(), mainHandlerProvider.get(), notificationLockscreenUserManagerProvider.get(), groupManagerProvider.get(), visualStabilityManagerProvider.get(), statusBarStateControllerProvider.get(), notificationEntryManagerProvider.get(), DoubleCheck.lazy(shadeControllerProvider), bypassControllerProvider.get(), bubbleControllerProvider.get(), privacyControllerProvider.get());
    }

    public static NotificationViewHierarchyManager_Factory create(Provider<Context> contextProvider, Provider<Handler> mainHandlerProvider, Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider, Provider<NotificationGroupManager> groupManagerProvider, Provider<VisualStabilityManager> visualStabilityManagerProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<NotificationEntryManager> notificationEntryManagerProvider, Provider<ShadeController> shadeControllerProvider, Provider<KeyguardBypassController> bypassControllerProvider, Provider<BubbleController> bubbleControllerProvider, Provider<DynamicPrivacyController> privacyControllerProvider) {
        return new NotificationViewHierarchyManager_Factory(contextProvider, mainHandlerProvider, notificationLockscreenUserManagerProvider, groupManagerProvider, visualStabilityManagerProvider, statusBarStateControllerProvider, notificationEntryManagerProvider, shadeControllerProvider, bypassControllerProvider, bubbleControllerProvider, privacyControllerProvider);
    }

    public static NotificationViewHierarchyManager newNotificationViewHierarchyManager(Context context, Handler mainHandler, NotificationLockscreenUserManager notificationLockscreenUserManager, NotificationGroupManager groupManager, VisualStabilityManager visualStabilityManager, StatusBarStateController statusBarStateController, NotificationEntryManager notificationEntryManager, Lazy<ShadeController> shadeController, KeyguardBypassController bypassController, BubbleController bubbleController, DynamicPrivacyController privacyController) {
        return new NotificationViewHierarchyManager(context, mainHandler, notificationLockscreenUserManager, groupManager, visualStabilityManager, statusBarStateController, notificationEntryManager, shadeController, bypassController, bubbleController, privacyController);
    }
}
