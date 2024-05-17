package com.xiaopeng.systemui.infoflow.speech.ui.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.speech.protocol.bean.weather.WeatherBean;
import com.xiaopeng.systemui.infoflow.speech.ui.model.WeatherFactory;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout;
import com.xiaopeng.systemui.infoflow.util.TimeUtils;
/* loaded from: classes24.dex */
public class WeatherAdapter extends BaseRecyclerAdapter<WeatherBean.WeatherData> {
    public WeatherAdapter(Context context) {
        super(context);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.adapter.BaseRecyclerAdapter
    public void bindData(BaseRecyclerAdapter<WeatherBean.WeatherData>.BaseViewHolder holder, WeatherBean.WeatherData weatherData, int position) {
        TextView itemDate = (TextView) holder.getView(R.id.item_date);
        String date = getLegalDate(weatherData.getDate());
        itemDate.setText(date);
        TextView itemWeatherTemp = (TextView) holder.getView(R.id.item_weather_temp);
        TextView itemWeatherTempMax = (TextView) holder.getView(R.id.item_weather_temp_max);
        TextView itemWeatherTempMin = (TextView) holder.getView(R.id.item_weather_temp_min);
        if (itemWeatherTemp != null) {
            bindTemp(weatherData, itemWeatherTemp);
        } else {
            bindTemp(weatherData, itemWeatherTempMax, itemWeatherTempMin);
        }
        ImageView itemWeatherIcon = (ImageView) holder.getView(R.id.item_weather_icon);
        itemWeatherIcon.setImageResource(WeatherFactory.getImgByWeather(weatherData.getConditionDay()));
        AlphaOptimizedRelativeLayout layout = (AlphaOptimizedRelativeLayout) holder.getView(R.id.weather_item_layout);
        if (layout != null) {
            if (position == 0) {
                layout.setBackgroundResource(R.drawable.bg_weather_item_left);
            } else if (position == getData().size() - 1) {
                layout.setBackgroundResource(R.drawable.bg_weather_item_right);
            } else {
                layout.setBackgroundResource(R.drawable.bg_weather_item_center);
            }
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.adapter.BaseRecyclerAdapter
    public int getItemLayoutId() {
        return R.layout.item_weather;
    }

    private void bindTemp(WeatherBean.WeatherData weatherData, TextView itemWeatherTemp) {
        int min = Math.min(weatherData.getTempDay(), weatherData.getTempNight());
        int max = Math.max(weatherData.getTempDay(), weatherData.getTempNight());
        itemWeatherTemp.setText(min + " / " + max + " ℃");
    }

    private void bindTemp(WeatherBean.WeatherData weatherData, TextView itemWeatherTempMax, TextView itemWeatherTempMin) {
        int min = Math.min(weatherData.getTempDay(), weatherData.getTempNight());
        int max = Math.max(weatherData.getTempDay(), weatherData.getTempNight());
        itemWeatherTempMax.setText("" + max);
        itemWeatherTempMin.setText("" + min + " ℃");
    }

    private String getLegalDate(String date) {
        long diff = TimeUtils.dateCompare(date);
        if (diff == 1) {
            String timestamp = this.mContext.getResources().getString(R.string.tomorrow);
            return timestamp;
        } else if (diff <= 1 || diff >= 7) {
            return null;
        } else {
            String timestamp2 = TimeUtils.getWeeks(this.mContext.getResources(), date);
            return timestamp2;
        }
    }
}
