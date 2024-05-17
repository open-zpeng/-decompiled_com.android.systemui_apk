package com.xiaopeng.systemui.infoflow.listener;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import com.android.systemui.SystemUIApplication;
import com.xiaopeng.systemui.infoflow.message.KeyConfig;
import com.xiaopeng.systemui.infoflow.util.Logger;
/* loaded from: classes24.dex */
public class TouchRotationSpeedObserver {
    private static final String KEY_TOUCH_ROTATION_SPEED = "xp_touch_rotation_speed";
    private static final String TAG = "TouchRotationSpeedObserver";
    private static final int TOUCH_ROTATION_SPEED_HIGH = 1;
    private static final int TOUCH_ROTATION_SPEED_LOW = 3;
    private static final int TOUCH_ROTATION_SPEED_MIDDLE = 2;
    private final ContentObserver mContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.xiaopeng.systemui.infoflow.listener.TouchRotationSpeedObserver.1
        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            TouchRotationSpeedObserver.this.checkTouchRotationSpeed();
        }
    };

    public void startMonitor() {
        ContentResolver contentResolver = SystemUIApplication.getContext().getContentResolver();
        checkTouchRotationSpeed();
        contentResolver.registerContentObserver(Settings.System.getUriFor(KEY_TOUCH_ROTATION_SPEED), false, this.mContentObserver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkTouchRotationSpeed() {
        int value = Settings.System.getInt(SystemUIApplication.getContext().getContentResolver(), KEY_TOUCH_ROTATION_SPEED, 2);
        Logger.d(TAG, "checkTouchRotationSpeed, value = " + value);
        KeyConfig.saveConfig(value);
    }
}
