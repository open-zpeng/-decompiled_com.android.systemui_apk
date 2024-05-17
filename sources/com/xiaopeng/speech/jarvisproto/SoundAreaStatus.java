package com.xiaopeng.speech.jarvisproto;

import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class SoundAreaStatus extends JarvisProto {
    public static final String EVENT = "jarvis.sound.area.status";
    public boolean displayStatus;
    public boolean enableStatus;
    public boolean listeningStatus;
    public int soundArea;
    public boolean waitAwakeListeningStatus;

    public SoundAreaStatus(int soundArea, boolean displayStatus) {
        this.soundArea = soundArea;
        this.displayStatus = displayStatus;
    }

    public SoundAreaStatus(int soundArea, boolean displayStatus, boolean enableStatus, boolean listeningStatus, boolean waitAwakeListeningStatus) {
        this.soundArea = soundArea;
        this.displayStatus = displayStatus;
        this.enableStatus = enableStatus;
        this.listeningStatus = listeningStatus;
        this.waitAwakeListeningStatus = waitAwakeListeningStatus;
    }

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getEvent() {
        return EVENT;
    }

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getJsonData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("soundArea", this.soundArea);
            jsonObject.put("displayStatus", this.displayStatus);
            jsonObject.put("enableStatus", this.enableStatus);
            jsonObject.put("listeningStatus", this.listeningStatus);
            jsonObject.put("waitAwakeListeningStatus", this.waitAwakeListeningStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static SoundAreaStatus fromJson(String jsonStr) {
        boolean displayStatus = true;
        boolean enableStatus = false;
        boolean listeningStatus = false;
        boolean waitAwakeListeningStatus = false;
        int soundArea = -1;
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            displayStatus = jsonObject.optBoolean("displayStatus", true);
            enableStatus = jsonObject.optBoolean("enableStatus", false);
            listeningStatus = jsonObject.optBoolean("listeningStatus", false);
            waitAwakeListeningStatus = jsonObject.optBoolean("waitAwakeListeningStatus", false);
            soundArea = jsonObject.optInt("soundArea");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new SoundAreaStatus(soundArea, displayStatus, enableStatus, listeningStatus, waitAwakeListeningStatus);
    }
}
