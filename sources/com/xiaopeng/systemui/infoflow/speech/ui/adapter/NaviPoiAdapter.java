package com.xiaopeng.systemui.infoflow.speech.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.speech.protocol.node.navi.bean.PoiBean;
import com.xiaopeng.speech.protocol.node.navi.bean.PoiExtraBean;
import com.xiaopeng.speech.vui.VuiEngine;
import com.xiaopeng.systemui.infoflow.montecarlo.util.NaviUtil;
import com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechListView;
import com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechNaviCardView;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedLinearLayout;
import com.xiaopeng.systemui.infoflow.theme.AnimatedTextView;
import com.xiaopeng.systemui.infoflow.theme.XRelativeLayout;
import com.xiaopeng.systemui.infoflow.theme.XTextView;
import com.xiaopeng.systemui.infoflow.widget.IFocusView;
import com.xiaopeng.systemui.utils.Utils;
import com.xiaopeng.vui.commons.VuiAction;
import com.xiaopeng.vui.commons.VuiElementType;
/* loaded from: classes24.dex */
public class NaviPoiAdapter extends BaseRecyclerAdapter<PoiBean> {
    private static final String TAG = NaviPoiAdapter.class.getSimpleName();
    private boolean mShowDetail;
    private SpeechListView mSpeechListView;

    public NaviPoiAdapter(Context context) {
        super(context);
        this.mShowDetail = false;
    }

