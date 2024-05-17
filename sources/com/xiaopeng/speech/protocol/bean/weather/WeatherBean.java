package com.xiaopeng.speech.protocol.bean.weather;

import com.xiaopeng.speech.common.bean.BaseBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class WeatherBean extends BaseBean {
    private static final String DATA_FORMAT = "yyyy-MM-dd";
    private static final String TEMP_SUPREFIX = "℃";
    private List<WeatherData> mWeatherDatas;
    private String searchDay;
    private WeatherData today;

    public static WeatherBean fromJsonObj(JSONObject jsonObject) {
        WeatherData today;
        if (jsonObject == null) {
            return null;
        }
        String widgetName = jsonObject.optString("name");
        if (!widgetName.equals("weather")) {
            return null;
        }
        WeatherBean weatherBean = new WeatherBean();
        WeatherData searchDayWeather = null;
        try {
            JSONObject searchDayObject = jsonObject.optJSONArray("forecastChoose").getJSONObject(0);
            searchDayWeather = parseWeathData(searchDayObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (searchDayWeather != null) {
            weatherBean.setSearchDay(searchDayWeather.getDate());
        }
        weatherBean.setTitle(jsonObject.optString("cityName"));
        JSONArray weathers = jsonObject.optJSONArray("forecast");
        List<WeatherData> weatherDatas = new ArrayList<>();
        String todayFomatStr = getTodaySampleDataFormat();
        for (int i = 0; i < weathers.length(); i++) {
            try {
                JSONObject object = weathers.getJSONObject(i);
                WeatherData weatherData = parseWeathData(object);
                if (weatherData != null && todayFomatStr.equals(weatherData.getDate())) {
                    weatherBean.setToday(weatherData);
                }
                if (weatherData != null) {
                    weatherDatas.add(weatherData);
                }
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
        if (searchDayWeather == null && (today = weatherBean.getToday()) != null) {
            weatherBean.setSearchDay(today.getDate());
        }
        weatherBean.setWeatherDatas(weatherDatas);
        return weatherBean;
    }

    private static String getTodaySampleDataFormat() {
        SimpleDateFormat format = new SimpleDateFormat(DATA_FORMAT);
        return format.format(new Date());
    }

    private static WeatherData parseWeathData(JSONObject beanObject) {
        if (beanObject == null) {
            return null;
        }
        WeatherData weatherData = new WeatherData();
        weatherData.setDate(beanObject.optString("predictDate"));
        weatherData.setWeather(beanObject.optString("conditionDayNight"));
        weatherData.setConditionDay(beanObject.optString("conditionDay"));
        weatherData.setConditionNight(beanObject.optString("conditionNight"));
        int tempDay = beanObject.optInt("tempDay");
        int tempNight = beanObject.optInt("tempNight");
        weatherData.setTempDay(tempDay);
        weatherData.setTempNight(tempNight);
        int min = Math.min(tempDay, tempNight);
        int max = Math.max(tempDay, tempNight);
        weatherData.setTemperature(min + "~" + max + TEMP_SUPREFIX);
        weatherData.setWind(beanObject.optString("windDirDay"));
        return weatherData;
    }

    public String getSearchDay() {
        return this.searchDay;
    }

    public void setSearchDay(String searchDay) {
        this.searchDay = searchDay;
    }

    public WeatherData getToday() {
        return this.today;
    }

    public void setToday(WeatherData today) {
        this.today = today;
    }

    public List<WeatherData> getWeatherDatas() {
        return this.mWeatherDatas;
    }

    public void setWeatherDatas(List<WeatherData> mWeatherDatas) {
        this.mWeatherDatas = mWeatherDatas;
    }

    /* loaded from: classes23.dex */
    public static class WeatherData {
        private String conditionDay;
        private String conditionNight;
        private String date;
        private int tempDay;
        private int tempNight;
        private String temperature;
        private String weather;
        private String wind;

        public String getConditionDay() {
            return this.conditionDay;
        }

        public void setConditionDay(String conditionDay) {
            this.conditionDay = conditionDay;
        }

        public String getConditionNight() {
            return this.conditionNight;
        }

        public void setConditionNight(String conditionNight) {
            this.conditionNight = conditionNight;
        }

        public int getTempDay() {
            return this.tempDay;
        }

        public void setTempDay(int tempDay) {
            this.tempDay = tempDay;
        }

        public int getTempNight() {
            return this.tempNight;
        }

        public void setTempNight(int tempNight) {
            this.tempNight = tempNight;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public void setWeather(String weather) {
            this.weather = weather;
        }

        public void setTemperature(String temperature) {
            this.temperature = temperature;
        }

        public void setWind(String wind) {
            this.wind = wind;
        }

        public String getDate() {
            return this.date;
        }

        public String getWeather() {
            return this.weather;
        }

        public String getTemperature() {
            return this.temperature;
        }

        public String getWind() {
            return this.wind;
        }
    }
}
