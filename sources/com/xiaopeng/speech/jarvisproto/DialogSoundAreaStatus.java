package com.xiaopeng.speech.jarvisproto;

import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class DialogSoundAreaStatus extends JarvisProto {
    public static final String EVENT = "jarvis.dialog.sound.area.status";
    public boolean listeningStatus;
    public boolean recognizingStatus;
    public int soundArea;
    public boolean ttsPlayingStatus;
    public boolean underStandingStatus;

    public DialogSoundAreaStatus(int soundArea, boolean listeningStatus, boolean recognizingStatus, boolean underStandingStatus, boolean ttsPlayingStatus) {
        this.soundArea = soundArea;
        this.listeningStatus = listeningStatus;
        this.recognizingStatus = recognizingStatus;
        this.underStandingStatus = underStandingStatus;
        this.ttsPlayingStatus = ttsPlayingStatus;
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
            jsonObject.put("listeningStatus", this.listeningStatus);
            jsonObject.put("recognizingStatus", this.recognizingStatus);
            jsonObject.put("underStandingStatus", this.underStandingStatus);
            jsonObject.put("ttsPlayingStatus", this.ttsPlayingStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static DialogSoundAreaStatus fromJson(String jsonStr) {
        boolean listeningStatus = false;
        boolean recognizingStatus = false;
        boolean underStandingStatus = false;
        boolean ttsPlayingStatus = false;
        int soundArea = -1;
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            listeningStatus = jsonObject.optBoolean("listeningStatus", false);
            recognizingStatus = jsonObject.optBoolean("recognizingStatus", false);
            underStandingStatus = jsonObject.optBoolean("underStandingStatus", false);
            ttsPlayingStatus = jsonObject.optBoolean("ttsPlayingStatus", false);
            soundArea = jsonObject.optInt("soundArea");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new DialogSoundAreaStatus(soundArea, listeningStatus, recognizingStatus, underStandingStatus, ttsPlayingStatus);
    }

    public boolean reset() {
        if (this.listeningStatus || this.recognizingStatus || this.underStandingStatus || this.ttsPlayingStatus) {
            this.listeningStatus = false;
            this.recognizingStatus = false;
            this.underStandingStatus = false;
            this.ttsPlayingStatus = false;
            return true;
        }
        return false;
    }
}
