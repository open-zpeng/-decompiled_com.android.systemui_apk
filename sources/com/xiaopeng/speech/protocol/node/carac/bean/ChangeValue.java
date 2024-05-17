package com.xiaopeng.speech.protocol.node.carac.bean;

import android.text.TextUtils;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class ChangeValue {
    private int decimal;
    private int value;

    public static ChangeValue fromJson(String data) {
        int value;
        ChangeValue changeValue = new ChangeValue();
        try {
            JSONObject jsonObject = new JSONObject(data);
            String number = jsonObject.optString("number");
            String target = jsonObject.optString("target");
            String scale = jsonObject.optString("scale");
            String decimal = jsonObject.optString("d");
            if (!TextUtils.isEmpty(target)) {
                value = Integer.parseInt(target);
            } else if (!TextUtils.isEmpty(scale)) {
                value = Integer.parseInt(scale);
            } else if (!TextUtils.isEmpty(number)) {
                value = Integer.parseInt(number);
            } else {
                value = 1;
            }
            if (!TextUtils.isEmpty(decimal)) {
                changeValue.decimal = Integer.parseInt(decimal);
            }
            changeValue.value = value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return changeValue;
    }

    public int getValue() {
        return this.value;
    }

    public ChangeValue setValue(int v) {
        this.value = v;
        return this;
    }

    public int getDecimal() {
        return this.decimal;
    }
}
