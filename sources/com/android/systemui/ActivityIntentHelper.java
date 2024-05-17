package com.android.systemui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import java.util.List;
/* loaded from: classes21.dex */
public class ActivityIntentHelper {
    private final Context mContext;

    public ActivityIntentHelper(Context context) {
        this.mContext = context;
    }

    public boolean wouldLaunchResolverActivity(Intent intent, int currentUserId) {
        ActivityInfo targetActivityInfo = getTargetActivityInfo(intent, currentUserId, false);
        return targetActivityInfo == null;
    }

    public ActivityInfo getTargetActivityInfo(Intent intent, int currentUserId, boolean onlyDirectBootAware) {
        ResolveInfo resolved;
        PackageManager packageManager = this.mContext.getPackageManager();
        int flags = onlyDirectBootAware ? 65536 : 65536 | 786432;
        List<ResolveInfo> appList = packageManager.queryIntentActivitiesAsUser(intent, flags, currentUserId);
        if (appList.size() == 0 || (resolved = packageManager.resolveActivityAsUser(intent, flags | 128, currentUserId)) == null || wouldLaunchResolverActivity(resolved, appList)) {
            return null;
        }
        return resolved.activityInfo;
    }

    public boolean wouldShowOverLockscreen(Intent intent, int currentUserId) {
        ActivityInfo targetActivityInfo = getTargetActivityInfo(intent, currentUserId, false);
        return targetActivityInfo != null && (targetActivityInfo.flags & 8389632) > 0;
    }

    public boolean wouldLaunchResolverActivity(ResolveInfo resolved, List<ResolveInfo> appList) {
        for (int i = 0; i < appList.size(); i++) {
            ResolveInfo tmp = appList.get(i);
            if (tmp.activityInfo.name.equals(resolved.activityInfo.name) && tmp.activityInfo.packageName.equals(resolved.activityInfo.packageName)) {
                return false;
            }
        }
        return true;
    }
}
