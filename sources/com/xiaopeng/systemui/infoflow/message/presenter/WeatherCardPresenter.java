package com.xiaopeng.systemui.infoflow.message.presenter;

import android.text.TextUtils;
import com.xiaopeng.systemui.infoflow.IWeatherCardView;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
/* loaded from: classes24.dex */
public class WeatherCardPresenter extends PushCardPresenter {
    private static final String TAG = "WeatherCardPresenter";
    private IWeatherCardView mWeatherCardView;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class SingleHolder {
        private static final WeatherCardPresenter sInstance = new WeatherCardPresenter();

        private SingleHolder() {
        }
    }

    public static WeatherCardPresenter getInstance() {
        return SingleHolder.sInstance;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.PushCardPresenter
    protected boolean supportTts() {
        return false;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.PushCardPresenter
    protected void updateView() {
        IWeatherCardView iWeatherCardView;
        WeatherBean weatherBean = getWeatherBean(this.mCardData.content);
        if (weatherBean != null && (iWeatherCardView = this.mWeatherCardView) != null) {
            iWeatherCardView.setWeatherCardContent(weatherBean);
        }
    }

    private WeatherBean getWeatherBean(String content) {
        if (!TextUtils.isEmpty(content)) {
            WeatherBean weatherBean = (WeatherBean) GsonUtil.fromJson(content, (Class<Object>) WeatherBean.class);
            return weatherBean;
        }
        return null;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.PushCardPresenter, com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onViewAttachedToWindow() {
        super.onViewAttachedToWindow();
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.PushCardPresenter, com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow();
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.PushCardPresenter
    protected void hideCardNotTip() {
        IWeatherCardView iWeatherCardView = this.mWeatherCardView;
        if (iWeatherCardView != null) {
            iWeatherCardView.hideWeatherCardNotTip();
        }
    }
}
