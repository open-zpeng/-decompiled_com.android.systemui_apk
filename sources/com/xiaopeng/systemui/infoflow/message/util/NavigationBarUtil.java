package com.xiaopeng.systemui.infoflow.message.util;

import android.os.RemoteException;
import android.os.ServiceManager;
import com.android.internal.statusbar.IStatusBarService;
/* loaded from: classes24.dex */
public class NavigationBarUtil {
    private static final int KEYCODE_NAVIGATION_HIDE = 2002;
    private static final int KEYCODE_NAVIGATION_SHOW = 2001;

    public static void showNavigationBar() {
        IStatusBarService barService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
        try {
            barService.handleSystemKey(2001);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void hideNavigationBar() {
        IStatusBarService barService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
        try {
            barService.handleSystemKey(2002);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
