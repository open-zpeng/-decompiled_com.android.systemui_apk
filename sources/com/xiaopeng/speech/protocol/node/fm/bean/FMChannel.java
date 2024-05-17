package com.xiaopeng.speech.protocol.node.fm.bean;

import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class FMChannel {
    private String mFMName;
    private float mFMRadio;

    public static final FMChannel fromJson(String jsonStr) {
        FMChannel fmChannel = new FMChannel();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            fmChannel.setFMName(jsonObject.optString("name"));
            if (jsonObject.has("fm")) {
                fmChannel.setFMRadio(Float.valueOf(jsonObject.optString("fm")).floatValue());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        return fmChannel;
    }

    public float getFMRadio() {
        return this.mFMRadio;
    }

    public void setFMRadio(float FMRadio) {
        this.mFMRadio = FMRadio;
    }

    public void setFMName(String name) {
        this.mFMName = name;
    }

    public String getFMName() {
        return this.mFMName;
    }

    public String toString() {
        return "FMChannel{FMRadio='" + this.mFMRadio + "'}";
    }
}
