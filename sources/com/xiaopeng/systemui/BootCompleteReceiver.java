package com.xiaopeng.systemui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/* loaded from: classes24.dex */
public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompleteReceiver";
    public static boolean sIsBootCompleted = false;

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Logger.d(TAG, "receive boot complete");
        sIsBootCompleted = true;
    }
}
