package com.xiaopeng.speech.protocol.node.tts;

import android.text.TextUtils;
import com.xiaopeng.speech.jarvisproto.JarvisProto;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class TtsEcho extends JarvisProto {
    public static final String EVENT_TTS_ECHO = "jarvis.tts.echo";
    public static final int TYPE_FAIL = 2;
    public static final int TYPE_LIGHTNING = 3;
    public static final int TYPE_SQUARE = 4;
    public static final int TYPE_SUCCESS = 1;
    public String msgId;
    public int soundArea;
    public String text;
    public long timestamp;
    public int type;

    public TtsEcho(int soundArea) {
        this(soundArea, null, null, 0, 0L);
    }

    public TtsEcho(int soundArea, String msgId, String text, int type, long timestamp) {
        this.soundArea = soundArea;
        this.msgId = msgId;
        this.text = text;
        this.type = type;
        this.timestamp = timestamp;
    }

    public TtsEcho(JSONObject object) {
        if (object != null) {
            this.soundArea = object.optInt("soundArea");
            this.msgId = object.optString("msgId");
            this.text = object.optString("text");
            this.type = object.optInt(VuiConstants.ELEMENT_TYPE);
            this.timestamp = object.optLong(VuiConstants.TIMESTAMP);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof TtsEcho) {
            TtsEcho echo = (TtsEcho) o;
            return this.soundArea == echo.soundArea && this.type == echo.type && this.timestamp == echo.timestamp && Objects.equals(this.msgId, echo.msgId) && Objects.equals(this.text, echo.text);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.soundArea), this.msgId, this.text, Integer.valueOf(this.type), Long.valueOf(this.timestamp));
    }

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getEvent() {
        return EVENT_TTS_ECHO;
    }

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getJsonData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("soundArea", this.soundArea);
            jsonObject.put("msgId", this.msgId);
            jsonObject.put("text", this.text);
            jsonObject.put(VuiConstants.ELEMENT_TYPE, this.type);
            jsonObject.put(VuiConstants.TIMESTAMP, this.timestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static boolean isEquals(TtsEcho first, TtsEcho second) {
        if (first == null) {
            return second == null;
        }
        return first.equals(second);
    }

    public static TtsEcho fromJson(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }
        int soundArea = -1;
        String msgId = "";
        String text = "";
        int type = 0;
        long timestamp = 0;
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            soundArea = jsonObject.optInt("soundArea");
            msgId = jsonObject.optString("msgId");
            text = jsonObject.optString("text");
            type = jsonObject.optInt(VuiConstants.ELEMENT_TYPE);
            timestamp = jsonObject.optLong(VuiConstants.TIMESTAMP);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new TtsEcho(soundArea, msgId, text, type, timestamp);
    }
}
