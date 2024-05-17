package com.xiaopeng.xui.vui.listenner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.xiaopeng.xui.vui.VuiRecyclerView;
/* loaded from: classes25.dex */
public class VuiRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    private VuiRecyclerView mVuiRecycleView;

    public VuiRecyclerViewScrollListener(VuiRecyclerView view) {
        this.mVuiRecycleView = null;
        this.mVuiRecycleView = view;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        VuiRecyclerView vuiRecyclerView;
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == 0 && (vuiRecyclerView = this.mVuiRecycleView) != null) {
            vuiRecyclerView.updateVuiScene();
        }
    }
}
