package com.xiaopeng.speech.protocol.bean;

import org.json.JSONObject;
/* loaded from: classes23.dex */
public class CardValue {
    private double acDriverTemp;
    private int acSeatHeatingLv;
    private int acSeatVentilateLv;
    private int acWindLv;
    private int atmosphereBrightness;
    private int atmosphereColor;
    private int icmBrightness;
    private int screenBrightness;

    public static CardValue fromJsonForAcTemp(String data) {
        CardValue changeValue = new CardValue();
        try {
            JSONObject jsonObject = new JSONObject(data);
            changeValue.acDriverTemp = jsonObject.optDouble("current");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return changeValue;
    }

    public static CardValue fromJsonForAcWindLv(String data) {
        CardValue changeValue = new CardValue();
        try {
            JSONObject jsonObject = new JSONObject(data);
            changeValue.acWindLv = jsonObject.optInt("current");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return changeValue;
    }

    public static CardValue fromJsonForAtmosphereBrightness(String data) {
        CardValue changeValue = new CardValue();
        try {
            JSONObject jsonObject = new JSONObject(data);
            changeValue.atmosphereBrightness = jsonObject.optInt("current");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return changeValue;
    }

    public static CardValue fromJsonForAtmosphereColor(String data) {
        CardValue changeValue = new CardValue();
        try {
            JSONObject jsonObject = new JSONObject(data);
            changeValue.atmosphereColor = jsonObject.optInt("current");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return changeValue;
    }

    public static CardValue fromJsonForAcSeatHeating(String data) {
        CardValue changeValue = new CardValue();
        try {
            JSONObject jsonObject = new JSONObject(data);
            changeValue.acSeatHeatingLv = jsonObject.optInt("current");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return changeValue;
    }

    public static CardValue fromJsonForAcSeatVentilate(String data) {
        CardValue changeValue = new CardValue();
        try {
            JSONObject jsonObject = new JSONObject(data);
            changeValue.acSeatVentilateLv = jsonObject.optInt("current");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return changeValue;
    }

    public static CardValue fromJsonForScreenBrightness(String data) {
        CardValue changeValue = new CardValue();
        try {
            JSONObject jsonObject = new JSONObject(data);
            changeValue.screenBrightness = jsonObject.optInt("current");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return changeValue;
    }

    public static CardValue fromJsonForIcmBrightness(String data) {
        CardValue changeValue = new CardValue();
        try {
            JSONObject jsonObject = new JSONObject(data);
            changeValue.icmBrightness = jsonObject.optInt("current");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return changeValue;
    }

    public double getAcDriverTemp() {
        return this.acDriverTemp;
    }

    public int getAcWindLv() {
        return this.acWindLv;
    }

    public int getAtmosphereBrightness() {
        return this.atmosphereBrightness;
    }

    public int getAtmosphereColor() {
        return this.atmosphereColor;
    }

    public int getAcSeatHeatingLv() {
        return this.acSeatHeatingLv;
    }

    public int getAcSeatVentilateLv() {
        return this.acSeatVentilateLv;
    }

    public int getScreenBrightness() {
        return this.screenBrightness;
    }

    public int getIcmBrightness() {
        return this.icmBrightness;
    }

    public String toString() {
        return "CardValue{acWindLv='" + this.acDriverTemp + "', acWindLv=" + this.acWindLv + ", atmosphereBrightness=" + this.atmosphereBrightness + ", atmosphereColor=" + this.atmosphereColor + ", acSeatHeatingLv=" + this.acSeatHeatingLv + ", acSeatVentilateLv=" + this.acSeatVentilateLv + ", screenBrightness=" + this.screenBrightness + ", icmBrightness=" + this.icmBrightness + '}';
    }
}
