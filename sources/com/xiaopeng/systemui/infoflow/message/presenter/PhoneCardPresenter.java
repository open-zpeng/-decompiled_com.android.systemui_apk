package com.xiaopeng.systemui.infoflow.message.presenter;

import com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter;
/* loaded from: classes24.dex */
public class PhoneCardPresenter extends BaseCardPresenter {
    private static final String TAG = "PhoneCardPresenter";

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class SingleHolder {
        private static final PhoneCardPresenter sInstance = new PhoneCardPresenter();

        private SingleHolder() {
        }
    }

    public static PhoneCardPresenter getInstance() {
        return SingleHolder.sInstance;
    }

    protected PhoneCardPresenter() {
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public String getCardPackageName() {
        return "com.xiaopeng.btphone";
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected int getCardType() {
        return 5;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onCardClicked() {
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithInfoflowView() {
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithCardHolder() {
    }
}
