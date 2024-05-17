package com.xiaopeng.speech.protocol.node.navi.bean;

import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class SelectParkingBean {
    private int num;

    public static SelectParkingBean fromJson(String data) {
        SelectParkingBean selectParkingBean = new SelectParkingBean();
        try {
            JSONObject jsonObject = new JSONObject(data);
            selectParkingBean.num = jsonObject.optInt("num");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return selectParkingBean;
    }

    public int getNum() {
        return this.num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String toString() {
        return "SelectParkingBean{num=" + this.num + '}';
    }
}
