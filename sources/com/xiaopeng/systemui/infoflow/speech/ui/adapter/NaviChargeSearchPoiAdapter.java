package com.xiaopeng.systemui.infoflow.speech.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.systemui.R;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.jarvisproto.FeedUIEvent;
import com.xiaopeng.speech.protocol.bean.FeedListUIValue;
import com.xiaopeng.speech.protocol.bean.search.ChargeData;
import com.xiaopeng.speech.protocol.bean.search.SearchContentBean;
import com.xiaopeng.systemui.infoflow.montecarlo.view.DrawbleTextView;
import com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechCardView;
import com.xiaopeng.systemui.infoflow.widget.IFocusView;
/* loaded from: classes24.dex */
public class NaviChargeSearchPoiAdapter extends RecyclerView.Adapter<ItemViewHolder> {
    private static final String TAG = NaviChargeSearchPoiAdapter.class.getSimpleName();
    private Context mContext;
    private SearchContentBean<ChargeData> mData;
    private LayoutInflater mLayoutInflater;

    public NaviChargeSearchPoiAdapter(Context context) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(this.mContext);
    }

    public void setData(SearchContentBean<ChargeData> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    @NonNull
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = this.mLayoutInflater.inflate(R.layout.item_navi_charge_search, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        itemViewHolder.bindData(i, this.mData.data.getSearchData().get(i));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        SearchContentBean<ChargeData> searchContentBean = this.mData;
        if (searchContentBean == null) {
            return 0;
        }
        return searchContentBean.data.getSearchData().size();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendSelectedEvent(int position) {
        FeedListUIValue feedListUIValue = new FeedListUIValue();
        feedListUIValue.source = this.mContext.getPackageName();
        feedListUIValue.index = position + 1;
        SpeechClient.instance().getAgent().sendUIEvent(FeedUIEvent.LIST_ITEM_SELECT, FeedListUIValue.toJson(feedListUIValue));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendFocusedEvent(int position) {
        FeedListUIValue feedListUIValue = new FeedListUIValue();
        feedListUIValue.source = this.mContext.getPackageName();
        feedListUIValue.index = position + 1;
        SpeechClient.instance().getAgent().sendUIEvent(FeedUIEvent.LIST_ITEM_FOCUS, FeedListUIValue.toJson(feedListUIValue));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes24.dex */
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private DrawbleTextView mEnterTv;
        private SpeechCardView mFocusView;
        private TextView mIndex;
        private int mPosition;
        private TextView mSubtitle;
        private TextView mTitle;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.mFocusView = (SpeechCardView) itemView;
            this.mIndex = (TextView) itemView.findViewById(R.id.tv_index);
            this.mTitle = (TextView) itemView.findViewById(R.id.tv_title);
            this.mSubtitle = (TextView) itemView.findViewById(R.id.tv_subtitle);
            this.mEnterTv = (DrawbleTextView) itemView.findViewById(R.id.tv_enter);
        }

        public void bindData(int position, ChargeData.ChargingStationData poiBean) {
            this.mFocusView.setFocused(false);
            this.mPosition = position;
            this.mIndex.setText(String.valueOf(position + 1));
            this.mTitle.setText(poiBean.getName());
            this.mEnterTv.setText(poiBean.getDisplayDistance());
            this.mSubtitle.setText(poiBean.getStationAddr());
            this.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.adapter.NaviChargeSearchPoiAdapter.ItemViewHolder.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    NaviChargeSearchPoiAdapter.this.sendSelectedEvent(ItemViewHolder.this.mPosition);
                }
            });
            this.mFocusView.setOnFocusChangedListener(new IFocusView.OnFocusChangedListener() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.adapter.NaviChargeSearchPoiAdapter.ItemViewHolder.2
                @Override // com.xiaopeng.systemui.infoflow.widget.IFocusView.OnFocusChangedListener
                public void onFocusedChanged(boolean focused) {
                    if (focused) {
                        NaviChargeSearchPoiAdapter.this.sendFocusedEvent(ItemViewHolder.this.mPosition);
                    }
                }

                @Override // com.xiaopeng.systemui.infoflow.widget.IFocusView.OnFocusChangedListener
                public void onFocusChangedForViewUpdate(boolean focused) {
                }
            });
        }
    }
}
