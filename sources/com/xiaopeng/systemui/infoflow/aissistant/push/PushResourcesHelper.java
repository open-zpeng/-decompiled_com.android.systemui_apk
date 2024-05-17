package com.xiaopeng.systemui.infoflow.aissistant.push;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes24.dex */
public class PushResourcesHelper {
    private static final Map<String, int[]> sWeatherBgMap = new HashMap<String, int[]>() { // from class: com.xiaopeng.systemui.infoflow.aissistant.push.PushResourcesHelper.1
        {
            put("晴", new int[]{R.drawable.ic_push_clear, R.drawable.bg_push_clear});
            put("多云", new int[]{R.drawable.ic_push_partly_cloudy, R.drawable.bg_push_partly_cloudy});
            put("阴", new int[]{R.drawable.ic_push_cloudy, R.drawable.bg_push_cloudy});
            put("轻度雾霾", new int[]{R.drawable.ic_push_haze, R.drawable.bg_push_haze});
            put("中度雾霾", new int[]{R.drawable.ic_push_haze, R.drawable.bg_push_haze});
            put("重度雾霾", new int[]{R.drawable.ic_push_haze, R.drawable.bg_push_haze});
            put("小雨", new int[]{R.drawable.ic_push_rain, R.drawable.bg_push_rain});
            put("中雨", new int[]{R.drawable.ic_push_rain, R.drawable.bg_push_rain});
            put("大雨", new int[]{R.drawable.ic_push_rain, R.drawable.bg_push_rain});
            put("暴雨", new int[]{R.drawable.ic_push_rain, R.drawable.bg_push_rain});
            put("小雪", new int[]{R.drawable.ic_push_snow, R.drawable.bg_push_snow});
            put("中雪", new int[]{R.drawable.ic_push_snow, R.drawable.bg_push_snow});
            put("大雪", new int[]{R.drawable.ic_push_snow, R.drawable.bg_push_snow});
            put("暴雪", new int[]{R.drawable.ic_push_snow, R.drawable.bg_push_snow});
            put("雾", new int[]{R.drawable.ic_push_fog, R.drawable.bg_push_fog});
            put("浮尘", new int[]{R.drawable.ic_push_dust, R.drawable.bg_push_dust});
            put("沙尘", new int[]{R.drawable.ic_push_dust, R.drawable.bg_push_dust});
            put("大风", new int[]{R.drawable.ic_push_wind, R.drawable.bg_push_wind});
        }
    };
    private static final Map<String, int[]> sSensesImgMap = new HashMap<String, int[]>() { // from class: com.xiaopeng.systemui.infoflow.aissistant.push.PushResourcesHelper.2
        {
            put("路况感知", new int[]{R.drawable.ic_push_senses_road_normal, R.drawable.bg_tv_senses_road_normal});
            put("路况感知1", new int[]{R.drawable.ic_push_senses_road_slight, R.drawable.bg_tv_senses_road_slight});
            put("路况感知2", new int[]{R.drawable.ic_push_senses_road_serious, R.drawable.bg_tv_senses_road_serious});
        }
    };
    private static final Map<String, Integer> sWarningImgMap = new HashMap<String, Integer>() { // from class: com.xiaopeng.systemui.infoflow.aissistant.push.PushResourcesHelper.3
        {
            put("蓝色", Integer.valueOf((int) R.drawable.bg_warning_blue));
            put("黄色", Integer.valueOf((int) R.drawable.bg_warning_yellow));
            put("橙色", Integer.valueOf((int) R.drawable.bg_warning_orange));
            put("红色", Integer.valueOf((int) R.drawable.bg_warning_red));
        }
    };
    private static final Map<String, int[]> sAqImgMap = new HashMap<String, int[]>() { // from class: com.xiaopeng.systemui.infoflow.aissistant.push.PushResourcesHelper.4
        {
            put("1", new int[]{R.drawable.bg_weather_aq_1, R.color.colorTvPushAq_30});
            put("2", new int[]{R.drawable.bg_weather_aq_2, R.color.colorTvPushAq_30});
            put("3", new int[]{R.drawable.bg_weather_aq_3, R.color.colorTvPushAq_30});
        }
    };

