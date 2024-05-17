package com.xiaopeng.speech.protocol.node.dialog.bean;

import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class PrepareReason {
    public boolean isPreparing;

    public PrepareReason(boolean isPreparing) {
        this.isPreparing = isPreparing;
    }

    public static PrepareReason fromJson(String jsonStr) {
        boolean isPreparing = true;
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            isPreparing = jsonObject.optBoolean("isPreparing");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new PrepareReason(isPreparing);
    }
}
