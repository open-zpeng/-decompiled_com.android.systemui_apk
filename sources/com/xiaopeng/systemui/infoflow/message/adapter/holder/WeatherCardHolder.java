package com.xiaopeng.systemui.infoflow.message.adapter.holder;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.IWeatherCardView;
import com.xiaopeng.systemui.infoflow.aissistant.push.PushResourcesHelper;
import com.xiaopeng.systemui.infoflow.message.presenter.IPushCardPresenter;
import com.xiaopeng.systemui.infoflow.message.presenter.PushBean;
import com.xiaopeng.systemui.infoflow.message.presenter.PushCardPresenter;
import com.xiaopeng.systemui.infoflow.message.presenter.WeatherBean;
/* loaded from: classes24.dex */
public class WeatherCardHolder extends BaseCardHolder implements IWeatherCardView {
    private static final String TAG = "WeatherCardHolder";
    public static final String WEATHER_CARD_KEY = "com.xiaopeng.aiassistant.weather_card_entry_key";
    private View mBtnNotTip7Day;
    private View mBtnNotTipAllDay;
    private View mBtnNotTipClose;
    private ViewGroup mBtnNotTipDown;
    private ViewGroup mBtnNotTipUp;
    private ImageView mIvFutrueOneHour;
    private ImageView mIvFutrueTomorrow;
    private ImageView mIvFutrueTwoHour;
    private ImageView mIvWeatherType;
    private View mLyFutrueOneHour;
    private View mLyFutrueTomorrow;
    private View mLyFutrueTwoHour;
    private ViewGroup mLyNotTip;
    private IPushCardPresenter mPushCardPresenter;
    private TextView mTvAq;
    private TextView mTvStreet;
    private TextView mTvTemperature;
    private TextView mTvWarning1;
    private TextView mTvWarning2;
    private TextView mTvWeatherType;

