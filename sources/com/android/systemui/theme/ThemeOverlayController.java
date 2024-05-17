package com.android.systemui.theme;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.om.OverlayManager;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.SystemUI;
import com.google.android.collect.Sets;
import java.util.Map;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes21.dex */
public class ThemeOverlayController extends SystemUI {
    private static final boolean DEBUG = false;
    private static final String TAG = "ThemeOverlayController";
    private ThemeOverlayManager mThemeManager;
    private UserManager mUserManager;

    @Override // com.android.systemui.SystemUI
    public void start() {
        this.mUserManager = (UserManager) this.mContext.getSystemService(UserManager.class);
        this.mThemeManager = new ThemeOverlayManager((OverlayManager) this.mContext.getSystemService(OverlayManager.class), AsyncTask.THREAD_POOL_EXECUTOR, this.mContext.getString(R.string.launcher_overlayable_package), this.mContext.getString(R.string.themepicker_overlayable_package));
        Handler bgHandler = (Handler) Dependency.get(Dependency.BG_HANDLER);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.USER_SWITCHED");
        filter.addAction("android.intent.action.MANAGED_PROFILE_ADDED");
        this.mContext.registerReceiverAsUser(new BroadcastReceiver() { // from class: com.android.systemui.theme.ThemeOverlayController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                ThemeOverlayController.this.updateThemeOverlays();
            }
        }, UserHandle.ALL, filter, null, bgHandler);
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("theme_customization_overlay_packages"), false, new ContentObserver(bgHandler) { // from class: com.android.systemui.theme.ThemeOverlayController.2
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Uri uri, int userId) {
                if (ActivityManager.getCurrentUser() == userId) {
                    ThemeOverlayController.this.updateThemeOverlays();
                }
            }
        }, -1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateThemeOverlays() {
        int currentUser = ActivityManager.getCurrentUser();
        String overlayPackageJson = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "theme_customization_overlay_packages", currentUser);
        Map<String, String> categoryToPackage = new ArrayMap<>();
        if (!TextUtils.isEmpty(overlayPackageJson)) {
            try {
                JSONObject object = new JSONObject(overlayPackageJson);
                for (String category : ThemeOverlayManager.THEME_CATEGORIES) {
                    if (object.has(category)) {
                        categoryToPackage.put(category, object.getString(category));
                    }
                }
            } catch (JSONException e) {
                Log.i(TAG, "Failed to parse THEME_CUSTOMIZATION_OVERLAY_PACKAGES.", e);
            }
        }
        Set<UserHandle> userHandles = Sets.newHashSet(new UserHandle[]{UserHandle.of(currentUser)});
        for (UserInfo userInfo : this.mUserManager.getEnabledProfiles(currentUser)) {
            if (userInfo.isManagedProfile()) {
                userHandles.add(userInfo.getUserHandle());
            }
        }
        this.mThemeManager.applyCurrentUserOverlays(categoryToPackage, userHandles);
    }
}
