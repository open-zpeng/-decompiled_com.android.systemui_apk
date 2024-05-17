package com.xiaopeng.speech.jarvisproto;

import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class AsrEvent extends JarvisProto {
    public static final String EVENT = "jarvis.asr.event";
    public static final int STATE_ASR_BEGIN = 1;
    public static final int STATE_ASR_END = 2;
    public static final int STATE_ASR_TEXT = 3;
    public static final int STATE_START_LISTEN = 0;
    public int mEvent;
    public int soundArea;

    public AsrEvent() {
        this.mEvent = -1;
        this.soundArea = -1;
    }

    public AsrEvent(int event) {
        this.mEvent = -1;
        this.soundArea = -1;
        this.mEvent = event;
    }

    public AsrEvent(int event, int soundArea) {
        this.mEvent = -1;
        this.soundArea = -1;
        this.mEvent = event;
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
            jsonObject.put("event", this.mEvent);
            jsonObject.put("soundArea", this.soundArea);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static AsrEvent fromJson(String data) {
        int event = -1;
        int soundArea = -1;
        try {
            JSONObject jsonObject = new JSONObject(data);
            event = jsonObject.optInt("event");
            soundArea = jsonObject.optInt("soundArea");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new AsrEvent(event, soundArea);
    }
}
