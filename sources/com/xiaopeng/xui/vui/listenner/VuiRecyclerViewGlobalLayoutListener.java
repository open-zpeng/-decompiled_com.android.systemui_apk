package com.xiaopeng.xui.vui.listenner;

import android.view.ViewTreeObserver;
import com.xiaopeng.xui.vui.VuiRecyclerView;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes25.dex */
public class VuiRecyclerViewGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
    private VuiRecyclerView mVuiRecycleView;
    private AtomicInteger updateNum = new AtomicInteger(0);

    public VuiRecyclerViewGlobalLayoutListener(VuiRecyclerView view) {
        this.mVuiRecycleView = null;
        this.mVuiRecycleView = view;
    }

    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
    public void onGlobalLayout() {
        if (this.updateNum.get() > 0 && this.mVuiRecycleView != null) {
            this.updateNum.decrementAndGet();
            this.mVuiRecycleView.updateVuiScene();
        }
    }

    public void setUpdateNum() {
        this.updateNum.incrementAndGet();
    }
}
