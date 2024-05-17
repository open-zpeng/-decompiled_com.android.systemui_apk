package com.xiaopeng.systemui.infoflow.speech.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.xiaopeng.speech.protocol.bean.recommend.RecommendBean;
import com.xiaopeng.systemui.infoflow.speech.ui.adapter.VuiRecommendAdapter;
/* loaded from: classes24.dex */
public class VuiRecommendView extends RecyclerView {
    private VuiRecommendAdapter mAdapter;

    public VuiRecommendView(@NonNull Context context) {
        super(context);
    }

    public VuiRecommendView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VuiRecommendView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        setLayoutManager(new LinearLayoutManager(getContext(), 0, false));
        if (this.mAdapter == null) {
            this.mAdapter = new VuiRecommendAdapter(this.mContext);
        }
        setAdapter(this.mAdapter);
    }

    public void setDataList(RecommendBean recommendBean) {
        if (recommendBean != null) {
            this.mAdapter.setNewData(recommendBean.getRelateList());
        }
    }

    public void clearData() {
        this.mAdapter.clear();
    }
}
