package com.android.systemui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
/* loaded from: classes21.dex */
public class SysuiRestartReceiver extends BroadcastReceiver {
    public static String ACTION = "com.android.systemui.action.RESTART";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Log.w("SysuiRestartReceiver", "receiver the RESTART action");
    }
}
