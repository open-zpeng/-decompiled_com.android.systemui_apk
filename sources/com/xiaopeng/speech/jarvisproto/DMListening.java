package com.xiaopeng.speech.jarvisproto;

import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class DMListening extends Volume {
    public static final String EVENT = "jarvis.dm.listening";

    public static final DMListening fromJson(String jsonString) {
        DMListening dmListening = new DMListening(0);
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            dmListening.volume = jsonObject.optInt("volume");
            dmListening.soundArea = jsonObject.optInt("soundArea");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dmListening;
    }

    public DMListening(int volume) {
        super(volume, -1);
    }

    public DMListening(int volume, int soundArea) {
        super(volume, soundArea);
    }

    public DMListening copy(Volume volume) {
        this.volume = volume.volume;
        return this;
    }

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getEvent() {
        return EVENT;
    }
}