    public WeatherCardHolder(View itemView) {
        super(itemView);
        this.mIvWeatherType = (ImageView) itemView.findViewById(R.id.iv_weather_type);
        this.mTvWeatherType = (TextView) itemView.findViewById(R.id.tv_weather_type);
        this.mTvTemperature = (TextView) itemView.findViewById(R.id.tv_temperature);
        this.mTvStreet = (TextView) itemView.findViewById(R.id.tv_street);
        this.mTvWarning1 = (TextView) itemView.findViewById(R.id.tv_warning);
        this.mTvWarning2 = (TextView) itemView.findViewById(R.id.tv_warning_two);
        this.mTvAq = (TextView) itemView.findViewById(R.id.tv_aq);
        this.mLyFutrueOneHour = itemView.findViewById(R.id.ly_futrue_one_hour);
        this.mLyFutrueTwoHour = itemView.findViewById(R.id.ly_futrue_two_hour);
        this.mLyFutrueTomorrow = itemView.findViewById(R.id.ly_futrue_tomorrow);
        this.mIvFutrueOneHour = (ImageView) itemView.findViewById(R.id.iv_futrue_one_hour);
        this.mIvFutrueTwoHour = (ImageView) itemView.findViewById(R.id.iv_futrue_two_hour);
        this.mIvFutrueTomorrow = (ImageView) itemView.findViewById(R.id.iv_future_tomorrow);
        this.mBtnNotTipDown = (ViewGroup) itemView.findViewById(R.id.btn_ai_not_tip_down);
        this.mBtnNotTipUp = (ViewGroup) itemView.findViewById(R.id.btn_ai_not_tip_up);
        this.mLyNotTip = (ViewGroup) itemView.findViewById(R.id.ly_ai_not_tip);
        this.mBtnNotTipClose = itemView.findViewById(R.id.tv_not_tip_close);
        this.mBtnNotTip7Day = itemView.findViewById(R.id.tv_not_tip_seven_day);
        this.mBtnNotTipAllDay = itemView.findViewById(R.id.tv_not_tip_all_day);
        this.mPushCardPresenter = (PushCardPresenter) this.mInfoflowCardPresenter;
        this.mBtnNotTipDown.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.WeatherCardHolder.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                WeatherCardHolder.this.mBtnNotTipDown.setVisibility(8);
                WeatherCardHolder.this.mBtnNotTipUp.setVisibility(0);
                WeatherCardHolder.this.mLyNotTip.setVisibility(0);
                WeatherCardHolder.this.mPushCardPresenter.onNotTipDownClicked();
            }
        });
        this.mLyNotTip.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.WeatherCardHolder.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                WeatherCardHolder.this.mPushCardPresenter.onNotTipClicked();
            }
        });
        this.mBtnNotTipClose.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.WeatherCardHolder.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                WeatherCardHolder.this.mPushCardPresenter.onNotTipCloseClicked();
            }
        });
        this.mBtnNotTip7Day.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.WeatherCardHolder.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                WeatherCardHolder.this.mPushCardPresenter.onNotTip7DayClicked();
            }
        });
        this.mBtnNotTipAllDay.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.WeatherCardHolder.5
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                WeatherCardHolder.this.mPushCardPresenter.onNotTipAllDayClicked();
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.IWeatherCardView
    public void setWeatherCardContent(WeatherBean weatherBean) {
        if (weatherBean != null) {
            setWeatherTypeBg(weatherBean.weatherType);
            this.mTvWeatherType.setText(weatherBean.weatherType);
            this.mTvTemperature.setText(weatherBean.temperature);
            this.mTvStreet.setText(weatherBean.street);
            if (!TextUtils.isEmpty(weatherBean.warning1)) {
                int mWarningBg = PushResourcesHelper.getBgByWarningType(weatherBean.warningType1);
                this.mTvWarning1.setVisibility(0);
                this.mTvWarning1.setText(weatherBean.warning1);
                this.mTvWarning1.setBackgroundResource(mWarningBg);
            } else {
                this.mTvWarning1.setVisibility(8);
            }
            if (!TextUtils.isEmpty(weatherBean.warning2)) {
                int mWarningBg2 = PushResourcesHelper.getBgByWarningType(weatherBean.warningType2);
                this.mTvWarning2.setVisibility(0);
                this.mTvWarning2.setText(weatherBean.warning1);
                this.mTvWarning2.setBackgroundResource(mWarningBg2);
            } else {
                this.mTvWarning2.setVisibility(8);
            }
            int aqBg = PushResourcesHelper.getBgByAq(weatherBean.aq);
            TextView textView = this.mTvAq;
            textView.setText(weatherBean.aqType + " " + weatherBean.aq);
            int textColorAq = PushResourcesHelper.getTextColorByAq(weatherBean.aq);
            TextView textView2 = this.mTvAq;
            textView2.setTextColor(textView2.getResources().getColor(textColorAq, null));
            this.mTvAq.setBackgroundResource(aqBg);
            boolean emptyTwoHour = TextUtils.isEmpty(weatherBean.weatherTwoHour);
            boolean emptyOneHour = TextUtils.isEmpty(weatherBean.weatherOneHour);
            boolean emptyTomorrow = TextUtils.isEmpty(weatherBean.weatherTomorrow);
            if (emptyOneHour && emptyTwoHour && !emptyTomorrow) {
                this.mLyFutrueTomorrow.setVisibility(0);
                int weatherTypeBg = PushResourcesHelper.getIcByWeather(weatherBean.weatherTomorrow);
                this.mIvFutrueTomorrow.setImageResource(weatherTypeBg);
            } else {
                this.mLyFutrueTomorrow.setVisibility(8);
            }
            if (!emptyTwoHour) {
                this.mLyFutrueTwoHour.setVisibility(0);
                int weatherTypeBg2 = PushResourcesHelper.getIcByWeather(weatherBean.weatherTwoHour);
                this.mIvFutrueTwoHour.setImageResource(weatherTypeBg2);
            } else {
                this.mLyFutrueTwoHour.setVisibility(8);
            }
            if (!emptyOneHour) {
                this.mLyFutrueOneHour.setVisibility(0);
                int weatherTypeBg3 = PushResourcesHelper.getIcByWeather(weatherBean.weatherOneHour);
                this.mIvFutrueOneHour.setImageResource(weatherTypeBg3);
            } else {
                this.mLyFutrueOneHour.setVisibility(8);
            }
            this.mBtnNotTipDown.setVisibility(0);
            this.mBtnNotTipUp.setVisibility(8);
            this.mLyNotTip.setVisibility(8);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.IWeatherCardView
    public void hideWeatherCardNotTip() {
        this.mBtnNotTipUp.setVisibility(8);
        this.mBtnNotTipDown.setVisibility(0);
        this.mLyNotTip.setVisibility(8);
    }

    private void setWeatherTypeBg(String weatherType) {
        int weatherTypeBg = PushResourcesHelper.getBgByWeather(weatherType);
        this.mIvWeatherType.setBackgroundResource(weatherTypeBg);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder, android.view.View.OnClickListener
    public void onClick(View view) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IPushCardView
    public void setPushCardContent(PushBean pushBean) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IPushCardView
    public void setCardFocused(int cardType, boolean focused) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IPushCardView
    public void hidePushCardNotTip() {
    }
}
