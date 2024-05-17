package com.android.systemui.statusbar.policy;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.ActivityIntentHelper;
import com.android.systemui.statusbar.phone.KeyguardPreviewContainer;
import java.util.List;
/* loaded from: classes21.dex */
public class PreviewInflater {
    private static final String META_DATA_KEYGUARD_LAYOUT = "com.android.keyguard.layout";
    private static final String TAG = "PreviewInflater";
    private final ActivityIntentHelper mActivityIntentHelper;
    private Context mContext;
    private LockPatternUtils mLockPatternUtils;

    public PreviewInflater(Context context, LockPatternUtils lockPatternUtils, ActivityIntentHelper activityIntentHelper) {
        this.mContext = context;
        this.mLockPatternUtils = lockPatternUtils;
        this.mActivityIntentHelper = activityIntentHelper;
    }

    public View inflatePreview(Intent intent) {
        WidgetInfo info = getWidgetInfo(intent);
        return inflatePreview(info);
    }

    public View inflatePreviewFromService(ComponentName componentName) {
        WidgetInfo info = getWidgetInfoFromService(componentName);
        return inflatePreview(info);
    }

    private KeyguardPreviewContainer inflatePreview(WidgetInfo info) {
        View v;
        if (info == null || (v = inflateWidgetView(info)) == null) {
            return null;
        }
        KeyguardPreviewContainer container = new KeyguardPreviewContainer(this.mContext, null);
        container.addView(v);
        return container;
    }

    private View inflateWidgetView(WidgetInfo widgetInfo) {
        try {
            Context appContext = this.mContext.createPackageContext(widgetInfo.contextPackage, 4);
            LayoutInflater appInflater = (LayoutInflater) appContext.getSystemService("layout_inflater");
            View widgetView = appInflater.cloneInContext(appContext).inflate(widgetInfo.layoutId, (ViewGroup) null, false);
            return widgetView;
        } catch (PackageManager.NameNotFoundException | RuntimeException e) {
            Log.w(TAG, "Error creating widget view", e);
            return null;
        }
    }

    private WidgetInfo getWidgetInfoFromService(ComponentName componentName) {
        PackageManager packageManager = this.mContext.getPackageManager();
        try {
            Bundle metaData = packageManager.getServiceInfo(componentName, 128).metaData;
            return getWidgetInfoFromMetaData(componentName.getPackageName(), metaData);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Failed to load preview; " + componentName.flattenToShortString() + " not found", e);
            return null;
        }
    }

    private WidgetInfo getWidgetInfoFromMetaData(String contextPackage, Bundle metaData) {
        int layoutId;
        if (metaData == null || (layoutId = metaData.getInt(META_DATA_KEYGUARD_LAYOUT)) == 0) {
            return null;
        }
        WidgetInfo info = new WidgetInfo();
        info.contextPackage = contextPackage;
        info.layoutId = layoutId;
        return info;
    }

    private WidgetInfo getWidgetInfo(Intent intent) {
        PackageManager packageManager = this.mContext.getPackageManager();
        List<ResolveInfo> appList = packageManager.queryIntentActivitiesAsUser(intent, 851968, KeyguardUpdateMonitor.getCurrentUser());
        if (appList.size() == 0) {
            return null;
        }
        ResolveInfo resolved = packageManager.resolveActivityAsUser(intent, 851968 | 128, KeyguardUpdateMonitor.getCurrentUser());
        if (this.mActivityIntentHelper.wouldLaunchResolverActivity(resolved, appList) || resolved == null || resolved.activityInfo == null) {
            return null;
        }
        return getWidgetInfoFromMetaData(resolved.activityInfo.packageName, resolved.activityInfo.metaData);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class WidgetInfo {
        String contextPackage;
        int layoutId;

        private WidgetInfo() {
        }
    }
}
