package com.android.systemui.statusbar.phone;

import dagger.internal.Factory;
/* loaded from: classes21.dex */
public final class LockscreenGestureLogger_Factory implements Factory<LockscreenGestureLogger> {
    private static final LockscreenGestureLogger_Factory INSTANCE = new LockscreenGestureLogger_Factory();

    @Override // javax.inject.Provider
    public LockscreenGestureLogger get() {
        return provideInstance();
    }

    public static LockscreenGestureLogger provideInstance() {
        return new LockscreenGestureLogger();
    }

    public static LockscreenGestureLogger_Factory create() {
        return INSTANCE;
    }

    public static LockscreenGestureLogger newLockscreenGestureLogger() {
        return new LockscreenGestureLogger();
    }
}
