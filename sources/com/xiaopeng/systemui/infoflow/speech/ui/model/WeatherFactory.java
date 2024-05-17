package com.xiaopeng.systemui.infoflow.speech.ui.model;

import android.util.SparseArray;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;
import java.util.HashMap;
/* loaded from: classes24.dex */
public class WeatherFactory {
    public static final int TYPE_CLOUDY = 6;
    public static final int TYPE_FOG = 3;
    public static final int TYPE_HAIL = 8;
    public static final int TYPE_HAZE = 11;
    public static final int TYPE_OVERCAST = 2;
    public static final int TYPE_PM = 7;
    public static final int TYPE_RAIN = 5;
    public static final int TYPE_SNOW = 4;
    public static final int TYPE_SUNNY = 1;
    public static final int TYPE_THUNDER = 10;
    public static final int TYPE_WIND = 9;
    private static HashMap<String, Integer> sMap;
    private static SparseArray<WeatherResource> sWeatherList = new SparseArray<>();

    static {
        sWeatherList.put(1, new WeatherResource(R.mipmap.bg_sunny, R.drawable.weather_sunny));
        sWeatherList.put(2, new WeatherResource(R.mipmap.bg_overcast, R.drawable.weather_overcast));
        sWeatherList.put(3, new WeatherResource(R.mipmap.bg_fog, R.drawable.weather_fog));
        sWeatherList.put(4, new WeatherResource(R.mipmap.bg_snow, R.drawable.weather_snow));
        sWeatherList.put(5, new WeatherResource(R.mipmap.bg_rain, R.drawable.weather_rain));
        sWeatherList.put(6, new WeatherResource(R.mipmap.bg_cloudy, R.drawable.weather_cloudy));
        sWeatherList.put(7, new WeatherResource(R.mipmap.bg_pm, R.drawable.weather_pm));
        sWeatherList.put(8, new WeatherResource(R.mipmap.bg_hail, R.drawable.weather_hail));
        sWeatherList.put(9, new WeatherResource(R.mipmap.bg_wind, R.drawable.weather_wind));
        sWeatherList.put(10, new WeatherResource(R.mipmap.bg_thunder, R.drawable.weather_thunder));
        sWeatherList.put(11, new WeatherResource(R.mipmap.bg_haze, R.drawable.weather_haze));
        sMap = new HashMap<>();
        sMap.put(getWeatherType(R.string.fine), 1);
        sMap.put(getWeatherType(R.string.sunny_day), 1);
        sMap.put(getWeatherType(R.string.most_sunny), 1);
        sMap.put(getWeatherType(R.string.overcast), 2);
        sMap.put(getWeatherType(R.string.shade), 2);
        sMap.put(getWeatherType(R.string.fog), 3);
        sMap.put(getWeatherType(R.string.freezing_fog), 3);
        sMap.put(getWeatherType(R.string.snow), 4);
        sMap.put(getWeatherType(R.string.snow_shower), 4);
        sMap.put(getWeatherType(R.string.light_snow), 4);
        sMap.put(getWeatherType(R.string.moderate_snow), 4);
        sMap.put(getWeatherType(R.string.heavy_snow), 4);
        sMap.put(getWeatherType(R.string.blizzard), 4);
        sMap.put(getWeatherType(R.string.light_to_moderate_snow), 4);
        sMap.put(getWeatherType(R.string.moderate_to_heavy_snow), 4);
        sMap.put(getWeatherType(R.string.heavy_to_blizzard), 4);
        sMap.put(getWeatherType(R.string.small_to_moderate_snow), 4);
        sMap.put(getWeatherType(R.string.flurry), 4);
        sMap.put(getWeatherType(R.string.rain), 5);
        sMap.put(getWeatherType(R.string.shower_thunder_shower), 5);
        sMap.put(getWeatherType(R.string.thunderstorm_with_hail), 5);
        sMap.put(getWeatherType(R.string.sleet), 5);
        sMap.put(getWeatherType(R.string.light_rain), 5);
        sMap.put(getWeatherType(R.string.moderate_rain), 5);
        sMap.put(getWeatherType(R.string.heavy_rain), 5);
        sMap.put(getWeatherType(R.string.rainstorm), 5);
        sMap.put(getWeatherType(R.string.heavy_rainstorm), 5);
        sMap.put(getWeatherType(R.string.torrential_rain), 5);
        sMap.put(getWeatherType(R.string.freezing_and_sandstorm_rain), 5);
        sMap.put(getWeatherType(R.string.light_to_moderate_rain), 5);
        sMap.put(getWeatherType(R.string.moderate_rain_to_heavy_rain), 5);
        sMap.put(getWeatherType(R.string.heavy_to_rainstorm_rain), 5);
        sMap.put(getWeatherType(R.string.rainstorm_to_heavy_rainstorm), 5);
        sMap.put(getWeatherType(R.string.downpour_to_heavy_downpour), 5);
        sMap.put(getWeatherType(R.string.moderate_to_heavy_rain), 5);
        sMap.put(getWeatherType(R.string.freezing_rain), 5);
        sMap.put(getWeatherType(R.string.heavy_to_rainstorm), 5);
        sMap.put(getWeatherType(R.string.small_to_moderate_rain), 5);
        sMap.put(getWeatherType(R.string.small_showers), 5);
        sMap.put(getWeatherType(R.string.isolate_showers), 5);
        sMap.put(getWeatherType(R.string.strong_shower), 5);
        sMap.put(getWeatherType(R.string.shower), 5);
        sMap.put(getWeatherType(R.string.thunder_shower), 5);
        sMap.put(getWeatherType(R.string.less_cloud), 6);
        sMap.put(getWeatherType(R.string.cloudy), 6);
        sMap.put(getWeatherType(R.string.particulate_matter), 7);
        sMap.put(getWeatherType(R.string.strong_floating_dust), 7);
        sMap.put(getWeatherType(R.string.sand_storm), 7);
        sMap.put(getWeatherType(R.string.dust_wind), 7);
        sMap.put(getWeatherType(R.string.strong_sandstorm), 7);
        sMap.put(getWeatherType(R.string.blowing_sand), 7);
        sMap.put(getWeatherType(R.string.floating_dust), 7);
        sMap.put(getWeatherType(R.string.hail), 8);
        sMap.put(getWeatherType(R.string.ice_particles), 8);
        sMap.put(getWeatherType(R.string.ice_needle), 8);
        sMap.put(getWeatherType(R.string.wind), 9);
        sMap.put(getWeatherType(R.string.thunder), 10);
        sMap.put(getWeatherType(R.string.thunderstorm), 10);
        sMap.put(getWeatherType(R.string.thunder_and_lightning), 10);
        sMap.put(getWeatherType(R.string.haze), 11);
    }

    static WeatherResource getResId(int type) {
        return sWeatherList.get(type);
    }

    public static int getCardBgResId(String weather) {
        int type = sMap.get(weather) == null ? 1 : sMap.get(weather).intValue();
        if (getResId(type) == null) {
            return R.mipmap.bg_sunny;
        }
        int id = getResId(type).getWeatherCardRes();
        return id;
    }

    public static int getImgByWeather(String weather) {
        int type = sMap.get(weather) == null ? 1 : sMap.get(weather).intValue();
        if (getResId(type) == null) {
            return R.drawable.weather_sunny;
        }
        int id = getResId(type).getWeatherRes();
        return id;
    }

    public static String getWeatherType(int stringId) {
        return SystemUIApplication.getContext().getString(stringId);
    }
}
