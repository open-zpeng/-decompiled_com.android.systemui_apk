package com.xiaopeng.speech.protocol.node.scene;

import android.text.TextUtils;
import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.common.SpeechConstant;
import com.xiaopeng.speech.protocol.event.VuiEvent;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.systemui.helper.PackageHelper;
import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class SceneNode extends SpeechNode<SceneListener> {
    List<String> supportAppNameList = Arrays.asList("com.android.systemui", VuiConstants.CHARGE, VuiConstants.SCENEDEMO, SpeechConstant.SPEECH_SERVICE_PACKAGE_NAME, PackageHelper.PACKAGE_ACCOUNT_CENTER);

    @SpeechAnnotation(event = VuiEvent.SCENE_CONTROL)
    public void onSceneEvent(String event, String data) {
        if (TextUtils.isEmpty(data)) {
            return;
        }
        try {
            String sceneId = new JSONObject(data).optString(VuiConstants.SCENE_ID);
            if (TextUtils.isEmpty(sceneId)) {
                return;
            }
            String packageName = null;
            if (sceneId.contains("-")) {
                packageName = sceneId.split("-")[0];
            } else if (sceneId.contains("_")) {
                packageName = sceneId.split("_")[0];
            }
            if (!TextUtils.isEmpty(packageName) && this.supportAppNameList.contains(packageName)) {
                callSceneEvent(event, data);
            }
        } catch (Exception e) {
        }
    }

    @SpeechAnnotation(event = VuiEvent.SCENE_DM_START)
    public void onDMStart(String event, String data) {
        callSceneEvent("jarvis.dm.start", data);
    }

    @SpeechAnnotation(event = VuiEvent.SCENE_DM_END)
    public void onDMEnd(String event, String data) {
        callSceneEvent("jarvis.dm.end", data);
    }

    @SpeechAnnotation(event = VuiEvent.SCENE_VUI_ENABLE)
    public void onVuiEnable(String event, String data) {
        callSceneEvent("enable.vui.feature", data);
    }

    @SpeechAnnotation(event = VuiEvent.SCENE_VUI_DISABLE)
    public void onVuiDisable(String event, String data) {
        callSceneEvent("disable.vui.feature", data);
    }

    @SpeechAnnotation(event = "scene.rebuild")
    public void onRebuild(String event, String data) {
        callSceneEvent(event, data);
    }

    private void callSceneEvent(String event, String data) {
        try {
            Object[] listenerList = this.mListenerList.collectCallbacks();
            if (listenerList != null) {
                for (Object obj : listenerList) {
                    ((SceneListener) obj).onSceneEvent(event, data);
                }
            }
        } catch (Exception e) {
        }
    }
}
