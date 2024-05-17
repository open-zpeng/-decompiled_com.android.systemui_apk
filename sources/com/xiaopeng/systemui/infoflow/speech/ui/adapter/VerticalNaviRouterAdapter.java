package com.xiaopeng.systemui.infoflow.speech.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import com.android.systemui.R;
import com.xiaopeng.speech.protocol.node.navi.bean.RouteSelectBean;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.montecarlo.view.RouteDetailView;
/* loaded from: classes24.dex */
public class VerticalNaviRouterAdapter extends NaviRouterAdapter {
    private int mMarginHorizontal;
    private int mSplitterWidth;

    public VerticalNaviRouterAdapter(Context context) {
        super(context);
        this.mMarginHorizontal = this.mContext.getResources().getDimensionPixelSize(R.dimen.infoflow_card_route_detail_margin_horizontal);
        this.mSplitterWidth = this.mContext.getResources().getDimensionPixelSize(R.dimen.infoflow_list_view_splitter_thickness);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.xiaopeng.systemui.infoflow.speech.ui.adapter.NaviRouterAdapter, com.xiaopeng.systemui.infoflow.speech.ui.adapter.BaseRecyclerAdapter
    public void bindData(BaseRecyclerAdapter<RouteSelectBean>.BaseViewHolder holder, RouteSelectBean selectBean, int position) {
        super.bindData(holder, selectBean, position);
        RouteDetailView itemView = (RouteDetailView) holder.itemView;
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) itemView.getLayoutParams();
        int itemCount = getItemCount();
        if (itemCount <= 0) {
            itemCount = 1;
        }
        lp.width = ((ContextUtils.getScreenWidth() - (this.mMarginHorizontal * 2)) - (this.mSplitterWidth * itemCount)) / itemCount;
    }
}
