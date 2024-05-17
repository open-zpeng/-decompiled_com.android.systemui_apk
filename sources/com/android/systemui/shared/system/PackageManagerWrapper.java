package com.android.systemui.shared.system;

import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.IPackageManager;
import android.content.pm.ResolveInfo;
import android.os.RemoteException;
import android.os.UserHandle;
import java.util.List;
/* loaded from: classes21.dex */
public class PackageManagerWrapper {
    public static final String ACTION_PREFERRED_ACTIVITY_CHANGED = "android.intent.action.ACTION_PREFERRED_ACTIVITY_CHANGED";
    private static final PackageManagerWrapper sInstance = new PackageManagerWrapper();
    private static final IPackageManager mIPackageManager = AppGlobals.getPackageManager();

    public static PackageManagerWrapper getInstance() {
        return sInstance;
    }

    private PackageManagerWrapper() {
    }

    public ActivityInfo getActivityInfo(ComponentName componentName, int userId) {
        try {
            return mIPackageManager.getActivityInfo(componentName, 128, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ComponentName getHomeActivities(List<ResolveInfo> allHomeCandidates) {
        try {
            return mIPackageManager.getHomeActivities(allHomeCandidates);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResolveInfo resolveActivity(Intent intent, int flags) {
        String resolvedType = intent.resolveTypeIfNeeded(AppGlobals.getInitialApplication().getContentResolver());
        try {
            return mIPackageManager.resolveIntent(intent, resolvedType, flags, UserHandle.getCallingUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }
}
