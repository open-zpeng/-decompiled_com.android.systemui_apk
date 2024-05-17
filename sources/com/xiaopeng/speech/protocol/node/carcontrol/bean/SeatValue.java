package com.xiaopeng.speech.protocol.node.carcontrol.bean;

import android.text.TextUtils;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class SeatValue {
    private int xValue;
    private int yValue;
    private int zValue;

    public static SeatValue fromJson(String data) {
        SeatValue changeValue = new SeatValue();
        try {
            JSONObject jsonObject = new JSONObject(data);
            int xValue = -1;
            int yValue = -1;
            int zValue = -1;
            String xTarget = jsonObject.optString("xtarget");
            String yTarget = jsonObject.optString("vtarget");
            String zTarget = jsonObject.optString("htarget");
            if (!TextUtils.isEmpty(xTarget)) {
                xValue = Integer.parseInt(xTarget);
            }
            if (!TextUtils.isEmpty(yTarget)) {
                yValue = Integer.parseInt(yTarget);
            }
            if (!TextUtils.isEmpty(zTarget)) {
                zValue = Integer.parseInt(zTarget);
            }
            changeValue.xValue = xValue;
            changeValue.yValue = yValue;
            changeValue.zValue = zValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return changeValue;
    }

    public int getyValue() {
        return this.yValue;
    }

    public int getxValue() {
        return this.xValue;
    }

    public int getzValue() {
        return this.zValue;
    }

    public String toString() {
        return "SeatValue{xValue=" + this.xValue + ", yValue=" + this.yValue + ", zValue=" + this.zValue + '}';
    }
}
