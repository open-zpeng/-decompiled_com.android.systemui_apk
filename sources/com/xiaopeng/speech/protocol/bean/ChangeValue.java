package com.xiaopeng.speech.protocol.bean;

import android.text.TextUtils;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class ChangeValue {
    private boolean isPercent = false;
    private boolean isScale = false;
    private int value;

    public static ChangeValue fromJson(String data) {
        ChangeValue changeValue = new ChangeValue();
        try {
            JSONObject jsonObject = new JSONObject(data);
            int value = 0;
            String target = jsonObject.optString("target");
            String scale = jsonObject.optString("scale");
            if (!TextUtils.isEmpty(target)) {
                changeValue.isScale = false;
                value = Integer.parseInt(target);
            } else if (!TextUtils.isEmpty(scale)) {
                changeValue.isScale = true;
                value = Integer.parseInt(scale);
            }
            String percent = jsonObject.optString("percent");
            if (!TextUtils.isEmpty(percent)) {
                changeValue.isPercent = true;
                value = Integer.parseInt(percent);
            }
            changeValue.value = value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return changeValue;
    }

    public boolean isPercent() {
        return this.isPercent;
    }

    public boolean isScale() {
        return this.isScale;
    }

    public int getValue() {
        return this.value;
    }
}
