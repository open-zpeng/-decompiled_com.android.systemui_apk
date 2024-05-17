package com.xiaopeng.systemui.infoflow.speech.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.systemui.R;
import com.xiaopeng.speech.protocol.bean.weather.WeatherBean;
import com.xiaopeng.systemui.infoflow.speech.ui.adapter.WeatherAdapter;
import com.xiaopeng.systemui.infoflow.speech.ui.model.WeatherFactory;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout;
import java.util.ArrayList;
/* loaded from: classes24.dex */
public class WeatherView extends AlphaOptimizedRelativeLayout {
    private static final String TAG = WeatherView.class.getSimpleName();
    private WeatherAdapter mAdapter;
    private AlphaOptimizedRelativeLayout mBackground;
    private TextView mLocalText;
    private RecyclerView mRecyclerView;
    private TextView mTempMax;
    private TextView mTempMin;
    private TextView mTodayWeather;

    public WeatherView(Context context) {
        super(context);
    }

    public WeatherView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setData(WeatherBean weatherBean) {
        if (weatherBean != null) {
            WeatherBean.WeatherData today = weatherBean.getToday();
            String str = TAG;
            Log.v(str, "today.getConditionDay() = " + today.getConditionDay());
            int resId = WeatherFactory.getCardBgResId(today.getConditionDay());
            this.mBackground.setBackgroundResource(resId);
            setTodayView(weatherBean.getTitle(), today);
            ArrayList<WeatherBean.WeatherData> dataList = new ArrayList<>();
            if (weatherBean.getWeatherDatas() != null) {
                for (int i = 1; i < 4; i++) {
                    dataList.add(weatherBean.getWeatherDatas().get(i));
                }
            }
            this.mAdapter.setNewData(dataList);
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mRecyclerView = (RecyclerView) findViewById(R.id.weather_recycle_view);
        this.mTempMax = (TextView) findViewById(R.id.temp_max_tx);
        this.mTempMin = (TextView) findViewById(R.id.temp_min_tx);
        this.mLocalText = (TextView) findViewById(R.id.location);
        this.mTodayWeather = (TextView) findViewById(R.id.today_weather);
        this.mBackground = (AlphaOptimizedRelativeLayout) findViewById(R.id.background_weather);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), 0, false));
        if (this.mAdapter == null) {
            this.mAdapter = new WeatherAdapter(getContext());
        }
        this.mRecyclerView.setAdapter(this.mAdapter);
    }

    private void setTodayView(String local, WeatherBean.WeatherData today) {
        int min = Math.min(today.getTempDay(), today.getTempNight());
        int max = Math.max(today.getTempDay(), today.getTempNight());
        TextView textView = this.mTempMax;
        textView.setText(max + "");
        TextView textView2 = this.mTempMin;
        textView2.setText(min + "");
        this.mLocalText.setText(local);
        this.mTodayWeather.setText(today.getWeather());
    }
}
