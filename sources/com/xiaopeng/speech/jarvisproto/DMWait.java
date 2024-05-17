package com.xiaopeng.speech.jarvisproto;

import com.android.systemui.globalactions.GlobalActionsDialog;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class DMWait extends JarvisProto {
    public static final String EVENT = "jarvis.dm.wait";
    public static final String STATUS_END = "end";
    public static final String STATUS_END_ASR = "end_asr";
    public static final String STATUS_ENTER = "enter";
    public static final String STATUS_FEED_NLU = "feednlu";
    public static final String STATUS_INVALID_SPEECH = "invalid_speech";
    public static final String STATUS_START_ASR = "start_asr";
    public static final String STATUS_TIME = "timeout";
    public static final String STATUS_TTS_END = "tts_end";
    public static final String STATUS_UPDATE = "update";
    public String expression;
    public String reason;
    public String sessionId;
    public int soundArea;
    public boolean speaking;
    public String tips;

    public DMWait() {
    }

    public DMWait(String reason, String id, String tip, String expression, boolean speaking) {
        this.reason = reason;
        this.tips = tip;
        this.sessionId = id;
        this.expression = expression;
        this.speaking = speaking;
    }

    public DMWait(String reason, String id, String tip, String expression, boolean speaking, int soundArea) {
        this.reason = reason;
        this.tips = tip;
        this.sessionId = id;
        this.expression = expression;
        this.speaking = speaking;
        this.soundArea = soundArea;
    }

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getEvent() {
        return EVENT;
    }

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getJsonData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(GlobalActionsDialog.SYSTEM_DIALOG_REASON_KEY, this.reason);
            jsonObject.put("tips", this.tips);
            jsonObject.put("sessionId", this.sessionId);
            jsonObject.put("expression", this.expression);
            jsonObject.put("speaking", this.speaking);
            jsonObject.put("soundArea", this.soundArea);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static DMWait fromJson(String data) {
        String reason = "normal";
        String sessionId = "";
        String tip = "";
        String expression = "";
        int soundArea = -1;
        boolean speaking = false;
        try {
            JSONObject jsonObject = new JSONObject(data);
            reason = jsonObject.optString(GlobalActionsDialog.SYSTEM_DIALOG_REASON_KEY);
            tip = jsonObject.optString("tips");
            sessionId = jsonObject.optString("sessionId");
            expression = jsonObject.optString("expression");
            speaking = jsonObject.optBoolean("speaking");
            soundArea = jsonObject.optInt("soundArea");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new DMWait(reason, sessionId, tip, expression, speaking, soundArea);
    }
}
