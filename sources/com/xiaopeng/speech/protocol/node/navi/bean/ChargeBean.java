package com.xiaopeng.speech.protocol.node.navi.bean;

import com.xiaopeng.speech.common.util.DontProguardClass;
import org.json.JSONException;
import org.json.JSONObject;
@DontProguardClass
/* loaded from: classes23.dex */
public class ChargeBean {
    private int available;
    private String power;
    private int total;

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getAvailable() {
        return this.available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public String getPower() {
        return this.power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public static ChargeBean fromJson(String fromJson) {
        ChargeBean chargeBean = new ChargeBean();
        try {
            JSONObject json = new JSONObject(fromJson);
            if (json.has("total")) {
                chargeBean.total = json.optInt("total");
            }
            if (json.has("available")) {
                chargeBean.available = json.optInt("available");
            }
            if (json.has("power")) {
                chargeBean.power = json.optString("power");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chargeBean;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("total", this.total);
        json.put("available", this.available);
        json.put("power", this.power);
        return json;
    }

    public String toString() {
        return "ChargeBean{total=" + this.total + ", available=" + this.available + ", power=" + this.power + '}';
    }
}
