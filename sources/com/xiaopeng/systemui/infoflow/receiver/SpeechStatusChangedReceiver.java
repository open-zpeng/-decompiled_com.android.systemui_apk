package com.xiaopeng.systemui.infoflow.receiver;

import android.bluetooth.BluetoothHeadsetClientCall;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.xiaopeng.systemui.infoflow.helper.AIAvatarViewServiceHelper;
/* loaded from: classes24.dex */
public class SpeechStatusChangedReceiver extends BroadcastReceiver {
    private static final String ACTION_SPEECH_STATUS_CHANGED = "com.xiaopeng.speechStatusChanged";
    private static final String TAG = "SpeechStatusChangedReceiver";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        BluetoothHeadsetClientCall mCall;
        String action = intent.getAction();
        if (!ACTION_SPEECH_STATUS_CHANGED.equals(action) && "android.bluetooth.headsetclient.profile.action.AG_CALL_CHANGED".equals(action) && (mCall = (BluetoothHeadsetClientCall) intent.getExtra("android.bluetooth.headsetclient.extra.CALL", null)) != null) {
            int callState = mCall.getState();
            Log.i(TAG, "ACTION_CALL_CHANGED:" + callState);
            if (callState == 4) {
                AIAvatarViewServiceHelper.instance().updateCallingStatus(true);
            } else if (callState == 2) {
                AIAvatarViewServiceHelper.instance().updateCallingStatus(true);
            } else if (callState == 0) {
                AIAvatarViewServiceHelper.instance().updateCallingStatus(true);
            } else if (callState == 7) {
                AIAvatarViewServiceHelper.instance().updateCallingStatus(false);
            }
        }
    }

    public void register(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SPEECH_STATUS_CHANGED);
        filter.addAction("android.bluetooth.headsetclient.profile.action.AG_CALL_CHANGED");
        context.registerReceiver(this, filter);
    }
}
