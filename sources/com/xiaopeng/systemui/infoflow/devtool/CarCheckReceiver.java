package com.xiaopeng.systemui.infoflow.devtool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.xiaopeng.systemui.infoflow.checking.CarCheckHelper;
import com.xiaopeng.systemui.infoflow.util.Logger;
/* loaded from: classes24.dex */
public class CarCheckReceiver extends BroadcastReceiver {
    private static final String ACTION_START_CAR_CHECK = "com.android.system.ACTION_START_CAR_CHECK";
    private static final String ACTION_START_CAR_CHECK_IGON_TIME = "com.android.system.ACTION_START_CAR_CHECK_IGON_TIME";
    private static final String ACTION_START_EASTER_EGG = "com.android.system.ACTION_START_EASTER_EGG";
    private static final String ACTION_STOP_CAR_CHECK = "com.android.system.ACTION_STOP_CAR_CHECK";
    private static final String ACTION_STOP_EASTER_EGG = "com.android.system.ACTION_STOP_EASTER_EGG";
    private static final String TAG = "CarCheckReceiver";

    public void register(Context context) {
        Logger.d(TAG, "CarCheckReceiver register");
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_START_CAR_CHECK);
        filter.addAction(ACTION_STOP_CAR_CHECK);
        filter.addAction(ACTION_START_EASTER_EGG);
        filter.addAction(ACTION_STOP_EASTER_EGG);
        filter.addAction(ACTION_START_CAR_CHECK_IGON_TIME);
        context.registerReceiver(this, filter);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Logger.d(TAG, "CarCheckReceiver onReceive");
        String action = intent.getAction();
        Logger.d(TAG, "CarCheckReceiver onReceive action--" + action);
        if (ACTION_START_CAR_CHECK.equals(action)) {
            Logger.d(TAG, "onReceive ACTION_START_CAR_CHECK");
            CarCheckHelper.notifyCarCheck(true);
        } else if (ACTION_STOP_CAR_CHECK.equals(action)) {
            Logger.d(TAG, "onReceive ACTION_STOP_CAR_CHECK");
            CarCheckHelper.notifyCarCheck(false);
        } else if (ACTION_START_EASTER_EGG.equals(action)) {
            Logger.d(TAG, "onReceive ACTION_START_EASTER_EGG");
            CarCheckHelper.showEastEgg(true);
        } else if (ACTION_STOP_EASTER_EGG.equals(action)) {
            Logger.d(TAG, "onReceive ACTION_STOP_EASTER_EGG");
            CarCheckHelper.showEastEgg(false);
        } else if (ACTION_START_CAR_CHECK_IGON_TIME.equals(action)) {
            boolean useTest = intent.getBooleanExtra("test", false);
            CarCheckHelper.setElapsedTimeSatisfiedTest(useTest);
        }
    }
}
