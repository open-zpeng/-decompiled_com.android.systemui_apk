package com.android.systemui.statusbar;

import android.content.Context;
import android.util.AttributeSet;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class NotificationShelf_Factory implements Factory<NotificationShelf> {
    private final Provider<AttributeSet> attrsProvider;
    private final Provider<Context> contextProvider;
    private final Provider<KeyguardBypassController> keyguardBypassControllerProvider;

    public NotificationShelf_Factory(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<KeyguardBypassController> keyguardBypassControllerProvider) {
        this.contextProvider = contextProvider;
        this.attrsProvider = attrsProvider;
        this.keyguardBypassControllerProvider = keyguardBypassControllerProvider;
    }

    @Override // javax.inject.Provider
    public NotificationShelf get() {
        return provideInstance(this.contextProvider, this.attrsProvider, this.keyguardBypassControllerProvider);
    }

    public static NotificationShelf provideInstance(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<KeyguardBypassController> keyguardBypassControllerProvider) {
        return new NotificationShelf(contextProvider.get(), attrsProvider.get(), keyguardBypassControllerProvider.get());
    }

    public static NotificationShelf_Factory create(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<KeyguardBypassController> keyguardBypassControllerProvider) {
        return new NotificationShelf_Factory(contextProvider, attrsProvider, keyguardBypassControllerProvider);
    }

    public static NotificationShelf newNotificationShelf(Context context, AttributeSet attrs, KeyguardBypassController keyguardBypassController) {
        return new NotificationShelf(context, attrs, keyguardBypassController);
    }
}
