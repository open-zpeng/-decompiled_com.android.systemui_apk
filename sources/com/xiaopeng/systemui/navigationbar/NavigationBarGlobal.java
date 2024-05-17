package com.xiaopeng.systemui.navigationbar;

import android.content.Context;
import android.provider.Settings;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.statusbar.StatusBarGlobal;
/* loaded from: classes24.dex */
public class NavigationBarGlobal {
    private static final String TAG = "NavigationBarGlobal";
    private static NavigationBarGlobal sNavigationBarGlobal;
    private Context mContext;

    private NavigationBarGlobal(Context context) {
        this.mContext = context;
    }

    public static NavigationBarGlobal getInstance(Context context) {
        NavigationBarGlobal navigationBarGlobal;
        synchronized (StatusBarGlobal.class) {
            if (sNavigationBarGlobal == null) {
                sNavigationBarGlobal = new NavigationBarGlobal(context);
            }
            navigationBarGlobal = sNavigationBarGlobal;
        }
        return navigationBarGlobal;
    }

    private int getIntForUser(String key, int defaultValue) {
        return Settings.System.getIntForUser(this.mContext.getContentResolver(), key, defaultValue, -2);
    }

    private void putIntForUser(String key, int value) {
        Logger.d(TAG, "putIntForUser key=" + key + " value=" + value);
        try {
            Settings.System.putIntForUser(this.mContext.getContentResolver(), key, value, -2);
        } catch (Exception e) {
        }
    }
}
