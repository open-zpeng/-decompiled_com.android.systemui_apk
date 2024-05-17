package com.xiaopeng.speech.protocol.node.navi.bean;

import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class SelectRouteBean {
    private int num;

    public static SelectRouteBean fromJson(String data) {
        SelectRouteBean selectRouteBean = new SelectRouteBean();
        try {
            JSONObject jsonObject = new JSONObject(data);
            selectRouteBean.num = jsonObject.optInt("num");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return selectRouteBean;
    }

    public int getNum() {
        return this.num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String toString() {
        return "selectRouteBean{num=" + this.num + '}';
    }
}
