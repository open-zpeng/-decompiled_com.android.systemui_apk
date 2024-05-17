package com.xiaopeng.systemui.infoflow.speech.ui.adapter;

import android.content.Context;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.speech.protocol.bean.base.ButtonBean;
import com.xiaopeng.systemui.infoflow.util.CommonUtils;
/* loaded from: classes24.dex */
public class VuiRecommendAdapter extends BaseRecyclerAdapter<ButtonBean> {
    public VuiRecommendAdapter(Context context) {
        super(context);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.adapter.BaseRecyclerAdapter
    public void bindData(BaseRecyclerAdapter<ButtonBean>.BaseViewHolder holder, ButtonBean buttonBean, int position) {
        TextView recommendText = (TextView) holder.getView(R.id.recommend_item_text);
        recommendText.setText(CommonUtils.getRecommendText(buttonBean));
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.adapter.BaseRecyclerAdapter
    public int getItemLayoutId() {
        return R.layout.item_recommend_vui;
    }
}
