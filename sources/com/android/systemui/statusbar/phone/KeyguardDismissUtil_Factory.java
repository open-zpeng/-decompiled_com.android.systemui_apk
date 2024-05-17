package com.android.systemui.statusbar.phone;

import dagger.internal.Factory;
/* loaded from: classes21.dex */
public final class KeyguardDismissUtil_Factory implements Factory<KeyguardDismissUtil> {
    private static final KeyguardDismissUtil_Factory INSTANCE = new KeyguardDismissUtil_Factory();

    @Override // javax.inject.Provider
    public KeyguardDismissUtil get() {
        return provideInstance();
    }

    public static KeyguardDismissUtil provideInstance() {
        return new KeyguardDismissUtil();
    }

    public static KeyguardDismissUtil_Factory create() {
        return INSTANCE;
    }

    public static KeyguardDismissUtil newKeyguardDismissUtil() {
        return new KeyguardDismissUtil();
    }
}
