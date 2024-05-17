package com.xiaopeng.speech.overall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class SpeechResult {
    public static final int FLOAT_ARRAY_TYPE = 2;
    public static final int INT_ARRAY_TYPE = 1;
    private int classType = 0;
    private String event;
    private Object result;

    public SpeechResult(String event, Object result) {
        this.event = event;
        this.result = initValue(result);
    }

    public String toString() {
        JSONObject json = new JSONObject();
        try {
            json.put("event", this.event);
            json.put("result", this.result);
            if (this.classType > 0) {
                json.put("classType", this.classType);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    private Object initValue(Object value) {
        if (value == null) {
            return value;
        }
        try {
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (value instanceof int[]) {
            this.classType = 1;
            return new JSONArray(value);
        }
        if (value instanceof float[]) {
            this.classType = 2;
            return new JSONArray(value);
        }
        return value;
    }
}
