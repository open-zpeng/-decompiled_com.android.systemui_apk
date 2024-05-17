package com.xiaopeng.systemui.infoflow.message.presenter;

import com.android.systemui.R;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter;
import com.xiaopeng.systemui.utils.DataLogUtils;
/* loaded from: classes24.dex */
public class HomeCardPresenter extends BaseCardPresenter {

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class SingleHolder {
        private static final HomeCardPresenter sInstance = new HomeCardPresenter();

        private SingleHolder() {
        }
    }

    public static HomeCardPresenter getInstance() {
        return SingleHolder.sInstance;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected int getCardType() {
        return 22;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onCardClicked() {
        PackageHelper.startActivitySafely(this.mContext, R.string.component_app, null);
        DataLogUtils.sendInfoDataLog("P00005", "B001");
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithInfoflowView() {
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithCardHolder() {
    }
}
