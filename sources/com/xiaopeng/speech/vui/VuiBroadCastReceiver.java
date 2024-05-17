package com.xiaopeng.speech.vui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.speech.vui.utils.LogUtils;
/* loaded from: classes.dex */
public class VuiBroadCastReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtils.i("VuiBroadCastReceiver", "onReceive:" + action);
        if ("carspeechservice.SpeechServer.Start".contains(action)) {
            VuiSceneManager.instance().setInSpeech(false);
            VuiSceneManager.instance().sendBroadCast();
            VuiSceneManager.instance().subscribe();
            VuiSceneManager.instance().handleSceneDataInfo();
        } else if (VuiConstants.INTENT_ACTION_ENV_CHANGED.contains(action)) {
            VuiSceneManager.instance().setInSpeech(false);
            VuiSceneManager.instance().handleAllSceneCache(false);
            VuiSceneManager.instance().handleSceneDataInfo();
        }
    }
}
