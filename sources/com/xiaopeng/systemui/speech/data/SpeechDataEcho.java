package com.xiaopeng.systemui.speech.data;

import com.xiaopeng.speech.protocol.node.tts.TtsEcho;
/* loaded from: classes24.dex */
public class SpeechDataEcho {
    public static final int TYPE_FAIL = 2;
    public static final int TYPE_LIGHTNING = 3;
    public static final int TYPE_SQUARE = 4;
    public static final int TYPE_SUCCESS = 1;
    public String msgId;
    public int soundArea;
    public String text;
    public long timestamp;
    public int type;

    public SpeechDataEcho(int soundArea, String msgId, String text, int type, long timestamp) {
        this.soundArea = soundArea;
        this.msgId = msgId;
        this.text = text;
        this.type = type;
        this.timestamp = timestamp;
    }

    public SpeechDataEcho(TtsEcho echo) {
        this.soundArea = echo.soundArea;
        this.msgId = echo.msgId;
        this.text = echo.text;
        this.type = echo.type;
        this.timestamp = echo.timestamp;
    }

    public int getSoundArea() {
        return this.soundArea;
    }

    public void setSoundArea(int soundArea) {
        this.soundArea = soundArea;
    }

    public String getMsgId() {
        return this.msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String toString() {
        return "SpeechDataEcho{soundArea=" + this.soundArea + ", msgId='" + this.msgId + "', text='" + this.text + "', type=" + this.type + ", timestamp=" + this.timestamp + '}';
    }
}
