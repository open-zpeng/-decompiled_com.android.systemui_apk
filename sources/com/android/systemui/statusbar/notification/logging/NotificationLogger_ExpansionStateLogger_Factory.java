package com.android.systemui.statusbar.notification.logging;

import com.android.systemui.UiOffloadThread;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class NotificationLogger_ExpansionStateLogger_Factory implements Factory<NotificationLogger.ExpansionStateLogger> {
    private final Provider<UiOffloadThread> uiOffloadThreadProvider;

    public NotificationLogger_ExpansionStateLogger_Factory(Provider<UiOffloadThread> uiOffloadThreadProvider) {
        this.uiOffloadThreadProvider = uiOffloadThreadProvider;
    }

    @Override // javax.inject.Provider
    public NotificationLogger.ExpansionStateLogger get() {
        return provideInstance(this.uiOffloadThreadProvider);
    }

    public static NotificationLogger.ExpansionStateLogger provideInstance(Provider<UiOffloadThread> uiOffloadThreadProvider) {
        return new NotificationLogger.ExpansionStateLogger(uiOffloadThreadProvider.get());
    }

    public static NotificationLogger_ExpansionStateLogger_Factory create(Provider<UiOffloadThread> uiOffloadThreadProvider) {
        return new NotificationLogger_ExpansionStateLogger_Factory(uiOffloadThreadProvider);
    }

    public static NotificationLogger.ExpansionStateLogger newExpansionStateLogger(UiOffloadThread uiOffloadThread) {
        return new NotificationLogger.ExpansionStateLogger(uiOffloadThread);
    }
}
