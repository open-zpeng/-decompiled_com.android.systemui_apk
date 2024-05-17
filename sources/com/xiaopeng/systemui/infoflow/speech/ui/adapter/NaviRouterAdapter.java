package com.xiaopeng.systemui.infoflow.speech.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.jarvisproto.FeedUIEvent;
import com.xiaopeng.speech.protocol.bean.FeedListUIValue;
import com.xiaopeng.speech.protocol.node.navi.bean.RouteSelectBean;
import com.xiaopeng.systemui.infoflow.montecarlo.view.RouteDetailView;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.infoflow.widget.IFocusView;
/* loaded from: classes24.dex */
public class NaviRouterAdapter extends BaseRecyclerAdapter<RouteSelectBean> {
    private static final String TAG = NaviRouterAdapter.class.getSimpleName();

    public NaviRouterAdapter(Context context) {
        super(context);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.adapter.BaseRecyclerAdapter
    public void bindData(BaseRecyclerAdapter<RouteSelectBean>.BaseViewHolder holder, RouteSelectBean selectBean, final int position) {
        TextView indexView = (TextView) holder.getView(R.id.tv_index);
        TextView titleView = (TextView) holder.getView(R.id.tv_title);
        TextView lightView = (TextView) holder.getView(R.id.tv_num_light);
        TextView moneyView = (TextView) holder.getView(R.id.tv_money);
        TextView contentView = (TextView) holder.getView(R.id.tv_content);
        TextView batteryRemainView = (TextView) holder.getView(R.id.tv_battery_content);
        final RouteDetailView itemView = (RouteDetailView) holder.itemView;
        itemView.bindData(selectBean, position);
        indexView.setText(String.valueOf(position + 1));
        titleView.setText(selectBean.routeTypeName);
        if (TextUtils.isEmpty(selectBean.trafficSignal)) {
            lightView.setVisibility(8);
        } else {
            lightView.setVisibility(0);
            lightView.setText(selectBean.trafficSignal);
        }
        if (TextUtils.isEmpty(selectBean.trafficCost)) {
            moneyView.setVisibility(8);
        } else {
            moneyView.setVisibility(0);
            moneyView.setText(selectBean.trafficCost);
        }
        contentView.setText(selectBean.totalTimeLine1);
        batteryRemainView.setText(selectBean.remainDistance);
        itemView.setOnFocusChangedListener(new IFocusView.OnFocusChangedListener() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.adapter.NaviRouterAdapter.1
            @Override // com.xiaopeng.systemui.infoflow.widget.IFocusView.OnFocusChangedListener
            public void onFocusedChanged(boolean focused) {
                if (focused) {
                    NaviRouterAdapter.this.sendFocusedEvent(position);
                }
            }

            @Override // com.xiaopeng.systemui.infoflow.widget.IFocusView.OnFocusChangedListener
            public void onFocusChangedForViewUpdate(boolean focused) {
                itemView.onFocusChanged(focused);
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.adapter.BaseRecyclerAdapter
    public void sendSelectedEvent(int position) {
        FeedListUIValue feedListUIValue = new FeedListUIValue();
        feedListUIValue.source = this.mContext.getPackageName();
        feedListUIValue.index = position + 1;
        feedListUIValue.type = FeedListUIValue.TYPE_ROUTE;
        SpeechClient.instance().getAgent().sendUIEvent(FeedUIEvent.LIST_ITEM_SELECT, FeedListUIValue.toJson(feedListUIValue));
        String str = TAG;
        Logger.d(str, "sendSelectedEvent position=" + position);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.adapter.BaseRecyclerAdapter
    public int getItemLayoutId() {
        return R.layout.item_navi_router;
    }
}
