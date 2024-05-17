package com.android.systemui.statusbar.phone;

import android.util.Log;
import com.android.systemui.plugins.ActivityStarter;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class KeyguardDismissUtil implements KeyguardDismissHandler {
    private static final String TAG = "KeyguardDismissUtil";
    private volatile KeyguardDismissHandler mDismissHandler;

    public void setDismissHandler(KeyguardDismissHandler dismissHandler) {
        this.mDismissHandler = dismissHandler;
    }

    @Override // com.android.systemui.statusbar.phone.KeyguardDismissHandler
    public void executeWhenUnlocked(ActivityStarter.OnDismissAction action, boolean requiresShadeOpen) {
        KeyguardDismissHandler dismissHandler = this.mDismissHandler;
        if (dismissHandler == null) {
            Log.wtf(TAG, "KeyguardDismissHandler not set.");
            action.onDismiss();
            return;
        }
        dismissHandler.executeWhenUnlocked(action, requiresShadeOpen);
    }
}
