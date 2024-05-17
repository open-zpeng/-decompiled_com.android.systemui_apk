package com.xiaopeng.speech.protocol.bean;

import org.json.JSONObject;
/* loaded from: classes23.dex */
public class SpeechParam {
    private boolean isAnimation;
    private boolean isAppTTS;

    public static SpeechParam fromJson(String data) {
        SpeechParam param = new SpeechParam();
        try {
            JSONObject jsonObject = new JSONObject(data);
            param.isAppTTS = jsonObject.optBoolean("app_tts", true);
            param.isAnimation = jsonObject.optBoolean("animation");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return param;
    }

    public boolean isAppTTS() {
        return this.isAppTTS;
    }

    public boolean isAnimation() {
        return this.isAnimation;
    }
}
