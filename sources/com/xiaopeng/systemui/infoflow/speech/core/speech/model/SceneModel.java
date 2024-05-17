package com.xiaopeng.systemui.infoflow.speech.core.speech.model;

import com.xiaopeng.speech.protocol.SpeechModel;
import com.xiaopeng.speech.protocol.node.scene.SceneListener;
import com.xiaopeng.speech.protocol.node.scene.SceneNode;
import com.xiaopeng.speech.vui.VuiEngine;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.util.Logger;
/* loaded from: classes24.dex */
public class SceneModel extends SpeechModel {
    private static final String TAG = "SceneModel";

    public SceneModel() {
        subscribe(SceneNode.class, new SceneListener() { // from class: com.xiaopeng.systemui.infoflow.speech.core.speech.model.SceneModel.1
            @Override // com.xiaopeng.speech.protocol.node.scene.SceneListener
            public void onSceneEvent(String event, String data) {
                Logger.d("onSceneEvent", "event = " + event + ", data = " + data);
                VuiEngine.getInstance(ContextUtils.getContext()).dispatchVuiEvent(event, data);
            }
        });
    }
}
