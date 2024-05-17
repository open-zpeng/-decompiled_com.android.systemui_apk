package com.android.systemui.shared.system;

import android.app.AppGlobals;
import android.app.admin.DevicePolicyManager;
/* loaded from: classes21.dex */
public class DevicePolicyManagerWrapper {
    private static final DevicePolicyManagerWrapper sInstance = new DevicePolicyManagerWrapper();
    private static final DevicePolicyManager sDevicePolicyManager = (DevicePolicyManager) AppGlobals.getInitialApplication().getSystemService(DevicePolicyManager.class);

    private DevicePolicyManagerWrapper() {
    }

    public static DevicePolicyManagerWrapper getInstance() {
        return sInstance;
    }

    public boolean isLockTaskPermitted(String pkg) {
        return sDevicePolicyManager.isLockTaskPermitted(pkg);
    }
}
