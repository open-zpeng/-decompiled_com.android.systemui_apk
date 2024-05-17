package com.xiaopeng.systemui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.xiaopeng.systemui.helper.TrafficStatusEventHelper;
/* loaded from: classes24.dex */
public class TrafficStatusChangeReceiver extends BroadcastReceiver {
    private static final String ACTION_TRAFFIC_STATUS_CHAGNE = "com.xiaopeng.action.TRAFFIC_STATUS_CHANGE";
    private static final String TAG = "TrafficStatusChangeReceiver";

    public void register(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.xiaopeng.action.TRAFFIC_STATUS_CHANGE");
        context.registerReceiver(this, filter);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "onReceive action -- " + action);
        if (action.equals("com.xiaopeng.action.TRAFFIC_STATUS_CHANGE")) {
            TrafficStatusEventHelper.getInstance().notifyTrafficStatusChangedEvent();
        }
    }
}