    public void setViewContainer(SpeechListView listView) {
        this.mSpeechListView = listView;
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.adapter.BaseRecyclerAdapter
    public void bindData(final BaseRecyclerAdapter<PoiBean>.BaseViewHolder holder, PoiBean poiBean, final int position) {
        AnimatedTextView dcTotal;
        XTextView btnDetail;
        SpeechNaviCardView itemView = (SpeechNaviCardView) holder.itemView;
        final TextView indexView = (TextView) holder.getView(R.id.tv_index);
        TextView titleView = (TextView) holder.getView(R.id.tv_title);
        TextView contentView = (TextView) holder.getView(R.id.tv_content);
        TextView subtitleView = (TextView) holder.getView(R.id.tv_subtitle);
        XTextView btnDetail2 = (XTextView) holder.getView(R.id.btn_detail);
        AlphaOptimizedLinearLayout chargeInfoContainer = (AlphaOptimizedLinearLayout) holder.getView(R.id.charging_info_container);
        AlphaOptimizedLinearLayout dcContainer = (AlphaOptimizedLinearLayout) holder.getView(R.id.dc_container);
        AlphaOptimizedLinearLayout acContainer = (AlphaOptimizedLinearLayout) holder.getView(R.id.ac_container);
        AnimatedTextView dcFree = (AnimatedTextView) holder.getView(R.id.tv_dc_free);
        AnimatedTextView dcTotal2 = (AnimatedTextView) holder.getView(R.id.tv_dc_total);
        AnimatedTextView acFree = (AnimatedTextView) holder.getView(R.id.tv_ac_free);
        AnimatedTextView acTotal = (AnimatedTextView) holder.getView(R.id.tv_ac_total);
        itemView.setFocused(false);
        indexView.setText(String.valueOf(position + 1));
        titleView.setText(poiBean.getName());
        String distanceString = NaviUtil.getDistanceString(this.mContext, poiBean.getDistance());
        subtitleView.setText(distanceString);
        btnDetail2.setText(R.string.speech_poi_detail);
        if (!Utils.isChineseLanguage()) {
            dcTotal = dcTotal2;
            btnDetail = btnDetail2;
            chargeInfoContainer.setVisibility(8);
        } else {
            PoiExtraBean poiExtraBean = poiBean.getPoiExtra();
            if (poiExtraBean == null) {
                dcTotal = dcTotal2;
                btnDetail = btnDetail2;
            } else {
                int dcNums = poiExtraBean.getDcNums();
                if (dcNums > 0) {
                    dcContainer.setVisibility(0);
                    StringBuilder sb = new StringBuilder();
                    btnDetail = btnDetail2;
                    sb.append(this.mContext.getString(R.string.infoflow_poi_charging_free));
                    sb.append(poiExtraBean.getDcFreeNums());
                    dcFree.setText(sb.toString());
                    dcTotal2.setText("/" + ((Object) this.mContext.getText(R.string.infoflow_poi_charging_total)) + dcNums);
                } else {
                    btnDetail = btnDetail2;
                    dcContainer.setVisibility(8);
                }
                int acNums = poiExtraBean.getAcNums();
                if (acNums > 0) {
                    acContainer.setVisibility(0);
                    StringBuilder sb2 = new StringBuilder();
                    dcTotal = dcTotal2;
                    sb2.append(this.mContext.getString(R.string.infoflow_poi_charging_free));
                    sb2.append(poiExtraBean.getAcFreeNums());
                    acFree.setText(sb2.toString());
                    acTotal.setText("/" + ((Object) this.mContext.getText(R.string.infoflow_poi_charging_total)) + acNums);
                } else {
                    dcTotal = dcTotal2;
                    acContainer.setVisibility(8);
                }
                if (poiExtraBean.getSelfSupportFlag() == 1) {
                    titleView.setCompoundDrawablesWithIntrinsicBounds(this.mContext.getDrawable(R.drawable.infoflow_poi_self_charging), (Drawable) null, (Drawable) null, (Drawable) null);
                } else {
                    titleView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
                }
                chargeInfoContainer.setVisibility((dcNums > 0 || acNums > 0) ? 0 : 8);
            }
        }
        contentView.setText(poiBean.getAddress());
        itemView.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.adapter.-$$Lambda$NaviPoiAdapter$ic8VNAMoFzj2fnhDZBhlOqf-49c
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                NaviPoiAdapter.this.lambda$bindData$0$NaviPoiAdapter(position, view);
            }
        });
        final XTextView btnDetail3 = btnDetail;
        btnDetail3.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.adapter.NaviPoiAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                NaviPoiAdapter.this.mShowDetail = true;
                if (NaviPoiAdapter.this.mOnItemClickListener != null) {
                    NaviPoiAdapter.this.mOnItemClickListener.onItemClick(NaviPoiAdapter.this, view, position);
                }
                NaviPoiAdapter.this.mSpeechListView.onPoiDetailShown(true);
            }
        });
        itemView.setOnFocusChangedListener(new IFocusView.OnFocusChangedListener() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.adapter.NaviPoiAdapter.2
            @Override // com.xiaopeng.systemui.infoflow.widget.IFocusView.OnFocusChangedListener
            public void onFocusedChanged(boolean focused) {
            }

            @Override // com.xiaopeng.systemui.infoflow.widget.IFocusView.OnFocusChangedListener
            public void onFocusChangedForViewUpdate(boolean focused) {
                if (!NaviPoiAdapter.this.mShowDetail || !focused) {
                    NaviPoiAdapter.this.updateViewOnFocusedChanged(holder, false);
                    indexView.setSelected(false);
                    btnDetail3.setSelected(false);
                    return;
                }
                NaviPoiAdapter.this.updateViewOnFocusedChanged(holder, true);
                NaviPoiAdapter.this.mShowDetail = false;
                NaviPoiAdapter.this.sendFocusedEvent(position);
            }
        });
        fillVuiInfo(holder, position);
    }

    public /* synthetic */ void lambda$bindData$0$NaviPoiAdapter(int position, View view) {
        this.mShowDetail = false;
        if (this.mOnItemClickListener != null) {
            this.mOnItemClickListener.onItemClick(this, view, position);
        }
        sendSelectedEvent(position);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.adapter.BaseRecyclerAdapter
    public int getItemLayoutId() {
        return R.layout.item_navi_poi;
    }

    private void fillVuiInfo(BaseRecyclerAdapter<PoiBean>.BaseViewHolder holder, int position) {
        XRelativeLayout poiItemContainer = (XRelativeLayout) holder.itemView;
        TextView titleView = (TextView) holder.getView(R.id.tv_title);
        XTextView btnDetail = (XTextView) holder.getView(R.id.btn_detail);
        poiItemContainer.setVuiElementId("" + poiItemContainer.getId() + "_" + position);
        poiItemContainer.setVuiLabel(titleView.getText().toString() + "|第" + (position + 1) + "个");
        poiItemContainer.setVuiElementType(VuiElementType.GROUP);
        poiItemContainer.setVuiAction(VuiAction.CLICK.getName());
        btnDetail.setVuiElementId("" + btnDetail.getId() + "_" + position);
        btnDetail.setVuiElementType(VuiElementType.BUTTON);
        VuiEngine vuiEngine = VuiEngine.getInstance(this.mContext);
        vuiEngine.setVuiElementTag(btnDetail, "poi_list_btn_detail_" + position);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateViewOnFocusedChanged(BaseRecyclerAdapter<PoiBean>.BaseViewHolder holder, boolean focused) {
        TextView indexView = (TextView) holder.getView(R.id.tv_index);
        XTextView btnDetail = (XTextView) holder.getView(R.id.btn_detail);
        if (focused) {
            btnDetail.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, this.mContext.getDrawable(R.drawable.infoflow_poi_detail_arrow), (Drawable) null);
            indexView.setBackground(this.mContext.getDrawable(R.mipmap.bg_poi_tag_focused));
            return;
        }
        btnDetail.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
        indexView.setBackground(this.mContext.getDrawable(R.mipmap.bg_poi_tag));
    }

    public void setShowDetail(boolean b) {
        this.mShowDetail = b;
    }
}
