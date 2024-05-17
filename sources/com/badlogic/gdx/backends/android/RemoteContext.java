package com.badlogic.gdx.backends.android;

import android.content.Context;
import android.content.pm.PackageManager;
/* loaded from: classes21.dex */
public class RemoteContext {
    private static Context remoteContext = null;

    public static Context getRemoteContext(Context mContext) {
        if (remoteContext == null) {
            try {
                remoteContext = mContext.createPackageContext("com.xiaopeng.aiavatarservice", 3);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return remoteContext;
    }
}
