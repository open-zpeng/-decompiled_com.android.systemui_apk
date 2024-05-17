package com.xiaopeng.speech.protocol.node.tts;

import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class TtsEchoValue {
    public boolean positive;
    public int soundArea;
    public String text;

    public TtsEchoValue(String text, int soundArea, boolean positive) {
        this.text = text;
        this.soundArea = soundArea;
        this.positive = positive;
    }

    public static TtsEchoValue fromJson(String jsonStr) {
        String text = "";
        int soundArea = -1;
        boolean positive = false;
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            text = jsonObject.optString("text");
            soundArea = jsonObject.optInt("soundArea");
            positive = jsonObject.optBoolean("positive");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new TtsEchoValue(text, soundArea, positive);
    }
}
