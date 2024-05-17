package com.xiaopeng.speech.jarvisproto;

import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class AsrCloundResult extends JarvisProto {
    public static final String EVENT = "jarvis.asr.cloundresult";
    public String filePath;
    public String text;
    public String token;

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getEvent() {
        return EVENT;
    }

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getJsonData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", this.token);
            jsonObject.put("text", this.text);
            jsonObject.put("filePath", this.filePath);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