    /* loaded from: classes24.dex */
    public interface LoadDrawableCallback {
        void onFinish(Drawable drawable);
    }

    public static int getBgByAq(int aq) {
        if (aq <= 100) {
            return sAqImgMap.get("1")[0];
        }
        return aq <= 200 ? sAqImgMap.get("2")[0] : sAqImgMap.get("3")[0];
    }

    public static int getTextColorByAq(int aq) {
        return R.color.colorTvPushAq_30;
    }

    public static int getBgByWarningType(String warningType) {
        if (sWarningImgMap.get(warningType) == null) {
            return R.drawable.bg_warning_blue;
        }
        int id = sWarningImgMap.get(warningType).intValue();
        return id;
    }

    public static int getIcBySenses(String senses) {
        if (sSensesImgMap.get(senses) == null) {
            return -1;
        }
        int id = sSensesImgMap.get(senses)[0];
        return id;
    }

    public static int getBgBySenses(String senses) {
        if (sSensesImgMap.get(senses) == null) {
            return R.drawable.bg_tv_senses_behavior;
        }
        int id = sSensesImgMap.get(senses)[1];
        return id;
    }

    public static int getIcByWeather(String weather) {
        if (sWeatherBgMap.get(weather) == null) {
            return R.drawable.ic_push_clear;
        }
        int id = sWeatherBgMap.get(weather)[0];
        return id;
    }

    public static int getBgByWeather(String weather) {
        if (sWeatherBgMap.get(weather) == null) {
            return R.drawable.bg_push_clear;
        }
        int id = sWeatherBgMap.get(weather)[1];
        return id;
    }

    public static void getImageFromAssetsFile(final String picPath, final LoadDrawableCallback callback) {
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.aissistant.push.PushResourcesHelper.5
            /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:7:0x0024 -> B:27:0x0034). Please submit an issue!!! */
            @Override // java.lang.Runnable
            public void run() {
                Drawable image = null;
                Resources resources = SystemUIApplication.getContext().getResources();
                AssetManager am = resources.getAssets();
                InputStream is = null;
                try {
                    try {
                        try {
                            is = am.open(picPath);
                            image = Drawable.createFromStream(is, null);
                            is.close();
                            is.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (is != null) {
                                is.close();
                            }
                        }
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    if (image == null) {
                        image = resources.getDrawable(R.drawable.ic_push_default, null);
                    }
                    if (callback != null) {
                        final Drawable finalDrawable = image;
                        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.aissistant.push.PushResourcesHelper.5.1
                            @Override // java.lang.Runnable
                            public void run() {
                                callback.onFinish(finalDrawable);
                            }
                        });
                    }
                } catch (Throwable th) {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e3) {
                            e3.printStackTrace();
                        }
                    }
                    throw th;
                }
            }
        });
    }

    public static void loadDrawable(final String picPath, final LoadDrawableCallback callback) {
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.aissistant.push.PushResourcesHelper.6
            @Override // java.lang.Runnable
            public void run() {
                InputStream inputStream = null;
                try {
                    try {
                        try {
                            File externalStorageDirectory = Environment.getExternalStorageDirectory();
                            File imagePath = new File(externalStorageDirectory, "aiassistant/xp_update/res3/" + picPath);
                            drawable = imagePath.exists() ? new BitmapDrawable(SystemUIApplication.getContext().getResources(), imagePath.getAbsolutePath()) : null;
                        } catch (Throwable th) {
                            if (0 != 0) {
                                try {
                                    inputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            throw th;
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        if (0 != 0) {
                            inputStream.close();
                        }
                    }
                    if (0 != 0) {
                        inputStream.close();
                    }
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
                if (callback != null) {
                    final Drawable finalDrawable = drawable;
                    ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.aissistant.push.PushResourcesHelper.6.1
                        @Override // java.lang.Runnable
                        public void run() {
                            callback.onFinish(finalDrawable);
                        }
                    });
                }
            }
        }, null, 0);
    }
}
