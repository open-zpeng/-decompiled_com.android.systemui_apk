package com.xiaopeng.speech.apirouter;

import android.util.Log;
import com.xiaopeng.lib.apirouter.server.IServicePublisher;
import com.xiaopeng.lib.apirouter.server.Publish;
import com.xiaopeng.speech.UnitySpeechEngine;
/* loaded from: classes23.dex */
public class ApiRouterUnitySceneService implements IServicePublisher {
    @Publish
    public void onEvent(String event, String data) {
        Log.i("ApiRouterUnityService", "消息接收 event== " + event + ",data:" + data);
        UnitySpeechEngine.dispatchVuiEvent(event, data);
    }

    @Publish
    public String getElementState(String sceneId, String elementId) {
        String result = UnitySpeechEngine.getElementState(sceneId, elementId);
        return result;
    }
}
