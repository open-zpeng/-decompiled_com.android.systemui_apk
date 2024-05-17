package com.android.systemui.statusbar.policy;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class UserInfoControllerImpl_Factory implements Factory<UserInfoControllerImpl> {
    private final Provider<Context> contextProvider;

    public UserInfoControllerImpl_Factory(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override // javax.inject.Provider
    public UserInfoControllerImpl get() {
        return provideInstance(this.contextProvider);
    }

    public static UserInfoControllerImpl provideInstance(Provider<Context> contextProvider) {
        return new UserInfoControllerImpl(contextProvider.get());
    }

    public static UserInfoControllerImpl_Factory create(Provider<Context> contextProvider) {
        return new UserInfoControllerImpl_Factory(contextProvider);
    }

    public static UserInfoControllerImpl newUserInfoControllerImpl(Context context) {
        return new UserInfoControllerImpl(context);
    }
}
