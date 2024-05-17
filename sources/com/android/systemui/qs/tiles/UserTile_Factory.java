package com.android.systemui.qs.tiles;

import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class UserTile_Factory implements Factory<UserTile> {
    private final Provider<QSHost> hostProvider;
    private final Provider<UserInfoController> userInfoControllerProvider;
    private final Provider<UserSwitcherController> userSwitcherControllerProvider;

    public UserTile_Factory(Provider<QSHost> hostProvider, Provider<UserSwitcherController> userSwitcherControllerProvider, Provider<UserInfoController> userInfoControllerProvider) {
        this.hostProvider = hostProvider;
        this.userSwitcherControllerProvider = userSwitcherControllerProvider;
        this.userInfoControllerProvider = userInfoControllerProvider;
    }

    @Override // javax.inject.Provider
    public UserTile get() {
        return provideInstance(this.hostProvider, this.userSwitcherControllerProvider, this.userInfoControllerProvider);
    }

    public static UserTile provideInstance(Provider<QSHost> hostProvider, Provider<UserSwitcherController> userSwitcherControllerProvider, Provider<UserInfoController> userInfoControllerProvider) {
        return new UserTile(hostProvider.get(), userSwitcherControllerProvider.get(), userInfoControllerProvider.get());
    }

    public static UserTile_Factory create(Provider<QSHost> hostProvider, Provider<UserSwitcherController> userSwitcherControllerProvider, Provider<UserInfoController> userInfoControllerProvider) {
        return new UserTile_Factory(hostProvider, userSwitcherControllerProvider, userInfoControllerProvider);
    }

    public static UserTile newUserTile(QSHost host, UserSwitcherController userSwitcherController, UserInfoController userInfoController) {
        return new UserTile(host, userSwitcherController, userInfoController);
    }
}
