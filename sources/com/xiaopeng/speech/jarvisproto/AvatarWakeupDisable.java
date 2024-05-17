package com.xiaopeng.speech.jarvisproto;

import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class AvatarWakeupDisable extends JarvisProto {
    public static final String EVENT = "jarvis.avatar.wakeup.disable";
    public String data;

    public AvatarWakeupDisable(String data) {
        this.data = data;
    }

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getEvent() {
        return EVENT;
    }

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getJsonData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("event", EVENT);
            jsonObject.put("data", this.data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
