package com.xiaopeng.speech.protocol.query.system;

import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.annotation.QueryAnnotation;
import com.xiaopeng.speech.protocol.event.query.QuerySystemEvent;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class ControlPanelQuery extends SpeechQuery<IControlPanelCaller> {
    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.BLUETOOTH_VOLUME_SET)
    public int onBlueToothVolumeSet(String event, String data) {
        int type = 0;
        try {
            JSONObject obj = new JSONObject(data);
            type = obj.optInt(VuiConstants.ELEMENT_TYPE, 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ((IControlPanelCaller) this.mQueryCaller).onBlueToothVolumeSet(type);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.BLUETOOTH_VOLUME_SET_VALUE)
    public int onBlueToothVolumeSetValue(String event, String data) {
        int value = 0;
        try {
            JSONObject obj = new JSONObject(data);
            value = obj.optInt(VuiConstants.ELEMENT_VALUE, 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ((IControlPanelCaller) this.mQueryCaller).onBlueToothVolumeSetValue(value);
    }
}
