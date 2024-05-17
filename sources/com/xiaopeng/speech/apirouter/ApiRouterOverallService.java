package com.xiaopeng.speech.apirouter;

import android.util.Log;
import com.xiaopeng.lib.apirouter.server.IServicePublisher;
import com.xiaopeng.lib.apirouter.server.Publish;
import com.xiaopeng.speech.XpSpeechEngine;
/* loaded from: classes23.dex */
public class ApiRouterOverallService implements IServicePublisher {
    @Publish
    public void onEvent(String event, String data) {
        Log.d("ApiRouterOverallService", "消息接收 event== " + event + ",data:" + data);
        XpSpeechEngine.dispatchOverallEvent(event, data);
    }

    @Publish
    public void onQuery(String event, String data, String callback) {
        Log.d("ApiRouterOverallService", "消息接收 event== " + event + ",data:" + data);
        XpSpeechEngine.dispatchOverallQuery(event, data, callback);
    }
}
