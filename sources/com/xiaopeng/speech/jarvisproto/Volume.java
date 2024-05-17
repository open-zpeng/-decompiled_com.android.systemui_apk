package com.xiaopeng.speech.jarvisproto;

import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public abstract class Volume extends JarvisProto {
    public int soundArea;
    public int volume;

    public Volume(int volume) {
        this.volume = volume;
    }

    public Volume(int volume, int soundArea) {
        this.volume = volume;
        this.soundArea = soundArea;
    }

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getJsonData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("volume", this.volume);
            jsonObject.put("soundArea", this.soundArea);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
