package com.xiaopeng.systemui.infoflow;

import com.xiaopeng.systemui.infoflow.message.presenter.WeatherBean;
/* loaded from: classes24.dex */
public interface IWeatherCardView extends IPushCardView {
    void hideWeatherCardNotTip();

    void setWeatherCardContent(WeatherBean weatherBean);
}
