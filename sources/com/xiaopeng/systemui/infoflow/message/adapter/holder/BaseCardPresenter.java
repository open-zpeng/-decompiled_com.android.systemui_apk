package com.xiaopeng.systemui.infoflow.message.adapter.holder;

import android.content.ComponentName;
import android.content.Context;
import com.xiaopeng.systemui.PresenterCenter;
import com.xiaopeng.systemui.controller.ActivityController;
import com.xiaopeng.systemui.infoflow.IInfoflowView;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.message.data.CardListData;
import com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.util.Logger;
/* loaded from: classes24.dex */
public abstract class BaseCardPresenter implements IInfoflowCardPresenter {
    private static final String TAG = "BaseCardPresenter";
    protected CardEntry mCardData;
    protected BaseCardHolder mCardHolder;
    protected Context mContext = ContextUtils.getContext();
    protected IInfoflowView mInfoflowView = PresenterCenter.getInstance().getInfoFlow().getInfoflowView();

    protected abstract void fillViewWithCardHolder();

    protected abstract void fillViewWithInfoflowView();

    protected abstract int getCardType();

    public boolean isAppForeground() {
        return isAppForeground(getCardPackageName());
    }

    public static boolean isAppForeground(String cardPackageName) {
        ComponentName curComponentName = ActivityController.getCurrentComponent();
        if (curComponentName != null && curComponentName.getPackageName() != null) {
            Logger.d(TAG, "checkAppOpened curPackageName : " + curComponentName.getPackageName() + " cardPackageName : " + cardPackageName);
            return cardPackageName.equals(curComponentName.getPackageName());
        }
        return false;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public String getCardPackageName() {
        return "";
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onViewAttachedToWindow() {
        Logger.d(TAG, "onViewAttachedToWindow : " + getCardType());
        this.mCardData = CardListData.getInstance().getCard(getCardType());
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onViewDetachedFromWindow() {
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onCardClicked() {
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onCardCloseClicked() {
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onActionClicked(int actionIndex) {
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void destroyCard() {
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void bindData(CardEntry cardEntry) {
        this.mCardData = cardEntry;
        fillView(null);
        bindDataImpl(cardEntry);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void bindData(BaseCardHolder cardHolder, CardEntry cardEntry) {
        this.mCardData = cardEntry;
        this.mCardHolder = cardHolder;
        fillView(cardHolder);
        bindDataImpl(cardEntry);
    }

    protected void bindDataImpl(CardEntry cardEntry) {
    }

    protected void fillView(BaseCardHolder cardHolder) {
        if (cardHolder != null) {
            fillViewWithCardHolder();
        } else {
            fillViewWithInfoflowView();
        }
    }
}
