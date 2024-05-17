package com.xiaopeng.systemui.infoflow.message.adapter.holder;

import android.view.View;
import com.android.systemui.R;
import com.xiaopeng.speech.protocol.bean.recommend.RecommendBean;
import com.xiaopeng.systemui.infoflow.IRecommendCardView;
import com.xiaopeng.systemui.infoflow.speech.ui.widget.RecommendView;
/* loaded from: classes24.dex */
public class RecommendCardHolder extends BaseCardHolder implements IRecommendCardView {
    private static final String TAG = "RecommendCardHolder";
    private RecommendView mRecommendView;

    public RecommendCardHolder(View itemView) {
        super(itemView);
        this.mRecommendView = (RecommendView) itemView.findViewById(R.id.view_recommend);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder
    public int getCardType() {
        return 26;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder, android.view.View.OnClickListener
    public void onClick(View view) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IRecommendCardView
    public void setRecommendCardContent(RecommendBean recommendBean) {
        this.mRecommendView.updateList(recommendBean);
    }
}
