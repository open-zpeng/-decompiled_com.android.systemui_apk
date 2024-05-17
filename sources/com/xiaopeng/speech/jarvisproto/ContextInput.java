package com.xiaopeng.speech.jarvisproto;

import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class ContextInput extends JarvisProto {
    public static final String EVENT = "jarvis.context.input";
    public boolean invalid;
    public boolean isEof;
    public boolean isInterrupted;
    public String pinyin = "";
    public int soundArea;
    public String text;

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getEvent() {
        return EVENT;
    }

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getJsonData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("text", this.text);
            jsonObject.put("isEof", this.isEof);
            jsonObject.put("isInterrupted", this.isInterrupted);
            jsonObject.put("pinyin", this.pinyin);
            jsonObject.put("soundArea", this.soundArea);
            jsonObject.put("invalid", this.invalid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
