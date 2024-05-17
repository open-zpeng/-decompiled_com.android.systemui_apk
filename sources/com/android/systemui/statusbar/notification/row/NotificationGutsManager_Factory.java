package com.android.systemui.statusbar.notification.row;

import android.content.Context;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class NotificationGutsManager_Factory implements Factory<NotificationGutsManager> {
    private final Provider<Context> contextProvider;
    private final Provider<VisualStabilityManager> visualStabilityManagerProvider;

    public NotificationGutsManager_Factory(Provider<Context> contextProvider, Provider<VisualStabilityManager> visualStabilityManagerProvider) {
        this.contextProvider = contextProvider;
        this.visualStabilityManagerProvider = visualStabilityManagerProvider;
    }

    @Override // javax.inject.Provider
    public NotificationGutsManager get() {
        return provideInstance(this.contextProvider, this.visualStabilityManagerProvider);
    }

    public static NotificationGutsManager provideInstance(Provider<Context> contextProvider, Provider<VisualStabilityManager> visualStabilityManagerProvider) {
        return new NotificationGutsManager(contextProvider.get(), visualStabilityManagerProvider.get());
    }

    public static NotificationGutsManager_Factory create(Provider<Context> contextProvider, Provider<VisualStabilityManager> visualStabilityManagerProvider) {
        return new NotificationGutsManager_Factory(contextProvider, visualStabilityManagerProvider);
    }

    public static NotificationGutsManager newNotificationGutsManager(Context context, VisualStabilityManager visualStabilityManager) {
        return new NotificationGutsManager(context, visualStabilityManager);
    }
}
