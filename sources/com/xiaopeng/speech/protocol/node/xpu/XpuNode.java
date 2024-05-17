package com.xiaopeng.speech.protocol.node.xpu;

import android.text.TextUtils;
import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.protocol.event.XpuEvent;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class XpuNode extends SpeechNode<IXpuListener> {
    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = XpuEvent.XPU_VOICE_CHANGE_LANE)
    public void laneChange(String event, String data) {
        try {
            JSONObject object = new JSONObject(data);
            if (object.has(VuiConstants.EVENT_VALUE_DIRECTION)) {
                String direction = object.optString(VuiConstants.EVENT_VALUE_DIRECTION);
                if (TextUtils.isEmpty(direction)) {
                    return;
                }
                int dir = Integer.parseInt(direction);
                Object[] listenerList = this.mListenerList.collectCallbacks();
                if (listenerList != null) {
                    for (Object obj : listenerList) {
                        ((IXpuListener) obj).laneChange(dir);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
