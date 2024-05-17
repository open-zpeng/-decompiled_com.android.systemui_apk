package com.xiaopeng.speech.protocol.bean.base;

import com.xiaopeng.speech.common.util.DontProguardClass;
import org.json.JSONObject;
@DontProguardClass
/* loaded from: classes23.dex */
public class CommandValue {
    private int color;
    private int number;
    private int percent;

    public int getPercent() {
        return this.percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public CommandValue(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.has("color")) {
                String color = jsonObject.getString("color");
                this.color = Integer.valueOf(color).intValue();
            }
            if (jsonObject.has("number")) {
                this.number = jsonObject.getInt("number");
            }
            if (jsonObject.has("percent")) {
                this.percent = jsonObject.getInt("percent");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
