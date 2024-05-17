package com.xiaopeng.speech.protocol.node.dialog.bean;

import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class DialogActiveReason {
    public int soundArea;

    public DialogActiveReason(int soundArea) {
        this.soundArea = -1;
        this.soundArea = soundArea;
    }

    public static DialogActiveReason fromJson(String jsonStr) {
        int soundArea = -1;
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            soundArea = jsonObject.optInt("soundArea");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new DialogActiveReason(soundArea);
    }
}
