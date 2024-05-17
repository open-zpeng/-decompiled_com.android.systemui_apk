package com.android.systemui.statusbar.policy;

import com.android.systemui.Dumpable;
/* loaded from: classes21.dex */
public interface SecurityController extends CallbackController<SecurityControllerCallback>, Dumpable {

    /* loaded from: classes21.dex */
    public interface SecurityControllerCallback {
        void onStateChanged();
    }

    String getDeviceOwnerName();

    CharSequence getDeviceOwnerOrganizationName();

    String getPrimaryVpnName();

    String getProfileOwnerName();

    CharSequence getWorkProfileOrganizationName();

    String getWorkProfileVpnName();

    boolean hasCACertInCurrentUser();

    boolean hasCACertInWorkProfile();

    boolean hasProfileOwner();

    boolean hasWorkProfile();

    boolean isDeviceManaged();

    boolean isNetworkLoggingEnabled();

    boolean isVpnBranded();

    boolean isVpnEnabled();

    boolean isVpnRestricted();

    void onUserSwitched(int i);
}
