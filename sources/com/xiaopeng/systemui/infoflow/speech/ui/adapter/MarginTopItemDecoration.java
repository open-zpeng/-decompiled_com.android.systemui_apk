package com.xiaopeng.systemui.infoflow.speech.ui.adapter;

import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
/* loaded from: classes24.dex */
public class MarginTopItemDecoration extends RecyclerView.ItemDecoration {
    private int mTopMargin;

    public MarginTopItemDecoration(int topMagin) {
        this.mTopMargin = topMagin;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = 0;
        } else {
            outRect.top = this.mTopMargin;
        }
    }
}
