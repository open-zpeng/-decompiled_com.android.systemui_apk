package com.xiaopeng.speech.protocol.bean;

import android.text.TextUtils;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class AdjustValue {
    private boolean isPercent = false;
    private int value;

    public static AdjustValue fromJson(String data) {
        AdjustValue adjustValue = new AdjustValue();
        try {
            JSONObject jsonObject = new JSONObject(data);
            int value = 0;
            String target = jsonObject.optString("target");
            String scale = jsonObject.optString("scale");
            String number = jsonObject.optString("number");
            if (!TextUtils.isEmpty(target)) {
                value = Integer.parseInt(target);
            } else if (!TextUtils.isEmpty(scale)) {
                value = Integer.parseInt(scale);
            } else if (!TextUtils.isEmpty(number)) {
                value = Integer.parseInt(number);
            }
            String percent = jsonObject.optString("percent");
            if (!TextUtils.isEmpty(percent)) {
                adjustValue.isPercent = true;
                value = Integer.parseInt(percent);
            }
            adjustValue.value = value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return adjustValue;
    }

    public boolean isPercent() {
        return this.isPercent;
    }

    public int getValue() {
        return this.value;
    }
}
