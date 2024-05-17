package com.xiaopeng.systemui;

import com.android.systemui.R;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
/* loaded from: classes24.dex */
public class DeviceFactory {
    private static final String TAG = "DeviceFactory";
    private DeviceFactory sInstance;

    public DeviceFactory getInstance() {
        if (this.sInstance == null) {
            this.sInstance = new DeviceFactory();
        }
        return this.sInstance;
    }

    private DeviceFactory() {
    }

    public static int getStatusBarResId() {
        return R.layout.status_bar_root;
    }

    public static boolean hasAccount() {
        return CarModelsManager.getFeature().isDriverAccountSupport();
    }
}
