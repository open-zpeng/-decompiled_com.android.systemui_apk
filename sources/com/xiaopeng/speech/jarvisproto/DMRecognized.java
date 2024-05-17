package com.xiaopeng.speech.jarvisproto;

import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class DMRecognized extends JarvisProto {
    public static final String EVENT = "jarvis.dm.recognized";
    public int errorId = 0;

    public static final DMRecognized fromJson(String jsonString) {
        DMRecognized dmRecognized = new DMRecognized();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            dmRecognized.errorId = jsonObject.optInt("errorId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dmRecognized;
    }

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getEvent() {
        return EVENT;
    }

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getJsonData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("errorId", this.errorId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
