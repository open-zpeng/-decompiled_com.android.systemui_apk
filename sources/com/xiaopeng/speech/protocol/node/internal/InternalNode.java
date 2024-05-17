package com.xiaopeng.speech.protocol.node.internal;

import android.text.TextUtils;
import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.common.SpeechEvent;
import com.xiaopeng.speech.protocol.event.InternalEvent;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class InternalNode extends SpeechNode<InternalListener> {
    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = InternalEvent.DM_OUTPUT)
    public void onDmOutput(String event, String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONObject dm = jsonObject.optJSONObject("dm");
            String dataFrom = dm.optString("dataFrom", "");
            if (dataFrom.equals("native")) {
                String api = dm.optString(SpeechWidget.DATA_SOURCE_API);
                if (TextUtils.isEmpty(api)) {
                    return;
                }
                String str = SpeechEvent.NATIVE_API_SCHEME + api;
            }
            JSONObject commandObj = dm.optJSONObject("command");
            if (commandObj != null) {
                String api2 = commandObj.optString(SpeechWidget.DATA_SOURCE_API);
                if (!TextUtils.isEmpty(api2)) {
                    String str2 = SpeechEvent.COMMAND_SCHEME + api2;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((InternalListener) obj).onDmOutput(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = InternalEvent.INPUT_DM_DATA)
    public void onInputDmData(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((InternalListener) obj).onInputDmData(event, data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = InternalEvent.LOCAL_WAKEUP_RESULT)
    public void onLocalWakeupResult(String event, String data) {
        String type = "";
        String word = "";
        String channel = "";
        try {
            JSONObject jsonObject = new JSONObject(data);
            type = jsonObject.optString(VuiConstants.ELEMENT_TYPE);
            word = jsonObject.optString("word");
            channel = jsonObject.optString("channel");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (int i = 0; i < listenerList.length; i++) {
                ((InternalListener) listenerList[i]).onLocalWakeupResult(type, word);
                ((InternalListener) listenerList[i]).onLocalWakeupResultWithChannel(type, word, channel);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = InternalEvent.RESOURCE_UPDATE_FINISH)
    public void onResourceUpdateFinish(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((InternalListener) obj).onResourceUpdateFinish(event, data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = InternalEvent.REBOOT_COMPLETE)
    public void onRebootComplete(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((InternalListener) obj).onRebootComplete(event, data);
            }
        }
    }
}
