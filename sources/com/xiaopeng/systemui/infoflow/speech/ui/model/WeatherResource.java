package com.xiaopeng.systemui.infoflow.speech.ui.model;
/* loaded from: classes24.dex */
public class WeatherResource {
    private int weatherCardRes;
    private int weatherRes;

    public WeatherResource(int bgRes, int weatherRes) {
        this.weatherCardRes = bgRes;
        this.weatherRes = weatherRes;
    }

    public int getWeatherCardRes() {
        return this.weatherCardRes;
    }

    public void setWeatherCardRes(int weatherCardRes) {
        this.weatherCardRes = weatherCardRes;
    }

    public int getWeatherRes() {
        return this.weatherRes;
    }

    public void setWeatherRes(int weatherRes) {
        this.weatherRes = weatherRes;
    }
}
