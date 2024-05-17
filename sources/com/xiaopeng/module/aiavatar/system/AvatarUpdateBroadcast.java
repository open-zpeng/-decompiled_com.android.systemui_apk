package com.xiaopeng.module.aiavatar.system;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
/* loaded from: classes23.dex */
public class AvatarUpdateBroadcast extends BroadcastReceiver {
    public static final String AVATAR_UPDATE_ACTION = "com.xiaopeng.module.aiavatar.update.event";
    private static final String TAG = "AvatarUpdateBroadcast";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (AVATAR_UPDATE_ACTION.equals(action)) {
            String event = intent.getStringExtra("event");
            Log.d(TAG, "event:" + event);
            EventDispatcherManager.getInstance().dispatch(event);
        }
    }
}
