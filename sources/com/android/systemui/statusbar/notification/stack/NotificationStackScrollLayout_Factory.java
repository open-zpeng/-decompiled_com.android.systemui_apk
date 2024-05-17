package com.android.systemui.statusbar.notification.stack;

import android.content.Context;
import android.util.AttributeSet;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class NotificationStackScrollLayout_Factory implements Factory<NotificationStackScrollLayout> {
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<Boolean> allowLongPressProvider;
    private final Provider<AttributeSet> attrsProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DynamicPrivacyController> dynamicPrivacyControllerProvider;
    private final Provider<FalsingManager> falsingManagerProvider;
    private final Provider<HeadsUpManagerPhone> headsUpManagerProvider;
    private final Provider<KeyguardBypassController> keyguardBypassControllerProvider;
    private final Provider<NotificationRoundnessManager> notificationRoundnessManagerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;

    public NotificationStackScrollLayout_Factory(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<Boolean> allowLongPressProvider, Provider<NotificationRoundnessManager> notificationRoundnessManagerProvider, Provider<DynamicPrivacyController> dynamicPrivacyControllerProvider, Provider<ConfigurationController> configurationControllerProvider, Provider<ActivityStarter> activityStarterProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<HeadsUpManagerPhone> headsUpManagerProvider, Provider<KeyguardBypassController> keyguardBypassControllerProvider, Provider<FalsingManager> falsingManagerProvider) {
        this.contextProvider = contextProvider;
        this.attrsProvider = attrsProvider;
        this.allowLongPressProvider = allowLongPressProvider;
        this.notificationRoundnessManagerProvider = notificationRoundnessManagerProvider;
        this.dynamicPrivacyControllerProvider = dynamicPrivacyControllerProvider;
        this.configurationControllerProvider = configurationControllerProvider;
        this.activityStarterProvider = activityStarterProvider;
        this.statusBarStateControllerProvider = statusBarStateControllerProvider;
        this.headsUpManagerProvider = headsUpManagerProvider;
        this.keyguardBypassControllerProvider = keyguardBypassControllerProvider;
        this.falsingManagerProvider = falsingManagerProvider;
    }

    @Override // javax.inject.Provider
    public NotificationStackScrollLayout get() {
        return provideInstance(this.contextProvider, this.attrsProvider, this.allowLongPressProvider, this.notificationRoundnessManagerProvider, this.dynamicPrivacyControllerProvider, this.configurationControllerProvider, this.activityStarterProvider, this.statusBarStateControllerProvider, this.headsUpManagerProvider, this.keyguardBypassControllerProvider, this.falsingManagerProvider);
    }

    public static NotificationStackScrollLayout provideInstance(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<Boolean> allowLongPressProvider, Provider<NotificationRoundnessManager> notificationRoundnessManagerProvider, Provider<DynamicPrivacyController> dynamicPrivacyControllerProvider, Provider<ConfigurationController> configurationControllerProvider, Provider<ActivityStarter> activityStarterProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<HeadsUpManagerPhone> headsUpManagerProvider, Provider<KeyguardBypassController> keyguardBypassControllerProvider, Provider<FalsingManager> falsingManagerProvider) {
        return new NotificationStackScrollLayout(contextProvider.get(), attrsProvider.get(), allowLongPressProvider.get().booleanValue(), notificationRoundnessManagerProvider.get(), dynamicPrivacyControllerProvider.get(), configurationControllerProvider.get(), activityStarterProvider.get(), statusBarStateControllerProvider.get(), headsUpManagerProvider.get(), keyguardBypassControllerProvider.get(), falsingManagerProvider.get());
    }

    public static NotificationStackScrollLayout_Factory create(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<Boolean> allowLongPressProvider, Provider<NotificationRoundnessManager> notificationRoundnessManagerProvider, Provider<DynamicPrivacyController> dynamicPrivacyControllerProvider, Provider<ConfigurationController> configurationControllerProvider, Provider<ActivityStarter> activityStarterProvider, Provider<StatusBarStateController> statusBarStateControllerProvider, Provider<HeadsUpManagerPhone> headsUpManagerProvider, Provider<KeyguardBypassController> keyguardBypassControllerProvider, Provider<FalsingManager> falsingManagerProvider) {
        return new NotificationStackScrollLayout_Factory(contextProvider, attrsProvider, allowLongPressProvider, notificationRoundnessManagerProvider, dynamicPrivacyControllerProvider, configurationControllerProvider, activityStarterProvider, statusBarStateControllerProvider, headsUpManagerProvider, keyguardBypassControllerProvider, falsingManagerProvider);
    }

    public static NotificationStackScrollLayout newNotificationStackScrollLayout(Context context, AttributeSet attrs, boolean allowLongPress, NotificationRoundnessManager notificationRoundnessManager, DynamicPrivacyController dynamicPrivacyController, ConfigurationController configurationController, ActivityStarter activityStarter, StatusBarStateController statusBarStateController, HeadsUpManagerPhone headsUpManager, KeyguardBypassController keyguardBypassController, FalsingManager falsingManager) {
        return new NotificationStackScrollLayout(context, attrs, allowLongPress, notificationRoundnessManager, dynamicPrivacyController, configurationController, activityStarter, statusBarStateController, headsUpManager, keyguardBypassController, falsingManager);
    }
}
