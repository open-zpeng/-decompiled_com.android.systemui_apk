package com.xiaopeng.speech.protocol.node.carcontrol.bean;

import org.json.JSONObject;
/* loaded from: classes23.dex */
public class SeatResumeValue {
    public boolean fromDriveDoor;

    public static SeatResumeValue fromJson(String data) {
        SeatResumeValue changeValue = new SeatResumeValue();
        try {
            JSONObject jsonObject = new JSONObject(data);
            changeValue.fromDriveDoor = jsonObject.optBoolean("fromDriveDoor");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return changeValue;
    }
}
