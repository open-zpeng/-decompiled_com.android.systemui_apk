package com.xiaopeng.speech.jarvisproto;

import com.xiaopeng.speech.protocol.bean.stats.SceneSwitchStatisticsBean;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class HotWordResult extends JarvisProto {
    public static final String EVENT = "jarvis.hotword.result";
    public String command;
    public String scene;
    public String tts;
    public String word;

    public HotWordResult(String word, String scene, String command, String tts) {
        this.word = word;
        this.scene = scene;
        this.command = command;
        this.tts = tts;
    }

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getJsonData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("word", this.word);
            jsonObject.put(SceneSwitchStatisticsBean.NAME_SCENE, this.scene);
            jsonObject.put("command", this.command);
            jsonObject.put("tts", this.tts);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static HotWordResult fromJson(String data) {
        String word = "";
        String scene = "";
        String command = "";
        String tts = "";
        try {
            JSONObject jsonObject = new JSONObject(data);
            word = jsonObject.optString("word");
            scene = jsonObject.optString(SceneSwitchStatisticsBean.NAME_SCENE);
            command = jsonObject.optString("command");
            tts = jsonObject.optString("tts");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new HotWordResult(word, scene, command, tts);
    }

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getEvent() {
        return EVENT;
    }
}
