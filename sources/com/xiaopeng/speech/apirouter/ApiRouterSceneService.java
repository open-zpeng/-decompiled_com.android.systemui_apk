package com.xiaopeng.speech.apirouter;

import android.util.Log;
import com.xiaopeng.lib.apirouter.server.IServicePublisher;
import com.xiaopeng.lib.apirouter.server.Publish;
import com.xiaopeng.speech.XpSpeechEngine;
/* loaded from: classes23.dex */
public class ApiRouterSceneService implements IServicePublisher {
    @Publish
    public void onEvent(String event, String data) {
        Log.i("ApiRouterSceneService", "消息接收 event== " + event + ",data:" + data);
        XpSpeechEngine.dispatchVuiEvent(event, data);
    }

    @Publish
    public String getElementState(String sceneId, String elementId) {
        String result = XpSpeechEngine.getElementState(sceneId, elementId);
        return result;
    }
}
