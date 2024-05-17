package com.xiaopeng.speech.protocol.bean;

import com.xiaopeng.speech.common.util.DontProguardClass;
import org.json.JSONObject;
@DontProguardClass
/* loaded from: classes23.dex */
public class WindowAnimState {
    private int from;
    private int state;

    public WindowAnimState() {
    }

    public WindowAnimState(int from, int state) {
        this.from = from;
        this.state = state;
    }

    public static WindowAnimState fromJson(String data) {
        WindowAnimState windowAnimState = new WindowAnimState();
        try {
            JSONObject jsonObject = new JSONObject(data);
            windowAnimState.from = jsonObject.optInt("from");
            windowAnimState.state = jsonObject.optInt("state");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return windowAnimState;
    }

    public int getFrom() {
        return this.from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
